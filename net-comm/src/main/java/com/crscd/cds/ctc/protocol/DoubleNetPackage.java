package com.crscd.cds.ctc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

/**
 * @author zhaole
 * @date 2022-05-02
 */
public class DoubleNetPackage {
    private DoubleNetSequence doubleNetSequence;
    private MessageHead messageHead;

    public static DoubleNetPackage create(DoubleNetSequence doubleNetSequence, MessageHead messageHead) {
        DoubleNetPackage pkt = new DoubleNetPackage();
        pkt.doubleNetSequence = doubleNetSequence;
        pkt.messageHead = messageHead;
        return pkt;
    }

    public final ByteBuf encode() {
        ByteBuf result = PooledByteBufAllocator.DEFAULT.buffer();

        doubleNetSequence.encode(result);
        ByteBuf buf = messageHead.encode();

        result.writeBytes(buf);

        return result;
    }

    @Override
    public String toString() {
        return "package" + doubleNetSequence;
    }
}
