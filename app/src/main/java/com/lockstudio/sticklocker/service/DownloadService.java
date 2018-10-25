package com.lockstudio.sticklocker.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.text.TextUtils;

import com.android.volley.Tommy.VolleyUtil;
import com.lockstudio.sticklocker.util.AppDownloadUtils;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.view.SimpleToast;

import java.util.ArrayList;

import cn.opda.android.activity.R;

public class DownloadService extends Service {
	public static ArrayList<String> downloadUrls = new ArrayList<String>();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String appName = intent.getStringExtra("name");
			String url = intent.getStringExtra("url");
			int icon = intent.getIntExtra("icon", R.drawable.ic_launcher_2);
			byte[] iconByte = intent.getByteArrayExtra("iconByte");
			String imageUrl = intent.getStringExtra("imageUrl");
			String packageName = intent.getStringExtra("packageName");

			downloadUrls.add(url);
			AppDownloadUtils appDownload = new AppDownloadUtils(this);
			appDownload.setAppName(appName);
			appDownload.setDownloadurl(url);
			if(!TextUtils.isEmpty(packageName)){
				appDownload.setPackageName(packageName);
			}
			if (iconByte != null && iconByte.length > 0) {
				appDownload.setAppIcon(DrawableUtils.byte2Bitmap(getApplicationContext(), iconByte));
			} else {
				if (icon != 0) {
					appDownload.setAppIcon(BitmapFactory.decodeResource(getResources(), icon));
				} else if (!TextUtils.isEmpty(imageUrl)) {
					Bitmap bitmap = VolleyUtil.instance().getBitmapForUrl(imageUrl);
					if (bitmap != null) {
						appDownload.setAppIcon(bitmap);
					}
				}
			}

			appDownload.createNotify();
			appDownload.startDownloadApp();
			SimpleToast.makeText(this, getResources().getString(R.string.app_start_download, appName), SimpleToast.LENGTH_SHORT).show();
		}

		return super.onStartCommand(intent, flags, startId);
	}

}
