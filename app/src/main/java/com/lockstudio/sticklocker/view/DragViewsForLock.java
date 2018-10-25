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

import java.util.ArrayList;


public class DragViewsForLock extends ImageView {
	
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
	private boolean mInputEnabled = true;
	private SlideLockerInfo mSlideLockerInfo;

	public DragViewsForLock(Context context,Handler mHandler) {
		super(context);
		this.mContext = context;
		this.mHandler = mHandler;
		init();
	}

	public DragViewsForLock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public DragViewsForLock(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	private void init() {
		sp = mContext.getSharedPreferences("myprefence", Context.MODE_MULTI_PROCESS);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = screenWidth;
	}
	
	public void disableInput() {
		mInputEnabled = false;
	}
	
	public void setSlideLockerInfo(SlideLockerInfo mSlideLockerInfo) {
		this.mSlideLockerInfo = mSlideLockerInfo;
		init();
	}
	
	public ArrayList<Integer> getZhuIconCenter(){
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		int left1 = mSlideLockerInfo.getLeft1();
		int top1 = mSlideLockerInfo.getTop1();
//		int left1 = sp.getInt("left1", 0);
//		int top1 = sp.getInt("top1", 0);
		arrayList.add(left1 + getWidth()/2);
		arrayList.add(top1 + getHeight()/2);
		return arrayList;
		
	}
	public ArrayList<Integer> getPoint(){
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		int left1 = mSlideLockerInfo.getLeft1();
		int top1 = mSlideLockerInfo.getTop1();
//		int left1 = sp.getInt("left1", 0);
//		int top1 = sp.getInt("top1", 0);
		arrayList.add(left1);
		arrayList.add(top1);
		return arrayList;
		
	}
	
	public int getScreenHeight(){
		return screenHeight;
	}
	
	// On touch Event.
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mInputEnabled || !isEnabled()) {
			return false;
		}
		final int iAction = event.getAction();
		iCurrentx = (int)event.getX();
		iCurrenty = (int)event.getY();
		
		switch (iAction) {
		case MotionEvent.ACTION_DOWN:
			mPreviousx = iCurrentx;
			mPreviousy = iCurrenty;
			break;
		case MotionEvent.ACTION_MOVE:
			int iDeltx = iCurrentx - mPreviousx;
			int iDelty = iCurrenty - mPreviousy;
			left = getLeft() + iDeltx;
			top = getTop() + iDelty;
			right = getRight() + iDeltx;
			bottom = getBottom() + iDelty;
			
			
			if (iDeltx != 0 || iDelty != 0) {
				
				if (left < 0) {
					left = 0;
					right = left + getWidth();
				}
				
				if(right > screenWidth){
					right = screenWidth;
					left = right - getWidth();
				}
				
				if(top < 0){
					top = 0;
					bottom = top + getHeight();
				}
				
				if(bottom > screenHeight){
					bottom = screenHeight;
					top = bottom - getHeight();
				}
				
				layout(left, top, right, bottom);
				Message msg = new Message();
				msg.arg1 = getLeft();
				msg.arg2 = getTop();
				msg.what = 0;
				mHandler.sendMessage(msg);
			}
			
			mPreviousx = iCurrentx - iDeltx;
			mPreviousy = iCurrenty - iDelty;
			break;
		case MotionEvent.ACTION_UP:
			Message msg = new Message();
			msg.arg1 = getLeft();
			msg.arg2 = getTop();
			msg.what = 1;
			mHandler.sendMessage(msg);
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return true;
	}
	
}
