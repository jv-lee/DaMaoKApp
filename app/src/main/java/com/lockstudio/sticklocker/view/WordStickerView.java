package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
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
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.WordStickerInfo;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.PluginSettingUtils;
import com.lockstudio.sticklocker.util.PluginSettingUtils.OnPluginSettingChange;
import com.lockstudio.sticklocker.util.PluginTextEditUtils;
import com.lockstudio.sticklocker.util.PluginTextEditUtils.OnTextChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.opda.android.activity.R;

/**
 * 文字插件
 * 
 * @author 庄宏岩
 * 
 */
public class WordStickerView extends View implements OnPluginSettingChange {
	public static float MAX_SCALE = 5.0f;
	public static final float MIN_SCALE = 0.4f;

	public static final int LEFT_TOP = 0;
	public static final int RIGHT_TOP = 1;
	public static final int RIGHT_BOTTOM = 2;
	public static final int LEFT_BOTTOM = 3;
	private Bitmap mBitmap;

	private PointF mCenterPoint = new PointF();
	private int mViewWidth, mViewHeight;

	private float mDegree = 0;
	private float mScale = 1.0f;
	private Matrix matrix = new Matrix();
	private int mViewPaddingLeft;
	private int mViewPaddingTop;

	/* 上下左右四个点 */
	private Point mLTPoint;
	private Point mRTPoint;
	private Point mRBPoint;
	private Point mLBPoint;

	/* 操作按钮的点 */
	private Point mRotatePoint = new Point();
	private Point mDeletePoint = new Point();
	private Point mGravityPoint = new Point();
	private Point mEditPoint = new Point();

	private Drawable mRotateDrawable, mDeleteDrawable, mGravityHDrawable, mGravityVDrawable, mEditDrawable;
	private int mDrawableWidth, mDrawableHeight;

	private Path mFramePath = new Path();
	private Paint mFramePaint;
	private TextPaint mTextPaint;

	public static final int STATUS_INIT = 0;
	public static final int STATUS_DRAG = 1;
	public static final int STATUS_ROTATE_ZOOM = 2;
	public static final int STATUS_DELETE = 3;
	public static final int STATUS_GRAVITY = 4;
	public static final int STATUS_EDIT = 5;
	private int mStatus = STATUS_INIT;
	private int framePadding = 8;
	private int frameColor = Color.GRAY;
	private int frameWidth = DensityUtil.dip2px(getContext(), 1.5f);

	private boolean isEditable = true;
	private boolean isVisiable = true;

	private PointF mPreMovePointF = new PointF();
	private PointF mCurMovePointF = new PointF();

	private int offsetX;
	private int offsetY;

	private WordStickerInfo mWordStickerInfo;
	private OnRemoveStickerViewListener mOnRemoveViewListener;
	private OnUpdateViewListener mOnUpdateViewListener;
	private OnFocuseChangeListener mOnFocuseChangeListener;
	private LayoutParams mContainerLayoutParams;
	private String text;
	private int textSize;

	private LinearLayout mController_container_layout;
	private View controllerView;
	private View wordEditView;
	private Context mContext;
	private PluginSettingUtils pluginSettingUtils;
	private float lastX = 0;
	private float lastY = 0;
	private int gravityCount = 0;
	private Typeface typeface;

	public WordStickerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext = context;
	}

	public WordStickerView(Context context) {
		this(context, null);
		this.mContext = context;
	}

	public WordStickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	private void init() {
		mFramePaint = new Paint();
		mFramePaint.setAntiAlias(true);
		mFramePaint.setColor(frameColor);
		mFramePaint.setStrokeWidth(frameWidth);
		mFramePaint.setStyle(Style.STROKE);
		PathEffect effects = new DashPathEffect(new float[] { 15, 15, 15, 15 }, 1);
		mFramePaint.setPathEffect(effects);

		if (mRotateDrawable == null) {
			mRotateDrawable = getContext().getResources().getDrawable(R.drawable.diy_rotate);
		}
		if (mDeleteDrawable == null) {
			mDeleteDrawable = getContext().getResources().getDrawable(R.drawable.diy_delete);
		}

		if (mGravityHDrawable == null) {
			mGravityHDrawable = getContext().getResources().getDrawable(R.drawable.diy_gravity_h);
		}
		if (mGravityVDrawable == null) {
			mGravityVDrawable = getContext().getResources().getDrawable(R.drawable.diy_gravity_v);
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

			mWordStickerInfo.x = mViewPaddingLeft + mDrawableWidth / 2 + framePadding;
			mWordStickerInfo.y = mViewPaddingTop + mDrawableHeight / 2 + framePadding;
			mWordStickerInfo.text = text;

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
			mFramePath.reset();
			mFramePath.moveTo(mLTPoint.x, mLTPoint.y);
			mFramePath.lineTo(mRTPoint.x, mRTPoint.y);
			mFramePath.lineTo(mRBPoint.x, mRBPoint.y);
			mFramePath.lineTo(mLBPoint.x, mLBPoint.y);
			mFramePath.lineTo(mLTPoint.x, mLTPoint.y);
			canvas.drawPath(mFramePath, mFramePaint);

			mRotateDrawable.setBounds(mRotatePoint.x - mDrawableWidth / 2, mRotatePoint.y - mDrawableHeight / 2, mRotatePoint.x + mDrawableWidth / 2,
					mRotatePoint.y + mDrawableHeight / 2);
			mRotateDrawable.draw(canvas);

			mDeleteDrawable.setBounds(mDeletePoint.x - mDrawableWidth / 2, mDeletePoint.y - mDrawableHeight / 2, mDeletePoint.x + mDrawableWidth / 2,
					mDeletePoint.y + mDrawableHeight / 2);
			mDeleteDrawable.draw(canvas);

			if (mWordStickerInfo.gravity == 0) {
				mGravityHDrawable.setBounds(mGravityPoint.x - mDrawableWidth / 2, mGravityPoint.y - mDrawableHeight / 2, mGravityPoint.x + mDrawableWidth / 2,
						mGravityPoint.y + mDrawableHeight / 2);
				mGravityHDrawable.draw(canvas);
			} else {
				mGravityVDrawable.setBounds(mGravityPoint.x - mDrawableWidth / 2, mGravityPoint.y - mDrawableHeight / 2, mGravityPoint.x + mDrawableWidth / 2,
						mGravityPoint.y + mDrawableHeight / 2);
				mGravityVDrawable.draw(canvas);
			}

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
		mWordStickerInfo.angle = (int) mDegree;
		mWordStickerInfo.textSize = (int) (textSize * mScale);

		// if (pluginSettingUtils != null) {
		// pluginSettingUtils.setScale(MAX_SCALE, mScale);
		// }
		invalidate();
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (!isEditable) {
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = event.getRawX();
			lastY = event.getRawY();
			mPreMovePointF.set(event.getX() + mViewPaddingLeft, event.getY() + mViewPaddingTop);

			mStatus = JudgeStatus(event.getX(), event.getY());

			break;
		case MotionEvent.ACTION_UP:

			if (isVisiable && mStatus == STATUS_DELETE && mStatus == JudgeStatus(event.getX(), event.getY())) {
				mOnRemoveViewListener.removeView(mWordStickerInfo, this);
				isEditable = false;
				mStatus = STATUS_INIT;
				return true;
			}
			if (isVisiable && mStatus == STATUS_GRAVITY && mStatus == JudgeStatus(event.getX(), event.getY())) {
				gravityCount++;
				if (gravityCount % 2 == 1) {
					getTextVerticalBitmap();
					transformDraw();
				} else {
					getTextBitmap();
					transformDraw();
				}
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
				// float scale = 1f;
				//
				// int halfBitmapWidth = (int) (mBitmap.getWidth() / mScale /
				// 2);
				// int halfBitmapHeight = (int) (mBitmap.getHeight() / mScale /
				// 2);
				//
				// // 图片某个点到图片中心的距离
				// float bitmapToCenterDistance = FloatMath.sqrt(halfBitmapWidth
				// * halfBitmapWidth + halfBitmapHeight * halfBitmapHeight);
				//
				// // 移动的点到图片中心的距离
				// float moveToCenterDistance = distance4PointF(mCenterPoint,
				// mCurMovePointF);
				//
				// // 计算缩放比例
				// scale = moveToCenterDistance / bitmapToCenterDistance;
				//
				// // 缩放比例的界限判断
				// if (scale <= MIN_SCALE) {
				// scale = MIN_SCALE;
				// } else if (scale >= MAX_SCALE) {
				// scale = MAX_SCALE;
				// }

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
				// mScale = scale;

				if (gravityCount % 2 == 1) {
					getTextVerticalBitmap();
					transformDraw();
				} else {
					getTextBitmap();
					transformDraw();
				}
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
		mGravityPoint = LocationToPoint(LEFT_BOTTOM);
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
		PointF gravityPointF = new PointF(mGravityPoint);
		PointF exitPointF = new PointF(mEditPoint);
		// 点击的点到控制旋转，缩放点的距离
		float distanceToRotate = distance4PointF(touchPoint, rotatePointF);
		float distanceToDelete = distance4PointF(touchPoint, deletePointF);
		float distanceToGravity = distance4PointF(touchPoint, gravityPointF);
		float distanceToEdit = distance4PointF(touchPoint, exitPointF);

		// 如果两者之间的距离小于 控制图标的宽度，高度的最小值，则认为点中了控制图标
		if (distanceToRotate < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_ROTATE_ZOOM;
		}
		if (distanceToDelete < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_DELETE;
		}
		if (distanceToGravity < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_GRAVITY;
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

	public void setWordStickerInfo(WordStickerInfo wordStickerInfo) {
		this.mWordStickerInfo = wordStickerInfo;
		textSize = (int) getResources().getDimension(R.dimen.defaultStickerTextSize);
		gravityCount = mWordStickerInfo.gravity;

		mScale = mWordStickerInfo.textSize * 1.0f / textSize;
		if (mScale >= MAX_SCALE) {
			mScale = MAX_SCALE;
		}
		if (!TextUtils.isEmpty(mWordStickerInfo.font)&&new File(mWordStickerInfo.font).exists()) {
			typeface = Typeface.createFromFile(mWordStickerInfo.font);
		}
		if (!TextUtils.isEmpty(mWordStickerInfo.text)) {
			text = mWordStickerInfo.text;

			if (mWordStickerInfo.gravity == 0) {
				getTextBitmap();
			} else {
				getTextVerticalBitmap();
			}

			Matrix matrix = new Matrix();
			matrix.postRotate(mWordStickerInfo.angle, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);

			Bitmap b = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

			mCenterPoint.x = mWordStickerInfo.x + b.getWidth() / 2.0f;
			mCenterPoint.y = mWordStickerInfo.y + b.getHeight() / 2.0f;
			mDegree = mWordStickerInfo.angle;
			init();
			if (isVisiable) {
				showControllerView();
			}
		} else {
			if (isVisiable) {
				showEditView(false);
			}
		}
	}

	private void showControllerView() {
		if (controllerView == null) {
			pluginSettingUtils = new PluginSettingUtils(getContext());
			pluginSettingUtils.setOnPluginSettingChange(this);
			pluginSettingUtils.initSelectData(mWordStickerInfo.font, mWordStickerInfo.textColor, mWordStickerInfo.shadowColor);
			pluginSettingUtils.setAlpha(mWordStickerInfo.alpha);
			pluginSettingUtils.setScale(MAX_SCALE, mScale);
			controllerView = pluginSettingUtils.getView();
		}
		if (controllerView != null && controllerView.getParent() == null) {
			mController_container_layout.addView(controllerView);
		}
	}

	private void showEditView(final boolean edit) {

		if (wordEditView == null) {
			PluginTextEditUtils pluginTextEditUtils = new PluginTextEditUtils(mContext, text);
			wordEditView = pluginTextEditUtils.getView();
			pluginTextEditUtils.setOnTextChangeListener(new OnTextChangeListener() {

				@Override
				public void textChange(String word) {
					mController_container_layout.removeAllViews();
					text = word;
					mWordStickerInfo.text = text;
					if (gravityCount % 2 == 1) {
						getTextVerticalBitmap();
					} else {
						getTextBitmap();
					}
					if (edit) {
						init();
					} else {
						mCenterPoint.x = mWordStickerInfo.x + mBitmap.getWidth() / 2;
						mCenterPoint.y = mWordStickerInfo.y + mBitmap.getHeight() / 2;
						mDegree = mWordStickerInfo.angle;
						init();
						showControllerView();
					}
				}
			});
		}
		mController_container_layout.removeAllViews();
		mController_container_layout.addView(wordEditView);

		// EditTextDialog editTextDialog = new EditTextDialog(mContext);
		// editTextDialog.setHintText("点击输入文字");
		// editTextDialog.setOnDismissedListener(new OnDismissedListener() {
		//
		// @Override
		// public void OnDialogDismissed() {
		// if (TextUtils.isEmpty(text)) {
		// mOnRemoveViewListener.removeView(mWordStickerInfo,
		// WordStickerView.this);
		// } else {
		// if (gravityCount % 2 == 1) {
		// getTextVerticalBitmap();
		// } else {
		// getTextBitmap();
		// }
		// if (edit) {
		// init();
		// } else {
		// mCenterPoint.x = mWordStickerInfo.x + mBitmap.getWidth() / 2;
		// mCenterPoint.y = mWordStickerInfo.y + mBitmap.getHeight() / 2;
		// mDegree = mWordStickerInfo.angle;
		// init();
		// showControllerView();
		// }
		// }
		//
		// }
		// });
		// editTextDialog.setEditTextOkClickListener(new
		// EditTextDialog.OnEditTextOkClickListener() {
		// @Override
		// public void OnEditTextOkClick(String string) {
		// text = string;
		// }
		// });
		// if (TextUtils.isEmpty(text)) {
		// editTextDialog.show();
		// } else {
		// editTextDialog.show(text);
		// }

	}

	private void getTextBitmap() {
		mWordStickerInfo.gravity = 0;
//		mTextPaint = new TextPaint();
//		mTextPaint.setAntiAlias(true);
//		mTextPaint.setColor(mWordStickerInfo.textColor);
//		mTextPaint.setTextSize(textSize * mScale);
//		mTextPaint.setShadowLayer(5, 0, 0, mWordStickerInfo.shadowColor);
//		mTextPaint.setAlpha(mWordStickerInfo.alpha);
//
//		if (typeface != null) {
//			mTextPaint.setTypeface(typeface);
//		}
//
//		int width = (int) (mTextPaint.measureText(text));
//		int height = (int) (mTextPaint.measureText("测"));
//		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//		Rect targetRect = new Rect(0, 0, width, height);
//		Canvas canvas = new Canvas(mBitmap);
//		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
//		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
//		mTextPaint.setTextAlign(Paint.Align.CENTER);
//		canvas.drawText(text, targetRect.centerX(), baseline, mTextPaint);
		
		mTextPaint = new TextPaint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mWordStickerInfo.textColor);
		mTextPaint.setTextSize(textSize * mScale);
		mTextPaint.setShadowLayer(5, 0, 0, mWordStickerInfo.shadowColor);

		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}

		int width = LockApplication.getInstance().getConfig().getScreenWidth() - DensityUtil.dip2px(getContext(), 20) * 2;
		int height = (int) (mTextPaint.measureText("测"));
		int textWidth = (int) mTextPaint.measureText(text);

		StaticLayout oneStaticLayout = new StaticLayout("测", mTextPaint, height, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
		int oneWidth = oneStaticLayout.getWidth();

		int len = width / oneWidth;
		int count = 0;
		if (text.length() % len == 0) {
			count = text.length() / len;
		} else {
			count = text.length() / len + 1;
		}
		
		if (count > 1) {
			int bitmapWidth = 0;
			int bitmapHeight = 0;
			ArrayList<StaticLayout> staticLayouts = new ArrayList<StaticLayout>();
			for (int i = 0; i < count; i++) {
				String text1 = text.substring(len * i, Math.min(len * (i + 1), text.length()));
				StaticLayout sl = new StaticLayout(text1, mTextPaint, (int) mTextPaint.measureText(text1), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
				bitmapWidth = Math.max(bitmapWidth, sl.getWidth());
				bitmapHeight = Math.max(bitmapHeight, sl.getHeight());
				staticLayouts.add(sl);
			}

			mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight * count, Config.ARGB_8888);
			Canvas canvas = new Canvas(mBitmap);
			for (int i = 0; i < staticLayouts.size(); i++) {
				StaticLayout staticLayout = staticLayouts.get(i);
				if (i == 0) {
					canvas.translate(0, 0);
				} else {
					if (i == staticLayouts.size() - 1) {
						if (mWordStickerInfo.gravity == 0) {
							canvas.translate(0, bitmapHeight);
						} else if (mWordStickerInfo.gravity == 1) {
							canvas.translate((bitmapWidth - staticLayout.getWidth()) / 2, bitmapHeight);
						} else {
							canvas.translate(bitmapWidth - staticLayout.getWidth(), bitmapHeight);
						}
					} else {
						canvas.translate(0, bitmapHeight);
					}
				}
				staticLayout.draw(canvas);
			}

		} else {
			StaticLayout staticLayout = new StaticLayout(text, mTextPaint, Math.min(width, textWidth), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
			width = staticLayout.getWidth();
			height = staticLayout.getHeight();
			mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(mBitmap);
			staticLayout.draw(canvas);
		}
	}

	private void getTextVerticalBitmap() {
		mWordStickerInfo.gravity = 1;
		mTextPaint = new TextPaint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mWordStickerInfo.textColor);
		mTextPaint.setTextSize(textSize * mScale);
		mTextPaint.setShadowLayer(5, 0, 0, mWordStickerInfo.shadowColor);
		mTextPaint.setAlpha(mWordStickerInfo.alpha);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}

		int width = (int) (mTextPaint.measureText(text));
		int height = (int) (mTextPaint.measureText("测"));
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		StaticLayout sl = new StaticLayout(text, mTextPaint, height, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
		width = sl.getHeight();
		mBitmap = Bitmap.createBitmap(height, width, Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);
		canvas.translate(textSize * mScale / 2, 0);
		sl.draw(canvas);
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
	public void change(String fontPath,String fontUrl, float size, int color, int shadowColor, int alpha) {
		if (color == shadowColor) {
			shadowColor = 0x00000000;
		}
		if (TextUtils.isEmpty(fontPath)) {
			typeface = null;
		} else {
			if (!fontPath.equals(mWordStickerInfo.font)) {
				mWordStickerInfo.font = fontPath;
				typeface = Typeface.createFromFile(mWordStickerInfo.font);
			}
		}
		this.mScale = size;
		mWordStickerInfo.alpha = alpha;
		mWordStickerInfo.textColor = color;
		mWordStickerInfo.font = fontPath;
		mWordStickerInfo.shadowColor = shadowColor;
		mWordStickerInfo.fontUrl = fontUrl;
		if (gravityCount % 2 == 1) {
			getTextVerticalBitmap();
			transformDraw();
		} else {
			getTextBitmap();
			transformDraw();
		}
	}

}
