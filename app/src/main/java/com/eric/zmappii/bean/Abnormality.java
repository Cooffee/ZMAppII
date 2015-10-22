package com.eric.zmappii.bean;

/**
 * Created by Canon on 2015/6/4.
 */
public class Abnormality {
    private String type; // 数据异常类型
    private String time; // 异常发生时间
    private String date; // 异常发生日期

    public Abnormality(String type, String time, String date) {
        super();
        this.type = type;
        this.time = time;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

