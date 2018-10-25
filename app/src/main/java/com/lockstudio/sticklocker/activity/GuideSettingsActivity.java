package com.lockstudio.sticklocker.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.util.AppManagerUtils;
import com.lockstudio.sticklocker.util.DeviceUtils;
import com.lockstudio.sticklocker.view.SimpleToast;

import org.json.JSONException;

import cn.opda.android.activity.R;

public class GuideSettingsActivity extends BaseActivity implements OnClickListener{

	String mtyb= Build.BRAND;//手机品牌
	private TextView guide_title,guide_title1,guide_title_page;
	private RelativeLayout guide_rl_start;
	private boolean guide_circle_start,guide_circle_system,guide_circle_window,guide_circle_boot,guide_circle_weishi,guide_circle_purebackground;
	private int pageNum,i=0,weishiNum;
	private ImageView guide_icon;
	private ComponentName componentName1;
	private ComponentName componentName2;
	private ComponentName componentName3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide_settings);
		if(mtyb.equals("Xiaomi")){
			mtyb="小米";
		}else if(mtyb.equals("huawei")){
			mtyb="华为";
		}
		isWeishi();
		initDateAndEvent();
	}

	private void isWeishi(){
		componentName1 = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
		componentName2 = new ComponentName("cn.opda.a.phonoalbumshoushou", "cn.com.opda.android.mainui.MainActivity");
		componentName3 = new ComponentName("com.tencent.qqpimsecure", "com.tencent.server.fore.QuickLoadActivity");
		if(AppManagerUtils.installed(mContext, componentName1)){
			guide_circle_weishi=true;
			weishiNum=1;
		}else if(AppManagerUtils.installed(mContext, componentName2)){
			guide_circle_weishi=true;
			weishiNum=2;
		}else if(AppManagerUtils.installed(mContext, componentName3)){
			guide_circle_weishi=true;
			weishiNum=3;
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (LockApplication.getInstance().getConfig().isGuide_setting_auto_start_done())
			guide_circle_start=false;
		if (LockApplication.getInstance().getConfig().isGuide_setting_turn_off_system_screen_lock_done())
			guide_circle_system=false;
		if (LockApplication.getInstance().getConfig().isGuide_setting_turn_on_window_manager_done())
			guide_circle_window=false;
		if (LockApplication.getInstance().getConfig().isGuide_setting_boot_protected_done())
			guide_circle_boot=false;
		if(guide_circle_start){
			guide_title.setText("文字君告诉你，设置开机自启动就可以保护你心爱的锁屏不消失哦，赶紧试试吧");
			guide_title1.setText("设置开机自启动");
		}else if(guide_circle_window){
			guide_title.setText("文字君发现你用的是"+mtyb+"手机哦，请打开悬浮窗以保证锁屏正常使用");
			guide_title1.setText("打开悬浮窗");
		}else if(guide_circle_system){
			guide_title.setText("文字君想跟你一直相处下去呢，关闭系统锁屏，能够保护文字君的安全~");
			guide_title1.setText("关闭系统自带锁屏");
		}else if(guide_circle_boot){
			guide_title.setText("开启守护服务的锁屏会异常强大，放心清理你的内存吧！");
			guide_title1.setText("开机守护服务");
		}else if(guide_circle_weishi){
			if(weishiNum==1){
				guide_title.setText("您的手机安装了[360手机卫士]，请进行设置\n保证锁屏稳定");
			}else if(weishiNum==2){
				guide_title.setText("您的手机安装了[百度卫士]，请进行设置\n保证锁屏稳定");
			}else if(weishiNum==3){
				guide_title.setText("您的手机安装了[腾讯手机管家]，请进行设置\n保证锁屏稳定");
			}
			guide_title1.setText("进行保护设置");
		}else if(guide_circle_purebackground){
			guide_title.setText("开启纯净后台，锁屏不再消失！");
			guide_title1.setText("开启纯净后台");
		}else{
			guide_title.setText("恭喜您已完成所以设置，可以顺利跟文字君相爱相杀啦！\n \n如果你想再次修改设置，请前往更多设置中进行修改");
			guide_icon.setBackgroundResource(R.drawable.guide_icon3);
			guide_title1.setText("开始使用");
			guide_title_page.setVisibility(View.GONE);
		}
		
	    guide_title_page.setText((++i)+"/"+pageNum);
	}



	private void initDateAndEvent(){
		guide_title=(TextView)findViewById(R.id.guide_title);
		guide_title1=(TextView)findViewById(R.id.guide_title1);
		guide_title_page=(TextView)findViewById(R.id.guide_title_page);
		guide_rl_start=(RelativeLayout)findViewById(R.id.guide_rl_start);
		guide_icon=(ImageView)findViewById(R.id.guide_icon);

		if (DeviceUtils.isMIUI()) {
			guide_circle_start=true;
			guide_circle_system=true;
			guide_circle_window=true;
			guide_circle_boot=true;
			pageNum=4;
		} else if (DeviceUtils.isHuawei()) {
			if (!DeviceUtils.emuiVersion3()) {
				guide_circle_start=true;
				guide_circle_system=true;
				guide_circle_boot=true;
				pageNum=3;
			} else {
				guide_circle_start=true;
				guide_circle_system=true;
				guide_circle_window=true;
				guide_circle_boot=true;
				pageNum=4;
			}
		} else if (DeviceUtils.isMeiZu()) {
			if(Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT_WATCH){
				guide_circle_start=true;
				guide_circle_boot=true;
				pageNum=2;
			}else{
				guide_circle_boot=true;
				pageNum=1;
			}
		}else if (DeviceUtils.isOPPO()) {
			if(Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN){
				try {
					String colorOsV=DeviceUtils.getDeviceProp("ro.build.version.opporom");
					colorOsV=colorOsV.substring(0, 4);
					if(colorOsV.equals("V2.0") || colorOsV.equals("V1.2")){
						if(colorOsV.equals("V2.0")){
							guide_circle_window=true;
							guide_circle_system=true;
							guide_circle_boot=true;
							guide_circle_purebackground=true;
							pageNum=4;
						}else if(colorOsV.equals("V1.2")){
							guide_circle_system=true;
							guide_circle_boot=true;
							guide_circle_purebackground=true;
							pageNum=3;
						}
					}else{
						guide_circle_system=true;
						guide_circle_boot=true;
						guide_circle_purebackground=true;
						pageNum=3;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				guide_circle_system=true;
				guide_circle_boot=true;
				pageNum=2;
			}
		} else {
			guide_circle_system=true;
			guide_circle_boot=true;
			pageNum=2;
		}
		if(guide_circle_weishi){
			pageNum++;
		}
		guide_rl_start.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(mContext, MiuiDetailsActivity.class);
		int i1 = v.getId();
		if (i1 == R.id.guide_rl_start) {
			if (guide_circle_start) {
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
					} else {
						DeviceUtils.openAppManagement(mContext);
						intent.putExtra("flag", 2);
						startActivity(intent);
					}
					LockApplication.getInstance().getConfig().setGuide_setting_auto_start_done(true);
				} catch (Exception e) {
					SimpleToast.makeText(getApplicationContext(), R.string.guide_failed, SimpleToast.LENGTH_SHORT).show();
				}
			} else if (guide_circle_window) {
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
					}
					LockApplication.getInstance().getConfig().setGuide_setting_turn_on_window_manager_done(true);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			} else if (guide_circle_system) {
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
			} else if (guide_circle_boot) {
				try {
					startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
					intent.putExtra("flag", 4);
					startActivity(intent);
					LockApplication.getInstance().getConfig().setGuide_setting_boot_protected_done(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (guide_circle_weishi) {
				try {
					if (weishiNum == 1) {
						DeviceUtils.runWeishi(mContext, componentName1);
					} else if (weishiNum == 2) {
						DeviceUtils.runWeishi(mContext, componentName2);
					} else if (weishiNum == 3) {
						DeviceUtils.runWeishi(mContext, componentName3);
					}
					guide_circle_weishi = false;
					intent.putExtra("flag", 11);
					intent.putExtra("weishiNum", weishiNum);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (guide_circle_purebackground) {
				try {
					if (DeviceUtils.isOPPO()) {
						DeviceUtils.setPureBackgroud(mContext);
						intent.putExtra("flag", 12);
						startActivity(intent);
						guide_circle_purebackground = false;
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			} else {
				int versionCode = 0;
				try {
					PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
					versionCode = pi.versionCode;
				} catch (PackageManager.NameNotFoundException e) {
					e.printStackTrace();
				}
				LockApplication.getInstance().getConfig().setLastGuideVersion(versionCode);
				finish();
			}

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
