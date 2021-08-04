/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.message;

import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.util.BaseError;
import com.codedog.rainbow.util.Assert;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

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
// @NoArgsConstructor // TODO remove?
@ToString
// @AllArgsConstructor // TODO remove?
public class JsonPacket {

    private final static String ERROR = "Error";

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
    @ToString.Exclude
    private byte[] checksum;

    public JsonPacket withRtd(Object ext) {
        this.rtd = ext;
        return this;
    }

    /**
     * 创建一个 {@link JsonPacket JsonPacket} 实例，该方法是 builder() 链式调用的便利方式。
     *
     * @param type 消息类型，不能为 null
     * @return JsonPacket 实例
     */
    public static JsonPacket of(String type) {
        Assert.notNull(type, "type");
        return JsonPacket.builder().type(type).build();
    }

    /**
     * 创建一个 {@link JsonPacket JsonPacket} 实例，该方法是 builder() 链式调用的便利方式。
     *
     * @param type    消息类型，不能为 null
     * @param payload 消息内容，可以为 null
     * @return JsonPacket 实例
     */
    public static JsonPacket of(String type, Object payload) {
        Assert.notNull(type, "type");
        return JsonPacket.builder().type(type).payload(payload).build();
    }

    /**
     * 创建一个 {@link JsonPacket JsonPacket} 实例，该方法是 builder() 链式调用的便利方式。
     *
     * @param type    消息类型，不能为 null
     * @param payload 消息内容，可以为 null
     * @param sn      消息序号，不能小于 0
     * @return JsonPacket 实例
     */
    public static JsonPacket of(String type, Object payload, int sn) {
        Assert.notNull(type, "type");
        Assert.isTrue(sn > 0, "sn > 0");
        return JsonPacket.builder().type(type).payload(payload).sn(sn).build();
    }

    /**
     * 创建一个“表示统一错误的” {@link JsonPacket JsonPacket 实例}，该实例的 type = Error（由 {@link #ERROR} 变量指定的）。
     *
     * @param code 错误代码
     * @param msg  错误描述
     * @return 表示统一错误的 JsonPacket 实例
     */
    public static JsonPacket errorOf(Serializable code, Object msg) {
        Assert.notNull(code, "code");
        Map<String, Object> payload = new HashMap<>();
        payload.put("code", code);
        payload.put("msg", msg);
        return JsonPacket.of(ERROR, payload);
    }

    /**
     * 创建一个“表示统一错误的” {@link JsonPacket JsonPacket 实例}，该实例的 type = Error（由 {@link #ERROR} 变量指定的）。
     *
     * @param error Error 对象， 不能为 null
     * @return 表示统一错误的 JsonPacket 实例
     */
    public static JsonPacket errorOf(BaseError error) {
        Assert.notNull(error, "error");
        return errorOf(error.getCode(), error.getMsg());
    }

    /**
     * 创建一个“表示统一错误的” {@link JsonPacket JsonPacket 实例}，该实例的 type = Error（由 {@link #ERROR} 变量指定的）。
     *
     * @param ex MessageHandlerException 对象，不能为 null
     * @return 表示统一错误的 JsonPacket 实例
     */
    public static JsonPacket errorOf(MessageHandlerException ex) {
        Assert.notNull(ex, "ex");
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
     * @throws IllegalArgumentException 如果 session 为 null
     * @see #writeTo(Session)
     * @see Session#write(Object)
     * @see Session#write(Object, boolean)
     */
    public CompletableFuture<Session> writeTo(Session session, boolean flush) {
        Assert.notNull(session, "session");
        return session.write(this, flush);
    }
}
