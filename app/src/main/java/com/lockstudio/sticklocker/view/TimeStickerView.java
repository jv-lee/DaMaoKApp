package com.lockstudio.sticklocker.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.lockstudio.sticklocker.Interface.OnFocuseChangeListener;
import com.lockstudio.sticklocker.Interface.OnRemoveStickerViewListener;
import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.model.TimeStickerInfo;
import com.lockstudio.sticklocker.util.DateTime;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.PluginSettingUtils;
import com.lockstudio.sticklocker.util.PluginSettingUtils.OnPluginSettingChange;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.opda.android.activity.R;

/**
 * 单手对图片进行缩放，旋转，平移操作，详情请查看
 * 
 */
public class TimeStickerView extends View implements OnPluginSettingChange {
	/**
	 * 图片的最大缩放比例
	 */
	private float MAX_SCALE = 2.0f;

	/**
	 * 图片的最小缩放比例
	 */
	private final float MIN_SCALE = 0.4f;

	/**
	 * 控制缩放，旋转图标所在四个点得位置
	 */
	private final int LEFT_TOP = 0;
	private final int RIGHT_TOP = 1;
	private final int RIGHT_BOTTOM = 2;
	private final int LEFT_BOTTOM = 3;

	/**
	 * 一些默认的常量
	 */
	private final int DEFAULT_FRAME_PADDING = 8;
	private final int DEFAULT_FRAME_COLOR = Color.GRAY;
	private final float DEFAULT_SCALE = 1.0f;
	private final float DEFAULT_DEGREE = 0;

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

	/**
	 * 用于缩放，旋转的图标
	 */
	private Drawable mRotateDrawable, mDeleteDrawable;

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

	/**
	 * 初始状态
	 */
	private final int STATUS_INIT = 0;

	/**
	 * 拖动状态
	 */
	private final int STATUS_DRAG = 1;

	/**
	 * 旋转或者放大状态
	 */
	private final int STATUS_ROTATE_ZOOM = 2;

	/**
	 * 删除
	 */
	private final int STATUS_DELETE = 3;

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

	private TimeStickerInfo mTimeStickerInfo;
	private OnRemoveStickerViewListener mOnRemoveViewListener;
	private OnUpdateViewListener mOnUpdateViewListener;
	private OnFocuseChangeListener mOnFocuseChangeListener;
	private LayoutParams mContainerLayoutParams;
	private String text1;
	private String text2;
	private int textSize1;
	private int textSize2;
	private int timeStyle;

	private LinearLayout mController_container_layout;
	private View controllerView;
	private Context mContext;
	private PluginSettingUtils pluginSettingUtils;
	private boolean timerLoop = false;

	private static final int MSG_START_TIMER_LOOP = 300;
	private float lastX = 0;
	private float lastY = 0;
	private Typeface typeface;

	public TimeStickerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext = context;
	}

	public TimeStickerView(Context context) {
		this(context, null);
		this.mContext = context;
	}

	public TimeStickerView(Context context, AttributeSet attrs, int defStyle) {
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

			mTimeStickerInfo.x = mViewPaddingLeft + mDrawableWidth / 2 + framePadding;
			mTimeStickerInfo.y = mViewPaddingTop + mDrawableHeight / 2 + framePadding;

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

		mTimeStickerInfo.angle = (int) mDegree;
		mTimeStickerInfo.textSize1 = (int) (textSize1 * mScale);
		mTimeStickerInfo.textSize2 = (int) (textSize2 * mScale);
		mTimeStickerInfo.timeStyle = timeStyle;
		if (pluginSettingUtils != null) {
			pluginSettingUtils.setScale(MAX_SCALE, mScale);
		}
		invalidate();
	}

	@Override
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
				mOnRemoveViewListener.removeView(mTimeStickerInfo, this);
				isEditable = false;
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
	private int getMaxValue(Integer... array) {
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
	private int getMinValue(Integer... array) {
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
	private Point obtainRoationPoint(Point center, Point source, float degree) {
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
	private double radianToDegree(double radian) {
		return radian * 180 / Math.PI;
	}

	/**
	 * 角度换算成弧度
	 * 
	 * @param degree
	 * @return
	 */
	private double degreeToRadian(double degree) {
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
		// 点击的点到控制旋转，缩放点的距离
		float distanceToRotate = distance4PointF(touchPoint, rotatePointF);
		float distanceToDelete = distance4PointF(touchPoint, deletePointF);

		// 如果两者之间的距离小于 控制图标的宽度，高度的最小值，则认为点中了控制图标
		if (distanceToRotate < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_ROTATE_ZOOM;
		}
		if (distanceToDelete < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_DELETE;
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
	public void setTimeStickerInfo(TimeStickerInfo timeStickerInfo) {

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		this.mContext.registerReceiver(mScreenBroadcastReceiver, filter);

		this.mTimeStickerInfo = timeStickerInfo;
		textSize1 = (int) mContext.getResources().getDimension(R.dimen.default_time_textsize);
		textSize2 = (int) mContext.getResources().getDimension(R.dimen.default_time_textsize_2);
		timeStyle = mTimeStickerInfo.timeStyle;

		mScale = mTimeStickerInfo.textSize1 * 1.0f / textSize1;
		if (mScale >= MAX_SCALE) {
			mScale = MAX_SCALE;
		}
		text1 = DateTime.getTime();
		if(TextUtils.isEmpty(text1)){
			DateFormat df = new SimpleDateFormat("HH:mm");
			text1=df.format(new Date());
		}

		if (!TextUtils.isEmpty(mTimeStickerInfo.font)) {
			typeface = Typeface.createFromFile(mTimeStickerInfo.font);
		}

		switch (timeStyle) {
		case 0:
			text2 = DateTime.getWeedendAndDay();
			getNewBitMap1(text1, text2);
			break;
		case 1:
			text2 = DateTime.getWeedendAndDay();
			getNewBitMap(text1, text2);
			break;
		case 2:
			text2 = DateTime.getWeedendAndDayEN();
			getNewBitMap1(text1, text2);
			break;
		case 3:
			getNewBitMap(text1);
			break;
		case 4:
			getNewBitMap1(text1);
			break;
		case 5:
			text2 = DateTime.getWeedendAndDayEN();
			getNewBitMap2(text1, text2);
			break;
		case 6:
			getNewBitMap();
			break;
		case 7:
			text2 = DateTime.getMonthAndDay();
			getNewBitMap2(text2);
			break;
		case 8:
			getNewBitMap3(text1);
			break;
		case 9:
			getNewBitMap4(text1);
			break;
		case 10:
			getNewBitMap5(text1);
			break;
		case 11:
			getNewBitMap6(text1);
			break;

		default:
			break;
		}
		Matrix matrix = new Matrix();
		matrix.postRotate(mTimeStickerInfo.angle, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
		Bitmap b = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
		mCenterPoint.x = mTimeStickerInfo.x + b.getWidth() / 2.0f;
		mCenterPoint.y = mTimeStickerInfo.y + b.getHeight() / 2.0f;
		mDegree = timeStickerInfo.angle;
		init();
		if (isVisiable) {
			showControllerView();
		}

		startTimer();
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START_TIMER_LOOP:
				handler.removeMessages(MSG_START_TIMER_LOOP);
				text1 = DateTime.getTime();
				switch (timeStyle) {
				case 0:
					text2 = DateTime.getWeedendAndDay();
					getNewBitMap1(text1, text2);
					break;
				case 1:
					text2 = DateTime.getWeedendAndDay();
					getNewBitMap(text1, text2);
					break;
				case 2:
					text2 = DateTime.getWeedendAndDayEN();
					getNewBitMap1(text1, text2);
					break;
				case 3:
					getNewBitMap(text1);
					break;
				case 4:
					getNewBitMap1(text1);
					break;
				case 5:
					text2 = DateTime.getWeedendAndDayEN();
					getNewBitMap2(text1, text2);
					break;
				case 6:
					getNewBitMap();
					break;
				case 7:
					text2 = DateTime.getMonthAndDay();
					getNewBitMap2(text2);
					break;
				case 8:
					getNewBitMap3(text1);
					break;
				case 9:
					getNewBitMap4(text1);
					break;
				case 10:
					getNewBitMap5(text1);
					break;
				case 11:
					getNewBitMap6(text1);
					break;

				default:
					break;
				}
				transformDraw();

				if (timerLoop) {
					handler.sendEmptyMessageDelayed(MSG_START_TIMER_LOOP, 1000);
				}
				break;

			default:
				break;
			}
			return false;
		}
	});

	/**
	 * 监听屏幕是否开启
	 */
	private BroadcastReceiver mScreenBroadcastReceiver = new BroadcastReceiver() {
		private String action = null;

		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				startTimer();
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				stopTimer();
			}
		}
	};

	/**
	 * 判断是否离开view
	 */
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if (visibility == VISIBLE) {
			startTimer();
		} else {
			stopTimer();
			if (mScreenBroadcastReceiver != null) {
				this.mContext.unregisterReceiver(mScreenBroadcastReceiver);
				mScreenBroadcastReceiver = null;
			}
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
		handler.removeMessages(MSG_START_TIMER_LOOP);
	}

	// 显示文字操作栏
	private void showControllerView() {
		if (controllerView == null) {
			pluginSettingUtils = new PluginSettingUtils(getContext());
			pluginSettingUtils.setOnPluginSettingChange(this);
			pluginSettingUtils.initSelectData(mTimeStickerInfo.font, mTimeStickerInfo.textColor, mTimeStickerInfo.shadowColor);
			pluginSettingUtils.setAlpha(mTimeStickerInfo.alpha);
			pluginSettingUtils.setScale(MAX_SCALE, mScale);
			controllerView = pluginSettingUtils.getView();
		}
		if (controllerView != null && controllerView.getParent() == null) {
			mController_container_layout.addView(controllerView);// ??????
		}
	}

	private void getNewBitMap(String text1, String text2) {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mTimeStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1 * mScale);
		mTextPaint.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		mTextPaint1 = new Paint();
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mTimeStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize2 * mScale);
		mTextPaint1.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}

		String str0 = null;
		String str1 = null;
		String st1[] = text1.split("\\:");
		str0 = st1[0];
		str1 = st1[1];
		String str2 = null;
		String str3 = null;
		String st2[] = text2.split("  ");
		str2 = st2[0];
		str3 = st2[1];

		int width = (int) (mTextPaint.measureText(str0));
		int width1 = (int) (mTextPaint.measureText(str1));
		int width2 = (int) (mTextPaint1.measureText(str2));
		int width3 = (int) (mTextPaint1.measureText(str3));
		int widthline = width > width1 ? width : width1;
		int widthbt = (int) (((width + width3) > (width1 + width2) ? (width + width3) : (width1 + width2)) + DensityUtil.dip2px(mContext, 15) * mScale);
		int height = (int) (mTextPaint.measureText("测"));
		int height1 = (int) (mTextPaint.measureText("测"));
		int height2 = height + height1;
		mBitmap = Bitmap.createBitmap(widthbt, height + height1 + 10, Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, widthbt, height);
		Rect targetRect1 = new Rect(0, height, widthbt, height + height1);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		FontMetricsInt fontMetrics1 = mTextPaint1.getFontMetricsInt();
		int baseline1 = targetRect1.top + (targetRect1.bottom - targetRect1.top - fontMetrics1.bottom + fontMetrics1.top) / 2 - fontMetrics1.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint1.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(str0, targetRect.centerX() - width / 2 - DensityUtil.dip2px(mContext, 7) * mScale, baseline, mTextPaint);
		canvas.drawText(str3, targetRect.centerX() + width3 / 2 + DensityUtil.dip2px(mContext, 5) * mScale, baseline, mTextPaint1);
		canvas.drawText(str1, targetRect1.centerX() - width1 / 2 - DensityUtil.dip2px(mContext, 7) * mScale, baseline1 + 10, mTextPaint);
		canvas.drawText(str2, targetRect1.centerX() + width2 / 2 + DensityUtil.dip2px(mContext, 5) * mScale, baseline1 + 10, mTextPaint1);
		canvas.drawLine(targetRect.left + widthline + DensityUtil.dip2px(mContext, 7) * mScale, targetRect.top + DensityUtil.dip2px(mContext, 7) * mScale,
				targetRect1.left + widthline + DensityUtil.dip2px(mContext, 7) * mScale, targetRect1.bottom - DensityUtil.dip2px(mContext, 5) * mScale,
				mTextPaint);

	}

	private void getNewBitMap1(String text1, String text2) {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mTimeStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1 * mScale);
		mTextPaint.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		mTextPaint1 = new Paint();
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mTimeStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize2 * mScale);
		mTextPaint1.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}

		int width = (int) (mTextPaint.measureText(text1));
		int height = (int) (mTextPaint.measureText("测"));
		int width1 = (int) (mTextPaint1.measureText(text2));
		int height1 = (int) (mTextPaint1.measureText("测"));
		int width2 = width > width1 ? width : width1;
		mBitmap = Bitmap.createBitmap(width2, (int) (height + height1 + DensityUtil.dip2px(mContext, 7) * mScale), Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, width2, height);
		Rect targetRect1 = new Rect(0, height, width2, (int) (height + height1 + DensityUtil.dip2px(mContext, 7) * mScale));
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		FontMetricsInt fontMetrics1 = mTextPaint1.getFontMetricsInt();
		int baseline1 = targetRect1.top + (targetRect1.bottom - targetRect1.top - fontMetrics1.bottom + fontMetrics1.top) / 2 - fontMetrics1.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint1.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(text1, targetRect.centerX(), baseline, mTextPaint);
		canvas.drawText(text2, targetRect1.centerX(), baseline1, mTextPaint1);

	}

	private void getNewBitMap2(String text1, String text2) {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mTimeStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1 * mScale / 3 * 2);
		mTextPaint.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		mTextPaint1 = new Paint();
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mTimeStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize2 * mScale / 3 * 2);
		mTextPaint1.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}

		int width = (int) (mTextPaint.measureText(text1));
		int height = (int) (mTextPaint.measureText("测"));
		int width1 = (int) (mTextPaint1.measureText(text2));
		int height1 = (int) (mTextPaint1.measureText("测"));
		int width2 = width > width1 ? width : width1;
		mBitmap = Bitmap.createBitmap(width2 * 2, width2 * 2, Config.ARGB_8888);
		Rect targetRect = new Rect(width2 * 2 / 5, width2 * 2 / 5, width2 * 2 / 5 * 4, width2 * 2 / 5 * 4);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint1.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(text1, targetRect.centerX(), baseline - height1, mTextPaint);
		canvas.drawText(text2, targetRect.centerX(), baseline + DensityUtil.dip2px(mContext, 10) * mScale, mTextPaint1);

		Paint paint1 = new Paint();
		paint1.setColor(Color.WHITE);
		paint1.setFakeBoldText(true);
		paint1.setStrokeWidth(2);
		paint1.setStyle(Style.STROKE);
		paint1.setAntiAlias(true);// 设置画笔的锯齿效果
		paint1.setColor(mTimeStickerInfo.textColor);
		paint1.setTextSize(textSize2);
		paint1.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		paint1.setAlpha(mTimeStickerInfo.alpha);
		// canvas.drawRect(width2*2/5, width2*2/5, width2*2/5*4, width2*2/5*4,
		// paint1);
		Path path1 = new Path();
		path1.moveTo(0, width2 * 2 / 2);
		path1.lineTo(width2 * 2 / 2, width2 * 2);
		path1.lineTo(width2 * 2, width2 * 2 / 2);
		path1.lineTo(width2 * 2 / 2, 0);
		path1.close();// 封闭
		canvas.drawPath(path1, paint1);
		Path path2 = new Path();
		path2.moveTo(width2 * 2 / 5, width2 * 2 / 5);
		path2.lineTo(width2 * 2 / 5, width2 * 2 / 5 * 4);
		path2.lineTo(width2 * 2 / 5 * 4, width2 * 2 / 5 * 4);
		path2.lineTo(width2 * 2 / 5 * 4, width2 * 2 / 5);
		path2.close();// 封闭
		canvas.drawPath(path2, paint1);

	}

	private void getNewBitMap(String text1) {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mTimeStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1 * mScale);
		mTextPaint.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		mTextPaint1 = new Paint();
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mTimeStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize2 * mScale);
		mTextPaint1.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}

		String str0 = null;
		String str1 = null;
		String st1[] = text1.split("\\:");
		str0 = st1[0];
		str1 = ": " + st1[1];

		int width = (int) (mTextPaint.measureText(str0));
		int height = (int) (mTextPaint.measureText("测"));
		int width1 = (int) (mTextPaint1.measureText(str1));
		int height1 = (int) (mTextPaint1.measureText("测"));
		int height2 = height > height1 ? height : height1;
		int width2 = (int) (width + width1 + DensityUtil.dip2px(mContext, 10) * mScale);
		mBitmap = Bitmap.createBitmap(width2, (int) (height2 + DensityUtil.dip2px(mContext, 5) * mScale), Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, width2, (int) (height2 + DensityUtil.dip2px(mContext, 5) * mScale));
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint1.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(str0, targetRect.centerX() - width / 4, baseline, mTextPaint);
		canvas.drawText(str1, targetRect.centerX() + width1, baseline - height / 2, mTextPaint1);

	}

	private void getNewBitMap1(String text1) {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setFakeBoldText(true);
		mTextPaint.setStrokeWidth(5);
		mTextPaint.setColor(mTimeStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1 * mScale);
		mTextPaint.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		mTextPaint1 = new Paint();
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mTimeStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize1 * mScale);
		mTextPaint1.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}

		String str0 = null;
		String str1 = null;
		String st1[] = text1.split("\\:");
		str0 = st1[0];
		str1 = st1[1];

		int width = (int) (mTextPaint.measureText(str0));
		int width1 = (int) (mTextPaint.measureText(str1));
		int width2 = width > width1 ? width : width1;
		int height = (int) (mTextPaint.measureText("测"));
		int height1 = (int) (mTextPaint.measureText("测"));
		int height2 = height + height1;
		mBitmap = Bitmap.createBitmap(width2, height2 + 10, Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, width2, height2);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint1.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(str0, targetRect.centerX(), baseline - height / 2, mTextPaint);
		canvas.drawText(str1, targetRect.centerX(), baseline + height / 2, mTextPaint1);

	}

	private void getNewBitMap3(String text1) {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setFakeBoldText(true);
		mTextPaint.setStrokeWidth(5);
		mTextPaint.setColor(mTimeStickerInfo.textColor);
		mTextPaint.setTextSize(textSize2 * mScale);
		mTextPaint.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}

		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, R.drawable.ic_time1, mTimeStickerInfo.textColor);
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

		bt = DrawableUtils.scaleTo(bt, 0.8f, 0.8f);
		bt = DrawableUtils.scaleTo(bt, mScale, mScale);
		int bitmapWidth = bt.getWidth();
		int bitmapHeight = bt.getHeight();

		// mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
		// Config.ARGB_8888);
		mBitmap = Bitmap.createBitmap(bt, 0, 0, bitmapWidth, bitmapHeight);
		Rect targetRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(text1, targetRect.centerX(), baseline, mTextPaint);

	}

	private void getNewBitMap4(String text1) {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setFakeBoldText(true);
		mTextPaint.setStrokeWidth(5);
		mTextPaint.setColor(mTimeStickerInfo.textColor);
		mTextPaint.setTextSize(textSize2 * 3 / 2 * mScale);
		mTextPaint.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}

		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, R.drawable.ic_time2, mTimeStickerInfo.textColor);
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

		bt = DrawableUtils.scaleTo(bt, 0.8f, 0.8f);
		bt = DrawableUtils.scaleTo(bt, mScale, mScale);
		int bitmapWidth = bt.getWidth();
		int bitmapHeight = bt.getHeight();

		mBitmap = Bitmap.createBitmap(bt, 0, 0, bitmapWidth, bitmapHeight);
		Rect targetRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(text1, targetRect.centerX(), baseline, mTextPaint);

	}

	private void getNewBitMap5(String text1) {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setFakeBoldText(true);
		mTextPaint.setStrokeWidth(5);
		mTextPaint.setColor(mTimeStickerInfo.textColor);
		mTextPaint.setTextSize(textSize2 / 3 * 2 * mScale);
		mTextPaint.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}

		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, R.drawable.ic_time3, mTimeStickerInfo.textColor);
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

		bt = DrawableUtils.scaleTo(bt, 0.8f, 0.8f);
		bt = DrawableUtils.scaleTo(bt, mScale, mScale);
		int bitmapWidth = bt.getWidth();
		int bitmapHeight = bt.getHeight();

		String str0 = null;
		String str1 = null;
		String st1[] = text1.split("\\:");
		str0 = st1[0];
		str1 = st1[1];

		int width = (int) (mTextPaint.measureText(str0));
		int width1 = (int) (mTextPaint.measureText(str1));

		mBitmap = Bitmap.createBitmap(bt, 0, 0, bitmapWidth, bitmapHeight);
		Rect targetRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(str0, targetRect.centerX() - bitmapHeight / 4 + DensityUtil.dip2px(mContext, 1) * mScale,
				baseline - bitmapWidth / 5 + DensityUtil.dip2px(mContext, 2) * mScale, mTextPaint);
		canvas.drawText(str1, targetRect.centerX() + bitmapHeight / 4 - DensityUtil.dip2px(mContext, 2) * mScale,
				baseline - bitmapWidth / 5 + DensityUtil.dip2px(mContext, 2) * mScale, mTextPaint);

	}

	private void getNewBitMap6(String text1) {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setFakeBoldText(true);
		mTextPaint.setStrokeWidth(5);
		mTextPaint.setColor(mTimeStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1 / 3 * 2 * mScale);
		mTextPaint.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}

		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, R.drawable.ic_time4, mTimeStickerInfo.textColor);
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

		bt = DrawableUtils.scaleTo(bt, 0.8f, 0.8f);
		bt = DrawableUtils.scaleTo(bt, mScale, mScale);
		int bitmapWidth = bt.getWidth();
		int bitmapHeight = bt.getHeight();
		int width = (int) (mTextPaint.measureText(text1));
		int height = (int) (mTextPaint.measureText("1"));

		mBitmap = Bitmap.createBitmap(bt, 0, 0, bitmapWidth, bitmapHeight);
		Rect targetRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(text1, targetRect.centerX() + width / 3, baseline + height / 2, mTextPaint);

	}

	private void getNewBitMap() {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Style.STROKE);
		mTextPaint.setColor(mTimeStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1 * mScale);
		mTextPaint.setStrokeWidth(4);
		mTextPaint.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		float timeScale = mScale;
		if (timeScale < 0.7f) {
			timeScale = 0.7f;
		}
		if (timeScale > 1.5f) {
			timeScale = 1.5f;
		}
		mTextPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mTimeStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize2 / 2 * timeScale);
		// mTextPaint1.setStyle(Paint.Style.STROKE);
		mTextPaint1.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}

		int width = (int) (textSize1 * mScale * 5 / 2);
		mBitmap = Bitmap.createBitmap((int) (width + DensityUtil.dip2px(mContext, 4) * mScale), (int) (width + DensityUtil.dip2px(mContext, 4) * mScale),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);

		canvas.drawCircle((width + DensityUtil.dip2px(mContext, 4) * mScale) / 2, (width + DensityUtil.dip2px(mContext, 4) * mScale) / 2, width / 2, mTextPaint);
		canvas.drawCircle((width + DensityUtil.dip2px(mContext, 4) * mScale) / 2, (width + DensityUtil.dip2px(mContext, 4) * mScale) / 2, textSize1 * timeScale
				* 2 / 33, mTextPaint1);
		drawHand(canvas, mTextPaint, (int) (width + DensityUtil.dip2px(mContext, 4) * mScale), mTimeStickerInfo.textColor);

	}

	private void getNewBitMap2(String text2) {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStrokeWidth(4);
		mTextPaint.setStyle(Style.STROKE);
		mTextPaint.setColor(mTimeStickerInfo.textColor);
		// mTextPaint.setTextSize(textSize1*mScale);
		mTextPaint.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		mTextPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mTimeStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize2 * mScale);
		mTextPaint1.setShadowLayer(5, 0, 0, mTimeStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mTimeStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}

		int width1 = (int) (mTextPaint1.measureText(text2));
		int width = (int) (width1 * 5 / 2);
		int height = (int) (mTextPaint1.measureText("2"));
		mBitmap = Bitmap.createBitmap(width, width, Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, width, height);
		Rect targetRect1 = new Rect(0, (int) (width - textSize2 * mScale), width, width);
		Rect targetRect2 = new Rect(0, 0, width, width);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint1.getFontMetricsInt();
		int baseline = targetRect2.top + (targetRect2.bottom - targetRect2.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		canvas.drawText("12", targetRect.centerX() - height, height + DensityUtil.dip2px(mContext, 3) * mScale, mTextPaint1);
		canvas.drawCircle(width / 2, width / 2, width / 2 - textSize2 * mScale, mTextPaint);
		canvas.drawCircle(width / 2, width / 2, width / 3 - textSize2 * mScale + DensityUtil.dip2px(mContext, 5) * mScale, mTextPaint);
		canvas.drawText("06", targetRect1.centerX() - height, width - DensityUtil.dip2px(mContext, 1) * mScale, mTextPaint1);
		canvas.drawText(text2, targetRect2.centerX() - width1 / 2, baseline, mTextPaint1);
		drawHand1(canvas, mTextPaint1, width, mTimeStickerInfo.textColor, height);

	}

	public void drawHand1(Canvas canvas, Paint paint, float local, int color, int height) {
		float x = local;
		float y = x;
		int hour;
		int minute;
		int second;

		Calendar calendar = Calendar.getInstance();
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		second = calendar.get(Calendar.SECOND);

		float h = ((hour + (float) minute / 60) / 12) * 360;
		float m = ((minute + (float) second / 60) / 60) * 360;
		float s = ((float) second / 60) * 360;

		Bitmap mBitmap1 = Bitmap.createBitmap((int) ((x / 20 + DensityUtil.dip2px(mContext, 2) * mScale)), (int) ((x / 20 + DensityUtil.dip2px(mContext, 2)
				* mScale)), Config.ARGB_8888);
		Bitmap mBitmap2 = Bitmap.createBitmap((int) ((x / 20 + DensityUtil.dip2px(mContext, 2) * mScale)), (int) ((x / 20 + DensityUtil.dip2px(mContext, 2)
				* mScale)), Config.ARGB_8888);
		Canvas canvas1 = new Canvas(mBitmap1);
		canvas1.drawCircle((x / 20 + DensityUtil.dip2px(mContext, 2) * mScale) / 2, (x / 20 + DensityUtil.dip2px(mContext, 2) * mScale) / 2,
				(x / 20 + DensityUtil.dip2px(mContext, 2) * mScale) / 2, paint);
		Canvas canvas2 = new Canvas(mBitmap2);
		canvas2.drawCircle((x / 20 + DensityUtil.dip2px(mContext, 2) * mScale) / 2, (x / 20 + DensityUtil.dip2px(mContext, 2) * mScale) / 2,
				(x / 20 + DensityUtil.dip2px(mContext, 2) * mScale) / 2, paint);

		// 时针
		canvas.save(); // 线锁定画布
		canvas.rotate(h, x / 2, y / 2); // 旋转画布
		canvas.drawBitmap(mBitmap1, x / 2, y / 3 - textSize2 * mScale - DensityUtil.dip2px(mContext, 1) * mScale, paint);
		canvas.restore();

		// 分针
		canvas.save();
		canvas.rotate(m, x / 2, y / 2); // 旋转画布
		canvas.drawBitmap(mBitmap2, x / 2, textSize2 * mScale - (x / 20 + DensityUtil.dip2px(mContext, 2) * mScale) / 2, paint);
		canvas.restore();

	}

	public void drawHand(Canvas canvas, Paint paint, int local, int color) {
		int x = local;
		int y = x;
		int hour;
		int minute;
		int second;

		Calendar calendar = Calendar.getInstance();
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		second = calendar.get(Calendar.SECOND);

		float h = ((hour + (float) minute / 60) / 12) * 360;
		float m = ((minute + (float) second / 60) / 60) * 360;
		float s = ((float) second / 60) * 360;

		// 时针
		canvas.save(); // 线锁定画布
		canvas.rotate(h, x / 2, y / 2); // 旋转画布
		Path path1 = new Path();
		path1.moveTo(x / 2, y / 2 + DensityUtil.dip2px(mContext, 10) * mScale); // 开始的基点
		path1.lineTo(x / 2, y / 4); // 最后的基点
		canvas.drawPath(path1, paint);
		canvas.restore();

		// 分针
		canvas.save();
		canvas.rotate(m, x / 2, y / 2); // 旋转画布
		Path path2 = new Path();
		path2.moveTo(x / 2, y / 2 + DensityUtil.dip2px(mContext, 10) * mScale); // 开始的基点
		path2.lineTo(x / 2, y / 6); // 最后的基点
		canvas.drawPath(path2, paint);
		canvas.restore();

		// // 时针
		// Bitmap mBitmap1 = Bitmap.createBitmap(x/2/2, 4, Config.ARGB_8888);
		// Bitmap mBitmap2 = Bitmap.createBitmap(x/2/5*4, 4, Config.ARGB_8888);
		// Canvas canvas1=new Canvas(mBitmap1);
		// canvas1.drawColor(color);
		// Canvas canvas2=new Canvas(mBitmap2);
		// canvas2.drawColor(color);
		//
		// canvas.save(); // 线锁定画布
		// canvas.rotate(h, x / 2, y / 2); // 旋转画布
		// canvas.drawBitmap(mBitmap1, x/2, y/2, paint);
		// canvas.restore();
		//
		// // 分针
		// canvas.save();
		// canvas.rotate(m, x / 2, y / 2); // 旋转画布
		// canvas.drawBitmap(mBitmap2, x/2, y/2, paint);
		// canvas.restore();

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
	public void change(String fontPath, String fontUrl, float size, int color, int shadowColor, int alpha) {
		if (color == shadowColor) {
			shadowColor = 0x00000000;
		}
		if (TextUtils.isEmpty(fontPath)) {
			typeface = null;
		} else {
			if (!fontPath.equals(mTimeStickerInfo.font)) {
				mTimeStickerInfo.font = fontPath;
				typeface = Typeface.createFromFile(mTimeStickerInfo.font);
			}
		}
		this.mScale = size;
		mTimeStickerInfo.alpha = alpha;
		mTimeStickerInfo.textColor = color;
		mTimeStickerInfo.font = fontPath;
		mTimeStickerInfo.shadowColor = shadowColor;
		mTimeStickerInfo.fontUrl = fontUrl;
		switch (timeStyle) {
		case 0:
			getNewBitMap1(text1, text2);
			break;
		case 1:
			getNewBitMap(text1, text2);
			break;
		case 2:
			getNewBitMap1(text1, text2);
			break;
		case 3:
			getNewBitMap(text1);
			break;
		case 4:
			getNewBitMap1(text1);
			break;
		case 5:
			getNewBitMap2(text1, text2);
			break;
		case 6:
			getNewBitMap();
			break;
		case 7:
			text2 = DateTime.getMonthAndDay();
			getNewBitMap2(text2);
			break;
		case 8:
			getNewBitMap3(text1);
			break;
		case 9:
			getNewBitMap4(text1);
			break;
		case 10:
			getNewBitMap5(text1);
			break;
		case 11:
			getNewBitMap6(text1);
			break;

		default:
			break;
		}
		transformDraw();
	}

}
