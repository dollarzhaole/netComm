package com.crscd.cds.ctc.protocol;

/**
 * @author zhaole
 * @date 2022-04-14
 */
public class DataType {
    public static final short REGISTER = 0x01;
    public static final short FORWARD = 0x02;

    public static final short MSGPACK_FORWARD_TYPE_BY_REG_NO_LOCAL_DISPATCH = 0X11;
    public static final short MSGPACK_FORWARD_TYPE_BY_DEST_NO_LOCAL_DISPATCH = 0X12;
    public static final short MSGPACK_FORWARD_TYPE_BY_REG_LOCAL_DISPATCH = 0X21;
    public static final short MSGPACK_FORWARD_TYPE_BY_DEST_LOCAL_DISPATCH = 0X22;

    public static final short PROP_VALUE_TYPE_BYTE = 0x01;
    public static final short PROP_VALUE_TYPE_SHORT = 0x02;
    public static final short PROP_VALUE_TYPE_INT = 0x03;
    public static final short PROP_VALUE_TYPE_STRING = 0x04;
    public static final short PROP_VALUE_TYPE_FIX_STRING = 0x05;
    public static final short PROP_VALUE_TYPE_VAR_STRING = 0x06;
    public static final short PROP_VALUE_TYPE_TDCS_TIME = 0x07;
    public static final short PROP_VALUE_TYPE_TIME_T = 0x08;

    public static final short PROTOCOL_TYPE_418 = 0x01;

    public static final short REGISTER_OPERATION_CODE_REGISTER = 0x01;
    public static final short REGISTER_OPERATION_CODE_CLEAR = 0x02;
    public static final short REGISTER_OPERATION_CODE_REGISTER_RETURN = 0x03;

    public static final short REGISTER_OPERATION_RESULT_FAIL = 0x00;
    public static final short REGISTER_OPERATION_RESULT_SUCCESS = 0x01;
}
