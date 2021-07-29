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
}
