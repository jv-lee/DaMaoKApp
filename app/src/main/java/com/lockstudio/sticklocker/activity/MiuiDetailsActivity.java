package com.lockstudio.sticklocker.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseFragmentActivity;
import com.lockstudio.sticklocker.util.DeviceUtils;

import org.json.JSONException;

import cn.opda.android.activity.R;

public class MiuiDetailsActivity extends BaseFragmentActivity {
	private TextView activity_miuidetail_tv;
	private RelativeLayout relativeLayout_guide_main,relativeLayout_guide_theme,relativeLayout_guide_diy,activity_plugin_message;
	private ImageView activity_plugin_message_know,activity_miuidetail_iv;
	private int value,weishiNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_miuidetail);
		value = getIntent().getIntExtra("flag", 1);
		weishiNum = getIntent().getIntExtra("weishiNum", 1);
		initWedgitsAndActions();
	}

	private void initWedgitsAndActions() {
		activity_miuidetail_tv = (TextView) findViewById(R.id.activity_miuidetail_tv);
		activity_miuidetail_iv=(ImageView)findViewById(R.id.activity_miuidetail_iv);
		activity_plugin_message_know=(ImageView)findViewById(R.id.activity_plugin_message_know);
		relativeLayout_guide_main=(RelativeLayout)findViewById(R.id.activity_guide_main);
		relativeLayout_guide_theme=(RelativeLayout)findViewById(R.id.activity_guide_theme);
		relativeLayout_guide_diy=(RelativeLayout)findViewById(R.id.activity_guide_diy);
		activity_plugin_message=(RelativeLayout)findViewById(R.id.activity_plugin_message);
		if(value!=10){
			findViewById(R.id.activity_miuidetail).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
			if (value == 1) { // 悬浮窗
				try {
					if (DeviceUtils.isMIUI()) {
						if (DeviceUtils.ROM_MIUI_V6.equals(DeviceUtils.getDeviceProp("ro.miui.ui.version.name")) 
								|| DeviceUtils.ROM_MIUI_V7.equals(DeviceUtils.getDeviceProp("ro.miui.ui.version.name"))) {// V6 v7
							activity_miuidetail_tv.setText("请滑至最下方点击【显示悬浮窗】选择【显示】\n完成后按手机返回键");
						} else {
							activity_miuidetail_tv.setText("请滑动至底部,\n将[悬浮窗]设置为允许\n完成后按手机返回键");
						}
					} else if (DeviceUtils.isHuawei()) {
						if (DeviceUtils.emuiVersion3()) {
							activity_miuidetail_tv.setText("请右滑至悬浮窗管理,\n打开文字锁屏悬浮窗\n完成后按手机返回键");
						}
					}
				} catch (Exception e) {
				}

			} else if (value == 2) { // 自启动
				try {
					if (DeviceUtils.isMIUI()) {
						if (DeviceUtils.ROM_MIUI_V6.equals(DeviceUtils.getDeviceProp("ro.miui.ui.version.name"))
								|| DeviceUtils.ROM_MIUI_V7.equals(DeviceUtils.getDeviceProp("ro.miui.ui.version.name"))) {
							activity_miuidetail_tv.setText("请找到【文字锁屏】并允许自启动\n完成后按手机返回键");
						} else {
							activity_miuidetail_tv.setText("1、请打开【我信任该程序】\n2、请允许文字锁屏自启动\n完成后按手机返回键");
						}
					} else if (DeviceUtils.isHuawei()) {
						activity_miuidetail_tv.setText("请将文字锁屏设为开机自启动\n完成后按手机返回键");
					} else if (DeviceUtils.isMeiZu()) {
						activity_miuidetail_tv.setText("请将文字锁屏设为开机自启动\n并打开悬浮窗\n完成后按手机返回键");
					} else {
						activity_miuidetail_tv.setText("请将文字锁屏设为开机自启动\n完成后按手机返回键");
					}
				} catch (JSONException e) {
				}
			} else if (value == 3) { // 关闭系统
				try {
					if (DeviceUtils.isMIUI()) {
						if (DeviceUtils.ROM_MIUI_V6.equals(DeviceUtils.getDeviceProp("ro.miui.ui.version.name"))) {// V6
							activity_miuidetail_tv.setText("1、请打开[开发者选项]\n2、请勾选[直接进入系统]\n完成后按手机返回键");
						} else {
							activity_miuidetail_tv.setText("1、请打开[开发者选项]\n2、请勾选[直接进入系统]\n完成后按手机返回键");
						}
					} else if (DeviceUtils.isHuawei()) {
						activity_miuidetail_tv.setText("请选择无或不锁屏\n完成后按手机返回键");
					} else {
						activity_miuidetail_tv.setText("请选择[无]或[不锁屏]\n完成后按手机返回键");
					}
				} catch (Exception e) {
				}
			} else if (value == 4) {
				try {
					activity_miuidetail_tv.setText("请在服务中开启“【文字锁屏】守护服务”\n完成后按手机返回键");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if (value == 6) {
				try {
					activity_miuidetail_tv.setVisibility(View.GONE);
					relativeLayout_guide_main.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if (value == 7) {
				try {
					activity_miuidetail_tv.setVisibility(View.GONE);
					relativeLayout_guide_theme.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if (value == 8) {
				try {
					activity_miuidetail_tv.setVisibility(View.GONE);
					relativeLayout_guide_diy.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if (value == 9) {
				try {
					activity_miuidetail_tv.setText("请在列表中打开“美美搭”的开关\n完成后按手机返回键");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if (value == 11) {
				try {
					if(weishiNum==1){
						activity_miuidetail_tv.setText("1、点击【清理加速】右上角【忽略名单】添加【文字锁屏】\n2、主界面向上划【自启动管理】允许【文字锁屏】\n完成后按手机返回键");
					}else if(weishiNum==2){
						activity_miuidetail_tv.setText("1、【自启管理】允许【文字锁屏】\n2、【白名单】加入【文字锁屏】\n完成后按手机返回键");
					}else if(weishiNum==3){
						activity_miuidetail_tv.setText("1、【清理加速—手机加速】右上角【设置—保护名单】添加【文字锁屏】\n2、【清理加速—自启管理】允许【文字锁屏】\n完成后按手机返回键");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if (value == 12) {
				try {
					activity_miuidetail_tv.setText("点击左下角【添加程序】选中【文字锁屏】后确定\n完成后按手机返回键");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
			try {
				activity_miuidetail_tv.setVisibility(View.GONE);
				activity_plugin_message.setVisibility(View.VISIBLE);
				findViewById(R.id.activity_plugin_message_know).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						LockApplication.getInstance().getConfig().setShowPhoneSms(!LockApplication.getInstance().getConfig().isShowPhoneSms());
						finish();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	@SuppressLint("NewApi")
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			View v = findViewById(R.id.root_layout);
			if (v != null) {
				v.setFitsSystemWindows(true);
			}
		}
	}

}
