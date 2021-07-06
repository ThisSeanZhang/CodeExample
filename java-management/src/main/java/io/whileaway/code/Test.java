package io.whileaway.code;

import javax.management.MBeanServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Test {
    public static void main( String[] args )
    {
        testManagementFactory();

    }

    private static void testManagementFactory() {
//      1获取JVM输入参数
        List<String> list= ManagementFactory.getRuntimeMXBean().getInputArguments();
//      2.获取当前JVM进程的PID
        String name = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println("up time: " + ManagementFactory.getRuntimeMXBean().getUptime());
        String pid = name.split("@")[0];
//      3.获取当前系统的负载
        double systemLoadAverage = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        System.out.println("system load: " + systemLoadAverage);
        System.out.println(ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors());
//      4.获取内存相关的
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();//堆内存
        MemoryUsage nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();//堆外内存
        System.out.println("heapMemoryUsage: " + heapMemoryUsage + ", nonHeapMemoryUsage: " + nonHeapMemoryUsage);
        ManagementFactory.getMemoryMXBean().getObjectPendingFinalizationCount();
//      5.获取堆栈信息相当于jstack
        ManagementFactory.getThreadMXBean().getThreadCount();
        ThreadInfo[] threadInfos = ManagementFactory.getThreadMXBean().dumpAllThreads(false, false);
//        for (ThreadInfo info : threadInfos) {
//            System.out.println(info.toString());
//        }
//      ThreadInfo 里有线程的信息

//      进行垃圾回收监控
//      MavenSpringApp.main(new String[]{"-gcutil", "-h5",pid,"1s"});

//      如何获取HotSpotDiagnosticMXBean   ？
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();

//      HotSpotDiagnosticMXBean   hotspotDiagnosticMXBean = new PlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);

        //获取young GC 和full GC 次数
        List<GarbageCollectorMXBean> list1=ManagementFactory.getGarbageCollectorMXBeans();
        for(GarbageCollectorMXBean e:list1){
            System.out.println(String.format("name=%s,count=%s,time=%s",e.getName(),e.getCollectionCount(),e.getCollectionTime()));
        }

        System.out.println(System.getProperty("os.name"));

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            printDockerInfo();
        }
        System.out.println("1000 use time" + (System.currentTimeMillis() - start));
    }

    private static void printDockerInfo() {
        long startTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        System.out.println("get RUN time: " + (System.currentTimeMillis() - startTime));
        startTime = System.currentTimeMillis();
        try {
            Process process = runtime.exec("cat /proc/self/cgroup");
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
            System.out.println("create Buffer Reader Time: " + (System.currentTimeMillis() - startTime));
            startTime = System.currentTimeMillis();
            String line;
//            System.out.println("OUTPUT");
//            stdoutReader.lines()
//                    .filter(Objects::nonNull)
//                    .filter(l -> l.contains("docker"))
//                    .map(l -> l.split(":"))
//                    .map(arr -> arr[arr.length -1])
//                    .forEach(System.out::println);
//            System.out.println("ERROR");
//            while ((line = stderrReader.readLine()) != null) {
//                System.out.println(line);
//            }
            int exitVal = process.waitFor();
            System.out.println("process exit value is " + exitVal + " ,end Time: " + (System.currentTimeMillis() - startTime));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Number> collectThreadInfo() {
        final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        Map<String, Number> map = new LinkedHashMap<String, Number>();
        map.put("jvm.thread.count", threadBean.getThreadCount());
        map.put("jvm.thread.daemon.count", threadBean.getDaemonThreadCount());
        map.put("jvm.thread.totalstarted.count", threadBean.getTotalStartedThreadCount());
        ThreadInfo[] threadInfos = threadBean.getThreadInfo(threadBean.getAllThreadIds());

        int newThreadCount = 0;
        int runnableThreadCount = 0;
        int blockedThreadCount = 0;
        int waitThreadCount = 0;
        int timeWaitThreadCount = 0;
        int terminatedThreadCount = 0;

        if (threadInfos != null) {
            for (ThreadInfo threadInfo : threadInfos) {
                if (threadInfo != null) {
                    switch (threadInfo.getThreadState()) {
                        case NEW:
                            newThreadCount++;
                            break;
                        case RUNNABLE:
                            runnableThreadCount++;
                            break;
                        case BLOCKED:
                            blockedThreadCount++;
                            break;
                        case WAITING:
                            waitThreadCount++;
                            break;
                        case TIMED_WAITING:
                            timeWaitThreadCount++;
                            break;
                        case TERMINATED:
                            terminatedThreadCount++;
                            break;
                        default:
                            break;
                    }
                } else {
                    /*
                     * If a thread of a given ID is not alive or does not exist,
                     * the corresponding element in the returned array will,
                     * contain null,because is mut exist ,so the thread is terminated
                     */
                    terminatedThreadCount++;
                }
            }
        }

        map.put("jvm.thread.new.count", newThreadCount);
        map.put("jvm.thread.runnable.count", runnableThreadCount);
        map.put("jvm.thread.blocked.count", blockedThreadCount);
        map.put("jvm.thread.waiting.count", waitThreadCount);
        map.put("jvm.thread.time_waiting.count", timeWaitThreadCount);
        map.put("jvm.thread.terminated.count", terminatedThreadCount);

        long[] ids = threadBean.findDeadlockedThreads();
        map.put("jvm.thread.deadlock.count", ids == null ? 0 : ids.length);

        return map;
    }
}
