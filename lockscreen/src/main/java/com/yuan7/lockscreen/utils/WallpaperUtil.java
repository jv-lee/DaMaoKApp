package com.yuan7.lockscreen.utils;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Administrator on 2018/5/29.
 */

public class WallpaperUtil {

    public static void setPortraitWallpaper(Activity activity, Bitmap bitmap) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);

        try {
//            wallpaperManager.setBitmap(BitmapUtil.sBitmap(bitmap, display.getWidth(), display.getHeight()));
            wallpaperManager.setBitmap(bitmap);
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setLandscapeWallpaper(Activity activity, Bitmap bitmap, Bitmap bitmap2) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
        int width = wallpaperManager.getDesiredMinimumWidth();
        int height = wallpaperManager.getDesiredMinimumHeight();

        try {

            if (width > height) {
                wallpaperManager.setBitmap(BitmapUtil.sBitmap(bitmap, width, height));
            } else {
                wallpaperManager.setBitmap(bitmap2);
            }
            bitmap.recycle();
            bitmap2.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setLockWallPaper(Activity activity, String path) {
        // TODO Auto-generated method stub
        try {
            WallpaperManager mWallManager = WallpaperManager.getInstance(activity);

            Toast.makeText(activity, "锁屏壁纸设置成功", Toast.LENGTH_SHORT).show();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void setLockScree(Activity activity, Bitmap bitmap) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
        int width = wallpaperManager.getDesiredMinimumWidth();
        int height = wallpaperManager.getDesiredMinimumHeight();

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                bitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
