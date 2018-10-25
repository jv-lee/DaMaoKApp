package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.lockstudio.sticklocker.activity.DiyActivity;

import cn.opda.android.activity.R;

/**
 * 添加编辑view的容器布局
 * 
 * @author 庄宏岩
 * 
 */
public class ControllerContainerView2 extends LinearLayout {
	private Context mContext;
	private ControllerContainerView controller_container_layout;
	private DiyActivity mDiyActivity;

	public ControllerContainerView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public ControllerContainerView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public ControllerContainerView2(Context context) {
		super(context);
		this.mContext = context;
	}

	public void setDiyActivity(DiyActivity diyActivity) {
		this.mDiyActivity = diyActivity;
	}

	@Override
	public void addView(View child) {
		super.addView(child);
		startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.in_from_bottom));
		if (controller_container_layout != null && controller_container_layout.getChildCount() > 0) {
			controller_container_layout.removeAllViews();
		}
	}

	@Override
	public void removeAllViews() {
		if (getChildCount() > 0) {
			Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.out_to_bottom);
			startAnimation(animation);
		}
		if (controller_container_layout != null && controller_container_layout.getChildCount() > 0) {
			controller_container_layout.removeAllViews();
		}
		if (mDiyActivity != null) {
			mDiyActivity.clearTabSelecter();
		}
		super.removeAllViews();

	}

	public void setControllerContainerLayout(ControllerContainerView controller_container_layout) {
		this.controller_container_layout = controller_container_layout;
	}

}
