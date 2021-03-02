package io.whileaway.code.c4;

import java.util.concurrent.TimeUnit;

public class Interrupted {

    public static void main(String[] args) {

    }

    static class SleepRunner implements Runnable {

        @Override
        public void run() {
            while(true) {
//                TimeUnit.SECONDS.sleep(10);
            }
        }
    }
}
