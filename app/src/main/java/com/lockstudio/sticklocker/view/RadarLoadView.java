package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.lockstudio.sticklocker.util.DensityUtil;

import java.util.Random;

import cn.opda.android.activity.R;

public class RadarLoadView extends View {
	private Bitmap rotate_bitmap;
	private int minHeight, minWidth;
	private int maxHeight, maxWidth;
	private Paint paint, paint2, bitmapPaint, pointPaint;
	private Context mContext;
	private int stroke_1dp;
	private float radius1, radius2, radius3;
	private float centerX, centerY;
	private int alpha1, alpha2, alpha3;
	private boolean init;
	private Matrix matrix = new Matrix();
	private Point point = new Point();
	private int pointAlpha, pointAlpha2;
	private float pointRadius1, pointRadius2;
	private float pointScale = 0.1f;
	private boolean stopAnim = false;

	public RadarLoadView(Context context) {
		super(context);
		this.mContext = context;
	}

	public RadarLoadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public RadarLoadView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext = context;
	}

	private void init() {
		stroke_1dp = DensityUtil.dip2px(mContext, 1);

		rotate_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_radar_rotate);
		minHeight = rotate_bitmap.getHeight();
		minWidth = rotate_bitmap.getWidth();

		bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bitmapPaint.setColor(Color.RED);

		pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		pointPaint.setColor(Color.RED);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(0xFFE3E3E3);

		paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint2.setStyle(Style.STROKE);
		paint2.setStrokeWidth(stroke_1dp);
		paint2.setColor(Color.RED);

		centerX = maxWidth / 2;
		centerY = maxHeight / 2;

		radius1 = minWidth / 2;
		radius2 = minWidth / 2 + (maxWidth - minWidth) / 2 / 3;
		radius3 = minWidth / 2 + (maxWidth - minWidth) / 3;

		alpha1 = 255;
		alpha2 = 255 / 3 * 2;
		alpha3 = 255 / 3;

		
		matrix = new Matrix();
		matrix.postTranslate((maxWidth - minWidth) / 2, (maxHeight - minHeight) / 2);

		pointAlpha = 255;
		pointAlpha2 = 255;
		pointRadius1 = DensityUtil.dip2px(mContext, 3);
		pointRadius2 = DensityUtil.dip2px(mContext, 12);

		getRandomPoint();

	}

	private void getRandomPoint() {

		int offSize = (maxWidth - minWidth) / 3;

		point.x = new Random().nextInt(maxWidth);

		if (point.x > maxWidth - offSize) {
			point.x = maxWidth - offSize;
		}
		if (point.x < offSize) {
			point.x = offSize;
		}

		if (point.x < centerX && point.x > centerX - minWidth) {
			point.x = (int) (centerX - minWidth - 50);
		}

		if (point.x > centerX && point.x < centerX + minWidth) {
			point.x = (int) (centerX + minWidth + 50);
		}

		point.y = new Random().nextInt(maxHeight);

		if (point.y > maxHeight - offSize) {
			point.y = maxHeight - offSize;
		}
		if (point.y < offSize) {
			point.y = offSize;
		}
		if (point.y < centerY && point.y > centerY - minHeight) {
			point.y = (int) (centerY - minHeight - 50);
		}

		if (point.y > centerY && point.y < centerY + minHeight) {
			point.y = (int) (centerY + minHeight + 50);
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int size = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(size, size);
		maxHeight = size;
		maxWidth = size;

		if (!init) {
			init();
			init = true;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (!stopAnim && maxWidth > 0) {

			paint.setAlpha(alpha1);
			paint2.setAlpha(alpha1);
			canvas.drawCircle(centerX, centerY, radius1 - stroke_1dp / 2, paint);
			canvas.drawCircle(centerX, centerY, radius1, paint2);

			paint.setAlpha(alpha2);
			paint2.setAlpha(alpha2);
			canvas.drawCircle(centerX, centerY, radius2 - stroke_1dp / 2, paint);
			canvas.drawCircle(centerX, centerY, radius2, paint2);

			paint.setAlpha(alpha3);
			paint2.setAlpha(alpha3);
			canvas.drawCircle(centerX, centerY, radius3 - stroke_1dp / 2, paint);
			canvas.drawCircle(centerX, centerY, radius3, paint2);

			canvas.drawBitmap(rotate_bitmap, matrix, bitmapPaint);

			pointPaint.setAlpha(pointAlpha2);
			canvas.drawCircle(point.x, point.y, pointRadius1, pointPaint);
			pointPaint.setAlpha(pointAlpha);
			canvas.drawCircle(point.x, point.y, pointRadius2 * pointScale, pointPaint);

			if (!stopAnim) {
				handler.sendEmptyMessageDelayed(0, 10);
			}
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				handler.removeMessages(0);

				matrix.postRotate(4, centerX, centerY);

				pointAlpha -= 3;
				pointAlpha2 -= 2;
				pointScale = radius1 * 1.0f / (maxWidth / 2);

				if (radius1 >= maxWidth / 2) {
					radius1 = minWidth / 2;
					alpha1 = 255;
					getRandomPoint();
					pointAlpha = 255;
					pointAlpha2 = 255;
					pointScale = 0.1f;
				}
				if (radius2 >= maxWidth / 2) {
					radius2 = minWidth / 2;
					alpha2 = 255;
				}
				if (radius3 >= maxWidth / 2) {
					radius3 = minWidth / 2;
					alpha3 = 255;
				}

				radius1 += 4;
				radius2 += 4;
				radius3 += 4;

				alpha1 -= 3;
				alpha2 -= 3;
				alpha3 -= 3;

				if (alpha1 < 0) {
					alpha1 = 0;
				}
				if (alpha2 < 0) {
					alpha2 = 0;
				}
				if (alpha3 < 0) {
					alpha3 = 0;
				}
				if (pointAlpha < 0) {
					pointAlpha = 0;
				}
				if (pointAlpha2 < 0) {
					pointAlpha2 = 0;
				}

				invalidate();
				break;
			case 1:

				break;
			default:
				break;
			}
		}

	};

	public void startAnim() {
		stopAnim = false;
		if (maxWidth > 0) {
			init();
			invalidate();
		}
	}

	public void stopAnim() {
		stopAnim = true;
	}
}
