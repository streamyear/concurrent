package com.greateforest.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * 消息队列测试
 * 生产者-消费者模式（异步）
 * 线程间的通信
 */
public class MessageQueueTest {
    public static void main(String[] args) {
        MessageQueue queue = new MessageQueue(2);
        // 生产者 producer
        for (int i = 1; i <4 ; i++) {
            int id = i;
            new Thread(() -> {
                Message message = new Message(id, "Message content: " + id);
                queue.put(message);
            }, "producer" + i).start();
        }
        
        // 消费者
        new Thread(() -> {
           while (true){
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               Message take = queue.take();
           }
        }, "consumer").start();
    }
}

@Slf4j(topic = "c.MessageQueue")
class MessageQueue {
    private LinkedList<Message> list;

    private int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
        this.list = new LinkedList<>();
    }

    // 获取消息
    public Message take() {
        synchronized (list) {
            while (list.isEmpty()) {
                try {
                    log.debug("队列中无消息，消费者: {} wait...", Thread.currentThread().getName());
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message message = list.removeFirst();
            log.debug("消费者获取到的消息为：{}", message);
            list.notifyAll();
            return message;
        }
    }

    // 放入消息
    public void put(Message message) {
        synchronized (list) {
            while (list.size() >= capacity) {
                try {
                    log.debug("队列中消息已满，生产者: {} wait...", Thread.currentThread().getName());
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            list.addLast(message);
            log.debug("{}, 存入消息：{}", Thread.currentThread().getName(), message);
            list.notifyAll();
        }
    }

}

class Message {
    private Integer id;

    private Object msg;

    public Message(Integer id, Object msg) {
        this.id = id;
        this.msg = msg;
    }

    public Integer getId() {
        return id;
    }

    public Object getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", msg=" + msg +
                '}';
    }
}
