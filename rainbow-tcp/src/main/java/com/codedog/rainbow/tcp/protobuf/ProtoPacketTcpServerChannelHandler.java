/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.protobuf;

import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.TcpServerChannelHandler;
import com.codedog.rainbow.tcp.session.DefaultSession;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.util.ProtoUtils;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket.Builder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.channel.ChannelHandlerContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;


/**
 * ProtoPacketTcpServerChannelHandler
 *
 * @author https://github.com/gukt
 */
@Slf4j
public final class ProtoPacketTcpServerChannelHandler extends TcpServerChannelHandler<ProtoPacket> {

    public ProtoPacketTcpServerChannelHandler(TcpProperties properties) {
        super(properties);
    }

    @Override
    protected Session newSession(ChannelHandlerContext delegate) {
        return new DefaultSession(delegate, properties) {
            @Override
            public boolean beforeWrite(@NonNull Object message) {
                /**
                 * 可以直接进返回 GameEnterResponse 这种已经 build 过了的对象，类型为 Message
                 */
                // MessageLite
                // MessageLite.Builder
                // MessageLiteOrBuilder
                // ProtoPacket -> ProtoPacketOrBuilder -> Message, Message.Builder - MessageLite, MessageLite.Builder -> MessageLiteOrBuilder
                // 1. FooResponse
                // 2. FooResponse.Builder
                // 3. ProtoPacket
                // 4. ProtoPacket.Builder
                // 5. 比如直接将 Payload 返回，因为 Payload 是直接
                if (!(message instanceof ProtoPacket)) {
                    if (message instanceof ByteString) {
                        // 转成 Message Or Builder
                        // message = xxx
                    }
                    if (!(message instanceof MessageLiteOrBuilder)) {
                        log.warn("Unsupported message: {}", message);
                        return false;
                    }
                    message = ProtoUtils.wrap((MessageLiteOrBuilder) message);
                }
                ProtoPacket.Builder builder;
                if (message instanceof ProtoPacket) {
                    builder = ((ProtoPacket) message).toBuilder();
                } else {
                    builder = ((Builder) message);
                }
                int seq = store.incrSequenceNumberAndGet();
                long now = System.currentTimeMillis();
                builder.setSn(seq);
                builder.setTime(now);
                builder.setSync(((ProtoPacket) processingRequest).getSync());
                builder.setRtd(((ProtoPacket) processingRequest).getRtd());
                // TODO 将该消息压缩后缓存起来
                // store.getCachedResponses().write(new Object[]{seq, EncryptionUtils.zip(message)});
                return true;
            }
        };
    }

}
