package com.crscd.cds.ctc.codec;

import com.crscd.cds.ctc.protocol.MessageHead;
import com.crscd.cds.ctc.protocol.RegisterMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-04-19
 */
public class MessageEncoder extends MessageToByteEncoder<MessageHead> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEncoder.class);
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageHead msg, ByteBuf out) throws Exception {
        LOGGER.debug("RegisterMessageEncoder: {}", msg);

        ByteBuf buf = msg.encode();
        out.writeBytes(buf.copy());
    }
}
