package com.lockstudio.sticklocker.http;

import android.content.Context;

import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.Locale;

public class BaseJsonUtil {
	protected Context context;
	private int code;

	public BaseJsonUtil(Context context) {
		this.context = context;
	}

	/**
	 * 添加请求前的公共参数
	 * 
	 * @param did
	 * @return 请求的根JSONObject
	 * @throws JSONException
	 */
	public JSONObject commonRequest() throws Exception {
		JSONObject value = new JSONObject();
		value.put("configversion", 1000);
		String language = Locale.getDefault().getLanguage();
		String country = Locale.getDefault().getCountry();
		value.put("language", language);
		value.put("country", country);
		value.put("imsi", DeviceInfoUtils.getIMSI(context));

		if (!NetworkUtil.getNetWorkState(context)) {
			throw new ConnectException();
		}

		return value;
	}

	public void parseResponse(String response) throws Exception {
		JSONObject root = new JSONObject(response);
		code = root.optInt("code");
	}

	public int getCode() {
		return code;
	}
}
