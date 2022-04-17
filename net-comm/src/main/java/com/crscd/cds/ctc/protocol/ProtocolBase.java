package com.crscd.cds.ctc.protocol;

/**
 * @author zhaole
 * @date 2022-04-15
 */
public class ProtocolBase {
    protected short type;
    protected short func;

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public short getFunc() {
        return func;
    }

    public void setFunc(short func) {
        this.func = func;
    }
}
