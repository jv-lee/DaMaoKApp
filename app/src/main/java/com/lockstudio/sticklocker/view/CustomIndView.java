package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.lockstudio.sticklocker.util.DensityUtil;

/**
 * 自定义viewpager的光标view
 * @author 庄宏岩
 *
 */
public class CustomIndView extends View {
	private Context mContext;
	private int maxPageCount;
	private int nowPage;
	private int space = 10;
	private Paint mPaint;
	private int ind_width = 10;
	private int ind_height = 10;

	public CustomIndView(Context context) {
		super(context);
	}

	public CustomIndView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomIndView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
	}

	public void setMaxPageCount(int maxPageCount) {
		this.maxPageCount = maxPageCount;
	}

	public void setNowPage(int nowPage) {
		this.nowPage = nowPage;
	}

	public void create() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (maxPageCount > 1) {
			int width = getWidth();
			int height = getHeight();
			int left = (width - (ind_width * maxPageCount) - (space * (maxPageCount - 1))) / 2;
			int top = (height - ind_height) / 2;
			int bottom = (height - ind_height) / 2 + ind_height;
			for (int i = 1; i <= maxPageCount; i++) {
				if (nowPage == i - 1) {
					mPaint.setColor(Color.parseColor("#f96c6c"));
				} else {
					mPaint.setColor(Color.parseColor("#dedde2"));
				}
//				canvas.drawRect(left, top, left + ind_width, bottom, mPaint);
				canvas.drawCircle(left, top, DensityUtil.dip2px(mContext, 4), mPaint);
				left = left + ind_width + DensityUtil.dip2px(mContext, space);
			}

		} else {
			return;
		}
	}

	public void updatePage(int position) {
		nowPage = position;
		invalidate();
	}

}
