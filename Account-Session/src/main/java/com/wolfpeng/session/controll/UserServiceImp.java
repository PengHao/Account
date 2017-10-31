package com.wolfpeng.session.controll;

import com.wolfpeng.session.dao.SessionDAO;
import com.wolfpeng.session.dao.UserDAO;
import com.wolfpeng.session.moudle.SessionDO;
import com.wolfpeng.session.moudle.UserDO;

/**
 * Created by penghao on 2017/9/15.
 * Copyright ? 2017年 wolfpeng. All rights reserved.
 */
public class UserServiceImp implements UserService {
    SessionDAO sessionDAO;
    UserDAO userDAO;
    public SessionDO login(String userName, String password) {
        UserDO userDO = userDAO.queryUserDO(userName);
        if (userDO == null||!userDO.getPassword().equals(password)) {
            return null;
        }

        SessionDO sessionDO = sessionDAO.refreshSessionDO(userDO.getId());
        return sessionDO;
    }

    public SessionDO regist(String userName, String password) {
        return null;
    }

    public boolean logout(SessionDO sessionDO) {
        return false;
    }

}
