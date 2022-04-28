package com.crscd.cds.spring.netcomm.core;

import org.springframework.context.Lifecycle;

/**
 * 消息监听容器
 * @author zhaole
 * @date 2022-04-24
 */
public interface MessageListenerContainer extends Lifecycle {
    void setupMessageListener(MessageListener messageListener);
}
