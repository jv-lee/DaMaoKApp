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
public class ShareDialog extends BaseDialog implements View.OnClickListener {

	private OnShareItemClickListener shareItemClickListener = null;
	private View mView = null;
	private int position;
	private int mark;
	private LinearLayout linearLayout ;

	public ShareDialog(Context context,int mark) {
		super(context);
		this.mark=mark;
		init();
	}

	private void init() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		if(mark==1){
			linearLayout = (LinearLayout) inflater.inflate(R.layout.dialog_share_layout, null);
			linearLayout.findViewById(R.id.share_to_qq_friend).setOnClickListener(this);
			linearLayout.findViewById(R.id.share_to_wechat_friend).setOnClickListener(this);
			linearLayout.findViewById(R.id.share_to_wechat_moment).setOnClickListener(this);
			linearLayout.findViewById(R.id.share_to_weibo).setOnClickListener(this);
		}else if(mark==2){
			linearLayout = (LinearLayout) inflater.inflate(R.layout.dialog_share2_layout, null);
			linearLayout.findViewById(R.id.share_to_qq_friend).setOnClickListener(this);
			linearLayout.findViewById(R.id.share_to_wechat_friend).setOnClickListener(this);
			linearLayout.findViewById(R.id.share_to_wechat_moment).setOnClickListener(this);
			linearLayout.findViewById(R.id.share_to_weibo).setOnClickListener(this);
		}else{
			linearLayout = (LinearLayout) inflater.inflate(R.layout.dialog_share3_layout, null);
			linearLayout.findViewById(R.id.share_to_wechat_friend).setOnClickListener(this);
			linearLayout.findViewById(R.id.share_to_wechat_moment).setOnClickListener(this);
		}

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
			if (shareItemClickListener != null) {
				shareItemClickListener.OnShareItemClick(position);
			}
			mView = null;
		}
	}

	public OnShareItemClickListener getShareItemClickListener() {
		return shareItemClickListener;
	}

	public void setShareItemClickListener(OnShareItemClickListener shareItemClickListener) {
		this.shareItemClickListener = shareItemClickListener;
	}

	public interface OnShareItemClickListener {
		public void OnShareItemClick(int id);
	}
}
