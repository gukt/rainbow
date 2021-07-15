/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.excel;

import java.lang.annotation.*;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-23 14:10
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelMapping {

    String file();

    String sheet();
}
