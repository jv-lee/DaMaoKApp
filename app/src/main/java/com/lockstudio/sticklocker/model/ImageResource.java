package com.lockstudio.sticklocker.model;

import android.graphics.Bitmap;

public class ImageResource {
	private String url;
	private String path;
	private boolean local;
	private long createTime;
	private boolean temp;
	private Bitmap bitmap;
	private boolean assets;
	
	public boolean isAssets() {
		return assets;
	}
	public void setAssets(boolean assets) {
		this.assets = assets;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public boolean isTemp() {
		return temp;
	}
	public void setTemp(boolean temp) {
		this.temp = temp;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isLocal() {
		return local;
	}
	public void setLocal(boolean local) {
		this.local = local;
	}
	
	
}
