package com.eric.zmappii.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.eric.zmappii.fragments.TabBreathFragment;
import com.eric.zmappii.fragments.TabHeartbeatFragment;
import com.eric.zmappii.fragments.TabMoveFragment;
import com.eric.zmappii.fragments.TabOverviewFragment;
import com.eric.zmappii.fragments.TabSleepFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class DataActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private LinearLayout mTabHeartbeatRate;
    private LinearLayout mTabBreatheRate;
    private LinearLayout mTabOverview;
    private LinearLayout mTabBodyMove;
    private LinearLayout mTabSleepEstimate;

    private ImageView mImgHeartbeatRate;
    private ImageView mImgBreatheRate;
    private ImageView mImgOverview;
    private ImageView mImgBodyMove;
    private ImageView mImgSleepEstimate;

    private TextView mTvOverview;
    private TextView mTvHeartbeat;
    private TextView mTvBreathe;
    private TextView mTvMove;
    private TextView mTvSleep;

    private Fragment mFragHeartRate;
    private Fragment mFragBreatheRate;
    private Fragment mFragOverview;
    private Fragment mFragBodyMove;
    private Fragment mFragSleepEstimate;

    private Context mContext = DataActivity.this;

    private long mExitTime = 0;
    private static final String BACK_TO_DESKTOP = "再按一次返回键回到桌面";

    private int gray;
    private int green;

    private Handler handler;
    private Timer timer;
    private TimerTask task;

    private String url;
    private SharedPreferences preferencesUrl;
    private String ip;
    private String port;
    private String phone;
    private String relationship;

    private String abType = "";
    private static final String TAG = "ZMAPP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        initToolbar(); // 显示toolbar
        initNavDrawer(); // 初始化抽屉式侧滑菜单
        initColor(); // 初始化颜色
        getUrlFromPreferences(); // 从preferences取出url相关数据
        initUrl(); // 初始化url
        findView(); // 关联控件
        resetTab(); // 重置tab
        initListener(); // 初始化监听事件
        setSelect(2); // 初始化tab默认选中
        warnPush(); // 报警消息推送
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        mTabHeartbeatRate.setOnClickListener(this);
        mTabBreatheRate.setOnClickListener(this);
        mTabOverview.setOnClickListener(this);
        mTabBodyMove.setOnClickListener(this);
        mTabSleepEstimate.setOnClickListener(this);
    }

    /**
     * 关联控件
     */
    private void findView() {
        /*tabs关联控件*/
        mTabHeartbeatRate = (LinearLayout)findViewById(R.id.id_tab_heartbeat_rate);
        mTabBreatheRate = (LinearLayout)findViewById(R.id.id_tab_breathe_rate);
        mTabOverview = (LinearLayout)findViewById(R.id.id_tab_overview);
        mTabBodyMove = (LinearLayout)findViewById(R.id.id_tab_body_move);
        mTabSleepEstimate = (LinearLayout)findViewById(R.id.id_tab_sleep_depth);

		/*ImageView控件关联*/
        mImgHeartbeatRate = (ImageView)findViewById(R.id.img_heartbeat_rate);
        mImgBreatheRate = (ImageView)findViewById(R.id.img_breathe_rate);
        mImgOverview = (ImageView)findViewById(R.id.img_overview);
        mImgBodyMove = (ImageView)findViewById(R.id.img_body_move);
        mImgSleepEstimate = (ImageView)findViewById(R.id.img_sleep_depth);

		/* 初始化tab栏的TextView控件 */
        mTvOverview = (TextView)findViewById(R.id.tv_overview);
        mTvHeartbeat = (TextView)findViewById(R.id.tv_heartbeat);
        mTvBreathe = (TextView)findViewById(R.id.tv_breathe);
        mTvMove = (TextView)findViewById(R.id.tv_move);
        mTvSleep = (TextView)findViewById(R.id.tv_sleep);
    }

    /**
     * 从preferences里取出url相关数据
     */
    private void getUrlFromPreferences() {
        preferencesUrl = getSharedPreferences(MyApplication.SHAREDPREFERENCES_FILE_NAME_URL,
                Activity.MODE_WORLD_READABLE);
        ip = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_IP, MyApplication.DEFAULT_IP);
        port = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_PORT, MyApplication.DEFAULT_PORT);
        phone = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_PHONE, MyApplication.DEFAULT_PHONE);
        relationship = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_RELATIONSHIP, MyApplication.DEFAULT_RELATIONSHIP);
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        url = "http://" + ip + ":" + port + "/interation/GetCurAlarm?appUserPhone=" + phone;
    }

    /**
     * 报警消息推送
     */
    private void warnPush() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                getWarnMessage(); // 获取报警信息
            }
        };
        setTimer(); // 设置定时器
    }

    /**
     * 设置定时器
     */
    private void setTimer() {
        if (null == timer) {
            timer = new Timer();
        }
        if (null == task) {
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            };
        }
        if (null != timer && null != task) {
            timer.schedule(task, 0, 1000);
        }
    }

    /**
     * 取消定时器
     */
    private void stopTimer() {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != task) {
            task.cancel();
            task = null;
        }
    }

    private void getWarnMessage() {
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int isAlarming = 0;
                            String abnormalType = "";
                            JSONObject mattressUserList = response.getJSONObject("mattressUserList");
                            JSONObject mattressUser = mattressUserList.getJSONObject(relationship);
                            JSONObject curAlarm = mattressUser.getJSONObject("curAlarm");
                            isAlarming = curAlarm.getInt("isAlarming");
                            if (1 == isAlarming) {
                                abnormalType = curAlarm.getString("abnormalType");
                                if (abnormalType.contains("心率") && !abnormalType.equals(abType)) {
                                    abType = abnormalType;
                                    showNotification(relationship, MyApplication.ABNORMAL_HEARTBEAT);
                                } else if (abnormalType.contains("呼吸") && !abnormalType.equals(abType)) {
                                    abType = abnormalType;
                                    showNotification(relationship, MyApplication.ABNORMAL_BREATH);
                                } else if (abnormalType.contains("辗转") && !abnormalType.equals(abType)) {
                                    abType = abnormalType;
                                    showNotification(relationship, MyApplication.ABNORMAL_TOSS_AND_TURN);
                                } else if (abnormalType.contains("离床") && !abnormalType.equals(abType)) {
                                    abType = abnormalType;
                                    showNotification(relationship, MyApplication.ABNORMAL_OFF_BED);
                                }
                            } else {
                                Log.d(TAG, "it is normal");
                                abType = "";
                            }
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
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    /**
     * tab图标选中时
     * @param i
     */
    private void  setSelect(int i) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);

		/*把图片设置为选中 切换内容区域*/
        switch (i) {
            case 0:
                if(mFragHeartRate == null) {
                    mFragHeartRate = new TabHeartbeatFragment();
                    transaction.add(R.id.content, mFragHeartRate);
                } else {
                    transaction.show(mFragHeartRate);
                }
                mImgHeartbeatRate.setImageResource(R.drawable.ic_heart_focus);
                mTvHeartbeat.setTextColor(green);
                break;
            case 1:
                if(mFragBreatheRate == null) {
                    mFragBreatheRate = new TabBreathFragment();
                    transaction.add(R.id.content, mFragBreatheRate);
                } else {
                    transaction.show(mFragBreatheRate);
                }
                mImgBreatheRate.setImageResource(R.drawable.ic_breath_focus);
                mTvBreathe.setTextColor(green);
                break;
            case 2:
                if(mFragOverview == null) {
                    mFragOverview = new TabOverviewFragment();
                    transaction.add(R.id.content, mFragOverview);
                } else {
                    transaction.show(mFragOverview);
                }
                mImgOverview.setImageResource(R.drawable.ic_overview_focus);
                mTvOverview.setTextColor(green);
                break;
            case 3:
                if(mFragBodyMove == null) {
                    mFragBodyMove = new TabMoveFragment();
                    transaction.add(R.id.content, mFragBodyMove);
                } else {
                    transaction.show(mFragBodyMove);
                }
                mImgBodyMove.setImageResource(R.drawable.ic_move_focus);
                mTvMove.setTextColor(green);
                break;
            case 4:
                if(mFragSleepEstimate == null) {
                    mFragSleepEstimate = new TabSleepFragment();
                    transaction.add(R.id.content, mFragSleepEstimate);
                } else {
                    transaction.show(mFragSleepEstimate);
                }
                mImgSleepEstimate.setImageResource(R.drawable.ic_sleep_focus);
                mTvSleep.setTextColor(green);
                break;
        }

        transaction.commit();
    }

    /**
     * 重置tab图标
     */
    private void resetTab() {
		/* 重置图片图标 */
        mImgHeartbeatRate.setImageResource(R.drawable.ic_heart);
        mImgBreatheRate.setImageResource(R.drawable.ic_breath);
        mImgOverview.setImageResource(R.drawable.ic_overview);
        mImgBodyMove.setImageResource(R.drawable.ic_move);
        mImgSleepEstimate.setImageResource(R.drawable.ic_sleep);

		/* 重置字体颜色 */
        mTvOverview.setTextColor(gray);
        mTvHeartbeat.setTextColor(gray);
        mTvBreathe.setTextColor(gray);
        mTvMove.setTextColor(gray);
        mTvSleep.setTextColor(gray);
    }

    /**
     * 隐藏Fragment
     * @param transaction
     */
    private void hideFragment(FragmentTransaction transaction) {
        if(mFragHeartRate != null) {
            transaction.hide(mFragHeartRate);
        }
        if(mFragBreatheRate != null) {
            transaction.hide(mFragBreatheRate);
        }
        if(mFragOverview != null) {
            transaction.hide(mFragOverview);
        }
        if(mFragBodyMove != null) {
            transaction.hide(mFragBodyMove);
        }
        if(mFragSleepEstimate != null) {
            transaction.hide(mFragSleepEstimate);
        }
    }

    private void initColor() {
        green = getResources().getColor(R.color.colorPrimary);
        gray = getResources().getColor(R.color.colorTabText);
    }

    private void initNavDrawer() {
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View v) {
        resetTab();
        switch (v.getId()) {
            case R.id.id_tab_heartbeat_rate:
                setSelect(0);
                break;
            case R.id.id_tab_breathe_rate:
                setSelect(1);
                break;
            case R.id.id_tab_overview:
                setSelect(2);
                break;
            case R.id.id_tab_body_move:
                setSelect(3);
                break;
            case R.id.id_tab_sleep_depth:
                setSelect(4);
                break;
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

}
