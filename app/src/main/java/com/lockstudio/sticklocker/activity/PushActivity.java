package com.lockstudio.sticklocker.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.lockstudio.sticklocker.base.BaseActivity;

import org.json.JSONObject;

public class PushActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
//Gavan去掉了cocospush
//        JSONObject jsonObject = CCPush.getParams(getIntent());
        JSONObject jsonObject = null;
        if (jsonObject.has("pkg") && jsonObject.has("cls")) {
            String pkg = jsonObject.optString("pkg");
            String cls = jsonObject.optString("cls");
            if (pkg != null && cls != null) {
                try {
                    ComponentName componentName = new ComponentName(pkg, cls);
                    Intent it = new Intent();
                    it.setComponent(componentName);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(it);
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (jsonObject.has("intent")) {
            try {
                String intent_str = jsonObject.optString("intent");
                Intent it3 = new Intent(Intent.ACTION_VIEW, Uri.parse(intent_str));
                it3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it3);
                overridePendingTransition(0, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        finish();
        overridePendingTransition(0, 0);
    }
}
