package io.whileaway.code.thread.locked.util;

import java.util.concurrent.TimeUnit;

public class SleepUtil {

    public static final void second(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
    }

    public static final void secondThrow(long seconds) throws InterruptedException {
        TimeUnit.SECONDS.sleep(seconds);
    }
}
