package com.crscd.cds.ctc.protocol;

/**
 * @author zhaole
 * @date 2022-04-08
 */
public class MessagePackage<T> {
    protected MessageHeader header;
    protected T data;

    public static <T> MessagePackage<T> create(MessageHeader header, T data) {
        MessagePackage<T> pkt = new MessagePackage<T>();
        pkt.header = header;
        pkt.data = data;

        return pkt;
    }

    public static MessagePackage<Object> createHeartBeat() {
        MessagePackage<Object> pkt = new MessagePackage<Object>();
        MessageHeader header = new MessageHeader();
        header.version = (long) 0x01;
        header.type = PackageType.HEART_BEAT;
        pkt.setHeader(header);

        return pkt;
    }

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
