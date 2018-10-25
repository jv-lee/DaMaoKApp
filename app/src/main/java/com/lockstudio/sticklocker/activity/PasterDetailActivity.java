package com.lockstudio.sticklocker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.lockstudio.sticklocker.Interface.FileDownloadListener;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.model.PasterInfo;
import com.lockstudio.sticklocker.service.CoreService;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.FileDownloader;
import com.lockstudio.sticklocker.util.FileUtils;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.RLog;
import com.lockstudio.sticklocker.util.ZipUtils;
import com.lockstudio.sticklocker.view.SimpleToast;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import cn.opda.android.activity.R;

public class PasterDetailActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private boolean isDownloaded;
	private boolean isJieYa;
	private boolean isDownloading;
	private Button download_btn_paster_item;
	private TextView title_bar_left_tv;
	private TextView title_bar_right_tv;
	private String url;
	private String previewUrl;

	private ProgressBar download_progressbar_paster_item;
	private String zipPath;
	private String imagePath;
	private String name;
	private PasterInfo pasterInfo;
	private ImageView paster_imageview;
	private int position;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paster_detail);
		mContext = PasterDetailActivity.this;
		title_bar_left_tv = (TextView) findViewById(R.id.title_bar_left_tv);
		title_bar_right_tv = (TextView) findViewById(R.id.title_bar_right_tv);
		download_progressbar_paster_item = (ProgressBar) findViewById(R.id.download_progressbar_paster_item);
		download_btn_paster_item = (Button) findViewById(R.id.download_btn_paster_item);
		paster_imageview = (ImageView) findViewById(R.id.paster_imageview);
		title_bar_left_tv.setOnClickListener(this);
		download_btn_paster_item.setOnClickListener(this);

		pasterInfo = (PasterInfo) getIntent().getSerializableExtra("pasterInfo");
		position = getIntent().getIntExtra("position", 0);
		name = pasterInfo.getName();
		title_bar_right_tv.setText(name);
		url = pasterInfo.getUrl();
		previewUrl = pasterInfo.getPreviewUrl();
		
		final Bitmap bitmap = VolleyUtil.instance().getBitmapForUrl(previewUrl);
		if (bitmap != null) {
			paster_imageview.setImageBitmap(bitmap);
		} else {
			ImageRequest imageRequest = new ImageRequest(previewUrl, new Response.Listener<Bitmap>() {
				@Override
				public void onResponse(Bitmap response) {
					if (response != null) {
						paster_imageview.setImageBitmap(response);
						String cacheKey = com.android.volley.toolbox.ImageLoader.getCacheKey(previewUrl, 0, 0, ImageView.ScaleType.CENTER_INSIDE);
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
		
		zipPath = MConstants.DOWNLOAD_PATH + name + ".zip";
		imagePath = MConstants.IMAGE_PATH + name;
		isDownloading = CoreService.donwloadPath.contains(url);
		// 是否下载
		isDownloaded = new File(zipPath).exists();// 看压缩目录是否存在
		// 是否解压
		isJieYa = new File(imagePath).exists() && new File(imagePath) != null && new File(imagePath).listFiles().length > 0;
		if (isDownloaded) {
			// 已经下载好的。不能点击
			download_btn_paster_item.setEnabled(false);
			download_btn_paster_item.setClickable(false);
			download_btn_paster_item.setText("已下载");
			download_btn_paster_item.setTextColor(mContext.getResources().getColor(R.color.bottom_all_btn_bg_color));
			// 判断文件夹是否有内容，如果有，代表已经解压
			if (!isJieYa) {
				if (new File(imagePath).exists()) {
					FileUtils.deleteFileByPath(imagePath);
				}
				try {
					ZipUtils.upZipFile(new File(zipPath), imagePath);
					VolleyUtil.instance().writeBitmapToFile(pasterInfo.getImage(), imagePath, "icon");
					new File(imagePath, MConstants.nomedia_file).createNewFile();
					pasterInfo.setUnzip(true);
					mContext.sendBroadcast(new Intent(MConstants.ACTION_UPDATE_IMAGE_PAGE));
				} catch (ZipException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
//		FancyLauncherApplication.getInstance().getActivityStackManager().addActivity(this);
	}

	private void download(String paster_url, final String zipPath, final String imagePath) {
		if (isDownloading) {
			return;
		}
		CoreService.donwloadPath.add(paster_url);
		isDownloading = true;
		pasterInfo.setDownloding(true);
		download_progressbar_paster_item.setVisibility(View.VISIBLE);
		download_progressbar_paster_item.setProgress(0);
		FileDownloader fileDownloader = new FileDownloader(mContext, new FileDownloadListener() {

			@Override
			public void finish(String downloadUrl, String path) {
				RLog.i("download finish", "downloadUrl===" + downloadUrl);
				CoreService.donwloadPath.remove(downloadUrl);
				isDownloading = false;
				isDownloaded = true;
				pasterInfo.setDownloding(false);
				pasterInfo.setDownloaded(true);
				download_progressbar_paster_item.setVisibility(View.INVISIBLE);
				SimpleToast.makeText(mContext, R.string.download_succsed, SimpleToast.LENGTH_SHORT).show();

				try {
					ZipUtils.upZipFile(new File(zipPath), imagePath);
					VolleyUtil.instance().writeBitmapToFile(pasterInfo.getImage(), imagePath, "icon");
					new File(imagePath, MConstants.nomedia_file).createNewFile();
					pasterInfo.setUnzip(true);
					mContext.sendBroadcast(new Intent(MConstants.ACTION_UPDATE_IMAGE_PAGE));
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				download_btn_paster_item.setText("已下载");
				download_btn_paster_item.setTextColor(mContext.getResources().getColor(R.color.bottom_all_btn_bg_color));

			}

			@Override
			public void error(String downloadUrl) {
				RLog.i("download error", "downloadUrl===" + downloadUrl);
				CoreService.donwloadPath.remove(downloadUrl);
				isDownloading = false;
				pasterInfo.setDownloding(false);
				pasterInfo.setDownloaded(false);
				download_btn_paster_item.setText("下载");
				download_progressbar_paster_item.setProgress(0);
				SimpleToast.makeText(mContext, R.string.download_faild, SimpleToast.LENGTH_SHORT).show();
			}

			@Override
			public void downloading(String downloadUrl, int size) {
				download_progressbar_paster_item.setMax(100);
				download_progressbar_paster_item.setProgress(size);
				// 下载中
				download_btn_paster_item.setText("下载中" + size + "%");
			}
		});
		fileDownloader.setDownloadurl(paster_url);
		fileDownloader.setFileDir(zipPath);
		fileDownloader.startDownload();
	}

	@Override
	public void onClick(View arg0) {
		int i = arg0.getId();
		if (i == R.id.download_btn_paster_item) {
			if (DeviceInfoUtils.sdMounted()) {
				download(url, zipPath, imagePath);
			} else {
				SimpleToast.makeText(mContext, R.string.sdcard_not_mounted_2, SimpleToast.LENGTH_SHORT).show();
			}

		} else if (i == R.id.title_bar_left_tv) {
			finishForResult();

		} else {
		}

	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finishForResult();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	

	private void finishForResult(){
		Intent intent = new Intent(mContext, PasterStoreActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("pasterInfo", pasterInfo);
		bundle.putInt("position", position);
		intent.putExtras(bundle);
		setResult(1010, intent);
		
		finish();
	}
}
