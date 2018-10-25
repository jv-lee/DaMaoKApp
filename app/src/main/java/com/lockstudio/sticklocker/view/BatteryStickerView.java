package com.lockstudio.sticklocker.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.lockstudio.sticklocker.Interface.OnFocuseChangeListener;
import com.lockstudio.sticklocker.Interface.OnRemoveStickerViewListener;
import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.model.BatteryStickerInfo;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.PluginPhoneSMSUtils;
import com.lockstudio.sticklocker.util.PluginPhoneSMSUtils.OnPluginSettingChange;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.opda.android.activity.R;

/**
 * 单手对图片进行缩放，旋转，平移操作，详情请查看
 * 
 */
public class BatteryStickerView extends View implements OnPluginSettingChange {
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
	private float textSize;

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

	private BatteryStickerInfo mBatteryStickerInfo;
	private OnRemoveStickerViewListener mOnRemoveViewListener;
	private OnUpdateViewListener mOnUpdateViewListener;
	private OnFocuseChangeListener mOnFocuseChangeListener;
	private LayoutParams mContainerLayoutParams;
	private int imageRes;

	private LinearLayout mController_container_layout;
	private View controllerView;
	private Context mContext;
	private PluginPhoneSMSUtils pluginSettingUtils;
	private boolean changing = false;

	private float lastX = 0;
	private float lastY = 0;

	public BatteryStickerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext = context;
	}

	public BatteryStickerView(Context context) {
		this(context, null);
		this.mContext = context;
	}

	public BatteryStickerView(Context context, AttributeSet attrs, int defStyle) {
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

			mBatteryStickerInfo.x = mViewPaddingLeft + mDrawableWidth / 2 + framePadding;
			mBatteryStickerInfo.y = mViewPaddingTop + mDrawableHeight / 2 + framePadding;

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

		mBatteryStickerInfo.angle = (int) mDegree;
		mBatteryStickerInfo.scale = mScale;
		mBatteryStickerInfo.textSize = (int) (textSize * mScale);
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
				mOnRemoveViewListener.removeView(mBatteryStickerInfo, this);
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
				float scale = 1f;

				int halfBitmapWidth = (int) (mBitmap.getWidth() / mScale / 2);
				int halfBitmapHeight = (int) (mBitmap.getHeight() / mScale / 2);

				// 图片某个点到图片中心的距离
				float bitmapToCenterDistance = (float) Math.sqrt(halfBitmapWidth * halfBitmapWidth + halfBitmapHeight * halfBitmapHeight);

				// 移动的点到图片中心的距离
				float moveToCenterDistance = distance4PointF(mCenterPoint, mCurMovePointF);

				// 计算缩放比例
				scale = moveToCenterDistance / bitmapToCenterDistance;

				// 缩放比例的界限判断
				if (scale <= MIN_SCALE) {
					scale = MIN_SCALE;
				} else if (scale >= MAX_SCALE) {
					scale = MAX_SCALE;
				}
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
				mScale = scale;
				switch (mBatteryStyle) {
				case 0:
					getNewBitMap();
					break;
				case 1:
					getNewBitMap1();
					break;
				case 2:
					getNewBitMap2();
					break;
				case 3:
					getNewBitMap3();
					break;
				case 4:
					getNewBitMap4();
					break;
				case 5:
					getNewBitMap5();
					break;

				default:
					break;
				}
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

	/**
	 * 判断是否离开view
	 */
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if (visibility == 8) {
			if (mBatInfoReceiver != null) {
				this.mContext.unregisterReceiver(mBatInfoReceiver);
				mBatInfoReceiver = null;
			}
		}
		if (visibility == 0) {
			this.mContext.registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		}
	}

	/**
	 * 监听电池变化
	 */
	private int level;
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				level = intent.getIntExtra("level", 0);
				int status = intent.getIntExtra("status", 0);

				switch (status) {
				case BatteryManager.BATTERY_STATUS_UNKNOWN:
					break;
				case BatteryManager.BATTERY_STATUS_CHARGING:
					changing = true;
					break;
				case BatteryManager.BATTERY_STATUS_DISCHARGING:
					changing = false;
					break;
				case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
					changing = false;
					break;
				case BatteryManager.BATTERY_STATUS_FULL:
					changing = false;
					break;
				}

				switch (mBatteryStyle) {
				case 0:
					getNewBitMap();
					break;
				case 1:
					getNewBitMap1();
					break;
				case 2:
					getNewBitMap2();
					break;
				case 3:
					getNewBitMap3();
					break;
				case 4:
					getNewBitMap4();
					break;
				case 5:
					getNewBitMap5();
					break;

				default:
					break;
				}
				transformDraw();
			}
		}
	};

	// 设置系统时间并实时更新
	private int mBatteryStyle;

	public void setBatteryStickerInfo(BatteryStickerInfo batteryStickerInfo) {

		this.mContext.registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		this.mBatteryStickerInfo = batteryStickerInfo;
		textSize = DensityUtil.dip2px(mContext, 10);
		if (mBatteryStickerInfo.textSize == 0) {
			mBatteryStickerInfo.textSize = DensityUtil.dip2px(mContext, 10);
		}

		if (mBatteryStickerInfo.batteryStyle == 0) {
			mScale = mBatteryStickerInfo.scale;
		} else {
			mScale = mBatteryStickerInfo.textSize * 1.0f / textSize;
		}
		if (mScale >= MAX_SCALE) {
			mScale = MAX_SCALE;
		}
		// imageRes = R.drawable.plugin_battery1;

		mBatteryStyle = mBatteryStickerInfo.batteryStyle;
		switch (mBatteryStyle) {
		case 0:
			getNewBitMap();
			break;
		case 1:
			getNewBitMap1();
			break;
		case 2:
			getNewBitMap2();
			break;
		case 3:
			getNewBitMap3();
			break;
		case 4:
			getNewBitMap4();
			break;
		case 5:
			getNewBitMap5();
			break;

		default:
			break;
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(mBatteryStickerInfo.angle, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
		Bitmap b = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
		mCenterPoint.x = mBatteryStickerInfo.x + b.getWidth() / 2.0f;
		mCenterPoint.y = mBatteryStickerInfo.y + b.getHeight() / 2.0f;
		mDegree = batteryStickerInfo.angle;
		init();
		if (isVisiable) {
			showControllerView();
		}
	}

	// 显示文字操作栏
	private void showControllerView() {
		if (controllerView == null) {
			pluginSettingUtils = new PluginPhoneSMSUtils(getContext());
			pluginSettingUtils.setOnPluginSettingChange(this);
			pluginSettingUtils.initSelectData(mBatteryStickerInfo.textColor);
			controllerView = pluginSettingUtils.getView();
		}
		if (controllerView != null && controllerView.getParent() == null) {
			mController_container_layout.addView(controllerView);// ??????
		}
	}

	private void getNewBitMap() {
		switch (level / 10) {
		case 0:
			imageRes = R.drawable.plugin_battery_no;
			break;
		case 1:
			imageRes = R.drawable.plugin_battery1;
			break;
		case 2:
			imageRes = R.drawable.plugin_battery2;
			break;
		case 3:
			imageRes = R.drawable.plugin_battery3;
			break;
		case 4:
			imageRes = R.drawable.plugin_battery4;
			break;
		case 5:
			imageRes = R.drawable.plugin_battery5;
			break;
		case 6:
			imageRes = R.drawable.plugin_battery6;
			break;
		case 7:
			imageRes = R.drawable.plugin_battery7;
			break;
		case 8:
			imageRes = R.drawable.plugin_battery8;
			break;
		case 9:
			imageRes = R.drawable.plugin_battery9;
			break;
		case 10:
			imageRes = R.drawable.plugin_battery10;
			break;

		default:
			break;
		}

		int bitmapRes;
		if (changing) {
			bitmapRes = R.drawable.plugin_battery_ing;
		} else {
			bitmapRes = imageRes;
		}

		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, bitmapRes, mBatteryStickerInfo.textColor);
		Bitmap bp = null;
		if (db != null) {
			BitmapDrawable bd = (BitmapDrawable) db;
			if (bd != null) {
				bp = bd.getBitmap();
			}
		}
		if (bp == null) {
			bp = DrawableUtils.getBitmap(mContext, bitmapRes);
		}
		bp = DrawableUtils.scaleTo(bp, (int) (mBatteryStickerInfo.width * mScale), (int) (mBatteryStickerInfo.height * mScale));
		int width = bp.getWidth();
		int height = bp.getHeight();

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mBatteryStickerInfo.textColor);
		mTextPaint.setTextSize(textSize * mScale);

		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);
		canvas.drawBitmap(bp, 0, 0, mTextPaint);
	}

	private void getNewBitMap1() {
		int bitmapRes;
		String levelstr = " ";
		if (changing) {
			bitmapRes = R.drawable.plugin_battery_style_ic1_ing;
		} else {
			levelstr = level + "%";
			bitmapRes = R.drawable.plugin_battery_style_1;
		}

		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, bitmapRes, mBatteryStickerInfo.textColor);
		Bitmap bp = null;
		if (db != null) {
			BitmapDrawable bd = (BitmapDrawable) db;
			if (bd != null) {
				bp = bd.getBitmap();
			}
		}
		if (bp == null) {
			bp = DrawableUtils.getBitmap(mContext, bitmapRes);
		}
		bp = DrawableUtils.scaleTo(bp, (int) (mBatteryStickerInfo.width * mScale), (int) (mBatteryStickerInfo.height * mScale));
		int width = bp.getWidth();
		int height = bp.getHeight();

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mBatteryStickerInfo.textColor);
		mTextPaint.setTextSize(textSize * mScale);

		int width1 = (int) (mTextPaint.measureText(levelstr));

		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, width, height);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(levelstr, targetRect.centerX() - width1 / 3, baseline, mTextPaint);
		canvas.drawBitmap(bp, 0, 0, mTextPaint);
	}

	private void getNewBitMap2() {
		switch (level / 10) {
		case 0:
		case 1:
			imageRes = R.drawable.plugin_battery_style_21;
			break;
		case 2:
			imageRes = R.drawable.plugin_battery_style_22;
			break;
		case 3:
			imageRes = R.drawable.plugin_battery_style_23;
			break;
		case 4:
			imageRes = R.drawable.plugin_battery_style_24;
			break;
		case 5:
			imageRes = R.drawable.plugin_battery_style_25;
			break;
		case 6:
			imageRes = R.drawable.plugin_battery_style_26;
			break;
		case 7:
			imageRes = R.drawable.plugin_battery_style_27;
			break;
		case 8:
			imageRes = R.drawable.plugin_battery_style_28;
			break;
		case 9:
			imageRes = R.drawable.plugin_battery_style_29;
			break;
		case 10:
			imageRes = R.drawable.plugin_battery_style_210;
			break;

		default:
			break;
		}

		int bitmapRes;
		String levelstr = " ";
		if (changing) {
			bitmapRes = R.drawable.plugin_battery_style_ic2_ing;
		} else {
			bitmapRes = imageRes;
			levelstr = level + "%";
		}

		Bitmap bp = BitmapFactory.decodeResource(getResources(), bitmapRes);
		if (bp == null) {
			bp = DrawableUtils.getBitmap(mContext, bitmapRes);
		}
		bp = DrawableUtils.scaleTo(bp, (int) (bp.getWidth() * mScale), (int) (bp.getHeight() * mScale));
		int width = bp.getWidth();
		int height = bp.getHeight();

		Bitmap batterybp = BitmapFactory.decodeResource(getResources(), R.drawable.plugin_battery_style_20);
		if (batterybp == null) {
			batterybp = DrawableUtils.getBitmap(mContext, R.drawable.plugin_battery_style_20);
		}
		batterybp = DrawableUtils.scaleTo(batterybp, (int) (bp.getWidth()), (int) (bp.getHeight()));

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mBatteryStickerInfo.textColor);
		mTextPaint.setTextSize(textSize * 1.3f * mScale);

		int width1 = (int) (mTextPaint.measureText(levelstr));
		int height1 = (int) (mTextPaint.measureText("1"));

		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, width, height);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(levelstr, targetRect.centerX() + width1 * 0.8f, baseline + height1 * 1.3f, mTextPaint);
		canvas.drawBitmap(batterybp, 0, 0, mTextPaint);
		canvas.drawBitmap(bp, 0, 0, mTextPaint);
	}

	private void getNewBitMap3() {
		switch (level / 10) {
		case 0:
		case 1:
			imageRes = R.drawable.plugin_battery_style_31;
			break;
		case 2:
			imageRes = R.drawable.plugin_battery_style_32;
			break;
		case 3:
			imageRes = R.drawable.plugin_battery_style_33;
			break;
		case 4:
			imageRes = R.drawable.plugin_battery_style_34;
			break;
		case 5:
			imageRes = R.drawable.plugin_battery_style_35;
			break;
		case 6:
			imageRes = R.drawable.plugin_battery_style_36;
			break;
		case 7:
			imageRes = R.drawable.plugin_battery_style_37;
			break;
		case 8:
			imageRes = R.drawable.plugin_battery_style_38;
			break;
		case 9:
			imageRes = R.drawable.plugin_battery_style_39;
			break;
		case 10:
			imageRes = R.drawable.plugin_battery_style_310;
			break;

		default:
			break;
		}

		int bitmapRes;
		String levelstr = "";
		if (changing) {
			bitmapRes = R.drawable.plugin_battery_style_ic3_ing;
		} else {
			levelstr = level + "%";
			bitmapRes = imageRes;
		}

		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, bitmapRes, mBatteryStickerInfo.textColor);
		Bitmap bp = null;
		if (db != null) {
			BitmapDrawable bd = (BitmapDrawable) db;
			if (bd != null) {
				bp = bd.getBitmap();
			}
		}
		if (bp == null) {
			bp = DrawableUtils.getBitmap(mContext, bitmapRes);
		}
		bp = DrawableUtils.scaleTo(bp, (int) (mBatteryStickerInfo.width * mScale), (int) (mBatteryStickerInfo.height * mScale));
		int width = bp.getWidth();
		int height = bp.getHeight();

		Drawable db1 = DrawableUtils.getDrawableCustomColor(mContext, R.drawable.plugin_battery_style_30, mBatteryStickerInfo.textColor);
		Bitmap batterybp = null;
		if (db1 != null) {
			BitmapDrawable batterybp1 = (BitmapDrawable) db1;
			if (batterybp1 != null) {
				batterybp = batterybp1.getBitmap();
			}
		}
		if (batterybp == null) {
			batterybp = DrawableUtils.getBitmap(mContext, R.drawable.plugin_battery_style_30);
		}
		batterybp = DrawableUtils.scaleTo(batterybp, (int) (mBatteryStickerInfo.width * mScale), (int) (mBatteryStickerInfo.height * mScale));
		// Bitmap batterybp=BitmapFactory.decodeResource(getResources(),
		// R.drawable.plugin_battery_style_30);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mBatteryStickerInfo.textColor);
		mTextPaint.setTextSize(textSize * mScale);

		int width1 = (int) (mTextPaint.measureText(levelstr));
		int height1 = (int) (mTextPaint.measureText("1"));

		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, width, height);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(levelstr, targetRect.centerX(), baseline - height1 * 3, mTextPaint);
		canvas.drawBitmap(bp, 0, 0, mTextPaint);
		canvas.drawBitmap(batterybp, 0, 0, mTextPaint);
	}

	private void getNewBitMap4() {
		switch (level / 10) {
		case 0:
		case 1:
			imageRes = R.drawable.plugin_battery_style_41;
			break;
		case 2:
			imageRes = R.drawable.plugin_battery_style_42;
			break;
		case 3:
			imageRes = R.drawable.plugin_battery_style_43;
			break;
		case 4:
			imageRes = R.drawable.plugin_battery_style_44;
			break;
		case 5:
			imageRes = R.drawable.plugin_battery_style_45;
			break;
		case 6:
			imageRes = R.drawable.plugin_battery_style_46;
			break;
		case 7:
			imageRes = R.drawable.plugin_battery_style_47;
			break;
		case 8:
			imageRes = R.drawable.plugin_battery_style_48;
			break;
		case 9:
			imageRes = R.drawable.plugin_battery_style_49;
			break;
		case 10:
			imageRes = R.drawable.plugin_battery_style_410;
			break;

		default:
			break;
		}

		int bitmapRes;
		String levelstr = "";
		if (changing) {
			bitmapRes = R.drawable.plugin_battery_style_ic4_ing;
			levelstr = "充电中";
		} else {
			levelstr = level + "%";
			bitmapRes = imageRes;
		}

		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, bitmapRes, mBatteryStickerInfo.textColor);
		Bitmap bp = null;
		if (db != null) {
			BitmapDrawable bd = (BitmapDrawable) db;
			if (bd != null) {
				bp = bd.getBitmap();
			}
		}
		if (bp == null) {
			bp = DrawableUtils.getBitmap(mContext, bitmapRes);
		}
		bp = DrawableUtils.scaleTo(bp, (int) (mBatteryStickerInfo.width * mScale), (int) (mBatteryStickerInfo.height * mScale));
		int width = bp.getWidth();
		int height = bp.getHeight();

		Drawable db1 = DrawableUtils.getDrawableCustomColor(mContext, R.drawable.plugin_battery_style_40, mBatteryStickerInfo.textColor);
		Bitmap batterybp = null;
		if (db1 != null) {
			BitmapDrawable batterybp1 = (BitmapDrawable) db1;
			if (batterybp1 != null) {
				batterybp = batterybp1.getBitmap();
			}
		}
		if (batterybp == null) {
			batterybp = DrawableUtils.getBitmap(mContext, R.drawable.plugin_battery_style_40);
		}
		batterybp = DrawableUtils.scaleTo(batterybp, (int) (mBatteryStickerInfo.width * mScale), (int) (mBatteryStickerInfo.height * mScale));
		// Bitmap batterybp=BitmapFactory.decodeResource(getResources(),
		// R.drawable.plugin_battery_style_30);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mBatteryStickerInfo.textColor);
		mTextPaint.setTextSize(textSize * mScale);
		mTextPaint.setStrokeWidth(2);

		int width1 = (int) (mTextPaint.measureText(levelstr));
		int height1 = (int) (mTextPaint.measureText("1"));

		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, width, height);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(levelstr, targetRect.centerX(), baseline + height1 * 3.2f, mTextPaint);
		canvas.drawBitmap(bp, 0, 0, mTextPaint);
		canvas.drawBitmap(batterybp, 0, 0, mTextPaint);
	}

	private void getNewBitMap5() {
		int bitmapRes;
		String levelstr = " ";
		if (changing) {
			bitmapRes = R.drawable.plugin_battery_style_ic5_ing;
		} else {
			levelstr = level + "%";
			bitmapRes = R.drawable.plugin_battery_style_5;
		}

		Drawable db = DrawableUtils.getDrawableCustomColor(mContext, bitmapRes, mBatteryStickerInfo.textColor);
		Bitmap bp = null;
		if (db != null) {
			BitmapDrawable bd = (BitmapDrawable) db;
			if (bd != null) {
				bp = bd.getBitmap();
			}
		}
		if (bp == null) {
			bp = DrawableUtils.getBitmap(mContext, bitmapRes);
		}
		bp = DrawableUtils.scaleTo(bp, (int) (mBatteryStickerInfo.width * mScale), (int) (mBatteryStickerInfo.height * mScale));
		int width = bp.getWidth();
		int height = bp.getHeight();

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mBatteryStickerInfo.textColor);
		mTextPaint.setTextSize(textSize * mScale);

		int width1 = (int) (mTextPaint.measureText(levelstr));

		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Rect targetRect = new Rect(0, 0, width, height);
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(levelstr, targetRect.centerX() + width1 / 2, baseline, mTextPaint);
		canvas.drawBitmap(bp, 0, 0, mTextPaint);
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
	public void change(int color) {
		mBatteryStickerInfo.textColor = color;
		switch (mBatteryStyle) {
		case 0:
			getNewBitMap();
			break;
		case 1:
			getNewBitMap1();
			break;
		case 2:
			getNewBitMap2();
			break;
		case 3:
			getNewBitMap3();
			break;
		case 4:
			getNewBitMap4();
			break;
		case 5:
			getNewBitMap5();
			break;

		default:
			break;
		}
		transformDraw();
	}

}
