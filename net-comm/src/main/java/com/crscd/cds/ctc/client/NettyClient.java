package com.crscd.cds.ctc.client;


import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

import com.crscd.cds.ctc.codec.ApplicationDataDecoder;
import com.crscd.cds.ctc.codec.HeaderEncoder;
import com.crscd.cds.ctc.handler.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author zhaole
 * @date 2022-03-26
 */
public class NettyClient {
    private String host;
    private int port;
    private Channel channel;
    private Bootstrap bootstrap = null;

    private static final int MAX_PACKAGE_LENGTH = Integer.MAX_VALUE;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        init();
    }

    private void init() {
        bootstrap = new Bootstrap();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup).option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("idleState", new IdleStateHandler(2, 2, 2));
                        pipeline.addLast("hb", new HeartBeatHandler());
                        pipeline.addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, MAX_PACKAGE_LENGTH, 4, 4, 5, 0, true));
                        pipeline.addLast("PackageChannelInboundHandler", new PackageChannelInboundHandler());
                        pipeline.addLast("DoubleNetSeqInboundHandler", new DoubleNetSeqInboundHandler());
                        pipeline.addLast("ForwardInboundHandler", new ForwardInboundHandler());
                        pipeline.addLast("decoder", new ApplicationDataDecoder());
                        pipeline.addLast("handler", new NettyClientHandler(NettyClient.this));
                        pipeline.addLast("encoder", new HeaderEncoder());
                    }
                });
    }

    public void start() {
        ChannelFuture f = bootstrap.connect(host, port);
        //断线重连
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    final EventLoop loop = channelFuture.channel().eventLoop();
                    loop.schedule(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("not connect service");
                            start();
                        }
                    }, 1L, TimeUnit.SECONDS);
                } else {
                    channel = channelFuture.channel();
                }
            }
        });
    }

    public Channel getChannel() {
        return channel;
    }

    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient("10.2.54.251", 8001);
        nettyClient.start();
    }
}
