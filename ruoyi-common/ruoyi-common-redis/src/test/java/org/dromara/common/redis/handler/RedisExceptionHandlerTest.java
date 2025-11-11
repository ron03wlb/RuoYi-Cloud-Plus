package org.dromara.common.redis.handler;

import cn.hutool.http.HttpStatus;
import com.baomidou.lock.exception.LockFailureException;
import jakarta.servlet.http.HttpServletRequest;
import org.dromara.common.core.domain.R;
import org.dromara.common.redis.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * RedisExceptionHandler 测试
 * <p>
 * 测试 Redis 异常处理器
 * </p>
 *
 * @author Test Team
 */
@DisplayName("RedisExceptionHandler 测试")
class RedisExceptionHandlerTest extends BaseUnitTest {

    @InjectMocks
    private RedisExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    private static final String TEST_URI = "/api/order/create";

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(TEST_URI);
    }

    @Nested
    @DisplayName("1. handleLockFailureException() 分布式锁异常处理测试")
    class HandleLockFailureExceptionTests {

        @Test
        @DisplayName("应该返回503状态码和友好提示消息")
        void shouldReturn503ForLockFailure() {
            // Arrange
            LockFailureException exception = new LockFailureException("Lock acquisition failed");

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
            assertThat(result.getMsg()).contains("业务处理中");
            assertThat(result.getMsg()).contains("请稍后再试");
        }

        @Test
        @DisplayName("应该处理锁超时异常")
        void shouldHandleLockTimeoutException() {
            // Arrange
            LockFailureException exception = new LockFailureException("Lock timeout after 5000ms");

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
        }

        @Test
        @DisplayName("应该处理锁已被占用异常")
        void shouldHandleLockAlreadyHeldException() {
            // Arrange
            LockFailureException exception = new LockFailureException(
                "Lock 'order:create:123' is already held by another thread"
            );

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
        }

        @Test
        @DisplayName("应该处理空消息的锁异常")
        void shouldHandleEmptyMessageLockException() {
            // Arrange
            LockFailureException exception = new LockFailureException("");

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
        }

        @Test
        @DisplayName("应该处理null消息的锁异常")
        void shouldHandleNullMessageLockException() {
            // Arrange
            LockFailureException exception = new LockFailureException(null);

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
        }
    }

    @Nested
    @DisplayName("2. 边界条件测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("应该处理特殊字符的URI")
        void shouldHandleSpecialCharactersInURI() {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/订单/创建?id=123&name=测试");
            LockFailureException exception = new LockFailureException("Lock failed");

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
        }

        @Test
        @DisplayName("应该处理长URI路径")
        void shouldHandleLongURI() {
            // Arrange
            when(request.getRequestURI()).thenReturn(
                "/api/very/long/path/to/resource/with/many/segments/order/create"
            );
            LockFailureException exception = new LockFailureException("Lock failed");

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
        }
    }

    @Nested
    @DisplayName("3. 业务场景测试")
    class BusinessScenarioTests {

        @Test
        @DisplayName("应该处理订单创建锁失败场景")
        void shouldHandleOrderCreationLockFailure() {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/order/create");
            LockFailureException exception = new LockFailureException(
                "Failed to acquire lock for order creation"
            );

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
            assertThat(result.getMsg()).contains("业务处理中，请稍后再试");
        }

        @Test
        @DisplayName("应该处理支付锁失败场景")
        void shouldHandlePaymentLockFailure() {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/payment/process");
            LockFailureException exception = new LockFailureException(
                "Payment processing lock is busy"
            );

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
        }

        @Test
        @DisplayName("应该处理库存扣减锁失败场景")
        void shouldHandleInventoryDeductionLockFailure() {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/inventory/deduct");
            LockFailureException exception = new LockFailureException(
                "Inventory lock contention detected"
            );

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
        }

        @Test
        @DisplayName("应该处理用户操作频繁的锁失败场景")
        void shouldHandleFrequentUserOperationLockFailure() {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/user/update");
            LockFailureException exception = new LockFailureException(
                "User operation too frequent, lock unavailable"
            );

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
            // 验证返回的消息对用户友好，没有暴露技术细节
            assertThat(result.getMsg()).doesNotContain("lock");
            assertThat(result.getMsg()).doesNotContain("Lock4j");
        }
    }

    @Nested
    @DisplayName("4. 不同锁类型测试")
    class DifferentLockTypeTests {

        @Test
        @DisplayName("应该处理分布式锁失败")
        void shouldHandleDistributedLockFailure() {
            // Arrange
            LockFailureException exception = new LockFailureException(
                "Distributed lock failed: redis-lock:order:123"
            );

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
        }

        @Test
        @DisplayName("应该处理可重入锁失败")
        void shouldHandleReentrantLockFailure() {
            // Arrange
            LockFailureException exception = new LockFailureException(
                "Reentrant lock acquisition failed"
            );

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
        }

        @Test
        @DisplayName("应该处理读写锁失败")
        void shouldHandleReadWriteLockFailure() {
            // Arrange
            LockFailureException exception = new LockFailureException(
                "Write lock acquisition timeout"
            );

            // Act
            R<Void> result = handler.handleLockFailureException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAVAILABLE);
        }
    }
}
