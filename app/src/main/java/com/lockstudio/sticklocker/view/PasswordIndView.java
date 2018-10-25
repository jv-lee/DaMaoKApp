package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.lockstudio.sticklocker.util.DensityUtil;

/**
 * 密码输入指示器
 * 
 * @author 庄宏岩
 * 
 */
public class PasswordIndView extends View {
	private int maxPassLength;
	private int inputPassLength;
	private int space = 20;
	private Paint mPaint;
	private Paint mInputPaint;
	private int ind_width = 10;
	private int ind_height = 10;

	public PasswordIndView(Context context) {
		super(context);
	}

	public PasswordIndView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PasswordIndView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setMaxPassLength(int maxPassLength) {
		this.maxPassLength = maxPassLength;
	}

	public void setInputPassLength(int inputPassLength) {
		this.inputPassLength = inputPassLength;
		invalidate();
	}

	public void create() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(DensityUtil.dip2px(getContext(), 1));
		
		mInputPaint = new Paint();
		mInputPaint.setAntiAlias(true);
		mInputPaint.setColor(Color.WHITE);
		mInputPaint.setStyle(Style.FILL_AND_STROKE);
		mInputPaint.setStrokeWidth(DensityUtil.dip2px(getContext(), 1));
		
		ind_width = ind_height = DensityUtil.dip2px(getContext(), 10);
		space = DensityUtil.dip2px(getContext(), 10);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (maxPassLength > 1) {
			int width = getWidth();
			int height = getHeight();
			int left = (width - (ind_width * maxPassLength) - (space * (maxPassLength - 1))) / 2;
			int top = (height - ind_height) / 2;
			int bottom = (height - ind_height) / 2 + ind_height;
			for (int i = 0; i < maxPassLength; i++) {
				if (i < inputPassLength) {
					canvas.drawArc(new RectF(left, top, left + ind_width, bottom), 0, 360, true, mInputPaint);
				} else {
					canvas.drawArc(new RectF(left, top, left + ind_width, bottom), 0, 360, true, mPaint);
				}
				
				left = left + ind_width + space;

			}

		} else {
			return;
		}
	}

}
