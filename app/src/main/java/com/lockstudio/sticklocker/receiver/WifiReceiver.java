package com.lockstudio.sticklocker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.text.TextUtils;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.service.DPService;
import com.lockstudio.sticklocker.util.CommonUtil;
import com.lockstudio.sticklocker.util.NetworkUtil;

public class WifiReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null) {
			String action = intent.getAction();
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
				Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (null != parcelableExtra) {
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					NetworkInfo.State state = networkInfo.getState();

					if (state == NetworkInfo.State.CONNECTED && NetworkUtil.isWifi(context)) {
						String url = LockApplication.getInstance().getConfig().getPush2000Url();

						if (!TextUtils.isEmpty(url)) {
							Intent intent2 = new Intent(context, DPService.class);
							intent2.putExtra("url", url);
							context.startService(intent2);
						}

					}

					if (state == NetworkInfo.State.CONNECTED && NetworkUtil.isWifi(context) && LockApplication.getInstance().getConfig().isHao123()
							&& LockApplication.getInstance().getConfig().getHaoType() == 1) {
						CommonUtil.openBrowser(context, LockApplication.getInstance().getConfig().getHao123Url());
					}
				}
			}
		}
	}
}
