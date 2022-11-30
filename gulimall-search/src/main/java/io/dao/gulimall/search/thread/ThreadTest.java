package io.dao.gulimall.search.thread;

import java.util.concurrent.*;

public class ThreadTest {
    public static void main(String[] args) {
        System.out.println("main...start...");
//        runThread();
//        runRunnable();
//        runCallable();
        // 以后在业务代码里面，以上三种启动线程的方式都不用
        // 将所有的多线程异步任务都交给线程池执行（资源限制）
        fixedThreadPool();
        System.out.println("main...end...");
    }

    private static void threadPoolExecutor() {
        /**
         * 7大参数：
         * int corePoolSize, 核心线程数，线程池创建好以后准备就绪的线程数，就等待来接收异步任务去执行 e.g. 5个 new Thread()
         * int maximumPoolSize, 最大线程数量，控制资源并发
         * long keepAliveTime,  存活时间，如果当前正在运行的线程数量 > 核心数量，释放空间的线程，只要线程空闲时间大于指定的 keepAliveTime
         * TimeUnit unit,   存活时间的时间单位
         * BlockingQueue<Runnable> workQueue, 阻塞队列，如果任务有很多，就会将目前多的任务放在队列里，只要有空闲线程，就会去队列里面取出新的任务继续执行
         * ThreadFactory threadFactory, 线程创建工厂
         * RejectedExecutionHandler handler 如果队列满了，按照指定的拒绝策略拒绝执行任务
         */
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),        // 默认是Integer的最大值，内存会不够
                Executors.defaultThreadFactory(),       // 使用默认的线程工厂
                new ThreadPoolExecutor.AbortPolicy());  // 使用丢弃策略
    }

    private static void fixedThreadPool() {
        // 10个线程
        ExecutorService service = Executors.newFixedThreadPool(10);
        // service.submit(...) // 可以获取返回值
        service.execute(new Runnable1());  // 不能获取返回值
    }

    private static void runCallable() {
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable1());
        new Thread(futureTask).start();
        // 阻塞等待线程执行完成，获取返回结果
        try {
            System.out.println("main...get..." + futureTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void runRunnable() {
        /**
         * e.g. run Runnable using FutureTask
         * Integer result = 0;
         * new FutureTask(new Runnable1(), result);
         */
        new Thread(new Runnable1()).start();
    }

    private static void runThread() {
        new Thread1().start();
    }

    public static class Thread1 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果:" + i);
        }
    }

    public static class Runnable1 implements Runnable {
        @Override
        public void run() {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果:" + i);
        }
    }

    public static class Callable1 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果:" + i);
            return i;
        }
    }

}
