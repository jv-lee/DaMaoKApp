package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.text.TextUtils;

import com.lockstudio.sticklocker.application.LockApplication;

import java.util.List;

/**
 * 图案解锁加密、解密工具类
 * 
 * @author way
 * 
 */
public class LockPatternUtils_12 {
	public static final int LOCKED_TIME = 30;
	public static final int MIN_LOCK_PATTERN_SIZE = 4;
	public static final int FAILED_ATTEMPTS_BEFORE_TIMEOUT = 10;
	public static final int MIN_PATTERN_REGISTER_FAIL = MIN_LOCK_PATTERN_SIZE;
	public static final long FAILED_ATTEMPT_TIMEOUT_MS = 30 * 1000;

	public LockPatternUtils_12(Context context) {
	}


	public static String patternToString(List<LockPatternView_12.Cell> pattern) {
		if (pattern == null) {
			return "";
		}
		final int patternSize = pattern.size();

		byte[] res = new byte[patternSize];
		for (int i = 0; i < patternSize; i++) {
			LockPatternView_12.Cell cell = pattern.get(i);
			res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
		}
		return new String(res);
	}

	public void saveLockPattern(List<LockPatternView_12.Cell> pattern) {
        LockApplication.getInstance().getConfig().setPassword(patternToString(pattern));
	}

	
	public boolean checkPattern(List<LockPatternView_12.Cell> pattern) {
		String pass = LockApplication.getInstance().getConfig().getPassword();

		return TextUtils.isEmpty(pass) || pass.equals(patternToString(pattern));
	}
}
