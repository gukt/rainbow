/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.excel;

import java.lang.annotation.*;

/**
 * ExcelProperty
 *
 * @author https://github.com/gukt
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelProperty {

    /**
     * 默认为空，当数据表中的列名和字段名不匹配时指定。
     */
    String value() default "";

    /**
     * 是否必填。对于 Primitive 类型，使用它们的默认值。
     */
    boolean required() default false;

    String defaultValue() default "";

    boolean normalizeRequired() default false;

    /**
     * 是否忽略该字段的解析。
     */
    boolean ignored() default false;
}
