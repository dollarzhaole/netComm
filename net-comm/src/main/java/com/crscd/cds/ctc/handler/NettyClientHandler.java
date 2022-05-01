package com.crscd.cds.ctc.handler;


import java.util.concurrent.TimeUnit;

import com.crscd.cds.ctc.client.NettyClient;
import com.crscd.cds.ctc.flow.FlowController;
import com.crscd.cds.ctc.protocol.NegotiationRequestMessage;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-03-26
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);
    private NettyClient nettyClient;
    private int attempts = 0;
    private final FlowController flowController;

    public NettyClientHandler(NettyClient nettyClient, FlowController flowController) {
        this.nettyClient = nettyClient;
        this.flowController = flowController;
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
