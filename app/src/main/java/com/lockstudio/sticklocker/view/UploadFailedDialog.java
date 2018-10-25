package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lockstudio.sticklocker.base.BaseDialog;

import cn.opda.android.activity.R;

public class UploadFailedDialog extends BaseDialog implements OnClickListener {
	private OnOkClickListener onOkClickListener;

	public UploadFailedDialog(Context context) {
		super(context);

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_upload_failed_layout, null);
		Button upload_button = (Button) view.findViewById(R.id.upload_button);
		upload_button.setOnClickListener(this);
		setAlignTop(true);
		initViews(view);
	}

	public void setOnOkClickListener(OnOkClickListener onOkClickListener) {
		this.onOkClickListener = onOkClickListener;
	}

	public interface OnOkClickListener {
		public void OnOkClick();
	}

	@Override
	public void onClick(View v) {
		onOkClickListener.OnOkClick();
		dismiss();
	}
}
