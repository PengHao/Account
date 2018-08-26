package com.wolfpeng.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by penghao on 2018/8/24.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Builder
public class ProcessException extends Exception {
    @Setter @Getter
    int errorCode = 0;

    @Setter @Getter
    String errorMsg = null;

}
