/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.lab;

import java.lang.annotation.Repeatable;

/**
 * Role class
 *
 * @author https://github.com/gukt
 */
@Repeatable(Roles.class)
public @interface Role {

    String value();
}
