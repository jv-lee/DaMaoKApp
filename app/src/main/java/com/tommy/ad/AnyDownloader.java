package com.tommy.ad;

import android.text.TextUtils;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

/**
 * Created by Tommy on 15/6/25.
 */
public class AnyDownloader {

    public static final int STATE_NEW = 1;
    public static final int STATE_CONTINUE = 2;
    public static final int STATE_EXIST = 3;
    public static final int STATE_DOWNLOADED = 4;



    private final static String TMP = ".tmp";
    private final static String CFG = ".cfg";


    private String url;
    private String savePath;
    private String fileName;
    private String title;
    private int retry = 3;
    private long totalSize = 0;
    private long finishedSize = 0;

    private Callback callback = null;


    public AnyDownloader() {
    }

    public int start(String url, String downloadDir, String title) {
        return start(url, downloadDir, title, null, 3);
    }

    public int start(String url, String downloadDir, String title, Callback callback) {
        return start(url, downloadDir, title, callback, 3);
    }

    public int start(String url, String downloadDir, String title, Callback callback, int retry) {

        if (callback != null) {
            this.callback = callback;
        }

        if (TextUtils.isEmpty(url)) {
            if (callback != null) {
                callback.onAnyDownloadFailed(url, title, downloadDir, HASH.md5sum(url), -1);
            }
            return -1;
        }
        if (TextUtils.isEmpty(downloadDir)) {
            if (callback != null) {
                callback.onAnyDownloadFailed(url, title, downloadDir, HASH.md5sum(url), -2);
            }
            return -2;
        }
        if (TextUtils.isEmpty(title)) {
            if (callback != null) {
                callback.onAnyDownloadFailed(url, title, downloadDir, HASH.md5sum(url), -3);
            }
            return -3;
        }


        this.url = url;
        this.savePath = downloadDir;
        this.title = title;
        this.retry = retry;
        this.fileName = HASH.md5sum(url);

        File dir = new File(downloadDir);
        if (!dir.exists()) {
            boolean b = dir.mkdirs();
        } else {
        }

        readConfig();
        startDownload();

        return 0;
    }


    static void writeLong(OutputStream os, long n) throws IOException {
        os.write((byte) (n >>> 0));
        os.write((byte) (n >>> 8));
        os.write((byte) (n >>> 16));
        os.write((byte) (n >>> 24));
        os.write((byte) (n >>> 32));
        os.write((byte) (n >>> 40));
        os.write((byte) (n >>> 48));
        os.write((byte) (n >>> 56));
    }

    static long readLong(InputStream is) throws IOException {
        long n = 0;
        n |= ((read(is) & 0xFFL) << 0);
        n |= ((read(is) & 0xFFL) << 8);
        n |= ((read(is) & 0xFFL) << 16);
        n |= ((read(is) & 0xFFL) << 24);
        n |= ((read(is) & 0xFFL) << 32);
        n |= ((read(is) & 0xFFL) << 40);
        n |= ((read(is) & 0xFFL) << 48);
        n |= ((read(is) & 0xFFL) << 56);
        return n;
    }

//    static void writeString(OutputStream os, String s) throws IOException {
//        byte[] b = s.getBytes("UTF-8");
//        writeLong(os, b.length);
//        os.write(b, 0, b.length);
//    }
//
//    static String readString(InputStream is) throws IOException {
//        int n = (int) readLong(is);
//        byte[] b = streamToBytes(is, n);
//        return new String(b, "UTF-8");
//    }

    private static int read(InputStream is) throws IOException {
        int b = is.read();
        if (b == -1) {
            throw new EOFException();
        }
        return b;
    }

//    private static byte[] streamToBytes(InputStream in, int length) throws IOException {
//        byte[] bytes = new byte[length];
//        int count;
//        int pos = 0;
//        while (pos < length && ((count = in.read(bytes, pos, length - pos)) != -1)) {
//            pos += count;
//        }
//        if (pos != length) {
//            throw new IOException("Expected " + length + " bytes, read " + pos + " bytes");
//        }
//        return bytes;
//    }


    public interface Callback {
        void onAnyDownloadStart(int state, final String url, final String title, final String path, final String name);

        void onAnyDownloadFinished(int state, final String url, final String title, final String path, final String name);

        void onAnyDownloadFailed(final String url, final String title, final String path, final String name, int errorCode);

        void onAnyDownloadUpdate(final String url, final String title, final String path, final String name, long totalSize, long finishSize);
    }


    public static class HASH {
        private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        private static String toHexString(byte[] bytes) {
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(HEX_DIGITS[(b & 0xf0) >>> 4]);
                sb.append(HEX_DIGITS[b & 0x0f]);
            }
            return sb.toString();
        }

        public static String md5sum(String str) {
            byte[] strByte = str.getBytes();
            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("MD5");
                md5.reset();
                md5.update(strByte, 0, strByte.length);
                return toHexString(md5.digest());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

//        public static String md5sumWithFile(String filename) {
//            byte[] buffer = new byte[4096];
//            int numRead = 0;
//            MessageDigest md5;
//            try {
//                InputStream fis = new FileInputStream(filename);
//                md5 = MessageDigest.getInstance("MD5");
//                md5.reset();
//                while ((numRead = fis.read(buffer)) > 0) {
//                    md5.update(buffer, 0, numRead);
//                }
//                fis.close();
//                return toHexString(md5.digest());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return "";
//        }
    }

    private void readConfig() {

        File file = new File(savePath, fileName + CFG);
        if (file.exists() && file.isFile()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                totalSize = readLong(fis);
                finishedSize = readLong(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void writeConfig() {

        File file = new File(savePath, fileName + CFG);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            writeLong(fos, totalSize);
            writeLong(fos, finishedSize);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private HttpURLConnection getHttpURLConnection() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(this.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(true);
            if (this.finishedSize > 0) {
                connection.setRequestProperty("RANGE", "bytes=" + this.finishedSize + "-");
            }
            connection.setRequestProperty("User-Agent", "AnyDownloader");
            connection.setReadTimeout(30 * 1000);
            connection.setUseCaches(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void callbackDownloadStart(int state) {
        if (callback != null) {
            callback.onAnyDownloadStart(state, this.url, this.title, this.savePath, this.fileName);
        }
    }

    private void callbackDownloadFinish(int state) {
        File f = new File(this.savePath, this.fileName + CFG);
        if (f.exists()) {
            boolean b = f.delete();
        }

        if (callback != null) {
            callback.onAnyDownloadFinished(state, this.url, this.title, this.savePath, this.fileName);
        }
    }

    private void callbackDownloadFailed(int errorCode) {
        if (callback != null) {
            callback.onAnyDownloadFailed(this.url, this.title, this.savePath, this.fileName, errorCode);
        }
    }

    private void callbackDownloadUpdate() {
        if (callback != null) {
            callback.onAnyDownloadUpdate(this.url, this.title, this.savePath, this.fileName, this.totalSize, this.finishedSize);
        }
    }


    private void reDownload() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (retry > 0) {
            retry--;
            startDownload();
        } else {
            callbackDownloadFailed(-1);
        }
    }

    private void startDownload() {
        HttpURLConnection connection = getHttpURLConnection();
        if (connection != null) {
            int code = 0;
            try {
                code = connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (code < HttpURLConnection.HTTP_OK || code >= HttpURLConnection.HTTP_BAD_REQUEST) {

                connection.disconnect();
                reDownload();
                return;
            }

            int length = connection.getContentLength();
//            Log.d("Ray", "length=" + length);
//            Log.d("Ray", "finishedSize=" + finishedSize);
//            Log.d("Ray", "totalSize=" + totalSize);

            File f = new File(savePath, fileName);
            File tmpFile = new File(savePath, fileName + TMP);
            if (f.exists()) {
                if (f.length() == length) {
                    connection.disconnect();
                    callbackDownloadFinish(STATE_EXIST);
                    return;
                } else {
                    f.delete();

                    if (tmpFile.exists()) {
                        tmpFile.delete();
                    }

                    connection.disconnect();

                    totalSize = finishedSize = 0;
                    writeConfig();

                    reDownload();
                    return;
                }
            }

            if (finishedSize == 0 || totalSize == 0) {
                totalSize = length;
            } else {
                if ((length + finishedSize) != totalSize) {
//                    RLog.d("length", "length != totalSize");
                    totalSize = finishedSize = 0;
                    writeConfig();

                    if (tmpFile.exists()) {
                        tmpFile.delete();
                    }

                    connection.disconnect();
                    reDownload();
                    return;
                }
            }

//            RLog.d("inputStream", "开始 inputStream");
            InputStream inputStream = null;
            try {
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (inputStream == null) {
                connection.disconnect();
                reDownload();
                return;
            }

            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(tmpFile, "rw");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (raf == null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                connection.disconnect();
                callbackDownloadFailed(-2);
                return;
            }

            long tmpLen = 0;
            try {
                tmpLen = raf.length();
            } catch (IOException e) {
                e.printStackTrace();
            }
//                    if (tmpLen == 0 ) {
//                        anyModel.setDownloadedSize(0);
//                        anyModel.setTotalSize(0);
//
//                        try {
//                            inputStream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        connection.disconnect();
//                        reDownload();
//                        return;
//                    }

            if (finishedSize > tmpLen) {
                totalSize = finishedSize = 0;
                writeConfig();

                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                connection.disconnect();

                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
                reDownload();
                return;
            }

            byte[] buffer = new byte[8192];
            int len = 0;
            try {
                raf.seek(this.finishedSize);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (finishedSize == 0) {
                callbackDownloadStart(STATE_NEW);
            } else {
                callbackDownloadStart(STATE_CONTINUE);
            }

//            RLog.d("开始下载", "" + raf);
            try {
                while ((len = inputStream.read(buffer)) != -1) {
                    raf.write(buffer, 0, len);
                    this.finishedSize += len;
                    writeConfig();

                    callbackDownloadUpdate();
                }
                inputStream.close();
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (this.finishedSize == this.totalSize || this.totalSize < 0) {
//                RLog.d("下载完成", "" + this.finishedSize);
                if (tmpFile.renameTo(new File(savePath, fileName))) {
                    callbackDownloadFinish(STATE_DOWNLOADED);
                } else {
                    callbackDownloadFailed(-1);
                }
            } else {
                callbackDownloadFailed(-100);
            }
            connection.disconnect();
        } else {
            reDownload();
        }
    }

}
