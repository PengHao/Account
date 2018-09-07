package com.wolfpeng.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.model.CoverDO;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;
import com.wolfpeng.server.manager.LibraryManager;
import com.wolfpeng.server.manager.Session;
import com.wolfpeng.server.manager.SessionManager;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

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
    LibraryManager libraryManager;

    @Resource
    SessionManager sessionManager;

    @RequestMapping(value = "/browser")
    public String browser(HttpServletRequest request) {
        HttpResult httpResult = new HttpResult();
        try {
            Session session = getSession(request);
            Long targetId = Long.valueOf(request.getParameter("target_id"));
            List<FileDO> fileDOs = libraryManager.getSubFileList(targetId);

            Map<String, Object> data = new HashMap<>(4);
            data.put("files", fileDOs);
            httpResult.setData(data);
        } catch (ProcessException e) {
            e.printStackTrace();
            httpResult.setSuccess(false);
            httpResult.setCode(e.getErrorCode());
            httpResult.setErrorMsg(e.getErrorMsg());
        }
        request.setAttribute("data", JSON.toJSONString(httpResult, SerializerFeature.BrowserCompatible));
        return "json";
    }


    @RequestMapping(value = "/cover")
    public void cover(HttpServletRequest request, HttpServletResponse response) {
        Long targetId = Long.valueOf(request.getParameter("target_id"));
        CoverDO coverDO = libraryManager.getCoverOfFile(targetId);
        if (coverDO == null) {
            return;
        }
        response.setContentType("image/png");
        response.setContentLength(coverDO.getData().length);
        try {
            response.getOutputStream().write(coverDO.getData());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/metadata")
    public String metadata(HttpServletRequest request) {
        HttpResult httpResult = new HttpResult();
        try {
            Session session = getSession(request);
            Long targetId = Long.valueOf(request.getParameter("target_id"));
            Map<String, Object> data = new HashMap<>(4);
            data.put("metadatas", libraryManager.getMetadataFromTarget(targetId));
            httpResult.setData(data);
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
