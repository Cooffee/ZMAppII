package com.eric.zmappii.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eric.zmappii.R;
import com.eric.zmappii.application.MyApplication;

public class SetBreathActivity extends BaseActivity {

    private Toolbar toolbar;
    private EditText etBreathMax;
    private EditText etBreathMin;

    private Context mContext = SetBreathActivity.this;
    private static final String TOAST_NOT_COMPLETE = "请完整填写阈值范围";
    private static final String TOAST_VALUE_ILLEGAL = "请规范输入阈值范围";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_breath);

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
        etBreathMax = (EditText) findViewById(R.id.et_breath_max);
        etBreathMin = (EditText) findViewById(R.id.et_breath_min);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_breath, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                Intent intent = new Intent(mContext, SetJudgementActivity.class);
                Bundle bundle = new Bundle();
                String maxBreath = etBreathMax.getText().toString().trim();
                String minBreath = etBreathMin.getText().toString().trim();
                if (!maxBreath.equals("") && !minBreath.equals("")) {
                    double max = Double.valueOf(maxBreath);
                    double min = Double.valueOf(minBreath);
                    if (min <= max) {
                        bundle.putString(MyApplication.KEY_HEARTBEAT_MAX, maxBreath);
                        bundle.putString(MyApplication.KEY_HEARTBEAT_MIN, minBreath);
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
