package com.wolfpeng.media;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Created by penghao on 2018/8/26.
 * Copyright © 2017年 penghao. All rights reserved.
 */


public class Player {

    Thread thread;

    public void stop() {
        if (thread == null) {
            stop();
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
            Integer bufferSize = rs.intValue();
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while (len > -1) {
                long time1=System.nanoTime()/1000000;
                len = inputStream.read(buffer, 0, buffer.length);
                if (len == -1) {
                    continue;
                }
                playerCallBack.onReadData(buffer, len);
                long time2=System.nanoTime()/1000000;
                Thread.sleep(1000 + time1 - time2);
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
