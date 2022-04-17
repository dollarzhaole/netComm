package com.crscd.cds.ctc.protocol;

import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * @author zhaole
 * @date 2022-04-14
 */
public class DataMessage extends DoubleNetMessage{
    private short dataType;
    private short protocolType;
    private int forwardLength;
    private short forwardType;
    private short type;
    private short func;
    private NetAddress src;
    private List<NetAddress> dest;
    private List<ConditionalProperty> properties;

    public short getDataType() {
        return dataType;
    }

    public void setDataType(short dataType) {
        this.dataType = dataType;
    }

    public short getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(short protocolType) {
        this.protocolType = protocolType;
    }

    public int getForwardLength() {
        return forwardLength;
    }

    public void setForwardLength(int forwardLength) {
        this.forwardLength = forwardLength;
    }

    public short getForwardType() {
        return forwardType;
    }

    public void setForwardType(short forwardType) {
        this.forwardType = forwardType;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public short getFunc() {
        return func;
    }

    public void setFunc(short func) {
        this.func = func;
    }

    public NetAddress getSrc() {
        return src;
    }

    public void setSrc(NetAddress src) {
        this.src = src;
    }

    public List<NetAddress> getDest() {
        return dest;
    }

    public void setDest(List<NetAddress> dest) {
        this.dest = dest;
    }

    public List<ConditionalProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<ConditionalProperty> properties) {
        this.properties = properties;
    }

    void encode(ByteBuf byteBuf) {
        
    }
}
