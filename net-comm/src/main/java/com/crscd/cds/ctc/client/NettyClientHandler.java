package com.crscd.cds.ctc.client;


import java.util.concurrent.TimeUnit;

import com.crscd.cds.ctc.protocol.MessagePackage;
import com.crscd.cds.ctc.protocol.NegotiationRequestPackage;
import com.crscd.cds.ctc.protocol.PackageType;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author zhaole
 * @date 2022-03-26
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<MessagePackage> {
    private NettyClient nettyClient;
    private String tenantId;
    private int attempts = 0;

    public NettyClientHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessagePackage o) {
        System.out.println("recv from server: " + o.toString());

//        NegotiationRequestPackage pkt = new NegotiationRequestPackage();
//        // client id of tdci should be 129 ~ 159
//        pkt.setClientId(129);
//        pkt.setLength(2L);
//        pkt.setVersion(1L);
//        pkt.setSeq(0L);
//        pkt.setType(PackageType.NEGOTIATION_REQUEST);
//
//        channelHandlerContext.channel().writeAndFlush(pkt).addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                System.out.println("send negotiation package " + channelFuture.isSuccess());
//            }
//        });

//        channelHandlerContext.writeAndFlush(o);
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("connected: " + channelHandlerContext.channel().toString());

        NegotiationRequestPackage pkt = new NegotiationRequestPackage();
        // client id of tdci should be 129 ~ 159
        pkt.setClientId(129);
        pkt.setLength(2L);
        pkt.setVersion(1L);
        pkt.setSeq(0L);
        pkt.setType(PackageType.NEGOTIATION_REQUEST);

        channelHandlerContext.channel().writeAndFlush(pkt).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println("send negotiation package " + channelFuture.isSuccess());
            }
        });

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
//        if (evt instanceof IdleStateEvent) {
//            IdleStateEvent event = (IdleStateEvent) evt;
//            if (event.state().equals(IdleState.READER_IDLE)) {
//                System.out.println("READER_IDLE");
//            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
//                //发送心跳，保持长连接
//                String s = "NettyClient" + System.getProperty("line.separator");
//                ctx.channel().writeAndFlush(s);  //发送心跳成功
//            } else if (event.state().equals(IdleState.ALL_IDLE)) {
//                System.out.println("ALL_IDLE");
//            }
//        }
        super.userEventTriggered(ctx, evt);
    }
}
