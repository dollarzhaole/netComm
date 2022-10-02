package com.crscd.cds.spring.netcomm.config;

import com.crscd.cds.ctc.client.DoubleClient;
import com.crscd.cds.ctc.protocol.NetAddress;
import com.crscd.cds.spring.netcomm.converter.MessageConverter;
import com.crscd.cds.spring.netcomm.exception.NetCommIllegalStateException;

import java.util.Collection;
import java.util.Collections;

/**
 * @author zhaole
 * @date 2022-04-24
 */
public class NetCommTemplate {
    private final DoubleClient client;
    private final MessageConverter messageConverter;

    public NetCommTemplate(DoubleClient client, MessageConverter messageConverter) {
        this.client = client;
        this.messageConverter = messageConverter;
    }

    public void convertAndSend(Object object) throws IllegalAccessException {
        byte[] message = convertMessageIfNecessary(object);

        sendData(message);
    }

    public void convertAndSend(Object object, Collection<NetAddress> destAddresses)
            throws IllegalAccessException {
        byte[] message = convertMessageIfNecessary(object);

        sendData(message, destAddresses);
    }

    public void convertAndSend(Object object, NetAddress destAddress)
            throws IllegalAccessException {
        byte[] message = convertMessageIfNecessary(object);

        sendData(message, Collections.singletonList(destAddress));
    }

    private byte[] convertMessageIfNecessary(Object object) throws IllegalAccessException {
        if (object instanceof byte[]) {
            return (byte[]) object;
        }

        return getRequiredMessageConverter().toMessage(object);
    }

    private MessageConverter getRequiredMessageConverter() throws IllegalStateException {
        MessageConverter converter = messageConverter;
        if (converter == null) {
            throw new NetCommIllegalStateException(
                    "No 'messageConverter' specified. Check configuration of RabbitTemplate.");
        }
        return converter;
    }

    public void sendData(byte[] data) {
        client.send(data);
    }

    public void sendData(byte[] data, short type, short func) {
        client.send(data, type, func);
    }

    public void sendData(byte[] data, Collection<NetAddress> destAddresses) {
        client.send(data, destAddresses);
    }

    public void sendData(
            byte[] data, short type, short func, Collection<NetAddress> destAddresses) {
        client.send(data, type, func, destAddresses);
    }

    public void sendData(byte[] data, NetAddress destAddress) {
        sendData(data, Collections.singletonList(destAddress));
    }

    public void sendData(byte[] data, short type, short func, NetAddress destAddress) {
        sendData(data, type, func, Collections.singletonList(destAddress));
    }
}
