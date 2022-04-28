package com.crscd.cds.spring.netcomm.core;

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

    private Executor taskExecutor;

    private Integer prefetchCount;

    public void setDispatcher(NetCommDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setPrefetchCount(Integer prefetchCount) {
        this.prefetchCount = prefetchCount;
    }

    public NetCommDispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    public C createListenerContainer(short type, short func, MessageListener messageListener) {
        C instance = createContainerInstance();

        instance.setType(type);
        instance.setFunc(func);
        instance.setMessageListener(messageListener);
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
