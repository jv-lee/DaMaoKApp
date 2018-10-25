package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.lockstudio.sticklocker.Interface.FileDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * @author 庄宏岩
 * 
 */
public class FileDownloader {
	public Context mContext;
	private String downloadurl;
	private FileDownloadListener downloadListener;
	private String zipPath;

	public FileDownloader(Context context) {
		this.mContext = context;
	}

	public FileDownloader(Context context, FileDownloadListener downloadListener) {
		this.mContext = context;
		this.downloadListener = downloadListener;
	}

	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}

	public void setFileDir(String zipPath) {
		this.zipPath = zipPath;
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1: {
				downloadListener.error(downloadurl);
				break;
			}
			case 2: {
				downloadListener.downloading(downloadurl, (Integer) msg.obj);
				break;
			}
			case 3: {
				downloadListener.finish(downloadurl, (String) msg.obj);
				break;
			}
			default:
				break;
			}
		}
	};

	/**
	 * 开始下载app
	 */
	public void startDownload() {
		Message msg2 = new Message();
		msg2.obj = 0;
		msg2.what = 2;
		mHandler.sendMessage(msg2);
		new Thread(new Runnable() {
			@Override
			public void run() {
				String path = downloadurl;
				String folder = new File(zipPath).getParent();
				if (!new File(folder).exists()) {
					new File(folder).mkdirs();
				}
				String filePath = zipPath;
				String filePathTemp = zipPath + ".temp";
				if (new File(filePath).exists()) {
					Message msg = new Message();
					msg.what = 3;
					msg.obj = filePath;
					mHandler.sendMessage(msg);
					return;
				}
				long totalSize = 0;
				long downloadSize = 0;
				long progress = 0;
				try {
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5 * 1000);
					conn.setRequestMethod("GET");
					conn.setReadTimeout(30 * 1000);
					if (conn.getResponseCode() == 200) {
						InputStream inStream = conn.getInputStream();
						FileOutputStream outputStream = new FileOutputStream(filePathTemp);
						byte[] buffer = new byte[1024];
						totalSize = conn.getContentLength();
						int len;
						while ((len = inStream.read(buffer)) != -1) {
							outputStream.write(buffer, 0, len);
							downloadSize += len;
							int newProgress = (int) ((100 * downloadSize) / totalSize);
							if (newProgress - 1 > progress) {
								progress = newProgress;
								Message msg = new Message();
								msg.obj = newProgress;
								msg.what = 2;
								mHandler.sendMessage(msg);
							}
						}
						inStream.close();
						outputStream.close();
						if (totalSize == downloadSize) {
							new File(filePathTemp).renameTo(new File(filePath));

							Message msg = new Message();
							msg.what = 3;
							msg.obj = filePath;
							mHandler.sendMessage(msg);
						}
					} else {
						mHandler.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					mHandler.sendEmptyMessage(1);
					e.printStackTrace();
				}
			}
		}).start();

	}

}
