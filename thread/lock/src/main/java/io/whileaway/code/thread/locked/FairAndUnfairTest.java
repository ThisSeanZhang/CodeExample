package io.whileaway.code.thread.locked;

import io.whileaway.code.thread.locked.util.SleepUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class FairAndUnfairTest {
    private static ReentrantLock2 fairLock = new ReentrantLock2(true);
    private static ReentrantLock2 unfairLock = new ReentrantLock2(false);

    public static void main(String[] args) {
        System.out.println("公平: \n");
        testLock(fairLock);
        System.out.println("非公平: \n");
        testLock(unfairLock);
    }

    private static void testLock(ReentrantLock2 lock) {
        for (int i = 0; i < 5; i++) {
            Job job = new Job(lock);
            job.setDaemon(true);
            job.start();
        }

        SleepUtil.second(15);
    }
    private static class Job extends Thread {
        private final ReentrantLock2 lock;
        public Job(ReentrantLock2 lock) {
            this.lock = lock;
        }
        public void run() {// 连续2次打印当前的Thread和等待队列中的Thread（略）
            lock.lock();
            try {
                SleepUtil.second(1);
//                SleepUtil.second(Math.min(5, Double.valueOf(Math.random() * 10).longValue()));
                String threadNames = lock.getQueuedThreads().stream().map(Thread::getName).collect(Collectors.joining(","));
                System.out.println(Thread.currentThread().getName() + ":" + threadNames);
            } finally {
                lock.unlock();
            }
            lock.lock();
            try {
                SleepUtil.second(1);
//                SleepUtil.second(Math.min(5, Double.valueOf(Math.random() * 10).longValue()));
                String threadNames = lock.getQueuedThreads().stream().map(Thread::getName).collect(Collectors.joining(","));
                System.out.println(Thread.currentThread().getName() + ":" + threadNames);
            } finally {
                lock.unlock();
            }
            System.out.println();
        }
    }
    private static class ReentrantLock2 extends ReentrantLock {
        public ReentrantLock2(boolean fair) {
            super(fair);
        }
        public Collection<Thread> getQueuedThreads() {
            List<Thread> arrayList = new ArrayList<>(super.getQueuedThreads());
            Collections.reverse(arrayList);
            return arrayList;
        }
    }
}

