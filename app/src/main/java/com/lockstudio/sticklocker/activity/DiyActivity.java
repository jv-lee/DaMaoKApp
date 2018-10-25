package com.lockstudio.sticklocker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseDialog;
import com.lockstudio.sticklocker.base.BaseFragmentActivity;
import com.lockstudio.sticklocker.model.ImageStickerInfo;
import com.lockstudio.sticklocker.model.LockerInfo;
import com.lockstudio.sticklocker.model.StickerInfo;
import com.lockstudio.sticklocker.model.ThemeConfig;
import com.lockstudio.sticklocker.model.TimerStickerInfo;
import com.lockstudio.sticklocker.model.WordStickerInfo;
import com.lockstudio.sticklocker.util.ChooseBatteryUitls;
import com.lockstudio.sticklocker.util.ChooseBatteryUitls.OnPluginSelectorListener3;
import com.lockstudio.sticklocker.util.ChooseLockerUitls;
import com.lockstudio.sticklocker.util.ChooseLockerUitls.OnLockerSelectorListener;
import com.lockstudio.sticklocker.util.ChoosePluginUitls;
import com.lockstudio.sticklocker.util.ChoosePluginUitls.OnPluginSelectorListener;
import com.lockstudio.sticklocker.util.ChooseStickerUtils;
import com.lockstudio.sticklocker.util.ChooseTimeUitls;
import com.lockstudio.sticklocker.util.ChooseTimeUitls.OnPluginSelectorListener1;
import com.lockstudio.sticklocker.util.ChooseWeatherUitls;
import com.lockstudio.sticklocker.util.ChooseWeatherUitls.OnPluginSelectorListener2;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.FileUtils;
import com.lockstudio.sticklocker.util.ImageLoader;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.PluginTextEditUtils;
import com.lockstudio.sticklocker.util.PluginTextEditUtils.OnTextChangeListener;
import com.lockstudio.sticklocker.util.ThemeUtils;
import com.lockstudio.sticklocker.view.ControllerContainerView;
import com.lockstudio.sticklocker.view.ControllerContainerView2;
import com.lockstudio.sticklocker.view.LockContainer;
import com.lockstudio.sticklocker.view.LockContainer.ToggleListener;
import com.lockstudio.sticklocker.view.SimpleToast;
import com.lockstudio.sticklocker.view.TimerSettingDialog;
import com.lockstudio.sticklocker.view.TimerSettingDialog.OnEditTextOkClickListener;
import com.lockstudio.sticklocker.view.TipsDialog;

import java.io.File;
import java.io.IOException;

import cn.opda.android.activity.R;

public class DiyActivity extends BaseFragmentActivity implements OnClickListener,ToggleListener {
	private LockContainer lockContainer;
	private LinearLayout diy_tab_wallpaper, diy_tab_word, diy_tab_plugin, diy_tab_sticker, diy_tab_locker;
	private ControllerContainerView controller_container_layout;
	private ControllerContainerView2 controller_container_layout_2;
	private String theme_path;
	private ChooseStickerUtils chooseStickerUtils;
	private boolean exit = false;
	private String tempWallpaper;
	private int maxThumbnailWidth, maxThumbnailHeight;
	private TextView diy_tab_plugin_textview, diy_tab_locker_textview;
	private LinearLayout diy_tab,diy_controller;
	private boolean animStart;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diy);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			sp = getSharedPreferences("default.cfg", Context.MODE_MULTI_PROCESS);
		} else {
			sp = getSharedPreferences("default.cfg", Context.MODE_PRIVATE);
		}
		if(!sp.getBoolean("newGuide", false)){
			Intent intent = new Intent(mContext, MiuiDetailsActivity.class);
			intent.putExtra("flag", 8);
			startActivity(intent);
			sp.edit().putBoolean("newGuide", true).commit();
		}
		
		maxThumbnailWidth = LockApplication.getInstance().getConfig().getScreenWidth() / 3;
		maxThumbnailHeight = maxThumbnailWidth * 16 / 9;

		initViewAndEvent();
		deleteTempWallpaper();
		lockContainer = (LockContainer) findViewById(R.id.lockcontainer);
		lockContainer.setControllerContainerLayout(controller_container_layout);
		lockContainer.setControllerContainerLayout2(controller_container_layout_2);
		lockContainer.init();
		lockContainer.setToggleListener(this);
		controller_container_layout.setControllerContainerLayout2(controller_container_layout_2);
		controller_container_layout_2.setControllerContainerLayout(controller_container_layout);
		controller_container_layout_2.setDiyActivity(this);
		theme_path = getIntent().getStringExtra("theme_path");
		if (!TextUtils.isEmpty(theme_path)) {
			ThemeConfig themeConfig = ThemeUtils.parseConfig(mContext, new File(theme_path, MConstants.config).getAbsolutePath());
			if(themeConfig!=null){
				lockContainer.addTheme(themeConfig);
				if (!TextUtils.isEmpty(themeConfig.getWallpaper())) {
					saveTempWallpaperByPath(themeConfig.getWallpaper());
				}
			}
		}

		String wallpaperUrl = getIntent().getStringExtra("wallpaperUrl");
		if (!TextUtils.isEmpty(wallpaperUrl)) {
			Bitmap bitmap = VolleyUtil.instance().getBitmapForUrl(wallpaperUrl);
			if (bitmap != null) {
				saveTempWallpaperByUrl(wallpaperUrl);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					lockContainer.setBackground(DrawableUtils.bitmap2Drawable(this, bitmap));
				} else {
					lockContainer.setBackgroundDrawable(DrawableUtils.bitmap2Drawable(this, bitmap));
				}
			}
		}

		final String imageUrl = getIntent().getStringExtra("imageUrl");
		String thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");

		boolean imageDownloaded = getIntent().getBooleanExtra("imageDownloaded", false);
		if (imageDownloaded && !TextUtils.isEmpty(imageUrl)) {
			Bitmap bitmap = VolleyUtil.instance().getBitmapForUrl(imageUrl);
			if (bitmap != null) {
				saveTempWallpaperByUrl(imageUrl);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					lockContainer.setBackground(DrawableUtils.bitmap2Drawable(this, bitmap));
				} else {
					lockContainer.setBackgroundDrawable(DrawableUtils.bitmap2Drawable(this, bitmap));
				}
			}
		} else {
			if (!TextUtils.isEmpty(thumbnailUrl)) {
				Bitmap thumb = VolleyUtil.instance().getBitmapForUrl(thumbnailUrl, maxThumbnailWidth, maxThumbnailHeight);
				if (thumb != null) {
					saveTempWallpaperByUrl(thumbnailUrl);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						lockContainer.setBackground(DrawableUtils.bitmap2Drawable(this, thumb));
					} else {
						lockContainer.setBackgroundDrawable(DrawableUtils.bitmap2Drawable(this, thumb));
					}

				}
			}

			if (!TextUtils.isEmpty(imageUrl)) {
				ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap response) {
						if (response != null) {
							saveTempWallpaperByUrl(imageUrl);
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
								lockContainer.setBackground(DrawableUtils.bitmap2Drawable(mContext, response));
							} else {

								lockContainer.setBackgroundDrawable(DrawableUtils.bitmap2Drawable(mContext, response));
							}
							String cacheKey = com.android.volley.toolbox.ImageLoader.getCacheKey(imageUrl, 0, 0, ImageView.ScaleType.CENTER_INSIDE);
							VolleyUtil.instance().putBitmap(cacheKey, response);
						}
					}
				}, 0, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
				VolleyUtil.instance().addRequest(imageRequest);
			}
		}
		
		String path = getIntent().getStringExtra("path");
		if (path != null && !TextUtils.isEmpty(path)) {
			saveTempWallpaperByPath(path);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				lockContainer.setBackground(DrawableUtils.bitmap2Drawable(mContext, ImageLoader.getInstance().decodeSampledBitmapFromResource(path)));
			} else {
				lockContainer.setBackgroundDrawable(DrawableUtils.bitmap2Drawable(mContext,
						ImageLoader.getInstance().decodeSampledBitmapFromResource(path)));
			}
		}
	}

	private void initViewAndEvent() {
		diy_tab_wallpaper = (LinearLayout) findViewById(R.id.diy_tab_wallpaper);
		diy_tab_word = (LinearLayout) findViewById(R.id.diy_tab_word);
		diy_tab_plugin = (LinearLayout) findViewById(R.id.diy_tab_plugin);
		diy_tab_sticker = (LinearLayout) findViewById(R.id.diy_tab_sticker);
		diy_tab_locker = (LinearLayout) findViewById(R.id.diy_tab_locker);

		diy_tab_wallpaper.setOnClickListener(this);
		diy_tab_word.setOnClickListener(this);
		diy_tab_plugin.setOnClickListener(this);
		diy_tab_sticker.setOnClickListener(this);
		diy_tab_locker.setOnClickListener(this);

		diy_tab_plugin_textview = (TextView) findViewById(R.id.diy_tab_plugin_textview);
		diy_tab_locker_textview = (TextView) findViewById(R.id.diy_tab_locker_textview);

		diy_tab = (LinearLayout) findViewById(R.id.diy_tab);
		diy_controller = (LinearLayout) findViewById(R.id.diy_controller);
		controller_container_layout = (ControllerContainerView) findViewById(R.id.controller_container_layout);
		controller_container_layout.setDiyTabView(diy_tab);
		controller_container_layout_2 = (ControllerContainerView2) findViewById(R.id.controller_container_layout_2);
		findViewById(R.id.diy_ok_image).setOnClickListener(this);
		findViewById(R.id.diy_cancel_image).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		lockContainer.clearAllFocuse(false);
		int i = v.getId();
		if (i == R.id.diy_ok_image) {
			if (DeviceInfoUtils.sdMounted()) {

				LockerInfo lockerInfo = lockContainer.getLockInfo();
				if (lockerInfo != null) {
					lockContainer.clearAllFocuse(true);
					SimpleToast.makeText(this, R.string.locker_saving_tips, SimpleToast.LENGTH_LONG).show();
					new Thread(new Runnable() {

						@Override
						public void run() {
							ThemeConfig themeConfig = new ThemeConfig();
							themeConfig.setScreenHeight(LockApplication.getInstance().getConfig().getScreenHeight());
							themeConfig.setScreenWidth(LockApplication.getInstance().getConfig().getScreenWidth());
							themeConfig.setThemePath(theme_path);
							themeConfig.setLockerInfo(lockContainer.getLockInfo());
							themeConfig.setStickerInfoList(lockContainer.getStickerInfos());
							themeConfig.setThemePreview(lockContainer.getLockPreview());
							themeConfig.setWallpaperColor(lockContainer.getWallpaperColor());
							if (!TextUtils.isEmpty(tempWallpaper) && new File(tempWallpaper).exists()) {
								themeConfig.setWallpaper(tempWallpaper);
							}
							ThemeUtils.saveTheme(mContext, themeConfig);
							finish();

							Intent intent = new Intent(MConstants.ACTION_UPDATE_LOCAL_THEME);
							intent.putExtra("theme_path", theme_path);
							sendBroadcast(intent);
						}
					}).start();

				} else {
					SimpleToast.makeText(this, R.string.toast_locker_not_add, SimpleToast.LENGTH_SHORT).show();
					showLockerChooseView();
				}
			} else {
				SimpleToast.makeText(this, R.string.sdcard_not_mounted, SimpleToast.LENGTH_SHORT).show();
			}


		} else if (i == R.id.diy_cancel_image) {
			if (!lockContainer.isDiyChange()) {
				finish();
			} else {
				final TipsDialog tipsDialog = new TipsDialog(mContext);
				tipsDialog.setOnDismissedListener(new BaseDialog.OnDismissedListener() {
					@Override
					public void OnDialogDismissed() {
						if (exit) {
							finish();
						}
						exit = false;
					}
				});
				tipsDialog.setMessage(R.string.dialog_tips_lock_not_save);
				tipsDialog.setCancelButton(R.string.dialog_button_giveup, new OnClickListener() {

					@Override
					public void onClick(View v) {
						exit = true;
						tipsDialog.dismiss();

					}
				});
				tipsDialog.setOkButton(R.string.dialog_button_continue_diy, null);
				tipsDialog.show();
			}

		} else if (i == R.id.diy_tab_wallpaper) {
			startActivityForResult(new Intent(mContext, WallpaperStoreActivity.class), MConstants.REQUEST_CODE_WALLPAPER);
			overridePendingTransition(R.anim.activity_in, 0);

		} else if (i == R.id.diy_tab_word) {
			PluginTextEditUtils pluginTextEditUtils = new PluginTextEditUtils(mContext, "");
			pluginTextEditUtils.setOnTextChangeListener(new OnTextChangeListener() {

				@Override
				public void textChange(String text) {
					controller_container_layout.removeAllViews();
					WordStickerInfo wordStickerInfo = new WordStickerInfo();
					wordStickerInfo.textSize = (int) getResources().getDimension(R.dimen.defaultStickerTextSize);
					wordStickerInfo.styleId = StickerInfo.StyleWord;
					wordStickerInfo.textColor = Color.WHITE;
					wordStickerInfo.shadowColor = Color.TRANSPARENT;
					wordStickerInfo.text = text;
					Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
					paint.setTextSize(wordStickerInfo.textSize);
					int width = (int) paint.measureText(wordStickerInfo.text);
//					wordStickerInfo.x = (LockApplication.getInstance().getConfig().getScreenWidth() - width) / 2;
					wordStickerInfo.x = DensityUtil.dip2px(mContext, 30);
					wordStickerInfo.y = 250;
					wordStickerInfo.angle = 0;
					lockContainer.addSticker(wordStickerInfo, true);
				}
			});
			controller_container_layout.removeAllViews();
			controller_container_layout.addView(pluginTextEditUtils.getView());


		} else if (i == R.id.diy_tab_plugin) {
			if (!diy_tab_plugin_textview.isSelected()) {
				ChoosePluginUitls choosePluginUitls = new ChoosePluginUitls(this);
				controller_container_layout_2.removeAllViews();
				controller_container_layout_2.addView(choosePluginUitls.getView());
				choosePluginUitls.setOnPluginSelectorListener(new OnPluginSelectorListener() {

					@Override
					public void selectPlugin(StickerInfo stickerInfo) {
						controller_container_layout_2.removeAllViews();
						if (stickerInfo.styleId == StickerInfo.StyleTimer) {
							final TimerSettingDialog timerSettingDialog = new TimerSettingDialog(mContext);
							timerSettingDialog.setEditTextOkClickListener(new OnEditTextOkClickListener() {

								@Override
								public void OnEditTextOkClick(String text, String date) {
									timerSettingDialog.dismiss();
									TimerStickerInfo timerStickerInfo = new TimerStickerInfo();
									timerStickerInfo.styleId = StickerInfo.StyleTimer;
									timerStickerInfo.text_title = text;
									timerStickerInfo.text_time = date;
									timerStickerInfo.textSize1 = DensityUtil.dip2px(mContext, 26);
									timerStickerInfo.textSize2 = DensityUtil.dip2px(mContext, 18);
									timerStickerInfo.textColor = Color.WHITE;
									timerStickerInfo.shadowColor = Color.TRANSPARENT;
									timerStickerInfo.x = 200;
									timerStickerInfo.y = 250;
									timerStickerInfo.angle = 0;
									if (new File(MConstants.TTF_PATH + "en_one.ttf").exists()) {
										timerStickerInfo.font = MConstants.TTF_PATH + "en_one.ttf";
									}
									lockContainer.addSticker(timerStickerInfo, true);

								}
							});
							timerSettingDialog.show();
						} else if (stickerInfo.styleId == StickerInfo.StyleTime) {
							timeUtils();
						} else if (stickerInfo.styleId == StickerInfo.StyleWeather) {
							weatherUtils();
						} else if (stickerInfo.styleId == StickerInfo.StyleBattery) {
							batteryUtils();
						} else if (stickerInfo.styleId == StickerInfo.StyleMessage) {
							Intent intent = new Intent(mContext, MiuiDetailsActivity.class);
							intent.putExtra("flag", 10);
							startActivity(intent);
						} else {
							lockContainer.addSticker(stickerInfo, true);
						}
					}
				});
				diy_tab_plugin_textview.setSelected(true);
			}

		} else if (i == R.id.diy_tab_sticker) {//			if (chooseStickerUtils == null) {
//				chooseStickerUtils = new ChooseStickerUtils(this);
//			}
			LockApplication.getInstance().getConfig().setFrom_id(ChooseStickerUtils.FROM_STICKER);
//			controller_container_layout.removeAllViews();
//			controller_container_layout.addView(chooseStickerUtils.getView());
//			chooseStickerUtils.setOnImageSelectorListener(new OnImageSelectorListener() {
//
//				@Override
//				public void selectImage(String imageUrl) {
//					Bitmap bitmap = VolleyUtil.instance().getBitmapForUrl(imageUrl);
//					if (bitmap != null) {
//						ImageStickerInfo imageStickerInfo = new ImageStickerInfo();
//						imageStickerInfo.styleId = StickerInfo.StyleImage;
//						imageStickerInfo.imageBitmap = bitmap;
//						imageStickerInfo.width = (int) getResources().getDimension(R.dimen.defaultStickerImageWidth);
//						imageStickerInfo.height = (int) getResources().getDimension(R.dimen.defaultStickerImageWidth);
//						imageStickerInfo.x = 150;
//						imageStickerInfo.y = 250;
//						imageStickerInfo.angle = 0;
//						lockContainer.addSticker(imageStickerInfo, true);
//					}
//				}
//			});
			Intent intent = new Intent(mContext, SelectImageActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivityForResult(intent, MConstants.REQUEST_CODE_STICKER_EDIT);


		} else if (i == R.id.diy_tab_locker) {
			if (!diy_tab_locker_textview.isSelected()) {
				showLockerChooseView();
				diy_tab_locker_textview.setSelected(true);
			}


		} else {
		}
	}

	public void showLockerChooseView() {
		final LockerInfo selectLocker = lockContainer.getLockInfo();
		ChooseLockerUitls chooseLockerUitls = new ChooseLockerUitls(this);
		chooseLockerUitls.setSelectLocker(selectLocker);
		controller_container_layout_2.removeAllViews();
		controller_container_layout_2.addView(chooseLockerUitls.getView());
		chooseLockerUitls.setOnLockerSelectorListener(new OnLockerSelectorListener() {

			@Override
			public void selectLocker(LockerInfo lockerInfo) {
				controller_container_layout_2.removeAllViews();
				if (selectLocker == null || selectLocker.getStyleId() != lockerInfo.getStyleId()) {
					lockContainer.addLocker(lockerInfo, true);
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			if (controller_container_layout.getChildCount() > 0) {
				controller_container_layout.removeAllViews();
				return true;
			} else if (controller_container_layout_2.getChildCount() > 0) {
				controller_container_layout_2.removeAllViews();
				return true;
			} else {
				if (!lockContainer.isDiyChange()) {
					finish();
				} else {
					final TipsDialog tipsDialog = new TipsDialog(mContext);
					tipsDialog.setOnDismissedListener(new BaseDialog.OnDismissedListener() {
						@Override
						public void OnDialogDismissed() {
							if (exit) {
								finish();
							}
							exit = false;
						}
					});
					tipsDialog.setMessage(R.string.dialog_tips_lock_not_save);
					tipsDialog.setCancelButton(R.string.dialog_button_giveup, new OnClickListener() {

						@Override
						public void onClick(View v) {
							exit = true;
							tipsDialog.dismiss();

						}
					});
					tipsDialog.setOkButton(R.string.dialog_button_continue_diy, null);
					tipsDialog.show();
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (null == data) {
			return;
		}
		switch (requestCode) {

		case MConstants.REQUEST_CODE_WALLPAPER:
			if (resultCode == MConstants.RESULT_CODE_ALBUM) {
				String path = data.getStringExtra("path");
				if (path != null && !TextUtils.isEmpty(path)) {
					saveTempWallpaperByPath(path);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						lockContainer.setBackground(DrawableUtils.bitmap2Drawable(mContext, ImageLoader.getInstance().decodeSampledBitmapFromResource(path)));
					} else {
						lockContainer.setBackgroundDrawable(DrawableUtils.bitmap2Drawable(mContext,
								ImageLoader.getInstance().decodeSampledBitmapFromResource(path)));
					}
					controller_container_layout.removeAllViews();
				}
			}
			break;
		case MConstants.REQUEST_CODE_STICKER:
			String picturePath;
			Uri u = data.getData();
			if (!TextUtils.isEmpty(u.getAuthority())) {
				Cursor cursor = getContentResolver().query(u, new String[] { MediaStore.Images.Media.DATA }, null, null, null);
				if (null == cursor) {
					SimpleToast.makeText(this, R.string.resource_not_found, SimpleToast.LENGTH_SHORT).show();
					return;
				}
				cursor.moveToFirst();
				picturePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				cursor.close();
			} else {
				picturePath = u.getPath();
			}

			Intent intent = new Intent(mContext, IconImageEditActivity.class);
			intent.putExtra("resource_path", picturePath);
			startActivityForResult(intent, MConstants.REQUEST_CODE_STICKER_EDIT);
			break;
		case MConstants.REQUEST_CODE_STICKER_EDIT:
			byte[] iconByte = data.getByteArrayExtra("iconByte");
			if (iconByte != null) {
				Bitmap bitmap = DrawableUtils.byte2Bitmap(mContext, iconByte);
				if (LockApplication.getInstance().getConfig().getFrom_id() == ChooseStickerUtils.FROM_STICKER) {
					if (bitmap != null) {
						ImageStickerInfo imageStickerInfo = new ImageStickerInfo();
						imageStickerInfo.styleId = StickerInfo.StyleImage;
						imageStickerInfo.imageBitmap = bitmap;
						imageStickerInfo.width = (int) getResources().getDimension(R.dimen.defaultStickerImageWidth);
						imageStickerInfo.height = (int) getResources().getDimension(R.dimen.defaultStickerImageWidth);
						imageStickerInfo.x = 100;
						imageStickerInfo.y = 100;
						imageStickerInfo.angle = 0;
						lockContainer.addSticker(imageStickerInfo, true);
					}
				} else {
					lockContainer.setResourceImage(bitmap);
				}
			}
			break;
		default:
			break;
		}

	}

	private void saveTempWallpaperByPath(final String wallpaper) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					tempWallpaper = getFilesDir() + "/tempWallpaper";
					FileUtils.copyFile(new File(wallpaper), new File(tempWallpaper));
				} catch (IOException e) {
					e.printStackTrace();
					tempWallpaper = null;
				}
			}
		}).start();

	}

	private void saveTempWallpaperByUrl(final String imageUrl) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				tempWallpaper = getFilesDir() + "/tempWallpaper";
				VolleyUtil.instance().writeBitmapToFile(imageUrl, getFilesDir() + "", "tempWallpaper");

			}
		}).start();
	}

	private void deleteTempWallpaper() {
		File f = new File(getFilesDir() + "/tempWallpaper");
		if (f.exists()) {
			f.delete();
		}
	}


	public void clearTabSelecter() {
		diy_tab_plugin_textview.setSelected(false);
		diy_tab_locker_textview.setSelected(false);
	}

	public void timeUtils() {
		ChooseTimeUitls chooseTimeUitls = new ChooseTimeUitls(this);
		controller_container_layout_2.removeAllViews();
		controller_container_layout_2.addView(chooseTimeUitls.getView());
		chooseTimeUitls.setOnPluginSelectorListener1(new OnPluginSelectorListener1() {

			@Override
			public void selectPlugin(StickerInfo stickerInfo) {
				controller_container_layout_2.removeAllViews();
				lockContainer.addSticker(stickerInfo, true);
			}
		});
	}

	public void weatherUtils() {
		ChooseWeatherUitls chooseWeatherUitls = new ChooseWeatherUitls(this);
		controller_container_layout_2.removeAllViews();
		controller_container_layout_2.addView(chooseWeatherUitls.getView());
		chooseWeatherUitls.setOnPluginSelectorListener2(new OnPluginSelectorListener2() {

			@Override
			public void selectPlugin(StickerInfo stickerInfo) {
				controller_container_layout_2.removeAllViews();
				lockContainer.addSticker(stickerInfo, true);
			}
		});
	}
	
	public void batteryUtils(){
		ChooseBatteryUitls chooseBatteryUitls = new ChooseBatteryUitls(this);
		controller_container_layout_2.removeAllViews();
		controller_container_layout_2.addView(chooseBatteryUitls.getView());
		chooseBatteryUitls.setOnPluginSelectorListener3(new OnPluginSelectorListener3() {

			@Override
			public void selectPlugin(StickerInfo stickerInfo) {
				controller_container_layout_2.removeAllViews();
				lockContainer.addSticker(stickerInfo, true);
			}
		});
	}

	@Override
	public void toggleTabView() {
		if(!animStart){
			animStart = true;
			if(diy_tab.getVisibility()==View.VISIBLE){
				Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.out_to_bottom);
				animation.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {
						
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						diy_tab.setVisibility(View.INVISIBLE);
						//diy_controller.setVisibility(View.INVISIBLE);
						animStart = false;
					}
				});
				diy_tab.startAnimation(animation);
				Animation animation2 = AnimationUtils.loadAnimation(mContext, R.anim.out_to_top);
				//diy_controller.startAnimation(animation2);
			}else{
				diy_tab.setVisibility(View.VISIBLE);
				//diy_controller.setVisibility(View.VISIBLE);
				Animation animation2 = AnimationUtils.loadAnimation(mContext, R.anim.in_from_top);
				//diy_controller.startAnimation(animation2);
				Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.in_from_bottom);
				animation.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {
						
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						animStart = false;
					}
				});
				diy_tab.startAnimation(animation);
				
			}
			
		}
	}
}
