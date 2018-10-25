package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import cn.opda.android.activity.R;

/**
 * 图片缩放工具类
 * 
 * @author 庄宏岩
 * 
 */
public class DrawableUtils {

	// 放大缩小图片
	public static Bitmap scaleTo(final Bitmap bitmapOrg, final int newWidth, final int newHeight) {
		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();
		if (width == 0 || height == 0) {
			return bitmapOrg;
		}
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		return Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
	}

	// 放大缩小图片
	public static Bitmap scaleTo(final Bitmap bitmapOrg, final float scaleWidth, final float scaleHeight) {
		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		return Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
	}

	public static Bitmap byte2Bitmap(Context mContext, byte[] b) {
		if (b != null) {
			if (b.length != 0) {
				return BitmapFactory.decodeByteArray(b, 0, b.length);
			} else {
				return BitmapFactory.decodeResource(mContext.getResources(), android.R.drawable.sym_def_app_icon);
			}
		}
		return BitmapFactory.decodeResource(mContext.getResources(), android.R.drawable.sym_def_app_icon);
	}

	public static Bitmap drawable2Bitmap(Context mContext, Drawable drawable) {
		if (drawable instanceof ColorDrawable) {
			return null;
		}
		return ((BitmapDrawable) drawable).getBitmap();
	}

	public static Bitmap getBitmap(Context mContext, int resId) {
		return BitmapFactory.decodeResource(mContext.getResources(), resId);
	}

	public static Bitmap getBitmap(Context mContext, String file) {
		if (new File(file).exists()) {
			final Options options = new Options();
			options.inJustDecodeBounds = true;
			options.inPreferredConfig = Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			if (ImageLoader.getFileSize(new File(file)) > (5 * 1024 * 1024)) {
				options.inSampleSize = 5;
			} else if (ImageLoader.getFileSize(new File(file)) > (1024 * 1024)) {
				options.inSampleSize = 2;
			} else {
				options.inSampleSize = 1;
			}
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(file, options);
		}
		return null;
	}

	public static Bitmap getBitmap(Context mContext, String file, Options options) {
		if (new File(file).exists()) {
			return BitmapFactory.decodeFile(file, options);
		}
		return null;
	}

	public static Bitmap getBitmap(Context mContext, InputStream inputStream) {
		if (inputStream != null) {
			return BitmapFactory.decodeStream(inputStream);
		}
		return null;
	}

	public static byte[] drawable2Byte(Drawable icon) {
		Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static byte[] bitmap2Byte(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static Drawable bitmap2Drawable(Context mContext, Bitmap bitmap) {
		return new BitmapDrawable(mContext.getResources(), bitmap);
	}

	public static Drawable byte2Drawable(Context mContext, byte[] b) {
		if (b != null) {
			if (b.length != 0) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
				return new BitmapDrawable(mContext.getResources(), bitmap);
			} else {
				return null;
			}
		}
		return null;
	}

	public static void saveBitmap(File path, Bitmap bitmap, boolean simple) {
		try {
			FileOutputStream out = new FileOutputStream(path);
			bitmap.compress(simple ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG, simple ? 85 : 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Bitmap drawResourceIcon(Context mContext, ArrayList<Bitmap> lockImages) {
		if (lockImages == null)
			return null;

		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		ArrayList<Bitmap> bitmaps;
		int[] posX = new int[9];
		int[] posY = new int[9];
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		int width = dm.widthPixels / 2;
		int x = 5;
		int w = (width - 4 * x) / 3;
		bitmaps = new ArrayList<Bitmap>();

		ArrayList<Integer> positions = new ArrayList<Integer>();
		if (lockImages.size() > 9) {
			while (true) {
				int i = new Random().nextInt(lockImages.size() - 1);
				if (!positions.contains(i)) {
					positions.add(i);
				}
				if (positions.size() == 9) {
					break;
				}
			}
			ArrayList<Bitmap> newBitmaps = new ArrayList<Bitmap>();
			for (int i = 0; i < positions.size(); i++) {
				newBitmaps.add(lockImages.get(positions.get(i)));
			}
			lockImages.clear();
			lockImages.addAll(newBitmaps);
		}

		if (lockImages.size() == 2) {
			for (int i = 0; i < 9; i++) {
				int ix = i % 3;
				int iy = i / 3;
				if (ix == 0) {
					posX[i] = x;
				}
				if (iy == 0) {
					posY[i] = x;
				}
				if (ix == 1) {
					posX[i] = 2 * x + w;
				}
				if (iy == 1) {
					posY[i] = 2 * x + w;
				}
				if (ix == 2) {
					posX[i] = 3 * x + 2 * w;
				}
				if (iy == 2) {
					posY[i] = 3 * x + 2 * w;
				}
				if (i == 4) {
					bitmaps.add(scaleTo(lockImages.get(1), w, w));
				} else {
					bitmaps.add(scaleTo(lockImages.get(0), w, w));
				}
			}
		} else if (lockImages.size() >= 9) {
			for (int i = 0; i < 9; i++) {
				int ix = i % 3;
				int iy = i / 3;
				if (ix == 0) {
					posX[i] = x;
				}
				if (iy == 0) {
					posY[i] = x;
				}
				if (ix == 1) {
					posX[i] = 2 * x + w;
				}
				if (iy == 1) {
					posY[i] = 2 * x + w;
				}
				if (ix == 2) {
					posX[i] = 3 * x + 2 * w;
				}
				if (iy == 2) {
					posY[i] = 3 * x + 2 * w;
				}
				bitmaps.add(scaleTo(lockImages.get(i), w, w));
			}
		} else if (lockImages.size() != 0) {
			for (int i = 0; i < 9; i++) {
				int ix = i % 3;
				int iy = i / 3;
				if (ix == 0) {
					posX[i] = x;
				}
				if (iy == 0) {
					posY[i] = x;
				}
				if (ix == 1) {
					posX[i] = 2 * x + w;
				}
				if (iy == 1) {
					posY[i] = 2 * x + w;
				}
				if (ix == 2) {
					posX[i] = 3 * x + 2 * w;
				}
				if (iy == 2) {
					posY[i] = 3 * x + 2 * w;
				}
				if (i < lockImages.size()) {
					bitmaps.add(scaleTo(lockImages.get(i), w, w));
				} else {
					bitmaps.add(getBitmap(mContext, R.drawable.translucent_bitmap));
				}

			}
		}
		if (bitmaps.size() == 9) {
			Bitmap bitmap = Bitmap.createBitmap(width, width, Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			canvas.drawColor(0x00000000);
			for (int i = 0; i < 9; i++) {
				canvas.drawBitmap(bitmaps.get(i), posX[i], posY[i], paint);
			}
			canvas.save(Canvas.ALL_SAVE_FLAG);// 保存
			canvas.restore();
			return bitmap;
		}
		return null;

	}

	public static Drawable getDrawableCustomColor(Context context, int resId, int color) {
		Bitmap oldBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
		if (oldBitmap == null)
			return null;
		Drawable oldDrawable = context.getResources().getDrawable(resId);
		int w = oldBitmap.getWidth();
		int h = oldBitmap.getHeight();
		Bitmap newBitmap = oldBitmap.copy(Config.ARGB_8888, true);
		Canvas canvas = new Canvas(newBitmap);
		canvas.translate(0f, 0f);
		if (oldDrawable != null) {
			PorterDuffColorFilter localPorterDuffColorFilter = new PorterDuffColorFilter(color, Mode.MULTIPLY);
			oldDrawable.setBounds(0, 0, w, h);
			oldDrawable.setColorFilter(localPorterDuffColorFilter);
			oldDrawable.draw(canvas);
		}
		return new BitmapDrawable(context.getResources(), newBitmap);
	}

	public static Drawable getDrawableCustomShadowColor(Context context, int resId, int color) {
		Bitmap oldBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
		int w = oldBitmap.getWidth();
		int h = oldBitmap.getHeight();
		Bitmap newBitmap = Bitmap.createBitmap(w + 20, h + 20, Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShadowLayer(10, 0, 0, color);
		canvas.drawBitmap(oldBitmap, 10, 10, paint);
		return new BitmapDrawable(context.getResources(), newBitmap);
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xffffffff;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
	
	public static void saveTempImage(final Bitmap bitmap, final String filename) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				File fileDir = new File(MConstants.TEMP_IMAGE_PATH);
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}

				File file = new File(fileDir, HASH.md5sum(filename));
				if (!file.exists()) {
					saveBitmap(file, bitmap, false);
				} else {
					return;
				}
				File[] files = fileDir.listFiles();
				if (files != null && files.length > 24) {

				}
			}
		}).start();

	}

}
