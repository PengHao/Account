package com.wolfpeng.server.process;


import javax.annotation.Resource;

import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.server.manager.Session;
import com.wolfpeng.server.manager.SessionManager;
import com.wolfpeng.server.protocol.Base.Control;
import com.wolfpeng.server.protocol.Base.Control.Action;
import com.wolfpeng.server.protocol.Base.Device;
import com.wolfpeng.server.protocol.MessageOuterClass.*;
import com.wolfpeng.server.protocol.NotifyOuterClass.Notify;
import com.wolfpeng.server.protocol.RequestOuterClass.*;
import com.wolfpeng.server.protocol.ResponseOuterClass.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by penghao on 2018/8/24.
 * Copyright © 2017年 penghao. All rights reserved.
 */
@Component("REQUEST")

@Slf4j
public class RequestProcess extends BaseProcess {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestProcess.class);

    @Resource
    SessionManager sessionManager;

    @Override
    String getName(Message message) {
        Request request = message.getRequest();
        return request.getPackageCase().name();
    }

    @Override
    Object unPackage(Message message) {
        return message.getRequest();
    }



    @PathMethod(name = "LOGIN_REQUEST")
    public void onLogin(Request request, ChannelHandlerContext ctx) {
        LoginRequest loginRequest = request.getLoginRequest();
        String name = loginRequest.getUserName();
        String pwd = loginRequest.getPassWord();
        Channel channel = ctx.channel();
        Session session = null;
        LoginResponse.Builder loginResponse = LoginResponse.newBuilder();
        try {
            session = sessionManager.login(name, pwd, channel);
            loginResponse.setUid(session.getUserDO().getId());
        } catch (ProcessException e) {
            loginResponse.setUid(-1);
            loginResponse.setMessage(e.getErrorMsg());
        }

        Response.Builder response = Response.newBuilder().setLoginResponse(loginResponse);
        session.sendResponse(response);
    }


    @PathMethod(name = "TARGET_REQUEST")
    public void onTarget(Request request, ChannelHandlerContext ctx) {
        TargetRequest targetRequest = request.getTargetRequest();

    }

    @PathMethod(name = "HEART_BEAT_REQUEST")
    public void onHeartBeat(Request request, ChannelHandlerContext ctx) {
        HeartBeatRequest heartBeatRequest = request.getHeartBeatRequest();
        Channel channel = ctx.channel();
        Session session = sessionManager.getSession(channel);

        HeartBeatResponse.Builder builder = HeartBeatResponse.newBuilder();
        Response.Builder response = Response.newBuilder().setHeartBeatResponse(builder);
        session.sendResponse(response);
    }

    @PathMethod(name = "PLAY_REQUEST")
    public void onPlay(Request request, ChannelHandlerContext ctx) {
        PlayRequest playRequest = request.getPlayRequest();
        Long deviceId = playRequest.getDeviceId();
        Long metaId = playRequest.getMetadataId();
        Channel channel = ctx.channel();

        Session session = sessionManager.getSession(channel);

        PlayResponse.Builder playResponse = PlayResponse.newBuilder();
        Session deviceSession = sessionManager.getSession(deviceId);
        if (deviceSession == null
            || deviceSession.getControllChannel() == null
            || deviceSession.getMusicChannel() == null) {
            LOGGER.error("device not found, deviceId = {}", deviceId);
            playResponse.setSuccess(false);
            playResponse.setMessage("device not found");

        } else {
            if (session == deviceSession) {
                //play
                session.play(metaId);
            } else {
                Control.Builder control = Control.newBuilder()
                    .setCorpus(Action.PLAY)
                    .setContent(String.format("%d", metaId));
                deviceSession.sendNotify(Notify.newBuilder().setControl(control));
            }
            playResponse.setSuccess(true);
        }
        Response.Builder response = Response.newBuilder().setPlayResponse(playResponse);
        session.sendResponse(response);
    }

    @PathMethod(name = "BIND_REQUEST")
    public void onBind(Request request, ChannelHandlerContext ctx) {
        BindRequest bindRequest = request.getBindRequest();
        bindRequest.getUid();

        BindResponse.Builder bindResponse = BindResponse.newBuilder();
        Session session = null;
        try {
            session = sessionManager.bind(bindRequest.getUid(), ctx.channel());
            bindResponse.setSuccess(true);
            session.play(1L);
        } catch (ProcessException e) {
            e.printStackTrace();
            bindResponse.setSuccess(false);
        }

        Response.Builder response = Response.newBuilder().setBindResponse(bindResponse);
        session.sendResponse(response);
    }

    @PathMethod(name = "PLAYABLE_DEVICE_REQUEST")
    public void onPlaybleDevice(Request request, ChannelHandlerContext ctx) {
        PlayableDeviceRequest playableDeviceRequest = request.getPlayableDeviceRequest();
        PlayableDeviceResponse.Builder builder = PlayableDeviceResponse.newBuilder();
        Session session = sessionManager.getSession(ctx.channel());

        sessionManager.getPlayableSessions().forEach(e -> {
            Device device = Device.newBuilder()
                .setId(e.getUserDO().getId())
                .setName(e.getUserDO().getName())
                .build();
            builder.addDevices(device);
        });

        Response.Builder response = Response.newBuilder().setPlayableDeviceResponse(builder);
        session.sendResponse(response);
    }

    @PathMethod(name = "CONTROL_REQUEST")
    public void onControl(Request request, ChannelHandlerContext ctx) {
        ControlRequest controlRequest = request.getControlRequest();

        LoginResponse.Builder loginResponse = LoginResponse.newBuilder().setUid(1);
        Response.Builder response = Response.newBuilder().setLoginResponse(loginResponse);
    }

    @PathMethod(name = "METADATA_REQUEST")
    public void onMetadata(Request request, ChannelHandlerContext ctx) {
        MetadataRequest metadataRequest = request.getMetadataRequest();

        LoginResponse.Builder loginResponse = LoginResponse.newBuilder().setUid(1);
        Response.Builder response = Response.newBuilder().setLoginResponse(loginResponse);
    }

    @PathMethod(name = "COVER_REQUEST")
    public void onCover(Request request, ChannelHandlerContext ctx) {
        CoverRequest coverRequest = request.getCoverRequest();

        LoginResponse.Builder loginResponse = LoginResponse.newBuilder().setUid(1);
        Response.Builder response = Response.newBuilder().setLoginResponse(loginResponse);
    }


}
