package com.lockstudio.sticklocker.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AppManagerUtils {

	private LinkedHashMap<ComponentName, AppInfo> map_all_apps = new LinkedHashMap<ComponentName, AppInfo>();
	private Context mContext;

	public AppManagerUtils(Context mContext) {
		this.mContext = mContext;
	}
	
	public LinkedHashMap<ComponentName, AppInfo> getAppList() {
		if (map_all_apps == null) {

			getAllApp();
		}
		return map_all_apps;
	}
	
	/**
	 * 获取全部的app
	 * 
	 * @param mContext
	 */
	public void getAllApp() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				LinkedHashMap<ComponentName, AppInfo> hashMap = new LinkedHashMap<ComponentName, AppInfo>();
				ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();
				PackageManager pm = mContext.getPackageManager();
				Intent intent = new Intent(Intent.ACTION_MAIN, null);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
				for (int i = 0; i < resolveInfoList.size(); i++) {
					ResolveInfo resolveInfo = resolveInfoList.get(i);
					AppInfo appInfo = new AppInfo();
					appInfo.setPackageName(resolveInfo.activityInfo.packageName);
					appInfo.setAppName(resolveInfo.activityInfo.loadLabel(pm).toString().trim());
					appInfo.setAppIcon(resolveInfo.activityInfo.loadIcon(pm));
					ComponentName componentName = new ComponentName(appInfo.getPackageName(), resolveInfo.activityInfo.name);
					appInfo.setComponentName(componentName);
					appInfo.setChecked(false);
					appInfos.add(appInfo);
				}
//				Collections.sort(appInfos);
				for (int i = 0; i < appInfos.size(); i++) {
					AppInfo appInfo = appInfos.get(i);
					hashMap.put(appInfo.getComponentName(), appInfo);
				}
				map_all_apps.clear();
				map_all_apps.putAll(hashMap);
				appInfos.clear();
				appInfos = null;
				hashMap.clear();
				hashMap = null;
			}
		}).start();
	}
	
	/**
	 * 运行app
	 * 
	 * @param packageName
	 */
	public static void runApp(Context context, String packageName) {
		PackageManager pManager = context.getPackageManager();
		Intent intent = pManager.getLaunchIntentForPackage(packageName);
		if (intent != null) {
			context.startActivity(intent);
		}
	}

	/**
	 * 获取某个应用的LaunchIntent
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static Intent getLaunchIntent(Context context, String packageName) {
		PackageManager pManager = context.getPackageManager();
		return pManager.getLaunchIntentForPackage(packageName);
	}

	/**
	 * 打开某个app的详细设置页面
	 * 
	 * @param cxt
	 * @param packageName
	 */
	public static void openInstalledDetail(Context cxt, String packageName) {
		try {
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
			intent.putExtra("com.android.settings.ApplicationPkgName", packageName);
			intent.putExtra("pkg", packageName);
			cxt.startActivity(intent);
		} catch (Exception e) {
			try {
				Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", packageName, null));
				cxt.startActivity(intent);
			} catch (Exception e1) {
				openUninstaller(cxt, packageName);
			}
		}
	}

	/**
	 * 调用系统卸载某个应用
	 * 
	 * @param cxt
	 * @param packageName
	 */
	public static void openUninstaller(Context cxt, String packageName) {
		Uri packageURI = Uri.parse("package:" + packageName);
		Intent uninstallIntent = new Intent("android.intent.action.DELETE", packageURI);
		cxt.startActivity(uninstallIntent);
	}

	/**
	 * 调用系统安装某个应用
	 * 
	 * @param cxt
	 * @param filePath
	 */
	public static void openInstaller(Context cxt, String filePath) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		File file = new File(filePath);
		if (file.exists() && file.isAbsolute()) {
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			cxt.startActivity(intent);
		}
	}

	/**
	 * 判断应用的安装状态
	 * 
	 * @param mContext
	 * @param packageName
	 * @return true: 已安装
	 */
	public static boolean appInstalled(Context mContext, String packageName) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
		}
		return packageInfo != null ? true : false;
	}
	
	public static boolean installed(Context mContext, ComponentName componentName) {
		AppInfo appInfo = LockApplication.getInstance().getAppManagerUtils().map_all_apps.get(componentName);
		if (appInfo != null) {
			return true;
		} else {
			return false;
		}
	}
	public static void startActivity(Context mContext, ComponentName componentName) {
//		if (!FakeActivity.isRun) {
//			Intent fakeIntent = new Intent(mContext, FakeActivity.class);
//			fakeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			mContext.startActivity(fakeIntent);
//		}
//		Intent intent = new Intent(MConstants.ACTION_BROADCAST_FAKE);
//		intent.putExtra(MConstants.FAKE_START_ACTIVITY_PACKAGE_NAME, componentName.getPackageName());
//		intent.putExtra(MConstants.FAKE_START_ACTIVITY_COMPONENT_NAME, componentName.getClassName());
////		intent.putExtra(MConstants.FAKE_WALLPAPER_KEY, LauncherUtils.getInstance(mContext).getCurPreview());
//		mContext.sendBroadcast(intent);
		try
	    {
	      Intent intent=new Intent();
	      intent.setComponent(componentName);
	      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	      intent.setAction("android.intent.action.MAIN");
	      intent.addCategory("android.intent.category.LAUNCHER");
	      mContext.startActivity(intent);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }

	}
}
