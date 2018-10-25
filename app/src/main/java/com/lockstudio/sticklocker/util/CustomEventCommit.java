package com.lockstudio.sticklocker.util;

import android.content.Context;


import java.util.HashMap;

public class CustomEventCommit {
    public static final String download = "download";
    public static final String install = "install";
    public static final String share = "share";

    /**
     * @param mContext
     * @param event    事件
     * @param appName  用于提交名称
     */
    public static void commit(Context mContext, String event, String appName, boolean immediate) {

        HashMap<String, String> hashMap = new HashMap<String, String>();
        if (appName == null) {
            appName = "null";
        }
        hashMap.put("appname", appName);
        if (immediate) {
        }

        RLog.d("CustomEventCommit", "event:" + event + "::appName:" + appName);
    }

    public static void commit(Context mContext, String event, String appName) {
        commit(mContext, event, appName, true);
    }

    public static void commitEvent(Context context, String eventId, String key, String value, boolean immediate) {
        HashMap<String, String> map_value = new HashMap<String, String>();
        map_value.put(key, value);
        if (immediate) {
        }
    }

    public static void commitEvent(Context context, String eventId, String key, String value) {
        commitEvent(context, eventId, key, value, true);
    }
    
    public static void commitEvent(Context context, String eventId,String value) {
        commitEvent(context, eventId, "event_key", value, true);
    }
}
