package com.eric.zmappii.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.eric.zmappii.R;
import com.eric.zmappii.application.MyApplication;

public class SetJudgementActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private RelativeLayout layoutSetHeartbeat;
    private RelativeLayout layoutSetBreath;
    private RelativeLayout layoutSetMove;

    private Context mContext = SetJudgementActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_judgement);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        getBundle(); // 接收传值
        initListener(); // 初始化点击事件
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
     * 接收传值
     */
    private void getBundle() {
        String rangeHeartbeat = "";
        String rangeBreath = "";
        String rangeMove = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            rangeHeartbeat = bundle.getString(MyApplication.KEY_HEARTBEAT_MIN) + "次/分 ~ " +
                    bundle.getString(MyApplication.KEY_HEARTBEAT_MAX + "次/分");
        }
    }

    /**
     * 关联控件
     */
    private void findView() {
        layoutSetHeartbeat = (RelativeLayout) findViewById(R.id.layout_set_heartbeat);
        layoutSetBreath = (RelativeLayout) findViewById(R.id.layout_set_breath);
        layoutSetMove = (RelativeLayout) findViewById(R.id.layout_set_move);
    }

    /**
     * 初始化点击事件
     */
    private void initListener() {
        layoutSetHeartbeat.setOnClickListener(this);
        layoutSetBreath.setOnClickListener(this);
        layoutSetMove.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_judgement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_set_heartbeat:
                startActivity(new Intent(mContext, SetHeartbeatActivity.class));
                break;
            case R.id.layout_set_breath:
                startActivity(new Intent(mContext, SetBreathActivity.class));
                break;
            case R.id.layout_set_move:
                startActivity(new Intent(mContext, SetMoveActivity.class));
                break;
        }
    }
}
