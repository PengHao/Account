package com.wolfpeng.model;

import lombok.Data;

/**
 * Created by penghao on 2018/8/26.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Data
public class CoverDO extends BaseDO {
    String path;

    byte[] data;
}

/**

CREATE TABLE `cover` (
     `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `data` mediumblob,
    `path` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,

    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
**/