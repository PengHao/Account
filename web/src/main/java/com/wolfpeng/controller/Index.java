package com.wolfpeng.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.server.manager.LibarayManager;
import com.wolfpeng.server.manager.SessionManager;
import com.wolfpeng.server.netty.ControlServer;
import com.wolfpeng.server.netty.MusicServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by penghao on 2017/4/25.
 * Copyright ? 2017年 com.wolfpeng. All rights reserved.
 */

@Controller("HomeIndex")
@RequestMapping("/home")
public class Index {

    @Resource
    SessionManager sessionManagerl;

    @Resource(name = "mediaServer")
    MusicServer mediaServer;

    @Resource(name = "controllServer")
    ControlServer controlServer;

    @Resource
    LibarayManager libarayManager;

    @RequestMapping(value = "/")
    public String index(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        List<String> beSales = new ArrayList<>();
        beSales.add("123");
        beSales.add("456");
        request.setAttribute("beSale", beSales);
        return "home";
    }

    @RequestMapping(value = "/scan")
    public String scan(HttpServletRequest request, HttpServletResponse response) {

        libarayManager.scan();
        return "index";
    }

    @RequestMapping(value = "/register")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("userName");
        String passWord = request.getParameter("passwd");
        try {
            sessionManagerl.register(userName, passWord);
        } catch (ProcessException e) {

        }
        return "index";
    }

    @RequestMapping(value = "/start")
    public String start(HttpServletRequest request, HttpServletResponse response) {
        mediaServer.run();
        controlServer.run();
        return "index";
    }

}