package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.protocol.PackageDefine;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HeartbeatServer Handler.
 *
 * @author zhaole
 * @date 2022-03-2
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatHandler.class);
    private static final ByteBuf HEART_BEAT_BUF = Unpooled.buffer(PackageDefine.HEART_BEAT_LENGTH);

    static {
        HEART_BEAT_BUF.writeIntLE(PackageDefine.CURRENT_VERSION);
        HEART_BEAT_BUF.writeIntLE(0);
        HEART_BEAT_BUF.writeByte(PackageDefine.HEART_BEAT);
        HEART_BEAT_BUF.writeIntLE(0);
    }

    private int index = 0;

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                onReadIdle(ctx);
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                onWriteIdle(ctx);
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                LOGGER.debug("all idle on {}", ctx.channel());
            }
        }

        super.userEventTriggered(ctx, evt);
    }

    private void onReadIdle(final ChannelHandlerContext ctx) {
        LOGGER.debug("read idle from {}", ctx.channel());
        try {
            ctx.close()
                    .addListener(
                            new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture channelFuture)
                                        throws Exception {
                                    LOGGER.info("read idle, so channel closed: {}", ctx);
                                }
                            });
        } catch (Exception e) {
            LOGGER.error("HeartBeatHandler.onReadIdle exception", e);
        }
    }

    private void onWriteIdle(final ChannelHandlerContext ctx) throws InterruptedException {
        if (ctx.channel().isActive()) {
            index += 1;
            LOGGER.debug("before write heart {} beat to {}", index, ctx.channel());
            // 此处要context发送数据，是因为避免从pipline的tail发送
            ByteBuf buf = HEART_BEAT_BUF.copy();
            buf.setShortLE(9, index);
            ctx.writeAndFlush(buf).sync();
            LOGGER.debug("after write heart {} beat to {}", index, ctx.channel());
        } else {
            try {
                LOGGER.info("ctx is inactive, so about to close {}", ctx);
                ctx.close()
                        .addListener(
                                new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture channelFuture)
                                            throws Exception {
                                        LOGGER.info("ctx is inactive, so close {} finished", ctx);
                                    }
                                });
            } catch (Exception e) {
                LOGGER.error("close exception:", e);
            }
        }
    }
}
