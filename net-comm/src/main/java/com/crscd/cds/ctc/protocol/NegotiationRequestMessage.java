package com.crscd.cds.ctc.protocol;

/**
 * @author zhaole
 * @date 2022-04-02
 */
public class NegotiationRequestMessage extends MessageHead {
    private Integer clientId;

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }
}