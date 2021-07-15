/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author https://github.com/gukt
 */
@Component
@ConfigurationProperties(prefix = "app")
@Data
public class GameOptions {

    private String name = "xxx";
    private String description = "xxx";
    private boolean debug = false;
    private String dbHost = "localhost";
    private int dbPort = 3306;
    private String dbCatalog = "rainbow";
    private String dbUsername = "root";
    private String dbPassword = "P@s$w0rd#!!32";
    private int tcpPort = 3306;
    private int tcpKeepAliveTimeout = 30000;
    private int rpcPort = 15000;
    private String pumperExecThreadPattern = "msg-pumper";
    private int pumperWaitMillisOnRejected = 200000;
    private int pumperWaitMillisOnIdle = 200000;
    private long slowProcessingThreshold = 1000;
    private int sessionMaxPendingRequestSize = 10;
    private int sessionMaxCacheResponseSize = 100;
    private String protocol = "json";
    private String excelPath = "/static/";
    private int tcpMaxConnections = 30000;
    private int tcpContinuousBadPacketThreshold = 50;
    private boolean tcpPacketSeqCheckEnabled = false;
    private String tcpServerBootstrapThreadPattern = "tcp-server-bootstrap";
    private String rpcServerBootstrapThreadPattern = "rpc-server-bootstrap";
    private String tcpBossGroupThreadPattern = "tcp-boss";
    private String tcpWorkerGroupThreadPattern = "tcp-worker-%d";
    private String shutdownHookThreadPattern = "app-shutdown-hook";
    private boolean tcpServerEnabled = true;
    private boolean rpcServerEnabled = true;
    private boolean httpServerEnabled = true;
    private BizExec bizExec;

    @Getter
    @Setter
    public static class BizExec {

        private int corePoolSize = 0;
        private int maxPoolSize = 1;
        private int keepAliveTimeoutSeconds = 60;
        private int queueCapacity = 1;
        private String threadPattern = "biz-%d";
        private int waitTerminationTimeoutMillis = 5_000;
    }
}
