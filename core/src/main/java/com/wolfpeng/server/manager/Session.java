package com.wolfpeng.server.manager;

import com.wolfpeng.model.UserDO;
import com.wolfpeng.server.protocol.MessageOuterClass.Message;
import com.wolfpeng.server.protocol.NotifyOuterClass.Notify;
import com.wolfpeng.server.protocol.ResponseOuterClass.Response;
import io.netty.channel.Channel;
import lombok.Data;

/**
 * Created by penghao on 2018/8/25.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Data
public class Session {
    Channel controllChannel;
    Channel musicChannel;
    UserDO userDO;

    public void sendResponse(Response.Builder response) {
        Message responseMessage = Message.newBuilder().setResponse(response).build();
        controllChannel.writeAndFlush(responseMessage);
    }

    public void sendNotify(Notify.Builder notify) {
        Message responseMessage = Message.newBuilder().setNotify(notify).build();
        controllChannel.writeAndFlush(responseMessage);
    }

    public void play(Long metaId) {

        //send metaData


        //send format

        //send stream

    }
}
