package com.lockstudio.sticklocker.model;

import android.graphics.Bitmap;

public class AdInfo {
	private String name;
	private String desc;
	private String packageName;
	private String imageUrl;
	private String apkUrl;
	private Bitmap bitmapIcon;
	private String size;
	private String buttonName;
	private boolean isDownloading;
	private boolean installed;
	
	
	
	public boolean isDownloading() {
		return isDownloading;
	}
	public void setDownloading(boolean isDownloading) {
		this.isDownloading = isDownloading;
	}
	public boolean isInstalled() {
		return installed;
	}
	public void setInstalled(boolean installed) {
		this.installed = installed;
	}
	public String getButtonName() {
		return buttonName;
	}
	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getApkUrl() {
		return apkUrl;
	}
	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}
	public Bitmap getBitmapIcon() {
		return bitmapIcon;
	}
	public void setBitmapIcon(Bitmap bitmapIcon) {
		this.bitmapIcon = bitmapIcon;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
	
}
