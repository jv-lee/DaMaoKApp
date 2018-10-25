package com.lockstudio.sticklocker.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.model.NinePatternLockerInfo;
import com.lockstudio.sticklocker.model.ThemeConfig;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.FileUtils;
import com.lockstudio.sticklocker.util.ImageLoader;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.ThemeUtils;
import com.lockstudio.sticklocker.view.LockContainer;
import com.lockstudio.sticklocker.view.LockPatternUtils;
import com.lockstudio.sticklocker.view.LockPatternView;
import com.lockstudio.sticklocker.view.LockPatternView.Cell;
import com.lockstudio.sticklocker.view.LockPatternView.DisplayMode;
import com.lockstudio.sticklocker.view.SimpleToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.opda.android.activity.R;

/**
 * 九宫格锁屏设置页面
 * 
 * @author 庄宏岩
 * 
 */
public class CreateGesturePasswordActivity extends BaseActivity implements OnClickListener, OnUpdateViewListener {
	private LockPatternView mLockPatternView;
	private TextView mHeaderText;
	private List<LockPatternView.Cell> mChosenPattern = null;
	private Stage mUiStage = Stage.Introduction;
	private LockContainer lockContainer;
	private String themePath;
	private boolean themeSendBroadcast;

	protected enum Stage {
		Introduction, ChoiceTooShort, FirstChoiceValid, NeedToConfirm, ConfirmWrong, ChoiceConfirmed
	}

	private CheckBox canvas_line_togglebutton;
	private ImageView canvas_line_white_imageview, canvas_line_blue_imageview, canvas_line_yellow_imageview;
	private int lineColor;
	private boolean drawLine;
	private NinePatternLockerInfo ninePatternLockerInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesturepassword_create);
		initViewAndEvent();
		updateStage(Stage.Introduction);
		SimpleToast.makeText(mContext, R.string.setup_password_tips, SimpleToast.LENGTH_SHORT).show();
	}

	@SuppressLint("NewApi")
	private void initViewAndEvent() {

		themePath = getIntent().getStringExtra("theme_path");
		themeSendBroadcast = getIntent().getBooleanExtra("theme_sendBroadcast", true);
		lockContainer = (LockContainer) findViewById(R.id.lockcontainer);
		lockContainer.setDiy(false);
		ImageView bg_imageview = (ImageView) findViewById(R.id.bg_imageview);
		View passwordView = LayoutInflater.from(mContext).inflate(R.layout.pattern_locker_textview, null);
		mHeaderText = (TextView) passwordView.findViewById(R.id.gesturepwd_unlock_textview);
		mHeaderText.setVisibility(View.VISIBLE);
		mHeaderText.setText(R.string.lockpattern_recording_intro_header);

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

		ninePatternLockerInfo = (NinePatternLockerInfo) themeConfig.getLockerInfo();

		LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLayoutParams.leftMargin = ninePatternLockerInfo.getX();
		mLayoutParams.topMargin = ninePatternLockerInfo.getY();

		mLockPatternView = (LockPatternView) LayoutInflater.from(mContext).inflate(R.layout.nine_pattern_locker_layout, null);
		mLockPatternView.setContainerLayoutParams(mLayoutParams);
		mLockPatternView.setOnUpdateViewListener(this);
		mLockPatternView.setVisible(false);
		mLockPatternView.setNinePatternLockerInfo(ninePatternLockerInfo);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(LockApplication.getInstance().getConfig().isVibrate());
		mLockPatternView.updateImage();
		lockContainer.addView(mLockPatternView, mLayoutParams);

		LayoutParams passLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		passLayoutParams.topMargin = ninePatternLockerInfo.getY() - 50;
		lockContainer.addView(passwordView, passLayoutParams);

		canvas_line_togglebutton = (CheckBox) findViewById(R.id.canvas_line_togglebutton);
		canvas_line_togglebutton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				drawLine = isChecked;
				ninePatternLockerInfo.setDrawLine(drawLine);
			}
		});
		canvas_line_white_imageview = (ImageView) findViewById(R.id.canvas_line_white_imageview);
		canvas_line_blue_imageview = (ImageView) findViewById(R.id.canvas_line_blue_imageview);
		canvas_line_yellow_imageview = (ImageView) findViewById(R.id.canvas_line_yellow_imageview);
		canvas_line_white_imageview.setOnClickListener(this);
		canvas_line_blue_imageview.setOnClickListener(this);
		canvas_line_yellow_imageview.setOnClickListener(this);
		drawLine = ninePatternLockerInfo.isDrawLine();
		if (drawLine) {
			canvas_line_togglebutton.setChecked(true);
		} else {
			canvas_line_togglebutton.setChecked(false);
		}

		lineColor = ninePatternLockerInfo.getLineColor();
		updateImageView();
	}

	public void updateImageView() {
		ninePatternLockerInfo.setLineColor(lineColor);
		canvas_line_white_imageview.setSelected(false);
		canvas_line_blue_imageview.setSelected(false);
		canvas_line_yellow_imageview.setSelected(false);
		switch (lineColor) {
		case LockPatternUtils.COLOR_BLUE:
			canvas_line_blue_imageview.setSelected(true);
			break;
		case LockPatternUtils.COLOR_WHITE:
			canvas_line_white_imageview.setSelected(true);
			break;
		case LockPatternUtils.COLOR_YELLOW:
			canvas_line_yellow_imageview.setSelected(true);
			break;

		default:
			break;
		}
	}

	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
		}
	};

	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;
			if (mUiStage == Stage.NeedToConfirm || mUiStage == Stage.ConfirmWrong) {
				if (mChosenPattern == null)
					throw new IllegalStateException("null chosen pattern in stage 'need to confirm");
				if (mChosenPattern.equals(pattern)) {
					updateStage(Stage.ChoiceConfirmed);
				} else {
					updateStage(Stage.ConfirmWrong);
				}
			} else if (mUiStage == Stage.Introduction || mUiStage == Stage.ChoiceTooShort) {
				if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
					updateStage(Stage.ChoiceTooShort);
				} else {
					mChosenPattern = new ArrayList<LockPatternView.Cell>(pattern);
					updateStage(Stage.FirstChoiceValid);
				}
			} else {
				throw new IllegalStateException("Unexpected stage " + mUiStage + " when " + "entering the pattern.");
			}
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
			mHeaderText.setText(R.string.lockpattern_recording_inprogress);
		}
	};

	private void updateStage(Stage stage) {
		mUiStage = stage;

		mLockPatternView.setDisplayMode(DisplayMode.Correct);

		switch (mUiStage) {
		case Introduction:
			mHeaderText.setText(R.string.lockpattern_recording_intro_header);
			mLockPatternView.clearPattern();
			break;
		case ChoiceTooShort:
			mHeaderText.setText(R.string.lockpattern_recording_incorrect_too_short);
			mLockPatternView.setDisplayMode(DisplayMode.Wrong);
			postClearPatternRunnable();
			break;
		case FirstChoiceValid:
			updateStage(Stage.NeedToConfirm);
			break;
		case NeedToConfirm:
			mHeaderText.setText(R.string.lockpattern_need_to_confirm);
			mLockPatternView.clearPattern();
			break;
		case ConfirmWrong:
			// mHeaderText.setText(R.string.lockpattern_need_to_unlock_wrong);
			// mLockPatternView.setDisplayMode(DisplayMode.Wrong);
			// postClearPatternRunnable();
			mChosenPattern = null;
			mLockPatternView.clearPattern();
			updateStage(Stage.Introduction);
			break;
		case ChoiceConfirmed:
			saveChosenPatternAndFinish();
			break;
		}

	}

	private void postClearPatternRunnable() {
		mLockPatternView.removeCallbacks(mClearPatternRunnable);
		mLockPatternView.postDelayed(mClearPatternRunnable, 1000);
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.canvas_line_yellow_imageview) {
			lineColor = LockPatternUtils.COLOR_YELLOW;
			updateImageView();

		} else if (i == R.id.canvas_line_blue_imageview) {
			lineColor = LockPatternUtils.COLOR_BLUE;
			updateImageView();

		} else if (i == R.id.canvas_line_white_imageview) {
			lineColor = LockPatternUtils.COLOR_WHITE;
			updateImageView();

		}
	}

	private void showToast(String string) {
		SimpleToast.makeText(mContext, string, SimpleToast.LENGTH_SHORT).show();
	}

	private void saveChosenPatternAndFinish() {
		String configJson = FileUtils.getFileString(new File(themePath + "/" + MConstants.config));
		try {
			JSONObject jsonObject = new JSONObject(configJson);
			JSONObject lockJsonObject = jsonObject.optJSONObject("lock");
			lockJsonObject.put("line_color", lineColor);
			lockJsonObject.put("line_show", drawLine);
			jsonObject.put("lock", lockJsonObject);
			FileUtils.write(new File(themePath + "/" + MConstants.config).getAbsolutePath(), jsonObject.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		new LockPatternUtils(mContext).saveLockPattern(mChosenPattern);
		showToast(getString(R.string.password_create_succsed));
		LockApplication.getInstance().getConfig().setThemeName(themePath,themeSendBroadcast);
		finish();
	}

	@Override
	public void updateView(View stickerView, LayoutParams layoutParams) {
		lockContainer.updateView(stickerView, layoutParams);
	}

}
