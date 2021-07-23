/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.*;

import static com.codedog.rainbow.util.MapUtils.newHashMap;

/**
 * TODO 提供能忽略对象属性默认值的功能，这样就可以自由在对象上给属性设置默认值了（之前不敢写，是因为DataBind 时会）
 *
 * @author https://github.com/gukt
 */
public final class BeanUtils {

    /** Prevents to construct an instance. */
    private BeanUtils() {
        throw new AssertionError("No ArrayUtils instances for you.");
    }

    private static String[] getNullProperties(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> nullProperties = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                nullProperties.add(pd.getName());
            }
        }
        String[] result = new String[nullProperties.size()];
        return nullProperties.toArray(result);
    }

    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, true);
    }

    public static void copyProperties(Object source, Object target, boolean ignoreNullProperties, String... ignoreProperties) {
        String[] ignoreList = ignoreNullProperties
                ? getNullProperties(source)
                : new String[0];
        if (ignoreProperties != null && ignoreProperties.length > 0) {
            ignoreList = ArrayUtils.concat(ignoreList, ignoreProperties);
        }
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreList);
    }

    public static Map<String, Object> toMap(Object obj, String... fields) {
        Objects.requireNonNull(obj);
        Map<String, Object> map = newHashMap();
        Arrays.stream(obj.getClass().getDeclaredFields())
                .forEach(field -> Arrays.stream(fields)
                        .filter(key -> Objects.equals(field.getName(), key))
                        .forEach(key -> {
                            try {
                                field.setAccessible(true);
                                map.put(key, field.get(obj));
                            } catch (IllegalAccessException e) {
                                System.out.println("Cannot read field value: field=" + field);
                            } finally {
                                field.setAccessible(false);
                            }
                        }));
        return map;
    }
}
