package com.crscd.cds.ctc.protocol;

import com.crscd.cds.ctc.filter.FilterRegister;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhaole
 * @date 2022-04-18
 */
public class RegisterMessage extends MessageHead {
    private short operationCode;
    private long requestCode;
    private short result;
    private String registerString;

    private static final AtomicLong REQUEST_CODE = new AtomicLong(1);

    public static RegisterMessage create(FilterRegister register) {
        String string = register.toXMLString();

        RegisterMessage message = new RegisterMessage();
        message.setOperationCode(DataType.REGISTER_OPERATION_CODE_REGISTER);
        message.setRequestCode(REQUEST_CODE.incrementAndGet());
        message.setResult(DataType.REGISTER_OPERATION_RESULT_SUCCESS);
        message.setRegisterString(string);
        message.setDataType(DataType.REGISTER);
        message.setProtocolType(DataType.PROTOCOL_TYPE_418);

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

    public String getRegisterString() {
        return registerString;
    }

    public void setRegisterString(String registerString) {
        this.registerString = registerString;
    }

    @Override
    public void encode(ByteBuf buffer) {
        int offset = buffer.readableBytes();

        buffer.writeIntLE(0);
        buffer.writeByte(operationCode);
        buffer.writeIntLE((int) requestCode);
        buffer.writeByte(DataType.REGISTER_OPERATION_RESULT_FAIL);
        buffer.writeShortLE(registerString.length());
        buffer.writeCharSequence(registerString, Charset.forName("UTF-8"));

        buffer.setIntLE(offset, buffer.readableBytes() - (1 + 4 + 1 + 2 + registerString.length()));
    }
}
