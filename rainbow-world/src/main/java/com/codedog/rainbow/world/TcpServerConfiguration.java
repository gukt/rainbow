/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world;

import com.codedog.rainbow.world.net.*;
import com.codedog.rainbow.world.net.json.JsonPacket;
import com.codedog.rainbow.world.net.json.JsonPacketDispatcher;
import com.codedog.rainbow.world.net.json.TcpServerHandler;
import com.codedog.rainbow.world.net.json.interceptor.KeepAliveInterceptor;
import com.codedog.rainbow.world.net.json.interceptor.SecurityInterceptor;
import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author https://github.com/gukt
 */
@Slf4j
public class TcpServerConfiguration {

    private final ApplicationContext context;
    private final GameOptions options;
    private final EventPublisher eventPublisher;

    public TcpServerConfiguration(ApplicationContext context, GameOptions options,
                                  EventPublisher eventPublisher) {
        this.context = context;
        this.options = options;
        this.eventPublisher = eventPublisher;
    }

    @Bean
    public TcpServer tcpServer() {
        // 创建MessageDispatcher对象，此处是支持json格式的消息派发器
        MessageDispatcher dispatcher = new JsonPacketDispatcher(options);
        messageHandlers().forEach(dispatcher::registerHandler);

        TcpServerHandler tcpServerHandler = new TcpServerHandler(options, eventPublisher);
        // 创建TcpServerHandler对象
        tcpServerHandler.getInterceptorList().addAll(messageInterceptors(options));
        return new TcpServer(options, tcpServerHandler, dispatcher);
    }

    @Bean
    public List<MessageHandler<?>> messageHandlers() {
        log.debug("TCP: 正在查找所有可用的TCP消息处理器");
        List<MessageHandler<?>> handlers = new ArrayList<>();
        context.getBeansWithAnnotation(Controller.class).values().forEach(bean -> {
            Class<?> beanType = bean.getClass();
            if (MessageHandler.class.isAssignableFrom(beanType)) {
                handlers.add((MessageHandler) bean);
            } else {
                final MethodAccess methodAccess = MethodAccess.get(beanType);
                Arrays.stream(beanType.getDeclaredMethods())
                        // 过滤出所有标注了@HandlerMapping注解的方法
                        .filter(m -> m.isAnnotationPresent(HandlerMapping.class))
                        // 使用适配器模式将每个方法包装成MessageHandler
                        .forEach(m -> {
                            final HandlerMapping anno = m.getAnnotation(HandlerMapping.class);
                            handlers.add(new MessageHandlerAdapter<JsonPacket>(bean, methodAccess, m.getName()) {
                                @Override
                                public Serializable getType() {
                                    return anno.value();
                                }
                            });
                        });
            }
        });
        return handlers;
    }

    @Bean
    public List<MessageInterceptor<JsonPacket>> messageInterceptors(GameOptions opts) {
        List<MessageInterceptor<JsonPacket>> interceptors = new ArrayList<>();
        interceptors.add(new SecurityInterceptor(opts));
        interceptors.add(new KeepAliveInterceptor());
        return interceptors;
    }
}
