package com.eric.zmappii.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eric.zmappii.R;
import com.eric.zmappii.activities.ErrorActivity;
import com.eric.zmappii.application.MyApplication;
import com.eric.zmappii.pojo.Heartbeat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
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
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by Canon on 2015/4/10.
 */
public class TabHeartbeatFragment extends BaseFragment implements View.OnClickListener {

    private TextView tvCurHeartbeat;
    private TextView tvHeartbeatState;
    private TextView tvLastHeartbeat;
    private ImageView imgWarning;
    private ProgressDialog progressDialog;
    private LinearLayout chartHeart; // 心率图的布局声明

    private static final String TAG = "ZMAPP";
    private static final String LOADING = "数据加载中";
    private static final String TITLE = "heartbeat";
    private String url;
    private static final int POINT_ACCOUNT = 7;

    private Timer timer;
    private TimerTask task;
    private Handler handler;
    private ConnectAsyncTask mTask = null;

    private XYSeries seriesHeart;
    private XYMultipleSeriesDataset mDatasetHeart;
    private XYMultipleSeriesRenderer renderer = null;

    private GraphicalView viewHeartChart;

    private static final int X_MAX = 6;
    private static final int X_LENGTH = 7; // x轴的刻度个数

    private static final int MIN_HEARTBEAT_RATE = 40;
    private static final int MAX_HEARTBEAT_RATE = 110;

    /* 颜色值声明*/
    private int green;
    private Queue<Integer> qDataHeartbeat = new LinkedBlockingQueue<Integer>();

    private SharedPreferences preferencesUrl;

    private String ip;
    private String port;
    private String phone;
    private String relationship;

    private boolean stateShow = true;
    private String stateHeartbeat = "无人";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        green = getResources().getColor(R.color.colorPrimary);
        initProgressDialog(); // 初始化进度条对话框
        return inflater.inflate(R.layout.fragment_heartbeat, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        findView(); // 绑定控件
        getUrlFromPreferences(); // 从preferences里取出url的端口号、ip、appUserPhone、relationship
        initUrl(); // 初始化接口的url
        initListener(); // 初始化监听事件
        initWarningIcon(); // 初始化警告图标

        // 初始化心率曲线
        mDatasetHeart = new XYMultipleSeriesDataset();
        seriesHeart = new XYSeries(TITLE);
        // 绘制折线
        int i = 0;
        String str = "";
        for (Integer q : qDataHeartbeat) {
            str += " " + q;
            seriesHeart.add(i++, q);
        }

        mDatasetHeart.addSeries(seriesHeart);
        viewHeartChart = initChart(mDatasetHeart);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 刷新图表
                updateChart();
                super.handleMessage(msg);
            }
        };

        setTimer(); // 设置定时器
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            stopTimer();
        } else {
            setTimer();
        }

    }

    /**
     * 设置定时器
     */
    private void setTimer() {
        if (null == timer) {
            timer = new Timer();
        }
        if (null == task) {
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            };
        }
        if (null != timer && null != task) {
            timer.schedule(task, 0, 1000);
        }
    }

    /**
     * 取消定时器
     */
    private void stopTimer() {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != task) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 关联控件
     */
    private void findView() {
        tvCurHeartbeat = (TextView) getActivity().findViewById(R.id.tv_cur_hearbeat_rate);
        tvHeartbeatState = (TextView) getActivity().findViewById(R.id.tv_heartbeat_state);
        tvLastHeartbeat = (TextView) getActivity().findViewById(R.id.tv_ave_heartbeat_rate);
        imgWarning = (ImageView) getActivity().findViewById(R.id.img_warning_heartbeat);
    }

    /**
     * 从preferences里面取出ip、port、phone、relationship
     */
    private void getUrlFromPreferences() {
        preferencesUrl = getActivity().getSharedPreferences(MyApplication.SHAREDPREFERENCES_FILE_NAME_URL,
                Activity.MODE_WORLD_READABLE);
        ip = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_IP, MyApplication.DEFAULT_IP);
        port = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_PORT, MyApplication.DEFAULT_PORT);
        phone = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_PHONE, MyApplication.DEFAULT_PHONE);
        relationship = preferencesUrl.getString(MyApplication.SHAREDPREFERENCES_KEY_RELATIONSHIP, MyApplication.DEFAULT_RELATIONSHIP);
    }

    /**
     * 初始化接口的url
     */
    private void initUrl() {
        url = "http://" + ip + ":" + port + "/interation/getState?appUserPhone=" +
                phone + "&relationship=" + relationship;
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        imgWarning.setOnClickListener(this);
    }

    /**
     * 初始化警报图标
     */
    private void initWarningIcon() {
        if (true == MyApplication.IS_HEARTBEAT_ERROR)
            imgWarning.setVisibility(View.VISIBLE);
        else
            imgWarning.setVisibility(View.INVISIBLE);
    }

    /**
     * 初始化进度条对话框
     */
    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(LOADING);
    }

    public GraphicalView initChart(XYMultipleSeriesDataset mDataset) {
        renderer = getRenderer();
        GraphicalView view = ChartFactory.getLineChartView(getActivity(),
                mDataset, renderer);
        view.setBackgroundColor(Color.WHITE);

        chartHeart = (LinearLayout) getView().findViewById(R.id.chart_heart);
        chartHeart.addView(view);
        return view;
    }

    private XYMultipleSeriesRenderer getRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setZoomButtonsVisible(true);

        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(green);
        r.setPointStyle(PointStyle.CIRCLE); // 设置点的样式
        r.setFillPoints(true); // 填充点（显示的点是空心还是实心）
        r.setDisplayChartValues(true); // 将点的值显示出来
        r.setDisplayChartValuesDistance(2); // 让每一个点都显示数值
        r.setChartValuesSpacing(10); // 显示的点的值与图的距离
        r.setChartValuesTextSize(24); // 点的值的文字大小
        r.setLineWidth(2); // 设置线宽

        // 往xymultiplerender中增加一个系列
        renderer.addSeriesRenderer(r);
        setChartSettings(renderer);

        return renderer;
    }

    private void setChartSettings(XYMultipleSeriesRenderer renderer) {
        renderer.setAxisTitleTextSize(20); // 设置坐标轴标题文本大小
        renderer.setShowLegend(false); // 设置不显示图例文字
        renderer.setLabelsTextSize(24); // 设置轴标签文本大小
        renderer.setPointSize((5f)); // 设置点的大小
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
        renderer.setYAxisMax(120);
        renderer.setYLabels(8); // 设置y轴显示的刻度标签个数
        renderer.setYLabelsColor(0, green); // 设置Y轴刻度颜色
        renderer.setYLabelsAlign(Paint.Align.CENTER.RIGHT); // 设置Y轴刻度下面的值相对位置

        renderer.setXAxisMin(0); // 设置X轴最小值是0
        renderer.setXAxisMax(X_MAX); // 设置X轴最大值是60
        renderer.setXLabelsColor(green); // 设置X轴刻度颜色
        renderer.setXLabels(0); // 设置x轴刻度个数
        int j = 0;
        Heartbeat.updateQueueTime(); // 更新时间队列
        for (String d : Heartbeat.getQueueTime()) {
            renderer.addXTextLabel(j++, String.valueOf(d));
        }
    }

    // --------------图方法结束------------------
    private void updateChart() {
        stopAsyncTaskRunning();
        mTask = new ConnectAsyncTask(url, tvCurHeartbeat);
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mDatasetHeart.removeSeries(seriesHeart);
        seriesHeart.clear();
        // 更新数据
        // TODO 没网时，不注释下列三行
//        int y = Heartbeat.curValue;
//        qDataHeartbeat.remove();
//        qDataHeartbeat.offer(y);
        int i = 0;
        for (Integer q : qDataHeartbeat) {
            seriesHeart.add(i++, q);
        }
        mDatasetHeart.addSeries(seriesHeart);
        // 更新x轴显示时间
        int j = 0;
        for (String t : Heartbeat.getQueueTime()) {
            renderer.addXTextLabel(j++, String.valueOf(t));
        }

        viewHeartChart.repaint();
        chartHeart.postInvalidate();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_warning_heartbeat:
                checkError(); // 检查异常
                break;
        }
    }

    private void checkError() {
        MyApplication.IS_MOVE_ERROR = false;
        MyApplication.IS_HEARTBEAT_ERROR = false;
        MyApplication.IS_BREATHE_ERROR = false;
        startActivity(new Intent(getActivity(), ErrorActivity.class));
        getActivity().finish();
    }

    /**
     * 如果当前存在正在运行的异步，则关闭
     */
    private void stopAsyncTaskRunning() {
        if (mTask != null &&
                AsyncTask.Status.RUNNING == mTask.getStatus()) {
            mTask.cancel(true);
        }
    }

    class ConnectAsyncTask extends AsyncTask<String, Void, Boolean> {

        String result = "";
        String curHeartbeat = "";
        int curHeartbeatInt = -1;
        String lastHearteat = "";
        String urlString = "";
        int curMovementState;

        TextView tvCurHeart;

        ConnectAsyncTask(String url, TextView textView) {
            urlString = url;
            tvCurHeart = textView;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            HttpGet httpRequest = new HttpGet(urlString);
            Log.d(TAG, "heartbeat--- " + urlString);
            // 发送请求并等待响应
            HttpResponse httpResponse = null;
            try {
                httpResponse = new DefaultHttpClient().execute(httpRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 若状态码为200 OK
            if (200 == httpResponse.getStatusLine().getStatusCode()) {
                HttpEntity httpEntity = httpResponse.getEntity();
                try {
                    result = EntityUtils.toString(httpEntity, "UTF-8");
                    JSONObject mattressUserList = new JSONObject(result).getJSONObject("mattressUserList");
                    JSONObject mattressUser = mattressUserList.getJSONObject(relationship);
                    curMovementState = mattressUser.getInt("curMovementState");
                    if (-1 == curMovementState) {
                        curHeartbeat = "";
                        stateHeartbeat = "无人";
                        qDataHeartbeat.clear();
                        for (int j = 0; j < POINT_ACCOUNT; j++) {
                            qDataHeartbeat.add(0);
                        }
                    } else {
                        curHeartbeatInt = mattressUser.getInt("curHeartRate");
                        curHeartbeat = curHeartbeatInt + "次/分钟";
                        if (-1 == curHeartbeatInt) {
                            stateHeartbeat = "";
                            curHeartbeat = "--";
                            qDataHeartbeat.clear();
                            for (int j = 0; j < POINT_ACCOUNT; j++) {
                                qDataHeartbeat.add(0);
                            }
                        } else {
                            if (curHeartbeatInt > MIN_HEARTBEAT_RATE && curHeartbeatInt < MAX_HEARTBEAT_RATE) {
                                stateHeartbeat = "平稳";
                            } else if (curHeartbeatInt < MIN_HEARTBEAT_RATE) {
                                stateHeartbeat = "偏低";
                            } else {
                                stateHeartbeat = "偏高";
                            }
                            JSONArray hearbeatPoints = mattressUser.getJSONArray("heartbeatPoints");
                            qDataHeartbeat.clear();
                            for (int j = 0; j < hearbeatPoints.length(); j++) {
                                qDataHeartbeat.add(hearbeatPoints.getInt(j));
                            }
                        }
                    }
                    lastHearteat = mattressUser.getInt("lastHeartRate") + "次/分钟";
                    Heartbeat.updateQueueTime(); // 更新时间队列
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            hideProgressDialog(progressDialog); // 隐藏进度条对话框
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            tvCurHeart.setText(curHeartbeat);
            tvLastHeartbeat.setText(lastHearteat);
            tvHeartbeatState.setText(stateHeartbeat);
        }
    }
}
