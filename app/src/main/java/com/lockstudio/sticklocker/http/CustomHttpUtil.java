package com.lockstudio.sticklocker.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.AppConfig;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.RLog;

import java.util.Map;

/**
 * HTTP 请求类
 * 
 * @author Administrator
 * 
 */
public class CustomHttpUtil {
	public static final String UTF8 = "utf-8";

	/**
	 * 普通POST表单请求
	 * 
	 * @param path
	 * @param params
	 * @param encoding
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String sendPostRequest(Context context, String path, Map<String, String> params) throws Exception {
		return sendPostRequest(context, path, params, (FormFile) null);
	}

	/**
	 * 包含文件上传功能
	 * 
	 * @param path
	 * @param params
	 * @param encoding
	 * @param formFile
	 *            文件
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String sendPostRequest(Context context, String path, Map<String, String> params, FormFile formFile) throws Exception {
		return sendPostRequest(context, path, params, true, formFile);
	}

	/**
	 * 包含文件上传功能和是否发送cookie
	 * 
	 * @param path
	 * @param params
	 * @param encoding
	 * @param needSendCookie
	 *            是否发送cookie
	 * @param formFile
	 *            文件
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String sendPostRequest(Context context, String path, Map<String, String> params, boolean needSendCookie, FormFile formFile) throws Exception {
		AppConfig appConfig = LockApplication.getInstance().getConfig();
		String oldurl = formFile != null ? MConstants.uploadHosts[0] : appConfig.getHost();
		if (!TextUtils.isEmpty(oldurl)) {
			try {
				String json = null;
				if (params != null && !params.isEmpty()) {
					PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
					params.put("appversion", packageInfo != null ? packageInfo.versionCode + "" : "1");
					params.put("appcode", MConstants.UPDATE_APPCODE);
					RLog.i("debug", "request--->" + "path：" + oldurl + path + "\nrequest:" + params.toString());
				}
				CustomHttpClient httpClient = CustomHttpClient.getInstance();
				httpClient.sendCookie = needSendCookie;
				if (!httpClient.sendCookie) {// 只有在登录、注册的时候不发送cookie
					httpClient.connTimeOut = 40000;
					httpClient.readTimeOut = 180000;
				}
				json = httpClient.post(context, oldurl + path, UTF8, params, formFile);

				if (!TextUtils.isEmpty(json)) {
					if (formFile == null) {
						appConfig.setHost(oldurl);
					}
					RLog.i("debug", "response: " + json);
					return json;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}
}
