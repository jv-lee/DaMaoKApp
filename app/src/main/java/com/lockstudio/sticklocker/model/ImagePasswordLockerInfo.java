package com.lockstudio.sticklocker.model;

import android.graphics.Bitmap;

/**
 * 密码锁
 * @author 庄宏岩
 *
 */
public class ImagePasswordLockerInfo extends LockerInfo {
	private Bitmap[]bitmaps = new Bitmap[12];

	public Bitmap[] getBitmaps() {
		return bitmaps;
	}

	public void setBitmaps(Bitmap[] bitmaps) {
		this.bitmaps = bitmaps;
	}
	
}
