package com.greateforest.concurrent;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.TestDeadLock")
public class TestDeadLock {
    public static void main(String[] args) {
        Object lockA = new Object();
        Object lockB = new Object();

        new Thread(() -> {
            synchronized (lockA) {
                log.debug("{},获取的锁：{}", Thread.currentThread().getName(), "lockA");
                synchronized (lockB) {
                    log.debug("{},获取的锁：{}", Thread.currentThread().getName(), "lockB");
                }
            }
        }, "Thread-A").start();

        new Thread(() -> {
            synchronized (lockB) {
                log.debug("{},获取的锁：{}", Thread.currentThread().getName(), "lockB");
                synchronized (lockA) {
                    log.debug("{},获取的锁：{}", Thread.currentThread().getName(), "lockA");
                }
            }
        }, "Thread-B").start();
    }
}
