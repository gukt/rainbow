/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world;

import com.codedog.rainbow.tcp.TcpProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * TcpServerProperties class
 *
 * @author https://github.com/gukt
 */
@Component
@ConfigurationProperties(prefix = "app.tcp")
public class TcpServerProperties extends TcpProperties {
}