package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FileUtils {

	public static String getFolderSize(Context mContext, File file) {
		long folderSize = FileUtils.FolderSize(file);
		// RLog.v("folderSize", folderSize);
		// String formatSize = FileUtils.getFormatSize(folderSize);
		// RLog.v("formatSize", formatSize);
		if (folderSize > 11000000) {
			return "点我";
		}
		return "";
	}

	private static long FolderSize(File file) {

		long size = 0;
		try {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()) {
					size = size + FolderSize(fileList[i]);

				} else {
					size = size + fileList[i].length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	/**
	 * 递归删除文件
	 * 
	 * @author 王雷
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.exists() == false) {
			return;
		} else {
			if (file.isFile()) {
				file.delete();
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					file.delete();
					return;
				}
				for (File f : childFile) {
					deleteFile(f);
				}
				file.delete();
			}
		}
	}

	/**
	 * 删除指定目录下文件及目录
	 * 
	 * @param deleteThisPath
	 * @param filepath
	 * @return
	 */
	public void deleteFolderFile(String filePath, boolean deleteThisPath) {
		if (!TextUtils.isEmpty(filePath)) {
			try {
				File file = new File(filePath);
				if (file.isDirectory()) {
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						deleteFolderFile(files[i].getAbsolutePath(), true);
					}
				}
				if (deleteThisPath) {
					if (!file.isDirectory()) {
						file.delete();
					} else {
						if (file.listFiles().length == 0) {
							file.delete();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void SaveIncludedFileIntoFilesFolder(String resource, String filename, Context mContext) throws Exception {
		FileInputStream fis = new FileInputStream(resource);
		FileOutputStream fos = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
		byte[] bytebuf = new byte[1024];
		int read;
		while ((read = fis.read(bytebuf)) >= 0) {
			fos.write(bytebuf, 0, read);
		}
		fis.close();
		fos.getChannel().force(true);
		fos.flush();
		fos.close();
	}

	/**
	 * 拷贝文件
	 * 
	 * @param ComeFromPath
	 *            源文件地址
	 * @param GoalPath
	 *            目标文件地址
	 */
	public static void copyFile(String ComeFromPath, String GoalPath) {
		try {
			int byteread = 0;
			File oldfile = new File(ComeFromPath);
			String folder = new File(GoalPath).getParent();
			if (!new File(folder).exists()) {
				new File(folder).mkdirs();
			}
			if (oldfile.exists()) {
				InputStream is = new FileInputStream(ComeFromPath);
				FileOutputStream fos = new FileOutputStream(GoalPath);
				byte[] buffer = new byte[1024];
				while ((byteread = is.read(buffer)) != -1) {
					fos.write(buffer, 0, byteread);
				}
				fos.flush();
				fos.close();
				is.close();
			}

		} catch (Exception e) {
			RLog.e("copyFile", e);
		}
	}

	/**
	 * 根据目录删除所有的文件
	 * 
	 * @param filePath
	 */
	public static void deleteFileByPath(String filePath) {
		File rootfile = new File(filePath);
		if (rootfile != null && rootfile.exists()) {
			if (rootfile.isDirectory()) {
				File[] files = rootfile.listFiles();
				if (files != null) {
					for (File childFile : files) {
						deleteFileByPath(childFile.getAbsolutePath());
					}
				}
				rootfile.delete();
			} else if (rootfile.isFile()) {
				rootfile.delete();
			}
		}
	}
	
	/**
	 * 根据目录删除所有的文件
	 * 
	 * @param filePath
	 */
	public static void deleteFileByPathIgnore(String filePath) {
		File rootfile = new File(filePath);
		if (rootfile != null && rootfile.exists()) {
			if (rootfile.isDirectory()) {
				File[] files = rootfile.listFiles();
				if (files != null) {
					for (File childFile : files) {
						deleteFileByPathIgnore(childFile.getAbsolutePath());
					}
				}
				rootfile.delete();
			} else if (rootfile.isFile()) {
				if(!rootfile.getName().equals(MConstants.isCloud)&&!rootfile.getName().equals(MConstants.uploaded)){
					rootfile.delete();
				}
			}
		}
	}

	/**
	 * 根据目录删除所有的文件
	 * 
	 * @param filePath
	 */
	public static void deleteFileByList(ArrayList<String> pathList) {
		if (pathList != null) {
			for (String string : pathList) {
				deleteFileByPath(string);
			}
		}

	}

	/**
	 * 将内容写到文件中
	 * 
	 * @param filePath
	 * @param content
	 */
	public static void write(String filePath, String content) {
		File file = new File(filePath);
		File file2 = file.getParentFile();
		if (!file2.exists()) {
			file2.mkdir();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedWriter bw = null;
		try {
			// 根据文件路径创建缓冲输出流
			bw = new BufferedWriter(new FileWriter(filePath));
			// 将内容写入文件中
			bw.write(content);
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					bw = null;
				}
			}
		}
	}

	public static String getFileString(File configFile) {
		FileInputStream inputStream = null;
		StringBuffer sb = new StringBuffer();
		try {
			inputStream = new FileInputStream(configFile);

			int len = 0;
			byte[] buff = new byte[4096];
			while ((len = inputStream.read(buff)) != -1) {
				sb.append(new String(buff, 0, len));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	public static void copyAssetFile(Context context, String fileName) {
		InputStream is = null;
		FileOutputStream os = null;
		try {
			is = context.getAssets().open(fileName);
			os = new FileOutputStream(context.getFilesDir() + "/" + fileName);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
				if (os != null)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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

	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		// 新建文件输入流并对它进行缓冲
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		// 刷新此缓冲的输出流
		outBuff.flush();

		// 关闭流
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}

	public static void copyFolder(File sourceFile, File targetFile) throws IOException {

		if (sourceFile.exists()) {
			if (!targetFile.exists()) {
				targetFile.mkdirs();
			}
			File[] files = sourceFile.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					copyFile(file, new File(targetFile, file.getName()));
				}
			}
		}
	}

	public static void copyFile(InputStream input, File targetFile) throws IOException {
		// 新建文件输入流并对它进行缓冲
		BufferedInputStream inBuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		// 刷新此缓冲的输出流
		outBuff.flush();

		// 关闭流
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}

	/**
	 * 判断手机是否有SD卡。
	 * 
	 * @return 有SD卡返回true，没有返回false。
	 */
	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/**
	 * 从手机或者sd卡获取Bitmap
	 * 
	 * @param fileName
	 * @return
	 */
	public static Bitmap getBitmap(String fileName, boolean needOption, int inSampleSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		if (needOption) {
			options.inSampleSize = inSampleSize;
		} else {
			options.inSampleSize = 1;
		}
		return BitmapFactory.decodeFile(MConstants.IMAGECACHE_PATH + File.separator + fileName, options);
	}

	public static Bitmap getBitmap(String path, String fileName, boolean needOption, int inSampleSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		if (needOption) {
			options.inSampleSize = inSampleSize;
		} else {
			options.inSampleSize = 1;
		}
		return BitmapFactory.decodeFile(new File(path, fileName).getAbsolutePath(), options);
	}

	/**
	 * 从手机或者sd卡获取Bitmap
	 * 
	 * @param fileName
	 * @return
	 */
	public static Bitmap getBitmap(String fileName) {
		return BitmapFactory.decodeFile(MConstants.IMAGECACHE_PATH + File.separator + fileName);
	}

	/**
	 * 从手机或者sd卡获取Bitmap
	 * 
	 * @param fileName
	 * @return
	 */
	public static Bitmap getBitmap(String path, String fileName) {
		return BitmapFactory.decodeFile(new File(path, fileName).getAbsolutePath());
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isImageExists(String fileName) {
		return new File(MConstants.IMAGECACHE_PATH, fileName).exists();
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isImageExists(String path, String fileName) {
		return new File(path, fileName).exists();
	}

	/**
	 * 判断apk文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isApkExists(String fileName) {
		return new File(MConstants.DOWNLOAD_PATH + File.separator + fileName).exists();
	}

	/**
	 * 判断临时文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isTempApkExists(String fileName) {
		return new File(MConstants.DOWNLOAD_PATH + File.separator + fileName + ".temp").exists();
	}

}
