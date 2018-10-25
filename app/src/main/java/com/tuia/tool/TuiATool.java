package com.tuia.tool;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class TuiATool {

	/**
	 * Gzip 压缩数据
	 * 
	 * @param unGzipStr
	 * @return
	 */
	public static String compressForGzip(String unGzipStr) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(baos);
			gzip.write(unGzipStr.getBytes());
			gzip.close();
			byte[] encode = baos.toByteArray();
			baos.flush();
			baos.close();
			return encode(encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * ********************************************************************************************
	 * Base64 encoding
	 */
	private static final char last2byte = (char) Integer
			.parseInt("00000011", 2);
	private static final char last4byte = (char) Integer
			.parseInt("00001111", 2);
	private static final char last6byte = (char) Integer
			.parseInt("00111111", 2);
	private static final char lead6byte = (char) Integer
			.parseInt("11111100", 2);
	private static final char lead4byte = (char) Integer
			.parseInt("11110000", 2);
	private static final char lead2byte = (char) Integer
			.parseInt("11000000", 2);
	private static final char[] encodeTable = new char[] { 'A', 'B', 'C', 'D',
			'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
			'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', '+', '/' };

	/**
	 * Base64 encoding.
	 * 
	 * @param from
	 *            The src data.
	 * @return
	 */
	public static String encode(byte[] from) {
		StringBuffer to = new StringBuffer((int) (from.length * 1.34) + 3);
		int num = 0;
		char currentByte = 0;
		for (int i = 0; i < from.length; i++) {
			num = num % 8;
			while (num < 8) {
				switch (num) {
				case 0:
					currentByte = (char) (from[i] & lead6byte);
					currentByte = (char) (currentByte >>> 2);
					break;
				case 2:
					currentByte = (char) (from[i] & last6byte);
					break;
				case 4:
					currentByte = (char) (from[i] & last4byte);
					currentByte = (char) (currentByte << 2);
					if ((i + 1) < from.length) {
						currentByte |= (from[i + 1] & lead2byte) >>> 6;
					}
					break;
				case 6:
					currentByte = (char) (from[i] & last2byte);
					currentByte = (char) (currentByte << 4);
					if ((i + 1) < from.length) {
						currentByte |= (from[i + 1] & lead4byte) >>> 4;
					}
					break;
				}
				to.append(encodeTable[currentByte]);
				num += 6;
			}
		}
		if (to.length() % 4 != 0) {
			for (int i = 4 - to.length() % 4; i > 0; i--) {
				to.append("=");
			}
		}
		return to.toString();
	}

	/*
	 * ********************************************************************************************
	 * sha1
	 */

	public static String sha1(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16)
					.substring(1));
		}

		return sb.toString();
	}

	/*
	 * ********************************************************************************************
	 * 获取经纬度
	 */

	private static LocationManager locationManager;
	private static String locationProvider;
	public static Location _location;

	/**
	 * 调用本地GPS来获取经纬度
	 * 
	 * @param context
	 */
	public static Location getLocation(Context context) {
		// 1.获取位置管理器
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// 2.获取位置提供器，GPS或是NetWork
		List<String> providers = locationManager.getProviders(true);
		if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
			// 如果是网络定位
			locationProvider = LocationManager.NETWORK_PROVIDER;
		} else if (providers.contains(LocationManager.GPS_PROVIDER)) {
			// 如果是GPS定位
			locationProvider = LocationManager.GPS_PROVIDER;
		} else if (providers.contains(LocationManager.PASSIVE_PROVIDER)) {
			// 如果是PASSIVE定位
			locationProvider = LocationManager.PASSIVE_PROVIDER;
		} else {
			// Toast.makeText(context, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
			// return;
		}

		// 3.获取上次的位置，一般第一次运行，此值为null
//		if (ActivityCompat.checkSelfPermission(context,
//				Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//				&& ActivityCompat.checkSelfPermission(context,
//						Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//			// TODO: Consider calling
//			// ActivityCompat#requestPermissions
//			// here to request the missing permissions, and then overriding
//			// public void onRequestPermissionsResult(int requestCode, String[]
//			// permissions,
//			// int[] grantResults)
//			// to handle the case where the user grants the permission. See the
//			// documentation
//			// for ActivityCompat#requestPermissions for more details.
//			// return;
//		}
		_location = locationManager.getLastKnownLocation(locationProvider);
		if (_location != null) {
			return _location;
		} else {
			// 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
			locationManager.requestLocationUpdates(locationProvider, 0, 0,
					locationListener);
			return null;
		}
	}

	static LocationListener locationListener = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		// 如果位置发生变化，重新显示
		@Override
		public void onLocationChanged(Location location) {
			_location = location;
			removeLocationUpdatesListener();
		}
	};

	/**
	 * 获取经纬度
	 * 
	 * @param location
	 */
	private static void showLocation(Location location) {
		String longtitude = String.valueOf(location.getLongitude());
		String latitude = String.valueOf(location.getLatitude());
		System.out.println("经纬度信息：" + longtitude + "  " + latitude);
	}

	// 移除定位监听
	public static void removeLocationUpdatesListener() {
		// 需要检查权限,否则编译不过
		// if (Build.VERSION.SDK_INT >= 23 &&
		// ActivityCompat.checkSelfPermission( mContext,
		// Manifest.permission.ACCESS_FINE_LOCATION ) !=
		// PackageManager.PERMISSION_GRANTED &&
		// ActivityCompat.checkSelfPermission( mContext,
		// Manifest.permission.ACCESS_COARSE_LOCATION ) !=
		// PackageManager.PERMISSION_GRANTED) {
		// return;
		// }
		if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
		}
	}

	/*
	 * ********************************************************************************************
	 * 获取非系统APP列表
	 */
	public static String getPackagename(Context context) {
		String apps = "";
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = context.getPackageManager()
				.getInstalledPackages(0);
		for (PackageInfo packageInfo : packageInfos) {
			/*
			 * if ((!includeSystemApps) && null == packageInfo.versionName) {
			 * continue; }
			 */
			// 判断是否为非系统预装的应用 (大于0为系统预装应用，小于等于0为非系统应用)

			if ((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) > 0) {
				continue;
			}
			String packageName = packageInfo.packageName;
			apps = apps + packageName + ",";

		}
		apps = apps.substring(0,apps.length() - 1);
		return apps;
	}
	
	/**
	 * 判断是否安装了该包名的应用
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isInstallApp(Context context, String packageName) {
		List<PackageInfo> packageInfos = context.getPackageManager()
				.getInstalledPackages(0);
		for (PackageInfo packageInfo : packageInfos) {
			/*
			 * if ((!includeSystemApps) && null == packageInfo.versionName) {
			 * continue; }
			 */
			// 判断是否为非系统预装的应用 (大于0为系统预装应用，小于等于0为非系统应用)

			if ((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) > 0) {
				continue;
			}
			String name = packageInfo.packageName;
			if (name.equals(packageName)) {
				return true;
			}

		}
		return false;
	}
	
	/*
	 * ********************************************************************************************
	 * 判断服务是否开启
	 */
	/** 
     * 判断服务是否开启 
     *  
     * @return 
     */  
    public static boolean isServiceRunning(Context context, String ServiceName) {  
        if (("").equals(ServiceName) || ServiceName == null)  
            return false;  
        ActivityManager myManager = (ActivityManager) context  
                .getSystemService(Context.ACTIVITY_SERVICE);  
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager  
                .getRunningServices(30);  
        for (int i = 0; i < runningService.size(); i++) {  
            if (runningService.get(i).service.getClassName().toString()  
                    .equals(ServiceName)) {  
                return true;  
            }  
        }  
        return false;  
    }  
}
