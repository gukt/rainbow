/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world;

import lombok.Getter;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author https://github.com/gukt
 */
public class GameAppStats {

    @Getter
    public final LongAdder messageRead = new LongAdder();
    @Getter
    public final LongAdder messageWritten = new LongAdder();
    @Getter
    public final LongAdder bytesRead = new LongAdder();
    @Getter
    public final LongAdder bytesWritten = new LongAdder();
    public int connectionCount;
    public int onlineRoleCount;
    public int offlineRoleCount;
    public int reconnectCount;
    public int reconnectSuccessCount;
    public int maxBytesRead;
    public int maxBytesWritten;
    public int bizExecPoolSize;
    public int bizExecLagestPoolSize;
    public int bizExecQueued;
    public int bizExecActive;
    public int bizExecCompletedTaskCount;
    public int bizExecRejectedCount;
    public int bizExecRejectedRatio;
    public int cpu;
    public int memory;

    /**
     * 返回AppStats单例
     *
     * @return return AppStats instance
     */
    public static GameAppStats getInstance() {
        return Singleton.INSTANCE.getGameAppStats();
    }

    /**
     * 利用枚举懒加载模式实现单例
     */
    private enum Singleton {
        /**
         * Singleton.INSTANCE
         */
        INSTANCE;

        @Getter
        private final GameAppStats gameAppStats;

        Singleton() {
            this.gameAppStats = new GameAppStats();
        }
    }
}
