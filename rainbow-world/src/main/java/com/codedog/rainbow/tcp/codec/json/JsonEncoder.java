/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.codec.json;

import com.codedog.rainbow.util.JsonUtils;
import com.codedog.rainbow.world.net.json.JsonPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * 协议编码器，用于将 {@link JsonPacket} 类型消息编码成 JSON 字符串。
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class JsonEncoder extends MessageToMessageEncoder<JsonPacket> {

    /**
     * 是否支持 WebSocket, 默认不支持
     */
    private final boolean websocketEnabled;

    public JsonEncoder() {
        this(false);
    }

    public JsonEncoder(boolean websocketEnabled) {
        this.websocketEnabled = websocketEnabled;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, JsonPacket msg, List<Object> out) {
        if (msg == null) {
            log.warn("TCP - You are trying to encode a null message.");
            return;
        }
        String json = JsonUtils.toJson(msg);
        if (json != null) {
            // TODO 这里不要使用 Unpooled HeapBuffer，而使用默认的方式
            // 参考 StringDecoder
            ByteBuf buffer = wrappedBuffer(json.getBytes());
            if (websocketEnabled) {
                // TODO 如果是 protobuf，要支持二进制格式的 frame
                out.add(new TextWebSocketFrame(buffer));
            } else {
                out.add(buffer);
            }
        }
    }
}
