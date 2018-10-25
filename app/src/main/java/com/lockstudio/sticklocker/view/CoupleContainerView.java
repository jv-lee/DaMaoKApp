package com.lockstudio.sticklocker.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Tommy.VolleyUtil;
import com.lockstudio.sticklocker.Interface.OnFocuseChangeListener;
import com.lockstudio.sticklocker.Interface.OnRemoveLockerViewListener;
import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.activity.SelectImageActivity;
import com.lockstudio.sticklocker.model.CoupleLockerInfo;
import com.lockstudio.sticklocker.util.ChooseStickerUtils;
import com.lockstudio.sticklocker.util.ChooseStickerUtils.OnImageSelectorListener;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.MConstants;

import java.lang.reflect.Field;

import cn.opda.android.activity.R;

public class CoupleContainerView extends RelativeLayout implements OnImageSelectorListener {

	private Bitmap[] bitmaps = new Bitmap[2];
	private ImageView iv1;
	private ImageView iv2;
	private LayoutParams lp1;
	private LayoutParams lp2;
	private Context mContext;
	private OnUpdateViewListener mOnUpdateViewListener;
	private OnRemoveLockerViewListener mOnRemoveViewListener;
	private LayoutParams mContainerLayoutParams;
	private Bitmap mBitmapCircleDefault;
	private Bitmap mBitmapCircleGreen;
	private SharedPreferences sp;
	private int mPreviousx = 0;
	private int mPreviousy = 0;
	private int screenWidth;
	private int screenHeight;
	private int iCurrentx;
	private int iCurrenty;
	private int left;
	private int top;
	private int right;
	private int bottom;

	/**
	 * 是否处于可以缩放，平移状态
	 */
	private boolean isEditable = true;
	private boolean isVisiable = true;
	private boolean setImage;
	public static final int STATUS_INIT = 0;
	public static final int STATUS_DRAG = 1;
	public static final int STATUS_DELETE = 2;
	public static final int STATUS_CONTROLLER = 3;
	public static final int STATUS_LEFT_IV = 4;
	public static final int STATUS_RIGHT_IV = 5;
	private int mStatus = STATUS_INIT;
	private Path mFramePath = new Path();
	private Path mSelectPath = new Path();
	private Paint mSelectPaint;
	private Paint mFramePaint;
	private int frameColor = Color.GRAY;
	private int frameWidth = DensityUtil.dip2px(getContext(), 1.5f);
	private Drawable mDeleteDrawable/* , mControllerDrawable */;
	/* 操作按钮的点 */
	private Point mDeletePoint = new Point();
	private Point mControllerPoint = new Point();
	public static final int LEFT_TOP = 0;
	public static final int RIGHT_TOP = 1;
	public static final int RIGHT_BOTTOM = 2;
	public static final int LEFT_BOTTOM = 3;
	private int mDrawableWidth, mDrawableHeight;
	/* 上下左右四个点 */
	private Point mLTPoint;
	private Point mRTPoint;
	private Point mRBPoint;
	private Point mLBPoint;
	private CoupleLockerInfo mCoupleLockerInfo;
	private OnFocuseChangeListener mOnFocuseChangeListener;
	private View controllerView;
	private LinearLayout mController_container_layout;
	private int leftOrRight;

	public CoupleContainerView(Context context) {
		super(context);
		this.mContext = context;
	}

	public CoupleContainerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public CoupleContainerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	private void init() {
		setWillNotDraw(false);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels - DensityUtil.dip2px(mContext, 20);
		screenHeight = dm.heightPixels;
		/**** 获取状态栏高度 ****/
		int statusBarHeight = 0;
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
			statusBarHeight = 0;
		}

		mFramePaint = new Paint();
		mFramePaint.setAntiAlias(true);
		mFramePaint.setColor(frameColor);
		mFramePaint.setStrokeWidth(frameWidth);
		mFramePaint.setStyle(Style.STROKE);
		PathEffect effects = new DashPathEffect(new float[] { 15, 15, 15, 15 }, 1);
		mFramePaint.setPathEffect(effects);

		mSelectPaint = new Paint();
		mSelectPaint.setAntiAlias(true);
		mSelectPaint.setColor(Color.GRAY);
		mSelectPaint.setStrokeWidth(DensityUtil.dip2px(mContext, 1));
		mSelectPaint.setStyle(Style.STROKE);
		PathEffect effects2 = new DashPathEffect(new float[] { 8, 8, 8, 8 }, 1);
		mSelectPaint.setPathEffect(effects2);

		mLTPoint = new Point();
		mRTPoint = new Point();
		mRBPoint = new Point();
		mLBPoint = new Point();

		mDeletePoint = LocationToPoint(LEFT_TOP);
		mControllerPoint = LocationToPoint(LEFT_BOTTOM);

		if (mDeleteDrawable == null) {
			mDeleteDrawable = getContext().getResources().getDrawable(R.drawable.diy_delete);
		}
		// if (mControllerDrawable == null) {
		// mControllerDrawable =
		// getContext().getResources().getDrawable(R.drawable.diy_rotate);
		// }
		mDrawableWidth = mDeleteDrawable.getIntrinsicWidth();
		mDrawableHeight = mDeleteDrawable.getIntrinsicHeight();

		screenHeight = screenHeight - statusBarHeight;
		sp = mContext.getSharedPreferences("myprefence", Context.MODE_MULTI_PROCESS);
		int largen = sp.getInt("couple_largen", -1);

		iv1 = new ImageView(mContext);
		iv2 = new ImageView(mContext);
		updateImage();
		if (largen == -1) {
			lp1 = new LayoutParams(DensityUtil.dip2px(mContext, 60), DensityUtil.dip2px(mContext, 60));
			lp2 = new LayoutParams(DensityUtil.dip2px(mContext, 60), DensityUtil.dip2px(mContext, 60));
		} else {
			lp1 = new LayoutParams(largen, largen);
			lp2 = new LayoutParams(largen, largen);
		}
		lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
		lp1.setMargins(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 15), 0, 0);
		addView(iv1, lp1);

		lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 2);
		lp2.setMargins(0, DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 15), 0);
		addView(iv2, lp2);
//		if(setImage){
//			ChooseStickerUtils chooseStickerUtils = new ChooseStickerUtils(mContext, ChooseStickerUtils.FROM_LOCKER_LOVERS);
//			chooseStickerUtils.setOnImageSelectorListener(this);
//			controllerView = chooseStickerUtils.getView();
//		}

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

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 处于可编辑状态才画边框和控制图标
		if (isEditable && isVisiable) {
			if (leftOrRight == 1) {
				mSelectPath.reset();
				mSelectPath.moveTo(iv1.getLeft(), iv1.getTop());
				mSelectPath.lineTo(iv1.getRight(), iv1.getTop());
				mSelectPath.lineTo(iv1.getRight(), iv1.getBottom());
				mSelectPath.lineTo(iv1.getLeft(), iv1.getBottom());
				mSelectPath.lineTo(iv1.getLeft(), iv1.getTop());
				canvas.drawPath(mSelectPath, mSelectPaint);

			}
			if (leftOrRight == 2) {
				mSelectPath.reset();
				mSelectPath.moveTo(iv2.getLeft(), iv2.getTop());
				mSelectPath.lineTo(iv2.getRight(), iv2.getTop());
				mSelectPath.lineTo(iv2.getRight(), iv2.getBottom());
				mSelectPath.lineTo(iv2.getLeft(), iv2.getBottom());
				mSelectPath.lineTo(iv2.getLeft(), iv2.getTop());
				canvas.drawPath(mSelectPath, mSelectPaint);

			}
			mFramePath.reset();
			mLTPoint.x = DensityUtil.dip2px(mContext, 13);
			mLTPoint.y = DensityUtil.dip2px(mContext, 13);
			mRTPoint.x = getWidth() - DensityUtil.dip2px(mContext, 13);
			mRTPoint.y = DensityUtil.dip2px(mContext, 13);
			mRBPoint.x = getWidth() - DensityUtil.dip2px(mContext, 13);
			mRBPoint.y = getHeight() - DensityUtil.dip2px(mContext, 13);
			mLBPoint.x = DensityUtil.dip2px(mContext, 13);
			mLBPoint.y = getHeight() - DensityUtil.dip2px(mContext, 13);

			mFramePath.moveTo(mLTPoint.x, mLTPoint.y);
			mFramePath.lineTo(mRTPoint.x, mRTPoint.y);
			mFramePath.lineTo(mRBPoint.x, mRBPoint.y);
			mFramePath.lineTo(mLBPoint.x, mLBPoint.y);
			mFramePath.lineTo(mLTPoint.x, mLTPoint.y);
			canvas.drawPath(mFramePath, mFramePaint);

			mDeleteDrawable.setBounds(mDeletePoint.x - mDrawableWidth / 2, mDeletePoint.y - mDrawableHeight / 2, mDeletePoint.x + mDrawableWidth / 2,
					mDeletePoint.y + mDrawableHeight / 2);
			mDeleteDrawable.draw(canvas);

			// mControllerDrawable.setBounds(mControllerPoint.x - mDrawableWidth
			// / 2, mControllerPoint.y - mDrawableHeight / 2, mControllerPoint.x
			// + mDrawableWidth / 2, mControllerPoint.y + mDrawableHeight / 2);
			// mControllerDrawable.draw(canvas);
		}

	}

	private void updateImage() {
		mBitmapCircleDefault = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
		mBitmapCircleGreen = BitmapFactory.decodeResource(getResources(), R.drawable.boy);

		Bitmap bitmap = mCoupleLockerInfo.getBitmaps()[0];
		Bitmap bitmap2 = mCoupleLockerInfo.getBitmaps()[1];
		if (bitmap != null) {
			bitmaps[0] = bitmap;
		} else {
			bitmaps[0] = mBitmapCircleDefault;
		}
		if (bitmap2 != null) {
			bitmaps[1] = bitmap2;
		} else {
			bitmaps[1] = mBitmapCircleGreen;
		}
		iv1.setBackgroundDrawable(new BitmapDrawable(bitmaps[0]));
		iv2.setBackgroundDrawable(new BitmapDrawable(bitmaps[1]));
	}

	/**
	 * 两个点之间的距离
	 */
	private float distance4PointF(PointF pf1, PointF pf2) {
		float disX = pf2.x - pf1.x;
		float disY = pf2.y - pf1.y;
		return (float) Math.sqrt(disX * disX + disY * disY);
	}

	/**
	 * 根据点击的位置判断是否点中控制旋转，缩放的图片， 初略的计算
	 */
	private int JudgeStatus(float x, float y) {
		PointF touchPoint = new PointF(x, y);
		PointF deletePointF = new PointF(mDeletePoint);
		PointF controllerPointF = new PointF(mControllerPoint);
		PointF ivLeftPointF = new PointF(new Point((iv1.getRight() + iv1.getLeft()) / 2, (iv1.getBottom() + iv1.getTop()) / 2));
		PointF ivrightPointF = new PointF(new Point((iv2.getRight() + iv2.getLeft()) / 2, (iv2.getBottom() + iv2.getTop()) / 2));

		float distanceToDelete = distance4PointF(touchPoint, deletePointF);
		float distanceToController = distance4PointF(touchPoint, controllerPointF);
		float distanceToLeftIV = distance4PointF(touchPoint, ivLeftPointF);
		float distanceToRightIV = distance4PointF(touchPoint, ivrightPointF);
		if (distanceToDelete < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_DELETE;
		}
		if (distanceToController < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_CONTROLLER;
		}

		if (distanceToLeftIV < Math.min(iv1.getWidth() / 2, iv1.getHeight() / 2)) {
			leftOrRight = 1;
			invalidate();
			showControllerView();
			return STATUS_LEFT_IV;
		}
		if (distanceToRightIV < Math.min(iv2.getWidth() / 2, iv2.getHeight() / 2)) {
			leftOrRight = 2;
			invalidate();
			showControllerView();
			return STATUS_RIGHT_IV;
		}
		return STATUS_DRAG;

	}

	private void showControllerView() {
//		if (controllerView != null && controllerView.getParent() == null) {
//			LockApplication.getInstance().getConfig().setFrom_id(ChooseStickerUtils.FROM_LOCKER_LOVERS);
//			mController_container_layout.removeAllViews();
//			mController_container_layout.addView(controllerView);
//		}
		Intent intent = new Intent(mContext, SelectImageActivity.class);
		intent.putExtra("from", ChooseStickerUtils.FROM_LOCKER_LOVERS);
		((Activity)mContext).startActivityForResult(intent, MConstants.REQUEST_CODE_STICKER_EDIT);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int iAction = event.getAction();
		iCurrentx = (int) event.getX();
		iCurrenty = (int) event.getY();

		switch (iAction) {
		case MotionEvent.ACTION_DOWN:
			mPreviousx = iCurrentx;
			mPreviousy = iCurrenty;
			mStatus = JudgeStatus(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
			if (isVisiable && mStatus == STATUS_DELETE && mStatus == JudgeStatus(event.getX(), event.getY())) {
				mOnRemoveViewListener.removeView(mCoupleLockerInfo, this);
				isEditable = false;
				return true;
			}

			if (isVisiable && mStatus == STATUS_CONTROLLER && mStatus == JudgeStatus(event.getX(), event.getY())) {
				// showControllerView();
				return true;
			}

			mStatus = STATUS_INIT;

			if (setImage && !isVisiable) {
				mOnFocuseChangeListener.focuseChange(this);
			}

			break;
		case MotionEvent.ACTION_MOVE:
			int iDeltx = iCurrentx - mPreviousx;
			int iDelty = iCurrenty - mPreviousy;
			left = getLeft() + iDeltx;
			top = getTop() + iDelty;
			right = getRight() + iDeltx;
			bottom = getBottom() + iDelty;

			if (iDeltx != 0 || iDelty != 0) {

				if (left < DensityUtil.dip2px(mContext, 10)) {
					left = DensityUtil.dip2px(mContext, 10);
					right = left + getWidth();
				}

				if (right > screenWidth) {
					right = screenWidth;
					left = right - getWidth();
				}

				if (top < 0) {
					top = 0;
					bottom = top + getHeight();
				}

				if (bottom > screenHeight) {
					bottom = screenHeight;
					top = bottom - getHeight();
				}

				mContainerLayoutParams.topMargin = top;
				mCoupleLockerInfo.setY(top);
				mOnUpdateViewListener.updateView(CoupleContainerView.this, mContainerLayoutParams);
				layout(left, top, right, bottom);

			}

			mPreviousx = iCurrentx - iDeltx;
			mPreviousy = iCurrenty - iDelty;
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return true;
	}

	public void setCoupleLockerInfo(CoupleLockerInfo mCoupleLockerInfo) {
		this.mCoupleLockerInfo = mCoupleLockerInfo;
		init();
//		if (isVisiable) {
//			showControllerView();
//		}
	}

	public void setImage(boolean b) {
		setImage = b;
	}

	/**
	 * 设置是否显示绘制操作按钮
	 */
	public void setVisible(boolean isVisiable) {
		this.isVisiable = isVisiable;
		leftOrRight = -1;
		invalidate();
	}

	public static interface OnCoupleListener {
		void unlockSuccsed();
	}

	public void setOnUpdateViewListener(OnUpdateViewListener onUpdateViewListener) {
		this.mOnUpdateViewListener = onUpdateViewListener;
	}

	public void setContainerLayoutParams(LayoutParams containerLayoutParams) {
		this.mContainerLayoutParams = containerLayoutParams;
	}

	public void setOnRemoveViewListener(OnRemoveLockerViewListener onRemoveViewListener) {
		this.mOnRemoveViewListener = onRemoveViewListener;
	}

	public void setControllerContainerLayout(LinearLayout controller_container_layout) {
		this.mController_container_layout = controller_container_layout;
	}

	public void setOnFocuseChangeListener(OnFocuseChangeListener onFocuseChangeListener) {
		this.mOnFocuseChangeListener = onFocuseChangeListener;
	}

	@Override
	public void selectImage(String imageUrl) {
		Bitmap bitmap = VolleyUtil.instance().getBitmapForUrl(imageUrl);
		if (leftOrRight == 1) {
			if (bitmap != null) {
				mCoupleLockerInfo.getBitmaps()[0] = bitmap;
			}
		}
		if (leftOrRight == 2) {
			if (bitmap != null) {
				mCoupleLockerInfo.getBitmaps()[1] = bitmap;
			}
		}
		updateImage();
	}

	public void selectImage(Bitmap bitmap) {
		if (leftOrRight == 1) {
			if (bitmap != null) {
				mCoupleLockerInfo.getBitmaps()[0] = bitmap;
			}
		}
		if (leftOrRight == 2) {
			if (bitmap != null) {
				mCoupleLockerInfo.getBitmaps()[1] = bitmap;
			}
		}
		updateImage();

	}

}
