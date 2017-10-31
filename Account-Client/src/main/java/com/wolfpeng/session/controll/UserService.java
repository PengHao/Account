package com.wolfpeng.session.controll;

import com.wolfpeng.session.moudle.SessionDO;

/**
 * Created by penghao on 2017/9/15.
 * Copyright ? 2017年 wolfpeng. All rights reserved.
 */
public interface UserService {
    SessionDO login(String userName, String password);
    SessionDO regist(String userName, String password);
    boolean logout(SessionDO sessionDO);
}
