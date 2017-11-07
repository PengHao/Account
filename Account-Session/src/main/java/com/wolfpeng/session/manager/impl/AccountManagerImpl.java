package com.wolfpeng.session.manager.impl;

import java.util.Date;

import javax.security.auth.login.AccountException;

import com.wolfpeng.session.dao.SessionDAO;
import com.wolfpeng.session.dao.UserDAO;
import com.wolfpeng.session.manager.AccountManager;
import com.wolfpeng.session.moudle.LobbyDO;
import com.wolfpeng.session.moudle.SessionDO;
import com.wolfpeng.session.moudle.UserDO;

/**
 * Created by penghao on 2017/11/6.
 * Copyright © 2017年 Alibaba. All rights reserved.
 */
public class AccountManagerImpl implements AccountManager {

    SessionDAO sessionDAO;
    UserDAO userDAO;

    @Override
    public SessionDO login(String userName, String password) throws AccountException {
        UserDO userDO = userDAO.queryUserDO(userName);
        if (userDO == null||!userDO.getPassword().equals(password)) {
            throw new AccountException("userName is not match password");
        }

        SessionDO sessionDO = sessionDAO.refreshSessionDO(userDO.getId());
        return sessionDO;
    }

    @Override
    public Boolean regist(String userName, String password) throws AccountException {
        return userDAO.insert(userName, password) != null;
    }

    @Override
    public Boolean logout(SessionDO sessionDO) throws AccountException {
        return null;
    }

    @Override
    public LobbyDO enter(SessionDO sessionDO) throws AccountException {
        SessionDO sessionDOInDB = sessionDAO.querySessionDO(sessionDO.getId());
        if (sessionDOInDB.getExprie().compareTo(new Date()) <= 0
            || !sessionDOInDB.getToken().equals(sessionDO.getToken())) {
            throw new AccountException("invalid session");
        }
        LobbyDO lobbyDO = new LobbyDO();
        lobbyDO.setHost("127.0.0.1");
        lobbyDO.setPort(1000);
        return lobbyDO;
    }

}
