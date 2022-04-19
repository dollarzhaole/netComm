package com.crscd.cds.ctc.handler;


import java.util.concurrent.TimeUnit;

import com.crscd.cds.ctc.client.NettyClient;
import com.crscd.cds.ctc.codec.HeaderEncoder;
import com.crscd.cds.ctc.protocol.PackageHeader;
import com.crscd.cds.ctc.protocol.Package;
import com.crscd.cds.ctc.protocol.NegotiationRequestMessage;
import com.crscd.cds.ctc.protocol.PackageType;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-03-26
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<PackageHeader> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);
    private static final PackageHeader HEART_BEAT = PackageHeader.createHeartBeat(HeaderEncoder.VERSION);
    private NettyClient nettyClient;
    private int attempts = 0;

    public NettyClientHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PackageHeader o) {
        System.out.println("recv from server: " + o.toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        LOGGER.debug(">>>>>>>>> connected: {}", channelHandlerContext.channel());

        Package<NegotiationRequestMessage> pkt = new Package<NegotiationRequestMessage>();
        PackageHeader header = new PackageHeader();
        NegotiationRequestMessage negotiationRequestPackage = new NegotiationRequestMessage();

        // client id of tdci should be 129 ~ 159
        negotiationRequestPackage.setClientId(129);
        header.setLength(2L);
        header.setVersion(1L);
        header.setSeq(0L);
        header.setType(PackageType.NEGOTIATION_REQUEST);
        pkt.setHeader(header);
        pkt.setData(negotiationRequestPackage);

        channelHandlerContext.channel().writeAndFlush(pkt).sync();
        LOGGER.debug("send negotiation package, pkt={}", pkt);

        attempts = 0;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info(">>>>>>>>> offline: {}", ctx.channel());

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

//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof IdleStateEvent) {
//            IdleStateEvent event = (IdleStateEvent) evt;
//            if (event.state().equals(IdleState.READER_IDLE)) {
//                LOGGER.debug("read idle from {}", ctx.channel());
//            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
//                LOGGER.debug("before write heart beat to {}", ctx.channel());
//                ctx.channel().writeAndFlush(HEART_BEAT).sync();  //发送心跳成功
//                LOGGER.debug("after write heart beat to {}", ctx.channel());
//            } else if (event.state().equals(IdleState.ALL_IDLE)) {
//                LOGGER.debug("all idle on {}", ctx.channel());
//            }
//        }
//        super.userEventTriggered(ctx, evt);
//    }
}
