/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.config;

import com.codedog.rainbow.tcp.*;
import com.codedog.rainbow.tcp.json.JsonPacketMessageResolver;
import com.codedog.rainbow.tcp.json.JsonPacketTcpServerChannelHandler;
import com.codedog.rainbow.tcp.protobuf.ProtoPacketMessageResolver;
import com.codedog.rainbow.tcp.protobuf.ProtoPacketTcpServerChannelHandler;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.codedog.rainbow.world.net.json.JsonPacket;
import com.codedog.rainbow.world.net.json.interceptor.KeepAliveMessageInterceptor;
import com.codedog.rainbow.world.net.json.interceptor.TcpSecurityMessageInterceptor;
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
        // 获取消息协议类型
//        MessageProtocol messageProtocol = properties.getMessageProtocol();
//        if (ObjectUtils.isEmpty(messageProtocol)) {
//            // 如果没有指定，默认使用 json 格式。
//            log.warn("TCP: No protocol found, back to 'json'.");
//            messageProtocol = "json";
//        }
        // 创建 tcpServerHandler
//        TcpServerChannelHandler<?> tcpServerChannelHandler;
//        MessageDispatcher dispatcher;
//        switch (messageProtocol.toLowerCase()) {
//            case "protobuf":
//                tcpServerChannelHandler = new ProtoPacketTcpServerChannelHandler(properties, protoPacketMessageResolver());
//                dispatcher = new DefaultMessageDispatcher<>(properties, protoPacketMessageResolver());
//                break;
//            case "json":
//                tcpServerChannelHandler = new JsonPacketTcpServerChannelHandler(properties, jsonPacketMessageResolver());
//                dispatcher = new DefaultMessageDispatcher<>(properties, jsonPacketMessageResolver());
//                break;
//            default:
//                throw new TcpServerException("TCP: 不支持的消息协议类型: actual: " + messageProtocol + " (expected: protobuf|json)");
//        }
        // 注册 Handlers
//        messageHandlerFinder.findMessageHandlers().forEach(dispatcher::registerHandler);
        // 注册 Interceptors
//        messageInterceptors(properties).forEach(item -> tcpServerChannelHandler.getInterceptorList().add(item));

        MessageResolver<?> messageResolver = getMessageResolverByProtocol(properties.getMessageProtocol());
        properties.setMessageResolver(messageResolver);

        // 构造一个 TcpServer 实例返回
        return new TcpServer(properties, tcpServerChannelHandler(properties), messageDispatcher(properties));
    }

    private MessageResolver<?> getMessageResolverByProtocol(MessageProtocol protocol) {
        switch (protocol) {
            case PROTOBUF:
                return protoPacketMessageResolver();
            case JSON:
                return jsonPacketMessageResolver();
            default:
                throw new TcpConfigurationException("tcp.message.protocol: "
                        + protocol + " (expected: json/protobuf");
        }
    }

    @Bean
    public TcpServerChannelHandler<?> tcpServerChannelHandler(TcpProperties properties) {
        TcpServerChannelHandler<?> channelHandler;
        MessageProtocol protocol = properties.getMessageProtocol();
        switch (protocol) {
            case PROTOBUF:
                channelHandler = new ProtoPacketTcpServerChannelHandler(
                        properties, protoPacketMessageResolver());
                break;
            case JSON:
                channelHandler = new JsonPacketTcpServerChannelHandler(
                        properties, jsonPacketMessageResolver());
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
    public MessageDispatcher messageDispatcher(TcpProperties properties) {
        MessageResolver<?> messageResolver = getMessageResolverByProtocol(properties.getMessageProtocol());
        MessageDispatcher dispatcher = new DefaultMessageDispatcher<>(properties, messageResolver);
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
       interceptors.add(new TcpSecurityMessageInterceptor(properties));
       interceptors.add(new KeepAliveMessageInterceptor());
        return interceptors;
    }
}
