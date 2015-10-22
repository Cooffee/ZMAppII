package com.eric.zmappii.fragments;

import android.content.Context;
import android.content.Entity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.eric.zmappii.R;
import com.eric.zmappii.algorithm.ScoreAlg;
import com.eric.zmappii.application.MyApplication;
import com.eric.zmappii.bean.Overview;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

/**
 * Created by Canon on 2015/4/10.
 */
@SuppressWarnings("unused")
public class TabOverviewFragment extends Fragment {

    private TextView tvScore;
    private RatingBar rbStar;
    private TextView tvComment;

    private TextView tvStateHeartbeat;
    private TextView tvStateBreath;
    private TextView tvStateMove;
    private TextView tvStateSleep;

    private TextView tvScoreHeartbeat;
    private TextView tvScoreBreath;
    private TextView tvScoreMove;
    private TextView tvScoreSleep;

    private static final String TAG = "ZMAPP";

    private String scoreTotal;
    private String comment;
    private String stateHeartbeat;
    private String stateBreath;
    private String stateMove;
    private String stateSleep;
    private String scoreHeartbeat;
    private String scoreBreath;
    private String scoreMove;
    private String scoreSleep;
    private String url;

    private static final int STAR_NUM = 5;

    private SharedPreferences preferencesUrl;
    private String ip;
    private String port;
    private String phone;
    private String relationship;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findView(); // 关联控件
        getUrlFromPreferences(); // 从preferences里取出url相关数据
        initUrl(); // 初始化url
//        new JSONAsyncTask().execute(""); // 异步获取json数据
        new ConnectAsyncTask(url).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }


    /**
     * 关联控件
     */
    private void findView() {
        rbStar = (RatingBar) getActivity().findViewById(R.id.rb_star);
        tvComment = (TextView) getActivity().findViewById(R.id.tv_total_comment);

        tvStateHeartbeat = (TextView) getActivity().findViewById(R.id.tv_state_heartbeat);
        tvStateBreath = (TextView) getActivity().findViewById(R.id.tv_state_breathe);
        tvStateMove = (TextView) getActivity().findViewById(R.id.tv_state_move);
        tvStateSleep = (TextView) getActivity().findViewById(R.id.tv_state_sleep);

        tvScoreHeartbeat = (TextView) getActivity().findViewById(R.id.tv_score_heartbeat);
        tvScoreBreath = (TextView) getActivity().findViewById(R.id.tv_score_breathe);
        tvScoreMove = (TextView) getActivity().findViewById(R.id.tv_score_move);
        tvScoreSleep = (TextView) getActivity().findViewById(R.id.tv_score_sleep);
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
     * 初始化url
     */
    private void initUrl() {
        url = "http://" + ip + ":" + port + "/interation/getState?appUserPhone=" +
                phone + "&relationship=" + relationship;
    }

    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                json();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            updateView(); // 更新视图
        }

        /**
         * 更新视图
         */
        private void updateView() {
//            tvScore.setText(scoreTotal);
            tvComment.setText(comment);
            tvStateHeartbeat.setText(stateHeartbeat);
            tvStateBreath.setText(stateBreath);
            tvStateMove.setText(stateMove);
            tvStateSleep.setText(stateSleep);
            tvScoreHeartbeat.setText(scoreHeartbeat);
            tvScoreBreath.setText(scoreBreath);
            tvScoreMove.setText(scoreMove);
            tvScoreSleep.setText(scoreSleep);


            rbStar.setProgress(string2int(scoreTotal) * STAR_NUM);
        }

        /**
         * 字符串转化为整型
         *
         * @param str
         * @return
         */
        private int string2int(String str) {
            String subString = "";
            if (str.contains(".")) {
                subString = str.substring(0, str.indexOf("."));
            }
            return Integer.valueOf(subString);
        }

        private void json() throws IOException {
            try {
                // 将json文件读取到buffer数组中
                InputStream is = getActivity().getResources().openRawResource(R.raw.overview);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);

                // 将字节数组转换为以GB2312编码的字符串
                String json = new String(buffer, "UTF-8");

                // 将字符串json转换为json对象，以便于取出数据
                JSONObject overview = new JSONObject(json).getJSONObject("overview");

                // 获得命名为overview的JSONObject对象
                scoreTotal = overview.getString("score_total");
                comment = overview.getString("comment");
                stateHeartbeat = overview.getString("state_heartbeat");
                stateBreath = overview.getString("state_breath");
                stateMove = overview.getString("state_move");
                stateSleep = overview.getString("state_sleep");
                scoreHeartbeat = overview.getString("score_heartbeat");
                scoreBreath = overview.getString("score_breath");
                scoreMove = overview.getString("score_move");
                scoreSleep = overview.getString("score_sleep");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 异步获取数据
     */
    public class ConnectAsyncTask extends AsyncTask {
        // 通过AsyncTask类提交数据 异步显示
        private String result;
        private String urlApi;
        private int lastBreathRate;
        private int lastHeartRate;
        private double lastMovePersent;
        private double lastDeepSleepPersent = 0.008;
        private double scoreGeneral = 0;

        ConnectAsyncTask(String urlApi) {
            this.urlApi = urlApi;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            HttpGet httpRequest = new HttpGet(urlApi);
            HttpResponse httpResponse = null;
            try {
                httpResponse = new DefaultHttpClient().execute(httpRequest);
                HttpEntity httpEntity = httpResponse.getEntity();
                result = EntityUtils.toString(httpEntity, "utf-8");
                JSONObject mattressUserList = new JSONObject(result).getJSONObject("mattressUserList");
                JSONObject sleeper = mattressUserList.getJSONObject(relationship);
                lastBreathRate = sleeper.getInt("lastBreathRate");
                lastHeartRate = sleeper.getInt("lastHeartRate");
                lastMovePersent = sleeper.getDouble("lastMovePersent");
                scoreGeneral = ScoreAlg.getFinalScore(lastHeartRate, lastBreathRate, lastMovePersent, lastDeepSleepPersent);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            tvScoreHeartbeat.setText(lastHeartRate + "");
            tvScoreBreath.setText(lastBreathRate + "");
            String movePersent = formatNum(lastMovePersent * 100, 2);
            tvScoreMove.setText(movePersent + "%");
            String scoreSleep = formatNum(lastDeepSleepPersent * 100.0, 2);
            tvScoreSleep.setText(scoreSleep + "%");
            rbStar.setProgress((int) (scoreGeneral * STAR_NUM));
        }

        /**
         * 保留n为小数
         *
         * @param num 待处理的double类型数
         * @return 字符串型的格式化后数
         */
        private String formatNum(double num, int n) {
            String formatStr = "##0.";
            for (int i = 0; i < n; i++) {
                formatStr += "0";
            }
            DecimalFormat df = new DecimalFormat(formatStr);
            return df.format(num);
        }
    }
}
