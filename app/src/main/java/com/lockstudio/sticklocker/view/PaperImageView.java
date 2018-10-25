package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PaperImageView extends ImageView {

	public PaperImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PaperImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PaperImageView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int size = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(size, size * 16 / 9);
//        int screenWidth = LockApplication.getInstance().getConfig().getScreenWidth();
//        int screenHeight = LockApplication.getInstance().getConfig().getScreenHeight();
//		setMeasuredDimension(size, size * screenHeight / screenWidth);
	}
}
