package org.dromara.common.satoken.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.hutool.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * SaTokenExceptionHandler 测试
 *
 * <p>测试 Sa-Token 异常处理器
 *
 * @author Test Team
 */
@DisplayName("SaTokenExceptionHandler 测试")
class SaTokenExceptionHandlerTest extends BaseUnitTest {

    @InjectMocks private SaTokenExceptionHandler handler;

    @Mock private HttpServletRequest request;

    private static final String TEST_URI = "/api/test";

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(TEST_URI);
    }

    @Nested
    @DisplayName("1. handleNotPermissionException() 权限异常处理测试")
    class HandleNotPermissionExceptionTests {

        @Test
        @DisplayName("应该返回403状态码和权限不足消息")
        void shouldReturn403ForPermissionDenied() {
            // Arrange
            NotPermissionException exception = new NotPermissionException("system:user:add");

            // Act
            R<Void> result = handler.handleNotPermissionException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_FORBIDDEN);
            assertThat(result.getMsg()).contains("没有访问权限");
        }

        @Test
        @DisplayName("应该处理空权限码异常")
        void shouldHandleEmptyPermissionException() {
            // Arrange
            NotPermissionException exception = new NotPermissionException("");

            // Act
            R<Void> result = handler.handleNotPermissionException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_FORBIDDEN);
        }
    }

    @Nested
    @DisplayName("2. handleNotRoleException() 角色异常处理测试")
    class HandleNotRoleExceptionTests {

        @Test
        @DisplayName("应该返回403状态码和角色权限不足消息")
        void shouldReturn403ForRoleDenied() {
            // Arrange
            NotRoleException exception = new NotRoleException("admin");

            // Act
            R<Void> result = handler.handleNotRoleException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_FORBIDDEN);
            assertThat(result.getMsg()).contains("没有访问权限");
        }

        @Test
        @DisplayName("应该处理空角色异常")
        void shouldHandleEmptyRoleException() {
            // Arrange
            NotRoleException exception = new NotRoleException("");

            // Act
            R<Void> result = handler.handleNotRoleException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_FORBIDDEN);
        }
    }

    @Nested
    @DisplayName("3. handleNotLoginException() 认证异常处理测试")
    class HandleNotLoginExceptionTests {

        @Test
        @DisplayName("应该返回401状态码和认证失败消息")
        void shouldReturn401ForNotLogin() {
            // Arrange
            NotLoginException exception =
                    NotLoginException.newInstance(
                            "user", NotLoginException.NOT_TOKEN, "未提供Token", null);

            // Act
            R<Void> result = handler.handleNotLoginException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAUTHORIZED);
            assertThat(result.getMsg()).contains("认证失败");
        }

        @Test
        @DisplayName("应该处理Token过期异常")
        void shouldHandleTokenExpiredException() {
            // Arrange
            NotLoginException exception =
                    NotLoginException.newInstance(
                            "user", NotLoginException.TOKEN_TIMEOUT, "Token已过期", null);

            // Act
            R<Void> result = handler.handleNotLoginException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAUTHORIZED);
            assertThat(result.getMsg()).contains("认证失败");
        }

        @Test
        @DisplayName("应该处理Token被顶下线异常")
        void shouldHandleTokenBeReplacedException() {
            // Arrange
            NotLoginException exception =
                    NotLoginException.newInstance(
                            "user", NotLoginException.BE_REPLACED, "Token已被顶下线", null);

            // Act
            R<Void> result = handler.handleNotLoginException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    @Nested
    @DisplayName("4. 边界条件测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("应该处理null异常消息")
        void shouldHandleNullExceptionMessage() {
            // Arrange
            NotPermissionException exception = new NotPermissionException(null);

            // Act
            R<Void> result = handler.handleNotPermissionException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_FORBIDDEN);
        }

        @Test
        @DisplayName("应该处理特殊字符的URI")
        void shouldHandleSpecialCharactersInURI() {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/test?id=123&name=测试");
            NotLoginException exception =
                    NotLoginException.newInstance(
                            "user", NotLoginException.NOT_TOKEN, "未提供Token", null);

            // Act
            R<Void> result = handler.handleNotLoginException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_UNAUTHORIZED);
        }
    }
}
