package com.greateforest.concurrent.thread.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 自定义的线程池
 */
@Slf4j(topic = "c.TestThreadPool")
public class TestThreadPool {
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(5, 10, 1, TimeUnit.SECONDS
                , (queue, task) -> {
//            log.debug("队列已经满了...");
            // 死等
             queue.put(task);
            // 超时提交
//            boolean offerRes = queue.offer(task, 50, TimeUnit.NANOSECONDS);
//            log.debug("offer result: {}", offerRes);
        });
        for (int i = 0; i < 20; i++) {
            int j = i;
            threadPool.execute(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("~~~~~~{}", j);
            });
        }
    }

    private static void testBlockQueue() {
        BlockingQueue<String> queue = new BlockingQueue<String>(5);
        for (int i = 0; i < 8; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String take = queue.take();
                log.debug("take...{}", take);
            }, "consumer-" + i).start();
        }

        for (int i = 0; i < 8; i++) {
            int t = i;
            new Thread(() -> {
                queue.put("content-" + t);
            }, "producer-" + i).start();
        }
    }
}

