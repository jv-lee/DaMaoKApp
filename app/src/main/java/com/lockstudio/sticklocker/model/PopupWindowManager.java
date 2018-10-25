package com.lockstudio.sticklocker.model;

import android.content.Context;

import com.lockstudio.sticklocker.base.CustomPopupView;
import com.lockstudio.sticklocker.util.CustomEventCommit;

import java.util.ArrayList;

public class PopupWindowManager {
	private ArrayList<CustomPopupView> popupWindows;
	private static final String TAG = "POPUPWINDOW_RUN";
	public void addPopup(Context mContext,CustomPopupView popupWindow) {
		if (popupWindows == null) {
			popupWindows = new ArrayList<CustomPopupView>();
		}
		if (popupWindow != null&&!popupWindows.contains(popupWindow)) {
			popupWindows.add(popupWindow);
		}
		if(popupWindow!=null){
			CustomEventCommit.commitEvent(mContext, TAG, popupWindow.getClass().getSimpleName());
		}
	}

	public void dismissAll() {
		if (popupWindows != null) {
			for (int i = 0; i < popupWindows.size(); i++) {
				CustomPopupView popupWindow = popupWindows.get(i);
				popupWindows.remove(i);
				i--;
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
			popupWindows.clear();
		}

	}

	public void remove(CustomPopupView customPopupView) {
		if (popupWindows != null && customPopupView != null && popupWindows.contains(customPopupView)) {
			popupWindows.remove(customPopupView);
		}
	}
}
