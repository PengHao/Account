package com.wolfpeng.server.netty;


import com.wolfpeng.server.protocol.MessageOuterClass.Message;
import com.wolfpeng.server.process.BaseProcess;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.context.ApplicationContext;

/**
 * Created by penghao on 2018/8/23.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public class ProtocolBufferInboundHandler extends ChannelInboundHandlerAdapter {

    public static ApplicationContext currentApplicationContext;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message)msg;
        try {
            BaseProcess baseProcess = currentApplicationContext.getBean(BaseProcess.class, message.getPackageCase().name());
            baseProcess.excute(message, ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ctx.fireChannelRead(msg);
    }
}
