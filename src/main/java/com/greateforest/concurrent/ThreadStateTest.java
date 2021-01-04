package com.greateforest.concurrent;

public class ThreadStateTest {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println("test 0000!!!!");
        }, "testThread");
        System.out.println(t.getState());
        t.start();
        System.out.println(t.getState());
        System.out.println(t.getState());
        System.out.println(t.getState());
    }
}

