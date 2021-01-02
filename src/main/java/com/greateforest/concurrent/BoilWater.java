package com.greateforest.concurrent;

import ch.qos.logback.core.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 多线程烧开水
 */
@Slf4j(topic = "c.BoilWater")
public class BoilWater {
    public static void main(String[] args) throws InterruptedException {
        Thread employeeZhang = new Thread(() -> {
            log.debug("张工开始洗水壶...");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("张工结束洗水壶...花费1分钟");

            log.debug("张工开始烧开水...");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("张工结束烧开水...花费15分钟");
        }, "张工");
        employeeZhang.start();

        Thread employeeLi = new Thread(() -> {
            log.debug("李工开始洗茶壶、洗茶杯、拿茶叶...");
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("李工结束洗茶壶、洗茶杯、拿茶叶...花费4分钟");
        }, "李工");
        employeeLi.start();

        employeeZhang.join();
        employeeLi.join();

        log.debug("开始泡茶...");
    }
}
