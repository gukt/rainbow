/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.net;

import com.codedog.rainbow.world.config.TcpServerAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * EnableTcpServer
 *
 * @author https://github.com/gukt
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TcpServerAutoConfiguration.class)
public @interface EnableTcpServer {

}
