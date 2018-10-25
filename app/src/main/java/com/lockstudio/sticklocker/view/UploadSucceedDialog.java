package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.lockstudio.sticklocker.base.BaseDialog;

import cn.opda.android.activity.R;

public class UploadSucceedDialog extends BaseDialog {

	public UploadSucceedDialog(Context context) {
		super(context);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_upload_secceed_layout, null);
        setAlignTop(true);
        initViews(view);
	}
	
}
