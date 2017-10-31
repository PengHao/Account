package com.wolfpeng.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wolfpeng.session.observer.EventObserverCenter;
import com.wolfpeng.session.observer.event.Protocol1;
import com.wolfpeng.session.observer.event.Protocol2;
import com.wolfpeng.session.observer.event.Protocol3;
import com.wolfpeng.session.controll.UserService;
import com.wolfpeng.session.pipeline.Context;
import com.wolfpeng.session.pipeline.Protocol1Pipeline;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by penghao on 2017/4/25.
 * Copyright ? 2017年 wolfpeng. All rights reserved.
 */

@Controller("HomeIndex")
@RequestMapping("/home")
public class Index implements ApplicationContextAware {

    //http://localhost:8080/home/

    @Resource
    EventObserverCenter eventObserverCenter;

    @RequestMapping(value = "/")
    public String index(HttpServletRequest request) {

        Protocol1 protocol1 = new Protocol1();
        Protocol2 protocol2 = new Protocol2();
        Protocol3 protocol3 = new Protocol3();

        eventObserverCenter.activeEvent(protocol1);
        eventObserverCenter.activeEvent(protocol2);
        eventObserverCenter.activeEvent(protocol3);

        request.setAttribute("date", "handle index request");
        Context<Map<String, Object>> context = new Context<>();

        Protocol1Pipeline pipeline = applicationContext.getBean(Protocol1Pipeline.class, context);
        pipeline.getContext();
        System.out.print("123321");


        return "index";


    }

    UserService userService;

    @RequestMapping(value = "/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("userName");
        String passWord = request.getParameter("passwd");
        userService.login(userName, passWord);
        Cookie cookie = new Cookie("session_token", "session_token");
        response.addCookie(cookie);
        return "index";
    }

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}