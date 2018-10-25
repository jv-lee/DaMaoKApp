package com.lockstudio.sticklocker.application;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.lockstudio.sticklocker.model.AppConfig;
import com.lockstudio.sticklocker.model.PopupWindowManager;
import com.lockstudio.sticklocker.util.AppManagerUtils;

import java.util.ArrayList;


public class LockApplication {
    private static LockApplication mInstance;
    private boolean isTable = false;
    private AppConfig config;
    private PopupWindowManager popupWindowManager;
    private AppManagerUtils appManagerUtils;

    public static LockApplication getInstance() {
        if (mInstance == null) {
            mInstance = new LockApplication();
        }
        return mInstance;
    }

    private Application application;


    public void onCreate(Application application) {
        this.application = application;

        Resources localResources = application.getResources();
        if (localResources != null) {
            Configuration localConfiguration = localResources.getConfiguration();
            if (localConfiguration != null) {
                isTable = ((localConfiguration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);
            }
        }

        popupWindowManager = new PopupWindowManager();
        appManagerUtils = new AppManagerUtils(application);
        appManagerUtils.getAllApp();

        config = new AppConfig(application);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            getPackageManager().setComponentEnabledSetting(new ComponentName(this, SystemNotificationService.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
//        }

        try {
//            CCPush.setAccount(getApplicationContext(), DeviceInfoUtils.getIMEI(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断平板的方法
     *
     * @return
     */
    public boolean isTable() {
        return isTable;
    }

    /**
     * 设置信息
     *
     * @return
     */
    public AppConfig getConfig() {
        return config;
    }

    public void setPushTags() {
        ArrayList<String> tags = new ArrayList<String>();
        tags.add(Build.BRAND);
        tags.add("SDK:" + Build.VERSION.SDK_INT);
        try {
            PackageInfo pi = application.getPackageManager().getPackageInfo(application.getPackageName(), 0);
            tags.add(pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tags.add(config.isEnabled() ? "Enabled" : "Disabled");
        try {
//            CCPush.setTags(getApplicationContext(), tags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PopupWindowManager getPopupWindowManager() {
        return popupWindowManager;
    }

    public AppManagerUtils getAppManagerUtils() {
        return appManagerUtils;
    }
}
