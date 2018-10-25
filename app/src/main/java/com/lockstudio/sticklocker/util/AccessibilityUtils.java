package com.lockstudio.sticklocker.util;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.text.TextUtils.SimpleStringSplitter;
import android.view.accessibility.AccessibilityManager;

import com.lockstudio.sticklocker.service.SystemNotificationService;

import java.util.List;

public class AccessibilityUtils {

	public static String getString(Context context, String id, String packageName) {
		Context createPackageContext = null;
		try {
			createPackageContext = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
		} catch (Exception e) {
			createPackageContext = null;
		}
		if (createPackageContext == null) {
			return null;
		}
		
		try {
			Resources resources = createPackageContext.getResources();
			return resources.getString(resources.getIdentifier(id, "string", packageName));
		} catch (Exception e2) {
			return null;
		}
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean is_accessibility_services_runing(Context context) {
        List<AccessibilityServiceInfo> enabledAccessibilityServiceList = ((AccessibilityManager) context.getSystemService("accessibility")).getEnabledAccessibilityServiceList(16);
        String name = SystemNotificationService.class.getName();
        if (!(enabledAccessibilityServiceList == null || enabledAccessibilityServiceList.isEmpty())) {
            for (AccessibilityServiceInfo accessibilityServiceInfo : enabledAccessibilityServiceList) {
                ResolveInfo resolveInfo = accessibilityServiceInfo.getResolveInfo();
                if (resolveInfo != null && name.equals(resolveInfo.serviceInfo.name)) {
                    return true;
                }
            }
        }
        String string = Secure.getString(context.getContentResolver(), "enabled_accessibility_services");
        if (string != null) {
            SimpleStringSplitter simpleStringSplitter = new SimpleStringSplitter(':');
            simpleStringSplitter.setString(string);
            while (simpleStringSplitter.hasNext()) {
                if (simpleStringSplitter.next().equalsIgnoreCase(context.getPackageName() + "/" + name)) {
                    return true;
                }
            }
        }
        return false;
    }
	
	public static boolean is_support_forcestop() {
        return VERSION.SDK_INT >= 16;
    }
	
	public static Intent get_accessibility_settings_intent() {
        Intent intent = new Intent();
        intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
        return intent;
    }
	
	public static Intent get_uninstall_intent(String packageName) {
        Intent intent = new Intent("android.intent.action.UNINSTALL_PACKAGE");
        intent.setClassName("com.android.packageinstaller", "com.android.packageinstaller.UninstallerActivity");
        intent.putExtra("android.intent.extra.RETURN_RESULT", true);
        intent.setData(Uri.fromParts("package", packageName, null));
        return intent;
    }

    public static Intent get_appdetails_intent(String packageName) {
        Intent intent = new Intent();
        int sdk_int = VERSION.SDK_INT;
        if (sdk_int >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", packageName, null));
        } else {
            String key = sdk_int > 7 ? "pkg" : "com.android.settings.ApplicationPkgName";
            intent.setAction("android.intent.action.VIEW");
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra(key, packageName);
        }
        return intent;
    }
}
