package com.wolfpeng.media;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
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

    public void asyncPlay(String filePath, Long start, Long duration, PlayerCallBack playerCallBack) {
        stop();
        thread = new Thread(() -> {
            syncPlay(filePath, start, duration, playerCallBack);
        });
        thread.start();
    }


    public void syncPlay(String filePath, Long start, Long duration, PlayerCallBack playerCallBack) {
        final File file = new File(filePath);
        try {
            final AudioInputStream in = AudioSystem.getAudioInputStream(file);
            AudioFormat inFormat = getConvertOutFormat(in.getFormat());
            long frameLength = in.getFrameLength();
            playerCallBack.onReadFormat(inFormat, frameLength);
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(inFormat, in);

            final int sampleSizePreFrame = inFormat.getChannels() * inFormat.getSampleSizeInBits() / 8;
            Float rs = sampleSizePreFrame * inFormat.getSampleRate();
            Integer oneSecBufferSize = rs.intValue();

            //快进1秒

            byte[] buffer = new byte[oneSecBufferSize];
            int len = 0;
            Long decodedDurationMS = 0L;
            Long startMS = start != null && start > 0 ? start : null;
            Long endMs = startMS != null && duration != null && duration > 0 ? startMS + duration : null;

            while (len > -1) {
                long time1=System.nanoTime()/1000000;
                len = inputStream.read(buffer, 0, buffer.length);
                decodedDurationMS += len/oneSecBufferSize*1000;
                if (startMS != null && startMS > decodedDurationMS) {
                    continue;
                }
                if (len == -1) {
                    continue;
                }
                playerCallBack.onReadData(buffer, len);
                if (endMs != null && decodedDurationMS >= endMs) {
                    playerCallBack.onReadEnd();
                    continue;
                }
                long time2=System.nanoTime()/1000000;
                long sleepTime = 900 + time1 - time2;
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            }
            playerCallBack.onReadEnd();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private AudioFormat getConvertOutFormat(AudioFormat inFormat) {
        final int channelNumber = inFormat.getChannels();
        final Float rate = inFormat.getSampleRate();
        final int sampleSizeInBits = inFormat.getSampleSizeInBits();
        final int sampleSizePreFrame = inFormat.getChannels() * sampleSizeInBits / 8;
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, sampleSizeInBits, channelNumber, sampleSizePreFrame, rate, false);
    }

}
