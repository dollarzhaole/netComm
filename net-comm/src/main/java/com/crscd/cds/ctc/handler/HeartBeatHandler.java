package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.protocol.Message;
import com.crscd.cds.ctc.protocol.Package;
import com.crscd.cds.ctc.protocol.PackageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * HeartbeatServer Handler.
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 1.0.0 2019年12月19日
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatHandler.class);
    private static final ByteBuf HEART_BEAT_BUF = Unpooled.buffer(13);

    static {
        HEART_BEAT_BUF.writeIntLE(0x01);
        HEART_BEAT_BUF.writeIntLE(13);
        HEART_BEAT_BUF.writeByte(PackageType.HEART_BEAT);
        HEART_BEAT_BUF.writeIntLE(0);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                LOGGER.debug("read idle from {}", ctx.channel());
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                LOGGER.debug("before write heart beat to {}", ctx.channel());
                ctx.channel().writeAndFlush(HEART_BEAT_BUF.copy()).sync();
                LOGGER.debug("after write heart beat to {}", ctx.channel());
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                LOGGER.debug("all idle on {}", ctx.channel());
            }
        }

        super.userEventTriggered(ctx, evt);
    }
}
