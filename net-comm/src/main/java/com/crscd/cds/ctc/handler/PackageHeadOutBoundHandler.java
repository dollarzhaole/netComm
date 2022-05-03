package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.controller.FlowController;
import com.crscd.cds.ctc.protocol.PackageDefine;
import com.crscd.cds.ctc.utils.HexUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 在此Handler中包裹最外层的的包头
 * @author zhaole
 * @date 2022-04-14
 */
public class PackageHeadOutBoundHandler extends ChannelOutboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackageHeadOutBoundHandler.class);
    private final FlowController flowController;

    public PackageHeadOutBoundHandler(FlowController flowController) {
        this.flowController = flowController;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        LOGGER.debug("DoubleNetSeqOutBoundHandler: {}", msg);

        if (!(msg instanceof ByteBuf)) {
            super.write(ctx, msg, promise);
            return;
        }

        if (waitIfNeedAck()) {
            waitForAck(ctx, (ByteBuf) msg, promise);
            return;
        }

        ByteBuf out = wrap((ByteBuf) msg);

        byte[] bytes = new byte[out.readableBytes()];
        out.getBytes(0, bytes);
        LOGGER.debug("send data: {}", HexUtils.bytesToHex(bytes, 20));

        super.write(ctx, out, promise);
    }

    private void waitForAck(ChannelHandlerContext ctx, ByteBuf msg, ChannelPromise promise) {
        FlowController.WaitingAckCache cache = FlowController.WaitingAckCache.create(ctx, msg, promise);
        flowController.addCache(cache);
    }

    private boolean waitIfNeedAck() {
        return flowController.waitAckIfNecessary();
    }

    private ByteBuf wrap(ByteBuf msg) {
        flowController.updateSend();
        long packageSequence = flowController.getAndIncrementSendSequence();

        ByteBuf out = PooledByteBufAllocator.DEFAULT.buffer();

        // 设置包头
        out.writeIntLE(PackageDefine.CURRENT_VERSION);
        out.writeIntLE(msg.readableBytes());
        out.writeByte(PackageDefine.DATA);
        out.writeIntLE((int) packageSequence);

        out.writeBytes(msg);

        return out;
    }
}
