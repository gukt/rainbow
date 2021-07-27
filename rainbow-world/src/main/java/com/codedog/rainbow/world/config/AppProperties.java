/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author https://github.com/gukt
 */
@Component
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    private String description = "xxx";
    private boolean debug = false;
    private boolean tcpServerEnabled = true;
    private boolean rpcServerEnabled = true;
    private String shutdownHookThreadPattern = "app-shutdown-hook";
    private String excelPath = "/data/excel";

//    private String dbHost = "localhost";
//    private int dbPort = 3306;
//    private String dbCatalog = "rainbow";
//    private String dbUsername = "root";
//    private String dbPassword = "P@s$w0rd#!!32";
//    private int tcpPort = 3306;
//    private int tcpKeepAliveTimeout = 30000;
//    private int rpcPort = 15000;
//    private String pumperExecThreadPattern = "msg-pumper";
//    private int pumperWaitMillisOnRejected = 200000;
//    private int pumperWaitMillisOnIdle = 200000;
//    private long slowProcessingThreshold = 1000;
//    private int sessionMaxPendingRequestSize = 10;
//    private int sessionMaxCacheResponseSize = 100;
//    private String protocol = "json";
//    private String excelPath = "/static/";
//    private int tcpMaxConnections = 30000;
//    private int tcpContinuousBadPacketThreshold = 50;
//    private boolean tcpPacketSeqCheckEnabled = false;
//    private String tcpServerBootstrapThreadPattern = "tcp-server-bootstrap";
//    private String rpcServerBootstrapThreadPattern = "rpc-server-bootstrap";
//    private String tcpBossGroupThreadPattern = "tcp-boss";
//    private String tcpWorkerGroupThreadPattern = "tcp-worker-%d";
//    private String shutdownHookThreadPattern = "app-shutdown-hook";
//    private boolean tcpServerEnabled = true;
//    private boolean rpcServerEnabled = true;
//    private boolean httpServerEnabled = true;
//    private Tcp tcp;
//    private Rpc rpc;

//    private Map<String, Object> tcp;
//
//    @Getter
//    @Setter
//    public static class Tcp {
//        private boolean enabled = false;
//        // TODO 改为枚举
//        private String protocol = "json";
//        private Integer port = 5000;
//        private Integer keepAliveTimeout = 30; // Seconds
//        private Boolean seqCheckEnabled = false;
//        private Integer maxConnections = 100;
//        private Integer badPacketThreshold = 100;
//        private String bossThreadName = "tcp-boss";
//        private String bootstrapThreadName = "tcp-bootstrap";
//        private String workerThreadPattern = "tcp-%d";
//    }
//
//    @Getter
//    @Setter
//    public static class Rpc {
//        private boolean enabled = false;
//        private Integer port = 15000;
//        private String bootstrapThreadName = "tcp-bootstrap";
//    }
}
