/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.util;

import com.codedog.rainbow.lang.TypeMismatchException;
import com.codedog.rainbow.util.Assert;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket.MsgType;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacketOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;

/**
 * ProtoUtils class
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class ProtoUtils {

    /**
     * 将指定的对象，包装成 {@link ProtoPacketOrBuilder ProtoPacketOrBuilder} 实例。
     * 如果已经是 ProtoPacketOrBuilder 类型的对象，则原样返回；
     * 如果是其他类型，则根据消息的 {@link Object#getClass() 类型} 推断出 {@link MsgType MsgType}，
     * 然后包装成一个 {@link ProtoPacket.Builder ProtoPacket.Builder} 对象实例返回。
     *
     * @param message 包装前的消息，不能为 null
     * @return ProtoPacketOrBuilder 对象实例
     * @apiNote 除非参数指定的原对象本身就是 {@link ProtoPacket ProtoPacket}，否则包装过的对象一律都以 {@link ProtoPacket.Builder ProtoPacket.Builder} 类型返回。
     */
    public static ProtoPacketOrBuilder wrap(Object message) {
        return wrap(message, false);
    }

    /**
     * 将指定的对象，包装成 {@link ProtoPacketOrBuilder ProtoPacketOrBuilder} 实例。
     * 如果已经是 ProtoPacketOrBuilder 类型的对象，则原样返回；
     * 如果是其他类型，则根据消息的 {@link Object#getClass() 类型} 推断出 {@link MsgType MsgType}，
     * 然后包装成一个 {@link ProtoPacket.Builder ProtoPacket.Builder} 对象实例返回。
     *
     * @param message    包装前的消息，不能为 null
     * @param forceBuild 是否强制将返回值 build 一下
     * @return ProtoPacketOrBuilder 对象实例
     * @apiNote 除非参数指定的原对象本身就是 {@link ProtoPacket ProtoPacket}，否则包装过的对象一律都以 {@link ProtoPacket.Builder ProtoPacket.Builder} 类型返回。
     */
    public static ProtoPacketOrBuilder wrap(Object message, boolean forceBuild) {
        Assert.notNull(message, "message");
        if (message instanceof ProtoPacketOrBuilder) {
            return  forceBuild
                    ? (ProtoPacket)buildIfPossible((ProtoPacketOrBuilder) message)
                    : (ProtoPacketOrBuilder) message;
        }
        ProtoPacket.Builder builder = ProtoPacket.newBuilder()
                .setType(deduceMessageType(message))
                .setPayload(toByteString(message));
        return forceBuild ? builder.build() : builder;
    }

    /**
     * 将指定的对象转换为 {@link ByteString} 类型，如果对象为 null，则返回 null。
     *
     * @param message 待转的对象，可以为 null，如果为 null 则返回 null
     * @return ByteString类型对象，可能为 null
     * @throws TypeMismatchException 如果对象类型不是 {@link MessageLiteOrBuilder}
     */
    @Nullable
    public static ByteString toByteString(Object message) {
        if (message == null) return null;
        if (!(message instanceof MessageLiteOrBuilder)) {
            throw new TypeMismatchException(message, MessageLiteOrBuilder.class);
        }
        if (message instanceof Message.Builder) {
            return ((Builder) message).build().toByteString();
        } else {
            return ((Message) message).toByteString();
        }
    }

    /**
     * 根据类型类型，推断出 {@link MsgType} 对象。
     *
     * @param message 将被推断的对象，不能为 null
     * @return MsgType 枚举实例
     */
    public static MsgType deduceMessageType(Object message) {
        Assert.notNull(message, "message");
        String typeName = message.getClass().getCanonicalName();
        // 例如: *.generated.CommonProto.Echo.Builder 这种，要将后面的 .Builder 去掉。
        if (typeName.endsWith(".Builder")) {
            // 取倒数第二个 "."，到最后一个"." 之间的内容
            int lastIndex = typeName.lastIndexOf(".");
            typeName = typeName.substring(typeName.lastIndexOf(".", lastIndex - 1) + 1, lastIndex);
        } else {
            // 取最后一个"." 之后的内容
            typeName = typeName.substring(typeName.lastIndexOf(".") + 1);
        }
        return MsgType.valueOf(typeName);
    }

    public static <V extends MessageLiteOrBuilder> MessageLite buildIfPossible(V messageOrBuilder) {
        if (messageOrBuilder instanceof Builder) {
            return ((MessageLite.Builder) messageOrBuilder).build();
        } else {
            return (MessageLite) messageOrBuilder;
        }
    }

//     private static final Map<String, MsgType> messageTypesByName = new HashMap<>();
//     // private static final AtomicInteger SEQUENCE = new AtomicInteger(0);
//
//     static {
//         for (MsgType type : MsgType.values()) {
//             messageTypesByName.put(type.name().replace("_", "").toLowerCase(), type);
//         }
//     }
//
//     public static ProtoPacket wrap(MessageLiteOrBuilder message) {
//         if (message instanceof ProtoPacket) {
//             return ((ProtoPacket) message);
//         }
//         if (message instanceof ProtoPacket.Builder) {
//             return ((ProtoPacket.Builder) message).build();
//         }
//         ByteString bytes = toByteString(message);
//         ProtoPacket.Builder builder = ProtoPacket.newBuilder();
//         builder.setType(getMessageType(message));
//         builder.setPayload(bytes);
//         return builder.build();
//     }
//
//     // public static ProtoPacket wrap(MessageLiteOrBuilder message, int syncId) {
//     //     if (message instanceof ProtoPacket) {
//     //         return ((ProtoPacket) message);
//     //     }
//     //     if (message instanceof ProtoPacket.Builder) {
//     //         return ((ProtoPacket.Builder) message).build();
//     //     }
//     //     // TODO 如果是 builder 就不需要 newBuilder 了
//     //     return ProtoPacket.newBuilder()
//     //             .setSync(syncId)
//     //             .setType(getMessageType(message))
//     //             .setPayload(toByteString(message))
//     //             .build();
//     // }
//
// //    public static Packet.Builder wrapBuilder(MessageLiteOrBuilder message) {
// //        if (message instanceof Packet) {
// //            return ((Packet) message).toBuilder();
// //        }
// //
// //        if (message instanceof Packet.Builder) {
// //            return (Packet.Builder) message;
// //        }
// //
// //        ByteString bytes = toByteString(message);
// //
// //        Packet.Builder builder = Packet.newBuilder();
// //        builder.setSequence(SEQUENCE.incrementAndGet());
// //
// //        MessageType messageType = getMessageType(message);
// //
// //        builder.setType(messageType);
// //        builder.setData(bytes);
// //
// //        return builder;
// //    }
//
//     private static ByteString toByteString(MessageLiteOrBuilder message) {
//         ByteString bytes = null;
//         if (message instanceof MessageLite) {
//             bytes = ((MessageLite) message).toByteString();
//         } else if (message instanceof MessageLite.Builder) {
//             bytes = ((MessageLite.Builder) message).build().toByteString();
//         }
//         return bytes;
//     }
//
//     public static MsgType getMessageType(MessageLiteOrBuilder message) {
//         String key = null;
//         if (message instanceof MessageLite) {
//             key = message.getClass().getSimpleName();
//         }
//         if (message instanceof MessageLite.Builder) {
//             key = ((MessageLite.Builder) message).build().getClass().getSimpleName();
//         }
//         MsgType type = messageTypesByName.get(Objects.requireNonNull(key).toLowerCase());
//         if (type == null) {
//             log.error("没有找到{}对应的MessageType枚举项", key);
//             throw new RuntimeException("没有找到" + key + "对应的MessageType枚举项");
//         }
//         return type;
//     }
}

