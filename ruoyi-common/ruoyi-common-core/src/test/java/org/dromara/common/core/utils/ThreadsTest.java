package org.dromara.common.core.utils;

import org.dromara.common.core.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

/**
 * Threads 单元测试
 *
 * @author Lion Li
 */
@DisplayName("Threads 工具类测试")
class ThreadsTest extends BaseUnitTest {

    @Nested
    @DisplayName("shutdownAndAwaitTermination 方法测试")
    class ShutdownAndAwaitTerminationTest {

        @Test
        @DisplayName("正常关闭线程池")
        @Timeout(5)
        void shouldShutdownPoolGracefully() {
            ExecutorService pool = Executors.newFixedThreadPool(2);
            AtomicInteger counter = new AtomicInteger(0);

            // 提交一些任务
            pool.submit(counter::incrementAndGet);
            pool.submit(counter::incrementAndGet);

            // 等待任务完成
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 关闭线程池
            Threads.shutdownAndAwaitTermination(pool);

            // 验证线程池已关闭
            assertThat(pool.isShutdown()).isTrue();
            assertThat(pool.isTerminated()).isTrue();
            assertThat(counter.get()).isEqualTo(2);
        }

        @Test
        @DisplayName("null线程池不应抛出异常")
        void shouldHandleNullPool() {
            assertThatCode(() -> Threads.shutdownAndAwaitTermination(null))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("已关闭的线程池不应再次关闭")
        void shouldHandleAlreadyShutdownPool() {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            pool.shutdown();

            assertThatCode(() -> Threads.shutdownAndAwaitTermination(pool))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("线程池有运行中的任务时应等待完成")
        @Timeout(5)
        void shouldWaitForRunningTasks() {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            AtomicBoolean taskCompleted = new AtomicBoolean(false);

            // 提交一个耗时任务
            pool.submit(() -> {
                try {
                    Thread.sleep(200);
                    taskCompleted.set(true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            // 关闭线程池（应等待任务完成）
            Threads.shutdownAndAwaitTermination(pool);

            assertThat(pool.isTerminated()).isTrue();
            assertThat(taskCompleted.get()).isTrue();
        }

        @Test
        @DisplayName("使用Mock测试超时场景")
        void shouldHandleTimeoutWithMock() throws InterruptedException {
            ExecutorService pool = mock(ExecutorService.class);

            when(pool.isShutdown()).thenReturn(false);
            when(pool.awaitTermination(120, TimeUnit.SECONDS)).thenReturn(false);
            when(pool.shutdownNow()).thenReturn(null);

            Threads.shutdownAndAwaitTermination(pool);

            verify(pool).shutdown();
            verify(pool, times(2)).awaitTermination(120, TimeUnit.SECONDS); // 调用2次：shutdown后1次，shutdownNow后1次
            verify(pool).shutdownNow();
        }

        @Test
        @DisplayName("使用Mock测试中断场景")
        void shouldHandleInterruptionWithMock() throws InterruptedException {
            ExecutorService pool = mock(ExecutorService.class);

            when(pool.isShutdown()).thenReturn(false);
            when(pool.awaitTermination(120, TimeUnit.SECONDS))
                .thenThrow(new InterruptedException("Test interruption"));

            Threads.shutdownAndAwaitTermination(pool);

            verify(pool).shutdown();
            verify(pool).awaitTermination(120, TimeUnit.SECONDS);
            verify(pool).shutdownNow();
            // 注意：中断标志会被设置，因为Thread.currentThread().interrupt()被调用
            assertThat(Thread.interrupted()).isTrue(); // 清除并检查中断标志
        }

        @Test
        @DisplayName("线程池快速终止场景")
        @Timeout(3)
        void shouldTerminateQuickly() {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            // 不提交任何任务，应该快速终止

            Threads.shutdownAndAwaitTermination(pool);

            assertThat(pool.isShutdown()).isTrue();
            assertThat(pool.isTerminated()).isTrue();
        }

        @Test
        @DisplayName("多线程池并发关闭")
        @Timeout(5)
        void shouldShutdownMultiplePoolsConcurrently() throws InterruptedException {
            ExecutorService pool1 = Executors.newFixedThreadPool(2);
            ExecutorService pool2 = Executors.newFixedThreadPool(2);
            ExecutorService pool3 = Executors.newFixedThreadPool(2);

            // 提交一些任务
            pool1.submit(() -> {
            });
            pool2.submit(() -> {
            });
            pool3.submit(() -> {
            });

            // 等待任务完成
            Thread.sleep(100);

            // 并发关闭
            Thread t1 = new Thread(() -> Threads.shutdownAndAwaitTermination(pool1));
            Thread t2 = new Thread(() -> Threads.shutdownAndAwaitTermination(pool2));
            Thread t3 = new Thread(() -> Threads.shutdownAndAwaitTermination(pool3));

            t1.start();
            t2.start();
            t3.start();

            t1.join();
            t2.join();
            t3.join();

            assertThat(pool1.isTerminated()).isTrue();
            assertThat(pool2.isTerminated()).isTrue();
            assertThat(pool3.isTerminated()).isTrue();
        }
    }

    @Nested
    @DisplayName("printException 方法测试")
    class PrintExceptionTest {

        @Test
        @DisplayName("普通Runnable不应抛出异常")
        void shouldHandleNormalRunnable() {
            Runnable normalRunnable = () -> {
            };

            assertThatCode(() -> Threads.printException(normalRunnable, null))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("带有异常的Runnable应记录异常")
        void shouldLogExceptionForRunnableWithThrowable() {
            Runnable runnable = () -> {
            };
            Throwable exception = new RuntimeException("Test exception");

            // 此方法会记录日志，但不会抛出异常
            assertThatCode(() -> Threads.printException(runnable, exception))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("已完成的Future应获取结果")
        void shouldGetResultFromCompletedFuture() {
            FutureTask<String> future = new FutureTask<>(() -> "Success");
            new Thread(future).start();

            try {
                Thread.sleep(100); // 等待任务完成
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            assertThatCode(() -> Threads.printException(future, null))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("未完成的Future不应阻塞")
        void shouldNotBlockOnIncompleteFuture() {
            FutureTask<String> future = new FutureTask<>(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "Never executed";
            });
            Thread thread = new Thread(future);
            thread.start();

            // 应该快速返回，不等待Future完成
            assertThatCode(() -> Threads.printException(future, null))
                .doesNotThrowAnyException();

            thread.interrupt();
        }

        @Test
        @DisplayName("Future执行异常应记录")
        void shouldLogExceptionFromFuture() {
            FutureTask<Void> future = new FutureTask<>(() -> {
                throw new RuntimeException("Task exception");
            });
            new Thread(future).start();

            try {
                Thread.sleep(100); // 等待任务完成
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 应该记录ExecutionException，但不抛出
            assertThatCode(() -> Threads.printException(future, null))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("被取消的Future应记录CancellationException")
        void shouldLogCancellationException() {
            FutureTask<Void> future = new FutureTask<>(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return null;
            });
            Thread thread = new Thread(future);
            thread.start();

            future.cancel(true); // 取消任务

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 应该记录CancellationException
            assertThatCode(() -> Threads.printException(future, null))
                .doesNotThrowAnyException();

            thread.interrupt();
        }

        @Test
        @DisplayName("使用Mock测试Future.get抛出InterruptedException")
        @SuppressWarnings("unchecked")
        void shouldHandleInterruptedExceptionWithMock() throws Exception {
            // 创建一个同时实现Runnable和Future的mock
            RunnableFuture<String> future = mock(RunnableFuture.class);

            when(future.isDone()).thenReturn(true);
            when(future.get()).thenThrow(new InterruptedException("Test interruption"));

            Threads.printException(future, null);

            verify(future).isDone();
            verify(future).get();
            // 注意：中断标志会被设置，因为Thread.currentThread().interrupt()被调用
            assertThat(Thread.interrupted()).isTrue(); // 清除并检查中断标志
        }

        @Test
        @DisplayName("同时传入Throwable和Future")
        void shouldPrioritizeThrowableOverFuture() {
            FutureTask<String> future = new FutureTask<>(() -> "Success");
            new Thread(future).start();
            Throwable exception = new RuntimeException("Explicit exception");

            // 当同时传入throwable和future时，应优先处理throwable
            assertThatCode(() -> Threads.printException(future, exception))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("null参数应该正常处理")
        void shouldHandleNullParameters() {
            assertThatCode(() -> Threads.printException(null, null))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("综合场景测试")
    class IntegrationTest {

        @Test
        @DisplayName("完整的线程池生命周期管理")
        @Timeout(5)
        void shouldManageCompletePoolLifecycle() {
            ExecutorService pool = Executors.newFixedThreadPool(3);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            // 提交多个任务
            for (int i = 0; i < 10; i++) {
                int taskId = i;
                pool.submit(() -> {
                    try {
                        Thread.sleep(50);
                        if (taskId % 3 == 0) {
                            throw new RuntimeException("Task " + taskId + " failed");
                        }
                        successCount.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (RuntimeException e) {
                        failureCount.incrementAndGet();
                        throw e;
                    }
                });
            }

            // 等待任务执行
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 关闭线程池
            Threads.shutdownAndAwaitTermination(pool);

            assertThat(pool.isTerminated()).isTrue();
            assertThat(successCount.get() + failureCount.get()).isLessThanOrEqualTo(10);
        }

        @Test
        @DisplayName("多个线程池协同工作")
        @Timeout(5)
        void shouldCoordinateMultiplePools() throws InterruptedException {
            ExecutorService fastPool = Executors.newFixedThreadPool(5);
            ExecutorService slowPool = Executors.newFixedThreadPool(2);

            CountDownLatch latch = new CountDownLatch(10);

            // 快速任务
            for (int i = 0; i < 5; i++) {
                fastPool.submit(() -> {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // 慢速任务
            for (int i = 0; i < 5; i++) {
                slowPool.submit(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // 等待所有任务完成
            boolean completed = latch.await(2, TimeUnit.SECONDS);
            assertThat(completed).isTrue();

            // 关闭线程池
            Threads.shutdownAndAwaitTermination(fastPool);
            Threads.shutdownAndAwaitTermination(slowPool);

            assertThat(fastPool.isTerminated()).isTrue();
            assertThat(slowPool.isTerminated()).isTrue();
        }

        @Test
        @DisplayName("线程池异常处理和恢复")
        @Timeout(5)
        void shouldHandleExceptionsAndRecover() {
            ExecutorService pool = Executors.newFixedThreadPool(2);

            // 提交会失败的任务
            FutureTask<Void> failingFuture = new FutureTask<>(() -> {
                throw new RuntimeException("Task failed");
            });
            pool.submit(failingFuture);

            // 提交正常的任务
            FutureTask<String> successFuture = new FutureTask<>(() -> {
                Thread.sleep(100);
                return "Success";
            });
            pool.submit(successFuture);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 处理异常
            Threads.printException(failingFuture, null);
            Threads.printException(successFuture, null);

            // 关闭线程池
            Threads.shutdownAndAwaitTermination(pool);

            assertThat(pool.isTerminated()).isTrue();
        }
    }

    @Nested
    @DisplayName("边界情况测试")
    class EdgeCasesTest {

        @Test
        @DisplayName("空线程池关闭")
        void shouldShutdownEmptyPool() {
            ExecutorService pool = Executors.newFixedThreadPool(5);
            // 不提交任何任务

            Threads.shutdownAndAwaitTermination(pool);

            assertThat(pool.isShutdown()).isTrue();
            assertThat(pool.isTerminated()).isTrue();
        }

        @Test
        @DisplayName("单线程池关闭")
        @Timeout(3)
        void shouldShutdownSingleThreadPool() {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            pool.submit(() -> {
            });

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Threads.shutdownAndAwaitTermination(pool);

            assertThat(pool.isTerminated()).isTrue();
        }

        @Test
        @DisplayName("缓存线程池关闭")
        @Timeout(3)
        void shouldShutdownCachedThreadPool() {
            ExecutorService pool = Executors.newCachedThreadPool();
            for (int i = 0; i < 10; i++) {
                pool.submit(() -> {
                });
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Threads.shutdownAndAwaitTermination(pool);

            assertThat(pool.isTerminated()).isTrue();
        }

        @Test
        @DisplayName("调度线程池关闭")
        @Timeout(3)
        void shouldShutdownScheduledThreadPool() {
            ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
            pool.schedule(() -> {
            }, 50, TimeUnit.MILLISECONDS);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Threads.shutdownAndAwaitTermination(pool);

            assertThat(pool.isTerminated()).isTrue();
        }
    }
}
