package com.wolfpeng.model;

import lombok.Data;

/**
 * Created by penghao on 2018/8/31.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Data
public class SystemConfigDO extends BaseDO {
    public static SystemConfigDO defaultSystemConfigDO = new SystemConfigDO();

    String libarayPath = "/Users/penghao/Music";

    String fileTypes = ".flac, .cue, .wav, .mp3";
}


/**
 CREATE TABLE `system_config` (
 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
 `create_time` datetime NOT NULL,
 `modify_time` datetime NOT NULL,
 `libaray_path` text COLLATE utf8mb4_unicode_ci NOT NULL,
 `file_types` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件类型',
 PRIMARY KEY (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
**/