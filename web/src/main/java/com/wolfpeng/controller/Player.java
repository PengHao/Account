package com.wolfpeng.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import com.wolfpeng.exception.MediaServerException;
import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.model.FileDO;
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
