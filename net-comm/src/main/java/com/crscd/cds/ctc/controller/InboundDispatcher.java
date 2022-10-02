package com.crscd.cds.ctc.controller;

import com.crscd.cds.ctc.protocol.NetAddress;

/**
 * @author zhaole
 * @date 2022-04-28
 */
public interface InboundDispatcher {
    void dispatch(short type, short func, byte[] data, NetAddress srcAddress) throws Exception;
}
