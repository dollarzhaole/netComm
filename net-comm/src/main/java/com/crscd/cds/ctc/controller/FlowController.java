package com.crscd.cds.ctc.controller;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author zhaole
 * @date 2022-04-23
 */
public class FlowController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowController.class);
    private static final int MAX_SEND_WINDOW_SIZE = 255;
    private static final int MAX_COMM_WIN_SIZE = 5;
    private static final int MAX_WAITING_FOR_ACK_QUEUE_LENGTH = 50;
    private Channel channel;

    private final int ackOverTimeInterval;

    // 发送窗口，保存的是发送
    private final long[] sendWindow = new long[MAX_SEND_WINDOW_SIZE];
    private final long[] recWindow = new long[MAX_SEND_WINDOW_SIZE];

    private final int windowSize;

    private int recCount = 0;
    private long recSequence = 0;

    private int sendCount = 0;
    private long sendSequence = 1;

    private boolean isNeedReceiveAck = false;

    private final ConcurrentLinkedQueue<WaitingAckCache> waitingAckCaches = new ConcurrentLinkedQueue<WaitingAckCache>();

    public FlowController(int ackOverTimeInterval, int windowSize) {
        this.ackOverTimeInterval = ackOverTimeInterval;
        this.windowSize = windowSize;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public synchronized void onInactive() {
        LOGGER.info("on inactive, clear waiting ack cache, reset recCount, recSequence, sendCount to 0 and sendSequence to 1");

        waitingAckCaches.clear();
        recCount = 0;
        recSequence = 0;
        sendCount = 0;
        sendSequence = 1;
        isNeedReceiveAck = false;
    }

    public synchronized boolean isNeedToAck() {
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

    public void updateReceive(long seq) {
        LOGGER.info("rec package seq: {}", seq);

        if (windowSize > 0 && recCount < MAX_COMM_WIN_SIZE) {
            recWindow[recCount] = seq;
            recCount += 1;
        }

        if (recSequence + 1 != seq) {
            LOGGER.warn("rec package seq={}, but last seq={}", seq, recSequence);
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

        LOGGER.debug("rec {}, sendCount={}, before ack: {}", seq, sendCount, sendWindow);

        int i = sendCount;
        while (i >= 0) {
            if (sendWindow[i] == (int) seq) {
                break;
            }

            if (i > 0) {
                i--;
            } else {
                LOGGER.warn("can not find ack seq: {}", seq);
                break;
            }
        }

        // 移动滑动窗口
        System.arraycopy(sendWindow, i + 1, sendWindow, 0, sendWindow.length - i - 1);
        sendCount = sendCount - i - 1;

        isNeedReceiveAck = false;

        LOGGER.debug("after move, i={}, sendCount={}: {}", i, sendCount, sendWindow);

        int j = windowSize - sendCount;
        while (j > 0 && !waitingAckCaches.isEmpty()) {
            WaitingAckCache cache = waitingAckCaches.poll();
            if (cache.context.channel().isActive()) {
                cache.context.writeAndFlush(cache.data, cache.promise);
                LOGGER.debug("after rec ack, sending cache to {}: {}", cache.context.channel(), cache.data.readableBytes());
            } else {
                waitingAckCaches.clear();
            }

            j--;
        }
    }

    public void updateSend() {
        if (windowSize == 0 || sendCount >= sendWindow.length) {
            return;
        }

        sendWindow[sendCount] = sendSequence;
        sendCount += 1;

        if (sendCount >= windowSize) {
            LOGGER.debug("need receive ack: sendWindow={}", sendWindow);

            isNeedReceiveAck = true;
        }
    }

    public long getAndIncrementSendSequence() {
        long result = sendSequence;
        sendSequence += 1;

        return result;
    }

    public int getSendCount() {
        return sendCount;
    }

    public long getSendSequence() {
        return sendSequence;
    }

    public boolean waitAckIfNecessary() {
        return isNeedReceiveAck;
    }

    public void addCache(WaitingAckCache cache) {
        waitingAckCaches.add(cache);
        LOGGER.debug("add cache, after cache size: {}", waitingAckCaches.size());

        if (waitingAckCaches.size() > MAX_WAITING_FOR_ACK_QUEUE_LENGTH) {
            channel.close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    LOGGER.info("waiting ack cache size is more than {}, so close channel {}",
                            MAX_WAITING_FOR_ACK_QUEUE_LENGTH, channel);
                }
            });
        }
    }

    public static class WaitingAckCache {
        private ChannelHandlerContext context;
        private ByteBuf data;
        private ChannelPromise promise;

        private WaitingAckCache() {
        }

        public static WaitingAckCache create(ChannelHandlerContext context, ByteBuf data, ChannelPromise promise) {
            WaitingAckCache cache = new WaitingAckCache();
            cache.context = context;
            cache.data = data;
            cache.promise = promise;

            return cache;
        }
    }
}
