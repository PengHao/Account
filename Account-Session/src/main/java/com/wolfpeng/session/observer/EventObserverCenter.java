package com.wolfpeng.session.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.CollectionUtils;

/**
 * Created by penghao on 2017/10/12.
 * Copyright © 2017年 Alibaba. All rights reserved.
 *
 * 事件观察者的注册和分发中心
 */
public class EventObserverCenter {

    HashMap<Class<ObserveEventProtocol>, List<EventObserver>> observerHashMap = new HashMap<>(50);

    static private EventObserverCenter Singleton;
    /**
     * @param observeEventProtocol 发送的事件对象
     */
    public void activeEvent(ObserveEventProtocol observeEventProtocol) {
        List<EventObserver> eventObservers = observerHashMap.get(observeEventProtocol.getClass());
        if (CollectionUtils.isEmpty(eventObservers)) {
            return;
        }
        for (EventObserver eventObserver : eventObservers) {
            eventObserver.onHandleEvent(observeEventProtocol);
        }
    }

    /**
     * @param key       要监听的事件key
     * @param eventObserver  监听对象
     */
    public void addObserver(Class<ObserveEventProtocol> key, EventObserver eventObserver) {
        List<EventObserver> listeners = observerHashMap.get(key);

        if (CollectionUtils.isEmpty(listeners)) {
            listeners = new ArrayList();
            observerHashMap.put(key, listeners);
        }
        listeners.add(eventObserver);
        listeners.sort((o1, o2) -> o1.getPriority() - o2.getPriority());
    }
}
