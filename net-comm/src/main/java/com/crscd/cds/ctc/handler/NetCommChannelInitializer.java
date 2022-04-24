package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.client.NettyClient;
import com.crscd.cds.ctc.codec.MessageDecoder;
import com.crscd.cds.ctc.codec.HeaderEncoder;
import com.crscd.cds.ctc.codec.MessageEncoder;
import com.crscd.cds.ctc.filter.FilterRegister;
import com.crscd.cds.ctc.flow.FlowController;
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
    private FilterRegister register;

    private static final int MAX_PACKAGE_LENGTH = Integer.MAX_VALUE;
    private static final int LENGTH_FIELD_OFFSET = 4;
    private static final int LENGTH_FIELD_LENGTH = 4;
    private static final int LENGTH_ADJUSTMENT = 5;
    private static final int INITIAL_BYTES_TO_STRIP = 0;

    public NetCommChannelInitializer(NettyClient client, FlowController flowController, NetAddress localAddress, FilterRegister register) {
        this.client = client;
        this.flowController = flowController;
        this.localAddress = localAddress;
        this.register = register;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("idleState", new IdleStateHandler(7, 2, 2));
        pipeline.addLast("hb", new HeartBeatHandler());
        pipeline.addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, MAX_PACKAGE_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP, true));
        pipeline.addLast("PackageChannelInboundHandler", new PackageChannelInboundHandler(flowController, register));
        pipeline.addLast("DoubleNetSeqInboundHandler", new DoubleNetSeqInboundHandler());
        pipeline.addLast("ForwardInboundHandler", new ForwardInboundHandler());
        pipeline.addLast("decoder", new MessageDecoder());
        pipeline.addLast("encoder", new HeaderEncoder());
        pipeline.addLast("handler", new NettyClientHandler(client));
        pipeline.addLast("DoubleNetSeqOutBoundHandler", new DoubleNetSeqOutBoundHandler(flowController));
        pipeline.addLast("RegisterMessageEncoder", new MessageEncoder());
        pipeline.addLast("ExceptionHandler", new ExceptionHandler());
    }
}
