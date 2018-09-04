package com.wolfpeng.server.manager;

import java.util.List;

import com.wolfpeng.exception.MediaServerException;
import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.model.UserDO;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * Created by penghao on 2018/8/24.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public interface SessionManager {

    void register(String name, String pwd) throws ProcessException;

    void logout(Session session) ;

    void addSession(Session session, UserDO userDO) throws ProcessException ;

    Session bindMusicChannel(Long id, Channel channel) throws ProcessException;

    Session getSession(Long id);

    List<Session> getPlayableSessions();


}
