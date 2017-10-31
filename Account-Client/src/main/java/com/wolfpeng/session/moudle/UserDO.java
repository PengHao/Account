package com.wolfpeng.session.moudle;

/**
 * Created by penghao on 2017/9/17.
 * Copyright ? 2017年 wolfpeng. All rights reserved.
 */

public class UserDO {
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    Long id;
    String userName;
    String password;
}
