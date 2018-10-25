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

import com.lockstudio.sticklocker.model.BatteryStickerInfo;
import com.lockstudio.sticklocker.model.StickerInfo;

import java.util.ArrayList;

import cn.opda.android.activity.R;

/**
 * 选择锁屏样式的工具类
 * 
 * @author 庄宏岩
 * 
 */
public class ChooseBatteryUitls {
	private Context mContext;
	private View view;
	private GridView plugin_gridview;
	private OnPluginSelectorListener3 mOnPluginSelectorListener3;
	private ChooseStickerAdapter chooseStickerAdapter;

	public ChooseBatteryUitls(Context context) {
		this.mContext = context;
		view = LayoutInflater.from(mContext).inflate(R.layout.choose_time_layout, null);
		plugin_gridview = (GridView) view.findViewById(R.id.time_gridview);
		initData();
	}

	private void initData() {

		ArrayList<StickerInfo> stickerInfos = new ArrayList<StickerInfo>();

		for (int i = 0; i < 6; i++) {
			BatteryStickerInfo batteryStickerInfo = new BatteryStickerInfo();
			
			batteryStickerInfo.width = DensityUtil.dip2px(mContext, 60);
			batteryStickerInfo.height = DensityUtil.dip2px(mContext, 60);
			batteryStickerInfo.styleId = StickerInfo.StyleBattery;
			batteryStickerInfo.textSize = DensityUtil.dip2px(mContext, 10);
			
			batteryStickerInfo.batteryStyle = i;
			batteryStickerInfo.textColor = Color.WHITE;
			batteryStickerInfo.shadowColor = Color.TRANSPARENT;
			batteryStickerInfo.x = 150;
			batteryStickerInfo.y = 150;
			batteryStickerInfo.angle = 0;
			switch (i) {
			case 0:
				batteryStickerInfo.stickerName = "样式一";
				batteryStickerInfo.previewRes = R.drawable.plugin_battery_ic;
				break;
			case 1:
				batteryStickerInfo.stickerName = "样式二";
				batteryStickerInfo.previewRes = R.drawable.plugin_battery_style_ic1;
				break;
			case 2:
				batteryStickerInfo.stickerName = "样式三";
				batteryStickerInfo.previewRes = R.drawable.plugin_battery_style_ic2;
				break;
			case 3:
				batteryStickerInfo.stickerName = "样式四";
				batteryStickerInfo.previewRes = R.drawable.plugin_battery_style_ic3;
				break;
			case 4:
				batteryStickerInfo.stickerName = "样式五";
				batteryStickerInfo.previewRes = R.drawable.plugin_battery_style_ic4;
				break;
			case 5:
				batteryStickerInfo.stickerName = "样式六";
				batteryStickerInfo.previewRes = R.drawable.plugin_battery_style_ic5;
				break;

			default:
				break;
			}
			stickerInfos.add(batteryStickerInfo);
		}

		chooseStickerAdapter = new ChooseStickerAdapter(mContext, stickerInfos);
		plugin_gridview.setAdapter(chooseStickerAdapter);
		plugin_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StickerInfo stickerInfo = (StickerInfo) parent.getItemAtPosition(position);
				if (mOnPluginSelectorListener3 != null) {
					mOnPluginSelectorListener3.selectPlugin(stickerInfo);
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

	public void setOnPluginSelectorListener3(OnPluginSelectorListener3 onPluginSelectorListener) {
		this.mOnPluginSelectorListener3 = onPluginSelectorListener;
	}

	public interface OnPluginSelectorListener3 {
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
