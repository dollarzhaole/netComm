package com.crscd.cds.ctc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

/**
 * @author zhaole
 * @date 2022-04-08
 */
public class MessageHead {
    protected final int FORWARD_LENGTH_OFFSET = 2;
    private short dataType;
    private short protocolType;

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

    ByteBuf encode() {
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeByte(dataType);
        buffer.writeByte(protocolType);
        buffer.writeShortLE(0);

        return buffer;
    }
}
