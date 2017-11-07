package com.wolfpeng.session.moudle;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 *
 * @author penghao
 * @date 2017/9/15
 * Copyright ? 2017年 wolfpeng. All rights reserved.
 */
@Data
public class SessionDO implements Serializable {
    private static final long serialVersionUID = 6043026017274273971L;
    Long id;
    String token;
    Date exprie;
}
