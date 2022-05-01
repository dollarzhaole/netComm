package com.crscd.cds.netcomm.sample;

import com.crscd.cds.spring.netcomm.message.MessageContent;

/**
 * @author zhaole
 * @date 2022-05-01
 */
public class RequestMessageContent extends MessageContent {
    private Short type;
    private Short func;

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
