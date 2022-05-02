package com.crscd.cds.ctc.controller;

/**
 * @author zhaole
 * @date 2022-04-28
 */
public interface InboundDispatcher {
    void dispatch(short type, short func, byte[] data) throws Exception;
}
