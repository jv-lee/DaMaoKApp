package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lockstudio.sticklocker.Interface.UnlockListener;
import com.lockstudio.sticklocker.application.LockApplication;

public class FreeLockerView extends View {
	private UnlockListener mUnlockListener;
	private int slideSizeX = 300;
	private int slideSizeY = 500;

	public FreeLockerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public FreeLockerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FreeLockerView(Context context) {
		super(context);
		init();
	}

	private void init() {
		int screenWidth = LockApplication.getInstance().getConfig().getScreenWidth();
		int screenHeight = LockApplication.getInstance().getConfig().getScreenHeight();
		slideSizeX = (int) (screenWidth / 2.5f);
		slideSizeY = (int) (screenHeight / 3f);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int screenWidth = LockApplication.getInstance().getConfig().getScreenWidth();
		int screenHeight = LockApplication.getInstance().getConfig().getScreenHeight();
		setMeasuredDimension(screenWidth, screenHeight);
	}

	public void setUnlockListener(UnlockListener mUnlockListener) {
		this.mUnlockListener = mUnlockListener;
	}

	private float lastX = 0;
	private float lastY = 0;
	private boolean morePoint;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			morePoint = false;
			lastX = event.getX();
			lastY = event.getY();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			morePoint = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if (morePoint) {
				return true;
			}
			if (Math.abs(event.getX() - lastX) >= slideSizeX) {
				if (mUnlockListener != null) {
					mUnlockListener.OnUnlockSuccess();
					return true;
				}
			}
			if (Math.abs(event.getY() - lastY) >= slideSizeY) {
				if (mUnlockListener != null) {
					mUnlockListener.OnUnlockSuccess();
					return true;
				}
			}
			break;
		}
		return true;
	}

}
