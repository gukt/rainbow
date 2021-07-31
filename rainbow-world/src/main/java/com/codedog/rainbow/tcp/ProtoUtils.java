/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.world.generated.CommonProto.MsgTypeEnum;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ProtoUtils class
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class ProtoUtils {

    private static Map<String, MsgTypeEnum> messageTypesByName = new HashMap<>();
    private static AtomicInteger SEQUENCE = new AtomicInteger(0);

    static {
        for (MsgTypeEnum type : MsgTypeEnum.values()) {
            messageTypesByName.put(type.name().replace("_", "").toLowerCase(), type);
        }
    }

    public static ProtoPacket wrap(MessageLiteOrBuilder message) {
        if (message instanceof ProtoPacket) {
            return ((ProtoPacket) message);
        }

        if (message instanceof ProtoPacket.Builder) {
            return ((ProtoPacket.Builder) message).build();
        }

        ByteString bytes = toByteString(message);

        ProtoPacket.Builder builder = ProtoPacket.newBuilder();
        builder.setSn(SEQUENCE.incrementAndGet());
        MsgTypeEnum messageType = getMessageType(message);
        builder.setType(messageType);
        builder.setPayload(bytes);

        return builder.build();
    }

    public static ProtoPacket wrap(MessageLiteOrBuilder message, int syncId) {
        if (message instanceof ProtoPacket) {
            return ((ProtoPacket) message);
        }
        if (message instanceof ProtoPacket.Builder) {
            return ((ProtoPacket.Builder) message).build();
        }
        // TODO 如果是 builder 就不需要 newBuilder 了
        return ProtoPacket.newBuilder()
                .setSn(SEQUENCE.incrementAndGet())
                .setSync(syncId)
                .setType(getMessageType(message))
                .setPayload(toByteString(message))
                .build();
    }

//    public static Packet.Builder wrapBuilder(MessageLiteOrBuilder message) {
//        if (message instanceof Packet) {
//            return ((Packet) message).toBuilder();
//        }
//
//        if (message instanceof Packet.Builder) {
//            return (Packet.Builder) message;
//        }
//
//        ByteString bytes = toByteString(message);
//
//        Packet.Builder builder = Packet.newBuilder();
//        builder.setSequence(SEQUENCE.incrementAndGet());
//
//        MessageType messageType = getMessageType(message);
//
//        builder.setType(messageType);
//        builder.setData(bytes);
//
//        return builder;
//    }

    private static ByteString toByteString(MessageLiteOrBuilder message) {
        ByteString bytes = null;
        if (message instanceof MessageLite) {
            bytes = ((MessageLite) message).toByteString();
        } else if (message instanceof MessageLite.Builder) {
            bytes = ((MessageLite.Builder) message).build().toByteString();
        }
        return bytes;
    }

    public static MsgTypeEnum getMessageType(MessageLiteOrBuilder message) {
        String key = null;
        if (message instanceof MessageLite) {
            key = message.getClass().getSimpleName();
        }
        if (message instanceof MessageLite.Builder) {
            key = ((MessageLite.Builder) message).build().getClass().getSimpleName();
        }
        MsgTypeEnum type = messageTypesByName.get(Objects.requireNonNull(key).toLowerCase());
        if (type == null) {
            log.error("没有找到{}对应的MessageType枚举项", key);
            throw new RuntimeException("没有找到" + key + "对应的MessageType枚举项");
        }
        return type;
    }
}

