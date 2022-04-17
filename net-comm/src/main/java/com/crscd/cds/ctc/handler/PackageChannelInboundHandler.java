package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.protocol.PackageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-04-12
 */
public class PackageChannelInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackageChannelInboundHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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

        }

        return true;
    }

    private void sendRegisterRequest(ChannelHandlerContext channelHandlerContext) {

    }

    private void doNegotiationResponse(ChannelHandlerContext context, ByteBuf byteBuf) {
        short window = byteBuf.readUnsignedByte();
        short ack = byteBuf.readUnsignedByte();
        short ackOvertime = byteBuf.readUnsignedByte();
        short heartBeat = byteBuf.readUnsignedByte();
        short heartBeatOvertime = byteBuf.readUnsignedByte();

        LOGGER.info("receive negotiation response from {}: window={}, ack={}, ack_overtime={}, heartBeat={}, heart_beat_ot={}",
                context.channel(), window, ack, ackOvertime, heartBeat, heartBeatOvertime);

        // todo 增加动态添加Handler到NettyClient
    }

    private void doHeartBeat(ChannelHandlerContext context) {
        LOGGER.debug("receive heart beat from {}", context.channel());
    }
}
