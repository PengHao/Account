package com.wolfpeng.dao;

import com.wolfpeng.model.MetadataDO;

/**
 * Created by penghao on 2018/8/30.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public interface MetadataDAO {

    MetadataDO queryMetadataDO(Long id);

    MetadataDO queryMetadataDOByTargetId(Long targetId);

    void insertMetadataDO(MetadataDO userDO);

    void updateMetadataDO(MetadataDO userDO);

    void deleteMetadataDO(MetadataDO userDO);
}
