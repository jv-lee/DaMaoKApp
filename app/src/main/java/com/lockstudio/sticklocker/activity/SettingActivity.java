package com.lockstudio.sticklocker.activity;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.toolbox.ClearCacheRequest;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.base.BaseDialog.OnDismissedListener;
import com.lockstudio.sticklocker.service.CoreService;
import com.lockstudio.sticklocker.service.SystemNotificationListener;
import com.lockstudio.sticklocker.service.SystemNotificationService;
import com.lockstudio.sticklocker.util.AppUpdate;
import com.lockstudio.sticklocker.util.BannerUtils;
import com.lockstudio.sticklocker.util.CommonUtil;
import com.lockstudio.sticklocker.util.DeviceUtils;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.view.ChooseLockSoundDialog;
import com.lockstudio.sticklocker.view.ChooseUnlockSoundDialog;
import com.lockstudio.sticklocker.view.SimpleToast;

import org.json.JSONException;

import java.util.List;

import cn.opda.android.activity.R;

public class SettingActivity extends BaseActivity implements OnClickListener {

	private static final int MSG_CLEAR_DONE = 40;

	private CheckBox enable_checkbox;
	private CheckBox vibrate_checkbox;
	private CheckBox statusbar_checkbox;
	private CheckBox notification_checkbox;
	private CheckBox accessibility_checkbox;
	private CheckBox showphonesms_checkbox;
//	private CheckBox usage_checkbox;
	private TextView lock_sound_summary_textview, unlock_sound_summary_textview;
	private boolean shortcutCreate;
	private UsageStatsManager usageStatsManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			usageStatsManager = (UsageStatsManager) getSystemService("usagestats");
		}

		BannerUtils.setBannerTitle(this, R.drawable.title_settings);
		initViewAndEvent();
	}

	private void initViewAndEvent() {
		enable_checkbox = (CheckBox) findViewById(R.id.setting_checkbox_enable);
		vibrate_checkbox = (CheckBox) findViewById(R.id.setting_checkbox_vibrate);
		statusbar_checkbox = (CheckBox) findViewById(R.id.setting_checkbox_statusbar);
		notification_checkbox = (CheckBox) findViewById(R.id.setting_checkbox_notification);
		accessibility_checkbox = (CheckBox) findViewById(R.id.setting_checkbox_accessibility);
		showphonesms_checkbox = (CheckBox) findViewById(R.id.setting_checkbox_showphonesms);
//		usage_checkbox = (CheckBox) findViewById(R.id.setting_checkbox_usage);

		View disable_system_screenlock = findViewById(R.id.setting_disable_system_screenlock);
		View enable_auto_startup = findViewById(R.id.setting_enable_auto_startup);
		View enable_window_manager = findViewById(R.id.setting_enable_window_manager);

		if (DeviceUtils.isMeiZu()) {
			if (Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT_WATCH) {
				enable_window_manager.setVisibility(View.GONE);
			}
			disable_system_screenlock.setVisibility(View.GONE);
		} else if (DeviceUtils.isMIUI()) {
			try {
				if (DeviceUtils.ROM_MIUI_V6.equals(DeviceUtils.getDeviceProp("ro.miui.ui.version.name"))
						|| DeviceUtils.ROM_MIUI_V6.equals(DeviceUtils.getDeviceProp("ro.miui.ui.version.name"))) {
					TextView tv = (TextView) findViewById(R.id.setting_enable_auto_startup_text);
					tv.setText(R.string.setting_text_guide_startself_v6);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (DeviceUtils.isHuawei()) {
			if (!DeviceUtils.emuiVersion3()) {
				enable_window_manager.setVisibility(View.GONE);
			}
		} else if (DeviceUtils.isOPPO()) {
			if (Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN_MR1) {
				enable_window_manager.setVisibility(View.GONE);
			}
			try {
				String colorOsV=DeviceUtils.getDeviceProp("ro.build.version.opporom");
				colorOsV=colorOsV.substring(0, 4);
				if(!colorOsV.equals("V2.0")){
					enable_window_manager.setVisibility(View.GONE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			enable_auto_startup.setVisibility(View.GONE);
			enable_window_manager.setVisibility(View.GONE);
		}

		enable_checkbox.setChecked(LockApplication.getInstance().getConfig().isEnabled());
		vibrate_checkbox.setChecked(LockApplication.getInstance().getConfig().isVibrate());
		statusbar_checkbox.setChecked(LockApplication.getInstance().getConfig().isShowStatusBar());
		showphonesms_checkbox.setChecked(LockApplication.getInstance().getConfig().isShowPhoneSms());

		TextView setting_version = (TextView) findViewById(R.id.setting_version);
		setting_version.setText(getAppVersion(mContext));

		View enable_notification = findViewById(R.id.setting_toggle_notification);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			enable_notification.setVisibility(View.VISIBLE);
			enable_notification.setOnClickListener(this);
		} else {
			enable_notification.setVisibility(View.GONE);
		}

//		LinearLayout guide_rl_usage = (LinearLayout) findViewById(R.id.setting_toggle_usage);
//		guide_rl_usage.setOnClickListener(this);
//		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//			guide_rl_usage.setVisibility(View.VISIBLE);
//		}else{
//			guide_rl_usage.setVisibility(View.GONE);
//		}

		findViewById(R.id.setting_toggle_enable).setOnClickListener(this);
		findViewById(R.id.setting_toggle_unlock_sound).setOnClickListener(this);
		findViewById(R.id.setting_toggle_lock_sound).setOnClickListener(this);
		findViewById(R.id.setting_toggle_vibrate).setOnClickListener(this);
		findViewById(R.id.setting_toggle_statusbar).setOnClickListener(this);
		findViewById(R.id.setting_toggle_accessibility).setOnClickListener(this);
		findViewById(R.id.setting_toggle_showphonesms).setOnClickListener(this);

		findViewById(R.id.setting_clear).setOnClickListener(this);
		findViewById(R.id.setting_rate_us).setOnClickListener(this);
		findViewById(R.id.setting_check_update).setOnClickListener(this);
		findViewById(R.id.setting_create_lock_shortcut).setOnClickListener(this);

		disable_system_screenlock.setOnClickListener(this);
		enable_auto_startup.setOnClickListener(this);
		enable_window_manager.setOnClickListener(this);

		lock_sound_summary_textview = (TextView) findViewById(R.id.lock_sound_summary_textview);
		unlock_sound_summary_textview = (TextView) findViewById(R.id.unlock_sound_summary_textview);
		updateSummary();
	}

	private boolean usageSwitchOpen(){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
	        long time = System.currentTimeMillis();
			List<UsageStats> appList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*1000, time);
			if(appList!=null&&appList.size()>0){
				return true;
			}
		}
		return false;
	}

	private void updateSummary() {
		if (LockApplication.getInstance().getConfig().isPlaySound()) {
			int soundId = LockApplication.getInstance().getConfig().getUnlockSoundId();

			if (soundId == 0) {
				unlock_sound_summary_textview.setText("跟随系统");

			} else if (soundId == R.raw.unlock_sound1) {
				unlock_sound_summary_textview.setText("解锁声音一");

			} else if (soundId == R.raw.unlock_sound2) {
				unlock_sound_summary_textview.setText("解锁声音二");

			} else if (soundId == R.raw.unlock_shuidi1) {
				unlock_sound_summary_textview.setText("水滴声一");

			} else if (soundId == R.raw.unlock_shuidi2) {
				unlock_sound_summary_textview.setText("水滴声二");

			} else if (soundId == R.raw.unlock_shuidi3) {
				unlock_sound_summary_textview.setText("水滴声三");

			} else if (soundId == R.raw.unlock_shuidi4) {
				unlock_sound_summary_textview.setText("水滴声四");

			} else if (soundId == R.raw.unlock_shuidi5) {
				unlock_sound_summary_textview.setText("水滴声五");

			} else if (soundId == R.raw.unlock_boli) {
				unlock_sound_summary_textview.setText("打碎玻璃");

			} else if (soundId == R.raw.unlock_baojian) {
				unlock_sound_summary_textview.setText("宝剑出鞘");

			} else if (soundId == R.raw.unlock_dingdong) {
				unlock_sound_summary_textview.setText("叮咚");

			} else if (soundId == R.raw.unlock_kaimen) {
				unlock_sound_summary_textview.setText("开门声");

			} else if (soundId == R.raw.unlock_windows) {
				unlock_sound_summary_textview.setText("windows解锁声音");

			} else if (soundId == R.raw.unlock_duola) {
				unlock_sound_summary_textview.setText("哆啦A梦解锁声音");

			} else if (soundId == R.raw.unlock_fangpi) {
				unlock_sound_summary_textview.setText("放屁解锁声音");

			} else if (soundId == R.raw.unlock_riyu) {
				unlock_sound_summary_textview.setText("日语解锁声音");

			} else {
			}

		} else {
			unlock_sound_summary_textview.setText("无");
		}

		if (LockApplication.getInstance().getConfig().isPlayLockSound()) {
			int soundId = LockApplication.getInstance().getConfig().getLockSoundId();

			if (soundId == 0) {
				lock_sound_summary_textview.setText("跟随系统");

			} else if (soundId == R.raw.lock_1) {
				lock_sound_summary_textview.setText("锁屏声音一");

			} else if (soundId == R.raw.lock_2) {
				lock_sound_summary_textview.setText("锁屏声音二");

			} else if (soundId == R.raw.lock_3) {
				lock_sound_summary_textview.setText("锁屏声音三");

			} else if (soundId == R.raw.lock_4) {
				lock_sound_summary_textview.setText("锁屏声音四");

			} else if (soundId == R.raw.lock_5) {
				lock_sound_summary_textview.setText("锁屏声音五");

			} else if (soundId == R.raw.lock_6) {
				lock_sound_summary_textview.setText("锁屏声音六");

			} else if (soundId == R.raw.lock_7) {
				lock_sound_summary_textview.setText("锁屏声音七");

			} else if (soundId == R.raw.lock_iphone) {
				lock_sound_summary_textview.setText("iPhone锁屏声");

			} else if (soundId == R.raw.lock_windows) {
				lock_sound_summary_textview.setText("windows锁屏声音");

			} else if (soundId == R.raw.lock_fangpi) {
				lock_sound_summary_textview.setText("放屁锁屏声音");

			} else if (soundId == R.raw.lock_riyu) {
				lock_sound_summary_textview.setText("日语锁屏声音");

			} else {
			}

		} else {
			lock_sound_summary_textview.setText("无");
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(mContext, MiuiDetailsActivity.class);
		int i = v.getId();
		if (i == R.id.setting_rate_us) {
			try {
				Uri uri = Uri.parse("market://details?id=" + getPackageName());
				Intent shareIntent = new Intent(Intent.ACTION_VIEW, uri);
				shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.getApplicationContext().startActivity(shareIntent);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (i == R.id.setting_check_update) {
			new AppUpdate(this).checkUpdate(false);

		} else if (i == R.id.setting_create_lock_shortcut) {
			if (!shortcutCreate) {
				shortcutCreate = true;
				CommonUtil.addShortcut(mContext, getString(R.string.lock_title), R.drawable.shortcut_lock, getPackageName(), LockScreenActivity.class.getName());
				SimpleToast.makeText(mContext, R.string.shortcut_create_succsed, SimpleToast.LENGTH_SHORT).show();
			} else {
				SimpleToast.makeText(mContext, R.string.shortcut_exist, SimpleToast.LENGTH_SHORT).show();
			}

		} else if (i == R.id.setting_disable_system_screenlock) {
			if (DeviceUtils.isMIUI()) {
				try {
					DeviceUtils.setBootStart(mContext);
					intent.putExtra("flag", 3);
					startActivity(intent);
					LockApplication.getInstance().getConfig().setGuide_setting_turn_off_system_screen_lock_done(true);
				} catch (Exception e) {
					SimpleToast.makeText(getApplicationContext(), R.string.guide_failed, SimpleToast.LENGTH_SHORT).show();
				}
			} else {
				try {
					DeviceUtils.setBootStartDefault(mContext);
					intent.putExtra("flag", 3);
					startActivity(intent);
					LockApplication.getInstance().getConfig().setGuide_setting_turn_off_system_screen_lock_done(true);
					return;
				} catch (Exception e1) {
					SimpleToast.makeText(getApplicationContext(), R.string.guide_failed, SimpleToast.LENGTH_SHORT).show();
				}
			}

		} else if (i == R.id.setting_enable_auto_startup) {
			try {
				if (DeviceUtils.isMIUI()) {
					if (DeviceUtils.ROM_MIUI_V6.equals(DeviceUtils.getDeviceProp("ro.miui.ui.version.name"))
							|| DeviceUtils.ROM_MIUI_V7.equals(DeviceUtils.getDeviceProp("ro.miui.ui.version.name"))) {
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
				} else if (DeviceUtils.isMeiZu() && Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
					DeviceUtils.openAppManagement(mContext);
					intent.putExtra("flag", 2);
					startActivity(intent);
				}
				LockApplication.getInstance().getConfig().setGuide_setting_auto_start_done(true);
			} catch (Exception e) {
				SimpleToast.makeText(getApplicationContext(), R.string.guide_failed, SimpleToast.LENGTH_SHORT).show();
			}

		} else if (i == R.id.setting_enable_window_manager) {
			try {
				if (DeviceUtils.isMIUI()) {
					DeviceUtils.openMiuiPermissionActivity(mContext);
					intent.putExtra("flag", 1);
					startActivity(intent);
				} else if (DeviceUtils.isHuawei()) {
					DeviceUtils.offWindow_huawei(mContext);
					intent.putExtra("flag", 1);
					startActivity(intent);
				} else if (DeviceUtils.isOPPO()) {
					DeviceUtils.openWindow_oppo(mContext);
					intent.putExtra("flag", 1);
					startActivity(intent);
				} else if (DeviceUtils.isMeiZu()) {
					DeviceUtils.openAppManagement(mContext);
					intent.putExtra("flag", 2);
					startActivity(intent);
				}
				LockApplication.getInstance().getConfig().setGuide_setting_turn_on_window_manager_done(true);
			} catch (Exception e2) {
				e2.printStackTrace();
			}

//		case R.id.setting_toggle_usage:
//			try {
//				startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//				intent.putExtra("flag", 9);
//				startActivity(intent);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			break;
		} else if (i == R.id.setting_clear) {
			ClearCacheRequest clearCacheRequest = new ClearCacheRequest(VolleyUtil.instance().getCache(), new Runnable() {
				@Override
				public void run() {
					handler.sendEmptyMessage(MSG_CLEAR_DONE);
				}
			});
			VolleyUtil.instance().addRequest(clearCacheRequest);

		} else if (i == R.id.setting_toggle_notification) {
			try {
				startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (i == R.id.setting_toggle_accessibility) {
			try {
				startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
				intent.putExtra("flag", 4);
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (i == R.id.setting_toggle_enable) {
			if (!LockApplication.getInstance().getConfig().isEnabled()) {
				int screen_off_timeout = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
				if (screen_off_timeout <= 0 || screen_off_timeout == MConstants.MIN_SCREEN_OFF_TIMEOUT) {
					screen_off_timeout = MConstants.DEFAULT_SCREEN_OFF_TIMEOUT;
				}
				LockApplication.getInstance().getConfig().setScreenOffTimeout(screen_off_timeout);
				mContext.startService(new Intent(mContext, CoreService.class));
			}
			LockApplication.getInstance().getConfig().setEnabled(!LockApplication.getInstance().getConfig().isEnabled());
			LockApplication.getInstance().setPushTags();
			enable_checkbox.toggle();

		} else if (i == R.id.setting_toggle_unlock_sound) {
			ChooseUnlockSoundDialog chooseSoundDialog = new ChooseUnlockSoundDialog(mContext);
			chooseSoundDialog.setOnDismissedListener(new OnDismissedListener() {
				@Override
				public void OnDialogDismissed() {
					updateSummary();
				}
			});
			chooseSoundDialog.show();

		} else if (i == R.id.setting_toggle_lock_sound) {
			ChooseLockSoundDialog chooseLockSoundDialog = new ChooseLockSoundDialog(mContext);
			chooseLockSoundDialog.setOnDismissedListener(new OnDismissedListener() {
				@Override
				public void OnDialogDismissed() {
					updateSummary();
				}
			});
			chooseLockSoundDialog.show();

		} else if (i == R.id.setting_toggle_vibrate) {
			LockApplication.getInstance().getConfig().setVibrate(!LockApplication.getInstance().getConfig().isVibrate());
			vibrate_checkbox.toggle();

		} else if (i == R.id.setting_toggle_statusbar) {
			LockApplication.getInstance().getConfig().setShowStatusBar(!LockApplication.getInstance().getConfig().isShowStatusBar());
			statusbar_checkbox.toggle();

		} else if (i == R.id.setting_toggle_showphonesms) {
			LockApplication.getInstance().getConfig().setShowPhoneSms(!LockApplication.getInstance().getConfig().isShowPhoneSms());
			showphonesms_checkbox.toggle();

		} else {
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			if (notification_checkbox != null) {
				notification_checkbox.setChecked(SystemNotificationListener.isEnabled(this));
			}
		}
		
//		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//			if(usageSwitchOpen()){
//				usage_checkbox.setChecked(true);
//			}else{
//				usage_checkbox.setChecked(false);
//			}
//		}

		if (accessibility_checkbox != null) {
			if(SystemNotificationService.isAccessibilitySettingsOn(this)){
				accessibility_checkbox.setChecked(true);
				LockApplication.getInstance().getConfig().setGuide_setting_watch_service_done(true);
			}
			if(LockApplication.getInstance().getConfig().isGuide_setting_watch_service_done()){
				accessibility_checkbox.setChecked(true);
			}
			//accessibility_checkbox.setChecked(SystemNotificationService.isAccessibilitySettingsOn(this));
		}
	}

	private String getAppVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(context.getPackageName(), 0);
			return "V" + packinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "V5.x.x";
		}
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			int what = msg.what;
			handler.removeMessages(what);
			switch (what) {
			case MSG_CLEAR_DONE:
				SimpleToast.makeText(mContext, R.string.clear_done, SimpleToast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
			return false;
		}
	});

}
