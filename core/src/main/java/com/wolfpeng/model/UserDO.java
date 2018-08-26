package com.wolfpeng.model;

import java.util.Date;

import lombok.Data;

/**
 * Created by penghao on 2018/8/24.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Data
public class UserDO extends BaseDO {
    String name;
    String pwd;
    Date lastLoginTime;
}


/**

CREATE TABLE `User` (
 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` char(24) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `pwd` char(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `create_time` datetime NOT NULL,
  `modify_time` datetime NOT NULL,
  `last_login_time` datetime DEFAULT NULL,

  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

 **/