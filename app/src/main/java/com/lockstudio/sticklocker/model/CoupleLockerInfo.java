package com.lockstudio.sticklocker.model;

import android.graphics.Bitmap;

/**
 * 十二宫格锁
 * @author 庄宏岩
 *
 */
public class CoupleLockerInfo extends LockerInfo {
	private Bitmap[]bitmaps = new Bitmap[2];

	public Bitmap[] getBitmaps() {
		return bitmaps;
	}

	public void setBitmaps(Bitmap[] bitmaps) {
		this.bitmaps = bitmaps;
	}
}
