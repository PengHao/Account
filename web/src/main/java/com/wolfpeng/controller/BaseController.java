package com.wolfpeng.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.server.manager.Session;
import com.wolfpeng.server.manager.SessionManager;

/**
 * Created by penghao on 2018/9/5.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public class BaseController {

    @Resource
    SessionManager sessionManager;
    final public Session getSession(HttpServletRequest request) throws ProcessException {
        String token = request.getParameter("token");
        Long uid = Long.valueOf(request.getParameter("uid"));
        Session session = sessionManager.getSession(uid);
        if (session == null || session.getToken().equals(token) == false) {
            throw ProcessException.builder().errorMsg("Session not logined").errorCode(-1).build();
        }
        return session;
    }
}
