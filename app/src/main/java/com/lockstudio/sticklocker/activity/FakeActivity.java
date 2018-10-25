package com.lockstudio.sticklocker.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.DeviceUtils;
import com.lockstudio.sticklocker.util.MConstants;

import cn.opda.android.activity.R;


public class FakeActivity extends Activity {

    private final String TAG = "V5_FAKE_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        IntentFilter finishFilter = new IntentFilter(MConstants.ACTION_CLOSE_FAKE_ACTIVITY);
        finishFilter.addAction(Intent.ACTION_SCREEN_ON);
        finishFilter.setPriority(999);
        registerReceiver(finishReceiver, finishFilter);


        findViewById(R.id.root_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        CustomEventCommit.commit(getApplicationContext(), TAG, "SHOW");

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishReceiver);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (!ViewConfiguration.get(this).hasPermanentMenuKey()) {
                systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
        }

        sendBroadcast(new Intent(MConstants.ACTION_FAKE_ACTIVITY_CREAT_DONE));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    if (!DeviceUtils.isMIUI()) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            exit();
                        } else if (ViewConfiguration.get(FakeActivity.this).hasPermanentMenuKey() && KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)) {
                            exit();
                        }
                    }
                } else {
                    exit();
                }
            }

        }
    };

    private void exit() {
        CustomEventCommit.commit(getApplicationContext(), TAG, "EXIT");

        finish();
        overridePendingTransition(0, 0);
    }
}
