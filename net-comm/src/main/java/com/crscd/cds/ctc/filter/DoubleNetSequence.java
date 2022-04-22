package com.crscd.cds.ctc.filter;

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
}
