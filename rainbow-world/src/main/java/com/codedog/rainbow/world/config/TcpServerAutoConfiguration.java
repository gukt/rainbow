/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.config;

import com.codedog.rainbow.MessageHandlerFinder;
import com.codedog.rainbow.tcp.TcpConfigurationException;
import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.TcpServer;
import com.codedog.rainbow.tcp.channel.TcpServerChannelHandler;
import com.codedog.rainbow.tcp.channel.json.JsonPacketTcpServerChannelHandler;
import com.codedog.rainbow.tcp.channel.protobuf.ProtoPacketTcpServerChannelHandler;
import com.codedog.rainbow.tcp.interceptor.MessageInterceptor;
import com.codedog.rainbow.tcp.interceptor.json.JsonKeepAliveMessageInterceptor;
import com.codedog.rainbow.tcp.interceptor.json.JsonTcpSecurityMessageInterceptor;
import com.codedog.rainbow.tcp.interceptor.protobuf.ProtoKeepAliveMessageInterceptor;
import com.codedog.rainbow.tcp.interceptor.protobuf.ProtoTcpSecurityMessageInterceptor;
import com.codedog.rainbow.tcp.message.DefaultMessageDispatcher;
import com.codedog.rainbow.tcp.message.JsonPacket;
import com.codedog.rainbow.tcp.message.MessageDispatcher;
import com.codedog.rainbow.tcp.message.MessageProtocol;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * Auto configuration for {@link TcpServer} support
 *
 * @author https://github.com/gukt
 */
// @Configuration
@ConditionalOnClass(TcpServer.class)
@Slf4j
public class TcpServerAutoConfiguration {

    private final ApplicationContext context;

    public TcpServerAutoConfiguration(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    @ConditionalOnMissingBean(TcpServer.class)
    // @ConditionalOnSingleCandidate(TcpProperties.class)
    public TcpServer tcpServer(TcpProperties properties) {
        // ?????? TcpServer ??????
        return new TcpServer(properties, tcpServerChannelHandler(properties), messageDispatcher(properties));
    }

    @Bean
    // @ConditionalOnSingleCandidate(TcpProperties.class)
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

    @Configuration(proxyBeanMethods = false)
    public static class ThirdPartyConfiguration {

        @Bean
        @ConfigurationProperties(prefix = "app.tcp")
        @Validated
        public TcpProperties tcpProperties() {
            return new TcpProperties();
        }
    }

    // @Bean
    // @ConfigurationProperties(prefix = "app.tcp")
    // public TcpProperties tcpProperties() {
    //     return new TcpProperties();
    // }

    @Bean
    public MessageHandlerFinder messageHandlerFinder() {
        return new MessageHandlerFinder(context);
    }

    @Bean
    // @ConditionalOnSingleCandidate(TcpProperties.class)
    public MessageDispatcher messageDispatcher(TcpProperties properties) {
        MessageDispatcher dispatcher = new DefaultMessageDispatcher(properties);
        // ?????????????????????
        Class<?> supportedMessageType = properties.getMessageProtocol() == MessageProtocol.PROTOBUF ? ProtoPacket.class : JsonPacket.class;
        // ???????????????????????????
        messageHandlerFinder().findMessageHandlers(supportedMessageType)
                .forEach(dispatcher::registerHandler);
        return dispatcher;
    }

    private List<MessageInterceptor<?>> messageInterceptors(TcpProperties properties) {
        List<MessageInterceptor<?>> interceptors = new ArrayList<>();
        // ?????????????????????????????????
        if (properties.getMessageProtocol() == MessageProtocol.PROTOBUF) {
            interceptors.add(new ProtoKeepAliveMessageInterceptor());
            interceptors.add(new ProtoTcpSecurityMessageInterceptor(properties));
        }
        if (properties.getMessageProtocol() == MessageProtocol.JSON) {
            interceptors.add(new JsonKeepAliveMessageInterceptor());
            interceptors.add(new JsonTcpSecurityMessageInterceptor(properties));
        }
        return interceptors;
    }
}
