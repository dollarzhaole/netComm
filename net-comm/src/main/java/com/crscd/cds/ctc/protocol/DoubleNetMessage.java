package com.crscd.cds.ctc.protocol;

/**
 * @author zhaole
 * @date 2022-04-14
 */
public class DoubleNetMessage extends MessageHead {
    protected long doubleNetSequence;

    public long getDoubleNetSequence() {
        return doubleNetSequence;
    }

    public void setDoubleNetSequence(long doubleNetSequence) {
        this.doubleNetSequence = doubleNetSequence;
    }
}
