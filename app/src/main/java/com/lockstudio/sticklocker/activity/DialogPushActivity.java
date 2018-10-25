package com.lockstudio.sticklocker.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DrawableUtils;

import java.io.File;

import cn.opda.android.activity.R;

public class DialogPushActivity extends Activity {
	private Bitmap bitmap;
	private static final String TAG = "V5_PUSH_DIALOG_ACTIVITY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
		setContentView(R.layout.activity_dialog_push);
		LockApplication.getInstance().getConfig().setPush2000Url("");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setFinishOnTouchOutside(false);
		}

		String imagePath = getIntent().getStringExtra("imagePath");
		final String apkPath = getIntent().getStringExtra("apkPath");
		final String pn = getIntent().getStringExtra("pn");
		final String title = getIntent().getStringExtra("title");
		

		bitmap = BitmapFactory.decodeFile(imagePath);
		int screenWidth = LockApplication.getInstance().getConfig().getScreenWidth();
		float bitmapWidth = screenWidth - DensityUtil.dip2px(this, 80);

		float scale = bitmapWidth * 1.0f / bitmap.getWidth();
		bitmap = DrawableUtils.scaleTo(bitmap, scale, scale);

		ImageView ad_imageview = (ImageView) findViewById(R.id.ad_imageview);
		ad_imageview.setImageBitmap(bitmap);
		ad_imageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(title)) {
					CustomEventCommit.commit(DialogPushActivity.this, TAG, pn);
				} else {
					CustomEventCommit.commit(DialogPushActivity.this, TAG, title);
				}
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(Intent.ACTION_VIEW);
				File file = new File(apkPath);
				if (file.exists() && file.isAbsolute()) {
					intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
					startActivity(intent);
				}
				finish();
				overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
