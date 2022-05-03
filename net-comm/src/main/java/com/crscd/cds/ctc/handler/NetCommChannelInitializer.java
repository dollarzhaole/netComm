package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.client.NettyClient;
import com.crscd.cds.ctc.codec.MessageDecoder;
import com.crscd.cds.ctc.codec.NegotiationRequestEncoder;
import com.crscd.cds.ctc.codec.MessageEncoder;
import com.crscd.cds.ctc.controller.DoubleNetController;
import com.crscd.cds.ctc.enums.ClientFlagEnum;
import com.crscd.cds.ctc.forward.FilterRegister;
import com.crscd.cds.ctc.controller.FlowController;
import com.crscd.cds.ctc.controller.InboundDispatcher;
import com.crscd.cds.ctc.protocol.NetAddress;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.ByteOrder;

/**
 * @author zhaole
 * @date 2022-04-21
 */
public class NetCommChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final NettyClient client;
    private final FlowController flowController;
    private final NetAddress localAddress;
    private final FilterRegister register;
    private final InboundDispatcher inboundDispatcher;
    private final ClientFlagEnum netFlag;
    private final DoubleNetController doubleNetController;

    private static final int MAX_PACKAGE_LENGTH = Integer.MAX_VALUE;
    private static final int LENGTH_FIELD_OFFSET = 4;
    private static final int LENGTH_FIELD_LENGTH = 4;
    private static final int LENGTH_ADJUSTMENT = 5;
    private static final int INITIAL_BYTES_TO_STRIP = 0;

    public NetCommChannelInitializer(NettyClient client, FlowController flowController, NetAddress localAddress, FilterRegister register, InboundDispatcher inboundDispatcher, ClientFlagEnum netFlag, DoubleNetController doubleNetController) {
        this.client = client;
        this.flowController = flowController;
        this.localAddress = localAddress;
        this.register = register;
        this.inboundDispatcher = inboundDispatcher;
        this.netFlag = netFlag;
        this.doubleNetController = doubleNetController;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("idleState", new IdleStateHandler(7, 2, 2));
        pipeline.addLast("hb", new HeartBeatHandler());
        pipeline.addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, MAX_PACKAGE_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP, true));
        pipeline.addLast("PackageChannelInboundHandler", new PackageChannelInboundHandler(flowController, register, doubleNetController, localAddress));
        pipeline.addLast("DoubleNetSeqInboundHandler", new DoubleNetSeqInboundHandler(netFlag, doubleNetController));
        pipeline.addLast("ForwardInboundHandler", new ForwardInboundHandler());
        pipeline.addLast("decoder", new MessageDecoder(inboundDispatcher));
        pipeline.addLast("encoder", new NegotiationRequestEncoder());
        pipeline.addLast("handler", new ClientEventHandler(client, flowController, doubleNetController, netFlag, localAddress));
        pipeline.addLast("DoubleNetSeqOutBoundHandler", new PackageHeadOutBoundHandler(flowController));
        pipeline.addLast("RegisterMessageEncoder", new MessageEncoder());
        pipeline.addLast("ExceptionHandler", new ExceptionHandler());
    }
}
