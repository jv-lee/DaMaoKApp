package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lockstudio.sticklocker.application.LockApplication;

public class ThemeImageView extends ImageView {

	public ThemeImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ThemeImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ThemeImageView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int size = MeasureSpec.getSize(widthMeasureSpec);
		int screenWidth = LockApplication.getInstance().getConfig().getScreenWidth();
		int screenHeight = LockApplication.getInstance().getConfig().getScreenHeight();

		setMeasuredDimension(size, size * screenHeight / screenWidth);
	}

}
