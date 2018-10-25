package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.lockstudio.sticklocker.base.BaseDialog;

import cn.opda.android.activity.R;

/**
 * Created by Tommy on 15/3/17.
 */
public class UploadDialog extends BaseDialog implements View.OnClickListener {

	private OnUploadItemClickListener uploadItemClickListener = null;
	private View mView = null;
	private int position;

	public UploadDialog(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.dialog_upload_layout, null);
		linearLayout.findViewById(R.id.share_to_cloud).setOnClickListener(this);
//		linearLayout.findViewById(R.id.share_to_competition).setOnClickListener(this);
		linearLayout.findViewById(R.id.share_to_qq_friend).setOnClickListener(this);
		linearLayout.findViewById(R.id.share_to_wechat_friend).setOnClickListener(this);
		linearLayout.findViewById(R.id.share_to_wechat_moment).setOnClickListener(this);
		linearLayout.findViewById(R.id.share_to_weibo).setOnClickListener(this);
		setPadding(30);
		initViews(linearLayout);
	}

	@Override
	public void onClick(View v) {
		mView = v;
		int i = v.getId();
		if (i == R.id.share_to_wechat_friend) {
			position = 1;

		} else if (i == R.id.share_to_wechat_moment) {
			position = 2;

		} else if (i == R.id.share_to_qq_friend) {
			position = 3;

		} else if (i == R.id.share_to_weibo) {
			position = 4;

		} else if (i == R.id.share_to_cloud) {
			position = 5;

//		case R.id.share_to_competition:
//			position = 6;
//			break;
		} else {
		}

		dismiss();
	}

	/**
	 * dismiss回调
	 */
	@Override
	protected void onDismissed() {
		super.onDismissed();

		if (mView != null) {
			if (uploadItemClickListener != null) {
				uploadItemClickListener.OnUploadItemClick(position);
			}
			mView = null;
		}
	}

	public OnUploadItemClickListener getUploadItemClickListener() {
		return uploadItemClickListener;
	}

	public void setUploadItemClickListener(OnUploadItemClickListener uploadItemClickListener) {
		this.uploadItemClickListener = uploadItemClickListener;
	}

	public interface OnUploadItemClickListener {
		public void OnUploadItemClick(int id);
	}
}
