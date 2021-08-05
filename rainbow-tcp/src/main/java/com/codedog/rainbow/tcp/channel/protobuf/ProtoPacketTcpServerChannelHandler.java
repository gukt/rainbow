/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.channel.protobuf;

import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.channel.TcpServerChannelHandler;
import com.codedog.rainbow.tcp.message.MessageHandler;
import com.codedog.rainbow.tcp.message.MessageHandler.Error;
import com.codedog.rainbow.tcp.session.DefaultSession;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.util.ProtoUtils;
import com.codedog.rainbow.util.Assert;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacketOrBuilder;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.channel.ChannelHandlerContext;
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
            public Object beforeWrite(Object message) {
                message = super.beforeWrite(message);
                // 只能发送 Error 或 MessageLiteOrBuilder 类型
                Assert.isAssignableFromAny(message.getClass(), MessageHandler.Error.class, MessageLiteOrBuilder.class);
                // 处理 Error
                if (message instanceof MessageHandler.Error) {
                    message = ProtoUtils.errorOf((Error) message);
                }
                // 处理 MessageLiteOrBuilder
                MessageLiteOrBuilder msg = (MessageLiteOrBuilder) message;
                // 如果返回的只是 Payload 数据（不是 ProtoPacketOrBuilder）则需要 wrap 一下
                if (!(msg instanceof ProtoPacketOrBuilder)) {
                    msg = ProtoUtils.wrapPacket(msg);
                }
                // 至此，msg 一定是 ProtoPacketOrBuilder 类型了。但此时仍不能确定它是不是 Builder 对象。
                // 因为后面需要 Builder 对象进行赋值，所以需要先获得 Builder 对象。
                ProtoPacket.Builder builder = ProtoUtils.safeGetBuilder(msg);
                int sn = store.incrSequenceNumberAndGet();
                builder.setSn(sn);
                builder.setTime(System.currentTimeMillis());
                if (processingRequest != null) {
                    builder.setSync(((ProtoPacket) processingRequest).getSync());
                    builder.setRtd(((ProtoPacket) processingRequest).getRtd());
                }
                // 缓存起来
                store.cacheResponse(sn, message);
                return msg;
            }
        };
    }

    private boolean isTypeSupported(Object msg) {
        return msg != null && (msg instanceof MessageHandler.Error || msg instanceof MessageLiteOrBuilder);
    }
}
