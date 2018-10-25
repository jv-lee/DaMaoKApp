package com.yuan7.lockscreen.helper.web;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebView;


public class NWebDialog extends AlertDialog {

    private Context mContext;
    private String mUrl;
    private String mHtml;

    private ProgressDialog mProgress;
    private NWebView webView;


    double mWidthScale, mHightScale;
    boolean mDismissProgres;

    public NWebDialog(Context context, String url) {
        super(context);
        mContext = context;
        mUrl = url;
    }

    public NWebDialog(Context context, String url, String html) {
        super(context);
        mContext = context;
        mUrl = url;
        mHtml = html;
    }

    public NWebDialog(Context context, String url, double widthScale, double heightScale) {
        super(context);
        mContext = context;
        mUrl = url;
        mWidthScale = widthScale;
        mHightScale = heightScale;
    }

    public NWebDialog(Context context, String url, double widthScale, double heightScale, boolean dismiss) {
        super(context);
        mContext = context;
        mUrl = url;
        mWidthScale = widthScale;
        mHightScale = heightScale;
        mDismissProgres = dismiss;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (mWidthScale > 0 && mHightScale > 0) {
            getWindow().setLayout((int) (dm.widthPixels * mWidthScale), (int) (dm.heightPixels * mWidthScale));
        } else {
            getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        webView = new NWebView(mContext, mUrl, mHtml, new NWebView.Callback() {

            @Override
            public void onPageFinished(WebView view, String url) {
                dismissProgress();
            }

            @Override
            public void onDismiss() {
                dismiss();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // TODO Auto-generated method stub
                dismissProgress();
            }
        });

        if (mUrl != null) {
            if (mUrl.contains("wx.tenpay.com")) {
                webView.setVisibility(View.GONE);
            }
        }

        if (mWidthScale > 0 && mHightScale > 0) {
            setContentView(webView,
                    new LayoutParams((int) (dm.widthPixels * mWidthScale), (int) (dm.heightPixels * mWidthScale)));
        } else {
            setContentView(webView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webView != null) {
                        if (webView.canGoBack()) {
                            webView.goBack();
                            return true;
                        } else {
                            dismiss();
                            webDestroy();
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        });
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        super.show();
        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        try {
            if (!mDismissProgres) {
                mProgress = ProgressDialog.show(getContext(), null, "加载中，请稍候……", false, true);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissProgress();
    }

    private void dismissProgress() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    private void webDestroy() {
        if (webView != null) {
            webView.clearCache(true);
            webView.clearHistory();
            webView.setVisibility(View.GONE);
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }

}
