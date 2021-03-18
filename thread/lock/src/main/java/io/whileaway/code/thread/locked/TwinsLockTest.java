package io.whileaway.code.thread.locked;

import io.whileaway.code.thread.locked.util.SleepUtil;

import java.util.concurrent.locks.Lock;

public class TwinsLockTest {
    public static void main(String[] args) {
        test();
    }
    public static void test() {
        final Lock lock = new TwinsLock2();
        class Worker extends Thread {
            public void run() {
                System.out.println("start : " + Thread.currentThread().getName());
//                while (true) {
                    lock.lock();
                    try {
                        SleepUtil.second(1);
                        System.out.println(Thread.currentThread().getName());
                    } finally {
                        lock.unlock();
                    }
//                }
            }
        }
// 启动10个线程
        for (int i = 0; i < 10; i++) {
            Worker w = new Worker();
            w.setName("thread-" + i);
            w.setDaemon(true);
            w.start();
        }
// 每隔1秒换行
        for (int i = 0; i < 10; i++) {
            SleepUtil.second(1);
            System.out.println();
        }
    }
}