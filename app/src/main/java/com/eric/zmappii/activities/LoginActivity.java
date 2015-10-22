package com.eric.zmappii.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.eric.zmappii.pojo.Member;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private long mExitTime = 0;

    private Button btLogin;
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvSearchPwd;
    private TextView tvSetIp;
    private ProgressDialog pDialog;

    private Context mContext = LoginActivity.this;

    private Intent intent;
    private SharedPreferences preferencesLogin = null;
    private SharedPreferences preferencesUrl = null;
    private SharedPreferences.Editor editorLogin = null;
    private SharedPreferences.Editor editorUrl = null;

    private static final String TAG = "ZMAPP";

    private String url = "";
    private String ip;
    private String port;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findView(); // 关联控件
        initProgressDialog(); // 初始化进度条对话框
        initLastLogin(); // 初始化上一次登录名
        getUrlFromPreferences(); // 从sharedpreferences获取url的ip地址和端口号
        initUrl(); // 初始化url
        initListener(); // 注册监听事件
        enableButtonOrNot(btLogin, etUsername, etPassword); // 根据输入框是否为空，判断是否使能按钮
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLastLogin(); // 初始化上一次登录名
        getUrlFromPreferences(); // 从sharedpreferences获取url的ip地址和端口号
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initLastLogin();
        getUrlFromPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void login(String url) {
        Log.d(TAG, "login: " + url);
        RequestQueue rq = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (String)null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean canLog = response.getBoolean("isAccess");
                    Log.d(TAG, canLog+"");
                    if (canLog) {
                        rememberLastLogin(); // 记住上一次登录的账号
                        addUrlPhoneToPreferences(); // 将phone存到preferences里
                        hideDialog(); // 隐藏进度条对话框
                        startActivity(new Intent(mContext, MemberListActivity.class));
                        finish();
                    } else {
                        hideDialog(); // 隐藏进度条对话框
                        Toast.makeText(mContext, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog(); // 隐藏进度条对话框
                Toast.makeText(mContext, "当前网络不稳定", Toast.LENGTH_SHORT).show();
            }
        });
        rq.add(jsonRequest);
    }

    /**
     * 初始化进度条对话框
     */
    private void initProgressDialog() {
        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("正在登录中...");
        pDialog.setCancelable(false);
    }

    /**
     * 显示进度条对话框
     */
    private void showDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    /**
     * 隐藏进度条对话框
     */
    private void hideDialog() {
        if (pDialog.isShowing()) {
            pDialog.hide();
        }
    }

    /**
     * 注册监听事件
     */
    private void initListener() {
        btLogin.setOnClickListener(this);
        tvSearchPwd.setOnClickListener(this);
        tvSetIp.setOnClickListener(this);
    }

    /**
     * 关联控件
     */
    private void findView() {
        btLogin = (Button) findViewById(R.id.bt_login);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        tvSearchPwd = (TextView) findViewById(R.id.tv_searchpassword);
        tvSetIp = (TextView) findViewById(R.id.tv_setIP);
    }

    /**
     * 初始化接口url
     */
    private void initUrl() {
        url = "http://" + ip + ":" + port + "/interation/Login";
    }

    /**
     * 初始化上一次登录名
     */
    private void initLastLogin() {
        preferencesLogin = getSharedPreferences(MyApplication.SHAREDPREFERENCES_FILE_NAME_LOGIN,
                Activity.MODE_WORLD_READABLE);
        String loginName = preferencesLogin.getString(MyApplication.SHAREDPREFERENCES_KEY_LOGIN, "");
        etUsername.setText(loginName);
        etUsername.setSelection(loginName.length());
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                showDialog(); // 显示进度条对话框
                String appUserPhone = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String urlApi = url + "?appUserPhone=" + appUserPhone + "&password=" + password;
                login(urlApi);
//                startActivity(new Intent(this, MemberListActivity.class));
//                finish();
                break;
            case R.id.tv_searchpassword:
                searchPwd();
                break;
            case R.id.tv_setIP:
                startActivity(new Intent(mContext, IPSettingActivity.class));
                break;
        }
    }

    /**
     * 记住上一次登录
     */
    private void rememberLastLogin() {
        String loginName = etUsername.getText().toString();
        if (null == preferencesLogin) {
            preferencesLogin = getSharedPreferences(MyApplication.SHAREDPREFERENCES_FILE_NAME_LOGIN,
                    Context.MODE_PRIVATE); // 私有数据
        }
        if (null == editorLogin) {
            editorLogin = preferencesLogin.edit();
        }
        editorLogin.putString(MyApplication.SHAREDPREFERENCES_KEY_LOGIN, loginName);
        editorLogin.commit();
    }

    /**
     * 跳转找回密码界面
     */
    public void searchPwd() {
        intent = new Intent();
        intent.setClass(LoginActivity.this, SearchPwdActivity.class);
        startActivity(intent);
    }

    /**
     * 返回键按键事件监听
     * 点击两次，退出应用
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
            mExitTime = System.currentTimeMillis();
        } else {
            System.exit(0);
        }
    }

    /**
     * 从sharedpreference中获取url的ip地址和端口号
     */
    private void getUrlFromPreferences() {
        preferencesUrl = getSharedPreferences(MyApplication.SHAREDPREFERENCES_FILE_NAME_URL,
                Activity.MODE_WORLD_READABLE);
        ip = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_IP, "192.168.1.103");
        port = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_PORT, "8091");
    }

    /**
     * 将appUserPhone添加到preferences里
     */
    private void addUrlPhoneToPreferences() {
        String phone = etUsername.getText().toString();
        if (null == preferencesUrl) {
            preferencesUrl = getSharedPreferences(MyApplication.SHAREDPREFERENCES_FILE_NAME_URL,
                    Context.MODE_PRIVATE);
        }
        if (null == editorUrl) {
            editorUrl = preferencesUrl.edit();
        }
        editorUrl.putString(MyApplication.SHAREDPREFERENCES_KEY_PHONE, phone);
        editorUrl.commit();
    }

}
