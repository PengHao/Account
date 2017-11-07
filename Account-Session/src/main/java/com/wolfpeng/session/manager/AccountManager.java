package com.wolfpeng.session.manager;

import javax.security.auth.login.AccountException;

import com.wolfpeng.session.moudle.RoomDO;
import com.wolfpeng.session.moudle.SessionDO;

/**
 * Created by penghao on 2017/11/6.
 * Copyright © 2017年 Alibaba. All rights reserved.
 */
public interface AccountManager {
    SessionDO login(String userName, String password) throws AccountException;
    Boolean regist(String userName, String password) throws AccountException;
    Boolean logout(SessionDO sessionDO) throws AccountException;
    RoomDO enter(SessionDO sessionDO) throws AccountException;
}
