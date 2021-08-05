/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.channel;

import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.TcpServer;
import com.codedog.rainbow.tcp.interceptor.MessageInterceptor;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.util.BaseError;
import com.codedog.rainbow.tcp.util.MessageUtils;
import com.codedog.rainbow.util.Assert;
import com.codedog.rainbow.util.ObjectUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codedog.rainbow.tcp.util.BaseError.SERVER_BUSYING;
import static com.codedog.rainbow.tcp.util.BaseError.SERVER_UNAVAILABLE;

/**
 * TcpServerChannelHandler
 *
 * @author https://github.com/gukt
 */
@Slf4j
@Sharable
public abstract class TcpServerChannelHandler<T> extends SimpleChannelInboundHandler<T> {

    protected final TcpProperties properties;

    /**
     * 拦截器链,供外部初始化时使用。不用担心拦截器链在运行时会被动态更改。
     * <p>它们只会在 {@link #getInterceptors()} 中初始化一次，并被保存到 {@link #interceptors} 变量。
     */
    @Getter private final List<MessageInterceptor<?>> interceptorList = new ArrayList<>();
    /**
     * 活跃状态，一旦设置为 <code>true</code>，就表示可以接受请求了。
     */
    @Setter protected volatile boolean active;
    /**
     * 拦截器链，{@link #getInterceptors() 第一次初始化} 之后就不会再改变了。
     */
    private MessageInterceptor<T>[] interceptors;

    protected TcpServerChannelHandler(TcpProperties properties) {
        Assert.notNull(properties, "properties");
        this.properties = properties;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("TCP - New client registering: {}", ctx.channel());
        if (!active) {
            rejectConnection(ctx, MessageUtils.errorOf(SERVER_UNAVAILABLE));
            return;
        }
        if (isOverload()) {
            rejectConnection(ctx, MessageUtils.errorOf(SERVER_BUSYING));
            return;
        }
        if (isConnectionsExceeded()) {
            rejectConnection(ctx, MessageUtils.errorOf(BaseError.EXCEED_CONNECTIONS));
            return;
        }
        super.channelRegistered(ctx);
        log.debug("TCP - New client registered: {}", ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, final T message) {
        final Session session = ctx.channel().attr(Session.KEY).get();
        log.debug("TCP - Receiving: {}, session={}, backlog={}", message, session, session.getStore().getPendingRequests().size());

        // 应用拦截器链的所有前置检查
        if (applyPreHandle(session, message)) {
            Object err = null;
            if (!active) { // non-active?
                err = MessageUtils.errorOf(BaseError.SERVER_UNAVAILABLE);
            } else if (isOverload()) { // Busying?
                err = MessageUtils.errorOf(BaseError.SERVER_BUSYING);
            }
            // 将接收到的请求消息入列到 buffer 中等待处理，
            // 如果入列失败，表示积压待处理的消息太多，立即返回错误码
            // TODO 这里的写法太长了，是不是得改写一下？
            else if (!session.getStore().getPendingRequests().write(message)) {
                err = MessageUtils.errorOf(BaseError.EXCEED_SESSION_BACKLOGS);
            }
            if (err != null) {
                // Object rtd = resolver.getRtd(message);
                // session.write(resolver.withRtd(err, rtd));
                session.write(err);
            }
        }
        // 应用拦截器链中的“后置处理”
        this.applyPostHandle(session, message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.debug("TCP - New client connected: {}", channel);
        Attribute<Session> sessionAttribute = channel.attr(Session.KEY);
        sessionAttribute.setIfAbsent(newSession(ctx));
        super.channelActive(ctx);
        TcpServer.addSession(sessionAttribute.get());
    }

    protected abstract Session newSession(ChannelHandlerContext delegate);

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
            // interceptors = interceptorList.toArray(this.interceptors);
            interceptors = interceptorList.toArray(new MessageInterceptor[0]);
            // interceptors = ArrayUtils.toArray(interceptorList, MessageInterceptor.class);
            // this.interceptors = ArrayUtils.toArray(interceptorList);
            // this.interceptors = interceptorList.toArray(new MessageInterceptor[0]);
            // IntStream.range(0, interceptorList.size()).forEach(i -> interceptors[i] = (MessageInterceptor<T>) interceptorList.get(0));
        }
        return interceptors;
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
        final MessageInterceptor<T>[] interceptors = getInterceptors();
        if (ObjectUtils.isEmpty(interceptors)) {
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
        // if (interceptors != null && interceptors.length > 0) {
        if (!ObjectUtils.isEmpty(interceptors)) {
            // 反向调用
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
