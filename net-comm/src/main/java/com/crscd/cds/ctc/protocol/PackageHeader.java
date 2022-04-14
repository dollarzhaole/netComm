package com.crscd.cds.ctc.protocol;

/**
 * @author zhaole
 * @date 2022-04-02
 */
public class PackageHeader {
    protected Long version;
    protected Long length;
    protected Short type;
    protected Long seq;

    public static PackageHeader createHeartBeat(int version) {
        PackageHeader header = new PackageHeader();
        header.version = (long) version;
        header.type = PackageType.HEART_BEAT;
        return header;
    }

    public static int HEADER_LENGTH = 13;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    @Override
    public String toString() {
        return "MessagePackage{" +
                "version=" + version +
                ", length=" + length +
                ", type=" + type +
                ", seq=" + seq +
                '}';
    }
}
