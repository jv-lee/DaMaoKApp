package com.lockstudio.sticklocker.model;

import android.content.ComponentName;

/**
 * Created by Tommy on 15/3/15.
 */
public class LockerInfo {
	public static final int StyleLove = 1;
	public static final int StyleNinePattern = 2;
	public static final int StyleTwelvePattern = 3;
	public static final int StyleCouple = 4;
	public static final int StyleSlide = 5;
	public static final int StyleNone = 6;
	public static final int StyleFree = 7;
	public static final int StyleImagePassword = 8;
	public static final int StyleWordPassword = 9;
	public static final int StyleNumPassword = 10;
	public static final int StyleApp = 11;
    protected int styleId;
    protected int x;
    
    protected int y;
    protected int width;
    protected int height;
    protected String lockerName;
    protected String fontUrl;
    protected int previewRes;
    protected boolean selecter;
    
    public String action1;
    public String action2;
    public String action3;
    public  ComponentName componentName;
    public  ComponentName componentName2;
    public  ComponentName componentName3;
    
    
	public String getFontUrl() {
		return fontUrl;
	}

	public void setFontUrl(String fontUrl) {
		this.fontUrl = fontUrl;
	}

	public String getLockerName() {
		return lockerName;
	}

	public void setLockerName(String lockerName) {
		this.lockerName = lockerName;
	}

	public int getPreviewRes() {
		return previewRes;
	}

	public void setPreviewRes(int previewRes) {
		this.previewRes = previewRes;
	}

	public int getStyleId() {
        return styleId;
    }

    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

	public boolean isSelecter() {
		return selecter;
	}

	public void setSelecter(boolean selecter) {
		this.selecter = selecter;
	}
    
    
}
