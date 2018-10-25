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

public class ChooseLockSoundDialog extends BaseDialog {
	private TextView cancelButton, okButton;
	private RadioGroup radioGroup;
	private RadioButton radiobutton_null, radiobutton_system, radiobutton_lock1, radiobutton_lock2, radiobutton_lock3, radiobutton_lock4, radiobutton_lock5,
	                    radiobutton_lock6, radiobutton_lock7,radiobutton_iphone,radiobutton_lock_windows, radiobutton_lock_fangpi,radiobutton_lock_riyu;
	private boolean soundEnable;
	private int soundId;

	public ChooseLockSoundDialog(Context context) {
		super(context);

		soundEnable = LockApplication.getInstance().getConfig().isPlayLockSound();
		soundId = LockApplication.getInstance().getConfig().getLockSoundId();

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_choose_lock_sound_layout, null);
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
				LockApplication.getInstance().getConfig().setPlayLockSound(soundEnable);
				LockApplication.getInstance().getConfig().setLockSoundId(soundId);
				dismiss();
			}
		});
		radiobutton_null = (RadioButton) view.findViewById(R.id.radiobutton_null);
		radiobutton_system = (RadioButton) view.findViewById(R.id.radiobutton_system);
		radiobutton_lock1 = (RadioButton) view.findViewById(R.id.radiobutton_lock1);
		radiobutton_lock2 = (RadioButton) view.findViewById(R.id.radiobutton_lock2);
		radiobutton_lock3 = (RadioButton) view.findViewById(R.id.radiobutton_lock3);
		radiobutton_lock4 = (RadioButton) view.findViewById(R.id.radiobutton_lock4);
		radiobutton_lock5 = (RadioButton) view.findViewById(R.id.radiobutton_lock5);
		radiobutton_lock6 = (RadioButton) view.findViewById(R.id.radiobutton_lock6);
		radiobutton_lock7 = (RadioButton) view.findViewById(R.id.radiobutton_lock7);
		radiobutton_iphone = (RadioButton) view.findViewById(R.id.radiobutton_iphone);
		radiobutton_lock_windows = (RadioButton) view.findViewById(R.id.radiobutton_lock_windows);
		radiobutton_lock_fangpi = (RadioButton) view.findViewById(R.id.radiobutton_lock_fangpi);
		radiobutton_lock_riyu = (RadioButton) view.findViewById(R.id.radiobutton_lock_riyu);
		if (soundEnable) {
			if (soundId == 0) {
				radiobutton_system.setChecked(true);

			} else if (soundId == R.raw.lock_1) {
				radiobutton_lock1.setChecked(true);

			} else if (soundId == R.raw.lock_2) {
				radiobutton_lock2.setChecked(true);

			} else if (soundId == R.raw.lock_3) {
				radiobutton_lock3.setChecked(true);

			} else if (soundId == R.raw.lock_4) {
				radiobutton_lock4.setChecked(true);

			} else if (soundId == R.raw.lock_5) {
				radiobutton_lock5.setChecked(true);

			} else if (soundId == R.raw.lock_6) {
				radiobutton_lock6.setChecked(true);

			} else if (soundId == R.raw.lock_7) {
				radiobutton_lock7.setChecked(true);

			} else if (soundId == R.raw.lock_iphone) {
				radiobutton_iphone.setChecked(true);

			} else if (soundId == R.raw.lock_windows) {
				radiobutton_lock_windows.setChecked(true);

			} else if (soundId == R.raw.lock_fangpi) {
				radiobutton_lock_fangpi.setChecked(true);

			} else if (soundId == R.raw.lock_riyu) {
				radiobutton_lock_riyu.setChecked(true);

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

				} else if (checkedId == R.id.radiobutton_lock1) {
					soundEnable = true;
					soundId = R.raw.lock_1;

				} else if (checkedId == R.id.radiobutton_lock2) {
					soundEnable = true;
					soundId = R.raw.lock_2;

				} else if (checkedId == R.id.radiobutton_lock3) {
					soundEnable = true;
					soundId = R.raw.lock_3;

				} else if (checkedId == R.id.radiobutton_lock4) {
					soundEnable = true;
					soundId = R.raw.lock_4;

				} else if (checkedId == R.id.radiobutton_lock5) {
					soundEnable = true;
					soundId = R.raw.lock_5;

				} else if (checkedId == R.id.radiobutton_lock6) {
					soundEnable = true;
					soundId = R.raw.lock_6;

				} else if (checkedId == R.id.radiobutton_lock7) {
					soundEnable = true;
					soundId = R.raw.lock_7;

				} else if (checkedId == R.id.radiobutton_iphone) {
					soundEnable = true;
					soundId = R.raw.lock_iphone;

				} else if (checkedId == R.id.radiobutton_lock_windows) {
					soundEnable = true;
					soundId = R.raw.lock_windows;

				} else if (checkedId == R.id.radiobutton_lock_fangpi) {
					soundEnable = true;
					soundId = R.raw.lock_fangpi;

				} else if (checkedId == R.id.radiobutton_lock_riyu) {
					soundEnable = true;
					soundId = R.raw.lock_riyu;

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
				MediaPlayerUtils.play(mContext, "/system/media/audio/ui/Lock.ogg");
			} else {
				MediaPlayerUtils.play(mContext, soundId);
			}
		}
	}
}
