/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.util;

/**
 * @author https://github.com/gukt
 */
public class MoreStrings {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static String nullToDefault(String s, String defaultValue) {
        return s == null ? defaultValue : s;
    }
}
