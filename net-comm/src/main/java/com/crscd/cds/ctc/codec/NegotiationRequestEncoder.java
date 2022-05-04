package com.crscd.cds.ctc.codec;

import com.crscd.cds.ctc.controller.FlowController;
import com.crscd.cds.ctc.protocol.NegotiationRequestMessage;
import com.crscd.cds.ctc.protocol.PackageDefine;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-04-02
 */
public class NegotiationRequestEncoder extends MessageToByteEncoder<NegotiationRequestMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NegotiationRequestEncoder.class);
    private static final int OFFSET_LENGTH = 4;
    private final FlowController flowController;

    public NegotiationRequestEncoder(FlowController flowController) {
        this.flowController = flowController;
    }

    @Override
    protected void encode(
            ChannelHandlerContext channelHandlerContext, NegotiationRequestMessage msg, ByteBuf out)
            throws Exception {
        LOGGER.debug("PackageEncoder, msg={}", msg);

        out.writeIntLE(PackageDefine.CURRENT_VERSION);
        out.writeIntLE(0);
        out.writeByte(PackageDefine.NEGOTIATION_REQUEST);
        out.writeIntLE((int) flowController.getAndIncrementSendSequence());

        out.writeShortLE(msg.getClientId());

        out.setIntLE(
                out.readerIndex() + OFFSET_LENGTH,
                out.readableBytes() - PackageDefine.HEART_BEAT_LENGTH);
    }
}
