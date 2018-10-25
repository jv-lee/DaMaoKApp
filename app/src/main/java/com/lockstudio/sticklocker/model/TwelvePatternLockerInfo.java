package com.lockstudio.sticklocker.model;

import android.graphics.Bitmap;

import com.lockstudio.sticklocker.view.LockPatternUtils;


/**
 * 十二宫格锁
 * 
 * @author 庄宏岩
 * 
 */
public class TwelvePatternLockerInfo extends LockerInfo {
	private Bitmap[] bitmaps = new Bitmap[12];
	private int lineColor = LockPatternUtils.COLOR_YELLOW;
	private boolean drawLine = true;

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	public boolean isDrawLine() {
		return drawLine;
	}

	public void setDrawLine(boolean drawLine) {
		this.drawLine = drawLine;
	}

	public Bitmap[] getBitmaps() {
		return bitmaps;
	}

	public void setBitmaps(Bitmap[] bitmaps) {
		this.bitmaps = bitmaps;
	}

	
	
}
