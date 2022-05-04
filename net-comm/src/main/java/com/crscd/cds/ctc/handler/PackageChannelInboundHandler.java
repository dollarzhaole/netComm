package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.controller.DoubleNetController;
import com.crscd.cds.ctc.controller.FlowController;
import com.crscd.cds.ctc.controller.RegisterController;
import com.crscd.cds.ctc.forward.FilterRegister;
import com.crscd.cds.ctc.protocol.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-04-12
 */
public class PackageChannelInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PackageChannelInboundHandler.class);
    private static final ByteBuf ACK_BUFFER = Unpooled.buffer(13);

    static {
        ACK_BUFFER.writeIntLE(0x01);
        ACK_BUFFER.writeIntLE(0);
        ACK_BUFFER.writeByte(PackageDefine.ACK_CONFIRM);
    }

    private final FlowController flowController;
    private final FilterRegister register;
    private final NetAddress local;
    private final DoubleNetController doubleNetController;
    private final RegisterController registerController;

    public PackageChannelInboundHandler(
            FlowController flowController,
            FilterRegister register,
            NetAddress local,
            DoubleNetController doubleNetController,
            RegisterController registerController) {
        this.flowController = flowController;
        this.register = register;
        this.local = local;
        this.doubleNetController = doubleNetController;
        this.registerController = registerController;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.debug("rec msg: {}", msg);

        ByteBuf byteBuf = (ByteBuf) msg;
        if (doChannelRead(ctx, byteBuf) && byteBuf.isReadable()) {
            super.channelRead(ctx, msg);
        }
    }

    private boolean doChannelRead(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf)
            throws Exception {
        byteBuf.readUnsignedIntLE();
        byteBuf.readUnsignedIntLE();
        short type = byteBuf.readUnsignedByte();
        long sequence = byteBuf.readUnsignedIntLE();

        if (type == PackageDefine.NEGOTIATION_RESPONSE) {
            doNegotiationResponse(channelHandlerContext, byteBuf);
            sendRegisterRequest(channelHandlerContext);
            return false;
        } else if (type == PackageDefine.HEART_BEAT) {
            doHeartBeat(channelHandlerContext);
            return false;
        } else if (type == PackageDefine.DATA) {
            LOGGER.debug(
                    "rec app data double net ({},{})",
                    byteBuf.getUnsignedIntLE(13),
                    byteBuf.getUnsignedInt(17));
            checkAndSendAckIfNecessary(channelHandlerContext, sequence);
        } else if (type == PackageDefine.ACK_CONFIRM) {
            doAckConfirm(channelHandlerContext, sequence);
            return false;
        }

        return true;
    }

    private void checkAndSendAckIfNecessary(
            ChannelHandlerContext channelHandlerContext, long sequence) {
        flowController.updateReceive(sequence);
        if (flowController.isNeedToAck()) {
            sendAck(channelHandlerContext, sequence);
            flowController.onSendAck();
        }
    }

    private void sendAck(final ChannelHandlerContext channelHandlerContext, final long sequence) {
        ByteBuf buf = ACK_BUFFER.copy();
        buf.writeIntLE((int) sequence);

        channelHandlerContext
                .writeAndFlush(buf)
                .addListener(
                        new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture channelFuture)
                                    throws Exception {
                                LOGGER.info(
                                        "send ack seq={} to {}", sequence, channelHandlerContext);
                            }
                        });
    }

    private long getPackageSequence(ByteBuf buffer) {
        if (buffer.readableBytes() > 13) {
            return buffer.getUnsignedIntLE(9);
        }

        throw new RuntimeException("data's length is less than 13");
    }

    private void doAckConfirm(ChannelHandlerContext channelHandlerContext, long sequence) {
        LOGGER.debug("recv ack from {}", channelHandlerContext);
        flowController.onReceiveAck(sequence);
    }

    private void sendRegisterRequest(final ChannelHandlerContext channelHandlerContext)
            throws InterruptedException {
        if (registerController.isRegistered()) {
            return;
        }

        registerController.setRegistered();

        MessageHead msg = MessageHead.createRegisterMessage(register, local);

        DoubleNetSequence sequence = DoubleNetSequence.createRegisterDoubleNetSequence();
        DoubleNetPackage pkt = DoubleNetPackage.create(sequence, msg);

        channelHandlerContext
                .channel()
                .writeAndFlush(pkt)
                .addListener(
                        new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture channelFuture)
                                    throws Exception {
                                LOGGER.info(
                                        "send register xml to {}: {}",
                                        channelHandlerContext.channel(),
                                        channelFuture.isSuccess());
                            }
                        });
    }

    private void doNegotiationResponse(ChannelHandlerContext context, ByteBuf byteBuf) {
        NegotiationResponseMessage message = NegotiationResponseMessage.decode(byteBuf);

        LOGGER.info("receive negotiation response from {}: {}", context.channel(), message);

        // todo 增加动态添加Handler到NettyClient
        Short windowSize = message.getWindowSize();
        Short heartBeatInterval = message.getHeartBeatInterval();
        Short heartBeatOverTime = message.getHeartBeatOverTime();
        Short overTime = message.getAckOverTime();
        Short confirm = message.getAck();

        flowController.setWindowSize(windowSize);
        flowController.setAckOverTimeInterval(overTime);

        context.channel()
                .pipeline()
                .addFirst(
                        new IdleStateHandler(
                                heartBeatOverTime, heartBeatInterval, heartBeatOverTime));
    }

    private void doHeartBeat(ChannelHandlerContext context) {
        LOGGER.debug("receive heart beat from {}", context.channel());
    }
}
