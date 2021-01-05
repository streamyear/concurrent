package com.greateforest.concurrent.thread.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 阻塞队列
 * @param <T>
 */
@Slf4j(topic = "a.BlockingQueue")
public class BlockingQueue<T> {
    // 双向操作队列
    private Deque<T> queue = new ArrayDeque<>();

    // 锁
    private ReentrantLock lock = new ReentrantLock();

    // 获取时，阻塞的waitSet
    private Condition takeWaitSet = lock.newCondition();

    // 放入队列时，阻塞的waitSet
    private Condition putWaitSet = lock.newCondition();

    // 队列的容量
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    // 带超时的获取poll
    public T poll(long timeout, TimeUnit unit) {
        // 转化成纳秒
        long nanos = unit.toNanos(timeout);
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    if (nanos <= 0) {
                        return null;
                    }
                    // awaitNanos方法返回的是应该剩余等待的时间
                    nanos = takeWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T element = queue.removeFirst();
            putWaitSet.signalAll();
            return element;
        } finally {
            lock.unlock();
        }
    }

    // 从队列中获取
    public T take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    log.debug("queue为空，await...");
                    takeWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T element = queue.removeFirst();
            putWaitSet.signalAll();
            return element;
        } finally {
            lock.unlock();
        }
    }

    // 向队列中put
    public void put(T t) {
        lock.lock();
        try {
            while (capacity <= queue.size()) {
                try {
                    log.debug("queue已满，await...");
                    putWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(t);
            takeWaitSet.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // 获取队列的大小
    public int size() {
        lock.lock();
        try {
            return queue.size();
        }finally {
            lock.unlock();
        }
    }

    /**
     * 带超时的提交任务，提交成功返回true;否则返回false
     * @param task
     * @param timeout
     * @param unit
     * @return
     */
    public boolean offer(T task, long timeout, TimeUnit unit) {
        long nanos = unit.toNanos(timeout);
        lock.lock();
        try {
            while (capacity <= queue.size()) {
                try {
                    if (nanos <= 0) {
                        return false;
                    }
                    nanos = putWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(task);
            takeWaitSet.signalAll();
            return true;
        } finally {
            lock.unlock();
        }
    }

    // 根据执行策略来处理任务
    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            if (queue.size() >= capacity) {
                rejectPolicy.apply(this, task);
            } else {
                // 没有满
                queue.addLast(task);
                takeWaitSet.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}
