package com.lockstudio.sticklocker.activity;

import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.base.BaseDialog.OnDismissedListener;
import com.lockstudio.sticklocker.service.DownloadService;
import com.lockstudio.sticklocker.util.BannerUtils;
import com.lockstudio.sticklocker.util.CommonUtil;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.HASH;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.RLog;
import com.lockstudio.sticklocker.util.ShareUtils;
import com.lockstudio.sticklocker.view.EditTextDialog;
import com.lockstudio.sticklocker.view.EditTextDialog.OnEditTextOkClickListener;
import com.lockstudio.sticklocker.view.ShareDialog;
import com.lockstudio.sticklocker.view.ShareDialog.OnShareItemClickListener;
import com.lockstudio.sticklocker.view.SimpleToast;
import com.lockstudio.sticklocker.view.TipsDialog;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.opda.android.activity.R;

public class WebviewActivity extends BaseActivity implements OnShareItemClickListener{
	public static final String TAG = "V5_WEBVIEW_ACTIVITY";
	private ProgressDialog mProgress;

	private static final int MSG_IMAGE_DOWNLOAD_FINISH = 600;
	private static final int MSG_IMAGE_DOWNLOAD_ERROR = 601;
	private static final int IMAGE_USE_TYPE_SAVE_LOCAL = 602;
	private static final int IMAGE_USE_TYPE_DIY = 603;
	private int image_use_type = 0;
	private boolean fromLock = false;
	private String appName;
	private boolean isJiaocheng,isFeatured;
	private String shareUrl;
	private String shareTitle;
	private ProgressBar webview_progressbar;
	private String shareUrl2,share_icon,share_content;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chrome);

		String name;
		String url;
		boolean hideTitleBar = false;

		
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("title", "测试你的言值");
//			jsonObject.put("url", "http://m.lockstudio.com/game/csndyz/index.html");
////			jsonObject.put("share_content", "测试分享");
//			jsonObject.put("share_icon", "http://m.lockstudio.com/game/csndyz/t016f64657116c9580e.ico");
////			jsonObject.put("share_url", "http://blog.csdn.net/zuolongsnail/article/details/6559338");
////			jsonObject.put("share_title", "测试测试测试");
////			jsonObject.put("key", "hello word");
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		//Gavan 注释lecocospush
//		JSONObject jsonObject = CCPush.getParams(getIntent());
		JSONObject jsonObject = null;
		if (jsonObject.has("title") && jsonObject.has("url")) {
			name = jsonObject.optString("title");
			url = jsonObject.optString("url");
			appName = jsonObject.optString("appName");
			if (jsonObject.has("hideTitle")) {
				hideTitleBar = jsonObject.optBoolean("hideTitle");
			}
			if(jsonObject.has("share_icon")){
				share_icon = jsonObject.optString("share_icon");
				shareUrl2 = url;
				shareTitle = name;
				if(jsonObject.has("share_content")){
					share_content = jsonObject.optString("share_content");
				}else{
					share_content = shareTitle;
				}
				isFeatured=true;
			}
		} else {
			name = getIntent().getStringExtra("title");
			url = getIntent().getStringExtra("url");
			appName = getIntent().getStringExtra("appName");
			hideTitleBar = getIntent().getBooleanExtra("hideTitle", false);
		}
		isJiaocheng = getIntent().getBooleanExtra("isJiaocheng", false);
		if (isJiaocheng) {
			TextView banner_share = (TextView) findViewById(R.id.banner_share);
			banner_share.setVisibility(View.VISIBLE);
			banner_share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CommonUtil.share(mContext, getString(R.string.app_name) + shareTitle + ":" + shareUrl);
				}
			});
			shareUrl = url;
			shareTitle = name;
		}
		if(isFeatured){
			ImageView banner_share = (ImageView) findViewById(R.id.banner_share_imageview);
			banner_share.setVisibility(View.VISIBLE);
			banner_share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CustomEventCommit.commit(mContext, CustomEventCommit.share,shareUrl2);
					ShareDialog sd = new ShareDialog(mContext,3);
					sd.setShareItemClickListener(WebviewActivity.this);
					sd.show();
				}
			});
		}
		if (TextUtils.isEmpty(appName)) {
			appName = name;
		}

		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			if (!url.contains("version_code=")) {
				if (url.contains("?")) {
					url += ("&version_code=" + packageInfo.versionCode);
				} else {
					url += ("?version_code=" + packageInfo.versionCode);
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		String eventValue = name;
		if (!TextUtils.isEmpty(eventValue)) {
			if (eventValue.length() > 5) {
				eventValue = eventValue.substring(eventValue.length() - 5, eventValue.length());
			}
			CustomEventCommit.commit(mContext, TAG, eventValue);
		}

		fromLock = getIntent().getBooleanExtra("fromLock", false);

		webview_progressbar = (ProgressBar) findViewById(R.id.webview_progressbar);

		final WebView webview = (WebView) findViewById(R.id.chrome_webView);
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setSavePassword(true);
		webSettings.setSaveFormData(true);
		webSettings.setGeolocationEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setDefaultTextEncodingName("UTF-8");
		webview.addJavascriptInterface(new JsShare(), "JSBridge");
		if (isJiaocheng) {
			// webview.addJavascriptInterface(new InJavaScriptLocalObj(),
			// "local_obj");
		}
		if (!TextUtils.isEmpty(url)) {
			webview.loadUrl(url);
		}
		webview.setWebViewClient(new HelloWebViewClient());
		webview.setWebChromeClient(new MyWebChromeClient());
		webview.setDownloadListener(new MyWebViewDownLoadListener());
		webview.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
						webview.goBack();
						return true;
					}
				}
				return false;
			}
		});
		if (!TextUtils.isEmpty(name)) {
			if ("faq".equals(name)) {
				BannerUtils.setBannerTitle(this, R.drawable.title_faq);
			} else {
				BannerUtils.setBannerTitle_String(this, name);
			}
		}
		findViewById(R.id.banner_left).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if (hideTitleBar) {
			View v = findViewById(R.id.banner_layout);
			v.setVisibility(View.GONE);
		}
		webview.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				CustomEventCommit.commit(mActivity, TAG, "PIC_LONG_CLICK");
				WebView.HitTestResult result = ((WebView) v).getHitTestResult();
				if (result != null) {
					int type = result.getType();
					if (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
						final String imageUrl = result.getExtra();

						final TipsDialog tipsDialog = new TipsDialog(mContext);
						tipsDialog.setMessage(R.string.tips_choose_operation);
						tipsDialog.setOnDismissedListener(new OnDismissedListener() {

							@Override
							public void OnDialogDismissed() {
								if (image_use_type > 0) {
									loadImage(imageUrl);
								}
							}
						});
						tipsDialog.setGreenButton(R.string.dialog_save_wallpaper, new OnClickListener() {

							@Override
							public void onClick(View v) {
								tipsDialog.dismiss();
								image_use_type = IMAGE_USE_TYPE_SAVE_LOCAL;

							}
						});
						tipsDialog.setOkButton(R.string.dialog_diy_wallpaper, new OnClickListener() {

							@Override
							public void onClick(View v) {
								tipsDialog.dismiss();
								image_use_type = IMAGE_USE_TYPE_DIY;
							}
						});
						tipsDialog.show();
					}
				}
				return false;
			}
		});

		if (!name.contains("锁屏大赛") && !"美化教程".equals(name) && !"faq".equals(name) && !hideTitleBar && !name.contains("神秘软件")) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					SimpleToast.makeText(mContext, R.string.long_click_to_download, SimpleToast.LENGTH_SHORT).show();
				}
			}, 500);
		}
	}

	private void loadImage(final String imageUrl) {

		showProgress();
		ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap response) {
				if (response != null) {
					String cacheKey = com.android.volley.toolbox.ImageLoader.getCacheKey(imageUrl, 0, 0, ImageView.ScaleType.CENTER_INSIDE);
					VolleyUtil.instance().putBitmap(cacheKey, response);

					Message msg = new Message();
					msg.what = MSG_IMAGE_DOWNLOAD_FINISH;
					msg.obj = imageUrl;
					handler.sendMessage(msg);
				}
			}
		}, 0, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Message msg = new Message();
				msg.what = MSG_IMAGE_DOWNLOAD_ERROR;
				msg.obj = imageUrl;
				handler.sendMessage(msg);
			}
		});
		VolleyUtil.instance().addRequest(imageRequest);
	}

	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			shareUrl = url;
			try {
				PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				if (!url.contains("version_code=")) {
					if (url.contains("?")) {
						url += ("&version_code=" + packageInfo.versionCode);
					} else {
						url += ("?version_code=" + packageInfo.versionCode);
					}
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (isJiaocheng) {
				// view.loadUrl("javascript:window.local_obj.showSource('<head>'+"
				// +
				// "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
			}
		}

	}

	private boolean okClick;
	private String commitString;

	private class MyWebChromeClient extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			webview_progressbar.setProgress(newProgress);
			if (newProgress < 100) {
				webview_progressbar.setVisibility(View.VISIBLE);
			} else {
				webview_progressbar.setVisibility(View.GONE);
			}

		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			shareTitle = title;
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			RLog.i("MyWebChromeClient", "onJsAlert");
			final TipsDialog tipsDialog = new TipsDialog(mContext);
			tipsDialog.setMessage(message);
			tipsDialog.hideZhanwei();
			tipsDialog.setOkButton(R.string.comfrim, null);
			tipsDialog.show();
			result.confirm();
			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
			RLog.i("MyWebChromeClient", "onJsConfirm");
			okClick = false;
			final TipsDialog tipsDialog = new TipsDialog(mContext);
			tipsDialog.setMessage(message);
			tipsDialog.setOnDismissedListener(new OnDismissedListener() {

				@Override
				public void OnDialogDismissed() {
					if (!okClick) {
						result.cancel();
					}
				}
			});
			tipsDialog.setOkButton(R.string.comfrim, new OnClickListener() {

				@Override
				public void onClick(View v) {
					okClick = true;
					result.confirm();
					tipsDialog.dismiss();
				}
			});
			tipsDialog.setCancelButton(R.string.cancle, new OnClickListener() {

				@Override
				public void onClick(View v) {
					result.cancel();
					tipsDialog.dismiss();
				}
			});
			tipsDialog.show();
			return true;
		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
			RLog.i("MyWebChromeClient", "onJsPrompt");
			CustomEventCommit.commit(mActivity, TAG, "FULI_CLICK");
			commitString = null;
			final EditTextDialog editTextDialog = new EditTextDialog(mContext);
			editTextDialog.setHintText(message);
			editTextDialog.setOnDismissedListener(new OnDismissedListener() {

				@Override
				public void OnDialogDismissed() {
					if (commitString == null) {
						result.cancel();
					} else {
						result.confirm(commitString);
					}
				}
			});
			editTextDialog.setEditTextOkClickListener(new OnEditTextOkClickListener() {

				@Override
				public void OnEditTextOkClick(String string) {
					commitString = string;
					editTextDialog.dismiss();
				}
			});
			editTextDialog.show();
			return true;
		}

		@Override
		public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
			RLog.i("MyWebChromeClient", "onJsBeforeUnload");
			return super.onJsBeforeUnload(view, url, message, result);
		}

	}

	private class MyWebViewDownLoadListener implements DownloadListener {
		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
			if (!DownloadService.downloadUrls.contains(url)) {
				Intent intent = new Intent(mContext, DownloadService.class);
				intent.putExtra("name", appName);
				intent.putExtra("url", url);
				startService(intent);
			} else {
				SimpleToast.makeText(mContext, R.string.app_is_downloading, SimpleToast.LENGTH_SHORT).show();
			}
		}
	}

	private void showProgress() {
		Context context = this;
		mProgress = ProgressDialog.show(context, null, mContext.getResources().getString(R.string.webview_detail));
		mProgress.show();
	}

	private void closeProgress() {
		if (null != mProgress) {
			mProgress.dismiss();
			mProgress = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 1080:
			setResult(1089);
			finish();
			break;
		}
	}

	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_IMAGE_DOWNLOAD_FINISH:
				String url = (String) msg.obj;
				closeProgress();
				if (!TextUtils.isEmpty(url)) {
					if (image_use_type == IMAGE_USE_TYPE_SAVE_LOCAL) {
						if (DeviceInfoUtils.sdMounted()) {
							saveToAlbum(url);
						} else {
							SimpleToast.makeText(mContext, R.string.sdcard_not_mounted, SimpleToast.LENGTH_SHORT).show();
						}
					} else {
						CustomEventCommit.commit(mContext.getApplicationContext(), TAG, "DIY");
						Intent intent = new Intent(mContext, DiyActivity.class);
						intent.putExtra("wallpaperUrl", url);
						startActivity(intent);
						finish();
					}
					image_use_type = 0;
				}
				break;

			case MSG_IMAGE_DOWNLOAD_ERROR:
				SimpleToast.makeText(WebviewActivity.this, R.string.download_fail_try_again, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			return false;
		}
	});

	private String saveToAlbum(String imageUrl) {
		String name = HASH.md5sum(imageUrl) + ".jpg";
		boolean success = VolleyUtil.instance().writeBitmapToFile(imageUrl, MConstants.USER_PHOTO_PATH, name);
		if (success) {
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + MConstants.USER_PHOTO_PATH + name)));
			SimpleToast.makeText(mContext, R.string.wallpaper_save_to_nature, SimpleToast.LENGTH_SHORT).show();
			CustomEventCommit.commit(mContext.getApplicationContext(), TAG, "SAVE");
			return MConstants.USER_PHOTO_PATH + name;
		}
		return null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (fromLock) {
			if (!isKeyguardRestricted()) {
				mContext.sendBroadcast(new Intent(MConstants.ACTION_LOCK_NOW));
			}
		}
	}

	public boolean isKeyguardRestricted() {
		KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
		return keyguardManager != null && keyguardManager.inKeyguardRestrictedInputMode();
	}

	class JsShare implements OnShareItemClickListener {
		private String shareImageUrl;
		private String shareUrl;
		private String shareTitle;
		private String shareDesc;

		@JavascriptInterface
		public void openDiy() {
			startActivity(new Intent(mContext, DiyActivity.class));
		}

		@JavascriptInterface
		public void download(String json) {
			if (!TextUtils.isEmpty(json)) {
				try {
					JSONObject jsonObject = new JSONObject(json);
					if (jsonObject != null) {
						Intent intent = new Intent(mContext, LockThemePreviewActivity.class);
						intent.putExtra("THUMBNAIL_URL", jsonObject.optString("fileurlabbr"));
						intent.putExtra("IMAGE_URL", jsonObject.optString("fileurlimg"));
						intent.putExtra("THEME_URL", jsonObject.optString("fileurlzip"));
						intent.putExtra("themeAuthor", jsonObject.optString("uploadmemo"));
						intent.putExtra("themeName", jsonObject.optString("uploadname"));
						intent.putExtra("themeId", jsonObject.optString("id"));
						intent.putExtra("FROM", "FEATURED");
						startActivity(intent);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@JavascriptInterface
		public void share(String imageurl, String url, String title, String desc) {
			RLog.i("imageurl", imageurl);
			RLog.i("url", url);
			RLog.i("title", title);
			RLog.i("desc", desc);
			this.shareImageUrl = imageurl;
			this.shareUrl = url;
			this.shareTitle = title;
			this.shareDesc = desc;

			ShareDialog sd = null;
			sd = new ShareDialog(mContext,1);
			sd.setShareItemClickListener(JsShare.this);
			sd.show();
		}

		@JavascriptInterface
		public void changestatus(int status) {
			SharedPreferences sharedPreferences = null;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				sharedPreferences = mContext.getSharedPreferences("huodong.cfg", Context.MODE_MULTI_PROCESS);
			} else {
				sharedPreferences = mContext.getSharedPreferences("huodong.cfg", Context.MODE_PRIVATE);
			}
			sharedPreferences.edit().putInt("show_Huodong_view_count", 4).apply();
		}

		@Override
		public void OnShareItemClick(int id) {
			switch (id) {
			case 1:// 微信
				try {
					new ShareUtils(mActivity).sendURLToweixin(shareTitle, shareDesc, shareImageUrl, shareUrl, true);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case 2:// 朋友圈
				try {
					new ShareUtils(mActivity).sendURLToweixin(shareTitle, shareDesc, shareImageUrl, shareUrl, false);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case 3:// QQ好友
				try {
					new ShareUtils(mActivity).shareUrlToQQ(shareTitle, shareDesc, shareUrl, shareImageUrl);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case 4:
				try {
					new ShareUtils(mActivity).sendWeiBoMessage(shareImageUrl, shareUrl, shareTitle, shareDesc);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;

			default:
				break;
			}
		}
	}

	String webContent = "";

	/**
	 * @author 庄宏岩 获取分享的内容 p标签中的内容
	 */
	final class InJavaScriptLocalObj {
		@JavascriptInterface
		public void showSource(String html) {
			RLog.i("HTML", html);
			int start = html.indexOf("<p");
			int end = html.indexOf("</p>");
			if (start != -1 && end != -1) {
				String string = html.substring(start, end + 4);
				String regex = "<p.*?>(.*?)</p>";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(string);
				if (m.find()) {
					string = m.group(1);
					regex = "<span.*?>(.*?)</span>";
					p = Pattern.compile(regex);
					m = p.matcher(string);
					if (m.find()) {
						string = m.group(1);
						if (string.length() > 30) {
							webContent = string.substring(0, 30) + "...";
						} else {
							webContent = string;
						}
					} else {
						if (string.length() > 30) {
							webContent = string.substring(0, 30) + "...";
						} else {
							webContent = string;
						}
					}
					RLog.i("debug", "要分享的内容:" + string);
				}
			}
		}
	}

	@Override
	public void OnShareItemClick(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case 1:// 微信
			try {
				new ShareUtils(mActivity).sendURLToweixin(shareTitle, share_content, share_icon, shareUrl2, true);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		case 2:// 朋友圈
			try {
				new ShareUtils(mActivity).sendURLToweixin(shareTitle, shareTitle, share_icon, shareUrl2, false);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	
		default:
			break;
		}
	}

}
