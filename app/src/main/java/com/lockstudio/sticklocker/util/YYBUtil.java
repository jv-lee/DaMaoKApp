package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by Tommy on 15/6/17.
 */
public class YYBUtil {

    public static boolean yybInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo("com.tencent.android.qqdownloader", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void activityYYB(Context context, String tmast) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tmast));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            CustomEventCommit.commit(context, "YYB", tmast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
