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
import com.lockstudio.sticklocker.model.WeatherStickerInfo;

import java.util.ArrayList;

import cn.opda.android.activity.R;

/**
 * 选择锁屏样式的工具类
 * 
 * @author 庄宏岩
 * 
 */
public class ChooseWeatherUitls {
	private Context mContext;
	private View view;
	private GridView plugin_gridview;
	private OnPluginSelectorListener2 mOnPluginSelectorListener2;
	private ChooseStickerAdapter chooseStickerAdapter;

	public ChooseWeatherUitls(Context context) {
		this.mContext = context;
		view = LayoutInflater.from(mContext).inflate(R.layout.choose_time_layout, null);
		plugin_gridview = (GridView) view.findViewById(R.id.time_gridview);
		initData();
	}

	private void initData() {

		ArrayList<StickerInfo> stickerInfos = new ArrayList<StickerInfo>();

		
		for(int i =0;i<7;i++){
			WeatherStickerInfo wheatherStickerInfo = new WeatherStickerInfo();
			wheatherStickerInfo.styleId = StickerInfo.StyleWeather;
			wheatherStickerInfo.weatherStyle = i;
			wheatherStickerInfo.textSize1 = (int) mContext.getResources().getDimension(R.dimen.default_time_textsize);
			wheatherStickerInfo.textSize2 = (int) mContext.getResources().getDimension(R.dimen.default_time_textsize_2);
			wheatherStickerInfo.textColor = Color.WHITE;
			wheatherStickerInfo.shadowColor = Color.TRANSPARENT;
			wheatherStickerInfo.x = 150;
			wheatherStickerInfo.y = 150;
			wheatherStickerInfo.angle = 0;
			switch (i) {
			case 0:
				wheatherStickerInfo.stickerName = "样式一";
				wheatherStickerInfo.previewRes = R.drawable.weather_1;
				break;
			case 1:
				wheatherStickerInfo.stickerName = "样式二";
				wheatherStickerInfo.previewRes = R.drawable.weather_2;
				break;
			case 2:
				wheatherStickerInfo.stickerName = "样式三";
				wheatherStickerInfo.previewRes = R.drawable.weather_3;
				break;
			case 3:
				wheatherStickerInfo.stickerName = "样式四";
				wheatherStickerInfo.previewRes = R.drawable.weather_4;
				break;
			case 4:
				wheatherStickerInfo.stickerName = "样式五";
				wheatherStickerInfo.previewRes = R.drawable.weather_5;
				break;
			case 5:
				wheatherStickerInfo.stickerName = "样式六";
				wheatherStickerInfo.previewRes = R.drawable.weather_6;
				break;
			case 6:
				wheatherStickerInfo.stickerName = "样式七";
				wheatherStickerInfo.previewRes = R.drawable.weather_7;
				break;

			default:
				break;
			}
			stickerInfos.add(wheatherStickerInfo);
			
		}
		chooseStickerAdapter = new ChooseStickerAdapter(mContext, stickerInfos);
		plugin_gridview.setAdapter(chooseStickerAdapter);
		plugin_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StickerInfo stickerInfo = (StickerInfo) parent.getItemAtPosition(position);
				if (mOnPluginSelectorListener2 != null) {
					mOnPluginSelectorListener2.selectPlugin(stickerInfo);
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

	public void setOnPluginSelectorListener2(OnPluginSelectorListener2 onPluginSelectorListener) {
		this.mOnPluginSelectorListener2 = onPluginSelectorListener;
	}

	public interface OnPluginSelectorListener2 {
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
