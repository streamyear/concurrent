package com.greateforest.concurrent;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.TestCreateThread")
public class TestCreateThread {
    public static void main(String[] args) {
        new Thread(){
            @Override
            public void run() {
                log.debug("subRunning");
            }
        }.start();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.debug("subRunning");
            }
        };
        new Thread(runnable).start();

        Runnable subRunnable = () -> log.debug("subRunning");
        new Thread(subRunnable).start();

        new Thread(() -> log.debug("test running"), "test").start();

        log.debug("mainThread running");
    }
}
