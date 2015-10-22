package com.eric.zmappii.fragments;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.eric.zmappii.R;
import com.eric.zmappii.activities.ErrorActivity;

/**
 * Created by eric on 15/7/12.
 */
public class BaseFragment extends Fragment {

    private Context mContext = null;
    private static final int NOTIFICATION_FLAG = 1;



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void showNotification(String relationship, String abMsg) {
        String relationMsg;
        switch (relationship) {
            case "mother":
                relationMsg = "母亲的睡眠";
                break;
            case "father":
                relationMsg = "父亲的睡眠";
                break;
            case "me":
                relationMsg = "我的睡眠";
                break;
            default:
                relationMsg = "睡眠情况";
                break;
        }
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                new Intent(mContext, ErrorActivity.class), 0);
        Notification notify = new Notification.Builder(mContext)
                .setSmallIcon(R.drawable.zm_launcher)
                .setTicker("知眠：" + "您有一则异常睡眠消息，请注意查收！")
                .setContentTitle(relationship + ": " + abMsg)
                .setContentIntent(pendingIntent)
                .setNumber(1)
                .build();
        notify.defaults |= Notification.DEFAULT_SOUND;
        notify.defaults |= Notification.DEFAULT_LIGHTS;
//        notify.defaults |= Notification.DEFAULT_VIBRATE; // 震动

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(NOTIFICATION_FLAG, notify);
    }

    /**
     * 显示进度条对话框
     * @param progressDialog 要显示的进度条控件
     */
    protected void showProgressDialog(ProgressDialog progressDialog) {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 隐藏进度条对话框
     * @param progressDialog 要隐藏的进度条控件
     */
    protected void hideProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
