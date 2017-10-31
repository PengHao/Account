package com.wolfpeng.session.pipeline;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by penghao on 2017/10/30.
 * Copyright ? 2017年 Alibaba. All rights reserved.
 */
@Component
@Scope("prototype")
public class Protocol1Pipeline {

    Context context;

    public Context getContext() {
        return context;
    }

    public Protocol1Pipeline() {
        System.out.print("123");
    }

    private Protocol1Pipeline(Context context) {
        this.context = context;
        System.out.print("321");
    }


}
