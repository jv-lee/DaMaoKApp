package com.lockstudio.sticklocker.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.lockstudio.sticklocker.activity.LockThemePreviewActivity;
import com.lockstudio.sticklocker.service.CoreService;
import com.lockstudio.sticklocker.util.AppOpsUtils;
import com.lockstudio.sticklocker.util.ChooseStickerUtils;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.FileUtils;
import com.lockstudio.sticklocker.util.MConstants;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Tommy on 15/3/14.
 */
public class AppConfig {

    private Context context;

    private int screenWidth, screenHeight;

    public AppConfig(Context context) {
        this.context = context;

        screenWidth = DeviceInfoUtils.getDeviceWidth(context);
        screenHeight = DeviceInfoUtils.getDeviceHeight2(context);

        screenWidth = Math.min(screenWidth, screenHeight);
        screenHeight = Math.max(screenWidth, screenHeight);
    }

    private SharedPreferences getSharedPreferences() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return context.getSharedPreferences("settings.cfg", Context.MODE_MULTI_PROCESS);
        } else {
            return context.getSharedPreferences("settings.cfg", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
        }
    }

    public int getLastGuideVersion() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getInt("LAST_GUIDE_VER", -1);
    }

    public void setLastGuideVersion(int lastGuideVersion) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putInt("LAST_GUIDE_VER", lastGuideVersion).commit();
    }

    public boolean isEnabled() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("ENABLED", true);
    }

    public void setEnabled(boolean isEnabled) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("ENABLED", isEnabled).commit();
        if (isEnabled) {
            context.sendBroadcast(new Intent(MConstants.ACTION_DISABLE_KEYGUARD));
        } else {
            context.sendBroadcast(new Intent(MConstants.ACTION_ENABLE_KEYGUARD));
        }
    }

    public boolean isPlaySound() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("PLAY_SOUND", true);
    }

    public void setPlaySound(boolean isPlaySound) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("PLAY_SOUND", isPlaySound).commit();
    }

    public boolean isPlayLockSound() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("PLAY_LOCK_SOUND", true);
    }

    public void setPlayLockSound(boolean isPlayLockSound) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("PLAY_LOCK_SOUND", isPlayLockSound).commit();
    }

    public boolean isVibrate() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("VIBRATE", false);
    }

    public void setVibrate(boolean isVibrate) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("VIBRATE", isVibrate).commit();
    }

    public boolean isReboot() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("isReboot", false);
    }

    public void setReboot(boolean isReboot) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("isReboot", isReboot).commit();
    }

    public int getScreenOffTimeout() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getInt("SCREEN_OFF_TIMEOUT", MConstants.DEFAULT_SCREEN_OFF_TIMEOUT);
    }

    public void setScreenOffTimeout(int screenOffTimeout) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putInt("SCREEN_OFF_TIMEOUT", screenOffTimeout).commit();
    }

    public String getThemeName() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getString("THEME", "");
    }

    public void setThemeName(final String themePath, final boolean sendBroadcast) {
        final SharedPreferences sharedPreferences = getSharedPreferences();
        if (TextUtils.isEmpty(themePath)) {
            sharedPreferences.edit().putString("THEME", themePath).commit();
        } else {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("ENABLED", true);

                    String lastThemePath = getThemeName();
                    if (new File(lastThemePath).exists()) {
                        if (new File(lastThemePath).getParent().equals(context.getFilesDir().getAbsolutePath())) {
                            FileUtils.deleteFileByPath(lastThemePath);
                        }
                    }

                    File targetFile = new File(context.getFilesDir(), new File(themePath).getName());
                    try {
                        FileUtils.copyFolder(new File(themePath), targetFile);
                        editor.putString("THEME", targetFile.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        editor.putString("THEME", themePath);
                    }
                    editor.commit();

                    context.startService(new Intent(context, CoreService.class));

                    context.sendBroadcast(new Intent(MConstants.ACTION_UPDATE_LOCAL_THEME));

                    if (sendBroadcast) {
                        context.sendBroadcast(new Intent(MConstants.ACTION_LOCK_NOW));
                    }else{
                		Intent intent = new Intent(context, LockThemePreviewActivity.class);
                		intent.putExtra("theme_path", themePath);
                		intent.putExtra("theme_diy", true);
                		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                		context.startActivity(intent);
                    }
                }
            }).start();
        }
    }

    public String getPassword() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getString("PASSWORD", "");
    }

    public void setPassword(String password) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putString("PASSWORD", password).commit();
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public String getHost() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getString("HOST", MConstants.hosts[0]);
    }

    public void setHost(String host) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putString("HOST", host).commit();
    }

    public String getDailyWords() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getString("DAILY_WORDS", "今天的心情是什么样子呢？");
    }

    public void setDailyWords(String dailyWords) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putString("DAILY_WORDS", dailyWords).commit();
    }

    public int getDailyWordsTime() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getInt("DAILY_WORDS_TIME", -1);
    }

    public void setDailyWordsTime(int dailyWordsTime) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putInt("DAILY_WORDS_TIME", dailyWordsTime).commit();
    }

//	public boolean isLocked() {
//        SharedPreferences sharedPreferences = getSharedPreferences();
//		return sharedPreferences.getBoolean("LOCKED", false);
//	}
//
//	public void setLocked(boolean isLocked) {
//        SharedPreferences sharedPreferences = getSharedPreferences();
//		sharedPreferences.edit().putBoolean("LOCKED", isLocked).commit();
//	}

    public boolean isGuide_setting_watch_service_done() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("GUIDE_SETTING_WATCH_SERVICE_DONE", false);
    }

    public void setGuide_setting_watch_service_done(boolean guide_setting_auto_start_done) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("GUIDE_SETTING_WATCH_SERVICE_DONE", guide_setting_auto_start_done).commit();
    }
    
    public boolean isGuide_setting_boot_protected_done() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("GUIDE_SETTING_BOOT_PROTECTED_DONE", false);
    }

    public void setGuide_setting_boot_protected_done(boolean guide_setting_auto_start_done) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("GUIDE_SETTING_BOOT_PROTECTED_DONE", guide_setting_auto_start_done).commit();
    }
    
    public boolean isGuide_setting_auto_start_done() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("GUIDE_SETTING_AUTO_START_DONE", false);
    }

    public void setGuide_setting_auto_start_done(boolean guide_setting_auto_start_done) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("GUIDE_SETTING_AUTO_START_DONE", guide_setting_auto_start_done).commit();
    }

    public boolean isGuide_setting_turn_off_system_screen_lock_done() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("GUIDE_SETTING_TURN_OFF_SYSTEM_SCREEN_LOCK_DONE", false);
    }

    public void setGuide_setting_turn_off_system_screen_lock_done(boolean guide_setting_turn_off_system_screen_lock_done) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("GUIDE_SETTING_TURN_OFF_SYSTEM_SCREEN_LOCK_DONE", guide_setting_turn_off_system_screen_lock_done).commit();
    }

    public boolean isGuide_setting_turn_on_window_manager_done() {
    	if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
    		return AppOpsUtils.checkOp(context, AppOpsUtils.OP_SYSTEM_ALERT_WINDOW);
    	}
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("GUIDE_SETTING_TURN_ON_WINDOW_MANAGER_DONE", false);
    }

    public void setGuide_setting_turn_on_window_manager_done(boolean guide_setting_turn_on_window_manager_done) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("GUIDE_SETTING_TURN_ON_WINDOW_MANAGER_DONE", guide_setting_turn_on_window_manager_done).commit();
    }

    public int getFrom_id() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getInt("image_pick_from_id", ChooseStickerUtils.FROM_STICKER);
    }

    public void setFrom_id(int from_id) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putInt("image_pick_from_id", from_id).commit();
    }

    public long getUpdateTime() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getLong("updateTime", 0);
    }

    public void setUpdateTime() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putLong("updateTime", Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).commit();
    }

    public long getElapsedRealtime() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getLong("ELAPSED_REAL_TIME", 0);
    }

    public void setElapsedRealtime(long elapsedRealtime) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putLong("ELAPSED_REAL_TIME", elapsedRealtime).commit();
    }

    public String getLocalCity() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getString("city", "");
    }

    public void setLocalCity(String city) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putString("city", city).commit();
    }

    public boolean isHao123() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("HAO", false);
    }

    public void setHao123(boolean hao123) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("HAO", hao123).commit();
    }

    public int getHaoType() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getInt("HAO_TYPE", 2);
    }

    public void setHaoType(int haoType) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putInt("HAO_TYPE", haoType).commit();
    }

    public String getHao123Url() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getString("HAO_URL", "http://m.hao123.com/?union=1&from=1012771b&tn=ops1012771b");
    }

    public void setHao123Url(String hao123Url) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putString("HAO_URL", hao123Url).commit();
    }

    public boolean isShowStatusBar() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("SHOW_STATUS", false);
    }

    public void setShowStatusBar(boolean showStatusBar) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("SHOW_STATUS", showStatusBar).commit();
    }
    
    public boolean isShowPhoneSms() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("SHOW_PHONE_SMS", false);
    }

    public void setShowPhoneSms(boolean showPhoneSms) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("SHOW_PHONE_SMS", showPhoneSms).commit();
    }
    
    public boolean isOPPOColorOS() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("IS_OPPO_COLOROS", true);
    }

    public void setOPPOColorOS(boolean oppoColorOS) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("IS_OPPO_COLOROS", oppoColorOS).commit();
    }
    
    public boolean isShowPhoneSms_face() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean("SHOW_PHONE_SMS_FACE", true);
    }

    public void setShowPhoneSms_face(boolean showPhoneSms) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("SHOW_PHONE_SMS_FACE", showPhoneSms).commit();
    }

    public int getActivityTimes() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getInt("ACTIVITY_TIMES", 3);
    }

    public void setActivityTimes(int activity_times) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putInt("ACTIVITY_TIMES", activity_times).commit();
    }

    public int getActivityDay() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getInt("ACTIVITY_DAY", -1);
    }

    public void setActivityDay(int activity_day) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putInt("ACTIVITY_DAY", activity_day).commit();
    }

    public int getActivityRanLeft() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getInt("ACTIVITY_LEFT", 3);
    }

    public void setActivityRanLeft(int activity_left_times) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putInt("ACTIVITY_LEFT", activity_left_times).commit();
    }

    public String getIntentStr() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        Random ran = new Random(System.currentTimeMillis());
        int r = ran.nextInt(100) % 3;
        if (r == 0) {
            return sharedPreferences.getString("INTENT_STR", "tmast://appdetails?hostpname=lock&pname=com.zhiqupk.ziti&oplist=0&via=ANDROIDZDS.YYB.GIFT");
        } else if (r == 1) {
            return sharedPreferences.getString("INTENT_STR", "tmast://appdetails?hostpname=lock&pname=cn.com.opda.android.clearmaster&oplist=0&via=ANDROIDZDS.YYB.GIFT");
        }
        return sharedPreferences.getString("INTENT_STR", "tmast://appdetails?hostpname=lock&pname=com.google.android.ears&oplist=0&via=ANDROIDZDS.YYB.GIFT");
//        return sharedPreferences.getString("INTENT_STR", "tmast://webview?url=http%3a%2f%2fappicsh.qq.com%2fcgi-bin%2fappstage%2fmyapp_task_center.cgi%3fversion%3d1%26pf%3dtma%26tpl%3d1%26fromvia%3dANDROIDZDS.YYB.URL");
    }

    public void setIntentStr(String intentStr) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putString("INTENT_STR", intentStr).commit();
    }

    public int getLockSoundId() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getInt("lockSoundId", 0);
    }

    public void setLockSoundId(int soundId) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putInt("lockSoundId", soundId).commit();
    }


    public int getUnlockSoundId() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getInt("unlockSoundId", 0);
    }

    public void setUnlockSoundId(int unlockSoundId) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putInt("unlockSoundId", unlockSoundId).commit();
    }
    
    public String getPush2000Url() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getString("push_2000_url", "");
    }

    public void setPush2000Url(String url) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putString("push_2000_url", url).commit();
    }
    
    public int getNavigationBarHeight() {
		SharedPreferences sharedPreferences = getSharedPreferences();
		return sharedPreferences.getInt("NavigationBarHeight", 0);
	}

	public void setNavigationBarHeight(int height) {
		if (height < 0) {
			height = 0;
		}
		SharedPreferences sharedPreferences = getSharedPreferences();
		sharedPreferences.edit().putInt("NavigationBarHeight", height).commit();
	}
}
