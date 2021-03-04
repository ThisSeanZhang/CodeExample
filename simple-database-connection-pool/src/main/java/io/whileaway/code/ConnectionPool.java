package io.whileaway.code;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ConnectionPool {

    private final LinkedList<Connection> pool = new LinkedList<>();

    public ConnectionPool(int initialSize) {
        if (initialSize > 0) {
            IntStream.range(0, initialSize)
                    .mapToObj(a -> ConnectionDriver.createConnection())
                    .forEach(pool::add);
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (pool) {
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }


    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {

//            Function<Long, Long> updateWay = mills <= 0 ? Function.identity() : l -> l - System.currentTimeMillis();

            if(mills <= 0) {
                while (pool.isEmpty()) {
                    pool.wait();
                }
                return pool.removeFirst();
            } else {
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0) {
                    pool.wait();
                    remaining = future - System.currentTimeMillis();
                }
                return pool.isEmpty() ? null : pool.removeFirst();
            }

        }
    }

}
