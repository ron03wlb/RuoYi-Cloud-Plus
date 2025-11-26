package org.dromara.common.core.exception.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.Locale;
import org.dromara.common.core.BaseIntegrationTest;
import org.dromara.common.core.exception.base.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * FileException 集成测试
 *
 * @author Test Team
 */
@DisplayName("FileException 集成测试")
class FileExceptionIntegrationTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTest {

        @Test
        @DisplayName("应该通过错误码和参数数组创建异常")
        void shouldCreateWithCodeAndArgsArray() {
            // Act
            FileException exception =
                    new FileException("file.upload.msg.sizeLimitExceeded", new Object[] {10});

            // Assert
            assertThat(exception.getModule()).isEqualTo("file");
            assertThat(exception.getCode()).isEqualTo("file.upload.msg.sizeLimitExceeded");
            assertThat(exception.getArgs()).containsExactly(10);
            assertThat(exception.getDefaultMessage()).isNull();
        }

        @Test
        @DisplayName("应该支持空参数数组")
        void shouldCreateWithEmptyArgsArray() {
            // Act
            FileException exception =
                    new FileException("file.upload.msg.sizeLimitExceeded", new Object[0]);

            // Assert
            assertThat(exception.getModule()).isEqualTo("file");
            assertThat(exception.getCode()).isEqualTo("file.upload.msg.sizeLimitExceeded");
            assertThat(exception.getArgs()).isEmpty();
        }

        @Test
        @DisplayName("应该支持多参数数组")
        void shouldCreateWithMultipleArgs() {
            // Act
            FileException exception =
                    new FileException(
                            "file.upload.msg.fileSizeLimitExceeded", new Object[] {5, "image.jpg"});

            // Assert
            assertThat(exception.getModule()).isEqualTo("file");
            assertThat(exception.getArgs()).containsExactly(5, "image.jpg");
        }

        @Test
        @DisplayName("应该支持 null 参数数组")
        void shouldCreateWithNullArgsArray() {
            // Act
            FileException exception = new FileException("file.upload.msg.sizeLimitExceeded", null);

            // Assert
            assertThat(exception.getModule()).isEqualTo("file");
            assertThat(exception.getArgs()).isNull();
        }
    }

    @Nested
    @DisplayName("getMessage 方法测试 - MessageUtils 集成")
    class GetMessageIntegrationTest {

        @Test
        @DisplayName("应该通过 MessageUtils 获取文件相关国际化消息")
        void shouldGetFileMessageFromMessageUtils() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException("file.upload.msg.sizeLimitExceeded", new Object[] {10});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("文件大小超出限制，最大允许10MB");
        }

        @Test
        @DisplayName("应该正确格式化文件名长度限制消息")
        void shouldFormatFileNameLengthMessage() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException(
                            "file.upload.msg.fileNameLengthLimitExceeded", new Object[] {255});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("文件名长度超出限制，最大长度255个字符");
        }

        @Test
        @DisplayName("应该正确格式化单个文件大小限制消息")
        void shouldFormatFileSizeLimitMessage() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException("file.upload.msg.fileSizeLimitExceeded", new Object[] {5});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("单个文件大小超出限制，最大允许5MB");
        }
    }

    @Nested
    @DisplayName("真实业务场景测试")
    class RealScenarioTest {

        @Test
        @DisplayName("文件大小超限场景 - 10MB")
        void shouldHandleFileSizeExceededScenario10MB() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException("file.upload.msg.sizeLimitExceeded", new Object[] {10});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(exception.getModule()).isEqualTo("file");
            assertThat(message).isEqualTo("文件大小超出限制，最大允许10MB");
        }

        @Test
        @DisplayName("文件大小超限场景 - 50MB")
        void shouldHandleFileSizeExceededScenario50MB() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException("file.upload.msg.sizeLimitExceeded", new Object[] {50});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(exception.getModule()).isEqualTo("file");
            assertThat(message).isEqualTo("文件大小超出限制，最大允许50MB");
        }

        @Test
        @DisplayName("单个文件大小超限场景")
        void shouldHandleSingleFileSizeExceededScenario() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException("file.upload.msg.fileSizeLimitExceeded", new Object[] {5});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(exception.getModule()).isEqualTo("file");
            assertThat(message).isEqualTo("单个文件大小超出限制，最大允许5MB");
        }

        @Test
        @DisplayName("文件名长度超限场景 - 100字符")
        void shouldHandleFileNameLengthExceededScenario100() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException(
                            "file.upload.msg.fileNameLengthLimitExceeded", new Object[] {100});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(exception.getModule()).isEqualTo("file");
            assertThat(message).isEqualTo("文件名长度超出限制，最大长度100个字符");
        }

        @Test
        @DisplayName("文件名长度超限场景 - 255字符")
        void shouldHandleFileNameLengthExceededScenario255() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException(
                            "file.upload.msg.fileNameLengthLimitExceeded", new Object[] {255});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(exception.getModule()).isEqualTo("file");
            assertThat(message).isEqualTo("文件名长度超出限制，最大长度255个字符");
        }

        @Test
        @DisplayName("批量上传文件错误场景")
        void shouldHandleBatchUploadScenario() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);

            // Simulate multiple file upload errors
            FileException exception1 =
                    new FileException("file.upload.msg.sizeLimitExceeded", new Object[] {10});
            FileException exception2 =
                    new FileException(
                            "file.upload.msg.fileNameLengthLimitExceeded", new Object[] {255});

            // Act
            String message1 = exception1.getMessage();
            String message2 = exception2.getMessage();

            // Assert
            assertThat(message1).isEqualTo("文件大小超出限制，最大允许10MB");
            assertThat(message2).isEqualTo("文件名长度超出限制，最大长度255个字符");
        }
    }

    @Nested
    @DisplayName("异常特性测试")
    class ExceptionFeaturesTest {

        @Test
        @DisplayName("应该是 BaseException 的子类")
        void shouldBeSubclassOfBaseException() {
            // Arrange
            FileException exception =
                    new FileException("file.upload.msg.sizeLimitExceeded", new Object[] {10});

            // Act & Assert
            assertThat(exception).isInstanceOf(BaseException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("应该可以被抛出和捕获")
        void shouldBeThrowableAndCatchable() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);

            // Act & Assert
            assertThat(
                            catchThrowable(
                                    () -> {
                                        throw new FileException(
                                                "file.upload.msg.sizeLimitExceeded",
                                                new Object[] {10});
                                    }))
                    .isInstanceOf(FileException.class)
                    .isInstanceOf(BaseException.class)
                    .hasMessage("文件大小超出限制，最大允许10MB");
        }

        @Test
        @DisplayName("应该可以作为 BaseException 捕获")
        void shouldBeCatchableAsBaseException() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            boolean caught = false;
            String message = null;

            // Act
            try {
                throw new FileException("file.upload.msg.fileSizeLimitExceeded", new Object[] {5});
            } catch (BaseException e) {
                caught = true;
                message = e.getMessage();
            }

            // Assert
            assertThat(caught).isTrue();
            assertThat(message).isEqualTo("单个文件大小超出限制，最大允许5MB");
        }

        @Test
        @DisplayName("应该可以作为 RuntimeException 捕获")
        void shouldBeCatchableAsRuntimeException() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            boolean caught = false;
            String message = null;

            // Act
            try {
                throw new FileException(
                        "file.upload.msg.fileNameLengthLimitExceeded", new Object[] {255});
            } catch (RuntimeException e) {
                caught = true;
                message = e.getMessage();
            }

            // Assert
            assertThat(caught).isTrue();
            assertThat(message).isEqualTo("文件名长度超出限制，最大长度255个字符");
        }
    }

    @Nested
    @DisplayName("边界值测试")
    class BoundaryTest {

        @Test
        @DisplayName("应该处理零值参数")
        void shouldHandleZeroValue() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException("file.upload.msg.sizeLimitExceeded", new Object[] {0});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("文件大小超出限制，最大允许0MB");
        }

        @Test
        @DisplayName("应该处理负数参数")
        void shouldHandleNegativeValue() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException("file.upload.msg.sizeLimitExceeded", new Object[] {-1});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("文件大小超出限制，最大允许-1MB");
        }

        @Test
        @DisplayName("应该处理超大数字参数")
        void shouldHandleLargeNumberValue() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException("file.upload.msg.sizeLimitExceeded", new Object[] {10000});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).contains("10,000"); // MessageFormat adds thousand separators
        }

        @Test
        @DisplayName("应该处理特殊字符参数")
        void shouldHandleSpecialCharactersInArgs() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException(
                            "file.upload.msg.fileNameLengthLimitExceeded",
                            new Object[] {"<script>alert('xss')</script>"});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).contains("<script>alert('xss')</script>");
        }

        @Test
        @DisplayName("应该处理包含 null 的参数数组")
        void shouldHandleNullInArgsArray() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException("file.upload.msg.sizeLimitExceeded", new Object[] {null});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).contains("null");
        }

        @Test
        @DisplayName("应该处理混合类型参数")
        void shouldHandleMixedTypeArgs() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            FileException exception =
                    new FileException(
                            "file.upload.msg.fileSizeLimitExceeded",
                            new Object[] {10, "test.jpg", true});

            // Act
            String message = exception.getMessage();

            // Assert
            // 只使用第一个参数
            assertThat(message).isEqualTo("单个文件大小超出限制，最大允许10MB");
        }
    }
}
