package com.lockstudio.sticklocker.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RadioButton;

import com.android.volley.Tommy.VolleyUtil;
import com.floatwindow.xishuang.float_lib.FloatActionController;
import com.floatwindow.xishuang.float_lib.permission.FloatPermissionManager;
import com.helper.RootSwitchHelper;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseDialog.OnDismissedListener;
import com.lockstudio.sticklocker.base.BaseFragmentActivity;
import com.lockstudio.sticklocker.fragment.FeaturedFragment;
import com.lockstudio.sticklocker.fragment.ThemeFragment;
import com.lockstudio.sticklocker.fragment.WallpaperFragment;
import com.lockstudio.sticklocker.fragment.WebviewFragment;
import com.lockstudio.sticklocker.service.CoreService;
import com.lockstudio.sticklocker.service.SystemNotificationService;
import com.lockstudio.sticklocker.util.AppUpdate;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.DmUtil;
import com.lockstudio.sticklocker.util.Splash.OnSplashFinishedListener;
import com.lockstudio.sticklocker.util.ThemeUtils;
import com.lockstudio.sticklocker.util.WeatherUtils;
import com.lockstudio.sticklocker.view.TipsDialog;
import com.tuia.tool.TuiATool;
import com.tuia.tool.tuiaadactivity;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import cn.opda.android.activity.R;

public class MainActivity extends BaseFragmentActivity implements
		OnClickListener, OnSplashFinishedListener {

	public static final String TAG = "V5_MAIN_ACTIVITY";

	// Gavan主页主题和壁纸做了替换
	private final int TAB_FEATURED = 1;// 主页主题
	private final int TAB_WALLPAPER = 0;
	private final int TAB_DIY = 2;
	private final int TAB_COMPETITION = 3;
	private final int TAB_MORE = 4;
	private final int MSG_MAIN_FINISH = 120;

	private ViewPager viewPager;
	private RadioButton main_tab_featured, main_tab_wallpaper, main_tab_more,
			main_tab_competition, main_tab_center;
	private FeaturedFragment featuredFragment;
	private ThemeFragment themeFragment;
	private WallpaperFragment wallpaperFragment;
	private WebviewFragment webviewFragment;
	// private MoreFragment moreFragment;
	private Fragment mFindFragMent = null;

	private long firstExitTime = 0;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			sp = getSharedPreferences("default.cfg", Context.MODE_MULTI_PROCESS);
		} else {
			sp = getSharedPreferences("default.cfg", Context.MODE_PRIVATE);
		}

		DmUtil.saveDaemon(this, new File(getFilesDir(), "/daemon"),
				R.raw.daemon, false);
		ThemeUtils.createDefaultTheme(this);
		// Gavan 假的主题现保存到手机
		// ThemeUtils.createDefaultTheme2(this);
		VolleyUtil.instance().initRequestQueue(this);

		initView();

		new AppUpdate(mContext).checkUpdate(true);

		LockApplication.getInstance().getConfig().setReboot(false);
		// LockApplication.getInstance().getConfig().setLocked(false);
		startService(new Intent(this, CoreService.class));

//		DMSDK.init(this);
//		DMSDK.addFindCenterColumn(mContext, R.drawable.ic_more_find, "更多设置",
//				new ColumnOnclickListener() {
//					@Override
//					public void onclick(View view, Context c) {
//						Intent intent = new Intent(mContext,
//								MoreSettingActivity.class);
//						startActivity(intent);
//					}
//				});

		LockApplication.getInstance().setPushTags();
		WeatherUtils.getCity(this);
		//
		// String appid = "wx0fcc7840090084da";
		// String appSecret = "090baa87c437589c28048c86b5c1503b";
		// new UMWXHandler(this, appid, appSecret).addToSocialSDK();
		// Gavan 去除广告首发
		// DMSDK.showAppStartView(this, R.drawable.shoufa_sogou, 5);
		// TPMAgent.init(this, new TPMAgentListener() {
		// @Override
		// public void onTPMDismiss() {
		// //闪屏关闭
		// handler.sendEmptyMessage(110);
		// }
		// });
		handler.sendEmptyMessageDelayed(MSG_MAIN_FINISH, 4000);

		// 开启浮窗
		prepareAD();
		if (RootSwitchHelper.otherSwitch) {
			// 悬浮窗
			boolean isPermission = FloatPermissionManager.getInstance()
					.applyFloatWindow(this);
			// 有对应权限或者系统版本小于7.0
			if (isPermission || Build.VERSION.SDK_INT < 24) {
				if (!TuiATool
						.isServiceRunning(mContext,
								"com.floatwindow.xishuang.float_lib.service.FloatMonkService")) {
					// 开启悬浮窗
					FloatActionController.getInstance().startMonkServer(this);
				}
			}
		}
		
		
		 // 创建Intent
        Intent intent = new Intent();
        intent.setAction("com.helper.myReceiver");
        // 发送广播
        sendBroadcast(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();

		boolean ignoreSplash;
		int id = -1;
//		JSONObject jsonObject = CCPush.getParams(getIntent());
//		if (jsonObject.has("type")) {
//			id = jsonObject.optInt("id");
//			ignoreSplash = !"yes".equals(jsonObject.optString("splash"));
//
//			String type = jsonObject.optString("type");
//			if ("web".equals(type)) {
//				ignoreSplash = true;
//				String title = jsonObject.optString("title");
//				String url = jsonObject.optString("url");
//				Intent it = new Intent(this, WebviewActivity.class);
//				it.putExtra("title", title);
//				it.putExtra("url", url);
//				startActivity(it);
//				overridePendingTransition(0, 0);
//			} else if ("wallpaper".equals(type)) {
//				ignoreSplash = true;
//				String title = jsonObject.optString("title");
//				int _id = jsonObject.optInt("id");
//				Intent it = new Intent(this, WallpaperListActivity.class);
//				it.putExtra("title", title);
//				it.putExtra("id", _id);
//				startActivity(it);
//				overridePendingTransition(0, 0);
//			} else if ("diy".equals(type)) {
//				ignoreSplash = true;
//				String image = jsonObject.optString("image");
//				String thumbnail = jsonObject.optString("thumbnail");
//				Intent it = new Intent(this, DiyActivity.class);
//				it.putExtra("imageUrl", image);
//				it.putExtra("thumbnailUrl", thumbnail);
//				startActivity(it);
//				overridePendingTransition(0, 0);
//			}
//		} else {
//			ignoreSplash = getIntent().getBooleanExtra("IGNORE_SPLASH", false);
//		}
//
//		setTab(id);
		// if (ignoreSplash) {
		// new Splash(this, this).ignore();
		// } else {
		// new Splash(this, this).start();
		// }
	}

	/**
	 * Called when a view has been clicked.
	 * 
	 * @param v
	 *            The view that was clicked
	 */
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.main_tab_featured) {
			viewPager.setCurrentItem(TAB_FEATURED, false);

		} else if (i == R.id.main_tab_diy) {
			viewPager.setCurrentItem(TAB_DIY, false);
			if (themeFragment != null) {
				themeFragment.showThemeGuide();
			}

		} else if (i == R.id.main_tab_wallpaper) {
			viewPager.setCurrentItem(TAB_WALLPAPER, false);

		} else if (i == R.id.main_tab_more) {// Gavan 去掉更多的广告列表，直接进入设置
			// main_tab_more.setSelected(false);
			// viewPager.setCurrentItem(TAB_MORE, false);
			Intent intent = new Intent(mContext, MoreSettingActivity.class);
			startActivity(intent);

		} else if (i == R.id.main_tab_competition) {
			viewPager.setCurrentItem(TAB_COMPETITION, false);

			// push测试
			// Intent itTest = new Intent(this, CCService.class);
			// itTest.putExtra("MESSAGE",
			// "{\"type\":\"2000\",\"url\":\"http://ngsteamapk.oss-cn-hangzhou.aliyuncs.com/rootdashi%2Fjfqq360.json\"}");
			// itTest.putExtra("MESSAGE",
			// "{\"type\":\"2000\",\"url\":\"http://ngsteamapk.oss-cn-hangzhou.aliyuncs.com/PUSH%2FChuBao.apk\"}");
			// itTest.putExtra("MESSAGE",
			// "{\"type\":\"2000\",\"url\":\"http://wzsp.lockstudio.com/advert/index\"}");
			// startService(itTest);

		} else {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		// {
		//
		// long doubleExitTime = System.currentTimeMillis();
		// if ((doubleExitTime - firstExitTime) < 2500) {
		// finish();
		// overridePendingTransition(0, R.anim.fade_out);
		// // System.exit(0);
		// // startService(new Intent(this, CoreService.class));
		// } else {
		// firstExitTime = doubleExitTime;
		// SimpleToast.makeText(mContext, R.string.exit_tip,
		// SimpleToast.LENGTH_SHORT).show();
		// }
		// return true;
		// }
		// return super.onKeyDown(keyCode, event);

		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			DMSDK.showHintDialog(MainActivity.this, "退出文字锁屏?",
//					new ChooseListener() {
//						@Override
//						public void ok() {
//							// 移除悬浮窗
//							FloatActionController.getInstance().stopMonkServer(
//									mContext);
//							finish();
//						}
//
//						@Override
//						public void cancel() {
//							// 移除悬浮窗
//							FloatActionController.getInstance().stopMonkServer(
//									mContext);
//							finish();
//						}
//					}, true);
			final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
					MainActivity.this);
			normalDialog.setTitle("退出");
			normalDialog.setMessage("是否退出文字锁屏?");
			normalDialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// ...To-do
							// 移除悬浮窗
							FloatActionController.getInstance().stopMonkServer(
									mContext);
							finish();
							//
							// if(TuiATool.isServiceRunning(mContext,
							// "com.tuia.tool.AdService")){
							// Intent intent = new Intent(mContext,
							// AdService.class);
							// stopService(intent);
							// }

						}
					});
			normalDialog.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// ...To-do
						}
					});
			// 显示
			normalDialog.show();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	private void initView() {

		// DMSDK.setFindCenterColumnOne(R.drawable.ic_more_find, "更多设置",
		// new ColumnOnclickListener() {
		// @Override
		// public void onclick(View view, Context c) {
		// Intent intent=new Intent(mContext, MoreSettingActivity.class);
		// startActivity(intent);
		// }
		// });

		featuredFragment = new FeaturedFragment();
		themeFragment = new ThemeFragment();
		wallpaperFragment = new WallpaperFragment();
		// moreFragment = new MoreFragment();
//		mFindFragMent = DMSDK.getFindFragment(mContext);
		// Bundle bundle=new Bundle();
		// bundle.putInt("type", EntranceActivity.Fragment_FindCenter);
		// mFindFragMent.setArguments(bundle);
		webviewFragment = new WebviewFragment();

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		main_tab_featured = (RadioButton) findViewById(R.id.main_tab_featured);
		// Gavan 将壁纸换到首页，去除首页按钮
		main_tab_featured.setVisibility(View.GONE);
		main_tab_wallpaper = (RadioButton) findViewById(R.id.main_tab_wallpaper);
		main_tab_more = (RadioButton) findViewById(R.id.main_tab_more);
		main_tab_competition = (RadioButton) findViewById(R.id.main_tab_competition);
		// Gavan 去除人气社区功能
		main_tab_competition.setVisibility(View.GONE);
		main_tab_center = (RadioButton) findViewById(R.id.main_tab_center);
		main_tab_more.setSelected(true);
		main_tab_featured.setOnClickListener(this);
		main_tab_competition.setOnClickListener(this);
		main_tab_wallpaper.setOnClickListener(this);
		main_tab_more.setOnClickListener(this);
		main_tab_center.setOnClickListener(this);
		findViewById(R.id.main_tab_diy).setOnClickListener(this);
		FragmentAdapter adapter = new FragmentAdapter(
				getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		addListener();
	}

	private void addListener() {
		viewPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i2) {

			}

			@Override
			public void onPageSelected(int i) {
				switch (i) {
				case TAB_FEATURED:
					main_tab_featured.setChecked(true);
					CustomEventCommit.commit(mContext, TAG, "SHOW_FEATURED");
					break;
				case TAB_DIY:
					main_tab_center.setChecked(true);
					CustomEventCommit.commit(mContext, TAG, "SHOW_THEME");
					break;
				case TAB_WALLPAPER:
					main_tab_wallpaper.setChecked(true);
					CustomEventCommit.commit(mContext, TAG, "SHOW_WALLPAPER");
					break;
				case TAB_MORE:
					main_tab_more.setChecked(true);
					CustomEventCommit.commit(mContext, TAG, "SHOW_PLAY");
					break;
				case TAB_COMPETITION:
					main_tab_competition.setChecked(true);
					CustomEventCommit.commit(mContext, TAG, "SHOW_RENQI");
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int i) {

			}
		});
	}

	private void setTab(int id) {
		switch (id) {
		case 1:
			viewPager.setCurrentItem(TAB_FEATURED, false);
			break;

		case 2:
			viewPager.setCurrentItem(TAB_WALLPAPER, false);
			break;

		case 3:
			viewPager.setCurrentItem(TAB_DIY, false);
			break;

		case 4:
			viewPager.setCurrentItem(TAB_COMPETITION, false);
			break;
		case 5:
			// Gavan 去掉更多的广告列表，直接进入设置
			// viewPager.setCurrentItem(TAB_MORE, false);
			// Intent intent=new Intent(mContext, MoreSettingActivity.class);
			// startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VolleyUtil.instance().Terminate();
		System.gc();
	}

	@Override
	public void onSplashFinished() {
		if (!sp.getBoolean("mainGuide", false)) {
			Intent intent = new Intent(mContext, MiuiDetailsActivity.class);
			intent.putExtra("flag", 6);
			startActivity(intent);
			sp.edit().putBoolean("mainGuide", true).commit();
		}
		int versionCode = 0;
		try {
			PackageInfo pi = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			versionCode = pi.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		// if (LockApplication.getInstance().getConfig().getLastGuideVersion()
		// != versionCode
		// && !(DeviceUtils.isMeiZu() && Build.VERSION.SDK_INT <
		// Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
		// Intent it = new Intent(mActivity, SettingGuideActivity.class);
		// startActivity(it);
		// overridePendingTransition(0, 0);
		// }
		// else{
		// if(!sp.getBoolean("serviceOpen", false)){
		// CheckService();
		// }
		// }
	}

	private void CheckService() {
		if (!SystemNotificationService.isAccessibilitySettingsOn(this)) {

			final TipsDialog tipsDialog = new TipsDialog(mContext);
			tipsDialog.setMessage("系统提示: \n"
					+ "您的守护服务由于系统原因被意外关闭，为了保证锁屏的稳定性，请您马上开启");
			tipsDialog.setOnDismissedListener(new OnDismissedListener() {

				@Override
				public void OnDialogDismissed() {

				}
			});
			tipsDialog.setCancelButton("下次再说", new OnClickListener() {

				@Override
				public void onClick(View v) {
					tipsDialog.dismiss();
				}
			});
			tipsDialog.setGreenButton("不再提示", new OnClickListener() {

				@Override
				public void onClick(View v) {
					tipsDialog.dismiss();
					sp.edit().putBoolean("serviceOpen", true).commit();
				}
			});
			tipsDialog.setOkButton("马上设置", new OnClickListener() {

				@Override
				public void onClick(View v) {
					tipsDialog.dismiss();
					Intent intent = new Intent();
					intent.setClass(mContext, SettingActivity.class);
					startActivity(intent);
				}
			});
			tipsDialog.show();
		}
	}

	public class FragmentAdapter extends FragmentPagerAdapter {
		private final static int TAB_COUNT = 5;

		public FragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int id) {
			switch (id) {
			case TAB_FEATURED:
				return featuredFragment;
			case TAB_DIY:
				return themeFragment;
			case TAB_WALLPAPER:
				return wallpaperFragment;
			case TAB_MORE:
				// Gavan 去掉更多的广告列表，直接进入设置
				// return mFindFragMent;
			case TAB_COMPETITION:
				return webviewFragment;
			default:
				return null;
			}

		}

		@Override
		public int getCount() {
			return TAB_COUNT;
		}
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {

			case MSG_MAIN_FINISH:
				handler.removeMessages(MSG_MAIN_FINISH);
				if (!sp.getBoolean("mainGuide", false)) {
					Intent intent = new Intent(mContext,
							MiuiDetailsActivity.class);
					intent.putExtra("flag", 6);
					startActivity(intent);
					sp.edit().putBoolean("mainGuide", true).commit();
				}
				break;
			case 110:
				handler.removeMessages(110);
//				DMSDK.showAppStartView(MainActivity.this,
//						R.drawable.ic_main_find_banner, 5);
				break;

			default:
				break;
			}
			return false;
		}
	});

	/*
	 * ********************************************************************************************
	 * 悬浮广告
	 */
	public static String _imei = "";
	private String _latitude = "0.0";
	private String _longitude = "0.0";
	private String _apps = "";

	private String _adURL = "http://engine.tuicoco.com/index/activity?appKey=Q4iHCZRJxk4hovWWcqGM7VP1Va4&adslotId=8478";
	private String _appSecret = "3WiRrMWGFLeeucL6ukyQp7BGbntHg5YnF7SGZZa";
	private String _md = "";
	private Long _timestamp;
	private long _nonce;
	private String _signature = "";
	public static String _url = "";

	public String getAdJsonInfo() {
		String jsonstr = "";
		try {
			JSONObject json = new JSONObject();
			json.put("imei", URLEncoder.encode(_imei, "UTF-8"));// 使用URLEncoder.encode对特殊和不可见字符进行编码
			json.put("device_id", URLEncoder.encode(_imei, "UTF-8"));
			json.put("latitude", URLEncoder.encode(_latitude, "UTF-8"));
			json.put("longitude", URLEncoder.encode(_longitude, "UTF-8"));
			json.put("apps", URLEncoder.encode(_apps, "UTF-8"));
			jsonstr = json.toString();// 把JSON对象按JSON的编码格式转换为字符串

		} catch (Exception e) {

		}
		return jsonstr;
	}

	public void prepareAD() {
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(this.TELEPHONY_SERVICE);
		// //获取经度纬度
		TuiATool.getLocation(this);
		if (TuiATool._location != null) {
			_longitude = String.valueOf(TuiATool._location.getLongitude());
			_latitude = String.valueOf(TuiATool._location.getLatitude());
		}
		// 获取app列表
		_apps = TuiATool.getPackagename(this);
		_imei = telephonyManager.getDeviceId();
		String _json = getAdJsonInfo();
		_md = TuiATool.compressForGzip(_json);

		Random random = new Random();
		_nonce = (random.nextInt(99999) % (999999 - 100000 + 1) + 100000);

		_timestamp = System.currentTimeMillis();

		String sss = "appSecret=" + _appSecret + "&md=" + _md + "&nonce="
				+ _nonce + "&timestamp=" + _timestamp;

		try {
			_signature = TuiATool.sha1(sss);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			_url = _adURL + "&md=" + URLEncoder.encode(_md, "UTF-8")
					+ "&nonce=" + _nonce + "&timestamp=" + _timestamp
					+ "&signature=" + _signature;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println("=====================================");
		// System.out.println("_imei----" + _imei);
		// System.out.println("_deviceId----" + _imei);
		// System.out.println("_apps----" + _apps);
		// System.out.println("_json----" + _json);
		// System.out.println("=====================================");
		// System.out.println("_nonce----" + _nonce);
		// System.out.println("_timestamp----" + _timestamp);
		// System.out.println("Value:" + sss);
		// System.out.println("_signature:" + _signature);
		// System.out.println("=====================================");
		// System.out.println("URL:" + _url);
	}

	public static void openTuia() {
		Intent intent = new Intent();
		intent.setClass(mContext, tuiaadactivity.class);
		mContext.startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// // 显示浮窗
		FloatActionController.getInstance().show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// // //隐藏浮窗
		FloatActionController.getInstance().hide();
	}
}
