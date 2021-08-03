/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.message;

import java.lang.annotation.*;

/**
 * @author https://github.com/gukt
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(value = HandlerMappings.class)
public @interface HandlerMapping {

    String value();

    String accept() default "protobuf";
}
