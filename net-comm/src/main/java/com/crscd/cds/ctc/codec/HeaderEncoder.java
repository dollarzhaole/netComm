package com.crscd.cds.ctc.codec;

import com.crscd.cds.ctc.protocol.MessageHead;
import com.crscd.cds.ctc.protocol.PackageHeader;
import com.crscd.cds.ctc.protocol.Package;
import com.crscd.cds.ctc.utils.ReflectionUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhaole
 * @date 2022-04-02
 */
public class HeaderEncoder extends MessageToByteEncoder<Package<MessageHead>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderEncoder.class);
    public static final int VERSION = 0x01;
    private static final int OFFSET_LENGTH = 4;
    private final AtomicLong packageSeq = new AtomicLong(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Package<MessageHead> msg, ByteBuf out) throws Exception {
        LOGGER.debug("PackageEncoder, msg={}", msg);

        PackageHeader header = msg.getHeader();
        out.writeIntLE(VERSION);
        out.writeIntLE(0);
        out.writeByte(header.getType());
        out.writeIntLE((int) packageSeq.getAndIncrement());

        if (packageSeq.get() > 0xFFFFFFFF) {
            packageSeq.set(0);
        }

        if (msg.getData() != null) {
            Class<?> clazz = msg.getData().getClass();
            Object data = msg.getData();
            List<Field> fields = ReflectionUtils.getAllFieldsList(clazz);

            for (Field field : fields) {
                if (field == null) {
                    continue;
                }

                field.setAccessible(true);

                if (Long.class.equals(field.getType())) {
                    out.writeIntLE(((Long) field.get(data)).intValue());
                } else if (Integer.class.equals(field.getType())) {
                    out.writeShortLE(((Integer) field.get(data)));
                } else if (Short.class.equals(field.getType())) {
                    out.writeByte((Short) field.get(data));
                }
            }
        }

        out.setIntLE(out.readerIndex() + OFFSET_LENGTH, out.readableBytes() - PackageHeader.HEADER_LENGTH);
    }
}
