/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import com.codedog.rainbow.support.RingBuffer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author https://github.com/gukt
 */
public class SessionStore {

    /**
     * 待处理的请求
     */
    @Getter
    private final RingBuffer<Object> pendingRequests;
    /**
     * 缓存的响应，存入该buffer的内容为[seq, response],
     * response应该经过压缩以节约内存空间，由于response需要压缩，因此当需要按序号删除buffer中的节点元素时没法取得序号
     * 因此buffer中的元素保存为[包序, 压缩过的下发消息
     */
    @Getter
    private final RingBuffer<Object[]> cachedResponses;
    /**
     * 用以生成确认序号，注意：确认序号表示下一条可接收的包序(已接受包序+1）
     */
    @Getter
    private final AtomicInteger nextAck = new AtomicInteger(1);
    /**
     * 下一条消息序号
     */
    private final AtomicInteger nextSeq = new AtomicInteger(0);
    /**
     * "连续接收到的无效包个数"计数器，一旦消息被接受该字段会被清零
     */
    @Getter
    private int continuousBadRequestCount = 0;
    /**
     * 总计"连续接收到的无效包个数"
     */
    @Getter
    private int totalBadRequestCount = 0;

    SessionStore(int maxPendingRequestSize, int maxCacheResponseSize) {
        this.pendingRequests = new RingBuffer<>(maxPendingRequestSize);
        this.cachedResponses = new RingBuffer<>(maxCacheResponseSize);
    }

    public int incrementSeq() {
        // 一旦某个包被接受了，就重置"连续接收无效包计数器"为0
        continuousBadRequestCount = 0;
        return nextSeq.incrementAndGet();
    }

    /**
     * 递增"持续非法请求数"，该方法一般在单线程中调用，因此不用同步
     */
    public void incrementContinuousBadRequestCount() {
        // 递增持续非法请求
        continuousBadRequestCount++;
        // 递增累计非法请求数
        totalBadRequestCount++;
    }

    /**
     * 获取从某个一序号开始往后的所有已缓存的响应
     *
     * @param fromAck 确认序号
     * @return 返回自指定确认序号（包含）及以后的所有缓存的消息
     */
    public List<Object> retrieveResponsesSince(int fromAck) {
        List<Object> retList = new ArrayList<>();
        Object[] elements = cachedResponses.toArray();
        for (Object element : elements) {
            Object[] pair = (Object[]) element;
            if ((int) pair[0] >= fromAck) {
                retList.add(pair[1]);
            }
        }
        return retList;
    }
}
