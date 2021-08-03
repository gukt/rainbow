/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.util.BaseError;
import com.codedog.rainbow.util.Assert;
import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Json类型的数据交互格式
 * TODO sn不能设置为负数（使用lombok）
 *
 * @author https://github.com/gukt
 */
@Builder
@Data
@NoArgsConstructor
@ToString(exclude = {"checksum"})
@AllArgsConstructor
public class JsonPacket {

    /**
     * 包序号，递增，默认从0开始，客户端和服务器各自维护
     */
    private int sn;
    /**
     * 确认序号，表示确认端期望接收到的下一个序号，该字段值为成功接收到的消息序号 + 1
     */
    private int ack;
    /**
     * 同步标记，供客户端使用，当客户端需要同步等待服务器响应时，客户端界面阻塞，直到收到带有该标记位的响应
     */
    private Integer sync;
    /**
     * 消息类型
     */
    private String type;
    /**
     * 消息内容
     */
    private Object payload;
    /**
     * 透传信息（Round Trip Data），一般供客户端发送请求时使用，在请求响应消息模式下，客户端请求携带的透传信息会在响应消息中原封不动的返回。
     */
    private Object rtd;
    /**
     * 发送消息的时间，毫秒
     */
    private long time;
    /**
     * 校验和，防篡改
     */
    private byte[] checksum;

    public JsonPacket withRtd(Object ext) {
        this.rtd = ext;
        return this;
    }

    public static JsonPacket of(String type) {
        return of(type, new HashMap<>());
    }

    public static JsonPacket of(String type, Object payload) {
        return of(type, payload, 0, null);
    }

    public static JsonPacket of(String type, Object payload, int sn) {
        return of(type, payload, sn, null);
    }

    // TODO 有没有必要新建JsonPacket时指定ext，如果不需要，考虑发送返回包时将透传信息带上
    public static JsonPacket of(String type, Object payload, int sn, String ext) {
        return JsonPacket.builder()
                .type(type)
                .sn(sn)
                .payload(payload)
                .rtd(ext)
                .build();
    }

    public static JsonPacket errorOf(Serializable code, Object msg) {
        Assert.notNull(code, "code");
        Map<String, Object> payload = new HashMap<>();
        payload.put("code", code);
        payload.put("msg", msg);
        return JsonPacket.of("Error", payload);
    }

    public static JsonPacket errorOf(BaseError error) {
        return errorOf(error.getCode(), error.getMsg());
    }
    public static JsonPacket errorOf(MessageHandlerException ex) {
        return errorOf(ex.getErrorCode(), ex.getErrorMessage());
    }

    /**
     * 将消息写到指定的 {@link Session}。它是 {@link #writeTo(Session, boolean) writeTo(session, true)} 的便利方法。
     *
     * @param session 表示消息被发送到的目标连接，不能为 null
     * @return {@link CompletableFuture}，表示异步完成的结果
     * @see #writeTo(Session, boolean)
     * @see Session#write(Object)
     * @see Session#write(Object, boolean)
     */
    public CompletableFuture<Session> writeTo(Session session) {
        return this.writeTo(session, true);
    }

    /**
     * 将消息写到指定的 {@link Session}。可以通过参数 <code>flush</code> 指定是否需要立即发送消息。
     *
     * @param session 表示消息被发送到的目标连接，不能为 null
     * @param flush   是否需要立即发送
     * @return {@link CompletableFuture}，表示异步完成的结果
     * @see #writeTo(Session)
     * @see Session#write(Object)
     * @see Session#write(Object, boolean)
     */
    public CompletableFuture<Session> writeTo(Session session, boolean flush) {
        Assert.notNull(session, "session");
        return session.write(this, flush);
    }

    @Deprecated
    public String toCompactString() {
        return String.format("JsonPacket#%d-%s-%s", sn, type, payload);
    }
}
