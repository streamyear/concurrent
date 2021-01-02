package com.greateforest.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "c.ParkTest")
public class ParkTest {
    public static void main(String[] args) throws InterruptedException {
        Thread test1 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LockSupport.park();
            log.debug("unpark1");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LockSupport.park();
            log.debug("unpark2");

        }, "Test1");
        test1.start();

//        Thread.sleep(1000);
        LockSupport.unpark(test1);
    }
}
