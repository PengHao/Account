package com.wolfpeng.session.manager.impl;

import javax.security.auth.login.AccountException;

import com.wolfpeng.session.dao.SessionDAO;
import com.wolfpeng.session.dao.UserDAO;
import com.wolfpeng.session.manager.AccountManager;
import com.wolfpeng.session.moudle.RoomDO;
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
    public RoomDO enter(SessionDO sessionDO) throws AccountException {
        return null;
    }

}
