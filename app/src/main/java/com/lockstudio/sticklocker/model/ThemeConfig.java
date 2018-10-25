package com.lockstudio.sticklocker.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Tommy on 15/3/15.
 */
public class ThemeConfig {
    private ArrayList<StickerInfo> stickerInfoList;
    private LockerInfo lockerInfo;
    private String themePreviewPath;
    private String themePath;
    private String wallpaper;
    private boolean checked;
    private int screenWidth;
    private int screenHeight;
    private Bitmap themePreview;
    private long createTime;
    private int wallpaperColor = 0xff00b7ee;
    

    public int getWallpaperColor() {
		return wallpaperColor;
	}

	public void setWallpaperColor(int wallpaperColor) {
		this.wallpaperColor = wallpaperColor;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public Bitmap getThemePreview() {
		return themePreview;
	}

	public void setThemePreview(Bitmap themePreview) {
		this.themePreview = themePreview;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}


	public String getWallpaper() {
		return wallpaper;
	}

	public void setWallpaper(String wallpaper) {
		this.wallpaper = wallpaper;
	}

	public String getThemePath() {
		return themePath;
	}

	public void setThemePath(String themePath) {
		this.themePath = themePath;
	}

	public ArrayList<StickerInfo> getStickerInfoList() {
		return stickerInfoList;
	}

	public void setStickerInfoList(ArrayList<StickerInfo> stickerInfoList) {
		this.stickerInfoList = stickerInfoList;
	}

	public LockerInfo getLockerInfo() {
		return lockerInfo;
	}

	public void setLockerInfo(LockerInfo lockerInfo) {
		this.lockerInfo = lockerInfo;
	}

	public String getThemePreviewPath() {
		return themePreviewPath;
	}

	public void setThemePreviewPath(String themePreviewPath) {
		this.themePreviewPath = themePreviewPath;
	}


}
