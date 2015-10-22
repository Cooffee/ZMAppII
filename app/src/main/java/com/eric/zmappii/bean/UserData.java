package com.eric.zmappii.bean;

/**
 * Created by Canon on 2015/6/4.
 */
public class UserData {
    // 心率数据
    private String curHeartbeat;
    private String aveHeartbeat;
    private String refHeartbeat;

    // 呼吸数据
    private String curBreath;
    private String aveBreath;
    private String refBreath;

    // 体动数据
    private Boolean curMovement;
    private String aveMovement;

    // 睡眠数据
    private String curSleepState;
    private String todaySleepState;

    // 数据总览
    private String todayScore;
    private String heartbeatState;
    private String heatbeatScore;
    private String breathState;
    private String breathScore;
    private String movementState;
    private String movementScore;
    private String sleepState;
    private String sleepScore;

    public UserData() {

    }

    public String getCurHeartbeat() {
        return curHeartbeat;
    }

    public void setCurHeartbeat(String curHeartbeat) {
        this.curHeartbeat = curHeartbeat;
    }

    public String getAveHeartbeat() {
        return aveHeartbeat;
    }

    public void setAveHeartbeat(String aveHeartbeat) {
        this.aveHeartbeat = aveHeartbeat;
    }

    public String getRefHeartbeat() {
        return refHeartbeat;
    }

    public void setRefHeartbeat(String refHeartbeat) {
        this.refHeartbeat = refHeartbeat;
    }

    public String getCurBreath() {
        return curBreath;
    }

    public void setCurBreath(String curBreath) {
        this.curBreath = curBreath;
    }

    public String getAveBreath() {
        return aveBreath;
    }

    public void setAveBreath(String aveBreath) {
        this.aveBreath = aveBreath;
    }

    public String getRefBreath() {
        return refBreath;
    }

    public void setRefBreath(String refBreath) {
        this.refBreath = refBreath;
    }

    public Boolean getCurMovement() {
        return curMovement;
    }

    public void setCurMovement(Boolean curMovement) {
        this.curMovement = curMovement;
    }

    public String getAveMovement() {
        return aveMovement;
    }

    public void setAveMovement(String aveMovement) {
        this.aveMovement = aveMovement;
    }

    public String getCurSleepState() {
        return curSleepState;
    }

    public void setCurSleepState(String curSleepState) {
        this.curSleepState = curSleepState;
    }

    public String getTodaySleepState() {
        return todaySleepState;
    }

    public void setTodaySleepState(String todaySleepState) {
        this.todaySleepState = todaySleepState;
    }

    public String getTodayScore() {
        return todayScore;
    }

    public void setTodayScore(String todayScore) {
        this.todayScore = todayScore;
    }

    public String getHeartbeatState() {
        return heartbeatState;
    }

    public void setHeartbeatState(String heartbeatState) {
        this.heartbeatState = heartbeatState;
    }

    public String getHeatbeatScore() {
        return heatbeatScore;
    }

    public void setHeatbeatScore(String heatbeatScore) {
        this.heatbeatScore = heatbeatScore;
    }

    public String getBreathState() {
        return breathState;
    }

    public void setBreathState(String breathState) {
        this.breathState = breathState;
    }

    public String getBreathScore() {
        return breathScore;
    }

    public void setBreathScore(String breathScore) {
        this.breathScore = breathScore;
    }

    public String getMovementState() {
        return movementState;
    }

    public void setMovementState(String movementState) {
        this.movementState = movementState;
    }

    public String getMovementScore() {
        return movementScore;
    }

    public void setMovementScore(String movementScore) {
        this.movementScore = movementScore;
    }

    public String getSleepState() {
        return sleepState;
    }

    public void setSleepState(String sleepState) {
        this.sleepState = sleepState;
    }

    public String getSleepScore() {
        return sleepScore;
    }

    public void setSleepScore(String sleepScore) {
        this.sleepScore = sleepScore;
    }
}
