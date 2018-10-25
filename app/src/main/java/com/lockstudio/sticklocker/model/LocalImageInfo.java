package com.lockstudio.sticklocker.model;

import android.graphics.Bitmap;

public class LocalImageInfo {
	private String path;
	private String name;
	private Bitmap icon;
	private boolean assets;
	
	public boolean isAssets() {
		return assets;
	}
	public void setAssets(boolean assets) {
		this.assets = assets;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Bitmap getIcon() {
		return icon;
	}
	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}
	
}
