package io.whileaway.code.c4;

import io.whileaway.code.c4.util.SleepUtil;

import java.util.concurrent.TimeUnit;

public class Interrupted {

    public static void main(String[] args) throws Exception{

        //SleepThread 不停尝试睡眠
        Thread sleepThread =  new Thread(() -> { while(true) { SleepUtil.second(100); } }, "SleepThread");
        sleepThread.setDaemon(true);

        // busyRunner 不停运行
        Thread busyRunner = new Thread(() -> { while(true) {} }, "BusyRunner");
        busyRunner.setDaemon(true);

        Thread catch_ = new Thread(() -> {
            try {
                while (true) {
                    SleepUtil.secondThrow(10);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "catch ");
        catch_.setDaemon(true);

        sleepThread.start();
        busyRunner.start();
        catch_.start();

        // 休眠让充分运行
        TimeUnit.SECONDS.sleep(5);

        sleepThread.interrupt();
        busyRunner.interrupt();
        catch_.interrupt();

        System.out.println("SleepThread interrupt is: " + sleepThread.isInterrupted() );
        System.out.println("busyRunner interrupt is: " + busyRunner.isInterrupted() );
        System.out.println("catch_ interrupt is: " + catch_.isInterrupted() );
        System.out.println();
        System.out.println("SleepThread interrupt is: " + sleepThread.isInterrupted() );
        System.out.println("busyRunner interrupt is: " + busyRunner.isInterrupted() );
        System.out.println("catch_ interrupt is: " + catch_.isInterrupted() );

        // 防止立刻退出
        SleepUtil.second(2);
    }
}
