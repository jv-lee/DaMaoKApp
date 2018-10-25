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
import com.lockstudio.sticklocker.model.TimerStickerInfo;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.PluginSettingUtils;
import com.lockstudio.sticklocker.util.PluginSettingUtils.OnPluginSettingChange;
import com.lockstudio.sticklocker.view.TimerSettingDialog.OnEditTextOkClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.opda.android.activity.R;

/**
 * 单手对图片进行缩放，旋转，平移操作，详情请查看
 * 
 */
public class TimerStickerView extends View implements OnPluginSettingChange {
	/**
	 * 图片的最大缩放比例
	 */
	public float MAX_SCALE = 5.0f;

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
	public static final String TAG = "TimeStickerView";

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
	public static final int STATUS_EDIT = 5;

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

	private TimerStickerInfo mTimerStickerInfo;
	private OnRemoveStickerViewListener mOnRemoveViewListener;
	private OnUpdateViewListener mOnUpdateViewListener;
	private OnFocuseChangeListener mOnFocuseChangeListener;
	private LayoutParams mContainerLayoutParams;
	private String text;
	private String text1;
	private String text2;
	private String text_time;
	private int textSize1;
	private int textSize2;
	private int textSize3;

	private LinearLayout mController_container_layout;
	private View controllerView;
	private Context mContext;
	private PluginSettingUtils pluginSettingUtils;
	private Thread mThread;
	private boolean flag = false;
	private boolean timerLoop = false;
	private boolean dounFlag = true;
	private boolean timerFlag;

	private static final int MSG_START_TIMER_LOOP = 300;
	private static final int UPDATE_MY_TV = 1;
	Message message = null;
	private float lastX = 0;
	private float lastY = 0;
	private Typeface typeface;

	public TimerStickerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext = context;
	}

	public TimerStickerView(Context context) {
		this(context, null);
		this.mContext = context;
	}

	public TimerStickerView(Context context, AttributeSet attrs, int defStyle) {
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
			mEditDrawable = getContext().getResources().getDrawable(R.drawable.diy_edit);
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

			mTimerStickerInfo.x = mViewPaddingLeft + mDrawableWidth / 2 + framePadding;
			mTimerStickerInfo.y = mViewPaddingTop + mDrawableHeight / 2 + framePadding;

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
		int bitmapWidth = (int) (mBitmap.getWidth() * mScale);
		int bitmapHeight = (int) (mBitmap.getHeight() * mScale);
		computeRect(-framePadding, -framePadding, bitmapWidth + framePadding, bitmapHeight + framePadding, mDegree);

		// 设置缩放比例
		matrix.setScale(mScale, mScale);
		// 绕着图片中心进行旋转
		if (mDegree > 5 || mDegree < -5) {
			matrix.postRotate(mDegree % 360, bitmapWidth / 2, bitmapHeight / 2);
		} else {
			matrix.postRotate(0, bitmapWidth / 2, bitmapHeight / 2);
		}
		// 设置画该图片的起始点
		matrix.postTranslate(offsetX + mDrawableWidth / 2, offsetY + mDrawableHeight / 2);

		mTimerStickerInfo.angle = (int) mDegree;
		mTimerStickerInfo.textSize1 = (int) (textSize1 * mScale);
		mTimerStickerInfo.textSize2 = (int) (textSize2 * mScale);
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
				mOnRemoveViewListener.removeView(mTimerStickerInfo, this);
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

	public void setTimerStickerInfo(TimerStickerInfo timerStickerInfo) {

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		this.mContext.registerReceiver(mScreenBroadcastReceiver, filter);

		this.mTimerStickerInfo = timerStickerInfo;
		textSize1 = mTimerStickerInfo.textSize1;
		textSize2 = mTimerStickerInfo.textSize2;
		textSize3 = textSize2 / 5 * 3;
		text2 = mTimerStickerInfo.text_time;
		text1 = mTimerStickerInfo.text_title;
		timerFlag = mTimerStickerInfo.timerFlag;

		int defaultTextSize = DensityUtil.dip2px(mContext, 35);
		int maxTextSize = (int) (defaultTextSize * MAX_SCALE);

		MAX_SCALE = maxTextSize / textSize1;

		if (!TextUtils.isEmpty(mTimerStickerInfo.font)) {
			typeface = Typeface.createFromFile(mTimerStickerInfo.font);
		}

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		if (Long.valueOf(str) > Long.valueOf(text2)) {
			timerFlag = true;
			dounFlag = true;
		} else {
			timerFlag = false;
		}
		text_time = getDistanceTime(mContext, text2, str);
		getNewBitMap(text1, text_time);

		startTimer();

		Matrix matrix = new Matrix();
		matrix.postRotate(mTimerStickerInfo.angle, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
		Bitmap b = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
		mCenterPoint.x = mTimerStickerInfo.x + b.getWidth() / 2.0f;
		mCenterPoint.y = mTimerStickerInfo.y + b.getHeight() / 2.0f;
		mDegree = timerStickerInfo.angle;
		init();
		if (isVisiable) {
			showControllerView();
		}
	}

	private void showEditView(final boolean edit) {
		final TimerSettingDialog timerSettingDialog = new TimerSettingDialog(mContext);
		timerSettingDialog.setEditTextOkClickListener(new OnEditTextOkClickListener() {

			@Override
			public void OnEditTextOkClick(String string, String date) {
				timerSettingDialog.dismiss();

				text1 = string;
				text2 = date;
				mTimerStickerInfo.text_time = text2;
				mTimerStickerInfo.text_title = text1;
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String currentTime = formatter.format(curDate);
				if (Long.valueOf(currentTime) > Long.valueOf(date)) {
					timerFlag = true;
					dounFlag = true;
				} else {
					timerFlag = false;
				}
				mTimerStickerInfo.timerFlag = timerFlag;
				if (!TextUtils.isEmpty(text1) && !TextUtils.isEmpty(text2)) {
					text_time = getDistanceTime(mContext, text2, currentTime);
					getNewBitMap(text1, text_time);
					startTimer();
					if (edit) {
						init();
					} else {
						mCenterPoint.x = mTimerStickerInfo.x + mBitmap.getWidth() / 2;
						mCenterPoint.y = mTimerStickerInfo.y + mBitmap.getHeight() / 2;
						mDegree = mTimerStickerInfo.angle;
						init();
						showControllerView();
					}
				} else {
					mOnRemoveViewListener.removeView(mTimerStickerInfo, TimerStickerView.this);
				}

			}
		});

		timerSettingDialog.show(text1, text2);

	}

	/**
	 * 两个时间相差距离
	 * 
	 * @param str1
	 *            时间参数 1 格式：yyyyMMddHHmm
	 * @return String 返回值为：xx天xx小时xx分xx秒
	 */
	public static String getDistanceTime(Context context, String str1, String str2) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {

			one = df.parse(str1.toString());
			two = df.parse(str2.toString());
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

		} catch (ParseException e) {
		}
		if (day == 0 && hour == 0 && min == 0 && sec == 0)
			return "";
		else if (day == 0 && hour == 0 && min == 0)
			return hour + "|" + context.getResources().getString(R.string.hour) + "|" + min + "|" + context.getResources().getString(R.string.minute) + "|"
					+ sec + "|" + context.getResources().getString(R.string.second);
		else if (day == 0 && hour == 0)
			return hour + "|" + context.getResources().getString(R.string.hour) + "|" + min + "|" + context.getResources().getString(R.string.minute) + "|"
					+ sec + "|" + context.getResources().getString(R.string.second);
		else if (day == 0)
			return hour + "|" + context.getResources().getString(R.string.hour) + "|" + min + "|" + context.getResources().getString(R.string.minute) + "|"
					+ sec + "|" + context.getResources().getString(R.string.second);
		return day + "|" + context.getResources().getString(R.string.day) + "|" + hour + "|" + context.getResources().getString(R.string.hour) + "|" + min
				+ "|" + context.getResources().getString(R.string.minute) + "|" + sec + "|" + context.getResources().getString(R.string.second);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START_TIMER_LOOP:
				handler.removeMessages(MSG_START_TIMER_LOOP);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String currentTime = formatter.format(curDate);

				text_time = getDistanceTime(mContext, text2, currentTime);
				if (!timerFlag) {
					if (Long.valueOf(currentTime) > Long.valueOf(text2)) {
						text_time = "";
					}
				}

				if (text_time == null || "".equals(text_time)) {
					if (!timerFlag) {
						dounFlag = false;
						stopTimer();
						text_time = "0|时|0|分|0|秒";
					}
				}
				getNewBitMap(text1, text_time);
				transformDraw();// 强制刷新，调整布局
				if (timerLoop) {
					handler.sendEmptyMessageDelayed(MSG_START_TIMER_LOOP, 1000);
				}
				break;
			}
		}
	};

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
		if (visibility == 8) {
			stopTimer();
			if (mScreenBroadcastReceiver != null) {
				this.mContext.unregisterReceiver(mScreenBroadcastReceiver);
				mScreenBroadcastReceiver = null;
			}
		}
		if (visibility == 0) {
			startTimer();
		}
	}

	private void startTimer() {
		// 实时更新时间
		if (timerLoop)
			return;
		if (dounFlag) {
			timerLoop = true;
			handler.sendEmptyMessage(MSG_START_TIMER_LOOP);
		}
	}

	private void stopTimer() {
		timerLoop = false;
	}

	// 显示文字操作栏
	private void showControllerView() {
		if (controllerView == null) {
			pluginSettingUtils = new PluginSettingUtils(getContext());
			pluginSettingUtils.setOnPluginSettingChange(this);
			pluginSettingUtils.initSelectData(mTimerStickerInfo.font, mTimerStickerInfo.textColor, mTimerStickerInfo.shadowColor);
			pluginSettingUtils.setAlpha(mTimerStickerInfo.alpha);
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
		mTextPaint.setColor(mTimerStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1);
		mTextPaint.setShadowLayer(5, 0, 0, mTimerStickerInfo.shadowColor);
		mTextPaint.setAlpha(mTimerStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}
		mTextPaint1 = new Paint();
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setColor(mTimerStickerInfo.textColor);
		mTextPaint1.setTextSize(textSize2);
		mTextPaint1.setShadowLayer(5, 0, 0, mTimerStickerInfo.shadowColor);
		mTextPaint1.setAlpha(mTimerStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint1.setTypeface(typeface);
		}
		mTextPaint2 = new Paint();
		mTextPaint2.setAntiAlias(true);
		mTextPaint2.setColor(mTimerStickerInfo.textColor);
		mTextPaint2.setTextSize(textSize3);
		mTextPaint2.setShadowLayer(2, 0, 0, mTimerStickerInfo.shadowColor);
		if (typeface != null) {
			mTextPaint2.setTypeface(typeface);
		}

		int width = (int) (mTextPaint.measureText(text1));
		int height = (int) (mTextPaint.measureText("测"));
		int height1 = (int) (mTextPaint1.measureText("测"));
		int width1 = 0;

		String str0 = null;
		String str1 = null;
		String str2 = null;
		String str3 = null;
		String str4 = null;
		String str5 = null;
		String str6 = null;
		String str7 = null;
		String st1[] = text2.split("\\|");
		if (st1.length == 8) {
			str0 = st1[0];
			str1 = st1[1];
			str2 = st1[2];
			str3 = st1[3];
			str4 = st1[4];
			str5 = st1[5];
			str6 = st1[6];
			str7 = st1[7];
			if (Integer.valueOf(str0) < 10) {
				str0 = "0" + str0;
			}
			if (Integer.valueOf(str2) < 10) {
				str2 = "0" + str2;
			}
			if (Integer.valueOf(str4) < 10) {
				str4 = "0" + str4;
			}
			if (Integer.valueOf(str6) < 10) {
				str6 = "0" + str6;
			}
			width1 = (int) (mTextPaint1.measureText(str0) + mTextPaint2.measureText(str1) + mTextPaint1.measureText(str2) + mTextPaint2.measureText(str3)
					+ mTextPaint1.measureText(str4) + mTextPaint2.measureText(str5) + mTextPaint1.measureText(str6) + mTextPaint2.measureText(str7))
					+ DensityUtil.dip2px(mContext, 15);
		} else if (st1.length == 6) {
			str0 = st1[0];
			str1 = st1[1];
			str2 = st1[2];
			str3 = st1[3];
			str4 = st1[4];
			str5 = st1[5];
			if (Integer.valueOf(str0) < 10) {
				str0 = "0" + str0;
			}
			if (Integer.valueOf(str2) < 10) {
				str2 = "0" + str2;
			}
			if (Integer.valueOf(str4) < 10) {
				str4 = "0" + str4;
			}
			width1 = (int) (mTextPaint1.measureText(str0) + mTextPaint2.measureText(str1) + mTextPaint1.measureText(str2) + mTextPaint2.measureText(str3)
					+ mTextPaint1.measureText(str4) + mTextPaint2.measureText(str5))
					+ DensityUtil.dip2px(mContext, 10);
		}
		int width2 = width > width1 ? width : width1;
		mBitmap = Bitmap.createBitmap(width2, height + height1 + 10, Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, width2, height);
		Rect targetRect1 = new Rect(0, height, width2, height + height1);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		FontMetricsInt fontMetrics1 = mTextPaint1.getFontMetricsInt();
		int baseline1 = targetRect1.top + (targetRect1.bottom - targetRect1.top - fontMetrics1.bottom + fontMetrics1.top) / 2 - fontMetrics1.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint1.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(text1, targetRect.centerX(), baseline, mTextPaint);
		if (str0 != null && !"".equals(str0) && st1.length == 8 && Integer.valueOf(str0) > 100 && width < width1) {
			int str0Width = (int) (mTextPaint1.measureText(str0));
			int str1Width = (int) (mTextPaint2.measureText(str1));
			int str2Width = (int) (mTextPaint1.measureText("99"));
			int str3Width = (int) (mTextPaint2.measureText(str3));
			int str4Width = (int) (mTextPaint1.measureText("99"));
			int str5Width = (int) (mTextPaint2.measureText(str5));
			int str6Width = (int) (mTextPaint1.measureText("99"));
			int str7Width = (int) (mTextPaint2.measureText(str7));
			canvas.drawText(str0, DensityUtil.dip2px(mContext, 5) + DensityUtil.dip2px(mContext, 20), baseline1 + 10, mTextPaint1);
			canvas.drawText(str1, str0Width + DensityUtil.dip2px(mContext, 8), baseline1 + 10, mTextPaint2);
			canvas.drawText(str2, str0Width + str1Width + DensityUtil.dip2px(mContext, 20), baseline1 + 10, mTextPaint1);
			canvas.drawText(str3, str0Width + str1Width + str2Width + DensityUtil.dip2px(mContext, 8), baseline1 + 10, mTextPaint2);
			canvas.drawText(str4, str0Width + str1Width + str2Width + str3Width + DensityUtil.dip2px(mContext, 20), baseline1 + 10, mTextPaint1);
			canvas.drawText(str5, str0Width + str1Width + str2Width + str3Width + str4Width + DensityUtil.dip2px(mContext, 8), baseline1 + 10, mTextPaint2);
			canvas.drawText(str6, str0Width + str1Width + str2Width + str3Width + str4Width + str5Width + DensityUtil.dip2px(mContext, 20), baseline1 + 10,
					mTextPaint1);
			canvas.drawText(str7, str0Width + str1Width + str2Width + str3Width + str4Width + str5Width + str6Width + DensityUtil.dip2px(mContext, 8),
					baseline1 + 10, mTextPaint2);
		} else if (str0 != null && !"".equals(str0) && st1.length == 8) {
			int str0Width = (int) (mTextPaint1.measureText(str0));
			int str1Width = (int) (mTextPaint2.measureText(str1));
			int str2Width = (int) (mTextPaint1.measureText("99"));
			int str3Width = (int) (mTextPaint2.measureText(str3));
			int str4Width = (int) (mTextPaint1.measureText("99"));
			int str5Width = (int) (mTextPaint2.measureText(str5));
			int str6Width = (int) (mTextPaint1.measureText("99"));
			int str7Width = (int) (mTextPaint2.measureText(str7));
			canvas.drawText(str0, targetRect1.centerX() - (str0Width / 2 + str1Width + str2Width + str3Width), baseline1 + 10, mTextPaint1);
			canvas.drawText(str1, targetRect1.centerX() - (str1Width + str2Width + str3Width), baseline1 + 10, mTextPaint2);
			canvas.drawText(str2, targetRect1.centerX() - (str2Width / 2 + str3Width), baseline1 + 10, mTextPaint1);
			canvas.drawText(str3, targetRect1.centerX() - str3Width, baseline1 + 10, mTextPaint2);
			canvas.drawText(str4, targetRect1.centerX() + str3Width, baseline1 + 10, mTextPaint1);
			canvas.drawText(str5, targetRect1.centerX() + str3Width + str4Width / 2, baseline1 + 10, mTextPaint2);
			canvas.drawText(str6, targetRect1.centerX() + str3Width + str4Width + str5Width, baseline1 + 10, mTextPaint1);
			canvas.drawText(str7, targetRect1.centerX() + str3Width + str4Width + str5Width + str6Width / 2, baseline1 + 10, mTextPaint2);
		}
		if (str0 != null && !"".equals(str0) && st1.length == 6) {
			int str0Width = (int) (mTextPaint1.measureText(str0));
			int str1Width = (int) (mTextPaint2.measureText(str1));
			int str2Width = (int) (mTextPaint1.measureText(str2));
			int str3Width = (int) (mTextPaint2.measureText(str3));
			int str4Width = (int) (mTextPaint1.measureText(str4));
			int str5Width = (int) (mTextPaint2.measureText(str5));
			canvas.drawText(str0, targetRect1.centerX() - (str0Width + str1Width) - DensityUtil.dip2px(mContext, 7), baseline1 + 10, mTextPaint1);
			canvas.drawText(str1, targetRect1.centerX() - (str1Width + str2Width / 2) - DensityUtil.dip2px(mContext, 7), baseline1 + 10, mTextPaint2);
			canvas.drawText(str2, targetRect1.centerX() - DensityUtil.dip2px(mContext, 7), baseline1 + 10, mTextPaint1);
			canvas.drawText(str3, targetRect1.centerX() + str2Width / 2 - DensityUtil.dip2px(mContext, 7), baseline1 + 10, mTextPaint2);
			canvas.drawText(str4, targetRect1.centerX() + str2Width + str3Width - DensityUtil.dip2px(mContext, 7), baseline1 + 10, mTextPaint1);
			canvas.drawText(str5, targetRect1.centerX() + str2Width + str3Width + str4Width / 2 - DensityUtil.dip2px(mContext, 7), baseline1 + 10, mTextPaint2);
		}
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
			if (!fontPath.equals(mTimerStickerInfo.font)) {
				mTimerStickerInfo.font = fontPath;
				typeface = Typeface.createFromFile(mTimerStickerInfo.font);
			}
		}
		this.mScale = size;
		mTimerStickerInfo.alpha = alpha;
		mTimerStickerInfo.textColor = color;
		mTimerStickerInfo.font = fontPath;
		mTimerStickerInfo.shadowColor = shadowColor;
		mTimerStickerInfo.fontUrl = fontUrl;
		getNewBitMap(text1, text_time);
		transformDraw();
	}

}
