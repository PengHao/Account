package com.wolfpeng.server.manager;

import javax.annotation.Resource;
import javax.sound.sampled.AudioFormat;

import com.google.protobuf.ByteString;
import com.wolfpeng.dao.FileDAO;
import com.wolfpeng.dao.MetadataDAO;
import com.wolfpeng.exception.MediaServerException;
import com.wolfpeng.media.Player;
import com.wolfpeng.media.PlayerCallBack;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;
import com.wolfpeng.model.UserDO;
import com.wolfpeng.server.protocol.Base.Control;
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
    Boolean playAble = false;

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

    @Resource
    MetadataDAO metadataDAO;

    @Resource
    FileDAO fileDAO;

    public void play(String filePath, Long start, Long duration) throws MediaServerException {
        if (!playAble) {
            throw MediaServerException.builder().errorMessage("Device is not playable").build();
        }

        player.play(filePath, start, duration, new PlayerCallBack() {
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

            @Override
            public void onReadEnd() {
                //数据读取完毕

            }
        });

    }
}
