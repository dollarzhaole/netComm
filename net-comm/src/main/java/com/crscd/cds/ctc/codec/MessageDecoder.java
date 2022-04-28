package com.crscd.cds.ctc.codec;

import com.crscd.cds.ctc.flow.InboundDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/** 应用层协议
 * @author zhaole
 * @date 2022-04-03
 */
public class MessageDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDecoder.class);
    private final InboundDispatcher dispatcher;

    public MessageDecoder(InboundDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        LOGGER.debug("ApplicationDataDecoder decode, byteBuf={}", byteBuf);

        long bodyLen = byteBuf.readUnsignedIntLE();
        ByteBuf bodyBuf = byteBuf.readBytes((int) bodyLen);

        byte[] data = new byte[bodyBuf.readableBytes()];
        bodyBuf.getBytes(0, data);

        list.add(data);

        if (dispatcher != null && data.length > 2) {
            dispatcher.dispatch(data[0], data[1], data);
        }
    }
}
