package com.wolfpeng.media;

/**
 * Created by penghao on 2018/9/7.
 * Copyright © 2017年 penghao. All rights reserved.
 */

import lombok.Getter;
import lombok.Setter;

public class WaveHeader {

    private static int byte2int_little(byte[] source, int offset) {
        int ret = source[offset + 0] & 0xff;
        ret += (source[offset + 1] & 0xff) << 8;
        ret += (source[offset + 2] & 0xff) << 16;
        ret += (source[offset + 3] & 0xff) << 24;
        return ret;
    }

    private static void int2byte_little(int source, byte[] bytes, int offset) {
        bytes[offset + 0] = (byte) (source & 0xff);
        bytes[offset + 1] = (byte) ((source >> 8) & 0xff);
        bytes[offset + 2] = (byte) ((source >> 16) & 0xff);
        bytes[offset + 3] = (byte) ((source >> 24) & 0xff);
    }

    private static short byte2short_little(byte[] source, int offset) {
        short ret = (short) (source[offset + 0] & 0xff);
        ret += (source[offset + 1] & 0xff) << 8;
        return ret;
    }

    private static void short2byte_little(short source, byte[] bytes, int offset) {
        bytes[offset + 0] = (byte) (source & 0xff);
        bytes[offset+1] = (byte) ((source >> 8) & 0xff);
    }

    private static final String CHUNK_ID = "RIFF";
    private static final String FORMAT_WAVE = "WAVE";
    private static final String SUBCHUNK_1ID = "fmt ";
    private static final String SUBCHUNK_2ID = "data";
    private static final int MIN_HEADER_LENGTH = 44;

    public boolean inited = false;
    private String chunkID = null;
    private int chunckSize = 0;
    private String format = null;
    private String subChunk1ID = null;
    private int subChunk1Size = 16;
    private short audioFormat = 1;
    @Setter
    @Getter
    private short numChannels = 1;
    @Setter
    @Getter
    private int sampleRate = 8000;
    private int byteRate = 0;
    private short blockAlign = 0;
    @Setter
    @Getter
    private short bitsPerSample = 8;
    private String subChunk2ID = null;
    @Setter
    @Getter
    private int subChunk2Size = 0;
    private byte[] data;

    public boolean parseHeader(byte[] bytes) {
        if (bytes == null || bytes.length < MIN_HEADER_LENGTH) {
            return false;
        }

        // starts with 'RIFF' in ascii
        this.chunkID = new String(bytes, 0, 4);
        if (!this.chunkID.equals(CHUNK_ID)) {
            return false;
        }

        this.chunckSize = byte2int_little(bytes, 4);

        this.format = new String(bytes, 8, 4);
        if (!this.format.equals(FORMAT_WAVE)) {
            return false;
        }

        this.subChunk1ID = new String(bytes, 12, 4);
        if (!this.subChunk1ID.equals(SUBCHUNK_1ID)) {
            return false;
        }

        this.subChunk1Size = byte2int_little(bytes, 16);
        this.audioFormat = byte2short_little(bytes, 20);
        this.numChannels = byte2short_little(bytes, 22);
        this.sampleRate = byte2int_little(bytes, 24);
        this.byteRate = byte2int_little(bytes, 28);
        this.blockAlign = byte2short_little(bytes, 32);
        this.bitsPerSample = byte2short_little(bytes, 34);

        // TODO - parse subChunk2
        // this.subChunk2ID = new String(bytes, 36, 4);
        // if(!subChunk2ID.equals(SUBCHUNK_2ID)){
        // return false;
        // }

        // this.subChunk2Size = BinaryUtil.byte2int_little(bytes, 40);

        this.inited = true;
        return inited;
    }

    private int writeHeader(byte[] bytes, int offset) {
        this.byteRate = this.sampleRate * this.numChannels * this.bitsPerSample / 8;
        this.blockAlign = (short) (this.numChannels * this.bitsPerSample / 8);
        this.chunckSize = 4 + (8 + this.subChunk1Size) + (8 + this.subChunk2Size);

        System.arraycopy(CHUNK_ID.getBytes(), 0, bytes, offset, 4);
        int2byte_little(this.chunckSize, bytes, offset + 4);
        System.arraycopy(FORMAT_WAVE.getBytes(), 0, bytes, offset + 8, 4);
        System.arraycopy(SUBCHUNK_1ID.getBytes(), 0, bytes, offset + 12, 4);
        int2byte_little(this.subChunk1Size, bytes, offset + 16);
        short2byte_little(this.audioFormat, bytes, offset + 20);
        short2byte_little(this.numChannels, bytes, offset + 22);
        int2byte_little(this.sampleRate, bytes, offset + 24);
        int2byte_little(this.byteRate, bytes, offset + 28);
        short2byte_little(this.blockAlign, bytes, offset + 32);
        short2byte_little(this.bitsPerSample, bytes, offset + 34);
        System.arraycopy(SUBCHUNK_2ID.getBytes(), 0, bytes, offset + 36, 4);
        int2byte_little(this.subChunk2Size, bytes, offset + 40);
        return MIN_HEADER_LENGTH;
    }

    public byte[] writeHeader() {
        byte[] ret = new byte[MIN_HEADER_LENGTH];
        this.writeHeader(ret, 0);
        return ret;
    }

    public String printFormat() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.format);
        stringBuilder.append(":\nNumberOfChannels:");
        stringBuilder.append(this.numChannels);
        stringBuilder.append("\nSampleRate:");
        stringBuilder.append(this.sampleRate);
        stringBuilder.append("\nBitsPerSample:");
        stringBuilder.append(this.bitsPerSample);
        stringBuilder.append("\n----\nByteRate:");
        stringBuilder.append(this.byteRate);
        stringBuilder.append("\nBlockAlign:");
        stringBuilder.append(this.blockAlign);

        stringBuilder.append("\nDataSize:");
        stringBuilder.append(this.subChunk2Size);
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }
}

