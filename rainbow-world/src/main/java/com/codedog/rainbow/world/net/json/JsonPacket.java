/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.net.json;

import com.codedog.rainbow.util.MoreStrings;
import com.codedog.rainbow.world.net.ErrorCodeEnum;
import com.codedog.rainbow.world.net.Payload;
import com.codedog.rainbow.world.net.Session;
import lombok.*;

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
    private int seq;
    /**
     * 确认序号，表示确认端期望接收到的下一个序号，因此该字段值为成功接收到的消息序号+1
     */
    private int ack;
    /**
     * 同步标记，供客户端使用，当客户端需要同步等待服务器响应时，客户端界面阻塞，直到收到带有该标记位的响应
     * // TODO 换成int型
     */
    @Builder.Default
    private int sync = 0;
    /**
     * 消息类型
     */
    private String type;
    /**
     * 消息内容
     */
    private Object payload;
    /**
     * 透传信息，一般供客户端发送请求时使用，在请求响应消息模式下，客户端请求携带的透传信息会在响应消息中原封不动的返回
     */
    private String ext;
    /**
     * 发送消息的时间，毫秒
     */
    private long time;
    /**
     * 校验和，防篡改
     */
    private byte[] checksum;

    public static JsonPacket empty(String type) {
        return of(type, new Payload());
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
                .seq(sn)
                .payload(payload)
                .ext(ext)
                .build();
    }

    public static JsonPacket ofError(@NonNull ErrorCodeEnum errCode) {
        return ofError(errCode, null);
    }

    public static JsonPacket ofError(@NonNull ErrorCodeEnum errCode, String text) {
        return ofError(errCode.getValue(), MoreStrings.nullToDefault(text, errCode.getText()));
    }

    public static JsonPacket ofError(int code, String msg) {
        return JsonPacket.of(MsgTypeEnum.ERROR.getText(), Payload.empty()
                .put("code", code)
                .put("msg", msg)
        );
    }

    public JsonPacket withExt(String ext) {
        this.ext = ext;
        return this;
    }

    public CompletableFuture<Void> writeTo(@NonNull Session session) {
        return session.write(this);
    }

    public CompletableFuture<Void> writeTo(@NonNull Session session, boolean flush) {
        return session.write(this, flush);
    }

    @SuppressWarnings("unchecked")
    public <V> V getPayload() {
        return (V) payload;
    }

    public String toCompatString() {
        return String.format("JsonPacket#%d-%s-%s", seq, type, payload);
    }
}
