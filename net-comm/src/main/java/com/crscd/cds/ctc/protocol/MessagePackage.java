package com.crscd.cds.ctc.protocol;

/**
 * @author zhaole
 * @date 2022-04-02
 */
public class MessagePackage {
    private Long version;
    private Long length;
    private Short type;
    private Long seq;

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
}