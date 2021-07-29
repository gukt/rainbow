/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.util;

import com.codedog.rainbow.tcp.PacketWrapper;
import com.codedog.rainbow.world.generated.BanRequest;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.codedog.rainbow.world.generated.GameEnterRequest;
import com.codedog.rainbow.world.net.json.JsonPacket;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLiteOrBuilder;
import lombok.extern.slf4j.Slf4j;

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

    public static <V> Object resolveArgs(Object source, Class<V> expectedType) {
        if (source.getClass().equals(expectedType)) {
            return source;
        }
        if (source instanceof ProtoPacket) {
            ProtoPacket packet = (ProtoPacket) source;
            if (ByteString.class.isAssignableFrom(expectedType)) {
                return packet.getPayload();
            }
            if (MessageLiteOrBuilder.class.isAssignableFrom(expectedType)) {
                try {
                    // 获得静态方法
                    Method parseFrom = expectedType.getMethod("parseFrom", ByteString.class);
                    try {
                        // 调用静态方法
                        return parseFrom.invoke(null, packet.getPayload());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.warn("Cannot invoke the 'parseFrom' static method of {}", expectedType, e);
                        return null;
                    }
                } catch (NoSuchMethodException e) {
                    log.warn("The 'parseFrom(ByteString)' static method not found in {}", expectedType.getName());
                }
            }
        } else if (source instanceof JsonPacket) {
            JsonPacket packet = (JsonPacket) source;
            try {
                if (Set.class.isAssignableFrom(expectedType)) {
                    return packet.getPayload();
                    // return new HashSet<>((Collection<V>) packet.getPayload());
                } else if (List.class.isAssignableFrom(expectedType)) {
                    return packet.getPayload();
                    // return new ArrayList<>((Collection<V>) packet.getPayload());
                } else if (Map.class.isAssignableFrom(expectedType)) {
                    return packet.getPayload();
                    // return new HashMap<>((Map<?,?>) packet.getPayload());
                }
            } catch (Exception e) {
                log.warn("Cannot convert the payload to type {}: {}", expectedType.getSimpleName(), packet.getPayload());
            }
        }
        return null;
    }

    public static void main(String[] args) {
        GameEnterRequest request = GameEnterRequest.newBuilder().setUid(1).build();
        Object argValue = MessageUtils.resolveArgs(PacketWrapper.wrap(request), BanRequest.class);
        System.out.println("argValue:" + argValue);
    }
}
