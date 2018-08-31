package com.wolfpeng.media;

import java.io.File;

import javax.annotation.Resource;

import com.wolfpeng.dao.CoverDAO;
import com.wolfpeng.dao.FileDAO;
import com.wolfpeng.dao.MetadataDAO;
import com.wolfpeng.model.CoverDO;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;
import com.wolfpeng.model.SystemConfigDO;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by penghao on 2018/8/31.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Service
public class Libaray {
    private static final Logger LOGGER = LoggerFactory.getLogger(Libaray.class);


    @Resource
    FileDAO fileDao;

    @Resource
    CoverDAO coverDAO;

    @Resource
    MetadataDAO metadataDAO;

    SystemConfigDO systemConfigDO = SystemConfigDO.defaultSystemConfigDO;


    public void scan() {
        File file = new File(systemConfigDO.getLibarayPath());
        if (file.exists()) {
            readFile(file, null);
        } else {
            System.out.println("文件不存在!");
        }
    }

    private void readFile(File file, Long parentId) {
        String absolutePath = file.getAbsolutePath();
        String fileName = file.getName();
        if (fileName.charAt(0) == '.') {
            return;
        }
        FileDO fileDO = new FileDO();
        fileDO.setPath(absolutePath);
        fileDO.setName(file.getName());
        fileDO.setParentId(parentId);
        fileDO.setType(file.isDirectory() ? FileDO.DIC : FileDO.FILE);
        fileDao.insertFileDO(fileDO);

        if (file.isDirectory()) {
            File[] subfiles = file.listFiles();
            if (null == subfiles || subfiles.length == 0) {
                return;
            } else {
                for (File subfile : subfiles) {
                    readFile(subfile, fileDO.getId());
                }
            }
        }

        int idx = absolutePath.lastIndexOf(".");
        if (idx < 0) {
            return;
        }

        String fileTyle = absolutePath.substring(idx, absolutePath.length());
        Boolean isMatch = false;
        for ( String type : systemConfigDO.getFileTypes().split(",")) {
            if (type.trim().equals(fileTyle)) {
                isMatch = true;
                break;
            }
        }
        if (!isMatch) {
            return;
        }

        MetadataDO metadataDO = new MetadataDO();
        metadataDO.setTargetId(fileDO.getId());
        try {
            AudioFile f = AudioFileIO.read(file);
            Tag tag = f.getTag();

            metadataDO.setArtist(tag.getFirst(FieldKey.ARTIST));
            metadataDO.setTitle(tag.getFirst(FieldKey.TITLE));
            metadataDO.setAblum(tag.getFirst(FieldKey.ALBUM));
            metadataDO.setAblumArtist(tag.getFirst(FieldKey.ALBUM_ARTIST));
            metadataDO.setComposer(tag.getFirst(FieldKey.COMPOSER));
            metadataDO.setDate(tag.getFirst(FieldKey.YEAR));
            metadataDO.setDiscNumber(tag.getFirst(FieldKey.DISC_NO));
            metadataDO.setTrackNumber(tag.getFirst(FieldKey.TRACK));
            //.setCopyRight(tag.getFirst(FieldKey.))
            metadataDO.setGenre(tag.getFirst(FieldKey.GENRE));
            //.setGenreNumber(tag.getFirst(FieldKey.CONDUCTOR));
            //.setOrganization(tag.getFirst(FieldKey.CONDUCTOR));
            metadataDO.setComment(tag.getFirst(FieldKey.COMMENT));
            //.setPerformer();
            metadataDO.setMood(tag.getFirst(FieldKey.MOOD));
            //metadataDO.setDuration(f.getAudioHeader().getTrackLength());

            Artwork artwork = tag.getFirstArtwork();
            if (artwork != null) {
                CoverDO coverDO = new CoverDO();
                coverDO.setData(artwork.getBinaryData());
                coverDO.setPath(artwork.getImageUrl());
                coverDAO.insertCoverDO(coverDO);
                metadataDO.setCoverId(coverDO.getId());
            }

        } catch (Exception e) {
            LOGGER.error("absolutePath = {}, e = {}", absolutePath, e);
        }
        if (StringUtils.isEmpty(metadataDO.getTitle())) {
            metadataDO.setTitle(fileName);
        }
        metadataDAO.insertMetadataDO(metadataDO);
    }
}
