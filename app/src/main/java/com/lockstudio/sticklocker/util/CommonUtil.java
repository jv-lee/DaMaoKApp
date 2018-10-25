package com.lockstudio.sticklocker.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.util.List;

/**
 * Created by Tommy on 15/5/7.
 */
public class CommonUtil {

	/*
	 * need android.permission.ACCESS_BROWSER permission 未测试
	 */
	public static void setBrowserHomePage(Context context, String url) {
		try {
			Context browserContext = context.createPackageContext("com.android.browser", Context.CONTEXT_IGNORE_SECURITY);
			SharedPreferences sp = browserContext.getSharedPreferences("com.android.browser_preferences", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("homepage", url);
			editor.apply();
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void addShortcut(Context mContext, String title, int resId, String packageName, String cls) {
		Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		// 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加
		shortcutIntent.putExtra("duplicate", false);
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addFlags(270532608);
		// 要启动的应用程序的ComponentName，即应用程序包名+activity的名字
		intent.setComponent(new ComponentName(packageName, cls));

		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(mContext.getResources(), resId));
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, resId);
		mContext.sendBroadcast(shortcutIntent);
	}

	public static void addShortcut(Context mContext, String title, Bitmap icon, String packageName, String cls) {
		Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		// 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加
		shortcutIntent.putExtra("duplicate", false);
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addFlags(270532608);
		// 要启动的应用程序的ComponentName，即应用程序包名+activity的名字
		intent.setComponent(new ComponentName(packageName, cls));

		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
		mContext.sendBroadcast(shortcutIntent);
	}
	/**
	 * 打开浏览器,未测试
	 * @param mContext
	 * @param title
	 * @param icon
	 * @param url
	 */
	public static void addShortcut(Context mContext, String title, Bitmap icon, String url) {
		Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		// 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加
		shortcutIntent.putExtra("duplicate", false);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		intent.addFlags(270532608);
		
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
		mContext.sendBroadcast(shortcutIntent);
	}

	public static void addShortcut(Context mContext, String title, int resId, String url) {
		Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		// 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加
		shortcutIntent.putExtra("duplicate", false);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		intent.addFlags(270532608);

		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(mContext.getResources(), resId));
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, resId);
		mContext.sendBroadcast(shortcutIntent);
	}

	public static void openBrowser(Context context, String url) {
		Intent it= new Intent();
		it.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		it.setData(content_url);
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (isInstall(context, "com.android.browser")) {
			it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
		} else if (isInstall(context, "com.sec.android.app.sbrowser")) {
			it.setClassName("com.sec.android.app.sbrowser", "com.sec.android.app.sbrowser.SBrowserMainActivity");
		} else if (isInstall(context, "com.htc.sense.browser")) {
			it.setClassName("com.htc.sense.browser", "com.htc.sense.browser.BrowserActivity");
		} else if (isInstall(context, "com.UCMobile")) {
			it.setClassName("com.UCMobile", "com.UCMobile.main.UCMobile");
		} else if (isInstall(context, "com.uc.browser")) {
			it.setClassName("com.uc.browser", "com.uc.browser.ActivityUpdate");
		} else if (isInstall(context, "com.tencent.mtt")) {
			it.setClassName("com.tencent.mtt", "com.tencent.mtt.MainActivity");
		} else if (isInstall(context, "com.opera.mini.android")) {
			it.setClassName("com.opera.mini.android", "com.opera.mini.android.Browser");
		}
		try {
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isInstall(Context context, String pName) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> pInfo = pm.getInstalledPackages(0);//获取所有已安装程序的包信息
		for (int i = 0, j = pInfo.size() - 1; i <= j; i++, j--) {
			PackageInfo info = pInfo.get(i);
			if (info.packageName.equals(pName)) {
				return true;
			}
			info = pInfo.get(j);
			if (info.packageName.equals(pName)) {
				return true;
			}
		}
//		for (PackageInfo info : pInfo) {
//			if (info.packageName.equals(pName)) {
//				return true;
//			}
//		}
		return false;
	}
	
	public static void share(Context context,String string) {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, string);
			intent.putExtra(Intent.EXTRA_SUBJECT, "文字锁屏教程分享");
			context.startActivity(Intent.createChooser(intent, "分享"));
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

	}
}
