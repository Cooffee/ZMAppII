package com.eric.zmappii.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.zmappii.R;
import com.eric.zmappii.application.MyApplication;

import org.w3c.dom.Text;

public class IPSettingActivity extends BaseActivity {

    private Toolbar toolbar;
    private EditText etIP;
    private TextView tvIP;
    private EditText etPort;
    private TextView tvPort;

    private Context mContext = IPSettingActivity.this;

    private String ipString;
    private String portString;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String SHAREDPREFERENCES_FILE_NAME_URL = "zm_preferences_url";
    private static final String SHAREDPREFERENCES_KEY_IP = "api_ip";
    private static final String SHAREDPREFERENCES_KEY_PORT = "api_port";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipsetting);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        showUrlMessage(); // 显示之前的IP地址
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
        etIP = (EditText) findViewById(R.id.et_ip);
        tvIP = (TextView) findViewById(R.id.tv_ip);
        etPort = (EditText) findViewById(R.id.et_port);
        tvPort = (TextView) findViewById(R.id.tv_port);
    }

    /**
     * 显示之前的IP地址
     */
    private void showUrlMessage() {
        getUrlFromSharedpreference(); // 从preferences里面获取port和ip
        tvIP.setText("IP: " + ipString);
        tvPort.setText("端口号: " + portString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ipsetting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
//                confirmSetting(); // 确认完成设置IP地址
                addUrlToSharedpreferences(); // 将url添加到sharedPreference
                startActivity(new Intent(mContext, LoginActivity.class));
                findView();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 将url添加到sharedPreference里面
     */
    private void addUrlToSharedpreferences() {
        String ip = etIP.getText().toString().trim();
        String port = etPort.getText().toString().trim();
        if (ip.equals("")) {
            ip = ipString;
        }
        if (port.equals("")) {
            port = portString;
        }
        if (null == sharedPreferences) {
            sharedPreferences = getSharedPreferences(SHAREDPREFERENCES_FILE_NAME_URL,
                    Context.MODE_PRIVATE);
        }
        if (null == editor) {
            editor = sharedPreferences.edit();
        }
        editor.putString(SHAREDPREFERENCES_KEY_IP, ip);
        editor.putString(SHAREDPREFERENCES_KEY_PORT, port);
        editor.commit();
    }

    /**
     * 将url的端口号和ip地址从preferences里面取出
     */
    private void getUrlFromSharedpreference() {
        sharedPreferences = getSharedPreferences(MyApplication.SHAREDPREFERENCES_FILE_NAME_URL,
                Activity.MODE_WORLD_READABLE);
        ipString = sharedPreferences.getString(SHAREDPREFERENCES_KEY_IP,
                "192.168.1.103");
        portString = sharedPreferences.getString(SHAREDPREFERENCES_KEY_PORT,
                "8091");
    }
}
