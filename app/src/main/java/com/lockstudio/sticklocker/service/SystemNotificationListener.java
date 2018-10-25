package com.lockstudio.sticklocker.service;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.RLog;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class SystemNotificationListener extends NotificationListenerService {

    private static final String TAG = "V5_SYSTEMNOTIFICATIONLISTENER";

//    protected static SystemNotificationListener mInstance;

//    public static SystemNotificationListener getInstance() {
//        return mInstance;
//    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        startService(new Intent(this, CoreService.class));
        RLog.i("onNotificationPosted", sbn + "");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        startService(new Intent(this, CoreService.class));
        RLog.i("onNotificationRemoved", sbn + "");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mInstance = this;
//        LockApplication.getInstance().getConfig().setReboot(true);
        startService(new Intent(this, CoreService.class));
        CustomEventCommit.commit(this, TAG, "onCreate");
        RLog.i("SystemNotificationListener", "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mInstance = null;
    }


    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    public static boolean isEnabled(final Context context) {
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
