package com.lockstudio.sticklocker.util;

import com.lockstudio.sticklocker.Interface.FontDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class FontUtils {
	/**
	 * 
	 * @param ttfSuoXie
	 *            ttf英文缩写
	 * @param url
	 *            url最后路径
	 * @param filePath
	 *            文件路径
	 */

	public static void downloadTTF(final String ttfSuoXie, final String url) {
		try {
			String encode = URLEncoder.encode(url, "UTF-8");
			long totalSize = 0;
			long downloadSize = 0;
			File imageFile = new File(getFontPath(url));
			File imageFileTemp = new File(getFontPath(url) + ".temp");
			URL enurl = new URL("http://open.api.opda.com/ttf/make.php?ft=" + ttfSuoXie + "&c=" + encode);
			HttpURLConnection conn = (HttpURLConnection) enurl.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			int code = conn.getResponseCode();
			if (code == 200) {
				totalSize = conn.getContentLength();
				InputStream inputStream = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(imageFileTemp);
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
			// return filePath;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return null;
	}

	/**
	 * 获取字体文件路径
	 * 
	 * @author Ray
	 * @param fontUrl
	 * @return
	 */
	public static String getFontPath(String fontUrl) {
		String fontDir = MConstants.TTF_PATH;
		File file = new File(fontDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		String fontPath = fontDir + "/" + HASH.md5sum(fontUrl);
		return fontPath;
	}

	/**
	 * 获取字体(本地有则读取本地,没有则下载)
	 * 
	 * @author Ray
	 * @param fontUrl
	 * @return 字体路径
	 */
	public static void loadTtf(String fontUrl, FontDownloadListener downloadListener) {
		File fontFile = new File(getFontPath(fontUrl));
		if (!fontFile.exists() | fontFile.length() == 0) {
			downloadTTF(fontUrl, downloadListener);
		} else {
			downloadListener.finish(fontUrl,fontFile.getAbsolutePath());
		}
	}

	/**
	 * 下载字体
	 * 
	 * @author Ray
	 * @param fontUrl
	 */
	public static void downloadTTF(final String url, FontDownloadListener downloadListener) {
		try {
			long totalSize = 0;
			long downloadSize = 0;
			File imageFile = new File(getFontPath(url));
			File imageFileTemp = new File(getFontPath(url) + ".temp");
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
