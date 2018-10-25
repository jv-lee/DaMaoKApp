package com.lockstudio.sticklocker.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import com.jess.ui.TwoWayGridView;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.model.EffectInfo;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.HASH;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.view.SimpleToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cn.opda.android.activity.R;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.GPUImageView.OnPictureSavedListener;
import jp.co.cyberagent.android.gpuimage.util.GPUImageFilterTools;
import jp.co.cyberagent.android.gpuimage.util.GPUImageFilterTools.FilterAdjuster;
import jp.co.cyberagent.android.gpuimage.util.GPUImageFilterTools.FilterType;

public class PaperEditActivity extends BaseActivity implements OnClickListener {
	private Bitmap mBitmap;
	private GPUImageView mImageView;
	public boolean mWaitingToPick, mSaving;
	private ProgressDialog mProgressDialog;
	private Bitmap mTmpBmp;
	private Context mContext = PaperEditActivity.this;
	private boolean from_diy = true;
	private boolean is_local = false;
	private String thumbnailUrl, imageUrl;
	private int maxImageWidth, maxImageHeight;
	private String image_path;
	private File mCroptemp;
	private TwoWayGridView effect_gridview;
	private final String TAG = "V5_WALLPAPER_PREVIEW_ACTIVITY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_editpaper);
		maxImageHeight = LockApplication.getInstance().getConfig().getScreenHeight();
		maxImageWidth = LockApplication.getInstance().getConfig().getScreenWidth();
		initWedgitsAndActions();
		initEffectView();
	}

	private void initWedgitsAndActions() {
		mImageView = (GPUImageView) findViewById(R.id.edit_crop_image);
		mImageView.setOnClickListener(this);
		findViewById(R.id.activity_editwallpaper_ll_crop).setOnClickListener(this);
		findViewById(R.id.activity_editwallpaper_ll_effect).setOnClickListener(this);
		findViewById(R.id.activity_editwallpaper_ll_save).setOnClickListener(this);

		effect_gridview = (TwoWayGridView) findViewById(R.id.effect_gridview);
		effect_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
				EffectInfo effectInfo = (EffectInfo) parent.getItemAtPosition(position);
				switchFilterTo(effectInfo);
			}
		});

		Intent intent = getIntent();
		from_diy = intent.getBooleanExtra("from_diy", false);
		is_local = intent.getBooleanExtra("Local", false);
		image_path = intent.getStringExtra("image_path");
		if (null == image_path) {
			if (new File(MConstants.DEFAULTIMAGE_PATH).exists()) {
				image_path = MConstants.DEFAULTIMAGE_PATH;
			} else {
				SimpleToast.makeText(mContext, R.string.pic_url_error, SimpleToast.LENGTH_LONG).show();
				finish();
			}
		}
		thumbnailUrl = intent.getStringExtra("THUMBNAIL_URL");
		imageUrl = intent.getStringExtra("IMAGE_URL");

		if (!TextUtils.isEmpty(thumbnailUrl)) {
			Bitmap bitmap = VolleyUtil.instance().getBitmapForUrl(imageUrl);
			if (null != bitmap) {
				mBitmap = bitmap;
				DrawableUtils.saveBitmap(new File(image_path), mBitmap, false);
				mTmpBmp = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
				mImageView.setImage(mBitmap);
			} else {
				bitmap = VolleyUtil.instance().getBitmapForUrl(thumbnailUrl, maxImageWidth, maxImageHeight);
				if (null != bitmap) {
					mBitmap = bitmap;
					DrawableUtils.saveBitmap(new File(image_path), mBitmap, false);
					mTmpBmp = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
					mImageView.setImage(mBitmap);
				} else {
					ImageRequest imageRequest = new ImageRequest(thumbnailUrl, new Response.Listener<Bitmap>() {
						@Override
						public void onResponse(Bitmap response) {
							if (response != null) {
								mBitmap = DrawableUtils.scaleTo(response, maxImageWidth, maxImageHeight);
								DrawableUtils.saveBitmap(new File(image_path), mBitmap, false);
								mTmpBmp = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
								mImageView.setImage(mBitmap);
							}
						}
					}, 0, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {

						}
					});
					VolleyUtil.instance().addRequest(imageRequest);
				}
				ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap response) {
						if (response != null) {
							saveToAlbum();
							mBitmap = response;
							DrawableUtils.saveBitmap(new File(image_path), mBitmap, false);
							mTmpBmp = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
							mImageView.setImage(mBitmap);
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

		} else {
			mBitmap = BitmapFactory.decodeFile(image_path);
			if (mBitmap == null) {
				SimpleToast.makeText(mContext, R.string.pic_download_error, SimpleToast.LENGTH_LONG).show();
				finish();
			} else {
				mTmpBmp = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
			}
			if (null == mTmpBmp)
				finish();
			mImageView.setImage(mBitmap);
		}

	}
	
	private String saveToAlbum() {
		String name = HASH.md5sum(imageUrl) + ".jpg";
		boolean success = VolleyUtil.instance().writeBitmapToFile(imageUrl, MConstants.USER_PHOTO_PATH, name);
		if (success) {
			sendBroadcast(new Intent(MConstants.ACTION_UPDATE_LOCAL_WALLPAPER));
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + MConstants.USER_PHOTO_PATH + name)));
			CustomEventCommit.commit(mContext.getApplicationContext(), TAG, "SAVE");
			return MConstants.USER_PHOTO_PATH + name;
		}
		return null;
	}

	private void initEffectView() {
		ArrayList<EffectInfo> effectInfos = new ArrayList<EffectInfo>();
		EffectInfo effectInfo = new EffectInfo();
		effectInfo.setEffectName("原始");
		effectInfo.setFilterType(FilterType.NORMAL);
		effectInfo.setEffectImage(R.drawable.effect_normal);
		effectInfo.setGpuImageFilter(new GPUImageFilter());
		effectInfos.add(effectInfo);

		effectInfo = new EffectInfo();
		effectInfo.setEffectName("黑白");
		effectInfo.setFilterType(FilterType.GRAYSCALE);
		effectInfo.setEffectImage(R.drawable.effect_grayscale);
		effectInfo.setGpuImageFilter(GPUImageFilterTools.createFilterForType(mContext, effectInfo.getFilterType()));
		effectInfos.add(effectInfo);

		effectInfo = new EffectInfo();
		effectInfo.setEffectName("模糊");
		effectInfo.setFilterType(FilterType.GAUSSIAN_BLUR);
		effectInfo.setEffectImage(R.drawable.effect_blur);
		effectInfo.setGpuImageFilter(GPUImageFilterTools.createFilterForType(mContext, effectInfo.getFilterType()));
		effectInfo.setProgress(100);
		effectInfos.add(effectInfo);

		effectInfo = new EffectInfo();
		effectInfo.setEffectName("浮雕");
		effectInfo.setFilterType(FilterType.EMBOSS);
		effectInfo.setEffectImage(R.drawable.effect_emboss);
		effectInfo.setGpuImageFilter(GPUImageFilterTools.createFilterForType(mContext, effectInfo.getFilterType()));
		effectInfo.setProgress(25);
		effectInfos.add(effectInfo);

		effectInfo = new EffectInfo();
		effectInfo.setEffectName("黄昏");
		effectInfo.setFilterType(FilterType.SEPIA);
		effectInfo.setEffectImage(R.drawable.effect_sepia);
		effectInfo.setGpuImageFilter(GPUImageFilterTools.createFilterForType(mContext, effectInfo.getFilterType()));
		effectInfo.setProgress(60);
		effectInfos.add(effectInfo);

		effectInfo = new EffectInfo();
		effectInfo.setEffectName("烟雾");
		effectInfo.setFilterType(FilterType.HAZE);
		effectInfo.setEffectImage(R.drawable.effect_haze);
		effectInfo.setGpuImageFilter(GPUImageFilterTools.createFilterForType(mContext, effectInfo.getFilterType()));
		effectInfo.setProgress(0);
		effectInfos.add(effectInfo);

		effectInfo = new EffectInfo();
		effectInfo.setEffectName("虚光");
		effectInfo.setFilterType(FilterType.VIGNETTE);
		effectInfo.setEffectImage(R.drawable.effect_vignette);
		effectInfo.setGpuImageFilter(GPUImageFilterTools.createFilterForType(mContext, effectInfo.getFilterType()));
		effectInfo.setProgress(0);
		effectInfos.add(effectInfo);

		EffectAdapter effectAdapter = new EffectAdapter(mContext, effectInfos);
		effect_gridview.setAdapter(effectAdapter);
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.activity_editwallpaper_ll_save) {
			effect_gridview.setVisibility(View.INVISIBLE);
			mProgressDialog = ProgressDialog.show(this, null, getString(R.string.saving));
			mProgressDialog.show();
			final String path = MConstants.IMAGECACHE_PATH + "natureCrop";
			mImageView.saveToPictures(MConstants.IMAGECACHE_PATH, "natureCrop", new OnPictureSavedListener() {

				@Override
				public void onPictureSaved(Uri uri) {
					if (mProgressDialog != null) {
						mProgressDialog.dismiss();
					}
					if (path != null) {

						if (from_diy || is_local) {
							Intent data = new Intent();
							data.putExtra("path", path);
							setResult(MConstants.RESULT_CODE_EDIT, data);
						} else {
							Intent intent = new Intent(mContext, DiyActivity.class);
							intent.putExtra("path", path);
							startActivity(intent);
						}
					}
					finish();
				}
			});

		} else if (i == R.id.activity_editwallpaper_ll_crop) {
			crop();
			effect_gridview.setVisibility(View.INVISIBLE);

		} else if (i == R.id.activity_editwallpaper_ll_effect) {
			if (effect_gridview.getVisibility() == View.VISIBLE) {
				effect_gridview.setVisibility(View.INVISIBLE);
				effect_gridview.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.out_to_bottom));
			} else {
				effect_gridview.setVisibility(View.VISIBLE);
				effect_gridview.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.in_from_bottom));
			}

		} else if (i == R.id.edit_crop_image) {
			if (effect_gridview.getVisibility() == View.VISIBLE) {
				effect_gridview.setVisibility(View.INVISIBLE);
				effect_gridview.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.out_to_bottom));
			}

		} else {
		}
	}

	/**
	 * 裁剪
	 */
	private void crop() {
		if(image_path!=null){
		    mCroptemp = new File(image_path);
		}
		DrawableUtils.saveBitmap(mCroptemp, mTmpBmp, false);
		zoomCrop(Uri.fromFile(mCroptemp));
		reset(mTmpBmp);
	}

	/**
	 * 重新设置一下图片
	 */
	private void reset(Bitmap mBitmap) {
		mImageView.setImage(mBitmap);
		mImageView.invalidate();
	}

	/**
	 * 裁剪图片方法
	 * 
	 * @param uri
	 */
	public void zoomCrop(Uri uri) {
		int deviceWidth = DeviceInfoUtils.getDeviceWidth(mContext);
		int deviceHeight = DeviceInfoUtils.getDeviceHeight(mContext);
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", deviceWidth);
		intent.putExtra("aspectY", deviceHeight);
		intent.putExtra("outputX", deviceWidth);
		intent.putExtra("outputY", deviceHeight);
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (mCroptemp!=null && Uri.fromFile(mCroptemp) != null) {
				mTmpBmp = decodeUriAsBitmap(Uri.fromFile(mCroptemp));
				mBitmap = mTmpBmp;
				reset(mTmpBmp);
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	private void switchFilterTo(final EffectInfo effectInfo) {

		GPUImageFilter gpuImageFilter = effectInfo.getGpuImageFilter();
		mImageView.setFilter(gpuImageFilter);
		FilterAdjuster mFilterAdjuster = new FilterAdjuster(gpuImageFilter);
		if (mFilterAdjuster != null) {
			mFilterAdjuster.adjust(effectInfo.getProgress());
		}
		mImageView.requestRender();
	}

	public class EffectAdapter extends BaseAdapter {
		private ArrayList<EffectInfo> effectInfos = new ArrayList<EffectInfo>();
		private LayoutInflater inflater;

		public EffectAdapter(Context mContext, ArrayList<EffectInfo> effectInfos) {
			this.effectInfos = effectInfos;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return effectInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return effectInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.gridview_item_effect, parent, false);
				holder.effect_imageview = (ImageView) convertView.findViewById(R.id.effect_imageview);
				holder.select_imageview = (ImageView) convertView.findViewById(R.id.select_imageview);
				holder.effect_textview = (TextView) convertView.findViewById(R.id.effect_textview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final EffectInfo effectInfo = effectInfos.get(position);
			holder.effect_textview.setText(effectInfo.getEffectName());
			if (effectInfo.isSelected()) {
				holder.select_imageview.setVisibility(View.VISIBLE);
			} else {
				holder.select_imageview.setVisibility(View.GONE);

			}
			holder.effect_imageview.setImageResource(effectInfo.getEffectImage());

			return convertView;
		}

		class ViewHolder {
			public ImageView effect_imageview;
			public ImageView select_imageview;
			public TextView effect_textview;
		}
	}

}