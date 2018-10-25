package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.text.TextUtils;

import com.lockstudio.sticklocker.application.LockApplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoveLockUtils {

	public LoveLockUtils(Context context) {
	}

	public void saveLockPass(String password) {
        LockApplication.getInstance().getConfig().setPassword(password);

	}

	public byte[] passwordToHash(String password) {
		byte[] res = password.getBytes();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] hash = md.digest(res);
			return hash;
		} catch (NoSuchAlgorithmException nsa) {
			return res;
		}
	}

	public boolean checkPassword(String password) {
		String pass = LockApplication.getInstance().getConfig().getPassword();
		if (TextUtils.isEmpty(pass)) {
			return true;
		}
		return password.equals(pass);
	}
}
