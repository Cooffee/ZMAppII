package com.eric.zmappii.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eric.zmappii.R;
import com.eric.zmappii.application.MyApplication;
import com.eric.zmappii.pojo.Sleep;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

public class TabSleepFragment extends Fragment implements View.OnClickListener {

    private TextView tvEstimate;
    private TextView tvTimeTotal;
    private TextView tvTimeDeep;
    private TextView tvTimeLight;
    private TextView tvTimeAwake;
    private LinearLayout chartSleep; // 呼吸图的布局声明
    private RelativeLayout layoutCircle;

    private static final String TAG = "ZMAPP";

    private final static String TITLE = "movement";
    private XYSeries seriesSleep;
    private XYMultipleSeriesDataset mDatasetSleep;
    private XYMultipleSeriesRenderer renderer;

    private GraphicalView viewSleep;

    private static final int X_LENGTH = 24; // x轴的刻度个数

    /* 颜色值声明*/
    private int green;
    private String timeStart = "";
    private String timeStop = "";
    private String url = "";

    private Queue<Integer> qDataSleep = new LinkedBlockingQueue<Integer>();

    private static final String DIALOG_TITLE = "健康建议";
    private static final int POINT_ACCOUNT = 45;

    private SharedPreferences preferencesUrl;
    private String ip;
    private String port;
    private String phone;
    private String relationship;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        green = getResources().getColor(R.color.colorPrimary);
        return inflater.inflate(R.layout.fragment_sleep, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        findView(); // 关联控件
        getUrlFromPreferences(); // 从preferences里取出url相关数据
        initUrl(); // 初始化接口的url
        initListener(); // 初始化监听事件

        // 初始化呼吸曲线
        mDatasetSleep = new XYMultipleSeriesDataset();
        seriesSleep = new XYSeries(TITLE);

        volleyGetData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * volley获取数据
     */
    private void volleyGetData() {
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject mattressUserList = response.getJSONObject("mattressUserList");
                            JSONObject mattressUse = mattressUserList.getJSONObject(relationship);
                            JSONObject lastNight = mattressUse.getJSONObject("lastNight");

                            String lastSleepEstimation = lastNight.getString("lastSleepEstimation");
                            String timeTotal = lastNight.getString("timeTotal");
                            String timeDeepSleep = lastNight.getString("timeDeepSleep");
                            String timeLightSleep = lastNight.getString("timeLightSleep");
                            String timeAwake = lastNight.getString("timeAwake");

                            timeStart = lastNight.getString("timeStart");
                            timeStop = lastNight.getString("timeStop");
                            JSONArray sleepPoints = lastNight.getJSONArray("sleepPoints");
                            if (0 != qDataSleep.size()) {
                                qDataSleep.clear();
                            }
                            for (int i = 0; i < POINT_ACCOUNT; i++) {
                                if (i >= sleepPoints.length()) {
                                    qDataSleep.add(0);
                                } else {
                                    qDataSleep.add(sleepPoints.getInt(i));
                                }
                            }

                            tvEstimate.setText(lastSleepEstimation);
                            tvTimeTotal.setText(timeTotal);
                            tvTimeDeep.setText(timeDeepSleep);
                            tvTimeLight.setText(timeLightSleep);
                            tvTimeAwake.setText(timeAwake);

                            // 初始化折线
                            int i = 0;
                            for (Integer q : qDataSleep) {
                                seriesSleep.add(i++, q);
                            }

                            mDatasetSleep.addSeries(seriesSleep);
                            viewSleep = initChart(mDatasetSleep);

                            // 在图中添加睡眠开始时间和睡眠结束时间
                            for (int j = 0; j < POINT_ACCOUNT; j++) {
                                if (0 == j) {
                                    renderer.addXTextLabel(j, timeStart);
                                } else if ((POINT_ACCOUNT - 2) == j) {
                                    renderer.addXTextLabel(j, timeStop);
                                } else {
                                    renderer.addXTextLabel(j, "");
                                }
                            }

//                            viewSleep.repaint();
//                            chartSleep.postInvalidate();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rq.add(jsonObjectRequest);
    }

    /**
     * 关联控件
     */
    private void findView() {
        tvEstimate = (TextView) getActivity().findViewById(R.id.tv_sleep_depth_estimation);
        tvTimeTotal = (TextView) getActivity().findViewById(R.id.tv_time_total);
        tvTimeDeep = (TextView) getActivity().findViewById(R.id.tv_time_deep);
        tvTimeLight = (TextView) getActivity().findViewById(R.id.tv_time_light);
        tvTimeAwake = (TextView) getActivity().findViewById(R.id.tv_time_awake);
        chartSleep = (LinearLayout) getActivity().findViewById(R.id.chart_sleep);
        layoutCircle = (RelativeLayout) getActivity().findViewById(R.id.layout_circle);
    }

    /**
     * 从preferences里取出url相关数据
     */
    private void getUrlFromPreferences() {
        preferencesUrl = getActivity().getSharedPreferences(MyApplication.SHAREDPREFERENCES_FILE_NAME_URL,
                Context.MODE_WORLD_READABLE);
        ip = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_IP, MyApplication.DEFAULT_IP);
        port = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_PORT, MyApplication.DEFAULT_PORT);
        phone = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_PHONE, MyApplication.DEFAULT_PHONE);
        relationship = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_RELATIONSHIP, MyApplication.DEFAULT_RELATIONSHIP);
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        url = "http://" + ip + ":" + port + "/interation/getState?appUserPhone=" +
                phone + "&relationship=" + relationship;
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        layoutCircle.setOnClickListener(this);
    }

    /**
     * 初始化图标
     * @param mDataset
     * @return
     */
    public GraphicalView initChart(XYMultipleSeriesDataset mDataset) {
        renderer = getRenderer();
        GraphicalView view = ChartFactory.getBarChartView(getActivity(),
                mDataset, renderer, BarChart.Type.DEFAULT);
        view.setBackgroundColor(Color.WHITE);
        chartSleep.addView(view);
        return view;
    }

    /**
     * 获取renderer
     * @return
     */
    private XYMultipleSeriesRenderer getRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setZoomButtonsVisible(true);

        XYSeriesRenderer rDeep = new XYSeriesRenderer();
        rDeep.setColor(green);
        rDeep.setPointStyle(PointStyle.CIRCLE); // 设置点的样式

        // 往xymultiplerender中增加一个系列
        renderer.addSeriesRenderer(rDeep);
        setChartSettings(renderer);

        return renderer;
    }

    /**
     * 图标设置
     * @param renderer
     */
    private void setChartSettings(XYMultipleSeriesRenderer renderer) {
        renderer.setAxisTitleTextSize(20); // 设置坐标轴标题文本大小
        renderer.setShowLegend(false); // 设置不显示图例文字
        renderer.setLabelsTextSize(24); // 设置轴标签文本大小
        renderer.setPointSize((5f)); // 设置点的大小
        renderer.setYLabelsPadding(30);
        renderer.setAxesColor(green);
        renderer.setLabelsColor(green);
        renderer.setDisplayValues(true);
        renderer.setZoomEnabled(false, false);//设置x，y方向都不可以放大或缩小
        renderer.setPanEnabled(false, false);//设置x方向,y方向不可以滑动
        renderer.setMarginsColor(Color.WHITE);
        renderer.setBackgroundColor(Color.WHITE); // 设置背景色
        renderer.setApplyBackgroundColor(true);
        renderer.setFitLegend(true);

        renderer.setYAxisMin(0); // 设置y轴左小值是0
        renderer.setYAxisMax(2);
        renderer.setYLabelsColor(0, green); // 设置Y轴刻度颜色
        renderer.setYLabelsAlign(Paint.Align.CENTER.RIGHT); // 设置Y轴刻度下面的值相对位置
        renderer.setYLabels(0);
        renderer.addYTextLabel(0, "醒");
        renderer.addYTextLabel(1, "浅");
        renderer.addYTextLabel(2, "深");

        renderer.setXAxisMin(0); // 设置X轴最小值是0
        renderer.setXAxisMax(POINT_ACCOUNT - 1); // 设置X轴最大值是X_LENDGTH - 1
        renderer.setXLabelsColor(green); // 设置X轴刻度颜色
        renderer.setXLabels(0); // 设置x轴刻度个数

        for (int j = 0; j < X_LENGTH; j++) {
            if (0 == j) {
                renderer.addXTextLabel(j, Sleep.timeStart);
            } else if ((POINT_ACCOUNT - 1) == j) {
                renderer.addXTextLabel(j, Sleep.timeEnd);
            } else {
                renderer.addXTextLabel(j, "");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_circle:
                showHealthSuggestion(); // 显示健康建议
                break;
        }
    }

    /**
     * 显示健康建议
     */
    private void showHealthSuggestion() {
        String message = "睡眠时长过短，建议提早入睡时间\n" +
                "浅睡时长过长，无法及时安稳入睡，建议睡前喝一杯牛奶，提高睡眠质量";
        new AlertDialog.Builder(getActivity())
                .setTitle(DIALOG_TITLE)
                .setMessage(message)
                .setCancelable(true)
                .show();
    }
}
