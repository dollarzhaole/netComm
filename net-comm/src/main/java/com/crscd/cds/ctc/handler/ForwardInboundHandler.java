package com.crscd.cds.ctc.handler;

import com.crscd.cds.ctc.protocol.DataType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * @author zhaole
 * @date 2022-04-14
 */
public class ForwardInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForwardInboundHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;

            short protocolType = byteBuf.readUnsignedByte();
            if (protocolType != DataType.FORWARD) {
                int forwardInfoLen = byteBuf.readUnsignedShortLE();
                return;
            }

            int forwardInfoLen = byteBuf.readUnsignedShortLE();

            short forwardType = byteBuf.readUnsignedByte();
            short dataType = byteBuf.readUnsignedByte();
            short dateFunc = byteBuf.readUnsignedByte();

            // 源地址
            decodeAddress(byteBuf);

            // 解析目的地址
            int destAddressCount = byteBuf.readUnsignedShortLE();
            for (int i = 0; i < destAddressCount; i++) {
                decodeAddress(byteBuf);
            }

            // 解析额外属性
            short propCount = byteBuf.readUnsignedByte();
            for (int i = 0; i < propCount; i++) {
                decodeProp(byteBuf);
            }
        }

        super.channelRead(ctx, msg);
    }

    private void decodeProp(ByteBuf byteBuf) {
        short propNameLen = byteBuf.readUnsignedByte();
        String propName = byteBuf.readCharSequence(propNameLen, Charset.forName("utf-8")).toString();

        short valDataType = byteBuf.readUnsignedByte();
        short propValueLen = byteBuf.readUnsignedByte();

        if (valDataType == DataType.PROP_VALUE_TYPE_STRING) {
            String propValueString = byteBuf.readCharSequence(propValueLen, Charset.forName("utf-8")).toString();
        } else if (valDataType == DataType.PROP_VALUE_TYPE_BYTE) {
            short propValueByte = byteBuf.readUnsignedByte();
        } else if (valDataType == DataType.PROP_VALUE_TYPE_SHORT) {
            int propValueShort = byteBuf.readUnsignedShortLE();
        } else if (valDataType == DataType.PROP_VALUE_TYPE_INT) {
            long propValueInt = byteBuf.readUnsignedIntLE();
        } else {
            LOGGER.warn("msg head prop value type not valid: {}", valDataType);
        }
    }

    private void decodeAddress(ByteBuf byteBuf) {
        short bureauType = byteBuf.readUnsignedByte();
        short bureauCode = byteBuf.readUnsignedByte();
        short sysType = byteBuf.readUnsignedByte();
        short sysId = byteBuf.readUnsignedByte();
        short unitType = byteBuf.readUnsignedByte();
        int unitId = byteBuf.readUnsignedShortLE();
        short devType = byteBuf.readUnsignedByte();
        int devId = byteBuf.readUnsignedShortLE();
        short procType = byteBuf.readUnsignedByte();
        int procId = byteBuf.readUnsignedShortLE();
        short userType = byteBuf.readUnsignedByte();
        long userId = byteBuf.readUnsignedIntLE();
    }
}
