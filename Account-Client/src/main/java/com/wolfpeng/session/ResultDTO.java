package com.wolfpeng.session;
import java.io.Serializable;

import lombok.Data;

/**
 * Created by penghao on 2017/11/6.
 * Copyright © 2017年 Alibaba. All rights reserved.
 */
@Data
public class ResultDTO<T> implements Serializable {

    private static final long serialVersionUID = 7224878114955535047L;
    private T result;
    private Boolean success;
    private String errorMsg;

    public static ResultDTO BuildFaild(String errorMsg) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setSuccess(false);
        resultDTO.setErrorMsg(errorMsg);
        return resultDTO;
    }

}
