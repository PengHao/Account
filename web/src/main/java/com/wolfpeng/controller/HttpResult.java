package com.wolfpeng.controller;

import java.util.Map;

import lombok.Data;

/**
 * Created by penghao on 2018/9/5.
 * Copyright © 2017年 penghao. All rights reserved.
 */

@Data
public class HttpResult {
    Map<String, Object> data;
    Integer code;
    Boolean success = true;
    String errorMsg;
}
