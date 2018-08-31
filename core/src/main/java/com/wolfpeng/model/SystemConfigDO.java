package com.wolfpeng.model;

import lombok.Data;

/**
 * Created by penghao on 2018/8/31.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Data
public class SystemConfigDO {
    public static SystemConfigDO defaultSystemConfigDO;

    String libarayPath = "/Users/penghao/Music";

    String fileTypes = ".flac, .cue, .wav, .mp3";
}
