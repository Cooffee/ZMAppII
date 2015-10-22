package com.eric.zmappii.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.eric.zmappii.R;
import com.eric.zmappii.activities.AnalyseActivity;
import com.eric.zmappii.activities.DataActivity;
import com.eric.zmappii.activities.ErrorActivity;
import com.eric.zmappii.activities.LoginActivity;
import com.eric.zmappii.activities.HealthKnowledgeActivity;
import com.eric.zmappii.activities.MemberListActivity;
import com.eric.zmappii.activities.OrganizationActivity;
import com.eric.zmappii.activities.PersonalCenterActivity;
import com.eric.zmappii.activities.StandardActivity;
import com.eric.zmappii.adapters.VivzAdapter;
import com.eric.zmappii.application.MyApplication;
import com.eric.zmappii.bean.Organization;
import com.eric.zmappii.pojo.MyMenu;
import com.eric.zmappii.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private TextView tvLogout;
    private TextView tvUser;
    private RelativeLayout layoutWarning;
    private ImageView imgPoint;

    public static final String PREF_FILE_NAME = "testpref";
    public static final String KEY_USER_LEARN_DRAWER = "user_learned_drawer";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private VivzAdapter adapter;

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View containerView;

    private static final int ACTIVITY_DATA = 0;
    private static final int ACTIVITY_ERROR = 1;
    private static final int ACTIVITY_HEALTH_KNOWLEDGE = 2;
    private static final int ACTIVITY_ANALYSE = 3;
    private static final int ACTIVITY_PERSONAL_CENTER = 4;
    private static final int ACTIVITY_NURSING_HOME = 4;

    private static final int OTHERS_DATA = 0;
    private static final int MY_DATA = 1;

    private SharedPreferences preferencesUrl;
    private String relationship;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(),
                KEY_USER_LEARN_DRAWER, "false"));
        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        adapter = new VivzAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter); // 将数据填到recylerview
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // recyclerview点击事件监听
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(),
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if (1 == MyApplication.IS_MY_DATA) {
                                    // 这对不同item的点击进行不同的操作
                                    switch (position) {
                                        case ACTIVITY_DATA: // "数据查看"点击事件
                                            // 如果点击的不是当前项，则跳转页面
                                            if (DataActivity.class != getActivity().getClass()) {
                                                startActivity(new Intent(getActivity(), DataActivity.class));
                                                getActivity().finish();
                                            }
                                            // 如果点击的是当前项，则隐藏菜单
                                            else {
                                                mDrawerLayout.closeDrawer(Gravity.LEFT);
                                            }
                                            break;
                                        case ACTIVITY_ERROR: // "异常查看"点击事件
                                            // 如果点击的不是当前项，则跳转页面
                                            if (ErrorActivity.class != getActivity().getClass()) {
                                                startActivity(new Intent(getActivity(), ErrorActivity.class));
                                                getActivity().finish();
                                            }
                                            // 如果点击的是当前项，则隐藏菜单
                                            else {
                                                mDrawerLayout.closeDrawer(Gravity.LEFT);
                                            }
                                            break;
                                        case ACTIVITY_HEALTH_KNOWLEDGE:
                                            // 如果点击的不是当前项，则跳转页面
                                            if (HealthKnowledgeActivity.class != getActivity().getClass()) {
                                                startActivity(new Intent(getActivity(), HealthKnowledgeActivity.class));
                                                getActivity().finish();
                                            }
                                            // 如果点击的是当前项，则隐藏菜单
                                            mDrawerLayout.closeDrawer(Gravity.LEFT);
                                            break;
                                        case ACTIVITY_ANALYSE:
                                            // 如果点击的不是当前项，则跳转页面
                                            if (AnalyseActivity.class != getActivity().getClass()) {
                                                startActivity(new Intent(getActivity(), AnalyseActivity.class));
                                                getActivity().finish();
                                            }
                                            // 如果点击的是当前项，则隐藏菜单
                                            mDrawerLayout.closeDrawer(Gravity.LEFT);
                                            break;
                                        case ACTIVITY_PERSONAL_CENTER:
                                            // 如果点击的不是当前项，则跳转页面
                                            if (PersonalCenterActivity.class != getActivity().getClass()) {
                                                startActivity(new Intent(getActivity(), PersonalCenterActivity.class));
                                                getActivity().finish();
                                            }
                                            // 如果点击的是当前项，则隐藏菜单
                                            mDrawerLayout.closeDrawer(Gravity.LEFT);
                                            break;
                                    }
                                } else if (0 == MyApplication.IS_MY_DATA) {
                                    switch (position) {
                                        case ACTIVITY_DATA:
                                            // 如果点击的不是当前项，则跳转页面
                                            if (DataActivity.class != getActivity().getClass()) {
                                                startActivity(new Intent(getActivity(), DataActivity.class));
                                                getActivity().finish();
                                            }
                                            // 如果点击的是当前项，则隐藏菜单
                                            else {
                                                mDrawerLayout.closeDrawer(Gravity.LEFT);
                                            }
                                            break;
                                        case ACTIVITY_ERROR: // "异常查看"点击事件
                                            // 如果点击的不是当前项，则跳转页面
                                            if (ErrorActivity.class != getActivity().getClass()) {
                                                startActivity(new Intent(getActivity(), ErrorActivity.class));
                                                getActivity().finish();
                                            }
                                            // 如果点击的是当前项，则隐藏菜单
                                            else {
                                                mDrawerLayout.closeDrawer(Gravity.LEFT);
                                            }
                                            break;
                                        case ACTIVITY_HEALTH_KNOWLEDGE:
                                            // 如果点击的不是当前项，则跳转页面
                                            if (HealthKnowledgeActivity.class != getActivity().getClass()) {
                                                startActivity(new Intent(getActivity(), HealthKnowledgeActivity.class));
                                                getActivity().finish();
                                            }
                                            // 如果点击的是当前项，则隐藏菜单
                                            mDrawerLayout.closeDrawer(Gravity.LEFT);
                                            break;
                                        case ACTIVITY_ANALYSE:
                                            // 如果点击的不是当前项，则跳转页面
                                            if (AnalyseActivity.class != getActivity().getClass()) {
                                                startActivity(new Intent(getActivity(), AnalyseActivity.class));
                                                getActivity().finish();
                                            }
                                            // 如果点击的是当前项，则隐藏菜单
                                            mDrawerLayout.closeDrawer(Gravity.LEFT);
                                            break;
                                        case ACTIVITY_NURSING_HOME:
                                            // TODO 创建养老院activity
                                            if (OrganizationActivity.class != getActivity().getClass()) {
                                                startActivity(new Intent(getActivity(), OrganizationActivity.class));
                                                getActivity().finish();
                                            }
                                            mDrawerLayout.closeDrawer(Gravity.LEFT);
                                            break;
                                    }
                                }
                            }
                        })
        );



        // 监听事件
        tvLogout = (TextView) layout.findViewById(R.id.tv_logout);
        tvLogout.setOnClickListener(this);

        layoutWarning = (RelativeLayout) layout.findViewById(R.id.layout_warning);
        layoutWarning.setOnClickListener(this);

        imgPoint = (ImageView) layout.findViewById(R.id.img_point);

        initErrorFlag(); // 初始化异常事件
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initErrorFlag(); // 初始化异常标记
        getRelationshipFromPreferences(); // 从preferences里面取出relationship
        tvUser = (TextView) getActivity().findViewById(R.id.tv_user);
        switch (relationship) {
            case "father":
                tvUser.setText("父亲");
                break;
            case "mother":
                tvUser.setText("母亲");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initErrorFlag(); // 初始化异常标记
    }

    /**
     * 初始化异常标记
     */
    private void initErrorFlag() {
        if (MyApplication.IS_HEARTBEAT_ERROR ||
                MyApplication.IS_BREATHE_ERROR ||
                MyApplication.IS_MOVE_ERROR) {
            imgPoint.setVisibility(View.VISIBLE);
            layoutWarning.setClickable(true);
        } else {
            imgPoint.setVisibility(View.INVISIBLE);
            layoutWarning.setClickable(false);
        }
    }

    /**
     * 从preferences里取出relationship
     */
    private void getRelationshipFromPreferences() {
        preferencesUrl = getActivity().getSharedPreferences(MyApplication.SHAREDPREFERENCES_FILE_NAME_URL,
                Activity.MODE_WORLD_READABLE);
        relationship = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_RELATIONSHIP, MyApplication.DEFAULT_RELATIONSHIP);
    }

    /**
     * 获取菜单列表数据
     *
     * @return
     */
    public static List<MyMenu> getData() {
        List<MyMenu> data = new ArrayList<>();
        int[] iconsMyOwn = {R.drawable.ic_leftmenu_datacheck, R.drawable.ic_leftmenu_abnormalcheck,
                R.drawable.ic_leftmenu_about_us, R.drawable.ic_leftmenu_push, R.drawable.ic_leftmenu_standard};
        String[] titlesMyOwn = {"数据查看", "异常查看", "健康知识", "个性分析", "个人中心"};
        int[] iconsOthers = {R.drawable.ic_leftmenu_datacheck, R.drawable.ic_leftmenu_abnormalcheck,
                R.drawable.ic_leftmenu_about_us, R.drawable.ic_leftmenu_push,
                R.drawable.ic_home_normal};
        String[] titlesOthers = {"数据查看", "异常查看", "健康知识", "个性分析", "养老院"};

        switch (MyApplication.IS_MY_DATA) {
            case OTHERS_DATA:
                for (int i = 0; i < titlesOthers.length && i < iconsOthers.length; i++) {
                    MyMenu current = new MyMenu();
                    current.iconId = iconsOthers[i % iconsOthers.length];
                    current.title = titlesOthers[i % titlesOthers.length];
                    data.add(current);
                }
                break;
            case MY_DATA:
                for (int i = 0; i < titlesMyOwn.length && i < iconsMyOwn.length; i++) {
                    MyMenu current = new MyMenu();
                    current.iconId = iconsMyOwn[i % iconsMyOwn.length];
                    current.title = titlesMyOwn[i % titlesMyOwn.length];
                    data.add(current);
                }
                break;
        }

        return data;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARN_DRAWER, mUserLearnedDrawer + "");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();

            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(containerView);
        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferences(Context context, String preferenceName,
                                         String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.commit();
    }

    public static String readFromPreferences(Context context, String preferenceName,
                                             String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_logout:
                changeSleeper(); // 切换睡眠者
                break;
            case R.id.layout_warning:
                checkError();
                break;
        }
    }

    /**
     * 切换睡眠者
     * 跳转MemberListActivity，查看其他床垫使用者的睡眠信息
     */
    private void changeSleeper() {
        startActivity(new Intent(getActivity(), MemberListActivity.class));
        getActivity().finish();
    }

    /**
     * 检查异常状况
     */
    private void checkError() {
        MyApplication.IS_ERROR = false;
        if (ErrorActivity.class != getActivity().getClass()) {
            startActivity(new Intent(getActivity(), ErrorActivity.class));
            getActivity().finish();
        }
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }
}