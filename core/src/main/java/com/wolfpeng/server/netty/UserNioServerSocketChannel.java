package com.wolfpeng.server.netty;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * Created by penghao on 2018/9/4.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public class UserNioServerSocketChannel extends NioServerSocketChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(UserNioServerSocketChannel.class);

    Map<Long, UserSocketChannel> channelMap = new HashMap<>();

    public void bind(Long uid, UserSocketChannel channel) {
        channelMap.put(uid, channel);
    }

    public UserSocketChannel getChannel(Long uid) {
        return channelMap.get(uid);
    }

    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        SocketChannel ch = SocketUtils.accept(javaChannel());

        try {
            if (ch != null) {
                buf.add(new UserSocketChannel(this, ch));
                return 1;
            }
        } catch (Throwable t) {
            logger.warn("Failed to create a new channel from an accepted socket.", t);

            try {
                ch.close();
            } catch (Throwable t2) {
                logger.warn("Failed to close a socket.", t2);
            }
        }

        return 0;
    }
}
