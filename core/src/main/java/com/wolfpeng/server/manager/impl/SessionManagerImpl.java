package com.wolfpeng.server.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import com.wolfpeng.dao.UserDAO;
import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.server.manager.Session;
import com.wolfpeng.model.UserDO;
import com.wolfpeng.server.manager.SessionManager;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by penghao on 2018/8/24.
 * Copyright © 2017年 penghao. All rights reserved.
 */

@Service
public class SessionManagerImpl implements SessionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManagerImpl.class);

    private static HashMap<Long, Session> sessionHashMap = new HashMap<>(512);

    @Resource
    UserDAO userDAO;

    @Override
    public Session login(String name, String pwd, Channel channel) throws ProcessException {
        UserDO userDO = userDAO.queryUserDO(name, pwd);
        if (userDO == null) {
            LOGGER.error("login failed: name = {}, pwd = {}", name, pwd);
            throw ProcessException.builder().errorCode(-1).errorMsg("invalid user").build();
        }

        Session session = getSession(channel);
        if (session != null) {
            LOGGER.error("this channel is aready logined");
            throw ProcessException.builder().errorCode(-1).errorMsg("aready logined").build();
        }

        userDO.setLastLoginTime(new Date());
        userDAO.updateUserDO(userDO);
        session = sessionHashMap.get(userDO.getId());
        if (session == null) {
            session = new Session();
        } else {
            //close channel
        }
        session.setUserDO(userDO);
        session.setControllChannel(channel);
        channel.attr(SESSION_KEY).set(session);
        sessionHashMap.put(userDO.getId(), session);
        return session;
    }

    @Override
    public void register(String name, String pwd) throws ProcessException {
        UserDO userDO = new UserDO();
        userDO.setName(name);
        userDO.setPwd(pwd);
        userDAO.inserUserDO(userDO);
    }

    @Override
    public void logout(Session session)  {
        UserDO userDO = session.getUserDO();
        if (userDO == null) {
            return;
        }
        sessionHashMap.put(userDO.getId(), null);
    }

    @Override
    public Session bind(Long id, Channel channel) throws ProcessException {
        Session session = getSession(channel);
        if (session != null) {
            LOGGER.error("this channel is aready logined");
            throw ProcessException.builder().errorCode(-1).errorMsg("aready logined").build();
        }

        session = sessionHashMap.get(id);
        if (session == null) {
            throw ProcessException.builder().errorCode(-1).errorMsg("user not logined").build();
        }
        session.setMusicChannel(channel);
        return session;
    }

    @Override
    public Session getSession(Channel channel) {
        Session session = channel.attr(SESSION_KEY).get();
        return session;
    }

    @Override
    public Session getSession(Long id) {
        return sessionHashMap.get(id);
    }

    @Override
    public List<Session> getPlayableSessions() {
        List<Session> result = new ArrayList<>();
        sessionHashMap.values().forEach(session -> {
            if(session.getMusicChannel() != null) {
                result.add(session);
            }
        });
        return result;
    }
}
