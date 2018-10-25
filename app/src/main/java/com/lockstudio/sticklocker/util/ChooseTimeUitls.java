package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lockstudio.sticklocker.model.StickerInfo;
import com.lockstudio.sticklocker.model.TimeStickerInfo;

import java.util.ArrayList;

import cn.opda.android.activity.R;

/**
 * 选择锁屏样式的工具类
 * 
 * @author 庄宏岩
 * 
 */
public class ChooseTimeUitls {
	private Context mContext;
	private View view;
	private GridView plugin_gridview;
	private OnPluginSelectorListener1 mOnPluginSelectorListener1;
	private ChooseStickerAdapter chooseStickerAdapter;

	public ChooseTimeUitls(Context context) {
		this.mContext = context;
		view = LayoutInflater.from(mContext).inflate(R.layout.choose_time_layout, null);
		plugin_gridview = (GridView) view.findViewById(R.id.time_gridview);
		initData();
	}

	private void initData() {

		ArrayList<StickerInfo> stickerInfos = new ArrayList<StickerInfo>();

		for (int i = 0; i < 12; i++) {
			TimeStickerInfo timeStickerInfo = new TimeStickerInfo();
			timeStickerInfo.styleId = StickerInfo.StyleTime;
			timeStickerInfo.timeStyle = i;
			timeStickerInfo.textSize1 = (int) mContext.getResources().getDimension(R.dimen.default_time_textsize);
			timeStickerInfo.textSize2 = (int) mContext.getResources().getDimension(R.dimen.default_time_textsize_2);
			timeStickerInfo.textColor = Color.WHITE;
			timeStickerInfo.shadowColor = Color.TRANSPARENT;
			timeStickerInfo.x = 150;
			timeStickerInfo.y = 150;
			timeStickerInfo.angle = 0;
			switch (i) {
			case 0:
				timeStickerInfo.stickerName = "样式一";
				timeStickerInfo.previewRes = R.drawable.time_1;
				break;
			case 1:
				timeStickerInfo.stickerName = "样式二";
				timeStickerInfo.previewRes = R.drawable.time_2;
				break;
			case 2:
				timeStickerInfo.stickerName = "样式三";
				timeStickerInfo.previewRes = R.drawable.time_3;
				break;
			case 3:
				timeStickerInfo.stickerName = "样式四";
				timeStickerInfo.previewRes = R.drawable.time_4;
				break;
			case 4:
				timeStickerInfo.stickerName = "样式五";
				timeStickerInfo.previewRes = R.drawable.time_5;
				break;
			case 5:
				timeStickerInfo.stickerName = "样式六";
				timeStickerInfo.previewRes = R.drawable.time_6;
				break;
			case 6:
				timeStickerInfo.stickerName = "样式七";
				timeStickerInfo.previewRes = R.drawable.time_7;
				break;
			case 7:
				timeStickerInfo.stickerName = "样式八";
				timeStickerInfo.previewRes = R.drawable.time_8;
				break;
			case 8:
				timeStickerInfo.stickerName = "样式九";
				timeStickerInfo.previewRes = R.drawable.time_9;
				break;
			case 9:
				timeStickerInfo.stickerName = "样式十";
				timeStickerInfo.previewRes = R.drawable.time_10;
				break;
			case 10:
				timeStickerInfo.stickerName = "样式十一";
				timeStickerInfo.previewRes = R.drawable.time_11;
				break;
			case 11:
				timeStickerInfo.stickerName = "样式十二";
				timeStickerInfo.previewRes = R.drawable.time_12;
				break;

			default:
				break;
			}
			stickerInfos.add(timeStickerInfo);
		}

		chooseStickerAdapter = new ChooseStickerAdapter(mContext, stickerInfos);
		plugin_gridview.setAdapter(chooseStickerAdapter);
		plugin_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StickerInfo stickerInfo = (StickerInfo) parent.getItemAtPosition(position);
				if (mOnPluginSelectorListener1 != null) {
					mOnPluginSelectorListener1.selectPlugin(stickerInfo);
				}
			}
		});
		setGridViewWidth();

	}

	private void setGridViewWidth() {
		LayoutParams params = new LayoutParams(chooseStickerAdapter.getCount() * (DensityUtil.dip2px(mContext, 76)), LayoutParams.WRAP_CONTENT);
		plugin_gridview.setLayoutParams(params);
		plugin_gridview.setHorizontalSpacing(DensityUtil.dip2px(mContext, 4));
		plugin_gridview.setVerticalSpacing(0);
		plugin_gridview.setColumnWidth(DensityUtil.dip2px(mContext, 72));
		plugin_gridview.setStretchMode(GridView.NO_STRETCH);
		plugin_gridview.setNumColumns(chooseStickerAdapter.getCount());
	}

	public View getView() {
		return view;
	}

	public void setOnPluginSelectorListener1(OnPluginSelectorListener1 onPluginSelectorListener) {
		this.mOnPluginSelectorListener1 = onPluginSelectorListener;
	}

	public interface OnPluginSelectorListener1 {
		void selectPlugin(StickerInfo stickerInfo);
	}

	public class ChooseStickerAdapter extends BaseAdapter {
		private ArrayList<StickerInfo> stickerInfos = new ArrayList<StickerInfo>();
		private LayoutInflater inflater;

		public ChooseStickerAdapter(Context mContext, ArrayList<StickerInfo> stickerInfos) {
			this.stickerInfos = stickerInfos;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return stickerInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return stickerInfos.get(position);
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
				convertView = inflater.inflate(R.layout.gridview_item_choose_sticker, parent, false);
				holder.sticker_imageview = (ImageView) convertView.findViewById(R.id.sticker_imageview);
				holder.sticker_textview = (TextView) convertView.findViewById(R.id.sticker_textview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final StickerInfo stickerInfo = stickerInfos.get(position);
			holder.sticker_imageview.setImageResource(stickerInfo.previewRes);
			holder.sticker_textview.setText(stickerInfo.stickerName);
			return convertView;
		}

		class ViewHolder {
			public ImageView sticker_imageview;
			public TextView sticker_textview;
		}
	}
}
