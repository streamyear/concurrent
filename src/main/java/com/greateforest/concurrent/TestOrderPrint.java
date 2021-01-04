package com.greateforest.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

/**
 * 先打印2，后打印1
 */
@Slf4j(topic = "c.TestOrderPrint")
public class TestOrderPrint {
    private static final Object lock = new Object();

    private static boolean t2Runned = false;

    public static void main(String[] args) {
//        waitAndNotifyVersion();
        parkAndUnparkVersion();
    }

    public static void parkAndUnparkVersion() {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            log.debug("Print: 1");
        }, "t1");
        t1.start();

        new Thread(() -> {
            log.debug("Print: 2");
            LockSupport.unpark(t1);
        }, "t2").start();
    }


    public static void waitAndNotifyVersion() {
        new Thread(() -> {
            synchronized (lock) {
                while (!t2Runned) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            log.debug("Print: 1");
        }, "t1").start();

        new Thread(() -> {
            synchronized (lock) {
                log.debug("Print: 2");
                t2Runned = true;
                lock.notify();
            }
        }, "t2").start();
    }
}
