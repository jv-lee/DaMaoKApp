package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.lockstudio.sticklocker.view.ColorCircle;
import com.lockstudio.sticklocker.view.ColorPickerSeekBar;
import com.lockstudio.sticklocker.view.ColorPickerSeekBar.OnColorSeekBarChangeListener;

import java.util.ArrayList;

import cn.opda.android.activity.R;

public class PluginPhoneSMSUtils/* extends BaseActivity */implements OnClickListener {

	private Context mContext;

	private RelativeLayout plugin_tab_color;
	private LinearLayout colorpicker_layout;

	private ArrayList<ColorCircle> textColorCircles = new ArrayList<ColorCircle>();
	private int[] colors = { 0xffffffff, 0xff000000, 0xfffe0071, 0xffff4800, 0xfff36c60, 0xffffba00, 0xffffff77, 0xff00b3fc, 0xff00d9e1, 0xff00b6a5,
			0xff00e676, 0xffbdf400, 0xff444f89, 0xff9a68f2, 0xffb68cd7, 0xffa1887f, 0xffffe195, 0xff595959, 0xffb3b3b3 };
	private OnPluginSettingChange onPluginSettingChange;

	private ColorPickerSeekBar sb_plugin_color;

	private int plugin_color = Color.WHITE;
	private View view;

	public PluginPhoneSMSUtils(Context mContext) {
		this.mContext = mContext;
		view = LayoutInflater.from(mContext).inflate(R.layout.plugin_phone_sms_layout, null);
		plugin_tab_color = (RelativeLayout) view.findViewById(R.id.plugin_tab_color);

		colorpicker_layout = (LinearLayout) view.findViewById(R.id.colorpicker_layout);
		sb_plugin_color = (ColorPickerSeekBar) view.findViewById(R.id.sb_plugin_color);
		pluginSizeOrColerOrLightListener();
		initPickerView();

		plugin_tab_color.setOnClickListener(this);

	}

	public View getView() {
		return view;
	}

	private void initPickerView() {
		LayoutParams layoutParams = new LayoutParams(100, 100);
		layoutParams.height = (int) mContext.getResources().getDimension(R.dimen.color_circle_width);
		layoutParams.width = (int) mContext.getResources().getDimension(R.dimen.color_circle_width);
		layoutParams.leftMargin = 20;
		layoutParams.rightMargin = 20;

		for (int i = 0; i < colors.length; i++) {
			ColorCircle colorCircle = new ColorCircle(mContext);
			colorCircle.setLayoutParams(layoutParams);
			if (i == 0) {
				colorCircle.init(colors[i], true);
			} else {
				colorCircle.init(colors[i], false);
			}
			colorCircle.setTag("color");
			colorCircle.setId(i + 1);
			colorCircle.setOnClickListener(this);
			colorpicker_layout.addView(colorCircle);
			textColorCircles.add(colorCircle);
		}
	}

	private void pluginSizeOrColerOrLightListener() {

		sb_plugin_color.setOnColorSeekbarChangeListener(new OnColorSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onColorChanged(SeekBar seekBar, int color, boolean b) {
				plugin_color = color;
				onPluginSettingChange.change(plugin_color);
				for (int i = 0; i < textColorCircles.size(); i++) {
					textColorCircles.get(i).setSelecter(false);
				}
			}
		});

	}

	@Override
	public void onClick(View v) {

		if ("color".equals(v.getTag())) {
			int id = v.getId();
			if (id <= colors.length) {
				plugin_color = colors[id - 1];
				onPluginSettingChange.change(plugin_color);
				for (int i = 0; i < textColorCircles.size(); i++) {
					if (i == id - 1) {
						textColorCircles.get(i).setSelecter(true);
					} else {
						textColorCircles.get(i).setSelecter(false);
					}
				}
			}
			return;
		}

	}

	public OnPluginSettingChange getOnPluginSettingChange() {
		return onPluginSettingChange;
	}

	public void setOnPluginSettingChange(OnPluginSettingChange onPluginSettingChange) {
		this.onPluginSettingChange = onPluginSettingChange;
	}

	public interface OnPluginSettingChange {
		public abstract void change(int color);
	}

	public void initSelectData(int textColor) {
		this.plugin_color = textColor;

	}

}
