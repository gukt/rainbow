/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world;

import com.codedog.rainbow.world.net.*;
import com.codedog.rainbow.world.net.json.JsonPacket;
import com.codedog.rainbow.world.net.json.JsonPacketDispatcher;
import com.codedog.rainbow.world.net.json.TcpServerHandler;
import com.codedog.rainbow.world.net.json.interceptor.KeepAliveInterceptor;
import com.codedog.rainbow.world.net.json.interceptor.TcpSecurityInterceptor;
import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Auto configuration for {@link TcpServer} support
 *
 * @author https://github.com/gukt
 */
@Configuration
@ConditionalOnClass(TcpServer.class)
@EnableConfigurationProperties(TcpProperties.class)
@Slf4j
public class TcpServerAutoConfiguration {

    private final ApplicationContext context;
    private final EventPublisher eventPublisher;

    public TcpServerAutoConfiguration(ApplicationContext context,
                                      EventPublisher eventPublisher) {
        this.context = context;
        this.eventPublisher = eventPublisher;
    }

    @Bean
    @ConditionalOnMissingBean(TcpServer.class)
    @ConditionalOnSingleCandidate(TcpProperties.class)
    public TcpServer tcpServer(TcpProperties tcpProperties) {
        // 创建 MessageDispatcher 对象，默认支持 JSON 格式协议
        MessageDispatcher dispatcher = new JsonPacketDispatcher(tcpProperties);
        messageHandlers().forEach(dispatcher::registerHandler);

        TcpServerHandler tcpServerHandler = new TcpServerHandler(tcpProperties, eventPublisher);
        // 创建TcpServerHandler对象
        tcpServerHandler.getInterceptorList().addAll(messageInterceptors(tcpProperties));
        return new TcpServer(tcpProperties, tcpServerHandler, dispatcher);
    }

//    @Bean
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

//    @Bean
    public List<MessageInterceptor<JsonPacket>> messageInterceptors(TcpProperties tcpProperties) {
        List<MessageInterceptor<JsonPacket>> interceptors = new ArrayList<>();
        interceptors.add(new TcpSecurityInterceptor(tcpProperties));
        interceptors.add(new KeepAliveInterceptor());
        return interceptors;
    }
}
