package io.whileaway.code.thread.pool;

public interface ThreadPool<Job extends Runnable> {

    void execute(Job job);

    void shutdown();

    void addWorks(int nim);

    void removeWorker(int num);

    int getJobSize();
}
