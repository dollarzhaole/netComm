package com.crscd.cds.spring.netcomm.core;

import com.crscd.cds.spring.netcomm.converter.MessageConverter;
import com.crscd.cds.spring.netcomm.message.MessageContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;

/**
 * 抽象消息监听容器
 *
 * @author zhaole
 * @date 2022-04-24
 */
public abstract class AbstractMessageListenerContainer implements MessageListenerContainer, DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageListenerContainer.class);

    private volatile MessageListener messageListener;
    private short type;
    private short func;
    private Class<? extends MessageContent> parameterType;
    private MessageConverter messageConverter;
    private Executor executor;

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setParameterType(Class<? extends MessageContent> parameterType) {
        this.parameterType = parameterType;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public short getFunc() {
        return func;
    }

    public void setFunc(short func) {
        this.func = func;
    }
    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void invokeListener(byte[] message) throws Exception {
        MessageListener listener = getMessageListener();
        if (executor == null) {
            Object object = messageConverter.fromMessage(message, parameterType);
            listener.onMessage(object);
            return;
        }

        try {
            executor.execute(() -> {
                try {
                    Object object = messageConverter.fromMessage(message, parameterType);
                    listener.onMessage(object);
                } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                    LOGGER.debug("exception happened while onMessage: ", e);
                }
            });

        }
        catch (Exception e) {
            throw wrapToListenerExecutionFailedExceptionIfNeeded(e, message);
        }
    }

    protected Exception wrapToListenerExecutionFailedExceptionIfNeeded(Exception e, byte[] message) {
//        if (!(e instanceof ListenerExecutionFailedException)) {
//            // Wrap exception to ListenerExecutionFailedException.
//            return new ListenerExecutionFailedException("Listener threw exception", e, message);
//        }
        return e;
    }

    @Override
    public void setupMessageListener(MessageListener messageListener) {
        setMessageListener(messageListener);
    }
}
