package com.greateforest.concurrent;

public final class SingletonV2 {
    private SingletonV2(){}

    private volatile static SingletonV2 INSTANCE = null;

    public static SingletonV2 getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (SingletonV2.class) {
            if (INSTANCE == null) {
                INSTANCE = new SingletonV2();
            }
            return INSTANCE;
        }
    }

}
