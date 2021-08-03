/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import com.codedog.rainbow.tcp.JsonPacket;
import com.codedog.rainbow.tcp.util.MessageUtils;
import com.codedog.rainbow.tcp.util.ProtoUtils;
import com.codedog.rainbow.util.MapUtils;
import com.codedog.rainbow.world.generated.CommonProto.Echo;
import com.codedog.rainbow.world.generated.CommonProto.EchoOrBuilder;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket.MsgType;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacketOrBuilder;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.MessageOrBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.codedog.rainbow.tcp.util.ProtoUtils.wrap;

/**
 * MessageUtilsTests class
 *
 * @author https://github.com/gukt
 */
public class MessageUtilsTests {

    private static final Logger log = LoggerFactory.getLogger(MessageUtilsTests.class);

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

    @Test
    void testMessageWrapper() {
        Object msg1 = Echo.getDefaultInstance();
        Object msg2 = Echo.getDefaultInstance();
        // 消息的 getDefaultInstance 静态方法返回单例
        Assertions.assertEquals(msg1, msg2);

        Object msg3 = Echo.newBuilder();
        Object msg4 = Echo.newBuilder();
        // newBuilder 每次返回的都是不一样的对象，想想也是
        Assertions.assertNotEquals(msg3, msg4);

        Echo msg5 = Echo.getDefaultInstance();
        Echo.Builder msg6 = Echo.newBuilder();
        Assertions.assertTrue(Echo.Builder.class.isAssignableFrom(msg6.getClass()));
        Assertions.assertTrue(EchoOrBuilder.class.isAssignableFrom(msg6.getClass()));
        Assertions.assertTrue(Message.Builder.class.isAssignableFrom(msg6.getClass()));
        Assertions.assertTrue(MessageOrBuilder.class.isAssignableFrom(msg6.getClass()));
        Assertions.assertTrue(MessageLite.Builder.class.isAssignableFrom(msg6.getClass()));
        Assertions.assertTrue(MessageLiteOrBuilder.class.isAssignableFrom(msg6.getClass()));
        // builder 类型是不可以赋值给 *OrBuilder 或 .Builder 类型的
        Assertions.assertFalse(Echo.class.isAssignableFrom(msg6.getClass()));
        Assertions.assertTrue(Echo.class.isAssignableFrom(msg5.getClass()));
        Assertions.assertFalse(Message.class.isAssignableFrom(msg6.getClass()));
        Assertions.assertTrue(Message.class.isAssignableFrom(msg5.getClass()));

        Echo.Builder msg7 = Echo.newBuilder();
        Assertions.assertTrue(msg7.isInitialized());
        Assertions.assertTrue(msg7.build().isInitialized());
        Echo msg8 = Echo.getDefaultInstance();
        Assertions.assertTrue(msg8.isInitialized());

        Echo msg9 = Echo.getDefaultInstance();
        Message.Builder msg10 = msg9.toBuilder();
        Message.Builder msg11 = msg9.toBuilder();
        Message.Builder msg12 = msg10.clone();

        Echo msg13 = msg9.toBuilder().build();
        Echo msg14 = msg9.toBuilder().build();

        Echo msg15 = msg9.toBuilder().buildPartial();
        Echo msg16 = msg9.toBuilder().buildPartial();

        System.out.println("here");
        // Object message2 = Echo.newBuilder();
        // Object message3 = Echo.newBuilder().setText("aaa");
        // Object message4 = Echo.newBuilder();
        // Object message5 = Echo.newBuilder().build();
        // Object message6 = Echo.newBuilder().buildPartial();


        Echo echo1 = Echo.newBuilder().setText("foo").build();
        Object message8 = Echo.newBuilder(echo1);

        // Object wrapped = wrap(message1);
    }

    @Test
    void test3() {
        // Wrap Message.Builder
        Object msg1 = Echo.newBuilder().setText("xxx");
        System.out.println(wrap(msg1));

        ProtoPacketOrBuilder message = ProtoUtils.wrap(msg1);
        System.out.println(message);

        // Wrap Message
        Object msg2 = Echo.newBuilder().setText("xxx").build();
        System.out.println(wrap(msg2));

        // Wrap Message
        Object msg3 = Echo.newBuilder().setText("xxx").buildPartial();
        System.out.println(wrap(msg3));

        Message.Builder msg4 = ProtoPacket.newBuilder()
                .setType(MsgType.Echo)
                .setPayload(Echo.newBuilder().setText("foo").build().toByteString());
        // Wrap ProtoPacket.Builder
        System.out.println(wrap(msg4));
        // Wrap ProtoPacket
        Assertions.assertEquals(ProtoPacket.class, wrap(msg4.build()).getClass());
        System.out.println();

    }

    // /**
    //  * 将指定的对象，包装成 {@link ProtoPacketOrBuilder ProtoPacketOrBuilder} 实例。
    //  * 如果已经是 ProtoPacketOrBuilder 类型的对象，则原样返回；
    //  * 如果是其他类型，则根据消息的 {@link Object#getClass() 类型} 推断出 {@link MsgType MsgType}，
    //  * 然后包装成一个 {@link ProtoPacket.Builder ProtoPacket.Builder} 对象实例返回。
    //  *
    //  * @param message 包装前的消息，不能为 null
    //  * @return ProtoPacketOrBuilder 对象实例
    //  * @apiNote 除非参数指定的原对象本身就是 {@link ProtoPacket ProtoPacket}，否则包装过的对象一律都以 {@link ProtoPacket.Builder ProtoPacket.Builder} 类型返回。
    //  */
    // private ProtoPacketOrBuilder wrap(Object message) {
    //     Assert.notNull(message, "message");
    //     if (message instanceof ProtoPacketOrBuilder) {
    //         return (ProtoPacketOrBuilder) message;
    //     }
    //     return ProtoPacket.newBuilder()
    //             .setType(deduceMessageType(message))
    //             .setPayload(toByteString(message));
    // }
    //
    // /**
    //  * 将指定的对象转换为 {@link ByteString} 类型，如果对象为 null，则返回 null。
    //  *
    //  * @param message 待转的对象，可以为 null，如果为 null 则返回 null
    //  * @return ByteString类型对象，可能为 null
    //  * @throws TypeMismatchException 如果对象类型不是 {@link MessageLiteOrBuilder}
    //  */
    // @Nullable
    // public static ByteString toByteString(Object message) {
    //     if (message == null) return null;
    //     if (!(message instanceof MessageLiteOrBuilder)) {
    //         throw new TypeMismatchException(message, MessageLiteOrBuilder.class);
    //     }
    //     if (message instanceof Message.Builder) {
    //         return ((Builder) message).build().toByteString();
    //     } else {
    //         return ((Message) message).toByteString();
    //     }
    // }
    //
    // /**
    //  * 根据类型类型，推断出 {@link MsgType} 对象。
    //  *
    //  * @param message 将被推断的对象，不能为 null
    //  * @return MsgType 枚举实例
    //  */
    // public static MsgType deduceMessageType(Object message) {
    //     Assert.notNull(message, "message");
    //     String typeName = message.getClass().getCanonicalName();
    //     // 例如: *.generated.CommonProto.Echo.Builder 这种，要将后面的 .Builder 去掉。
    //     if (typeName.endsWith(".Builder")) {
    //         // 取倒数第二个 "."，到最后一个"." 之间的内容
    //         int lastIndex = typeName.lastIndexOf(".");
    //         typeName = typeName.substring(typeName.lastIndexOf(".", lastIndex - 1) + 1, lastIndex);
    //     } else {
    //         // 取最后一个"." 之后的内容
    //         typeName = typeName.substring(typeName.lastIndexOf(".") + 1);
    //     }
    //     return MsgType.valueOf(typeName);
    // }
}
