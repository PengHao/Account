package com.wolfpeng.server.netty;

import com.wolfpeng.server.protocol.MessageOuterClass.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by penghao on 2018/8/25.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public class SessionInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message)msg;

        ctx.fireChannelRead(msg);
    }
}
