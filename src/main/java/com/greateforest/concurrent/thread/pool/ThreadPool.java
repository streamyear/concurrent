package com.greateforest.concurrent.thread.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 */
@Slf4j(topic = "c.ThreadPool")
public class ThreadPool {
    // 放等待执行的线程任务
    private BlockingQueue<Runnable> queue;

    // 存放工作线程的set
    private Set<Work> works = new HashSet<>();

    // 核心线程数
    private int coreSize;

    // 队列的最大储存数
    private int queueCapacity;

    // 超时时间
    private long timeout;

    private TimeUnit timeUnit;

    // 拒绝策略
    private RejectPolicy<Runnable> rejectPolicy;

    public ThreadPool(int coreSize, int queueCapacity, long timeout, TimeUnit timeUnit, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.queueCapacity = queueCapacity;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.queue = new BlockingQueue<>(queueCapacity);
        this.rejectPolicy = rejectPolicy;
    }

    /**
     * 提交任务
     * @param task
     */
    public void execute(Runnable task) {
        synchronized (works) {
            // 当works的大小没有超过coreSize时，创建新的线程执行
            if (works.size() < coreSize) {
                Work work = new Work(task, works, queue, timeout, timeUnit);
                work.start();
                works.add(work);
            } else {
                // 否则提交到队列中，等待执行
//                queue.put(task);
                // 队列满了之后，让用户选择具体的执行策略
                queue.tryPut(rejectPolicy, task);
            }
        }
    }
}

@FunctionalInterface
interface RejectPolicy<T> {
    void apply(BlockingQueue<T> queue, T task);
}

/**
 * 工作线程
 */
@Slf4j(topic = "c.Work")
class Work extends Thread{
    private Runnable task;

    private Set<Work> works;

    private BlockingQueue<Runnable> queue;

    private long timeout;

    private TimeUnit timeUnit;

    public Work(Runnable task, Set<Work> works, BlockingQueue<Runnable> queue, long timeout, TimeUnit timeUnit) {
        this.task = task;
        this.works = works;
        this.queue = queue;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public void run() {
        while (task != null || (task = queue.poll(timeout, timeUnit)) != null) {
            try {
                task.run();
            } catch (Exception e) {
              e.printStackTrace();
            } finally {
                task = null;
            }
        }
        // 线程执行结束，移除线程
        synchronized (works) {
            log.debug("移除线程: {}", this);
            works.remove(this);
        }
    }
}
