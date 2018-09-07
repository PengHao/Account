package com.wolfpeng.model;

import lombok.Data;

/**
 * Created by penghao on 2018/8/26.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Data
public class CoverDO extends BaseDO {
    String path;
    Long fileId;

    byte[] data;
}

/**

CREATE TABLE `cover` (
     `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
     `create_time` datetime NOT NULL COMMENT '创建时间',
     `modify_time` datetime NOT NULL COMMENT '修改时间',
     `status` tinyint NOT NULL COMMENT '-1:删除， 1:有效',
    `data` mediumblob,
    `path` TEXT COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `file_id` bigint unsigned NOT NULL COMMENT '文件id',

    PRIMARY KEY (`id`),
    KEY `file_id` (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
**/