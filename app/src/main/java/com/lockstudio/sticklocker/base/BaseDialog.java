package com.lockstudio.sticklocker.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import cn.opda.android.activity.R;

/**
 * Created by Tommy on 15/3/17.
 */
public class BaseDialog extends Dialog {

	protected Context mContext;
	private View mBg;
	private FrameLayout mDialogView;
	protected boolean mDismissed = true;
	private boolean mCancelableOnTouchOutside = true;

	private int screenH = 0;

	protected OnDismissedListener onDismissedListener = null;
	private boolean alignTop = false;
	private boolean gravity_center = false;
	private int padding = 40;

	/**
	 * Create a Dialog window that uses the default dialog frame style.
	 * 
	 * @param context
	 *            The Context the Dialog is to run it. In particular, it uses
	 *            the window manager and theme in this context to present its
	 *            UI.
	 */
	public BaseDialog(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);// 全屏
		this.mContext = context;

		requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
		getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN); // 隐藏状态栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().setFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS, LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		getWindow().setGravity(Gravity.CENTER);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
	}

	protected void initViews(View v) {
		initViews(v, true);
	}

	protected void initViews(View v, boolean hideKeyboard) {
		if (hideKeyboard) {
			/* 隐藏软键盘 */
			InputMethodManager imm = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				View focusView = getWindow().peekDecorView();
				if (focusView != null)
					imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
			}
		}
		getWindow().setContentView(createView(v));
	}
	public void setPadding(int padding){
		this.padding = padding;
	}

	public void setAlignTop(boolean alignTop) {
		this.alignTop = alignTop;
	}

	public void setGravityCenter(boolean gravity_center) {
		this.gravity_center = gravity_center;
	}

	/**
	 * 创建基本的背景视图
	 */
	private View createView(View v) {
		FrameLayout parent = new FrameLayout(mContext);
		FrameLayout.LayoutParams parentParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		parent.setLayoutParams(parentParams);

		mBg = new View(mContext);
		mBg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mBg.setBackgroundResource(R.color.translucent);
		mBg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCancelableOnTouchOutside) {
					dismiss();
				}
			}
		});

		LinearLayout panel = new LinearLayout(mContext);
		panel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		panel.setOrientation(LinearLayout.VERTICAL);
		panel.setGravity(gravity_center == true ? Gravity.CENTER : Gravity.CENTER_HORIZONTAL);

		mDialogView = new FrameLayout(mContext);
		FrameLayout.LayoutParams mPanelParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		mDialogView.setLayoutParams(mPanelParams);
		mDialogView.setBackgroundResource(android.R.color.transparent);
		mDialogView.setPadding(dip2px(mContext, padding), 0, dip2px(mContext, padding), 0);
		mDialogView.setClickable(true);

		if (v != null) {
			mDialogView.addView(v);

			if (!gravity_center) {
				screenH = getDeviceHeight(mContext);
				// 测量view
				v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
				int dialogH = v.getMeasuredHeight();
				int topY = 0;
				if (alignTop) {
					topY = screenH * 5 / 10 - (dialogH / 2);
				} else {
					topY = screenH * 6 / 10 - (dialogH / 2);
				}

				View top = new View(mContext);
				top.setLayoutParams(new LinearLayout.LayoutParams(1, topY));

				panel.addView(top);
			}
			panel.addView(mDialogView);
		}

		parent.addView(mBg);
		parent.addView(panel);
		return parent;
	}

	/**
	 * Start the dialog and display it on screen. The window is placed in the
	 * application layer and opaque. Note that you should not override this
	 * method to do initialization when the dialog is shown, instead implement
	 * that in {@link #onStart}.
	 */
	@Override
	public void show() {
		if (!mDismissed)
			return;

		super.show();

		if (!gravity_center) {
			AlphaAnimation fade_in = new AlphaAnimation(0, 1);
			fade_in.setDuration(500);
			fade_in.setFillBefore(true);
			fade_in.setInterpolator(new DecelerateInterpolator());
			mBg.startAnimation(fade_in);

			Animation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, screenH,
					Animation.RELATIVE_TO_PARENT, 0);
			translateAnimation.setInterpolator(new OvershootInterpolator(0.7f));
			translateAnimation.setDuration(500);
			translateAnimation.setFillBefore(true);
			mDialogView.startAnimation(translateAnimation);

		} else {
			AlphaAnimation fade_in = new AlphaAnimation(0, 1);
			fade_in.setDuration(200);
			fade_in.setFillBefore(true);
			fade_in.setInterpolator(new DecelerateInterpolator());
			mBg.startAnimation(fade_in);

			
			AnimationSet animationSet = new AnimationSet(false);
			AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
			alphaAnimation.setFillAfter(true);
			alphaAnimation.setDuration(200);
			ScaleAnimation scaleAnimation = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setDuration(200);
			scaleAnimation.setFillAfter(true);
			animationSet.addAnimation(alphaAnimation);
			animationSet.addAnimation(scaleAnimation);
			animationSet.setDuration(200);
			mDialogView.startAnimation(animationSet);
		}

		mDismissed = false;
	}

	/**
	 * Dismiss this dialog, removing it from the screen. This method can be
	 * invoked safely from any thread. Note that you should not override this
	 * method to do cleanup when the dialog is dismissed, instead implement that
	 * in {@link #onStop}.
	 */
	@Override
	public void dismiss() {
		if (mDismissed)
			return;

		mDismissed = true;
		if (!gravity_center) {

			Animation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
					Animation.ABSOLUTE, screenH);
			translateAnimation.setInterpolator(new AnticipateInterpolator(0.7f));
			translateAnimation.setDuration(500);
			translateAnimation.setFillAfter(true);
			mDialogView.startAnimation(translateAnimation);

			AlphaAnimation fade_out = new AlphaAnimation(1, 0);
			fade_out.setDuration(400);
			fade_out.setInterpolator(new AccelerateInterpolator());
			fade_out.setAnimationListener(new dismissAnimationListener());
			fade_out.setStartOffset(250);
			fade_out.setFillAfter(true);
			mBg.startAnimation(fade_out);
		} else {
			
			AnimationSet animationSet = new AnimationSet(false);
			AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
			alphaAnimation.setFillAfter(true);
			alphaAnimation.setDuration(200);
			ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setDuration(200);
			scaleAnimation.setFillAfter(true);
			animationSet.addAnimation(alphaAnimation);
			animationSet.addAnimation(scaleAnimation);
			animationSet.setDuration(200);
			mDialogView.startAnimation(animationSet);

			
			
			AlphaAnimation fade_out = new AlphaAnimation(1, 0);
			fade_out.setDuration(200);
			fade_out.setInterpolator(new AccelerateInterpolator());
			fade_out.setAnimationListener(new dismissAnimationListener());
			fade_out.setFillAfter(true);
			mBg.startAnimation(fade_out);
		}
	}

	public BaseDialog setCancelableOnTouchMenuOutside(boolean cancelable) {
		mCancelableOnTouchOutside = cancelable;
		return this;
	}

	private int getDeviceHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		boolean isRotation = false;
		int rotation = wm.getDefaultDisplay().getRotation();
		if (!(rotation == 3 || rotation == 1)) {
			isRotation = true;
		}

		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return isRotation ? dm.heightPixels : dm.widthPixels;
	}

	private int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * dismiss回调
	 */
	protected void onDismissed() {
		if (onDismissedListener != null) {
			onDismissedListener.OnDialogDismissed();
		}
	}

	protected void onShow() {

	}

	private class dismissAnimationListener implements Animation.AnimationListener {
		/**
		 * <p>
		 * Notifies the start of the animation.
		 * </p>
		 * 
		 * @param animation
		 *            The started animation.
		 */
		@Override
		public void onAnimationStart(Animation animation) {

		}

		/**
		 * <p>
		 * Notifies the repetition of the animation.
		 * </p>
		 * 
		 * @param animation
		 *            The animation which was repeated.
		 */
		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		/**
		 * <p>
		 * Notifies the end of the animation. This callback is not invoked for
		 * animations with repeat count setUrlImage to INFINITE.
		 * </p>
		 * 
		 * @param animation
		 *            The animation which reached its end.
		 */
		@Override
		public void onAnimationEnd(Animation animation) {
			BaseDialog.super.dismiss();
			onDismissed();
		}
	}

	public void setOnDismissedListener(OnDismissedListener onDismissedListener) {
		this.onDismissedListener = onDismissedListener;
	}

	public interface OnDismissedListener {
		public void OnDialogDismissed();
	}
}
