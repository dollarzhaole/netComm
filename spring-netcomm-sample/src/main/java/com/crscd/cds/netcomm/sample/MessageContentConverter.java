package com.crscd.cds.netcomm.sample;

import com.crscd.cds.ctc.utils.ReflectionUtility;
import com.crscd.cds.spring.netcomm.converter.MessageConverter;
import com.crscd.cds.spring.netcomm.exception.NetCommIllegalStateException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author zhaole
 * @date 2022-05-01
 */
public class MessageContentConverter implements MessageConverter {

    @Nullable
    @Override
    public Object fromMessage(byte[] data, Class<?> targetClass) throws InstantiationException, IllegalAccessException {
        List<Field> fields = ReflectionUtility.getAllFieldsList(targetClass);

        Object object = targetClass.newInstance();

        ByteBuf buf = Unpooled.copiedBuffer(data);

        for (Field field : fields) {
            if (field == null) {
                continue;
            }

            field.setAccessible(true);

            Object value;

            if (Long.class.equals(field.getType())) {
                value = buf.readUnsignedIntLE();
                ReflectionUtils.setField(field, object, value);
            } else if (Integer.class.equals(field.getType())) {
                value = buf.readUnsignedShortLE();
            } else if (Short.class.equals(field.getType())) {
                value = buf.readUnsignedByte();
            } else {
                throw new NetCommIllegalStateException("unrecognized filed type: " + field.getType());
            }

            ReflectionUtils.setField(field, object, value);
        }

        return object;
    }

    @Nullable
    @Override
    public byte[] toMessage(Object object) throws IllegalAccessException {
        List<Field> fields = ReflectionUtility.getAllFieldsList(object.getClass());

        ByteBuf out = Unpooled.buffer();

        for (Field field : fields) {
            if (field == null) {
                continue;
            }

            field.setAccessible(true);

            if (Long.class.equals(field.getType())) {
                out.writeIntLE(((Long) field.get(object)).intValue());
            } else if (Integer.class.equals(field.getType())) {
                out.writeShortLE(((Integer) field.get(object)));
            } else if (Short.class.equals(field.getType())) {
                out.writeByte((Short) field.get(object));
            }
        }

        byte[] bytes = new byte[out.readableBytes()];
        out.readBytes(bytes);
        return bytes;
    }


}
