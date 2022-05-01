package com.crscd.cds.spring.netcomm.core;

import com.crscd.cds.spring.netcomm.converter.MessageConverter;
import com.crscd.cds.spring.netcomm.message.MessageContent;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.Executor;

/**
 * 抽象 Amazon SQS 监听容器创建工厂类
 * @param <C> 监听容器
 *
 * @author zhaole
 * @date 2022-04-24
 */
public abstract class AbstractNetCommListenerContainerFactory<C extends AbstractMessageListenerContainer>
        implements NetCommListenerContainerFactory<C>, InitializingBean {

    private NetCommDispatcher dispatcher;
    private MessageConverter messageConverter;

    private Executor taskExecutor;

    public void setDispatcher(NetCommDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public NetCommDispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    public C createListenerContainer(short type, short func, Class<? extends MessageContent> parameterType, MessageListener messageListener) {
        C instance = createContainerInstance();

        instance.setType(type);
        instance.setFunc(func);
        instance.setMessageListener(messageListener);
        instance.setParameterType(parameterType);
        instance.setMessageConverter(messageConverter);
        instance.setExecutor(taskExecutor);
        initializeContainer(instance);

        return instance;
    }

    @Override
    public void afterPropertiesSet(){

    }

    protected abstract C createContainerInstance();

    protected void initializeContainer(C instance) {
    }

}
