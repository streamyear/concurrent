package com.greateforest.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自定义简单的线程池
 */
@Slf4j(topic = "c.PoolTest")
public class PoolTest {
    public static void main(String[] args) {
        Pool pool = new Pool(3);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                MockConnection take = pool.take();
                log.debug("已经借到数据连接..." + take);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pool.free(take);
            }, "t-" + (i+1)).start();
        }
    }
}

@Slf4j(topic = "c.Pool")
class Pool {
    private MockConnection[] connections;

    private int capacity;

    private AtomicIntegerArray connectionStates;

    private ReentrantLock lock = new ReentrantLock();

    private Condition waitSet = lock.newCondition();

    public Pool(int capacity) {
        this.capacity = capacity;
        connectionStates = new AtomicIntegerArray(capacity);
        connections = new MockConnection[capacity];
        for (int i = 0; i < capacity; i++) {
            connections[i] = new MockConnection("数据连接..." + i);
        }
    }

    // 获取数据连接
    public MockConnection take() {
        while (true) {
            for (int i = 0; i < connectionStates.length(); i++) {
                if (connectionStates.get(i) == 0) {
                    // 找到空闲的连接
                    if (connectionStates.compareAndSet(i, 0, 1)) {
                        log.debug("获取到数据库连接..." + connections[i]);
                        return connections[i];
                    }
                }
            }
            // 一轮没有找到空闲的，就waite
            lock.lock();
            try {
                try {
                    log.debug("没有获取到数据库连接，await...");
                    waitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                lock.unlock();
            }
        }

    }

    // 归还数据库连接
    public void free(MockConnection connection) {
        for (int i = 0; i < connections.length; i++) {
            if (connections[i] == connection) {
                // 修改状态
                connectionStates.set(i, 0);
                log.debug("已经归还数据库连接...");
                lock.lock();
                try {
                    waitSet.signal();
                } finally {
                    lock.unlock();
                }
                break;

            }
        }
    }
}

class MockConnection {
    private String name;

    public MockConnection(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MockConnection{" +
                "name='" + name + '\'' +
                '}';
    }
}
