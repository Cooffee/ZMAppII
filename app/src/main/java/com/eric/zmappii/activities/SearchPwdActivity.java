package com.eric.zmappii.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eric.zmappii.R;


public class SearchPwdActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText etPhone;
    private Button btnSend;

    private Context mContext = SearchPwdActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pwd);

        initToolbar(); // 初始化toolbar
        initBackButton(); // 初始化返回按钮
        findView(); // 关联控件
        initListener(); // 初始化监听事件

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_pwd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化返回按钮
     */
    private void initBackButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
    }

    /**
     * 关联控件
     */
    private void findView() {
        etPhone = (EditText) findViewById(R.id.et_phone);
        btnSend = (Button) findViewById(R.id.btn_send);
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        btnSend.setOnClickListener(this);
        enableButtonOrNot(btnSend, etPhone); // 根据手机号码输入框判断是否使能按钮
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                pushPwd(); // 推送密码
                break;
        }
    }

    /**
     * 服务器以短信的形式推送密码
     */
    private void pushPwd() {
        Toast.makeText(mContext, "密码已成功发送，请注意查收", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(mContext, LoginActivity.class));
    }
}
