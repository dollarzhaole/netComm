package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.protocol.PackageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhaole
 * @date 2022-04-14
 */
public class DoubleNetSeqOutBoundHandler extends ChannelOutboundHandlerAdapter {
    private final AtomicLong packageSeq = new AtomicLong(1);
    private final AtomicLong doubleNetSeq = new AtomicLong(1);
    private static final int OFFSET_LENGTH = 4;
    public static final int VERSION = 0x01;
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleNetSeqOutBoundHandler.class);
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        LOGGER.debug("DoubleNetSeqOutBoundHandler: {}", msg);
        super.write(ctx, msg, promise);

//        if (!(msg instanceof ByteBuf)) {
//            super.write(ctx, msg, promise);
//            return;
//        }
//
//        ByteBuf data = (ByteBuf) msg;
//
//        ByteBuf out = PooledByteBufAllocator.DEFAULT.buffer();
//
//        // 设置包头
//        out.writeIntLE(VERSION);
//        out.writeIntLE(data.readableBytes());
//        out.writeByte(PackageType.DATA);
//        out.writeIntLE((int) packageSeq.getAndIncrement());
//
//        // 设置双网层
//        long seq = doubleNetSeq.getAndIncrement();
//        out.writeIntLE((int) seq);
//        out.writeIntLE((int) (seq >> (8 * 4)));
//
//        out.writeBytes(data);
//
//        super.write(ctx, out, promise);
//
//        data.release();
    }
}