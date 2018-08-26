package com.wolfpeng.dao;

import com.wolfpeng.model.UserDO;

/**
 * Created by penghao on 2018/8/24.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public interface UserDAO {
    UserDO queryUserDO(String name, String pwd);

    void inserUserDO(UserDO userDO);

    void updateUserDO(UserDO userDO);

    void deleteUserDO(UserDO userDO);


}
