package com.lockstudio.sticklocker.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.lockstudio.sticklocker.service.DownloadService;
import com.lockstudio.sticklocker.view.SimpleToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.opda.android.activity.R;

public class AppDownloadUtils {
	public Context mContext;
	private int NF_ID = 2015;
	private Notification nf;
	private NotificationManager nm;
	private String downloadurl;
	private String appName;
	private String packageName;
	private Bitmap appIcon;
	private Builder builder;
	private DownloadListener downloadListener;
	private int continueCount;
	public static final int MaxContinueCount = 30;
	private boolean showNotify = true;

	public AppDownloadUtils(Context context) {
		this.mContext = context;
	}

	public void createNotify() {
		String time = System.currentTimeMillis() + "";
		NF_ID = Integer.parseInt(time.substring(time.length() / 2, time.length()));
		nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			builder = new Builder(mContext);
			builder.setSmallIcon(R.drawable.ic_launcher_2);
			if (appIcon == null) {
				appIcon = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.ic_launcher_2)).getBitmap();
			}
			builder.setLargeIcon(appIcon);
			builder.setContentTitle(appName);
			builder.setContentText("0%");
			builder.setProgress(100, 0, false);
			builder.setOngoing(true);

			nf = builder.build();
			nf.flags = Notification.FLAG_NO_CLEAR;
			nf.flags = Notification.FLAG_ONGOING_EVENT;
			nf.flags |= Notification.FLAG_FOREGROUND_SERVICE;
			nf.icon = android.R.drawable.stat_sys_download;
			nf.contentIntent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);

		} else {
			nf = new Notification(R.drawable.ic_launcher_2, "", System.currentTimeMillis());
			nf.icon = android.R.drawable.stat_sys_download;
			nf.flags = Notification.FLAG_NO_CLEAR;
			nf.flags = Notification.FLAG_ONGOING_EVENT;
			nf.flags |= Notification.FLAG_FOREGROUND_SERVICE;
			nf.contentView = new RemoteViews(mContext.getPackageName(), R.layout.notification_layout);
			nf.contentView.setProgressBar(R.id.progressbar_notification, 100, 0, false);
			nf.contentView.setTextViewText(R.id.textivew_notification, "0%");
			nf.contentIntent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);
		}
	}

	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}
	
	public void setShowNotify(boolean showNotify){
		this.showNotify = showNotify;
	}

	public void setAppIcon(Bitmap appIcon) {
		if (appIcon != null) {
			int newWidth = DensityUtil.dip2px(mContext, 48);
			if (appIcon.getWidth() > newWidth) {
				Bitmap bitmap = DrawableUtils.scaleTo(appIcon, newWidth, newWidth);
				this.appIcon = bitmap;
			} else {
				this.appIcon = appIcon;
			}
		}
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 3: {
				if (downloadListener != null) {
					downloadListener.onError(downloadurl, appName);
				}
				if(showNotify){
					nm.cancel(NF_ID);
					SimpleToast.makeText(mContext, R.string.download_faild, SimpleToast.LENGTH_SHORT).show();
					if (DownloadService.downloadUrls.contains(downloadurl)) {
						DownloadService.downloadUrls.remove(downloadurl);
					}
					if (!TextUtils.isEmpty(packageName)) {
						Intent intent = new Intent("APP_DOWNLOAD_FAILD");
						intent.putExtra("packageName", packageName);
						mContext.sendBroadcast(intent);
					}
				}
				break;
			}
			case 4: {
				if(showNotify){
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						builder.setContentText(msg.obj + "%");
						builder.setProgress(100, (Integer) msg.obj, false);
						nf = builder.build();
					} else {
						nf.contentView.setProgressBar(R.id.progressbar_notification, 100, (Integer) msg.obj, false);
						nf.contentView.setTextViewText(R.id.textivew_notification, appName + mContext.getString(R.string.download_progress) + msg.obj + "%");
					}
					nm.notify(NF_ID, nf);
				}
				break;
			}
			case 5: {
				if (downloadListener != null) {
					downloadListener.onDownloaded(downloadurl, appName);
				}

				if(showNotify){
					if (DownloadService.downloadUrls.contains(downloadurl)) {
						DownloadService.downloadUrls.remove(downloadurl);
					}

					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(Intent.ACTION_VIEW);
					File file = new File((String) msg.obj);
					if (file.exists() && file.isAbsolute()) {
						intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
						mContext.startActivity(intent);
						if (!TextUtils.isEmpty(appName)) {
							CustomEventCommit.commit(mContext, CustomEventCommit.install, appName);
						}
					}
					nm.cancel(NF_ID);
				}
				break;
			}
			default:
				break;
			}
		}
	};
	
	public void setDownloadListener(final DownloadListener downloadListener){
		this.downloadListener = downloadListener;
	}

	public void startDownloadApp() {
		CustomEventCommit.commit(mContext, CustomEventCommit.download, appName);
		Message msg2 = new Message();
		msg2.obj = 0;
		msg2.what = 4;
		mHandler.sendMessage(msg2);
		new Thread(new Runnable() {

			@Override
			public void run() {

				String path = downloadurl;
				String apkName = HASH.md5sum(path);
				String downloadPath = MConstants.DOWNLOAD_PATH;
				if (!new File(downloadPath).exists()) {
					new File(downloadPath).mkdirs();
				}
				String apkPath = downloadPath + "/" + apkName;
				String apkPathTemp = downloadPath + "/" + apkName + ".temp";
				if (new File(apkPath).exists()) {

					Message msg = new Message();
					msg.what = 5;
					msg.obj = apkPath;
					mHandler.sendMessage(msg);
					return;
				}
				long totalSize = 0;
				long progress = 0;
				long bytes = new File(apkPathTemp).exists() ? new File(apkPathTemp).length() : 0;
				long downloadSize = bytes;
				InputStream inStream = null;
				FileOutputStream outputStream = null;
				try {
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5 * 1000);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					conn.setInstanceFollowRedirects(true);
					conn.setRequestProperty("RANGE", "bytes=" + bytes + "-");
					conn.setRequestProperty("User-Agent", "Ray-Downer");
					conn.setReadTimeout(30 * 1000);
					int connCode = conn.getResponseCode();
					if (connCode < 400 && connCode >= 200) {
						inStream = conn.getInputStream();
						outputStream = new FileOutputStream(apkPathTemp, true);
						byte[] buffer = new byte[4096];
						totalSize = conn.getContentLength();
						totalSize += bytes;
						int len;
						while ((len = inStream.read(buffer)) != -1) {
							outputStream.write(buffer, 0, len);
							downloadSize += len;
							int newProgress = (int) ((100 * downloadSize) / totalSize);
							if (newProgress - 1 > progress) {
								progress = newProgress;
								Message msg = new Message();
								msg.obj = newProgress;
								msg.what = 4;
								mHandler.sendMessage(msg);
							}
						}
						inStream.close();
						outputStream.close();
						if (totalSize == downloadSize) {
							new File(apkPathTemp).renameTo(new File(apkPath));

							Message msg = new Message();
							msg.what = 5;
							msg.obj = apkPath;
							mHandler.sendMessage(msg);
						}
					} else {
						mHandler.sendEmptyMessage(3);
					}
				} catch (Exception e) {
					e.printStackTrace();
					continueDownloadApp();
				} finally {
					try {
						if (inStream != null)
							inStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						if (outputStream != null)
							outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}
	public void continueDownloadApp() {
		continueCount++;
		new Thread(new Runnable() {

			@Override
			public void run() {

				String apkName = HASH.md5sum(downloadurl);
				String downloadPath = MConstants.DOWNLOAD_PATH;
				if (!new File(downloadPath).exists()) {
					new File(downloadPath).mkdirs();
				}
				String apkPath = downloadPath + "/" + apkName;
				String apkPathTemp = downloadPath + "/" + apkName + ".temp";
				if (new File(apkPath).exists()) {

					Message msg = new Message();
					msg.what = 5;
					msg.obj = apkPath;
					mHandler.sendMessage(msg);
					return;
				}
				long totalSize = 0;
				long progress = 0;
				long bytes = new File(apkPathTemp).exists() ? new File(apkPathTemp).length() : 0;
				long downloadSize = bytes;
				InputStream inStream = null;
				FileOutputStream outputStream = null;
				try {
					URL url = new URL(downloadurl);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5 * 1000);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					conn.setInstanceFollowRedirects(true);
					conn.setRequestProperty("RANGE", "bytes=" + bytes + "-");
					conn.setRequestProperty("User-Agent", "Ray-Downer");
					conn.setReadTimeout(30 * 1000);
					int connCode = conn.getResponseCode();
					if (connCode < 400 && connCode >= 200) {
						inStream = conn.getInputStream();
						outputStream = new FileOutputStream(apkPathTemp, true);
						byte[] buffer = new byte[4096];
						totalSize = conn.getContentLength();
						totalSize += bytes;
						int len;
						while ((len = inStream.read(buffer)) != -1) {
							outputStream.write(buffer, 0, len);
							downloadSize += len;
							int newProgress = (int) ((100 * downloadSize) / totalSize);
							if (newProgress - 1 > progress) {
								progress = newProgress;
								Message msg = new Message();
								msg.obj = newProgress;
								msg.what = 4;
								mHandler.sendMessage(msg);
							}
						}
						if (totalSize == downloadSize) {
							new File(apkPathTemp).renameTo(new File(apkPath));

							Message msg = new Message();
							msg.what = 5;
							msg.obj = apkPath;
							mHandler.sendMessage(msg);
						}
					} else {
						mHandler.sendEmptyMessage(3);
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (continueCount < MaxContinueCount) {
						continueDownloadApp();
					} else {
						mHandler.sendEmptyMessage(3);
					}
				} finally {
					try {
						if (inStream != null)
							inStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						if (outputStream != null)
							outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	public interface DownloadListener {
		void onError(String url, String appName);

		void onInstalled();

		void onDownloaded(String url, String appName);
	}

}
