package com.wolfpeng.session.controll;

import javax.security.auth.login.AccountException;

import com.wolfpeng.session.ResultDTO;
import com.wolfpeng.session.manager.AccountManager;
import com.wolfpeng.session.moudle.LobbyDO;
import com.wolfpeng.session.moudle.SessionDO;

/**
 * Created by penghao on 2017/9/15.
 * Copyright ? 2017年 wolfpeng. All rights reserved.
 */
public class AccountServiceImp implements AccountService {
    AccountManager accountManager;

    @Override
    public ResultDTO<SessionDO> login(String userName, String password) {
        ResultDTO<SessionDO> resultDTO = new ResultDTO<>();
        try {
            SessionDO sessionDO = accountManager.login(userName, password);
            resultDTO.setResult(sessionDO);
            resultDTO.setSuccess(false);
            return resultDTO;
        } catch (AccountException e) {
            e.printStackTrace();
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(e.getMessage());
        }
        return resultDTO;
    }

    @Override
    public ResultDTO<Boolean> regist(String userName, String password) {
        ResultDTO<Boolean> resultDTO = new ResultDTO<>();
        try {
            Boolean rs = accountManager.regist(userName, password);
            resultDTO.setSuccess(true);
            resultDTO.setResult(rs);
        } catch (AccountException e) {
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(e.getMessage());
        }
        return resultDTO;
    }

    @Override
    public ResultDTO<Boolean> logout(SessionDO sessionDO) {
        ResultDTO<Boolean> resultDTO = new ResultDTO<>();
        try {
            Boolean rs = accountManager.logout(sessionDO);
            resultDTO.setSuccess(true);
            resultDTO.setResult(rs);
        } catch (AccountException e) {
            e.printStackTrace();
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(e.getMessage());
        }
        return resultDTO;
    }

    @Override
    public ResultDTO<LobbyDO> enterRoom(SessionDO sessionDO) {
        ResultDTO<LobbyDO> resultDTO = new ResultDTO<>();
        try {
            LobbyDO rs = accountManager.enter(sessionDO);
            resultDTO.setSuccess(true);
            resultDTO.setResult(rs);
        } catch (AccountException e) {
            e.printStackTrace();
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(e.getMessage());
        }
        return resultDTO;
    }

}
