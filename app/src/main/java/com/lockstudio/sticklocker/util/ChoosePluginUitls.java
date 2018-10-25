package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.BatteryStickerInfo;
import com.lockstudio.sticklocker.model.CameraStickerInfo;
import com.lockstudio.sticklocker.model.DayWordStickerInfo;
import com.lockstudio.sticklocker.model.FlashlightStickerInfo;
import com.lockstudio.sticklocker.model.HollowWordsStickerInfo;
import com.lockstudio.sticklocker.model.StatusbarStickerInfo;
import com.lockstudio.sticklocker.model.StickerInfo;
import com.lockstudio.sticklocker.model.TimeStickerInfo;
import com.lockstudio.sticklocker.model.WeatherStickerInfo;

import java.io.File;
import java.util.ArrayList;

import cn.opda.android.activity.R;

/**
 * 选择插件的工具类
 * 
 * @author 庄宏岩
 * 
 */
public class ChoosePluginUitls {
	private Context mContext;
	private View view;
	private GridView plugin_gridview;
	private OnPluginSelectorListener mOnPluginSelectorListener;
	private ChooseStickerAdapter chooseStickerAdapter;

	public ChoosePluginUitls(Context context) {
		this.mContext = context;
		view = LayoutInflater.from(mContext).inflate(R.layout.choose_plugin_layout, null);
		plugin_gridview = (GridView) view.findViewById(R.id.plugin_gridview);
		initData();
	}

	private void initData() {

		ArrayList<StickerInfo> stickerInfos = new ArrayList<StickerInfo>();


		TimeStickerInfo timeStickerInfo = new TimeStickerInfo();
		timeStickerInfo.styleId = StickerInfo.StyleTime;
		timeStickerInfo.textSize1 = (int) mContext.getResources().getDimension(R.dimen.default_time_textsize);
		timeStickerInfo.textSize2 = (int) mContext.getResources().getDimension(R.dimen.default_time_textsize_2);
		timeStickerInfo.textColor = Color.WHITE;
		timeStickerInfo.shadowColor = Color.TRANSPARENT;
		timeStickerInfo.x = 200;
		timeStickerInfo.y = 250;
		timeStickerInfo.angle = 0;
		timeStickerInfo.stickerName = "时间";
		timeStickerInfo.previewRes = R.drawable.plugin_date;
		if (new File(MConstants.TTF_PATH + "en_one.ttf").exists()) {
			timeStickerInfo.font = MConstants.TTF_PATH + "en_one.ttf";
		}
		stickerInfos.add(timeStickerInfo);
		
		
		WeatherStickerInfo weatherStickerInfo = new WeatherStickerInfo();
		weatherStickerInfo.styleId = StickerInfo.StyleWeather;
		weatherStickerInfo.textSize1 = DensityUtil.dip2px(mContext, 35);
		weatherStickerInfo.textSize2 = DensityUtil.dip2px(mContext, 18);
		weatherStickerInfo.textColor = Color.WHITE;
		weatherStickerInfo.shadowColor = Color.TRANSPARENT;
		weatherStickerInfo.x = 200;
		weatherStickerInfo.y = 250;
		weatherStickerInfo.angle = 0;
		weatherStickerInfo.stickerName = "天气";
		weatherStickerInfo.previewRes = R.drawable.plugin_weather;
		if (new File(MConstants.TTF_PATH + "en_one.ttf").exists()) {
			weatherStickerInfo.font = MConstants.TTF_PATH + "en_one.ttf";
		}
		stickerInfos.add(weatherStickerInfo);


		HollowWordsStickerInfo hollowWordsStickerInfo = new HollowWordsStickerInfo();
		hollowWordsStickerInfo.styleId = StickerInfo.StyleHollowWords;
		hollowWordsStickerInfo.stickerName = "字中字";
		hollowWordsStickerInfo.previewRes = R.drawable.plugin_hollowwords;
		hollowWordsStickerInfo.upTextSize = DensityUtil.dip2px(mContext, 14);
		hollowWordsStickerInfo.downTextSize = DensityUtil.dip2px(mContext, 51);
		hollowWordsStickerInfo.upTextColor = Color.WHITE;
		hollowWordsStickerInfo.downTextColor = Color.WHITE;
		hollowWordsStickerInfo.upShadowColor = Color.TRANSPARENT;
		hollowWordsStickerInfo.downShadowColor = Color.TRANSPARENT;
		hollowWordsStickerInfo.upText = "我心阳光 无畏悲伤";
		hollowWordsStickerInfo.downText = "阳光";
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(hollowWordsStickerInfo.upTextSize);
		int width = (int) paint.measureText(hollowWordsStickerInfo.upText);
		hollowWordsStickerInfo.x = (LockApplication.getInstance().getConfig().getScreenWidth() - width) / 2;
		hollowWordsStickerInfo.y = 250;
		stickerInfos.add(hollowWordsStickerInfo);

		StatusbarStickerInfo statusbarStickerInfo = new StatusbarStickerInfo();
		statusbarStickerInfo.styleId = StickerInfo.StyleStatusbar;
		statusbarStickerInfo.stickerName = "状态栏";
		statusbarStickerInfo.previewRes = R.drawable.plugin_statebar;
		statusbarStickerInfo.textSize = (int) mContext.getResources().getDimension(R.dimen.statusbar_textsize);
		statusbarStickerInfo.textRes = R.drawable.status_lock;
		statusbarStickerInfo.textColor = Color.WHITE;
		statusbarStickerInfo.shadowColor = Color.TRANSPARENT;
		statusbarStickerInfo.text = "文字锁屏";
		statusbarStickerInfo.x = 0;
		statusbarStickerInfo.y = 0;
		statusbarStickerInfo.angle = 0;
		stickerInfos.add(statusbarStickerInfo);

		CameraStickerInfo cameraStickerInfo = new CameraStickerInfo();
		cameraStickerInfo.width = DensityUtil.dip2px(mContext, 60);
		cameraStickerInfo.height = DensityUtil.dip2px(mContext, 60);
		cameraStickerInfo.styleId = StickerInfo.StyleCamera;
		cameraStickerInfo.previewRes = R.drawable.plugin_camera_ic;
		cameraStickerInfo.textSize = DensityUtil.dip2px(mContext, 20);
		cameraStickerInfo.textColor = Color.WHITE;
		cameraStickerInfo.x = 250;
		cameraStickerInfo.y = 250;
		cameraStickerInfo.angle = 0;
		cameraStickerInfo.isClick = true;
		cameraStickerInfo.stickerName = "照相机";
		stickerInfos.add(cameraStickerInfo);
		
		FlashlightStickerInfo flashlightStickerInfo = new FlashlightStickerInfo();
		flashlightStickerInfo.width = DensityUtil.dip2px(mContext, 60);
		flashlightStickerInfo.height = DensityUtil.dip2px(mContext, 60);
		flashlightStickerInfo.styleId = StickerInfo.StyleFlashlight;
		flashlightStickerInfo.previewRes = R.drawable.plugin_flashlight_ic;
		flashlightStickerInfo.textSize = DensityUtil.dip2px(mContext, 20);
		flashlightStickerInfo.textColor = Color.WHITE;
		flashlightStickerInfo.x = 250;
		flashlightStickerInfo.y = 250;
		flashlightStickerInfo.angle = 0;
		flashlightStickerInfo.isClick = true;
		flashlightStickerInfo.stickerName = "手电筒";
		stickerInfos.add(flashlightStickerInfo);
		
		/**
		 * 
		 
		PhoneStickerInfo phoneStickerInfo = new PhoneStickerInfo();
		phoneStickerInfo.width = DensityUtil.dip2px(mContext, 60);
		phoneStickerInfo.height = DensityUtil.dip2px(mContext, 60);
		phoneStickerInfo.styleId = StickerInfo.StylePhone;
		phoneStickerInfo.previewRes = R.drawable.plugin_phone_ic;
		phoneStickerInfo.textSize = DensityUtil.dip2px(mContext, 20);
		phoneStickerInfo.textColor = Color.WHITE;
		phoneStickerInfo.x = 250;
		phoneStickerInfo.y = 250;
		phoneStickerInfo.angle = 0;
		phoneStickerInfo.stickerName = "未接来电";
		stickerInfos.add(phoneStickerInfo);

		SMSStickerInfo smsStickerInfo = new SMSStickerInfo();
		smsStickerInfo.width = DensityUtil.dip2px(mContext, 60);
		smsStickerInfo.height = DensityUtil.dip2px(mContext, 60);
		smsStickerInfo.styleId = StickerInfo.StyleSMS;
		smsStickerInfo.previewRes = R.drawable.plugin_sms_ic;
		smsStickerInfo.textSize = DensityUtil.dip2px(mContext, 20);
		smsStickerInfo.textColor = Color.WHITE;
		smsStickerInfo.x = 250;
		smsStickerInfo.y = 250;
		smsStickerInfo.angle = 0;
		smsStickerInfo.stickerName = "未读短信";
		stickerInfos.add(smsStickerInfo);
		*/

		StickerInfo stickerInfo1 = new StickerInfo();
		stickerInfo1.stickerName = "未读消息";
		stickerInfo1.styleId=StickerInfo.StyleMessage;
		stickerInfo1.previewRes = R.drawable.plugin_message;
		stickerInfos.add(stickerInfo1);
		
		StickerInfo stickerInfo = new StickerInfo();
		stickerInfo.stickerName = "纪念日";
		stickerInfo.previewRes = R.drawable.plugin_mark;
		stickerInfo.styleId = StickerInfo.StyleTimer;
		stickerInfos.add(stickerInfo);
		
		BatteryStickerInfo batteryStickerInfo = new BatteryStickerInfo();
		batteryStickerInfo.width = DensityUtil.dip2px(mContext, 60);
		batteryStickerInfo.height = DensityUtil.dip2px(mContext, 60);
		batteryStickerInfo.styleId = StickerInfo.StyleBattery;
		batteryStickerInfo.previewRes = R.drawable.plugin_battery_ic;
		batteryStickerInfo.textSize = DensityUtil.dip2px(mContext, 20);
		batteryStickerInfo.textColor = Color.WHITE;
		batteryStickerInfo.x = 250;
		batteryStickerInfo.y = 250;
		batteryStickerInfo.angle = 0;
		batteryStickerInfo.stickerName = "电量";
		stickerInfos.add(batteryStickerInfo);
		
		DayWordStickerInfo dayWordStickerInfo = new DayWordStickerInfo();
		dayWordStickerInfo.textSize = DensityUtil.dip2px(mContext, 18);
		dayWordStickerInfo.styleId = StickerInfo.StyleDayWord;
		dayWordStickerInfo.textColor = Color.WHITE;
		dayWordStickerInfo.shadowColor = Color.TRANSPARENT;
		dayWordStickerInfo.text = LockApplication.getInstance().getConfig().getDailyWords();
		dayWordStickerInfo.x = DensityUtil.dip2px(mContext, 20);
		dayWordStickerInfo.y = LockApplication.getInstance().getConfig().getScreenHeight() / 2;
		dayWordStickerInfo.angle = 0;
		dayWordStickerInfo.stickerName = "每日一言";
		dayWordStickerInfo.previewRes = R.drawable.plugin_dayword;
		stickerInfos.add(dayWordStickerInfo);
		
		
		chooseStickerAdapter = new ChooseStickerAdapter(mContext, stickerInfos);
		plugin_gridview.setAdapter(chooseStickerAdapter);
		plugin_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StickerInfo stickerInfo = (StickerInfo) parent.getItemAtPosition(position);
				if (mOnPluginSelectorListener != null) {
					mOnPluginSelectorListener.selectPlugin(stickerInfo);
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

	public void setOnPluginSelectorListener(OnPluginSelectorListener onPluginSelectorListener) {
		this.mOnPluginSelectorListener = onPluginSelectorListener;
	}

	public interface OnPluginSelectorListener {
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
