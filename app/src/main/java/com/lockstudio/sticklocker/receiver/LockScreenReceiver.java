package com.lockstudio.sticklocker.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import com.lockstudio.sticklocker.activity.LockScreenActivity;

public class LockScreenReceiver extends DeviceAdminReceiver {
	@Override
	public void onEnabled(Context context, Intent intent) {
		Intent intent2 = new Intent(context, LockScreenActivity.class);
		intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent2.putExtra("from_receiver", true);
		context.startActivity(intent2);
	}
}
