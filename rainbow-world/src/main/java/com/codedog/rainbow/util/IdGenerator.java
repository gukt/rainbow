/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.util;

/**
 * @author https://github.com/gukt
 */
public class IdGenerator {

    public static long nextId() {
        // TODO 使用 snowflake 算法代替
        return System.currentTimeMillis();
    }
}
