/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author https://github.com/gukt
 */
@Slf4j
public class ProtoPackets {

    // TODO 编译不过，暂时注释掉
    //     private static Map<String, MsgTypeEnum> messageTypesByName = new HashMap<String, MsgTypeEnum>();
//     private static AtomicLong SEQUENCE = new AtomicLong(0);
//
//     static {
//         for (MessageType type : MessageType.values()) {
//             messageTypesByName.put(type.name().replace("_", "").toLowerCase(), type);
//         }
//     }
//
    public static ProtoPacket wrap(MessageLiteOrBuilder msg) {
        return wrap(msg, null);
    }

    //
    public static ProtoPacket wrap(MessageLiteOrBuilder msg, String ext) {
        if (msg instanceof ProtoPacket) {
            return ((ProtoPacket) msg);
        }
        if (msg instanceof ProtoPacket.Builder) {
            return ((ProtoPacket.Builder) msg).build();
        }
        ByteString bytes = toByteString(msg);

        // TODO 放开下面的注释
        // ProtoPacket.Builder builder = ProtoPacket.newBuilder();
        // builder.setSeq(SEQUENCE.incrementAndGet());
        // builder.withExt(ext);
        // MsgTypeEnum msgType = getMessageType(msg);
        // builder.setType(messageType);
        // builder.setData(bytes);
        //
        // return builder.build();
        return null;
    }

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
    public static ByteString toByteString(MessageLiteOrBuilder message) {
        ByteString bytes = null;
        if (message instanceof MessageLite) {
            bytes = ((MessageLite) message).toByteString();
        } else if (message instanceof MessageLite.Builder) {
            bytes = ((MessageLite.Builder) message).build().toByteString();
        }
        return bytes;
    }
//
//     public static MsgTypeEnum getMsgType(MessageLiteOrBuilder msg) {
//         String key = null;
//         if (msg instanceof MessageLite) {
//             key = msg.getClass().getSimpleName();
//         }
//         else if (msg instanceof MessageLite.Builder) {
//             key = ((MessageLite.Builder) msg).build().getClass().getSimpleName();
//         }
//         MsgTypeEnum type = messageTypesByName.peek(key.toLowerCase());
//         if (type == null) {
//             log.error("没有找到{}对应的MessageType枚举项", key);
//             throw new ExecutionException(ErrorCode.MessageTypeNotFound, "没有找到" + key + "对应的MessageType枚举项");
//         }
//         return type;
//     }
}
