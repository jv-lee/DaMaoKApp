package com.helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class RootSwitchHelper {
	final public static String packagename = "com.zuiai.libigongzhu.baidu";

	static String _serviceProvider = "";
	static String _imei = "";
	private static String _bxmSwitchId = "";//变现猫开关
	private static String _tuiaSwitchId = "";//变现猫开关
	private static String _dmSwitchId = "";//大猫广告
	private static String _pushSwitchId = "";//通知栏拉起
	
	private static String _adSwitchId = "268";//广点通广告
	private static String _otherSwitchId ="269";//广点通以外的广告开关
	
	final static int TYPE_BXM_SWITCH = 100000;
	final static int TYPE_AD_SWITCH = 200000;
	final static int TYPE_DM_SWITCH = 300000;
	final static int TYPE_TUIA_SWITCH = 400000;
	final static int TYPE_PUSH_SWITCH = 500000;
	final static int TYPE_OTHER_SWITCH = 600000;
	
	public static boolean adSwitch = false;
	public static boolean bxmSwitch = false;
	public static boolean tuiaSwitch = false;
	public static boolean dmSwitch = false;
	public static boolean pushSwitch = false;
	public static boolean otherSwitch = false;
	
	public static void getyuan7switch(String sim_info) {
//		System.out.println(sim_info);
		
		adSwitch = false;
		bxmSwitch = false;
		tuiaSwitch = false;
		dmSwitch = false;
		pushSwitch = false;
		otherSwitch = false;
		
		if (sim_info != "unknown") {
			String[] strs = sim_info.split("@@");
			_serviceProvider = strs[0];
			_imei = strs[1];
			
			 new Thread(new Runnable(){
	                @Override
	                public void run() {
//	                	getSwtich(_serviceProvider, _imei,TYPE_DM_SWITCH);
//	                	getSwtich(_serviceProvider, _imei,TYPE_TUIA_SWITCH);
//	                	getSwtich(_serviceProvider, _imei,TYPE_PUSH_SWITCH);
//	                	getSwtich(_serviceProvider, _imei,TYPE_BXM_SWITCH);
	                	getSwtich(_serviceProvider, _imei,TYPE_AD_SWITCH);
	                	getSwtich(_serviceProvider, _imei,TYPE_OTHER_SWITCH);
//	                	System.out.println("======http------tuiaSwitch======="+tuiaSwitch);
//	                	System.out.println("======http------adSwitch======="+adSwitch);
//	                	System.out.println("======http------dmSwitch======="+dmSwitch);
//	                	System.out.println("======http------bxmSwitch======="+bxmSwitch);
//	                	System.out.println("======http------pushSwitch======="+pushSwitch);
	                }
	            }).start();
		}
	}

	/**
	 * 创建json对象 例{ "imei": "1232343imei", "packageName": "com.chaochao.ceshi",
	 * "serviceProvider": "中国移动,广东省", "id": "1" }
	 * 
	 * @param _serviceProvider
	 * @param _imei
	 * @return
	 */

	public static String getJsonInfo(String _serviceProvider, String _imei,int type) {
		String jsonstr = "";
		try {
			JSONObject json = new JSONObject();
			json.put("imei", URLEncoder.encode(_imei, "UTF-8"));// 使用URLEncoder.encode对特殊和不可见字符进行编码
			json.put("packageName", URLEncoder.encode(packagename, "UTF-8"));
			json.put("serviceProvider",
					java.net.URLDecoder.decode(_serviceProvider, "UTF-8"));// 把数据put进json对象中
			switch (type) {
			case TYPE_BXM_SWITCH:
				json.put("id", URLEncoder.encode(_bxmSwitchId, "UTF-8"));
				break;
			case TYPE_AD_SWITCH:
				json.put("id", URLEncoder.encode(_adSwitchId, "UTF-8"));
				break;
			case TYPE_DM_SWITCH:
				json.put("id", URLEncoder.encode(_dmSwitchId, "UTF-8"));
				break;
			case TYPE_TUIA_SWITCH:
				json.put("id", URLEncoder.encode(_tuiaSwitchId, "UTF-8"));
				break;
			case TYPE_PUSH_SWITCH:
				json.put("id", URLEncoder.encode(_pushSwitchId, "UTF-8"));
				break;
			case TYPE_OTHER_SWITCH:
				json.put("id", URLEncoder.encode(_otherSwitchId, "UTF-8"));
				break;
			default:
				break;
			}
			
			json.put("type", 1);
			jsonstr = json.toString();// 把JSON对象按JSON的编码格式转换为字符串

		} catch (Exception e) {

		}
		return jsonstr;
	}

	/**
	 * 发送请求
	 * 
	 * @param _serviceProvider
	 *            地区
	 * @param _imei
	 *            手机imei
	 */
	private static void getSwtich(String _serviceProvider, String _imei,int type) {
		try {
			HttpURLConnection uc = (HttpURLConnection) new URL(
					"http://120.77.128.96:8088/sdkserver/daiji/appconfig")
					.openConnection();
			uc.setDoOutput(true);
			uc.setRequestMethod("POST");
			uc.setConnectTimeout(5000);
			uc.setUseCaches(false);
			uc.setRequestProperty("Content-Type", "application/json");
			uc.setRequestProperty("Accept", "application/json");

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					uc.getOutputStream()));
			String jsonstr = getJsonInfo(_serviceProvider, _imei,type);
			bw.write(jsonstr);
			bw.flush();

			if (uc.getResponseCode() == 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						uc.getInputStream()));
				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = br.readLine()) != null) {
					result.append(line);
				}
//				System.out.println("Result:" + result);

				// json 解析
				JSONObject rjson;
				try {
					rjson = new JSONObject(result.toString());
					System.out.println("rjson=" + rjson);

					boolean canshow = rjson.getBoolean("obj");// 从rjson对象中得到key值为"json"的数据，这里服务端返回的是一个boolean类型的数据

//					System.out.println("canshow=" + canshow);
					if (canshow) {
						switch (type) {
						case TYPE_BXM_SWITCH:
							bxmSwitch = true;
							break;
						case TYPE_AD_SWITCH:
							adSwitch = true;
							break;
						case TYPE_TUIA_SWITCH:
							tuiaSwitch = true;
							break;
						case TYPE_DM_SWITCH:
							dmSwitch = true;
							break;
						case TYPE_PUSH_SWITCH:
							pushSwitch = true;
							break;
						case TYPE_OTHER_SWITCH:
							otherSwitch = true;
							break;
						default:
							break;
						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				System.out.println("uc.getResponseCode()=="
						+ uc.getResponseCode());
				System.out.println("uc.getResponseMessage()=="
						+ uc.getResponseMessage());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
