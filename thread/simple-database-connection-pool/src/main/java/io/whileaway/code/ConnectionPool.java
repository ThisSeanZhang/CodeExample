package io.whileaway.code;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ConnectionPool {

    private final LinkedList<Connection> pool = new LinkedList<>();

    public ConnectionPool(int initialSize) {
        if (initialSize > 0) {
            IntStream.range(0, initialSize)
//                    .parallel()
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
            Supplier<Boolean> timeout = getTimeoutSupplier(mills);
            while (pool.isEmpty() && timeout.get()) {
                pool.wait();
            }
            return pool.isEmpty() ? null : pool.removeFirst();
        }
    }

    private static Supplier<Boolean> getTimeoutSupplier(long mills) {
        long future = System.currentTimeMillis() + mills;
        return mills <= 0 ? () -> true : () -> future > System.currentTimeMillis();
    }

    public static void main(String[] args) {
        System.out.println(aa(2));;
    }

    public static int aa(int c) {
        return c == (c = c | 1) ? 777 : c;
    }

}
