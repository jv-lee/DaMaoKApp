package com.lockstudio.sticklocker.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lockstudio.sticklocker.Interface.FileDownloadListener;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.model.PasterInfo;
import com.lockstudio.sticklocker.service.CoreService;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.FileDownloader;
import com.lockstudio.sticklocker.util.FileUtils;
import com.lockstudio.sticklocker.util.HostUtil;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.RLog;
import com.lockstudio.sticklocker.util.ZipUtils;
import com.lockstudio.sticklocker.view.SimpleToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipException;

import cn.opda.android.activity.R;

public class PasterStoreActivity extends BaseActivity {

	private int startNum = 1;
	public int PAGE_SIZE = 100;
	private final int MSG_REQUEST_CACHED_JSON = 101;
	private final int MSG_NOTIFY_CHANGED = 102;
	private final int JSON_NULL = 104;
	private ArrayList<PasterInfo> pasterInfos = new ArrayList<PasterInfo>();
	private ListView lv_select_paster;
	private PasterListAdapter adapter;
	private Context mContext;
	private TextView title_bar_left_tv;
	private TextView title_bar_right_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paster_store);
		mContext = PasterStoreActivity.this;
		title_bar_left_tv = (TextView) findViewById(R.id.title_bar_left_tv);
		title_bar_right_tv = (TextView) findViewById(R.id.title_bar_right_tv);
		title_bar_right_tv.setText("贴纸商店");
		lv_select_paster = (ListView) findViewById(R.id.lv_select_paster);
		title_bar_left_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		lv_select_paster.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(mContext, PasterDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("pasterInfo", pasterInfos.get(arg2));
				bundle.putInt("position", arg2);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1010);
			}
		});
		ImageButton title_bar_right_btn = (ImageButton) findViewById(R.id.title_bar_right_btn);
		title_bar_right_btn.setVisibility(View.VISIBLE);
		title_bar_right_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, PasterRemoveActivity.class));
			}
		});
		requestUrlJson();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MConstants.ACTION_REMOVE_IMAGE);
		registerReceiver(broadcastReceiver, intentFilter);
//		FancyLauncherApplication.getInstance().getActivityStackManager().addActivity(this);
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				return;
			}
			if (MConstants.ACTION_REMOVE_IMAGE.equals(intent.getAction())) {
				mContext.sendBroadcast(new Intent(MConstants.ACTION_UPDATE_IMAGE_PAGE));
				String path = intent.getStringExtra("remove_path");
				if (!TextUtils.isEmpty(path) && adapter != null) {
					adapter.updateRemovePath(path);
				}
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (null == data) {
			return;
		}
		switch (requestCode) {
		case 1010:
			Bundle bundle = data.getExtras();
			PasterInfo pasterInfo = (PasterInfo) bundle.getSerializable("pasterInfo");
			int position = bundle.getInt("position");
			if (pasterInfos != null) {
				pasterInfos.set(position, pasterInfo);
				adapter.notifyDataSetChanged();
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@SuppressWarnings("unchecked")
		@Override
		public boolean handleMessage(Message msg) {
			int what = msg.what;
			mHandler.removeMessages(msg.what);
			switch (what) {
			case MSG_NOTIFY_CHANGED:
				pasterInfos = (ArrayList<PasterInfo>) msg.obj;
				adapter = new PasterListAdapter(pasterInfos);
				lv_select_paster.setAdapter(adapter);
				break;
			case JSON_NULL:
				RLog.i("JSON_NULL", "没有数据!");
				pasterInfos = new ArrayList<PasterInfo>();
				break;
			case MSG_REQUEST_CACHED_JSON:
				RLog.i("JSON_NULL", "错误!");
				requestCachedJson();
				break;
			default:
				break;
			}
			return false;
		}
	});

	private String getRequestUrl() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("name", "");
			jsonObject.put("begin_num", startNum);
			jsonObject.put("end_num", PAGE_SIZE);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = HostUtil.getUrl(MConstants.URL_STICKER_ALL + "?json=" + jsonObject.toString());
		return url;
	}

	private void requestUrlJson() {
		String url = getRequestUrl();
		if (url != null) {
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					parseJson(response);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					mHandler.sendEmptyMessage(MSG_REQUEST_CACHED_JSON);
				}
			});
			VolleyUtil.instance().addRequest(jsonObjectRequest);
		}
	}

	private void requestCachedJson() {
		String url = getRequestUrl();
		if (url != null) {
			JSONObject jsonObjectCached = VolleyUtil.instance().getJsonObject(url);
			if (jsonObjectCached != null) {
				parseJson(jsonObjectCached);
				return;
			}
		}
		mHandler.sendEmptyMessage(JSON_NULL);
	}

	private void parseJson(JSONObject jsonObject) {
		RLog.i("parseJson", jsonObject.toString());
		if (jsonObject.optInt("code") == 200 && jsonObject.has("json")) {
			ArrayList<PasterInfo> list = new ArrayList<PasterInfo>();
			JSONArray array = jsonObject.optJSONArray("json");
			if (array != null && array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject js = array.optJSONObject(i);
					PasterInfo pasterInfo = new PasterInfo();
					pasterInfo.setCreated(js.optString("created"));
					pasterInfo.setImage(js.optString("image"));
					pasterInfo.setMemo(js.optString("memo"));
					pasterInfo.setName(js.optString("name"));
					pasterInfo.setUrl(js.optString("url"));
					pasterInfo.setPreviewUrl(js.optString("show_url"));
					String zipPath = MConstants.DOWNLOAD_PATH + pasterInfo.getName() + ".zip";
					pasterInfo.setZipPath(zipPath);
					if (new File(zipPath).exists()) {
						pasterInfo.setDownloaded(true);
					}

					if (CoreService.donwloadPath.contains(pasterInfo.getUrl())) {
						pasterInfo.setDownloding(true);
					}

					String imagePath = MConstants.IMAGE_PATH + pasterInfo.getName();
					pasterInfo.setImagePath(imagePath);
					if (new File(imagePath).exists() && new File(imagePath) != null && new File(imagePath).listFiles().length > 0) {
						pasterInfo.setUnzip(true);
					}
					if (pasterInfo.isDownloaded()) {
						list.add(0, pasterInfo);
					} else {
						list.add(pasterInfo);
					}
				}
				Message message = new Message();
				message.what = MSG_NOTIFY_CHANGED;
				message.obj = list;
				mHandler.sendMessage(message);
			} else {
				mHandler.sendEmptyMessage(JSON_NULL);
			}
		}
	}

	private class PasterListAdapter extends BaseAdapter {

		private ArrayList<PasterInfo> pasterInfos;

		public PasterListAdapter(ArrayList<PasterInfo> pasterInfos) {
			this.pasterInfos = pasterInfos;
		}

		@Override
		public int getCount() {
			return pasterInfos.size();
		}

		@Override
		public PasterInfo getItem(int position) {
			return pasterInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_select_paster, null);
				viewHolder = new ViewHolder();
				viewHolder.item_iv_paster = (ImageView) convertView.findViewById(R.id.item_iv_paster);
				viewHolder.item_tv_paster_name = (TextView) convertView.findViewById(R.id.item_tv_paster_name);
				viewHolder.item_tv_paster_desc = (TextView) convertView.findViewById(R.id.item_tv_paster_desc);
				viewHolder.item_btn_paster_down = (Button) convertView.findViewById(R.id.item_btn_paster_down);
				viewHolder.download_paster_progressbar = (ProgressBar) convertView.findViewById(R.id.download_paster_progressbar);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final PasterInfo pasterInfo = pasterInfos.get(position);
			// 显示小贴纸
			VolleyUtil.instance().setUrlImage(VolleyUtil.instance().getRequestQueue(), viewHolder.item_iv_paster, pasterInfo.getImage(), 0, 0);
			viewHolder.item_tv_paster_name.setText(pasterInfo.getName());
			viewHolder.item_tv_paster_desc.setText(pasterInfo.getMemo());
			viewHolder.download_paster_progressbar.setMax(100);
			final String zipPath = pasterInfo.getZipPath();
			final String imagePath = pasterInfo.getImagePath();
			if (pasterInfo.isDownloaded()) {
				// 已经下载好的。不能点击
				viewHolder.item_btn_paster_down.setEnabled(false);
				viewHolder.item_btn_paster_down.setClickable(false);
				viewHolder.item_btn_paster_down.setText("已下载");
				viewHolder.item_btn_paster_down.setTextColor(Color.parseColor("#d3d3d3"));
				viewHolder.item_btn_paster_down.setBackgroundResource(R.drawable.btn_empty_blue_bg1);
				viewHolder.download_paster_progressbar.setVisibility(View.GONE);
				viewHolder.download_paster_progressbar.setProgress(0);
				// 判断文件夹是否有内容，如果有，代表已经解压
				if (!pasterInfo.isUnzip()) {
					if (new File(imagePath).exists()) {
						FileUtils.deleteFileByPath(imagePath);
					}
					try {
						ZipUtils.upZipFile(new File(zipPath), imagePath);
						VolleyUtil.instance().writeBitmapToFile(pasterInfo.getImage(), imagePath, "icon");
						new File(imagePath, MConstants.nomedia_file).createNewFile();
						pasterInfo.setUnzip(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				if(pasterInfo.isDownloding()){
					viewHolder.item_btn_paster_down.setEnabled(false);
					viewHolder.item_btn_paster_down.setClickable(false);
					viewHolder.download_paster_progressbar.setVisibility(View.VISIBLE);
					viewHolder.download_paster_progressbar.setProgress(pasterInfo.getProgress());
					viewHolder.item_btn_paster_down.setText(pasterInfo.getProgress() + "%");
				}else{
					viewHolder.item_btn_paster_down.setEnabled(true);
					viewHolder.item_btn_paster_down.setClickable(true);
					viewHolder.item_btn_paster_down.setText("下载");
					viewHolder.item_btn_paster_down.setTextColor(Color.parseColor("#3bac25"));
					viewHolder.item_btn_paster_down.setBackgroundResource(R.drawable.btn_empty_blue_bg_select);
					viewHolder.download_paster_progressbar.setVisibility(View.GONE);
					viewHolder.download_paster_progressbar.setProgress(0);
				}
			}

			viewHolder.item_btn_paster_down.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (DeviceInfoUtils.sdMounted()) {
						if (pasterInfo.isDownloding()) {
							return;
						}
						CoreService.donwloadPath.add(pasterInfo.getUrl());
						pasterInfo.setDownloding(true);
						viewHolder.item_btn_paster_down.setEnabled(false);
						viewHolder.item_btn_paster_down.setClickable(false);
						viewHolder.download_paster_progressbar.setVisibility(View.VISIBLE);
						viewHolder.download_paster_progressbar.setProgress(0);
						viewHolder.item_btn_paster_down.setText(0 + "%");
						FileDownloader fileDownloader = new FileDownloader(mContext, new FileDownloadListener() {

							@Override
							public void finish(String downloadUrl, String path) {
								RLog.i("download finish", "downloadUrl===" + downloadUrl);
								if (downloadUrl.equals(pasterInfo.getUrl())) {

									CoreService.donwloadPath.remove(downloadUrl);
									pasterInfo.setDownloding(false);
									pasterInfo.setDownloaded(true);
									notifyDataSetChanged();
									SimpleToast.makeText(mContext, R.string.download_succsed, SimpleToast.LENGTH_SHORT).show();

									if (new File(imagePath).exists()) {
										FileUtils.deleteFileByPath(imagePath);
									}
									try {
										ZipUtils.upZipFile(new File(zipPath), imagePath);
										VolleyUtil.instance().writeBitmapToFile(pasterInfo.getImage(), imagePath, "icon");
										new File(imagePath, MConstants.nomedia_file).createNewFile();
									} catch (ZipException e1) {
										e1.printStackTrace();
									} catch (IOException e1) {
										e1.printStackTrace();
									}
									mContext.sendBroadcast(new Intent(MConstants.ACTION_UPDATE_IMAGE_PAGE));
								}
							}

							@Override
							public void error(String downloadUrl) {
								RLog.i("download error", "downloadUrl===" + downloadUrl);
								if (downloadUrl.equals(pasterInfo.getUrl())) {
									CoreService.donwloadPath.remove(downloadUrl);
									pasterInfo.setDownloding(false);
									pasterInfo.setDownloaded(false);
									notifyDataSetChanged();
									SimpleToast.makeText(mContext, R.string.download_faild, SimpleToast.LENGTH_SHORT).show();
								}
							}

							@Override
							public void downloading(String downloadUrl, int size) {
								if (downloadUrl.equals(pasterInfo.getUrl())) {
									pasterInfo.setProgress(size);
									notifyDataSetChanged();
								}
							}
						});
						fileDownloader.setDownloadurl(pasterInfo.getUrl());
						fileDownloader.setFileDir(zipPath);
						fileDownloader.startDownload();
					} else {
						SimpleToast.makeText(mContext, R.string.sdcard_not_mounted_2, SimpleToast.LENGTH_SHORT).show();
					}
				}
			});
			return convertView;
		}

		public void updateRemovePath(String path) {
			for (int i = 0; i < pasterInfos.size(); i++) {
				PasterInfo pasterInfo = pasterInfos.get(i);
				String imagePath = pasterInfo.getImagePath();
				if (path.equals(imagePath)) {
					pasterInfo.setDownloaded(false);
					pasterInfo.setDownloding(false);
					pasterInfo.setUnzip(false);
					notifyDataSetChanged();
				}
			}
		}

	}

	class ViewHolder {
		ImageView item_iv_paster;
		TextView item_tv_paster_name;
		TextView item_tv_paster_desc;
		Button item_btn_paster_down;
		ProgressBar download_paster_progressbar;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}
}
