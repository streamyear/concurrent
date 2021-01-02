package com.greateforest.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 两阶段终止模式
 */
class MonitorMain {
    public static void main(String[] args) throws InterruptedException {
        TwoPhaseTermination twoPhaseTermination = new TwoPhaseTermination();
        twoPhaseTermination.start();

        TimeUnit.SECONDS.sleep(5);
        twoPhaseTermination.stop();
    }
}

/**
 * 监控程序
 */
@Slf4j(topic = "c.TwoPhaseTermination")
class TwoPhaseTermination {
    private Thread monitor;

    public void start() {
        monitor = new Thread(() -> {
            while (true) {
                Thread currentThread = Thread.currentThread();
                if (currentThread.isInterrupted()) {
                    // 被打断
                    log.info("停止监控程序的工作...");
                    break;
                }
                try {
                    // 睡眠中打断线程，会走异常
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // 会清楚打断标记，所以需要重置打断标记
                    currentThread.interrupt();
                }
                // 正常程序中被打断时，会设置打断标记
                log.info("执行监控程序...");
            }
        }, "monitorThread");
        monitor.start();
    }

    public void stop() {
        monitor.interrupt();
    }

}
