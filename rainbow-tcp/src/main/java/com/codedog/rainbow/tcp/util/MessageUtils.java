/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.util;

import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.codedog.rainbow.tcp.JsonPacket;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLiteOrBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * MessageUtils class
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class MessageUtils {

    @Nullable
    public static Object resolveArgs(Object message, Class<?> expectedArgType) {
        if (expectedArgType.isAssignableFrom(message.getClass())) {
            return message;
        }
        // 如果消息类型是 ProtoPacket
        if (message instanceof ProtoPacket) {
            ByteString payload = ((ProtoPacket) message).getPayload();
            // 如果参数类型是 ByteString（或其子类），直接将 payload 返回
            if (ByteString.class.isAssignableFrom(expectedArgType)) {
                return payload;
            }
            // 如果参数绑定对象为 GameEnterRequest 等消息体，它们的类型为 MessageLiteOrBuilder
            // 此时，需要调用 parseFrom 静态方法将 ByteString 类型的 payload 解析成期望类型的对象
            if (MessageLiteOrBuilder.class.isAssignableFrom(expectedArgType)) {
                try {
                    // 获得静态方法
                    Method parseFrom = expectedArgType.getMethod("parseFrom", ByteString.class);
                    try {
                        // 调用静态方法
                        return parseFrom.invoke(null, payload);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.warn("Cannot invoke the 'parseFrom' static method of {}", expectedArgType, e);
                        return null;
                    }
                } catch (NoSuchMethodException e) {
                    log.warn("The 'parseFrom(ByteString)' static method not found in {}", expectedArgType.getName());
                }
            }
        } else if (message instanceof JsonPacket) {
            Object payload = ((JsonPacket) message).getPayload();
            // JsonPacket 是可以不带 payload 的, 此种情况一律返回 null
            if (payload == null) return null;
            // 类型兼容直接返回
            if (expectedArgType.isAssignableFrom(payload.getClass())) {
                return payload;
            }
            // Set -> List
            if (Set.class.isAssignableFrom(expectedArgType)) {
                if (LinkedHashSet.class.isAssignableFrom(expectedArgType)) {
                    return new LinkedHashSet<>((Collection<?>) payload);
                }
                return new HashSet<>((Collection<?>) payload);
            }
            // List -> Set
            if (List.class.isAssignableFrom(expectedArgType)) {
                if (LinkedList.class.isAssignableFrom(expectedArgType)) {
                    return new LinkedList<>((Collection<?>) payload);
                }
                return new ArrayList<>((Collection<?>) payload);
            }
            log.warn("Cannot assign the payload [{}] to the argument (expected: {})", payload.getClass(), expectedArgType);

            // // 如果参数类型为集合或 Map 类型，意味着都需要从 payload 中解析
            // if (Collection.class.isAssignableFrom(expectedArgType) || Map.class.isAssignableFrom(expectedArgType)) {
            //     // 类型兼容直接返回
            //     if (expectedArgType.isAssignableFrom(payload.getClass())) {
            //         return payload;
            //     }
            //     // Set -> List
            //     if (Set.class.isAssignableFrom(expectedArgType)) {
            //         return new HashSet<>((Collection<?>) payload);
            //     }
            //     // List -> Set
            //     if (List.class.isAssignableFrom(expectedArgType)) {
            //         return new ArrayList<>((Collection<?>) payload);
            //     }
            //     log.warn("Cannot assign the payload [{}] to the argument (expected: {})", payload.getClass(), expectedArgType);
            // }
        }
        return null;
    }

    // public static Object errorOf(MessageResolver<?> resolver, ErrorEnum err) {
    //     Assert.notNull(resolver, "resolver");
    //     return resolver.errorOf(err.getCode(), err.getMsg());
    // }

    // @SuppressWarnings("unchecked")
    // private static <V extends Collection<V>> V castCollection(Object payload, Class<V> expectedType) {
    //     if (expectedType.isAssignableFrom(payload.getClass())) {
    //         return (V) payload;
    //     }
    //     // 如果期望返回 List（或其子类）
    //     if (List.class.isAssignableFrom(expectedType)) {
    //         return (V) new ArrayList<>((Collection<V>) payload);
    //     }
    //     // 如果期望返回 Set（或其子类）
    //     if (Set.class.isAssignableFrom(expectedType)) {
    //         return (V) new HashSet<>((Collection<V>) payload);
    //     }
    //     return null;
    // }
    //
    // private static <V> V cast(Object payload, Class<V> expectedType) {
    //     try {
    //         System.out.println(expectedType);
    //         @SuppressWarnings("unchecked")
    //         V value = (V) payload;
    //         return value;
    //     } catch (Exception e) {
    //         log.warn("Cannot convert the payload to type {}: {}", expectedType.getSimpleName(), payload);
    //         return null;
    //     }
    // }
    //
    // private static void typeMismatched(Object obj, Class<?> expectedArgType) {
    //     log.warn("Cannot convert the payload to type {}: {}", expectedArgType.getSimpleName(), obj);
    // }
}
