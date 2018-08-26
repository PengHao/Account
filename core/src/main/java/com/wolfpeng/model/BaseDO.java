package com.wolfpeng.model;

import java.util.Date;

import lombok.Data;

/**
 * Created by penghao on 2018/8/25.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Data
public class BaseDO {
    Long id;
    Date createTime;
    Date modifyTime;
    Integer status; //-1: delete, other: normal
}
