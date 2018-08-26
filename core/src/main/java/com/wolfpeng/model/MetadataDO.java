package com.wolfpeng.model;

import java.util.Date;

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
    Date date;

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
    Long pictureId;

}


/**
 CREATE TABLE `metadata` (
 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
 `create_time` datetime NOT NULL COMMENT '创建时间',
 `modify_time` datetime NOT NULL COMMENT '修改时间',

 `file_id` bigint unsigned NOT NULL COMMENT '文件id',
 `start_time` int(11) unsigned COMMENT 'cue开始时间点',
 `duration` int(11) unsigned COMMENT '持续时间',
 `artist` varchar(128) NOT NULL COMMENT 'artist',
 `title` varchar(128) NOT NULL COMMENT 'title',
 `ablum` varchar(128) NOT NULL COMMENT 'ablum',
 `ablum_artist` varchar(128) NOT NULL COMMENT 'ablum_artist',
 `composer` varchar(128) NOT NULL COMMENT 'composer',
 `date` datetime NOT NULL COMMENT 'date',
 `disc_number` varchar(128) NOT NULL COMMENT 'disc_number',
 `track_number` varchar(128) NOT NULL COMMENT 'track_number',
 `copy_right` varchar(128) NOT NULL COMMENT 'copy_right',
 `genre` varchar(128) NOT NULL COMMENT 'genre',
 `genre_number` varchar(128) NOT NULL COMMENT 'genre_number',
 `organization` varchar(128) NOT NULL COMMENT 'organization',
 `comment` varchar(128) NOT NULL COMMENT 'comment',
 `performer` varchar(128) NOT NULL COMMENT 'performer',
 `mood` varchar(128) NOT NULL COMMENT 'mood',
 `cover_id` varchar(128) NOT NULL COMMENT 'cover_id',

 PRIMARY KEY (`id`),
 KEY `file_id` (`file_id`),
 KEY `artist` (`artist`),
 KEY `title` (`title`),
 KEY `ablum` (`ablum`),
 KEY `ablum_artist` (`ablum_artist`),
 KEY `composer` (`composer`),
 KEY `genre` (`genre`),
 KEY `performer` (`performer`)

 ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci  COMMENT='媒体表';
 **/