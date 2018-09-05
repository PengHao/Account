package com.wolfpeng.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSON;
import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.model.UserDO;
import com.wolfpeng.server.manager.Session;
import com.wolfpeng.server.manager.SessionManager;
import com.wolfpeng.server.manager.UserManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by penghao on 2018/9/4.
 * Copyright © 2017年 penghao. All rights reserved.
 */

@Controller
@RequestMapping("/account")
public class Account {
    @Resource
    UserManager userManager;

    @Resource
    SessionManager sessionManager;
    @RequestMapping(value = "/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("user_name");
        String pwd = request.getParameter("pwd");
        Boolean playAble = Boolean.valueOf(request.getParameter("playable"));

        HttpResult httpResult = new HttpResult();
        try {
            UserDO userDO = userManager.login(userName, pwd);
            Session session = sessionManager.getSession(userDO.getId());
            if (session == null) {
                session = new Session();
            }
            session.setUserDO(userDO);
            session.setPlayAble(playAble);
            sessionManager.addSession(session, userDO);

            Long uid = userDO.getId();
            String token = session.getToken();
            Map<String, Object> jsonObject = new HashMap<>(4);
            jsonObject.put("uid", uid);
            jsonObject.put("token", token);
            httpResult.setData(jsonObject);
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
