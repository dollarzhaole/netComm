package com.crscd.cds.ctc.protocol;

/**
 * @author zhaole
 * @date 2022-04-08
 */
public class Package<T extends MessageHead> {
    protected PackageHeader header;
    protected T data;

    public static <T extends MessageHead> Package<T> create(PackageHeader header, T data) {
        Package<T> pkt = new Package<T>();
        pkt.header = header;
        pkt.data = data;

        return pkt;
    }

    public static Package<MessageHead> createHeartBeat() {
        Package<MessageHead> pkt = new Package<MessageHead>();
        PackageHeader header = new PackageHeader();
        header.version = (long) 0x01;
        header.type = PackageType.HEART_BEAT;
        pkt.setHeader(header);

        return pkt;
    }

    public PackageHeader getHeader() {
        return header;
    }

    public void setHeader(PackageHeader header) {
        this.header = header;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
