package com.crscd.cds.spring.netcomm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Amazon SQS 消费者容器
 *
 * @author zhaole
 * @date 2022-04-24
 */
public class SimpleMessageListenerContainer extends AbstractMessageListenerContainer {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SimpleMessageListenerContainer.class);

    @Override
    public void destroy() throws Exception {}

    @Override
    public void start() {}

    @Override
    public void stop() {}

    @Override
    public boolean isRunning() {
        return false;
    }
}
