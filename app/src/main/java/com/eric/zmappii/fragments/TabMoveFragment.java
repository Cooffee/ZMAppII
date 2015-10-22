package com.eric.zmappii.fragments;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
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
import com.eric.zmappii.activities.BaseActivity;
import com.eric.zmappii.activities.ErrorActivity;
import com.eric.zmappii.application.MyApplication;
import com.eric.zmappii.pojo.Heartbeat;
import com.eric.zmappii.pojo.Move;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
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
public class TabMoveFragment extends BaseFragment {

    private TextView tvMoveState;
    private TextView tvLastMovement;
    private ImageView imgWarning;
    private LinearLayout chartMove; // 体动图的布局声明
    private ProgressDialog progressDialog;

    private static final String TAG = "ZMAPP";
    private static final String LOADING = "数据加载中";
    private String url;

    private static final String TITLE = "movement";
    private Timer timer = new Timer();
    private TimerTask task;
    private ConnectAsyncTask mTask = null;

    private Handler handler;
    private XYSeries seriesMove;
    private XYMultipleSeriesDataset mDatasetMove;
    private XYMultipleSeriesRenderer renderer = null;

    private GraphicalView viewMovement;

    private static final int X_MAX = 6;
    private static final int X_LENGTH = 7;
    private static final int NORMAL = 1; // 正常状态
    private static final int TOSS_AND_TURN = 2; // 辗转状态
    private static final int OFF_BED = 0; // 离床状态

    /* 颜色值声明*/
    private int green;

    private Queue<Integer> qDataMovement = new LinkedBlockingQueue<Integer>();

    private SharedPreferences preferencesUrl;
    private String ip;
    private String port;
    private String phone;
    private String relationship;

    private boolean isAlarming = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initProgressDialog(); // 初始化进度条对话框
        green = getResources().getColor(R.color.colorPrimary);
        return inflater.inflate(R.layout.fragment_move, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);

        findView(); // 关联控件
        getUrlFromPreferences(); // 从preferences里取出url相关数据
        initUrl(); // 初始化接口的url
//        initMoveData(); // 初始化体动数据队列
        initWarningIcon(); // 初始化警告图标



        // 初始化体动曲线
        mDatasetMove = new XYMultipleSeriesDataset();
        seriesMove = new XYSeries(TITLE);
        // 绘制折线
        int i = 0;
        for (Integer q : qDataMovement) {
            seriesMove.add(i++, q);
        }

        mDatasetMove.addSeries(seriesMove);
        viewMovement = initChart(mDatasetMove);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 刷新图表
                updateChart();
                super.handleMessage(msg);
            }
        };

//        task = new TimerTask() {
//            @Override
//            public void run() {
//                Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);
//            }
//        };
//        timer.schedule(task, 0, 1000);
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
    public void onDestroy() {
        stopAsyncTaskRunning(); // 关闭异步
        super.onDestroy();
    }

    /**
     * 关联控件
     */
    private void findView() {
        tvMoveState = (TextView) getActivity().findViewById(R.id.tv_body_move_state);
        tvLastMovement = (TextView) getActivity().findViewById(R.id.tv_ave_body_move_estimation);
        imgWarning = (ImageView) getActivity().findViewById(R.id.img_warning_move);
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
     * 初始化接口的url
     */
    private void initUrl() {
        url = "http://" + ip + ":" + port + "/interation/getState?appUserPhone=" +
                phone + "&relationship=" + relationship;
    }

    /**
     * 初始化体动数据
     */
    private void initMoveData() {
        showProgressDialog(progressDialog); // 显示进度条对话框
        mTask = new ConnectAsyncTask(url);
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        hideProgressDialog(progressDialog); // 隐藏进度条

        for (int i = 0; i < X_LENGTH; i++) {
            qDataMovement.offer((int)(Math.random() * 2 + 0.5));
        }
    }

    /**
     * 初始化报警图标
     */
    private void initWarningIcon() {
        if (true == MyApplication.IS_MOVE_ERROR)
            imgWarning.setVisibility(View.VISIBLE);
        else
            imgWarning.setVisibility(View.INVISIBLE);
    }

    /**
     * 初始化图表
     * @param mDataset
     * @return
     */
    public GraphicalView initChart(XYMultipleSeriesDataset mDataset) {
        renderer = getRenderer();
        GraphicalView view = ChartFactory.getBarChartView(getActivity(),
                mDataset, renderer, BarChart.Type.DEFAULT);
        view.setBackgroundColor(Color.WHITE);

        chartMove= (LinearLayout)  getView().findViewById(R.id.chart_move);
        chartMove.addView(view);
        return view;
    }

    /**
     * 初始化进度条弹出框
     */
    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false); // 点击弹出框外围，不退出弹出框
        progressDialog.setMessage(LOADING);
    }

    private XYMultipleSeriesRenderer getRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setZoomButtonsVisible(true);

        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(green);

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
        renderer.setYAxisMax(2);
        renderer.setYLabelsColor(0, green); // 设置Y轴刻度颜色
        renderer.setYLabelsAlign(Paint.Align.CENTER.RIGHT); // 设置Y轴刻度下面的值相对位置
        renderer.setYLabels(0);
        renderer.addYTextLabel(0, "离床");
        renderer.addYTextLabel(1, "正常");
        renderer.addYTextLabel(2, "辗转");

        renderer.setXAxisMin(0); // 设置X轴最小值是0
        renderer.setXAxisMax(X_MAX); // 设置X轴最大值是60
        renderer.setXLabelsColor(green); // 设置X轴刻度颜色
        renderer.setXLabels(0); // 设置x轴刻度个数
        int j = 0;
        Move.updateQueueTime(); // 更新时间队列
        for (String d : Move.getQueueTime()) {
            renderer.addXTextLabel(j++, String.valueOf(d));
        }
    }

    // --------------图方法结束------------------
    private void updateChart() {
        stopAsyncTaskRunning(); // 关闭异步
        mTask = new ConnectAsyncTask(url);
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mDatasetMove.removeSeries(seriesMove);
        seriesMove.clear();
        // TODO 更新数据 没有网络时，不用注释掉下列三行
//        int y = Move.isMove;
//        qDataMovement.remove();
//        qDataMovement.offer(y);
        int i = 0;
        for (Integer q : qDataMovement) {
            seriesMove.add(i++, q);
        }
        mDatasetMove.addSeries(seriesMove);
        // 更新x轴显示时间
        int j = 0;
        for (String t : Move.getQueueTime()) {
            renderer.addXTextLabel(j++, String.valueOf(t));
        }
        viewMovement.repaint();
        chartMove.postInvalidate();
    }

    private void stopAsyncTaskRunning() {
        if (mTask != null &&
                AsyncTask.Status.RUNNING == mTask.getStatus()) {
            mTask.cancel(true);
        }
    }

    class ConnectAsyncTask extends AsyncTask {

        String result = "";
        String curMovement = "";
        String lastMovement = "";
        String urlString = "";
        String abnormalType = "";

        ConnectAsyncTask(String url) {
            urlString = url;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            HttpGet httpRequest = new HttpGet(urlString);
            Log.d(TAG, "movement--- " + urlString);
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
                    int state = mattressUser.getInt("curMovementState") + 1;
                    curMovement = state + "";
                    // 根据状态值判断
                    switch (state) {
                        case NORMAL:
                            curMovement = "正常";
                            break;
                        case TOSS_AND_TURN:
                            curMovement = "辗转";
                            break;
                        case OFF_BED:
                            curMovement = "离床";
                            break;
                    }

                    // 根据时间占比做不同的描述
                    double lastMovePersent = mattressUser.getDouble("lastMovePersent");
                    lastMovement = lastMovePersent + "";
                    if (lastMovePersent < 0.2) {
                        lastMovement = "较少";
                    } else {
                        lastMovement = "较多";
                    }
                    JSONArray movementPoints = mattressUser.getJSONArray("movementPoints");
                    qDataMovement.clear();
                    for (int j = 0; j < movementPoints.length(); j++) {
                        qDataMovement.add(movementPoints.getInt(j) + 1); // 接口与app的数值相差一
                    }
                    Move.updateQueueTime(); // 更新时间队列
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            tvMoveState.setText(curMovement);
            tvLastMovement.setText(lastMovement);
        }
    }
}
