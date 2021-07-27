/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 全局 ID 生成组件
 * <p></p>
 * Long (64 bits) = [timestampBits] + [workerBits] + [sequenceBits]
 *
 * @author https://github.com/gukt
 * @see <a href="https://github.com/twitter/snowflake">Snow flake</a>
 */
@Component
@Slf4j
public class IdGenerator {

    private static final long workerIdBits = 14L;
    private static final long sequenceBits = 10L;
    private static final long workerIdShift = sequenceBits;
    private static final long timestampShift = sequenceBits + workerIdBits;
    private static final long sequenceMask = ~(-1L << sequenceBits);
    private static final long epoch = 1625068800000L; // 2021-07-01 00:00:00
    private static long sequence = 0L;
    private static long lastTimestamp = -1L;
    /** Worker id, also called server id. */
    private long workerId = 1;

    private IdGenerator() {
        long timeToLive = this.testUntil();
        log.info("ID Generator configuration: timestampShift={}, workerIdBits={}, sequenceBits={}, workerId={}, deadline={}",
                timestampShift, workerIdBits, sequenceBits, workerId,
                new SimpleDateFormat("yyyy-MM-dd").format(timeToLive));
    }

    public static long nextId() {
        return IdGeneratorHolder.INSTANCE.nextId0();
    }

    public static void main(String[] args) {
        long timestamp = IdGeneratorHolder.INSTANCE.testUntil();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Id will run out util about: " + sdf.format(new Date(timestamp)));
    }

    @Value("${app.server.id:${server.id:1}}")
    private void setWorkerId(long workerId) {
        long maxWorkerId = ~(-1L << workerIdBits);
        if (workerId > maxWorkerId) {
            log.error("The workerId exceed: {} (expected: <={})", workerId, maxWorkerId);
            System.exit(1);
        }
        this.workerId = workerId;
    }

    private synchronized long nextId0() {
        long now = now();
        if (now < lastTimestamp) {
            log.error("Clock is moving backwards. Rejecting requests until {}.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                    lastTimestamp - now));
        }
        if (lastTimestamp == now) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                now = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = now() % 2;
        }
        lastTimestamp = now;
        return ((now - epoch) << timestampShift) | (workerId << workerIdShift) | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = now();
        while (timestamp <= lastTimestamp) {
            timestamp = now();
        }
        return timestamp;
    }

    private long now() {
        return System.currentTimeMillis();
    }

    private long testUntil() {
        for (long i = 0; true; i = i + 60_000) {
            long id = (i << timestampShift) | (workerId << workerIdShift) | sequence;
            if (id < 0) {
                return epoch + i;
            }
        }
    }

    /**
     * 使用 Demand Holder 模式实现线程安全的单例。
     * 这比 "double-checked locking" 模式更高效且更容易理解。
     * 这种单例实现模式是有由"内部类的静态成员直到第一次使用时才初始化"的特性保证的。
     * @see <a href="https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom">Initialization-on-demand holder idiom</a>
     */
    private static class IdGeneratorHolder {

        private static final IdGenerator INSTANCE = new IdGenerator();
    }
}