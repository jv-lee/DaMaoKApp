package com.lockstudio.sticklocker.http;

import android.content.Context;

import com.lockstudio.sticklocker.util.NetworkUtil;
import com.lockstudio.sticklocker.util.RLog;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * 采用HttpURLConnection进行网络交互，改工具包含文件发送、cookie的维护、支持gzip,deflate传输格式功能
 */
public class CustomHttpClient {
	public int connTimeOut = 20000;
	public int readTimeOut = 20000;
	public boolean sendCookie = true;
	public static final String UTF8 = "utf-8";

	private CustomHttpClient() {
	}

	private CustomHttpClient(int connTimeOut, int readTimeOut) {
		this.connTimeOut = connTimeOut;
		this.readTimeOut = readTimeOut;
	}

	public static CustomHttpClient getInstance(int connTimeOut, int readTimeOut) {
		return new CustomHttpClient(connTimeOut, readTimeOut);
	}

	public static CustomHttpClient getInstance() {
		return new CustomHttpClient();
	}

	/**
	 * 
	 * 直接通过HTTP协议提交数据到服务器,实现如下面表单提交功能:
	 * 
	 * <FORM METHOD=POST ACTION="http://192.168.0.200:8080/ssi/fileload/test.do"
	 * enctype="multipart/form-data"> <INPUT TYPE="text" NAME="name"> <INPUT
	 * TYPE="text" NAME="id"> <input type="file" name="imagefile"/> <input
	 * type="file" name="zip"/> </FORM>
	 * 
	 * @param actionUrl
	 *            上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，你可以使用http://
	 *            www
	 *            .cnblogs.com/guoshiandroid或http://192.168.1.10:8080这样的路径测试)
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 * @throws Exception
	 * @throws IOException
	 */
	public String post(Context context, String actionUrl, String encoding, Map<String, String> params, FormFile... files) throws Exception {
		DataOutputStream outStream = null;
		HttpURLConnection conn = null;
		BufferedReader brout = null;
		try {
			String BOUNDARY = "---------7d4a6d158c9"; // 数据分隔线
			String MULTIPART_FORM_DATA = "multipart/form-data";
			conn = NetworkUtil.getURLConnection(actionUrl, context);
			conn.setConnectTimeout(connTimeOut);
			conn.setReadTimeout(readTimeOut);
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);// 不使用Cache
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			conn.setRequestProperty("Charset", encoding);
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
			StringBuilder sb = new StringBuilder();
			if (params != null)
				for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
					sb.append("--");
					sb.append(BOUNDARY);
					sb.append("\r\n");
					sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
					sb.append(entry.getValue());
					sb.append("\r\n");
				}
			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());// 发送表单字段数据
			if (files != null)
				for (FormFile file : files) {// 发送文件数据
					if (file == null)
						continue;
					StringBuilder split = new StringBuilder();
					split.append("--");
					split.append(BOUNDARY);
					split.append("\r\n");
					split.append("Content-Disposition: form-data;name=\"" + file.getParameterName() + "\";filename=\"" + file.getFilename() + "\"\r\n");
					split.append("Content-Type: " + file.getContentType() + "\r\n\r\n");
					outStream.write(split.toString().getBytes());
					if (file.getInStream() != null) {
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = file.getInStream().read(buffer)) != -1) {
							outStream.write(buffer, 0, len);
						}
						file.getInStream().close();
					} else {
						outStream.write(file.getData(), 0, file.getData().length);
					}
					outStream.write("\r\n".getBytes());
					RLog.i("debug", "发送文件成功--------------");
				}
			byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志
			outStream.write(end_data);
			outStream.flush();
			int code = conn.getResponseCode();
			if (code != 200)
				throw new Exception("服务器异常: " + code);
			String contentEncoding = conn.getContentEncoding();
			InputStream is = null;
			if ((null != contentEncoding) && (-1 != contentEncoding.indexOf("gzip"))) {
				is = new GZIPInputStream(conn.getInputStream());
			} else if ((null != contentEncoding) && (-1 != contentEncoding.indexOf("deflate"))) {
				is = new InflaterInputStream(conn.getInputStream());
			} else {
				is = conn.getInputStream();
			}

			InputStreamReader isrout = new InputStreamReader(is);
			brout = new BufferedReader(isrout, 1024);
			char[] ch = new char[1024];
			StringBuilder b = new StringBuilder();
			int length = 0;
			while ((length = brout.read(ch)) != -1) {
				b.append(ch, 0, length);
			}

			return b.toString();
		} finally {
			if (brout != null)
				brout.close();
			if (outStream != null)
				outStream.close();
			if (conn != null)
				conn.disconnect();
		}

	}

	public String post(Context context, String actionUrl, String encoding, Map<String, String> params) throws Exception {
		return post(context, actionUrl, encoding, params, (FormFile) null);
	}

	private List<String> cookies = new ArrayList<String>();// 接受回来的cookie

	/**
	 * 获取Http响应头字段
	 * 
	 * @param http
	 * @return
	 */
	public Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
		try {
			RLog.i("debug", http.getContent().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null)
				break;
			header.put(http.getHeaderFieldKey(i), mine);
			if ("setUrlImage-cookie".equalsIgnoreCase(http.getHeaderFieldKey(i))) {
				cookies.add(mine);
			}
			// LogUtil.print(Constant.TAG,"key:"+http.getHeaderFieldKey(i)+"  value:"+mine);
		}
		return header;
	}
}