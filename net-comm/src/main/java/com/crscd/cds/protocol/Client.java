package com.crscd.cds.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author zhaole
 * @date 2022-03-25
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        final EventLoopGroup businessGroup = new NioEventLoopGroup(8);
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline channelPipeline = nioSocketChannel.pipeline();
                        channelPipeline.addLast(new StringDecoder());
                        channelPipeline.addLast(new StringEncoder());
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect("10.2.54.251", 8001).sync();
        channelFuture.channel().closeFuture().sync();
    }
}
