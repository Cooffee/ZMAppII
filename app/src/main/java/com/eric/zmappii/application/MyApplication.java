package com.eric.zmappii.application;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by eric on 15/7/2.
 */
public class MyApplication extends Application {
    private static MyApplication sInstance;

    public static boolean IS_ERROR = true;
    public static boolean IS_BREATHE_ERROR = false;
    public static boolean IS_HEARTBEAT_ERROR = false;
    public static boolean IS_MOVE_ERROR = false;

    public static final String KEY_HEARTBEAT_MAX = "max_heartbeat";
    public static final String KEY_HEARTBEAT_MIN = "min_heartbeat";
    public static final String KEY_BREATH_MAX = "max_breath";
    public static final String KEY_BREATH_MIN = "min_breath";
    public static final String KEY_MOVE_MAX = "max_move";

    public static final String SHAREDPREFERENCES_FILE_NAME_LOGIN = "zm_preferences_login";
    public static final String SHAREDPREFERENCES_KEY_LOGIN = "last_login";
    public static final String SHAREDPREFERENCES_FILE_NAME_URL = "zm_preferences_url";
    public static final String SHAREDPREFERENCES_KEY_IP = "api_ip";
    public static final String SHAREDPREFERENCES_KEY_PORT = "api_port";
    public static final String SHAREDPREFERENCES_KEY_PHONE = "api_phone";
    public static final String SHAREDPREFERENCES_KEY_RELATIONSHIP = "api_relationship";

    public static final String DEFAULT_IP = "192.168.1.103";
    public static final String DEFAULT_PORT = "8091";
    public static final String DEFAULT_PHONE = "18616896255";
    public static final String DEFAULT_RELATIONSHIP = "father";

    public static final String ABNORMAL_HEARTBEAT = "心率异常";
    public static final String ABNORMAL_BREATH = "呼吸异常";
    public static final String ABNORMAL_TOSS_AND_TURN = "辗转异常";
    public static final String ABNORMAL_OFF_BED = "离床异常";

    public static int IS_MY_DATA = 0; // 查看的数据是不是自己的睡眠数据

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}
