package com.lockstudio.sticklocker.model;

import android.graphics.Bitmap;

/**
 * 每一个壁纸分类元素中的图片子元素
 */
public class OwnWallpaperImageInfo {

    private int id;
    private Bitmap thumbnailBitmap;
    private Bitmap imageBitmap;
    private String imageUrl;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Bitmap getThumbnailBitmap() {
		return thumbnailBitmap;
	}
	public void setThumbnailBitmap(Bitmap thumbnailBitmap) {
		this.thumbnailBitmap = thumbnailBitmap;
	}
	public Bitmap getImageBitmap() {
		return imageBitmap;
	}
	public void setImageBitmap(Bitmap imageBitmap) {
		this.imageBitmap = imageBitmap;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

    
}
