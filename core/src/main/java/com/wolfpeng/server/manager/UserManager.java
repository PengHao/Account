package com.wolfpeng.server.manager;

import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.model.UserDO;
/**
 * Created by penghao on 2018/9/4.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public interface UserManager {

    UserDO login(String name, String pwd) throws ProcessException;
}
