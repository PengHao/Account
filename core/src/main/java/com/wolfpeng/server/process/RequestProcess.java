package com.wolfpeng.server.process;


import java.util.List;

import javax.annotation.Resource;

import com.google.protobuf.ByteString;
import com.wolfpeng.exception.MediaServerException;
import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.model.CoverDO;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;
import com.wolfpeng.server.manager.LibarayManager;
import com.wolfpeng.server.manager.PlayerManager;
import com.wolfpeng.server.manager.Session;
import com.wolfpeng.server.manager.SessionManager;
import com.wolfpeng.server.protocol.Base.Control;
import com.wolfpeng.server.protocol.Base.Control.Action;
import com.wolfpeng.server.protocol.Base.Device;
import com.wolfpeng.server.protocol.Base.Metadata;
import com.wolfpeng.server.protocol.Base.Target;
import com.wolfpeng.server.protocol.Base.Target.TargetType;
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

    @Resource
    LibarayManager libarayManager;

    @Resource
    PlayerManager playerManager;

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
        Boolean playAble = loginRequest.getPlayAble();
        Channel channel = ctx.channel();
        Session session = null;
        LoginResponse.Builder loginResponse = LoginResponse.newBuilder();
        try {
            session = sessionManager.login(name, pwd, channel);
            session.setPlayAble(playAble);
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
        Channel channel = ctx.channel();
        Session session = sessionManager.getSession(channel);
        long targetID = targetRequest.getTargetId();
        List<FileDO> fileDOs = libarayManager.getSubFileList(targetID);

        TargetResponse.Builder targetResponse = TargetResponse.newBuilder();
        fileDOs.forEach(fileDO -> {
            targetResponse.addTargets(Target.newBuilder()
                .setName(fileDO.getName())
                .setId(fileDO.getId())
                .setPath(fileDO.getPath())
                .setTargetType(TargetType.forNumber(fileDO.getType()))
            );
        });
        Response.Builder response = Response.newBuilder().setTargetResponse(targetResponse);
        session.sendResponse(response);

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

        try {
            playerManager.play(session, metaId, deviceId);
        } catch (MediaServerException e) {
            LOGGER.error("onPlay error, msg = {}, e = {}", e.getErrorMessage(), e);
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
        } catch (ProcessException e) {
            e.printStackTrace();
            bindResponse.setSuccess(false);
        }

        Response.Builder response = Response.newBuilder().setBindResponse(bindResponse);

        Message responseMessage = Message.newBuilder().setResponse(response).build();
        ctx.channel().writeAndFlush(responseMessage);
    }

    @PathMethod(name = "PLAYABLE_DEVICE_REQUEST")
    public void onPlaybleDevice(Request request, ChannelHandlerContext ctx) {
        PlayableDeviceRequest playableDeviceRequest = request.getPlayableDeviceRequest();
        PlayableDeviceResponse.Builder builder = PlayableDeviceResponse.newBuilder();
        Session session = sessionManager.getSession(ctx.channel());
        sessionManager.getPlayableSessions().forEach(e -> {
            if (e.getPlayAble()) {
                Device device = Device.newBuilder()
                    .setId(e.getUserDO().getId())
                    .setName(e.getUserDO().getName())
                    .build();
                builder.addDevices(device);
            }
        });

        Response.Builder response = Response.newBuilder().setPlayableDeviceResponse(builder);
        session.sendResponse(response);
    }

    @PathMethod(name = "CONTROL_REQUEST")
    public void onControl(Request request, ChannelHandlerContext ctx) {
        ControlRequest controlRequest = request.getControlRequest();
        Control control = controlRequest.getControl();
        Session session = sessionManager.getSession(controlRequest.getDeviceId());
        session.sendNotify(Notify.newBuilder().setControl(control));
    }

    @PathMethod(name = "METADATA_REQUEST")
    public void onMetadata(Request request, ChannelHandlerContext ctx) {
        MetadataRequest metadataRequest = request.getMetadataRequest();
        Long targetId = metadataRequest.getTargetId();
        Session session = sessionManager.getSession(ctx.channel());
        List<MetadataDO> metadataDOs = libarayManager.getMetadataFromTarget(targetId);

        MetadataResponse.Builder metadataResponse = MetadataResponse.newBuilder()
            .setTargetId(targetId);
        for (MetadataDO metadataDO : metadataDOs) {
            Metadata.Builder metadata = Metadata.newBuilder();
            metadata.setTargetId(metadataDO.getTargetId());
            metadata.setId(metadataDO.getId());
            if (metadataDO.getStartTime() != null) {
                metadata.setStart(metadataDO.getStartTime());
            }
            if (metadataDO.getDuration() != null) {
                metadata.setDuration(metadataDO.getDuration());
            }
            if (metadataDO.getArtist() != null) {
                metadata.setArtist(metadataDO.getArtist());
            }
            if (metadataDO.getTitle() != null) {
                metadata.setTitle(metadataDO.getTitle());
            }
            if (metadataDO.getAblum() != null) {
                metadata.setAblum(metadataDO.getAblum());
            }
            if (metadataDO.getAblumArtist() != null) {
                metadata.setAblumArtist(metadataDO.getAblumArtist());
            }
            if (metadataDO.getComposer() != null) {
                metadata.setComposer(metadataDO.getComposer());
            }
            if (metadataDO.getDate() != null) {
                metadata.setDate(metadataDO.getDate());
            }
            if (metadataDO.getDiscNumber() != null) {
                metadata.setDiscNumber(metadataDO.getDiscNumber());
            }
            if (metadataDO.getTrackNumber() != null) {
                metadata.setTrackNumber(metadataDO.getTrackNumber());
            }
            if (metadataDO.getCopyRight() != null) {
                metadata.setCopyRight(metadataDO.getCopyRight());
            }
            if (metadataDO.getGenre() != null) {
                metadata.setGenre(metadataDO.getGenre());
            }
            if (metadataDO.getGenreNumber() != null) {
                metadata.setGenreNumber(metadataDO.getGenreNumber());
            }
            if (metadataDO.getOrganization() != null) {
                metadata.setOrganization(metadataDO.getOrganization());
            }
            if (metadataDO.getComment() != null) {
                metadata.setComment(metadataDO.getComment());
            }
            if (metadataDO.getPerformer() != null) {
                metadata.setPerformer(metadataDO.getPerformer());
            }
            if (metadataDO.getMood() != null) {
                metadata.setMood(metadataDO.getMood());
            }
            if (metadataDO.getCoverId() != null) {
                metadata.setPictureId(metadataDO.getCoverId());
            }
            metadataResponse.addMetadatas(metadata);
        }

        Response.Builder response = Response.newBuilder().setMetadataResponse(metadataResponse);
        session.sendResponse(response);
    }

    @PathMethod(name = "COVER_REQUEST")
    public void onCover(Request request, ChannelHandlerContext ctx) {
        CoverRequest coverRequest = request.getCoverRequest();
        Session session = sessionManager.getSession(ctx.channel());
        CoverDO coverDO = libarayManager.getCover(coverRequest.getPictureId());
        CoverResponse.Builder coverResponse = CoverResponse.newBuilder();

        if (coverDO != null) {
            if (coverDO.getData() != null) {
                coverResponse.setPictureData(ByteString.copyFrom(coverDO.getData()));
            }
            coverResponse.setPictureId(coverDO.getId());
        }

        Response.Builder response = Response.newBuilder().setCoverResponse(coverResponse);
        session.sendResponse(response);
    }


}
