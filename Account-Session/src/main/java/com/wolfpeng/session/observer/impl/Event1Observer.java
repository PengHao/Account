package com.wolfpeng.session.observer.impl;

import com.wolfpeng.session.observer.EventObserver;
import com.wolfpeng.session.observer.ObserveEventProtocol;

/**
 * Created by penghao on 2017/10/16.
 * Copyright ? 2017年 Alibaba. All rights reserved.
 */
public class Event1Observer extends EventObserver {
    @Override
    public void onHandleEvent(ObserveEventProtocol observeEventProtocol) {
        System.out.print("Event1Observer onHandleEvent");

    }
}