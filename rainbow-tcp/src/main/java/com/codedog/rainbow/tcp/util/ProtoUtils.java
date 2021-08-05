/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.util;

import com.codedog.rainbow.lang.TypeMismatchException;
import com.codedog.rainbow.tcp.message.MessageHandler;
import com.codedog.rainbow.util.Assert;
import com.codedog.rainbow.world.generated.CommonProto.Error;
import com.codedog.rainbow.world.generated.CommonProto.Error.ErrorCode;
import com.codedog.rainbow.world.generated.CommonProto.ErrorOrBuilder;
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
    public static ProtoPacketOrBuilder wrapPacket(Object message) {
        return wrapPacket(message, false);
    }

    /**
     * 将指定的对象，包装成 {@link ProtoPacketOrBuilder ProtoPacketOrBuilder} 实例。
     * 如果已经是 ProtoPacketOrBuilder 类型的对象，则原样返回；
     * 如果是其他类型，则根据消息的 {@link Object#getClass() 类型} 推断出 {@link MsgType MsgType}，
     * 然后包装成一个 {@link ProtoPacket.Builder ProtoPacket.Builder} 对象实例返回。
     *
     * @param message    包装前的消息，不能为 null
     * @param forceBuild 如果返回值是 {@link Builder Message.Builder} 类型的话，是否强制将返回值 build 一下
     * @return ProtoPacketOrBuilder 对象实例
     * @apiNote 除非参数指定的原对象本身就是 {@link ProtoPacket ProtoPacket}，否则包装过的对象一律都以 {@link ProtoPacket.Builder ProtoPacket.Builder} 类型返回。
     */
    public static ProtoPacketOrBuilder wrapPacket(Object message, boolean forceBuild) {
        Assert.notNull(message, "message");
        if (message instanceof ProtoPacketOrBuilder) {
            return forceBuild
                    ? (ProtoPacket) buildIfPossible((ProtoPacketOrBuilder) message)
                    : (ProtoPacketOrBuilder) message;
        }
        ProtoPacket.Builder builder = ProtoPacket.newBuilder()
                .setType(deduceMessageType(message))
                .setPayload(toByteString(message));
        return forceBuild ? builder.build() : builder;
    }

    public static ErrorOrBuilder errorOf(int code, String msg, boolean build) {
        ErrorCode errorCode = ErrorCode.forNumber(code);
        if (errorCode == null) {
            log.error("Cannot convert ErrorCode from code: {}, msg={}", code, msg);
            errorCode = ErrorCode.UNKNOWN;
        }
        Error.Builder builder = Error.newBuilder().setCode(errorCode).setMsg(msg);
        return build ? builder.build() : builder;
    }

    public static Error errorOf(int code, String msg) {
        return (Error) errorOf(code, msg, true);
    }

    public static <V extends MessageHandler.Error> ErrorOrBuilder errorOf(V error, boolean build) {
        return errorOf(error.getCode(), error.getMsg(), build);
    }

    public static <V extends MessageHandler.Error> Error errorOf(V error) {
        return (Error) errorOf(error, true);
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

    /**
     * 判断指定的对象是否是一个实现了 {@link Builder Builder} 对象，如果是则调用它的 build 方法返回，反之直接返回。
     *
     * @param messageOrBuilder MessageLiteOrBuilder 对象，可以为 null，为 null 时返回 null
     * @param <V>              可以接受的参数类型
     * @return MessageLite 对象（可能原本就是，或者是通过 build 方法构建出来的）
     */
    public static <V extends MessageLiteOrBuilder> MessageLite buildIfPossible(V messageOrBuilder) {
        if (messageOrBuilder instanceof Builder) {
            return ((MessageLite.Builder) messageOrBuilder).build();
        } else {
            return (MessageLite) messageOrBuilder;
        }
    }

    /**
     * 从一个 {@link MessageLiteOrBuilder MessageLiteOrBuilder} 类型的对象中安全地返回 {@link MessageLite.Builder MessageLite.Builder} 实例。
     * 如果对象已经是 {@link MessageLite.Builder MessageLite.Builder} 类型，则直接返回；
     * 反之，调用对象的 {@link MessageLite#toBuilder() toBuilder()} 方法返回一个 MessageLite.Builder 实例
     *
     * @param message 目标对象，不能为 null
     * @param <V>     返回值类型，继承自 {@link MessageLite.Builder}
     * @param <E>     目标对象的类型，继承自 {@link MessageLiteOrBuilder MessageLiteOrBuilder}
     * @return 继承自 {@link MessageLite.Builder MessageLite.Builder} 的对象实例。
     * @see #safeGetBuilder(Object)
     */
    @SuppressWarnings("unchecked")
    public static <V extends MessageLite.Builder, E extends MessageLiteOrBuilder> V safeGetBuilder(E message) {
        Assert.notNull(message, "message");
        if (message instanceof MessageLite.Builder) {
            return (V) message;
        } else {
            return (V) ((MessageLite) message).toBuilder();
        }
    }

    public static <V extends MessageLite.Builder> V safeGetBuilder(Object message) {
        return safeGetBuilder(requireMessageLiteOrBuilder(message, "message"));
    }

    public static MessageLiteOrBuilder requireMessageLiteOrBuilder(Object message, String name) {
        Assert.notNull(message, name);
        Assert.isAssignableFrom(message.getClass(), MessageLiteOrBuilder.class);
        return (MessageLiteOrBuilder) message;
    }
}

