package com.acgist.snail.net.torrent.utp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.acgist.snail.context.SystemThreadContext;
import com.acgist.snail.logger.Logger;
import com.acgist.snail.logger.LoggerFactory;

/**
 * UTP请求队列
 * 请求队列用来异步处理UTP请求，每个接收窗口对应一个请求队列，每个请求队列可以处理多个接收窗口。
 * 
 * @author acgist
 */
public final class UtpRequestQueue {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UtpRequestQueue.class);

    private static final UtpRequestQueue INSTANCE = new UtpRequestQueue();
    
    public static final UtpRequestQueue getInstance() {
        return INSTANCE;
    }
    
    /**
     * 是否可用
     */
    private boolean available;
    /**
     * 队列长度
     * 注意：如果使用静态常量引用外部常量容易出现异常
     */
    private final int queueSize;
    /**
     * 请求队列索引
     */
    private final AtomicInteger queueIndex;
    /**
     * 请求队列处理线程池
     * 
     * @see #QUEUE_SIZE
     */
    private final ExecutorService executor;
    /**
     * 请求队列集合
     * 
     * @see #QUEUE_SIZE
     */
    private final List<BlockingQueue<UtpRequest>> queues;
    
    private UtpRequestQueue() {
        this.queueSize = SystemThreadContext.DEFAULT_THREAD_SIZE;
        this.available = true;
        this.queueIndex = new AtomicInteger(0);
        this.queues = new ArrayList<>(this.queueSize);
        int[] executor = {this.queueSize, this.queueSize, 10000};
        this.executor = SystemThreadContext.newExecutor(executor, 60L, SystemThreadContext.SNAIL_THREAD_UTP_QUEUE);
        this.buildQueues();
        LOGGER.debug("启动UTP请求队列：{}", this.queueSize);
    }
    
    /**
     * @return 请求队列
     */
    public BlockingQueue<UtpRequest> queue() {
        final int index = this.queueIndex.getAndIncrement() % this.queueSize;
        return this.queues.get(Math.abs(index));
    }
    
    /**
     * 新建请求队列
     */
    private void buildQueues() {
        for (int index = 0; index < this.queueSize; index++) {
            final var queue = new LinkedBlockingQueue<UtpRequest>();
            this.executor.submit(() -> this.queueExecute(queue));
            this.queues.add(queue);
        }
    }

    /**
     * 处理请求队列
     * 
     * @param queue 请求队列
     */
    private void queueExecute(BlockingQueue<UtpRequest> queue) {
        while(this.available) {
            try {
                queue.take().execute();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.debug("UTP处理请求异常", e);
            } catch (Exception e) {
                LOGGER.error("UTP处理请求异常", e);
            }
        }
    }
    
    /**
     * 关闭UTP请求队列处理线程池
     */
    public void shutdown() {
        LOGGER.debug("关闭UTP请求队列处理线程池");
        this.available = false;
        SystemThreadContext.shutdown(this.executor);
    }
    
}
