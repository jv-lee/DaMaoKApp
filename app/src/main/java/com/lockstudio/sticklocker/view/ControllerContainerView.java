package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import cn.opda.android.activity.R;

/**
 * 添加编辑view的容器布局
 * 
 * @author 庄宏岩
 * 
 */
public class ControllerContainerView extends LinearLayout {
	private View diyTabView;
	private Context mContext;
	private ControllerContainerView2 controllerContainerView2;

	public ControllerContainerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public ControllerContainerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public ControllerContainerView(Context context) {
		super(context);
		this.mContext = context;
	}

	public void setDiyTabView(View diyTabView) {
		this.diyTabView = diyTabView;
	}

	@Override
	public void addView(View child) {
		super.addView(child);
		diyTabView.setVisibility(View.GONE);
		diyTabView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.out_to_bottom));
		startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.in_from_bottom));
		if (controllerContainerView2 != null && controllerContainerView2.getChildCount() > 0) {
			controllerContainerView2.removeAllViews();
		}
	}

	@Override
	public void removeAllViews() {
		if (getChildCount() > 0) {
			diyTabView.setVisibility(View.VISIBLE);
			diyTabView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.in_from_bottom));
			Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.out_to_bottom);
			startAnimation(animation);
		}
		if (controllerContainerView2 != null && controllerContainerView2.getChildCount() > 0) {
			controllerContainerView2.removeAllViews();
		}
		super.removeAllViews();
	}

	public void setControllerContainerLayout2(ControllerContainerView2 controller_container_layout_2) {
		this.controllerContainerView2 = controller_container_layout_2;
	}

}
