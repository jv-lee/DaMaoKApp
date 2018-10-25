package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import cn.opda.android.activity.R;

public class SquareWheelProcessView extends View {
	
	private Paint radPaint;
	private Paint rectPaint;
	private int ral;
	private boolean downloaded;

	public SquareWheelProcessView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SquareWheelProcessView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SquareWheelProcessView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		radPaint = new Paint();
		radPaint.setAntiAlias(true);
		radPaint.setColor(getResources().getColor(R.color.trans_35_black));
		radPaint.setStyle(Paint.Style.FILL);
		radPaint.setStrokeCap(Paint.Cap.ROUND);
		
		rectPaint = new Paint();
		rectPaint.setAntiAlias(true);
		rectPaint.setColor(getResources().getColor(R.color.trans_35_white));
		rectPaint.setStyle(Paint.Style.FILL);
		rectPaint.setStrokeCap(Paint.Cap.ROUND);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float w = getWidth();
		float h = getHeight();
		double r = Math.sqrt(w * w + h * h);
		RectF oval = new RectF(0, 0, w, h);
		RectF oval2 = new RectF((float)(w - r), (float)(h - r), (float)r, (float)r);
		canvas.drawRect(oval, rectPaint);
		if (downloaded){
			canvas.drawRect(oval, radPaint);
		} else {
			canvas.drawArc(oval2, -90, ral, true, radPaint);;
		}
	}
	
	public void setProgress(int progress) {
		ral = progress * 36/10;
		invalidate();
	}
	
	public void setIfDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

}
