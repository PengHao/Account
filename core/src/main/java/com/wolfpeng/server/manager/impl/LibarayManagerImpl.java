package com.wolfpeng.server.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import com.wolfpeng.dao.CoverDAO;
import com.wolfpeng.dao.FileDAO;
import com.wolfpeng.dao.MetadataDAO;
import com.wolfpeng.model.CoverDO;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;
import com.wolfpeng.server.manager.LibarayManager;
import org.springframework.stereotype.Service;

/**
 * Created by penghao on 2018/8/31.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Service
public class LibarayManagerImpl implements LibarayManager {
    @Resource
    FileDAO fileDAO;

    @Resource
    MetadataDAO metadataDAO;

    @Resource
    CoverDAO coverDAO;
    @Override
    public List<FileDO> getSubFileList(Long fileId) {
        if (fileId == -1) {
            return fileDAO.queryRootFileDO();
        }
        return fileDAO.querySubFileDO(fileId);
    }

    @Override
    public MetadataDO getMetadataFromTarget(Long targetId) {
        MetadataDO metadataDO = metadataDAO.queryMetadataDOByTargetId(targetId);
        if (metadataDO == null) {
            FileDO fileDO = fileDAO.queryFileDO(targetId);
            metadataDO = new MetadataDO();
            metadataDO.setTitle(fileDO.getName());
            metadataDO.setTargetId(fileDO.getId());
        }
        return metadataDO;
    }

    @Override
    public CoverDO getCover(Long coverId) {
        return coverDAO.queryCoverDO(coverId);
    }

    @Override
    public void addFile(FileDO fileDO) {
        fileDAO.insertFileDO(fileDO);
    }

    @Override
    public void addMetadata(MetadataDO metadataDO) {
        metadataDAO.insertMetadataDO(metadataDO);
    }

    @Override
    public void addCover(CoverDO coverDO) {
        coverDAO.insertCoverDO(coverDO);
    }
}
