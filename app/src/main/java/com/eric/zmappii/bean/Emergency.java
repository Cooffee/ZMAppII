package com.eric.zmappii.bean;

/**
 * Created by Canon on 2015/6/4.
 */
public class Emergency {
    private String emerType;
    private String emerTime;
    private String emerDate;

    public Emergency() {

    }

    public Emergency(String emerType, String emerTime, String emerDate) {
        this.emerType = emerType;
        this.emerTime = emerTime;
        this.emerDate = emerDate;
    }

    public String getEmerType() {
        return emerType;
    }

    public void setEmerType(String emerType) {
        this.emerType = emerType;
    }

    public String getEmerTime() {
        return emerTime;
    }

    public void setEmerTime(String emerTime) {
        this.emerTime = emerTime;
    }

    public String getEmerDate() {
        return emerDate;
    }

    public void setEmerDate(String emerDate) {
        this.emerDate = emerDate;
    }
}
