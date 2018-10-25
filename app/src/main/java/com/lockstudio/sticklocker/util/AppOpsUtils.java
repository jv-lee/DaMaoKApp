package com.lockstudio.sticklocker.util;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;

import java.lang.reflect.Method;

public class AppOpsUtils {

	public static final int OP_SYSTEM_ALERT_WINDOW = 24;
	public static final int OP_GET_USAGE_STATS = 43;

	public static boolean checkOp(Context mContext, int op) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			AppOpsManager appOpsManager = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
			try {
				Class<?>[] typeArgs = new Class<?>[] { int.class, int.class, String.class };
				Method checkOpMethod = appOpsManager.getClass().getDeclaredMethod("checkOp", typeArgs);
				Object[] valueArgs = new Object[] { op, Binder.getCallingUid(), mContext.getPackageName() };
				if (AppOpsManager.MODE_ALLOWED == (Integer) checkOpMethod.invoke(appOpsManager, valueArgs)) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
