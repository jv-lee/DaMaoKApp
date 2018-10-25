package com.lockstudio.sticklocker.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.ViewConfiguration;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceUtils {
	public void DeviceUtil() {
	}

	public static boolean hasSmartBar() {
		if (isMeiZu()) {
			try {
				return ((Boolean) Class.forName("android.os.Build").getMethod("hasSmartBar", new Class[0]).invoke(null, new Object[0])).booleanValue();
			} catch (Exception localException) {
				if ((Build.DEVICE.equalsIgnoreCase("mx")) || (Build.DEVICE.equalsIgnoreCase("m9"))) {
					return false;
				}
			}
		}
		return false;
	}

	public static boolean hasVirtualButtons(Context paramContext) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!ViewConfiguration.get(paramContext).hasPermanentMenuKey()) {
				return true;
			}
		}
		return false;
	}

	private static boolean isIntentAvailable(Context paramContext, Intent paramIntent) {
		return paramContext.getPackageManager().queryIntentActivities(paramIntent, 1).size() > 0;
	}

	public static boolean isMeiZu() {
		return ("meizu".equalsIgnoreCase(Build.BRAND));
	}

	public static boolean isSamsung() {
		return ("samsung".equalsIgnoreCase(Build.BRAND)) || ("samsung".equalsIgnoreCase(Build.MANUFACTURER));
	}
	
	public static boolean isOPPO() {
		return ("oppo".equalsIgnoreCase(Build.BRAND)) || ("oppo".equalsIgnoreCase(Build.MANUFACTURER));
	}

	public static void gotoMiuiDetail(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info;
		try {
			info = pm.getPackageInfo(context.getPackageName(), 0);
			Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
			i.setClassName("com.android.settings", "com.miui.securitycenter.permission.AppPermissionsEditor");
			i.putExtra("extra_package_uid", info.applicationInfo.uid);
			context.startActivity(i);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void setBootStart(Context mContext) {
		try {
			Intent intent2 = new Intent();
			intent2.setClassName("com.android.settings", "com.android.settings.Settings$DevelopmentSettingsActivity");
			mContext.startActivity(intent2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setBootStartV6(Context mContext) {
		try {
			Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
			intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setBootStartDefault(Context mContext) {
		try {
			Intent localIntent2 = new Intent("/");
			localIntent2.setComponent(new ComponentName("com.android.settings", "com.android.settings.ChooseLockGeneric"));
			mContext.startActivity(localIntent2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setPureBackgroud(Context mContext) {
		try {
			Intent localIntent2 = new Intent("/");
			localIntent2.setComponent(new ComponentName("com.oppo.purebackground", "com.oppo.purebackground.PurebackgroundTopActivity"));
			mContext.startActivity(localIntent2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isMIUI() {
//		return (Build.BRAND.equals("Xiaomi") | Build.DISPLAY.equals("Xiaomi"));
		try {
			String rom = getDeviceProp("ro.miui.ui.version.name");
			return (DeviceUtils.ROM_MIUI_V5.equals(rom) || DeviceUtils.ROM_MIUI_V6.equals(rom) || DeviceUtils.ROM_MIUI_V7.equals(rom));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String ROM_MIUI_V5 = "V5";
	public static String ROM_MIUI_V6 = "V6";
	public static String ROM_MIUI_V7 = "V7";

	public static void openMiuiPermissionActivity(Context context) {
		Intent intent = null;
		try {
			String rom = getDeviceProp("ro.miui.ui.version.name");
			if (DeviceUtils.ROM_MIUI_V5.equals(rom)) {
				intent = new Intent();
				intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
				intent.setData(Uri.parse("package:" + context.getPackageName()));
				intent.putExtra("cmp", "com.android.settings/.applications.InstalledAppDetails");
				intent.addCategory("android.intent.category.DEFAULT");
			} else if (DeviceUtils.ROM_MIUI_V6.equals(rom)) {
				intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
				intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
				intent.putExtra("extra_pkgname", context.getPackageName());
			}else if (DeviceUtils.ROM_MIUI_V7.equals(rom)) {
				intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
				intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
				intent.putExtra("extra_pkgname", context.getPackageName());
			}

			if (isIntentAvailable(context, intent)) {
				if (context instanceof Activity) {
					Activity a = (Activity) context;
					a.startActivityForResult(intent, 2);
				}
			} else {
				RLog.e("Intent is not available!", true);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public static void openSystemStart_huawei(Context mContext) {
		try {
			Intent intent = new Intent();
			intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void offWindow_huawei(Context mContext) {
		try {
			Intent intent = new Intent();
			intent.setClassName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void openWindow_oppo(Context mContext) {
		try {
			Intent intent = new Intent();
			intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.PermissionTopActivity");
			mContext.startActivity(intent);
		} catch (Exception e) {
//			e.printStackTrace();
			try {
				Intent intent = new Intent();
				intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionTopActivity");
				mContext.startActivity(intent);
			} catch (Exception e1) {
				e.printStackTrace();
			}
		}
	}
	
	public static void openAppManagement(Context mContext){
		Intent intent = new Intent();  
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");  
        Uri uri = Uri.fromParts("package", "cn.opda.android.activity", null);  
        intent.setData(uri);  
        mContext.startActivity(intent);
	}
	
	public static void runWeishi(Context mContext,ComponentName componentName){
		Intent intent1 =  new Intent();  
		intent1.setAction("android.intent.action.MAIN");  
		intent1.setComponent(componentName);
		mContext.startActivity(intent1); 
	}

	public static boolean isHuawei() {
		return ("huawei".equalsIgnoreCase(Build.BRAND)) || ("huawei".equalsIgnoreCase(Build.MANUFACTURER));
	}

	public static boolean emuiVersion3() {
		float version = 0;
		try {
			String emuiVersion = getDeviceProp("ro.build.version.emui");
			if (!TextUtils.isEmpty(emuiVersion)) {
				if (emuiVersion.contains("_")) {
					emuiVersion = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
					try {
						version = Float.parseFloat(emuiVersion);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return (version > 2.0f);
	}

	public static String getDeviceProp(String key) throws JSONException {
		List<String> list = runComm("getprop");
		if (list == null || list.size() == 0) {
			list = runComm("cat /system/build.prop");
		}
		Map<String, String> m = new HashMap<String, String>();
		for (String item : list) {
			if (item.startsWith("[")) {
				String[] strings = item.split("\\]: \\[");
				if (strings.length == 2) m.put(strings[0].substring(1), strings[1].substring(0, strings[1].length() - 1));
			} else {
				String[] strings = item.split("=");
				if (strings.length == 2) m.put(strings[0], strings[1]);
			}
		}

		return getV(m, key);
	}

	private static String getV(Map<String, String> mapSource, String key) {
		String value = mapSource.get(key);
		if (TextUtils.isEmpty(value)) value = "";
		return value;
	}

	public static ArrayList<String> runComm(String comm) {
		Process process = null;
		ArrayList<String> resultStr = new ArrayList<String>();
		String line;
		BufferedReader brout = null;
		try {
			process = Runtime.getRuntime().exec(comm);
			InputStream outs = process.getInputStream();
			InputStreamReader isrout = new InputStreamReader(outs);
			brout = new BufferedReader(isrout, 8 * 1024);
			while ((line = brout.readLine()) != null) {
				resultStr.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (brout != null) {
					brout.close();
				}
				if (process != null) {
					process.destroy();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultStr;
	}

}
