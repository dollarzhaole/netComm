package com.crscd.cds.ctc.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author zhaole
 * @date 2022-04-29
 */
class FlowControllerTest {

    @Test
    void onReceiveAck() {
        FlowController flowController = new FlowController(2, 5);
        flowController.updateSend();
        for (int i = 0; i < 5; i++) {
            flowController.getAndIncrementSendSequence();
            flowController.updateSend();
            flowController.waitAckIfNecessary();
        }
        flowController.onReceiveAck(2);

        assertEquals(6, flowController.getSendSequence());
        assertEquals(4, flowController.getSendCount());

        flowController.onReceiveAck(3);
        assertEquals(6, flowController.getSendSequence());
        assertEquals(3, flowController.getSendCount());
    }
}