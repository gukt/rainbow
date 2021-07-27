/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.config;

import com.codedog.rainbow.util.ObjectUtils;
import com.codedog.rainbow.tcp.PacketDispatcher;
import com.codedog.rainbow.tcp.PacketDispatcher.JsonPacketResolver;
import com.codedog.rainbow.tcp.PacketDispatcher.PacketResolver;
import com.codedog.rainbow.tcp.PacketDispatcher.ProtobufPacketResolver;
import com.codedog.rainbow.world.net.json.JsonPacket;
import com.codedog.rainbow.tcp.TcpServerHandler;
import com.codedog.rainbow.world.net.json.interceptor.KeepAliveInterceptor;
import com.codedog.rainbow.world.net.json.interceptor.TcpSecurityInterceptor;
import com.codedog.rainbow.tcp.*;
import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    public TcpServerAutoConfiguration(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    @ConditionalOnMissingBean(TcpServer.class)
    @ConditionalOnSingleCandidate(TcpProperties.class)
    public TcpServer tcpServer(TcpProperties properties) {
        // 获取消息协议类型
        String messageProtocol = properties.getProtocol();
        if (ObjectUtils.isEmpty(messageProtocol)) {
            // 如果没有指定，默认使用 json 格式。
            log.warn("TCP: No protocol found, back to 'json'.");
            messageProtocol = "json";
        }
        PacketResolver<?> resolver;
        switch (messageProtocol.toLowerCase()) {
            case "protobuf":
                resolver = new ProtobufPacketResolver();
                break;
            case "json":
                resolver = new JsonPacketResolver();
                break;
            default:
                throw new TcpServerException("TCP: 不支持的消息协议类型: actual: " + messageProtocol + " (expected: protobuf|json)");
        }
        // 创建消息分发器
        MessageDispatcher dispatcher = new PacketDispatcher<>(properties, resolver);
        // 查找所有的 handlers，并将它们注册到 dispatcher
        messageHandlers().forEach(dispatcher::registerHandler);
        // 创建 tcpServerHandler
        TcpServerHandler<?> tcpServerHandler = new TcpServerHandler<>(properties, resolver);
        // 查找所有的 interceptors，并将它们注册到 tcpServerHandler
        messageInterceptors(properties).forEach(item -> tcpServerHandler.getInterceptorList().add(item));
        return new TcpServer(properties, tcpServerHandler, dispatcher);
    }

    private List<MessageHandler<?>> messageHandlers() {
        log.debug("TCP: 正在查找所有可用的消息处理器");
        List<MessageHandler<?>> handlers = new ArrayList<>();
        context.getBeansWithAnnotation(Controller.class).values().forEach(bean -> {
            Class<?> beanType = bean.getClass();
            if (MessageHandler.class.isAssignableFrom(beanType)) {
                handlers.add((MessageHandler<?>) bean);
            } else {
                final MethodAccess methodAccess = MethodAccess.get(beanType);
                Arrays.stream(beanType.getDeclaredMethods())
                        // 过滤出所有标注了@HandlerMapping注解的方法
                        .filter(m -> m.isAnnotationPresent(HandlerMapping.class))
                        // 使用适配器模式将每个方法包装成MessageHandler
                        .forEach(m -> {
                            final HandlerMapping mapping = m.getAnnotation(HandlerMapping.class);
                            handlers.add(new MessageHandlerAdapter<JsonPacket>(bean, methodAccess, m.getName()) {
                                @Override
                                public Serializable getType() {
                                    return mapping.value();
                                }
                            });
                        });
            }
        });
        return handlers;
    }

    private List<MessageInterceptor<?>> messageInterceptors(TcpProperties properties) {
        List<MessageInterceptor<?>> interceptors = new ArrayList<>();
        interceptors.add(new TcpSecurityInterceptor(properties));
        interceptors.add(new KeepAliveInterceptor());
        return interceptors;
    }
}
