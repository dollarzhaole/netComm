package com.crscd.cds.spring.netcomm.config;

import com.crscd.cds.ctc.client.DoubleClient;

/**
 * @author zhaole
 * @date 2022-04-24
 */
public class NetCommTemplate {
    private final DoubleClient client;

    public NetCommTemplate(DoubleClient client) {
        this.client = client;
//        client.start();
    }

    public void sendData(byte[] data) {
        client.send(data);
    }

    public void sendData(byte[] data, short type, short func) {
        client.send(data, type, func);
    }

    public byte[] receive() {
        return new byte[0];
    }
}
