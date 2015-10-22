package com.eric.zmappii.utils;

import android.util.Log;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Canon on 2015/5/28.
 */
public class JSONUtil {
    private static final String TAG = "JSONUtil";

    /**
     * 获取json内容
     * @param url
     * @return
     */
    public static JSONObject getJSON(String url) throws JSONException, Exception {
        return new JSONObject(getRequest(url));
    }

    /**
     * 向api发送get请求，返回从后台取得的信息
     * @param url
     * @return
     */
    private static String getRequest(String url) throws Exception{
        Log.e(TAG, "11111");
        return getRequest(url, new DefaultHttpClient(new BasicHttpParams()));
    }

    /**
     * 向api发送get请求，返回从后台取得的信息
     * @param url
     * @param client
     * @return
     * @throws Exception
     */
    private static String getRequest(String url, DefaultHttpClient client) throws Exception{
        Log.e(TAG, "22222");
        String result = null;
        int statusCode = 0;
        HttpGet getMethod = new HttpGet(url);
        Log.e(TAG, "do the getRequest, url = " + url);
        try {
            HttpResponse httpResponse = client.execute(getMethod); // 200表示正常
            Log.e(TAG, httpResponse.toString());
            statusCode = httpResponse.getStatusLine().getStatusCode();
            Log.e(TAG, "statuscode = " + statusCode);
            result = retrieveInputStream(httpResponse.getEntity());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            throw new Exception(e);
        } finally {
            getMethod.abort();
        }
        return result;
    }

    /**
     * 处理httpResponse信息，返回String
     * @param httpEntity
     * @return
     */
    protected static String retrieveInputStream(HttpEntity httpEntity) {

        int length = (int) httpEntity.getContentLength();
        // the number of bytes of the content, or a negative number if unknown.
        // If the content length is known but exceeds Long.MAX_VALUE, a negative number is returned.
        // length == -1,下面这句报错，println needs a message
        if (length < 0)
            length = 10000;
        StringBuffer stringBuffer = new StringBuffer(length);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(), HTTP.UTF_8);
            char buffer[] = new char[length];
            int count;
            while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
                stringBuffer.append(buffer, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }


}
