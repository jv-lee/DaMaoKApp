package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FullWidthImageView extends ImageView {

	public FullWidthImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FullWidthImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FullWidthImageView(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ImageView#onMeasure(int, int)
	 * 让imageview的高度和宽度是一致的.高度和宽度由宽度决定.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int size = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(size, size);
	}
}
