package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

public class NetworkUtil {

    private static final String TAG = "NetworkUtil";
    private static String networkIp = "127.0.0.1";

    /**
     * 得到网络连接类型
     *
     * @param context
     * @return
     */
    public static String NetType(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                String typeName = info.getTypeName(); // WIFI/MOBILE
                if (typeName.equalsIgnoreCase("wifi")) {

                } else {
                    typeName = info.getExtraInfo();
                    // 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
                }
                RLog.i(TAG, "IAP name is " + typeName);
                return typeName;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否是wifi
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 得到网络连接
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static HttpURLConnection getURLConnection(final String url, Context ctx) throws Exception {

        final String netType = NetType(ctx);
        String proxyHost = android.net.Proxy.getHost(ctx);
        // String proxyHost = android.net.Proxy.getDefaultHost();
        if (proxyHost != null) {
            java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(android.net.Proxy.getHost(ctx), android.net.Proxy.getPort(ctx)));
            // java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP,
            // new InetSocketAddress(android.net.Proxy.getDefaultHost(),
            // android.net.Proxy.getDefaultPort()));

            RLog.i(TAG, "The Host is " + p.toString());
            if ("wifi".equalsIgnoreCase(netType)) {

                return (HttpURLConnection) new URL(url).openConnection();
            } else {

                return (HttpURLConnection) new URL(url).openConnection(p);
            }

        } else {

            RLog.i(TAG, "There is no Host & proxy!!!");
            return (HttpURLConnection) new URL(url).openConnection();
        }
    }

    /**
     * 获取本机ip
     *
     * @param context
     * @return 如果能得到ip则可以正常上网，如得到的是null则网络异�?
     */
    public static String getNetworkIp(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.isConnected()) {
            RLog.i(TAG, "NetworkInfo is ok!");
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {

                        InetAddress inetAddress = enumIpAddr.nextElement();

                        if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().toString() != null) {

                            RLog.i(TAG, "HostAddress[" + inetAddress.getHostAddress().toString() + "]");
                            RLog.i(TAG, "Address[" + inetAddress.getAddress().toString() + "]");
                            RLog.i(TAG, "HostName[" + inetAddress.getHostName().toString() + "]");
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (SocketException ex) {
                RLog.e(TAG, ex.toString());
                return null;
            }
        }
        return null;
    }

    /**
     * 快�?判断网络是否正常
     *
     * @param context
     * @return
     */
    public static boolean getNetWorkState(final Context context) {
        networkIp = "127.0.0.1";
        long start = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                networkIp = getNetworkIp(context);
            }
        }).start();
        while (true) {
            // RLog.i("debug", "networkIp  = " + networkIp);
            if (networkIp == null) {
                return false;
            } else {
                if (System.currentTimeMillis() - start > 50) {
                    return true;
                } else {
                    if (!"127.0.0.1".equals(networkIp)) {
                        return true;
                    }
                }
            }
        }
    }

    /**
     * 格式化url连接
     *
     * @param url
     * @return
     */
    public static String fixUrl(final String url) {
        String fixedUrl = url;
        if (!url.startsWith("http://")) {
            fixedUrl = "http://" + url;
        }
        fixedUrl.replaceAll(" ", "%20");
        // fixedUrl.replaceAll("+", "%2B");

        return fixedUrl;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isConnected();
//                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    public static boolean ping() {
        String result = null;

        try {
            String ip = "www.baidu.com";// 除非百度挂了，否则用这个应该没问题~
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 10 " + ip);//ping3次

            // PING的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "successful~";
                return true;
            } else {
                result = "failed~ cannot reach the IP address";
            }
        } catch (IOException e) {
            result = "failed~ IOException";
        } catch (InterruptedException e) {
            result = "failed~ InterruptedException";
        } finally {
//            Log.i("TTT", "result = " + result);
        }

        return false;
    }


    public static String getNetIp() {
        URL infoUrl = null;
        InputStream inStream = null;
        try {
            infoUrl = new URL("http://www.ip.cn");
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    //从反馈的结果中提取出IP地址
                    int start = line.indexOf("<code>");
                    int end = line.indexOf("</code>", start + 6);

                    if (start != -1 && end != -1 && end > start) {
                        line = line.substring(start + 6, end);
                        break;
                    }
                }
                inStream.close();

                return line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
