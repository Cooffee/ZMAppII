package com.eric.zmappii.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eric.zmappii.R;
import com.eric.zmappii.application.MyApplication;
import com.eric.zmappii.fragments.NavigationDrawerFragment;
import com.eric.zmappii.utils.HttpUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AnalyseActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView tvSleeperName;
    private TextView tvTimeGoBed;
    private TextView tvTimeWakeUp;
    private TextView tvRangeHeartbeat;
    private TextView tvRangeBreath;
    private TextView tvRangeMovement;

    private long mExitTime = 0;
    private String url = "";

    private Context mContext = AnalyseActivity.this;

    private static final String BACK_TO_DESKTOP = "再按一次返回键回到桌面";
    private static final String TAG = "ZMAPP";

    private SharedPreferences preferencesUrl;
    private String ip;
    private String port;
    private String phone;
    private String relationship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);

        findView(); // 关联控件
        getUrlFromPreferences(); // 从preferences里取出url相关信息
        initUrl(); // 初始化url
        initToolbar(); // 初始化toolbar
        initNavDrawer(); // 初始化抽屉式侧滑菜单
        volleyGetData(); // volley获取数据
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 关联控件
     */
    private void findView() {
        tvSleeperName = (TextView) findViewById(R.id.tv_sleeper_name);
        tvTimeGoBed = (TextView) findViewById(R.id.tv_time_go_bed);
        tvTimeWakeUp = (TextView) findViewById(R.id.tv_time_wake_up);
        tvRangeHeartbeat = (TextView) findViewById(R.id.tv_range_hearbeat);
        tvRangeBreath = (TextView) findViewById(R.id.tv_range_breath);
        tvRangeMovement = (TextView) findViewById(R.id.tv_range_movement);
    }

    /**
     * 从preferences里取出url相关数据
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
        url = "http://" + ip + ":" + port + "/interation/getState?appUserPhone="
                + phone + "&relationship=" + relationship;
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
     * volley获取数据
     */
    private void volleyGetData() {
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject mattressUserList = response.getJSONObject("mattressUserList");
                            JSONObject mattressUser = mattressUserList.getJSONObject(relationship);
                            JSONObject alarm = mattressUser.getJSONObject("alarm");

                            String timeGoBed = alarm.getString("timeGoBed");
                            String timeGetUp = alarm.getString("timeGetUp");
                            int minHeartbeatRate = alarm.getInt("minHeartbeatRate");
                            int maxHeartbeatRate = alarm.getInt("maxHeartbeatRate");
                            int minBreathRate = alarm.getInt("minBreathRate");
                            int maxBreathRate = alarm.getInt("maxBreathRate");
                            int maxMovement = alarm.getInt("maxMovement");
                            String sleeperName = alarm.getString("sleeperName");

                            tvTimeGoBed.setText(timeGoBed);
                            tvTimeWakeUp.setText(timeGetUp);
                            tvRangeHeartbeat.setText(minHeartbeatRate + "次/分~" + maxHeartbeatRate + "次/分");
                            tvRangeBreath.setText(minBreathRate + "次/分~" + maxBreathRate + "次/分");
                            tvRangeMovement.setText("持续" + maxMovement + "秒以下");
                            tvSleeperName.setText(sleeperName);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "当前网络不稳定", Toast.LENGTH_SHORT).show();
            }
        });
        rq.add(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_analyse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
}
