package com.lockstudio.sticklocker.view;


import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.lockstudio.sticklocker.Interface.OnFocuseChangeListener;
import com.lockstudio.sticklocker.Interface.OnRemoveLockerViewListener;
import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.Interface.UnlockListener;
import com.lockstudio.sticklocker.model.AppLockerInfo;
import com.lockstudio.sticklocker.util.AppManagerUtils;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.ThemeUtils;

import cn.opda.android.activity.R;

public class AppLockView extends View {

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
	private int[] select_img = { R.drawable.ic_lockscreen_message_activated, R.drawable.ic_lockscreen_unlock_activated, R.drawable.ic_lockscreen_phone_activated, R.drawable.ic_tab_theme_selected };
	private Bitmap[] normal_img_bitmap = new Bitmap[STONE_COUNT];
	private Bitmap[] select_img_bitmap = new Bitmap[STONE_COUNT];
	
	private boolean isVisiable = true;
	private Path mFramePath = new Path();
	private Path mSelectPath = new Path();
	private Paint mSelectPaint;
	private Paint mFramePaint;
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
	private UnlockListener mUnlockListener;
	
	public AppLockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public AppLockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public AppLockView(Context context) {
		super(context);
		this.mContext = context;
	}

	public void init(int px, int py, int radius) {
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(0);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.STROKE);
		
		lockscre_pressed_bit = BitmapFactory.decodeResource(getResources(), R.drawable.lockscre_pressed);
		lockscreen_normal_bit = BitmapFactory.decodeResource(getResources(), R.drawable.lockscreen_normal);
		select_bg_bit = BitmapFactory.decodeResource(getResources(), R.drawable.lockscre_pressed);
		for (int index = 0; index < STONE_COUNT; index++) {
			if(mAppLockerInfo.getBitmaps()[index]!=null){
				normal_img_bitmap[index] = mAppLockerInfo.getBitmaps()[index];
				select_img_bitmap[index] = DrawableUtils.scaleTo(mAppLockerInfo.getBitmaps()[index], DensityUtil.dip2px(mContext, 40), DensityUtil.dip2px(mContext, 40));
			}else{
				normal_img_bitmap[index] = BitmapFactory.decodeResource(getResources(), normal_img[index]);
				select_img_bitmap[index] = BitmapFactory.decodeResource(getResources(), select_img[index]);
			}
		}
		
		mPointX = px / 2;
		mPointY = py / 3 * 2;
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
			isPressLock = isPressLockPic(x, y);
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
						leftOrRight=1;
						mStones[0].bitmap = select_img_bitmap[0];
						centerStones.bitmap = select_bg_bit;
						centerStones.x = mStones[0].x;
						centerStones.y = mStones[0].y;
					}
					if (centerStones.angle <= (mStones[1].angle + 15) && centerStones.angle >= (mStones[1].angle - 15)) {
						leftOrRight=2;
						mStones[1].bitmap = select_img_bitmap[1];
						centerStones.bitmap = select_bg_bit;
						centerStones.x = mStones[1].x;
						centerStones.y = mStones[1].y;
					}
					if (centerStones.angle <= (mStones[2].angle + 15) && centerStones.angle >= (mStones[2].angle - 15)) {
						leftOrRight=3;
						mStones[2].bitmap = select_img_bitmap[2];
						centerStones.bitmap = select_bg_bit;
						centerStones.x = mStones[2].x;
						centerStones.y = mStones[2].y;
					}
					if (centerStones.angle <= (mStones[3].angle + 15) && centerStones.angle >= (mStones[3].angle - 15)) {
						leftOrRight=4;
						mStones[3].bitmap = select_img_bitmap[3];
						centerStones.bitmap = select_bg_bit;
						centerStones.x = mStones[3].x;
						centerStones.y = mStones[3].y;
					}
				}
				invalidate();
			}
			break;

		case MotionEvent.ACTION_UP:
			//处理Action_Up事件：  判断是否解锁成功，成功则结束我们的Activity ；否则 ，缓慢回退该图片。
			handleActionUpEvent(event);
			break;
		}
		return true;
	}
	
	private void handleActionUpEvent(MotionEvent event){
		boolean islocksuc = false;// 是否解锁成功
		float x = event.getX();
		float y = event.getY();
		centerStones.angle = computeCurrentAngle(x, y);
		if (getDistance(x, y) >= mRadius) {
			if (centerStones.angle <= (mStones[0].angle + 15) && centerStones.angle >= (mStones[0].angle - 15) && mStones[0].isVisible) {
				islocksuc = true;
				mUnlockListener.OnUnlockSuccess();
				runApp();
			}
			if (centerStones.angle <= (mStones[1].angle + 15) && centerStones.angle >= (mStones[1].angle - 15) && mStones[1].isVisible) {
				islocksuc = true;
				mUnlockListener.OnUnlockSuccess();
			}
			if (centerStones.angle <= (mStones[2].angle + 15) && centerStones.angle >= (mStones[2].angle - 15) && mStones[2].isVisible) {
				islocksuc = true;
				mUnlockListener.OnUnlockSuccess();
				runApp();
			}
			if (centerStones.angle <= (mStones[3].angle + 15) && centerStones.angle >= (mStones[3].angle - 15) && mStones[3].isVisible) {
				islocksuc = true;
				mUnlockListener.OnUnlockSuccess();
				runApp();
			}
		} 
		if(!islocksuc) { // 未解锁成功
			backToCenter();
		}
	}
	
	public void runApp(){
		if (leftOrRight==1) {
			mAppLockerInfo.componentName=ThemeUtils.parseAction2ComponentName(mAppLockerInfo.action1);
			if(null == mAppLockerInfo.componentName){
				ComponentName componentName = new ComponentName(
						"com.android.mms", "com.yulong.android.mms.ui.MmsMainListFormActivity");
				mAppLockerInfo.componentName=componentName;
			}
			if(AppManagerUtils.installed(mContext, mAppLockerInfo.componentName)){
				AppManagerUtils.startActivity(mContext, mAppLockerInfo.componentName);
			}
		}
		if (leftOrRight==3) {
			mAppLockerInfo.componentName2=ThemeUtils.parseAction2ComponentName(mAppLockerInfo.action2);
			if(null == mAppLockerInfo.componentName2){
				ComponentName componentName = new ComponentName(
						"com.android.contacts", "com.android.contacts.DialtactsActivity");
				mAppLockerInfo.componentName2=componentName;
			}
			if(AppManagerUtils.installed(mContext, mAppLockerInfo.componentName2)){
				AppManagerUtils.startActivity(mContext, mAppLockerInfo.componentName2);
			}
		}
		if (leftOrRight==4) {
			mAppLockerInfo.componentName3=ThemeUtils.parseAction2ComponentName(mAppLockerInfo.action3);
			if(null == mAppLockerInfo.componentName3){
				ComponentName componentName = new ComponentName(
						"com.android.camera", "com.android.camera.camera");
				mAppLockerInfo.componentName3=componentName;
			}
			if(AppManagerUtils.installed(mContext, mAppLockerInfo.componentName3)){
				AppManagerUtils.startActivity(mContext, mAppLockerInfo.componentName3);
			}
		}
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

	@Override
	public void onDraw(Canvas canvas) {
		if (isPressLock) {// 手指按下状态
			canvas.drawCircle(mPointX, mPointY, mRadius, mPaint);// 画圆
			drawInCenter(canvas, centerStones.bitmap, centerStones.x, centerStones.y);// 画中心锁图片
			for (int index = 0; index < STONE_COUNT; index++) {
				if (!mStones[index].isVisible)
					continue;
				if(index==0){
					drawInCenter(canvas, mStones[index].bitmap, mStones[index].x, mStones[index].y);
				}else if(index==1){
					drawInCenter(canvas, mStones[index].bitmap, mStones[index].x, mStones[index].y);
				}else if(index==2){
					drawInCenter(canvas, mStones[index].bitmap, mStones[index].x, mStones[index].y);
				}else if(index==3){
					drawInCenter(canvas, mStones[index].bitmap, mStones[index].x, mStones[index].y);
				}
			}
		} else {
//			for (int index = 0; index < STONE_COUNT; index++) {
//				if(index==0){
//					drawInCenter(canvas, mStones[index].bitmap, mStones[index].x-DensityUtil.dip2px(mContext, 10), mStones[index].y);
//				}else{
//					drawInCenter(canvas, mStones[index].bitmap, mStones[index].x, mStones[index].y);
//				}
//			}
			centerStones.bitmap = lockscreen_normal_bit;
			drawInCenter(canvas, centerStones.bitmap, centerStones.x, centerStones.y);// 画中心锁图片
		}
	}
	
	public void setAppLockerInfo(AppLockerInfo mAppLockerInfo) {
		this.mAppLockerInfo = mAppLockerInfo;
		init(mAppLockerInfo.getWidth(), mAppLockerInfo.getHeight()/2+DensityUtil.dip2px(mContext, 45), mAppLockerInfo.getWidth()/3);
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
	
	public static interface OnCoupleListener {
		void unlockSuccsed();
	}
	
	public void setUnlockListener(UnlockListener mUnlockListener) {
		this.mUnlockListener = mUnlockListener;
	}
	
}
