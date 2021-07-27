/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import com.codedog.rainbow.world.GameWorld;
import com.codedog.rainbow.world.net.EnableRpcServer;
import com.codedog.rainbow.world.net.EnableTcpServer;
import com.codedog.rainbow.tcp.TcpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableConfigurationProperties
@EnableTcpServer
@EnableRpcServer
@RestController
public class RainbowWorldApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RainbowWorldApplication.class, args);

        GameWorld world = context.getBean(GameWorld.class);
        TcpServer tcpServer = context.getBean(TcpServer.class);
        world.setTcpServer(tcpServer);
        world.start();
    }

    @GetMapping("hello")
    public void hello() {
        System.out.println("hello");
    }

}
