/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.config;

import com.codedog.rainbow.tcp.*;
import com.codedog.rainbow.util.ObjectUtils;
import com.codedog.rainbow.world.net.json.JsonPacket;
import com.codedog.rainbow.world.net.json.interceptor.KeepAliveInterceptor;
import com.codedog.rainbow.world.net.json.interceptor.TcpSecurityInterceptor;
import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
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
@Slf4j
public class TcpServerAutoConfiguration {

    private final ApplicationContext context;

    public TcpServerAutoConfiguration(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    public ProtoPacketMessageResolver protoPacketMessageResolver() {
        return new ProtoPacketMessageResolver();
    }

    @Bean
    public JsonPacketMessageResolver jsonPacketMessageResolver() {
        return new JsonPacketMessageResolver();
    }

    @Bean
    @ConditionalOnMissingBean(TcpServer.class)
    @ConditionalOnSingleCandidate(TcpProperties.class)
    public TcpServer tcpServer(TcpProperties properties) {
        // 获取消息协议类型
        String messageProtocol = properties.getMessageProtocol();
        if (ObjectUtils.isEmpty(messageProtocol)) {
            // 如果没有指定，默认使用 json 格式。
            log.warn("TCP: No protocol found, back to 'json'.");
            messageProtocol = "json";
        }
        // 创建 tcpServerHandler
        TcpServerChannelHandler<?> tcpServerChannelHandler;
        MessageDispatcher dispatcher;
        switch (messageProtocol.toLowerCase()) {
            case "protobuf":
                tcpServerChannelHandler = new ProtoPacketTcpServerChannelHandler(properties, protoPacketMessageResolver());
                dispatcher = new DefaultMessageDispatcher<>(properties, protoPacketMessageResolver());
                break;
            case "json":
                tcpServerChannelHandler = new JsonPacketTcpServerChannelHandler(properties, jsonPacketMessageResolver());
                dispatcher = new DefaultMessageDispatcher<>(properties, jsonPacketMessageResolver());
                break;
            default:
                throw new TcpServerException("TCP: 不支持的消息协议类型: actual: " + messageProtocol + " (expected: protobuf|json)");
        }
        // 注册 Handlers
        messageHandlers().forEach(dispatcher::registerHandler);
        // 注册 Interceptors
        messageInterceptors(properties).forEach(item -> tcpServerChannelHandler.getInterceptorList().add(item));
        // 构造一个 TcpServer 实例返回
        return new TcpServer(properties, tcpServerChannelHandler, dispatcher);
    }

    private List<MessageHandler<?>> messageHandlers() {
        log.debug("TCP - Scanning message handlers...");
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
        log.info("TCP - Finished message handler scanning: found {} handlers", handlers.size());
        return handlers;
    }

    private List<MessageInterceptor<?>> messageInterceptors(TcpProperties properties) {
        List<MessageInterceptor<?>> interceptors = new ArrayList<>();
        interceptors.add(new TcpSecurityInterceptor(properties));
        interceptors.add(new KeepAliveInterceptor());
        return interceptors;
    }
}
