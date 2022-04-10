package com.crscd.cds.ctc.codec;

import com.crscd.cds.ctc.protocol.MessageHeader;
import com.crscd.cds.ctc.protocol.PackageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author zhaole
 * @date 2022-04-03
 */
public class PackageDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackageDecoder.class);
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        MessageHeader header = new MessageHeader();

        long version = byteBuf.readUnsignedIntLE();
        long length = byteBuf.readUnsignedIntLE();
        short type = byteBuf.readUnsignedByte();
        long seq = byteBuf.readUnsignedIntLE();

        header.setVersion(version);
        header.setLength(length);
        header.setType(type);
        header.setSeq(seq);

        if (type == PackageType.NEGOTIATION_RESPONSE) {
            doNegotiationResponse(channelHandlerContext, byteBuf);
        } else if (type == PackageType.HEART_BEAT) {
            LOGGER.debug("receive heart beat from {}", channelHandlerContext.channel());
        } else if (type == PackageType.DATA) {

        }
    }

    private void doNegotiationResponse(ChannelHandlerContext context, ByteBuf byteBuf) {
        short window = byteBuf.readUnsignedByte();
        short ack = byteBuf.readUnsignedByte();
        short ackOvertime = byteBuf.readUnsignedByte();
        short heartBeat = byteBuf.readUnsignedByte();
        short heartBeatOvertime = byteBuf.readUnsignedByte();

        LOGGER.info("receive negotiation response from {}: window={}, ack={}, ack_overtime={}, heartBeat={}, heart_beat_ot={}",
                context.channel(), window, ack, ackOvertime, heartBeat, heartBeatOvertime);
    }
}
