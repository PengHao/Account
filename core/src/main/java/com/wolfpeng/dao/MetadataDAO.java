package com.wolfpeng.dao;

import java.util.List;

import com.wolfpeng.model.MetadataDO;

/**
 * Created by penghao on 2018/8/30.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public interface MetadataDAO {

    MetadataDO queryMetadataDO(Long id);

    List<MetadataDO> queryMetadataDOByTargetId(Long targetId);

    List<MetadataDO> queryAblume();

    void insertMetadataDO(MetadataDO userDO);

    void updateMetadataDO(MetadataDO userDO);

    void deleteMetadataDO(MetadataDO userDO);

    void cleanMetadata();
}
