package io.whileaway.code.thread.locked;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TwinsLock implements Lock {

    //静态内部类继承AQS
    private static final class Sync extends AbstractQueuedSynchronizer {
        //初始化同步状态为2，表示没有线程对资源进行访问
        public Sync(int count) {
            if (count < 0) {
                throw new IllegalArgumentException("count must larger than zero.");
            }
            setState(count);
        }

        //获取同步状态：使用循环CAS操作来保证设置status的线程安全
        @Override
        protected int tryAcquireShared(int reduceCount) {
            for(;;) {
                int current = getState();
                int newCount = current - reduceCount;
                if (newCount < 0 || compareAndSetState(current, newCount)) {
                    return newCount; //结果<0,表示获取锁失败；=0，表示获取锁成功，但无剩余锁；=1，表示获取锁成功，还有剩余锁；
                }
            }
        }
        //释放同步状态：因为可能两个线程同时进行释放，需要使用循环CAS来保证安全
        @Override
        protected boolean tryReleaseShared(int returnCount) {
            for(;;) {
                int current = getState();
                int newCount = current + returnCount;
                if (compareAndSetState(current, newCount)) {
                    return true;
                }
            }
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }

    }

    private final Sync sync = new Sync(2);

    @Override
    public void lock() {
        sync.acquireShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        int count = sync.tryAcquireShared(1);
        return count >= 0;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.releaseShared(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}