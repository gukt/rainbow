/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * @author https://github.com/gukt
 * @version 1.0
 * @date 2020/2/2 17:26
 */
@Slf4j
public class Beans {

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

    public static void copyProperties(Object source, Object target, boolean ignoreNullProperties) {
        if (ignoreNullProperties) {
            BeanUtils.copyProperties(source, target, getNullProperties(source));
        } else {
            BeanUtils.copyProperties(source, target);
        }
    }
}
