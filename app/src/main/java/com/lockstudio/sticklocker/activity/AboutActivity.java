package com.lockstudio.sticklocker.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.util.ShareUtils;
import com.lockstudio.sticklocker.view.SimpleToast;

import cn.opda.android.activity.R;

public class AboutActivity extends BaseActivity implements OnClickListener {

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
        findViewById(R.id.button_back).setOnClickListener(this);
        findViewById(R.id.button_home).setOnClickListener(this);
        findViewById(R.id.button_qq).setOnClickListener(this);
        findViewById(R.id.button_wechat).setOnClickListener(this);
        findViewById(R.id.button_weibo).setOnClickListener(this);
        findViewById(R.id.button_tieba).setOnClickListener(this);
	}
    
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.button_back) {
			finish();

		} else if (i == R.id.button_home) {
			openBrowserURL("http://www.lockstudio.com/");

		} else if (i == R.id.button_qq) {
			new ShareUtils(mActivity).joinQQGroup("egM6G7aJICjPgHrQoUo9em6RyeC-UEGa");

		} else if (i == R.id.button_wechat) {
			try {
				Intent intent = new Intent();
				ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
				intent.setAction(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setComponent(cmp);
				startActivity(intent);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						SimpleToast.makeText(mContext, R.string.please_search_and_add, SimpleToast.LENGTH_LONG).show();
					}
				}, 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (i == R.id.button_weibo) {
			openBrowserURL("http://weibo.com/u/5512960468");

		} else if (i == R.id.button_tieba) {
			openBrowserURL("http://tieba.baidu.com/f?ie=utf-8&kw=%E6%96%87%E5%AD%97%E9%94%81%E5%B1%8F&fr=search");

		} else {
		}
	}
	
	private void openBrowserURL(String url){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}
}
