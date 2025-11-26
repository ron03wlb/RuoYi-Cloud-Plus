package org.dromara.common.tenant.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.dromara.common.tenant.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * TenantException 测试
 *
 * <p>测试租户异常类的创建和属性
 *
 * @author Test Team
 */
@DisplayName("TenantException 测试")
class TenantExceptionTest extends BaseUnitTest {

    @Nested
    @DisplayName("1. 异常创建测试")
    class ExceptionCreationTests {

        @Test
        @DisplayName("应该创建带错误码的异常")
        void shouldCreateExceptionWithCode() {
            // Arrange
            String code = "tenant.not.found";

            // Act
            TenantException exception = new TenantException(code);

            // Assert
            assertThat(exception).isNotNull();
            assertThat(exception.getModule()).isEqualTo("tenant");
            assertThat(exception.getCode()).isEqualTo(code);
        }

        @Test
        @DisplayName("应该创建带错误码和参数的异常")
        void shouldCreateExceptionWithCodeAndArgs() {
            // Arrange
            String code = "tenant.operation.failed";
            Object[] args = {"TENANT-001", "删除操作"};

            // Act
            TenantException exception = new TenantException(code, args);

            // Assert
            assertThat(exception).isNotNull();
            assertThat(exception.getModule()).isEqualTo("tenant");
            assertThat(exception.getCode()).isEqualTo(code);
            assertThat(exception.getArgs()).containsExactly(args);
        }

        @Test
        @DisplayName("应该创建带空参数数组的异常")
        void shouldCreateExceptionWithEmptyArgs() {
            // Arrange
            String code = "tenant.invalid";
            Object[] args = {};

            // Act
            TenantException exception = new TenantException(code, args);

            // Assert
            assertThat(exception).isNotNull();
            assertThat(exception.getArgs()).isEmpty();
        }

        @Test
        @DisplayName("应该创建带null参数的异常")
        void shouldCreateExceptionWithNullArgs() {
            // Arrange
            String code = "tenant.error";
            Object[] args = {null, null};

            // Act
            TenantException exception = new TenantException(code, args);

            // Assert
            assertThat(exception).isNotNull();
            assertThat(exception.getArgs()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("2. 业务场景测试")
    class BusinessScenarioTests {

        @Test
        @DisplayName("应该创建租户不存在异常")
        void shouldCreateTenantNotFoundException() {
            // Arrange
            String tenantId = "TENANT-999";

            // Act
            TenantException exception = new TenantException("tenant.not.found", tenantId);

            // Assert
            assertThat(exception.getModule()).isEqualTo("tenant");
            assertThat(exception.getCode()).isEqualTo("tenant.not.found");
            assertThat(exception.getArgs()).containsExactly(tenantId);
        }

        @Test
        @DisplayName("应该创建租户已过期异常")
        void shouldCreateTenantExpiredException() {
            // Arrange
            String tenantId = "TENANT-001";
            String expireDate = "2024-12-31";

            // Act
            TenantException exception = new TenantException("tenant.expired", tenantId, expireDate);

            // Assert
            assertThat(exception.getCode()).isEqualTo("tenant.expired");
            assertThat(exception.getArgs()).containsExactly(tenantId, expireDate);
        }

        @Test
        @DisplayName("应该创建租户权限不足异常")
        void shouldCreateTenantPermissionDeniedException() {
            // Arrange
            String operation = "删除用户";

            // Act
            TenantException exception = new TenantException("tenant.permission.denied", operation);

            // Assert
            assertThat(exception.getCode()).isEqualTo("tenant.permission.denied");
        }

        @Test
        @DisplayName("应该创建租户配额超限异常")
        void shouldCreateTenantQuotaExceededException() {
            // Arrange
            String resource = "用户数";
            Integer limit = 100;
            Integer current = 101;

            // Act
            TenantException exception =
                    new TenantException("tenant.quota.exceeded", resource, limit, current);

            // Assert
            assertThat(exception.getCode()).isEqualTo("tenant.quota.exceeded");
            assertThat(exception.getArgs()).containsExactly(resource, limit, current);
        }
    }

    @Nested
    @DisplayName("3. 边界条件测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("应该处理空字符串错误码")
        void shouldHandleEmptyCode() {
            // Act
            TenantException exception = new TenantException("");

            // Assert
            assertThat(exception.getCode()).isEmpty();
        }

        @Test
        @DisplayName("应该处理多个参数")
        void shouldHandleMultipleArgs() {
            // Arrange
            Object[] args = {"arg1", 2, true, null, "arg5"};

            // Act
            TenantException exception = new TenantException("tenant.test", args);

            // Assert
            assertThat(exception.getArgs()).hasSize(5);
            assertThat(exception.getArgs()).containsExactly(args);
        }

        @Test
        @DisplayName("应该处理特殊字符的错误码")
        void shouldHandleSpecialCharactersInCode() {
            // Arrange
            String code = "tenant.error.中文.特殊@字符#测试";

            // Act
            TenantException exception = new TenantException(code);

            // Assert
            assertThat(exception.getCode()).isEqualTo(code);
        }
    }

    @Nested
    @DisplayName("4. 继承属性测试")
    class InheritanceTests {

        @Test
        @DisplayName("应该正确设置模块名为tenant")
        void shouldSetModuleNameAsTenant() {
            // Act
            TenantException exception = new TenantException("test.code");

            // Assert
            assertThat(exception.getModule()).isEqualTo("tenant");
        }

        @Test
        @DisplayName("应该继承BaseException的属性")
        void shouldInheritFromBaseException() {
            // Act
            TenantException exception = new TenantException("test.code", "arg1");

            // Assert
            assertThat(exception).isInstanceOf(RuntimeException.class);
            assertThat(exception.getModule()).isNotNull();
            assertThat(exception.getCode()).isNotNull();
        }
    }
}
