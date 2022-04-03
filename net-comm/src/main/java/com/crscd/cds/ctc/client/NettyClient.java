package com.crscd.cds.ctc.client;


import java.util.concurrent.TimeUnit;

import com.crscd.cds.ctc.codec.PackageDecoder;
import com.crscd.cds.ctc.codec.PackageEncoder;
import com.crscd.cds.ctc.protocol.NegotiationRequestPackage;
import com.crscd.cds.ctc.protocol.PackageType;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
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
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author zhaole
 * @date 2022-03-26
 */
public class NettyClient {
    private String host;
    private int port;
    private Channel channel;
    private Bootstrap b = null;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        init();
    }

    private void init() {
        b = new Bootstrap();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        b.group(workerGroup).option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
//                        pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE,
//                                Unpooled.copiedBuffer(System.getProperty("line.separator").getBytes())));
                        pipeline.addLast("decoder", new PackageDecoder());
                        pipeline.addLast("handler", new NettyClientHandler(NettyClient.this));
                        //字符串编码解码
                        pipeline.addLast("encoder", new PackageEncoder());
                        //心跳检测
//                        pipeline.addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                        //客户端的逻辑

                    }
                });
    }

    public void start() {
        ChannelFuture f = b.connect(host, port);
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
                    System.out.println("connected" + channel);
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

        NegotiationRequestPackage pkt = new NegotiationRequestPackage();
        pkt.setClientId(0x11);
        pkt.setLength(7L);
        pkt.setVersion(1L);
        pkt.setSeq(0L);
        pkt.setType(PackageType.NEGOTIATION_REQUEST);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        nettyClient.getChannel().writeAndFlush(Unpooled.buffer(10)).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println("send " + channelFuture.isSuccess());
            }
        });
    }
}
