package com.wolfpeng.session.controll;

import com.wolfpeng.session.ResultDTO;
import com.wolfpeng.session.moudle.RoomDO;
import com.wolfpeng.session.moudle.SessionDO;

/**
 * Created by penghao on 2017/9/15.
 * Copyright ? 2017年 wolfpeng. All rights reserved.
 */
public interface AccountService {
    ResultDTO<SessionDO> login(String userName, String password);
    ResultDTO<Boolean> regist(String userName, String password);
    ResultDTO<Boolean> logout(SessionDO sessionDO);
    ResultDTO<RoomDO> enterRoom(SessionDO sessionDO);
}
