package com.eric.zmappii.activities;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.eric.zmappii.R;

import cn.bmob.v3.Bmob;

/**
 * Created by eric on 15/7/1.
 */
public class BaseActivity extends ActionBarActivity {

    private Context mContext = BaseActivity.this;
    private static final String MY_APP_BMOB_ID = "0abc5fe7e21fe4f0b4c7e00e929c7c56";

    private static final int NOTIFICATION_FLAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBmob();
    }

    private void initBmob() {
        Bmob.initialize(mContext, MY_APP_BMOB_ID);
    }

    /**
     * 根据EditText文本内容是否为空判断是否disable按钮
     * 如果有一个EditText文本内容为空，则disable按钮
     * @param button
     * @param editText
     */
    protected void enableButtonOrNot(final Button button, final EditText... editText) {
        int length = editText.length; // 表示传入的EditText的个数
        final boolean[] flag = new boolean[length]; // 表示各个edittext文本内容是否为空，false表示空，true表示非空

        for (int i = 0; i < length; i++) {
            final int finalI = i;

            // 初始时查看EditText是否有内容
            if (editText[finalI].getText().toString().length() == 0) {
                flag[finalI] = false;
            } else {
                flag[finalI] = true;
            }
            if (allNotEmpty(flag)) {
                enableButton(button);
            } else {
                disableButton(button);
            }

            // EditText文本有变即监听是否有内容
            editText[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (editText[finalI].getText().toString().length() == 0) {
                        flag[finalI] = false;
                    } else {
                        flag[finalI] = true;
                    }
                    if (allNotEmpty(flag)) {
                        enableButton(button);
                    } else {
                        disableButton(button);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });


        }
    }

    /**
     * 判断是不是所有的标记表示的都是非空
     * 如果有一个表示的为空，则返回false
     * 否则，返回true
     *
     * @param flag
     * @return
     */
    private boolean allNotEmpty(boolean[] flag) {
        for (int i = 0; i < flag.length; i++) {
            if (false == flag[i])
                return false;
        }
        return true;
    }

    /**
     * 使按钮点击无效
     * @param button
     */
    protected void disableButton(Button button) {
        button.setEnabled(false);
    }

    /**
     * 使按钮点击有效
     * @param button
     */
    protected void enableButton(Button button) {
        button.setEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void showNotification(String relationship, String message) {
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                new Intent(mContext, ErrorActivity.class), 0);
        Notification notify = new Notification.Builder(mContext)
                .setSmallIcon(R.drawable.zm_launcher)
                .setTicker("知眠：" + "您有一则异常睡眠消息，请注意查收！")
                .setContentTitle(relationship + ": " + message)
                .setContentIntent(pendingIntent)
                .setNumber(1)
                .build();
        notify.defaults |= Notification.DEFAULT_SOUND;
        notify.defaults |= Notification.DEFAULT_LIGHTS;
//        notify.defaults |= Notification.DEFAULT_VIBRATE; // 震动

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(NOTIFICATION_FLAG, notify);
    }
}
