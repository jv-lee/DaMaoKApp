package com.lockstudio.sticklocker.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
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
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.lockstudio.sticklocker.Interface.BindShortcutListener;
import com.lockstudio.sticklocker.Interface.OnFocuseChangeListener;
import com.lockstudio.sticklocker.Interface.OnRemoveLockerViewListener;
import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.model.AppLockerInfo;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.ThemeUtils;
import com.lockstudio.sticklocker.view.SelectAppPopup.OnImageSelectorListener;

import cn.opda.android.activity.R;

/**
 * 圆盘式的view
 */
public class AppContainerView extends View implements OnImageSelectorListener{
	
	private Activity act;
	private Context mContext;
	
	private Paint mPaint = new Paint();
	
	// 图片列表列表
	private BigStone[] mStones;
	// 中心点stone
	private BigStone centerStones;
	// 数目
	private static final int STONE_COUNT = 4;
	// 圆心坐标
	private int mPointX = 0, mPointY = 0;
	// 半径
	private int mRadius = 0;
	// 每两个点间隔的角度
	private int mDegreeDelta;
	// 
	private Bitmap lockscre_pressed_bit ;
	private Bitmap lockscreen_normal_bit;
	private Bitmap select_bg_bit;

	private int[] normal_img = { R.drawable.ic_lockscreen_message_normal, R.drawable.ic_lockscreen_unlock_normal, R.drawable.ic_lockscreen_phone_normal, R.drawable.ic_tab_theme_normal };
//	private int[] select_img = { R.drawable.ic_lockscreen_message_activated, R.drawable.ic_lockscreen_unlock_activated, R.drawable.ic_lockscreen_phone_activated, R.drawable.ic_tab_theme_selected };
	private Bitmap[] normal_img_bitmap = new Bitmap[STONE_COUNT];
//	private Bitmap[] select_img_bitmap = new Bitmap[STONE_COUNT];
	
	private boolean isVisiable = true;
	private Path mFramePath = new Path();
	private Path mSelectPath = new Path();
	private Paint mSelectPaint;
	private Paint mFramePaint;
	private int frameColor = Color.GRAY;
	private int frameWidth = DensityUtil.dip2px(getContext(), 1.5f);
	/* 上下左右四个点 */
	private Point mLTPoint;
	private Point mRTPoint;
	private Point mRBPoint;
	private Point mLBPoint;
	public static final int LEFT_TOP = 0;
	public static final int RIGHT_TOP = 1;
	public static final int RIGHT_BOTTOM = 2;
	public static final int LEFT_BOTTOM = 3;
	private int sWidth,sHeight;
	private Drawable mDeleteDrawable/* , mControllerDrawable */;
	private int mDrawableWidth, mDrawableHeight;
	private Point mDeletePoint = new Point();
	public static final int STATUS_INIT = 0;
	public static final int STATUS_DRAG = 1;
	public static final int STATUS_DELETE = 2;
	public static final int STATUS_TOP_IV = 3;
	public static final int STATUS_BOTTOM_IV = 4;
	public static final int STATUS_LEFT_IV = 5;
	public static final int STATUS_RIGHT_IV = 6;
	private int mStatus = STATUS_INIT;
	private OnUpdateViewListener mOnUpdateViewListener;
	private OnRemoveLockerViewListener mOnRemoveViewListener;
	private android.widget.RelativeLayout.LayoutParams mContainerLayoutParams;
	private OnFocuseChangeListener mOnFocuseChangeListener;
	private LinearLayout mController_container_layout;
	private AppLockerInfo mAppLockerInfo;
	private int leftOrRight;
	private Bitmap[] bitmaps = new Bitmap[4];
	
	public AppContainerView(Context context, int px, int py, int radius) {
		super(context);
		this.mContext = context;
		this.sWidth=px;
		this.sHeight=py;
//		init(px, py, radius);
	}

	public AppContainerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public AppContainerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public AppContainerView(Context context) {
		super(context);
		this.mContext = context;
	}

	public void init(int px, int py, int radius) {
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(0);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.STROKE);
		
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
		if (mDeleteDrawable == null) {
			mDeleteDrawable = getContext().getResources().getDrawable(R.drawable.diy_delete);
		}
		// if (mControllerDrawable == null) {
		// mControllerDrawable =
		// getContext().getResources().getDrawable(R.drawable.diy_rotate);
		// }
		mDrawableWidth = mDeleteDrawable.getIntrinsicWidth();
		mDrawableHeight = mDeleteDrawable.getIntrinsicHeight();
		
		lockscre_pressed_bit = BitmapFactory.decodeResource(getResources(), R.drawable.lockscre_pressed);
		lockscreen_normal_bit = BitmapFactory.decodeResource(getResources(), R.drawable.lockscreen_normal);
		select_bg_bit = BitmapFactory.decodeResource(getResources(), R.drawable.template_checkbox_normal);
		for (int index = 0; index < STONE_COUNT; index++) {
			if(mAppLockerInfo.getBitmaps()[index]!=null){
				normal_img_bitmap[index] = mAppLockerInfo.getBitmaps()[index];
			}else{
				normal_img_bitmap[index] = BitmapFactory.decodeResource(getResources(), normal_img[index]);
			}
//			select_img_bitmap[index] = BitmapFactory.decodeResource(getResources(), select_img[index]);
		}
		
		mPointX = px / 2;
		mPointY = py ;
		mRadius = radius;

		setupStones();
		computeCoordinates();
	}

	/**
	 * 初始化每个点
	 */
	private void setupStones() {
		mStones = new BigStone[STONE_COUNT];
		BigStone stone;
		int angle = 0;
		mDegreeDelta = 360 / STONE_COUNT;

		centerStones = new BigStone();
		centerStones.angle = angle;
		centerStones.x = mPointX;
		centerStones.y = mPointY;

		for (int index = 0; index < STONE_COUNT; index++) {
			stone = new BigStone();
			stone.angle = angle;
			angle += mDegreeDelta;

			mStones[index] = stone;
		}
	}


	/**
	 * 计算每个点的坐标
	 */
	private void computeCoordinates() {
		BigStone stone;
		for (int index = 0; index < STONE_COUNT; index++) {
			stone = mStones[index];
			stone.x = mPointX + (float) ((mRadius + select_bg_bit.getWidth()/2) * Math.cos(stone.angle * Math.PI / 180));
			stone.y = mPointY + (float) ((mRadius + select_bg_bit.getHeight()/2) * Math.sin(stone.angle * Math.PI / 180));
			stone.bitmap = normal_img_bitmap[index];
			stone.angle = computeCurrentAngle(stone.x, stone.y);
		}
	}

	/**
	 * 计算坐标点与圆心直径的角度
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private int computeCurrentAngle(float x, float y) {
		float distance = (float) Math.sqrt(((x - mPointX) * (x - mPointX) + (y - mPointY) * (y - mPointY)));
		int degree = (int) (Math.acos((x - mPointX) / distance) * 180 / Math.PI);
		if (y < mPointY) {
			degree = -degree;
		}
		return degree;
	}

	private boolean isPressLock = false;// 标记是否按住中心锁图片

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		float x, y;
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			x = event.getX();
			y = event.getY();
			mStatus = JudgeStatus(event.getX(), event.getY());
//			isPressLock = isPressLockPic(x, y);
			setIsVisible(isPressLock);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			x = event.getX();
			y = event.getY();
			// 算出当前坐标和圆心的距离
			centerStones.angle = computeCurrentAngle(x, y);
			if (isPressLock) {
				centerStones.bitmap = lockscre_pressed_bit;
				computeCoordinates();
				if (getDistance(x, y) <= mRadius) {
					centerStones.x = x;
					centerStones.y = y;
				} else {// 大于直径时根据角度算出坐标
					centerStones.x = mPointX + (float) ((mRadius) * Math.cos(centerStones.angle * Math.PI / 180));
					centerStones.y = mPointY + (float) ((mRadius) * Math.sin(centerStones.angle * Math.PI / 180));
					if (centerStones.angle <= (mStones[0].angle + 15) && centerStones.angle >= (mStones[0].angle - 15)) {
//						mStones[0].bitmap = select_img_bitmap[0];
						centerStones.bitmap = select_bg_bit;
						centerStones.x = mStones[0].x;
						centerStones.y = mStones[0].y;
					}
					if (centerStones.angle <= (mStones[1].angle + 15) && centerStones.angle >= (mStones[1].angle - 15)) {
//						mStones[1].bitmap = select_img_bitmap[1];
						centerStones.bitmap = select_bg_bit;
						centerStones.x = mStones[1].x;
						centerStones.y = mStones[1].y;
					}
					if (centerStones.angle <= (mStones[2].angle + 15) && centerStones.angle >= (mStones[2].angle - 15)) {
//						mStones[2].bitmap = select_img_bitmap[2];
						centerStones.bitmap = select_bg_bit;
						centerStones.x = mStones[2].x;
						centerStones.y = mStones[2].y;
					}
					if (centerStones.angle <= (mStones[3].angle + 15) && centerStones.angle >= (mStones[3].angle - 15)) {
//						mStones[3].bitmap = select_img_bitmap[3];
						centerStones.bitmap = select_bg_bit;
						centerStones.x = mStones[3].x;
						centerStones.y = mStones[3].y;
					}
				}
				invalidate();
			}
			break;

		case MotionEvent.ACTION_UP:
			if (mStatus == STATUS_DELETE && mStatus == JudgeStatus(event.getX(), event.getY())) {
				mOnRemoveViewListener.removeView(mAppLockerInfo, this);
				return true;
			}
			if (!isVisiable) {
				mOnFocuseChangeListener.focuseChange(this);
			}
			//处理Action_Up事件：  判断是否解锁成功，成功则结束我们的Activity ；否则 ，缓慢回退该图片。
			break;
		}
		return true;
	}
	
	/**
	 * 根据点击的位置判断是否点中控制旋转，缩放的图片， 初略的计算
	 */
	private int JudgeStatus(float x, float y) {
		PointF touchPoint = new PointF(x, y);
		PointF deletePointF = new PointF(mDeletePoint);
		PointF ivLeftPointF = new PointF(new Point((int)mStones[2].x , (int)mStones[2].y));
		PointF ivRightPointF = new PointF(new Point((int)mStones[0].x , (int)mStones[0].y));
		PointF ivTopPointF = new PointF(new Point((int)mStones[3].x , (int)mStones[3].y));
		PointF ivBottomPointF = new PointF(new Point((int)mStones[1].x , (int)mStones[1].y));

		float distanceToDelete = distance4PointF(touchPoint, deletePointF);
		float distanceToLeftIV = distance4PointF(touchPoint, ivLeftPointF);
		float distanceToRightIV = distance4PointF(touchPoint, ivRightPointF);
		float distanceToTopIV = distance4PointF(touchPoint, ivTopPointF);
		float distanceToBottomIV = distance4PointF(touchPoint, ivBottomPointF);
		if (distanceToDelete < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_DELETE;
		}
		if (distanceToLeftIV < Math.min(normal_img_bitmap[2].getWidth() / 2, normal_img_bitmap[2].getHeight() / 2)) {
			leftOrRight=3;
			invalidate();
			showLinkDialog();
//			showControllerView();
			return STATUS_LEFT_IV;
		}
		if (distanceToRightIV < Math.min(normal_img_bitmap[0].getWidth() / 2, normal_img_bitmap[0].getHeight() / 2)) {
			leftOrRight=1;
			invalidate();
			showLinkDialog();
//			showControllerView();
			return STATUS_RIGHT_IV;
		}
		if (distanceToTopIV < Math.min(normal_img_bitmap[3].getWidth() / 2, normal_img_bitmap[3].getHeight() / 2)) {
			leftOrRight=4;
			invalidate();
			showLinkDialog();
//			showControllerView();
			return STATUS_TOP_IV;
		}
		if (distanceToBottomIV < Math.min(normal_img_bitmap[1].getWidth() / 2, normal_img_bitmap[1].getHeight() / 2)) {
			leftOrRight=2;
			invalidate();
//			showLinkDialog();
//			showControllerView();
			return STATUS_BOTTOM_IV;
		}
		return STATUS_DRAG;

	}
	
	private void showLinkDialog() {
				BindShortcutListener mBindShortcutListener = new BindShortcutListener() {

					@Override
					public void bindShortcut(String packageName, String className) {
						if(leftOrRight==1){
							mAppLockerInfo.componentName = new ComponentName(packageName, className);
							ThemeUtils.parseComponentName2Action(mAppLockerInfo, mAppLockerInfo.componentName,leftOrRight);
						}else if(leftOrRight==3){
							mAppLockerInfo.componentName2 = new ComponentName(packageName, className);
							ThemeUtils.parseComponentName2Action(mAppLockerInfo, mAppLockerInfo.componentName2,leftOrRight);
						}else if(leftOrRight==4){
							mAppLockerInfo.componentName3 = new ComponentName(packageName, className);
							ThemeUtils.parseComponentName2Action(mAppLockerInfo, mAppLockerInfo.componentName3,leftOrRight);
						}
					}

				};
				SelectAppPopup selectAppPopup = new SelectAppPopup(mContext, (View) getParent(), null);
				selectAppPopup.setBindShortcutListener(mBindShortcutListener);
				selectAppPopup.setOnImageSelectorListener( this);
	}
	/**
	 * 两个点之间的距离
	 */
	private float distance4PointF(PointF pf1, PointF pf2) {
		float disX = pf2.x - pf1.x;
		float disY = pf2.y - pf1.y;
		return (float) Math.sqrt(disX * disX + disY * disY);
	}
	
	//回退动画时间间隔值 
	private static int BACK_DURATION = 20 ;   // 20ms
    //水平方向前进速率
	private static float VE_HORIZONTAL = 0.8f ;  //0.1dip/ms
	private Handler mHandler =new Handler ();
	
	private void backToCenter() {
		mHandler.postDelayed(BackDragImgTask, BACK_DURATION);
	}
	
	//通过延时控制当前绘制bitmap的位置坐标
	private Runnable BackDragImgTask = new Runnable(){
		public void run(){
			//一下次Bitmap应该到达的坐标值
			if(centerStones.x>=mPointX){
				centerStones.x = centerStones.x - BACK_DURATION * VE_HORIZONTAL;
				if(centerStones.x<mPointX){
					centerStones.x = mPointX;
				}
			} else {
				centerStones.x = centerStones.x + BACK_DURATION * VE_HORIZONTAL;
				if(centerStones.x>mPointX){
					centerStones.x = mPointX;
				}
			} 
			centerStones.y = mPointY + (float) ((centerStones.x-mPointX) * Math.tan(centerStones.angle * Math.PI / 180));
			
			invalidate();//重绘		
			boolean shouldEnd = getDistance(centerStones.x, centerStones.y) <= 8 ;			
			if(!shouldEnd)
			    mHandler.postDelayed(BackDragImgTask, BACK_DURATION);
			else { //复原初始场景
				centerStones.x = mPointX;
				centerStones.y = mPointY;
				isPressLock = false;
				setIsVisible(isPressLock);
				invalidate();
			}				
		}
	};

	/**
	 * 获取坐标点与圆心直径的距离
	 * @param x
	 * @param y
	 * @return
	 */
	private float getDistance(float x, float y) {
		float distance = (float) Math.sqrt(((x - mPointX) * (x - mPointX) + (y - mPointY) * (y - mPointY)));
		return distance;
	}

	/**
	 * 判断手指按下的时候是否按住中心锁图片
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isPressLockPic(float x, float y) {
		float l = centerStones.x - centerStones.bitmap.getWidth() / 2;
		float r = centerStones.x + centerStones.bitmap.getWidth() / 2;
		float t = centerStones.y - centerStones.bitmap.getHeight() / 2;
		float b = centerStones.y + centerStones.bitmap.getHeight() / 2;
		if (x >= l && x <= r && y >= t && y <= b) {
			return true;
		}
		return false;
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
	public void onDraw(Canvas canvas) {
		
		if(isVisiable){
			
			if (leftOrRight == 1) {
				mSelectPath.reset();
				mSelectPath.moveTo(mStones[0].x-DensityUtil.dip2px(mContext, 19), mStones[0].y+DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[0].x+DensityUtil.dip2px(mContext, 19), mStones[0].y+DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[0].x+DensityUtil.dip2px(mContext, 19), mStones[0].y-DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[0].x-DensityUtil.dip2px(mContext, 19), mStones[0].y-DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[0].x-DensityUtil.dip2px(mContext, 19), mStones[0].y+DensityUtil.dip2px(mContext, 19));
				canvas.drawPath(mSelectPath, mSelectPaint);

			}else{
				mSelectPath.reset();
				mSelectPath.moveTo(mStones[0].x-DensityUtil.dip2px(mContext, 13), mStones[0].y+DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[0].x+DensityUtil.dip2px(mContext, 13), mStones[0].y+DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[0].x+DensityUtil.dip2px(mContext, 13), mStones[0].y-DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[0].x-DensityUtil.dip2px(mContext, 13), mStones[0].y-DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[0].x-DensityUtil.dip2px(mContext, 13), mStones[0].y+DensityUtil.dip2px(mContext, 13));
				canvas.drawPath(mSelectPath, mSelectPaint);
			}
			if (leftOrRight == 2) {
				mSelectPath.reset();
				mSelectPath.moveTo(mStones[1].x-DensityUtil.dip2px(mContext, 19), mStones[1].y+DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[1].x+DensityUtil.dip2px(mContext, 19), mStones[1].y+DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[1].x+DensityUtil.dip2px(mContext, 19), mStones[1].y-DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[1].x-DensityUtil.dip2px(mContext, 19), mStones[1].y-DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[1].x-DensityUtil.dip2px(mContext, 19), mStones[1].y+DensityUtil.dip2px(mContext, 19));
				canvas.drawPath(mSelectPath, mSelectPaint);
			}else{
				mSelectPath.reset();
				mSelectPath.moveTo(mStones[1].x-DensityUtil.dip2px(mContext, 13), mStones[1].y+DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[1].x+DensityUtil.dip2px(mContext, 13), mStones[1].y+DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[1].x+DensityUtil.dip2px(mContext, 13), mStones[1].y-DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[1].x-DensityUtil.dip2px(mContext, 13), mStones[1].y-DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[1].x-DensityUtil.dip2px(mContext, 13), mStones[1].y+DensityUtil.dip2px(mContext, 13));
				canvas.drawPath(mSelectPath, mSelectPaint);
			}
			if (leftOrRight == 3) {
				mSelectPath.reset();
				mSelectPath.moveTo(mStones[2].x-DensityUtil.dip2px(mContext, 19), mStones[2].y+DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[2].x+DensityUtil.dip2px(mContext, 19), mStones[2].y+DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[2].x+DensityUtil.dip2px(mContext, 19), mStones[2].y-DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[2].x-DensityUtil.dip2px(mContext, 19), mStones[2].y-DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[2].x-DensityUtil.dip2px(mContext, 19), mStones[2].y+DensityUtil.dip2px(mContext, 19));
				canvas.drawPath(mSelectPath, mSelectPaint);
				
			}else{
				mSelectPath.reset();
				mSelectPath.moveTo(mStones[2].x-DensityUtil.dip2px(mContext, 13), mStones[2].y+DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[2].x+DensityUtil.dip2px(mContext, 13), mStones[2].y+DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[2].x+DensityUtil.dip2px(mContext, 13), mStones[2].y-DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[2].x-DensityUtil.dip2px(mContext, 13), mStones[2].y-DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[2].x-DensityUtil.dip2px(mContext, 13), mStones[2].y+DensityUtil.dip2px(mContext, 13));
				canvas.drawPath(mSelectPath, mSelectPaint);
			}
			if (leftOrRight == 4) {
				mSelectPath.reset();
				mSelectPath.moveTo(mStones[3].x-DensityUtil.dip2px(mContext, 19), mStones[3].y+DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[3].x+DensityUtil.dip2px(mContext, 19), mStones[3].y+DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[3].x+DensityUtil.dip2px(mContext, 19), mStones[3].y-DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[3].x-DensityUtil.dip2px(mContext, 19), mStones[3].y-DensityUtil.dip2px(mContext, 19));
				mSelectPath.lineTo(mStones[3].x-DensityUtil.dip2px(mContext, 19), mStones[3].y+DensityUtil.dip2px(mContext, 19));
				canvas.drawPath(mSelectPath, mSelectPaint);
			}else{
				mSelectPath.reset();
				mSelectPath.moveTo(mStones[3].x-DensityUtil.dip2px(mContext, 13), mStones[3].y+DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[3].x+DensityUtil.dip2px(mContext, 13), mStones[3].y+DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[3].x+DensityUtil.dip2px(mContext, 13), mStones[3].y-DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[3].x-DensityUtil.dip2px(mContext, 13), mStones[3].y-DensityUtil.dip2px(mContext, 13));
				mSelectPath.lineTo(mStones[3].x-DensityUtil.dip2px(mContext, 13), mStones[3].y+DensityUtil.dip2px(mContext, 13));
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
		}
		
		if (isPressLock) {// 手指按下状态
			canvas.drawCircle(mPointX, mPointY, mRadius, mPaint);// 画圆
			drawInCenter(canvas, centerStones.bitmap, centerStones.x, centerStones.y);// 画中心锁图片
			for (int index = 0; index < STONE_COUNT; index++) {
				if (!mStones[index].isVisible)
					continue;
				drawInCenter(canvas, mStones[index].bitmap, mStones[index].x, mStones[index].y);
			}
		} else {
			canvas.drawCircle(mPointX, mPointY, mRadius, mPaint);// 画圆
			for (int index = 0; index < STONE_COUNT; index++) {
				drawInCenter(canvas, mStones[index].bitmap, mStones[index].x, mStones[index].y);
			}
			centerStones.bitmap = lockscreen_normal_bit;
			drawInCenter(canvas, centerStones.bitmap, centerStones.x, centerStones.y);// 画中心锁图片
		}
	}
	
	public void setAppLockerInfo(AppLockerInfo mAppLockerInfo) {
		this.mAppLockerInfo = mAppLockerInfo;
		init(mContainerLayoutParams.width, mContainerLayoutParams.height/2, mContainerLayoutParams.width/3);
	}

	/**
	 * 把中心点放到中心处
	 * 
	 * @param canvas
	 * @param bitmap
	 * @param left
	 * @param top
	 */
	void drawInCenter(Canvas canvas, Bitmap bitmap, float left, float top) {
		canvas.drawBitmap(bitmap, left - bitmap.getWidth() / 2, top - bitmap.getHeight() / 2, null);
	}
	
	private void setIsVisible(boolean isVisible){
		for (int index = 0; index < STONE_COUNT; index++) {
			mStones[index].isVisible = isVisible;
		}
	}

	class BigStone {
		// 图片
		public Bitmap bitmap;
		// 角度
		public int angle;
		// x坐标
		public float x;
		// y坐标
		public float y;
		// 是否可见
		public boolean isVisible = true;
	}
	
	/**
	 * 设置是否显示绘制操作按钮
	 */
	public void setVisible(boolean isVisiable) {
		this.isVisiable = isVisiable;
		invalidate();
	}
	public void setOnUpdateViewListener(OnUpdateViewListener onUpdateViewListener) {
		this.mOnUpdateViewListener = onUpdateViewListener;
	}

	public void setContainerLayoutParams(android.widget.RelativeLayout.LayoutParams containerLayoutParams) {
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
	
	public void selectImage(Bitmap bitmap) {
		bitmap=DrawableUtils.scaleTo(bitmap, DensityUtil.dip2px(mContext, 30), DensityUtil.dip2px(mContext, 30));
		if (leftOrRight == 1) {
			if (bitmap != null) {
				mAppLockerInfo.getBitmaps()[0] = bitmap;
			}
		}
		if (leftOrRight == 2) {
			if (bitmap != null) {
				mAppLockerInfo.getBitmaps()[1] = bitmap;
			}
		}
		if (leftOrRight == 3) {
			if (bitmap != null) {
				mAppLockerInfo.getBitmaps()[2] = bitmap;
			}
		}
		if (leftOrRight == 4) {
			if (bitmap != null) {
				mAppLockerInfo.getBitmaps()[3] = bitmap;
			}
		}
		updateImage();
	}
	private void updateImage() {
		Bitmap bitmap = mAppLockerInfo.getBitmaps()[0];
		Bitmap bitmap2 = mAppLockerInfo.getBitmaps()[1];
		Bitmap bitmap3 = mAppLockerInfo.getBitmaps()[2];
		Bitmap bitmap4 = mAppLockerInfo.getBitmaps()[3];
		if (bitmap != null) {
			bitmaps[0] = bitmap;
		} else {
			bitmaps[0] = normal_img_bitmap[0];
		}
		if (bitmap2 != null) {
			bitmaps[1] = bitmap2;
		} else {
			bitmaps[1] = normal_img_bitmap[1];
		}
		if (bitmap3 != null) {
			bitmaps[2] = bitmap3;
		} else {
			bitmaps[2] = normal_img_bitmap[2];
		}
		if (bitmap4 != null) {
			bitmaps[3] = bitmap4;
		} else {
			bitmaps[3] = normal_img_bitmap[3];
		}
		for(int i=0;i<4;i++){
			normal_img_bitmap[i]=bitmaps[i];
		}
		computeCoordinates();
		invalidate();
	}

}
