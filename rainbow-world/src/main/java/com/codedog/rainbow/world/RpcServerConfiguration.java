/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
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
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
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
