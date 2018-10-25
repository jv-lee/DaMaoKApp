package com.lockstudio.sticklocker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.TextSettingUtils;
import com.lockstudio.sticklocker.util.TextSettingUtils.OnPluginSettingChange;
import com.lockstudio.sticklocker.view.TextImageEditView;

import cn.opda.android.activity.R;

/**
 * 锁屏文字图片编辑页面
 * 
 * @author 庄宏岩
 * 
 */
public class TextImageEditActivity extends BaseActivity implements OnClickListener, OnPluginSettingChange {
	private TextImageEditView textImageEditView;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_image_edit);
		mContext = TextImageEditActivity.this;
		initViewAndEvent();
	}

	private void initViewAndEvent() {
		findViewById(R.id.diy_ok_image).setOnClickListener(this);
		String text = getIntent().getStringExtra("text");
		String image_path = getFileStreamPath("temp.png").getAbsolutePath();
		textImageEditView = (TextImageEditView) findViewById(R.id.textImageEditView);
		textImageEditView.setDrawText(text, image_path);

		LinearLayout controller_container_layout = (LinearLayout) findViewById(R.id.controller_container_layout);

		TextSettingUtils textSettingUtils = new TextSettingUtils(this);
		textSettingUtils.setOnPluginSettingChange(this);
		controller_container_layout.addView(textSettingUtils.getView());
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.diy_ok_image) {
			Bitmap bitmap = textImageEditView.creatNewImage();
			Intent intent = new Intent(mContext, DiyActivity.class);
			intent.putExtra("iconByte", DrawableUtils.bitmap2Byte(bitmap));
			setResult(MConstants.REQUEST_CODE_STICKER_EDIT, intent);
			finish();

		} else {
		}
	}

	@Override
	public void change(String fontPath, int color, int shadowColor) {
		textImageEditView.setTextFont(fontPath);
		textImageEditView.setDrawTextColor(color);
		textImageEditView.setDrawTextShadow(shadowColor);
	}

}
