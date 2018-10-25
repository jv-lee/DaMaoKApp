package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.lockstudio.sticklocker.base.BaseDialog;

import cn.opda.android.activity.R;

public class TipsDialog extends BaseDialog {
	private TextView textView;
	private Button cancelButton;
	private Button okButton;
	private Button greenButton;
	private View zhanwei_view;

	public TipsDialog(Context context) {
		super(context);

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_tips_layout, null);
		textView = (TextView) view.findViewById(R.id.message);
		okButton = (Button) view.findViewById(R.id.dialog_button_ok);
		greenButton = (Button) view.findViewById(R.id.dialog_button_green);
		cancelButton = (Button) view.findViewById(R.id.dialog_button_cancel);
		zhanwei_view = view.findViewById(R.id.button_zhanwei_view);
		setAlignTop(true);
		initViews(view);
	}
	
	public void setSystemAlert(){
		getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	}

	public void setMessage(String message) {
		textView.setText(message);
	}

	public void hideZhanwei() {
		zhanwei_view.setVisibility(View.GONE);
	}

	public void setOkButton(String button, View.OnClickListener onClickListener) {
		okButton.setVisibility(View.VISIBLE);
		okButton.setText(button);
		if (onClickListener != null) {
			okButton.setOnClickListener(onClickListener);
		} else {
			okButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}
	}

	public void setOkButton(int button, View.OnClickListener onClickListener) {
		okButton.setVisibility(View.VISIBLE);
		okButton.setText(button);
		if (onClickListener != null) {
			okButton.setOnClickListener(onClickListener);
		} else {
			okButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}
	}

	public void setCancelButton(String button, View.OnClickListener onClickListener) {
		cancelButton.setVisibility(View.VISIBLE);
		cancelButton.setText(button);
		if (onClickListener != null) {
			cancelButton.setOnClickListener(onClickListener);
		} else {
			cancelButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}
	}

	public void setCancelButton(int button, View.OnClickListener onClickListener) {
		cancelButton.setVisibility(View.VISIBLE);
		cancelButton.setText(button);
		if (onClickListener != null) {
			cancelButton.setOnClickListener(onClickListener);
		} else {
			cancelButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}
	}
	
	public void setGreenButton(String button, View.OnClickListener onClickListener) {
		greenButton.setVisibility(View.VISIBLE);
		greenButton.setText(button);
		if (onClickListener != null) {
			greenButton.setOnClickListener(onClickListener);
		} else {
			greenButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}
	}
	
	public void setGreenButton(int button, View.OnClickListener onClickListener) {
		greenButton.setVisibility(View.VISIBLE);
		greenButton.setText(button);
		if (onClickListener != null) {
			greenButton.setOnClickListener(onClickListener);
		} else {
			greenButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}
	}

	public void setMessage(int stringRes) {
		textView.setText(stringRes);
	}
}
