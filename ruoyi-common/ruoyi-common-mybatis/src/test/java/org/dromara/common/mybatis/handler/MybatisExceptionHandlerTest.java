package org.dromara.common.mybatis.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import cn.hutool.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DuplicateKeyException;

/**
 * MybatisExceptionHandler 测试
 *
 * <p>测试 MyBatis 异常处理器
 *
 * @author Test Team
 */
@DisplayName("MybatisExceptionHandler 测试")
class MybatisExceptionHandlerTest extends BaseUnitTest {

    @InjectMocks private MybatisExceptionHandler handler;

    @Mock private HttpServletRequest request;

    private static final String TEST_URI = "/api/user/add";

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(TEST_URI);
    }

    @Nested
    @DisplayName("1. handleDuplicateKeyException() 重复键异常处理测试")
    class HandleDuplicateKeyExceptionTests {

        @Test
        @DisplayName("应该返回409状态码和数据重复提示消息")
        void shouldReturn409ForDuplicateKey() {
            // Arrange
            DuplicateKeyException exception =
                    new DuplicateKeyException("Duplicate entry 'admin' for key 'uk_username'");

            // Act
            R<Void> result = handler.handleDuplicateKeyException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_CONFLICT);
            assertThat(result.getMsg()).contains("数据库中已存在该记录");
        }

        @Test
        @DisplayName("应该处理主键重复异常")
        void shouldHandlePrimaryKeyDuplicate() {
            // Arrange
            DuplicateKeyException exception =
                    new DuplicateKeyException("Duplicate entry '1' for key 'PRIMARY'");

            // Act
            R<Void> result = handler.handleDuplicateKeyException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_CONFLICT);
            assertThat(result.getMsg()).isNotEmpty();
        }

        @Test
        @DisplayName("应该处理唯一索引重复异常")
        void shouldHandleUniqueIndexDuplicate() {
            // Arrange
            DuplicateKeyException exception =
                    new DuplicateKeyException(
                            "Duplicate entry 'test@example.com' for key 'uk_email'");

            // Act
            R<Void> result = handler.handleDuplicateKeyException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_CONFLICT);
        }

        @Test
        @DisplayName("应该处理空消息的重复键异常")
        void shouldHandleEmptyMessageDuplicateKey() {
            // Arrange
            DuplicateKeyException exception = new DuplicateKeyException("");

            // Act
            R<Void> result = handler.handleDuplicateKeyException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_CONFLICT);
        }
    }

    @Nested
    @DisplayName("2. handleCannotFindDataSourceException() MyBatis系统异常处理测试")
    class HandleMyBatisSystemExceptionTests {

        @Test
        @DisplayName("应该处理未找到数据源异常并返回500状态码")
        void shouldHandleCannotFindDataSourceException() {
            // Arrange
            MyBatisSystemException exception =
                    new MyBatisSystemException(
                            new RuntimeException(
                                    "CannotFindDataSourceException: No datasource set"));

            // Act
            R<Void> result = handler.handleCannotFindDataSourceException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_INTERNAL_ERROR);
            assertThat(result.getMsg()).contains("未找到数据源");
        }

        @Test
        @DisplayName("应该处理包含CannotFindDataSourceException关键字的异常")
        void shouldDetectCannotFindDataSourceKeyword() {
            // Arrange
            MyBatisSystemException exception =
                    new MyBatisSystemException(
                            new RuntimeException(
                                    "Error getting datasource: CannotFindDataSourceException"
                                            + " occurred"));

            // Act
            R<Void> result = handler.handleCannotFindDataSourceException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_INTERNAL_ERROR);
            assertThat(result.getMsg()).contains("未找到数据源");
        }

        @Test
        @DisplayName("应该处理一般MyBatis系统异常")
        void shouldHandleGeneralMyBatisSystemException() {
            // Arrange
            String errorMessage = "Error querying database. Cause: java.sql.SQLException";
            MyBatisSystemException exception =
                    new MyBatisSystemException(new RuntimeException(errorMessage));

            // Act
            R<Void> result = handler.handleCannotFindDataSourceException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_INTERNAL_ERROR);
            assertThat(result.getMsg()).contains(errorMessage);
        }

        @Test
        @DisplayName("应该处理SQL执行异常")
        void shouldHandleSQLExecutionException() {
            // Arrange
            MyBatisSystemException exception =
                    new MyBatisSystemException(
                            new RuntimeException(
                                    "PreparedStatementCallback; SQL [SELECT * FROM user]; error"));

            // Act
            R<Void> result = handler.handleCannotFindDataSourceException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_INTERNAL_ERROR);
        }

        @Test
        @DisplayName("应该处理空消息的MyBatis异常")
        void shouldHandleEmptyMessageMyBatisException() {
            // Arrange
            MyBatisSystemException exception = new MyBatisSystemException(new RuntimeException(""));

            // Act
            R<Void> result = handler.handleCannotFindDataSourceException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_INTERNAL_ERROR);
        }
    }

    @Nested
    @DisplayName("3. 边界条件测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("应该处理特殊字符的URI")
        void shouldHandleSpecialCharactersInURI() {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/用户/添加?id=123&name=测试");
            DuplicateKeyException exception = new DuplicateKeyException("Duplicate key");

            // Act
            R<Void> result = handler.handleDuplicateKeyException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_CONFLICT);
        }

        @Test
        @DisplayName("应该处理长URI路径")
        void shouldHandleLongURI() {
            // Arrange
            when(request.getRequestURI())
                    .thenReturn("/api/very/long/path/to/resource/with/many/segments/user/add");
            DuplicateKeyException exception = new DuplicateKeyException("Duplicate key");

            // Act
            R<Void> result = handler.handleDuplicateKeyException(exception, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(HttpStatus.HTTP_CONFLICT);
        }
    }
}
