package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.controller.DoubleNetController;
import com.crscd.cds.ctc.enums.ClientFlagEnum;
import com.crscd.cds.ctc.controller.DoubleNetSequence;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-04-12
 */
public class DoubleNetSeqInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleNetSeqInboundHandler.class);
    private final ClientFlagEnum clientFlag;
    private final DoubleNetController doubleNetController;

    public DoubleNetSeqInboundHandler(ClientFlagEnum clientFlag, DoubleNetController doubleNetController) {
        this.clientFlag = clientFlag;
        this.doubleNetController = doubleNetController;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean continueRead = false;
        if (msg instanceof ByteBuf) {
            continueRead = handleDoubleNetSequence((ByteBuf) msg, ctx);
        }

        if (continueRead) {
            super.channelRead(ctx, msg);
        }
    }

    private boolean handleDoubleNetSequence(ByteBuf msg, ChannelHandlerContext ctx) {
        if (!msg.isReadable()) {
            return false;
        }

        long low = msg.readUnsignedIntLE();
        long high = msg.readUnsignedIntLE();

        DoubleNetSequence sequence = DoubleNetSequence.create(low, high);
        DoubleNetController.ValidResultEnum result = doubleNetController.validate(sequence, clientFlag);
        if (result.equals(DoubleNetController.ValidResultEnum.ABANDON)) {
            return false;
        }

        if (result.equals(DoubleNetController.ValidResultEnum.ACCEPT)) {
            return true;
        }

        if (result.equals(DoubleNetController.ValidResultEnum.CLOSE_ONE)) {
            ctx.close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) {
                    LOGGER.info("close {} because of double net sequence error: {}", clientFlag, channelFuture.isSuccess());
                }
            });
        }

        if (result.equals(DoubleNetController.ValidResultEnum.CLOSE_BOTH)) {
            ctx.close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) {
                    LOGGER.info("close {} because of double net sequence error: {}", clientFlag, channelFuture.isSuccess());
                }
            });
        }

        return false;
    }
}
