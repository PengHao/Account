package com.wolfpeng.server.manager;

import java.util.List;

import com.wolfpeng.exception.ProcessException;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * Created by penghao on 2018/8/24.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public interface SessionManager {

    static final AttributeKey<Session> SESSION_KEY = AttributeKey.newInstance("SESSION_KEY");

    Session login(String name, String pwd, Channel channel) throws ProcessException;

    void register(String name, String pwd) throws ProcessException;

    void logout(Session session) ;

    Session bind(Long id, Channel channel) throws ProcessException;

    Session getSession(Channel channel);

    Session getSession(Long id);

    List<Session> getPlayableSessions();


}
