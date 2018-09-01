package com.wolfpeng.model;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * Created by penghao on 2018/8/26.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Data
public class MetadataDO extends BaseDO {
    Long targetId;
    //开始时间点(针对cue)
    Long startTime;
    //持续时间
    Long duration;

    String artist;

    String title;

    String ablum;

    String ablumArtist;
    //作曲
    String composer;
    String date;

    String discNumber;
    String trackNumber;
    String copyRight;
    //类型
    String genre;
    String genreNumber;
    String organization;
    String comment;
    String performer;
    String mood;
    Long coverId;

}


/**
 CREATE TABLE `metadata` (
 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
 `create_time` datetime NOT NULL COMMENT '创建时间',
 `modify_time` datetime NOT NULL COMMENT '修改时间',
 `status` tinyint NOT NULL COMMENT '-1:删除， 1:有效',

 `target_id` bigint unsigned NOT NULL COMMENT '文件id',
 `start_time` bigint unsigned COMMENT 'cue开始时间点',
 `duration` bigint unsigned COMMENT '持续时间',
 `artist` varchar(128) COMMENT 'artist',
 `title` varchar(128) COMMENT 'title',
 `ablum` varchar(128) COMMENT 'ablum',
 `ablum_artist` varchar(128) COMMENT 'ablum_artist',
 `composer` varchar(128) COMMENT 'composer',
 `date` varchar(128) COMMENT 'date',
 `disc_number` varchar(128) COMMENT 'disc_number',
 `track_number` varchar(128) COMMENT 'track_number',
 `copy_right` varchar(128) COMMENT 'copy_right',
 `genre` varchar(128) COMMENT 'genre',
 `genre_number` varchar(128) COMMENT 'genre_number',
 `organization` varchar(128) COMMENT 'organization',
 `comment` text(2048) COMMENT 'comment',
 `performer` varchar(128) COMMENT 'performer',
 `mood` varchar(128) COMMENT 'mood',
 `cover_id` bigint unsigned COMMENT 'cover_id',

 PRIMARY KEY (`id`),
 KEY `target_id` (`target_id`),
 KEY `artist` (`artist`),
 KEY `title` (`title`),
 KEY `ablum` (`ablum`),
 KEY `ablum_artist` (`ablum_artist`),
 KEY `composer` (`composer`),
 KEY `genre` (`genre`),
 KEY `performer` (`performer`)

 ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci  COMMENT='媒体表';
 **/