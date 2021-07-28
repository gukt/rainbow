/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * ReflectUtils class
 *
 * @author https://github.com/gukt
 */
public class ReflectUtils {

    private static Map<Object[], Method> METHOD_CACHE = new HashMap<>();

    public static Method getMethod(Class<?> type, String name, Class<?>[] argumentTypes, boolean cache) {
        Assert.notNull(type, "type");
        Assert.notNull(type, "methodName");

//        Map<String, Object[]> map = METHOD_CACHE.get(type);
//        type.getMethod(name);
        return null;
    }

    private static Method getMethodFromCache(Class<?> type, String name, Class<?>[] argumentTypes) {
//        METHOD_CACHE.get()
        return null;
    }
}
