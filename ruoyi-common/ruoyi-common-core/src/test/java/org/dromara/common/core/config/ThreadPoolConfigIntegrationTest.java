package org.dromara.common.core.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.dromara.common.core.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * ThreadPoolConfig 集成测试类
 *
 * <p>测试线程池配置的 Bean 创建、参数配置、线程命名、虚拟线程支持等功能。
 *
 * @author Test Team
 */
@DisplayName("ThreadPoolConfig 集成测试")
class ThreadPoolConfigIntegrationTest extends BaseIntegrationTest {

    @Autowired private ApplicationContext applicationContext;

    @Autowired private ScheduledExecutorService scheduledExecutorService;

    @Nested
    @DisplayName("Bean 创建测试")
    class BeanCreationTest {

        @Test
        @DisplayName("应该成功创建 scheduledExecutorService Bean")
        void shouldCreateScheduledExecutorServiceBean() {
            assertThat(scheduledExecutorService).isNotNull();
        }

        @Test
        @DisplayName("应该能够从 ApplicationContext 获取 scheduledExecutorService")
        void shouldGetBeanFromApplicationContext() {
            ScheduledExecutorService bean =
                    applicationContext.getBean(
                            "scheduledExecutorService", ScheduledExecutorService.class);

            assertThat(bean).isNotNull();
            assertThat(bean).isSameAs(scheduledExecutorService);
        }

        @Test
        @DisplayName("scheduledExecutorService 应该是单例")
        void shouldBeSingleton() {
            ScheduledExecutorService bean1 =
                    applicationContext.getBean(ScheduledExecutorService.class);
            ScheduledExecutorService bean2 =
                    applicationContext.getBean(ScheduledExecutorService.class);

            assertThat(bean1).isSameAs(bean2);
        }

        @Test
        @DisplayName("应该是 ScheduledThreadPoolExecutor 实例")
        void shouldBeScheduledThreadPoolExecutorInstance() {
            assertThat(scheduledExecutorService).isInstanceOf(ScheduledThreadPoolExecutor.class);
        }
    }

    @Nested
    @DisplayName("线程池配置测试")
    class ThreadPoolConfigurationTest {

        @Test
        @DisplayName("核心线程数应该等于 CPU核心数+1")
        void shouldHaveCorrectCorePoolSize() {
            int expectedCorePoolSize = Runtime.getRuntime().availableProcessors() + 1;

            if (scheduledExecutorService instanceof ScheduledThreadPoolExecutor executor) {
                assertThat(executor.getCorePoolSize()).isEqualTo(expectedCorePoolSize);
            }
        }

        @Test
        @DisplayName("应该使用 CallerRunsPolicy 拒绝策略")
        void shouldUseCallerRunsPolicy() {
            if (scheduledExecutorService instanceof ScheduledThreadPoolExecutor executor) {
                assertThat(executor.getRejectedExecutionHandler().getClass().getSimpleName())
                        .isEqualTo("CallerRunsPolicy");
            }
        }

        @Test
        @DisplayName("线程池不应该已关闭")
        void shouldNotBeShutdown() {
            assertThat(scheduledExecutorService.isShutdown()).isFalse();
        }

        @Test
        @DisplayName("线程池不应该已终止")
        void shouldNotBeTerminated() {
            assertThat(scheduledExecutorService.isTerminated()).isFalse();
        }
    }

    @Nested
    @DisplayName("线程命名测试")
    class ThreadNamingTest {

        @Test
        @DisplayName("线程名称应该包含 schedule-pool 或 virtual-schedule-pool")
        void shouldHaveCorrectThreadNamePattern() throws Exception {
            AtomicInteger threadNameChecked = new AtomicInteger(0);

            // 提交一个任务来检查线程名称
            scheduledExecutorService
                    .submit(
                            () -> {
                                String threadName = Thread.currentThread().getName();

                                // 线程名称应该匹配 "schedule-pool-N" 或 "virtual-schedule-pool-N"
                                assertThat(threadName)
                                        .satisfiesAnyOf(
                                                name ->
                                                        assertThat(name)
                                                                .matches("schedule-pool-\\d+"),
                                                name ->
                                                        assertThat(name)
                                                                .matches(
                                                                        "virtual-schedule-pool-\\d+"));

                                threadNameChecked.incrementAndGet();
                            })
                    .get(5, TimeUnit.SECONDS);

            assertThat(threadNameChecked.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("守护线程标志应该为 true")
        void shouldBeDaemonThread() throws Exception {
            AtomicInteger daemonChecked = new AtomicInteger(0);

            scheduledExecutorService
                    .submit(
                            () -> {
                                // BasicThreadFactory 配置的线程应该是守护线程
                                boolean isDaemon = Thread.currentThread().isDaemon();
                                assertThat(isDaemon).isTrue();

                                daemonChecked.incrementAndGet();
                            })
                    .get(5, TimeUnit.SECONDS);

            assertThat(daemonChecked.get()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("任务执行测试")
    class TaskExecutionTest {

        @Test
        @DisplayName("应该能够提交并执行 Runnable 任务")
        void shouldExecuteRunnableTask() throws Exception {
            AtomicInteger counter = new AtomicInteger(0);

            scheduledExecutorService
                    .submit(() -> counter.incrementAndGet())
                    .get(5, TimeUnit.SECONDS);

            assertThat(counter.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("应该能够提交并执行 Callable 任务")
        void shouldExecuteCallableTask() throws Exception {
            String result =
                    scheduledExecutorService
                            .submit(() -> "Hello from ThreadPool")
                            .get(5, TimeUnit.SECONDS);

            assertThat(result).isEqualTo("Hello from ThreadPool");
        }

        @Test
        @DisplayName("应该能够调度延时任务")
        void shouldScheduleDelayedTask() throws Exception {
            AtomicInteger counter = new AtomicInteger(0);
            long startTime = System.currentTimeMillis();

            scheduledExecutorService
                    .schedule(() -> counter.incrementAndGet(), 100, TimeUnit.MILLISECONDS)
                    .get(5, TimeUnit.SECONDS);

            long duration = System.currentTimeMillis() - startTime;

            assertThat(counter.get()).isEqualTo(1);
            assertThat(duration).isGreaterThanOrEqualTo(100); // 至少延迟100ms
        }

        @Test
        @DisplayName("应该能够调度周期性任务")
        void shouldSchedulePeriodicTask() throws Exception {
            AtomicInteger counter = new AtomicInteger(0);

            var future =
                    scheduledExecutorService.scheduleAtFixedRate(
                            () -> counter.incrementAndGet(),
                            0, // 初始延迟
                            50, // 周期
                            TimeUnit.MILLISECONDS);

            // 等待一段时间让任务执行多次
            Thread.sleep(250);
            future.cancel(false);

            // 应该至少执行了3次 (0ms, 50ms, 100ms, 150ms, 200ms)
            assertThat(counter.get()).isGreaterThanOrEqualTo(3);
        }

        @Test
        @DisplayName("应该能够取消已调度的任务")
        void shouldCancelScheduledTask() throws Exception {
            AtomicInteger counter = new AtomicInteger(0);

            var future =
                    scheduledExecutorService.schedule(
                            () -> counter.incrementAndGet(),
                            1000, // 延迟1秒
                            TimeUnit.MILLISECONDS);

            // 立即取消任务
            boolean cancelled = future.cancel(false);

            Thread.sleep(1100); // 等待超过延迟时间

            assertThat(cancelled).isTrue();
            assertThat(counter.get()).isEqualTo(0); // 任务不应该被执行
        }
    }

    @Nested
    @DisplayName("异常处理测试")
    class ExceptionHandlingTest {

        @Test
        @DisplayName("任务抛出异常时不应该导致线程池崩溃")
        void shouldHandleTaskExceptionGracefully() throws Exception {
            AtomicInteger successCount = new AtomicInteger(0);

            // 提交一个会抛异常的任务
            scheduledExecutorService.submit(
                    () -> {
                        throw new RuntimeException("Test exception");
                    });

            // 稍等片刻让异常任务完成
            Thread.sleep(100);

            // 线程池应该仍然可用,能够执行后续任务
            scheduledExecutorService
                    .submit(() -> successCount.incrementAndGet())
                    .get(5, TimeUnit.SECONDS);

            assertThat(successCount.get()).isEqualTo(1);
            assertThat(scheduledExecutorService.isShutdown()).isFalse();
        }

        @Test
        @DisplayName("应该能够捕获任务执行中的异常")
        void shouldCatchTaskException() {
            var future =
                    scheduledExecutorService.submit(
                            () -> {
                                throw new IllegalStateException("Test exception");
                            });

            assertThatThrownBy(() -> future.get(5, TimeUnit.SECONDS))
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Test exception");
        }
    }

    @Nested
    @DisplayName("并发测试")
    class ConcurrencyTest {

        @Test
        @DisplayName("应该能够并发执行多个任务")
        void shouldExecuteMultipleTasksConcurrently() throws Exception {
            int taskCount = 10;
            AtomicInteger counter = new AtomicInteger(0);

            var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();

            for (int i = 0; i < taskCount; i++) {
                futures.add(
                        scheduledExecutorService.submit(
                                () -> {
                                    counter.incrementAndGet();
                                    Thread.sleep(10);
                                    return null;
                                }));
            }

            // 等待所有任务完成
            for (var future : futures) {
                future.get(5, TimeUnit.SECONDS);
            }

            assertThat(counter.get()).isEqualTo(taskCount);
        }

        @Test
        @DisplayName("应该能够处理高负载任务提交")
        void shouldHandleHighLoadTaskSubmission() throws Exception {
            int taskCount = 100;
            AtomicInteger successCount = new AtomicInteger(0);

            var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();

            for (int i = 0; i < taskCount; i++) {
                futures.add(
                        scheduledExecutorService.submit(
                                () -> {
                                    successCount.incrementAndGet();
                                }));
            }

            // 等待所有任务完成
            for (var future : futures) {
                future.get(10, TimeUnit.SECONDS);
            }

            assertThat(successCount.get()).isEqualTo(taskCount);
        }
    }

    @Nested
    @DisplayName("真实业务场景测试")
    class RealWorldScenarioTest {

        @Test
        @DisplayName("应该能够执行定时数据同步任务")
        void shouldExecuteScheduledDataSyncTask() throws Exception {
            AtomicInteger syncCount = new AtomicInteger(0);

            // 模拟每50ms执行一次数据同步
            var future =
                    scheduledExecutorService.scheduleAtFixedRate(
                            () -> {
                                // 模拟数据同步逻辑
                                syncCount.incrementAndGet();
                            },
                            0,
                            50,
                            TimeUnit.MILLISECONDS);

            Thread.sleep(250);
            future.cancel(false);

            assertThat(syncCount.get()).isGreaterThanOrEqualTo(3);
        }

        @Test
        @DisplayName("应该能够执行延迟缓存清理任务")
        void shouldExecuteDelayedCacheCleanupTask() throws Exception {
            AtomicInteger cleanupExecuted = new AtomicInteger(0);

            // 模拟延迟100ms执行缓存清理
            scheduledExecutorService
                    .schedule(
                            () -> {
                                // 模拟缓存清理逻辑
                                cleanupExecuted.incrementAndGet();
                            },
                            100,
                            TimeUnit.MILLISECONDS)
                    .get(5, TimeUnit.SECONDS);

            assertThat(cleanupExecuted.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("应该能够执行异步日志记录任务")
        void shouldExecuteAsyncLoggingTask() throws Exception {
            var logMessages = new java.util.concurrent.CopyOnWriteArrayList<String>();

            scheduledExecutorService
                    .submit(
                            () -> {
                                // 模拟异步日志记录
                                logMessages.add("User login");
                                logMessages.add("Data processed");
                                logMessages.add("Request completed");
                            })
                    .get(5, TimeUnit.SECONDS);

            assertThat(logMessages).hasSize(3);
        }
    }
}
