package com.crscd.cds.ctc.handler;


import java.util.concurrent.TimeUnit;

import com.crscd.cds.ctc.client.NettyClient;
import com.crscd.cds.ctc.controller.DoubleNetController;
import com.crscd.cds.ctc.controller.FlowController;
import com.crscd.cds.ctc.enums.ClientFlagEnum;
import com.crscd.cds.ctc.protocol.NegotiationRequestMessage;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-03-26
 */
public class ClientEventHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientEventHandler.class);
    private final NettyClient nettyClient;
    private int attempts = 0;
    private final FlowController flowController;
    private final DoubleNetController doubleNetController;
    private final ClientFlagEnum clientFlag;

    public ClientEventHandler(NettyClient nettyClient, FlowController flowController, DoubleNetController doubleNetController, ClientFlagEnum clientFlag) {
        this.nettyClient = nettyClient;
        this.flowController = flowController;
        this.doubleNetController = doubleNetController;
        this.clientFlag = clientFlag;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) {
        System.out.println("recv from server: " + o.toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        LOGGER.debug(">>>>>>>>> connected: {}", channelHandlerContext.channel());

        NegotiationRequestMessage negotiationRequestPackage = new NegotiationRequestMessage();

        // client id of tdci should be 129 ~ 159
        negotiationRequestPackage.setClientId(129);

        channelHandlerContext.writeAndFlush(negotiationRequestPackage).sync();
        LOGGER.debug("send negotiation package, pkt={}", negotiationRequestPackage);

        attempts = 0;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info(">>>>>>>>> offline: {}", ctx.channel());

        flowController.onInactive();
        doubleNetController.onInactive(clientFlag);


        //使用过程中断线重连
        final EventLoop eventLoop = ctx.channel().eventLoop();
        if (attempts < 12) {
            attempts++;
        }
        int timeout = 2 << attempts;
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                nettyClient.start();
            }
        }, timeout, TimeUnit.SECONDS);
        ctx.fireChannelInactive();
    }
}
