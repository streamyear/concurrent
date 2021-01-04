package com.greateforest.concurrent;

/**
 * 比较推荐的方式
 */
public final class SingletonV3 {
    private SingletonV3(){}

    private static class LazyHolder{
        static final SingletonV3 INSTANCE = new SingletonV3();
    }

    public static SingletonV3 getInstance() {
        return LazyHolder.INSTANCE;
    }
}
