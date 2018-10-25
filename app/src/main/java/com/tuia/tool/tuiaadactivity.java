package com.tuia.tool;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.lockstudio.sticklocker.activity.MainActivity;

import cn.opda.android.activity.R;

public class tuiaadactivity extends Activity {
	private WebView webView;
	private ImageView adview_back;
	private boolean canClose = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tuiaview);

		// String url =
		// "http://engine.tuicoco.com/index/image?appKey=Q4iHCZRJxk4hovWWcqGM7VP1Va4&adslotId=8478&deviceId=866328029952122";
		// String url = "http://www.baidu.com";
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient());
		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl(MainActivity._url);

		adview_back = (ImageView) findViewById(R.id.adview_back_bt);
		adview_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		// 延迟显示红叉
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// execute the task
				adview_back.setVisibility(View.VISIBLE);
				canClose = true;
			}
		}, 3000);

		// //浏览器打开
		// Intent intent = new Intent();
		// intent.setAction("android.intent.action.VIEW");
		// Uri content_url = Uri.parse(url);
		// intent.setData(content_url);
		// startActivity(intent);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 这是一个监听用的按键的方法，keyCode
		// 监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作，因为mWebView.canGoBack()返回的是一个Boolean类型，所以我们把它返回为true
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// mWebView.goBack();
			if(canClose){
        		finish();
        	}
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}
}
