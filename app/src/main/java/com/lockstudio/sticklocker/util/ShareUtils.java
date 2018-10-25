package com.lockstudio.sticklocker.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.lockstudio.sticklocker.view.SimpleToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import cn.opda.android.activity.R;

/**
 * 分享的公共类
 * 
 * @author Ray
 * 
 */
public class ShareUtils {

	private Activity mContext;

	public ShareUtils(Activity mContext) {
		super();
		this.mContext = mContext;
		String wxAppId = MConstants.WX_APP_ID;
		String packageName = mContext.getPackageName();
		if ("cn.opda.android.activity".equals(packageName)) {
			wxAppId = MConstants.WX_APP_ID;
		} else if ("com.lockscreen.wenzisuoping".equals(packageName)) {
			wxAppId = MConstants.WX_APP_ID_1;
		} else if ("com.lockstudio.sticklocker".equals(packageName)) {
			wxAppId = MConstants.WX_APP_ID_2;
		}
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	/**
	 * 加入qq群
	 * 
	 * @author Ray
	 * @param key
	 * @return
	 */
	public boolean joinQQGroup(String key) {
		Intent intent = new Intent();
		intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
		try {
			mContext.startActivity(intent);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 分享软件到QQ空间
	 * 
	 * @author Ray
	 */
	public void shareAppToQzone() {
		final Bundle params = new Bundle();
	}

	/**
	 * 发送URL到QQ好友
	 * 
	 * @author Ray
	 */
	public void shareUrlToQQ(String title, String content, String url, String imageUrl) {
	}

	/**
	 * 发送本地图片到QQ好友
	 * 
	 * @author Ray
	 * @param path
	 */
	public void shareContentToQQ(String path) {
	}

	/**
	 * 发送URL到QQ好友
	 * 
	 * @author Ray
	 */
	public void shareUrlToQzone(String title, String content, String url, String imageUrl) {
		final Bundle params = new Bundle();
	}

	/**
	 * 发送本地图片到QQ空间
	 * 
	 * @author Ray
	 * @param path
	 */
	public void shareContentToQzone(String path) {
		final Bundle params = new Bundle();
	}

	/**
	 * 分享URL到微信
	 * 
	 * @author Ray
	 */
	public void sendURLToweixin(String title, String content, String imageUrl, String url, boolean isFriend) {
	}

	/**
	 * 分享图片到微信
	 * 
	 * @author Ray
	 * @param bitmap
	 * @param isFriend
	 *            是否发送给好友 ture 好友 false 朋友圈
	 */
	public void shareContentToWeixin(Bitmap bitmap, boolean isFriend) {
		if (bitmap == null) {
			return;
		}
	}

	/**
	 * 原图压缩方法
	 * 
	 * @author Ray
	 * @param bitmap
	 * @param size
	 * @return
	 */
	private byte[] compressBitmap(Bitmap bitmap, float size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int quality = 75;
		while (baos.toByteArray().length / 1024f > size) {
			quality = quality - 4;
			baos.reset();
			if (quality <= 0) {
				break;
			}
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		}
		RLog.i("share bitmap size", baos.toByteArray().length);
		return baos.toByteArray();
	}

	/**
	 * 压缩图片成为Bitmap
	 * 
	 * @author Ray
	 * @param image
	 * @return
	 */
	private Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 75;
		while (baos.toByteArray().length / 1024 > 100) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 4;
			if (options <= 0) {
				break;
			}
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		return BitmapFactory.decodeStream(isBm, null, null);
	}

	/**
	 * 图片压缩为320 480 大小
	 * 
	 * @author Ray
	 * @param image
	 * @return
	 */
	private Bitmap comp320480(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float ww = 320f;
		float hh = 480f;
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		RLog.i("share ThumbImage bitmap size", baos.toByteArray().length);
		return compressImage(bitmap);
	}


	/**
	 * 分享带网页信息
	 * 
	 * @param imageUrl
	 * @param url
	 * @param title
	 * @param text
	 */
	public void sendWeiBoMessage(String imageUrl, String url, String title, String text) {
	}

	public void sendWeiBoMessage(Bitmap bitmap, String text) {
	}


}
