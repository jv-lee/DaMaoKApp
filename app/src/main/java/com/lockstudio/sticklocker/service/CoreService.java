package com.lockstudio.sticklocker.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.core.CoreLock;
import com.lockstudio.sticklocker.util.DmUtil;
import com.lockstudio.sticklocker.util.MConstants;

import java.io.File;
import java.util.ArrayList;

import cn.opda.android.activity.R;

public class CoreService extends Service {

    private CoreLock coreLock = null;
    
    public static ArrayList<String> donwloadPath = new ArrayList<String>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initCore();

        improvePriority();

        //服务意外重启后 恢复锁屏状态
        if (LockApplication.getInstance().getConfig().isEnabled() && coreLock != null) {
//            long time = LockApplication.getInstance().getConfig().getElapsedRealtime();
            if (LockApplication.getInstance().getConfig().isReboot()
//                    || (time != 0 && time > SystemClock.elapsedRealtime())
                    || !coreLock.isScreenOn()) {
                LockApplication.getInstance().getConfig().setReboot(false);
                coreLock.lock(this, true);
            }
        }

        String dmPath = getFilesDir() + "/daemon";
        DmUtil.killDaemon(DmUtil.getDaemons(dmPath));
        DmUtil.saveDaemon(this, new File(dmPath), R.raw.daemon, false);
        DmUtil.exec(dmPath + " " + getPackageName() + "/" + getClass().getName());

        try {
        	//Gavan去掉了cocospush
//        CCPush.setDebugMode(true);
//            CCPush.init(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        worsenPriority();
        unregisterReceiver(broadcastReceiver);
        coreLock.onDestroy();
        coreLock = null;
        try {
        	//Gavan去掉了cocospush
//            CCPush.stopPush(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        startService(new Intent(this, CoreService.class));
    }

    private void initCore() {
        if (coreLock != null) {
            return;
        }

        coreLock = new CoreLock(this);

        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(MConstants.ACTION_PHONE_STATE);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        //自定义广播
        filter.addAction(MConstants.ACTION_LOCK_NOW);
        filter.addAction(MConstants.ACTION_DISABLE_KEYGUARD);
        filter.addAction(MConstants.ACTION_ENABLE_KEYGUARD);
        filter.addAction(MConstants.ACTION_FAKE_ACTIVITY_CREAT_DONE);

        // 闹钟广播
        filter.addAction("com.android.deskclock.ALARM_ALERT");
        filter.addAction("com.android.deskclock.ALARM_DONE");
        filter.addAction("com.samsung.sec.android.clockpackage.alarm.ALARM_ALERT");// 三星
        filter.addAction("com.htc.worldclock.ALARM_ALERT");// HTC
        filter.addAction("com.android.alarmclock.ALARM_ALERT");// 魅族
        filter.addAction("com.nubia.deskclock.ALARM_ALERT");// nubia
        filter.addAction("com.cn.google.AlertClock.ALARM_ALERT");// vivo Y12 BBK
        filter.addAction("com.sonyericsson.alarm.ALARM_ALERT");// sony
        filter.addAction("com.oppo.alarmclock.alarmclock.ALARM_ALERT");// OPPO

        registerReceiver(broadcastReceiver, filter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (coreLock != null) {
                coreLock.onReceive(context, intent);
            }
//            Log.i("__________", intent.toString());
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//
//                Iterator<String> it = bundle.keySet().iterator();
//                while (it.hasNext()) {
//                    String key = it.next();
//                    Log.i("__________", key + "=" + bundle.get(key));
//                }
//            }
        }
    };


    @SuppressLint("NewApi")
	private void improvePriority() {
        Notification notification = new Notification();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            notification.priority = Notification.PRIORITY_MIN;
        }
        startForeground(0, notification);

        final Intent it = new Intent();
        it.setAction("android.intent.action.BOOST_DOWNLOADING");
        it.putExtra("package_name", "com.android.contacts");
        it.putExtra("enabled", true);
        //开机完成标志
        it.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        sendBroadcast(it);
    }

    private void worsenPriority() {
        stopForeground(true);

        final Intent it = new Intent();
        it.setAction("android.intent.action.BOOST_DOWNLOADING");
        it.putExtra("package_name", "com.android.contacts");
        it.putExtra("enabled", false);
        sendBroadcast(it);
    }

//    private void startAlarm(int second) {
//        Intent intent = new Intent(this, CoreService.class);
//        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.SECOND, second);
//
//        // Schedule the alarm!
//        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
////        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), second * 1000, sender);
//    }
//
//    private void cancelAlarm() {
//        Intent intent = new Intent(this, CoreService.class);
//        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
//
//        // And cancel the alarm.
//        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//        am.cancel(sender);
//    }
}
