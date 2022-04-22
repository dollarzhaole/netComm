package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.filter.FilterRegister;
import com.crscd.cds.ctc.protocol.MessageHead;
import com.crscd.cds.ctc.protocol.NegotiationResponseMessage;
import com.crscd.cds.ctc.protocol.PackageType;
import com.crscd.cds.ctc.protocol.RegisterMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author zhaole
 * @date 2022-04-12
 */
public class PackageChannelInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackageChannelInboundHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.debug("rec msg: {}", msg);

        ByteBuf byteBuf = (ByteBuf) msg;
        if (channelRead0(ctx, byteBuf) && byteBuf.isReadable()) {
            super.channelRead(ctx, msg);
        }
    }

    protected boolean channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        byteBuf.readUnsignedIntLE();
        byteBuf.readUnsignedIntLE();
        short type = byteBuf.readUnsignedByte();
        byteBuf.readUnsignedIntLE();

        if (type == PackageType.NEGOTIATION_RESPONSE) {
            doNegotiationResponse(channelHandlerContext, byteBuf);
            sendRegisterRequest(channelHandlerContext);
            return false;
        } else if (type == PackageType.HEART_BEAT) {
            doHeartBeat(channelHandlerContext);
            return false;
        } else if (type == PackageType.DATA) {

        } else if (type == PackageType.ACK_CONFIRM) {
            doAckConfirm(channelHandlerContext);
        }

        return true;
    }

    private void doAckConfirm(ChannelHandlerContext channelHandlerContext) {
        LOGGER.debug("recv ack from {}", channelHandlerContext);
    }

    private void sendRegisterRequest(final ChannelHandlerContext channelHandlerContext) throws InterruptedException {
        ArrayList<FilterRegister.TypeFunc> funcs = new ArrayList<FilterRegister.TypeFunc>();
        funcs.add(FilterRegister.TypeFunc.create((short) 0xFF, (short) 0xFF));

        FilterRegister.ClientAddress address = FilterRegister.ClientAddress.create(0x01, 0x02, 0x01);

        MessageHead msg = MessageHead.createRegisterMessage(FilterRegister.create(funcs, address));

        channelHandlerContext.channel().writeAndFlush(msg).sync();
        LOGGER.info("send register xml to {} successfully", channelHandlerContext);
    }

    private void doNegotiationResponse(ChannelHandlerContext context, ByteBuf byteBuf) {
        NegotiationResponseMessage message = NegotiationResponseMessage.decode(byteBuf);

        LOGGER.info("receive negotiation response from {}: {}", context.channel(), message);

        // todo 增加动态添加Handler到NettyClient
    }

    private void doHeartBeat(ChannelHandlerContext context) {
        LOGGER.debug("receive heart beat from {}", context.channel());
    }
}
