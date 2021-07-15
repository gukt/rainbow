/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.core.concurrent;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Once是一个方便的工具类，确保只运行任务，是对golang中的once的Java实现
 *
 * 使用方法如下：
 * <pre>
 * Once once = new Once();
 * for (int i = 0; i < 10; i++) {
 *  once.run(() -> System.out.println("hello"));
 * }
 * </pre>
 *
 * 还可以使用双重检查的方式实现：
 *
 * <pre>
 * private volatile boolean done;
 *
 * public void run(Runnable task) {
 *     if (!done) {
 *         synchronized (this) {
 *             if (!done) {
 *                 task.run();
 *                 done = true;
 *             }
 *         }
 *     }
 * }
 * </pre>
 *
 * @author https://github.com/gukt
 */
public class Once {

    private final AtomicBoolean done = new AtomicBoolean(false);

    public static void main(String[] args) {
        Once once = new Once();
        for (int i = 0; i < 10; i++) {
            once.run(() -> System.out.println("hello"));
        }
    }

    public void run(Runnable task) {
        if (done.get()) {
            return;
        }
        if (done.compareAndSet(false, true)) {
            try {
                task.run();
            } catch (Exception e) {
                done.set(false);
            }
        }
    }

    // private Semaphore semaphore = new Semaphore(1);
    //
    // public void run(Runnable task) {
    //     if (semaphore.tryAcquire()) {
    //         try {
    //             task.run();
    //         } catch (Exception e) {
    //             semaphore.release();
    //         }
    //     }
    // }
}
