package com.crscd.cds.ctc.client;


import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.crscd.cds.ctc.codec.ApplicationDataDecoder;
import com.crscd.cds.ctc.codec.ApplicationDataEncoder;
import com.crscd.cds.ctc.codec.HeaderEncoder;
import com.crscd.cds.ctc.codec.MessageEncoder;
import com.crscd.cds.ctc.filter.FilterRegister;
import com.crscd.cds.ctc.handler.*;
import com.crscd.cds.ctc.protocol.ApplicationData;
import com.crscd.cds.ctc.protocol.NetAddress;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-03-26
 */
public class NettyClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    private String host;
    private int port;
    private Channel channel;
    private Bootstrap bootstrap = null;

    private static final int MAX_PACKAGE_LENGTH = Integer.MAX_VALUE;
    private final NetAddress localAddress;

    public NettyClient(String host, int port, NetAddress localAddress) {
        this.host = host;
        this.port = port;
        this.localAddress = localAddress;
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
                        pipeline.addLast("idleState", new IdleStateHandler(7, 2, 2));
                        pipeline.addLast("hb", new HeartBeatHandler());
                        pipeline.addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, MAX_PACKAGE_LENGTH, 4, 4, 5, 0, true));
                        pipeline.addLast("PackageChannelInboundHandler", new PackageChannelInboundHandler());
                        pipeline.addLast("DoubleNetSeqInboundHandler", new DoubleNetSeqInboundHandler());
                        pipeline.addLast("ForwardInboundHandler", new ForwardInboundHandler());
                        pipeline.addLast("decoder", new ApplicationDataDecoder());
                        pipeline.addLast("encoder", new HeaderEncoder());
                        pipeline.addLast("handler", new NettyClientHandler(NettyClient.this));
                        pipeline.addLast("ApplicationDataEncoder", new ApplicationDataEncoder(localAddress));
                        pipeline.addLast("DoubleNetSeq2OutBoundHandler", new DoubleNetSeq2OutBoundHandler());
                        pipeline.addLast("DoubleNetSeqOutBoundHandler", new DoubleNetSeqOutBoundHandler());
                        pipeline.addLast("RegisterMessageEncoder", new MessageEncoder());
                        pipeline.addLast("ExceptionHandler", new ExceptionHandler());
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

    public void sendData(ApplicationData data) throws InterruptedException {
        if (getChannel() == null) {
            LOGGER.debug("not connected, send fail {}", channel);
            return;
        }

        if (!getChannel().isActive()) {
            LOGGER.debug("channel {} is inactive, send fail", channel);
            return;
        }

        getChannel().writeAndFlush(data).sync();
        LOGGER.debug("channel {} send data {} successfully", channel, data.toString());
    }

    public void sendData(byte[] data) {
        if (getChannel().isActive()) {
            getChannel().writeAndFlush(data);
        } else {
            LOGGER.warn("{} is inactive, send failed", getChannel());
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public static void main(String[] args) throws InterruptedException {
        NetAddress localAddress = new NetAddress();
        localAddress.setBureauType((short) 0x01);
        localAddress.setBureauCode((short) 0x01);
        localAddress.setSysType((short) 0x01);
        localAddress.setSysId((short) 0x01);
        localAddress.setUnitType((short) 0x01);
        localAddress.setUnitId((short) 0x01);
        localAddress.setDevType((short) 0x01);
        localAddress.setDevId((short) 0x01);
        localAddress.setProcType((short) 0x01);
        localAddress.setProcId((short) 129);
        localAddress.setUserType((short) 0x01);
        localAddress.setUserId(0x01);

        NettyClient nettyClient = new NettyClient("10.2.54.251", 8001, localAddress);
        nettyClient.start();

        Thread.sleep(1000);
        ArrayList<FilterRegister.TypeFunc> funcs = new ArrayList<FilterRegister.TypeFunc>();
        funcs.add(FilterRegister.TypeFunc.create((short) 0xFF, (short) 0xFF));
//        RegisterMessage msg = RegisterMessage.create(FilterRegister.create(funcs));
//        LOGGER.debug("before write register message");
//        nettyClient.getChannel().writeAndFlush(msg).sync();
//        LOGGER.debug("after write register message");

        byte[] bytes = new byte[10];
        bytes[0] = 0x01;
        bytes[1] = 0x01;
        bytes[2] = 0x01;
        bytes[3] = 0x01;
        bytes[4] = 0x01;
        bytes[5] = 0x01;
        bytes[6] = 0x01;
        bytes[7] = 0x01;
        bytes[8] = 0x01;
        bytes[9] = 0x01;
        ApplicationData protocolBase = ApplicationData.create((short) 0x01, (short) 0x01, bytes, null);

        while (true) {
            try {
                Thread.sleep(10000);
//                nettyClient.sendData(protocolBase);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
