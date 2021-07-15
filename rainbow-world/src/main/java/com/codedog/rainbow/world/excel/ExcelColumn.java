/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.excel;

import com.codedog.rainbow.world.excel.ExcelParser.Transformer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ExcelProperty
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelColumn {

    /**
     * @return 返回映射的列名
     */
    String alias() default "";

    /**
     * 使用的内容转换器
     *
     * @return {@linkplain Transformer#NOOP} by default
     */
    // Class<?> transformer() default ExcelParser.xxx;

    /**
     * 是否忽略该字段的映射
     *
     * @return ignored if true
     */
    boolean ignore() default false;
}
