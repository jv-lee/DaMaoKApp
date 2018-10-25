package com.lockstudio.sticklocker.util;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import cn.opda.android.activity.R;


/** 标题栏
 */
public class BannerUtils {
	

	public static void setBannerTitle(final Activity mActivity, int resID){
		TextView banner_title = (TextView) mActivity.findViewById(R.id.banner_title);
		banner_title.setVisibility(View.GONE);

		ImageView imageView = (ImageView) mActivity.findViewById(R.id.banner_image);
		imageView.setVisibility(View.VISIBLE);
		imageView.setImageResource(resID);

		View left = mActivity.findViewById(R.id.banner_left);
		if (left != null) {
			left.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActivity.finish();
				}
			});
		}
	}

	
	
	public static void setBannerTitle_String(final Activity mActivity, String title){
		TextView banner_title = (TextView) mActivity.findViewById(R.id.banner_title);
		banner_title.setText(title);
		banner_title.setVisibility(View.VISIBLE);

		ImageView imageView = (ImageView) mActivity.findViewById(R.id.banner_image);
		imageView.setVisibility(View.GONE);

		View left = mActivity.findViewById(R.id.banner_left);
		if (left != null) {
			left.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActivity.finish();
				}
			});
		}
	}
}
