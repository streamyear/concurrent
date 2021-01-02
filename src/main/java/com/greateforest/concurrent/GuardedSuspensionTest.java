package com.greateforest.concurrent;

import lombok.extern.slf4j.Slf4j;

/**
 * 同步模式--保护性暂停
 */
@Slf4j(topic = "c.GuardedSuspensionTest")
public class GuardedSuspensionTest {
    public static void main(String[] args) {
        GuardedSuspensionV2 guardedSuspension = new GuardedSuspensionV2();
        new Thread(() -> {
            log.debug("开始获取结果...");
            Object res = guardedSuspension.get(2000);
            log.debug("获取的结果为：{}", res);
        }, "getThread").start();

        new Thread(() -> {
            log.debug("开始设置结果...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            guardedSuspension.setResult("订单金额为：500.00");
            log.debug("设置结果结束...");
        }, "setThread").start();
    }
}

/**
 * 获取带超时的版本
 */
class GuardedSuspensionV2 {
    private Object response;

    private final Object lock = new Object();

    // 获取结果
    public Object get(long timeout) {
        synchronized (lock) {
            // 条件不满足时，等待, 注意需要用while, 防止虚假唤醒
            long start = System.currentTimeMillis();
            long passedTime = 0;
            while (response == null) {
                long waitTime = timeout - passedTime;
                if (waitTime <= 0) {
                    break;
                }
                try {
                    lock.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                passedTime = System.currentTimeMillis() - start;
            }
            return response;
        }
    }

    // 设置值
    public void setResult(Object response) {
        synchronized (lock) {
            // 条件满足，唤醒wait的线程
            this.response = response;
            lock.notifyAll();
        }
    }
}

class GuardedSuspension {
    private Object response;

    private final Object lock = new Object();

    // 获取结果
    public Object get() {
        synchronized (lock) {
            // 条件不满足时，等待, 注意需要用while, 防止虚假唤醒
            while (response == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }

    // 设置值
    public void setResult(Object response) {
        synchronized (lock) {
            // 条件满足，唤醒wait的线程
            this.response = response;
            lock.notifyAll();
        }
    }
}
