package com.greateforest.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

public class MultiGuardedSuspensionTest {
    public static void main(String[] args) throws InterruptedException {
        // 收信人
        for (int i = 0; i < 3; i++) {
            People people = new People();
            people.setName("收信人: " + i);
            people.start();
        }

        Thread.sleep(1000);

        // 邮递员
        for (Integer boxId : MessageBox.getBoxIds()) {
            Postman postman = new Postman(boxId, "你好呀，" + boxId);
            postman.setName("邮递员，" + boxId);
            postman.start();
        }
    }
}
@Slf4j(topic = "c.People")
class People extends Thread {
    @Override
    public void run() {
        // 收信件
        SuperGuardedSuspension guarded = MessageBox.createSuperGuardedSuspension();
        log.debug("开始收信件...id: {}", guarded.getId());
        Object msg = guarded.get(5000);
        log.debug("收到的内容为：" + msg);
    }
}

@Slf4j(topic = "c.Postman")
class Postman extends Thread {
    private Integer id;

    private String message;

    public Postman(Integer id, String message) {
        this.id = id;
        this.message = message;
    }

    @Override
    public void run() {
        log.debug("开始送信件...id: {}", id);
        SuperGuardedSuspension guarded = MessageBox.getSuperGuardedSuspension(id);
        guarded.setResult(message);
        log.debug("送信件的内容为：" + message);
    }
}

class MessageBox {
    private static Map<Integer, SuperGuardedSuspension> box = new Hashtable<>();

    private static Integer autoId = 0;

    // 创建id
    private synchronized static Integer  createId() {
        return autoId++;
    }

    // 创建SuperGuardedSuspension
    public static SuperGuardedSuspension createSuperGuardedSuspension() {
        SuperGuardedSuspension suspension = new SuperGuardedSuspension(createId());
        box.put(suspension.getId(), suspension);
        return suspension;
    }

    // 根据id获取SuperGuardedSuspension
    public static SuperGuardedSuspension getSuperGuardedSuspension(Integer id) {
        return box.get(id);
    }

    // 获取所有的id
    public synchronized static List<Integer> getBoxIds() {
        Set<Integer> integers = box.keySet();
        List<Integer> collect = integers.stream().collect(Collectors.toList());
        return collect;
    }

}

/**
 * 获取带超时的版本
 */
class SuperGuardedSuspension{
    private Object response;

    private Integer id;

    private final Object lock = new Object();

    public SuperGuardedSuspension(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

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
