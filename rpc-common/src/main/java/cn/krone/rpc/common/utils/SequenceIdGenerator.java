package cn.krone.rpc.common.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * from 黑马 netty
 * @author yihang
 * @create 2022-06-08-14:36
 */
public abstract class SequenceIdGenerator {
    private static final AtomicInteger id = new AtomicInteger();

    public static int nextId() {
        return id.incrementAndGet();
    }
}
