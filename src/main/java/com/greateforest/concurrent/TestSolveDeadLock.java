package com.greateforest.concurrent;

import lombok.extern.slf4j.Slf4j;

/**
 * 利用顺序加锁，解决死锁,
 * 容易造成线程饥饿的问题
 */
@Slf4j(topic = "c.TestDeadLock")
public class TestSolveDeadLock {
    public static void main(String[] args) {
        Object lockA = new Object();
        Object lockB = new Object();

        new Thread(() -> {
            while (true) {
                synchronized (lockA) {
                    log.debug("{},获取的锁：{}", Thread.currentThread().getName(), "lockA");
                    synchronized (lockB) {
                        log.debug("{},获取的锁：{}", Thread.currentThread().getName(), "lockB");
                    }
                }
            }

        }, "Thread-A").start();

        new Thread(() -> {
            while (true){
                synchronized (lockA) {
                    log.debug("{},获取的锁：{}", Thread.currentThread().getName(), "lockA");
                    synchronized (lockB) {
                        log.debug("{},获取的锁：{}", Thread.currentThread().getName(), "lockB");
                    }
                }
            }
        }, "Thread-B").start();
    }
}
