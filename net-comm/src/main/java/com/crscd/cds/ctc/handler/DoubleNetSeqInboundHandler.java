package com.crscd.cds.ctc.handler;

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
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            
            if (byteBuf.isReadable()) {
                byteBuf.readUnsignedIntLE();
                byteBuf.readUnsignedIntLE();
            }
        }

        super.channelRead(ctx, msg);
    }
}
