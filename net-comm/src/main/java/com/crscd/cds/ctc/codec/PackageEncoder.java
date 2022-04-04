package com.crscd.cds.ctc.codec;

import com.crscd.cds.ctc.protocol.MessagePackage;
import com.crscd.cds.ctc.utils.ReflectionUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author zhaole
 * @date 2022-04-02
 */
public class PackageEncoder extends MessageToByteEncoder<MessagePackage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessagePackage msg, ByteBuf out) throws Exception {
        Class<? extends MessagePackage> clazz = msg.getClass();
        List<Field> fields = ReflectionUtils.getAllFieldsList(clazz);

        for (Field field : fields) {
            if (field == null) {
                continue;
            }

            field.setAccessible(true);

            if (Long.class.equals(field.getType())) {
                out.writeIntLE(((Long) field.get(msg)).intValue());
            } else if (Integer.class.equals(field.getType())) {
                out.writeShortLE(((Integer) field.get(msg)));
            } else if (Short.class.equals(field.getType())) {
                out.writeByte((Short) field.get(msg));
            }
        }
    }
}
