package com.crscd.cds.ctc.protocol;

import com.crscd.cds.ctc.forward.FilterRegister;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhaole
 * @date 2022-04-18
 */
public class RegisterMessage {
    private static final AtomicLong REQUEST_CODE = new AtomicLong(1);
    private short operationCode;
    private long requestCode;
    private short result;
    private FilterRegister register;

    public static RegisterMessage create(FilterRegister register) {
        RegisterMessage message = new RegisterMessage();
        message.setOperationCode(DataType.REGISTER_OPERATION_CODE_REGISTER);
        message.setRequestCode(REQUEST_CODE.incrementAndGet());
        message.setResult(DataType.REGISTER_OPERATION_RESULT_SUCCESS);
        message.setRegister(register);

        return message;
    }

    public short getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(short operationCode) {
        this.operationCode = operationCode;
    }

    public long getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(long requestCode) {
        this.requestCode = requestCode;
    }

    public short getResult() {
        return result;
    }

    public void setResult(short result) {
        this.result = result;
    }

    public FilterRegister getRegister() {
        return register;
    }

    public void setRegister(FilterRegister register) {
        this.register = register;
    }

    public byte[] encode() {
        String xml = register.toXMLString();
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeByte(operationCode);
        buffer.writeIntLE((int) requestCode);
        buffer.writeByte(DataType.REGISTER_OPERATION_RESULT_SUCCESS);
        buffer.writeShortLE(xml.length());
        buffer.writeCharSequence(xml, Charset.forName("UTF-8"));

        byte[] data = new byte[buffer.readableBytes()];
        buffer.getBytes(0, data);

        return data;
    }
}
