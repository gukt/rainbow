/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.util;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
 */
public class IdGenerator {

    public static long nextId() {
        // TODO 使用 snowflake 算法代替
        return System.currentTimeMillis();
    }
}
