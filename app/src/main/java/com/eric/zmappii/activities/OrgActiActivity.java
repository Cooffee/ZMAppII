package com.eric.zmappii.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.eric.zmappii.R;
import com.eric.zmappii.adapters.OrgActivitiesAdapter;
import com.eric.zmappii.bean.OrgActivity;

import java.util.ArrayList;
import java.util.List;

public class OrgActiActivity extends BaseActivity {

    private Toolbar toolbar;
    private ListView lvActivities;

    private List<OrgActivity> activities;
    private OrgActivitiesAdapter adapter;

    private Context mContext = OrgActiActivity.this;

    private static final String TAG = "ZMAPP";

    private static final String TITLE = "钓鱼大赛";
    private static final String CONTENT = "\t\t为丰富老年人生活，加强老年人互动，我院将举办老年人钓鱼大赛，" +
            "此次大赛将根据老人钓鱼钓得的鱼的重量，评出一等奖一名，二等奖三名，三等奖五名。期待大家的热情参与！";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_acti);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initData(); // 初始化数据
        putIntoAdapter(); // 放入适配器
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
        lvActivities = (ListView) findViewById(R.id.lv_activities);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        activities = new ArrayList<OrgActivity>();
        for (int i = 0; i < 7; i++) {
            OrgActivity activity = new OrgActivity();
            activity.setTitle(TITLE);
            activity.setTimePub("2015-07-2" + i);
            activity.setTimeStart("2015-07-2" + (i + 2));
            activity.setTimeEnd("2015-07-25");
            activity.setContent(CONTENT);

            activities.add(activity);
        }
    }

    /**
     * 放入适配器
     */
    private void putIntoAdapter() {
        adapter = new OrgActivitiesAdapter(this, activities);
        lvActivities.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_org_acti, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
