package com.eric.zmappii.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.eric.zmappii.MainActivity;
import com.eric.zmappii.R;
import com.eric.zmappii.adapters.MemberListAdapter;
import com.eric.zmappii.application.MyApplication;
import com.eric.zmappii.pojo.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MemberListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private Toolbar toolbar;
    private ListView lvMembers;
    private List<Member> members;
    private Member member;
    private MemberListAdapter adapter;

    private Context mContext = MemberListActivity.this;

    private SharedPreferences preferencesUrl;
    private SharedPreferences.Editor editorUrl;

    private static final String TAG = "ZMAPP";
    private static final String DLG_TITLE = "退出登录";
    private static final String DLG_MSG = "是否退出当前账号？";
    private static final String DLG_POSITIVE = "确定";
    private static final String DLG_NEGATIVE = "取消";
    private static final String BACK_TO_DESKTOP = "再按一次返回键回到桌面";

    private String[] whoseDatas = {"我的睡眠情况", "父亲的睡眠情况", "母亲的睡眠情况"};

    private static final String MY_DATA = "我的睡眠情况";
    private static final String MOTHER_DATA = "母亲的睡眠情况";
    private static final String FATHER_DATA = "父亲的睡眠情况";

    private long mExitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initList(); // 初始化列表
        initListener(); // 初始化监听事件
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar); // 使用toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * 关联控件
     */
    private void findView() {
        lvMembers = (ListView) findViewById(R.id.lv_members);
    }

    /**
     * 初始化列表
     */
    private void initList() {
        members = new ArrayList<>();
        for (int i = 0; i < whoseDatas.length; i++) {
            member = new Member();
            member.imgResId = R.drawable.zm_launcher;
            member.whoseData = whoseDatas[i];
            members.add(member);
            member = null;
        }
        adapter = new MemberListAdapter(this, members);
        lvMembers.setAdapter(adapter);
    }

    private void initListener() {
        lvMembers.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_member_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                new AlertDialog.Builder(this)
                        .setTitle(DLG_TITLE)
                        .setMessage(DLG_MSG)
                        .setPositiveButton(DLG_POSITIVE, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(mContext, LoginActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton(DLG_NEGATIVE, null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_members:
                switch (members.get(position).whoseData) {
                    case MY_DATA:
                        // TODO 跳转个人睡眠数据模块
                        MyApplication.IS_MY_DATA = 1;
//                        startActivity(new Intent(mContext, DataActivity.class));
                        startActivity(new Intent(mContext, NullUserActivity.class));
                        break;
                    case MOTHER_DATA:
                        addRelationshipToPreferences("mother");
                        // TODO 跳转他人睡眠数据模块
                        MyApplication.IS_MY_DATA = 0;
                        startActivity(new Intent(mContext, DataActivity.class));
                        finish();
                        break;
                    case FATHER_DATA:
                        addRelationshipToPreferences("father");
                        // TODO 跳转他人睡眠数据模块
                        MyApplication.IS_MY_DATA = 0;
                        startActivity(new Intent(mContext, DataActivity.class));
                        finish();
                        break;
                }
                break;
        }
    }

    /**
     * 将relationship存到preferences里
     * @param relationship
     */
    private void addRelationshipToPreferences(String relationship) {
        if (null == preferencesUrl) {
            preferencesUrl = getSharedPreferences(MyApplication.SHAREDPREFERENCES_FILE_NAME_URL,
                    Context.MODE_PRIVATE);
        }
        if (null == editorUrl) {
            editorUrl = preferencesUrl.edit();
        }
        editorUrl.putString(MyApplication.SHAREDPREFERENCES_KEY_RELATIONSHIP, relationship);
        editorUrl.commit();
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
