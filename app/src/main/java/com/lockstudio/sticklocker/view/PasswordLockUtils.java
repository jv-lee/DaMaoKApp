package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.text.TextUtils;

import com.lockstudio.sticklocker.application.LockApplication;

import java.util.ArrayList;

public class PasswordLockUtils {

	public PasswordLockUtils(Context context) {
	}

	public void saveLockPass(ArrayList<Integer> passArray) {
		String password = "";
		for (int i = 0; i < passArray.size(); i++) {
			if (i == 0) {
				password += passArray.get(i);
			} else {
				password += ("," + passArray.get(i));
			}
			LockApplication.getInstance().getConfig().setPassword(password);
		}

	}

	public boolean checkPassword(ArrayList<Integer> passArray) {
		String pass = LockApplication.getInstance().getConfig().getPassword();
		String password = "";
		for (int i = 0; i < passArray.size(); i++) {
			if (i == 0) {
				password += passArray.get(i);
			} else {
				password += ("," + passArray.get(i));
			}
		}
		return password.equals(pass);
	}

	public int getPasswordLength() {
		String pass = LockApplication.getInstance().getConfig().getPassword();
		if (!TextUtils.isEmpty(pass)) {
			String[] strs = pass.split(",");
			if (strs != null) {
				return strs.length;
			}
		}
		return 0;
	}
}
