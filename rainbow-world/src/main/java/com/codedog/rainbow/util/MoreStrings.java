/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.util;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
 */
public class MoreStrings {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static String ifNull(String s, String defaultValue) {
        return s == null ? defaultValue : s;
    }
}
