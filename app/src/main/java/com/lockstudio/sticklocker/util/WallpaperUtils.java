package com.lockstudio.sticklocker.util;

import android.content.Context;

/**
 * 获取壁纸分辨率
 * @author Ray
 *
 */
public class WallpaperUtils {
	private Context mContext;
	public WallpaperUtils(Context mContext)
	{
		super();
		this.mContext = mContext;
	}
	public static enum Resolution{
		small,normal,big
	}
	
//	W≥1080，返回1080P壁纸
//	480<W<1080，返回720P壁纸，这里包括960*540这个分辨率
//	W≤480，返回480P壁纸
	public Resolution getResolution(){
		int width = DeviceInfoUtils.getDeviceWidth(mContext);
        if(width >= 1080) return Resolution.big;
        if(width >= 640) return Resolution.normal;
        return Resolution.small;
	}
}
