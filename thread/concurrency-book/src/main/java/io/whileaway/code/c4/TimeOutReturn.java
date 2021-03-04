package io.whileaway.code.c4;

import java.util.Objects;

public class TimeOutReturn {

    private Object result;

    public synchronized Object get(long mills) throws InterruptedException {
        long future = System.currentTimeMillis() + mills;
        long remaining = mills;
        while (result != null && remaining > 0) {
            wait(remaining);
            remaining = future - System.currentTimeMillis();
        }
        return result;
    }
}
