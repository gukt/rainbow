/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import com.codedog.rainbow.domain.User;
import com.codedog.rainbow.util.BeanUtils;
import com.codedog.rainbow.util.JsonUtils;
import com.codedog.rainbow.util.MapUtils;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * ApplicaitonIndependentTests class
 *
 * @author https://github.com/gukt
 */
public class ApplicationIndependentTests {

    @Test
    void testMapUtils() {
        // 测试四种方式创建 HashMap
        //
        Map<String, String> map1 = MapUtils.newHashMap();
        Map<String, String> map2 = MapUtils.newHashMap("k1", "v1");

        Map<String, String> map31 = MapUtils.newHashMap(new Object[][]{
                {"k1"},
                {"k2", "v2"}
        });

        User user = new User();
        user.setName("foo");
        user.setPassword("bar");
        Map<String, Object> map4 = BeanUtils.toMap(user, "name", "password");
    }

    @Test
    void test111() {
        User user = new User();
        user.setName("foo");
        user.setPassword("111");
        System.out.println(JsonUtils.toJson(user));

        String json = "{\"name\": \"xxx\", \"password\": \"aaaaa\"}";
        System.out.println(JsonUtils.toBean(json, User.class));
    }

    @Test
    void test1() {
        String json = "{ \"id\": 1, \"name\": \"xxx\" }";
        User u = JsonUtils.toBean(json, User.class);
        System.out.println(u);

//        final BeanWrapper src = new BeanWrapperImpl(u);
//        PropertyDescriptor[] pds = src.getPropertyDescriptors();
//        Set<String> nullProperties = new HashSet<>();
//        for (PropertyDescriptor pd : pds) {
//            Object srcValue = src.getPropertyValue(pd.getName());
//            if (srcValue == null) {
//                nullProperties.add(pd.getName());
//            }
//        }
//        String[] result = new String[nullProperties.size()];
    }
}
