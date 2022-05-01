package com.crscd.cds.netcomm.sample;

import com.crscd.cds.spring.netcomm.message.MessageContent;

/**
 * @author zhaole
 * @date 2022-05-01
 */
public class TestMessage extends MessageContent {
    private Short type;
    private Short func;
    private Integer value1;
    private Integer value2;
    private Long value3;

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

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }

    public long getValue3() {
        return value3;
    }

    public void setValue3(long value3) {
        this.value3 = value3;
    }
}
