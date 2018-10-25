package com.lockstudio.sticklocker.model;

import android.graphics.Bitmap;

/**
 * 十二宫格锁
 * @author 庄宏岩
 *
 */
public class SlideLockerInfo extends LockerInfo {
	private Bitmap[]bitmaps = new Bitmap[2];
	private int left1;
	private int top1;
	private int right1;
	private int bottom1;
	private int left2;
	private int top2;
	private int right2;
	private int bottom2;
	private int bitmapRes;

	public int getBitmapRes() {
		return bitmapRes;
	}

	public void setBitmapRes(int bitmapRes) {
		this.bitmapRes = bitmapRes;
	}

	private boolean first;//初始位置
	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public int getLeft1() {
		return left1;
	}

	public void setLeft1(int left1) {
		this.left1 = left1;
	}

	public int getTop1() {
		return top1;
	}

	public void setTop1(int top1) {
		this.top1 = top1;
	}

	public int getRight1() {
		return right1;
	}

	public void setRight1(int right1) {
		this.right1 = right1;
	}

	public int getBottom1() {
		return bottom1;
	}

	public void setBottom1(int bottom1) {
		this.bottom1 = bottom1;
	}

	public int getLeft2() {
		return left2;
	}

	public void setLeft2(int left2) {
		this.left2 = left2;
	}

	public int getTop2() {
		return top2;
	}

	public void setTop2(int top2) {
		this.top2 = top2;
	}

	public int getRight2() {
		return right2;
	}

	public void setRight2(int right2) {
		this.right2 = right2;
	}

	public int getBottom2() {
		return bottom2;
	}

	public void setBottom2(int bottom2) {
		this.bottom2 = bottom2;
	}

	public Bitmap[] getBitmaps() {
		return bitmaps;
	}

	public void setBitmaps(Bitmap[] bitmaps) {
		this.bitmaps = bitmaps;
	}
}
