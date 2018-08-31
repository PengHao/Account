package com.wolfpeng.server.manager.impl;

import javax.annotation.Resource;

import com.wolfpeng.dao.FileDAO;
import com.wolfpeng.dao.MetadataDAO;
import com.wolfpeng.exception.MediaServerException;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;
import com.wolfpeng.server.manager.PlayerManager;
import com.wolfpeng.server.manager.Session;
import com.wolfpeng.server.manager.SessionManager;
import com.wolfpeng.server.protocol.Base.Control;
import com.wolfpeng.server.protocol.Base.Control.Action;
import com.wolfpeng.server.protocol.NotifyOuterClass.Notify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by penghao on 2018/9/1.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Service
public class PlayerManagerImpl implements PlayerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerManagerImpl.class);

    @Resource
    SessionManager sessionManager;

    @Resource
    MetadataDAO metadataDAO;

    @Resource
    FileDAO fileDAO;

    @Override
    public void play(Session session, Long metaId, Long deviceId) throws MediaServerException {
        Session deviceSession = sessionManager.getSession(deviceId);
        if (deviceSession == null
            || deviceSession.getControllChannel() == null
            || deviceSession.getMusicChannel() == null
            || deviceSession.getPlayAble() == false) {
            LOGGER.error("device not found, deviceId = {}", deviceId);
            throw MediaServerException.builder().errorMessage("device not found").build();
        }
        if (session == deviceSession) {
            //play
            MetadataDO metadataDO = metadataDAO.queryMetadataDO(metaId);
            FileDO fileDO = fileDAO.queryFileDO(metadataDO.getTargetId());
            if (fileDO == null) {
                throw MediaServerException.builder().errorMessage(String.format("file not found, metaId = %d", metaId)).build();
            }
            String filePath = fileDO.getPath();
            session.play(filePath);
        } else {
            Control.Builder control = Control.newBuilder()
                .setCorpus(Action.PLAY)
                .setContent(String.format("%d", metaId));
            deviceSession.sendNotify(Notify.newBuilder().setControl(control));
        }

    }

    @Override
    public void pause(Session session) throws MediaServerException {

    }

    @Override
    public void next(Session session) throws MediaServerException {

    }
}
