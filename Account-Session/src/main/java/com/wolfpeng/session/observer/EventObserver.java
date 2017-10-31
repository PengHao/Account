package com.wolfpeng.session.observer;


import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/**
 * Created by penghao on 2017/10/13.
 * Copyright © 2017年 Alibaba. All rights reserved.
 *
 * 观察者基类
 */
abstract public class EventObserver implements InitializingBean {

    /**
     * 事件key
     */
    private Class<ObserveEventProtocol> key;

    /**
     * 观察者优先级，数字越大优先级越高
     */
    private Integer priority = 0;

    private String event;

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return this.event;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }

    @Resource
    EventObserverCenter eventObserverCenter;

    private boolean isClassImplInterface(Class cls) {
        for (Class itf : cls.getInterfaces()) {
            if (itf.equals(ObserveEventProtocol.class)) {
                return true;
            }
        }
        Class superclass = cls.getSuperclass();
        if (!superclass.equals(cls)) {
            return isClassImplInterface(superclass);
        }
        return false;
    }

    @Override
    final public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(event)) {
            throw new Exception("event not set yet");
        }

        Class cls = Class.forName(event);
        if (isClassImplInterface(cls)) {
            eventObserverCenter.addObserver(cls, this);
            return;
        }

        throw new Exception("property class " + event + " has not implement interface ObserveEventProtocol");
    }

    /**
     * 当事件触发的时候，观察者会接收到该消息
     * @param observeEventProtocol 接收到的事件对象
     */
    abstract public void onHandleEvent(ObserveEventProtocol observeEventProtocol);
}
