package com.crscd.cds.ctc.client;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.crscd.cds.ctc.filter.FilterRegister;
import com.crscd.cds.ctc.handler.*;
import com.crscd.cds.ctc.protocol.ApplicationData;
import com.crscd.cds.ctc.protocol.MessageHead;
import com.crscd.cds.ctc.protocol.NetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
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

    private final NetAddress localAddress;

    public NettyClient(String host, int port, NetAddress localAddress) {
        this.host = host;
        this.port = port;
        this.localAddress = localAddress;

        MessageHead.setSrc(localAddress);

        init();
    }

    private void init() {
        bootstrap = new Bootstrap();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup).option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new NetCommChannelInitializer(this));
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

        MessageHead msg = MessageHead.createApplicationData(data.getType(), data.getFunc(), data.getData());
        getChannel().writeAndFlush(msg).sync();
        LOGGER.debug("channel {} send data {} successfully", channel, data);
    }

    public void sendData(byte[] data) {
        if (getChannel() == null) {
            LOGGER.debug("not connected, send failed {}", channel);
            return;
        }

        if (!getChannel().isActive()) {
            LOGGER.warn("{} is inactive, send failed", getChannel());
            return;
        }

        MessageHead msg = MessageHead.createApplicationData(data);
        getChannel().writeAndFlush(msg);
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
