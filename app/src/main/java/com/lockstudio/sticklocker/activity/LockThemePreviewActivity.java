package com.lockstudio.sticklocker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lockstudio.sticklocker.Interface.FileDownloadListener;
import com.lockstudio.sticklocker.Interface.FontDownloadListener;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.base.BaseDialog.OnDismissedListener;
import com.lockstudio.sticklocker.model.LockerInfo;
import com.lockstudio.sticklocker.model.ThemeConfig;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.DeviceUtils;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.FileDownloader;
import com.lockstudio.sticklocker.util.FileUtils;
import com.lockstudio.sticklocker.util.FontUtils;
import com.lockstudio.sticklocker.util.HostUtil;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.RLog;
import com.lockstudio.sticklocker.util.ShareUtils;
import com.lockstudio.sticklocker.util.ThemeUtils;
import com.lockstudio.sticklocker.util.UploadThemeUitls;
import com.lockstudio.sticklocker.util.ZipUtils;
import com.lockstudio.sticklocker.view.ContributeDialog;
import com.lockstudio.sticklocker.view.ContributeDialog.ContributeClickListener;
import com.lockstudio.sticklocker.view.ShareDialog;
import com.lockstudio.sticklocker.view.ShareDialog.OnShareItemClickListener;
import com.lockstudio.sticklocker.view.SimpleToast;
import com.lockstudio.sticklocker.view.SpringImageButton;
import com.lockstudio.sticklocker.view.TipsDialog;
import com.lockstudio.sticklocker.view.UploadDialog;
import com.lockstudio.sticklocker.view.UploadDialog.OnUploadItemClickListener;
import com.lockstudio.sticklocker.view.UploadFailedDialog;
import com.lockstudio.sticklocker.view.UploadFailedDialog.OnOkClickListener;
import com.lockstudio.sticklocker.view.UploadSucceedDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.ZipException;

import cn.opda.android.activity.R;

public class LockThemePreviewActivity extends BaseActivity implements OnClickListener, OnShareItemClickListener, OnUploadItemClickListener {

	private String theme_path;
	private boolean isUsed;
	private ThemeConfig themeConfig;
	private View locker_preview_button_layout;
	private ProgressBar download_progressbar;
	private boolean FROM_FEATURED = false;
	private String thumbnail_url;
	private String image_url;
	private String theme_url;
	private ImageView preview_imageview;

	private final int MSG_IMAGE_DOWNLOAD_ERROR = 21;
	private final int MSG_IMAGE_DOWNLOAD_FINISH = 22;
	private final int MSG_INIT_IMAGE = 23;
	private String zipPath;
	private String themePath;
	private boolean isDownloaded;
	private boolean isDownloading;
	private LinearLayout info_layout;
	private LinearLayout info_layout1;
	private String themeName;
	private String themeAuthor;
	private int themeId;
	private SpringImageButton diy_like_image;
	private SpringImageButton diy_delete_image;
	private SpringImageButton diy_info_image;
	private SpringImageButton diy_info_image1;
	private SharedPreferences sp;
	private ImageView center_imageview;
	private boolean isDestroy;

	private boolean imageDownloaded = false;
	private int maxThumbnailWidth, maxThumbnailHeight;
	private boolean theme_diy=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_preview);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			sp = getSharedPreferences("default.cfg", Context.MODE_MULTI_PROCESS);
		} else {
			sp = getSharedPreferences("default.cfg", Context.MODE_PRIVATE);
		}
		FROM_FEATURED = "FEATURED".equals(getIntent().getStringExtra("FROM"));
		if (FROM_FEATURED) {
			thumbnail_url = getIntent().getStringExtra("THUMBNAIL_URL");
			image_url = getIntent().getStringExtra("IMAGE_URL");
			theme_url = getIntent().getStringExtra("THEME_URL");
			themeName = getIntent().getStringExtra("themeName");
			themeAuthor = getIntent().getStringExtra("themeAuthor");
			themeId = getIntent().getIntExtra("themeId", 0);

			//Gavan模拟一个假路径
//			zipPath = MConstants.DOWNLOAD_PATH + HASH.md5sum(theme_url) + ".zip";
//			themePath = MConstants.THEME_PATH + HASH.md5sum(theme_url);
			zipPath = MConstants.DOWNLOAD_PATH + "222.zip";
			themePath = MConstants.THEME_PATH + "333";
			isDownloaded = new File(zipPath).exists();
			

			
			
			
			
			if (isDownloaded) {
//				if (!new File(themePath, MConstants.config).exists()) {
//					if (!new File(themePath, MConstants.config).exists()) {
//						try {
//							ZipUtils.upZipFile(new File(zipPath), themePath);
//						} catch (ZipException e) {
//							e.printStackTrace();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}
				System.out.println("isDownloaded");
				theme_path = themePath;
				isUsed = checkUsed(theme_path,false);
				
			}
		} else {
			isDownloaded = true;
			theme_path = getIntent().getStringExtra("theme_path");
			theme_diy = getIntent().getBooleanExtra("theme_diy", false);
			isUsed = checkUsed(theme_path,theme_diy);
		}

		maxThumbnailWidth = LockApplication.getInstance().getConfig().getScreenWidth() / 3;
		maxThumbnailHeight = maxThumbnailWidth * 16 / 9;

		initViewAndEvent();
	}

	private boolean checkUsed(String theme_path,boolean theme_diy) {
		if (TextUtils.isEmpty(theme_path)) {
			return false;
		}
		if(theme_diy){
			return false;
		}
		if (new File(LockApplication.getInstance().getConfig().getThemeName()).exists()
				&& new File(theme_path).getName().equals(new File(LockApplication.getInstance().getConfig().getThemeName()).getName())) {
			return true;
		}
		return false;
	}
	
	private void initViewAndEvent() {
		preview_imageview = (ImageView) findViewById(R.id.preview_imageview);

		diy_delete_image = (SpringImageButton) findViewById(R.id.diy_delete_image);
		diy_delete_image.setOnClickListener(this);
		if (!FROM_FEATURED) {
			diy_delete_image.setVisibility(View.VISIBLE);
		} else {
			diy_delete_image.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(theme_path)) {
			System.out.println("theme_path<LTPA>"+theme_path);
			
			themeConfig = ThemeUtils.parseConfig(this, new File(theme_path, MConstants.config).getAbsolutePath());
		}

		center_imageview = (ImageView) findViewById(R.id.center_imageview);
		if (isDownloaded) {
			if (isUsed) {
				center_imageview.setImageResource(R.drawable.text_diy);
			} else {
				center_imageview.setImageResource(R.drawable.text_use);
			}
		} else {
			center_imageview.setImageResource(R.drawable.text_download);
		}
		center_imageview.setOnClickListener(this);

		TextView left_textview = (TextView) findViewById(R.id.left_textview);
		TextView right_textview = (TextView) findViewById(R.id.right_textview);

		if (isUsed) {
			left_textview.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.button_reset_pass_selector), null, null);
			left_textview.setText(R.string.button_reset_pass);
		} else {
			left_textview.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.button_diy_selector), null, null);
			left_textview.setText(R.string.button_diy);
		}

		if (FROM_FEATURED) {
			right_textview.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.button_upload_selector), null, null);
			right_textview.setText(R.string.button_share);
		} else {
			if (new File(theme_path, MConstants.isCloud).exists()) {
				right_textview.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.button_upload_selector), null, null);
				right_textview.setText(R.string.button_upload);
			} else {
				right_textview.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.button_upload_selector), null, null);
				right_textview.setText(R.string.button_upload);
			}
		}

		left_textview.setOnClickListener(this);
		right_textview.setOnClickListener(this);
		preview_imageview.setOnClickListener(this);

		locker_preview_button_layout = findViewById(R.id.locker_preview_button_layout);
		locker_preview_button_layout.setOnClickListener(this);

		download_progressbar = (ProgressBar) findViewById(R.id.download_progressbar);
		
		if (!FROM_FEATURED) {
			changeBottomButtonVisibility();
			int banbenhao=getLocalVersionCode(mContext);
			if(banbenhao>=5330000){
				if (new File(theme_path, MConstants.autor).exists()) {
					info_layout1 = (LinearLayout) findViewById(R.id.info_layout1);
					TextView theme_name_textview = (TextView) findViewById(R.id.theme_name_textview1);
					TextView theme_author_textview = (TextView) findViewById(R.id.theme_author_textview1);
					File autorFile=new File(theme_path, MConstants.autor);
					Scanner scan = null ;							// 扫描输入
					StringBuffer msg=new StringBuffer();
					try {
						scan = new Scanner(new FileInputStream(autorFile)) ;	// 实例化Scanner
						while(scan.hasNext()){							// 循环读取
				        	msg.append(scan.next() + ";") ;		// 设置文本
				        }
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (scan != null) {
							scan.close() ;							// 关闭打印流
						}
					}
					String str=new String(msg);
					String []strAutor=str.split(";");
					theme_name_textview.setText(getString(R.string.theme_name, strAutor[0]));
					if (TextUtils.isEmpty(strAutor[1])) {
						theme_author_textview.setText(getString(R.string.theme_author, getString(R.string.anonymous)));
					} else {
						theme_author_textview.setText(getString(R.string.theme_author, strAutor[1]));
					}
					diy_info_image1 = (SpringImageButton) findViewById(R.id.diy_info_image1);
					diy_info_image1.setVisibility(View.VISIBLE);
					diy_info_image1.setOnClickListener(this);
				}
			}
		} else {

			info_layout = (LinearLayout) findViewById(R.id.info_layout);

			TextView theme_name_textview = (TextView) findViewById(R.id.theme_name_textview);
			TextView theme_author_textview = (TextView) findViewById(R.id.theme_author_textview);

			theme_name_textview.setText(getString(R.string.theme_name, themeName));
			if (TextUtils.isEmpty(themeAuthor)) {

				theme_author_textview.setText(getString(R.string.theme_author, getString(R.string.anonymous)));
			} else {
				theme_author_textview.setText(getString(R.string.theme_author, themeAuthor));
			}

			diy_like_image = (SpringImageButton) findViewById(R.id.diy_like_image);
			diy_info_image = (SpringImageButton) findViewById(R.id.diy_info_image);
			diy_like_image.setVisibility(View.VISIBLE);
			diy_info_image.setVisibility(View.VISIBLE);
			diy_like_image.setOnClickListener(this);
			diy_info_image.setOnClickListener(this);

			if (sp.getBoolean("like_" + theme_url, false)) {
				diy_like_image.setSelected(true);
			}
		}

		if (!FROM_FEATURED) {
			String preview = theme_path + "/preview";
			if (new File(preview).exists()) {
				preview_imageview.setImageBitmap(DrawableUtils.getBitmap(mContext, preview));
				preview_imageview.setVisibility(View.VISIBLE);
			}
		} else {
			if (!TextUtils.isEmpty(thumbnail_url)) {
				VolleyUtil.instance().setUrlImage(VolleyUtil.instance().getRequestQueue(), preview_imageview, thumbnail_url, 0, 0, maxThumbnailWidth,
						maxThumbnailHeight);
			}
			mHandler.sendEmptyMessageDelayed(MSG_INIT_IMAGE, 200);
		}
	}
	
	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	public static int getLocalVersionCode(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private void initImage() {
		if (!TextUtils.isEmpty(image_url)) {
			final Bitmap bitmap = VolleyUtil.instance().getBitmapForUrl(image_url);
			if (bitmap != null) {
				imageDownloaded = true;
				preview_imageview.setImageBitmap(bitmap);
				mHandler.sendEmptyMessage(MSG_IMAGE_DOWNLOAD_FINISH);
				if (!TextUtils.isEmpty(themePath)) {
					VolleyUtil.instance().writeBitmapToFile(image_url, themePath, "preview");
				}
			} else {
				ImageRequest imageRequest = new ImageRequest(image_url, new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap response) {
						if (response != null) {
							imageDownloaded = true;
							preview_imageview.setImageBitmap(response);
							String cacheKey = com.android.volley.toolbox.ImageLoader.getCacheKey(image_url, 0, 0, ImageView.ScaleType.CENTER_INSIDE);
							VolleyUtil.instance().putBitmap(cacheKey, response);
							mHandler.sendEmptyMessage(MSG_IMAGE_DOWNLOAD_FINISH);
							if (!TextUtils.isEmpty(themePath)) {
								VolleyUtil.instance().writeBitmapToFile(image_url, themePath, "preview");
							}
						}
					}
				}, 0, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						mHandler.sendEmptyMessage(MSG_IMAGE_DOWNLOAD_ERROR);
					}
				});
				VolleyUtil.instance().addRequest(imageRequest);
			}
		}
	}

	public void updateView() {
		if (!TextUtils.isEmpty(theme_path)) {
			themeConfig = ThemeUtils.parseConfig(this, new File(theme_path, MConstants.config).getAbsolutePath());
		}

		if (isDownloaded) {
			if (isUsed) {
				center_imageview.setImageResource(R.drawable.text_diy);
			} else {
				center_imageview.setImageResource(R.drawable.text_use);
			}
		} else {
			center_imageview.setImageResource(R.drawable.text_download);
		}

		TextView left_textview = (TextView) findViewById(R.id.left_textview);

		if (isUsed) {
			left_textview.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.button_reset_pass_selector), null, null);
			left_textview.setText(R.string.button_reset_pass);
		} else {
			left_textview.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.button_diy_selector), null, null);
			left_textview.setText(R.string.button_diy);
		}
		left_textview.setOnClickListener(this);
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {

			case MSG_IMAGE_DOWNLOAD_ERROR:// 错误
				break;

			case MSG_IMAGE_DOWNLOAD_FINISH:// 完成
				changeBottomButtonVisibility();
				break;

			case MSG_INIT_IMAGE:
				initImage();
				break;

			default:
				break;
			}
			return false;
		}
	});

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.diy_delete_image) {
			diy_delete_image.spring();
			final TipsDialog tipsDialog = new TipsDialog(mContext);
			tipsDialog.setMessage(R.string.dialog_tips_delete_lock);
			tipsDialog.setCancelButton(R.string.cancle, null);
			tipsDialog.setOkButton(R.string.delete, new OnClickListener() {

				@Override
				public void onClick(View v) {
					tipsDialog.dismiss();
					if (isUsed) {
						LockApplication.getInstance().getConfig().setThemeName("", false);
					}
					FileUtils.deleteFileByPath(theme_path);
					String themeZipPath = MConstants.DOWNLOAD_PATH + new File(theme_path).getName() + ".zip";
					if (new File(themeZipPath).exists()) {
						FileUtils.deleteFileByPath(themeZipPath);
					}
					finish();
					sendBroadcast(new Intent(MConstants.ACTION_UPDATE_LOCAL_THEME));
				}
			});
			tipsDialog.show();

		} else if (i == R.id.diy_info_image) {
			toggleInfoView();

		} else if (i == R.id.diy_info_image1) {
			toggleInfoView1();

		} else if (i == R.id.diy_like_image) {
			showLikeAnim();

		} else if (i == R.id.center_imageview) {
			if (isDownloaded) {
				if (isUsed) {
					Intent intent = new Intent(this, DiyActivity.class);
					intent.putExtra("theme_path", theme_path);
					startActivity(intent);
					finish();
				} else {
					if (!LockApplication.getInstance().getConfig().isGuide_setting_boot_protected_done()) {
						int versionCode = 0;
						if (LockApplication.getInstance().getConfig().getLastGuideVersion() != versionCode
								&& !(DeviceUtils.isMeiZu() && Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
							final TipsDialog tipsDialog2 = new TipsDialog(mContext);
//		                	tipsDialog2.setSystemAlert();
							tipsDialog2.setMessage("系统提示: \n" + "文字君发现您的手机尚未进行设置，为了让锁屏正常工作，麻烦亲动手设置部分权限");
							tipsDialog2.setOkButton("马上设置", new OnClickListener() {

								@Override
								public void onClick(View v) {
									tipsDialog2.dismiss();
									Intent intent = new Intent();
									intent.setClass(mContext, GuideSettingsActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									mContext.startActivity(intent);
								}
							});
							tipsDialog2.show();
						}
					} else {
						setPassword();
					}
				}
			} else {
				if (DeviceInfoUtils.sdMounted()) {
					download();
				} else {
					SimpleToast.makeText(mContext, R.string.sdcard_not_mounted_2, SimpleToast.LENGTH_SHORT).show();
				}
			}

		} else if (i == R.id.left_textview) {
			if (isDownloaded) {
				if (isUsed) {
					setPassword();
				} else {
					Intent intent = new Intent(this, DiyActivity.class);
					intent.putExtra("theme_path", theme_path);
					startActivity(intent);
					finish();
				}
			} else {
				SimpleToast.makeText(mContext, R.string.lock_undownload, SimpleToast.LENGTH_SHORT).show();

			}


		} else if (i == R.id.right_textview) {
			if (FROM_FEATURED) {
				ShareDialog sd = new ShareDialog(this, 2);
				sd.setShareItemClickListener(this);
				sd.show();
			} else {
				if (new File(theme_path, MConstants.isCloud).exists()) {
					ShareDialog sd = new ShareDialog(this, 2);
					sd.setShareItemClickListener(this);
					sd.show();
				} else {
					UploadDialog uploadDialog = new UploadDialog(this);
					uploadDialog.setUploadItemClickListener(this);
					uploadDialog.show();
				}

			}


		} else if (i == R.id.preview_imageview) {
			changeBottomButtonVisibility();

		} else {
		}
	}

	private void showLikeAnim() {
		if (!diy_like_image.isSelected()) {

			diy_like_image.setSelected(true);
			diy_like_image.spring();

			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", themeId);
				String url = HostUtil.getUrl(MConstants.URL_PRAISE + "?json=" + jsonObject.toString());
				JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						RLog.i("JSON", response.toString());
						if (response.optInt("code") == 200) {
							sp.edit().putBoolean("like_" + theme_url, true).apply();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});

				VolleyUtil.instance().addRequest(jsonObjectRequest);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void toggleInfoView() {
		if (info_layout.getVisibility() == View.VISIBLE) {
			ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setDuration(200);
			scaleAnimation.setFillAfter(true);
			scaleAnimation.setInterpolator(mContext, android.R.anim.accelerate_interpolator);
			info_layout.startAnimation(scaleAnimation);
			info_layout.setVisibility(View.INVISIBLE);
		} else {
			ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setDuration(200);
			scaleAnimation.setFillAfter(true);
			scaleAnimation.setInterpolator(mContext, android.R.anim.overshoot_interpolator);
			info_layout.startAnimation(scaleAnimation);
			info_layout.setVisibility(View.VISIBLE);
		}
		diy_info_image.spring();
	}
	
	private void toggleInfoView1() {
		if (info_layout1.getVisibility() == View.VISIBLE) {
			ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setDuration(200);
			scaleAnimation.setFillAfter(true);
			scaleAnimation.setInterpolator(mContext, android.R.anim.accelerate_interpolator);
			info_layout1.startAnimation(scaleAnimation);
			info_layout1.setVisibility(View.INVISIBLE);
		} else {
			ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setDuration(200);
			scaleAnimation.setFillAfter(true);
			scaleAnimation.setInterpolator(mContext, android.R.anim.overshoot_interpolator);
			info_layout1.startAnimation(scaleAnimation);
			info_layout1.setVisibility(View.VISIBLE);
		}
		diy_info_image1.spring();
	}

	private void closeInfoView() {

		if (info_layout != null && info_layout.getVisibility() == View.VISIBLE) {
			ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setDuration(200);
			scaleAnimation.setInterpolator(mContext, android.R.anim.accelerate_interpolator);
			scaleAnimation.setFillAfter(true);
			info_layout.startAnimation(scaleAnimation);
			info_layout.setVisibility(View.INVISIBLE);
		}
		
		if (info_layout1 != null && info_layout1.getVisibility() == View.VISIBLE) {
			ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setDuration(200);
			scaleAnimation.setInterpolator(mContext, android.R.anim.accelerate_interpolator);
			scaleAnimation.setFillAfter(true);
			info_layout1.startAnimation(scaleAnimation);
			info_layout1.setVisibility(View.INVISIBLE);
		}

	}
	ArrayList<String> fontsList=new ArrayList<String>();
	private void download() {
		if (isDownloading) {
			return;
		}
		isDownloading = true;
		download_progressbar.setVisibility(View.VISIBLE);
		download_progressbar.setProgress(0);
		FileDownloader fileDownloader = new FileDownloader(mContext, new FileDownloadListener() {

			@Override
			public void finish(String downloadUrl, String path) {
				isDownloading = false;
				isDownloaded = true;
				download_progressbar.setVisibility(View.GONE);
				SimpleToast.makeText(mContext, R.string.download_succsed, SimpleToast.LENGTH_SHORT).show();

				if (!new File(themePath, MConstants.config).exists()) {
					try {
						ZipUtils.upZipFile(new File(zipPath), themePath);
					} catch (ZipException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				fontsList=ThemeUtils.fontsPrase(new File(themePath, MConstants.fonts));
				if(fontsList!=null && fontsList.size()>0){
					final TipsDialog tipsDialog = new TipsDialog(mContext);
					tipsDialog.setMessage("温馨提示: \n" + "字体样式不存在，是否下载");
					tipsDialog.setOnDismissedListener(new OnDismissedListener() {

						@Override
						public void OnDialogDismissed() {
							
						}
					});
					tipsDialog.setCancelButton("取消", new OnClickListener() {

						@Override
						public void onClick(View v) {
							tipsDialog.dismiss();
						}
					});
					tipsDialog.setOkButton("确定", new OnClickListener() {

						@Override
						public void onClick(View v) {
							tipsDialog.dismiss();
							for(final String fonturl : fontsList){
								new Thread(new Runnable() {

									@Override
									public void run() {
										FontUtils.loadTtf(fonturl, new FontDownloadListener() {

											@Override
											public void finish(String fonturl, String path) {
											}

											@Override
											public void error(String fonturl) {
											}

											@Override
											public void downloading(String fonturl, int size) {
											}
										});

									}
								}).start();
							}
						}
					});
					tipsDialog.show();
				}

				if (!new File(themePath, MConstants.autor).exists()) {
					File autorFile=new File(themePath, MConstants.autor);
					PrintStream out = null ;						// 打印流对象用于输出
					try {
						out = new PrintStream(new FileOutputStream(autorFile, true));	// 追加文件
						out.println(themeName);
						out.println(themeAuthor);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (out != null) {
							out.close() ;							// 关闭打印流
						}
					}
				}
				
				theme_path = themePath;
				try {
					new File(theme_path, MConstants.isCloud).createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				isUsed = checkUsed(theme_path,false);
				updateView();
				sendBroadcast(new Intent(MConstants.ACTION_UPDATE_LOCAL_THEME));
			}

			@Override
			public void error(String downloadUrl) {
				isDownloading = false;
				download_progressbar.setVisibility(View.GONE);
				SimpleToast.makeText(mContext, R.string.download_faild, SimpleToast.LENGTH_SHORT).show();
			}

			@Override
			public void downloading(String downloadUrl, int size) {
				download_progressbar.setMax(100);
				download_progressbar.setProgress(size);
			}
		});
		fileDownloader.setDownloadurl(theme_url);
		fileDownloader.setFileDir(zipPath);
		fileDownloader.startDownload();
	}

	private void changeBottomButtonVisibility() {
		closeInfoView();
		if (locker_preview_button_layout.getVisibility() == View.VISIBLE) {
			Animation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
			translateAnimation.setDuration(200);
			translateAnimation.setInterpolator(mContext, android.R.anim.accelerate_interpolator);
			locker_preview_button_layout.startAnimation(translateAnimation);
			locker_preview_button_layout.setVisibility(View.INVISIBLE);
		} else {
			Animation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
			translateAnimation.setDuration(200);
			translateAnimation.setInterpolator(mContext, android.R.anim.accelerate_decelerate_interpolator);
			locker_preview_button_layout.startAnimation(translateAnimation);
			locker_preview_button_layout.setVisibility(View.VISIBLE);
		}
	}

	private void setPassword() {
		System.out.println("1111");
		if (themeConfig != null) {
			LockerInfo lockerInfo = themeConfig.getLockerInfo();
			if (lockerInfo != null) {
				if (LockerInfo.StyleLove == lockerInfo.getStyleId()) {
					Intent intent = new Intent(mContext, CreateLoveLockPasswordActivity.class);
					intent.putExtra("theme_path", theme_path);
					intent.putExtra("theme_sendBroadcast", true);
					startActivity(intent);
				} else if (LockerInfo.StyleNinePattern == lockerInfo.getStyleId()) {
					Intent intent = new Intent(mContext, CreateGesturePasswordActivity.class);
					intent.putExtra("theme_path", theme_path);
					intent.putExtra("theme_sendBroadcast", true);
					startActivity(intent);
				} else if (LockerInfo.StyleTwelvePattern == lockerInfo.getStyleId()) {
					Intent intent = new Intent(mContext, CreateGesture12PasswordActivity.class);
					intent.putExtra("theme_path", theme_path);
					intent.putExtra("theme_sendBroadcast", true);
					startActivity(intent);
				} else if (LockerInfo.StyleImagePassword == lockerInfo.getStyleId()) {
					Intent intent = new Intent(mContext, CreatePasswordLockActivity.class);
					intent.putExtra("theme_path", theme_path);
					intent.putExtra("theme_sendBroadcast", true);
					startActivity(intent);
				} else if (LockerInfo.StyleWordPassword == lockerInfo.getStyleId()) {
					Intent intent = new Intent(mContext, CreateWordPasswordLockActivity.class);
					intent.putExtra("theme_path", theme_path);
					intent.putExtra("theme_sendBroadcast", true);
					startActivity(intent);
				}else if (LockerInfo.StyleNumPassword == lockerInfo.getStyleId()) {
					Intent intent = new Intent(mContext, CreateNumPasswordLockActivity.class);
					intent.putExtra("theme_path", theme_path);
					intent.putExtra("theme_sendBroadcast", true);
					startActivity(intent);
				} else if (LockerInfo.StyleNone == lockerInfo.getStyleId()) {
					LockApplication.getInstance().getConfig().setThemeName(theme_path, true);
				} else if (LockerInfo.StyleCouple == lockerInfo.getStyleId()) {
					LockApplication.getInstance().getConfig().setThemeName(theme_path, true);
				} else if (LockerInfo.StyleSlide == lockerInfo.getStyleId()) {
					LockApplication.getInstance().getConfig().setThemeName(theme_path, true);
				} else if (LockerInfo.StyleFree == lockerInfo.getStyleId()) {
					LockApplication.getInstance().getConfig().setThemeName(theme_path, true);
				}else if (LockerInfo.StyleApp == lockerInfo.getStyleId()) {
					LockApplication.getInstance().getConfig().setThemeName(theme_path, true);
				}
			}

		}
		finish();
	}

	@Override
	public void OnShareItemClick(int id) {
		String previewPath = null;
		if (FROM_FEATURED) {
			previewPath = themePath + "/preview";
		} else {
			previewPath = theme_path + "/preview";
		}
		switch (id) {
		case 1:// 微信
			if (!TextUtils.isEmpty(previewPath))
				new ShareUtils(mActivity).shareContentToWeixin(
						com.lockstudio.sticklocker.util.ImageLoader.getInstance().decodeSampledBitmapFromResource(previewPath), true);
			break;
		case 2:// 朋友圈
			if (!TextUtils.isEmpty(previewPath))
				new ShareUtils(mActivity).shareContentToWeixin(
						com.lockstudio.sticklocker.util.ImageLoader.getInstance().decodeSampledBitmapFromResource(previewPath), false);
			break;
		case 3:// QQ好友
			if (!TextUtils.isEmpty(previewPath))
				new ShareUtils(mActivity).shareContentToQQ(previewPath);
			break;
		case 4:// 微博
			try {
				if (!TextUtils.isEmpty(previewPath))
					new ShareUtils(mActivity).sendWeiBoMessage(
							com.lockstudio.sticklocker.util.ImageLoader.getInstance().decodeSampledBitmapFromResource(previewPath),
							getString(R.string.share_weibo_content));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	private void uploadTheme(final String name, final String author, final String contact, final int upload_status) {
		SimpleToast.makeText(mContext, R.string.lock_theme_uploading, SimpleToast.LENGTH_SHORT).show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				UploadThemeUitls uploadThemeUitls = new UploadThemeUitls(mContext);
				ArrayList<File> configFiles = new ArrayList<File>();
				File[] files = new File(theme_path).listFiles();
				for (File file : files) {
					if (file.getName().endsWith(".zip")) {
						continue;
					}
					if (file.getName().equals("preview")) {
						continue;
					}
					configFiles.add(file);
				}
				String zipPath = theme_path + "/" + new File(theme_path).getName() + ".zip";
				String uploadZipPath = theme_path + "/" + "upload.zip";
				if (new File(zipPath).exists()) {
					new File(zipPath).delete();
				}
				if (new File(uploadZipPath).exists()) {
					new File(uploadZipPath).delete();
				}
				try {
					ZipUtils.zipFiles(configFiles, new File(zipPath));
				} catch (Exception e) {
					e.printStackTrace();
				}

				ArrayList<File> uploadFiles = new ArrayList<File>();
				uploadFiles.add(new File(zipPath));
				uploadFiles.add(new File(theme_path, "preview"));

				try {
					ZipUtils.zipFiles(uploadFiles, new File(uploadZipPath));
				} catch (Exception e) {
					e.printStackTrace();
				}

				boolean b = false;
				try {
					b = uploadThemeUitls.upload(uploadZipPath, name, author, contact, upload_status);
				} catch (Exception e) {
					e.printStackTrace();
				}
				new File(theme_path, MConstants.uploading).delete();
				if (b) {

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								new File(theme_path, MConstants.uploaded).createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
							UploadSucceedDialog uploadSucceedDialog = new UploadSucceedDialog(mContext);
							if (!isDestroy) {
								uploadSucceedDialog.show();
							}

						}
					});
				} else {

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							UploadFailedDialog uploadFailedDialog = new UploadFailedDialog(mContext);
							uploadFailedDialog.setOnOkClickListener(new OnOkClickListener() {

								@Override
								public void OnOkClick() {
									try {
										new File(theme_path, MConstants.uploading).createNewFile();
									} catch (IOException e) {
										e.printStackTrace();
									}
									uploadTheme(name, author, contact, upload_status);
								}
							});
							if (!isDestroy) {
								uploadFailedDialog.show();
							}

						}
					});
				}

			}
		}).start();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isDestroy = true;
	}

	@Override
	public void OnUploadItemClick(final int id) {
		if (id == 5 || id == 6) {
			if (!new File(theme_path, MConstants.uploaded).exists()) {
				if (!new File(theme_path, MConstants.uploading).exists()) {
					final ContributeDialog contributeDialog = new ContributeDialog(this);
					contributeDialog.setTipsTextType(id);
					contributeDialog.setContributeClickListener(new ContributeClickListener() {

						@Override
						public void OnContributeClickListener(final String name, final String author, final String contact) {
							contributeDialog.dismiss();
							try {
								new File(theme_path, MConstants.uploading).createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
							if (id == 5) {
								uploadTheme(name, author, contact, 1);
							} else {
								uploadTheme(name, author, contact, 4);
							}
						}
					});
					contributeDialog.show();
				} else {
					SimpleToast.makeText(mContext, "正在上传中...", SimpleToast.LENGTH_SHORT).show();
				}
			} else {
				SimpleToast.makeText(mContext, R.string.lock_theme_is_uploaded, SimpleToast.LENGTH_SHORT).show();
			}

		} else {
			OnShareItemClick(id);
		}

	}
}
