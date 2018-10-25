package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lockstudio.sticklocker.Interface.OnFocuseChangeListener;
import com.lockstudio.sticklocker.Interface.OnRemoveLockerViewListener;
import com.lockstudio.sticklocker.Interface.OnRemoveStickerViewListener;
import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.Interface.UnlockListener;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.AppLockerInfo;
import com.lockstudio.sticklocker.model.BatteryStickerInfo;
import com.lockstudio.sticklocker.model.CameraStickerInfo;
import com.lockstudio.sticklocker.model.CoupleLockerInfo;
import com.lockstudio.sticklocker.model.DayWordStickerInfo;
import com.lockstudio.sticklocker.model.FlashlightStickerInfo;
import com.lockstudio.sticklocker.model.HollowWordsStickerInfo;
import com.lockstudio.sticklocker.model.ImagePasswordLockerInfo;
import com.lockstudio.sticklocker.model.ImageStickerInfo;
import com.lockstudio.sticklocker.model.LockerInfo;
import com.lockstudio.sticklocker.model.LoveLockerInfo;
import com.lockstudio.sticklocker.model.NinePatternLockerInfo;
import com.lockstudio.sticklocker.model.NumPasswordLockerInfo;
import com.lockstudio.sticklocker.model.SlideLockerInfo;
import com.lockstudio.sticklocker.model.StatusbarStickerInfo;
import com.lockstudio.sticklocker.model.StickerInfo;
import com.lockstudio.sticklocker.model.ThemeConfig;
import com.lockstudio.sticklocker.model.TimeStickerInfo;
import com.lockstudio.sticklocker.model.TimerStickerInfo;
import com.lockstudio.sticklocker.model.TwelvePatternLockerInfo;
import com.lockstudio.sticklocker.model.WeatherStickerInfo;
import com.lockstudio.sticklocker.model.WordPasswordLockerInfo;
import com.lockstudio.sticklocker.model.WordStickerInfo;
import com.lockstudio.sticklocker.util.ChooseStickerUtils;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import cn.opda.android.activity.R;

public class LockContainer extends RelativeLayout implements OnRemoveLockerViewListener, OnRemoveStickerViewListener, OnUpdateViewListener,
		OnFocuseChangeListener {
	private Context mContext;
	private ArrayList<StickerInfo> mStickerInfos = new ArrayList<StickerInfo>();
	private ArrayList<View> mStickerViews = new ArrayList<View>();
	private LockerInfo mLockerInfo;
	private View mLockView;
	private ControllerContainerView mController_container_layout;
	private ControllerContainerView2 mController_container_layout_2;
	private UnlockListener mUnlockListener;
	private boolean diy = true;
	private boolean lockEnable = true;
	private StatusbarStickerInfo statusbarStickerInfo;
	private int wallpaperColor;
	private int[] wallpaperColorArray = { 0xff9679e3, 0xff5ac8f3, 0xfff46758, 0xffb2eb74, 0xff55dfbd };

	private Bitmap wallpaperBitmap = null;
	private boolean diyChange = false;
	private ToggleListener mToggleListener;

	public LockContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public LockContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public LockContainer(Context context) {
		super(context);
		this.mContext = context;
	}

	public void destroy() {
		if (wallpaperBitmap != null) {
			if (!wallpaperBitmap.isRecycled()) {
				wallpaperBitmap.recycle();
			}
			wallpaperBitmap = null;
		}
	}

	public void init() {
		int i = new Random().nextInt(5);
		wallpaperColor = wallpaperColorArray[i];
		setBackgroundColor(wallpaperColor);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int screenWidth = LockApplication.getInstance().getConfig().getScreenWidth();
		int screenHeight = LockApplication.getInstance().getConfig().getScreenHeight();
		setMeasuredDimension(screenWidth, screenHeight);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (diy) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if (mToggleListener != null) {
					boolean hasChild = false;
					if (mController_container_layout != null && mController_container_layout.getChildCount() > 0) {
						hasChild = true;
					} else if (mController_container_layout_2 != null && mController_container_layout_2.getChildCount() > 0) {
						hasChild = true;
					}
					if (!hasChild) {
						mToggleListener.toggleTabView();
					}
				}
				clearAllFocuse(true);
				break;
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	public void setControllerContainerLayout(ControllerContainerView controller_container_layout) {
		this.mController_container_layout = controller_container_layout;
	}

	public void setControllerContainerLayout2(ControllerContainerView2 controller_container_layout_2) {
		this.mController_container_layout_2 = controller_container_layout_2;
	}

	/**
	 * 清除所有view的操作按钮
	 */
	public void clearAllFocuse(boolean removeView) {
		if (removeView) {
			if (mController_container_layout != null) {
				mController_container_layout.removeAllViews();
			}
			if (mController_container_layout_2 != null) {
				mController_container_layout_2.removeAllViews();
			}
		}
		for (View stickerView : mStickerViews) {
			if (stickerView instanceof ImageStickerView) {
				((ImageStickerView) stickerView).setVisible(false);
				continue;
			}
			if (stickerView instanceof WordStickerView) {
				((WordStickerView) stickerView).setVisible(false);
				continue;
			}
			if (stickerView instanceof TimeStickerView) {
				((TimeStickerView) stickerView).setVisible(false);
				continue;
			}
			if (stickerView instanceof WeatherStickerView) {
				((WeatherStickerView) stickerView).setVisible(false);
				continue;
			}
			if (stickerView instanceof TimerStickerView) {
				((TimerStickerView) stickerView).setVisible(false);
				continue;
			}
			if (stickerView instanceof StatusbarStickerView) {
				((StatusbarStickerView) stickerView).setVisible(false);
				continue;
			}
			if (stickerView instanceof BatteryStickerView) {
				((BatteryStickerView) stickerView).setVisible(false);
				continue;
			}
			if (stickerView instanceof PhoneStickerView) {
				((PhoneStickerView) stickerView).setVisible(false);
				continue;
			}
			if (stickerView instanceof SMSStickerView) {
				((SMSStickerView) stickerView).setVisible(false);
				continue;
			}
			if (stickerView instanceof DayWordView) {
				((DayWordView) stickerView).setVisible(false);
				continue;
			}
			if (stickerView instanceof HollowWordsView) {
				((HollowWordsView) stickerView).setVisible(false);
				continue;
			}
			if (stickerView instanceof FlashlightStickerView) {
				((FlashlightStickerView) stickerView).setVisible(false);
				continue;
			}
			if (stickerView instanceof CameraStickerView) {
				((CameraStickerView) stickerView).setVisible(false);
				continue;
			}
		}
		if (mLockView != null) {
			if (mLockView instanceof LoveLockView) {
				((LoveLockView) mLockView).setVisible(false);
				return;
			}
			if (mLockView instanceof LockPatternView) {
				((LockPatternView) mLockView).setVisible(false);
				return;
			}
			if (mLockView instanceof LockPatternView_12) {
				((LockPatternView_12) mLockView).setVisible(false);
				return;
			}
			if (mLockView instanceof CoupleContainerView) {
				((CoupleContainerView) mLockView).setVisible(false);
				return;
			}
			if (mLockView instanceof AppContainerView) {
				((AppContainerView) mLockView).setVisible(false);
				return;
			}
			if (mLockView instanceof SlideContainerView) {
				((SlideContainerView) mLockView).setVisible(false);
				return;
			}
			if (mLockView instanceof ImagePasswordLockView) {
				((ImagePasswordLockView) mLockView).setVisible(false);
				return;
			}
			if (mLockView instanceof WordPasswordLockView) {
				WordPasswordLockView wordPasswordLockView=(WordPasswordLockView) mLockView;
				if(wordPasswordLockView.getVisible() && !wordPasswordLockView.getShowEdit()){
				    wordPasswordLockView.showControllerView();
				}else{
					((WordPasswordLockView) mLockView).setVisible(false);
				}
				return;
			}
			if (mLockView instanceof NumPasswordLockView) {
				((NumPasswordLockView) mLockView).setVisible(false);
				return;
			}
		}
	}

	/**
	 * 只显示当前操作view的操作项,其他view的操作项隐藏
	 * 
	 * @param stickerView
	 */
	private void clearOtherFocuse(View stickerView) {
		if (mController_container_layout != null) {
			mController_container_layout.removeAllViews();
		}
		int index = mStickerViews.indexOf(stickerView);
		for (int i = 0; i < mStickerViews.size(); i++) {
			View view = mStickerViews.get(i);
			if (index != i) {
				if (view instanceof ImageStickerView) {
					((ImageStickerView) view).setVisible(false);
					continue;
				}
				if (view instanceof WordStickerView) {
					((WordStickerView) view).setVisible(false);
					continue;
				}
				if (view instanceof TimeStickerView) {
					((TimeStickerView) view).setVisible(false);
					continue;
				}
				if (view instanceof WeatherStickerView) {
					((WeatherStickerView) view).setVisible(false);
					continue;
				}
				if (view instanceof TimerStickerView) {
					((TimerStickerView) view).setVisible(false);
					continue;
				}
				if (view instanceof StatusbarStickerView) {
					((StatusbarStickerView) view).setVisible(false);
					continue;
				}
				if (view instanceof BatteryStickerView) {
					((BatteryStickerView) view).setVisible(false);
					continue;
				}
				if (view instanceof PhoneStickerView) {
					((PhoneStickerView) view).setVisible(false);
					continue;
				}
				if (view instanceof SMSStickerView) {
					((SMSStickerView) view).setVisible(false);
					continue;
				}
				if (view instanceof DayWordView) {
					((DayWordView) view).setVisible(false);
					continue;
				}
				if (view instanceof HollowWordsView) {
					((HollowWordsView) view).setVisible(false);
					continue;
				}
				if (view instanceof FlashlightStickerView) {
					((FlashlightStickerView) view).setVisible(false);
					continue;
				}
				if (view instanceof CameraStickerView) {
					((CameraStickerView) view).setVisible(false);
					continue;
				}
			} else {
				if (stickerView instanceof ImageStickerView) {
					((ImageStickerView) stickerView).setVisible(true);
					continue;
				}
				if (stickerView instanceof WordStickerView) {
					((WordStickerView) stickerView).setVisible(true);
					continue;
				}
				if (stickerView instanceof TimeStickerView) {
					((TimeStickerView) stickerView).setVisible(true);
					continue;
				}
				if (stickerView instanceof WeatherStickerView) {
					((WeatherStickerView) stickerView).setVisible(true);
					continue;
				}
				if (stickerView instanceof TimerStickerView) {
					((TimerStickerView) stickerView).setVisible(true);
					continue;
				}
				if (stickerView instanceof StatusbarStickerView) {
					((StatusbarStickerView) stickerView).setVisible(true);
					continue;
				}
				if (stickerView instanceof BatteryStickerView) {
					((BatteryStickerView) stickerView).setVisible(true);
					continue;
				}
				if (stickerView instanceof PhoneStickerView) {
					((PhoneStickerView) stickerView).setVisible(true);
					continue;
				}
				if (stickerView instanceof SMSStickerView) {
					((SMSStickerView) stickerView).setVisible(true);
					continue;
				}
				if (stickerView instanceof DayWordView) {
					((DayWordView) stickerView).setVisible(true);
					continue;
				}
				if (stickerView instanceof HollowWordsView) {
					((HollowWordsView) stickerView).setVisible(true);
					continue;
				}
				if (stickerView instanceof FlashlightStickerView) {
					((FlashlightStickerView) stickerView).setVisible(true);
					continue;
				}
				if (stickerView instanceof CameraStickerView) {
					((CameraStickerView) stickerView).setVisible(true);
					continue;
				}

			}
		}

		if (mLockView != null) {
			if (index < 0) {
				if (mLockView instanceof LoveLockView) {
					((LoveLockView) mLockView).setVisible(true);
					return;
				}
				if (mLockView instanceof LockPatternView) {
					((LockPatternView) mLockView).setVisible(true);
					return;
				}
				if (mLockView instanceof LockPatternView_12) {
					((LockPatternView_12) mLockView).setVisible(true);
					return;
				}
				if (mLockView instanceof CoupleContainerView) {
					((CoupleContainerView) mLockView).setVisible(true);
					return;
				}
				if (mLockView instanceof AppContainerView) {
					((AppContainerView) mLockView).setVisible(true);
					return;
				}
				if (mLockView instanceof SlideContainerView) {
					((SlideContainerView) mLockView).setVisible(true);
					return;
				}
				if (mLockView instanceof ImagePasswordLockView) {
					((ImagePasswordLockView) mLockView).setVisible(true);
					return;
				}
				if (mLockView instanceof WordPasswordLockView) {
					((WordPasswordLockView) mLockView).setVisible(true);
					return;
				}
				if (mLockView instanceof NumPasswordLockView) {
					((NumPasswordLockView) mLockView).setVisible(true);
					return;
				}
			} else {
				if (mLockView instanceof LoveLockView) {
					((LoveLockView) mLockView).setVisible(false);
					return;
				}
				if (mLockView instanceof LockPatternView) {
					((LockPatternView) mLockView).setVisible(false);
					return;
				}
				if (mLockView instanceof LockPatternView_12) {
					((LockPatternView_12) mLockView).setVisible(false);
					return;
				}
				if (mLockView instanceof CoupleContainerView) {
					((CoupleContainerView) mLockView).setVisible(false);
					return;
				}
				if (mLockView instanceof AppContainerView) {
					((AppContainerView) mLockView).setVisible(false);
					return;
				}
				if (mLockView instanceof SlideContainerView) {
					((SlideContainerView) mLockView).setVisible(false);
					return;
				}
				if (mLockView instanceof ImagePasswordLockView) {
					((ImagePasswordLockView) mLockView).setVisible(false);
					return;
				}
				if (mLockView instanceof WordPasswordLockView) {
					((WordPasswordLockView) mLockView).setVisible(false);
					return;
				}
				if (mLockView instanceof NumPasswordLockView) {
					((NumPasswordLockView) mLockView).setVisible(false);
					return;
				}
			}
		}

	}

	public void addTheme(ThemeConfig themeConfig) {
		if (!TextUtils.isEmpty(themeConfig.getWallpaper()) && new File(themeConfig.getWallpaper()).exists()) {
			wallpaperBitmap = ImageLoader.getInstance().decodeSampledBitmapFromResource(themeConfig.getWallpaper());
			Drawable drawable = DrawableUtils.bitmap2Drawable(mContext, wallpaperBitmap);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				setBackground(drawable);
			} else {
				setBackgroundDrawable(drawable);
			}
		} else {
			wallpaperColor = themeConfig.getWallpaperColor();
			setBackgroundColor(wallpaperColor);
		}
		ArrayList<StickerInfo> stickerInfos = themeConfig.getStickerInfoList();
		ArrayList<StickerInfo> stickerInfosClick = new ArrayList<StickerInfo>();
		if (stickerInfos != null && stickerInfos.size() > 0) {
			for (StickerInfo stickerInfo : stickerInfos) {
				if(stickerInfo.isClick){
					stickerInfosClick.add(stickerInfo);
				}else{
					addSticker(stickerInfo, false);
				}
			}
		}
		LockerInfo lockerInfo = themeConfig.getLockerInfo();
		addLocker(lockerInfo, false);
		
		if (stickerInfosClick != null && stickerInfosClick.size() > 0) {
			for (StickerInfo stickerInfo : stickerInfosClick) {
					addSticker(stickerInfo, false);
			}
		}
	}

	/**
	 * 添加贴纸
	 * 
	 */
	public void addSticker(StickerInfo stickerInfo, boolean isVisable) {

		if (isVisable) {
			diyChange = true;
		}
		clearAllFocuse(true);
		if (stickerInfo instanceof ImageStickerInfo) {
			ImageStickerInfo imageStickerInfo = (ImageStickerInfo) stickerInfo;
			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = imageStickerInfo.x;
			mLayoutParams.topMargin = imageStickerInfo.y;
			mLayoutParams.width = imageStickerInfo.width;
			mLayoutParams.height = imageStickerInfo.height;

			ImageStickerView stickerView = new ImageStickerView(mContext);
			stickerView.setContainerLayoutParams(mLayoutParams);
			stickerView.setOnUpdateViewListener(LockContainer.this);
			stickerView.setOnRemoveViewListener(LockContainer.this);
			stickerView.setOnFocuseChangeListener(LockContainer.this);
			stickerView.setControllerContainerLayout(mController_container_layout);
			stickerView.setVisible(isVisable);
			stickerView.setEditable(diy);
			stickerView.setImageStickerInfo(imageStickerInfo);
			addView(stickerView, mLayoutParams);

			mStickerInfos.add(imageStickerInfo);
			mStickerViews.add(stickerView);
			return;
		}

		if (stickerInfo instanceof WordStickerInfo) {

			final WordStickerInfo wordStickerInfo = (WordStickerInfo) stickerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = wordStickerInfo.x;
			mLayoutParams.topMargin = wordStickerInfo.y;

			WordStickerView stickerView = new WordStickerView(mContext);
			stickerView.setContainerLayoutParams(mLayoutParams);
			stickerView.setOnUpdateViewListener(LockContainer.this);
			stickerView.setOnRemoveViewListener(LockContainer.this);
			stickerView.setOnFocuseChangeListener(LockContainer.this);
			stickerView.setControllerContainerLayout(mController_container_layout);
			stickerView.setVisible(isVisable);
			stickerView.setEditable(diy);
			stickerView.setWordStickerInfo(wordStickerInfo);
			addView(stickerView, mLayoutParams);

			mStickerInfos.add(wordStickerInfo);
			mStickerViews.add(stickerView);
			return;
		}

		if (stickerInfo instanceof TimeStickerInfo) {

			final TimeStickerInfo timeStickerInfo = (TimeStickerInfo) stickerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = timeStickerInfo.x;
			mLayoutParams.topMargin = timeStickerInfo.y;

			TimeStickerView stickerView = new TimeStickerView(mContext);
			stickerView.setContainerLayoutParams(mLayoutParams);
			stickerView.setOnUpdateViewListener(LockContainer.this);
			stickerView.setOnRemoveViewListener(LockContainer.this);
			stickerView.setOnFocuseChangeListener(LockContainer.this);
			stickerView.setControllerContainerLayout(mController_container_layout);
			stickerView.setVisible(isVisable);
			stickerView.setEditable(diy);
			stickerView.setTimeStickerInfo(timeStickerInfo);
			addView(stickerView, mLayoutParams);

			mStickerInfos.add(timeStickerInfo);
			mStickerViews.add(stickerView);
			return;
		}
		if (stickerInfo instanceof TimerStickerInfo) {

			final TimerStickerInfo timerStickerInfo = (TimerStickerInfo) stickerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = timerStickerInfo.x;
			mLayoutParams.topMargin = timerStickerInfo.y;

			TimerStickerView stickerView = new TimerStickerView(mContext);
			stickerView.setContainerLayoutParams(mLayoutParams);
			stickerView.setOnUpdateViewListener(LockContainer.this);
			stickerView.setOnRemoveViewListener(LockContainer.this);
			stickerView.setOnFocuseChangeListener(LockContainer.this);
			stickerView.setControllerContainerLayout(mController_container_layout);
			stickerView.setVisible(isVisable);
			stickerView.setEditable(diy);
			stickerView.setTimerStickerInfo(timerStickerInfo);
			addView(stickerView, mLayoutParams);

			mStickerInfos.add(timerStickerInfo);
			mStickerViews.add(stickerView);
			return;
		}
		if (stickerInfo instanceof WeatherStickerInfo) {

			final WeatherStickerInfo weatherStickerInfo = (WeatherStickerInfo) stickerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = weatherStickerInfo.x;
			mLayoutParams.topMargin = weatherStickerInfo.y;

			WeatherStickerView stickerView = new WeatherStickerView(mContext);
			stickerView.setContainerLayoutParams(mLayoutParams);
			stickerView.setOnUpdateViewListener(LockContainer.this);
			stickerView.setOnRemoveViewListener(LockContainer.this);
			stickerView.setOnFocuseChangeListener(LockContainer.this);
			stickerView.setControllerContainerLayout(mController_container_layout);
			stickerView.setVisible(isVisable);
			stickerView.setEditable(diy);
			stickerView.setWeatherStickerInfo(weatherStickerInfo);
			addView(stickerView, mLayoutParams);

			mStickerInfos.add(weatherStickerInfo);
			mStickerViews.add(stickerView);
			return;
		}
		if (stickerInfo instanceof StatusbarStickerInfo) {
			if (statusbarStickerInfo == null) {
				final StatusbarStickerInfo statusbarStickerInfo = (StatusbarStickerInfo) stickerInfo;
				this.statusbarStickerInfo = statusbarStickerInfo;
				LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				mLayoutParams.leftMargin = statusbarStickerInfo.x;
				mLayoutParams.topMargin = statusbarStickerInfo.y;

				StatusbarStickerView stickerView = new StatusbarStickerView(mContext);
				stickerView.setContainerLayoutParams(mLayoutParams);
				stickerView.setOnUpdateViewListener(LockContainer.this);
				stickerView.setOnRemoveViewListener(LockContainer.this);
				stickerView.setOnFocuseChangeListener(LockContainer.this);
				stickerView.setControllerContainerLayout(mController_container_layout);
				stickerView.setVisible(isVisable);
				stickerView.setEditable(diy);
				stickerView.setStatusbarStickerInfo(statusbarStickerInfo);

				if (diy || !LockApplication.getInstance().getConfig().isShowStatusBar()) {
					addView(stickerView, mLayoutParams);

					mStickerInfos.add(statusbarStickerInfo);
					mStickerViews.add(stickerView);
				}
			}
			return;
		}

		if (stickerInfo instanceof BatteryStickerInfo) {

			final BatteryStickerInfo batteryStickerInfo = (BatteryStickerInfo) stickerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = batteryStickerInfo.x;
			mLayoutParams.topMargin = batteryStickerInfo.y;

			BatteryStickerView stickerView = new BatteryStickerView(mContext);
			stickerView.setContainerLayoutParams(mLayoutParams);
			stickerView.setOnUpdateViewListener(LockContainer.this);
			stickerView.setOnRemoveViewListener(LockContainer.this);
			stickerView.setOnFocuseChangeListener(LockContainer.this);
			stickerView.setControllerContainerLayout(mController_container_layout);
			stickerView.setVisible(isVisable);
			stickerView.setEditable(diy);
			stickerView.setBatteryStickerInfo(batteryStickerInfo);
			addView(stickerView, mLayoutParams);

			mStickerInfos.add(batteryStickerInfo);
			mStickerViews.add(stickerView);
			return;
		}
		
		if (stickerInfo instanceof FlashlightStickerInfo) {

			final FlashlightStickerInfo flashlightStickerInfo = (FlashlightStickerInfo) stickerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = flashlightStickerInfo.x;
			mLayoutParams.topMargin = flashlightStickerInfo.y;

			FlashlightStickerView stickerView = new FlashlightStickerView(mContext);
			stickerView.setContainerLayoutParams(mLayoutParams);
			stickerView.setOnUpdateViewListener(LockContainer.this);
			stickerView.setOnRemoveViewListener(LockContainer.this);
			stickerView.setOnFocuseChangeListener(LockContainer.this);
			stickerView.setControllerContainerLayout(mController_container_layout);
			stickerView.setVisible(isVisable);
			stickerView.setEditable(diy);
			stickerView.setFlashlightStickerInfo(flashlightStickerInfo);
			addView(stickerView, mLayoutParams);

			mStickerInfos.add(flashlightStickerInfo);
			mStickerViews.add(stickerView);
			return;
		}
		
		if (stickerInfo instanceof CameraStickerInfo) {

			final CameraStickerInfo cameraStickerInfo = (CameraStickerInfo) stickerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = cameraStickerInfo.x;
			mLayoutParams.topMargin = cameraStickerInfo.y;

			CameraStickerView stickerView = new CameraStickerView(mContext);
			stickerView.setContainerLayoutParams(mLayoutParams);
			stickerView.setOnUpdateViewListener(LockContainer.this);
			stickerView.setOnRemoveViewListener(LockContainer.this);
			stickerView.setOnFocuseChangeListener(LockContainer.this);
			stickerView.setControllerContainerLayout(mController_container_layout);
			stickerView.setVisible(isVisable);
			stickerView.setUnlockListener(mUnlockListener);
			stickerView.setEditable(diy);
			stickerView.setCameraStickerInfo(cameraStickerInfo);
			addView(stickerView, mLayoutParams);

			mStickerInfos.add(cameraStickerInfo);
			mStickerViews.add(stickerView);
			return;
		}
		/**
		 * 
		 
		if (stickerInfo instanceof PhoneStickerInfo) {

			final PhoneStickerInfo phoneStickerInfo = (PhoneStickerInfo) stickerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = phoneStickerInfo.x;
			mLayoutParams.topMargin = phoneStickerInfo.y;

			PhoneStickerView stickerView = new PhoneStickerView(mContext);
			stickerView.setContainerLayoutParams(mLayoutParams);
			stickerView.setOnUpdateViewListener(LockContainer.this);
			stickerView.setOnRemoveViewListener(LockContainer.this);
			stickerView.setOnFocuseChangeListener(LockContainer.this);
			stickerView.setControllerContainerLayout(mController_container_layout);
			stickerView.setVisible(isVisable);
			stickerView.setEditable(diy);
			stickerView.setPhoneStickerInfo(phoneStickerInfo);
			addView(stickerView, mLayoutParams);

			mStickerInfos.add(phoneStickerInfo);
			mStickerViews.add(stickerView);
			return;
		}
		if (stickerInfo instanceof SMSStickerInfo) {

			final SMSStickerInfo smsStickerInfo = (SMSStickerInfo) stickerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = smsStickerInfo.x;
			mLayoutParams.topMargin = smsStickerInfo.y;

			SMSStickerView stickerView = new SMSStickerView(mContext);
			stickerView.setContainerLayoutParams(mLayoutParams);
			stickerView.setOnUpdateViewListener(LockContainer.this);
			stickerView.setOnRemoveViewListener(LockContainer.this);
			stickerView.setOnFocuseChangeListener(LockContainer.this);
			stickerView.setControllerContainerLayout(mController_container_layout);
			stickerView.setVisible(isVisable);
			stickerView.setEditable(diy);
			stickerView.setSMSStickerInfo(smsStickerInfo);
			addView(stickerView, mLayoutParams);

			mStickerInfos.add(smsStickerInfo);
			mStickerViews.add(stickerView);
			return;
		}
		*/
		if (stickerInfo instanceof DayWordStickerInfo) {

			final DayWordStickerInfo dayWordStickerInfo = (DayWordStickerInfo) stickerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = dayWordStickerInfo.x;
			mLayoutParams.topMargin = dayWordStickerInfo.y;

			DayWordView dayWordView = new DayWordView(mContext);
			dayWordView.setContainerLayoutParams(mLayoutParams);
			dayWordView.setOnUpdateViewListener(LockContainer.this);
			dayWordView.setOnRemoveViewListener(LockContainer.this);
			dayWordView.setOnFocuseChangeListener(LockContainer.this);
			dayWordView.setControllerContainerLayout(mController_container_layout);
			dayWordView.setVisible(isVisable);
			dayWordView.setEditable(diy);
			dayWordView.setDayWordStickerInfo(dayWordStickerInfo);
			addView(dayWordView, mLayoutParams);

			mStickerInfos.add(dayWordStickerInfo);
			mStickerViews.add(dayWordView);
			return;
		}

		if (stickerInfo instanceof HollowWordsStickerInfo) {

			final HollowWordsStickerInfo hollowWordsStickerInfo = (HollowWordsStickerInfo) stickerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = hollowWordsStickerInfo.x;
			mLayoutParams.topMargin = hollowWordsStickerInfo.y;

			HollowWordsView hollowWordsView = new HollowWordsView(mContext);
			hollowWordsView.setContainerLayoutParams(mLayoutParams);
			hollowWordsView.setOnUpdateViewListener(LockContainer.this);
			hollowWordsView.setOnRemoveViewListener(LockContainer.this);
			hollowWordsView.setOnFocuseChangeListener(LockContainer.this);
			hollowWordsView.setControllerContainerLayout(mController_container_layout);
			hollowWordsView.setVisible(isVisable);
			hollowWordsView.setEditable(diy);
			hollowWordsView.setHollowWordsStickerInfo(hollowWordsStickerInfo);
			addView(hollowWordsView, mLayoutParams);

			mStickerInfos.add(hollowWordsStickerInfo);
			mStickerViews.add(hollowWordsView);
			return;
		}
	}

	/**
	 * 添加锁
	 */
	public void addLocker(LockerInfo lockerInfo, boolean isVisable) {
		if (isVisable) {
			diyChange = true;
		}
		mLockerInfo = lockerInfo;
		if (mLockView != null) {
			removeView(mLockView);
		}
		if (mLockerInfo instanceof LoveLockerInfo) {
			LoveLockerInfo loveLockerInfo = (LoveLockerInfo) mLockerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = loveLockerInfo.getX();
			mLayoutParams.topMargin = loveLockerInfo.getY();

			LoveLockView loveLockView = (LoveLockView) LayoutInflater.from(mContext).inflate(R.layout.love_locker_layout, null);
			loveLockView.setImage(diy);
			loveLockView.setContainerLayoutParams(mLayoutParams);
			loveLockView.setOnUpdateViewListener(LockContainer.this);
			loveLockView.setOnRemoveViewListener(LockContainer.this);
			loveLockView.setOnFocuseChangeListener(LockContainer.this);
			loveLockView.setControllerContainerLayout(mController_container_layout);
			loveLockView.setVisible(isVisable);
			loveLockView.setInputEnabel(lockEnable);
			loveLockView.setEditable(diy);
			loveLockView.setLoveLockerInfo(loveLockerInfo);
			loveLockView.updateImage();

			if (!diy) {
				View passwordView = LayoutInflater.from(mContext).inflate(R.layout.password_layout, null);
				PasswordIndView passwordIndView = (PasswordIndView) passwordView.findViewById(R.id.passwordIndView);
				TextView textView = (TextView) passwordView.findViewById(R.id.cleanpass);
				passwordIndView.create();
				passwordIndView.setMaxPassLength(4);
				passwordIndView.setInputPassLength(0);
				TextView mHeaderText = (TextView) passwordView.findViewById(R.id.loveslock_create_text);
				mHeaderText.setText(R.string.lovelocker_input_pass);

				loveLockView.setPasswordIndView(passwordIndView);
				loveLockView.setTextView(textView);
				loveLockView.setUnlockListener(mUnlockListener);
				LayoutParams passLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				passLayoutParams.topMargin = loveLockerInfo.getY() - 150;
				addView(passwordView, passLayoutParams);
			}

			addView(loveLockView, mLayoutParams);

			mLockView = loveLockView;
			return;
		}

		if (mLockerInfo instanceof NinePatternLockerInfo) {
			NinePatternLockerInfo ninePatternLockerInfo = (NinePatternLockerInfo) mLockerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = ninePatternLockerInfo.getX();
			mLayoutParams.topMargin = ninePatternLockerInfo.getY();

			LockPatternView lockPatternView = (LockPatternView) LayoutInflater.from(mContext).inflate(R.layout.nine_pattern_locker_layout, null);
			lockPatternView.setTactileFeedbackEnabled(LockApplication.getInstance().getConfig().isVibrate());
			lockPatternView.setImage(diy);
			lockPatternView.setContainerLayoutParams(mLayoutParams);
			lockPatternView.setOnUpdateViewListener(LockContainer.this);
			lockPatternView.setOnRemoveViewListener(LockContainer.this);
			lockPatternView.setOnFocuseChangeListener(LockContainer.this);
			lockPatternView.setControllerContainerLayout(mController_container_layout);
			lockPatternView.setVisible(isVisable);
			if (!lockEnable) {
				lockPatternView.disableInput();
			}
			lockPatternView.setNinePatternLockerInfo(ninePatternLockerInfo);
			lockPatternView.updateImage();

			if (!diy) {
				View passwordView = LayoutInflater.from(mContext).inflate(R.layout.pattern_locker_textview, null);
				TextView gesturepwd_unlock_textview = (TextView) passwordView.findViewById(R.id.gesturepwd_unlock_textview);
				lockPatternView.setHeadTextView(gesturepwd_unlock_textview);
				lockPatternView.setUnlockListener(mUnlockListener);
				LayoutParams passLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				passLayoutParams.topMargin = ninePatternLockerInfo.getY() - 50;
				addView(passwordView, passLayoutParams);
			}

			addView(lockPatternView, mLayoutParams);
			mLockView = lockPatternView;

			return;
		}

		if (mLockerInfo instanceof TwelvePatternLockerInfo) {
			TwelvePatternLockerInfo twelvePatternLockerInfo = (TwelvePatternLockerInfo) mLockerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = twelvePatternLockerInfo.getX();
			mLayoutParams.topMargin = twelvePatternLockerInfo.getY();

			LockPatternView_12 lockPatternView_12 = (LockPatternView_12) LayoutInflater.from(mContext).inflate(R.layout.twelve_pattern_locker_layout, null);
			lockPatternView_12.setTactileFeedbackEnabled(LockApplication.getInstance().getConfig().isVibrate());
			lockPatternView_12.setImage(diy);
			lockPatternView_12.setContainerLayoutParams(mLayoutParams);
			lockPatternView_12.setOnUpdateViewListener(LockContainer.this);
			lockPatternView_12.setOnRemoveViewListener(LockContainer.this);
			lockPatternView_12.setOnFocuseChangeListener(LockContainer.this);
			lockPatternView_12.setControllerContainerLayout(mController_container_layout);
			lockPatternView_12.setVisible(isVisable);
			if (!lockEnable) {
				lockPatternView_12.disableInput();
			}
			lockPatternView_12.setTwelvePatternLockerInfo(twelvePatternLockerInfo);
			if (diy) {
				lockPatternView_12.updateTempImage();
			} else {
				lockPatternView_12.updateImage();
			}

			if (!diy) {
				View passwordView = LayoutInflater.from(mContext).inflate(R.layout.pattern_locker_textview, null);
				TextView gesturepwd_unlock_textview = (TextView) passwordView.findViewById(R.id.gesturepwd_unlock_textview);
				lockPatternView_12.setHeadTextView(gesturepwd_unlock_textview);
				lockPatternView_12.setUnlockListener(mUnlockListener);
				LayoutParams passLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				passLayoutParams.topMargin = twelvePatternLockerInfo.getY() - 50;
				addView(passwordView, passLayoutParams);
			}

			addView(lockPatternView_12, mLayoutParams);
			mLockView = lockPatternView_12;

			return;
		}

		if (mLockerInfo instanceof CoupleLockerInfo) {
			CoupleLockerInfo coupleLockerInfo = (CoupleLockerInfo) mLockerInfo;
			if (diy) {
				LayoutParams mLayoutParams = new LayoutParams(LockApplication.getInstance().getConfig().getScreenWidth() - DensityUtil.dip2px(mContext, 20),
						DensityUtil.dip2px(mContext, 90));
				mLayoutParams.leftMargin = DensityUtil.dip2px(mContext, 15);
				mLayoutParams.topMargin = coupleLockerInfo.getY();

				CoupleContainerView coupleContainerView = (CoupleContainerView) LayoutInflater.from(mContext).inflate(R.layout.couple_setting_lock_view, null);
				coupleContainerView.setContainerLayoutParams(mLayoutParams);
				coupleContainerView.setOnUpdateViewListener(LockContainer.this);
				coupleContainerView.setOnRemoveViewListener(LockContainer.this);
				coupleContainerView.setOnFocuseChangeListener(LockContainer.this);
				coupleContainerView.setImage(true);
				coupleContainerView.setControllerContainerLayout(mController_container_layout);
				coupleContainerView.setVisible(isVisable);
				coupleContainerView.setCoupleLockerInfo(coupleLockerInfo);
				addView(coupleContainerView, mLayoutParams);
				mLockView = coupleContainerView;
			} else {
				LayoutParams mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				mLayoutParams.topMargin = coupleLockerInfo.getY();
				mLayoutParams.leftMargin = DensityUtil.dip2px(mContext, 10);
				mLayoutParams.rightMargin = DensityUtil.dip2px(mContext, 10);
				CoupleLockView coupleLockView = (CoupleLockView) LayoutInflater.from(mContext).inflate(R.layout.couple_lock_view, null);
				coupleLockView.setUnlockListener(mUnlockListener);
				coupleLockView.setCoupleLockerInfo(coupleLockerInfo);
				addView(coupleLockView, mLayoutParams);
				if (!lockEnable) {
					coupleLockView.disableInput();
				}
			}

			return;
		}
		
		if (mLockerInfo instanceof AppLockerInfo) {
			AppLockerInfo appLockerInfo = (AppLockerInfo) mLockerInfo;
			if (diy) {
				LayoutParams mLayoutParams = new LayoutParams(LockApplication.getInstance().getConfig().getScreenWidth() - DensityUtil.dip2px(mContext, 20),
						LockApplication.getInstance().getConfig().getScreenHeight()/3*2-DensityUtil.dip2px(mContext, 50));
				mLayoutParams.leftMargin = DensityUtil.dip2px(mContext, 15);
				mLayoutParams.topMargin = appLockerInfo.getY();

//				AppContainerView appContainerView = new AppContainerView(mContext,appLockerInfo.getWidth(),appLockerInfo.getHeight(),appLockerInfo.getWidth()/3);
				AppContainerView appContainerView = (AppContainerView) LayoutInflater.from(mContext).inflate(R.layout.app_setting_lock_view, null);
				appContainerView.setContainerLayoutParams(mLayoutParams);
				appContainerView.setOnUpdateViewListener(LockContainer.this);
				appContainerView.setOnRemoveViewListener(LockContainer.this);
				appContainerView.setOnFocuseChangeListener(LockContainer.this);
//				appContainerView.setImage(true);
				appContainerView.setControllerContainerLayout(mController_container_layout);
				appContainerView.setVisible(isVisable);
				appContainerView.setAppLockerInfo(appLockerInfo);
				addView(appContainerView,mLayoutParams);
				mLockView = appContainerView;
			} else {
				LayoutParams mLayoutParams = new LayoutParams(LockApplication.getInstance().getConfig().getScreenWidth() - DensityUtil.dip2px(mContext, 5),
						LockApplication.getInstance().getConfig().getScreenHeight()/3*2-DensityUtil.dip2px(mContext, 50));
				mLayoutParams.leftMargin = DensityUtil.dip2px(mContext, 5);
				mLayoutParams.topMargin = appLockerInfo.getY()+DensityUtil.dip2px(mContext, 55);
				AppLockView appLockView = (AppLockView) LayoutInflater.from(mContext).inflate(R.layout.app_lock_view, null);
				appLockView.setUnlockListener(mUnlockListener);
				appLockView.setAppLockerInfo(appLockerInfo);
				addView(appLockView,mLayoutParams);
//				if (!lockEnable) {
//					coupleLockView.disableInput();
//				}
			}

			return;
		}

		if (mLockerInfo instanceof SlideLockerInfo) {
			SlideLockerInfo slideLockerInfo = (SlideLockerInfo) mLockerInfo;
			if (diy) {
				LayoutParams mLayoutParams = new LayoutParams(LockApplication.getInstance().getConfig().getScreenWidth(), LockApplication.getInstance()
						.getConfig().getScreenWidth());
				mLayoutParams.topMargin = slideLockerInfo.getY();
				SlideContainerView slideContainerView = (SlideContainerView) LayoutInflater.from(mContext).inflate(R.layout.slide_setting_lock_view, null);
				slideContainerView.setOnUpdateViewListener(LockContainer.this);
				slideContainerView.setContainerLayoutParams(mLayoutParams);
				slideContainerView.setOnRemoveViewListener(LockContainer.this);
				slideContainerView.setOnFocuseChangeListener(LockContainer.this);
				slideContainerView.setImage(true);
				slideContainerView.setControllerContainerLayout(mController_container_layout);
				slideContainerView.setVisible(isVisable);
				slideContainerView.setSlideLockerInfo(slideLockerInfo);
				addView(slideContainerView, mLayoutParams);
				mLockView = slideContainerView;
			} else {
				LayoutParams mLayoutParams = new LayoutParams(LockApplication.getInstance().getConfig().getScreenWidth(), LockApplication.getInstance()
						.getConfig().getScreenWidth());
				mLayoutParams.topMargin = slideLockerInfo.getY();
				SlideLockView slideLockView = (SlideLockView) LayoutInflater.from(mContext).inflate(R.layout.slide_lock_view, null);
				slideLockView.setUnlockListener(mUnlockListener);
				slideLockView.setSlideLockerInfo(slideLockerInfo);
				addView(slideLockView, mLayoutParams);
				if (!lockEnable) {
					slideLockView.disableInput();
				}
			}

			return;
		}

		if (mLockerInfo instanceof ImagePasswordLockerInfo) {
			ImagePasswordLockerInfo passwordLockerInfo = (ImagePasswordLockerInfo) mLockerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = passwordLockerInfo.getX();
			mLayoutParams.topMargin = passwordLockerInfo.getY();

			ImagePasswordLockView passwordLockView = (ImagePasswordLockView) LayoutInflater.from(mContext).inflate(R.layout.imagepassword_locker_layout, null);
			passwordLockView.setImage(diy);
			passwordLockView.setContainerLayoutParams(mLayoutParams);
			passwordLockView.setOnUpdateViewListener(LockContainer.this);
			passwordLockView.setOnRemoveViewListener(LockContainer.this);
			passwordLockView.setOnFocuseChangeListener(LockContainer.this);
			passwordLockView.setControllerContainerLayout(mController_container_layout);
			passwordLockView.setVisible(isVisable);
			passwordLockView.setInputEnabel(lockEnable);
			passwordLockView.setEditable(diy);
			passwordLockView.setPasswordLockerInfo(passwordLockerInfo);
			passwordLockView.updateImage();

			if (!diy) {
				View passwordView = LayoutInflater.from(mContext).inflate(R.layout.password_layout, null);
				PasswordIndView passwordIndView = (PasswordIndView) passwordView.findViewById(R.id.passwordIndView);
				passwordIndView.create();
				passwordIndView.setMaxPassLength(4);
				passwordIndView.setInputPassLength(0);
				TextView mHeaderText = (TextView) passwordView.findViewById(R.id.loveslock_create_text);
				mHeaderText.setText(R.string.lovelocker_input_pass);

				passwordLockView.setPasswordIndView(passwordIndView);
				passwordLockView.setUnlockListener(mUnlockListener);
				LayoutParams passLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				passLayoutParams.topMargin = passwordLockerInfo.getY() - 150;
				addView(passwordView, passLayoutParams);

			}

			addView(passwordLockView, mLayoutParams);

			mLockView = passwordLockView;
			return;
		}

		if (mLockerInfo instanceof WordPasswordLockerInfo) {
			WordPasswordLockerInfo passwordLockerInfo = (WordPasswordLockerInfo) mLockerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = passwordLockerInfo.getX();
			mLayoutParams.topMargin = passwordLockerInfo.getY();

			WordPasswordLockView passwordLockView = (WordPasswordLockView) LayoutInflater.from(mContext).inflate(R.layout.wordpassword_locker_layout, null);
			passwordLockView.setContainerLayoutParams(mLayoutParams);
			passwordLockView.setOnUpdateViewListener(LockContainer.this);
			passwordLockView.setOnRemoveViewListener(LockContainer.this);
			passwordLockView.setOnFocuseChangeListener(LockContainer.this);
			passwordLockView.setControllerContainerLayout(mController_container_layout);
			passwordLockView.setVisible(isVisable);
			passwordLockView.setInputEnabel(lockEnable);
			passwordLockView.setEditable(diy);
			passwordLockView.setPasswordLockerInfo(passwordLockerInfo);

			if (!diy) {
				View passwordView = LayoutInflater.from(mContext).inflate(R.layout.password_layout, null);
				PasswordIndView passwordIndView = (PasswordIndView) passwordView.findViewById(R.id.passwordIndView);
				TextView textView = (TextView) passwordView.findViewById(R.id.cleanpass);
				passwordIndView.create();
				passwordIndView.setMaxPassLength(4);
				passwordIndView.setInputPassLength(0);
				TextView mHeaderText = (TextView) passwordView.findViewById(R.id.loveslock_create_text);
				mHeaderText.setText(R.string.lovelocker_input_pass);

				passwordLockView.setPasswordIndView(passwordIndView);
				passwordLockView.setTextView(textView);
				passwordLockView.setUnlockListener(mUnlockListener);
				LayoutParams passLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				passLayoutParams.topMargin = passwordLockerInfo.getY() - 150;
				addView(passwordView, passLayoutParams);
			}

			addView(passwordLockView, mLayoutParams);

			mLockView = passwordLockView;
			return;
		}
		
		if (mLockerInfo instanceof NumPasswordLockerInfo) {
			NumPasswordLockerInfo passwordLockerInfo = (NumPasswordLockerInfo) mLockerInfo;

			LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.leftMargin = passwordLockerInfo.getX();
			mLayoutParams.topMargin = passwordLockerInfo.getY();

			NumPasswordLockView passwordLockView = (NumPasswordLockView) LayoutInflater.from(mContext).inflate(R.layout.numpassword_locker_layout, null);
			passwordLockView.setContainerLayoutParams(mLayoutParams);
			passwordLockView.setOnUpdateViewListener(LockContainer.this);
			passwordLockView.setOnRemoveViewListener(LockContainer.this);
			passwordLockView.setOnFocuseChangeListener(LockContainer.this);
			passwordLockView.setControllerContainerLayout(mController_container_layout);
			passwordLockView.setVisible(isVisable);
			passwordLockView.setInputEnabel(lockEnable);
			passwordLockView.setEditable(diy);
			passwordLockView.setPassnumLockerInfo(passwordLockerInfo);

			if (!diy) {
				View passwordView = LayoutInflater.from(mContext).inflate(R.layout.password_layout, null);
				PasswordIndView passwordIndView = (PasswordIndView) passwordView.findViewById(R.id.passwordIndView);
				TextView textView = (TextView) passwordView.findViewById(R.id.cleanpass);
				passwordIndView.create();
				passwordIndView.setMaxPassLength(4);
				passwordIndView.setInputPassLength(0);
				TextView mHeaderText = (TextView) passwordView.findViewById(R.id.loveslock_create_text);
				mHeaderText.setText(R.string.lovelocker_input_pass);

				passwordLockView.setPasswordIndView(passwordIndView);
				passwordLockView.setTextView(textView);
				passwordLockView.setUnlockListener(mUnlockListener);
				LayoutParams passLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				passLayoutParams.topMargin = passwordLockerInfo.getY() - 150;
				addView(passwordView, passLayoutParams);
			}

			addView(passwordLockView, mLayoutParams);

			mLockView = passwordLockView;
			return;
		}

		if (LockerInfo.StyleFree == lockerInfo.getStyleId()) {
			if (!diy) {
				FreeLockerView freeLockerView = new FreeLockerView(mContext);
				freeLockerView.setUnlockListener(mUnlockListener);
				addView(freeLockerView);
			}
		}

	}

	public void setResourceImage(Bitmap bitmap) {
		if (mLockView != null) {
			int fromId = LockApplication.getInstance().getConfig().getFrom_id();
			switch (fromId) {
			case ChooseStickerUtils.FROM_LOCKER_LOVE:
				if (mLockView instanceof LoveLockView) {
					LoveLockView loveLockView = (LoveLockView) mLockView;
					loveLockView.selectImage(bitmap);

				}
				break;
			case ChooseStickerUtils.FROM_LOCKER_LOVERS:
				if (mLockView instanceof CoupleContainerView) {
					CoupleContainerView coupleContainerView = (CoupleContainerView) mLockView;
					coupleContainerView.selectImage(bitmap);

				}
				break;
			case ChooseStickerUtils.FROM_LOCKER_APPS:
				if (mLockView instanceof AppContainerView) {
					AppContainerView appContainerView = (AppContainerView) mLockView;
					appContainerView.selectImage(bitmap);

				}
				break;
			case ChooseStickerUtils.FROM_LOCKER_NINE:
				if (mLockView instanceof LockPatternView) {
					LockPatternView lockPatternView = (LockPatternView) mLockView;
					lockPatternView.selectImage(bitmap);
				}
				break;
			case ChooseStickerUtils.FROM_LOCKER_TWELVE:
				if (mLockView instanceof LockPatternView_12) {
					LockPatternView_12 lockPatternView_12 = (LockPatternView_12) mLockView;
					lockPatternView_12.selectImage(bitmap);
				}
				break;
			case ChooseStickerUtils.FROM_LOCKER_SLIDE:
				if (mLockView instanceof SlideContainerView) {
					SlideContainerView slideContainerView = (SlideContainerView) mLockView;
					slideContainerView.selectImage(bitmap);
				}
				break;
			case ChooseStickerUtils.FROM_LOCKER_IMAGEPASSWORD:
				if (mLockView instanceof ImagePasswordLockView) {
					ImagePasswordLockView imagePasswordLockView = (ImagePasswordLockView) mLockView;
					imagePasswordLockView.selectImage(bitmap);
				}
				break;

			default:
				break;
			}
		}
	}

	public void setDiy(boolean diy) {
		this.diy = diy;
	}

	public void setLockEnable(boolean lockEnable) {
		this.lockEnable = lockEnable;
	}

	public void setUnlockListener(UnlockListener unlockListener) {
		this.mUnlockListener = unlockListener;
	}

	@Override
	public void removeView(StickerInfo stickerInfo, View stickerView) {
		diyChange = true;
		removeView(stickerView);
		mStickerInfos.remove(stickerInfo);
		mStickerViews.remove(stickerView);
		mController_container_layout.removeAllViews();
		if (stickerInfo instanceof StatusbarStickerInfo) {
			statusbarStickerInfo = null;
		}
	}

	@Override
	public void updateView(View stickerView, LayoutParams layoutParams) {
		updateViewLayout(stickerView, layoutParams);
	}

	@Override
	public void focuseChange(View stickerView) {
		clearOtherFocuse(stickerView);
	}

	@Override
	public void removeView(LockerInfo lockerInfo, View view) {
		diyChange = true;
		removeView(view);
		mLockerInfo = null;
		mLockView = null;
		mController_container_layout.removeAllViews();
	}

	public ArrayList<StickerInfo> getStickerInfos() {
		return mStickerInfos;
	}

	public LockerInfo getLockInfo() {
		return mLockerInfo;
	}

	public Bitmap getLockPreview() {
		boolean willNotCache = willNotCacheDrawing();
		setWillNotCacheDrawing(false);
		int color = getDrawingCacheBackgroundColor();
		setDrawingCacheBackgroundColor(0);
		if (color != 0) {
			destroyDrawingCache();
		}
		buildDrawingCache();
		Bitmap cacheBitmap = getDrawingCache();
		if (cacheBitmap == null) {
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
		destroyDrawingCache();
		setWillNotCacheDrawing(willNotCache);
		setDrawingCacheBackgroundColor(color);
		
		return bitmap;
	}

	public int getWallpaperColor() {
		return wallpaperColor;
	}

	public boolean isDiyChange() {
		return diyChange;
	}

	public void setDiyChange(boolean diyChange) {
		this.diyChange = diyChange;
	}

	public void setToggleListener(ToggleListener toggleListener) {
		this.mToggleListener = toggleListener;
	}

	public interface ToggleListener {
		public void toggleTabView();
	}

}
