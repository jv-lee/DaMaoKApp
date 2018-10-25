package com.lockstudio.sticklocker.Interface;

import android.view.View;

import com.lockstudio.sticklocker.model.LockerInfo;

public interface OnRemoveLockerViewListener {
	public abstract void removeView(LockerInfo lockerInfo, View view);
}
