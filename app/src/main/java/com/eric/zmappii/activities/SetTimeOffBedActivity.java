package com.eric.zmappii.activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.eric.zmappii.R;

public class SetTimeOffBedActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private RelativeLayout layoutWarnTimeStart;
    private RelativeLayout layoutWarnTimeStop;

    private TextView tvWarnStart;
    private TextView tvWarnStop;

    private Context mContext = SetTimeOffBedActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time_off_bed);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initListener(); // 初始化监听事件
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
        layoutWarnTimeStart = (RelativeLayout) findViewById(R.id.layout_warn_time_start);
        layoutWarnTimeStop = (RelativeLayout) findViewById(R.id.layout_warn_time_stop);
        tvWarnStart = (TextView) findViewById(R.id.tv_warn_start);
        tvWarnStop = (TextView) findViewById(R.id.tv_warn_stop);
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        layoutWarnTimeStart.setOnClickListener(this);
        layoutWarnTimeStop.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_time_off_bed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                startActivity(new Intent(mContext, SetTimeOffBedActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_warn_time_start:
                showTimePickerDialog(tvWarnStart);
                break;
            case R.id.layout_warn_time_stop:
                showTimePickerDialog(tvWarnStop);
                break;
        }
    }

    /**
     * 显示时间选择器弹出框
     */
    private void showTimePickerDialog(final TextView textView) {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                textView.setText(hourOfDay + ":" + minute);
            }
        }, 0, 0, true).show();
    }
}
