/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world;

import com.codedog.rainbow.tcp.TcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 游戏配置数据加载器
 *
 * @author https://github.com/gukt
 */
// @Component
@Order(0)
@Slf4j
public class TemplateDataLoader implements ApplicationRunner {

    private final GameWorld world;
    private final TcpServer tcpServer;

    public TemplateDataLoader(GameWorld world, TcpServer tcpServer) {
        this.world = world;
        this.tcpServer = tcpServer;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("正在加载游戏配置数据...");
        log.info("游戏配置数据加载完毕");
    }
}
