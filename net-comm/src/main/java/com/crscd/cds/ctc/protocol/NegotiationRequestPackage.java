package com.crscd.cds.ctc.protocol;

/**
 * @author zhaole
 * @date 2022-04-02
 */
public class NegotiationRequestPackage extends MessagePackage{
    private int clientId;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
}
