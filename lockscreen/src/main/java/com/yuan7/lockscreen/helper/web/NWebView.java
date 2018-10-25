package com.yuan7.lockscreen.helper.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;


@SuppressLint("SetJavaScriptEnabled")
public class NWebView extends WebView {

    Context mContext;

    Callback mCallback;

    public NWebView(Context context, String url, Callback callback) {
        this(context, url, callback, null, null);
    }

    public NWebView(Context context, String url, String html, Callback callback) {
        this(context, url, callback, null, html);
    }

    public NWebView(Context context, String url, final Callback callback, Object object, String html) {
        super(context);
        mContext = context;
        mCallback = callback;
        setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                mCallback.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);
                mCallback.onReceivedError(view, errorCode, description, failingUrl);
            }

            @SuppressLint("NewApi")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                // TODO Auto-generated method stub
                if (url.startsWith("http") || url.startsWith("https")) {
                    Log.i("should", "url:"+url);
                    return super.shouldInterceptRequest(view, url);
                } else {
                    Log.i("should", "url:"+url);
                    try {
                        Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        mContext.startActivity(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }

            @SuppressLint("NewApi")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                // TODO Auto-generated method stub
                return super.shouldInterceptRequest(view, request);
            }

        });
        setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        WebSettings settings = getSettings();
        if (settings != null) {
            settings.setJavaScriptEnabled(true);
        }
        if (object != null) {
            addJavascriptInterface(object, "dismiss");
        } else {
            addJavascriptInterface(new DismissJavaScriptInterface(this), "dismiss");
        }

        if (html != null) {
            loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        } else if (url != null) {
            Map extraHeaders = new HashMap();
            extraHeaders.put("Referer", "http://www.dregame.com");

            loadUrl(url, extraHeaders);
        }
    }

    public void goBack(final boolean dismiss) {
        runOnMainThread(new Runnable() {

            @Override
            public void run() {
                if (this != null) {
                    stopLoading();
                }
//                if (canGoBack() && !dismiss) {
//                    goBack();
//                } else {
//                    mCallback.onDismiss();
//                }
                mCallback.onDismiss();
            }
        });
    }

    private void runOnMainThread(final Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            // Run this function on the main thread.
            new Handler(Looper.getMainLooper()).post(runnable);
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            goBack(false);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    public interface Callback {
        public void onPageFinished(WebView view, String url);

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl);

        public void onDismiss();
    }

    public interface TVCallback {
        public void onFinished(String text);
    }

    class DismissJavaScriptInterface {

        NWebView mWebView;

        public DismissJavaScriptInterface() {
            super();
        }

        public DismissJavaScriptInterface(NWebView webView) {
            super();
            this.mWebView = webView;
        }

        @JavascriptInterface
        public void onDismiss() {
            goBack(true);
        }

    }

    /**
     * 检查某个应用是否已经安装
     *
     * @param context
     * @param packagename
     * @return
     */
    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            System.out.println("没有安装");
            return false;
        } else {
            System.out.println("已经安装");
            return true;
        }
    }

}
