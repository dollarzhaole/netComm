package com.crscd.cds.spring.netcomm.core;

/**
 * Amazon SQS 消息监听容器创建工厂
 *
 * @author zhaole
 * @date 2022-04-24
 */
public class SimpleNetCommListenerContainerFactory
        extends AbstractNetCommListenerContainerFactory<SimpleMessageListenerContainer> {
    @Override
    public SimpleMessageListenerContainer createContainerInstance() {
        return new SimpleMessageListenerContainer();
    }

    @Override
    protected void initializeContainer(SimpleMessageListenerContainer instance) {
        super.initializeContainer(instance);
    }
}
