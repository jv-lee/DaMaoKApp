package com.lockstudio.sticklocker.Interface;

import android.view.View;

import com.lockstudio.sticklocker.model.StickerInfo;

public interface OnRemoveStickerViewListener {
	public abstract void removeView(StickerInfo stickerInfo, View stickerView);
}
