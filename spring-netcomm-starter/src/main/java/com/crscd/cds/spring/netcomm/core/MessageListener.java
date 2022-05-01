package com.crscd.cds.spring.netcomm.core;


import com.crscd.cds.spring.netcomm.message.MessageContent;

import java.lang.reflect.InvocationTargetException;

/**
 * 消息监听者
 * @author zhaole
 * @date 2022-04-24
 */
@FunctionalInterface
public interface MessageListener {
    void onMessage(Object content) throws InvocationTargetException, IllegalAccessException, InstantiationException;
}
