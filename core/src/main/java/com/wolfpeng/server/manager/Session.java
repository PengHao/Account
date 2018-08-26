package com.wolfpeng.server.manager;

import javax.sound.sampled.AudioFormat;

import com.google.protobuf.ByteString;
import com.wolfpeng.media.Player;
import com.wolfpeng.media.PlayerCallBack;
import com.wolfpeng.model.UserDO;
import com.wolfpeng.server.protocol.MessageOuterClass.Message;
import com.wolfpeng.server.protocol.NotifyOuterClass;
import com.wolfpeng.server.protocol.NotifyOuterClass.Format;
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
    Player player = new Player();

    public void logout() {
        player.stop();
        controllChannel.close();
        musicChannel.close();
    }

    public void sendResponse(Response.Builder response) {
        Message responseMessage = Message.newBuilder().setResponse(response).build();
        controllChannel.writeAndFlush(responseMessage);
    }

    public void sendNotify(Notify.Builder notify) {
        Message responseMessage = Message.newBuilder().setNotify(notify).build();
        musicChannel.writeAndFlush(responseMessage);
    }

    public void play(Long metaId) {

        //send metaData


        String filePath = "/Users/penghao/1.wav";
        player.play(filePath, new PlayerCallBack() {
            @Override
            public void onReadData(byte[] data, long len) {
                NotifyOuterClass.Data.Builder streamData = NotifyOuterClass.Data.newBuilder().setData(ByteString.copyFrom(data, 0, (int)len));
                sendNotify(Notify.newBuilder().setData(streamData));
            }

            @Override
            public void onReadFormat(AudioFormat audioFormat) {
                Format.Builder format = Format.newBuilder()
                    .setFormatID(0)
                    .setChannelsPerFrame(audioFormat.getChannels())
                    .setBitsPerChannel(audioFormat.getSampleSizeInBits())
                    .setFormatFlags(1)
                    .setSampleRate(Float.valueOf(audioFormat.getSampleRate()).intValue())
                    .setFramesPerPacket(1);
                sendNotify(Notify.newBuilder().setFormat(format));
            }
        });

    }
}
