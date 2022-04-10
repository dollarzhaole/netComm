package com.crscd.cds.ctc.protocol;

/**
 * @author zhaole
 * @date 2022-04-02
 */
public class PackageType {
    public static final short NEGOTIATION_REQUEST = 0x01;
    public static final short NEGOTIATION_RESPONSE = 0x02;
    public static final short HEART_BEAT = 0x03;
    public static final short DATA = 0x04;
    public static final short ACK_CONFIRM = 0x05;
}
