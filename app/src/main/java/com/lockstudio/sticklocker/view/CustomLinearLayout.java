package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

public class CustomLinearLayout extends LinearLayout {
	private OnKeyListener mOnKeyListener = null;

	public CustomLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomLinearLayout(Context context) {
		super(context);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && KeyEvent.ACTION_DOWN == event.getAction()) {
			if (mOnKeyListener != null) {
				mOnKeyListener.onKey(this, KeyEvent.KEYCODE_BACK, event);
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void setOnKeyListener(OnKeyListener l) {
		this.mOnKeyListener = l;
	}
}
