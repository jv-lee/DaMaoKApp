package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.lockstudio.sticklocker.util.DensityUtil;

public class CoupleDragViews2 extends ImageView {

	private int mPreviousx = 0;
	private int mPreviousy = 0;
	private int screenWidth;
	private int iCurrentx;
	private int iCurrenty;
	private int left;
	private int top;
	private int right;
	private int bottom;
	private Handler mHandler;
	private Context mContext;
	private boolean mInputEnabled = true;

	public CoupleDragViews2(Context context, Handler mHandler) {
		super(context);
		this.mHandler = mHandler;
		this.mContext = context;
		init();
	}

	public CoupleDragViews2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public CoupleDragViews2(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	private void init() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels - DensityUtil.dip2px(mContext, 20);
	}

	public void disableInput() {
		mInputEnabled = false;
	}

	// On touch Event.
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mInputEnabled || !isEnabled()) {
			return false;
		}
		final int iAction = event.getAction();
		iCurrentx = (int) event.getX();
		iCurrenty = (int) event.getY();

		switch (iAction) {
		case MotionEvent.ACTION_DOWN:
			mPreviousx = iCurrentx;
			mPreviousy = iCurrenty;
			break;
		case MotionEvent.ACTION_MOVE:
			int iDeltx = iCurrentx - mPreviousx;
			int iDelty = iCurrenty - mPreviousy;
			left = getLeft() + iDeltx;
			top = getTop() + iDelty;
			right = getRight() + iDeltx;
			bottom = getBottom() + iDelty;

			if (iDeltx != 0 || iDelty != 0) {

				if (left < 0) {
					left = 0;
					right = left + getWidth();
				}

				if (right > screenWidth) {
					right = screenWidth;
					left = right - getWidth();
				}

				if (top < 0) {
					top = 0;
					bottom = top + getHeight();
				}

				if (bottom > getHeight()) {
					bottom = getHeight();
					top = bottom - getHeight();
				}

				layout(left, top, right, bottom);
				Message msg = new Message();
				msg.arg1 = screenWidth - right;
				msg.what = 1;
				mHandler.sendMessage(msg);

				if (left <= (screenWidth / 2 - getWidth() / 10)) {
					mHandler.sendEmptyMessage(21);
					mHandler.removeMessages(1);
				}
			}

			mPreviousx = iCurrentx - iDeltx;
			mPreviousy = iCurrenty - iDelty;
			break;
		case MotionEvent.ACTION_UP:
			if (left > screenWidth / 2 - getWidth() / 10) {
				mHandler.sendEmptyMessage(12);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return true;
	}
}
