package com.eric.zmappii.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eric.zmappii.R;
import com.eric.zmappii.fragments.NavigationDrawerFragment;

public class PersonalCenterActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private RelativeLayout layoutJudgement;
    private RelativeLayout layoutTimeOffBed;
    private RelativeLayout layoutEmergeContact;

    private Context mContext = PersonalCenterActivity.this;

    private long mExitTime = 0;
    private static final String BACK_TO_DESKTOP = "再按一次返回键回到桌面";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initNavDrawer(); // 初始
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
        layoutJudgement = (RelativeLayout) findViewById(R.id.layout_judgement);
        layoutTimeOffBed = (RelativeLayout) findViewById(R.id.layout_time_off_bed);
        layoutEmergeContact = (RelativeLayout) findViewById(R.id.layout_emerge_contact);
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
     * 初始化监听事件
     */
    private void initListener() {
        layoutJudgement.setOnClickListener(this);
        layoutTimeOffBed.setOnClickListener(this);
        layoutEmergeContact.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_personal_center, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_judgement:
                startActivity(new Intent(mContext, SetJudgementActivity.class));
                break;
            case R.id.layout_time_off_bed:
                startActivity(new Intent(mContext, SetTimeOffBedActivity.class));
                break;
            case R.id.layout_emerge_contact:
                startActivity(new Intent(mContext, EmergencyContactActivity.class));
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
