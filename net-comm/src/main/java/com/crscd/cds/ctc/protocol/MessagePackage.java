package com.crscd.cds.ctc.protocol;

/**
 * @author zhaole
 * @date 2022-04-08
 */
public class MessagePackage<T> {
    protected MessageHeader header;
    protected T data;

    public MessageHeader getHeader() {
        return header;
    }

    public void setHeader(MessageHeader header) {
        this.header = header;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
