package com.crscd.cds.spring.netcomm.converter;

import javax.annotation.Nullable;

/**
 * @author zhaole
 * @date 2022-05-01
 */
public interface MessageConverter {
    @Nullable
    Object fromMessage(byte[] data, Class<?> targetClass) throws InstantiationException, IllegalAccessException;
    @Nullable
    byte[] toMessage(Object object) throws IllegalAccessException;
}
