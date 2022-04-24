package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.flow.FlowController;
import com.crscd.cds.ctc.protocol.PackageDefine;
import com.crscd.cds.ctc.utils.HexUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhaole
 * @date 2022-04-14
 */
public class DoubleNetSeqOutBoundHandler extends ChannelOutboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleNetSeqOutBoundHandler.class);
    private final FlowController flowController;
    private final AtomicLong doubleNetSeq = new AtomicLong(1);

    public DoubleNetSeqOutBoundHandler(FlowController flowController) {
        this.flowController = flowController;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        LOGGER.debug("DoubleNetSeqOutBoundHandler: {}", msg);

        if (!(msg instanceof ByteBuf)) {
            super.write(ctx, msg, promise);
            return;
        }

        LOGGER.debug("msg is ByteBuf");

        waiAckIfNecessary(ctx);

        flowController.updateSend();
        long packageSequence = flowController.getAndIncrementSendSequence();

        ByteBuf data = (ByteBuf) msg;

        ByteBuf out = PooledByteBufAllocator.DEFAULT.buffer();

        // 设置包头
        out.writeIntLE(PackageDefine.CURRENT_VERSION);
        out.writeIntLE(data.readableBytes() + 8);
        out.writeByte(PackageDefine.DATA);
        out.writeIntLE((int) packageSequence);

        // 设置双网层
        long seq = doubleNetSeq.getAndIncrement();
        out.writeIntLE((int) seq);
        out.writeIntLE((int) (seq >> (8 * 4)));

        out.writeBytes(data);

        super.write(ctx, out, promise);

        byte[] bytes = new byte[out.readableBytes()];
        out.getBytes(0, bytes);
        LOGGER.trace("send data: {}", HexUtils.bytesToHex(bytes, 20));
    }

    private void waiAckIfNecessary(ChannelHandlerContext ctx) {
        try {
            flowController.waitAckIfNecessary();
        } catch (InterruptedException e) {
            LOGGER.error("waiAckIfNecessary InterruptedException, so close channel", e);
            ctx.close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    LOGGER.info("close channel because of ack overtime: {}", channelFuture.isSuccess());
                }
            });
        }
    }
}
