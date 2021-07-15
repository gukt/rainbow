/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.core.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Phaser;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author https://github.com/gukt
 */
@Slf4j
public class WaitGroupWrapper {

    private final Phaser phaser = new Phaser(0) {
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            log.debug("phase: {}", phase);
            return phase == 1 || registeredParties == 0;
        }
    };

    public void run(Runnable task, String threadName) {
        phaser.register();

        // create a single thread to run specified task
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
                0, TimeUnit.SECONDS, new SynchronousQueue<>(),
                r -> new Thread(r, threadName));

        // execute task
        executor.execute(() -> {
            task.run();
            phaser.arriveAndDeregister();
        });
    }

    public void terminate() {
        phaser.forceTermination();
    }

    public void await() {
        phaser.arriveAndAwaitAdvance();
    }
}
