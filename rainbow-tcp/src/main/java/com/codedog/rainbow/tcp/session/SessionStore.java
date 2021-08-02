/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.session;

import com.codedog.rainbow.core.RingBuffer;
import com.codedog.rainbow.tcp.TcpProperties.SessionProperties;
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
    @Getter private final RingBuffer<Object> pendingRequests;
    /**
     * 缓存的响应，存入该buffer的内容为[seq, response],
     * response应该经过压缩以节约内存空间，由于response需要压缩，因此当需要按序号删除buffer中的节点元素时没法取得序号
     * 因此buffer中的元素保存为[包序, 压缩过的下发消息
     */
    @Getter private final RingBuffer<Object[]> cachedResponses;

    private final SessionProperties properties;

    SessionStore(SessionProperties properties) {
        this.properties = properties;
        this.pendingRequests = new RingBuffer<>(properties.getMaxPendingRequestSize());
        this.cachedResponses = new RingBuffer<>(properties.getMaxCacheResponseSize());
    }

    /**
     * 用以生成确认序号（Acknowledgement Number）。比如服务器接受了 SN=100 的消息，则回发消息时 ACK 会设置为 101，表示期望接受的下一个包的序号。
     */
    @Getter private final AtomicInteger ackNumber = new AtomicInteger(1);
    /**
     * 用以生成下一条下发消息的序号（Sequence Number）。
     */
    private final AtomicInteger sequenceNumber = new AtomicInteger(0);

    int getAck() {
        return ackNumber.get();
    }

    // /**
    //  * "连续接收到的无效包个数"计数器，一旦消息被接受该字段会被清零
    //  */
    // @Getter private int badPacketCount = 0;
    // /**
    //  * 总计"连续接收到的无效包个数"
    //  */
    // @Getter private int badPacketAmount = 0;

    public int incrSequenceNumberAndGet() {
        badCount.set(0);
        return sequenceNumber.incrementAndGet();
    }

    /**
     * 用以统计“无效消息”的计数器
     */
    @Getter private final AtomicInteger badCount = new AtomicInteger(0);

    // /**
    //  * 一般在单线程中调用，因此不用同步。
    //  */
    // public static class Counter {
    //
    //     int count = 0;
    //
    //     /**
    //      * 重置计数器
    //      */
    //     void reset() {
    //         this.count = 0;
    //     }
    //
    //     /**
    //      * 累加计数
    //      */
    //     public void incr() {
    //         this.count++;
    //     }
    //
    //     /**
    //      * 获得计数器当前值
    //      */
    //     int get() {
    //         return count;
    //     }
    // }

    // /**
    //  * 递增"持续非法请求数"，该方法一般在单线程中调用，因此不用同步
    //  */
    // public void incrBadPacketCount() {
    //     // 递增持续非法请求
    //     badPacketCount++;
    //     // 递增累计非法请求数
    //     badPacketAmount++;
    // }

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
