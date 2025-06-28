package com.jeffrey.jdk21;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * 虚拟线程（Virtual Threads）知识点演示
 * 注意：虚拟线程是Java 21的特性，当前环境为Java 17
 * 本示例展示虚拟线程的概念和传统线程的对比
 *
 * @author jeffrey
 */
public class VirtualThreadTest {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 虚拟线程知识点演示（Java 17环境下的概念演示）===\n");

        System.out.println("注意：虚拟线程是Java 21的特性！");
        System.out.println("当前Java版本：" + System.getProperty("java.version"));
        System.out.println("以下演示传统线程模拟虚拟线程的概念\n");

        // 1. 传统线程vs虚拟线程概念对比
        traditionalVsVirtualConcept();

        // 2. 高并发场景模拟
        highConcurrencySimulation();

        // 3. I/O密集型任务演示
        ioIntensiveTaskDemo();

        // 4. 虚拟线程的优势分析
        virtualThreadAdvantages();
    }

    /**
     * 1. 传统线程vs虚拟线程概念对比
     */
    private static void traditionalVsVirtualConcept() throws InterruptedException {
        System.out.println("1. 传统线程vs虚拟线程概念对比：");

        System.out.println("  传统线程特点：");
        System.out.println("    - 1:1映射到操作系统线程");
        System.out.println("    - 每个线程约2MB栈空间");
        System.out.println("    - 创建成本高，数量受限");
        System.out.println("    - 阻塞时占用OS线程资源");

        // 演示传统线程创建
        Thread traditionalThread = new Thread(() -> {
            System.out.println("    传统线程: " + Thread.currentThread().getName());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "traditional-thread");
        traditionalThread.start();
        traditionalThread.join();

        System.out.println("\n  虚拟线程特点（Java 21+）：");
        System.out.println("    - M:N映射到平台线程");
        System.out.println("    - 每个线程约几KB内存");
        System.out.println("    - 创建成本极低，可创建百万个");
        System.out.println("    - 阻塞时不占用载体线程");
        System.out.println("    - 由JVM调度，不是OS调度");

        // 在Java 21中的虚拟线程创建方式（仅展示代码）
        System.out.println("\n  虚拟线程创建方式（Java 21代码示例）：");
        System.out.println("    Thread.ofVirtual().start(() -> { /* 任务 */ });");
        System.out.println("    Executors.newVirtualThreadPerTaskExecutor();");

        System.out.println();
    }

    /**
     * 2. 高并发场景模拟
     */
    private static void highConcurrencySimulation() throws InterruptedException {
        System.out.println("2. 高并发场景模拟：");

        int taskCount = 1000;

        // 使用传统线程池处理高并发
        System.out.println("  使用传统线程池处理 " + taskCount + " 个任务：");
        Instant start = Instant.now();

        ExecutorService threadPool = Executors.newFixedThreadPool(50); // 限制50个线程
        CountDownLatch latch = new CountDownLatch(taskCount);

        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            threadPool.submit(() -> {
                try {
                    // 模拟I/O操作
                    Thread.sleep(10);
                    if (taskId < 5) {
                        System.out.println("    任务 " + taskId + " 完成，线程: " +
                                         Thread.currentThread().getName());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        threadPool.shutdown();
        Duration duration = Duration.between(start, Instant.now());

        System.out.println("  传统线程池完成时间: " + duration.toMillis() + "ms");
        System.out.println("  平均每个任务: " + (duration.toMillis() / (double) taskCount) + "ms");

        System.out.println("\n  虚拟线程的优势（理论分析）：");
        System.out.println("    - 可以为每个任务创建一个虚拟线程");
        System.out.println("    - 不受线程池大小限制");
        System.out.println("    - 阻塞时不占用载体线程");
        System.out.println("    - 更好的吞吐量和响应时间");

        System.out.println();
    }

    /**
     * 3. I/O密集型任务演示
     */
    private static void ioIntensiveTaskDemo() throws InterruptedException {
        System.out.println("3. I/O密集型任务演示：");

        int taskCount = 100;
        System.out.println("  模拟 " + taskCount + " 个I/O密集型任务：");

        Instant start = Instant.now();
        ExecutorService executor = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(taskCount);

        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    // 模拟数据库查询
                    Thread.sleep(20);

                    // 模拟网络请求
                    Thread.sleep(30);

                    // 模拟文件I/O
                    Thread.sleep(10);

                    if (taskId < 5) {
                        System.out.println("    I/O任务 " + taskId + " 完成");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Duration duration = Duration.between(start, Instant.now());
        System.out.println("  " + taskCount + " 个I/O任务完成时间: " + duration.toMillis() + "ms");

        System.out.println("\n  虚拟线程在I/O密集型场景的优势：");
        System.out.println("    - I/O阻塞时，虚拟线程会被挂起");
        System.out.println("    - 载体线程可以执行其他虚拟线程");
        System.out.println("    - 大大提高CPU利用率");
        System.out.println("    - 减少线程上下文切换开销");

        System.out.println();
    }

    /**
     * 4. 虚拟线程的优势分析
     */
    private static void virtualThreadAdvantages() {
        System.out.println("4. 虚拟线程的优势分析：");

        System.out.println("  📈 性能优势：");
        System.out.println("    - 内存占用：虚拟线程 ~KB vs 平台线程 ~2MB");
        System.out.println("    - 创建速度：虚拟线程创建速度快数百倍");
        System.out.println("    - 并发数量：可创建百万级虚拟线程");
        System.out.println("    - 阻塞成本：虚拟线程阻塞几乎无成本");

        System.out.println("\n  🎯 适用场景：");
        System.out.println("    - I/O密集型应用（数据库、网络、文件操作）");
        System.out.println("    - 高并发Web服务");
        System.out.println("    - 微服务架构中的服务间通信");
        System.out.println("    - 传统阻塞式编程模型");

        System.out.println("\n  ⚠️ 注意事项：");
        System.out.println("    - 不适合CPU密集型计算");
        System.out.println("    - 避免使用synchronized（会导致固定）");
        System.out.println("    - 使用ReentrantLock替代synchronized");
        System.out.println("    - 不要池化虚拟线程");

        System.out.println("\n  🔧 最佳实践：");
        System.out.println("    - 使用Executors.newVirtualThreadPerTaskExecutor()");
        System.out.println("    - 每个任务一个虚拟线程");
        System.out.println("    - 结合结构化并发使用");
        System.out.println("    - 监控载体线程池状态");

        System.out.println("\n  📊 性能对比示例：");
        System.out.println("    传统线程池（200线程）处理10000个I/O任务：~5000ms");
        System.out.println("    虚拟线程处理10000个I/O任务：~100ms");
        System.out.println("    性能提升：50倍+");

        System.out.println("\n  🚀 Java 21虚拟线程API示例：");
        printVirtualThreadExamples();

        System.out.println();
    }

    /**
     * 打印虚拟线程API示例代码
     */
    private static void printVirtualThreadExamples() {
        System.out.println("    // 基本创建");
        System.out.println("    Thread.ofVirtual().start(() -> { /* 任务 */ });");
        System.out.println("    ");
        System.out.println("    // 使用执行器");
        System.out.println("    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {");
        System.out.println("        executor.submit(() -> { /* 任务 */ });");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    // 结构化并发（预览特性）");
        System.out.println("    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {");
        System.out.println("        var task1 = scope.fork(() -> fetchData1());");
        System.out.println("        var task2 = scope.fork(() -> fetchData2());");
        System.out.println("        scope.join().throwIfFailed();");
        System.out.println("    }");
    }

}
