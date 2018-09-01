package com.wolfpeng.dao;

import com.wolfpeng.model.SystemConfigDO;

/**
 * Created by penghao on 2018/9/1.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public interface SystemConfigDAO {

    SystemConfigDO querySystemConfigDO();

    void insertSystemConfigDO(SystemConfigDO systemConfigDO);

    void updateSystemConfigDO(SystemConfigDO systemConfigDO);
}
