package com.lockstudio.sticklocker.activity;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.HASH;
import com.lockstudio.sticklocker.util.ImageLoader;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.ShareUtils;
import com.lockstudio.sticklocker.view.ShareDialog;
import com.lockstudio.sticklocker.view.ShareDialog.OnShareItemClickListener;
import com.lockstudio.sticklocker.view.SimpleToast;

import java.io.IOException;

import cn.opda.android.activity.R;

public class WallpaperPreviewActivity extends BaseActivity implements
		OnClickListener, OnShareItemClickListener {

	private final String TAG = "V5_WALLPAPER_PREVIEW_ACTIVITY";

	private final int MSG_IMAGE_DOWNLOAD_FINISH = 50;
	private final int MSG_INIT_IMAGE = 51;

	private ImageView wallpaper_image;
	private RelativeLayout left_button_layout, right_button_layout;
	private View paper_preview_buttom;
	private String thumbnailUrl, imageUrl;
	private boolean imageDownloaded = false;

	private int maxThumbnailWidth, maxThumbnailHeight;

	private int s_width;
	private int s_height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_wallpaper_preview);
		// Gavan获取屏幕宽高
		WindowManager manager = this.getWindowManager();
		DisplayMetrics outMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(outMetrics);
		s_width = outMetrics.widthPixels;
		s_height = outMetrics.heightPixels;

		System.out.println("s_width::" + s_width + "   s_height::" + s_height);

		imageUrl = getIntent().getStringExtra("image_url");
		thumbnailUrl = getIntent().getStringExtra("thumbnail_url");

		maxThumbnailWidth = LockApplication.getInstance().getConfig()
				.getScreenWidth() / 3;
		maxThumbnailHeight = maxThumbnailWidth * 16 / 9;

		initWeiBo();
		initWidgetsAndActions();
		initThumbnail();
		handler.sendEmptyMessageDelayed(MSG_INIT_IMAGE, 200);
	}

	private void initWeiBo() {

	}

	private void initWidgetsAndActions() {
		ImageView center_imageview = (ImageView) findViewById(R.id.activity_paper_preview_play);
		TextView left_textview = (TextView) findViewById(R.id.activity_paper_preview_phone);
		TextView right_textview = (TextView) findViewById(R.id.activity_paper_preview_share);
		TextView paper_preview_set = (TextView) findViewById(R.id.paper_preview_set);
		TextView paper_preview_diy = (TextView) findViewById(R.id.paper_preview_diy);
		center_imageview.setImageResource(R.drawable.text_use);
		wallpaper_image = (ImageView) findViewById(R.id.wallpaper_image);
		paper_preview_buttom = findViewById(R.id.paper_preview_buttom);
		paper_preview_buttom.setOnClickListener(this);
		left_textview.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.button_phone_selector),
				null, null);
		left_textview.setText(R.string.save_to_phone);
		right_textview.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.button_share_selector),
				null, null);
		right_textview.setText(R.string.share);
		left_textview.setOnClickListener(this);
		right_textview.setOnClickListener(this);
		paper_preview_set.setOnClickListener(this);
		paper_preview_diy.setOnClickListener(this);
		center_imageview.setOnClickListener(this);
		wallpaper_image.setOnClickListener(this);

		left_button_layout = (RelativeLayout) findViewById(R.id.left_button_layout);
		right_button_layout = (RelativeLayout) findViewById(R.id.right_button_layout);
	}

	private void initThumbnail() {

		if (!TextUtils.isEmpty(thumbnailUrl)) {
			VolleyUtil.instance().setUrlImage(
					VolleyUtil.instance().getRequestQueue(), wallpaper_image,
					thumbnailUrl, 0, 0, maxThumbnailWidth, maxThumbnailHeight);
		}
	}

	private void initImage() {
		if (!TextUtils.isEmpty(imageUrl)) {
			final Bitmap bitmap = VolleyUtil.instance().getBitmapForUrl(
					imageUrl);
			if (bitmap != null) {
				wallpaper_image.setImageBitmap(bitmap);
				imageDownloaded = true;
				handler.sendEmptyMessage(MSG_IMAGE_DOWNLOAD_FINISH);
			} else {
				ImageRequest imageRequest = new ImageRequest(
						imageUrl,
						new Response.Listener<Bitmap>() {
							@Override
							public void onResponse(Bitmap response) {
								if (response != null) {
									wallpaper_image.setImageBitmap(response);
									imageDownloaded = true;
									String cacheKey = com.android.volley.toolbox.ImageLoader
											.getCacheKey(
													imageUrl,
													0,
													0,
													ImageView.ScaleType.CENTER_INSIDE);
									VolleyUtil.instance().putBitmap(cacheKey,
											response);
									handler.sendEmptyMessage(MSG_IMAGE_DOWNLOAD_FINISH);
								}
							}
						}, 0, 0, ImageView.ScaleType.CENTER_INSIDE,
						Bitmap.Config.RGB_565, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {

							}
						});
				VolleyUtil.instance().addRequest(imageRequest);
			}
		}
	}

	private String saveToAlbum() {
		String name = HASH.md5sum(imageUrl) + ".jpg";
		boolean success = VolleyUtil.instance().writeBitmapToFile(imageUrl,
				MConstants.USER_PHOTO_PATH, name);
		if (success) {
			sendBroadcast(new Intent(MConstants.ACTION_UPDATE_LOCAL_WALLPAPER));
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
					Uri.parse("file://" + MConstants.USER_PHOTO_PATH + name)));
			CustomEventCommit.commit(mContext.getApplicationContext(), TAG,
					"SAVE");
			SimpleToast.makeText(mContext, R.string.sdcard_save_success,
					SimpleToast.LENGTH_SHORT).show();
			return MConstants.USER_PHOTO_PATH + name;
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.activity_paper_preview_phone) {
			if (DeviceInfoUtils.sdMounted()) {
				saveToAlbum();
			} else {
				SimpleToast.makeText(mContext, R.string.sdcard_not_mounted,
						SimpleToast.LENGTH_SHORT).show();
			}

		} else if (i == R.id.activity_paper_preview_share) {
			CustomEventCommit.commit(mContext.getApplicationContext(), TAG,
					"SHARE");
			ShareDialog sd = new ShareDialog(this, 1);
			sd.setShareItemClickListener(this);
			sd.show();

		} else if (i == R.id.activity_paper_preview_play) {
			if (left_button_layout.getVisibility() == View.VISIBLE
					&& right_button_layout.getVisibility() == View.VISIBLE) {
				ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f,
						1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
						Animation.RELATIVE_TO_SELF, 1.0f);
				scaleAnimation.setDuration(200);
				scaleAnimation.setInterpolator(mContext,
						android.R.anim.accelerate_interpolator);

				ScaleAnimation scaleAnimation1 = new ScaleAnimation(1.0f, 0.0f,
						1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 1.0f);
				scaleAnimation1.setDuration(200);
				scaleAnimation1.setInterpolator(mContext,
						android.R.anim.accelerate_interpolator);

				left_button_layout.startAnimation(scaleAnimation);
				right_button_layout.startAnimation(scaleAnimation1);
				left_button_layout.setVisibility(View.INVISIBLE);
				right_button_layout.setVisibility(View.INVISIBLE);
			} else {
				ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f,
						0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f,
						Animation.RELATIVE_TO_SELF, 1.0f);
				scaleAnimation.setDuration(200);
				scaleAnimation.setInterpolator(mContext,
						android.R.anim.accelerate_decelerate_interpolator);

				ScaleAnimation scaleAnimation1 = new ScaleAnimation(0.0f, 1.0f,
						0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 1.0f);
				scaleAnimation1.setDuration(200);
				scaleAnimation1.setInterpolator(mContext,
						android.R.anim.accelerate_decelerate_interpolator);

				left_button_layout.startAnimation(scaleAnimation);
				right_button_layout.startAnimation(scaleAnimation1);
				left_button_layout.setVisibility(View.VISIBLE);
				right_button_layout.setVisibility(View.VISIBLE);
			}
			CustomEventCommit.commit(mContext.getApplicationContext(), TAG,
					"USE");

		} else if (i == R.id.paper_preview_diy) {// Intent intent = new Intent(this, DiyActivity.class);
			// intent.putExtra("imageUrl", imageUrl);
			// intent.putExtra("thumbnailUrl", thumbnailUrl);
			// intent.putExtra("imageDownloaded", imageDownloaded);
			// startActivity(intent);

			String goal = MConstants.IMAGECACHE_PATH + "natureCrop";
			Intent intent = new Intent(mContext, PaperEditActivity.class);
			intent.putExtra("image_path", goal);
			intent.putExtra("THUMBNAIL_URL", thumbnailUrl);
			intent.putExtra("IMAGE_URL", imageUrl);
			startActivity(intent);

			CustomEventCommit.commit(mContext.getApplicationContext(), TAG,
					"USE_TO_NEW");
			finish();

		} else if (i == R.id.paper_preview_set) {// String themeName =
			// LockApplication.getInstance().getConfig().getThemeName();
			// if (!TextUtils.isEmpty(themeName)) {
			// mContext.startService(new Intent(mContext, CoreService.class));
			// if (imageDownloaded) {
			// VolleyUtil.instance().writeBitmapToFile(imageUrl, themeName,
			// "wallpaper");
			// } else {
			// VolleyUtil.instance().writeBitmapToFile(thumbnailUrl, themeName,
			// "wallpaper");
			// }
			// LockApplication.getInstance().getConfig().setEnabled(true);
			// sendBroadcast(new Intent(MConstants.ACTION_LOCK_NOW));
			// finish();
			// } else {
			// SimpleToast.makeText(this, R.string.no_locker_theme_tips,
			// SimpleToast.LENGTH_SHORT).show();
			// }
			// CustomEventCommit.commit(mContext.getApplicationContext(), TAG,
			// "USE_TO_CURRENT");
			saveToAlbum();
			changeBottomButtonVisibility();
			SimpleToast.makeText(WallpaperPreviewActivity.this,
					R.string.wallpaper_setting, SimpleToast.LENGTH_SHORT)
					.show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					WallpaperManager wallpaperManager = WallpaperManager
							.getInstance(WallpaperPreviewActivity.this);
					try {
						Bitmap wallpaperBitmap;
						String wallpaperName = "temp_wallpaper";
						if (imageDownloaded) {
							VolleyUtil.instance().writeBitmapToFile(imageUrl,
									MConstants.IMAGECACHE_PATH, wallpaperName);
						} else {
							VolleyUtil.instance().writeBitmapToFile(
									thumbnailUrl, MConstants.IMAGECACHE_PATH,
									wallpaperName);
						}
						wallpaperBitmap = BitmapFactory
								.decodeFile(MConstants.IMAGECACHE_PATH
										+ wallpaperName);
						if (wallpaperBitmap != null) {
							// Gavan 尝试解决缩放问题
							Bitmap bit = sBitmap(wallpaperBitmap, s_width,
									s_width);
							wallpaperManager.setBitmap(bit);
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									SimpleToast.makeText(
											WallpaperPreviewActivity.this,
											R.string.wallpaper_setting_succsed,
											SimpleToast.LENGTH_SHORT).show();
								}
							});
						} else {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									SimpleToast.makeText(
											WallpaperPreviewActivity.this,
											R.string.wallpaper_setting_faild,
											SimpleToast.LENGTH_SHORT).show();
								}
							});
						}
					} catch (IOException e) {
						e.printStackTrace();
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								SimpleToast.makeText(
										WallpaperPreviewActivity.this,
										R.string.wallpaper_setting_faild,
										SimpleToast.LENGTH_SHORT).show();
							}
						});
					}

				}
			}).start();

			CustomEventCommit.commit(mContext.getApplicationContext(), TAG,
					"USE_TO_SYSTEM_WALLPAPER");

		} else if (i == R.id.wallpaper_image) {
			changeBottomButtonVisibility();

		} else {
		}
	}

	private void changeBottomButtonVisibility() {
		if (paper_preview_buttom.getVisibility() == View.VISIBLE) {
			Animation translateAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 1.0f);

			translateAnimation.setDuration(200);
			if (left_button_layout.getVisibility() == View.VISIBLE
					&& right_button_layout.getVisibility() == View.VISIBLE) {
				translateAnimation.setStartOffset(150);
				ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f,
						1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
						Animation.RELATIVE_TO_SELF, 1.0f);
				scaleAnimation.setDuration(200);
				scaleAnimation.setInterpolator(mContext,
						android.R.anim.accelerate_interpolator);

				ScaleAnimation scaleAnimation1 = new ScaleAnimation(1.0f, 0.0f,
						1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 1.0f);
				scaleAnimation1.setDuration(200);
				scaleAnimation1.setInterpolator(mContext,
						android.R.anim.accelerate_interpolator);

				left_button_layout.startAnimation(scaleAnimation);
				right_button_layout.startAnimation(scaleAnimation1);
				left_button_layout.setVisibility(View.INVISIBLE);
				right_button_layout.setVisibility(View.INVISIBLE);
			}

			translateAnimation.setInterpolator(mContext,
					android.R.anim.accelerate_interpolator);
			paper_preview_buttom.startAnimation(translateAnimation);
			paper_preview_buttom.setVisibility(View.INVISIBLE);
		} else {
			Animation translateAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 1.0f,
					Animation.RELATIVE_TO_SELF, 0.0f);
			translateAnimation.setDuration(200);
			translateAnimation.setInterpolator(mContext,
					android.R.anim.accelerate_decelerate_interpolator);
			paper_preview_buttom.startAnimation(translateAnimation);
			paper_preview_buttom.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void OnShareItemClick(int id) {
		String name = HASH.md5sum(imageUrl);
		String wallpaperPath = null;
		boolean success = VolleyUtil.instance().writeBitmapToFile(imageUrl,
				MConstants.IMAGECACHE_PATH, name);
		if (success) {
			wallpaperPath = MConstants.IMAGECACHE_PATH + name;
		}
		switch (id) {
		case 1:// 微信
			if (!TextUtils.isEmpty(wallpaperPath))
				new ShareUtils(mActivity)
						.shareContentToWeixin(
								ImageLoader.getInstance()
										.decodeSampledBitmapFromResource(
												wallpaperPath), true);
			break;
		case 2:// 朋友圈
			if (!TextUtils.isEmpty(wallpaperPath))
				new ShareUtils(mActivity)
						.shareContentToWeixin(
								ImageLoader.getInstance()
										.decodeSampledBitmapFromResource(
												wallpaperPath), false);
			break;
		case 3:// QQ好友
			if (!TextUtils.isEmpty(wallpaperPath))
				new ShareUtils(mActivity).shareContentToQQ(wallpaperPath);
			break;
		case 4:// 微博
			try {
				if (!TextUtils.isEmpty(wallpaperPath))
					new ShareUtils(mActivity).sendWeiBoMessage(
							com.lockstudio.sticklocker.util.ImageLoader
									.getInstance()
									.decodeSampledBitmapFromResource(
											wallpaperPath),
							getString(R.string.share_weibo_content));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		default:
			break;
		}

	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			int what = msg.what;
			handler.removeMessages(what);
			switch (what) {
			case MSG_IMAGE_DOWNLOAD_FINISH:
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

	/**
	 * 设置为壁纸的图片应该填充满整个屏幕
	 * 
	 * @param bitMap
	 * @return
	 */
	public static Bitmap sBitmap(Bitmap bitmap, int screenWidth,
			int screenHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float scaleWidth = ((float) screenWidth) / width;
		float scaleHeight = ((float) screenHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);// 缩放
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}
}
