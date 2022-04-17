package com.crscd.cds.ctc.codec;

import com.crscd.cds.ctc.protocol.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhaole
 * @date 2022-04-02
 */
public class ApplicationDataEncoder extends MessageToByteEncoder<ApplicationData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDataEncoder.class);
    private static final int OFFSET_LENGTH = 4;
    public static final int VERSION = 0x01;
    private final AtomicLong packageSeq = new AtomicLong(1);
    private final AtomicLong doubleNetSeq = new AtomicLong(1);
    private final NetAddress localAddress;

    public ApplicationDataEncoder(NetAddress localAddress) {
        this.localAddress = localAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ApplicationData msg, ByteBuf out) throws Exception {
        LOGGER.debug("ApplicationDataEncoder, msg={}", msg);

        // 设置包头
        out.writeIntLE(VERSION);
        out.writeIntLE(0);
        out.writeByte(PackageType.DATA);
        out.writeIntLE((int) packageSeq.getAndIncrement());

        // 设置双网层
        long seq = doubleNetSeq.getAndIncrement();
        out.writeIntLE((int) seq);
        out.writeIntLE((int) (seq >> (8 * 4)));

        // 设置消息头
        out.writeByte(DataType.FORWARD);
        out.writeByte(DataType.PROTOCOL_TYPE_418);
        // 设置长度
        out.writeShortLE(0);
        int forwardLen = 0;

        // 设置转发
        out.writeByte(DataType.MSGPACK_FORWARD_TYPE_BY_REG_LOCAL_DISPATCH);
        forwardLen += 1;

        out.writeByte(msg.getType());
        forwardLen += 1;

        out.writeByte(msg.getFunc());
        forwardLen += 1;

        forwardLen += encodeAddress(localAddress, out);

        out.writeShortLE(msg.getDestAddresses() == null ? 0 : msg.getDestAddresses().size());
        forwardLen += 2;

        if (msg.getDestAddresses() != null) {
            for (NetAddress destAddress : msg.getDestAddresses()) {
                forwardLen += encodeAddress(destAddress, out);
            }
        }

        // 设置属性个数，忽略
        out.writeByte(0);
        forwardLen += 1;

        // 设置转发层长度
        out.setShortLE(out.writerIndex() - forwardLen - 2, forwardLen);

        // 设置数据长度
        out.writeIntLE(msg.getData().length);

        // 设置数据
        out.writeBytes(msg.getData());

        // 设置包长
        out.setIntLE(out.readerIndex() + OFFSET_LENGTH, out.readableBytes() - PackageHeader.HEADER_LENGTH);
    }

    private static int encodeAddress(NetAddress address, ByteBuf byteBuf) {
        if (address == null) {
            LOGGER.error("address is null, cannot encode");
            return 0;
        }

        byteBuf.writeByte(address.getBureauType());
        byteBuf.writeByte(address.getBureauCode());
        byteBuf.writeByte(address.getSysType());
        byteBuf.writeByte(address.getSysId());

        byteBuf.writeByte(address.getUnitType());
        byteBuf.writeShortLE(address.getUnitId());

        byteBuf.writeByte(address.getDevType());
        byteBuf.writeShortLE(address.getDevId());

        byteBuf.writeByte(address.getProcType());
        byteBuf.writeShortLE(address.getProcId());

        byteBuf.writeByte(address.getUserType());
        byteBuf.writeIntLE((int) address.getUserId());

        return 4 + 3 + 3 + 3 + 5;
    }
}
