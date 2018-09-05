package com.wolfpeng.server.manager;

import java.util.List;

import com.wolfpeng.model.CoverDO;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;

/**
 * Created by penghao on 2018/8/31.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public interface LibraryManager {
    List<FileDO> getSubFileList(Long fileId);

    List<MetadataDO> getMetadataFromTarget(Long targetId);

    CoverDO getCover(Long coverId);

    void addFile(FileDO fileDO);

    void addMetadata(MetadataDO metadataDO);

    void addCover(CoverDO coverDO);

    void scan();

}
