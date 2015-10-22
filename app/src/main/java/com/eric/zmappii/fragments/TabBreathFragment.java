package com.eric.zmappii.fragments;

import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eric.zmappii.R;
import com.eric.zmappii.activities.ErrorActivity;
import com.eric.zmappii.application.MyApplication;
import com.eric.zmappii.pojo.Breath;
import com.eric.zmappii.pojo.Heartbeat;

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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Canon on 2015/4/10.
 */
public class TabBreathFragment extends BaseFragment implements View.OnClickListener {

    private TextView tvCurBreath;
    private TextView tvBreathState;
    private TextView tvLastBreath;
    private ImageView imgWarning;
    private ProgressDialog progressDialog;

    private static final String TAG = "ZMAPP";
    private static final String LOADING = "数据加载中";
    private static final String TITLE = "breath";
    private String url;
    private static final int POINT_ACCOUNT = 7;

    private Timer timer;
    private TimerTask mTask;
    private Handler handler;
    private ConnectAsyncTask connTask = null;

    private XYSeries seriesBreath;
    private XYMultipleSeriesDataset mDatasetBreath;
    private XYMultipleSeriesRenderer renderer = null;
    private GraphicalView viewBreathChart;

    private static final int X_MAX = 6;
    private static final int X_LENGTH = 7; // x轴的刻度个数

    private static final int MAX_BREATH_RATE = 20;
    private static final int MIN_BREATH_RATE = 10;

    /* 颜色值声明*/
    private int green;

    private LinearLayout chartBreath; // 呼吸图的布局声明

    private Queue<Integer> qDataBreath = new LinkedBlockingQueue<Integer>();

    private SharedPreferences preferencesUrl;
    private String ip;
    private String port;
    private String phone;
    private String relationship;

    private boolean isAlarming = false;

    private boolean stateShow = true;
    private String stateBreath = "无人";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        green = getResources().getColor(R.color.colorPrimary);
        initProgressDialog(); // 初始化进度条对话框
        return inflater.inflate(R.layout.fragment_breath, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        findView(); // 关联控件
        getUrlFromPreferences();
        initUrl(); // 初始化接口的url
//        initBreathData(); // 初始化呼吸数据队列
        initListener(); // 初始化监听事件
        initWarningIcon(); // 初始化报警图标

        // 初始化呼吸曲线
        mDatasetBreath = new XYMultipleSeriesDataset();
        seriesBreath = new XYSeries(TITLE);
        // 绘制折线
        int i = 0;
        for (Integer q : qDataBreath) {
            seriesBreath.add(i++, q);
        }

        mDatasetBreath.addSeries(seriesBreath);
        viewBreathChart = initChart(mDatasetBreath);

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

        if (null == mTask) {
            mTask = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            };
        }

        if (null != timer && null != mTask) {
            timer.schedule(mTask, 0, 1000);
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

        if (null != mTask) {
            mTask.cancel();
            mTask = null;
        }
    }

    @Override
    public void onDestroy() {
        stopTimer();
        super.onDestroy();
    }

    /**
     * 关联控件
     */
    private void findView() {
        tvCurBreath = (TextView) getActivity().findViewById(R.id.tv_cur_breathe_rate);
        tvBreathState = (TextView) getActivity().findViewById(R.id.tv_breathe_rate_state);
        tvLastBreath = (TextView) getActivity().findViewById(R.id.tv_ave_breathe_rate);
        imgWarning = (ImageView) getActivity().findViewById(R.id.img_warning_breath);
    }

    private void getUrlFromPreferences() {
        preferencesUrl = getActivity().getSharedPreferences(MyApplication.SHAREDPREFERENCES_FILE_NAME_URL,
                Context.MODE_WORLD_READABLE);
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
     * 初始化呼吸数据队列
     */
//    private void initBreathData() {
//        showProgressDialog(progressDialog); // 显示进度条对话框
//        connTask = new ConnectAsyncTask(url);
//        connTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); // 使用线程池
//        hideProgressDialog(progressDialog); // 隐藏进度条
//        for (int i = 0; i < X_LENGTH; i++) {
//            qDataBreath.offer((int) (Math.random() * 10 + 15));
//        }
//    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        imgWarning.setOnClickListener(this);
    }

    /**
     * 初始化警告图标
     */
    private void initWarningIcon() {
        if (MyApplication.IS_BREATHE_ERROR)
            imgWarning.setVisibility(View.VISIBLE);
        else
            imgWarning.setVisibility(View.INVISIBLE);
    }

    /**
     * 初始化图表
     *
     * @param mDataset
     * @return
     */
    public GraphicalView initChart(XYMultipleSeriesDataset mDataset) {
        renderer = getRenderer();
        GraphicalView view = ChartFactory.getLineChartView(getActivity(),
                mDataset, renderer);
        view.setBackgroundColor(Color.WHITE);

        chartBreath = (LinearLayout) getView().findViewById(R.id.chart_breath);
        chartBreath.addView(view);
        return view;
    }

    /**
     * 初始化进度条对话框
     */
    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(LOADING);
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

        renderer.setYAxisMin(0); // 设置y轴最小值是0
        renderer.setYAxisMax(40); // 设置y轴最大值
        renderer.setYLabels(4); // 设置y轴显示的刻度标签个数
        renderer.setYLabelsColor(0, green); // 设置Y轴刻度颜色
        renderer.setYLabelsAlign(Paint.Align.CENTER.RIGHT); // 设置Y轴刻度下面的值相对位置

        renderer.setXAxisMin(0); // 设置X轴最小值是0
        renderer.setXAxisMax(X_MAX); // 设置X轴最大值是60
        renderer.setXLabelsColor(green); // 设置X轴刻度颜色
        renderer.setXLabels(0); // 设置x轴刻度个数
        int j = 0;
        Breath.updateQueueTime(); // 更新时间队列
        for (String d : Breath.getQueueTime()) {
            renderer.addXTextLabel(j++, String.valueOf(d));
        }
    }

    // --------------图方法结束------------------
    private void updateChart() {
        stopAsyncTask(); // 关闭异步
        connTask = new ConnectAsyncTask(url);
        connTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mDatasetBreath.removeSeries(seriesBreath);
        seriesBreath.clear();
        // 更新数据
        // TODO 没有联网时，不注释下面三行
//        int y = Breath.curValue;
//        qDataBreath.remove();
//        qDataBreath.offer(y);
        int i = 0;
        for (Integer q : qDataBreath) {
            seriesBreath.add(i++, q);
        }
        mDatasetBreath.addSeries(seriesBreath);
        // 更新x轴显示时间
        int j = 0;
        for (String t : Breath.getQueueTime()) {
            renderer.addXTextLabel(j++, String.valueOf(t));
        }
        viewBreathChart.repaint();
        Object[] values = qDataBreath.toArray();
        chartBreath.postInvalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_warning_breath:
                checkError(); // 查看异常
                break;
        }
    }

    /**
     * 查看异常
     */
    private void checkError() {
        MyApplication.IS_BREATHE_ERROR = false;
        MyApplication.IS_HEARTBEAT_ERROR = false;
        MyApplication.IS_MOVE_ERROR = false;
        startActivity(new Intent(getActivity(), ErrorActivity.class));
        getActivity().finish();
    }

    /**
     * 关闭在运行的异步
     */
    private void stopAsyncTask() {
        if (connTask != null &&
                AsyncTask.Status.RUNNING == connTask.getStatus()) {
            connTask.cancel(true); // 关闭异步
        }
    }

    class ConnectAsyncTask extends AsyncTask<String, Void, Boolean> {
        String result = "";
        String curBreath = "";
        int curBreathInt = -1;
        String lastBreath = "";
        String urlString = "";
        int curMovementState;

        ConnectAsyncTask(String url) {
            urlString = url;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            HttpGet httpRequest = new HttpGet(urlString);
            Log.d(TAG, "breath--- " + urlString);
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
                        stateBreath = "无人";
                        curBreath = "";
                        qDataBreath.clear(); // 清除原队列里的数据
                        for (int j = 0; j < POINT_ACCOUNT; j++) {
                            qDataBreath.add(0);
                        }
                    } else {
                        curBreathInt = mattressUser.getInt("curBreathRate");
                        Log.d(TAG, "breath value-------: " + curBreathInt);
                        curBreath = curBreathInt + "次/分钟";
                        if (-1 == curBreathInt) {
                            curBreath = "--";
                            stateBreath = "";
                            qDataBreath.clear(); // 清除原队列里的数据
                            for (int j = 0; j < POINT_ACCOUNT; j++) {
                                qDataBreath.add(0);
                            }
                        } else {
                            if (curBreathInt > MIN_BREATH_RATE && curBreathInt < MAX_BREATH_RATE) {
                                stateBreath = "舒畅";
                            } else if (curBreathInt < MIN_BREATH_RATE) {
                                stateBreath = "迟缓";
                            } else {
                                stateBreath = "急促";
                            }
                            JSONArray breathPoints = mattressUser.getJSONArray("breathPoints");
                            qDataBreath.clear(); // 清除原来队列里的数据
                            for (int j = 0; j < breathPoints.length(); j++) {
                                qDataBreath.add(breathPoints.getInt(j));
                            }
                        }
                    }
                    lastBreath = mattressUser.getInt("lastBreathRate") + "次/分钟";
                    Breath.updateQueueTime(); // 更新时间队列
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
            tvCurBreath.setText(curBreath);
            tvLastBreath.setText(lastBreath);
            tvBreathState.setText(stateBreath);
        }
    }
}
