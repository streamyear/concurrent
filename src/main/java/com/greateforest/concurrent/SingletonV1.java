package com.greateforest.concurrent;

import java.io.Serializable;

/**
 * 单例模式，V1
 *
 */

/**
 * 问题1：为什么加 final
 * 防止继承，重写父类的方法，破坏单例
  */

/**
 * 问题2：如果实现了序列化接口, 还要做什么来防止反序列化破坏单例
 * public Object readResolve() {
 *         return INSTANCE;
 *     }
 */
public final class SingletonV1 implements Serializable {
    /**
     * 问题3：为什么设置为私有? 是否能防止反射创建新的实例?
     * 防止在其他的类中，无限的创建对象； 不能防止反射创建实例
     */
    private SingletonV1(){}

    // 问题4：这样初始化是否能保证单例对象创建时的线程安全?  能保证线程安全，有JVM类加载时保证
    private static final SingletonV1 INSTANCE = new SingletonV1();

    // 问题5：为什么提供静态方法而不是直接将 INSTANCE 设置为 public, 说出你知道的理由
    // 更好的控制，封装
    public static SingletonV1 getInstance() {
        return INSTANCE;
    }

    public Object readResolve() {
        return INSTANCE;
    }
}
