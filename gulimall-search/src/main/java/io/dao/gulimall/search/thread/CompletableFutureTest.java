package io.dao.gulimall.search.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureTest {

    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        System.out.println("main...start...");
//        runAsync();
//        supplyAsync();
//        supplyAsyncWithHandle();

//        supplyAsyncWithThenRunAsync();
//        supplyAsyncWithThenAcceptAsync();
//        supplyAsyncWithThenApplyAsync();

        // 两个任务都完成后
//        supplyAsyncWithRunAfterBothAsync();
//        supplyAsAsyncWithThenAcceptBothAsync();
//        supplyAsAsyncWithThenCombineAsync();

        // 两个任务只要有一个完成
//        supplyAsAsyncWithRunAfterEitherAsync();
//        supplyAsAsyncWithAcceptEitherAsync();
//        supplyAsAsyncWithApplyToEitherAsync();

//        supplyAsyncAllOf();
        supplyAsyncAnyOf();

        System.out.println("main...end...");
    }

    private static void runAsync() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果:" + i);
        }, executor);
    }

    private static void supplyAsync() {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            int i = 10 / 0;
            System.out.println("运行结果:" + i);
            return i;
        }, executor).whenComplete((result, throwable) -> {
            // 虽然能得到异常信息，但是无法修改返回数据
            System.out.println("异步任务成功完成了...结果是：" + result + "，异常是：" + throwable);
        }).exceptionally((throwable) -> {
            // 可以感知异常同时返回默认值
            return 10;
        });

        try {
            System.out.println("main...get..." + future.get());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("main...exception...");
            e.printStackTrace();
        }
    }

    private static void supplyAsyncWithHandle() {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果:" + i);
            return i;
        }, executor)
                // 方法执行完成后的处理
                .handle((result, throwable) -> {
                    if (result != null) {
                        return result * 2;
                    }
                    return 0;
                });

        try {
            System.out.println("main...get..." + future.get());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("main...exception...");
            e.printStackTrace();
        }
    }

    private static void supplyAsyncWithThenRunAsync() {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果:" + i);
            return i;
        }, executor)
                // 不能获取到上一步的执行结果，但是无返回值
                .thenRunAsync(() -> {
                    System.out.println("任务2启动了...");
                }, executor);
    }

    private static void supplyAsyncWithThenAcceptAsync() {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果:" + i);
            return i;
        }, executor)
                // 能接收上一步结果，但是无返回值
                .thenAcceptAsync((res) -> {
                    System.out.println("任务2启动了..." + res);
                }, executor);
    }

    private static void supplyAsyncWithThenApplyAsync() {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果:" + i);
            return i;
        }, executor)
                // 能接收上一步结果，有返回值
                .thenApplyAsync((res) -> {
                    System.out.println("任务2启动了..." + res);
                    return "Hello " + res;
                }, executor);

        try {
            System.out.println("main...get..." + future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void supplyAsyncWithRunAfterBothAsync() {

        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("任务1结束:" + i);
            return i;
        }, executor);

        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程:" + Thread.currentThread().getId());
            System.out.println("任务2结束:");
            return "Hello";
        }, executor);

        future01.runAfterBothAsync(future02, () -> {
            System.out.println("任务3线程:" + Thread.currentThread().getId());
        }, executor);
    }

    private static void supplyAsAsyncWithThenAcceptBothAsync() {

        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("任务1结束:" + i);
            return i;
        }, executor);

        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程:" + Thread.currentThread().getId());
            System.out.println("任务2结束:");
            return "Hello";
        }, executor);

        future01.thenAcceptBothAsync(future02, (f1, f2) -> {
            System.out.println("任务3线程:" + Thread.currentThread().getId() + "; f1 = " + f1 + "; f2 = " + f2);
        }, executor);
    }

    private static void supplyAsAsyncWithThenCombineAsync() {

        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("任务1结束:" + i);
            return i;
        }, executor);

        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程:" + Thread.currentThread().getId());
            System.out.println("任务2结束:");
            return "Hello";
        }, executor);

        CompletableFuture<String> future = future01.thenCombineAsync(future02, (f1, f2) -> {
            System.out.println("任务3线程:" + Thread.currentThread().getId() + "; f1 = " + f1 + "; f2 = " + f2);
            return f1 + " " + f2;
        }, executor);

        try {
            System.out.println("main...get..." + future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void supplyAsAsyncWithRunAfterEitherAsync() {

        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("任务1结束:" + i);
            return i;
        }, executor);

        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程:" + Thread.currentThread().getId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务2结束:");
            return "Hello";
        }, executor);

        future01.runAfterEitherAsync(future02, () -> {
            System.out.println("任务3线程:" + Thread.currentThread().getId());
        }, executor);

    }

    private static void supplyAsAsyncWithAcceptEitherAsync() {

        CompletableFuture<Object> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("任务1结束:" + i);
            return i;
        }, executor);

        CompletableFuture<Object> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程:" + Thread.currentThread().getId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务2结束:");
            return "Hello";
        }, executor);

        future01.acceptEitherAsync(future02, (f) -> {
            System.out.println("任务3线程:" + Thread.currentThread().getId() + ", result = " + f);
        }, executor);

    }

    private static void supplyAsAsyncWithApplyToEitherAsync() {

        CompletableFuture<Object> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程:" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("任务1结束:" + i);
            return i;
        }, executor);

        CompletableFuture<Object> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程:" + Thread.currentThread().getId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务2结束:");
            return "Hello";
        }, executor);

        CompletableFuture<String> future = future01.applyToEitherAsync(future02, (f) -> {
            System.out.println("任务3线程:" + Thread.currentThread().getId() + ", result = " + f);
            return f + "->  task 3";
        }, executor);

        try {
            System.out.println("main...get..." + future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    private static void supplyAsyncAllOf() {
        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片信息");
            return "hello.jpg";
        }, executor);
        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性");
            return "黑色+256G";
        }, executor);
        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品介绍");
            return "华为";
        }, executor);
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futureImg, futureAttr, futureDesc);
        try {
            // 等待所有结果完成
            allOf.get();
            System.out.println("main...get..." + futureImg.get() + " " + futureAttr.get() + " " + futureDesc.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void supplyAsyncAnyOf() {
        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片信息");
            return "hello.jpg";
        }, executor);
        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性");
            return "黑色+256G";
        }, executor);
        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品介绍");
            return "华为";
        }, executor);
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futureImg, futureAttr, futureDesc);
        try {
            // 等待所有结果完成
            System.out.println("main...get..." + anyOf.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
