/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import com.codedog.rainbow.tcp.util.MessageUtils;
import com.codedog.rainbow.util.MapUtils;
import com.codedog.rainbow.tcp.JsonPacket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * MessageUtilsTests class
 *
 * @author https://github.com/gukt
 */
public class MessageUtilsTests {

    @Test
    void test1() {
        Map<String, Object> map = MapUtils.newHashMap();
        JsonPacket packet = JsonPacket.of("GameEnterRequest", map);
        Object value = MessageUtils.resolveArgs(packet, List.class);
        System.out.println(value);

        packet = JsonPacket.of("GameEnterRequest", new ArrayList<>());
        // 验证类型匹配的情况
        value = MessageUtils.resolveArgs(packet, List.class);
        Assertions.assertNotNull(value);
        Assertions.assertEquals(List.class, value.getClass());
        // 验证类型不匹配的情况
        value = MessageUtils.resolveArgs(packet, Set.class);
        Assertions.assertNotNull(value);
        Assertions.assertEquals(Set.class, value.getClass());
    }

    @Test
    void testResolveArgToSet() {
        JsonPacket packet = JsonPacket.of("GameEnterRequest", new HashSet<>());
        // 验证类型匹配的情况
        Object value = MessageUtils.resolveArgs(packet, Set.class);
        Assertions.assertNotNull(value);
        Assertions.assertEquals(HashSet.class, value.getClass());
        // // 验证 Set -> List
        value = MessageUtils.resolveArgs(packet, List.class);
        Assertions.assertNotNull(value);
        Assertions.assertEquals(ArrayList.class, value.getClass());

        packet = JsonPacket.of("GameEnterRequest", new ArrayList<>());
        // 验证类型匹配的情况
        value = MessageUtils.resolveArgs(packet, List.class);
        Assertions.assertNotNull(value);
        Assertions.assertEquals(ArrayList.class, value.getClass());
        // 验证 List -> Set
        value = MessageUtils.resolveArgs(packet, Set.class);
        Assertions.assertNotNull(value);
        Assertions.assertEquals(HashSet.class, value.getClass());
    }

    @Test
    void test2() {
        Map map1 = new HashMap();
        Map map2 = new LinkedHashMap();

        System.out.println(LinkedHashMap.class.isAssignableFrom(Map.class));
        System.out.println(Map.class.isAssignableFrom(HashMap.class));
        System.out.println(Map.class.isAssignableFrom(LinkedHashMap.class));

        System.out.println(map1);
        System.out.println(map2);


        int n = 1;
        String s;
        // s = n.toString(); // error
        s = String.valueOf(n); // 这样才可以
        Integer n1 = 1;
        s = n1.toString(); // 这样就可以了，但是如果为 null 呢？
        s = String.valueOf(n1); // 用这个最把稳，因为 内部是怎样写的： (obj == null) ? "null" : obj.toString();

        long[] b = {1, 2, 3, 4, 5};
        System.out.println(b.toString());
        System.out.println(Arrays.toString(b));

        // GameEnterRequest request = GameEnterRequest.newBuilder().setUid(1).build();
        // Object argValue = MessageUtils.resolveArgs(ProtoUtils.wrap(request), BanRequest.class);
        // System.out.println("argValue:" + argValue);
    }
}
