package com.wolfpeng.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Setter;

/**
 * Created by penghao on 2018/9/4.
 * Copyright © 2017年 penghao. All rights reserved.
 */

public class MusicServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlServer.class);

    @Setter
    Integer port;

    ServerBootstrap serverBootstrap = new ServerBootstrap();
    EventLoopGroup boss=new NioEventLoopGroup();
    EventLoopGroup worker=new NioEventLoopGroup();
    private Thread thread;

    public void run() {
        thread = new Thread(() -> {
            start();
        });
        thread.start();
    }

    void start() {

        try {
            serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast("decoder", new ByteToProtocolBufferDecoder());
                        p.addLast("encoder", new ProtocolBufferToByteEncoder());
                        p.addLast(new ProtocolBufferInboundHandler());
                    }
                } );

            ChannelFuture f= serverBootstrap.bind(port).sync();
            if(f.isSuccess()){
                LOGGER.warn("music server start---------------");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void shutDown() {
        try {
            worker.shutdownGracefully().sync();
            boss.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
