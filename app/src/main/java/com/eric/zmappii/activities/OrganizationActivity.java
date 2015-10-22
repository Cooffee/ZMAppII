package com.eric.zmappii.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eric.zmappii.R;
import com.eric.zmappii.fragments.NavigationDrawerFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.eric.zmappii.R.id.tv_activity_title;

public class OrganizationActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private RelativeLayout layoutOrgIntro;
    private RelativeLayout layoutOrgActi;
    private RelativeLayout layoutCall;

    private Context mContext = OrganizationActivity.this;
    private static final String DIALOG_TITLE = "拨打联系电话";
    private static final String DIALOG_MESSAGE = "是否联系养老院？\n联系电话：18857119873";
    private static final String BTN_POSITIVE_TEXT = "拨打";
    private static final String BTN_NEGATIVE_TEXT = "取消";
    private static final String TAG = "ZMAPP";

    private long mExitTime = 0;
    private static final String BACK_TO_DESKTOP = "再按一次返回键回到桌面";

    private static final String ALARM_URL = "http://172.17.64.205:8080/interation/GetCurAlarm?appUser=18616896255";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initNavDrawer(); // 初始化抽屉式侧滑菜单
        initListener(); // 初始化监听事件
        new AlarmAsyncTask(ALARM_URL).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        layoutOrgIntro = (RelativeLayout) findViewById(R.id.layout_org_intro);
        layoutOrgActi = (RelativeLayout) findViewById(R.id.layout_org_activity);
        layoutCall = (RelativeLayout) findViewById(R.id.layout_call);

    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        layoutOrgIntro.setOnClickListener(this);
        layoutOrgActi.setOnClickListener(this);
        layoutCall.setOnClickListener(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_organization, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_org_intro:
                startActivity(new Intent(mContext, OrgIntroActivity.class));
                break;
            case R.id.layout_org_activity:
                startActivity(new Intent(mContext, OrgActiActivity.class));
                break;
            case R.id.layout_call:
                showPopupWindow(); // 显示弹出窗口
                break;
        }
    }

    /**
     * 显示弹出窗口
     */
    private void showPopupWindow() {
        new AlertDialog.Builder(this)
                .setTitle(DIALOG_TITLE)
                .setMessage(DIALOG_MESSAGE)
                .setPositiveButton(BTN_POSITIVE_TEXT, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentCall = new Intent(Intent.ACTION_CALL);
                        startActivity(intentCall);
                    }
                })
                .setNegativeButton(BTN_NEGATIVE_TEXT, null)
                .setCancelable(false)
                .show();
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

    class AlarmAsyncTask extends AsyncTask {

        private String url;
        private String result;
        private int isAlarming = -1;

        AlarmAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            HttpGet httpRequest = new HttpGet(url);
            HttpResponse httpResponse = null;
            if (null == httpResponse) {
                try {
                    httpResponse = new DefaultHttpClient().execute(httpRequest);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    result = EntityUtils.toString(httpEntity, "utf-8");
                    isAlarming = new JSONObject(result).getInt("isAlarming");
                    Log.d(TAG, isAlarming+"");
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
