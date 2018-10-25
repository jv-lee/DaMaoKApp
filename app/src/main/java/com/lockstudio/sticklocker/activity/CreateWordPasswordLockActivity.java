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
import com.lockstudio.sticklocker.model.ThemeConfig;
import com.lockstudio.sticklocker.model.WordPasswordLockerInfo;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.ImageLoader;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.ThemeUtils;
import com.lockstudio.sticklocker.view.LockContainer;
import com.lockstudio.sticklocker.view.LoveLockUtils;
import com.lockstudio.sticklocker.view.PasswordIndView;
import com.lockstudio.sticklocker.view.SimpleToast;
import com.lockstudio.sticklocker.view.WordPasswordLockView;
import com.lockstudio.sticklocker.view.WordPasswordLockView.OnLockSettingListener;
import com.lockstudio.sticklocker.view.WordPasswordLockView.State;

import java.io.File;

import cn.opda.android.activity.R;

public class CreateWordPasswordLockActivity extends BaseActivity implements OnUpdateViewListener, OnLockSettingListener,OnClickListener {
	private WordPasswordLockView mPasswordLockView;
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
		setContentView(R.layout.activity_wordpasswordlock_create_password);

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
				bg_imageview.setBackground(DrawableUtils.bitmap2Drawable(mContext,
						ImageLoader.getInstance().decodeSampledBitmapFromResource(themeConfig.getWallpaper())));
			} else {
				bg_imageview.setBackgroundDrawable(DrawableUtils.bitmap2Drawable(mContext,
						ImageLoader.getInstance().decodeSampledBitmapFromResource(themeConfig.getWallpaper())));
			}
		} else {
			bg_imageview.setBackgroundColor(themeConfig.getWallpaperColor());
		}

		WordPasswordLockerInfo wordPasswordLockerInfo = (WordPasswordLockerInfo) themeConfig.getLockerInfo();

		LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLayoutParams.leftMargin = wordPasswordLockerInfo.getX();
		mLayoutParams.topMargin = wordPasswordLockerInfo.getY();

		mPasswordLockView = (WordPasswordLockView) LayoutInflater.from(mContext).inflate(R.layout.wordpassword_locker_layout, null);
		mPasswordLockView.setPassword(true);
		mPasswordLockView.setContainerLayoutParams(mLayoutParams);
		mPasswordLockView.setOnUpdateViewListener(this);
		mPasswordLockView.setVisible(false);
		mPasswordLockView.setEditable(false);
		mPasswordLockView.setPasswordLockerInfo(wordPasswordLockerInfo);
		mPasswordLockView.setPasswordIndView(passwordIndView);
		mPasswordLockView.setTextView(cleanPass);
		mPasswordLockView.setOnLockSettingListener(this);

		lockContainer.addView(mPasswordLockView, mLayoutParams);

		LayoutParams passLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		passLayoutParams.topMargin = wordPasswordLockerInfo.getY() - 150;
		lockContainer.addView(passwordView, passLayoutParams);

		updateState(State.First, "");

		SimpleToast.makeText(mContext, R.string.setup_password_tips, SimpleToast.LENGTH_SHORT).show();
	}

	private void savePassword() {
		new LoveLockUtils(this).saveLockPass(password);
		SimpleToast.makeText(this, R.string.password_create_succsed, SimpleToast.LENGTH_SHORT).show();
		LockApplication.getInstance().getConfig().setThemeName(themePath, themeSendBroadcast);
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
			// mHeaderText.setText(R.string.lockpattern_need_to_unlock_wrong);
			updateState(State.First, "");
			mPasswordLockView.updateState(State.First);
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
		if(mPasswordLockView!=null){
			mPasswordLockView.cleanPassword();
		}
	}

}
