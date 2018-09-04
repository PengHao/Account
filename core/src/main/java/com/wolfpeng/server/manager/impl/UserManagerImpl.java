package com.wolfpeng.server.manager.impl;

import java.util.Date;

import javax.annotation.Resource;

import com.wolfpeng.dao.UserDAO;
import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.model.UserDO;
import com.wolfpeng.server.manager.Session;
import com.wolfpeng.server.manager.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by penghao on 2018/9/4.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Service
public class UserManagerImpl implements UserManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagerImpl.class);

    @Resource
    UserDAO userDAO;

    @Override
    public UserDO login(String name, String pwd) throws ProcessException {
        UserDO userDO = userDAO.queryUserDO(name, pwd);
        if (userDO == null) {
            LOGGER.error("login failed: name = {}, pwd = {}", name, pwd);
            throw ProcessException.builder().errorCode(-1).errorMsg("invalid user").build();
        }

        userDO.setLastLoginTime(new Date());
        userDAO.updateUserDO(userDO);
        return userDO;
    }
}
