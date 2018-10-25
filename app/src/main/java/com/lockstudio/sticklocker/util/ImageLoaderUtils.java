package com.lockstudio.sticklocker.util;

import android.graphics.Bitmap;

import com.lockstudio.sticklocker.Interface.ImageDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoaderUtils {
	
	public static Bitmap loadImage(ImageLoader imageLoader, String imageUrl) {
		File imageFile = new File(ImageLoaderUtils.getImagePath(imageUrl));
		if (!imageFile.exists() | imageFile.length() == 0) {
			ImageLoaderUtils.downloadImage(imageLoader, imageUrl);
		}
		if (imageUrl != null) {
			Bitmap bitmap = imageLoader.decodeSampledBitmapFromResource(imageFile.getPath());
			if (bitmap != null) {
				imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
				return bitmap;
			}
		}
		return null;
	}

	public static void downloadImage(final ImageLoader imageLoader, final String imageUrl) {
		FileOutputStream fos = null;
		File imageFile = null;
		File imageFileTemp = null;
		long totalSize = 0;
		long downloadSize = 0;
		imageFile = new File(getImagePath(imageUrl));
		imageFileTemp = new File(getImagePath(imageUrl) + ".temp");
		try {
			imageFileTemp.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			int code = conn.getResponseCode();
			conn.setConnectTimeout(5 * 1000);
			conn.setReadTimeout(15 * 1000);
			if (code == 200) {
				totalSize = conn.getContentLength();
				InputStream inputStream = conn.getInputStream();
				fos = new FileOutputStream(imageFileTemp);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = inputStream.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					downloadSize += len;
				}
				fos.flush();
				inputStream.close();
				fos.close();
				if (totalSize == downloadSize) {
					imageFileTemp.renameTo(imageFile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		if (imageFile != null) {
			Bitmap bitmap = imageLoader.decodeSampledBitmapFromResource(imageFile.getPath());
			if (bitmap != null) {
				imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
			}
		}
	}

	public static String getImagePath(String imageUrl) {
		String imageDir = MConstants.IMAGECACHE_PATH;
		File file = new File(imageDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		String imagePath = imageDir + HASH.md5sum(imageUrl);
		return imagePath;
	}
	
	public static void loadImage(String imageUrl, ImageDownloadListener downloadListener) {
		File file = new File(getImagePath(imageUrl));
		if (!file.exists() | file.length() == 0) {
			download(imageUrl, downloadListener);
		} else {
			downloadListener.finish(imageUrl,file.getAbsolutePath());
		}
	}

	public static void download(final String url, ImageDownloadListener downloadListener) {
		try {
			long totalSize = 0;
			long downloadSize = 0;
			File imageFile = new File(getImagePath(url));
			File imageFileTemp = new File(getImagePath(url) + ".temp");
			URL enurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) enurl.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			int code = conn.getResponseCode();
			int progress = 0;
			if (code == 200) {
				totalSize = conn.getContentLength();
				InputStream inputStream = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(imageFileTemp);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = inputStream.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					downloadSize += len;
					int newprogress = (int) (downloadSize * 100 / totalSize);
					if (newprogress > progress) {
						progress = newprogress;
						downloadListener.downloading(url,progress);
					}
				}
				fos.flush();
				inputStream.close();
				fos.close();
				if (totalSize == downloadSize) {
					imageFileTemp.renameTo(imageFile);
					downloadListener.finish(url,imageFile.getAbsolutePath());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			downloadListener.error(url);
		}
	}
	
}
