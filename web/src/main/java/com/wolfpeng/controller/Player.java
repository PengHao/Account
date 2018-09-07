package com.wolfpeng.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFormat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wolfpeng.exception.MediaServerException;
import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.media.PlayerCallBack;
import com.wolfpeng.media.WaveHeader;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;
import com.wolfpeng.model.UserDO;
import com.wolfpeng.server.manager.LibraryManager;
import com.wolfpeng.server.manager.PlayerManager;
import com.wolfpeng.server.manager.Session;
import com.wolfpeng.server.manager.SessionManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by penghao on 2018/9/6.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Controller
@RequestMapping("/player")
public class Player extends BaseController {

    @Resource
    PlayerManager playerManager;

    @Resource
    LibraryManager libraryManager;

    @Resource
    SessionManager sessionManager;

    @RequestMapping(value = "/play")
    public String play(HttpServletRequest request) {
        HttpResult httpResult = new HttpResult();
        try {
            Session session = getSession(request);
            Long metaDataId = Long.valueOf(request.getParameter("meta_id"));
            Long deviceId = Long.valueOf(request.getParameter("device_id"));
            playerManager.play(metaDataId, deviceId);
        } catch (ProcessException e) {
            e.printStackTrace();
            httpResult.setSuccess(false);
            httpResult.setCode(e.getErrorCode());
            httpResult.setErrorMsg(e.getErrorMsg());
        } catch (MediaServerException e) {
            e.printStackTrace();
            httpResult.setSuccess(false);
            httpResult.setCode(e.getErrorCode());
            httpResult.setErrorMsg(e.getErrorMessage());
        }
        request.setAttribute("data", JSON.toJSONString(httpResult, SerializerFeature.BrowserCompatible));
        return "json";
    }


    @RequestMapping(value = "/stream")
    public void stream(HttpServletRequest request, HttpServletResponse response) {
        HttpResult httpResult = new HttpResult();
        try {
            Session session = getSession(request);
            Long metaDataId = Long.valueOf(request.getParameter("id"));
            MetadataDO metadataDO = libraryManager.getMetadata(metaDataId);
            if (metadataDO == null) {
                return;
            }
            FileDO file = libraryManager.getFile(metadataDO.getTargetId());
            if (file == null) {
                return;
            }
            response.setContentType("audio/mpeg");
            session.getPlayer().syncPlay(file.getPath(), metadataDO.getStartTime(), metadataDO.getDuration(),
                new PlayerCallBack() {
                    @Override
                    public void onReadData(byte[] data, long len) throws Exception {
                        response.getOutputStream().write(data);
                        response.getOutputStream().flush();
                    }

                    @Override
                    public void onReadFormat(AudioFormat audioFormat, long frameLength) throws Exception {
                        WaveHeader waveHeader = new WaveHeader();
                        waveHeader.setBitsPerSample((short)audioFormat.getSampleSizeInBits());
                        waveHeader.setNumChannels((short)audioFormat.getChannels());
                        waveHeader.setSampleRate((int)audioFormat.getSampleRate());
                        Long duration  = metadataDO.getDuration();
                        long playFrameLength = frameLength;
                        if (duration != null) {
                            playFrameLength = (long)(duration*audioFormat.getFrameRate());
                        }
                        waveHeader.setSubChunk2Size((int)(playFrameLength*audioFormat.getFrameSize()));
                        response.getOutputStream().write(waveHeader.writeHeader());
                        response.getOutputStream().flush();
                    }

                    @Override
                    public void onReadEnd() throws Exception {

                        response.getOutputStream().close();
                    }
                });
        } catch (ProcessException e) {
            e.printStackTrace();
            httpResult.setSuccess(false);
            httpResult.setCode(e.getErrorCode());
            httpResult.setErrorMsg(e.getErrorMsg());
        }
        request.setAttribute("data", JSON.toJSONString(httpResult, SerializerFeature.BrowserCompatible));
    }


    @RequestMapping(value = "/playable_device")
    public String getPlayableDevice(HttpServletRequest request) {
        HttpResult httpResult = new HttpResult();
        try {
            getSession(request);

            List<Map<String, Object>> users = new ArrayList<>();
            for (Session playAbleSession : sessionManager.getPlayableSessions()) {
                Map<String, Object> u = new HashMap<>();
                UserDO user = playAbleSession.getUserDO();
                u.put("uid", user.getId());
                u.put("name", user.getName());
                users.add(u);
            }
            Map<String, Object> rs = new HashMap<>();
            rs.put("devices", users);
            httpResult.setData(rs);

        } catch (ProcessException e) {
            e.printStackTrace();
            httpResult.setSuccess(false);
            httpResult.setCode(e.getErrorCode());
            httpResult.setErrorMsg(e.getErrorMsg());
        }
        request.setAttribute("data", JSON.toJSONString(httpResult, SerializerFeature.BrowserCompatible));
        return "json";
    }
}
