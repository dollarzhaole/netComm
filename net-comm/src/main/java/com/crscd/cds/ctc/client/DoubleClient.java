package com.crscd.cds.ctc.client;

import com.crscd.cds.ctc.filter.DoubleNetSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-04-22
 */
public class DoubleClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleClient.class);
    private final NettyClient client1;
    private final NettyClient client2;
    private DoubleNetSequence client1RecSequence;
    private DoubleNetSequence client2RecSequence;

    public DoubleClient(NettyClient client1, NettyClient client2) {
        this.client1 = client1;
        this.client2 = client2;
    }

    public void start() {
        if (client1 == null && client2 == null) {
            LOGGER.warn("client1 and client2 are both null, cannot start");
            return;
        }

        if (client1 != null) {
            client1.start();
        }

        if (client2 != null) {
            client2.start();
        }
    }

    public void send(byte[] data) {
        if (client1 != null && client1.isActive()) {
            client1.sendData(data);
        }

        if (client2 != null && client2.isActive()) {
            client2.sendData(data);
        }
    }

    public void send(byte[] data, short type, short func) {
        if (client1 != null && client1.isActive()) {
            client1.sendData(data, type, func);
        }

        if (client2 != null && client2.isActive()) {
            client2.sendData(data, type, func);
        }
    }
}
