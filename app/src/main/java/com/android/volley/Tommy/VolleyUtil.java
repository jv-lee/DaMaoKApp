package com.android.volley.Tommy;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.lockstudio.sticklocker.util.MConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.opda.android.activity.R;

/**
 * Created by Tommy on 15/4/5.
 */
public class VolleyUtil implements ImageCache {

	private static final int CACHE_SIZE = 20 * 1024 * 1024;
	private static final int DISK_CACHE_SIZE = CACHE_SIZE * 20;
	private static final String DEFAULT_CACHE_DIR = "cache";

	private static VolleyUtil volleyUtil;
	private RequestQueue requestQueue;
	private LruCache<String, Bitmap> lruCache;
	private Context context;

	public static VolleyUtil instance() {
		if (volleyUtil == null) {
			volleyUtil = new VolleyUtil();
		}
		return volleyUtil;
	}

	public VolleyUtil() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		lruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};
	}

	public void Terminate() {
		if (lruCache != null) {
			lruCache.evictAll();
			lruCache = null;
		}
		volleyUtil = null;
	}

	public void initRequestQueue(Context context) {
		this.context = context.getApplicationContext();

		if (requestQueue != null) {
			return;
		}

		String userAgent = "volley/0";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		HttpStack stack;
		if (Build.VERSION.SDK_INT >= 9) {
			stack = new HurlStack();
		} else {
			stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
		}

		int maxDiskCacheBytes;
		String cacheDir;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			cacheDir = MConstants.FILE_PATH;
			maxDiskCacheBytes = DISK_CACHE_SIZE;
		} else {
			cacheDir = context.getApplicationContext().getCacheDir() + "";
			maxDiskCacheBytes = CACHE_SIZE;
		}

		requestQueue = new RequestQueue(new DiskBasedCache(new File(cacheDir, DEFAULT_CACHE_DIR), maxDiskCacheBytes), new BasicNetwork(stack));
		requestQueue.start();
	}

	public <T> Request<T> addRequest(Request<T> request) {
		if (requestQueue != null && request != null) {
			return requestQueue.add(request);
		}
		return null;
	}

	public Cache getCache() {
		if (requestQueue != null) {
			return requestQueue.getCache();
		}
		return null;
	}

	public RequestQueue getRequestQueue() {
		return requestQueue;
	}

	public void restart() {
		if (requestQueue != null) {
			requestQueue.start();
		}
	}

	public void cancelAll(final Object tag) {
		if (requestQueue != null) {
			requestQueue.cancelAll(tag);
		}
	}

	@Override
	public Bitmap getBitmap(String cacheKey) {
		if (cacheKey == null) {
			return null;
		}
		if (lruCache != null) {
			return lruCache.get(cacheKey);
		} else {
			return null;
		}

		// if (bitmap == null) {
		// try {
		// int p = cacheKey.indexOf("http://");
		// String url = cacheKey.substring(p);
		// DiskBasedCache diskBasedCache = (DiskBasedCache)
		// requestQueue.getCache();
		// Cache.Entry entry = diskBasedCache.get(url);
		// if (entry != null) {
		// BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		// decodeOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		// bitmap = BitmapFactory.decodeByteArray(entry.data, 0,
		// entry.data.length, decodeOptions);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// putBitmap(cacheKey, bitmap);
		// }
		// return bitmap;
	}

	@Override
	public void putBitmap(String cacheKey, Bitmap bitmap) {
		if (cacheKey != null && bitmap != null && lruCache != null) {
			lruCache.put(cacheKey, bitmap);
		}
	}

	public void setUrlImage(RequestQueue requestQueue, ImageView imageView, String imageUrl, int defaultImageResId, int errorImageResId) {
		if (context != null) {
			imageView.setTag(R.id.tag_id, imageUrl);
			ImageLoader imageLoader = new ImageLoader(context, requestQueue, this);
			ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(imageView, defaultImageResId, errorImageResId);
			imageLoader.get(imageUrl, imageListener);
		}
	}

	public void setUrlImage(RequestQueue requestQueue, ImageView imageView, String imageUrl, int defaultImageResId, int errorImageResId, int maxWidth,
			int maxHeight) {
		if (context != null) {
			imageView.setTag(R.id.tag_id, imageUrl);
			ImageLoader imageLoader = new ImageLoader(context, requestQueue, this);
			ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(imageView, defaultImageResId, errorImageResId);
			imageLoader.get(imageUrl, imageListener, maxWidth, maxHeight);
		}
	}

	public void setUrlImage(RequestQueue requestQueue, ImageView imageView, String imageUrl, int defaultImageResId, int errorImageResId, int maxWidth,
			int maxHeight, Object tag) {
		if (context != null) {
			imageView.setTag(R.id.tag_id, imageUrl);
			ImageLoader imageLoader = new ImageLoader(context, requestQueue, this);
			ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(imageView, defaultImageResId, errorImageResId);
			imageLoader.get(imageUrl, imageListener, maxWidth, maxHeight, ImageView.ScaleType.CENTER_INSIDE, tag);
		}
	}

	public Bitmap getBitmapForUrl(String imageUrl, int maxWidth, int maxHeight) {
		if (imageUrl == null)
			return null;

		final String cacheKey = ImageLoader.getCacheKey(imageUrl, maxWidth, maxHeight, ImageView.ScaleType.CENTER_INSIDE);
		return getBitmap(cacheKey);
	}

	public Bitmap getBitmapForUrl(String imageUrl) {
		return getBitmapForUrl(imageUrl, 0, 0);
	}

	public boolean writeBitmapToFile(String imageUrl, String dir, String filename) {
		if (requestQueue == null) {
			return false;
		}

		DiskBasedCache diskBasedCache = (DiskBasedCache) requestQueue.getCache();
		if (diskBasedCache != null) {
			Cache.Entry entry = diskBasedCache.get(imageUrl);
			if (entry != null) {
				File file = new File(dir, filename);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				try {
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(entry.data);
					fos.close();
					return true;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public JSONObject getJsonObject(String url) {
		if (requestQueue == null) {
			return null;
		}

		Cache cache = requestQueue.getCache();
		if (cache != null) {
			DiskBasedCache diskBasedCache = (DiskBasedCache) cache;
			Cache.Entry entry = diskBasedCache.get(url);
			if (entry != null) {
				try {
					return new JSONObject(new String(entry.data));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
