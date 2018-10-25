package com.lockstudio.sticklocker.model;

/**
 * Created by Tommy on 15/3/15.
 */
public class StickerInfo {
	public static final int StyleImage = 1;
	public static final int StyleWord = 2;
	public static final int StyleTime = 3;
	public static final int StyleBattery = 4;
	public static final int StyleWeather = 5;
	public static final int StyleTimer = 6;
	public static final int StyleStatusbar = 7;
	public static final int StylePhone = 8;
	public static final int StyleSMS = 9;
	public static final int StyleDayWord = 10;
	public static final int StyleHollowWords = 11;
	public static final int StyleMessage = 12;
	public static final int StyleFlashlight = 13;
	public static final int StyleCamera = 14;
    /**
     * x, width 为真实数据和屏幕宽度的比例
     * y, height 为真实数据和屏幕高度的比例
     * angle 为旋转角度
     */
    public int x;
    public int y;
    public int width;
    public int height;
    public int angle;
    public int alpha = 255;

    public int styleId;
    public String content;
    public String font;
    public int color;
    public int shadowColor;
	public String stickerName;
	public String fontUrl;
	public int previewRes;
	public boolean isClick=false;
	
	
}
