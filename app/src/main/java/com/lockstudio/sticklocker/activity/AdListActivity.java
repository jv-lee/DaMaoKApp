package com.lockstudio.sticklocker.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lockstudio.sticklocker.adapter.Adapter4Ads;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.model.AdInfo;
import com.lockstudio.sticklocker.service.DownloadService;
import com.lockstudio.sticklocker.util.BannerUtils;
import com.lockstudio.sticklocker.util.HostUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;

import cn.opda.android.activity.R;

public class AdListActivity extends BaseActivity {
	private Adapter4Ads adsAdapter;
	private ArrayList<AdInfo> adInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ad_list);
		initViewAndEvent();
		BannerUtils.setBannerTitle_String(this, "应用推荐");

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addDataScheme("package");
		registerReceiver(packageReceiver, intentFilter);
		
		IntentFilter intentFilter2 = new IntentFilter();
		intentFilter2.addAction("APP_DOWNLOAD_FAILD");
		registerReceiver(AppStateReceiver, intentFilter2);
		
		
	}

	BroadcastReceiver packageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
				String packageName = URI.create(intent.getDataString()).getSchemeSpecificPart();
				if(adsAdapter!=null){
					adsAdapter.updateInstalled(packageName);
				}
			}
		}
	};
	
	BroadcastReceiver AppStateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && "APP_DOWNLOAD_FAILD".equals(intent.getAction())) {
				String packageName = intent.getStringExtra("packageName");
				if(adsAdapter!=null){
					adsAdapter.updateDownloadFaild(packageName);
				}
			}
		}
	};

	private void initViewAndEvent() {

		ListView more_tools_listview = (ListView) findViewById(R.id.more_tools_listview);
		adInfos = new ArrayList<AdInfo>();

		adsAdapter = new Adapter4Ads(mContext, new ArrayList<AdInfo>());
		more_tools_listview.setAdapter(adsAdapter);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(HostUtil.getUrl("MasterLockNewToo/moretool?json=1"), null,
				new Response.Listener<JSONObject>() {
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

	private void parseJson(JSONObject response) {
		if (response != null && response.optInt("code") == 200) {
			JSONArray jsonArray = response.optJSONArray("json");
			if (jsonArray != null && jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.optJSONObject(i);

					AdInfo adInfo = new AdInfo();
					adInfo.setApkUrl(jsonObject.optString("apkUrl"));
					adInfo.setName(jsonObject.optString("name"));
					adInfo.setDesc(jsonObject.optString("desc"));
					adInfo.setPackageName(jsonObject.optString("packageName"));
					adInfo.setImageUrl(jsonObject.optString("imageUrl"));
					adInfo.setSize(jsonObject.optString("size"));
					adInfo.setButtonName(jsonObject.optString("buttonName"));
					PackageInfo packageInfo = null;
					try {
						packageInfo = mContext.getPackageManager().getPackageInfo(adInfo.getPackageName(), 0);
					} catch (NameNotFoundException e) {
					}
					if (packageInfo != null) {
						adInfo.setInstalled(true);
					} else if (DownloadService.downloadUrls.contains(adInfo.getPackageName())) {
						adInfo.setDownloading(true);
					}
					adInfos.add(adInfo);
				}
				mHandler.sendEmptyMessage(1);
			} else {
				mHandler.sendEmptyMessage(0);
			}
		} else {
			mHandler.sendEmptyMessage(0);
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 0:

				break;
			case 1:
				adsAdapter.setList(adInfos);
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(AppStateReceiver);
		unregisterReceiver(packageReceiver);

	}

}
