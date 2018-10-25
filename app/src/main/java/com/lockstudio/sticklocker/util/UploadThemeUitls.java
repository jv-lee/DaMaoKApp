package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.http.BaseJsonUtil;
import com.lockstudio.sticklocker.http.CustomHttpUtil;
import com.lockstudio.sticklocker.http.FormFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UploadThemeUitls extends BaseJsonUtil {
	public UploadThemeUitls(Context context) {
		super(context);
	}

	public boolean upload(String filePath, final String name, final String author, final String contact, final int upload_status) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		String deviceId = "";
		try {
			deviceId = HASH.md5sum(DeviceInfoUtils.getIMEI(context) + DeviceInfoUtils.getIMSI(context));
		} catch (Exception e) {
			e.printStackTrace();
		}
		PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		params.put("uploadname", name);
		params.put("uploadmemo", author);
		params.put("uploadphone", contact);
		params.put("uploaddid", deviceId);
		params.put("upload_status", upload_status + "");
		params.put("version_code", packageInfo.versionCode + "");
		params.put("screen_height", LockApplication.getInstance().getConfig().getScreenHeight() + "");
		params.put("screen_width", LockApplication.getInstance().getConfig().getScreenWidth() + "");
		FormFile formFile = new FormFile(filePath, new File(filePath), "file", null);
		String response = CustomHttpUtil.sendPostRequest(context, MConstants.URL_UPLOADTHEME, params, formFile);
		parseResponse(response);
		return getCode() == 200;
	}

}
