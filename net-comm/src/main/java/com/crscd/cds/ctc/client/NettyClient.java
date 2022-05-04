package com.crscd.cds.ctc.client;


import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.crscd.cds.ctc.controller.DoubleNetController;
import com.crscd.cds.ctc.protocol.DoubleNetSequence;
import com.crscd.cds.ctc.enums.ClientFlagEnum;
import com.crscd.cds.ctc.forward.FilterRegister;
import com.crscd.cds.ctc.controller.FlowController;
import com.crscd.cds.ctc.controller.InboundDispatcher;
import com.crscd.cds.ctc.handler.*;
import com.crscd.cds.ctc.protocol.ApplicationData;
import com.crscd.cds.ctc.protocol.DoubleNetPackage;
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
    private final String host;
    private final int port;
    private final int localPort;
    private final NetAddress localAddress;
    private final FilterRegister register;
    private final InboundDispatcher inboundDispatcher;
    private final ClientFlagEnum clientFlag;
    private DoubleNetController doubleNetController;
    private Channel channel;
    private Bootstrap bootstrap = null;

    private final FlowController flowController = new FlowController(2, 5);

    public NettyClient(String host, int port, Integer localPort, NetAddress localAddress, FilterRegister register, InboundDispatcher dispatcher, ClientFlagEnum clientFlag) {
        this.host = host;
        this.port = port;
        this.localAddress = localAddress;
        this.register = register;
        this.inboundDispatcher = dispatcher;
        this.clientFlag = clientFlag;
        this.localPort = localPort == null ? 0 : localPort;
    }

    public NettyClient(String host, int port, NetAddress localAddress, FilterRegister register, InboundDispatcher dispatcher, ClientFlagEnum clientFlag) {
        this.host = host;
        this.port = port;
        this.localAddress = localAddress;
        this.register = register;
        this.inboundDispatcher = dispatcher;
        this.clientFlag = clientFlag;
        this.localPort = 0;
    }

    public void setDoubleNetController(DoubleNetController doubleNetController) {
        if (doubleNetController == null) {
            doubleNetController = new DoubleNetController();
        }

        this.doubleNetController = doubleNetController;
    }

    public void init() {
        bootstrap = new Bootstrap();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new NetCommChannelInitializer(this, flowController, localAddress, register, inboundDispatcher, clientFlag, doubleNetController));
    }

    public void start() {
        if (localPort != 0) {
            bootstrap.localAddress(new InetSocketAddress( localPort));
        }
        ChannelFuture f = bootstrap.connect(host, port);
        //断线重连
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    LOGGER.info(">>>>>>>>>> {} can not connect server: {}:{}. reason:{}", clientFlag, host, port, channelFuture.cause());

                    final EventLoop loop = channelFuture.channel().eventLoop();
                    loop.schedule(new Runnable() {
                        @Override
                        public void run() {
                            start();
                        }
                    }, 1L, TimeUnit.SECONDS);
                } else {
                    channel = channelFuture.channel();
                    flowController.setChannel(channel);
                }
            }
        });
    }

    public void sendData(final byte[] data, final short type, final short func, DoubleNetSequence dnSeq) {
        if (getChannel() == null) {
            LOGGER.debug("not connected, send fail {}", channel);
            return;
        }

        if (!getChannel().isActive()) {
            LOGGER.debug("channel {} is inactive, send fail", channel);
            return;
        }

        MessageHead msg = MessageHead.createApplicationData(type, func, data, localAddress);
        DoubleNetPackage pkt = DoubleNetPackage.create(dnSeq, msg);
        getChannel().writeAndFlush(pkt).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                LOGGER.debug("send data {}-{}: {} {}", type, func, data, channelFuture.isSuccess());
            }
        });
    }

    public void sendData(byte[] data, DoubleNetSequence dnSeq) {
        if (getChannel() == null) {
            LOGGER.debug("not connected, send failed {}", channel);
            return;
        }

        if (!getChannel().isActive()) {
            LOGGER.warn("{} is inactive, send failed", getChannel());
            return;
        }

        MessageHead msg = MessageHead.createApplicationData(data, localAddress);
        final DoubleNetPackage pkt = DoubleNetPackage.create(dnSeq, msg);
        getChannel().writeAndFlush(pkt).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                LOGGER.debug("sendData {}: {}", pkt, channelFuture.isSuccess());
            }
        });
    }

    public void close() {
        if (channel != null) {
            channel.close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) {
                    LOGGER.info("close manually to {} and result is {}", channel, channelFuture.isSuccess());
                }
            });
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

        ArrayList<FilterRegister.TypeFunc> funcs = new ArrayList<FilterRegister.TypeFunc>();
        funcs.add(FilterRegister.TypeFunc.create((short) 0xFF, (short) 0xFF));

        FilterRegister.ClientAddress address = FilterRegister.ClientAddress.create(0x01, 0x02, 0x01);
        FilterRegister register = FilterRegister.create(funcs, address);

        DoubleNetController doubleNetController = new DoubleNetController();

        NettyClient nettyClient = new NettyClient("10.2.54.251", 8001, localAddress, register, null, ClientFlagEnum.NET1);
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

    public boolean isActive() {
        if (channel == null) {
            return false;
        }

        return channel.isActive();
    }
}
