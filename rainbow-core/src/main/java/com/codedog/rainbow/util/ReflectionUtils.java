/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * ReflectUtils class
 *
 * @author https://github.com/gukt
 */
public class ReflectionUtils {

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

    // TODO 移到 ArgumentTypeMatcher 中
    // TODO 考虑 -> super class - > interfaces 及多接口的情况
    public static boolean isTypeArgumentMatched(Object obj, Class<?> expectedTypeArg) {
        Type[] genericInterfaceTypes = obj.getClass().getGenericInterfaces();
        if (genericInterfaceTypes.length > 0) {
            Type[] actualTypeArgs = ((ParameterizedType) genericInterfaceTypes[0]).getActualTypeArguments();
            if (actualTypeArgs.length > 0) {
                return actualTypeArgs[0].equals(expectedTypeArg);
            }
        }
        return false;
    }
}
