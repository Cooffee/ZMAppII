package com.eric.zmappii.pojo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by eric on 15/7/5.
 */
public class Sleep {
    public static String estimate;
    public static String timeTotal;
    public static String timeDeep;
    public static String timeLight;
    public static String timeAwake;
    public static String timeStart = "";
    public static String timeEnd = "";
    private static Queue<String> queueTime = new LinkedBlockingDeque<>();

    private static final int QUEUE_LENGTH = 24;

    public static Queue<String> getQueueTime() {
        return queueTime;
    }

    /**
     * 向时间队列中添加时间值，队列长度为QUEUE_LENGTH
     * 大于QUEUE_LENGTH，则去掉队列第一个元素
     */
    public static void updateQueueTime() {
        queueTime.clear(); // 清空队列
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        // 初始化时间队列
        Date curDate = new Date();
        for (int i = 0; i < QUEUE_LENGTH; i++) {
            Date curTime = new Date(curDate.getTime() - 1000 * (QUEUE_LENGTH - i));
            queueTime.offer(df.format(curTime));
        }
    }
}
