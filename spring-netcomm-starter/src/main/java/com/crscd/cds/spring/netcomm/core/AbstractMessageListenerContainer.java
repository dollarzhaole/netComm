package com.crscd.cds.spring.netcomm.core;

import com.crscd.cds.ctc.protocol.MessageContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

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

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
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

    public void invokeListener(byte[] message) throws Exception {
        MessageListener listener = getMessageListener();
        try {
            listener.onMessage(message);
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
