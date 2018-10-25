package com.tommy.ad;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tommy on 15/6/25.
 */
public class JsonRequest {

    private final static int MSG_RETRY = 600;

    private Listener listener;
    private int retryTimes = 3;
    private String url;
    private boolean isRunning = false;

    public JsonRequest() {
    }

    public int start(String url, Listener listener) {
        return start(url, listener, 3);
    }

    public int start(String url, Listener listener, int retry) {

        if (isRunning) {
            return -201;
        }

        if (listener == null) {
            return -1;
        }

        if (TextUtils.isEmpty(url)) {
            if (listener != null) {
                listener.onErrorResponse(-1);
            }
            return -2;
        }

        this.url = url;
        retryTimes = retry;
        this.listener = listener;

        request();

        return 0;
    }


    private HttpURLConnection getHttpURLConnection() {
        HttpURLConnection connection = null;
        try {
            URL _url = new URL(url);
            connection = (HttpURLConnection) _url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent", "JsonRequest");
            connection.setReadTimeout(30 * 1000);
            connection.setUseCaches(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void request() {

        isRunning = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder stringBuilder = new StringBuilder();
                HttpURLConnection connection = getHttpURLConnection();
                if (connection == null) {
                    reLoad();
                    return;
                }

                boolean ok = false;
                try {
                    connection.connect();
                    int code = connection.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        byte[] buffer = new byte[4096];
                        while ((inputStream.read(buffer)) != -1) {
                            stringBuilder.append(new String(buffer));
                        }
                        ok = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                connection.disconnect();

                if (!ok) {
                    reLoad();
                    return;
                }

                if (stringBuilder.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(stringBuilder.substring(0));
                        callbackResponse(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callbackError(-3);
                    }
                }
            }
        }).start();
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RETRY:
                    if (--retryTimes > 0) {
                        request();
                    } else {
                        callbackError(-100);
                    }
                    break;

                default:
                    break;
            }
            return false;
        }
    });


    private void reLoad() {
            handler.sendEmptyMessageDelayed(MSG_RETRY, 500);
        }


    private void callbackError(int code) {
        isRunning = false;
        if (listener != null) {
            listener.onErrorResponse(code);
        }
    }

    private void callbackResponse(JSONObject object) {
        isRunning = false;
        if (listener != null) {
            listener.onResponse(object);
        }
    }

    public interface Listener {
        void onResponse(JSONObject response);
        void onErrorResponse(int errCode);
    }
}
