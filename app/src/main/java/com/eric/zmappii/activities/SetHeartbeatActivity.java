package com.eric.zmappii.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eric.zmappii.R;
import com.eric.zmappii.application.MyApplication;

public class SetHeartbeatActivity extends BaseActivity {

    private Toolbar toolbar;
    private EditText etHeartbeatMax;
    private EditText etHeartbeatMin;

    private Context mContext = SetHeartbeatActivity.this;
    private static final String TOAST_NOT_COMPLETE = "请完整填写阈值范围";
    private static final String TOAST_VALUE_ILLEGAL = "请规范输入阈值范围";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_heartbeat);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件

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
        etHeartbeatMax = (EditText) findViewById(R.id.et_heartbeat_max);
        etHeartbeatMin = (EditText) findViewById(R.id.et_heartbeat_min);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_heartbeat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                Intent intent = new Intent(mContext, SetJudgementActivity.class);
                Bundle bundle = new Bundle();
                String maxHeartbeat = etHeartbeatMax.getText().toString().trim();
                String minHeartbeat = etHeartbeatMin.getText().toString().trim();
                if (!maxHeartbeat.equals("") && !minHeartbeat.equals("")) {
                    double max = Double.valueOf(maxHeartbeat);
                    double min = Double.valueOf(minHeartbeat);
                    if (min <= max) {
                        bundle.putString(MyApplication.KEY_HEARTBEAT_MAX, maxHeartbeat);
                        bundle.putString(MyApplication.KEY_HEARTBEAT_MIN, minHeartbeat);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(mContext, TOAST_VALUE_ILLEGAL, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, TOAST_NOT_COMPLETE, Toast.LENGTH_SHORT).show();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
