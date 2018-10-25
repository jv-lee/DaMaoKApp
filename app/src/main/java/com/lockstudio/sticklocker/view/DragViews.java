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


public class DragViews extends ImageView {
	
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
	private int left2;
	private int top2;
	private int right2;
	private int bottom2;
	private long currentTime;
	private boolean first;
	private SlideLockerInfo mSlideLockerInfo;

	public DragViews(Context context,Handler mHandler) {
		super(context);
		this.mContext = context;
		this.mHandler = mHandler;
	}

	public DragViews(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public DragViews(Context context, AttributeSet attrs) {
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
	
	public ArrayList<Integer> getInitPoint1(){
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		int largen = sp.getInt("largen", DensityUtil.dip2px(mContext, 60));
		arrayList.add(DensityUtil.dip2px(mContext, 19));
		arrayList.add(screenHeight/2);
		mSlideLockerInfo.setLeft1(0);
		mSlideLockerInfo.setTop1(screenHeight/2);
		mSlideLockerInfo.setRight1(largen);
		mSlideLockerInfo.setBottom1(screenHeight/2+largen);
//		sp.edit().putInt("left1", 0).commit();
//		sp.edit().putInt("top1", screenHeight/2).commit();
//		sp.edit().putInt("right1", screenHeight/2+largen).commit();
//		sp.edit().putInt("bottom1", screenHeight/2+largen).commit();
		return arrayList;
	}
	
	public ArrayList<Integer> getZhuIconCenter(){
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		int left1 = mSlideLockerInfo.getLeft1();
		if (left1 == 0) {
			left1 = DensityUtil.dip2px(mContext, 19);
		}
		int top1 = mSlideLockerInfo.getTop1();
		int right1 = mSlideLockerInfo.getRight1();
		int bottom1 = mSlideLockerInfo.getBottom1();
//		int left1 = sp.getInt("left1", DensityUtil.dip2px(mContext, 19));
//		int top1 = sp.getInt("top1", DensityUtil.dip2px(mContext, 19));
//		int right1 = sp.getInt("right1", screenWidth - DensityUtil.dip2px(mContext, 19));
//		int bottom1 = sp.getInt("bottom1", screenHeight - DensityUtil.dip2px(mContext, 19));
		int largen = sp.getInt("largen", DensityUtil.dip2px(mContext, 60));
		if ((right1 +left1)/2 > screenWidth/2 && (bottom1 +top1)/2 < screenHeight/2) {
			arrayList.add(left1 - (largen - (right1 - left1)));
			arrayList.add(top1);
		} else if ((right1 +left1)/2 < screenWidth/2 && (bottom1 +top1)/2 < screenHeight/2) {
			arrayList.add(left1);
			arrayList.add(top1);
		} else if ((right1 +left1)/2 > screenWidth/2 && (bottom1 +top1)/2 > screenHeight/2) {
			arrayList.add(left1 - (largen - (right1 - left1)));
			arrayList.add(top1 - (largen - (bottom1 - top1)));
		} else if ((right1 +left1)/2 < screenWidth/2 && (bottom1 +top1)/2 > screenHeight/2) {
			arrayList.add(left1);
			arrayList.add(top1 - (largen - (bottom1 - top1)));
		}else{
			arrayList.add(left1 - (largen - (right1 - left1)));
			arrayList.add(top1);
		}
		return arrayList;
		
	}
	
	public void saveSize() {
		int left1 = mSlideLockerInfo.getLeft1();
		int top1 = mSlideLockerInfo.getTop1();
		int right1 = mSlideLockerInfo.getRight1();
		int bottom1 = mSlideLockerInfo.getBottom1();
//		int left1 = sp.getInt("left1", 0);
//		int top1 = sp.getInt("top1", 0);
//		int right1 = sp.getInt("right1", 0);
//		int bottom1 = sp.getInt("bottom1", 0);
		int largen = sp.getInt("largen", DensityUtil.dip2px(mContext, 60));
		if ((right1 +left1)/2 > screenWidth/2 && (bottom1 +top1)/2 < screenHeight/2) {
			sp.edit().putInt("left1", left1 - (largen - (right1 - left1))).commit();
			sp.edit().putInt("bottom1", bottom1 + (largen - (bottom1 - top1))).commit();
		} else if ((right1 +left1)/2 < screenWidth/2 && (bottom1 +top1)/2 < screenHeight/2) {
			sp.edit().putInt("right1", right1 + (largen - (right1 - left1))).commit();
			sp.edit().putInt("bottom1", bottom1 + (largen - (bottom1 - top1))).commit();
		} else if ((right1 +left1)/2 > screenWidth/2 && (bottom1 +top1)/2 > screenHeight/2) {
			sp.edit().putInt("left1", left1 - (largen - (right1 - left1))).commit();
			sp.edit().putInt("top1", top1 - (largen - (bottom1 - top1))).commit();
		} else if ((right1 +left1)/2 < screenWidth/2 && (bottom1 +top1)/2 > screenHeight/2) {
			sp.edit().putInt("right1", right1 + (largen - (right1 - left1))).commit();
			sp.edit().putInt("top1", top1 - (largen - (bottom1 - top1))).commit();
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
			left2 = mSlideLockerInfo.getLeft2();
			top2 = mSlideLockerInfo.getTop2();
			right2 = mSlideLockerInfo.getRight2();
			bottom2 = mSlideLockerInfo.getBottom2();
//			left2 = sp.getInt("left2", 0);
//			top2 = sp.getInt("top2", 0);
//			right2 = sp.getInt("right2", 0);
//			bottom2 = sp.getInt("bottom2", 0);
			currentTime = System.currentTimeMillis();
			break;
		case MotionEvent.ACTION_UP:
			if (first) {
				sp.edit().putBoolean("first", false).commit();
			}
			mSlideLockerInfo.setLeft1(getLeft());
			mSlideLockerInfo.setTop1(getTop());
			mSlideLockerInfo.setRight1(getRight());
			mSlideLockerInfo.setBottom1(getBottom());
//			sp.edit().putInt("left1", getLeft()).commit();
//			sp.edit().putInt("top1", getTop()).commit();
//			sp.edit().putInt("right1", getRight()).commit();
//			sp.edit().putInt("bottom1", getBottom()).commit();
			long nextTime = System.currentTimeMillis();
			if (nextTime - currentTime < 300) {
				mHandler.sendEmptyMessage(11);
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
				
				if (top < bottom2 && top > top2) {
					//由下右																					由下左
					if ((left < right2 && left > left2 && (centerX - right2) < (centerY - bottom2)) || (right > left2 && right < right2) && (left2 - centerX) < (centerY - bottom2)) {
						top = bottom2;
						bottom = top + getHeight();
					}
				}
				
				if (left < right2 && left > left2) {
					//由右下																				由右上
					if ((top < bottom2 && top > top2 && (top2 - centerY) < (centerX - right2)) || (bottom > top2 && bottom < bottom2 && (top2 - centerY) < (centerX - right2))) {
						left = right2;
						right = left + getWidth();
					}
				}
				
				if (right > left2 && right < right2) {
					//由左下																				由左上
					if ((top < bottom2 && top > top2 && (centerY - bottom2) < (left2 - centerX)) || (bottom > top2 && bottom < bottom2 && (top2 - centerY) < (left2 - centerX))) {
						right = left2;
						left = right - getWidth();
					}
				}
				
				if (bottom > top2 && bottom < bottom2) {
					//由上右																					由上左
					if ((left < right2 && left > left2 && (left2 - centerX) < (bottom2 - centerY)) || (right > left2 && right < right2 && (centerX - right2) < (bottom2 - centerY))) {
						bottom = top2;
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
				msg.what = 10;
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
