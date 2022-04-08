package com.crscd.cds.ctc.codec;

import com.crscd.cds.ctc.protocol.MessagePackage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author zhaole
 * @date 2022-04-03
 */
public class PackageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
//        MessagePackage pkg = new MessagePackage();
//
//        long version = byteBuf.readUnsignedIntLE();
//        long length = byteBuf.readUnsignedIntLE();
//        short type = byteBuf.readUnsignedByte();
//        long seq = byteBuf.readUnsignedIntLE();
//
//        pkg.setVersion(version);
//        pkg.setLength(length);
//        pkg.setType(type);
//        pkg.setSeq(seq);
//
//        list.add(pkg);

//        int a = byteBuf.readUnsignedShortLE();
    }
}
