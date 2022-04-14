package com.crscd.cds.ctc.codec;

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
public class ApplicationDataDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDataDecoder.class);
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        LOGGER.debug("ApplicationDataDecoder decode, byteBuf={}", byteBuf);
    }
}
