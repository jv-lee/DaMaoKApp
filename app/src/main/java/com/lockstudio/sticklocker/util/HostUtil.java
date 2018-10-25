package com.lockstudio.sticklocker.util;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.AppConfig;

/**
 * Created by Tommy on 15/3/29.
 */
public class HostUtil {

    public static String getUrl(String url) {
        AppConfig appConfig = LockApplication.getInstance().getConfig();
        return appConfig.getHost() + url;
    }
}
