package com.crscd.cds.spring.netcomm.core;


import com.crscd.cds.ctc.protocol.MessageContent;

import java.lang.reflect.InvocationTargetException;

/**
 * 消息监听者
 * @author zhaole
 * @date 2022-04-24
 */
@FunctionalInterface
public interface MessageListener {
    void onMessage(byte[] message) throws InvocationTargetException, IllegalAccessException;
}
