/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.config;

import com.codedog.rainbow.MessageHandlerFinder;
import com.codedog.rainbow.tcp.*;
import com.codedog.rainbow.tcp.interceptor.MessageInterceptor;
import com.codedog.rainbow.tcp.json.JsonPacketMessageResolver;
import com.codedog.rainbow.tcp.json.JsonPacketTcpServerChannelHandler;
import com.codedog.rainbow.tcp.protobuf.ProtoPacketMessageResolver;
import com.codedog.rainbow.tcp.protobuf.ProtoPacketTcpServerChannelHandler;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
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
        MessageResolver<?> messageResolver = getMessageResolverByProtocol(properties.getMessageProtocol());
        properties.setMessageResolver(messageResolver);

        // 创建 TcpServer 实例
        return new TcpServer(properties, tcpServerChannelHandler(properties), messageDispatcher(properties));
    }

    private MessageResolver<?> getMessageResolverByProtocol(MessageProtocol protocol) {
        switch (protocol) {
            case PROTOBUF:
                return protoPacketMessageResolver();
            case JSON:
                return jsonPacketMessageResolver();
            default:
                throw new TcpConfigurationException("tcp.message.protocol: " + protocol + " (expected: json/protobuf");
        }
    }

    @Bean
    @ConditionalOnSingleCandidate(TcpProperties.class)
    public TcpServerChannelHandler<?> tcpServerChannelHandler(TcpProperties properties) {
        TcpServerChannelHandler<?> channelHandler;
        MessageProtocol protocol = properties.getMessageProtocol();
        switch (protocol) {
            case PROTOBUF:
                channelHandler = new ProtoPacketTcpServerChannelHandler(properties);
                break;
            case JSON:
                channelHandler = new JsonPacketTcpServerChannelHandler(properties);
                break;
            default:
                throw new TcpConfigurationException("tcp.message.protocol: "
                        + protocol + " (expected: json/protobuf");
        }
        messageInterceptors(properties).forEach(item -> channelHandler.getInterceptorList().add(item));
        return channelHandler;
    }

    @Bean
    public MessageHandlerFinder messageHandlerFinder() {
        return new MessageHandlerFinder(context);
    }

    @Bean
    @ConditionalOnSingleCandidate(TcpProperties.class)
    public MessageDispatcher messageDispatcher(TcpProperties properties) {
        MessageDispatcher dispatcher = new DefaultMessageDispatcher<>(properties);
        // 支持的消息类型
        Class<?> supportedMessageType = properties.getMessageProtocol() == MessageProtocol.PROTOBUF ? ProtoPacket.class : JsonPacket.class;
        // 注册“消息处理器”
        messageHandlerFinder().findMessageHandlers(supportedMessageType)
                .forEach(dispatcher::registerHandler);
        return dispatcher;
    }

    private List<MessageInterceptor<?>> messageInterceptors(TcpProperties properties) {
        List<MessageInterceptor<?>> interceptors = new ArrayList<>();
        // TODO 要根据消息类型选择性添加
        // interceptors.add(new TcpSecurityMessageInterceptor(properties));
        // interceptors.add(new KeepAliveMessageInterceptor());
        return interceptors;
    }
}
