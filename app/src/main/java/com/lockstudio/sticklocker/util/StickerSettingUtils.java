package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import cn.opda.android.activity.R;

public class StickerSettingUtils {
	private OnStickerSettingChange onStickerSettingChange;
	private View view;
	private SeekBar alpha_seekbar, size_seekbar;
	private int alpha;
	private float size;
	
	private TextView alpha_seekbar_info,size_seekbar_info;

	public StickerSettingUtils(Context mContext) {
		view = LayoutInflater.from(mContext).inflate(R.layout.image_sticker_edit_layout, null);
		
		size_seekbar_info=(TextView)view.findViewById(R.id.size_seekbar_info);
		alpha_seekbar_info=(TextView)view.findViewById(R.id.alpha_seekbar_info);

		alpha_seekbar = (SeekBar) view.findViewById(R.id.alpha_seekbar);
		alpha_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					alpha = progress;
					alpha_seekbar_info.setText((int)(alpha/255.0f*100)+"%");
					if (onStickerSettingChange != null) {
						onStickerSettingChange.change(size, alpha);
					}
				}

			}
		});

		size_seekbar = (SeekBar) view.findViewById(R.id.size_seekbar);
		size_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					size = progress / 100.0f;
					if (size < 0.3f) {
						size = 0.3f;
					}
					size_seekbar_info.setText((int) (size * 100)+"%");
					if (onStickerSettingChange != null) {
						onStickerSettingChange.change(size, alpha);
					}
				}
			}
		});
	}

	public void setOnStickerSettingChange(OnStickerSettingChange onStickerSettingChange) {
		this.onStickerSettingChange = onStickerSettingChange;
	}

	public View getView() {
		return view;
	}

	public void setAlpha(int max, int alpha) {
		this.alpha = alpha;
		alpha_seekbar.setMax(max);
		alpha_seekbar.setProgress(alpha);
		alpha_seekbar_info.setText((int)(alpha/255.0f*100)+"%");
	}

	public void setSize(float max, float size) {
		this.size = size;
		size_seekbar.setMax((int) (100 * max));
		size_seekbar.setProgress((int) (100 * size));
//		size_seekbar_info.setText((int) (size * 100)+"%");
	}

	public interface OnStickerSettingChange {
		public abstract void change(float size, int alpha);
	}
}
