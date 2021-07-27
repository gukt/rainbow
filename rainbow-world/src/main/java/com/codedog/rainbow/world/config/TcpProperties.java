/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.config;

import com.codedog.rainbow.tcp.session.SessionProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author https://github.com/gukt
 */
@Component
@ConfigurationProperties(prefix = "app.tcp")
@Data
public class TcpProperties {

    private boolean enabled = false;
    private String protocol = "json"; // TODO 改为枚举
    private Integer port = 5000;
    private Integer keepAliveTimeout = 30; // Seconds
    private boolean seqCheckEnabled = false;
    private Integer maxConnections = 100;
    private Integer badPacketThreshold = 100;
    private String bossThreadName = "tcp-boss";
    private String bootstrapThreadName = "tcp-bootstrap";
    private String workerThreadPattern = "tcp-%d";
    private Integer waitTerminationTimeoutMillis = 5000;
    private Integer sessionMaxPendingRequestSize = 10;
    private Integer sessionMaxCacheResponseSize = 100;
    private Integer slowProcessingThreshold = 1000;
    private String pumperExecThreadPattern = "message-pumper";
    private Integer pumperWaitMillisOnIdle = 300;
    private Integer pumperWaitMillisOnRejected = 300;

    private BizExecutor bizExecutor;
    public SessionProperties session;

    @Getter
    @Setter
    public static class BizExecutor {

        private int corePoolSize = 0;
        private int maxPoolSize = 1;
        private int keepAliveTimeoutSeconds = 60;
        private int queueCapacity = 1;
        private String threadPattern = "biz-%d";
        private int waitTerminationTimeoutMillis = 5_000;
    }
}
