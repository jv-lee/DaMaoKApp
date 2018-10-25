package com.lockstudio.sticklocker.util;

import android.util.Log;

/**
 * Log工具类
 * 
 * @author 王雷
 * 
 */
public final class RLog {
//	private static boolean EnableLog = true;
	private static boolean EnableLog = false;

	public static void d(Object obj) {
		if (EnableLog) {
			StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
			StringBuffer toStringBuffer = new StringBuffer("----------").append(traceElement.getFileName().substring(0, traceElement.getFileName().lastIndexOf(".")) + "__")
					.append(traceElement.getMethodName()).append("()|").append(traceElement.getLineNumber()).append("----------");
			String title = toStringBuffer.toString();
			if (obj != null) {
				Log.d("Ray", title + "\n" + obj);
			} else {
				Log.d("Ray", title + "\n" + "I am Here");
			}
		}
	}

	public static void d(final String key, final Object value) {
		if (EnableLog)
			Log.d("Ray", key + ":  " + value);
	}

    public static void v(final String key, final Object value) {
		if (EnableLog)
			Log.v("Ray", key + ":  " + value);
	}

	public static void i(final Object key, final Object value) {
		if (EnableLog)
			Log.i("Ray", key + ":  " + value);
	}

	public static void w(final String key, final Object value) {
		if (EnableLog)
			Log.w("Ray", key + ":  " + value);
	}

	public static void e(final String key, final Object value) {
        if (EnableLog)
		    Log.e("Ray", key + ":  " + value);
	}

}