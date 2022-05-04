package com.crscd.cds.spring.netcomm.core;

import com.crscd.cds.spring.netcomm.message.MessageContent;

/**
 * 消息监听容器工厂
 *
 * @param <C> 消息监听容器
 * @author zhaole
 * @date 2022-04-24
 */
public interface NetCommListenerContainerFactory<C extends MessageListenerContainer> {

    C createListenerContainer(
            short type,
            short func,
            Class<? extends MessageContent> parameterType,
            MessageListener messageListener);
}
