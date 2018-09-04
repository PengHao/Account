package com.wolfpeng.server.process;


import java.util.List;

import javax.annotation.Resource;

import com.google.protobuf.ByteString;
import com.wolfpeng.exception.MediaServerException;
import com.wolfpeng.exception.ProcessException;
import com.wolfpeng.model.CoverDO;
import com.wolfpeng.model.FileDO;
import com.wolfpeng.model.MetadataDO;
import com.wolfpeng.model.UserDO;
import com.wolfpeng.server.manager.LibarayManager;
import com.wolfpeng.server.manager.PlayerManager;
import com.wolfpeng.server.manager.Session;
import com.wolfpeng.server.manager.SessionManager;
import com.wolfpeng.server.manager.UserManager;
import com.wolfpeng.server.netty.UserSocketChannel;
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

    @Resource
    UserManager userManager;

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
        UserSocketChannel channel = (UserSocketChannel)ctx.channel();
        Session session = channel.getSession();
        session.setPlayAble(playAble);

        LoginResponse.Builder loginResponse = LoginResponse.newBuilder();
        try {
            UserDO userDO = userManager.login(name, pwd);
            sessionManager.addSession(session, userDO);
            channel.parent().bind(userDO.getId(), channel);
            loginResponse.setUid(userDO.getId());
        } catch (ProcessException e) {
            loginResponse.setUid(-1);
            loginResponse.setMessage(e.getErrorMsg());
        }
        Response.Builder response = Response.newBuilder().setLoginResponse(loginResponse);
        Message responseMessage = Message.newBuilder().setResponse(response).build();
        channel.writeAndFlush(responseMessage);
    }

    @PathMethod(name = "BIND_REQUEST")
    public void onBind(Request request, ChannelHandlerContext ctx) {
        BindRequest bindRequest = request.getBindRequest();
        BindResponse.Builder bindResponse = BindResponse.newBuilder();
        try {
            sessionManager.bindMusicChannel(bindRequest.getUid(), ctx.channel());
            bindResponse.setSuccess(true);
        } catch (ProcessException e) {
            e.printStackTrace();
            bindResponse.setSuccess(false);
        }

        Response.Builder response = Response.newBuilder().setBindResponse(bindResponse);

        Message responseMessage = Message.newBuilder().setResponse(response).build();
        ctx.channel().writeAndFlush(responseMessage);
    }

    @PathMethod(name = "TARGET_REQUEST")
    public void onTarget(Request request, ChannelHandlerContext ctx) {
        TargetRequest targetRequest = request.getTargetRequest();
        UserSocketChannel channel = (UserSocketChannel)ctx.channel();
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
        channel.sendResponse(response);

    }

    @PathMethod(name = "HEART_BEAT_REQUEST")
    public void onHeartBeat(Request request, ChannelHandlerContext ctx) {
        HeartBeatRequest heartBeatRequest = request.getHeartBeatRequest();
        UserSocketChannel channel = (UserSocketChannel)ctx.channel();

        HeartBeatResponse.Builder builder = HeartBeatResponse.newBuilder();
        Response.Builder response = Response.newBuilder().setHeartBeatResponse(builder);
        channel.sendResponse(response);
    }

    @PathMethod(name = "PLAY_REQUEST")
    public void onPlay(Request request, ChannelHandlerContext ctx) {
        PlayRequest playRequest = request.getPlayRequest();
        Long deviceId = playRequest.getDeviceId();
        Long metaId = playRequest.getMetadataId();
        UserSocketChannel channel = (UserSocketChannel)ctx.channel();
        PlayResponse.Builder playResponse = PlayResponse.newBuilder();

        try {
            UserSocketChannel targetChannel = channel.parent().getChannel(deviceId);
            if (targetChannel == null) {
                LOGGER.error("device not found, deviceId = {}", deviceId);
                throw MediaServerException.builder().errorMessage("device not found").build();
            }

            if (channel == targetChannel) {
                playerManager.play(metaId, deviceId);
            } else {
                Control.Builder control = Control.newBuilder()
                    .setCorpus(Action.PLAY)
                    .setContent(String.format("%d", metaId));
                targetChannel.sendNotify(Notify.newBuilder().setControl(control));
            }
        } catch (MediaServerException e) {
            LOGGER.error("onPlay error, msg = {}, e = {}", e.getErrorMessage(), e);
        }
        Response.Builder response = Response.newBuilder().setPlayResponse(playResponse);
        channel.sendResponse(response);
    }


    @PathMethod(name = "PLAYABLE_DEVICE_REQUEST")
    public void onPlaybleDevice(Request request, ChannelHandlerContext ctx) {
        PlayableDeviceRequest playableDeviceRequest = request.getPlayableDeviceRequest();
        PlayableDeviceResponse.Builder builder = PlayableDeviceResponse.newBuilder();
        UserSocketChannel channel = (UserSocketChannel)ctx.channel();
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
        channel.sendResponse(response);
    }

    @PathMethod(name = "CONTROL_REQUEST")
    public void onControl(Request request, ChannelHandlerContext ctx) {
        ControlRequest controlRequest = request.getControlRequest();
        Control control = controlRequest.getControl();
        UserSocketChannel channel = (UserSocketChannel)ctx.channel();
        UserSocketChannel deviceChannel = channel.parent().getChannel(controlRequest.getDeviceId());
        if (deviceChannel != null) {
            deviceChannel.sendNotify(Notify.newBuilder().setControl(control));
        }
    }

    @PathMethod(name = "METADATA_REQUEST")
    public void onMetadata(Request request, ChannelHandlerContext ctx) {
        MetadataRequest metadataRequest = request.getMetadataRequest();
        Long targetId = metadataRequest.getTargetId();
        List<MetadataDO> metadataDOs = libarayManager.getMetadataFromTarget(targetId);
        UserSocketChannel channel = (UserSocketChannel)ctx.channel();

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
        channel.sendResponse(response);
    }

    @PathMethod(name = "COVER_REQUEST")
    public void onCover(Request request, ChannelHandlerContext ctx) {
        CoverRequest coverRequest = request.getCoverRequest();
        CoverDO coverDO = libarayManager.getCover(coverRequest.getPictureId());
        CoverResponse.Builder coverResponse = CoverResponse.newBuilder();
        UserSocketChannel channel = (UserSocketChannel)ctx.channel();

        if (coverDO != null) {
            if (coverDO.getData() != null) {
                coverResponse.setPictureData(ByteString.copyFrom(coverDO.getData()));
            }
            coverResponse.setPictureId(coverDO.getId());
        }

        Response.Builder response = Response.newBuilder().setCoverResponse(coverResponse);
        channel.sendResponse(response);
    }


}
