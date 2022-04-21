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
    private static NetAddress src;
    private List<NetAddress> dest;
    private List<ConditionalProperty> properties;
    private byte[] data;

    public static ForwardMessage create(short type, short func, byte[] data) {
        ForwardMessage message = new ForwardMessage();
        message.setForwardType(DataType.MSGPACK_FORWARD_TYPE_BY_REG_LOCAL_DISPATCH);
        message.setData(data);
        message.setType(type);
        message.setFunc(func);
        message.setDest(null);
        message.setProperties(null);
        message.setDataType(DataType.FORWARD);
        message.setProtocolType(DataType.PROTOCOL_TYPE_418);

        return message;
    }

    public static ForwardMessage create(byte[] data) {
        if (data.length < 2) {
            return null;
        }

        short type = (short) data[0];
        short func = (short) data[1];

        return create(type, func, data);
    }

    public short getForwardType() {
        return forwardType;
    }

    public void setForwardType(short forwardType) {
        this.forwardType = forwardType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
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
    public void encode(ByteBuf out) {
        out.writeByte(forwardType);
        out.writeByte(type);
        out.writeByte(func);

        src.encode(out);

        out.writeShortLE(dest == null ? 0 : dest.size());

        if (dest != null) {
            for (NetAddress destAddress : dest) {
                destAddress.encode(out);
            }
        }

        // 设置属性个数，忽略
        out.writeByte(0);
        out.writeIntLE(data.length);
        out.writeBytes(data);
    }
}
