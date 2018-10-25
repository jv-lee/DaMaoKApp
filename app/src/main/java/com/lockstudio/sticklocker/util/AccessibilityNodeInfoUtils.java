package com.lockstudio.sticklocker.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class AccessibilityNodeInfoUtils {

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static List<AccessibilityNodeInfo> getByText(Context context, AccessibilityNodeInfo accessibilityNodeInfo, String[] textArr, String packagename) {
        for (String str : textArr) {
            String text = AccessibilityUtils.getString(context, str, packagename);
            if (text != null) {
                List<AccessibilityNodeInfo> accessibilitynodeinfos = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
                if (accessibilitynodeinfos != null && accessibilitynodeinfos.size() > 0) {
                    return accessibilitynodeinfos;
                }
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static List<AccessibilityNodeInfo> getByViewId(Context context, AccessibilityNodeInfo accessibilityNodeInfo, String[] idArr) {
        if (VERSION.SDK_INT < 18) {
            return null;
        }
        for (String id : idArr) {
            List<AccessibilityNodeInfo> accessibilitynodeinfos = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
            if (accessibilitynodeinfos != null && accessibilitynodeinfos.size() > 0) {
                return accessibilitynodeinfos;
            }
        }
        return null;
    }
}
