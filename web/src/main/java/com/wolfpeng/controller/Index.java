package com.wolfpeng.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wolfpeng.server.netty.MyServer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by penghao on 2017/4/25.
 * Copyright ? 2017年 com.wolfpeng. All rights reserved.
 */

@Controller("HomeIndex")
@RequestMapping("/home")
public class Index implements ApplicationContextAware {

    @RequestMapping(value = "/")
    public String index(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        List<String> beSales = new ArrayList<>();
        beSales.add("123");
        beSales.add("456");
        request.setAttribute("beSale", beSales);
        return "home";
    }
    @RequestMapping(value = "/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("userName");
        String passWord = request.getParameter("passwd");
        Cookie cookie = new Cookie("session_token", "session_token");
        response.addCookie(cookie);
        return "index";
    }

    ApplicationContext applicationContext;

    @Resource(name = "mediaServer")
    MyServer mediaServer;

    @Resource(name = "controllServer")
    MyServer controllServer;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        mediaServer.run();
        controllServer.run();
    }
}