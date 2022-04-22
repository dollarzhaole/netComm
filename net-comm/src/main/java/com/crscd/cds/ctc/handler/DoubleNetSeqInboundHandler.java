package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.filter.DoubleNetSequence;
import com.crscd.cds.ctc.protocol.PackageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-04-12
 */
public class DoubleNetSeqInboundHandler extends ChannelInboundHandlerAdapter {
    private DoubleNetSequence recSequence;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            handleDoubleNetSequence((ByteBuf) msg);
        }

        super.channelRead(ctx, msg);
    }

    private void handleDoubleNetSequence(ByteBuf msg) {
        if (recSequence == null) {
            recSequence = new DoubleNetSequence(0, 0);
        }

        if (!msg.isReadable()) {
            return;
        }

        long low = msg.readUnsignedIntLE();
        long high = msg.readUnsignedIntLE();

        if (recSequence.isContinuous(low, high)) {
            recSequence.set(low, high);
        }
    }
}
