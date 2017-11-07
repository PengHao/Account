package com.wolfpeng.session.moudle;

import java.io.Serializable;

import lombok.Data;

/**
 *
 * @author penghao
 * @date 2017/11/6
 * Copyright © 2017年 Alibaba. All rights reserved.
 */
@Data
public class RoomDO implements Serializable {
    private static final long serialVersionUID = 5370263860915417985L;
    private String host;
    private Integer port;
}
