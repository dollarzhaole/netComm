package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.client.NetCommClient;
import com.crscd.cds.ctc.controller.DoubleNetController;
import com.crscd.cds.ctc.controller.FlowController;
import com.crscd.cds.ctc.controller.RegisterController;
import com.crscd.cds.ctc.enums.ClientFlagEnum;
import com.crscd.cds.ctc.protocol.NegotiationRequestMessage;
import com.crscd.cds.ctc.protocol.NetAddress;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author zhaole
 * @date 2022-03-26
 */
public class ClientEventHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientEventHandler.class);
    private final NetCommClient nettyClient;
    private final FlowController flowController;
    private final DoubleNetController doubleNetController;
    private final ClientFlagEnum clientFlag;
    private final NetAddress localAddress;
    private final RegisterController registerController;
    private int attempts = 0;

    public ClientEventHandler(
            NetCommClient nettyClient,
            FlowController flowController,
            DoubleNetController doubleNetController,
            ClientFlagEnum clientFlag,
            NetAddress localAddress,
            RegisterController registerController) {
        this.nettyClient = nettyClient;
        this.flowController = flowController;
        this.doubleNetController = doubleNetController;
        this.clientFlag = clientFlag;
        this.localAddress = localAddress;
        this.registerController = registerController;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) {
        LOGGER.debug("{} channel read from {}: {}", clientFlag, channelHandlerContext.channel(), o);
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        LOGGER.info(">>>>>>>>> {} connected: {}", clientFlag, channelHandlerContext.channel());
        attempts = 0;

        if (registerController.isNegotiated()) {
            return;
        }

        registerController.setNegotiated();

        final NegotiationRequestMessage negotiationRequestPackage = new NegotiationRequestMessage();
        negotiationRequestPackage.setClientId(localAddress.getProcId());

        channelHandlerContext
                .writeAndFlush(negotiationRequestPackage)
                .addListener(
                        new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture channelFuture) {
                                LOGGER.debug(
                                        "{} send negotiation package: {}",
                                        clientFlag,
                                        channelFuture.isSuccess());
                            }
                        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info(">>>>>>>>> {} offline: {}", clientFlag, ctx.channel());

        flowController.onInactive();
        doubleNetController.onInactive(clientFlag);

        // 使用过程中断线重连
        final EventLoop eventLoop = ctx.channel().eventLoop();
        if (attempts < 12) {
            attempts++;
        }
        int timeout = 2 << attempts;
        eventLoop.schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        nettyClient.start();
                    }
                },
                timeout,
                TimeUnit.SECONDS);
        ctx.fireChannelInactive();
    }
}
