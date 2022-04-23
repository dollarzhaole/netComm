package com.crscd.cds.ctc.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-04-23
 */
public class FlowController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowController.class);
    private static final int MAX_SEND_WINDOW_SIZE = 255;
    private static final int MAX_COMM_WIN_SIZE = 5;

    // 发送窗口，保存的是发送
    private final long[] sendWindow = new long[MAX_SEND_WINDOW_SIZE];
    private final long[] recWindow = new long[MAX_SEND_WINDOW_SIZE];

    private final int windowSize = 5;

    private int recCount = 0;
    private int recSequence = 0;

    private int sendCount = 0;
    private long sendSequence = 0;

    public boolean isNeedToAck() {
        if (windowSize == 0) {
            return false;
        }

        if (recCount >= windowSize) {
            return true;
        }

        // todo 如果本轮已经收到过包，超过一定时间了，就发送一个ack
        if (recCount > 0) {
            return true;
        }

        return false;
    }

    public void updateReceive(int seq) {
        if (windowSize > 0 && recCount < MAX_COMM_WIN_SIZE) {
            recWindow[recCount] = seq;
            recCount += 1;
        }

        if (recSequence + 1 != seq) {
            LOGGER.warn("rec seq={}, but last seq={}", seq, recSequence);
        }

        recSequence = seq;
    }

    public void onSendAck() {
        recCount = 0;
    }

    public void onReceiveAck(long seq) {
        if (sendCount <= 0) {
            LOGGER.warn("sendCount<=0, but rec ack of seq {}", seq);
            return;
        }

        int i = sendCount;
        while (i >= 0) {
            if (sendWindow[i] == (int) seq){
                break;
            }

            i--;
        }

        // 移动滑动窗口
        System.arraycopy(sendWindow, i + 1, sendWindow, 0, sendCount - i);
        sendCount = sendCount - i - 1;
    }

    public void updateSend() {
        if (windowSize == 0 || sendCount >= sendWindow.length) {
            return;
        }

        sendWindow[sendCount] = sendSequence;
        sendCount += 1;

        if (sendCount >= windowSize) {
            LOGGER.debug("need receive ack: sendWindow={}", sendWindow);

            // todo 设置信号量，不能再发送应用数据了
        }
    }
}
