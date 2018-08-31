package com.wolfpeng.server.netty;

import com.wolfpeng.model.SystemConfigDO;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 *
 * @author penghao
 * @date 2018/8/23
 * Copyright © 2017年 penghao. All rights reserved.
 */

@Service
public class MyServer implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyServer.class);


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
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast("decoder", new ByteToProtocolBufferDecoder());
                        p.addLast("encoder", new ProtocolBufferToByteEncoder());
                        p.addLast(new ProtocolBufferInboundHandler());

                    }
                } );


            ChannelFuture f= serverBootstrap.bind(port).sync();
            if(f.isSuccess()){
                LOGGER.warn("server start---------------");
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ProtocolBufferInboundHandler.currentApplicationContext = applicationContext;
    }
}
