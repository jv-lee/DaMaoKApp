package com.lockstudio.sticklocker.util;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.LruCache;

import java.io.File;

public class ImageLoader {
	private static LruCache<String, Bitmap> mMemoryCache;
	private static ImageLoader mImageLoader;

	private ImageLoader() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 24;

		if (Build.VERSION.SDK_INT >= 12) {
			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
				@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
                @Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return bitmap.getByteCount();
				}
			};
		}
	}

	public static ImageLoader getInstance() {
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader();
		}
		return mImageLoader;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			if (mMemoryCache != null) {
				mMemoryCache.put(key, bitmap);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public Bitmap getBitmapFromMemoryCache(String key) {
		if (mMemoryCache != null) {
			return mMemoryCache.get(key);
		}
		return null;

	}

	public Bitmap decodeSampledBitmapFromResource(String pathName) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		long size = getFileSize(new File(pathName));
		if(size > (5*1024*1024)){
			options.inSampleSize = 5;
		}else if(size > (1024*1024)){
			options.inSampleSize = 2;
		}else{
			options.inSampleSize = 1;
		}
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}
	
	
	public static long getFileSize(File f) {
		if (f.exists()) {
			long size = 0;
			if (f != null && f.exists()) {
				if (f.isDirectory()) {
					File flist[] = f.listFiles();
					if (flist != null) {
						for (int j = 0; j < flist.length; j++) {

							if (flist[j].isDirectory()) {
								size = size + getFileSize(flist[j]);
							} else {
								size = size + flist[j].length();
							}
						}

					}
				} else {
					size = f.length();
				}
			}
			return size;
		}
		return 0;
	}


}
