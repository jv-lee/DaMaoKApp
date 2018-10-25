package com.lockstudio.sticklocker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.service.CoreService;
import com.lockstudio.sticklocker.util.DmUtil;
import com.lockstudio.sticklocker.util.RLog;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        RLog.i("-action-", action);

        if (Intent.ACTION_SHUTDOWN.equals(action) || Intent.ACTION_REBOOT.equals(action)) {
            LockApplication.getInstance().getConfig().setReboot(true);
//            LockApplication.getInstance().getConfig().setLocked(false);
            //杀死daemon.so服务
            DmUtil.killDaemon(DmUtil.getDaemons(context.getApplicationContext().getFilesDir() + "/daemon"));
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            LockApplication.getInstance().getConfig().setReboot(true);
            context.startService(new Intent(context, CoreService.class));
        }
    }
}
