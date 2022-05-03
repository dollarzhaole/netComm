package com.crscd.cds.ctc.protocol;

import com.crscd.cds.ctc.forward.FilterRegister;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.List;

/**
 * @author zhaole
 * @date 2022-04-08
 */
public class MessageHead {
    private short dataType;
    private short protocolType;
    private short forwardType;
    private short type;
    private short func;
    private NetAddress src;
    private List<NetAddress> dest;
    private List<ConditionalProperty> properties;
    private byte[] data;

    public static MessageHead createApplicationData(short type, short func, byte[] data, NetAddress src) {
        return create(type, func, data, DataType.FORWARD, src);
    }

    public static MessageHead createApplicationData(byte[] data, NetAddress src) {
        if (data.length < 2) {
            return null;
        }

        short type = (short) data[0];
        short func = (short) data[1];

        return createApplicationData(type, func, data, src);
    }

    public static MessageHead createRegisterMessage(FilterRegister register, NetAddress src) {
        RegisterMessage messageHead = RegisterMessage.create(register);
        byte[] data = messageHead.encode();

        return create(data, src);
    }

    private static MessageHead create(byte[] data, NetAddress src) {
        if (data.length < 2) {
            return null;
        }

        short type = (short) data[0];
        short func = (short) data[1];

        return create(type, func, data, DataType.REGISTER, src);
    }

    private static MessageHead create(short type, short func, byte[] data, short dataType, NetAddress src) {
        MessageHead message = new MessageHead();
        message.setForwardType(DataType.MSGPACK_FORWARD_TYPE_BY_REG_LOCAL_DISPATCH);
        message.setData(data);
        message.setType(type);
        message.setFunc(func);
        message.setDest(null);
        message.setProperties(null);
        message.setDataType(dataType);
        message.setProtocolType(DataType.PROTOCOL_TYPE_418);
        message.setSrc(src);

        return message;
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

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

    public final ByteBuf encode() {
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeByte(dataType);
        buffer.writeByte(protocolType);
        buffer.writeShortLE(0);

        buffer.writeByte(forwardType);
        buffer.writeByte(type);
        buffer.writeByte(func);

        src.encode(buffer);

        buffer.writeShortLE(dest == null ? 0 : dest.size());

        if (dest != null) {
            for (NetAddress destAddress : dest) {
                destAddress.encode(buffer);
            }
        }

        // 设置属性个数，忽略
        buffer.writeByte(properties == null ? 0 : properties.size());
        if (properties != null) {
            for (ConditionalProperty property : properties) {
                property.encode(buffer);
            }
        }

        int forwardLen = buffer.readableBytes() - 4;

        buffer.writeIntLE(data.length);
        buffer.writeBytes(data);

        buffer.setShortLE(2, forwardLen);

        return buffer;
    }

    public final void decode(ByteBuf buffer) {
        dataType = buffer.readUnsignedByte();
        protocolType = buffer.readUnsignedByte();
        int forwardLen = buffer.readUnsignedShortLE();

        forwardType = buffer.readByte();
        type = buffer.readByte();
        func = buffer.readByte();

        src = new NetAddress();
        src.decode(buffer);

        int destLen = buffer.readUnsignedShortLE();
        for (int i = 0; i < destLen; i++) {
            NetAddress address = new NetAddress();
            address.decode(buffer);
        }

        short propertiesLen = buffer.readUnsignedByte();
        for (int i = 0; i < propertiesLen; i++) {
            ConditionalProperty property = new ConditionalProperty();
            property.decode(buffer);
        }
    }
}
