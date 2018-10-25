package com.lockstudio.sticklocker.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

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
import com.lockstudio.sticklocker.model.PhoneStickerInfo;
import com.lockstudio.sticklocker.model.SMSStickerInfo;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import cn.opda.android.activity.R;

public class ThemeUtils {
	public static void saveTheme(Context mContext, ThemeConfig themeConfig) {

		String theme_path = themeConfig.getThemePath();
		File themeFolder = null;
		if (TextUtils.isEmpty(theme_path)) {
			File themeRootFolder = new File(MConstants.THEME_PATH);
			if (!themeRootFolder.exists()) {
				themeRootFolder.mkdirs();
			}

			String themeName = System.currentTimeMillis() + "";

			themeFolder = new File(MConstants.THEME_PATH, themeName);
			themeFolder.mkdirs();
		} else {
			themeFolder = new File(theme_path);
			FileUtils.deleteFileByPathIgnore(theme_path);
			themeFolder.mkdirs();
		}
		
		ArrayList<String> fontUlrs=new ArrayList<String>();

		JSONObject jsonObject = new JSONObject();
		try {
			if (!TextUtils.isEmpty(themeConfig.getWallpaper()) && new File(themeConfig.getWallpaper()).exists()) {
				File wallpaperFile = new File(themeFolder, "wallpaper");
				try {
					FileUtils.copyFile(new File(themeConfig.getWallpaper()), wallpaperFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			jsonObject.put("wallpaper_color", themeConfig.getWallpaperColor());
			jsonObject.put("screen_width", themeConfig.getScreenWidth());
			jsonObject.put("screen_height", themeConfig.getScreenHeight());

			LockerInfo lockerInfo = themeConfig.getLockerInfo();
			JSONObject lockerJsonObject = new JSONObject();
			if (lockerInfo instanceof TwelvePatternLockerInfo) {
				TwelvePatternLockerInfo twelvePatternLockerInfo = (TwelvePatternLockerInfo) lockerInfo;
				lockerJsonObject.put("type", twelvePatternLockerInfo.getStyleId());
				lockerJsonObject.put("y", twelvePatternLockerInfo.getY());
				lockerJsonObject.put("width", twelvePatternLockerInfo.getWidth());
				lockerJsonObject.put("height", twelvePatternLockerInfo.getHeight());
				lockerJsonObject.put("line_color", twelvePatternLockerInfo.getLineColor());
				lockerJsonObject.put("line_show", twelvePatternLockerInfo.isDrawLine());

				JSONArray imageJsonArray = new JSONArray();
				Bitmap[] bitmaps = twelvePatternLockerInfo.getBitmaps();
				for (int i = 0; i < bitmaps.length; i++) {
					Bitmap bitmap = bitmaps[i];
					if (bitmap != null) {
						DrawableUtils.saveBitmap(new File(themeFolder, "lockimage_" + i), bitmap, false);
						imageJsonArray.put("lockimage_" + i);
					} else {
						imageJsonArray.put("");
					}
				}
				lockerJsonObject.put("lockimage", imageJsonArray);
				jsonObject.put("lock", lockerJsonObject);
			} else if (lockerInfo instanceof LoveLockerInfo) {
				LoveLockerInfo loveLockerInfo = (LoveLockerInfo) lockerInfo;
				lockerJsonObject.put("type", loveLockerInfo.getStyleId());
				lockerJsonObject.put("y", loveLockerInfo.getY());
				lockerJsonObject.put("width", loveLockerInfo.getWidth());
				lockerJsonObject.put("height", loveLockerInfo.getHeight());

				JSONArray imageJsonArray = new JSONArray();
				Bitmap[] bitmaps = loveLockerInfo.getBitmaps();
				for (int i = 0; i < bitmaps.length; i++) {
					Bitmap bitmap = bitmaps[i];
					if (bitmap != null) {
						DrawableUtils.saveBitmap(new File(themeFolder, "lockimage_" + i), bitmap, false);
						imageJsonArray.put("lockimage_" + i);
					} else {
						imageJsonArray.put("");
					}
				}
				lockerJsonObject.put("lockimage", imageJsonArray);
				jsonObject.put("lock", lockerJsonObject);
			} else if (lockerInfo instanceof NinePatternLockerInfo) {
				NinePatternLockerInfo ninePatternLockerInfo = (NinePatternLockerInfo) lockerInfo;
				lockerJsonObject.put("type", ninePatternLockerInfo.getStyleId());
				lockerJsonObject.put("y", ninePatternLockerInfo.getY());
				lockerJsonObject.put("width", ninePatternLockerInfo.getWidth());
				lockerJsonObject.put("height", ninePatternLockerInfo.getHeight());
				lockerJsonObject.put("line_color", ninePatternLockerInfo.getLineColor());
				lockerJsonObject.put("line_show", ninePatternLockerInfo.isDrawLine());
				if (ninePatternLockerInfo.getBitmap() != null) {

					DrawableUtils.saveBitmap(new File(themeFolder, "lockimage"), ninePatternLockerInfo.getBitmap(), false);
					lockerJsonObject.put("lockimage", "lockimage");
				}

				jsonObject.put("lock", lockerJsonObject);
			} else if (lockerInfo instanceof CoupleLockerInfo) {
				CoupleLockerInfo coupleLockerInfo = (CoupleLockerInfo) lockerInfo;
				lockerJsonObject.put("type", coupleLockerInfo.getStyleId());
				lockerJsonObject.put("y", coupleLockerInfo.getY());
				JSONArray imageJsonArray = new JSONArray();
				Bitmap[] bitmaps = coupleLockerInfo.getBitmaps();
				Bitmap bitmap1 = bitmaps[0];
				Bitmap bitmap2 = bitmaps[1];
				if (bitmap1 != null) {
					DrawableUtils.saveBitmap(new File(themeFolder, "lockimage_" + 1), bitmap1, false);
					imageJsonArray.put("lockimage_" + 1);
				} else {
					imageJsonArray.put("");
				}
				if (bitmap2 != null) {
					DrawableUtils.saveBitmap(new File(themeFolder, "lockimage_" + 2), bitmap2, false);
					imageJsonArray.put("lockimage_" + 2);
				} else {
					imageJsonArray.put("");
				}
				lockerJsonObject.put("lockimage", imageJsonArray);
				jsonObject.put("lock", lockerJsonObject);

			} else if (lockerInfo instanceof AppLockerInfo) {
				AppLockerInfo coupleLockerInfo = (AppLockerInfo) lockerInfo;
				lockerJsonObject.put("type", coupleLockerInfo.getStyleId());
				lockerJsonObject.put("y", coupleLockerInfo.getY());
				lockerJsonObject.put("width", coupleLockerInfo.getWidth());
				lockerJsonObject.put("height", coupleLockerInfo.getHeight());
				lockerJsonObject.put("action1", coupleLockerInfo.action1);
				lockerJsonObject.put("action2", coupleLockerInfo.action2);
				lockerJsonObject.put("action3", coupleLockerInfo.action3);
				JSONArray imageJsonArray = new JSONArray();
				Bitmap[] bitmaps = coupleLockerInfo.getBitmaps();
				Bitmap bitmap1 = bitmaps[0];
				Bitmap bitmap2 = bitmaps[1];
				Bitmap bitmap3 = bitmaps[2];
				Bitmap bitmap4 = bitmaps[3];
				if (bitmap1 != null) {
					DrawableUtils.saveBitmap(new File(themeFolder, "lockimage_" + 1), bitmap1, false);
					imageJsonArray.put("lockimage_" + 1);
				} else {
					imageJsonArray.put("");
				}
				if (bitmap2 != null) {
					DrawableUtils.saveBitmap(new File(themeFolder, "lockimage_" + 2), bitmap2, false);
					imageJsonArray.put("lockimage_" + 2);
				} else {
					imageJsonArray.put("");
				}
				if (bitmap3 != null) {
					DrawableUtils.saveBitmap(new File(themeFolder, "lockimage_" + 3), bitmap3, false);
					imageJsonArray.put("lockimage_" + 3);
				} else {
					imageJsonArray.put("");
				}
				if (bitmap4 != null) {
					DrawableUtils.saveBitmap(new File(themeFolder, "lockimage_" + 4), bitmap4, false);
					imageJsonArray.put("lockimage_" + 4);
				} else {
					imageJsonArray.put("");
				}
				lockerJsonObject.put("lockimage", imageJsonArray);
				jsonObject.put("lock", lockerJsonObject);

			}else if (lockerInfo instanceof SlideLockerInfo) {
				SlideLockerInfo slideLockerInfo = (SlideLockerInfo) lockerInfo;
				lockerJsonObject.put("type", slideLockerInfo.getStyleId());
				lockerJsonObject.put("y", slideLockerInfo.getY());

				lockerJsonObject.put("left1", slideLockerInfo.getLeft1());
				lockerJsonObject.put("top1", slideLockerInfo.getTop1());
				lockerJsonObject.put("right1", slideLockerInfo.getRight1());
				lockerJsonObject.put("bottom1", slideLockerInfo.getBottom1());
				lockerJsonObject.put("left2", slideLockerInfo.getLeft2());
				lockerJsonObject.put("top2", slideLockerInfo.getTop2());
				lockerJsonObject.put("right2", slideLockerInfo.getRight2());
				lockerJsonObject.put("bottom2", slideLockerInfo.getBottom2());
				lockerJsonObject.put("first", slideLockerInfo.isFirst());
				lockerJsonObject.put("bitmapRes", slideLockerInfo.getBitmapRes());

				JSONArray imageJsonArray = new JSONArray();
				Bitmap[] bitmaps = slideLockerInfo.getBitmaps();
				Bitmap bitmap1 = bitmaps[0];
				Bitmap bitmap2 = bitmaps[1];
				if (bitmap1 != null) {
					DrawableUtils.saveBitmap(new File(themeFolder, "lockimage_" + 1), bitmap1, false);
					imageJsonArray.put("lockimage_" + 1);
				} else {
					imageJsonArray.put("");
				}
				if (bitmap2 != null) {
					DrawableUtils.saveBitmap(new File(themeFolder, "lockimage_" + 2), bitmap2, false);
					imageJsonArray.put("lockimage_" + 2);
				} else {
					imageJsonArray.put("");
				}
				lockerJsonObject.put("lockimage", imageJsonArray);
				jsonObject.put("lock", lockerJsonObject);
			} else if (lockerInfo instanceof ImagePasswordLockerInfo) {
				ImagePasswordLockerInfo imagePasswordLockerInfo = (ImagePasswordLockerInfo) lockerInfo;
				lockerJsonObject.put("type", imagePasswordLockerInfo.getStyleId());
				lockerJsonObject.put("y", imagePasswordLockerInfo.getY());
				lockerJsonObject.put("width", imagePasswordLockerInfo.getWidth());
				lockerJsonObject.put("height", imagePasswordLockerInfo.getHeight());

				JSONArray imageJsonArray = new JSONArray();
				Bitmap[] bitmaps = imagePasswordLockerInfo.getBitmaps();
				for (int i = 0; i < bitmaps.length; i++) {
					Bitmap bitmap = bitmaps[i];
					if (bitmap != null) {
						DrawableUtils.saveBitmap(new File(themeFolder, "lockimage_" + i), bitmap, false);
						imageJsonArray.put("lockimage_" + i);
					} else {
						imageJsonArray.put("");
					}
				}
				lockerJsonObject.put("lockimage", imageJsonArray);
				jsonObject.put("lock", lockerJsonObject);
			} else if (lockerInfo instanceof WordPasswordLockerInfo) {
				WordPasswordLockerInfo wordPasswordLockerInfo = (WordPasswordLockerInfo) lockerInfo;
				lockerJsonObject.put("type", wordPasswordLockerInfo.getStyleId());
				lockerJsonObject.put("y", wordPasswordLockerInfo.getY());
				lockerJsonObject.put("width", wordPasswordLockerInfo.getWidth());
				lockerJsonObject.put("height", wordPasswordLockerInfo.getHeight());

				JSONArray stringJsonArray = new JSONArray();
				String[] words = wordPasswordLockerInfo.getWords();
				for (int i = 0; i < words.length; i++) {
					String word = words[i];
					if (!TextUtils.isEmpty(word)) {
						stringJsonArray.put(word);
					} else {
						stringJsonArray.put("");
					}
				}
				lockerJsonObject.put("words", stringJsonArray);
				lockerJsonObject.put("alpha", wordPasswordLockerInfo.getAlpha());
				lockerJsonObject.put("textColor", wordPasswordLockerInfo.getTextColor());
				lockerJsonObject.put("shadowColor", wordPasswordLockerInfo.getShadowColor());
				lockerJsonObject.put("textScale", wordPasswordLockerInfo.getTextScale());
				lockerJsonObject.put("shapeScale", wordPasswordLockerInfo.getShapeScale());
				lockerJsonObject.put("shapeName", wordPasswordLockerInfo.getShapeName());
				if (!TextUtils.isEmpty(wordPasswordLockerInfo.getFont())) {
					String fontName = new File(wordPasswordLockerInfo.getFont()).getName();
					lockerJsonObject.put("font", fontName);
				}
				jsonObject.put("lock", lockerJsonObject);
			}else if (lockerInfo instanceof NumPasswordLockerInfo) {
				NumPasswordLockerInfo numPasswordLockerInfo = (NumPasswordLockerInfo) lockerInfo;
				lockerJsonObject.put("type", numPasswordLockerInfo.getStyleId());
				lockerJsonObject.put("y", numPasswordLockerInfo.getY());
				lockerJsonObject.put("width", numPasswordLockerInfo.getWidth());
				lockerJsonObject.put("height", numPasswordLockerInfo.getHeight());

				JSONArray stringJsonArray = new JSONArray();
				String[] words = numPasswordLockerInfo.getWords();
				for (int i = 0; i < words.length; i++) {
					String word = words[i];
					if (!TextUtils.isEmpty(word)) {
						stringJsonArray.put(word);
					} else {
						stringJsonArray.put("");
					}
				}
				lockerJsonObject.put("words", stringJsonArray);
				lockerJsonObject.put("alpha", numPasswordLockerInfo.getAlpha());
				lockerJsonObject.put("textColor", numPasswordLockerInfo.getTextColor());
				lockerJsonObject.put("shadowColor", numPasswordLockerInfo.getShadowColor());
				lockerJsonObject.put("textScale", numPasswordLockerInfo.getTextScale());
				lockerJsonObject.put("shapeScale", numPasswordLockerInfo.getShapeScale());
				lockerJsonObject.put("shapeName", numPasswordLockerInfo.getShapeName());
				if (!TextUtils.isEmpty(numPasswordLockerInfo.getFont())) {
					String fontName = new File(numPasswordLockerInfo.getFont()).getName();
					lockerJsonObject.put("font", fontName);
				}
				jsonObject.put("lock", lockerJsonObject);
			} else {
				lockerJsonObject.put("type", lockerInfo.getStyleId());
				jsonObject.put("lock", lockerJsonObject);
			}

			ArrayList<StickerInfo> stickerInfos = themeConfig.getStickerInfoList();
			JSONArray stickerJsonArray = new JSONArray();
			for (StickerInfo stickerInfo : stickerInfos) {
				JSONObject stickJsonObject = new JSONObject();
				if (stickerInfo instanceof ImageStickerInfo) {
					ImageStickerInfo imageStickerInfo = (ImageStickerInfo) stickerInfo;
					stickJsonObject.put("type", imageStickerInfo.styleId);
					stickJsonObject.put("x", imageStickerInfo.x);
					stickJsonObject.put("y", imageStickerInfo.y);
					stickJsonObject.put("width", imageStickerInfo.width);
					stickJsonObject.put("height", imageStickerInfo.height);
					stickJsonObject.put("alpha", imageStickerInfo.alpha);
					stickJsonObject.put("angle", imageStickerInfo.angle);

					File stickerFile = new File(themeFolder, System.currentTimeMillis() + "");
					if(imageStickerInfo.imageBitmap!=null){
						DrawableUtils.saveBitmap(stickerFile, imageStickerInfo.imageBitmap, false);
					}

					stickJsonObject.put("image", stickerFile.getName());
				} else if (stickerInfo instanceof WordStickerInfo) {
					WordStickerInfo wordStickerInfo = (WordStickerInfo) stickerInfo;
					stickJsonObject.put("type", wordStickerInfo.styleId);
					stickJsonObject.put("x", wordStickerInfo.x);
					stickJsonObject.put("y", wordStickerInfo.y);
					stickJsonObject.put("angle", wordStickerInfo.angle);
					stickJsonObject.put("text", wordStickerInfo.text);
					stickJsonObject.put("textSize", wordStickerInfo.textSize);
					stickJsonObject.put("textColor", wordStickerInfo.textColor);
					stickJsonObject.put("shadowColor", wordStickerInfo.shadowColor);
					stickJsonObject.put("gravity", wordStickerInfo.gravity);
					stickJsonObject.put("alpha", wordStickerInfo.alpha);
					if (!TextUtils.isEmpty(wordStickerInfo.font)) {
						String fontName = new File(wordStickerInfo.font).getName();
						stickJsonObject.put("font", fontName);
						if(!fontUlrs.contains(wordStickerInfo.fontUrl)){
							fontUlrs.add(wordStickerInfo.fontUrl);
						}
					}

				} else if (stickerInfo instanceof TimeStickerInfo) {
					TimeStickerInfo timeStickerInfo = (TimeStickerInfo) stickerInfo;
					stickJsonObject.put("type", timeStickerInfo.styleId);
					stickJsonObject.put("x", timeStickerInfo.x);
					stickJsonObject.put("y", timeStickerInfo.y);
					stickJsonObject.put("angle", timeStickerInfo.angle);
					stickJsonObject.put("alpha", timeStickerInfo.alpha);
					stickJsonObject.put("timeStyle", timeStickerInfo.timeStyle);
					stickJsonObject.put("textSize1", timeStickerInfo.textSize1);
					stickJsonObject.put("textSize2", timeStickerInfo.textSize2);
					stickJsonObject.put("textColor", timeStickerInfo.textColor);
					stickJsonObject.put("shadowColor", timeStickerInfo.shadowColor);
					if (!TextUtils.isEmpty(timeStickerInfo.font)) {
						String fontName = new File(timeStickerInfo.font).getName();
						stickJsonObject.put("font", fontName);
						if(!fontUlrs.contains(timeStickerInfo.fontUrl)){
							fontUlrs.add(timeStickerInfo.fontUrl);
						}
					}
				} else if (stickerInfo instanceof FlashlightStickerInfo) {
					FlashlightStickerInfo flashlightStickerInfo = (FlashlightStickerInfo) stickerInfo;
					stickJsonObject.put("isClick", flashlightStickerInfo.isClick);
					stickJsonObject.put("type", flashlightStickerInfo.styleId);
					stickJsonObject.put("x", flashlightStickerInfo.x);
					stickJsonObject.put("y", flashlightStickerInfo.y);
					stickJsonObject.put("angle", flashlightStickerInfo.angle);
					stickJsonObject.put("scale", flashlightStickerInfo.scale);
					stickJsonObject.put("textColor", flashlightStickerInfo.textColor);
					stickJsonObject.put("textSize", flashlightStickerInfo.textSize);;
				} else if (stickerInfo instanceof CameraStickerInfo) {
					CameraStickerInfo cameraStickerInfo = (CameraStickerInfo) stickerInfo;
					stickJsonObject.put("isClick", cameraStickerInfo.isClick);
					stickJsonObject.put("type", cameraStickerInfo.styleId);
					stickJsonObject.put("x", cameraStickerInfo.x);
					stickJsonObject.put("y", cameraStickerInfo.y);
					stickJsonObject.put("angle", cameraStickerInfo.angle);
					stickJsonObject.put("scale", cameraStickerInfo.scale);
					stickJsonObject.put("textColor", cameraStickerInfo.textColor);
					stickJsonObject.put("textSize", cameraStickerInfo.textSize);;
				} else if (stickerInfo instanceof BatteryStickerInfo) {
					BatteryStickerInfo batteryStickerInfo = (BatteryStickerInfo) stickerInfo;
					stickJsonObject.put("type", batteryStickerInfo.styleId);
					stickJsonObject.put("batteryStyle", batteryStickerInfo.batteryStyle);
					stickJsonObject.put("x", batteryStickerInfo.x);
					stickJsonObject.put("y", batteryStickerInfo.y);
					stickJsonObject.put("angle", batteryStickerInfo.angle);
					stickJsonObject.put("scale", batteryStickerInfo.scale);
					stickJsonObject.put("textColor", batteryStickerInfo.textColor);
					stickJsonObject.put("textSize", batteryStickerInfo.textSize);;
				} else if (stickerInfo instanceof WeatherStickerInfo) {
					WeatherStickerInfo weatherStickerInfo = (WeatherStickerInfo) stickerInfo;
					stickJsonObject.put("type", weatherStickerInfo.styleId);
					stickJsonObject.put("x", weatherStickerInfo.x);
					stickJsonObject.put("y", weatherStickerInfo.y);
					stickJsonObject.put("angle", weatherStickerInfo.angle);
					stickJsonObject.put("alpha", weatherStickerInfo.alpha);
					stickJsonObject.put("text", weatherStickerInfo.text);
					stickJsonObject.put("weatherStyle", weatherStickerInfo.weatherStyle);
					stickJsonObject.put("textSize1", weatherStickerInfo.textSize1);
					stickJsonObject.put("textSize2", weatherStickerInfo.textSize2);
					stickJsonObject.put("textColor", weatherStickerInfo.textColor);
					stickJsonObject.put("shadowColor", weatherStickerInfo.shadowColor);
					if (!TextUtils.isEmpty(weatherStickerInfo.font)) {
						String fontName = new File(weatherStickerInfo.font).getName();
						stickJsonObject.put("font", fontName);
						if(!fontUlrs.contains(weatherStickerInfo.fontUrl)){
							fontUlrs.add(weatherStickerInfo.fontUrl);
						}
					}
				} else if (stickerInfo instanceof TimerStickerInfo) {
					TimerStickerInfo timerStickerInfo = (TimerStickerInfo) stickerInfo;
					stickJsonObject.put("type", timerStickerInfo.styleId);
					stickJsonObject.put("x", timerStickerInfo.x);
					stickJsonObject.put("y", timerStickerInfo.y);
					stickJsonObject.put("angle", timerStickerInfo.angle);
					stickJsonObject.put("text_title", timerStickerInfo.text_title);
					stickJsonObject.put("text_time", timerStickerInfo.text_time);
					stickJsonObject.put("textSize1", timerStickerInfo.textSize1);
					stickJsonObject.put("textSize2", timerStickerInfo.textSize2);
					stickJsonObject.put("textColor", timerStickerInfo.textColor);
					stickJsonObject.put("shadowColor", timerStickerInfo.shadowColor);
					stickJsonObject.put("timerFlag", timerStickerInfo.timerFlag);
					stickJsonObject.put("alpha", timerStickerInfo.alpha);
					if (!TextUtils.isEmpty(timerStickerInfo.font)) {
						String fontName = new File(timerStickerInfo.font).getName();
						stickJsonObject.put("font", fontName);
						if(!fontUlrs.contains(timerStickerInfo.fontUrl)){
							fontUlrs.add(timerStickerInfo.fontUrl);
						}
					}
				} else if (stickerInfo instanceof StatusbarStickerInfo) {
					StatusbarStickerInfo statusbarStickerInfo = (StatusbarStickerInfo) stickerInfo;
					stickJsonObject.put("type", statusbarStickerInfo.styleId);
					stickJsonObject.put("x", statusbarStickerInfo.x);
					stickJsonObject.put("y", statusbarStickerInfo.y);
					stickJsonObject.put("angle", statusbarStickerInfo.angle);
					stickJsonObject.put("text", statusbarStickerInfo.text);
					stickJsonObject.put("textRes", statusbarStickerInfo.textRes);
					stickJsonObject.put("textSize", statusbarStickerInfo.textSize);
					stickJsonObject.put("textColor", statusbarStickerInfo.textColor);
					stickJsonObject.put("shadowColor", statusbarStickerInfo.shadowColor);
					if (!TextUtils.isEmpty(statusbarStickerInfo.font)) {
						String fontName = new File(statusbarStickerInfo.font).getName();
						stickJsonObject.put("font", fontName);
						if(!fontUlrs.contains(statusbarStickerInfo.fontUrl)){
							fontUlrs.add(statusbarStickerInfo.fontUrl);
						}
					}
				} else if (stickerInfo instanceof PhoneStickerInfo) {
					PhoneStickerInfo phoneStickerInfo = (PhoneStickerInfo) stickerInfo;
					stickJsonObject.put("type", phoneStickerInfo.styleId);
					stickJsonObject.put("x", phoneStickerInfo.x);
					stickJsonObject.put("y", phoneStickerInfo.y);
					stickJsonObject.put("angle", phoneStickerInfo.angle);
					stickJsonObject.put("scale", phoneStickerInfo.scale);
					stickJsonObject.put("textColor", phoneStickerInfo.textColor);
				} else if (stickerInfo instanceof SMSStickerInfo) {
					SMSStickerInfo smsStickerInfo = (SMSStickerInfo) stickerInfo;
					stickJsonObject.put("type", smsStickerInfo.styleId);
					stickJsonObject.put("x", smsStickerInfo.x);
					stickJsonObject.put("y", smsStickerInfo.y);
					stickJsonObject.put("angle", smsStickerInfo.angle);
					stickJsonObject.put("scale", smsStickerInfo.scale);
					stickJsonObject.put("textColor", smsStickerInfo.textColor);
				} else if (stickerInfo instanceof DayWordStickerInfo) {
					DayWordStickerInfo dayWordStickerInfo = (DayWordStickerInfo) stickerInfo;
					stickJsonObject.put("type", dayWordStickerInfo.styleId);
					stickJsonObject.put("x", dayWordStickerInfo.x);
					stickJsonObject.put("y", dayWordStickerInfo.y);
					stickJsonObject.put("angle", dayWordStickerInfo.angle);
					stickJsonObject.put("textSize", dayWordStickerInfo.textSize);
					stickJsonObject.put("textColor", dayWordStickerInfo.textColor);
					stickJsonObject.put("shadowColor", dayWordStickerInfo.shadowColor);
					stickJsonObject.put("orientation", dayWordStickerInfo.orientation);
					stickJsonObject.put("gravity", dayWordStickerInfo.gravity);
					if (!TextUtils.isEmpty(dayWordStickerInfo.font)) {
						String fontName = new File(dayWordStickerInfo.font).getName();
						stickJsonObject.put("font", fontName);
						if(!fontUlrs.contains(dayWordStickerInfo.fontUrl)){
							fontUlrs.add(dayWordStickerInfo.fontUrl);
						}
					}

				} else if (stickerInfo instanceof HollowWordsStickerInfo) {
					HollowWordsStickerInfo hollowWordsStickerInfo = (HollowWordsStickerInfo) stickerInfo;
					stickJsonObject.put("type", hollowWordsStickerInfo.styleId);
					stickJsonObject.put("x", hollowWordsStickerInfo.x);
					stickJsonObject.put("y", hollowWordsStickerInfo.y);
					stickJsonObject.put("upText", hollowWordsStickerInfo.upText);
					stickJsonObject.put("downText", hollowWordsStickerInfo.downText);
					stickJsonObject.put("upTextSize", hollowWordsStickerInfo.upTextSize);
					stickJsonObject.put("downTextSize", hollowWordsStickerInfo.downTextSize);
					stickJsonObject.put("upTextColor", hollowWordsStickerInfo.upTextColor);
					stickJsonObject.put("downTextColor", hollowWordsStickerInfo.downTextColor);
					stickJsonObject.put("upShadowColor", hollowWordsStickerInfo.upShadowColor);
					stickJsonObject.put("downShadowColor", hollowWordsStickerInfo.downShadowColor);
					stickJsonObject.put("upAlpha", hollowWordsStickerInfo.upAlpha);
					stickJsonObject.put("downAlpha", hollowWordsStickerInfo.downAlpha);
					stickJsonObject.put("offset", hollowWordsStickerInfo.offset);
					if (!TextUtils.isEmpty(hollowWordsStickerInfo.upFont)) {
						String fontName = new File(hollowWordsStickerInfo.upFont).getName();
						stickJsonObject.put("upFont", fontName);
					}
					if (!TextUtils.isEmpty(hollowWordsStickerInfo.downFont)) {
						String fontName = new File(hollowWordsStickerInfo.downFont).getName();
						stickJsonObject.put("downFont", fontName);
					}

				}

				stickerJsonArray.put(stickJsonObject);
			}
			jsonObject.put("stickers", stickerJsonArray);
			String configFile = themeFolder.getAbsolutePath() + "/" + MConstants.config;
			String configFonts = themeFolder.getAbsolutePath() + "/" + MConstants.fonts;
			String previewFile = themeFolder.getAbsolutePath() + "/preview";
			
			JSONObject fontsJsonObject=new JSONObject();
			JSONArray fontsJsonArray=new JSONArray();
			for (String font : fontUlrs) {
				fontsJsonArray.put(font);
			}
			fontsJsonObject.put("fontUrls", fontsJsonArray);

			DrawableUtils.saveBitmap(new File(previewFile), themeConfig.getThemePreview(), true);
			System.out.println("themeFolder.getAbsolutePath():::::"+themeFolder.getAbsolutePath());
			System.out.println("jsonObject.toString():::::"+jsonObject.toString());
			
			FileUtils.write(configFile, jsonObject.toString());
			FileUtils.write(configFonts, fontsJsonObject.toString());
			Log.i("debug", "tring-->"+fontsJsonObject.toString());

			if (LockerInfo.StyleLove == lockerInfo.getStyleId()) {
//				Intent intent = new Intent(mContext, CreateLoveLockPasswordActivity.class);
//				intent.putExtra("theme_path", themeFolder.getAbsolutePath());
//				intent.putExtra("theme_sendBroadcast", false);
//				mContext.startActivity(intent);
				LockApplication.getInstance().getConfig().setThemeName(themeFolder.getAbsolutePath(), false);
			} else if (LockerInfo.StyleNinePattern == lockerInfo.getStyleId()) {
//				Intent intent = new Intent(mContext, CreateGesturePasswordActivity.class);
//				intent.putExtra("theme_path", themeFolder.getAbsolutePath());
//				intent.putExtra("theme_sendBroadcast", false);
//				mContext.startActivity(intent);
				LockApplication.getInstance().getConfig().setThemeName(themeFolder.getAbsolutePath(), false);
			} else if (LockerInfo.StyleTwelvePattern == lockerInfo.getStyleId()) {
//				Intent intent = new Intent(mContext, CreateGesture12PasswordActivity.class);
//				intent.putExtra("theme_path", themeFolder.getAbsolutePath());
//				intent.putExtra("theme_sendBroadcast", false);
//				mContext.startActivity(intent);
				LockApplication.getInstance().getConfig().setThemeName(themeFolder.getAbsolutePath(), false);
			} else if (LockerInfo.StyleImagePassword == lockerInfo.getStyleId()) {
//				Intent intent = new Intent(mContext, CreatePasswordLockActivity.class);
//				intent.putExtra("theme_path", themeFolder.getAbsolutePath());
//				intent.putExtra("theme_sendBroadcast", false);
//				mContext.startActivity(intent);
				LockApplication.getInstance().getConfig().setThemeName(themeFolder.getAbsolutePath(), false);
			} else if (LockerInfo.StyleWordPassword == lockerInfo.getStyleId()) {
//				Intent intent = new Intent(mContext, CreateWordPasswordLockActivity.class);
//				intent.putExtra("theme_path", themeFolder.getAbsolutePath());
//				intent.putExtra("theme_sendBroadcast", false);
//				mContext.startActivity(intent);
				LockApplication.getInstance().getConfig().setThemeName(themeFolder.getAbsolutePath(), false);
			}else if (LockerInfo.StyleNumPassword == lockerInfo.getStyleId()) {
//				Intent intent = new Intent(mContext, CreateNumPasswordLockActivity.class);
//				intent.putExtra("theme_path", themeFolder.getAbsolutePath());
//				intent.putExtra("theme_sendBroadcast", false);
//				mContext.startActivity(intent);
				LockApplication.getInstance().getConfig().setThemeName(themeFolder.getAbsolutePath(), false);
			} else if (LockerInfo.StyleNone == lockerInfo.getStyleId()) {
				LockApplication.getInstance().getConfig().setThemeName(themeFolder.getAbsolutePath(), false);
			} else if (LockerInfo.StyleCouple == lockerInfo.getStyleId()) {
				LockApplication.getInstance().getConfig().setThemeName(themeFolder.getAbsolutePath(), false);
			} else if (LockerInfo.StyleSlide == lockerInfo.getStyleId()) {
				LockApplication.getInstance().getConfig().setThemeName(themeFolder.getAbsolutePath(), false);
			} else if (LockerInfo.StyleFree == lockerInfo.getStyleId()) {
				LockApplication.getInstance().getConfig().setThemeName(themeFolder.getAbsolutePath(), false);
			}else if (LockerInfo.StyleApp == lockerInfo.getStyleId()) {
				LockApplication.getInstance().getConfig().setThemeName(themeFolder.getAbsolutePath(), false);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public static ThemeConfig parseConfig(Context mContext, String configFile) {
		String configJson = FileUtils.getFileString(new File(configFile));
		String folderPath = new File(configFile).getParent();

		try {
			JSONObject jsonObject = new JSONObject(configJson);
			if (jsonObject != null) {
				ThemeConfig themeConfig = new ThemeConfig();
				themeConfig.setScreenWidth(jsonObject.optInt("screen_width"));
				themeConfig.setScreenHeight(jsonObject.optInt("screen_height"));
				int themeScreenWidth = themeConfig.getScreenWidth();
				int themeScreenHeight = themeConfig.getScreenHeight();
				int screenHeight = LockApplication.getInstance().getConfig().getScreenHeight();
				int screenWidth = LockApplication.getInstance().getConfig().getScreenWidth();
				int wallpaperColor = jsonObject.optInt("wallpaper_color");
				if (wallpaperColor != 0) {
					themeConfig.setWallpaperColor(wallpaperColor);
				}
				if (new File(folderPath, "wallpaper").exists()) {
					themeConfig.setWallpaper(new File(folderPath, "wallpaper").getAbsolutePath());
				}

				JSONObject lockerJsonObject = jsonObject.optJSONObject("lock");

				if (lockerJsonObject != null) {
					int styleId = lockerJsonObject.optInt("type");
					if (LockerInfo.StyleNone == styleId) {
						LockerInfo lockerInfo = new LockerInfo();
						lockerInfo.setStyleId(LockerInfo.StyleNone);
						themeConfig.setLockerInfo(lockerInfo);
					} else if (LockerInfo.StyleFree == styleId) {
						LockerInfo lockerInfo = new LockerInfo();
						lockerInfo.setStyleId(LockerInfo.StyleFree);
						themeConfig.setLockerInfo(lockerInfo);
					} else if (LockerInfo.StyleLove == styleId) {
						LoveLockerInfo loveLockerInfo = new LoveLockerInfo();
						loveLockerInfo.setStyleId(styleId);
						loveLockerInfo.setWidth((int) mContext.getResources().getDimension(R.dimen.lock_patternview_width));
						loveLockerInfo.setHeight(loveLockerInfo.getWidth());
						loveLockerInfo.setX((screenWidth - loveLockerInfo.getWidth()) / 2);
						int y = lockerJsonObject.optInt("y");
						loveLockerInfo.setY(y * screenHeight / themeScreenHeight);

						JSONArray imageJsonArray = lockerJsonObject.optJSONArray("lockimage");
						if (imageJsonArray != null && imageJsonArray.length() > 0) {
							Bitmap[] bitmaps = new Bitmap[10];
							for (int i = 0; i < imageJsonArray.length(); i++) {
								String path = imageJsonArray.optString(i);
								if (!TextUtils.isEmpty(path)) {
									bitmaps[i] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path);
								}
							}
							loveLockerInfo.setBitmaps(bitmaps);
						}
						themeConfig.setLockerInfo(loveLockerInfo);
					} else if (LockerInfo.StyleNinePattern == styleId) {
						NinePatternLockerInfo ninePatternLockerInfo = new NinePatternLockerInfo();
						ninePatternLockerInfo.setStyleId(styleId);
						ninePatternLockerInfo.setLineColor(lockerJsonObject.optInt("line_color"));
						ninePatternLockerInfo.setDrawLine(lockerJsonObject.optBoolean("line_show"));
						ninePatternLockerInfo.setWidth((int) mContext.getResources().getDimension(R.dimen.lock_patternview_width));
						ninePatternLockerInfo.setHeight(ninePatternLockerInfo.getWidth());
						ninePatternLockerInfo.setX((screenWidth - ninePatternLockerInfo.getWidth()) / 2);
						int y = lockerJsonObject.optInt("y");
						ninePatternLockerInfo.setY(y * screenHeight / themeScreenHeight);
						String path = lockerJsonObject.optString("lockimage");

						if (!TextUtils.isEmpty(path)) {
							ninePatternLockerInfo.setBitmap(DrawableUtils.getBitmap(mContext, folderPath + "/" + path));
						}
						themeConfig.setLockerInfo(ninePatternLockerInfo);
					} else if (LockerInfo.StyleTwelvePattern == styleId) {
						TwelvePatternLockerInfo twelvePatternLockerInfo = new TwelvePatternLockerInfo();
						twelvePatternLockerInfo.setStyleId(styleId);
						twelvePatternLockerInfo.setLineColor(lockerJsonObject.optInt("line_color"));
						twelvePatternLockerInfo.setDrawLine(lockerJsonObject.optBoolean("line_show"));
						twelvePatternLockerInfo.setWidth((int) mContext.getResources().getDimension(R.dimen.lock_patternview_width));
						twelvePatternLockerInfo.setHeight((int) mContext.getResources().getDimension(R.dimen.lock_patternview_width));
						twelvePatternLockerInfo.setX((screenWidth - twelvePatternLockerInfo.getWidth()) / 2);
						int y = lockerJsonObject.optInt("y");
						twelvePatternLockerInfo.setY(y * screenHeight / themeScreenHeight);
						JSONArray imageJsonArray = lockerJsonObject.optJSONArray("lockimage");
						if (imageJsonArray != null && imageJsonArray.length() > 0) {
							Bitmap[] bitmaps = new Bitmap[12];
							for (int i = 0; i < imageJsonArray.length(); i++) {
								String path = imageJsonArray.optString(i);
								if (!TextUtils.isEmpty(path)) {
									bitmaps[i] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path);
								}
							}
							twelvePatternLockerInfo.setBitmaps(bitmaps);
						}
						themeConfig.setLockerInfo(twelvePatternLockerInfo);
					} else if (LockerInfo.StyleCouple == styleId) {
						CoupleLockerInfo coupleLockerInfo = new CoupleLockerInfo();
						coupleLockerInfo.setStyleId(styleId);
						int y = lockerJsonObject.optInt("y");
						coupleLockerInfo.setY(y * screenHeight / themeScreenHeight);

						JSONArray imageJsonArray = lockerJsonObject.optJSONArray("lockimage");
						Bitmap[] bitmaps = new Bitmap[2];
						String path = imageJsonArray.optString(0);
						if (!TextUtils.isEmpty(path)) {
							bitmaps[0] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path);
						}
						String path2 = imageJsonArray.optString(1);
						if (!TextUtils.isEmpty(path2)) {
							bitmaps[1] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path2);
						}
						coupleLockerInfo.setBitmaps(bitmaps);
						themeConfig.setLockerInfo(coupleLockerInfo);
					}else if (LockerInfo.StyleApp == styleId) {
						AppLockerInfo coupleLockerInfo = new AppLockerInfo();
						coupleLockerInfo.setStyleId(styleId);
						int y = lockerJsonObject.optInt("y");
						coupleLockerInfo.setY(y * screenHeight / themeScreenHeight);
						coupleLockerInfo.setWidth(lockerJsonObject.optInt("width"));
						coupleLockerInfo.setHeight(lockerJsonObject.optInt("height"));
						coupleLockerInfo.action1=lockerJsonObject.optString("action1");
						coupleLockerInfo.action2=lockerJsonObject.optString("action2");
						coupleLockerInfo.action3=lockerJsonObject.optString("action3");

						JSONArray imageJsonArray = lockerJsonObject.optJSONArray("lockimage");
						Bitmap[] bitmaps = new Bitmap[4];
						String path = imageJsonArray.optString(0);
						if (!TextUtils.isEmpty(path)) {
							bitmaps[0] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path);
						}
						String path2 = imageJsonArray.optString(1);
						if (!TextUtils.isEmpty(path2)) {
							bitmaps[1] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path2);
						}
						String path3 = imageJsonArray.optString(2);
						if (!TextUtils.isEmpty(path3)) {
							bitmaps[2] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path3);
						}
						String path4 = imageJsonArray.optString(3);
						if (!TextUtils.isEmpty(path4)) {
							bitmaps[3] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path4);
						}
						coupleLockerInfo.setBitmaps(bitmaps);
						themeConfig.setLockerInfo(coupleLockerInfo);
					} else if (LockerInfo.StyleSlide == styleId) {
						SlideLockerInfo slideLockerInfo = new SlideLockerInfo();
						slideLockerInfo.setStyleId(styleId);
						int y = lockerJsonObject.optInt("y");
						slideLockerInfo.setY(y * screenHeight / themeScreenHeight);

						slideLockerInfo.setLeft1(lockerJsonObject.optInt("left1") * screenWidth / themeScreenWidth);
						slideLockerInfo.setTop1(lockerJsonObject.optInt("top1") * screenHeight / themeScreenHeight);
						slideLockerInfo.setRight1(lockerJsonObject.optInt("right1") * screenWidth / themeScreenWidth);
						slideLockerInfo.setBottom1(lockerJsonObject.optInt("bottom1") * screenHeight / themeScreenHeight);
						slideLockerInfo.setLeft2(lockerJsonObject.optInt("left2") * screenWidth / themeScreenWidth);
						slideLockerInfo.setTop2(lockerJsonObject.optInt("top2") * screenHeight / themeScreenHeight);
						slideLockerInfo.setRight2(lockerJsonObject.optInt("right2") * screenWidth / themeScreenWidth);
						slideLockerInfo.setBottom2(lockerJsonObject.optInt("bottom2") * screenHeight / themeScreenHeight);
						slideLockerInfo.setFirst(lockerJsonObject.optBoolean("first"));
						slideLockerInfo.setBitmapRes(lockerJsonObject.optInt("bitmapRes"));

						JSONArray imageJsonArray = lockerJsonObject.optJSONArray("lockimage");
						Bitmap[] bitmaps = new Bitmap[2];
						String path = imageJsonArray.optString(0);
						if (!TextUtils.isEmpty(path)) {
							bitmaps[0] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path);
						}
						String path2 = imageJsonArray.optString(1);
						if (!TextUtils.isEmpty(path2)) {
							bitmaps[1] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path2);
						}
						slideLockerInfo.setBitmaps(bitmaps);
						themeConfig.setLockerInfo(slideLockerInfo);
					} else if (LockerInfo.StyleImagePassword == styleId) {
						ImagePasswordLockerInfo imagePasswordLockerInfo = new ImagePasswordLockerInfo();
						imagePasswordLockerInfo.setStyleId(styleId);
						imagePasswordLockerInfo.setWidth((int) mContext.getResources().getDimension(R.dimen.lock_patternview_width));
						imagePasswordLockerInfo.setHeight((int) mContext.getResources().getDimension(R.dimen.lock_patternview_width));
						imagePasswordLockerInfo.setX((screenWidth - imagePasswordLockerInfo.getWidth()) / 2);
						int y = lockerJsonObject.optInt("y");
						imagePasswordLockerInfo.setY(y * screenHeight / themeScreenHeight);

						JSONArray imageJsonArray = lockerJsonObject.optJSONArray("lockimage");
						if (imageJsonArray != null && imageJsonArray.length() > 0) {
							Bitmap[] bitmaps = new Bitmap[12];
							for (int i = 0; i < imageJsonArray.length(); i++) {
								String path = imageJsonArray.optString(i);
								if (!TextUtils.isEmpty(path)) {
									bitmaps[i] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path);
								}
							}
							imagePasswordLockerInfo.setBitmaps(bitmaps);
						}
						themeConfig.setLockerInfo(imagePasswordLockerInfo);
					} else if (LockerInfo.StyleWordPassword == styleId) {
						WordPasswordLockerInfo wordPasswordLockerInfo = new WordPasswordLockerInfo();
						wordPasswordLockerInfo.setStyleId(styleId);
						wordPasswordLockerInfo.setWidth((int) mContext.getResources().getDimension(R.dimen.word_lock_width));
						wordPasswordLockerInfo.setHeight((int) mContext.getResources().getDimension(R.dimen.word_lock_height));
						wordPasswordLockerInfo.setX((screenWidth - wordPasswordLockerInfo.getWidth()) / 2);
						int y = lockerJsonObject.optInt("y");
						wordPasswordLockerInfo.setY(y * screenHeight / themeScreenHeight);

						wordPasswordLockerInfo.setAlpha(lockerJsonObject.optInt("alpha"));
						wordPasswordLockerInfo.setShadowColor(lockerJsonObject.optInt("shadowColor"));
						wordPasswordLockerInfo.setTextColor(lockerJsonObject.optInt("textColor"));
						wordPasswordLockerInfo.setShapeName(lockerJsonObject.optString("shapeName"));
						wordPasswordLockerInfo.setTextScale((float) lockerJsonObject.optDouble("textScale"));
						wordPasswordLockerInfo.setShapeScale((float) lockerJsonObject.optDouble("shapeScale"));
						String fontName = lockerJsonObject.optString("font");
						if (!TextUtils.isEmpty(fontName)) {
							File fontFile = new File(MConstants.TTF_PATH, fontName);
							if (fontFile.exists()) {
								wordPasswordLockerInfo.setFont(fontFile.getAbsolutePath());
							}
						}
						JSONArray wordsJsonArray = lockerJsonObject.optJSONArray("words");
						if (wordsJsonArray != null && wordsJsonArray.length() > 0) {
							String[] words = new String[12];
							for (int i = 0; i < wordsJsonArray.length(); i++) {
								String word = wordsJsonArray.optString(i);
								if (!TextUtils.isEmpty(word)) {
									words[i] = word;
								} else {
									words[i] = "";
								}
							}
							wordPasswordLockerInfo.setWords(words);
						}
						themeConfig.setLockerInfo(wordPasswordLockerInfo);
					}else if (LockerInfo.StyleNumPassword == styleId) {
						NumPasswordLockerInfo numPasswordLockerInfo = new NumPasswordLockerInfo();
						numPasswordLockerInfo.setStyleId(styleId);
						numPasswordLockerInfo.setWidth((int) mContext.getResources().getDimension(R.dimen.word_lock_width));
						numPasswordLockerInfo.setHeight((int) mContext.getResources().getDimension(R.dimen.word_lock_height));
						numPasswordLockerInfo.setX((screenWidth - numPasswordLockerInfo.getWidth()) / 2);
						int y = lockerJsonObject.optInt("y");
						numPasswordLockerInfo.setY(y * screenHeight / themeScreenHeight);

						numPasswordLockerInfo.setAlpha(lockerJsonObject.optInt("alpha"));
						numPasswordLockerInfo.setShadowColor(lockerJsonObject.optInt("shadowColor"));
						numPasswordLockerInfo.setTextColor(lockerJsonObject.optInt("textColor"));
						numPasswordLockerInfo.setShapeName(lockerJsonObject.optString("shapeName"));
						numPasswordLockerInfo.setTextScale((float) lockerJsonObject.optDouble("textScale"));
						numPasswordLockerInfo.setShapeScale((float) lockerJsonObject.optDouble("shapeScale"));
						String fontName = lockerJsonObject.optString("font");
						if (!TextUtils.isEmpty(fontName)) {
							File fontFile = new File(MConstants.TTF_PATH, fontName);
							if (fontFile.exists()) {
								numPasswordLockerInfo.setFont(fontFile.getAbsolutePath());
							}
						}
						JSONArray wordsJsonArray = lockerJsonObject.optJSONArray("words");
						if (wordsJsonArray != null && wordsJsonArray.length() > 0) {
							String[] words = new String[12];
							for (int i = 0; i < wordsJsonArray.length(); i++) {
								String word = wordsJsonArray.optString(i);
								if (!TextUtils.isEmpty(word)) {
									words[i] = word;
								} else {
									words[i] = "";
								}
							}
							numPasswordLockerInfo.setWords(words);
						}
						themeConfig.setLockerInfo(numPasswordLockerInfo);
					}
				}
				JSONArray stickerJsonArray = jsonObject.optJSONArray("stickers");
				if (stickerJsonArray != null && stickerJsonArray.length() > 0) {
					ArrayList<StickerInfo> stickerInfos = new ArrayList<StickerInfo>();
					for (int i = 0; i < stickerJsonArray.length(); i++) {
						JSONObject stickerJsonObject = stickerJsonArray.optJSONObject(i);
						if (stickerJsonObject != null) {
							int styleId = stickerJsonObject.optInt("type");
							if (StickerInfo.StyleImage == styleId) {
								ImageStickerInfo imageStickerInfo = new ImageStickerInfo();
								imageStickerInfo.styleId = styleId;
								imageStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								imageStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								imageStickerInfo.width = stickerJsonObject.optInt("width") * screenWidth / themeScreenWidth;
								imageStickerInfo.height = stickerJsonObject.optInt("height") * screenHeight / themeScreenHeight;
								imageStickerInfo.alpha = stickerJsonObject.optInt("alpha");
								imageStickerInfo.angle = stickerJsonObject.optInt("angle");
								imageStickerInfo.imagePath = folderPath + "/" + stickerJsonObject.optString("image");
								imageStickerInfo.imageBitmap = DrawableUtils.getBitmap(mContext, imageStickerInfo.imagePath);
								if (imageStickerInfo.imageBitmap != null) {
									stickerInfos.add(imageStickerInfo);
								}
								continue;
							}

							if (StickerInfo.StyleWord == styleId) {
								WordStickerInfo wordStickerInfo = new WordStickerInfo();
								wordStickerInfo.styleId = styleId;
								wordStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								wordStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								wordStickerInfo.angle = stickerJsonObject.optInt("angle");
								wordStickerInfo.textSize = stickerJsonObject.optInt("textSize") * screenWidth / themeScreenWidth;
								wordStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								wordStickerInfo.shadowColor = stickerJsonObject.optInt("shadowColor");
								if (stickerJsonObject.has("alpha")) {
									wordStickerInfo.alpha = stickerJsonObject.optInt("alpha");
								}
								String fontName = stickerJsonObject.optString("font");
								if (!TextUtils.isEmpty(fontName)) {
									File fontFile = new File(MConstants.TTF_PATH, fontName);
									if (fontFile.exists()) {
										wordStickerInfo.font = fontFile.getAbsolutePath();
									}
								}
								wordStickerInfo.text = stickerJsonObject.optString("text");
								wordStickerInfo.gravity = stickerJsonObject.optInt("gravity");
								stickerInfos.add(wordStickerInfo);
								continue;
							}
							if (StickerInfo.StyleTime == styleId) {
								TimeStickerInfo timeStickerInfo = new TimeStickerInfo();
								timeStickerInfo.styleId = styleId;
								timeStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								timeStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								timeStickerInfo.angle = stickerJsonObject.optInt("angle");
								if(stickerJsonObject.has("alpha")){
									timeStickerInfo.alpha = stickerJsonObject.optInt("alpha");
								}
								timeStickerInfo.timeStyle = stickerJsonObject.optInt("timeStyle");
								timeStickerInfo.textSize1 = stickerJsonObject.optInt("textSize1") * screenWidth / themeScreenWidth;
								timeStickerInfo.textSize2 = stickerJsonObject.optInt("textSize2") * screenWidth / themeScreenWidth;
								timeStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								timeStickerInfo.shadowColor = stickerJsonObject.optInt("shadowColor");
								String fontName = stickerJsonObject.optString("font");
								if (!TextUtils.isEmpty(fontName)) {
									File fontFile = new File(MConstants.TTF_PATH, fontName);
									if (fontFile.exists()) {
										timeStickerInfo.font = fontFile.getAbsolutePath();
									}
								}
								stickerInfos.add(timeStickerInfo);
								continue;
							}
							if (StickerInfo.StyleBattery == styleId) {
								BatteryStickerInfo batteryStickerInfo = new BatteryStickerInfo();
								batteryStickerInfo.styleId = styleId;
								batteryStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								batteryStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								batteryStickerInfo.angle = stickerJsonObject.optInt("angle");
								batteryStickerInfo.batteryStyle = stickerJsonObject.optInt("batteryStyle");
								batteryStickerInfo.scale = (float) stickerJsonObject.optDouble("scale");
								batteryStickerInfo.textSize = stickerJsonObject.optInt("textSize") * screenWidth / themeScreenWidth;
								batteryStickerInfo.width = DensityUtil.dip2px(mContext, 60);
								batteryStickerInfo.height = DensityUtil.dip2px(mContext, 60);
								batteryStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								stickerInfos.add(batteryStickerInfo);
								continue;
							}
							if (StickerInfo.StyleFlashlight == styleId) {
								FlashlightStickerInfo flashlightStickerInfo = new FlashlightStickerInfo();
								flashlightStickerInfo.styleId = styleId;
								flashlightStickerInfo.isClick = true;
								flashlightStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								flashlightStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								flashlightStickerInfo.angle = stickerJsonObject.optInt("angle");
								flashlightStickerInfo.scale = (float) stickerJsonObject.optDouble("scale");
								flashlightStickerInfo.textSize = stickerJsonObject.optInt("textSize") * screenWidth / themeScreenWidth;
								flashlightStickerInfo.width = DensityUtil.dip2px(mContext, 60);
								flashlightStickerInfo.height = DensityUtil.dip2px(mContext, 60);
								flashlightStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								stickerInfos.add(flashlightStickerInfo);
								continue;
							}
							if (StickerInfo.StyleCamera == styleId) {
								CameraStickerInfo cameraStickerInfo = new CameraStickerInfo();
								cameraStickerInfo.styleId = styleId;
								cameraStickerInfo.isClick = true;
								cameraStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								cameraStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								cameraStickerInfo.angle = stickerJsonObject.optInt("angle");
								cameraStickerInfo.scale = (float) stickerJsonObject.optDouble("scale");
								cameraStickerInfo.textSize = stickerJsonObject.optInt("textSize") * screenWidth / themeScreenWidth;
								cameraStickerInfo.width = DensityUtil.dip2px(mContext, 60);
								cameraStickerInfo.height = DensityUtil.dip2px(mContext, 60);
								cameraStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								stickerInfos.add(cameraStickerInfo);
								continue;
							}
							if (StickerInfo.StyleWeather == styleId) {
								WeatherStickerInfo weatherStickerInfo = new WeatherStickerInfo();
								weatherStickerInfo.styleId = styleId;
								weatherStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								weatherStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								weatherStickerInfo.angle = stickerJsonObject.optInt("angle");
								if(stickerJsonObject.has("alpha")){
									weatherStickerInfo.alpha = stickerJsonObject.optInt("alpha");
								}
								weatherStickerInfo.weatherStyle = stickerJsonObject.optInt("weatherStyle");
								weatherStickerInfo.text = stickerJsonObject.optString("text");
								weatherStickerInfo.textSize1 = stickerJsonObject.optInt("textSize1") * screenWidth / themeScreenWidth;
								weatherStickerInfo.textSize2 = stickerJsonObject.optInt("textSize2") * screenWidth / themeScreenWidth;
								weatherStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								weatherStickerInfo.shadowColor = stickerJsonObject.optInt("shadowColor");
								String fontName = stickerJsonObject.optString("font");
								if (!TextUtils.isEmpty(fontName)) {
									File fontFile = new File(MConstants.TTF_PATH, fontName);
									if (fontFile.exists()) {
										weatherStickerInfo.font = fontFile.getAbsolutePath();
									}
								}
								stickerInfos.add(weatherStickerInfo);
								continue;
							}
							if (StickerInfo.StyleTimer == styleId) {
								TimerStickerInfo timerStickerInfo = new TimerStickerInfo();
								timerStickerInfo.styleId = styleId;
								timerStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								timerStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								timerStickerInfo.angle = stickerJsonObject.optInt("angle");
								timerStickerInfo.text_title = stickerJsonObject.optString("text_title");
								timerStickerInfo.text_time = stickerJsonObject.optString("text_time");
								timerStickerInfo.textSize1 = stickerJsonObject.optInt("textSize1") * screenWidth / themeScreenWidth;
								timerStickerInfo.textSize2 = stickerJsonObject.optInt("textSize2") * screenWidth / themeScreenWidth;
								timerStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								timerStickerInfo.shadowColor = stickerJsonObject.optInt("shadowColor");
								timerStickerInfo.timerFlag = stickerJsonObject.optBoolean("timerFlag");
								if (stickerJsonObject.has("alpha")) {
									timerStickerInfo.alpha = stickerJsonObject.optInt("alpha");
								}
								String fontName = stickerJsonObject.optString("font");
								if (!TextUtils.isEmpty(fontName)) {
									File fontFile = new File(MConstants.TTF_PATH, fontName);
									if (fontFile.exists()) {
										timerStickerInfo.font = fontFile.getAbsolutePath();
									}
								}
								stickerInfos.add(timerStickerInfo);
								continue;
							}
							if (StickerInfo.StyleStatusbar == styleId) {
								StatusbarStickerInfo statusbarStickerInfo = new StatusbarStickerInfo();
								statusbarStickerInfo.styleId = styleId;
								statusbarStickerInfo.x = stickerJsonObject.optInt("x");
								statusbarStickerInfo.y = stickerJsonObject.optInt("y");
								statusbarStickerInfo.angle = stickerJsonObject.optInt("angle");
								statusbarStickerInfo.textSize = (int) mContext.getResources().getDimension(R.dimen.statusbar_textsize);
								statusbarStickerInfo.textRes = stickerJsonObject.optInt("textRes");
								statusbarStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								statusbarStickerInfo.shadowColor = stickerJsonObject.optInt("shadowColor");
								String fontName = stickerJsonObject.optString("font");
								if (!TextUtils.isEmpty(fontName)) {
									File fontFile = new File(MConstants.TTF_PATH, fontName);
									if (fontFile.exists()) {
										statusbarStickerInfo.font = fontFile.getAbsolutePath();
									}
								}
								statusbarStickerInfo.text = stickerJsonObject.optString("text");
								stickerInfos.add(statusbarStickerInfo);
								continue;
							}

							if (StickerInfo.StylePhone == styleId) {
								PhoneStickerInfo phoneStickerInfo = new PhoneStickerInfo();
								phoneStickerInfo.styleId = styleId;
								phoneStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								phoneStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								phoneStickerInfo.angle = stickerJsonObject.optInt("angle");
								phoneStickerInfo.width = DensityUtil.dip2px(mContext, 60);
								phoneStickerInfo.height = DensityUtil.dip2px(mContext, 60);
								phoneStickerInfo.textSize = DensityUtil.dip2px(mContext, 20);
								phoneStickerInfo.scale = (float) stickerJsonObject.optDouble("scale");
								phoneStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								stickerInfos.add(phoneStickerInfo);
								continue;
							}
							if (StickerInfo.StyleSMS == styleId) {
								SMSStickerInfo smsStickerInfo = new SMSStickerInfo();
								smsStickerInfo.styleId = styleId;
								smsStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								smsStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								smsStickerInfo.angle = stickerJsonObject.optInt("angle");
								smsStickerInfo.width = DensityUtil.dip2px(mContext, 60);
								smsStickerInfo.height = DensityUtil.dip2px(mContext, 60);
								smsStickerInfo.scale = (float) stickerJsonObject.optDouble("scale");
								smsStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								stickerInfos.add(smsStickerInfo);
								continue;
							}

							if (StickerInfo.StyleDayWord == styleId) {
								DayWordStickerInfo dayWordStickerInfo = new DayWordStickerInfo();
								dayWordStickerInfo.styleId = styleId;
								dayWordStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								dayWordStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								dayWordStickerInfo.angle = stickerJsonObject.optInt("angle");
								dayWordStickerInfo.textSize = stickerJsonObject.optInt("textSize") * screenWidth / themeScreenWidth;
								dayWordStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								dayWordStickerInfo.shadowColor = stickerJsonObject.optInt("shadowColor");
								dayWordStickerInfo.text = LockApplication.getInstance().getConfig().getDailyWords();
								String fontName = stickerJsonObject.optString("font");
								if (!TextUtils.isEmpty(fontName)) {
									File fontFile = new File(MConstants.TTF_PATH, fontName);
									if (fontFile.exists()) {
										dayWordStickerInfo.font = fontFile.getAbsolutePath();
									}
								}
								dayWordStickerInfo.orientation = stickerJsonObject.optInt("orientation");
								dayWordStickerInfo.gravity = stickerJsonObject.optInt("gravity");
								stickerInfos.add(dayWordStickerInfo);
								continue;
							}

							if (StickerInfo.StyleHollowWords == styleId) {
								HollowWordsStickerInfo hollowWordsStickerInfo = new HollowWordsStickerInfo();
								hollowWordsStickerInfo.styleId = styleId;
								hollowWordsStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								hollowWordsStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								hollowWordsStickerInfo.upText = stickerJsonObject.optString("upText");
								hollowWordsStickerInfo.downText = stickerJsonObject.optString("downText");
								hollowWordsStickerInfo.upTextSize = stickerJsonObject.optInt("upTextSize");
								hollowWordsStickerInfo.downTextSize = stickerJsonObject.optInt("downTextSize");
								hollowWordsStickerInfo.upTextColor = stickerJsonObject.optInt("upTextColor");
								hollowWordsStickerInfo.downTextColor = stickerJsonObject.optInt("downTextColor");
								hollowWordsStickerInfo.upShadowColor = stickerJsonObject.optInt("upShadowColor");
								hollowWordsStickerInfo.downShadowColor = stickerJsonObject.optInt("downShadowColor");
								hollowWordsStickerInfo.upAlpha = stickerJsonObject.optInt("upAlpha");
								hollowWordsStickerInfo.downAlpha = stickerJsonObject.optInt("downAlpha");
								hollowWordsStickerInfo.offset = (float) stickerJsonObject.optDouble("offset");
								String upFontName = stickerJsonObject.optString("upFont");
								if (!TextUtils.isEmpty(upFontName)) {
									File fontFile = new File(MConstants.TTF_PATH, upFontName);
									if (fontFile.exists()) {
										hollowWordsStickerInfo.upFont = fontFile.getAbsolutePath();
									}
								}

								String downFontName = stickerJsonObject.optString("downFont");
								if (!TextUtils.isEmpty(downFontName)) {
									File fontFile = new File(MConstants.TTF_PATH, downFontName);
									if (fontFile.exists()) {
										hollowWordsStickerInfo.downFont = fontFile.getAbsolutePath();
									}
								}

								stickerInfos.add(hollowWordsStickerInfo);
								continue;
							}
						}
					}

					themeConfig.setStickerInfoList(stickerInfos);
				}

				return themeConfig;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ThemeConfig parseConfigTest(Context mContext, String configFile) {
		String configJson = configFile;
		String folderPath = new File(configFile).getParent();

		try {
			JSONObject jsonObject = new JSONObject(configJson);
			if (jsonObject != null) {
				ThemeConfig themeConfig = new ThemeConfig();
				themeConfig.setScreenWidth(jsonObject.optInt("screen_width"));
				themeConfig.setScreenHeight(jsonObject.optInt("screen_height"));
				int themeScreenWidth = themeConfig.getScreenWidth();
				int themeScreenHeight = themeConfig.getScreenHeight();
				int screenHeight = LockApplication.getInstance().getConfig().getScreenHeight();
				int screenWidth = LockApplication.getInstance().getConfig().getScreenWidth();
				int wallpaperColor = jsonObject.optInt("wallpaper_color");
				if (wallpaperColor != 0) {
					themeConfig.setWallpaperColor(wallpaperColor);
				}
				if (new File(folderPath, "wallpaper").exists()) {
					themeConfig.setWallpaper(new File(folderPath, "wallpaper").getAbsolutePath());
				}

				JSONObject lockerJsonObject = jsonObject.optJSONObject("lock");

				if (lockerJsonObject != null) {
					int styleId = lockerJsonObject.optInt("type");
					if (LockerInfo.StyleNone == styleId) {
						LockerInfo lockerInfo = new LockerInfo();
						lockerInfo.setStyleId(LockerInfo.StyleNone);
						themeConfig.setLockerInfo(lockerInfo);
					} else if (LockerInfo.StyleFree == styleId) {
						LockerInfo lockerInfo = new LockerInfo();
						lockerInfo.setStyleId(LockerInfo.StyleFree);
						themeConfig.setLockerInfo(lockerInfo);
					} else if (LockerInfo.StyleLove == styleId) {
						LoveLockerInfo loveLockerInfo = new LoveLockerInfo();
						loveLockerInfo.setStyleId(styleId);
						loveLockerInfo.setWidth((int) mContext.getResources().getDimension(R.dimen.lock_patternview_width));
						loveLockerInfo.setHeight(loveLockerInfo.getWidth());
						loveLockerInfo.setX((screenWidth - loveLockerInfo.getWidth()) / 2);
						int y = lockerJsonObject.optInt("y");
						loveLockerInfo.setY(y * screenHeight / themeScreenHeight);

						JSONArray imageJsonArray = lockerJsonObject.optJSONArray("lockimage");
						if (imageJsonArray != null && imageJsonArray.length() > 0) {
							Bitmap[] bitmaps = new Bitmap[10];
							for (int i = 0; i < imageJsonArray.length(); i++) {
								String path = imageJsonArray.optString(i);
								if (!TextUtils.isEmpty(path)) {
									bitmaps[i] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path);
								}
							}
							loveLockerInfo.setBitmaps(bitmaps);
						}
						themeConfig.setLockerInfo(loveLockerInfo);
					} else if (LockerInfo.StyleNinePattern == styleId) {
						NinePatternLockerInfo ninePatternLockerInfo = new NinePatternLockerInfo();
						ninePatternLockerInfo.setStyleId(styleId);
						ninePatternLockerInfo.setLineColor(lockerJsonObject.optInt("line_color"));
						ninePatternLockerInfo.setDrawLine(lockerJsonObject.optBoolean("line_show"));
						ninePatternLockerInfo.setWidth((int) mContext.getResources().getDimension(R.dimen.lock_patternview_width));
						ninePatternLockerInfo.setHeight(ninePatternLockerInfo.getWidth());
						ninePatternLockerInfo.setX((screenWidth - ninePatternLockerInfo.getWidth()) / 2);
						int y = lockerJsonObject.optInt("y");
						ninePatternLockerInfo.setY(y * screenHeight / themeScreenHeight);
						String path = lockerJsonObject.optString("lockimage");

						if (!TextUtils.isEmpty(path)) {
							ninePatternLockerInfo.setBitmap(DrawableUtils.getBitmap(mContext, folderPath + "/" + path));
						}
						themeConfig.setLockerInfo(ninePatternLockerInfo);
					} else if (LockerInfo.StyleTwelvePattern == styleId) {
						TwelvePatternLockerInfo twelvePatternLockerInfo = new TwelvePatternLockerInfo();
						twelvePatternLockerInfo.setStyleId(styleId);
						twelvePatternLockerInfo.setLineColor(lockerJsonObject.optInt("line_color"));
						twelvePatternLockerInfo.setDrawLine(lockerJsonObject.optBoolean("line_show"));
						twelvePatternLockerInfo.setWidth((int) mContext.getResources().getDimension(R.dimen.lock_patternview_width));
						twelvePatternLockerInfo.setHeight((int) mContext.getResources().getDimension(R.dimen.lock_patternview_width));
						twelvePatternLockerInfo.setX((screenWidth - twelvePatternLockerInfo.getWidth()) / 2);
						int y = lockerJsonObject.optInt("y");
						twelvePatternLockerInfo.setY(y * screenHeight / themeScreenHeight);
						JSONArray imageJsonArray = lockerJsonObject.optJSONArray("lockimage");
						if (imageJsonArray != null && imageJsonArray.length() > 0) {
							Bitmap[] bitmaps = new Bitmap[12];
							for (int i = 0; i < imageJsonArray.length(); i++) {
								String path = imageJsonArray.optString(i);
								if (!TextUtils.isEmpty(path)) {
									bitmaps[i] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path);
								}
							}
							twelvePatternLockerInfo.setBitmaps(bitmaps);
						}
						themeConfig.setLockerInfo(twelvePatternLockerInfo);
					} else if (LockerInfo.StyleCouple == styleId) {
						CoupleLockerInfo coupleLockerInfo = new CoupleLockerInfo();
						coupleLockerInfo.setStyleId(styleId);
						int y = lockerJsonObject.optInt("y");
						coupleLockerInfo.setY(y * screenHeight / themeScreenHeight);

						JSONArray imageJsonArray = lockerJsonObject.optJSONArray("lockimage");
						Bitmap[] bitmaps = new Bitmap[2];
						String path = imageJsonArray.optString(0);
						if (!TextUtils.isEmpty(path)) {
							bitmaps[0] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path);
						}
						String path2 = imageJsonArray.optString(1);
						if (!TextUtils.isEmpty(path2)) {
							bitmaps[1] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path2);
						}
						coupleLockerInfo.setBitmaps(bitmaps);
						themeConfig.setLockerInfo(coupleLockerInfo);
					}else if (LockerInfo.StyleApp == styleId) {
						AppLockerInfo coupleLockerInfo = new AppLockerInfo();
						coupleLockerInfo.setStyleId(styleId);
						int y = lockerJsonObject.optInt("y");
						coupleLockerInfo.setY(y * screenHeight / themeScreenHeight);
						coupleLockerInfo.setWidth(lockerJsonObject.optInt("width"));
						coupleLockerInfo.setHeight(lockerJsonObject.optInt("height"));
						coupleLockerInfo.action1=lockerJsonObject.optString("action1");
						coupleLockerInfo.action2=lockerJsonObject.optString("action2");
						coupleLockerInfo.action3=lockerJsonObject.optString("action3");

						JSONArray imageJsonArray = lockerJsonObject.optJSONArray("lockimage");
						Bitmap[] bitmaps = new Bitmap[4];
						String path = imageJsonArray.optString(0);
						if (!TextUtils.isEmpty(path)) {
							bitmaps[0] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path);
						}
						String path2 = imageJsonArray.optString(1);
						if (!TextUtils.isEmpty(path2)) {
							bitmaps[1] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path2);
						}
						String path3 = imageJsonArray.optString(2);
						if (!TextUtils.isEmpty(path3)) {
							bitmaps[2] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path3);
						}
						String path4 = imageJsonArray.optString(3);
						if (!TextUtils.isEmpty(path4)) {
							bitmaps[3] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path4);
						}
						coupleLockerInfo.setBitmaps(bitmaps);
						themeConfig.setLockerInfo(coupleLockerInfo);
					} else if (LockerInfo.StyleSlide == styleId) {
						SlideLockerInfo slideLockerInfo = new SlideLockerInfo();
						slideLockerInfo.setStyleId(styleId);
						int y = lockerJsonObject.optInt("y");
						slideLockerInfo.setY(y * screenHeight / themeScreenHeight);

						slideLockerInfo.setLeft1(lockerJsonObject.optInt("left1") * screenWidth / themeScreenWidth);
						slideLockerInfo.setTop1(lockerJsonObject.optInt("top1") * screenHeight / themeScreenHeight);
						slideLockerInfo.setRight1(lockerJsonObject.optInt("right1") * screenWidth / themeScreenWidth);
						slideLockerInfo.setBottom1(lockerJsonObject.optInt("bottom1") * screenHeight / themeScreenHeight);
						slideLockerInfo.setLeft2(lockerJsonObject.optInt("left2") * screenWidth / themeScreenWidth);
						slideLockerInfo.setTop2(lockerJsonObject.optInt("top2") * screenHeight / themeScreenHeight);
						slideLockerInfo.setRight2(lockerJsonObject.optInt("right2") * screenWidth / themeScreenWidth);
						slideLockerInfo.setBottom2(lockerJsonObject.optInt("bottom2") * screenHeight / themeScreenHeight);
						slideLockerInfo.setFirst(lockerJsonObject.optBoolean("first"));
						slideLockerInfo.setBitmapRes(lockerJsonObject.optInt("bitmapRes"));

						JSONArray imageJsonArray = lockerJsonObject.optJSONArray("lockimage");
						Bitmap[] bitmaps = new Bitmap[2];
						String path = imageJsonArray.optString(0);
						if (!TextUtils.isEmpty(path)) {
							bitmaps[0] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path);
						}
						String path2 = imageJsonArray.optString(1);
						if (!TextUtils.isEmpty(path2)) {
							bitmaps[1] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path2);
						}
						slideLockerInfo.setBitmaps(bitmaps);
						themeConfig.setLockerInfo(slideLockerInfo);
					} else if (LockerInfo.StyleImagePassword == styleId) {
						ImagePasswordLockerInfo imagePasswordLockerInfo = new ImagePasswordLockerInfo();
						imagePasswordLockerInfo.setStyleId(styleId);
						imagePasswordLockerInfo.setWidth((int) mContext.getResources().getDimension(R.dimen.lock_patternview_width));
						imagePasswordLockerInfo.setHeight((int) mContext.getResources().getDimension(R.dimen.lock_patternview_width));
						imagePasswordLockerInfo.setX((screenWidth - imagePasswordLockerInfo.getWidth()) / 2);
						int y = lockerJsonObject.optInt("y");
						imagePasswordLockerInfo.setY(y * screenHeight / themeScreenHeight);

						JSONArray imageJsonArray = lockerJsonObject.optJSONArray("lockimage");
						if (imageJsonArray != null && imageJsonArray.length() > 0) {
							Bitmap[] bitmaps = new Bitmap[12];
							for (int i = 0; i < imageJsonArray.length(); i++) {
								String path = imageJsonArray.optString(i);
								if (!TextUtils.isEmpty(path)) {
									bitmaps[i] = DrawableUtils.getBitmap(mContext, folderPath + "/" + path);
								}
							}
							imagePasswordLockerInfo.setBitmaps(bitmaps);
						}
						themeConfig.setLockerInfo(imagePasswordLockerInfo);
					} else if (LockerInfo.StyleWordPassword == styleId) {
						WordPasswordLockerInfo wordPasswordLockerInfo = new WordPasswordLockerInfo();
						wordPasswordLockerInfo.setStyleId(styleId);
						wordPasswordLockerInfo.setWidth((int) mContext.getResources().getDimension(R.dimen.word_lock_width));
						wordPasswordLockerInfo.setHeight((int) mContext.getResources().getDimension(R.dimen.word_lock_height));
						wordPasswordLockerInfo.setX((screenWidth - wordPasswordLockerInfo.getWidth()) / 2);
						int y = lockerJsonObject.optInt("y");
						wordPasswordLockerInfo.setY(y * screenHeight / themeScreenHeight);

						wordPasswordLockerInfo.setAlpha(lockerJsonObject.optInt("alpha"));
						wordPasswordLockerInfo.setShadowColor(lockerJsonObject.optInt("shadowColor"));
						wordPasswordLockerInfo.setTextColor(lockerJsonObject.optInt("textColor"));
						wordPasswordLockerInfo.setShapeName(lockerJsonObject.optString("shapeName"));
						wordPasswordLockerInfo.setTextScale((float) lockerJsonObject.optDouble("textScale"));
						wordPasswordLockerInfo.setShapeScale((float) lockerJsonObject.optDouble("shapeScale"));
						String fontName = lockerJsonObject.optString("font");
						if (!TextUtils.isEmpty(fontName)) {
							File fontFile = new File(MConstants.TTF_PATH, fontName);
							if (fontFile.exists()) {
								wordPasswordLockerInfo.setFont(fontFile.getAbsolutePath());
							}
						}
						JSONArray wordsJsonArray = lockerJsonObject.optJSONArray("words");
						if (wordsJsonArray != null && wordsJsonArray.length() > 0) {
							String[] words = new String[12];
							for (int i = 0; i < wordsJsonArray.length(); i++) {
								String word = wordsJsonArray.optString(i);
								if (!TextUtils.isEmpty(word)) {
									words[i] = word;
								} else {
									words[i] = "";
								}
							}
							wordPasswordLockerInfo.setWords(words);
						}
						themeConfig.setLockerInfo(wordPasswordLockerInfo);
					}else if (LockerInfo.StyleNumPassword == styleId) {
						NumPasswordLockerInfo numPasswordLockerInfo = new NumPasswordLockerInfo();
						numPasswordLockerInfo.setStyleId(styleId);
						numPasswordLockerInfo.setWidth((int) mContext.getResources().getDimension(R.dimen.word_lock_width));
						numPasswordLockerInfo.setHeight((int) mContext.getResources().getDimension(R.dimen.word_lock_height));
						numPasswordLockerInfo.setX((screenWidth - numPasswordLockerInfo.getWidth()) / 2);
						int y = lockerJsonObject.optInt("y");
						numPasswordLockerInfo.setY(y * screenHeight / themeScreenHeight);

						numPasswordLockerInfo.setAlpha(lockerJsonObject.optInt("alpha"));
						numPasswordLockerInfo.setShadowColor(lockerJsonObject.optInt("shadowColor"));
						numPasswordLockerInfo.setTextColor(lockerJsonObject.optInt("textColor"));
						numPasswordLockerInfo.setShapeName(lockerJsonObject.optString("shapeName"));
						numPasswordLockerInfo.setTextScale((float) lockerJsonObject.optDouble("textScale"));
						numPasswordLockerInfo.setShapeScale((float) lockerJsonObject.optDouble("shapeScale"));
						String fontName = lockerJsonObject.optString("font");
						if (!TextUtils.isEmpty(fontName)) {
							File fontFile = new File(MConstants.TTF_PATH, fontName);
							if (fontFile.exists()) {
								numPasswordLockerInfo.setFont(fontFile.getAbsolutePath());
							}
						}
						JSONArray wordsJsonArray = lockerJsonObject.optJSONArray("words");
						if (wordsJsonArray != null && wordsJsonArray.length() > 0) {
							String[] words = new String[12];
							for (int i = 0; i < wordsJsonArray.length(); i++) {
								String word = wordsJsonArray.optString(i);
								if (!TextUtils.isEmpty(word)) {
									words[i] = word;
								} else {
									words[i] = "";
								}
							}
							numPasswordLockerInfo.setWords(words);
						}
						themeConfig.setLockerInfo(numPasswordLockerInfo);
					}
				}
				JSONArray stickerJsonArray = jsonObject.optJSONArray("stickers");
				if (stickerJsonArray != null && stickerJsonArray.length() > 0) {
					ArrayList<StickerInfo> stickerInfos = new ArrayList<StickerInfo>();
					for (int i = 0; i < stickerJsonArray.length(); i++) {
						JSONObject stickerJsonObject = stickerJsonArray.optJSONObject(i);
						if (stickerJsonObject != null) {
							int styleId = stickerJsonObject.optInt("type");
							if (StickerInfo.StyleImage == styleId) {
								ImageStickerInfo imageStickerInfo = new ImageStickerInfo();
								imageStickerInfo.styleId = styleId;
								imageStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								imageStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								imageStickerInfo.width = stickerJsonObject.optInt("width") * screenWidth / themeScreenWidth;
								imageStickerInfo.height = stickerJsonObject.optInt("height") * screenHeight / themeScreenHeight;
								imageStickerInfo.alpha = stickerJsonObject.optInt("alpha");
								imageStickerInfo.angle = stickerJsonObject.optInt("angle");
								imageStickerInfo.imagePath = folderPath + "/" + stickerJsonObject.optString("image");
								imageStickerInfo.imageBitmap = DrawableUtils.getBitmap(mContext, imageStickerInfo.imagePath);
								if (imageStickerInfo.imageBitmap != null) {
									stickerInfos.add(imageStickerInfo);
								}
								continue;
							}

							if (StickerInfo.StyleWord == styleId) {
								WordStickerInfo wordStickerInfo = new WordStickerInfo();
								wordStickerInfo.styleId = styleId;
								wordStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								wordStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								wordStickerInfo.angle = stickerJsonObject.optInt("angle");
								wordStickerInfo.textSize = stickerJsonObject.optInt("textSize") * screenWidth / themeScreenWidth;
								wordStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								wordStickerInfo.shadowColor = stickerJsonObject.optInt("shadowColor");
								if (stickerJsonObject.has("alpha")) {
									wordStickerInfo.alpha = stickerJsonObject.optInt("alpha");
								}
								String fontName = stickerJsonObject.optString("font");
								if (!TextUtils.isEmpty(fontName)) {
									File fontFile = new File(MConstants.TTF_PATH, fontName);
									if (fontFile.exists()) {
										wordStickerInfo.font = fontFile.getAbsolutePath();
									}
								}
								wordStickerInfo.text = stickerJsonObject.optString("text");
								wordStickerInfo.gravity = stickerJsonObject.optInt("gravity");
								stickerInfos.add(wordStickerInfo);
								continue;
							}
							if (StickerInfo.StyleTime == styleId) {
								TimeStickerInfo timeStickerInfo = new TimeStickerInfo();
								timeStickerInfo.styleId = styleId;
								timeStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								timeStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								timeStickerInfo.angle = stickerJsonObject.optInt("angle");
								if(stickerJsonObject.has("alpha")){
									timeStickerInfo.alpha = stickerJsonObject.optInt("alpha");
								}
								timeStickerInfo.timeStyle = stickerJsonObject.optInt("timeStyle");
								timeStickerInfo.textSize1 = stickerJsonObject.optInt("textSize1") * screenWidth / themeScreenWidth;
								timeStickerInfo.textSize2 = stickerJsonObject.optInt("textSize2") * screenWidth / themeScreenWidth;
								timeStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								timeStickerInfo.shadowColor = stickerJsonObject.optInt("shadowColor");
								String fontName = stickerJsonObject.optString("font");
								if (!TextUtils.isEmpty(fontName)) {
									File fontFile = new File(MConstants.TTF_PATH, fontName);
									if (fontFile.exists()) {
										timeStickerInfo.font = fontFile.getAbsolutePath();
									}
								}
								stickerInfos.add(timeStickerInfo);
								continue;
							}
							if (StickerInfo.StyleBattery == styleId) {
								BatteryStickerInfo batteryStickerInfo = new BatteryStickerInfo();
								batteryStickerInfo.styleId = styleId;
								batteryStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								batteryStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								batteryStickerInfo.angle = stickerJsonObject.optInt("angle");
								batteryStickerInfo.batteryStyle = stickerJsonObject.optInt("batteryStyle");
								batteryStickerInfo.scale = (float) stickerJsonObject.optDouble("scale");
								batteryStickerInfo.textSize = stickerJsonObject.optInt("textSize") * screenWidth / themeScreenWidth;
								batteryStickerInfo.width = DensityUtil.dip2px(mContext, 60);
								batteryStickerInfo.height = DensityUtil.dip2px(mContext, 60);
								batteryStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								stickerInfos.add(batteryStickerInfo);
								continue;
							}
							if (StickerInfo.StyleFlashlight == styleId) {
								FlashlightStickerInfo flashlightStickerInfo = new FlashlightStickerInfo();
								flashlightStickerInfo.styleId = styleId;
								flashlightStickerInfo.isClick = true;
								flashlightStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								flashlightStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								flashlightStickerInfo.angle = stickerJsonObject.optInt("angle");
								flashlightStickerInfo.scale = (float) stickerJsonObject.optDouble("scale");
								flashlightStickerInfo.textSize = stickerJsonObject.optInt("textSize") * screenWidth / themeScreenWidth;
								flashlightStickerInfo.width = DensityUtil.dip2px(mContext, 60);
								flashlightStickerInfo.height = DensityUtil.dip2px(mContext, 60);
								flashlightStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								stickerInfos.add(flashlightStickerInfo);
								continue;
							}
							if (StickerInfo.StyleCamera == styleId) {
								CameraStickerInfo cameraStickerInfo = new CameraStickerInfo();
								cameraStickerInfo.styleId = styleId;
								cameraStickerInfo.isClick = true;
								cameraStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								cameraStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								cameraStickerInfo.angle = stickerJsonObject.optInt("angle");
								cameraStickerInfo.scale = (float) stickerJsonObject.optDouble("scale");
								cameraStickerInfo.textSize = stickerJsonObject.optInt("textSize") * screenWidth / themeScreenWidth;
								cameraStickerInfo.width = DensityUtil.dip2px(mContext, 60);
								cameraStickerInfo.height = DensityUtil.dip2px(mContext, 60);
								cameraStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								stickerInfos.add(cameraStickerInfo);
								continue;
							}
							if (StickerInfo.StyleWeather == styleId) {
								WeatherStickerInfo weatherStickerInfo = new WeatherStickerInfo();
								weatherStickerInfo.styleId = styleId;
								weatherStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								weatherStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								weatherStickerInfo.angle = stickerJsonObject.optInt("angle");
								if(stickerJsonObject.has("alpha")){
									weatherStickerInfo.alpha = stickerJsonObject.optInt("alpha");
								}
								weatherStickerInfo.weatherStyle = stickerJsonObject.optInt("weatherStyle");
								weatherStickerInfo.text = stickerJsonObject.optString("text");
								weatherStickerInfo.textSize1 = stickerJsonObject.optInt("textSize1") * screenWidth / themeScreenWidth;
								weatherStickerInfo.textSize2 = stickerJsonObject.optInt("textSize2") * screenWidth / themeScreenWidth;
								weatherStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								weatherStickerInfo.shadowColor = stickerJsonObject.optInt("shadowColor");
								String fontName = stickerJsonObject.optString("font");
								if (!TextUtils.isEmpty(fontName)) {
									File fontFile = new File(MConstants.TTF_PATH, fontName);
									if (fontFile.exists()) {
										weatherStickerInfo.font = fontFile.getAbsolutePath();
									}
								}
								stickerInfos.add(weatherStickerInfo);
								continue;
							}
							if (StickerInfo.StyleTimer == styleId) {
								TimerStickerInfo timerStickerInfo = new TimerStickerInfo();
								timerStickerInfo.styleId = styleId;
								timerStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								timerStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								timerStickerInfo.angle = stickerJsonObject.optInt("angle");
								timerStickerInfo.text_title = stickerJsonObject.optString("text_title");
								timerStickerInfo.text_time = stickerJsonObject.optString("text_time");
								timerStickerInfo.textSize1 = stickerJsonObject.optInt("textSize1") * screenWidth / themeScreenWidth;
								timerStickerInfo.textSize2 = stickerJsonObject.optInt("textSize2") * screenWidth / themeScreenWidth;
								timerStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								timerStickerInfo.shadowColor = stickerJsonObject.optInt("shadowColor");
								timerStickerInfo.timerFlag = stickerJsonObject.optBoolean("timerFlag");
								if (stickerJsonObject.has("alpha")) {
									timerStickerInfo.alpha = stickerJsonObject.optInt("alpha");
								}
								String fontName = stickerJsonObject.optString("font");
								if (!TextUtils.isEmpty(fontName)) {
									File fontFile = new File(MConstants.TTF_PATH, fontName);
									if (fontFile.exists()) {
										timerStickerInfo.font = fontFile.getAbsolutePath();
									}
								}
								stickerInfos.add(timerStickerInfo);
								continue;
							}
							if (StickerInfo.StyleStatusbar == styleId) {
								StatusbarStickerInfo statusbarStickerInfo = new StatusbarStickerInfo();
								statusbarStickerInfo.styleId = styleId;
								statusbarStickerInfo.x = stickerJsonObject.optInt("x");
								statusbarStickerInfo.y = stickerJsonObject.optInt("y");
								statusbarStickerInfo.angle = stickerJsonObject.optInt("angle");
								statusbarStickerInfo.textSize = (int) mContext.getResources().getDimension(R.dimen.statusbar_textsize);
								statusbarStickerInfo.textRes = stickerJsonObject.optInt("textRes");
								statusbarStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								statusbarStickerInfo.shadowColor = stickerJsonObject.optInt("shadowColor");
								String fontName = stickerJsonObject.optString("font");
								if (!TextUtils.isEmpty(fontName)) {
									File fontFile = new File(MConstants.TTF_PATH, fontName);
									if (fontFile.exists()) {
										statusbarStickerInfo.font = fontFile.getAbsolutePath();
									}
								}
								statusbarStickerInfo.text = stickerJsonObject.optString("text");
								stickerInfos.add(statusbarStickerInfo);
								continue;
							}

							if (StickerInfo.StylePhone == styleId) {
								PhoneStickerInfo phoneStickerInfo = new PhoneStickerInfo();
								phoneStickerInfo.styleId = styleId;
								phoneStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								phoneStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								phoneStickerInfo.angle = stickerJsonObject.optInt("angle");
								phoneStickerInfo.width = DensityUtil.dip2px(mContext, 60);
								phoneStickerInfo.height = DensityUtil.dip2px(mContext, 60);
								phoneStickerInfo.textSize = DensityUtil.dip2px(mContext, 20);
								phoneStickerInfo.scale = (float) stickerJsonObject.optDouble("scale");
								phoneStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								stickerInfos.add(phoneStickerInfo);
								continue;
							}
							if (StickerInfo.StyleSMS == styleId) {
								SMSStickerInfo smsStickerInfo = new SMSStickerInfo();
								smsStickerInfo.styleId = styleId;
								smsStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								smsStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								smsStickerInfo.angle = stickerJsonObject.optInt("angle");
								smsStickerInfo.width = DensityUtil.dip2px(mContext, 60);
								smsStickerInfo.height = DensityUtil.dip2px(mContext, 60);
								smsStickerInfo.scale = (float) stickerJsonObject.optDouble("scale");
								smsStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								stickerInfos.add(smsStickerInfo);
								continue;
							}

							if (StickerInfo.StyleDayWord == styleId) {
								DayWordStickerInfo dayWordStickerInfo = new DayWordStickerInfo();
								dayWordStickerInfo.styleId = styleId;
								dayWordStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								dayWordStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								dayWordStickerInfo.angle = stickerJsonObject.optInt("angle");
								dayWordStickerInfo.textSize = stickerJsonObject.optInt("textSize") * screenWidth / themeScreenWidth;
								dayWordStickerInfo.textColor = stickerJsonObject.optInt("textColor");
								dayWordStickerInfo.shadowColor = stickerJsonObject.optInt("shadowColor");
								dayWordStickerInfo.text = LockApplication.getInstance().getConfig().getDailyWords();
								String fontName = stickerJsonObject.optString("font");
								if (!TextUtils.isEmpty(fontName)) {
									File fontFile = new File(MConstants.TTF_PATH, fontName);
									if (fontFile.exists()) {
										dayWordStickerInfo.font = fontFile.getAbsolutePath();
									}
								}
								dayWordStickerInfo.orientation = stickerJsonObject.optInt("orientation");
								dayWordStickerInfo.gravity = stickerJsonObject.optInt("gravity");
								stickerInfos.add(dayWordStickerInfo);
								continue;
							}

							if (StickerInfo.StyleHollowWords == styleId) {
								HollowWordsStickerInfo hollowWordsStickerInfo = new HollowWordsStickerInfo();
								hollowWordsStickerInfo.styleId = styleId;
								hollowWordsStickerInfo.x = stickerJsonObject.optInt("x") * screenWidth / themeScreenWidth;
								hollowWordsStickerInfo.y = stickerJsonObject.optInt("y") * screenHeight / themeScreenHeight;
								hollowWordsStickerInfo.upText = stickerJsonObject.optString("upText");
								hollowWordsStickerInfo.downText = stickerJsonObject.optString("downText");
								hollowWordsStickerInfo.upTextSize = stickerJsonObject.optInt("upTextSize");
								hollowWordsStickerInfo.downTextSize = stickerJsonObject.optInt("downTextSize");
								hollowWordsStickerInfo.upTextColor = stickerJsonObject.optInt("upTextColor");
								hollowWordsStickerInfo.downTextColor = stickerJsonObject.optInt("downTextColor");
								hollowWordsStickerInfo.upShadowColor = stickerJsonObject.optInt("upShadowColor");
								hollowWordsStickerInfo.downShadowColor = stickerJsonObject.optInt("downShadowColor");
								hollowWordsStickerInfo.upAlpha = stickerJsonObject.optInt("upAlpha");
								hollowWordsStickerInfo.downAlpha = stickerJsonObject.optInt("downAlpha");
								hollowWordsStickerInfo.offset = (float) stickerJsonObject.optDouble("offset");
								String upFontName = stickerJsonObject.optString("upFont");
								if (!TextUtils.isEmpty(upFontName)) {
									File fontFile = new File(MConstants.TTF_PATH, upFontName);
									if (fontFile.exists()) {
										hollowWordsStickerInfo.upFont = fontFile.getAbsolutePath();
									}
								}

								String downFontName = stickerJsonObject.optString("downFont");
								if (!TextUtils.isEmpty(downFontName)) {
									File fontFile = new File(MConstants.TTF_PATH, downFontName);
									if (fontFile.exists()) {
										hollowWordsStickerInfo.downFont = fontFile.getAbsolutePath();
									}
								}

								stickerInfos.add(hollowWordsStickerInfo);
								continue;
							}
						}
					}

					themeConfig.setStickerInfoList(stickerInfos);
				}

				return themeConfig;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void createDefaultTheme(final Context mContext) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				String fileName1 = "en_one.ttf";
				AssetManager am = mContext.getAssets();
				if (!new File(MConstants.TTF_PATH + fileName1).exists()) {
					try {
						InputStream is1 = am.open(fileName1);
						FileUtils.copyFile(is1, new File(MConstants.TTF_PATH + fileName1));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				SharedPreferences sp = null;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					sp = mContext.getSharedPreferences("default.cfg", Context.MODE_MULTI_PROCESS);
				} else {
					sp = mContext.getSharedPreferences("default.cfg", Context.MODE_PRIVATE);
				}
				RLog.i("create_default_theme", sp.getBoolean("create_default_theme", false));
				if (!sp.getBoolean("create_default_theme", false)) {
					boolean flag = false;// 用户是否已经创建默认锁屏
					File folder = new File(MConstants.THEME_PATH);
					if (folder.exists()) {
						File[] files = folder.listFiles();
						if (files != null) {
							for (int i = 0; i < files.length; i++) {
								File file = files[i];
								if (file.isDirectory()) {
									if (new File(file.getAbsolutePath(), MConstants.config).exists()) {
										flag = true;
										break;
									}
								}
							}
						}
					}
					RLog.i("create_theme_flag", flag);
					if (flag) {
						return;
					}
					String folderName = System.currentTimeMillis() + "";
					File themefolder = new File(MConstants.THEME_PATH, folderName);
					themefolder.mkdirs();
					try {
						new File(themefolder, MConstants.uploaded).createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						File configFile = new File(themefolder.getAbsolutePath(), MConstants.config);
						FileUtils.copyFile(am.open("config.json"), configFile);
						File previewFile = new File(themefolder.getAbsolutePath(), "/preview");
						FileUtils.copyFile(am.open("preview"), previewFile);

						File wallpaperFile = new File(themefolder.getAbsolutePath(), "/wallpaper");
						FileUtils.copyFile(am.open("wallpaper"), wallpaperFile);
						sp.edit().putBoolean("create_default_theme", true).apply();
						LockApplication.getInstance().getConfig().setThemeName(themefolder.getAbsolutePath(), false);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}).start();

	}
	
	public static void createDefaultTheme2(final Context mContext) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				String fileName1 = "en_one.ttf";
				AssetManager am = mContext.getAssets();
				if (!new File(MConstants.TTF_PATH + fileName1).exists()) {
					try {
						InputStream is1 = am.open(fileName1);
						FileUtils.copyFile(is1, new File(MConstants.TTF_PATH + fileName1));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				SharedPreferences sp = null;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					sp = mContext.getSharedPreferences("default.cfg", Context.MODE_MULTI_PROCESS);
				} else {
					sp = mContext.getSharedPreferences("default.cfg", Context.MODE_PRIVATE);
				}
				RLog.i("create_default_theme", sp.getBoolean("create_default_theme", false));
				if (!sp.getBoolean("create_default_theme", false)) {
					boolean flag = false;// 用户是否已经创建默认锁屏
					File folder = new File(MConstants.THEME_PATH);
					if (folder.exists()) {
						File[] files = folder.listFiles();
						if (files != null) {
							for (int i = 0; i < files.length; i++) {
								File file = files[i];
								if (file.isDirectory()) {
									if (new File(file.getAbsolutePath(), MConstants.config).exists()) {
										flag = true;
										break;
									}
								}
							}
						}
					}
					RLog.i("create_theme_flag", flag);
					if (flag) {
						return;
					}
					String folderName = "333";
					File themefolder = new File(MConstants.THEME_PATH, folderName);
					themefolder.mkdirs();
					try {
						new File(themefolder, MConstants.uploaded).createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						File configFile = new File(themefolder.getAbsolutePath(), MConstants.config);
						FileUtils.copyFile(am.open("config2.json"), configFile);
						File previewFile = new File(themefolder.getAbsolutePath(), "/preview");
						FileUtils.copyFile(am.open("preview2"), previewFile);

						File wallpaperFile = new File(themefolder.getAbsolutePath(), "/wallpaper");
						FileUtils.copyFile(am.open("wallpaper2"), wallpaperFile);
//						sp.edit().putBoolean("create_default_theme", true).apply();
//						LockApplication.getInstance().getConfig().setThemeName(themefolder.getAbsolutePath(), false);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}).start();

	}
	
	/**
	 * if (!TextUtils.isEmpty(fontName)) {
									File fontFile = new File(MConstants.TTF_PATH, fontName);
									if (fontFile.exists()) {
										dayWordStickerInfo.font = fontFile.getAbsolutePath();
									}
								}
	 * @param file
	 * @return 
	 */
	
	public static ArrayList<String> fontsPrase(File file){
		String configJson = FileUtils.getFileString(file);
		ArrayList<String> arrayList=new ArrayList<String>();
		try {
			JSONObject jsonObject = new JSONObject(configJson);
			if(jsonObject!=null){
				JSONArray fontsJsonArray = jsonObject.optJSONArray("fontUrls");
				if(fontsJsonArray!=null && fontsJsonArray.length()>0){
					for(int i=0;i<fontsJsonArray.length();i++){
						String fontUrl=fontsJsonArray.optString(i);
						String fontName=HASH.md5sum(fontUrl);
						if(!new File(MConstants.TTF_PATH,fontName).exists()){
							arrayList.add(fontUrl);
						}
					}
				}
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return arrayList;
	}
	
	/**
	 * 格式化componentName为字符串方便存储.
	 * 
	 * @param stickerInfo
	 * @param componentName
	 */
	public static void parseComponentName2Action(LockerInfo stickerInfo, ComponentName componentName,int position) {
		if (stickerInfo != null && componentName != null) {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("packageName", componentName.getPackageName());
				jsonObject.put("className", componentName.getClassName());
				if(position==1){
					stickerInfo.action1 = jsonObject.toString();
				}else if(position==3){
					stickerInfo.action2 = jsonObject.toString();
				}else if(position==4){
					stickerInfo.action3 = jsonObject.toString();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 格式化字符串为componentName
	 * 
	 * @param action
	 * @return
	 */
	public static ComponentName parseAction2ComponentName(String action) {
		if (!TextUtils.isEmpty(action)) {
			try {
				JSONObject jsonObject = new JSONObject(action);
				if (jsonObject != null) {
					String packageName = jsonObject.getString("packageName");
					String className = jsonObject.getString("className");
					return new ComponentName(packageName, className);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
