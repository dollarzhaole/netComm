package com.crscd.cds.ctc.protocol;

import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * @author zhaole
 * @date 2022-04-14
 */
public class ForwardMessage extends MessageHead {
    private short forwardType;
    private short type;
    private short func;
    private NetAddress src;
    private List<NetAddress> dest;
    private List<ConditionalProperty> properties;

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

    @Override
    ByteBuf encode() {
        ByteBuf buf = super.encode();
        return buf;
    }
}
