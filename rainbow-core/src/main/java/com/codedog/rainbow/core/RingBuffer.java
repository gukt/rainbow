/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core;

import com.codedog.rainbow.util.Assert;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * RingBuffer 是一个大小固定的环形缓冲队列
 *
 * @author https://github.com/gukt
 */
@SuppressWarnings("unused")
public class RingBuffer<T> {

    private static final int DEFAULT_CAPACITY = 16;
    private final int capacity;
    private final Object[] data;
    private int r = 0;
    private int w = 0;

    public RingBuffer() {
        this(DEFAULT_CAPACITY);
    }

    public RingBuffer(int initialCapacity) {
        this.capacity = initialCapacity;
        data = new Object[initialCapacity];
    }

    private boolean isEmpty() {
        return r == w;
    }

    private boolean isFull() {
        return (w + 1) % capacity == r;
    }

    /**
     * 清空 buffer，并重置读写指针。
     */
    public void purge() {
        Arrays.fill(data, null);
        r = 0;
        w = 0;
    }

    /**
     * 写入一个元素，成功返回 true; 反之 false。
     *
     * @param value 被写入的元素，不能为 null
     * @return 如果写入成功返回 true; 反之 false
     */
    public boolean write(T value) {
        Assert.notNull(value, "value");
        if (isFull()) {
            return false;
        }
        data[w] = value;
        w = (w + 1) % capacity;
        return true;
    }

    /**
     * 检查当前是否有可读的元素，如果有则返回，反之null
     * TODO Optional?
     */
    @Nullable
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T v = (T) data[r];
        return v;
    }

    public Iterator<Object> iterator() {
        // TODO implement me
        return null;
    }

    public Object[] toArray() {
        Object[] arr = new Object[size()];
        int index = 0;
        System.arraycopy(data, r, arr, 0, size());
        return arr;
    }

    public int remainingCapacity() {
        return capacity - size();
    }

    /**
     * 从Buffer中读取一个元素，如果buffer为空，返回null
     * TODO Optional
     */
    @Nullable
    public T read() {
        if (isEmpty()) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T v = (T) data[r];
        r = (r + 1) % capacity;
        return v;
    }

    public int size() {
        if (w >= r) {
            return w - r;
        } else {
            return w + capacity - r;
        }
    }
}
