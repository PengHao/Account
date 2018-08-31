package com.wolfpeng.model;

import lombok.Data;

/**
 * Created by penghao on 2018/8/26.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Data
public class FileDO extends BaseDO {
    public final static int DIC = 1;
    public final static int FILE = 0;


    String path;
    String name;
    Integer type;
    //父节点ID
    Long parentId;

}

/**
    CREATE TABLE `file` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `modify_time` datetime NOT NULL COMMENT '修改时间',
    `status` tinyint NOT NULL COMMENT '-1:删除， 1:有效',
    `path` text(4096) NOT NULL COMMENT '文件路径',
    `name` varchar(128) NOT NULL COMMENT '文件名称',
    `type` tinyint NOT NULL COMMENT '0:文件, 1:文件夹, 2: 隐藏',
    `parent_id` bigint unsigned COMMENT '父目录ID',

    PRIMARY KEY (`id`),
    KEY `name` (`name`),
    KEY `parent_id` (`parent_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci  COMMENT='文件表';
 **/