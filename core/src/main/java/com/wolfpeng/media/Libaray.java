package com.wolfpeng.media;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.wolfpeng.dao.SystemConfigDAO;
import com.wolfpeng.media.CUEParser.CueFileBean;
import com.wolfpeng.model.CoverDO;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;
import com.wolfpeng.model.SystemConfigDO;
import lombok.Data;
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
    SystemConfigDAO systemConfigDAO;

    SystemConfigDO systemConfigDO;

    @Data
    static public class FileMeta {
        FileDO fileDO;
        List<MetadataDO> metadataDOs;
        CoverDO cover;
        Map<String, FileMeta> subFileMetas;
    }

    public FileMeta scan() {
        systemConfigDO = systemConfigDAO.querySystemConfigDO();

        File file = new File(systemConfigDO.getLibarayPath());
        if (file.exists()) {
            return read(file, null);
        } else {
            return null;
        }
    }

    private FileMeta read(File file, Long parentId) {
        String fileName = file.getName();
        if (fileName.charAt(0) == '.') {
            return null;
        }

        if (file.isDirectory()) {
            return readDictory(file, parentId);
        }
        return readFile(file, parentId);
    }

    private FileMeta readDictory(File file, Long parentId) {
        String absolutePath = file.getAbsolutePath();
        FileDO fileDO = new FileDO();
        fileDO.setPath(absolutePath);
        fileDO.setName(file.getName());
        fileDO.setParentId(parentId);
        fileDO.setType(FileDO.DIC);

        FileMeta fileMeta = new FileMeta();
        fileMeta.setFileDO(fileDO);


        File[] subfiles = file.listFiles();
        if (null != subfiles && subfiles.length > 0) {
            Map<String, FileMeta> submetas = new HashMap<>();
            fileMeta.setSubFileMetas(submetas);
            for (File subfile : subfiles) {
                FileMeta subMeta = read(subfile, fileDO.getId());
                if (subMeta != null) {
                    String fileName = subMeta.getFileDO().getName();
                    FileMeta subFileMeta = submetas.get(fileName);
                    if (subFileMeta == null || (
                        subFileMeta.getMetadataDOs() != null &&
                        subMeta.getMetadataDOs() != null &&
                        subFileMeta.getMetadataDOs().size() < subMeta.getMetadataDOs().size()
                    )) {
                        submetas.put(fileName, subMeta);
                    }
                    continue;
                }
                if (fileMeta.getCover() != null) {
                    continue;
                }
                CoverDO cover = readCover(subfile);
                fileMeta.setCover(cover);
            }
        }
        return fileMeta;

    }

    private CoverDO readCover(File file) {

        String subName = file.getName();
        int idx = subName.lastIndexOf(".");
        if (idx < 0) {
            return null;
        }

        String fileTyle = subName.substring(idx, subName.length());
        if (!".jpg".equals(fileTyle) &&
            !".png".equals(fileTyle) &&
            !".jpeg".equals(fileTyle)&&
            !".bmp".equals(fileTyle)) {
            return null;
        }
        String fileName = subName.substring(0, idx);
        if (!"Cover".equals(fileName) &&
            !"cover".equals(fileName) &&
            !"COVER".equals(fileName)) {
            return null;
        }
        try {
            CoverDO coverDo = new CoverDO();
            coverDo.setPath(file.getAbsolutePath());
            FileInputStream reader = new FileInputStream(file);
            Long fileSize = file.length();
            if (fileSize > 1024*1024) {
                return null;
            }
            byte[] data = new byte[fileSize.intValue()];
            int i = reader.read(data);
            if (i != fileSize.intValue()) {
                return null;
            }
            coverDo.setData(data);
            return coverDo;
        } catch (Exception e) {
            LOGGER.error("read cover error, path = {}, e = {}", file.getAbsoluteFile(), e);
        }
        return null;
    }

    private FileMeta readFile(File file, Long parentId) {
        String absolutePath = file.getAbsolutePath();
        int idx = absolutePath.lastIndexOf(".");
        if (idx < 0) {
            return null;
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
            return null;
        }


        FileDO fileDO = new FileDO();
        fileDO.setPath(absolutePath);
        fileDO.setName(file.getName());
        fileDO.setParentId(parentId);
        fileDO.setType(FileDO.FILE);


        if (fileTyle.equals(".cue")) {
            CueFileBean cueFileBean = CUEParser.parseCueFile(file);
            if (StringUtils.isEmpty(cueFileBean.getFileName())) {
                String errormsg = String.format("Parse cue file error, audiofile not found. cue path = %s", absolutePath);
                LOGGER.error(errormsg);
            }

            String audioFileName = String.format("%s/%s", file.getParent(), cueFileBean.getFileName());
            File audioFile = new File(audioFileName);
            if (audioFile == null || audioFile.exists() == false) {
                return null;
            }
            FileMeta audioFileMeta = readFile(audioFile, parentId);
            if (audioFileMeta == null) {
                return null;
            }
            audioFileMeta.setMetadataDOs(cueFileBean.getSongs());
            return audioFileMeta;
        }

        FileMeta fileMeta = new FileMeta();
        fileMeta.setFileDO(fileDO);
        List<MetadataDO> metadatas = new ArrayList<>();

        MetadataDO metadataDO = new MetadataDO();
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
                fileMeta.setCover(coverDO);
            }
        } catch (Exception e) {
            LOGGER.error("absolutePath = {}, e = {}", absolutePath, e);
        }

        if (StringUtils.isEmpty(metadataDO.getTitle())) {
            metadataDO.setTitle(file.getName());
        }
        metadatas.add(metadataDO);
        fileMeta.setMetadataDOs(metadatas);
        return fileMeta;
    }
}
