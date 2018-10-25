package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.lockstudio.sticklocker.util.DensityUtil;

import cn.opda.android.activity.R;

public class ColorCircle extends View {
	private Paint paint;
	private Paint selectPaint;
	private int width;
	private int height;
	private float[] centerPoint = new float[2];
	private boolean selecter;
	private float radius;
	private Context mContext;
	private Paint strokePaint;
	private boolean isTransparent;

	public ColorCircle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public ColorCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public ColorCircle(Context context) {
		super(context);
		this.mContext = context;
	}

	public void init(int color, boolean selecter) {
		if (0x00ffffff == color) {
			isTransparent = true;
		}

		this.selecter = selecter;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);

		selectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		if (0xffffffff == color) {
			selectPaint.setColor(Color.RED);
		} else {
			selectPaint.setColor(Color.WHITE);
		}

		selectPaint.setTextSize(DensityUtil.dip2px(mContext, 15));

		strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		strokePaint.setColor(Color.WHITE);
		strokePaint.setStrokeWidth(2);
		strokePaint.setStyle(Style.STROKE);

		width = (int) getResources().getDimension(R.dimen.color_circle_width);
		height = (int) getResources().getDimension(R.dimen.color_circle_width);

		float cx = width / 2;
		float cy = height / 2;
		centerPoint[0] = cx;
		centerPoint[1] = cy;

		radius = width / 2;

		invalidate();
	}

	public void setSelecter(boolean selecter) {
		if (this.selecter != selecter) {
			this.selecter = selecter;
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (paint != null) {
			if (isTransparent) {
				canvas.drawCircle(centerPoint[0], centerPoint[1], radius - 2, strokePaint);

				Rect targetRect = new Rect(0, 0, (int) radius * 2, (int) radius * 2);
				FontMetricsInt fontMetrics = selectPaint.getFontMetricsInt();
				int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
				selectPaint.setTextAlign(Paint.Align.CENTER);
				canvas.drawText("无", targetRect.centerX(), baseline, selectPaint);

			} else {
				if (!selecter) {
					canvas.drawCircle(centerPoint[0], centerPoint[1], radius, paint);
				} else {
					canvas.drawCircle(centerPoint[0], centerPoint[1], radius, paint);
					canvas.drawCircle(centerPoint[0], centerPoint[1], radius, selectPaint);
					canvas.drawCircle(centerPoint[0], centerPoint[1], radius - DensityUtil.dip2px(mContext, 2), paint);
				}
			}
		}
	}
}
