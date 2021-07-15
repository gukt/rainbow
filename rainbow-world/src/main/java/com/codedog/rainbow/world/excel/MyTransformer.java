/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.excel;

import java.util.function.Function;

/**
 * @author https://github.com/gukt
 */
public class MyTransformer implements Function<String, String> {

    @Override
    public String apply(String s) {
        return "hello world";
    }
}
