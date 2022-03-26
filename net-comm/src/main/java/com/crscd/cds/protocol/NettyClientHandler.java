package com.crscd.cds.protocol;


import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author zhaole
 * @date 2022-03-26
 */
public class NettyClientHandler extends SimpleChannelInboundHandler {
    private NettyClient nettyClient;
    private String tenantId;
    private int attempts = 0;

    public NettyClientHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("service send message" + o.toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("output connected!");
        attempts = 0;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("offline");
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

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("READER_IDLE");
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                //发送心跳，保持长连接
                String s = "NettyClient" + System.getProperty("line.separator");
                ctx.channel().writeAndFlush(s);  //发送心跳成功
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                System.out.println("ALL_IDLE");
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
