package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.lockstudio.sticklocker.model.SlideLockerInfo;
import com.lockstudio.sticklocker.util.DensityUtil;

import java.util.ArrayList;


public class DragViews2 extends ImageView {
	
	private Handler mHandler;
	private SharedPreferences sp;
	private Context mContext;
	
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
	private int left1;
	private int top1;
	private int right1;
	private int bottom1;
	private long currentTime;
	private boolean first;
	private SlideLockerInfo mSlideLockerInfo;

	public DragViews2(Context context,Handler mHandler) {
		super(context);
		this.mContext = context;
		this.mHandler = mHandler;
	}

	public DragViews2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public DragViews2(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}
	
	public void setSlideLockerInfo(SlideLockerInfo mSlideLockerInfo) {
		this.mSlideLockerInfo = mSlideLockerInfo;
		init();
	}

	private void init() {
		sp = mContext.getSharedPreferences("myprefence", Context.MODE_MULTI_PROCESS);
		first = sp.getBoolean("first", false);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = screenWidth;
	}
	
	public ArrayList<Integer> getInitPoint2(){
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		int largen = sp.getInt("largen", DensityUtil.dip2px(mContext, 60));
		arrayList.add(screenWidth - largen - DensityUtil.dip2px(mContext, 19));
		arrayList.add(screenHeight/2);
		mSlideLockerInfo.setLeft2(screenWidth - largen);
		mSlideLockerInfo.setTop2(screenHeight/2);
		mSlideLockerInfo.setRight2(screenWidth);
		mSlideLockerInfo.setBottom2(screenHeight/2+largen);
//		sp.edit().putInt("left2", screenWidth - largen).commit();
//		sp.edit().putInt("top2", screenHeight/2).commit();
//		sp.edit().putInt("right2", screenWidth).commit();
//		sp.edit().putInt("bottom2", screenHeight/2+largen).commit();
		return arrayList;
	}
	
	public ArrayList<Integer> getZhuIconCenter(){
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		int left2 = mSlideLockerInfo.getLeft2();
		int top2 = mSlideLockerInfo.getTop2();
		int right2 = mSlideLockerInfo.getRight2();
		if (right2 == screenWidth) {
			right2 = screenWidth - DensityUtil.dip2px(mContext, 19);
		}
		int bottom2 = mSlideLockerInfo.getBottom2();
//		int left2 = sp.getInt("left2", 0);
//		int top2 = sp.getInt("top2", 0);
//		int right2 = sp.getInt("right2", 0);
//		int bottom2 = sp.getInt("bottom2", 0);
		int largen = sp.getInt("largen", DensityUtil.dip2px(mContext, 60));
		if ((right2 +left2)/2 > screenWidth/2 && (bottom2 +top2)/2 < screenHeight/2) {
			//右上
			arrayList.add(left2 - (largen - (right2 - left2)));
			arrayList.add(top2);
		} else if ((right2 +left2)/2 < screenWidth/2 && (bottom2 +top2)/2 < screenHeight/2) {
			//左上
			arrayList.add(left2);
			arrayList.add(top2);
		} else if ((right2 +left2)/2 > screenWidth/2 && (bottom2 +top2)/2 > screenHeight/2) {
			//右下
			arrayList.add(left2 - (largen - (right2 - left2)));
			arrayList.add(top2 - (largen - (bottom2 - top2)));
		} else if ((right2 +left2)/2 < screenWidth/2 && (bottom2 +top2)/2 > screenHeight/2) {
			//左下
			arrayList.add(left2);
			arrayList.add(top2 - (largen - (bottom2 - top2)));
		}else{
			arrayList.add(left2 - (largen - (right2 - left2)));
			arrayList.add(top2);
		}
		return arrayList;
		
	}
	
	public void saveSize() {
		int left2 = mSlideLockerInfo.getLeft2();
		int top2 = mSlideLockerInfo.getTop2();
		int right2 = mSlideLockerInfo.getRight2();
		int bottom2 = mSlideLockerInfo.getBottom2();
//		int left2 = sp.getInt("left2", 0);
//		int top2 = sp.getInt("top2", 0);
//		int right2 = sp.getInt("right2", 0);
//		int bottom2 = sp.getInt("bottom2", 0);
		int largen = sp.getInt("largen", DensityUtil.dip2px(mContext, 60));
		if ((right2 +left2)/2 > screenWidth/2 && (bottom2 +top2)/2 < screenHeight/2) {
			sp.edit().putInt("left2", left2 - (largen - (right2 - left2))).commit();
			sp.edit().putInt("bottom2", bottom2 + (largen - (bottom2 - top2))).commit();
		} else if ((right2 +left2)/2 < screenWidth/2 && (bottom2 +top2)/2 < screenHeight/2) {
			sp.edit().putInt("right2", right2 + (largen - (right2 - left2))).commit();
			sp.edit().putInt("bottom2", bottom2 + (largen - (bottom2 - top2))).commit();
		} else if ((right2 +left2)/2 > screenWidth/2 && (bottom2 +top2)/2 > screenHeight/2) {
			sp.edit().putInt("left2", left2 - (largen - (right2 - left2))).commit();
			sp.edit().putInt("top2", top2 - (largen - (bottom2 - top2))).commit();
		} else if ((right2 +left2)/2 < screenWidth/2 && (bottom2 +top2)/2 > screenHeight/2) {
			sp.edit().putInt("right2", right2 + (largen - (right2 - left2))).commit();
			sp.edit().putInt("top2", top2 - (largen - (bottom2 - top2))).commit();
		}
	}
	
	// On touch Event.
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int iAction = event.getAction();
		iCurrentx = (int)event.getX();
		iCurrenty = (int)event.getY();
		
		switch (iAction) {
		case MotionEvent.ACTION_DOWN:
			mPreviousx = iCurrentx;
			mPreviousy = iCurrenty;
			left1 = mSlideLockerInfo.getLeft1();
			top1 = mSlideLockerInfo.getTop1();
			right1 = mSlideLockerInfo.getRight1();
			bottom1 = mSlideLockerInfo.getBottom1();
//			left1 = sp.getInt("left1", 0);
//			top1 = sp.getInt("top1", 0);
//			right1 = sp.getInt("right1", 0);
//			bottom1 = sp.getInt("bottom1", 0);
			currentTime = System.currentTimeMillis();
			break;
		case MotionEvent.ACTION_UP:
			if (first) {
				sp.edit().putBoolean("first", false).commit();
			}
			mSlideLockerInfo.setLeft2(getLeft());
			mSlideLockerInfo.setTop2(getTop());
			mSlideLockerInfo.setRight2(getRight());
			mSlideLockerInfo.setBottom2(getBottom());
//			sp.edit().putInt("left2", getLeft()).commit();
//			sp.edit().putInt("top2", getTop()).commit();
//			sp.edit().putInt("right2", getRight()).commit();
//			sp.edit().putInt("bottom2", getBottom()).commit();
			long nextTime = System.currentTimeMillis();
			if (nextTime - currentTime < 300) {
				mHandler.sendEmptyMessage(22);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int iDeltx = iCurrentx - mPreviousx;
			int iDelty = iCurrenty - mPreviousy;
			left = getLeft() + iDeltx;
			top = getTop() + iDelty;
			right = getRight() + iDeltx;
			bottom = getBottom() + iDelty;
			
			int centerX = getLeft() + getWidth()/2;
			int centerY = getTop() + getHeight()/2;
			if (iDeltx != 0 || iDelty != 0) {
				
				if (top < bottom1 && top > top1) {
					//由下右																					由下左
					if ((left < right1 && left > left1 && (centerX - right1) < (centerY - bottom1)) || (right > left1 && right < right1) && (left1 - centerX) < (centerY - bottom1)) {
						top = bottom1;
						bottom = top + getHeight();
					}
				}
				
				if (left < right1 && left > left1) {
					//由右下																				由右上
					if ((top < bottom1 && top > top1 && (top1 - centerY) < (centerX - right1)) || (bottom > top1 && bottom < bottom1 && (top1 - centerY) < (centerX - right1))) {
						left = right1;
						right = left + getWidth();
					}
				}
				
				if (right > left1 && right < right1) {
					//由左下																				由左上
					if ((top < bottom1 && top > top1 && (centerY - bottom1) < (left1 - centerX)) || (bottom > top1 && bottom < bottom1 && (top1 - centerY) < (left1 - centerX))) {
						right = left1;
						left = right - getWidth();
					}
				}
				
				if (bottom > top1 && bottom < bottom1) {
					//由上右																					由上左
					if ((left < right1 && left > left1 && (left1 - centerX) < (bottom1 - centerY)) || (right > left1 && right < right1 && (centerX - right1) < (bottom1 - centerY))) {
						bottom = top1;
						top = bottom - getHeight();
					}
				}
				
				if (left < DensityUtil.dip2px(mContext, 19)) {
					left = DensityUtil.dip2px(mContext, 19);
					right = left + getWidth();
				}
				
				if(right > screenWidth-DensityUtil.dip2px(mContext, 19)){
					right = screenWidth-DensityUtil.dip2px(mContext, 19);
					left = right - getWidth();
				}
				
				if(top < DensityUtil.dip2px(mContext, 19)){
					top = DensityUtil.dip2px(mContext, 19);
					bottom = top + getHeight();
				}
				
				if(bottom > screenHeight-DensityUtil.dip2px(mContext, 19)){
					bottom = screenHeight-DensityUtil.dip2px(mContext, 19);
					top = bottom - getHeight();
				}
				
				Message msg = new Message();
				msg.arg1 = left;
				msg.arg2 = top;
				msg.what = 20;
				layout(left, top, right, bottom);
				mHandler.sendMessage(msg);
			}
			
			mPreviousx = iCurrentx - iDeltx;
			mPreviousy = iCurrenty - iDelty;
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return true;
	}

}
