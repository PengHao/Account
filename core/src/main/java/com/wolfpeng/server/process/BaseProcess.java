package com.wolfpeng.server.process;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.wolfpeng.server.protocol.MessageOuterClass.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/**
 * Created by penghao on 2018/8/24.
 * Copyright © 2017年 penghao. All rights reserved.
 */
public abstract class BaseProcess implements InitializingBean {
    Map<String, Method> methodMap = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProcess.class);

    @Override
    public void afterPropertiesSet() throws Exception {

        for (Method method : this.getClass().getMethods()) {
            PathMethod pathMethod = method.getAnnotation(PathMethod.class);
            if (pathMethod == null) {
                continue;
            }
            methodMap.put(pathMethod.name(), method);
        }
    }

    public void excute(Message message, ChannelHandlerContext ctx) {
        String name = getName(message);
        if (StringUtils.isEmpty(name)) {
            return;
        }
        Method method = methodMap.get(name);
        if (method == null) {
            return;
        }
        try {
            method.invoke(this, unPackage(message), ctx);
        } catch (IllegalAccessException e) {
            LOGGER.error("invoke function IllegalAccessException, name={}", name);
        } catch (InvocationTargetException e) {
            LOGGER.error("invoke function InvocationTargetException, name={}", name);
        }
    }

    abstract String getName(Message message);
    abstract Object unPackage(Message message);
}
