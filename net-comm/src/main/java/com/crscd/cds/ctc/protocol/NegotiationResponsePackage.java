package com.crscd.cds.ctc.protocol;

/**
 * @author zhaole
 * @date 2022-04-02
 */
public class NegotiationResponsePackage {
    private Short window;
    private Short confirm;
    private Short overTime;
    private Short heartBeatInterval;
    private Short heartBeatOverTime;

    public Short getWindow() {
        return window;
    }

    public void setWindow(Short window) {
        this.window = window;
    }

    public Short getConfirm() {
        return confirm;
    }

    public void setConfirm(Short confirm) {
        this.confirm = confirm;
    }

    public Short getOverTime() {
        return overTime;
    }

    public void setOverTime(Short overTime) {
        this.overTime = overTime;
    }

    public Short getHeartBeatInterval() {
        return heartBeatInterval;
    }

    public void setHeartBeatInterval(Short heartBeatInterval) {
        this.heartBeatInterval = heartBeatInterval;
    }

    public Short getHeartBeatOverTime() {
        return heartBeatOverTime;
    }

    public void setHeartBeatOverTime(Short heartBeatOverTime) {
        this.heartBeatOverTime = heartBeatOverTime;
    }
}
