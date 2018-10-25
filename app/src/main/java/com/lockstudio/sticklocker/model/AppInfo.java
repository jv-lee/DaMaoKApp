package com.lockstudio.sticklocker.model;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.lockstudio.sticklocker.util.Trans2PinYin;

public class AppInfo implements Comparable<AppInfo> {

	private String appName;
	private Drawable appIcon;
	private String packageName;
	private boolean isSystemApp;
	private ComponentName componentName;
	private boolean isChecked;
	private String pinyin;

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public ComponentName getComponentName() {
		return componentName;
	}

	public void setComponentName(ComponentName componentName) {
		this.componentName = componentName;
	}

	public boolean isSystemApp() {
		return isSystemApp;
	}

	public void setSystemApp(boolean isSystemApp) {
		this.isSystemApp = isSystemApp;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		if (!TextUtils.isEmpty(appName) && appName.length() > 0) {

			// 如果第一个首字母不是A-Z。a-z就把第一个转成#
			String strOldPinYin = Trans2PinYin.trans2PinYin(appName);
			String strCasePinYin = strOldPinYin;
			if (strOldPinYin.charAt(0) <= 'z' && strOldPinYin.charAt(0) >= 'a') {
				//strCasePinYin = strOldPinYin.charAt(0) - 32 + strOldPinYin.substring(1);
				if(strOldPinYin.length()>2){
					strCasePinYin = strOldPinYin.substring(0).toUpperCase()+strOldPinYin.substring(1);
				}else{
					strCasePinYin = strOldPinYin.substring(0).toUpperCase();
				}
			} else if (strOldPinYin.charAt(0) <= 'Z' && strOldPinYin.charAt(0) >= 'A') {
				//strCasePinYin = strOldPinYin.charAt(0) - 32 + strOldPinYin.substring(1);
			} else {
				strCasePinYin = "#" + strOldPinYin.substring(1);
			}
			setPinyin(strCasePinYin);
		} else {
			appName = "#";
			setPinyin("#");
		}
		this.appName = appName;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	@Override
	public int compareTo(AppInfo appInfo) {
		if ("#".equals(this.getPinyin().substring(0, 1))) {
			return 1;
		}
		/*if ("#".equals(appInfo.getPinyin().substring(0, 1))) {
			return -1;
		}*/
		return this.pinyin.compareTo(appInfo.getPinyin());
	}

}
