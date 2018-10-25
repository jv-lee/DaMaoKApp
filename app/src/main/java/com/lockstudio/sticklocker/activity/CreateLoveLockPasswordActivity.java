package com.lockstudio.sticklocker.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.model.LoveLockerInfo;
import com.lockstudio.sticklocker.model.ThemeConfig;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.ImageLoader;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.ThemeUtils;
import com.lockstudio.sticklocker.view.LockContainer;
import com.lockstudio.sticklocker.view.LoveLockUtils;
import com.lockstudio.sticklocker.view.LoveLockView;
import com.lockstudio.sticklocker.view.LoveLockView.OnLockSettingListener;
import com.lockstudio.sticklocker.view.LoveLockView.State;
import com.lockstudio.sticklocker.view.PasswordIndView;
import com.lockstudio.sticklocker.view.SimpleToast;

import java.io.File;

import cn.opda.android.activity.R;

public class CreateLoveLockPasswordActivity extends BaseActivity implements OnUpdateViewListener, OnLockSettingListener,OnClickListener {
	private LoveLockView mLovesLockView;
	private TextView mHeaderText;
	private PasswordIndView passwordIndView;
	private String password;
	private LockContainer lockContainer;
	private String themePath;
	private TextView  cleanPass;
	private boolean themeSendBroadcast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lovelock_create_password);
		
		themePath = getIntent().getStringExtra("theme_path");
		themeSendBroadcast = getIntent().getBooleanExtra("theme_sendBroadcast", true);
		lockContainer = (LockContainer) findViewById(R.id.lockcontainer);
		lockContainer.setDiy(false);
		ImageView bg_imageview = (ImageView) findViewById(R.id.bg_imageview);
		View passwordView = LayoutInflater.from(mContext).inflate(R.layout.password_layout, null);
		passwordIndView = (PasswordIndView) passwordView.findViewById(R.id.passwordIndView);
		passwordIndView.create();
		passwordIndView.setMaxPassLength(4);
		passwordIndView.setInputPassLength(0);
		mHeaderText = (TextView) passwordView.findViewById(R.id.loveslock_create_text);
		mHeaderText.setVisibility(View.VISIBLE);
		cleanPass=(TextView)passwordView.findViewById(R.id.cleanpass);
		String configPath = new File(themePath, MConstants.config).getAbsolutePath();
		ThemeConfig themeConfig = ThemeUtils.parseConfig(this, configPath);
		if (!TextUtils.isEmpty(themeConfig.getWallpaper()) && new File(themeConfig.getWallpaper()).exists()) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				bg_imageview.setBackground(DrawableUtils.bitmap2Drawable(mContext, ImageLoader.getInstance().decodeSampledBitmapFromResource(themeConfig.getWallpaper())));
			} else {
				bg_imageview.setBackgroundDrawable(DrawableUtils.bitmap2Drawable(mContext,
						ImageLoader.getInstance().decodeSampledBitmapFromResource(themeConfig.getWallpaper())));
			}
		}else{
			bg_imageview.setBackgroundColor(themeConfig.getWallpaperColor());
		}
		
		LoveLockerInfo loveLockerInfo = (LoveLockerInfo) themeConfig.getLockerInfo();

		LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLayoutParams.leftMargin = loveLockerInfo.getX();
		mLayoutParams.topMargin = loveLockerInfo.getY();

		mLovesLockView = (LoveLockView) LayoutInflater.from(mContext).inflate(R.layout.love_locker_layout, null);
		mLovesLockView.setPassword(true);
		mLovesLockView.setContainerLayoutParams(mLayoutParams);
		mLovesLockView.setOnUpdateViewListener(this);
		mLovesLockView.setVisible(false);
		mLovesLockView.setEditable(false);
		mLovesLockView.setLoveLockerInfo(loveLockerInfo);
		mLovesLockView.updateImage();
		mLovesLockView.setPasswordIndView(passwordIndView);
		mLovesLockView.setTextView(cleanPass);
		mLovesLockView.setOnLockSettingListener(this);

		lockContainer.addView(mLovesLockView, mLayoutParams);

		LayoutParams passLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		passLayoutParams.topMargin = loveLockerInfo.getY() - 150;
		lockContainer.addView(passwordView, passLayoutParams);

		updateState(State.First, "");

		SimpleToast.makeText(mContext, R.string.setup_password_tips, SimpleToast.LENGTH_SHORT).show();
	}

	private void savePassword() {
		new LoveLockUtils(this).saveLockPass(password);
		SimpleToast.makeText(this, R.string.password_create_succsed, SimpleToast.LENGTH_SHORT).show();
		LockApplication.getInstance().getConfig().setThemeName(themePath,themeSendBroadcast);
		finish();
	}

	@Override
	public void updateState(State state, String confirmPassword) {
		if (State.First == state) {
			mHeaderText.setText(R.string.lovelocker_password_setting_string);
			return;
		}
		if (State.Confirm == state) {
			mHeaderText.setText(R.string.lovelocker_need_to_confirm);
			return;
		}
		if (State.ConfirmWrong == state) {
			//mHeaderText.setText(R.string.lockpattern_need_to_unlock_wrong);
			updateState(State.First, "");
			mLovesLockView.updateState(State.First);
			return;
		}
		if (State.Done == state) {
			this.password = confirmPassword;
			savePassword();
			return;
		}
	}

	@Override
	public void updateView(View stickerView, LayoutParams layoutParams) {
		lockContainer.updateView(stickerView, layoutParams);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(mLovesLockView!=null){
			mLovesLockView.cleanPassword();
		}
	}

}
