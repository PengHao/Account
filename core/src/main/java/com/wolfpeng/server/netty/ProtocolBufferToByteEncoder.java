package com.wolfpeng.server.netty;

import com.wolfpeng.server.protocol.MessageOuterClass.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by penghao on 2018/8/23.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public class ProtocolBufferToByteEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        byte[] bytes = msg.toByteArray();
        out.writeIntLE(bytes.length);
        out.writeBytes(bytes);
    }
}
