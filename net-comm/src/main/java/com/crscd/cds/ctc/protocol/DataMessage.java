package com.crscd.cds.ctc.protocol;

import java.util.List;

/**
 * @author zhaole
 * @date 2022-04-14
 */
public class DataMessage extends DoubleNetMessage{
    private short dataType;
    private short protocolType;
    private int forwardLength;
    private short forwardType;
    private short type;
    private short func;
    private NetAddress src;
    private List<NetAddress> dest;
    private List<ConditionalProperty> properties;



}
