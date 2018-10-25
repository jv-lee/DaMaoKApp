package com.lockstudio.sticklocker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.model.LocalImageInfo;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.FileUtils;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.view.TipsDialog;

import java.io.File;
import java.util.ArrayList;

import cn.opda.android.activity.R;

public class PasterRemoveActivity extends BaseActivity {
	private ArrayList<LocalImageInfo> localImageInfos = new ArrayList<LocalImageInfo>();
	private Context mContext;
	private TextView title_bar_left_tv,title_bar_right_tv;
	private ListView lv_remove_paster;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paster_remove);
		mContext = PasterRemoveActivity.this;
		title_bar_left_tv = (TextView) findViewById(R.id.title_bar_left_tv);
		title_bar_right_tv = (TextView) findViewById(R.id.title_bar_right_tv);
		title_bar_right_tv.setText("本地贴纸");
		lv_remove_paster = (ListView) findViewById(R.id.lv_remove_paster);
		title_bar_left_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
//		FancyLauncherApplication.getInstance().getActivityStackManager().addActivity(this);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				loadLocal();
			}
		}).start();
	}

	// 遍历file文件夹
	private void loadLocal() {
		File fileDir = new File(MConstants.IMAGE_PATH);
		if (fileDir.exists()) {
			if (fileDir.isDirectory()) {
				File[] files = fileDir.listFiles();
				for (File file : files) {
					if (file.isDirectory()) {
						LocalImageInfo localImageInfo = new LocalImageInfo();
						localImageInfo.setName(file.getName());
						localImageInfo.setPath(file.getAbsolutePath());
						localImageInfo.setIcon(DrawableUtils.getBitmap(mContext, new File(file.getAbsolutePath(), "/icon").getAbsolutePath()));
						localImageInfos.add(localImageInfo);
					}
				}
			}
		}
		handler.sendEmptyMessage(1);
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				PasterListAdapter listAdapter = new PasterListAdapter(localImageInfos);
				lv_remove_paster.setAdapter(listAdapter);
				break;

			default:
				break;
			}
		}
		
	};

	private class PasterListAdapter extends BaseAdapter {

		private ArrayList<LocalImageInfo> localImageInfos;

		public PasterListAdapter(ArrayList<LocalImageInfo> localImageInfos) {
			this.localImageInfos = localImageInfos;
		}

		@Override
		public int getCount() {
			return localImageInfos.size();
		}

		@Override
		public LocalImageInfo getItem(int position) {
			return localImageInfos.get(position);
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

			final LocalImageInfo localImageInfo = localImageInfos.get(position);
			viewHolder.download_paster_progressbar.setVisibility(View.GONE);
			viewHolder.item_iv_paster.setImageBitmap(localImageInfo.getIcon());
			viewHolder.item_tv_paster_name.setText(localImageInfo.getName());
			viewHolder.item_tv_paster_desc.setVisibility(View.GONE);
			viewHolder.item_btn_paster_down.setText("移除");
			viewHolder.item_btn_paster_down.setTextColor(Color.parseColor("#464646"));
			viewHolder.item_btn_paster_down.setBackgroundResource(R.drawable.btn_empty_blue_bg2);
			viewHolder.item_btn_paster_down.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final TipsDialog tipsDialog = new TipsDialog(mContext);
					tipsDialog.setMessage("是否删除该套贴纸?");
					tipsDialog.setCancelButton("取消", null);
					tipsDialog.setOkButton("删除", new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							tipsDialog.dismiss();
							String fileName = new File(localImageInfo.getPath()).getName();
							String zipPath = MConstants.DOWNLOAD_PATH + fileName + ".zip";
							FileUtils.deleteFileByPath(zipPath);
							FileUtils.deleteFileByPath(localImageInfo.getPath());
							localImageInfos.remove(localImageInfo);
							notifyDataSetChanged();
							Intent intent = new Intent(MConstants.ACTION_REMOVE_IMAGE);
							intent.putExtra("remove_path", localImageInfo.getPath());
							mContext.sendBroadcast(intent);
						}
					});
					tipsDialog.show();
				}
			});
			return convertView;
		}
	}

	class ViewHolder {
		ImageView item_iv_paster;
		TextView item_tv_paster_name;
		TextView item_tv_paster_desc;
		Button item_btn_paster_down;
		ProgressBar download_paster_progressbar;
	}
}
