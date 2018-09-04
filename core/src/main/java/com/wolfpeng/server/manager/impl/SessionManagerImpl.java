package com.wolfpeng.server.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import com.wolfpeng.dao.FileDAO;
import com.wolfpeng.dao.MetadataDAO;
import com.wolfpeng.dao.UserDAO;
import com.wolfpeng.exception.MediaServerException;
import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;
import com.wolfpeng.server.manager.Session;
import com.wolfpeng.model.UserDO;
import com.wolfpeng.server.manager.SessionManager;
import com.wolfpeng.server.protocol.Base.Control;
import com.wolfpeng.server.protocol.Base.Control.Action;
import com.wolfpeng.server.protocol.NotifyOuterClass.Notify;
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

    @Resource
    FileDAO fileDAO;

    @Resource
    MetadataDAO metadataDAO;

    @Override
    public void register(String name, String pwd) throws ProcessException {
        UserDO userDO = new UserDO();
        userDO.setName(name);
        userDO.setPwd(pwd);
        userDAO.insertUserDO(userDO);
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
    public void addSession(Session session, UserDO userDO) throws ProcessException {
        if (sessionHashMap.containsKey(userDO.getId())) {
            LOGGER.error("this session is aready logined");
            throw ProcessException.builder().errorCode(-1).errorMsg("aready logined").build();
        }
        session.setUserDO(userDO);
        sessionHashMap.put(userDO.getId(), session);
    }

    @Override
    public Session bindMusicChannel(Long id, Channel channel) throws ProcessException {
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
