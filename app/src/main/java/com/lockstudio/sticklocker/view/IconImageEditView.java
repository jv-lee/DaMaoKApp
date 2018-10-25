package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.SearchPoint;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.opda.android.activity.R;

public class IconImageEditView extends ImageView {
	public static enum RotateTepe {
		rotate_0, rotate_90, rotate_180, rotate_270
	}

	private RotateTepe rotateTepe = RotateTepe.rotate_0;
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

	boolean matrixCheck = false;

	private int widthScreen;
	private int heightScreen;

	private Bitmap resourceBitmap;
	private float image_frame_width;
	private Path image_frame_path = new Path();
	private Path shape_path = new Path();
	private Path stroke_path = new Path();
	private Paint image_frame_paint = new Paint();
	private Paint image_shape_paint = new Paint();
	private Paint stroke_paint = new Paint();
	private Paint shapePaint = new Paint();
	private String resource_path;
	private String image_path;
	private float frameScale;
	private float shapeWidth;

	public IconImageEditView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public IconImageEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public IconImageEditView(Context mContext) {
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
			image_frame_width = widthScreen / 2.0f;

			if (!TextUtils.isEmpty(resource_path)) {
				resourceBitmap = getIconBitmap(resource_path);
				matrix = new Matrix();
				matrix.postTranslate((widthScreen - resourceBitmap.getWidth()) / 2, (heightScreen - resourceBitmap.getHeight()) / 2);// 平移
			}

			shapePaint.setAntiAlias(true);
			shapePaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			shapePaint.setStyle(Style.FILL);
			shapePaint.setColor(-65536);
			shapePaint.setFilterBitmap(true);

			image_frame_paint.setAntiAlias(true);
			image_frame_paint.setStyle(Style.FILL);
			image_frame_paint.setColor(getResources().getColor(R.color.trans_black));

			image_shape_paint.setAntiAlias(true);
			image_shape_paint.setStyle(Style.FILL);
			image_shape_paint.setColor(getResources().getColor(R.color.trans_black));

			stroke_paint.setAntiAlias(true);
			stroke_paint.setColor(Color.WHITE);
			stroke_paint.setStyle(Style.STROKE);
			stroke_paint.setStrokeWidth(1);
			stroke_paint.setStrokeCap(Cap.ROUND);
			stroke_paint.setStrokeJoin(Join.ROUND);

			try {
				setShap(BitmapFactory.decodeStream(getContext().getAssets().open("shapes/shape_yuanxing.png")));
			} catch (IOException e) {
				e.printStackTrace();
			}

			float frame_x = (widthScreen - image_frame_width) * 1.0f / 2.0f;
			float frame_y = (heightScreen - image_frame_width) * 1.0f / 2.0f;

			image_frame_path.rewind();
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
		}
	}

	protected void onDraw(Canvas canvas) {
		if (resourceBitmap != null) {
			canvas.save();
			canvas.drawBitmap(resourceBitmap, matrix, null);
			canvas.drawPath(image_frame_path, image_frame_paint);
			if (shapeWidth > 0) {
				canvas.translate((widthScreen - image_frame_width) / 2, (heightScreen - image_frame_width) / 2);
				canvas.scale(frameScale, frameScale);
				canvas.drawPath(shape_path, image_shape_paint);
				canvas.drawPath(stroke_path, stroke_paint);
			} else {
				canvas.drawPath(stroke_path, stroke_paint);
			}
			canvas.restore();
		}
	}

	public void setShap(Bitmap bitmap) {
		SearchPoint searchPoint = null;
		if (bitmap != null) {
			SearchPoint searchPoint2 = new SearchPoint(bitmap.getPixel(0, 0) & 0xFFFFFF);
			int shapeW = (int) searchPoint2.getWidth();
			int shapeH = (int) searchPoint2.getHeight();
			shapeWidth = shapeW;
			frameScale = (((float) image_frame_width) * 1.0f) / ((float) shapeW);
			if (bitmap != null) {
				int height = bitmap.getHeight();
				searchPoint2 = null;
				for (int i3 = 1; i3 < height; i3++) {
					if (searchPoint2 == null) {
						searchPoint2 = new SearchPoint(bitmap.getPixel(0, i3) & 0xFFFFFF);
						searchPoint = searchPoint2;
					} else {
						searchPoint2.b = new SearchPoint(bitmap.getPixel(0, i3) & 0xFFFFFF);
						searchPoint2 = searchPoint2.b;
					}
				}
			}
			shape_path.rewind();
			searchPoint.search(shape_path);
			shape_path.moveTo(0.0f, 0.0f);
			shape_path.lineTo(0.0f, shapeH);
			shape_path.lineTo(shapeW, shapeH);
			shape_path.lineTo(shapeW, 0.0f);
			shape_path.lineTo(0.0f, 0.0f);

			stroke_path.rewind();
			searchPoint.search(stroke_path);
		} else {
			shapeWidth = 0;
			float frame_x = (widthScreen - image_frame_width) * 1.0f / 2.0f;
			float frame_y = (heightScreen - image_frame_width) * 1.0f / 2.0f;
			stroke_path.rewind();
			stroke_path.moveTo(frame_x, frame_y);
			stroke_path.lineTo(frame_x + image_frame_width, frame_y);
			stroke_path.lineTo(frame_x + image_frame_width, frame_y + image_frame_width);
			stroke_path.lineTo(frame_x, frame_y + image_frame_width);
			stroke_path.lineTo(frame_x, frame_y);
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
				if (matrixCheck == false) {
					matrix.set(matrix1);
					invalidate();
				}
			} else if (mode == DRAG) {
				matrix1.set(savedMatrix);
				matrix1.postTranslate(event.getX() - x_down, event.getY() - y_down);// 平移
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

		Bitmap newBitmap = Bitmap.createBitmap(bitmap, (int) (widthScreen - image_frame_width) / 2, (int) (heightScreen - image_frame_width) / 2,
				(int) image_frame_width, (int) image_frame_width);

		if (shapeWidth > 0) {
			float width = ((float) newBitmap.getWidth()) / shapeWidth;
			canvas = new Canvas(newBitmap);
			canvas.save();
			canvas.scale(width, width);
			canvas.drawPath(shape_path, shapePaint);
			canvas.restore();
		}
		int size =  (int) ((getResources().getDimension(R.dimen.lock_patternview_width)) / 4.5);
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

	public void setDrawIcon(String resource_path, String image_path) {
		this.resource_path = resource_path;
		this.image_path = image_path;
		if (image_frame_width > 0) {
			resourceBitmap = getIconBitmap(resource_path);
			matrix = new Matrix();
			matrix.postTranslate((widthScreen - resourceBitmap.getWidth()) / 2, (heightScreen - resourceBitmap.getHeight()) / 2);// 平移
			invalidate();
		}
	}

	public void rotate() {
		matrix = new Matrix();
		float degrees = 0;
		if (rotateTepe == RotateTepe.rotate_0) {
			degrees = 90;
			rotateTepe = RotateTepe.rotate_90;
		} else if (rotateTepe == RotateTepe.rotate_90) {
			degrees = 180;
			rotateTepe = RotateTepe.rotate_180;
		} else if (rotateTepe == RotateTepe.rotate_180) {
			degrees = 270;
			rotateTepe = RotateTepe.rotate_270;
		} else if (rotateTepe == RotateTepe.rotate_270) {
			degrees = 0;
			rotateTepe = RotateTepe.rotate_0;
		}
		matrix.postRotate(degrees, resourceBitmap.getWidth() / 2, resourceBitmap.getHeight() / 2);// 旋轉
		matrix.postTranslate((widthScreen - resourceBitmap.getWidth()) / 2, (heightScreen - resourceBitmap.getHeight()) / 2);// 平移
		invalidate();
	}

	private Bitmap getIconBitmap(String resource_path) {
		Bitmap bitmap = DrawableUtils.getBitmap(getContext(), resource_path);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap newBitmap = null;
		if (width < height) {
			if (width > image_frame_width) {
				float scale = image_frame_width / width;
				newBitmap = DrawableUtils.scaleTo(bitmap, scale, scale);
			}
		} else {
			if (height > image_frame_width) {
				float scale = image_frame_width / height;
				newBitmap = DrawableUtils.scaleTo(bitmap, scale, scale);
			}
		}
		if (newBitmap != null) {
			try {
				bitmap.recycle();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return newBitmap;
		}
		return bitmap;
	}

}
