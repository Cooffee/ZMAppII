package com.eric.zmappii.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eric.zmappii.R;
import com.eric.zmappii.adapters.MyBaseAdapter;
import com.eric.zmappii.application.MyApplication;
import com.eric.zmappii.bean.Abnormality;
import com.eric.zmappii.fragments.NavigationDrawerFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class ErrorActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private Spinner spType; // 用于筛选异常类型的下拉列表
    private Spinner spDate; // 用于筛选日期的下拉列表
    private ListView lvAbnormality;

    private List<Abnormality> abnormalities = new ArrayList<>(); // 总的异常数据记录
    private List<Abnormality> abnormalitiesTemp = new ArrayList<>(); // 筛选的异常数据记录
    private ListAdapter adapter;

    private static final String TAG = "ZMAPP";
    private long mExitTime = 0;
    private static final String BACK_TO_DESKTOP = "再按一次返回键回到桌面";

    private String urlApi;

    // 异常类型下拉框每项位置标记
    private static final int ALL = 0;
    private static final int HEARTBEAT = 1;
    private static final int BREATH = 2;
    private static final int MOVE = 3;

    // 异常发生日期下拉框每项位置标记
    private static final int TODAY = 1;
    private static final int THIS_WEEK = 2;
    private static final int THIS_MONTH = 3;
    private static final int THREE_MONTH = 4;
    private static final int HALF_YEAR = 5;

    // 标记两个spinner的选择结果，用户筛选数据
    private int choiceType = 0;
    private int choiceDate = 0;

    // 匹配中的关键字
    private static final String KEY_HEARTBEAT = "心";
    private static final String KEY_BREATH = "呼";
    private static final String KEY_MOVE_01 = "辗转";
    private static final String KEY_MOVE_02 = "离床";

    private SharedPreferences preferencesUrl;
    private String ip;
    private String port;
    private String phone;
    private String relationship;


    private String[] abnType = new String[]{
            "心动过速", "心率偏低", "呼吸停滞"
    };
    private String[] abnTime = new String[]{
            "23:50", "21:10", "3:24", "23:17"
    };
    private String[] abnDate = new String[]{
            "2015-6-6", "2015-5-31", "2015-4-26", "2015-4-10", "2015-2-27",
            "2014-12-29"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        showToolbar(); // 显示toolbar
        initNavDrawer(); // 初始化抽屉式侧滑菜单
        findView(); // 关联控件
        getUrlFromPreferences(); // 从preferences里取出url相关数据
        initUrl(); // 初始化url
//        spDate.setSelection(0);
        volleyParseJson(urlApi); // volley请求数据
//        new MyAsyncTask(urlApi).execute("");
        initListener(); // 初始化事件监听
    }

    @Override
    protected void onResume() {
        super.onResume();
//        volleyParseJson(urlApi);
    }

    /**
     * 初始化列表
     */
    private void initList() {
//        abnormalities = new ArrayList<>();
//        abnormalitiesTemp = new ArrayList<>();
        int typeLength = abnType.length;
        int timeLength = abnTime.length;
        int dateLength = abnDate.length;
        for (int i = 0; i <= 20; i++) {
            abnormalities.add(new Abnormality(abnType[i % typeLength],
                    abnTime[i % timeLength],
                    abnDate[i % dateLength]));
        }
        for (int i = 0; i < abnormalities.size(); i++) {
            abnormalitiesTemp.add(abnormalities.get(i));
        }
        putIntoAdapter(); // 传递到适配器中
    }

    private void volleyParseJson(String url) {
        Log.d(TAG, "error url--- " + url);
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray abnormalList = response.getJSONArray("abnormalList");
                            int length = abnormalList.length();
                            initListFromJson(length, abnormalList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.getMessage()+"---"+error);
            }
        });
        rq.add(jsonObjectRequest);
    }

    /**
     * 加载json数据到列表中
     *
     * @param length
     * @param jsonArray
     */
    private void initListFromJson(int length, JSONArray jsonArray) {
        Log.d(TAG, "initListFromJson");
//        abnormalities = new ArrayList<>();
//        abnormalitiesTemp = new ArrayList<>();

        // 解析json数据到列表
        for (int i = 0; i < length; i++) {
            try {
                JSONObject item = jsonArray.getJSONObject(i);
                String type = item.getString("abnormalType");
                String time = item.getString("abnormalTime");
                String date = item.getString("abnormalDate");
                abnormalities.add(new Abnormality(type, time, date));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 复制数据到临时列表
        for (int i = 0; i < abnormalities.size(); i++) {
            abnormalitiesTemp.add(abnormalities.get(i));
        }
        putIntoAdapter(); // 传递到适配器中
    }

    /**
     * 从preferences取出url相关数据
     */
    private void getUrlFromPreferences() {
        preferencesUrl = getSharedPreferences(MyApplication.SHAREDPREFERENCES_FILE_NAME_URL,
                Context.MODE_WORLD_READABLE);
        ip = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_IP, MyApplication.DEFAULT_IP);
        port = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_PORT, MyApplication.DEFAULT_PORT);
        phone = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_PHONE, MyApplication.DEFAULT_PHONE);
        relationship = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_RELATIONSHIP, MyApplication.DEFAULT_RELATIONSHIP);
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        urlApi = "http://" + ip + ":" + port + "/interation/GetAbnormalState?appUserPhone="
                + phone + "&relationship=" + relationship;
    }


    /**
     * 传递到适配器中
     */
    private void putIntoAdapter() {
        if (null == adapter) {
            adapter = new MyBaseAdapter(ErrorActivity.this, abnormalitiesTemp);
        }
        lvAbnormality.setAdapter(adapter);
    }

    /**
     * 绑定控件
     */
    private void findView() {
        spType = (Spinner) findViewById(R.id.sp_abnormal_type);
        spDate = (Spinner) findViewById(R.id.sp_abnormal_date);
        lvAbnormality = (ListView) findViewById(R.id.lv_abnormal);
    }

    /**
     * 初始化抽屉式侧滑菜单
     */
    private void initNavDrawer() {
        // 显示切换按钮，并实现点击事件
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
    }

    /**
     * 显示toolbar
     */
    private void showToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * 初始化事件监听
     */
    private void initListener() {
        Log.d(TAG, "initListener");
        spDate.setOnItemSelectedListener(this);
        spType.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected");
        switch (parent.getId()) {
            case R.id.sp_abnormal_type: // 异常类型下拉框
                choiceType = position;
                filterRecords(choiceType, choiceDate); // 对数据进行筛选
                break;

            case R.id.sp_abnormal_date: // 异常日期下拉框
                choiceDate = position;
                filterRecords(choiceType, choiceDate); // 对数据进行筛选
                break;
            default:
                Log.d(TAG, "DEFAULT");
                Log.d(TAG, view.getId() + "---我点击了第" + position + "项");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * 筛选异常记录
     *
     * @param choiceType
     * @param choiceDate
     */
    private void filterRecords(int choiceType, int choiceDate) {
        Log.d(TAG, "filterRecords");
        initAbnormalitiesTemp(); // 初始化筛选后的异常列表

        // 筛选
        filterType(choiceType); // 筛选异常类型符合的异常记录
        filterDate(choiceDate); // 筛选异常日期符合的异常记录

        putIntoAdapter(); // 把数据传递到适配器中
    }

    /**
     * 初始化筛选后的异常列表
     */
    private void initAbnormalitiesTemp() {
        Log.d(TAG, "initAbnormalitiesTemp");
        if (abnormalitiesTemp.size() != 0) {
            abnormalitiesTemp.clear(); // 清空筛选后的异常列表
        }
        for (int i = 0; i < abnormalities.size(); i++) {
            abnormalitiesTemp.add(abnormalities.get(i));
        }
    }

    /**
     * 根据异常的类型筛选列表
     *
     * @param position 下拉列表的位置
     */
    private void filterType(int position) {
        switch (position) {
            case HEARTBEAT:
                for (int i = 0; i < abnormalitiesTemp.size(); i++) {
                    if (!abnormalitiesTemp.get(i).getType().contains(KEY_HEARTBEAT)) {
                        abnormalitiesTemp.remove(abnormalitiesTemp.get(i--));
                    }
                }
                break;
            case BREATH:
                for (int i = 0; i < abnormalitiesTemp.size(); i++) {
                    if (!abnormalitiesTemp.get(i).getType().contains(KEY_BREATH)) {
                        abnormalitiesTemp.remove(abnormalitiesTemp.get(i--));
                    }
                }
                break;
            case MOVE:
                for (int i = 0; i < abnormalitiesTemp.size(); i++) {
                    if (!abnormalitiesTemp.get(i).getType().contains(KEY_MOVE_01) &&
                            !abnormalitiesTemp.get(i).getType().contains(KEY_MOVE_02)) {
                        abnormalitiesTemp.remove(abnormalitiesTemp.get(i--));
                    }
                }
                break;
//            case SLEEP:
//                for (int i = 0; i < abnormalitiesTemp.size(); i++) {
//                    if (!abnormalitiesTemp.get(i).getType().contains(KEY_SLEEP)) {
//                        abnormalitiesTemp.remove(abnormalitiesTemp.get(i--));
//                    }
//                }
//                break;
        }
    }

    /**
     * 根据异常发生的日期筛选列表
     *
     * @param position 下拉列表的位置
     */
    private void filterDate(int position) {
        for (int i = 0; i < abnormalitiesTemp.size(); i++) {
            if (!isDateMatch(abnormalitiesTemp.get(i).getDate(), position)) {
                abnormalitiesTemp.remove(abnormalitiesTemp.get(i--));
            }
        }
    }

    /**
     * 匹配日期
     * 匹配，返回true
     * 不匹配，则返回false
     *
     * @param targetDate   要筛选的日期
     * @param dateInterval 日期的间隔标记
     * @return
     */
    private boolean isDateMatch(String targetDate, int dateInterval) {
        int year;
        int month;
        int day;
        String dateLine;
        switch (dateInterval) {
            case ALL:
                return true;
            case TODAY:
                Calendar c0 = Calendar.getInstance();
                year = c0.get(Calendar.YEAR);
                month = c0.get(Calendar.MONTH) + 1;
                day = c0.get(Calendar.DAY_OF_MONTH);
                dateLine = year + "-" + month + "-" + day;
                Log.d(TAG, "today:" + dateLine);
                return afterTheDay(targetDate, dateLine);
            case THIS_WEEK:
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DAY_OF_MONTH, -7);
                year = c1.get(Calendar.YEAR);
                month = c1.get(Calendar.MONTH) + 1;
                day = c1.get(Calendar.DAY_OF_MONTH);
                dateLine = year + "-" + month + "-" + day;
                Log.d(TAG, "week:" + dateLine);
                return afterTheDay(targetDate, dateLine);
            case THIS_MONTH:
                Calendar c2 = Calendar.getInstance();
                c2.add(Calendar.MONTH, -1);
                year = c2.get(Calendar.YEAR);
                month = c2.get(Calendar.MONTH) + 1;
                day = c2.get(Calendar.DAY_OF_MONTH);
                dateLine = year + "-" + month + "-" + day;
                Log.d(TAG, "month:" + dateLine);
                return afterTheDay(targetDate, dateLine);
            case THREE_MONTH:
                Calendar c3 = Calendar.getInstance();
                c3.add(Calendar.MONTH, -3);
                year = c3.get(Calendar.YEAR);
                month = c3.get(Calendar.MONTH) + 1;
                day = c3.get(Calendar.DAY_OF_MONTH);
                dateLine = year + "-" + month + "-" + day;
                Log.d(TAG, "month:" + dateLine);
                return afterTheDay(targetDate, dateLine);
            case HALF_YEAR:
                Calendar c4 = Calendar.getInstance();
                c4.add(Calendar.MONTH, -6);
                year = c4.get(Calendar.YEAR);
                month = c4.get(Calendar.MONTH) + 1;
                day = c4.get(Calendar.DAY_OF_MONTH);
                dateLine = year + "-" + month + "-" + day;
                Log.d(TAG, "year:" + dateLine);
                return afterTheDay(targetDate, dateLine);
        }
        return false;
    }

    /**
     * 判断指定日期是不是在日期线之后
     * 若待判断的日期在标准日期之后或是同一天，则返回true
     * 否则，返回false
     *
     * @param targetDate 待判断的日期
     * @param dateLine   标准时期
     * @return
     */
    private boolean afterTheDay(String targetDate, String dateLine) {
        // 获取待判断的日期的年月日
        int yearTarget = Integer.parseInt(targetDate.substring(0, targetDate.indexOf("-")));
        int monthTarget = Integer.parseInt(targetDate.substring(targetDate.indexOf("-") + 1, targetDate.lastIndexOf("-")));
        int dayTarget = Integer.parseInt(targetDate.substring(targetDate.lastIndexOf("-") + 1));

        // 获取标准时期的年月日
        int year = Integer.parseInt(dateLine.substring(0, dateLine.indexOf("-")));
        int month = Integer.parseInt(dateLine.substring(dateLine.indexOf("-") + 1, dateLine.lastIndexOf("-")));
        int day = Integer.parseInt(dateLine.substring(dateLine.lastIndexOf("-") + 1));

        if (yearTarget == year) {
            if (monthTarget == month) {
                if (dayTarget >= day) {
                    return true;
                } else {
                    return false;
                }
            } else if (monthTarget > month) {
                return true;
            } else {
                return false;
            }
        } else if (yearTarget > year) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 返回键按键事件监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToDesktop();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回桌面
     */
    public void backToDesktop() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, BACK_TO_DESKTOP, Toast.LENGTH_LONG).show();
            mExitTime = System.currentTimeMillis();
        } else {
            Intent mIntent = new Intent(Intent.ACTION_MAIN);
            mIntent.addCategory(Intent.CATEGORY_HOME);
            startActivity(mIntent);
        }
    }

//    class AbnormalityAsyncTask extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//            parseJson(); // 本地json格式数据解析
//            return null;
//        }
//
//        private void parseJson() {
//            try {
//                InputStream is = getResources().openRawResource(R.raw.abnormality);
//                byte[] buffer = new byte[is.available()];
//                is.read(buffer);
//
//                // 将字节数组转换为以GB2312编码的字符串
//                String json = new String(buffer, "UTF-8");
//
//                // 将字符串json转换为json对象，以便于取出数据
//                JSONArray abnormal = new JSONObject(json).getJSONArray("abnormality");
//
//                int length = abnormal.length();
//                initListFromJson(length, abnormal);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    class MyAsyncTask extends AsyncTask<String, Void, Boolean> {

        private String url;
        private String result;

        MyAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.d(TAG, "doInBackground");
            HttpGet httpRequest = new HttpGet(url);
            Log.d(TAG, url);
            HttpResponse httpResponse = null;
            try {
                httpResponse = new DefaultHttpClient().execute(httpRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, httpResponse.getStatusLine().getStatusCode() + "---status code");
            if (200 == httpResponse.getStatusLine().getStatusCode()) {
                HttpEntity httpEntity = httpResponse.getEntity();
                try {
                    result = EntityUtils.toString(httpEntity, "UTF-8");
                    JSONArray abnormalList = new JSONObject(result).getJSONArray("abnormalList");
                    int length = abnormalList.length();
                    Log.d(TAG, length + "");
                    initListFromJson(length, abnormalList);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


    }

}
