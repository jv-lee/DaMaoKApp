package com.lockstudio.sticklocker.core;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lockstudio.sticklocker.Interface.UnlockListener;
import com.lockstudio.sticklocker.activity.FakeActivity;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.ThemeConfig;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.DeviceUtils;
import com.lockstudio.sticklocker.util.HostUtil;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.MediaPlayerUtils;
import com.lockstudio.sticklocker.util.NetworkUtil;
import com.lockstudio.sticklocker.util.RLog;
import com.lockstudio.sticklocker.util.ThemeUtils;
import com.lockstudio.sticklocker.util.YYBUtil;
import com.lockstudio.sticklocker.view.LockContainer;
import com.lockstudio.sticklocker.view.NoPhoneOrMsmView;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Tommy on 15/3/13.
 */
public class CoreLock implements UnlockListener {

    private final String TAG = "V5_MAIN_SERVICE";

    private static final int MSG_ALARM_WAKEUP = 2000;
    private static final int MSG_KEYGUARD_GONE = 2002;
    private static final int MSG_ACTIVITY_YYB = 2004;

    //2015-7-4     DAY_OF_YEAR = 185   首发日期，提前一天上传
    private static final int DAY_OF_YEAR_EXCLUSIVE = 184;

    private Context mContext;
    private WindowManager windowManager;
    private LockContainer lockContainer;
    private static final Object LOCK;
    private static final Object KEYGUARD;

    private KeyguardManager.KeyguardLock keyguardLock;

    private boolean lockedBeforeRinging = false;
    private boolean calling = false;

    private RequestQueue requestQueue = null;
    private NoPhoneOrMsmView noPhoneOrMsmView;

    static {
        LOCK = new Object();
        KEYGUARD = new Object();
    }

    public CoreLock(Context context) {
        this.mContext = context;

        // 监听电话
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                if (!LockApplication.getInstance().getConfig().isEnabled()) {
                    lockedBeforeRinging = false;
                    calling = false;
                    return;
                }

                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        calling = true;
                        lockedBeforeRinging = isLocked();
                        unlock(true);
                        break;

                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        calling = true;
                        break;

                    case TelephonyManager.CALL_STATE_IDLE:
                        if (lockedBeforeRinging) {
                            lock(mContext, true);
                            setScreenOffTimeout(MConstants.MIN_SCREEN_OFF_TIMEOUT);
                        }
                        lockedBeforeRinging = false;
                        calling = false;
                        break;

                    default:
                        break;
                }
            }
        };
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        saveDefaultScreenOffTime();

        CustomEventCommit.commit(mContext, TAG, "CORE_SERVICE_RESTART");
    }

    public void onDestroy() {
        restoreScreenOffTimeout();
        enableKeyguard();
        if (requestQueue != null) {
            requestQueue.stop();
            requestQueue = null;
        }
    } 
    
    // 锁屏操作入口
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        CustomEventCommit.commit(mContext, TAG, action);

        // TODO: 15/7/9 先关闭应用宝拉活
//        if (Intent.ACTION_SCREEN_ON.equals(action)) {
//            startActivityViaIntent();
//        }

//        phoneNum.setText("未接电话："+readMissCall());
        if (MConstants.ACTION_DISABLE_KEYGUARD.equals(action)) {
            disableKeyguard();
        } else if (MConstants.ACTION_ENABLE_KEYGUARD.equals(action)) {
            enableKeyguard();
        }

        if (!LockApplication.getInstance().getConfig().isEnabled() || TextUtils.isEmpty(LockApplication.getInstance().getConfig().getThemeName())) {
            CustomEventCommit.commit(mContext, TAG, "DISABLE");
            return;
        }

        if (MConstants.ACTION_LOCK_NOW.equals(action)) {
            lock(context, false);
            saveDefaultScreenOffTime();
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            if (!calling) {
                if (!isLocked()) {
                    saveDefaultScreenOffTime();
                }
                restoreScreenOffTimeout();
                lock(context, false);
            }
        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            if (isLocked()) {
                checkAlarmWakeupWithinUnlock();
                setScreenOffTimeout(MConstants.MIN_SCREEN_OFF_TIMEOUT);
            }
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
            handler.sendEmptyMessageDelayed(MSG_KEYGUARD_GONE, 600);
        } else if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
            // 呼出电话
            calling = true;
            unlock(true);
        } else if ("com.android.deskclock.ALARM_ALERT".equals(action) || "com.android.deskclock.ALARM_DONE".equals(action)
                || "com.samsung.sec.android.clockpackage.alarm.ALARM_ALERT".equals(action) || "com.htc.worldclock.ALARM_ALERT".equals(action)
                || "com.android.alarmclock.ALARM_ALERT".equals(action) || "com.nubia.deskclock.ALARM_ALERT".equals(action)
                || "com.cn.google.AlertClock.ALARM_ALERT".equals(action) || "com.sonyericsson.alarm.ALARM_ALERT".equals(action)
                || "com.oppo.alarmclock.alarmclock.ALARM_ALERT".equals(action)) {
            // 闹钟广播
            unlock(true);
        } else if (MConstants.ACTION_FAKE_ACTIVITY_CREAT_DONE.equals(action)) {
            if (lockContainer == null || !lockContainer.isShown()) {
                mContext.sendBroadcast(new Intent(MConstants.ACTION_CLOSE_FAKE_ACTIVITY));
            }
        }
    }

    public void lock(Context context, boolean silent) {
        synchronized (LOCK) {
            CustomEventCommit.commit(mContext, TAG, "LOCK");

            calling = false;
            lockedBeforeRinging = false;
            // LockApplication.getInstance().getConfig().setLocked(true);
            // LockApplication.getInstance().getConfig().setElapsedRealtime(SystemClock.elapsedRealtime());

            if (isLocked()) {
                startFakeActivity(context);
                return;
            }

            String themePath = LockApplication.getInstance().getConfig().getThemeName();
            ThemeConfig themeConfig = ThemeUtils.parseConfig(mContext, new File(themePath, MConstants.config) + "");
            if (themeConfig == null) {
                return;
            }

            noPhoneOrMsmView=new NoPhoneOrMsmView(mContext);
            lockContainer = new LockContainer(mContext);
            lockContainer.setDiy(false);
            lockContainer.setUnlockListener(this);
            lockContainer.addTheme(themeConfig);
            // if (addHuodongView()) {
            // addShowHuodongViewCount(1);
            // LayoutParams layoutParams = new
            // LayoutParams(LayoutParams.WRAP_CONTENT,
            // LayoutParams.WRAP_CONTENT);
            // layoutParams.leftMargin =
            // LockApplication.getInstance().getConfig().getScreenWidth() -
            // DensityUtil.dip2px(context, 120);
            // layoutParams.topMargin =
            // LockApplication.getInstance().getConfig().getScreenHeight() / 2;
            // ImageView imageView = new ImageView(mContext);
            // imageView.setImageResource(R.drawable.lyf);
            // imageView.setOnClickListener(new OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // addShowHuodongViewCount(3);
            //
            // SharedPreferences sharedPreferences;
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // sharedPreferences = mContext.getSharedPreferences("huodong.cfg",
            // Context.MODE_MULTI_PROCESS);
            // } else {
            // sharedPreferences = mContext.getSharedPreferences("huodong.cfg",
            // Context.MODE_PRIVATE);
            // }
            // Intent intent = new Intent(mContext, WebviewFragment.class);
            // intent.putExtra("title",
            // sharedPreferences.getString("huodong_title", "文字锁屏"));
            // intent.putExtra("url", sharedPreferences.getString("huodong_url",
            // "http://www.lockstudio.com/"));
            // intent.putExtra("fromLock", true);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // mContext.startActivity(intent);
            // unlock(true);
            //
            // CustomEventCommit.commit(mContext, TAG, "李易峰生日活动");
            // }
            // });
            // AnimationDrawable animationDrawable = (AnimationDrawable)
            // imageView.getDrawable();
            // animationDrawable.start();
            // lockContainer.addView(imageView, layoutParams);
            // }

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.x = 0;
            layoutParams.y = 0;
            layoutParams.width = LockApplication.getInstance().getConfig().getScreenWidth();
            layoutParams.height = LockApplication.getInstance().getConfig().getScreenHeight();
            // layoutParams.height = DeviceInfoUtils.getDeviceHeight2(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                layoutParams.gravity = Gravity.RELATIVE_LAYOUT_DIRECTION | Gravity.TOP | Gravity.LEFT;
            } else {
                layoutParams.gravity = Gravity.FILL;
            }

            if (LockApplication.getInstance().getConfig().isShowStatusBar()) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (!ViewConfiguration.get(mContext).hasPermanentMenuKey() && !DeviceUtils.isMIUI()) {
                    // 添加后会失去焦点，导致长按返回键生效，小米手机会被结束掉
                    layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                }
            }
            // WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                layoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                layoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            }

            layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
            layoutParams.format = PixelFormat.TRANSPARENT;
            layoutParams.screenOrientation = Configuration.ORIENTATION_PORTRAIT;

            if (windowManager == null) {
                windowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            }

            windowManager.addView(lockContainer, layoutParams);
            if(LockApplication.getInstance().getConfig().isShowPhoneSms() && (noPhoneOrMsmView.readMissCall()>0 || noPhoneOrMsmView.getNewSmsCount()>0)){
            	//noPhoneOrMsmView.setAlpha(200);
            	windowManager.addView(noPhoneOrMsmView, layoutParams);
//            	phoneNum.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
//            			LayoutParams.WRAP_CONTENT));
//            	phoneNum.setGravity(Gravity.CENTER);
//            	lockContainer.addView(phoneNum, layoutParams);
            }

            startFakeActivity(context);
            if (LockApplication.getInstance().getConfig().isPlayLockSound() && !silent) {
                playLockSound();
            }
        }
    }

    private void unlock(boolean silent) {
        synchronized (LOCK) {
            mContext.sendBroadcast(new Intent(MConstants.ACTION_CLOSE_FAKE_ACTIVITY));
            // LockApplication.getInstance().getConfig().setLocked(false);

            restoreScreenOffTimeout();
            
            if (lockContainer != null && lockContainer.isShown()) {
                if (!silent) {
                    if (LockApplication.getInstance().getConfig().isPlaySound()) {
                        playUnlockSound();
                    }
                    if (LockApplication.getInstance().getConfig().isVibrate()) {
                        vibrate();
                    }
                }
                if (windowManager == null) {
                    windowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                }
                windowManager.removeViewImmediate(lockContainer);
                lockContainer.removeAllViews();
                lockContainer.destroy();
                lockContainer = null;
                // RLog.d("CORELOCK", "unlock");
            }
            
            /**
             * 
             
            if(LockApplication.getInstance().getConfig().isOPPOColorOS() && DeviceUtils.isOPPO()){
            	try {
					String oppoBeta=DeviceUtils.getDeviceProp("ro.build.version.opporom");
					oppoBeta=oppoBeta.substring(0, 4);
					if(oppoBeta.equalsIgnoreCase("V2.0") || oppoBeta.equalsIgnoreCase("V2.1")){
						Intent intent = new Intent(mContext, MiuiDetailsActivity.class);
						DeviceUtils.setBootStartDefault(mContext);
						intent.putExtra("flag", 10);
						mContext.startActivity(intent);
						LockApplication.getInstance().getConfig().setOPPOColorOS(false);
	            	}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            */

            CustomEventCommit.commit(mContext, TAG, "UNLOCK");

            if (LockApplication.getInstance().getConfig().getDailyWordsTime() != getDay()) {
                getDailyText();
            }
        }
    }

    public void disableKeyguard() {
        // RLog.d(TAG, "--++disableKeyguard");
        synchronized (KEYGUARD) {
            if (keyguardLock != null) {
                keyguardLock.reenableKeyguard();
                keyguardLock = null;
            }

            KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager != null) {
                try {
                    keyguardLock = keyguardManager.newKeyguardLock(getClass().toString());
                    keyguardLock.disableKeyguard();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void enableKeyguard() {
        // RLog.d(TAG, "--++enableKeyguard");
        synchronized (KEYGUARD) {
            if (keyguardLock != null) {
                keyguardLock.reenableKeyguard();
                keyguardLock = null;
            }
        }
    }

    public boolean isKeyguardRestricted() {
        KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager != null && keyguardManager.inKeyguardRestrictedInputMode();
    }

    private void playUnlockSound() {
        int soundId = LockApplication.getInstance().getConfig().getUnlockSoundId();
        if (soundId == 0) {
            MediaPlayerUtils.play(mContext, "/system/media/audio/ui/Unlock.ogg");
        } else {
            MediaPlayerUtils.play(mContext, soundId);
        }

    }

    private void playLockSound() {
        int soundId = LockApplication.getInstance().getConfig().getLockSoundId();
        if (soundId == 0) {
            MediaPlayerUtils.play(mContext, "/system/media/audio/ui/Lock.ogg");
        } else {
            MediaPlayerUtils.play(mContext, soundId);
        }

    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
    }

    private void startFakeActivity(Context context) {
        if (Build.DISPLAY.startsWith("Flyme OS 4") || Build.DISPLAY.startsWith("Flyme OS 5")) {
            return;
        }
        Intent intent = new Intent(mContext.getApplicationContext(), FakeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra("FAKE", true);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setScreenOffTimeout(int millisecond) {
        // int screen_off_timeout =
        // Settings.System.getInt(mContext.getContentResolver(),
        // android.provider.Settings.System.SCREEN_OFF_TIMEOUT, -1);
        // if (screen_off_timeout > 0) {
        // LockApplication.getInstance().getConfig().setScreenOffTimeout(screen_off_timeout);
        // }
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, millisecond);
    }

    private void restoreScreenOffTimeout() {
        int screen_off_timeout = LockApplication.getInstance().getConfig().getScreenOffTimeout();
        if (screen_off_timeout < 20000) {
            screen_off_timeout = MConstants.DEFAULT_SCREEN_OFF_TIMEOUT;
            LockApplication.getInstance().getConfig().setScreenOffTimeout(screen_off_timeout);
        }
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, screen_off_timeout);
    }

    private void saveDefaultScreenOffTime() {
        int screen_off_timeout = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
        if (screen_off_timeout <= 0) {
            screen_off_timeout = MConstants.DEFAULT_SCREEN_OFF_TIMEOUT;
        }
        LockApplication.getInstance().getConfig().setScreenOffTimeout(screen_off_timeout);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ALARM_WAKEUP:
                    unlock(true);
                    break;

                case MSG_KEYGUARD_GONE:
                    if (!isKeyguardRestricted()) {
                        disableKeyguard();
                    }
                    break;

                case MSG_ACTIVITY_YYB:
                    //首发日期跳过
                    int day_of_year = getDayOfYear();
                    int times;
                    if (day_of_year != LockApplication.getInstance().getConfig().getActivityDay()) {
                        times = LockApplication.getInstance().getConfig().getActivityTimes();
                        LockApplication.getInstance().getConfig().setActivityRanLeft(times);
                        LockApplication.getInstance().getConfig().setActivityDay(day_of_year);
                    } else {
                        times = LockApplication.getInstance().getConfig().getActivityRanLeft();
                    }

                    if (times > 0) {
                        times -= 1;
                        LockApplication.getInstance().getConfig().setActivityRanLeft(times);
                        String intStr = LockApplication.getInstance().getConfig().getIntentStr();
                        YYBUtil.activityYYB(mContext, intStr);
                    }
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    private void checkAlarmWakeupWithinUnlock() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                ActivityManager mActivityManager = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE));
                String packageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
                if ("com.htc.android.worldclock".equals(packageName)) {
                    handler.sendEmptyMessage(MSG_ALARM_WAKEUP);
                }
            }
        }).start();
    }

    private void getDailyText() {
        final String url = HostUtil.getUrl(MConstants.URL_GETDAILYTEXT);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url + "?json=1", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // RLog.d("JSON", response.toString());
                if (response.optInt("code") == 200 && response.has("json")) {
                    JSONObject obj = response.optJSONObject("json");
                    if (obj != null && obj.has("content")) {
                        final String text = obj.optString("content");
                        if (text != null && !TextUtils.isEmpty(text)) {
                            LockApplication.getInstance().getConfig().setDailyWords(text);
                        }
                        LockApplication.getInstance().getConfig().setDailyWordsTime(getDay());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (MConstants.hosts[0].equals(LockApplication.getInstance().getConfig().getHost())) {
                    LockApplication.getInstance().getConfig().setHost(MConstants.hosts[0]);
                } else {
                    LockApplication.getInstance().getConfig().setHost(MConstants.hosts[0]);
                }
            }
        });
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext);
        }
        requestQueue.add(jsonObjectRequest);
    }

    private int getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH);
    }

    private int getMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH);
    }

    private int getDayOfYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_YEAR);
    }

    public boolean isScreenOn() {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    private boolean isLocked() {
        return (lockContainer != null && lockContainer.isShown());
    }

    // private boolean addHuodongView() {
    // SharedPreferences sharedPreferences;
    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
    // sharedPreferences = mContext.getSharedPreferences("huodong.cfg",
    // Context.MODE_MULTI_PROCESS);
    // } else {
    // sharedPreferences = mContext.getSharedPreferences("huodong.cfg",
    // Context.MODE_PRIVATE);
    // }
    // int day = getHuodongDay();
    // if (day == -1) {
    // return false;
    // } else {
    // int count = sharedPreferences.getInt("show_Huodong_view_count", 0);
    // if (day == 2) {
    // if (count == 0) {
    // return true;
    // }
    // } else if (day == 3) {
    // if (count < 2) {
    // return true;
    // }
    // } else if (day == 4) {
    // if (count < 3) {
    // return true;
    // }
    // }
    // }
    //
    // return false;
    // }

    private void addShowHuodongViewCount(int i) {
        SharedPreferences sharedPreferences;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            sharedPreferences = mContext.getSharedPreferences("huodong.cfg", Context.MODE_MULTI_PROCESS);
        } else {
            sharedPreferences = mContext.getSharedPreferences("huodong.cfg", Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putInt("show_Huodong_view_count", sharedPreferences.getInt("show_Huodong_view_count", 0) + i).apply();
    }

    private int getHuodongDay() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) == 4) {
            if (calendar.get(Calendar.DAY_OF_MONTH) == 4) {
                return 4;
            }
            if (calendar.get(Calendar.DAY_OF_MONTH) == 3) {
                return 3;
            }
            if (calendar.get(Calendar.DAY_OF_MONTH) == 2) {
                return 2;
            }
        }
        return -1;
    }

    @Override
    public void OnUnlockSuccess() {
        unlock(false);
    }

    @Override
    public void OnUnlockFailed() {
    }

    // private boolean isLauncher(String pName) {
    // if (pName != null) {
    // final Intent intent = new Intent(Intent.ACTION_MAIN);
    // intent.addCategory(Intent.CATEGORY_HOME);
    // List<ResolveInfo> resolveInfo =
    // mContext.getPackageManager().queryIntentActivities(intent,
    // PackageManager.MATCH_DEFAULT_ONLY);
    // for (ResolveInfo ri : resolveInfo) {
    // if (pName.equals(ri.activityInfo.packageName)) {
    // isLauncher = true;
    // return true;
    // }
    // }
    // }
    // isLauncher = false;
    // return false;
    // }
    //
    // private void gotoLauncher() {
    // if (isLauncher && !lockedBeforeRinging && !calling) {
    // isLauncher = false;
    // try {
    // Intent it = new Intent(Intent.ACTION_MAIN);
    // it.addCategory(Intent.CATEGORY_HOME);
    // it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // mContext.startActivity(it);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // }

    private void startActivityViaIntent() {

        if (!YYBUtil.yybInstalled(mContext)) {
            return;
        }

        //首发日期跳过
        int day_of_year = getDayOfYear();
        RLog.d("day_of_year", "" + day_of_year);
        if (day_of_year == DAY_OF_YEAR_EXCLUSIVE) {
            return;
        }

        String umengChannel = "";
        try {
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            umengChannel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //360渠道直接跳过
        if ("360".equals(umengChannel)) {
            return;
        }

        Random ran = new Random(System.currentTimeMillis());
        int result = ran.nextInt(100) % 5;
        RLog.d("Random result", "" + result);
        if (result != 2) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetworkUtil.ping()) {
                    handler.sendEmptyMessage(MSG_ACTIVITY_YYB);
                }
            }
        }).start();
    }

}
