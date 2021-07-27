/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world;

import com.codedog.rainbow.tcp.TcpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * TcpServerBootstrap class
 *
 * @author https://github.com/gukt
 */
@Component
public class TcpServerBootstrap implements ApplicationRunner {

    private final GameWorld world;
    private final TcpServer tcpServer;

    public TcpServerBootstrap(GameWorld world, TcpServer tcpServer) {
        this.world = world;
        this.tcpServer = tcpServer;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (tcpServer != null) {
            world.getWaitGroup().run(() -> {
                try {
                    // 启动并阻塞，直到有错误发生
                    tcpServer.start();
                } catch (Exception e) {
                    world.exit(e);
                }
            }, "tcp-server");
        }
    }
}
