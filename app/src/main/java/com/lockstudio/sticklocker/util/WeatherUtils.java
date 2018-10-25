package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.WeatherBean;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import cn.opda.android.activity.R;

public class WeatherUtils {

	public static String[] list_yu = { "阵雨", "雷阵雨", "雷阵雨伴有冰雹", "雨夹雪", "小雨", "中雨", "大雨", "暴雨", "大暴雨", "特大暴雨", "冻雨", "小到中雨", "中到大雨", "大到暴雨", "暴雨到大暴雨", "大暴雨到特大暴雨" };
	public static String[] list_xue = { "小到中雪", "中到大雪", "大到暴雪", "阵雪", "小雪", "中雪", "大雪", "暴雪" };
	public static String[] list_mai = { "雾", "霾" };
	public static String[] list_chen = { "浮尘", "扬沙", "沙尘暴", "强沙尘暴" };
	public static String[] list_yun = { "多云" };
	public static String[] list_yin = { "阴" };
	public static String[] list_qing = { "晴" };

	/*
	 * { errNum: 0, errMsg: "success", retData: { city: "北京", //城市 pinyin:
	 * "beijing", //城市拼音 citycode: "101010100", //城市编码 date: "15-02-11", //日期
	 * time: "11:00", //发布时间 postCode: "100000", //邮编 longitude: 116.391, //经度
	 * latitude: 39.904, //维度 altitude: "33", //海拔 weather: "晴", //天气情况 temp:
	 * "10", //气温 l_tmp: "-4", //最低气温 h_tmp: "10", //最高气温 WD: "无持续风向", //风向 WS:
	 * "微风(<10m/h)", //风力 sunrise: "07:12", //日出时间 sunset: "17:44" //日落时间 } }
	 */

	public static WeatherBean getWeatherByHanZi(String cityPinyin) {
		WeatherBean weatherBean = null;
		try {
			String city = URLEncoder.encode(cityPinyin, "utf-8");
			URL enurl = new URL("http://apistore.baidu.com/microservice/weather?cityname=" + city);
			HttpURLConnection conn = (HttpURLConnection) enurl.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30 * 1000);
			conn.setRequestMethod("GET");
			conn.connect();
			int code = conn.getResponseCode();
			if (code == 200) {
				//
				InputStream inStream = conn.getInputStream();
				StringBuffer sb = new StringBuffer();
				byte[] buffer = new byte[1024];
				int len;
				while ((len = inStream.read(buffer)) != -1) {
					String string = new String(buffer, 0, len);
					sb.append(string);

				}
				inStream.close();
				String response = sb.toString();
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject != null) {
					String msg = jsonObject.optString("errMsg");
					if (msg.equals("success")) {
						JSONObject weatherJsonObject = new JSONObject(jsonObject.optString("retData"));
						weatherBean = new WeatherBean();
						weatherBean.setCity_name(weatherJsonObject.optString("city"));
						weatherBean.setWeather(weatherJsonObject.optString("weather"));
						weatherBean.setTemp(weatherJsonObject.optString("temp"));
						weatherBean.setHigh_temp(weatherJsonObject.optString("h_tmp"));
						weatherBean.setLow_temp(weatherJsonObject.optString("l_tmp"));
					} else {
						return null;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return weatherBean;
	}

	public static WeatherBean getWeather(String cityPinyin) {
		WeatherBean weatherBean = null;
		try {
			URL enurl = new URL("http://apistore.baidu.com/microservice/weather?citypinyin=" + cityPinyin);
			HttpURLConnection conn = (HttpURLConnection) enurl.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30 * 1000);
			conn.setRequestMethod("GET");
			conn.connect();
			int code = conn.getResponseCode();
			if (code == 200) {
				//
				InputStream inStream = conn.getInputStream();
				StringBuffer sb = new StringBuffer();
				byte[] buffer = new byte[1024];
				int len;
				while ((len = inStream.read(buffer)) != -1) {
					String string = new String(buffer, 0, len);
					sb.append(string);

				}
				inStream.close();
				String response = sb.toString();
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject != null) {
					String msg = jsonObject.optString("errMsg");
					if (msg.equals("success")) {
						JSONObject weatherJsonObject = new JSONObject(jsonObject.optString("retData"));
						weatherBean = new WeatherBean();
						weatherBean.setCity_name(weatherJsonObject.optString("city"));
						weatherBean.setWeather(weatherJsonObject.optString("weather"));
						weatherBean.setTemp(weatherJsonObject.optString("temp"));
					} else {
						return null;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return weatherBean;
	}

	public static String getState(String weather) {
		for (int i = 0; i < list_yu.length; i++) {
			if (list_yu[i].equals(weather)) {
				return "雨";
			}
		}
		for (int i = 0; i < list_xue.length; i++) {
			if (list_xue[i].equals(weather)) {
				return "雪";
			}
		}
		for (int i = 0; i < list_mai.length; i++) {
			if (list_mai[i].equals(weather)) {
				return "雾霾";
			}
		}
		for (int i = 0; i < list_chen.length; i++) {
			if (list_chen[i].equals(weather)) {
				return "浮尘";
			}
		}
		for (int i = 0; i < list_yun.length; i++) {
			if (list_yun[i].equals(weather)) {
				return "多云";
			}
		}
		for (int i = 0; i < list_yin.length; i++) {
			if (list_yin[i].equals(weather)) {
				return "阴";
			}
		}
		for (int i = 0; i < list_qing.length; i++) {
			if (list_qing[i].equals(weather)) {
				return "晴";
			}
		}
		return "未知";
	}

	public static int getStateIcon(String weather) {
		for (int i = 0; i < list_yu.length; i++) {
			if (list_yu[i].equals(weather)) {
				return R.drawable.ic_weather_yu;
			}
		}
		for (int i = 0; i < list_xue.length; i++) {
			if (list_xue[i].equals(weather)) {
				return R.drawable.ic_weather_xue;
			}
		}
		for (int i = 0; i < list_mai.length; i++) {
			if (list_mai[i].equals(weather)) {
				return R.drawable.ic_weather_mai;
			}
		}
		for (int i = 0; i < list_chen.length; i++) {
			if (list_chen[i].equals(weather)) {
				return R.drawable.ic_weather_chen;
			}
		}
		for (int i = 0; i < list_yun.length; i++) {
			if (list_yun[i].equals(weather)) {
				return R.drawable.ic_weather_yun;
			}
		}
		for (int i = 0; i < list_yin.length; i++) {
			if (list_yin[i].equals(weather)) {
				return R.drawable.ic_weather_yin;
			}
		}
		for (int i = 0; i < list_qing.length; i++) {
			if (list_qing[i].equals(weather)) {
				return R.drawable.ic_weather_qing;
			}
		}
		return R.drawable.ic_weather_qing;
	}

	/**
	 * 获取当前的城市
	 * @param mContext
	 */
	public static void getCity(Context mContext) {
		String url = HostUtil.getUrl("ipcity/city?json=1");

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (response != null && 200 == response.optInt("code")) {
					String city = response.optString("city");
					if(!TextUtils.isEmpty(city)){
						LockApplication.getInstance().getConfig().setLocalCity(city);
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		RequestQueue requestQueue = VolleyUtil.instance().getRequestQueue();
		if (requestQueue != null) {
			requestQueue.add(jsonObjectRequest);
		}
	}

}
