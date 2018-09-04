package com.wolfpeng.server.manager.impl;

import java.util.HashMap;
import java.util.Map;

import com.wolfpeng.server.manager.ChannelManage;
import io.netty.channel.Channel;

/**
 * Created by penghao on 2018/9/4.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public class ChannelManageImpl implements ChannelManage {
    Map<Long, Channel> channelMap = new HashMap<>();
    @Override
    public void bindSession(Long uid, Channel channel) {
        channelMap.put(uid, channel);

    }
}
