/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.config;

import lombok.extern.slf4j.Slf4j;

/**
 * @author https://github.com/gukt
 */
@Slf4j
public class RpcServerConfiguration {

//    private final ApplicationContext context;
//    private final AppProperties opts;
//
//    public RpcServerConfiguration(ApplicationContext context, AppProperties opts) {
//        this.context = context;
//        this.opts = opts;
//    }
//
//    @Bean
//    public RpcServer rpcServer() {
//        RpcServer rpcServer = new RpcServer(opts);
//        rpcServices().forEach(rpcServer::registerService);
//        return rpcServer;
//    }
//
//    @Bean
//    public List<BindableService> rpcServices() {
//        log.debug("RPC: 正在查找所有可处理RPC请求的服务...");
//        return context.getBeansWithAnnotation(Service.class).values().stream()
//                .filter(svc -> BindableService.class.isAssignableFrom(svc.getClass()))
//                .map(svc -> (BindableService) svc)
//                .collect(Collectors.toList());
//    }
}
