package com.crscd.cds.ctc.protocol;

import com.crscd.cds.ctc.utils.HexUtils;

import java.util.Collection;

/**
 * @author zhaole
 * @date 2022-04-16
 */
public class ApplicationData {
    private short type;
    private short func;
    private byte[] data;
    private Collection<NetAddress> destAddresses;

    public static ApplicationData create(
            short type, short func, byte[] data, Collection<NetAddress> dest) {
        ApplicationData applicationData = new ApplicationData();
        applicationData.type = type;
        applicationData.func = func;
        applicationData.data = data;
        applicationData.destAddresses = dest;

        return applicationData;
    }

    public Collection<NetAddress> getDestAddresses() {
        return destAddresses;
    }

    public short getType() {
        return type;
    }

    public short getFunc() {
        return func;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("0x%02X", type)
                + "-"
                + String.format("0x%02X", func)
                + ": "
                + HexUtils.bytesToHex(data);
    }
}
