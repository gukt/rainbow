/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.session;

import com.codedog.rainbow.core.RingBuffer;
import com.codedog.rainbow.tcp.TcpProperties.SessionProperties;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author https://github.com/gukt
 */
public class SessionStore {

    /**
     * 待处理的请求
     */
    @Getter private final RingBuffer<Object> pendingRequests;
    // @Getter private final RingBuffer<Object[]> cachedResponses;
    /**
     * 用以缓存“响应消息”
     */
    private final SortedMap<Integer, Object> responseCache = new TreeMap<>();

    public void cacheResponse(Integer sn, Object message) {
        responseCache.put(sn, message);
    }

    public Collection<Object> getCachedResponsesFrom(Integer from) {
        SortedMap<Integer, Object> map = responseCache.subMap(from, responseCache.lastKey());
        return map.values();
    }

    private final SessionProperties properties;

    SessionStore(SessionProperties properties) {
        this.properties = properties;
        this.pendingRequests = new RingBuffer<>(properties.getMaxPendingRequestSize());
    }

    /**
     * 用以生成确认序号（Acknowledgement Number）。
     * <p>比如服务器接受了 SN=100 的消息，则回发消息时 ACK 会设置为 101，表示期望接受的下一个包的序号。
     */
    @Getter private final AtomicInteger ackNumber = new AtomicInteger(1);
    /**
     * 用以生成下一条下发消息的序号（Sequence Number）。
     */
    private final AtomicInteger sequenceNumber = new AtomicInteger(0);

    int getAck() {
        return ackNumber.get();
    }

    public int incrSequenceNumberAndGet() {
        badCount.set(0);
        return sequenceNumber.incrementAndGet();
    }

    /**
     * 用以统计“无效消息”的计数器
     */
    @Getter private final AtomicInteger badCount = new AtomicInteger(0);

    // /**
    //  * 获取从某个一序号开始往后的所有已缓存的响应
    //  *
    //  * @param fromAck 确认序号
    //  * @return 返回自指定确认序号（包含）及以后的所有缓存的消息
    //  */
    // public List<Object> retrieveResponsesSince(int fromAck) {
    //     List<Object> retList = new ArrayList<>();
    //     Object[] elements = cachedResponses.toArray();
    //     for (Object element : elements) {
    //         Object[] pair = (Object[]) element;
    //         if ((int) pair[0] >= fromAck) {
    //             retList.add(pair[1]);
    //         }
    //     }
    //     return retList;
    // }
}
