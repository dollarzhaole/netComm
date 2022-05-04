package com.crscd.cds.ctc.protocol;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;

/**
 * @author zhaole
 * @date 2022-04-22
 */
public class DoubleNetSequence {
    private long low;
    private long high;

    public DoubleNetSequence(long low, long high) {
        this.low = low;
        this.high = high;
    }

    public static DoubleNetSequence create(long low, long high) {
        return new DoubleNetSequence(low, high);
    }

    /**
     * 因为连接建立时，双网序号是(0,0)，注册消息是发送的第一包，所以双网序号是(1,0)
     * @return 注册消息的双网序号
     */
    public static DoubleNetSequence createRegisterDoubleNetSequence() {
        return new DoubleNetSequence(1, 0);
    }

    public long getLow() {
        return low;
    }

    public void setLow(long low) {
        this.low = low;
    }

    public long getHigh() {
        return high;
    }

    public void setHigh(long high) {
        this.high = high;
    }

    public boolean isContinuous(long low, long high) {
        return true;
    }

    public void set(long low, long high) {
        this.low = low;
        this.high = high;
    }

    public DoubleNetSequence copy() {
        return new DoubleNetSequence(low, high);
    }

    public DoubleNetSequence getNext() {
        DoubleNetSequence result = new DoubleNetSequence(low, high);
        result.increment();
        return result;
    }

    public void increment() {
        low += 1;
        if ((low & 0xFFFFFFFFL) == 0) {
            low = low & 0xFFFFFFFFL;
            high += 1;
        }

        if ((high & 0xFFFFFFFFL) == 0) {
            high = 0;
        }
    }

    public boolean lessThan(DoubleNetSequence other) {
        if (high != other.high) {
            return high < other.high;
        }

        return low < other.low;
    }

    public boolean moreThan(DoubleNetSequence other) {
        if (high != other.high) {
            return high > other.high;
        }

        return low > other.low;
    }

    public boolean isInit() {
        return low == 0 && high == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleNetSequence that = (DoubleNetSequence) o;
        return low == that.low && high == that.high;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new long[] {low, high});
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", low, high);
    }

    public void set(DoubleNetSequence sequence) {
        low = sequence.low;
        high = sequence.high;
    }

    public boolean isFarFrom(DoubleNetSequence sequence) {
        long absLow = Math.abs(low - sequence.low);
        long absHigh = Math.abs(high - sequence.high);

        if (absHigh != 0) {
            return true;
        }

        return absLow > 50;
    }

    public void init() {
        this.low = 0;
        this.high = 0;
    }

    public void encode(ByteBuf buf) {
        buf.writeIntLE((int) low);
        buf.writeIntLE((int) high);
    }
}
