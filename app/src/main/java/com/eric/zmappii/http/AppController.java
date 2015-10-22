package com.eric.zmappii.http;

import android.app.Application;
import android.text.TextUtils;

/**
 * Created by Eric on 2015/6/4.
 */
public class AppController extends Application {

//    public static final String TAG = "ZMApp";
//
//    private RequestQueue mRequestQueue;
//
//    private static AppController mInstance;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mInstance = this;
//    }
//
//    public static synchronized AppController getInstance() {
//        return mInstance;
//    }
//
//    public RequestQueue getRequestQueue() {
//        if (mRequestQueue == null) {
//            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
//        }
//
//        return mRequestQueue;
//    }
//
//    public <T> void addToRequestQueue(Request<T> req, String tag) {
//        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
//        getRequestQueue().add(req);
//    }
//
//    public <T> void addToRequestQueue(Request<T> req) {
//        req.setTag(TAG);
//        getRequestQueue().add(req);
//    }
//
//    public void cancelPendingRequests(Object tag) {
//        if (mRequestQueue != null) {
//            mRequestQueue.cancelAll(tag);
//        }
//    }
}
