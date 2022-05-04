package com.crscd.cds.ctc.controller;

import com.crscd.cds.ctc.enums.ClientFlagEnum;
import com.crscd.cds.ctc.protocol.DoubleNetSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaole
 * @date 2022-05-02
 */
public class DoubleNetController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleNetController.class);
    private final DoubleNetSequence lastReceiveSequence1 = new DoubleNetSequence(0, 0);
    private final DoubleNetSequence lastReceiveSequence2 = new DoubleNetSequence(0, 0);
    private final DoubleNetSequence lastReceiveSequence = new DoubleNetSequence(0, 0);
    private final DoubleNetSequence sendSequence = new DoubleNetSequence(2, 0);

    public synchronized void onInactive(ClientFlagEnum clientFlag) {
        if (clientFlag.equals(ClientFlagEnum.NET1)) {
            lastReceiveSequence1.init();
        } else {
            lastReceiveSequence2.init();
        }
    }

    /**
     * 判断双网序号是否成立。有以下几种情况需要考虑 1. 双网正常 2. 只有网络1连接 3. 只启动网络1，网络2断开。网络2连接 4.
     * 只启动网络1，网络2断开。网络2连接，网络1断开，网络1连接
     *
     * @param sequence
     * @param flag
     * @return
     */
    public synchronized ValidResultEnum validate(DoubleNetSequence sequence, ClientFlagEnum flag) {
        DoubleNetSequence preOneClientReceive = getReceiveSequence(flag);

        if (!isContinuous(preOneClientReceive, sequence)) {
            LOGGER.warn(
                    "rec sequence {} of {} is not continuous with pre {}",
                    sequence,
                    flag,
                    preOneClientReceive);
            return ValidResultEnum.CLOSE_ONE;
        }

        // lastReceiveSequence.isInit表示双网初始化后收到的第一包数据，此时直接更新
        if (lastReceiveSequence.isInit()) {
            LOGGER.debug("double net rec first seq {} from {}", sequence, flag);
            lastReceiveSequence1.init();
            lastReceiveSequence2.init();
            setReceiveSequence(flag, sequence);
            return ValidResultEnum.ACCEPT;
        }

        if (lastReceiveSequence.getNext().equals(sequence)) {
            LOGGER.debug("double net rec next {} of {}", sequence, flag);
            setReceiveSequence(flag, sequence);
            return ValidResultEnum.ACCEPT;
        }

        // 收到的重复的包，舍弃
        if (lastReceiveSequence.equals(sequence)) {
            LOGGER.debug(
                    "rec double net {} is equal with {} of net {}, so abandon",
                    sequence,
                    lastReceiveSequence,
                    flag);
            setReceiveSequenceOfOneClient(flag, sequence);
            return ValidResultEnum.ABANDON;
        }

        // 感觉这种情况不应该发生
        if (lastReceiveSequence.getNext().isFarFrom(sequence)) {
            LOGGER.debug(
                    "rec double net {} is far from {} of net {}",
                    sequence,
                    lastReceiveSequence.getNext(),
                    flag);
            return ValidResultEnum.CLOSE_ONE;
        }

        // 收到的包小于当前的包
        if (lastReceiveSequence.getNext().moreThan(sequence)) {
            LOGGER.debug(
                    "rec double net seq {} is less than last {} of net {}",
                    sequence,
                    lastReceiveSequence.getNext(),
                    flag);
            setReceiveSequenceOfOneClient(flag, sequence);
            return ValidResultEnum.ABANDON;
        }

        // 正常情况下，lastReceiveSequence大于等于两个客户端的receiveSequence，如果这个条件不成立，就说明双网序号乱了，重新连接
        if (lastReceiveSequence1.moreThan(lastReceiveSequence)
                || lastReceiveSequence2.moreThan(lastReceiveSequence)) {
            return ValidResultEnum.CLOSE_BOTH;
        }

        if (lastReceiveSequence.getNext().lessThan(sequence)) {
            LOGGER.debug(
                    "rec double net seq {} is more than next {} of net {}",
                    sequence,
                    lastReceiveSequence.getNext(),
                    flag);
            return ValidResultEnum.CLOSE_ONE;
        }

        return ValidResultEnum.ABANDON;
    }

    public synchronized DoubleNetSequence getSendSequence() {
        DoubleNetSequence result = sendSequence.copy();
        sendSequence.increment();
        return result;
    }

    private DoubleNetSequence getReceiveSequence(ClientFlagEnum flag) {
        if (flag.equals(ClientFlagEnum.NET1)) {
            return lastReceiveSequence1;
        }

        return lastReceiveSequence2;
    }

    private void setReceiveSequence(ClientFlagEnum flag, DoubleNetSequence sequence) {
        setReceiveSequenceOfOneClient(flag, sequence);
        lastReceiveSequence.set(sequence);
    }

    private void setReceiveSequenceOfOneClient(ClientFlagEnum flag, DoubleNetSequence sequence) {
        if (flag.equals(ClientFlagEnum.NET1)) {
            lastReceiveSequence1.set(sequence);
        } else {
            lastReceiveSequence2.set(sequence);
        }
    }

    private boolean isContinuous(DoubleNetSequence pre, DoubleNetSequence rec) {
        if (rec.isInit()) {
            LOGGER.debug("rec double net seq is {}", rec);
            return false;
        }

        if (pre.isInit()) {
            LOGGER.debug("pre double net seq is init, so think they are continuous");
            return true;
        }

        return pre.getNext().equals(rec);
    }

    public enum ValidResultEnum {
        ACCEPT,
        ABANDON,
        CLOSE_ONE,
        CLOSE_BOTH,
    }
}
