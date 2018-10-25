package com.lockstudio.sticklocker.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat.Builder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseDialog.OnDismissedListener;
import com.lockstudio.sticklocker.view.SimpleToast;
import com.lockstudio.sticklocker.view.TipsDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import cn.opda.android.activity.R;

/**
 * 软件自身的自动更新类
 * 
 * @author 庄宏岩
 */
public class AppUpdate {
	public Context mContext;
	private boolean autoCheck; // 是否是后台检查 true：后台检查，不显示等待框，false：手动检查更新，显示等待框。
	private String appUrl; // 更新包的下载url
	private String updateNote; // 更新包的更新日志
	private int NF_ID = 1003;
	private Notification nf;
	private NotificationManager nm;
	private Bitmap appIcon;
	private Builder builder;

	public AppUpdate(Context context) {
		this.mContext = context;
		String time = System.currentTimeMillis() + "";
		NF_ID = Integer.parseInt(time.substring(time.length() / 2, time.length()));
		nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= 11) {
			builder = new Builder(mContext);
			builder.setSmallIcon(R.drawable.ic_launcher_2);
			if (appIcon == null) {
				appIcon = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.ic_launcher_2)).getBitmap();
			}
			if (appIcon != null) {
				builder.setLargeIcon(appIcon);
			}
			builder.setContentTitle(mContext.getString(R.string.app_name));
			builder.setContentText("0%");
			builder.setProgress(100, 0, false);

			nf = builder.build();
			nf.flags = Notification.FLAG_AUTO_CANCEL;
			nf.icon = android.R.drawable.stat_sys_download;
			nf.contentIntent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);
			nf = builder.build();
		} else {
			nf = new Notification(R.drawable.ic_launcher_2, "", System.currentTimeMillis());
			nf.icon = android.R.drawable.stat_sys_download;
			nf.flags = Notification.FLAG_NO_CLEAR;
			nf.contentView = new RemoteViews(mContext.getPackageName(), R.layout.notification_layout);
			nf.contentView.setProgressBar(R.id.progressbar_notification, 100, 0, false);
			nf.contentView.setTextViewText(R.id.textivew_notification, mContext.getResources().getString(R.string.download_progress) + "  0%");
			nf.contentIntent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);
		}

	}

	/**
	 * 检查更新
	 * 
	 * @param
	 */
	public void checkUpdate(boolean autoCheck) {
		this.autoCheck = autoCheck;
		boolean checkUpdate = true;
		if (autoCheck) {
			long updateTime = LockApplication.getInstance().getConfig().getUpdateTime();
			if (updateTime == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
				checkUpdate = false;
			}
		}
		if (checkUpdate) {
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(getRequestUrl(), null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					parseJson(response);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					mHandler.sendEmptyMessage(0);
				}
			});
			RequestQueue requestQueue = VolleyUtil.instance().getRequestQueue();
			if (requestQueue != null) {
				requestQueue.add(jsonObjectRequest);
			}
		}
	}

	private String getRequestUrl() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("app_md5", "369b1f8c91f5e3bb5c7012cf6a1d3d69");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = HostUtil.getUrl("appUpdate/index" + "?json=" + jsonObject.toString());
		return url;
	}

	private void parseJson(JSONObject jsonObject) {
		RLog.i("update response", jsonObject.toString());
		if (jsonObject.optInt("code") == 200 && jsonObject.has("json")) {
			JSONObject jsonObject2 = jsonObject.optJSONObject("json");
			if (null != jsonObject2) {
				int versionCode = jsonObject2.optInt("v");
				appUrl = jsonObject2.optString("u");
				updateNote = jsonObject2.optString("info");

				PackageManager packageManager = mContext.getPackageManager();
				PackageInfo packageInfo = null;
				try {
					packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				if (packageInfo != null && packageInfo.versionCode < versionCode) {
					mHandler.sendEmptyMessage(1);
				} else {
					mHandler.sendEmptyMessage(2);
				}

			}
		}
	}

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				if (!autoCheck) {
					SimpleToast.makeText(mContext, R.string.check_update_faild, SimpleToast.LENGTH_SHORT).show();
				}
				break;
			}
			case 1: {
				final TipsDialog tipsDialog = new TipsDialog(mContext);
				tipsDialog.setMessage("更新内容: \n" + updateNote);
				tipsDialog.setOnDismissedListener(new OnDismissedListener() {

					@Override
					public void OnDialogDismissed() {
						LockApplication.getInstance().getConfig().setUpdateTime();
					}
				});
				tipsDialog.setCancelButton("下次再说", new OnClickListener() {

					@Override
					public void onClick(View v) {
						tipsDialog.dismiss();
						LockApplication.getInstance().getConfig().setUpdateTime();
					}
				});
				tipsDialog.setOkButton("立即更新", new OnClickListener() {

					@Override
					public void onClick(View v) {
						tipsDialog.dismiss();
						updateApp();

					}
				});
				tipsDialog.show();
				break;
			}
			case 2: {
				if (!autoCheck) {
					SimpleToast.makeText(mContext, "已经是最新版本", SimpleToast.LENGTH_SHORT).show();
				} else {
					LockApplication.getInstance().getConfig().setUpdateTime();
				}
				break;
			}
			case 3: {
				nm.cancel(NF_ID);
				SimpleToast.makeText(mContext, R.string.download_faild, SimpleToast.LENGTH_SHORT).show();
				break;
			}
			case 4: {
				if (Build.VERSION.SDK_INT >= 11) {
					builder.setContentText(msg.obj + "%");
					builder.setProgress(100, (Integer) msg.obj, false);
					nf = builder.build();
				} else {
					nf.contentView.setProgressBar(R.id.progressbar_notification, 100, (Integer) msg.obj, false);
					nf.contentView.setTextViewText(R.id.textivew_notification, mContext.getString(R.string.app_name) + "下载进度: " + msg.obj + "%");
				}
				nm.notify(NF_ID, nf);
				break;
			}
			case 5: {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(Intent.ACTION_VIEW);
				File file = new File((String) msg.obj);
				if (file.exists() && file.isAbsolute()) {
					intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
					mContext.startActivity(intent);
				}
				SimpleToast.makeText(mContext, "更新下载完成", SimpleToast.LENGTH_SHORT).show();
				nm.cancel(NF_ID);
				break;
			}
			default:
				break;
			}
			return false;
		}
	});

	private void updateApp() {

		Message msg2 = new Message();
		msg2.obj = 0;
		msg2.what = 4;
		mHandler.sendMessage(msg2);
		new Thread(new Runnable() {
			@Override
			public void run() {
				String apkPath = Environment.getExternalStorageDirectory() + "/" + HASH.md5sum(appUrl);
				long totalSize = 0;
				long downloadSize = 0;
				long progress = 0;
				try {
					URL url = new URL(appUrl);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5 * 1000);
					conn.setRequestMethod("GET");
					conn.setReadTimeout(30 * 1000);
					if (conn.getResponseCode() == 200) {
						InputStream inStream = conn.getInputStream();
						FileOutputStream outputStream = new FileOutputStream(apkPath);
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
								msg.what = 4;
								mHandler.sendMessage(msg);
							}
						}
						inStream.close();
						outputStream.close();
						if (totalSize == downloadSize) {
							Message msg = new Message();
							msg.what = 5;
							msg.obj = apkPath;
							mHandler.sendMessage(msg);
						}
					} else {
						mHandler.sendEmptyMessage(3);
					}
				} catch (Exception e) {
					mHandler.sendEmptyMessage(3);
					e.printStackTrace();
				}
			}
		}).start();

	}
}
