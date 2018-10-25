package com.lockstudio.sticklocker.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import cn.opda.android.activity.R;

/**
 * Created by Tommy on 15/3/12.
 */
public class BaseFragmentActivity extends FragmentActivity {

	protected static Context mContext;
	protected Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mActivity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏状态栏
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.anim.activity_out);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.activity_in, 0);
	}

	@SuppressLint({ "NewApi", "InlinedApi" })
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
			// WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

		if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
			View v = findViewById(R.id.root_layout);
			if (v != null) {
				v.setFitsSystemWindows(true);
			}
//			View v2 = findViewById(R.id.umeng_comm_main_container);
//			if (v2 != null) {
//				v2.setFitsSystemWindows(true);
//			}
//			View v3 = findViewById(R.id.umeng_comm_feed_detail_root);
//			if (v3 != null) {
//				v3.setFitsSystemWindows(true);
//			}
		}
		

		if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) { 
            Window window = getWindow();  
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );  
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION  
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);  
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  
            window.setStatusBarColor(Color.TRANSPARENT);  
//            window.setNavigationBarColor(Color.TRANSPARENT);  
        }  
	}
}
