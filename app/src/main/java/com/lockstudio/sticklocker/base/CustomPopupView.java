package com.lockstudio.sticklocker.base;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.lockstudio.sticklocker.Interface.OnBackClickListener;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.view.CustomLinearLayout;

import cn.opda.android.activity.R;

public class CustomPopupView {
	private OnDismissListener mOnDismissListener;
	private WindowManager mWindowManager;
	private CustomLinearLayout rootLayout;
	private View popupView;
	private LayoutParams mLayoutParams;
	private boolean dismissed;
	private OnBackClickListener onBackClickListener;
	public CustomPopupView getPopupWindow(Context mContext, View popupView) {

		rootLayout = (CustomLinearLayout) LayoutInflater.from(mContext).inflate(R.layout.base_popup_layout, null);
		TextView text_top = (TextView) popupView.findViewById(R.id.text_top);
		TextView text_bottom = (TextView) popupView.findViewById(R.id.text_bottom);
		if (text_top != null) {
			text_top.setHeight(DeviceInfoUtils.getStatusBarHeight(mContext));
		}
		if (text_bottom != null) {
			text_bottom.setHeight(DeviceInfoUtils.getNavigationBarHeight(mContext));
		}

		android.widget.LinearLayout.LayoutParams layoutParams = new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.width = LockApplication.getInstance().getConfig().getScreenWidth();
		layoutParams.height = LockApplication.getInstance().getConfig().getScreenHeight();
		rootLayout.addView(popupView,layoutParams);

		if (mLayoutParams == null) {

			mLayoutParams = new LayoutParams();
			mLayoutParams.x = 0;
			mLayoutParams.y = 0;
			mLayoutParams.width = LockApplication.getInstance().getConfig().getScreenWidth();
			mLayoutParams.height = LockApplication.getInstance().getConfig().getScreenHeight();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				mLayoutParams.gravity = Gravity.RELATIVE_LAYOUT_DIRECTION | Gravity.TOP | Gravity.LEFT;
			} else {
				mLayoutParams.gravity = Gravity.FILL;
			}

			mLayoutParams.type = LayoutParams.TYPE_PHONE;

			mLayoutParams.flags |= LayoutParams.FLAG_LAYOUT_IN_SCREEN;
			mLayoutParams.flags |= LayoutParams.FLAG_LAYOUT_NO_LIMITS;
			mLayoutParams.flags |= LayoutParams.FLAG_FULLSCREEN;
			mLayoutParams.flags |= LayoutParams.FLAG_DISMISS_KEYGUARD;
			mLayoutParams.flags |= LayoutParams.FLAG_SHOW_WHEN_LOCKED;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				mLayoutParams.flags |= LayoutParams.FLAG_TRANSLUCENT_STATUS;
				mLayoutParams.flags |= LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
			}

			mLayoutParams.softInputMode = LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
			mLayoutParams.format = PixelFormat.TRANSPARENT;
			mLayoutParams.screenOrientation = Configuration.ORIENTATION_PORTRAIT;
		}
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		}

		rootLayout.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(onBackClickListener!=null){
					onBackClickListener.onBackClick();
				}else{
					dismiss();
				}
				return true;
			}
		});
		if (rootLayout != null && !rootLayout.isShown()) {
			mWindowManager.addView(rootLayout, mLayoutParams);
		}
		
		LockApplication.getInstance().getPopupWindowManager().addPopup(mContext,this);
		
		return this;
	}

	public void dismiss() {
		if(dismissed){
			return;
		}
		dismissed = true;
		if (mWindowManager != null) {
			if (rootLayout != null && rootLayout.isShown()) {
				try {
					mWindowManager.removeViewImmediate(rootLayout);
				} finally {
					if (popupView != null) {
						rootLayout.removeView(popupView);
					}
					popupView = null;
					rootLayout = null;
					if (mOnDismissListener != null) {
						mOnDismissListener.onDismiss();
					}
					System.gc();
				}

			}
		}
		LockApplication.getInstance().getPopupWindowManager().remove(this);
	}

	public void setOnDismissListener(OnDismissListener onDismissListener) {
		this.mOnDismissListener = onDismissListener;
	}

	public interface OnDismissListener {
		public void onDismiss();
	}
	
	public void setOnBackClickListener(OnBackClickListener onBackClickListener){
		this.onBackClickListener = onBackClickListener;
	}
}
