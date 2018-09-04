package com.wolfpeng.server.manager;

import com.wolfpeng.exception.MediaServerException;

/**
 * Created by penghao on 2018/9/1.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public interface PlayerManager {

    void play(Long metaId, Long deviceId) throws MediaServerException;

    void pause(Long deviceId) throws MediaServerException;

    void next(Long deviceId) throws MediaServerException;
}
