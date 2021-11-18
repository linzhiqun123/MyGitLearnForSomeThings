package com.clpm.quartz.util;

/**
 * @author Lzq
 */
public class Single {

    /**
     * //volatile的作用在于禁止指令重排
     *     //1.分配空间 2.初始化 3.对象指向内存地址
     */
    private volatile static Single single;

    /**
     * //私有化构造方法
      */
    private Single() {
    }


    public static Single getSingle() {
        //第一重检测
        if (single == null) {
            synchronized (Single.class) {
               //第二重检测
                if (single == null) {
                    single = new Single();
                }
            }
        }
        return single;
    }

}
