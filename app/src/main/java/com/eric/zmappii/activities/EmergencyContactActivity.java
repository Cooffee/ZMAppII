package com.eric.zmappii.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eric.zmappii.R;

public class EmergencyContactActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView tvContactName;
    private TextView tvContactPhone;
    private RelativeLayout layoutContactName;
    private RelativeLayout layoutContactPhone;

    private Context mContext = EmergencyContactActivity.this;

    private static final String CONFIRM = "确定";
    private static final String CANCEL = "取消";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

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
        tvContactName = (TextView) findViewById(R.id.tv_contact_name);
        tvContactPhone = (TextView) findViewById(R.id.tv_contact_name_phone);
        layoutContactName = (RelativeLayout) findViewById(R.id.layout_contact_name);
        layoutContactPhone = (RelativeLayout) findViewById(R.id.layout_contact_phone);
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        layoutContactName.setOnClickListener(this);
        layoutContactPhone.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_emergency_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                startActivity(new Intent(mContext, PersonalCenterActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_contact_name:
                final EditText etContactName = new EditText(this);
                etContactName.setSingleLine(true);
                etContactName.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(6)});
                etContactName.setSelection(etContactName.getText().length());
                etContactName.setHint("我的联系人姓名");
                new AlertDialog.Builder(this)
                        .setView(etContactName)
                        .setTitle("联系人姓名")
                        .setPositiveButton(CONFIRM, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 设置联系人姓名
                                String name = etContactName.getText().toString().trim();
                                tvContactName.setText(name);
                            }
                        })
                        .setNegativeButton(CANCEL, null)
                        .show();
                break;
            case R.id.layout_contact_phone:
                final EditText etContactPhone = new EditText(this);
                etContactPhone.setSingleLine(true);
                etContactPhone.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(11)});
                etContactPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
                etContactPhone.setSelection(etContactPhone.getText().length());
                etContactPhone.setHint("我的联系人电话");
                new AlertDialog.Builder(this)
                        .setView(etContactPhone)
                        .setTitle("联系人电话")
                        .setPositiveButton(CONFIRM, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 设置联系人电话
                                String phone = etContactPhone.getText().toString().trim();
                                tvContactPhone.setText(phone);
                            }
                        })
                        .setNegativeButton(CANCEL, null)
                        .show();
                break;
        }
    }
}
