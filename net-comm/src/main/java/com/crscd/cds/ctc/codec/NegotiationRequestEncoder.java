package com.crscd.cds.ctc.codec;

import com.crscd.cds.ctc.protocol.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhaole
 * @date 2022-04-02
 */
public class NegotiationRequestEncoder extends MessageToByteEncoder<NegotiationRequestMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NegotiationRequestEncoder.class);
    private static final int OFFSET_LENGTH = 4;
    private final AtomicLong packageSeq = new AtomicLong(1);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NegotiationRequestMessage msg, ByteBuf out) throws Exception {
        LOGGER.debug("PackageEncoder, msg={}", msg);

        out.writeIntLE(PackageDefine.CURRENT_VERSION);
        out.writeIntLE(0);
        out.writeByte(PackageDefine.NEGOTIATION_REQUEST);
        out.writeIntLE((int) packageSeq.getAndIncrement());

        if (packageSeq.get() > Integer.MAX_VALUE * 2L) {
            packageSeq.set(0);
        }

        out.writeShortLE(msg.getClientId());

        out.setIntLE(out.readerIndex() + OFFSET_LENGTH, out.readableBytes() - PackageDefine.HEART_BEAT_LENGTH);
    }
}
