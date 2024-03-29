package com.crscd.cds.ctc.client;

import com.crscd.cds.ctc.controller.DoubleNetController;
import com.crscd.cds.ctc.protocol.DoubleNetSequence;
import com.crscd.cds.ctc.protocol.NetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @author zhaole
 * @date 2022-04-22
 */
public class DoubleClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleClient.class);
    private final DoubleNetController doubleNetController = new DoubleNetController();
    private final NetCommClient client1;
    private final NetCommClient client2;

    public DoubleClient(NetCommClient client1, NetCommClient client2) {
        this.client1 = client1;
        this.client2 = client2;

        if (this.client1 != null) {
            this.client1.setDoubleNetController(doubleNetController);
        }

        if (this.client2 != null) {
            this.client2.setDoubleNetController(doubleNetController);
        }
    }

    public void start() {
        if (client1 == null && client2 == null) {
            LOGGER.warn("client1 and client2 are both null, cannot start");
            return;
        }

        if (client1 != null) {
            client1.init();
            client1.start();
        }

        if (client2 != null) {
            client2.init();
            client2.start();
        }
    }

    public void send(byte[] data) {
        send(data, null);
    }

    public void send(byte[] data, Collection<NetAddress> destAddresses) {
        if ((client1 == null || !client1.isActive()) && (client2 == null || !client2.isActive())) {
            return;
        }

        DoubleNetSequence dnSeq = doubleNetController.getSendSequence();

        if (client1 != null && client1.isActive()) {
            client1.sendData(data, dnSeq, destAddresses);
        }

        if (client2 != null && client2.isActive()) {
            client2.sendData(data, dnSeq, destAddresses);
        }
    }

    public void send(byte[] data, short type, short func, Collection<NetAddress> destAddresses) {
        if ((client1 == null || !client1.isActive()) && (client2 == null || !client2.isActive())) {
            return;
        }

        DoubleNetSequence dnSeq = doubleNetController.getSendSequence();

        if (client1 != null && client1.isActive()) {
            client1.sendData(data, type, func, dnSeq, destAddresses);
        }

        if (client2 != null && client2.isActive()) {
            client2.sendData(data, type, func, dnSeq, destAddresses);
        }
    }

    public void send(byte[] data, short type, short func) {
        send(data, type, func, null);
    }

    public void close() {
        if (client1 != null) {
            client1.close();
        }

        if (client2 != null) {
            client2.close();
        }
    }
}
