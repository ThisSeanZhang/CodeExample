package io.whileaway.code.thread.locked;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TwinsLock2 implements Lock {
    private final Sync sync = new Sync(2);
    private static final class Sync extends AbstractQueuedSynchronizer {
        Sync(int count) {
            if (count <= 0) {
                throw new IllegalArgumentException("count must large than zero.");
            }
            setState(count);
        }
        public int tryAcquireShared(int reduceCount) {
            for (;;) {
                int current = getState();int newCount = current - reduceCount;
                if (newCount < 0 || compareAndSetState(current, newCount)) {
                    System.out.println("current remain count: " + newCount);
                    return newCount;
                }
            }
        }
        public boolean tryReleaseShared(int returnCount) {
            for (;;) {
                int current = getState();
                int newCount = current + returnCount;
                if (compareAndSetState(current, newCount)) {
                    System.out.println("after release remain count: " + newCount);
                    return true;
                }
            }
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }
    }

    public void lock() {
        System.out.println("try allocate: " + Thread.currentThread().getName());
        sync.acquireShared(1);
    }
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }
    public boolean tryLock() {
        return sync.tryAcquireShared(1) >= 0;
    }

    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    public void unlock() {
        sync.releaseShared(1);
    }
    public Condition newCondition() { return sync.newCondition(); }


}
