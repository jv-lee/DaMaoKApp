package com.lockstudio.sticklocker.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Tommy.VolleyUtil;
import com.lockstudio.sticklocker.model.AdInfo;
import com.lockstudio.sticklocker.service.DownloadService;
import com.lockstudio.sticklocker.view.SimpleToast;

import java.util.ArrayList;

import cn.opda.android.activity.R;

/**
 * @author 庄宏岩
 * 
 */
public class Adapter4Ads extends BaseAdapter {
	private ArrayList<AdInfo> mAdInfos;
	private Context mContext;

	public Adapter4Ads(Context context, ArrayList<AdInfo> mAdInfos) {
		this.mAdInfos = mAdInfos;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return mAdInfos.size() > 0 ? mAdInfos.size() : 5;
	}

	@Override
	public Object getItem(int position) {
		return mAdInfos.get(position);
	}

	public ArrayList<AdInfo> getList() {
		return mAdInfos;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder mHolder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.listview_ad_item_layout, null);
			mHolder = new Holder();
			mHolder.ad_item_name = (TextView) convertView.findViewById(R.id.ad_item_name);
			mHolder.ad_item_icon = (ImageView) convertView.findViewById(R.id.ad_item_icon);
			mHolder.ad_item_title = (TextView) convertView.findViewById(R.id.ad_item_title);
			mHolder.ad_item_size = (TextView) convertView.findViewById(R.id.ad_item_size);
			mHolder.ad_item_download = (Button) convertView.findViewById(R.id.ad_item_download);
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		if (mAdInfos.size() > 0) {
			final AdInfo mAdInfo = mAdInfos.get(position);
			mHolder.ad_item_name.setText(mAdInfo.getName());
			mHolder.ad_item_title.setText(mAdInfo.getDesc());
			mHolder.ad_item_size.setText(mAdInfo.getSize());

			if (mAdInfo.isInstalled()) {
				mHolder.ad_item_download.setText("启动");
			} else if (mAdInfo.isDownloading()) {
				mHolder.ad_item_download.setText("下载中");
			} else {
				mHolder.ad_item_download.setText(mAdInfo.getButtonName());
			}
			VolleyUtil.instance().setUrlImage(VolleyUtil.instance().getRequestQueue(), mHolder.ad_item_icon, mAdInfo.getImageUrl(), R.drawable.ic_launcher_2,
					R.drawable.ic_launcher_2);
			mHolder.ad_item_download.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (!mHolder.ad_item_download.getText().equals("下载中")) {
						if (mHolder.ad_item_download.getText().equals("启动")) {
							mContext.startActivity(mContext.getPackageManager().getLaunchIntentForPackage(mAdInfo.getPackageName()));
						} else {
							if (!DownloadService.downloadUrls.contains(mAdInfo.getApkUrl())) {
								Intent intent = new Intent(mContext, DownloadService.class);
								intent.putExtra("name", mAdInfo.getName());
								intent.putExtra("url", mAdInfo.getApkUrl());
								intent.putExtra("imageUrl", mAdInfo.getImageUrl());
								intent.putExtra("packageName", mAdInfo.getPackageName());
								mContext.startService(intent);
								mHolder.ad_item_download.setText("下载中");
							} else {
								SimpleToast.makeText(mContext, R.string.app_is_downloading, SimpleToast.LENGTH_SHORT).show();
							}
						}
					}
				}
			});
		}
		return convertView;
	}

	class Holder {

		private ImageView ad_item_icon;
		private TextView ad_item_name;
		private Button ad_item_download;
		private TextView ad_item_title;
		private TextView ad_item_size;
	}

	public void setList(ArrayList<AdInfo> adInfos) {
		mAdInfos.addAll(adInfos);
		notifyDataSetChanged();
	}

	public void updateInstalled(String packageName) {
		for (int i = 0; i < mAdInfos.size(); i++) {
			AdInfo adInfo = mAdInfos.get(i);
			if (adInfo.getPackageName().equals(packageName)) {
				adInfo.setInstalled(true);
				adInfo.setDownloading(false);
				notifyDataSetChanged();
				break;
			}
		}
	}

	public void updateDownloadFaild(String packageName) {
		for (int i = 0; i < mAdInfos.size(); i++) {
			AdInfo adInfo = mAdInfos.get(i);
			if (adInfo.getPackageName().equals(packageName)) {
				adInfo.setDownloading(false);
				notifyDataSetChanged();
				break;
			}
		}

	}

}
