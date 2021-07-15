/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.excel;

import java.util.function.Function;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-08-02 00:43
 *
 * @author gukt <gukaitong@gmail.com>
 */
public class MyTransformer implements Function<String, String> {

    @Override
    public String apply(String s) {
        return "hello world";
    }
}
