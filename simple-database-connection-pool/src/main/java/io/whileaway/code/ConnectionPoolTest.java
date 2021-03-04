package io.whileaway.code;

import java.util.concurrent.CountDownLatch;

public class ConnectionPoolTest {
    static ConnectionPool pool = new ConnectionPool(10);

    static CountDownLatch start = new CountDownLatch(1);

    static CountDownLatch end;

    public static void main(String[] args) {
        int threadCount = 10;
        end = new CountDownLatch(threadCount);
        int count = 20;
    }
}

