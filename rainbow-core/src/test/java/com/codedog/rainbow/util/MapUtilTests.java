/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class MapUtilTests {

    @Test
    void testValueRetrieving() {
        Map<Object, Object> map = new HashMap<>();
        map.put("int1", 1);
        map.put("long1", 2L);
        map.put("boolean1", false);
        map.put("k1", "v1");
        map.put("roleId1", 1);
        map.put("roleId2", 2L);

        MapHelper.getLong(map, "roleId1");
        MapHelper.getInt(map, "roleId2");

        Integer int1 = MapHelper.getInt(map, "int1");
        Assertions.assertEquals(1, int1);

        Integer int2 = MapHelper.getInt(map, "int2");
        Assertions.assertNull(int2);

        int2 = MapHelper.getIntOrDefault(map, "int2", 100);
        Assertions.assertEquals(100, int2);

        Assertions.assertThrows(RuntimeException.class, () -> {
            // 将一个 Boolean 型转换为 Integer 的情况
            MapHelper.getInt(map, "boolean1");
        });

        Assertions.assertThrows(RuntimeException.class, () -> {
            // 将一个 String 型转换为 Integer 的情况
            MapHelper.getInt(map, "k1");
        });

        Assertions.assertThrows(RuntimeException.class, () -> {
            // 将一个 Integer 型转换为 Boolean  的情况
            MapHelper.getBoolean(map, "int1");
        });
    }

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

        // User user = new User();
        // user.setName("foo");
        // user.setPassword("bar");
        // Map<String, Object> map4 = BeanUtils.toMap(user, "name", "password");
    }

}
