package com.wolfpeng.server.netty;

import java.nio.channels.SocketChannel;

import com.wolfpeng.server.manager.Session;
import com.wolfpeng.server.protocol.MessageOuterClass.Message;
import com.wolfpeng.server.protocol.NotifyOuterClass.Notify;
import com.wolfpeng.server.protocol.ResponseOuterClass.Response;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;

/**
 * Created by penghao on 2018/8/24.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public class UserSocketChannel extends NioSocketChannel {

    @Getter
    Session session;
    public UserSocketChannel(Channel parent, SocketChannel socket) {
        super(parent, socket);
        session = new Session();
    }

    @Override
    public UserNioServerSocketChannel parent() {
        return (UserNioServerSocketChannel) super.parent();
    }


    public void sendResponse(Response.Builder response) {
        Message responseMessage = Message.newBuilder().setResponse(response).build();
        writeAndFlush(responseMessage);
    }


    public void sendNotify(Notify.Builder notify) {
        Message responseMessage = Message.newBuilder().setNotify(notify).build();
        writeAndFlush(responseMessage);
    }
}
