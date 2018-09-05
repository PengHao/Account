package com.wolfpeng.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;
import com.wolfpeng.model.UserDO;
import com.wolfpeng.server.manager.LibarayManager;
import com.wolfpeng.server.manager.Session;
import com.wolfpeng.server.manager.SessionManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by penghao on 2018/9/5.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Controller
@RequestMapping("/library")
public class Library extends BaseController {

    @Resource
    LibarayManager libarayManager;

    @Resource
    SessionManager sessionManager;

    @RequestMapping(value = "/browser")
    public String browser(HttpServletRequest request) {
        HttpResult httpResult = new HttpResult();
        try {
            Session session = getSession(request);
            Long targetId = Long.valueOf(request.getParameter("target_id"));

            List<FileDO> fileDOs = libarayManager.getSubFileList(targetId);
            Map<String, Object> data = new HashMap<>(4);
            data.put("files", fileDOs);
            httpResult.setData(data);
        } catch (ProcessException e) {
            e.printStackTrace();
            httpResult.setSuccess(false);
            httpResult.setCode(e.getErrorCode());
            httpResult.setErrorMsg(e.getErrorMsg());
        }
        request.setAttribute("data", JSON.toJSONString(httpResult));
        return "json";
    }
}
