package com.crscd.cds.ctc.protocol;

import io.netty.buffer.ByteBuf;

/**
 * @author zhaole
 * @date 2022-04-02
 */
public class NegotiationResponseMessage {
    private Short windowSize;
    private Short ack;
    private Short ackOverTime;
    private Short heartBeatInterval;
    private Short heartBeatOverTime;

    public static NegotiationResponseMessage decode(ByteBuf buffer) {
        NegotiationResponseMessage message = new NegotiationResponseMessage();
        message.windowSize = buffer.readUnsignedByte();
        message.ack = buffer.readUnsignedByte();
        message.ackOverTime = buffer.readUnsignedByte();
        message.heartBeatInterval = buffer.readUnsignedByte();
        message.heartBeatOverTime = buffer.readUnsignedByte();

        return message;
    }

    public Short getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(Short windowSize) {
        this.windowSize = windowSize;
    }

    public Short getAck() {
        return ack;
    }

    public void setAck(Short ack) {
        this.ack = ack;
    }

    public Short getAckOverTime() {
        return ackOverTime;
    }

    public void setAckOverTime(Short ackOverTime) {
        this.ackOverTime = ackOverTime;
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

    @Override
    public String toString() {
        return "NegotiationResponseMessage{"
                + "window="
                + windowSize
                + ", confirm="
                + ack
                + ", overTime="
                + ackOverTime
                + ", heartBeatInterval="
                + heartBeatInterval
                + ", heartBeatOverTime="
                + heartBeatOverTime
                + '}';
    }
}
