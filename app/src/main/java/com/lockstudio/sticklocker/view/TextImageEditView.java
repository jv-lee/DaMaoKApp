package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.lockstudio.sticklocker.util.DrawableUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.opda.android.activity.R;

public class TextImageEditView extends ImageView {

	private float x_down = 0;
	private float y_down = 0;
	private PointF mid = new PointF();
	private float oldDist = 1f;
	private float oldRotation = 0;
	private Matrix matrix = new Matrix();
	private Matrix matrix1 = new Matrix();
	private Matrix savedMatrix = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	private boolean matrixCheck = false;

	private int widthScreen;
	private int heightScreen;

	private Bitmap resourceBitmap;
	private int image_frame_width;
	private Path image_frame_path = new Path();
	private Paint image_frame_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
	private Paint stroke_paint = new Paint();
	private Path stroke_path = new Path();
	private String text;
	private String image_path;
	private int textColor = Color.WHITE;

	public TextImageEditView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextImageEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextImageEditView(Context mContext) {
		super(mContext);
		init();
	}

	public void init() {
		setBackgroundColor(0xff00b7ee);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (widthScreen == 0) {
			widthScreen = getWidth();
			heightScreen = getHeight();
			image_frame_width = widthScreen / 2;

			if (!TextUtils.isEmpty(text)) {
				resourceBitmap = getTextBitmap(text);
				matrix = new Matrix();
				matrix.postTranslate((widthScreen - resourceBitmap.getWidth()) / 2, (heightScreen - resourceBitmap.getHeight()) / 2);// 平移
			}

			image_frame_paint.setColor(getResources().getColor(R.color.trans_black));
			image_frame_paint.setStyle(Style.FILL);

			stroke_paint.setAntiAlias(true);
			stroke_paint.setColor(Color.WHITE);
			stroke_paint.setStyle(Style.STROKE);
			stroke_paint.setStrokeWidth(1);
			stroke_paint.setStrokeCap(Cap.ROUND);
			stroke_paint.setStrokeJoin(Join.ROUND);

			float frame_x = (widthScreen - image_frame_width) / 2;
			float frame_y = (heightScreen - image_frame_width) / 2;

			image_frame_path.moveTo(0, 0);
			image_frame_path.lineTo(widthScreen, 0);
			image_frame_path.lineTo(widthScreen, frame_y);
			image_frame_path.lineTo(0, frame_y);
			image_frame_path.lineTo(0, 0);

			image_frame_path.moveTo(0, frame_y);
			image_frame_path.lineTo(frame_x, frame_y);
			image_frame_path.lineTo(frame_x, frame_y + image_frame_width);
			image_frame_path.lineTo(0, frame_y + image_frame_width);
			image_frame_path.lineTo(0, frame_y);

			image_frame_path.moveTo(frame_x + image_frame_width, frame_y);
			image_frame_path.lineTo(widthScreen, frame_y);
			image_frame_path.lineTo(widthScreen, frame_y + image_frame_width);
			image_frame_path.lineTo(frame_x + image_frame_width, frame_y + image_frame_width);
			image_frame_path.lineTo(frame_x + image_frame_width, frame_y);

			image_frame_path.moveTo(0, frame_y + image_frame_width);
			image_frame_path.lineTo(widthScreen, frame_y + image_frame_width);
			image_frame_path.lineTo(widthScreen, heightScreen);
			image_frame_path.lineTo(0, heightScreen);
			image_frame_path.lineTo(0, frame_y + image_frame_width);

			stroke_path.rewind();
			stroke_path.moveTo(frame_x, frame_y);
			stroke_path.lineTo(frame_x + image_frame_width, frame_y);
			stroke_path.lineTo(frame_x + image_frame_width, frame_y + image_frame_width);
			stroke_path.lineTo(frame_x, frame_y + image_frame_width);
			stroke_path.lineTo(frame_x, frame_y);

		}
	}

	protected void onDraw(Canvas canvas) {
		if (resourceBitmap != null) {
			canvas.save();
			canvas.drawBitmap(resourceBitmap, matrix, null);
			canvas.drawPath(image_frame_path, image_frame_paint);
			canvas.drawPath(stroke_path, stroke_paint);
			canvas.restore();
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			x_down = event.getX();
			y_down = event.getY();
			savedMatrix.set(matrix);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			oldDist = spacing(event);
			oldRotation = rotation(event);
			savedMatrix.set(matrix);
			midPoint(mid, event);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == ZOOM) {
				matrix1.set(savedMatrix);
				float rotation = rotation(event) - oldRotation;
				float newDist = spacing(event);
				float scale = newDist / oldDist;
				matrix1.postScale(scale, scale, mid.x, mid.y);// 縮放
				matrix1.postRotate(rotation, mid.x, mid.y);// 旋轉
				matrix1.postTranslate(event.getX() - x_down, event.getY() - y_down);// 平移
				// matrixCheck = matrixCheck();
				if (matrixCheck == false) {
					matrix.set(matrix1);
					invalidate();
				}
			} else if (mode == DRAG) {
				matrix1.set(savedMatrix);
				matrix1.postTranslate(event.getX() - x_down, event.getY() - y_down);// 平移
				// matrixCheck = matrixCheck();
				if (matrixCheck == false) {
					matrix.set(matrix1);
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		}
		return true;
	}

	// 触碰两点间距离
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	// 取手势中心点
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// 取旋转角度
	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	public Bitmap creatNewImage() {
		Bitmap bitmap = Bitmap.createBitmap(widthScreen, heightScreen, Config.ARGB_8888); // 背景图片
		Canvas canvas = new Canvas(bitmap); // 新建画布
		canvas.drawBitmap(resourceBitmap, matrix, null); // 画图片

		Bitmap newBitmap = Bitmap.createBitmap(bitmap, (widthScreen - image_frame_width) / 2, (heightScreen - image_frame_width) / 2, image_frame_width,
				image_frame_width);

		int size = (int) ((getResources().getDimension(R.dimen.lock_patternview_width)) / 5);
		try {
			FileOutputStream out = new FileOutputStream(image_path);
			DrawableUtils.scaleTo(newBitmap, size, size).compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bitmap.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newBitmap;
	}

	public void setDrawText(String text, String image_path) {
		this.text = text;
		this.image_path = image_path;
		if (image_frame_width > 0) {
			resourceBitmap = getTextBitmap(text);
			matrix = new Matrix();
			matrix.postTranslate((widthScreen - resourceBitmap.getWidth()) / 2, (heightScreen - resourceBitmap.getHeight()) / 2);// 平移
			invalidate();
		}
	}

	public Bitmap getTextBitmap(String text) {
		if (image_frame_width > 0) {
			textPaint.setTextSize((image_frame_width / text.length()) * 0.6f);
			Bitmap bitmap = Bitmap.createBitmap(image_frame_width, image_frame_width, Config.ARGB_8888);
			Rect targetRect = new Rect(0, 0, image_frame_width, image_frame_width);
			Canvas canvas = new Canvas(bitmap);
			FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
			int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
			textPaint.setColor(textColor);
			textPaint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(text, targetRect.centerX(), baseline, textPaint);
			return bitmap;
		} else {
			return null;
		}

	}

	public void setDrawTextColor(int i) {
		this.textColor = i;
		resourceBitmap = getTextBitmap(text);
		invalidate();
	}

	public void setDrawTextShadow(int color) {
		textPaint.setShadowLayer(20, 0, 0, color);
		resourceBitmap = getTextBitmap(text);
		invalidate();
	}

	private String fontPath;

	public void setTextFont(String font) {
		if (TextUtils.isEmpty(font)) {
			textPaint.setTypeface(Typeface.DEFAULT);
		} else {
			if (!font.equals(fontPath)) {
				this.fontPath = font;
				textPaint.setTypeface(Typeface.createFromFile(font));
			}
		}
	}

}
