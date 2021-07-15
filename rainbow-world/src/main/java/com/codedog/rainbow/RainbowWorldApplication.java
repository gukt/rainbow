/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import com.codedog.rainbow.world.GameApp;
import com.codedog.rainbow.world.net.EnableRpcServer;
import com.codedog.rainbow.world.net.EnableTcpServer;
import com.codedog.rainbow.world.net.TcpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties
@EnableTcpServer
@EnableRpcServer
public class RainbowWorldApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RainbowWorldApplication.class, args);

        GameApp app = context.getBean(GameApp.class);
        TcpServer tcpServer = context.getBean(TcpServer.class);
        app.setTcpServer(tcpServer);
        app.start();
    }

}
