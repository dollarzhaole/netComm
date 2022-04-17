package com.crscd.cds.ctc.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @author zhaole
 * @date 2022-04-14
 */
public class DoubleNetSeqOutBoundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof byte[])) {
            super.write(ctx, msg, promise);
        } else {
//            PooledByteBufAllocator.
        }
    }
}
