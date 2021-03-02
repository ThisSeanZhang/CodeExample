package io.whileaway.code.c4;

import java.util.concurrent.TimeUnit;

public class ShutDown {

    public static void main(String[] args) throws InterruptedException {
        Runner r = new Runner();
        Thread countThread = new Thread(r, "CountThread");
        countThread.start();
        TimeUnit.SECONDS.sleep(1);
        countThread.interrupt();

        Runner runner = new Runner();
        Thread countThread2 = new Thread(runner, "CountThread2");
        countThread2.start();
        TimeUnit.SECONDS.sleep(1);
        runner.cancel();
    }

    private static class Runner implements Runnable {

        private long i;
        private volatile boolean on = true;
        @Override
        public void run() {
            while(on && !Thread.currentThread().isInterrupted()) {
                i++;
            }
            System.out.println("Count i = " + i);
        }

        public void cancel() {
            on = false;
        }
    }
}
