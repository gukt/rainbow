/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.tcp.message.MessageProtocol;
import lombok.Data;

import java.time.Duration;

/**
 * {@link com.codedog.rainbow.tcp.TcpServer} 相关配置
 *
 * @author https://github.com/gukt
 */
// @Component
// @ConfigurationProperties(prefix = "app.tcp")
@Data
public class TcpProperties {

    /**
     * 是否启用 TCP 服务
     */
    private boolean enabled = false;
    /**
     * TCP 传输协议的类型：Socket/WebSocket
     */
    private String type = "WebSocket";
    /**
     * 消息的协议类型
     */
    private MessageProtocol messageProtocol = MessageProtocol.JSON;
    /**
     * 服务端口号
     */
    private Integer port = 5000;
    /**
     * 心跳超时时间，单位：秒
     */
    private Duration keepAliveTimeout = Duration.ofMinutes(3);
    private boolean seqCheckEnabled = false;
    /**
     * 服务器允许的最大连接数
     */
    private Integer maxConnections = 10000;
    private Integer badRequestThreshold = 100;
    private String bootstrapThreadName = "tcp-bootstrap";
    private String bossThreadName = "tcp-boss";
    private String workerThreadPattern = "tcp-worker-%d";
    private Duration waitTerminationTimeout = Duration.ofMinutes(5);
    private Integer slowProcessingThreshold = 1000;
    private String pumperExecThreadPattern = "message-pumper";
    private Integer pumperWaitMillisOnIdle = 300;
    private Integer pumperWaitMillisOnRejected = 300;

    private SessionProperties session = new SessionProperties();
    private ExecutorProperties executor = new ExecutorProperties();
    private WebSocketProperties websocket = new WebSocketProperties();

    public boolean isWebSocketEnabled() {
        return enabled && "WebSocket".equalsIgnoreCase(type);
    }

    // @Configuration
    // @ConfigurationProperties(prefix = "app.tcp.session")
    @Data
    public static class SessionProperties {

        int maxPendingRequestSize = 10;
        int maxCacheResponseSize = 10;
    }

    // @Configuration
    // @ConfigurationProperties(prefix = "app.tcp.executor")
    @Data
    public static class ExecutorProperties {

        int corePoolSize = 0;
        int maxPoolSize = 1;
        Duration keepAliveTimeout = Duration.ofSeconds(60);
        int queueCapacity = 1;
        String threadPattern = "biz-%d";
    }

    // @Configuration
    // @ConfigurationProperties(prefix = "app.tcp.websocket")
    @Data
    public static class WebSocketProperties {

        /**
         * WebSocket 路径
         */
        String path = "/ws";
        /**
         * 是否启用 SSL
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
