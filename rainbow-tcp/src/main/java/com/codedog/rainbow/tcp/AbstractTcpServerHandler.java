/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.tcp.interceptor.MessageInterceptor;
import com.codedog.rainbow.tcp.session.DefaultSession;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.util.Assert;
import com.codedog.rainbow.util.EncryptionUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AbstractTcpServerHandler
 * 创建TcpServerHandler对象能不依赖具体类型吗
 *
 * @author https://github.com/gukt
 */
@Slf4j
public abstract class AbstractTcpServerHandler<T> extends SimpleChannelInboundHandler<T> {

    protected final TcpProperties properties;
    protected final MessageResolver<T> resolver;

    /**
     * 消息拦截器列表,供外部调用，动态添加interceptors用的
     * TODO 初始化完成后，需要是不可变的？？？ 可以动态添加是不是更好？考虑一下
     */
    @Getter private final List<MessageInterceptor<?>> interceptorList = new ArrayList<>();
    /**
     * 活跃状态，一旦设置为 true，就表示可以接受请求了。
     */
    @Setter protected volatile boolean active;
    /**
     * 消息拦截器列表，内部循环使用的
     */
    private MessageInterceptor<T>[] interceptors;

    AbstractTcpServerHandler(TcpProperties properties) {
        Assert.notNull(properties, "properties");
        Assert.notNull(properties.getMessageResolver(), "properties.getMessageResolver()");
        this.properties = properties;
        this.resolver = properties.getMessageResolver();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.debug("TCP - New client connected: {}", channel);
        // 实例化Session放到ctx的自定义属性中
        channel.attr(Session.KEY).setIfAbsent(new DefaultSession(ctx, properties.getSession()) {
            @Override
            public boolean beforeWrite(@NonNull Object message) {
                JsonPacket msg = (JsonPacket) message;
                int seq = store.incrSequenceNumberAndGet();
                long now = System.currentTimeMillis();
                msg.setSn(seq);
                msg.setTime(now);
                msg.setSync(((JsonPacket) processingRequest).getSync());
                // 将该消息压缩后缓存起来
                store.getCachedResponses().write(new Object[]{seq, EncryptionUtils.encrypt(message)});
                return true;
            }
        });
        super.channelActive(ctx);
        TcpServer.addSession(ctx.channel().attr(Session.KEY).get());
    }

    boolean isOverload() {
        // TODO 根据线程池或其他资源监测机制，判断当前服务器是否过载
        return false;
    }

    boolean isConnectionsExceeded() {
        return TcpServer.getSessionCount() >= properties.getMaxConnections();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("TCP - exceptionCaught: {}, {}", ctx.channel(), cause.getMessage());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("TCP - Client disconnected：{}", ctx.channel());
        Session session = ctx.channel().attr(Session.KEY).get();
        // TODO Fix it ASAP
        // fire event
//        eventPublisher.fireAsync(new SessionClosingEvent(session), true);
        TcpServer.removeSession(session);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE || e.state() == IdleState.ALL_IDLE) {
                log.info("TCP - Connection timeout: {}", ctx.channel());
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 获得拦截器数组
     */
    @SuppressWarnings("unchecked")
    private MessageInterceptor<T>[] getInterceptors() {
        if (this.interceptors == null) {
            this.interceptors = (MessageInterceptor<T>[]) interceptorList.toArray();
        }
        return this.interceptors;
    }

    /**
     * 应用拦截器链中所有拦截器的 {@link MessageInterceptor#preHandle(Session, Object) preHandle} 方法。
     *
     * <p>注意：和 {@link #applyPostHandle(Session, Object) applyPostHandle} 方法中对拦截器链的调用顺序正好相反。
     *
     * @param session Session 对象，不能为 null
     * @param msg     消息，不能为 null
     * @see #applyPostHandle(Session, Object)
     */
    boolean applyPreHandle(Session session, T msg) {
        MessageInterceptor<T>[] interceptors = getInterceptors();
        if (interceptors == null || interceptors.length == 0) {
            return true;
        }
        return Arrays.stream(interceptors).allMatch(i -> i.preHandle(session, msg));
    }

    /**
     * 应用拦截器链中所有拦截器的 {@link MessageInterceptor#postHandle(Session, Object) postHandle} 方法。
     *
     * <p>注意：和 {@link #applyPreHandle(Session, Object) applyPreHandle} 方法中对拦截器链的调用顺序正好相反。
     *
     * @param session Session 对象，不能为 null
     * @param msg     消息，不能为 null
     * @see #applyPreHandle(Session, Object)
     */
    void applyPostHandle(Session session, T msg) {
        MessageInterceptor<T>[] interceptors = getInterceptors();
        if (interceptors != null && interceptors.length > 0) {
            // 这里是反向调用
            for (int i = interceptors.length - 1; i >= 0; i--) {
                MessageInterceptor<T> interceptor = interceptors[i];
                interceptor.postHandle(session, msg);
            }
        }
    }

    /**
     * 发送消息后中断连接。
     *
     * @param ctx 表示一条 {@link ChannelHandlerContext 客户端连接}
     * @param msg 待发送的消息
     */
    void rejectConnection(ChannelHandlerContext ctx, Object msg) {
        log.debug("TCP - Client connection was rejected: msg={}, remoteAddress={}", msg, ctx.channel().remoteAddress());
        ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
    }
}
