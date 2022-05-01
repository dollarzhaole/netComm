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

        if (waitIfNeedAck()) {
            waitForAck(ctx, (ByteBuf) msg, promise);
            return;
        }

        ByteBuf out = wrap((ByteBuf) msg);

        super.write(ctx, out, promise);

        byte[] bytes = new byte[out.readableBytes()];
        out.getBytes(0, bytes);
        LOGGER.trace("send data: {}", HexUtils.bytesToHex(bytes, 20));
    }

    private void waitForAck(ChannelHandlerContext ctx, ByteBuf msg, ChannelPromise promise) {
        FlowController.WaitingAckCache cache = FlowController.WaitingAckCache.create(ctx, msg, promise);
        flowController.addCache(cache);
    }

    private boolean waitIfNeedAck() {
        return flowController.waitAckIfNecessary();
    }

    private ByteBuf wrap(ByteBuf msg) throws Exception {
        flowController.updateSend();
        long packageSequence = flowController.getAndIncrementSendSequence();

        ByteBuf out = PooledByteBufAllocator.DEFAULT.buffer();

        // 设置包头
        out.writeIntLE(PackageDefine.CURRENT_VERSION);
        out.writeIntLE(msg.readableBytes() + 8);
        out.writeByte(PackageDefine.DATA);
        out.writeIntLE((int) packageSequence);

        // 设置双网层
        long seq = doubleNetSeq.getAndIncrement();
        out.writeIntLE((int) seq);
        out.writeIntLE((int) (seq >> (8 * 4)));

        out.writeBytes(msg);

        return out;
    }
}
