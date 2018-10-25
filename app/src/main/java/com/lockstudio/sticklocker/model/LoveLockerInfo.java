package com.lockstudio.sticklocker.model;

import android.graphics.Bitmap;

/**
 * 爱心锁
 * @author 庄宏岩
 *
 */
public class LoveLockerInfo extends LockerInfo {
	private Bitmap[]bitmaps = new Bitmap[10];

	public Bitmap[] getBitmaps() {
		return bitmaps;
	}

	public void setBitmaps(Bitmap[] bitmaps) {
		this.bitmaps = bitmaps;
	}
	
}
