package com.crscd.cds.spring.netcomm.core;

import com.crscd.cds.ctc.protocol.NetAddress;

import java.lang.reflect.InvocationTargetException;

/**
 * 消息监听者
 *
 * @author zhaole
 * @date 2022-04-24
 */
@FunctionalInterface
public interface MessageListener {
    void onMessage(Object content, NetAddress srcAddress)
            throws InvocationTargetException, IllegalAccessException, InstantiationException;
}
