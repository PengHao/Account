package com.wolfpeng.server.manager;

import io.netty.channel.Channel;

/**
 * Created by penghao on 2018/9/4.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public interface ChannelManage {
    void bindSession(Long uid, Channel channel);
}
