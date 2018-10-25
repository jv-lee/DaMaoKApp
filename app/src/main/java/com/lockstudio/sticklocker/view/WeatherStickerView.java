package com.lockstudio.sticklocker.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.lockstudio.sticklocker.Interface.OnFocuseChangeListener;
import com.lockstudio.sticklocker.Interface.OnRemoveStickerViewListener;
import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.WeatherBean;
import com.lockstudio.sticklocker.model.WeatherStickerInfo;
import com.lockstudio.sticklocker.util.DateTime;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.PluginSettingUtils;
import com.lockstudio.sticklocker.util.PluginSettingUtils.OnPluginSettingChange;
import com.lockstudio.sticklocker.util.Trans2PinYin;
import com.lockstudio.sticklocker.util.WeatherUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.opda.android.activity.R;

/**
 * 单手对图片进行缩放，旋转，平移操作，详情请查看
 * 
 */
public class WeatherStickerView extends View implements OnPluginSettingChange {
	/**
	 * 图片的最大缩放比例
	 */
	public float MAX_SCALE = 2.0f;

	/**
	 * 图片的最小缩放比例
	 */
	public final float MIN_SCALE = 0.4f;

	/**
	 * 控制缩放，旋转图标所在四个点得位置
	 */
	public static final int LEFT_TOP = 0;
	public static final int RIGHT_TOP = 1;
	public static final int RIGHT_BOTTOM = 2;
	public static final int LEFT_BOTTOM = 3;

	/**
	 * 一些默认的常量
	 */
	public static final int DEFAULT_FRAME_PADDING = 8;
	public static final int DEFAULT_FRAME_COLOR = Color.GRAY;
	public static final float DEFAULT_SCALE = 1.0f;
	public static final float DEFAULT_DEGREE = 0;

	/**
	 * 用于旋转缩放的Bitmap
	 */
	private Bitmap mBitmap;

	/**
	 * SingleTouchView的中心点坐标，相对于其父类布局而言的
	 */
	private PointF mCenterPoint = new PointF();

	/**
	 * View的宽度和高度，随着图片的旋转而变化(不包括控制旋转，缩放图片的宽高)
	 */
	private int mViewWidth, mViewHeight;

	/**
	 * 图片的旋转角度
	 */
	private float mDegree = DEFAULT_DEGREE;

	/**
	 * 图片的缩放比例
	 */
	private float mScale = DEFAULT_SCALE;

	/**
	 * 用于缩放，旋转，平移的矩阵
	 */
	private Matrix matrix = new Matrix();

	/**
	 * SingleTouchView距离父类布局的左间距
	 */
	private int mViewPaddingLeft;

	/**
	 * SingleTouchView距离父类布局的上间距
	 */
	private int mViewPaddingTop;

	/**
	 * 图片四个点坐标
	 */
	private Point mLTPoint;
	private Point mRTPoint;
	private Point mRBPoint;
	private Point mLBPoint;

	/**
	 * 用于缩放，旋转的控制点的坐标
	 */
	private Point mRotatePoint = new Point();

	private Point mDeletePoint = new Point();

	private Point mEditPoint = new Point();
	/**
	 * 用于缩放，旋转的图标
	 */
	private Drawable mRotateDrawable, mDeleteDrawable, mEditDrawable;

	/**
	 * 缩放，旋转图标的宽和高
	 */
	private int mDrawableWidth, mDrawableHeight;

	/**
	 * 画外围框的Path
	 */
	private Path mPath = new Path();

	/**
	 * 画外围框的画笔
	 */
	private Paint mPaint;
	private Paint mTextPaint;
	private Paint mTextPaint1;
	private Paint mTextPaint2;

	/**
	 * 初始状态
	 */
	public static final int STATUS_INIT = 0;

	/**
	 * 拖动状态
	 */
	public static final int STATUS_DRAG = 1;

	/**
	 * 旋转或者放大状态
	 */
	public static final int STATUS_ROTATE_ZOOM = 2;

	/**
	 * 删除
	 */
	public static final int STATUS_DELETE = 3;

	/**
	 * 编辑
	 */
	public static final int STATUS_EDIT = 4;

	/**
	 * 当前所处的状态
	 */
	private int mStatus = STATUS_INIT;

	/**
	 * 外边框与图片之间的间距, 单位是dip
	 */
	private int framePadding = DEFAULT_FRAME_PADDING;

	/**
	 * 外边框颜色
	 */
	private int frameColor = DEFAULT_FRAME_COLOR;

	/**
	 * 外边框线条粗细, 单位是 dip
	 */
	private int frameWidth = DensityUtil.dip2px(getContext(), 1.5f);

	/**
	 * 是否处于可以缩放，平移，旋转状态
	 */
	private boolean isEditable = true;
	private boolean isVisiable = true;

	private PointF mPreMovePointF = new PointF();
	private PointF mCurMovePointF = new PointF();

	/**
	 * 图片在旋转时x方向的偏移量
	 */
	private int offsetX;
	/**
	 * 图片在旋转时y方向的偏移量
	 */
	private int offsetY;

	private WeatherStickerInfo mWeatherStickerInfo;
	private OnRemoveStickerViewListener mOnRemoveViewListener;
	private OnUpdateViewListener mOnUpdateViewListener;
	private OnFocuseChangeListener mOnFocuseChangeListener;
	private LayoutParams mContainerLayoutParams;
	private String text1;
	private String text2;
	private String text;
	private int textSize1;
	private int textSize2;
	private int weatherStyle;
	private String weathers;
	private String temp;
	private String curWeather;
	private int weatherIconRes;

	private LinearLayout mController_container_layout;
	private View controllerView;
	private Context mContext;
	private PluginSettingUtils pluginSettingUtils;
	WeatherBean weatherBean = null;
	private boolean timerLoop = false;

	private static final int MSG_START_TIMER_LOOP = 300;

	private static final int UPDATE_MY_TV = 1;
	private static final int UPDATE_MY_TVM = 2;
	Message message = null;
	private Thread mThread;
	private boolean flag = true;
	private float lastX = 0;
	private float lastY = 0;
	private Typeface typeface;

	public WeatherStickerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext = context;
	}

	public WeatherStickerView(Context context) {
		this(context, null);
		this.mContext = context;
	}

	public WeatherStickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(frameColor);
		mPaint.setStrokeWidth(frameWidth);
		mPaint.setStyle(Style.STROKE);
		PathEffect effects = new DashPathEffect(new float[] { 15, 15, 15, 15 }, 1);
		mPaint.setPathEffect(effects);

		if (mRotateDrawable == null) {
			mRotateDrawable = getContext().getResources().getDrawable(R.drawable.diy_rotate);
		}
		if (mDeleteDrawable == null) {
			mDeleteDrawable = getContext().getResources().getDrawable(R.drawable.diy_delete);
		}

		if (mEditDrawable == null) {
			mEditDrawable = getContext().getResources().getDrawable(R.drawable.diy_location);
		}

		mDrawableWidth = mRotateDrawable.getIntrinsicWidth();
		mDrawableHeight = mRotateDrawable.getIntrinsicHeight();

		transformDraw();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 调整View的大小，位置
	 */
	public void adjustLayout() {
		int actualWidth = mViewWidth + mDrawableWidth;
		int actualHeight = mViewHeight + mDrawableHeight;

		int newPaddingLeft = (int) (mCenterPoint.x - actualWidth / 2);
		int newPaddingTop = (int) (mCenterPoint.y - actualHeight / 2);

		if (mViewPaddingLeft != newPaddingLeft || mViewPaddingTop != newPaddingTop) {
			mViewPaddingLeft = newPaddingLeft;
			mViewPaddingTop = newPaddingTop;

			mContainerLayoutParams.leftMargin = mViewPaddingLeft;
			mContainerLayoutParams.topMargin = mViewPaddingTop;
			mContainerLayoutParams.width = actualWidth;
			mContainerLayoutParams.height = actualHeight;
			mOnUpdateViewListener.updateView(this, mContainerLayoutParams);

			mWeatherStickerInfo.x = mViewPaddingLeft + mDrawableWidth / 2 + framePadding;
			mWeatherStickerInfo.y = mViewPaddingTop + mDrawableHeight / 2 + framePadding;
			mWeatherStickerInfo.text = text;

			layout(newPaddingLeft, newPaddingTop, newPaddingLeft + actualWidth, newPaddingTop + actualHeight);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 每次draw之前调整View的位置和大小
		adjustLayout();

		super.onDraw(canvas);

		if (mBitmap == null)
			return;
		canvas.drawBitmap(mBitmap, matrix, null);

		// 处于可编辑状态才画边框和控制图标
		if (isEditable && isVisiable) {
			mPath.reset();
			mPath.moveTo(mLTPoint.x, mLTPoint.y);
			mPath.lineTo(mRTPoint.x, mRTPoint.y);
			mPath.lineTo(mRBPoint.x, mRBPoint.y);
			mPath.lineTo(mLBPoint.x, mLBPoint.y);
			mPath.lineTo(mLTPoint.x, mLTPoint.y);
			mPath.lineTo(mRTPoint.x, mRTPoint.y);
			canvas.drawPath(mPath, mPaint);

			mRotateDrawable.setBounds(mRotatePoint.x - mDrawableWidth / 2, mRotatePoint.y - mDrawableHeight / 2, mRotatePoint.x + mDrawableWidth / 2,
					mRotatePoint.y + mDrawableHeight / 2);
			mRotateDrawable.draw(canvas);

			mDeleteDrawable.setBounds(mDeletePoint.x - mDrawableWidth / 2, mDeletePoint.y - mDrawableHeight / 2, mDeletePoint.x + mDrawableWidth / 2,
					mDeletePoint.y + mDrawableHeight / 2);
			mDeleteDrawable.draw(canvas);

			mEditDrawable.setBounds(mEditPoint.x - mDrawableWidth / 2, mEditPoint.y - mDrawableHeight / 2, mEditPoint.x + mDrawableWidth / 2, mEditPoint.y
					+ mDrawableHeight / 2);
			mEditDrawable.draw(canvas);
		}
	}

	/**
	 * 设置Matrix, 强制刷新
	 */
	private void transformDraw() {
		int bitmapWidth = (int) (mBitmap.getWidth());
		int bitmapHeight = (int) (mBitmap.getHeight());
		computeRect(-framePadding, -framePadding, bitmapWidth + framePadding, bitmapHeight + framePadding, mDegree);

		// 设置缩放比例
		matrix.setScale(1.0f, 1.0f);
		// 绕着图片中心进行旋转
		if (mDegree > 5 || mDegree < -5) {
			matrix.postRotate(mDegree % 360, bitmapWidth / 2, bitmapHeight / 2);
		} else {
			matrix.postRotate(0, bitmapWidth / 2, bitmapHeight / 2);
		}
		// 设置画该图片的起始点
		matrix.postTranslate(offsetX + mDrawableWidth / 2, offsetY + mDrawableHeight / 2);

		mWeatherStickerInfo.angle = (int) mDegree;
		mWeatherStickerInfo.textSize1 = (int) (textSize1 * mScale);
		mWeatherStickerInfo.textSize2 = (int) (textSize2 * mScale);
		mWeatherStickerInfo.text = text;
		if (pluginSettingUtils != null) {
			pluginSettingUtils.setScale(MAX_SCALE, mScale);
		}
		invalidate();
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (!isEditable) {
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mPreMovePointF.set(event.getX() + mViewPaddingLeft, event.getY() + mViewPaddingTop);

			mStatus = JudgeStatus(event.getX(), event.getY());
			lastX = event.getRawX();
			lastY = event.getRawY();

			break;
		case MotionEvent.ACTION_UP:

			if (mStatus == STATUS_DELETE && isVisiable) {
				mOnRemoveViewListener.removeView(mWeatherStickerInfo, this);
				if(weatherStyle==6){
					stopTimer();
				}
				isEditable = false;
				mStatus = STATUS_INIT;
				return true;
			}

			if (isVisiable && mStatus == STATUS_EDIT && mStatus == JudgeStatus(event.getX(), event.getY())) {
				showEditView(true);
				mStatus = STATUS_INIT;
				return true;
			}
			mStatus = STATUS_INIT;
			if (isVisiable) {
				float newX = event.getRawX();
				float newY = event.getRawY();
				if (Math.abs(newX - lastX) <= 10 && Math.abs(newY - lastY) <= 10) {
					showControllerView();
				}
			}

			if (!isVisiable) {
				mOnFocuseChangeListener.focuseChange(this);
			}

			break;
		case MotionEvent.ACTION_MOVE:
			mCurMovePointF.set(event.getX() + mViewPaddingLeft, event.getY() + mViewPaddingTop);
			if (mStatus == STATUS_ROTATE_ZOOM) {

				// 角度
				double a = distance4PointF(mCenterPoint, mPreMovePointF);
				double b = distance4PointF(mPreMovePointF, mCurMovePointF);
				double c = distance4PointF(mCenterPoint, mCurMovePointF);

				double cosb = (a * a + c * c - b * b) / (2 * a * c);

				if (cosb >= 1) {
					cosb = 1f;
				}

				double radian = Math.acos(cosb);
				float newDegree = (float) radianToDegree(radian);

				// center -> proMove的向量， 我们使用PointF来实现
				PointF centerToProMove = new PointF((mPreMovePointF.x - mCenterPoint.x), (mPreMovePointF.y - mCenterPoint.y));

				// center -> curMove 的向量
				PointF centerToCurMove = new PointF((mCurMovePointF.x - mCenterPoint.x), (mCurMovePointF.y - mCenterPoint.y));

				// 向量叉乘结果, 如果结果为负数， 表示为逆时针， 结果为正数表示顺时针
				float result = centerToProMove.x * centerToCurMove.y - centerToProMove.y * centerToCurMove.x;

				if (result < 0) {
					newDegree = -newDegree;
				}

				mDegree = mDegree + newDegree;

				transformDraw();
			} else if (mStatus == STATUS_DRAG) {
				// 修改中心点
				mCenterPoint.x += mCurMovePointF.x - mPreMovePointF.x;
				mCenterPoint.y += mCurMovePointF.y - mPreMovePointF.y;

				adjustLayout();
			}

			mPreMovePointF.set(mCurMovePointF);
			break;
		}
		return true;
	}

	/**
	 * 获取四个点和View的大小
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @param degree
	 */
	private void computeRect(int left, int top, int right, int bottom, float degree) {
		float mDegree = degree;
		if (mDegree <= 5 && mDegree >= -5) {
			mDegree = 0;
		}
		Point lt = new Point(left, top);
		Point rt = new Point(right, top);
		Point rb = new Point(right, bottom);
		Point lb = new Point(left, bottom);
		Point cp = new Point((left + right) / 2, (top + bottom) / 2);
		mLTPoint = obtainRoationPoint(cp, lt, mDegree);
		mRTPoint = obtainRoationPoint(cp, rt, mDegree);
		mRBPoint = obtainRoationPoint(cp, rb, mDegree);
		mLBPoint = obtainRoationPoint(cp, lb, mDegree);

		// 计算X坐标最大的值和最小的值
		int maxCoordinateX = getMaxValue(mLTPoint.x, mRTPoint.x, mRBPoint.x, mLBPoint.x);
		int minCoordinateX = getMinValue(mLTPoint.x, mRTPoint.x, mRBPoint.x, mLBPoint.x);

		mViewWidth = maxCoordinateX - minCoordinateX;

		// 计算Y坐标最大的值和最小的值
		int maxCoordinateY = getMaxValue(mLTPoint.y, mRTPoint.y, mRBPoint.y, mLBPoint.y);
		int minCoordinateY = getMinValue(mLTPoint.y, mRTPoint.y, mRBPoint.y, mLBPoint.y);

		mViewHeight = maxCoordinateY - minCoordinateY;

		// View中心点的坐标
		Point viewCenterPoint = new Point((maxCoordinateX + minCoordinateX) / 2, (maxCoordinateY + minCoordinateY) / 2);

		offsetX = mViewWidth / 2 - viewCenterPoint.x;
		offsetY = mViewHeight / 2 - viewCenterPoint.y;

		int halfDrawableWidth = mDrawableWidth / 2;
		int halfDrawableHeight = mDrawableHeight / 2;

		// 将Bitmap的四个点的X的坐标移动offsetX + halfDrawableWidth
		mLTPoint.x += (offsetX + halfDrawableWidth);
		mRTPoint.x += (offsetX + halfDrawableWidth);
		mRBPoint.x += (offsetX + halfDrawableWidth);
		mLBPoint.x += (offsetX + halfDrawableWidth);

		// 将Bitmap的四个点的Y坐标移动offsetY + halfDrawableHeight
		mLTPoint.y += (offsetY + halfDrawableHeight);
		mRTPoint.y += (offsetY + halfDrawableHeight);
		mRBPoint.y += (offsetY + halfDrawableHeight);
		mLBPoint.y += (offsetY + halfDrawableHeight);

		mRotatePoint = LocationToPoint(RIGHT_BOTTOM);
		mDeletePoint = LocationToPoint(LEFT_TOP);
		mEditPoint = LocationToPoint(RIGHT_TOP);
	}

	/**
	 * 根据位置判断控制图标处于那个点
	 * 
	 * @return
	 */
	private Point LocationToPoint(int location) {
		switch (location) {
		case LEFT_TOP:
			return mLTPoint;
		case RIGHT_TOP:
			return mRTPoint;
		case RIGHT_BOTTOM:
			return mRBPoint;
		case LEFT_BOTTOM:
			return mLBPoint;
		}
		return mLTPoint;
	}

	/**
	 * 获取变长参数最大的值
	 * 
	 * @param array
	 * @return
	 */
	public int getMaxValue(Integer... array) {
		List<Integer> list = Arrays.asList(array);
		Collections.sort(list);
		return list.get(list.size() - 1);
	}

	/**
	 * 获取变长参数最大的值
	 * 
	 * @param array
	 * @return
	 */
	public int getMinValue(Integer... array) {
		List<Integer> list = Arrays.asList(array);
		Collections.sort(list);
		return list.get(0);
	}

	/**
	 * 获取旋转某个角度之后的点
	 * 
	 * @param viewCenter
	 * @param source
	 * @param degree
	 * @return
	 */
	public static Point obtainRoationPoint(Point center, Point source, float degree) {
		// 两者之间的距离
		Point disPoint = new Point();
		disPoint.x = source.x - center.x;
		disPoint.y = source.y - center.y;

		// 没旋转之前的弧度
		double originRadian = 0;

		// 没旋转之前的角度
		double originDegree = 0;

		// 旋转之后的角度
		double resultDegree = 0;

		// 旋转之后的弧度
		double resultRadian = 0;

		// 经过旋转之后点的坐标
		Point resultPoint = new Point();

		double distance = Math.sqrt(disPoint.x * disPoint.x + disPoint.y * disPoint.y);
		if (disPoint.x == 0 && disPoint.y == 0) {
			return center;
			// 第一象限
		} else if (disPoint.x >= 0 && disPoint.y >= 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(disPoint.y / distance);

			// 第二象限
		} else if (disPoint.x < 0 && disPoint.y >= 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(Math.abs(disPoint.x) / distance);
			originRadian = originRadian + Math.PI / 2;

			// 第三象限
		} else if (disPoint.x < 0 && disPoint.y < 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(Math.abs(disPoint.y) / distance);
			originRadian = originRadian + Math.PI;
		} else if (disPoint.x >= 0 && disPoint.y < 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(disPoint.x / distance);
			originRadian = originRadian + Math.PI * 3 / 2;
		}

		// 弧度换算成角度
		originDegree = radianToDegree(originRadian);
		resultDegree = originDegree + degree;

		// 角度转弧度
		resultRadian = degreeToRadian(resultDegree);

		resultPoint.x = (int) Math.round(distance * Math.cos(resultRadian));
		resultPoint.y = (int) Math.round(distance * Math.sin(resultRadian));
		resultPoint.x += center.x;
		resultPoint.y += center.y;

		return resultPoint;
	}

	/**
	 * 弧度换算成角度
	 * 
	 * @return
	 */
	public static double radianToDegree(double radian) {
		return radian * 180 / Math.PI;
	}

	/**
	 * 角度换算成弧度
	 * 
	 * @param degree
	 * @return
	 */
	public static double degreeToRadian(double degree) {
		return degree * Math.PI / 180;
	}

	/**
	 * 根据点击的位置判断是否点中控制旋转，缩放的图片， 初略的计算
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private int JudgeStatus(float x, float y) {
		PointF touchPoint = new PointF(x, y);
		PointF rotatePointF = new PointF(mRotatePoint);
		PointF deletePointF = new PointF(mDeletePoint);
		PointF exitPointF = new PointF(mEditPoint);
		// 点击的点到控制旋转，缩放点的距离
		float distanceToRotate = distance4PointF(touchPoint, rotatePointF);
		float distanceToDelete = distance4PointF(touchPoint, deletePointF);
		float distanceToEdit = distance4PointF(touchPoint, exitPointF);

		// 如果两者之间的距离小于 控制图标的宽度，高度的最小值，则认为点中了控制图标
		if (distanceToRotate < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_ROTATE_ZOOM;
		}
		if (distanceToDelete < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_DELETE;
		}
		if (distanceToEdit < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_EDIT;
		}
		return STATUS_DRAG;

	}

	/**
	 * 设置图片旋转角度
	 * 
	 * @param degree
	 */
	public void setImageDegree(float degree) {
		if (this.mDegree != degree) {
			this.mDegree = degree;
			transformDraw();
		}
	}

	/**
	 * 设置是否处于可缩放，平移，旋转状态
	 * 
	 * @param isEditable
	 */
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
		invalidate();
	}

	/**
	 * 设置是否显示绘制操作按钮
	 * 
	 * @param isVisiable
	 */
	public void setVisible(boolean isVisiable) {
		this.isVisiable = isVisiable;
		invalidate();
	}

	/**
	 * 两个点之间的距离
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	private float distance4PointF(PointF pf1, PointF pf2) {
		float disX = pf2.x - pf1.x;
		float disY = pf2.y - pf1.y;
		return (float) Math.sqrt(disX * disX + disY * disY);
	}


	// 设置系统时间并实时更新
	public void setWeatherStickerInfo(WeatherStickerInfo weatherStickerInfo) {
		this.mWeatherStickerInfo = weatherStickerInfo;

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		this.mContext.registerReceiver(mScreenBroadcastReceiver, filter);

		text = LockApplication.getInstance().getConfig().getLocalCity();
		if(TextUtils.isEmpty(text)){
			text=mWeatherStickerInfo.text;
			if(TextUtils.isEmpty(text)){
				text="广州";
			}
		}
		textSize1 = (int) mContext.getResources().getDimension(R.dimen.default_time_textsize);
		textSize2 = (int) mContext.getResources().getDimension(R.dimen.default_time_textsize_2);
		weatherStyle = mWeatherStickerInfo.weatherStyle;
		mScale = mWeatherStickerInfo.textSize1 * 1.0f / textSize1;
		if(mScale>=MAX_SCALE){
			mScale=MAX_SCALE;
		}
//		int defaultTextSize = (int) mContext.getResources().getDimension(R.dimen.default_time_textsize);
//		int maxTextSize = (int) (defaultTextSize * MAX_SCALE);
//		mScale = textSize1 * 1.0f / defaultTextSize;
//		MAX_SCALE = maxTextSize / textSize1;
		if (!TextUtils.isEmpty(mWeatherStickerInfo.font)) {
			typeface = Typeface.createFromFile(mWeatherStickerInfo.font);
		}
		if (TextUtils.isEmpty(text)) {
			text = LockApplication.getInstance().getConfig().getLocalCity();
			startThread();
		}
		text1 = DateTime.getTime();
		read(true);
		if (TextUtils.isEmpty(weathers)) {
			weathers = "3°~18°";
			temp = "18°";
			weatherIconRes = R.drawable.ic_weather_qing;
			switch (weatherStyle) {
			case 0:
				getNewBitMap(temp, text, weathers, weatherIconRes);
				break;
			case 1:
				getNewBitMap1(temp, text);
				break;
			case 2:
				weathers = "▼3° ▲18°";
				getNewBitMap2(temp, text, weathers);
				break;
			case 3:
				getNewBitMap3(temp, weatherIconRes);
				break;
			case 4:
				getNewBitMap4(temp, text, weatherIconRes);
				break;
			case 5:
				weathers = "3~18";
				getNewBitMap5(weathers, weatherIconRes);
				break;
			case 6:
				startTimer();
				weathers = "3~18";
				curWeather="晴";
				getNewBitMap6(temp, text, weathers, curWeather,weatherIconRes,text1);
				break;

			default:
				break;
			}
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(mWeatherStickerInfo.angle, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
		Bitmap b = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
		mCenterPoint.x = mWeatherStickerInfo.x + b.getWidth() / 2.0f;
		mCenterPoint.y = mWeatherStickerInfo.y + b.getHeight() / 2.0f;
		mDegree = weatherStickerInfo.angle;

		init();
		if (isVisiable) {
			showControllerView();
		}

	}
	

	private void startTimer() {
		// 实时更新时间
		if (timerLoop)
			return;
		timerLoop = true;
		handler.sendEmptyMessage(MSG_START_TIMER_LOOP);
	}

	private void stopTimer() {
		timerLoop = false;
	}

	public void startThread() {
		// 实时更新时间
		mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					weatherBean = WeatherUtils.getWeatherByHanZi(text);
					message = handler.obtainMessage(UPDATE_MY_TV, weatherBean);
					handler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mThread.start();
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_MY_TV:
				if (weatherBean != null) {
					curWeather = WeatherUtils.getState(weatherBean.getWeather());
					weathers = curWeather + " " + weatherBean.getLow_temp() + "°~" + weatherBean.getHigh_temp() + "°";
					text = weatherBean.getCity_name();
					temp = weatherBean.getTemp() + "°";
					weatherIconRes = WeatherUtils.getStateIcon(weatherBean.getWeather());
//					getNewBitMap(temp, text, weathers, weatherIconRes);
//					getNewBitMap1(temp, text);
//					getNewBitMap2(temp, text, weathers);
//					getNewBitMap3(temp, weatherIconRes);
//					getNewBitMap4(temp, text, weatherIconRes);
					switch (weatherStyle) {
					case 0:
						getNewBitMap(temp, text, weathers, weatherIconRes);
						break;
					case 1:
						getNewBitMap1(temp, text);
						break;
					case 2:
						weathers = "▼" + weatherBean.getLow_temp() + "°  ▲" + weatherBean.getHigh_temp() + "°";
						getNewBitMap2(temp, text, weathers);
						break;
					case 3:
						getNewBitMap3(temp, weatherIconRes);
						break;
					case 4:
						getNewBitMap4(temp, text, weatherIconRes);
						break;
					case 5:
						weathers = weatherBean.getLow_temp() + "~" + weatherBean.getHigh_temp();
						getNewBitMap5(weathers, weatherIconRes);
						break;
					case 6:
						weathers = weatherBean.getLow_temp() + "~" + weatherBean.getHigh_temp();
						getNewBitMap6(temp, text, weathers, curWeather,weatherIconRes,text1);
						break;

					default:
						break;
					}
					save();
					transformDraw();
				}
				break;
				
			case MSG_START_TIMER_LOOP:
				handler.removeMessages(MSG_START_TIMER_LOOP);
				text1 = DateTime.getTime();
				//weathers = weatherBean.getLow_temp() + "~" + weatherBean.getHigh_temp();
				if(weatherStyle==6){
				   getNewBitMap6(temp, text, weathers, curWeather,weatherIconRes,text1);
				   transformDraw();
				}
				if (timerLoop) {
					handler.sendEmptyMessageDelayed(MSG_START_TIMER_LOOP, 1000);
				}
				break;

			default:
				break;
			}
			return false;
		}
	}) {
	};

	private void save() {
		Long time = System.currentTimeMillis();
		SharedPreferences share = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			share = getContext().getSharedPreferences("weather.cfg", Context.MODE_MULTI_PROCESS);
		} else {
			share = getContext().getSharedPreferences("weather.cfg", Context.MODE_PRIVATE);
		}
		SharedPreferences.Editor shareEdit = share.edit();
		shareEdit.putString("weathers" + text + weatherStyle, weathers);
		shareEdit.putString("temp" + text, temp);
		shareEdit.putString("curWeather" + text, curWeather);
		shareEdit.putInt("weatherIconRes" + text, weatherIconRes);
		shareEdit.putLong("time" + text, time);
		shareEdit.apply();
	}

	private void read(boolean first) {
		Long secondTime = System.currentTimeMillis();
		SharedPreferences share = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			share = getContext().getSharedPreferences("weather.cfg", Context.MODE_MULTI_PROCESS);
		} else {
			share = getContext().getSharedPreferences("weather.cfg", Context.MODE_PRIVATE);
		}
		Long firstTime = share.getLong("time" + text, 0);
		if (secondTime - firstTime > 1000 * 60 * 30) {
			startThread();
		} else {
			weathers = share.getString("weathers" + text + weatherStyle, "3°~18°");
			temp = share.getString("temp" + text, "18°");
			curWeather = share.getString("curWeather" + text, "晴");
			weatherIconRes = share.getInt("weatherIconRes" + text, R.drawable.ic_weather_qing);
//			getNewBitMap(temp, text, weathers, weatherIconRes);
//			getNewBitMap2(temp, text, weathers);
//			getNewBitMap1(temp, text);
//			getNewBitMap3(temp, weatherIconRes);
//			getNewBitMap4(temp, text, weatherIconRes);
			
			switch (weatherStyle) {
			case 0:
				getNewBitMap(temp, text, weathers, weatherIconRes);
				break;
			case 1:
				getNewBitMap1(temp, text);
				break;
			case 2:
				getNewBitMap2(temp, text, weathers);
				break;
			case 3:
				getNewBitMap3(temp, weatherIconRes);
				break;
			case 4:
				getNewBitMap4(temp, text, weatherIconRes);
				break;
			case 5:
				getNewBitMap5(weathers, weatherIconRes);
				break;
			case 6:
				getNewBitMap6(temp, text, weathers, curWeather,weatherIconRes,text1);
				break;

			default:
				break;
			}
			
			if (!first) {
				transformDraw();
			}
		}
	}

	/**
	 * 监听屏幕是否开启
	 */
	private BroadcastReceiver mScreenBroadcastReceiver = new BroadcastReceiver() {
		private String action = null;

		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				read(false);
				if(weatherStyle==6){
					startTimer();
				}
			}
		}
	};

	/**
	 * 判断是否离开view
	 */
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		Log.i("debug", "visibility="+visibility);
		if (visibility == 8) {
			if (mScreenBroadcastReceiver != null) {
				try {
					mContext.unregisterReceiver(mScreenBroadcastReceiver);
				} catch (Exception e) {
					e.printStackTrace();
				}
				mScreenBroadcastReceiver = null;
			}
			stopTimer();
		}
		if (visibility == 0) {
			read(false);
			if(weatherStyle==6){
				startTimer();
			}
		}
	}

	// 地点改变输入对话框
	private void showEditView(final boolean edit) {

		EditTextDialog editTextDialog = new EditTextDialog(mContext);
		editTextDialog.setHintText("输入要查询的天气城市");
		editTextDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (TextUtils.isEmpty(text)) {
					mOnRemoveViewListener.removeView(mWeatherStickerInfo, WeatherStickerView.this);
				}
			}
		});
		editTextDialog.setEditTextOkClickListener(new EditTextDialog.OnEditTextOkClickListener() {
			@Override
			public void OnEditTextOkClick(String string) {
				text = string;
				if (!TextUtils.isEmpty(text)) {
					startThread();
					if (edit) {
						init();
					} else {
						mCenterPoint.x = mWeatherStickerInfo.x + mBitmap.getWidth() / 2;
						mCenterPoint.y = mWeatherStickerInfo.y + mBitmap.getHeight() / 2;
						mDegree = mWeatherStickerInfo.angle;
						init();
						showControllerView();
					}
				} else {
					mOnRemoveViewListener.removeView(mWeatherStickerInfo, WeatherStickerView.this);
				}
			}
		});
		editTextDialog.show();
	}

	// 显示文字操作栏
	private void showControllerView() {
		if (controllerView == null) {
			pluginSettingUtils = new PluginSettingUtils(getContext());
			pluginSettingUtils.setOnPluginSettingChange(this);
			pluginSettingUtils.initSelectData(mWeatherStickerInfo.font, mWeatherStickerInfo.textColor, mWeatherStickerInfo.shadowColor);
			pluginSettingUtils.setAlpha(mWeatherStickerInfo.alpha);
			pluginSettingUtils.setScale(MAX_SCALE, mScale);
			controllerView = pluginSettingUtils.getView();
		}
		if (controllerView != null && controllerView.getParent() == null) {
			mController_container_layout.addView(controllerView);// ??????
		}
	}
	
	private void getNewBitMap4(String temp,String text, int res) {
		text="中国，"+text;
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mWeatherStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1*mScale);
		mTextPaint.setFakeBoldText(true);
		mTextPaint.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		mTextPaint1 = new Paint();
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mWeatherStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize1*mScale/5*4);
		mTextPaint1.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}
		mTextPaint2 = new Paint();
		mTextPaint2.setAntiAlias(true);
		mTextPaint2.setColor(mWeatherStickerInfo.textColor);
		mTextPaint2.setTextSize(textSize2*mScale/3*2);
		mTextPaint2.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint2.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint2.setTypeface(typeface);
		}
		
		int width = (int) (mTextPaint1.measureText(temp));
		int height = (int) (mTextPaint1.measureText("测"));
		int width1 = (int) (mTextPaint2.measureText(text));
		int height1 = (int) (mTextPaint2.measureText("测"));
		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, res, mWeatherStickerInfo.textColor);
		Bitmap bt = null;
		if (db != null) {
			BitmapDrawable bd = (BitmapDrawable) db;
			if (bd != null) {
				bt = bd.getBitmap();
			}
		}
		if (bt == null) {
			bt = DrawableUtils.getBitmap(mContext, res);
		}
		bt = DrawableUtils.scaleTo(bt, mScale, mScale);
		int bitmapWidth = bt.getWidth();
		int bitmapHeight = bt.getHeight();
		int width2=width>width1?width:width1;
		int rectHeight=height+height1>bitmapHeight?height+height1:bitmapHeight;
		mBitmap = Bitmap.createBitmap((int) (width2 +bitmapWidth + DensityUtil.dip2px(mContext, 30)*mScale), (int) (rectHeight + DensityUtil.dip2px(mContext, 10)*mScale), Config.ARGB_8888);
		Rect targetRect = new Rect(bitmapWidth, 0, (int) (width2 +bitmapWidth + DensityUtil.dip2px(mContext, 30)*mScale), height);
		Rect targetRect1 = new Rect(bitmapWidth, height, (int) (width2 +bitmapWidth + DensityUtil.dip2px(mContext, 30)*mScale), (int) (rectHeight+ DensityUtil.dip2px(mContext, 10)*mScale));
		Canvas canvas = new Canvas(mBitmap);
		
		// 新建一个矩形
        RectF outerRect = new RectF(0, 0,  width2 +bitmapWidth + DensityUtil.dip2px(mContext, 30)*mScale, rectHeight + DensityUtil.dip2px(mContext, 10)*mScale);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0x50ffffff);
        paint.setStyle(Style.FILL);
		canvas.drawRoundRect(outerRect, 10, 10, paint);
		
		FontMetricsInt fontMetrics = mTextPaint1.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint1.setTextAlign(Paint.Align.CENTER);
		FontMetricsInt fontMetrics1 = mTextPaint2.getFontMetricsInt();
		int baseline1 = targetRect1.top + (targetRect1.bottom - targetRect1.top - fontMetrics1.bottom + fontMetrics1.top) / 2 - fontMetrics1.top;
		mTextPaint2.setTextAlign(Paint.Align.CENTER);
		canvas.drawBitmap(bt, DensityUtil.dip2px(mContext, 10)*mScale, DensityUtil.dip2px(mContext, 10)*mScale, mTextPaint);
		canvas.drawText(temp, targetRect.centerX()+DensityUtil.dip2px(mContext, 10)*mScale, baseline, mTextPaint1);
		canvas.drawText(text, targetRect1.centerX()+DensityUtil.dip2px(mContext, 10)*mScale, baseline1, mTextPaint2);
		
	}
	
	private void getNewBitMap3(String temp, int res) {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mWeatherStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1*mScale);
		mTextPaint.setFakeBoldText(true);
		mTextPaint.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		int width = (int) (mTextPaint.measureText(temp));
		int height = (int) (mTextPaint.measureText("测"));
		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, res, mWeatherStickerInfo.textColor);
		Bitmap bt = null;
		if (db != null) {
			BitmapDrawable bd = (BitmapDrawable) db;
			if (bd != null) {
				bt = bd.getBitmap();
			}
		}
		if (bt == null) {
			bt = DrawableUtils.getBitmap(mContext, res);
		}
		bt = DrawableUtils.scaleTo(bt, mScale, mScale);
		int bitmapWidth = bt.getWidth();
		int bitmapHeight = bt.getHeight();
		int rectHeight=height>bitmapHeight?height:bitmapHeight;
		mBitmap = Bitmap.createBitmap((int) (width +bitmapWidth + DensityUtil.dip2px(mContext, 30)*mScale), (int) (rectHeight + DensityUtil.dip2px(mContext, 5)*mScale), Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, (int) (width +bitmapWidth + DensityUtil.dip2px(mContext, 30)*mScale), (int) (rectHeight + DensityUtil.dip2px(mContext, 5)*mScale));
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawBitmap(bt, DensityUtil.dip2px(mContext, 10)*mScale , DensityUtil.dip2px(mContext, 10)*mScale, mTextPaint);
		canvas.drawText(temp, targetRect.centerX()+bitmapWidth/2+DensityUtil.dip2px(mContext, 10)*mScale, baseline, mTextPaint);
		
	}
	
	private void getNewBitMap2(String temp, String text,String weathers) {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mWeatherStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1*mScale*2);
		mTextPaint.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		mTextPaint1 = new Paint();
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mWeatherStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize2*mScale);
		mTextPaint1.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}
		int width = (int) (mTextPaint.measureText(temp));
		int height = (int) (mTextPaint.measureText("测"));
		int width1 = (int) (mTextPaint1.measureText(text));
		int height1 = (int) (mTextPaint1.measureText("测"));
		int width2 = (int) (mTextPaint1.measureText(weathers));

		int bitmapWidth=width>width1+width2?width:width1+width2;
		mBitmap = Bitmap.createBitmap((int) (bitmapWidth + DensityUtil.dip2px(mContext, 10)*mScale), (int) (height1 + height + DensityUtil.dip2px(mContext, 5)*mScale), Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, (int) (bitmapWidth + DensityUtil.dip2px(mContext, 10)*mScale), height);
		Rect targetRect1 = new Rect(0,(int) (height + DensityUtil.dip2px(mContext, 5)*mScale),(int) (bitmapWidth + DensityUtil.dip2px(mContext, 10)*mScale), (int) (height1 + height + DensityUtil.dip2px(mContext, 5)*mScale));
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		FontMetricsInt fontMetrics1 = mTextPaint1.getFontMetricsInt();
		int baseline1 = targetRect1.top + (targetRect1.bottom - targetRect1.top - fontMetrics1.bottom + fontMetrics1.top) / 2 - fontMetrics1.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint1.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(temp, targetRect.centerX(), baseline, mTextPaint);
		canvas.drawText(weathers+"   "+text, targetRect1.centerX(), baseline1, mTextPaint1);
		
	}
	
	private void getNewBitMap1(String temp, String text) {
		text="China, "+Trans2PinYin.trans2PinYin(text);
		temp=temp+"C";
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mWeatherStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1*mScale);
		mTextPaint.setFakeBoldText(true);
		mTextPaint.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		mTextPaint1 = new Paint();
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mWeatherStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize2*mScale);
		mTextPaint1.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}
		int width = (int) (mTextPaint.measureText(temp));
		int height = (int) (mTextPaint.measureText("测"));
		// int width1 = (int) (mTextPaint.measureText(text));
		int width1 = (int) (mTextPaint1.measureText(text));
		int height1 = (int) (mTextPaint1.measureText("测"));
		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, R.drawable.ic_weather_local, mWeatherStickerInfo.textColor);
		Bitmap bt = null;
		if (db != null) {
			BitmapDrawable bd = (BitmapDrawable) db;
			if (bd != null) {
				bt = bd.getBitmap();
			}
		}
		if (bt == null) {
			bt = DrawableUtils.getBitmap(mContext, R.drawable.ic_weather_local);
		}
		bt = DrawableUtils.scaleTo(bt,0.8f, 0.8f);
		bt = DrawableUtils.scaleTo(bt, mScale, mScale);
		int bitmapWidth = bt.getWidth();
		int bitmapHeight = bt.getHeight();
		int rectWidth=width1+bitmapWidth>width?width1+bitmapWidth:width;
		int rectHeight=height1>bitmapHeight?height1:bitmapHeight;
		mBitmap = Bitmap.createBitmap((int) (rectWidth + DensityUtil.dip2px(mContext, 10)*mScale), (int) (rectHeight + height + DensityUtil.dip2px(mContext, 5)*mScale), Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, (int) (rectWidth + DensityUtil.dip2px(mContext, 10)*mScale), rectHeight);
		Rect targetRect1 = new Rect(0,(int) (rectHeight + DensityUtil.dip2px(mContext, 5)*mScale),(int) (rectWidth + DensityUtil.dip2px(mContext, 10)*mScale), (int) (rectHeight + height + DensityUtil.dip2px(mContext, 5)*mScale));
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint1.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		FontMetricsInt fontMetrics1 = mTextPaint.getFontMetricsInt();
		int baseline1 = targetRect1.top + (targetRect1.bottom - targetRect1.top - fontMetrics1.bottom + fontMetrics1.top) / 2 - fontMetrics1.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint1.setTextAlign(Paint.Align.CENTER);
		canvas.drawBitmap(bt, targetRect.centerX()-bitmapWidth/2-width1/2 - DensityUtil.dip2px(mContext, 5)*mScale,DensityUtil.dip2px(mContext, 2)*mScale, mTextPaint1);
		canvas.drawText(text, targetRect.centerX()+bitmapWidth/2+DensityUtil.dip2px(mContext,5)*mScale, baseline, mTextPaint1);
		canvas.drawText(temp, targetRect1.centerX(), baseline1, mTextPaint);
		
	}

	private void getNewBitMap(String temp, String text, String weathers, int res) {

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mWeatherStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1*mScale);
		mTextPaint.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		mTextPaint1 = new Paint();
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mWeatherStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize2*mScale);
		mTextPaint1.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}

		int width = (int) (mTextPaint.measureText(temp));
		int height = (int) (mTextPaint.measureText("测"));
		// int width1 = (int) (mTextPaint.measureText(text));
		int width2 = (int) (mTextPaint1.measureText(weathers));
		int height1 = (int) (mTextPaint1.measureText("测"));

		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, res, mWeatherStickerInfo.textColor);
		Bitmap bt = null;
		if (db != null) {
			BitmapDrawable bd = (BitmapDrawable) db;
			if (bd != null) {
				bt = bd.getBitmap();
			}
		}
		if (bt == null) {
			bt = DrawableUtils.getBitmap(mContext, res);
		}
		bt = DrawableUtils.scaleTo(bt, mScale, mScale);

		int bitmapWidth = bt.getWidth();
		int bitmapHeight = bt.getHeight();
		int mHeight = bitmapHeight > (height + height1) ? bitmapHeight : (height + height1);

		mBitmap = Bitmap.createBitmap((int) (width + width2 + bitmapWidth + DensityUtil.dip2px(mContext, 10)*mScale), mHeight, Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, width, mHeight);
		Rect targetRect1 = new Rect((int) (width + bitmapWidth + width2 + DensityUtil.dip2px(mContext, 10)*mScale), 0, width + bitmapWidth, mHeight);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		FontMetricsInt fontMetrics1 = mTextPaint1.getFontMetricsInt();
		int baseline1 = targetRect1.top + (targetRect1.bottom - targetRect1.top - fontMetrics1.bottom + fontMetrics1.top) / 2 - fontMetrics1.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint1.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(temp, targetRect.centerX(), baseline, mTextPaint);
		canvas.drawBitmap(bt, targetRect.centerX() + width / 2, baseline - mHeight / 2 - DensityUtil.dip2px(mContext, 10)*mScale, mTextPaint);
		canvas.drawText(text, targetRect1.centerX(), baseline1 - height1 / 2 - DensityUtil.dip2px(mContext, 3)*mScale, mTextPaint1);
		canvas.drawText(weathers, targetRect1.centerX(), baseline1 + height1, mTextPaint1);

	}
	
	private void getNewBitMap5(String weathers, int res) {

		if (res == R.drawable.ic_weather_chen) {
			res = R.drawable.ic_weather_chen1;

		} else if (res == R.drawable.ic_weather_mai) {
			res = R.drawable.ic_weather_mai1;

		} else if (res == R.drawable.ic_weather_qing) {
			res = R.drawable.ic_weather_qing1;

		} else if (res == R.drawable.ic_weather_xue) {
			res = R.drawable.ic_weather_xue1;

		} else if (res == R.drawable.ic_weather_yin) {
			res = R.drawable.ic_weather_yin1;

		} else if (res == R.drawable.ic_weather_yu) {
			res = R.drawable.ic_weather_yu1;

		} else if (res == R.drawable.ic_weather_yun) {
			res = R.drawable.ic_weather_yun1;

		} else {
		}
		
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mWeatherStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1/2*mScale);
		mTextPaint.setFakeBoldText(true);
		mTextPaint.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		mTextPaint1 = new Paint();
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mWeatherStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize2/2*mScale);
		mTextPaint1.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}
		int width = (int) (mTextPaint.measureText(weathers));
		int height = (int) (mTextPaint.measureText("1"));
		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, res, mWeatherStickerInfo.textColor);
		Bitmap bt = null;
		if (db != null) {
			BitmapDrawable bd = (BitmapDrawable) db;
			if (bd != null) {
				bt = bd.getBitmap();
			}
		}
		if (bt == null) {
			bt = DrawableUtils.getBitmap(mContext, res);
		}
		bt = DrawableUtils.scaleTo(bt, mScale, mScale);
		int bitmapWidth = bt.getWidth();
		int bitmapHeight = bt.getHeight();
		mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint1.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint1.setTextAlign(Paint.Align.CENTER);
		canvas.drawBitmap(bt, 0,0, mTextPaint);
		canvas.drawText(weathers, targetRect.centerX()+width*0.35f, baseline+height*0.9f, mTextPaint1);
		
	}
	//getNewBitMap6(temp, text, weathers, curWeather,weatherIconRes);
    private void getNewBitMap6(String temp,String text,String weathers,String curWeather, int res,String text1) {
		//Log.i("debug", "temp="+temp+"    text="+text+"    weathers="+weathers+"    curweather="+curWeather+"  text1="+text1);
		if (res == R.drawable.ic_weather_chen) {
			res = R.drawable.ic_weather_chen2;

		} else if (res == R.drawable.ic_weather_mai) {
			res = R.drawable.ic_weather_mai2;

		} else if (res == R.drawable.ic_weather_qing) {
			res = R.drawable.ic_weather_qing2;

		} else if (res == R.drawable.ic_weather_xue) {
			res = R.drawable.ic_weather_xue2;

		} else if (res == R.drawable.ic_weather_yin) {
			res = R.drawable.ic_weather_yin2;

		} else if (res == R.drawable.ic_weather_yu) {
			res = R.drawable.ic_weather_yu2;

		} else if (res == R.drawable.ic_weather_yun) {
			res = R.drawable.ic_weather_yun2;

		} else {
		}
		
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mWeatherStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1/2*mScale);
		mTextPaint.setFakeBoldText(true);
		mTextPaint.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		mTextPaint1 = new Paint();
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mWeatherStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize2*mScale);
		mTextPaint1.setShadowLayer(5, 0, 0, mWeatherStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mWeatherStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}
		int width1 = (int) (mTextPaint.measureText(temp));
		int width2 = (int) (mTextPaint.measureText(text));
		int width3 = (int) (mTextPaint1.measureText(weathers));
		int width4 = (int) (mTextPaint1.measureText(curWeather));
		int width5 = (int) (mTextPaint.measureText(text1));
		int height1 = (int) (mTextPaint.measureText("1"));
		int height2 = (int) (mTextPaint1.measureText("1"));
		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, res, mWeatherStickerInfo.textColor);
		Bitmap bt = null;
		if (db != null) {
			BitmapDrawable bd = (BitmapDrawable) db;
			if (bd != null) {
				bt = bd.getBitmap();
			}
		}
		if (bt == null) {
			bt = DrawableUtils.getBitmap(mContext, res);
		}
		bt = DrawableUtils.scaleTo(bt, mScale, mScale);
		int bitmapWidth = bt.getWidth();
		int bitmapHeight = bt.getHeight();
		mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint1.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint1.setTextAlign(Paint.Align.CENTER);
		canvas.drawBitmap(bt, 0,0, mTextPaint);
		canvas.drawText(text, targetRect.centerX()-width2*2, baseline+height1*0.9f, mTextPaint);
		canvas.drawText(temp, targetRect.centerX()+width1*1.5f, baseline+height1*0.9f, mTextPaint);
		canvas.drawText(curWeather, targetRect.centerX()-width2*1.3f, baseline+height2*4, mTextPaint1);
		canvas.drawText(weathers, targetRect.centerX()+width3*1.3f, baseline+height2*4, mTextPaint1);
		canvas.drawText(text1, targetRect.centerX()-width5/2, baseline+height1*3, mTextPaint);
		
	}

	public void setOnRemoveViewListener(OnRemoveStickerViewListener onRemoveViewListener) {
		this.mOnRemoveViewListener = onRemoveViewListener;
	}

	public void setOnUpdateViewListener(OnUpdateViewListener onUpdateViewListener) {
		this.mOnUpdateViewListener = onUpdateViewListener;
	}

	public void setOnFocuseChangeListener(OnFocuseChangeListener onFocuseChangeListener) {
		this.mOnFocuseChangeListener = onFocuseChangeListener;
	}

	public void setContainerLayoutParams(LayoutParams containerLayoutParams) {
		this.mContainerLayoutParams = containerLayoutParams;
	}

	public void setControllerContainerLayout(LinearLayout controller_container_layout) {
		this.mController_container_layout = controller_container_layout;
	}

	@Override
	public void change(String fontPath, String fontUrl, float size, int color, int shadowColor,int alpha) {
		if (color == shadowColor) {
			shadowColor = 0x00000000;
		}
		if (TextUtils.isEmpty(fontPath)) {
			typeface = null;
		} else {
			if (!fontPath.equals(mWeatherStickerInfo.font)) {
				mWeatherStickerInfo.font = fontPath;
				typeface = Typeface.createFromFile(mWeatherStickerInfo.font);
			}
		}
		this.mScale = size;
		mWeatherStickerInfo.alpha = alpha;
		mWeatherStickerInfo.textColor = color;
		mWeatherStickerInfo.font = fontPath;
		mWeatherStickerInfo.shadowColor = shadowColor;
		mWeatherStickerInfo.fontUrl = fontUrl;
//		getNewBitMap(temp, text, weathers, weatherIconRes);
//		getNewBitMap1(temp, text);
//		getNewBitMap2(temp, text, weathers);
//		getNewBitMap3(temp, weatherIconRes);
//		getNewBitMap4(temp, text, weatherIconRes);
		switch (weatherStyle) {
		case 0:
			getNewBitMap(temp, text, weathers, weatherIconRes);
			break;
		case 1:
			getNewBitMap1(temp, text);
			break;
		case 2:
			getNewBitMap2(temp, text, weathers);
			break;
		case 3:
			getNewBitMap3(temp, weatherIconRes);
			break;
		case 4:
			getNewBitMap4(temp, text, weatherIconRes);
			break;
		case 5:
			getNewBitMap5(weathers, weatherIconRes);
			break;
		case 6:
			getNewBitMap6(temp, text, weathers, curWeather,weatherIconRes,text1);
			break;

		default:
			break;
		}
		transformDraw();
	}

}
