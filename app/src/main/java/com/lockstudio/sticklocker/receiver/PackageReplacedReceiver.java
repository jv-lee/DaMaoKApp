package com.lockstudio.sticklocker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lockstudio.sticklocker.service.CoreService;

public class PackageReplacedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, CoreService.class));
//        Intent it = new Intent(context, MainActivity.class);
//        it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(it);
    }
}
