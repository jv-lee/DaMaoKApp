package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseDialog;
import com.lockstudio.sticklocker.util.MediaPlayerUtils;

import cn.opda.android.activity.R;

public class ChooseUnlockSoundDialog extends BaseDialog {
	private TextView cancelButton, okButton;
	private RadioGroup radioGroup;
	private RadioButton radiobutton_null, radiobutton_system, radiobutton_shuidi1, radiobutton_shuidi2, radiobutton_shuidi3, radiobutton_shuidi4,
			radiobutton_shuidi5, radiobutton_boli, radiobutton_baojian, radiobutton_dingdong, radiobutton_kaimen,radiobutton_unlock_sound1,
			radiobutton_unlock_sound2, radiobutton_unlock_windows, radiobutton_unlock_duola, radiobutton_unlock_fangpi, radiobutton_unlock_riyu;
	private boolean soundEnable;
	private int soundId;

	public ChooseUnlockSoundDialog(Context context) {
		super(context);

		soundEnable = LockApplication.getInstance().getConfig().isPlaySound();
		soundId = LockApplication.getInstance().getConfig().getUnlockSoundId();

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_choose_unlock_sound_layout, null);
		cancelButton = (TextView) view.findViewById(R.id.dialog_button_cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		okButton = (TextView) view.findViewById(R.id.dialog_button_ok);
		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LockApplication.getInstance().getConfig().setPlaySound(soundEnable);
				LockApplication.getInstance().getConfig().setUnlockSoundId(soundId);
				dismiss();
			}
		});
		radiobutton_null = (RadioButton) view.findViewById(R.id.radiobutton_null);
		radiobutton_system = (RadioButton) view.findViewById(R.id.radiobutton_system);
		radiobutton_shuidi1 = (RadioButton) view.findViewById(R.id.radiobutton_shuidi1);
		radiobutton_shuidi2 = (RadioButton) view.findViewById(R.id.radiobutton_shuidi2);
		radiobutton_shuidi3 = (RadioButton) view.findViewById(R.id.radiobutton_shuidi3);
		radiobutton_shuidi4 = (RadioButton) view.findViewById(R.id.radiobutton_shuidi4);
		radiobutton_shuidi5 = (RadioButton) view.findViewById(R.id.radiobutton_shuidi5);
		radiobutton_boli = (RadioButton) view.findViewById(R.id.radiobutton_boli);
		radiobutton_baojian = (RadioButton) view.findViewById(R.id.radiobutton_baojian);
		radiobutton_dingdong = (RadioButton) view.findViewById(R.id.radiobutton_dingdong);
		radiobutton_kaimen = (RadioButton) view.findViewById(R.id.radiobutton_kaimen);
		radiobutton_unlock_sound1 = (RadioButton) view.findViewById(R.id.radiobutton_unlock_sound1);
		radiobutton_unlock_sound2 = (RadioButton) view.findViewById(R.id.radiobutton_unlock_sound2);
		radiobutton_unlock_windows = (RadioButton) view.findViewById(R.id.radiobutton_unlock_windows);
		radiobutton_unlock_duola = (RadioButton) view.findViewById(R.id.radiobutton_unlock_duola);
		radiobutton_unlock_fangpi = (RadioButton) view.findViewById(R.id.radiobutton_unlock_fangpi);
		radiobutton_unlock_riyu = (RadioButton) view.findViewById(R.id.radiobutton_unlock_riyu);

		if (soundEnable) {
			if (soundId == 0) {
				radiobutton_system.setChecked(true);

			} else if (soundId == R.raw.unlock_shuidi1) {
				radiobutton_shuidi1.setChecked(true);

			} else if (soundId == R.raw.unlock_shuidi2) {
				radiobutton_shuidi2.setChecked(true);

			} else if (soundId == R.raw.unlock_shuidi3) {
				radiobutton_shuidi3.setChecked(true);

			} else if (soundId == R.raw.unlock_shuidi4) {
				radiobutton_shuidi4.setChecked(true);

			} else if (soundId == R.raw.unlock_shuidi5) {
				radiobutton_shuidi5.setChecked(true);

			} else if (soundId == R.raw.unlock_boli) {
				radiobutton_boli.setChecked(true);

			} else if (soundId == R.raw.unlock_baojian) {
				radiobutton_baojian.setChecked(true);

			} else if (soundId == R.raw.unlock_dingdong) {
				radiobutton_dingdong.setChecked(true);

			} else if (soundId == R.raw.unlock_kaimen) {
				radiobutton_kaimen.setChecked(true);

			} else if (soundId == R.raw.unlock_sound1) {
				radiobutton_unlock_sound1.setChecked(true);

			} else if (soundId == R.raw.unlock_sound2) {
				radiobutton_unlock_sound2.setChecked(true);

			} else if (soundId == R.raw.unlock_windows) {
				radiobutton_unlock_windows.setChecked(true);

			} else if (soundId == R.raw.unlock_duola) {
				radiobutton_unlock_duola.setChecked(true);

			} else if (soundId == R.raw.unlock_fangpi) {
				radiobutton_unlock_fangpi.setChecked(true);

			} else if (soundId == R.raw.unlock_riyu) {
				radiobutton_unlock_riyu.setChecked(true);

			} else {
			}

		} else {
			radiobutton_null.setChecked(true);
		}

		radioGroup = (RadioGroup) view.findViewById(R.id.choose_sound_radiogroup);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.radiobutton_null) {
					soundEnable = false;

				} else if (checkedId == R.id.radiobutton_system) {
					soundEnable = true;
					soundId = 0;

				} else if (checkedId == R.id.radiobutton_shuidi1) {
					soundEnable = true;
					soundId = R.raw.unlock_shuidi1;

				} else if (checkedId == R.id.radiobutton_shuidi2) {
					soundEnable = true;
					soundId = R.raw.unlock_shuidi2;

				} else if (checkedId == R.id.radiobutton_shuidi3) {
					soundEnable = true;
					soundId = R.raw.unlock_shuidi3;

				} else if (checkedId == R.id.radiobutton_shuidi4) {
					soundEnable = true;
					soundId = R.raw.unlock_shuidi4;

				} else if (checkedId == R.id.radiobutton_shuidi5) {
					soundEnable = true;
					soundId = R.raw.unlock_shuidi5;

				} else if (checkedId == R.id.radiobutton_boli) {
					soundEnable = true;
					soundId = R.raw.unlock_boli;

				} else if (checkedId == R.id.radiobutton_baojian) {
					soundEnable = true;
					soundId = R.raw.unlock_baojian;

				} else if (checkedId == R.id.radiobutton_dingdong) {
					soundEnable = true;
					soundId = R.raw.unlock_dingdong;

				} else if (checkedId == R.id.radiobutton_kaimen) {
					soundEnable = true;
					soundId = R.raw.unlock_kaimen;

				} else if (checkedId == R.id.radiobutton_unlock_sound1) {
					soundEnable = true;
					soundId = R.raw.unlock_sound1;

				} else if (checkedId == R.id.radiobutton_unlock_sound2) {
					soundEnable = true;
					soundId = R.raw.unlock_sound2;

				} else if (checkedId == R.id.radiobutton_unlock_windows) {
					soundEnable = true;
					soundId = R.raw.unlock_windows;

				} else if (checkedId == R.id.radiobutton_unlock_duola) {
					soundEnable = true;
					soundId = R.raw.unlock_duola;

				} else if (checkedId == R.id.radiobutton_unlock_fangpi) {
					soundEnable = true;
					soundId = R.raw.unlock_fangpi;

				} else if (checkedId == R.id.radiobutton_unlock_riyu) {
					soundEnable = true;
					soundId = R.raw.unlock_riyu;

				} else {
				}
				playSound();
			}
		});
		setGravityCenter(true);
		initViews(view);
	}

	private void playSound() {
		if (soundEnable) {
			if (soundId == 0) {
				MediaPlayerUtils.play(mContext, "/system/media/audio/ui/Unlock.ogg");
			} else {
				MediaPlayerUtils.play(mContext, soundId);
			}
		}
	}
}
