package com.lockstudio.sticklocker.model;

import android.graphics.Bitmap;

import com.lockstudio.sticklocker.view.LockPatternUtils;

/**
 * 九宫格锁
 * @author 庄宏岩
 *
 */
public class NinePatternLockerInfo extends LockerInfo {
	private Bitmap bitmap;
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

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
}
