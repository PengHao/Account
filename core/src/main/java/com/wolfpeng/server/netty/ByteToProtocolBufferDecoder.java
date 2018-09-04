package com.wolfpeng.server.netty;

import java.util.List;

import com.wolfpeng.server.protocol.MessageOuterClass.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


/**
 * Created by penghao on 2018/8/23.
 * Copyright © 2017年 penghao. All rights reserved.
 */

public class ByteToProtocolBufferDecoder extends ByteToMessageDecoder {

    Integer currentPackageLength;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (currentPackageLength == null) {
            if (in.readableBytes() < 4) {
                break;
            }

            currentPackageLength = in.readIntLE();
            if (in.readableBytes() >= currentPackageLength) {
                byte[] bytes = new byte[currentPackageLength];
                in.readBytes(bytes);
                Message msg = Message.parseFrom(bytes);
                out.add(msg);
                currentPackageLength = null;
            }
        }
    }
}