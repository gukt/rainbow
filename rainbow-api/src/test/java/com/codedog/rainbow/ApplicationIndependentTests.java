/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import com.codedog.rainbow.domain.User;
import com.codedog.rainbow.util.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * ApplicaitonIndependentTests class
 *
 * @author https://github.com/gukt
 */
public class ApplicationIndependentTests {

    @Test
    void test1() {
        String json = "{ \"id\": 1, \"name\": \"xxx\" }";
        User u = JsonUtils.toBean(json, User.class);
        System.out.println(u);

        final BeanWrapper src = new BeanWrapperImpl(u);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> nullProperties = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                nullProperties.add(pd.getName());
            }
        }
        String[] result = new String[nullProperties.size()];
    }
}
