package com.eric.zmappii.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by eric on 15/7/8.
 */
//@SuppressWarnings("unused")
public class HttpUtils {
    private final static String PATH = "http://10.0.2.2:8080/TaxiServlet/login";
    private static URL url;

    private static final int TIME_OUT = 3000;

    public HttpUtils() {

    }

    /**
     * 向服务端提交数据
     * @param params url参数
     * @param encode 字节编码
     * @return
     */
    public static int sendMessage(Map<String, String> params, String encode) {
        // 初始化URL
        StringBuffer buffer = new StringBuffer();

        if (null != params && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buffer.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue()))
                        .append("&");
                // 删除多余的&
                buffer.deleteCharAt(buffer.length() - 1);
            }

            try {
                url = new URL(PATH);
                if (null != url) {
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    if (null == urlConnection) {
                        return -1;
                    }
                    urlConnection.setConnectTimeout(TIME_OUT);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true); // 表示从服务器获取数据
                    urlConnection.setDoOutput(true); // 表示向服务器发送数据

                    byte[] data = buffer.toString().getBytes();
                    // 设置请求体为文本类型
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
                    // 获得输出流
                    OutputStream outputStream = urlConnection.getOutputStream();
                    outputStream.write(data);
                    outputStream.close();
                    // 获得服务器的响应结果和状态码
                    int responseCode = urlConnection.getResponseCode();

                    if (200 == responseCode) {
                        return changeInputStream(urlConnection.getInputStream(), encode);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 获得网络返回值 【0 - 正常  1 - 用户名错误   2 - 密码错误】
     * @param inputStream
     * @param encode
     * @return
     */
    private static int changeInputStream(InputStream inputStream, String encode) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if (null != inputStream) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
                result = new String(outputStream.toByteArray(), encode);

                len = Integer.parseInt(result.substring(0, 1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return len;
    }

}
