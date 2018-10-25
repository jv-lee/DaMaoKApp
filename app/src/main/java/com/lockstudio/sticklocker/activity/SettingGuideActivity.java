package com.lockstudio.sticklocker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.util.DeviceUtils;
import com.lockstudio.sticklocker.view.SimpleToast;

import cn.opda.android.activity.R;

public class SettingGuideActivity extends BaseActivity implements OnClickListener {

	private ImageView guide_circle_start;
	private ImageView guide_circle_system;
	private ImageView guide_circle_window;
	private ImageView guide_circle_boot;
	private View buttom_finish;
	private SharedPreferences sp;
	private boolean systemOpen;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_guide);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			sp = getSharedPreferences("default.cfg", Context.MODE_MULTI_PROCESS);
		} else {
			sp = getSharedPreferences("default.cfg", Context.MODE_PRIVATE);
		}
		initDateAndEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (LockApplication.getInstance().getConfig().isGuide_setting_auto_start_done())
			guide_circle_start.setImageResource(R.drawable.guide_circle_finish);
		if (LockApplication.getInstance().getConfig().isGuide_setting_turn_off_system_screen_lock_done())
			guide_circle_system.setImageResource(R.drawable.guide_circle_finish);
		if (LockApplication.getInstance().getConfig().isGuide_setting_turn_on_window_manager_done())
			guide_circle_window.setImageResource(R.drawable.guide_circle_finish);
		try {
			if (DeviceUtils.emuiVersion3() || DeviceUtils.isMIUI()) {
				if (LockApplication.getInstance().getConfig().isGuide_setting_turn_on_window_manager_done() 
						&& LockApplication.getInstance().getConfig().isGuide_setting_auto_start_done()
						&& LockApplication.getInstance().getConfig().isGuide_setting_turn_off_system_screen_lock_done()
						&& systemOpen) {
					buttom_finish.setVisibility(View.VISIBLE);
					findViewById(R.id.guide_btn_unfinish).setVisibility(View.GONE);
				} else {
					buttom_finish.setVisibility(View.GONE);
					findViewById(R.id.guide_btn_unfinish).setVisibility(View.VISIBLE);
				}
			}else if(guide_circle_start.getVisibility()==View.VISIBLE 
					&& guide_circle_system.getVisibility()==View.VISIBLE 
					&& guide_circle_window.getVisibility()==View.VISIBLE){
				if (LockApplication.getInstance().getConfig().isGuide_setting_turn_on_window_manager_done() 
						&& LockApplication.getInstance().getConfig().isGuide_setting_auto_start_done()
						&& LockApplication.getInstance().getConfig().isGuide_setting_turn_off_system_screen_lock_done()
						&& systemOpen) {
					buttom_finish.setVisibility(View.VISIBLE);
					findViewById(R.id.guide_btn_unfinish).setVisibility(View.GONE);
				} else {
					buttom_finish.setVisibility(View.GONE);
					findViewById(R.id.guide_btn_unfinish).setVisibility(View.VISIBLE);
				}
			}else if(guide_circle_start.getVisibility()==View.VISIBLE 
					&& guide_circle_system.getVisibility()==View.VISIBLE){
				if (LockApplication.getInstance().getConfig().isGuide_setting_turn_off_system_screen_lock_done() 
						&& LockApplication.getInstance().getConfig().isGuide_setting_auto_start_done()
						&& systemOpen) {
					buttom_finish.setVisibility(View.VISIBLE);
					findViewById(R.id.guide_btn_unfinish).setVisibility(View.GONE);
				} else {
					buttom_finish.setVisibility(View.GONE);
					findViewById(R.id.guide_btn_unfinish).setVisibility(View.VISIBLE);
				}
			}else if(guide_circle_start.getVisibility()==View.VISIBLE 
					&& guide_circle_window.getVisibility()==View.VISIBLE){
				if (LockApplication.getInstance().getConfig().isGuide_setting_turn_on_window_manager_done()
						&& LockApplication.getInstance().getConfig().isGuide_setting_auto_start_done()
						&& systemOpen) {
					buttom_finish.setVisibility(View.VISIBLE);
					findViewById(R.id.guide_btn_unfinish).setVisibility(View.GONE);
				} else {
					buttom_finish.setVisibility(View.GONE);
					findViewById(R.id.guide_btn_unfinish).setVisibility(View.VISIBLE);
				}
			}else if(guide_circle_system.getVisibility()==View.VISIBLE){
				if(LockApplication.getInstance().getConfig().isGuide_setting_turn_off_system_screen_lock_done()
						&& systemOpen){
					buttom_finish.setVisibility(View.VISIBLE);
					findViewById(R.id.guide_btn_unfinish).setVisibility(View.GONE);
				}else{
					buttom_finish.setVisibility(View.GONE);
					findViewById(R.id.guide_btn_unfinish).setVisibility(View.VISIBLE);
				}
			}else{
				if(systemOpen){
					buttom_finish.setVisibility(View.VISIBLE);
					findViewById(R.id.guide_btn_unfinish).setVisibility(View.GONE);
				}else{
					buttom_finish.setVisibility(View.GONE);
					findViewById(R.id.guide_btn_unfinish).setVisibility(View.VISIBLE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (systemOpen) {
			guide_circle_boot.setImageResource(R.drawable.guide_circle_finish);
		}else{
			guide_circle_boot.setImageResource(R.drawable.guide_circle_unfinish);
		}
//		if (SystemNotificationService.isAccessibilitySettingsOn(this)) {
//			guide_circle_boot.setImageResource(R.drawable.guide_circle_finish);
//		}else{
//			guide_circle_boot.setImageResource(R.drawable.guide_circle_unfinish);
//		}
	}


	private void initDateAndEvent() {
		buttom_finish = findViewById(R.id.guide_btn_finish);
		buttom_finish.setOnClickListener(this);
		// if(getIntent().getBooleanExtra("hide_finish", false))
		// buttom_finish.setVisibility(View.GONE);
		RelativeLayout guide_rl_start = (RelativeLayout) findViewById(R.id.guide_rl_start);
		RelativeLayout guide_rl_system = (RelativeLayout) findViewById(R.id.guide_rl_system);
		RelativeLayout guide_rl_window = (RelativeLayout) findViewById(R.id.guide_rl_window);
		RelativeLayout guide_rl_boot = (RelativeLayout) findViewById(R.id.guide_rl_boot);
		LinearLayout guide_boot_layout = (LinearLayout) findViewById(R.id.guide_boot_layout);
		guide_circle_start = (ImageView) findViewById(R.id.guide_circle_start);
		guide_circle_system = (ImageView) findViewById(R.id.guide_circle_system);
		guide_circle_window = (ImageView) findViewById(R.id.guide_circle_window);
		guide_circle_boot = (ImageView) findViewById(R.id.guide_circle_boot);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			guide_boot_layout.setVisibility(View.VISIBLE);
		} else {
			guide_boot_layout.setVisibility(View.GONE);
		}

		if (DeviceUtils.isMIUI()) {
			guide_circle_start.setVisibility(View.VISIBLE);
			guide_circle_window.setVisibility(View.VISIBLE);
			guide_rl_start.setVisibility(View.VISIBLE);
			guide_rl_window.setVisibility(View.VISIBLE);
		} else if (DeviceUtils.isHuawei()) {
			if (!DeviceUtils.emuiVersion3()) {
				guide_circle_window.setVisibility(View.GONE);
				guide_rl_window.setVisibility(View.GONE);
				guide_circle_start.setVisibility(View.VISIBLE);
				guide_rl_start.setVisibility(View.VISIBLE);
			} else {
				guide_circle_start.setVisibility(View.VISIBLE);
				guide_circle_window.setVisibility(View.VISIBLE);
				guide_rl_start.setVisibility(View.VISIBLE);
				guide_rl_window.setVisibility(View.VISIBLE);
			}
		} else if (DeviceUtils.isMeiZu()) {
			guide_circle_system.setVisibility(View.GONE);
			guide_circle_start.setVisibility(View.GONE);
			guide_circle_window.setVisibility(View.GONE);
			guide_rl_start.setVisibility(View.GONE);
			guide_rl_window.setVisibility(View.GONE);
			guide_rl_system.setVisibility(View.GONE);
		} else {
			guide_circle_start.setVisibility(View.GONE);
			guide_circle_window.setVisibility(View.GONE);
			guide_rl_start.setVisibility(View.GONE);
			guide_rl_window.setVisibility(View.GONE);
		}

		guide_rl_start.setOnClickListener(this);
		guide_rl_system.setOnClickListener(this);
		guide_rl_window.setOnClickListener(this);
		guide_rl_boot.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(mContext, MiuiDetailsActivity.class);
		int i = v.getId();
		if (i == R.id.guide_rl_start) {
			try {
				if (DeviceUtils.isMIUI()) {
					if (DeviceUtils.ROM_MIUI_V6.equals(DeviceUtils.getDeviceProp("ro.miui.ui.version.name"))) {
						DeviceUtils.setBootStartV6(mContext);
					} else {
						DeviceUtils.gotoMiuiDetail(mContext);
					}
					intent.putExtra("flag", 2);
					startActivity(intent);
				} else if (DeviceUtils.isHuawei()) {
					DeviceUtils.openSystemStart_huawei(mContext);
					intent.putExtra("flag", 2);
					startActivity(intent);
				}
				LockApplication.getInstance().getConfig().setGuide_setting_auto_start_done(true);
			} catch (Exception e) {
				SimpleToast.makeText(getApplicationContext(), R.string.guide_failed, SimpleToast.LENGTH_SHORT).show();
			}

		} else if (i == R.id.guide_rl_window) {
			try {
				if (DeviceUtils.isMIUI()) {
					DeviceUtils.openMiuiPermissionActivity(mContext);
					intent.putExtra("flag", 1);
					startActivity(intent);
				} else if (DeviceUtils.isHuawei()) {
					DeviceUtils.offWindow_huawei(mContext);
					intent.putExtra("flag", 1);
					startActivity(intent);
				}
				LockApplication.getInstance().getConfig().setGuide_setting_turn_on_window_manager_done(true);
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		} else if (i == R.id.guide_rl_system) {
			if (DeviceUtils.isMIUI()) {
				try {
					DeviceUtils.setBootStart(mContext);
					intent.putExtra("flag", 3);
					startActivity(intent);
					LockApplication.getInstance().getConfig().setGuide_setting_turn_off_system_screen_lock_done(true);
				} catch (Exception e) {
					Toast toast = Toast.makeText(getApplicationContext(), R.string.guide_failed, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			} else {
				try {
					DeviceUtils.setBootStartDefault(mContext);
					intent.putExtra("flag", 3);
					startActivity(intent);
					LockApplication.getInstance().getConfig().setGuide_setting_turn_off_system_screen_lock_done(true);
					return;
				} catch (Exception e1) {
					Toast toast = Toast.makeText(getApplicationContext(), R.string.guide_failed, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}

		} else if (i == R.id.guide_rl_boot) {
			try {
				startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
				intent.putExtra("flag", 4);
				startActivity(intent);
				systemOpen = true;
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (i == R.id.guide_btn_finish) {
			int versionCode = 0;
			try {
				PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
				versionCode = pi.versionCode;
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			LockApplication.getInstance().getConfig().setLastGuideVersion(versionCode);
			finish();
//			if(!sp.getBoolean("mainGuide", false)){
//				intent.putExtra("flag", 6);
//				startActivity(intent);
//				sp.edit().putBoolean("mainGuide", true).commit();
//			}

		} else {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (DeviceUtils.isMIUI() || DeviceUtils.emuiVersion3()) {
				if (LockApplication.getInstance().getConfig().isGuide_setting_turn_on_window_manager_done()) {
					return true;
				} else {
					return false;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
