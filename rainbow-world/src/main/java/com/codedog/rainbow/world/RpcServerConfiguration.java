/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world;

import com.codedog.rainbow.world.net.RpcServer;
import io.grpc.BindableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author https://github.com/gukt
 */
@Slf4j
public class RpcServerConfiguration {

    private final ApplicationContext context;
    private final GameOptions opts;

    public RpcServerConfiguration(ApplicationContext context, GameOptions opts) {
        this.context = context;
        this.opts = opts;
    }

    @Bean
    public RpcServer rpcServer() {
        RpcServer rpcServer = new RpcServer(opts);
        rpcServices().forEach(rpcServer::registerService);
        return rpcServer;
    }

    @Bean
    public List<BindableService> rpcServices() {
        log.debug("RPC: 正在查找所有可处理RPC请求的服务...");
        return context.getBeansWithAnnotation(Service.class).values().stream()
                .filter(svc -> BindableService.class.isAssignableFrom(svc.getClass()))
                .map(svc -> (BindableService) svc)
                .collect(Collectors.toList());
    }
}
