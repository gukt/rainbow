/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.config;

import com.codedog.rainbow.tcp.MessageProtocol;
import com.codedog.rainbow.tcp.MessageResolver;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author https://github.com/gukt
 */
@Component
@ConfigurationProperties(prefix = "app.tcp")
@Data
public class TcpProperties {

    private boolean enabled = false;
    /**
     * TCP 传输协议的类型：Socket/Web Socket(ws)
     */
    private String type = "ws";
    /**
     * 消息的协议类型
     */
    private MessageProtocol messageProtocol = MessageProtocol.JSON;
    /**
     * 对外 Socket 服务的端口号
     */
    private Integer port = 5000;
    /**
     * 心跳超时时间，单位：秒
     */
    private Integer keepAliveTimeout = 30; // Seconds
    private boolean seqCheckEnabled = false;
    /**
     * 服务器允许的最大连接数
     */
    private Integer maxConnections = 1000;
    private Integer badPacketThreshold = 100;
    private String bossThreadName = "tcp-boss";
    private String bootstrapThreadName = "tcp-bootstrap";
    private String workerThreadPattern = "tcp-%d";
    private Integer waitTerminationTimeoutMillis = 5_000;
    private Integer slowProcessingThreshold = 1000;
    private String pumperExecThreadPattern = "message-pumper";
    private Integer pumperWaitMillisOnIdle = 300;
    private Integer pumperWaitMillisOnRejected = 300;

    private SessionProperties session;
    private ExecutorProperties executor;
    private WebSocketProperties websocket;

    private MessageResolver<?> messageResolver;

    public boolean isWebSocketEnabled() {
        return enabled && "ws".equalsIgnoreCase(type);
    }

    @Configuration
    @ConfigurationProperties(prefix = "app.tcp.session")
    @Data
    public static class SessionProperties {

        int maxPendingRequestSize;
        int maxCacheResponseSize;
    }

    @Configuration
    @ConfigurationProperties(prefix = "app.tcp.executor")
    @Data
    public static class ExecutorProperties {

        int corePoolSize = 0;
        int maxPoolSize = 1;
        int keepAliveTimeoutSeconds = 60;
        int queueCapacity = 1;
        String threadPattern = "biz-%d";
    }

    @Configuration
    @ConfigurationProperties(prefix = "app.tcp.websocket")
    @Data
    public static class WebSocketProperties {

        /**
         * The path of web socket endpoint
         */
        String path = "/ws";
        /**
         * SSL Enabled?
         */
        boolean sslEnabled = false;
        /**
         * Max web frame size in bytes
         */
        int maxFrameSize = 40960; // bytes
        /**
         * the maximum length of the aggregated content in bytes
         */
        int maxContentLen = 65536;
    }
}
