package com.wolfpeng.media;

import java.io.File;

import javax.annotation.Resource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.wolfpeng.dao.CoverDAO;
import com.wolfpeng.dao.FileDAO;
import com.wolfpeng.dao.MetadataDAO;
import com.wolfpeng.model.CoverDO;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;
import com.wolfpeng.server.protocol.Base.Metadata;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by penghao on 2018/8/26.
 * Copyright © 2017年 penghao. All rights reserved.
 */


public class Player {
    private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);

    Thread thread;

    public void stop() {
        if (thread == null) {
            return;
        }
        thread.interrupt();
    }

    public void play(String filePath, PlayerCallBack playerCallBack) {
        stop();
        thread = new Thread(() -> {
            read(filePath, playerCallBack);
        });
        thread.start();
    }


    private void read(String filePath, PlayerCallBack playerCallBack) {
        final File file = new File(filePath);
        try {
            final AudioInputStream in = AudioSystem.getAudioInputStream(file);
            AudioFormat inFormat = getConvertOutFormat(in.getFormat());
            playerCallBack.onReadFormat(inFormat);
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(inFormat, in);

            final int sampleSizePreFrame = inFormat.getChannels() * inFormat.getSampleSizeInBits() / 8;
            Float rs = sampleSizePreFrame * inFormat.getSampleRate();
            Integer oneSecBufferSize = rs.intValue();

            //快进1秒
            //inputStream.skip(oneSecBufferSize);

            byte[] buffer = new byte[oneSecBufferSize];
            int len = 0;
            while (len > -1) {
                long time1=System.nanoTime()/1000000;
                len = inputStream.read(buffer, 0, buffer.length);
                if (len == -1) {
                    continue;
                }
                playerCallBack.onReadData(buffer, len);
                long time2=System.nanoTime()/1000000;
                Thread.sleep(900 + time1 - time2);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private AudioFormat getConvertOutFormat(AudioFormat inFormat) {
        final int channelNumber = inFormat.getChannels();
        final Float rate = inFormat.getSampleRate();
        final int sampleSizeInBits = 16;
        final int sampleSizePreFrame = inFormat.getChannels() * inFormat.getSampleSizeInBits() / 8;
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, sampleSizeInBits, channelNumber, sampleSizePreFrame, rate, false);
    }

}
