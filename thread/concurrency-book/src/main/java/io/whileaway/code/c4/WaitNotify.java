package io.whileaway.code.c4;

import io.whileaway.code.c4.util.SleepUtil;

import java.util.concurrent.TimeUnit;

public class WaitNotify {
    static boolean flag = true;
    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread waitThread = new Thread(new Wait(), "WaitThread");
        waitThread.start();

        Thread waitThread2 = new Thread(new Wait(), "WaitThread2");
        waitThread2.start();

        TimeUnit.SECONDS.sleep(1);
        System.out.println();

        Thread notifyThread = new Thread(new Notify(), "NotifyThread");
        notifyThread.start();
    }

    static class Wait implements Runnable {

        @Override
        public void run() {
            synchronized (lock) {
                while (flag) {
                    try {
                        System.out.println(Thread.currentThread() + " flag is true. wait" + System.currentTimeMillis());
                        lock.wait(20_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //条件满足
                System.out.println(Thread.currentThread() + " flag is false. wait" + System.currentTimeMillis());
                System.out.println();
//                SleepUtil.second(2);
//                lock.notify();
            }
        }
    }

    static class Notify implements Runnable {

        @Override
        public void run() {
            synchronized (lock) {
                        System.out.println(Thread.currentThread() + " hold lock. notify" + System.currentTimeMillis());
                        lock.notify(); // 只唤醒一个线程
//                        lock.notifyAll(); // 唤醒所有线程
                        flag = false;
                        SleepUtil.second(5);
            }
            // 再次加锁
            synchronized (lock) {
                System.out.println(Thread.currentThread() + " hold lock again. sleep" + System.currentTimeMillis());
                SleepUtil.second(5);
            }
        }
    }
}
