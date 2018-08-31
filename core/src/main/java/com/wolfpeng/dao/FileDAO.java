package com.wolfpeng.dao;

import java.util.List;

import com.wolfpeng.model.FileDO;

/**
 * Created by penghao on 2018/8/30.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public interface FileDAO {
    FileDO queryFileDO(Long id);

    List<FileDO> querySubFileDO(Long id);

    List<FileDO> queryRootFileDO();



    void insertFileDO(FileDO fileDO);

    void updateFileDO(FileDO fileDO);

    void deleteFileDO(FileDO fileDO);

    void cleanFile();
}
