/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO 需要进一步深入一下Guava的EventBus机制的实现
 *
 * @author https://github.com/gukt
 */
@SuppressWarnings("UnstableApiUsage")
@Slf4j
public class EventPublisher {

    private static final SubscriberExceptionHandler exceptionHandler = new LoggingSubscriberExceptionHandler();
    private final EventBus eventBus;
    private final AsyncEventBus asyncEventBus;
    private final ThreadPoolExecutor eventExec;

    public EventPublisher() {
        // TODO 这里需要整理一下
        this.eventExec = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                r -> new Thread(r, "async-event-handler-%s"));

        this.eventBus = new EventBus(exceptionHandler);
        this.asyncEventBus = new AsyncEventBus(eventExec, exceptionHandler);
    }

    public EventPublisher(@NonNull Collection<Object> subscribers) {
        this();
        subscribers.forEach(this::registerSubscribe);
    }

    public void registerSubscribe(@NonNull Object subscriber) {
        eventBus.register(subscriber);
        asyncEventBus.register(subscriber);
    }

    /**
     * 触发事件，所有事件在执行fire方法所在线程中执行
     */
    public void fire(Object event) {
        log.debug("Event: Firing event: {}", event.getClass().getSimpleName());
        eventBus.post(event);
    }

    /**
     * 触发异步事件，所有处理程序在异步事件处理线程池中**并发**执行
     */
    public void fireAsync(Object event) {
        fireAsync(event, false);
    }

    /**
     * 触发异步事件，对事件的处理是在"异步事件线程池"中被执行的，
     * 如果指定了keepInSameThread为true则所有事件处理方法在同一个线程中串行执行，反之多个工作线程并发执行
     */
    public void fireAsync(final Object event, boolean keepInSameThread) {
        log.debug("Firing async event: {}, runsInSameThread: {}", event.getClass().getSimpleName(), keepInSameThread);

        if (keepInSameThread) {
            eventExec.execute(() -> eventBus.post(event));
        } else {
            asyncEventBus.post(event);
        }
    }

    public void stop() {
        // TODO 这里需要关闭线程池
    }

    private static final class LoggingSubscriberExceptionHandler implements
            SubscriberExceptionHandler {

        @Override
        public void handleException(Throwable exception, SubscriberExceptionContext context) {
            log.error("处理事件时发生了异常：event:{}: ", context.getEvent().getClass().getSimpleName(), exception);
        }
    }
}
