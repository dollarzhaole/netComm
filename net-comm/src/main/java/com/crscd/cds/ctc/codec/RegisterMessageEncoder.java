package com.crscd.cds.ctc.codec;

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
public class RegisterMessageEncoder extends MessageToByteEncoder<RegisterMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterMessageEncoder.class);
    @Override
    protected void encode(ChannelHandlerContext ctx, RegisterMessage msg, ByteBuf out) throws Exception {
        LOGGER.debug("RegisterMessageEncoder: {}", msg);

        ByteBuf buf = msg.encode();
        out.writeBytes(buf.copy());
        buf.release();
    }
}
