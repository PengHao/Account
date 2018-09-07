package com.wolfpeng.server.manager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.wolfpeng.dao.CoverDAO;
import com.wolfpeng.dao.FileDAO;
import com.wolfpeng.dao.MetadataDAO;
import com.wolfpeng.media.Libaray;
import com.wolfpeng.media.Libaray.FileMeta;
import com.wolfpeng.model.CoverDO;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;
import com.wolfpeng.server.manager.LibraryManager;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Created by penghao on 2018/8/31.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Service
public class LibraryManagerImpl implements LibraryManager {
    @Resource
    FileDAO fileDAO;

    @Resource
    MetadataDAO metadataDAO;

    @Resource
    CoverDAO coverDAO;

    @Resource
    Libaray libaray;

    @Override
    public List<FileDO> getSubFileList(Long fileId) {
        if (fileId == -1) {
            return fileDAO.queryRootFileDO();
        }
        return fileDAO.querySubFileDO(fileId);
    }

    @Override
    public List<MetadataDO> getMetadataFromTarget(Long targetId) {
        List<MetadataDO> metadataDOs = metadataDAO.queryMetadataDOByTargetId(targetId);
        if (CollectionUtils.isEmpty(metadataDOs)) {
            FileDO fileDO = fileDAO.queryFileDO(targetId);
            MetadataDO metadataDO = new MetadataDO();
            metadataDO.setTitle(fileDO.getName());
            metadataDO.setTargetId(fileDO.getId());
            metadataDOs = new ArrayList<>();
            metadataDOs.add(metadataDO);
        }
        return metadataDOs;
    }

    @Override
    public MetadataDO getMetadata(Long metadataId) {
        return metadataDAO.queryMetadataDO(metadataId);
    }

    @Override
    public FileDO getFile(Long fileId) {
        return fileDAO.queryFileDO(fileId);
    }

    @Override
    public CoverDO getCover(Long coverId) {
        return coverDAO.queryCoverDO(coverId);
    }

    @Override
    public CoverDO getCoverOfFile(Long fileId) {
        return coverDAO.queryCoverDOByFileId(fileId);
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

    @Override
    public void scan() {
        //清空所有数据
        fileDAO.cleanFile();
        coverDAO.cleanCover();
        metadataDAO.cleanMetadata();

        Libaray.FileMeta fileMeta = libaray.scan();
        saveFileMeta(fileMeta, null);
    }

    void saveFileMeta(Libaray.FileMeta fileMeta, Long parentId) {
        if (fileMeta == null) {
            return;
        }
        FileDO fileDO = fileMeta.getFileDO();
        if (fileDO == null) {
            return;
        }
        if (parentId != null) {
            fileDO.setParentId(parentId);
        }
        fileDAO.insertFileDO(fileDO);
        Long fileId = fileDO.getId();

        Long coverId = null;
        CoverDO coverDO = fileMeta.getCover();
        if (coverDO != null) {
            coverDO.setFileId(fileDO.getId());
            coverDAO.insertCoverDO(coverDO);
            coverId = coverDO.getId();
        }
        List<MetadataDO> metadataDOs = fileMeta.getMetadataDOs();
        if (metadataDOs != null) {

            for (MetadataDO metadataDO : metadataDOs) {
                if (coverId != null) {
                    metadataDO.setCoverId(coverId);
                }
                if (StringUtils.isEmpty(metadataDO.getAblum())) {
                    metadataDO.setAblum(fileDO.getName());
                }
                metadataDO.setTargetId(fileId);
                metadataDAO.insertMetadataDO(metadataDO);
            }
        }
        Map<String, FileMeta> subMetas = fileMeta.getSubFileMetas();
        if (subMetas == null) {
            return;
        }
        for (Libaray.FileMeta subMeta : subMetas.values()) {
            saveFileMeta(subMeta, fileId);
        }
    }
}
