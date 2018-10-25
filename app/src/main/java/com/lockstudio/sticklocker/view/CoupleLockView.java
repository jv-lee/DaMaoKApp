package com.lockstudio.sticklocker.view;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.lockstudio.sticklocker.Interface.UnlockListener;
import com.lockstudio.sticklocker.model.CoupleLockerInfo;
import com.lockstudio.sticklocker.util.DensityUtil;

import cn.opda.android.activity.R;

public class CoupleLockView extends RelativeLayout {

	private CoupleDragViews cdv1;
	private CoupleDragViews2 cdv2;
	private LayoutParams lp1;
	private LayoutParams lp2;
	private Context mContext;
	private Bitmap mBitmapCircleDefault;
	private Bitmap mBitmapCircleGreen;
	private SharedPreferences sp;
	private UnlockListener mUnlockListener;
	private CoupleLockerInfo mCoupleLockerInfo;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				int space = msg.arg1;
				lp2.setMargins(0, 0, space, 0);
				cdv2.setLayoutParams(lp2);
				lp1.setMargins(space, 0, 0, 0);
				cdv1.setLayoutParams(lp1);
				break;
			case 12:
				lp2.setMargins(0, 0, 0, 0);
				cdv2.setLayoutParams(lp2);
				lp1.setMargins(0, 0, 0, 0);
				cdv1.setLayoutParams(lp1);
				break;
			case 21:
				mUnlockListener.OnUnlockSuccess();
				break;

			default:
				break;
			}
		}

	};

	public CoupleLockView(Context context) {
		super(context);
		this.mContext = context;
//		init();
	}

	public CoupleLockView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
//		init();
	}

	public CoupleLockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
//		init();
	}

	private void init() {
		sp = mContext.getSharedPreferences("myprefence", Context.MODE_MULTI_PROCESS);
		int largen = sp.getInt("couple_largen", -1);

		cdv1 = new CoupleDragViews(mContext, mHandler);
		cdv2 = new CoupleDragViews2(mContext, mHandler);
		updateImage();
		if (largen == -1) {
			lp1 = new LayoutParams(DensityUtil.dip2px(mContext, 60), DensityUtil.dip2px(mContext, 60));
			lp2 = new LayoutParams(DensityUtil.dip2px(mContext, 60), DensityUtil.dip2px(mContext, 60));
		} else {
			lp1 = new LayoutParams(largen, largen);
			lp2 = new LayoutParams(largen, largen);
		}
        lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
		addView(cdv1, lp1);
		
		lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 2);
		addView(cdv2, lp2);
		
	}
	
	public void setCoupleLockerInfo(CoupleLockerInfo mCoupleLockerInfo) {
		this.mCoupleLockerInfo = mCoupleLockerInfo;
		init();
	}
	public void disableInput() {
		cdv1.disableInput();
		cdv2.disableInput();
	}
	private void updateImage() {
		mBitmapCircleDefault = BitmapFactory.decodeResource(getResources(),R.drawable.girl);
		mBitmapCircleGreen = BitmapFactory.decodeResource(getResources(),R.drawable.boy);
		Bitmap[] bitmaps = mCoupleLockerInfo.getBitmaps();
		if (bitmaps[0] != null){
			cdv1.setBackgroundDrawable(new BitmapDrawable(bitmaps[0]));
		} else {
			cdv1.setBackgroundDrawable(new BitmapDrawable(mBitmapCircleDefault));
		}
		if (bitmaps[1] != null){
			cdv2.setBackgroundDrawable(new BitmapDrawable(bitmaps[1]));
		} else {
			cdv2.setBackgroundDrawable(new BitmapDrawable(mBitmapCircleGreen));
		}
	}
	
	public static interface OnCoupleListener {
		void unlockSuccsed();
	}
	
	public void setUnlockListener(UnlockListener mUnlockListener) {
		this.mUnlockListener = mUnlockListener;
	}
	
}
