package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.lockstudio.sticklocker.application.LockApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceInfoUtils {
	/**
	 * 判断sd卡是否被挂载
	 * 
	 * @return true:挂载 false：未挂载
	 */
	public static boolean sdMounted() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public static int getStatusBarHeight(Context context) {
		int statusBarHeight = 0;
		/**** 获取状态栏高度 ****/
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return statusBarHeight;
	}
	
	public static int getNavigationBarHeight(Context mContext) {
		// int height = 0;
		// if(DeviceUtils.hasSmartBar()){
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		// {
		//
		// if (!ViewConfiguration.get(mContext).hasPermanentMenuKey()) {
		// Resources resources = mContext.getResources();
		// int resourceId = resources.getIdentifier("navigation_bar_height",
		// "dimen", "android");
		// // 获取NavigationBar的高度
		// height = resources.getDimensionPixelSize(resourceId);
		// if (height == 0) {
		// height = getDeviceHeight2(mContext) - getScreenShowHeight(mContext);
		// }
		// }
		//
		// }
		// }else{
		// height = getDeviceHeight2(mContext) - getScreenShowHeight(mContext);
		// }
		// return height;
		return LockApplication.getInstance().getConfig().getNavigationBarHeight();
	}

	public static String getDeviceModel() {
		return Build.MODEL;
	}

	public static String getAndroidVersion() {
		return VERSION.RELEASE;
	}

	public static int getAndroidSDK() {
		return VERSION.SDK_INT;
	}

	public static int getDeviceWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		boolean isRotation = false;
		int rotation = wm.getDefaultDisplay().getRotation();
		if (!(rotation == 3 || rotation == 1)) {
			isRotation = true;
		}

		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return isRotation ? dm.widthPixels : dm.heightPixels;
	}

	public static int getDeviceHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		
		
		boolean isRotation = false;
		int rotation = wm.getDefaultDisplay().getRotation();
		if (!(rotation == 3 || rotation == 1)) {
			isRotation = true;
		}
		
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return isRotation ? dm.heightPixels : dm.widthPixels;
	}

	public static int getDeviceHeight2(Context context) {
		WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		boolean isRotation = false;
		int rotation = wm.getDefaultDisplay().getRotation();
		if (!(rotation == 3 || rotation == 1)) {
			isRotation = true;
		}
		Point point = new Point();
		if (VERSION.SDK_INT >= 17) {
			wm.getDefaultDisplay().getRealSize(point);
			return isRotation ? point.y : point.x;
		} else {
			try {
				return ((Integer) (isRotation != true ? Display.class.getMethod("getRawWidth", new Class[0]) : Display.class.getMethod("getRawHeight",
						new Class[0])).invoke(wm.getDefaultDisplay(), new Object[0])).intValue();
			} catch (Exception e) {
				if (VERSION.SDK_INT >= 13) {
					wm.getDefaultDisplay().getSize(point);
					return isRotation ? point.y : point.x;
				} else {
					return getDeviceHeight(context);
				}
			}
		}
	}


	/**
	 * 添加公共参数
	 * 
	 * @param context
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject getInfoNode(Context context) throws JSONException {
		List<String> list = DeviceInfoUtils.runComm("getprop");
		if (list == null || list.size() == 0) {
			list = DeviceInfoUtils.runComm("cat /system/build.prop");
		}
		Map<String, String> m = new HashMap<String, String>();
		for (String item : list) {
			if (item.startsWith("[")) {
				String[] strings = item.split("\\]: \\[");
				if (strings != null && strings.length == 2)
					m.put(strings[0].substring(1), strings[1].substring(0, strings[1].length() - 1));
			} else {
				String[] strings = item.split("=");
				if (strings != null && strings.length == 2)
					m.put(strings[0], strings[1]);
			}
		}
		JSONObject root = new JSONObject();
		root.put("product_brand", getV(m, "ro.product.brand"));
		root.put("product_manufacturer", getV(m, "ro.product.manufacturer"));
		root.put("product_model", getV(m, "ro.product.model"));
		root.put("product_board", getV(m, "ro.product.board"));
		root.put("product_device", getV(m, "ro.product.device"));
		root.put("product_name", getV(m, "ro.product.name"));
		root.put("build_product", getV(m, "ro.build.product"));
		root.put("board_platform", getV(m, "ro.board.platform"));
		root.put("hardware", getV(m, "ro.hardware"));
		root.put("build_release", getV(m, "ro.build.version.release"));
		root.put("sdk", getV(m, "ro.build.version.sdk"));
		root.put("build_display_id", getV(m, "ro.build.display.id"));
		root.put("build_date_utc", getV(m, "ro.build.date.utc"));
		root.put("is_root", 0);
		root.put("build_prop", HASH.md5sumWithFile("/system/build.prop"));
		return root;
	}

	/**
	 * 添加请求的公共参数
	 * 
	 * @param did
	 * @param deviceid
	 * @param value
	 * @param context
	 * @throws JSONException
	 */
	public static void addDeviceNode(long did, long deviceid, JSONObject value, Context context) throws JSONException {
		if (deviceid > 0) {
			value.put("devices_id", deviceid);
		}
		if (did > 0) {
			value.put("did", did);
		} else {
			value.put("imei", DeviceInfoUtils.getIMEI(context));
			value.put("wifimac", DeviceInfoUtils.getMacAddress(context));
			if (value.isNull("imei")) {
				value.put("imei", "");
			}
			if (value.isNull("wifimac")) {
				value.put("wifimac", "");
			}
		}
		value.put("device", new JSONObject().put("info", getInfoNode(context)));
	}

	private static String getV(Map<String, String> mapSource, String key) {
		String value = mapSource.get(key);
		if (TextUtils.isEmpty(value))
			value = "";
		return value;
	}

	public static ArrayList<String> runComm(String comm) {
		Process process = null;
		ArrayList<String> resultStr = new ArrayList<String>();
		String line = null;
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
			} catch (IOException e) {
			}
		}
		return resultStr;
	}

	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	public static String getIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}

	public static String getMacAddress(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getMacAddress();
	}

}