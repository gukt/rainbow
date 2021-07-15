/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import java.lang.annotation.*;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-09 17:57
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HandlerMapping {

    String value();
}
