package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.filter.FilterRegister;
import com.crscd.cds.ctc.protocol.MessageHead;
import com.crscd.cds.ctc.protocol.NegotiationResponseMessage;
import com.crscd.cds.ctc.protocol.PackageDefine;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
    private long lastRecSequence = 0;
    private int recCount = 0;
    private int windowSize = 5;

    private static final ByteBuf ACK_BUFFER = Unpooled.buffer(13);
    static {
        ACK_BUFFER.writeIntLE(0x01);
        ACK_BUFFER.writeIntLE(0);
        ACK_BUFFER.writeByte(PackageDefine.ACK_CONFIRM);
    }

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

        if (type == PackageDefine.NEGOTIATION_RESPONSE) {
            doNegotiationResponse(channelHandlerContext, byteBuf);
            sendRegisterRequest(channelHandlerContext);
            return false;
        } else if (type == PackageDefine.HEART_BEAT) {
            doHeartBeat(channelHandlerContext);
            return false;
        } else if (type == PackageDefine.DATA) {
            checkAndSendAckIfNecessary(channelHandlerContext, byteBuf);
        } else if (type == PackageDefine.ACK_CONFIRM) {
            doAckConfirm(channelHandlerContext);
        }

        return true;
    }

    private void checkAndSendAckIfNecessary(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        lastRecSequence = getPackageSequence(byteBuf);
        recCount += 1;

        if (windowSize > 0 && recCount >= windowSize) {
            sendAck(channelHandlerContext, lastRecSequence);
            recCount = 0;
        }
    }

    private void sendAck(final ChannelHandlerContext channelHandlerContext, final long sequence) {
        ByteBuf buf = ACK_BUFFER.copy();
        buf.writeIntLE((int) sequence);

        channelHandlerContext.writeAndFlush(buf).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                LOGGER.debug("send ack seq={} to {}", sequence, channelHandlerContext);
            }
        });
    }

    private long getPackageSequence(ByteBuf buffer) {
        if (buffer.readableBytes() > 13) {
            return buffer.getUnsignedIntLE(9);
        }

        throw new RuntimeException("data's length is less than 13");
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
