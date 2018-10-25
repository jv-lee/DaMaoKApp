package com.lockstudio.sticklocker.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lockstudio.sticklocker.receiver.LockScreenReceiver;

public class LockScreenActivity extends Activity {
	private ComponentName componentName;
	private DevicePolicyManager policyManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		componentName = new ComponentName(this, LockScreenReceiver.class);
		boolean from_receiver = getIntent().getBooleanExtra("from_receiver", false);
		if(from_receiver){
			if (policyManager.isAdminActive(componentName)) {
				policyManager.lockNow();
			}
		}else{
			lock();
		}
		finish();
	}

	private void lock() {
		if (!policyManager.isAdminActive(componentName)) {
			activeManage();
		} else {
			policyManager.lockNow();
		}
	}

	private void activeManage() {
		try {
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "使用此功能，需要先激活设备管理器，目前所有的一键锁屏都需要该操作，激活后即可使用，绝对安全。注意：卸载前系统会提示无法卸载，请先在系统设置—安全—设备管理器中取消勾选文字锁屏即可。");
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
