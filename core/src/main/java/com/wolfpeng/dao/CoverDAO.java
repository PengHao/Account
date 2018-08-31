package com.wolfpeng.dao;

import com.wolfpeng.model.CoverDO;
import com.wolfpeng.model.UserDO;

/**
 * Created by penghao on 2018/8/30.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public interface CoverDAO  {

    CoverDO queryCoverDO(Long id);

    void insertCoverDO(CoverDO userDO);

    void updateCoverDO(CoverDO userDO);

    void deleteCoverDO(UserDO userDO);

    void cleanCover();

}
