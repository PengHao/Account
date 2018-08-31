package com.wolfpeng.exception;

import lombok.Builder;
import lombok.Data;

/**
 * Created by penghao on 2018/8/31.
 * Copyright © 2017年 penghao. All rights reserved.
 */

@Builder
@Data
public class MediaServerException extends Exception {
    String errorMessage;
    Integer errorCode;
}
