package com.crscd.cds.ctc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-04-14
 */
public class DoubleNetSeq2OutBoundHandler extends ChannelOutboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleNetSeq2OutBoundHandler.class);
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        LOGGER.debug("DoubleNetSeq2OutBoundHandler: {}", msg);

        super.write(ctx, msg, promise);
    }
}
