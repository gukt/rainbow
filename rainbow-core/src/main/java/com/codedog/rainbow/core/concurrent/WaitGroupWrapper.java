/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Phaser;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author https://github.com/gukt
 */
public class WaitGroupWrapper {

    private static final Logger logger = LoggerFactory.getLogger(WaitGroupWrapper.class);

    private final Phaser phaser = new Phaser(0) {
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            logger.debug("phase: {}", phase);
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
