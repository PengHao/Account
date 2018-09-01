package com.wolfpeng.media;

import javax.sound.sampled.AudioFormat;
/**
 * Created by penghao on 2018/8/27.
 * Copyright © 2017年 penghao. All rights reserved.
 */

public interface PlayerCallBack {
    void onReadData(byte[] data, long len);
    void onReadFormat(final AudioFormat audioFormat);
    void onReadEnd();
}