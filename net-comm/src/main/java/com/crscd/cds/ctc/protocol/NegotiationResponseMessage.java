package com.crscd.cds.ctc.protocol;

import io.netty.buffer.ByteBuf;

/**
 * @author zhaole
 * @date 2022-04-02
 */
public class NegotiationResponseMessage {
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

    public static NegotiationResponseMessage decode(ByteBuf buffer) {
        NegotiationResponseMessage message = new NegotiationResponseMessage();
        message.window = buffer.readUnsignedByte();
        message.confirm = buffer.readUnsignedByte();
        message.overTime = buffer.readUnsignedByte();
        message.heartBeatInterval = buffer.readUnsignedByte();
        message.heartBeatOverTime = buffer.readUnsignedByte();

        return message;
    }

    @Override
    public String toString() {
        return "NegotiationResponseMessage{" +
                "window=" + window +
                ", confirm=" + confirm +
                ", overTime=" + overTime +
                ", heartBeatInterval=" + heartBeatInterval +
                ", heartBeatOverTime=" + heartBeatOverTime +
                '}';
    }
}
