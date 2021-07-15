/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.excel;

import java.lang.annotation.*;

/**
 * @author https://github.com/gukt
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelMapping {

    String file();

    String sheet();
}
