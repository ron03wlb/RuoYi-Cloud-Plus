package org.dromara.common.core.utils;

import org.dromara.common.core.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MessageUtils 集成测试
 *
 * @author Test Team
 */
@DisplayName("MessageUtils 集成测试")
class MessageUtilsIntegrationTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("message 方法测试")
    class MessageMethodTest {

        @Test
        @DisplayName("应该正确获取简单消息")
        void shouldReturnSimpleMessage() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);

            // Act
            String message = MessageUtils.message("user.not.exists");

            // Assert
            assertThat(message).isEqualTo("用户不存在");
        }

        @Test
        @DisplayName("应该正确获取带参数的消息")
        void shouldReturnMessageWithArguments() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);

            // Act
            String message = MessageUtils.message("user.password.retry.limit.count", 3);

            // Assert
            assertThat(message).isEqualTo("密码输入错误3次");
        }

        @Test
        @DisplayName("应该正确获取多参数消息")
        void shouldReturnMessageWithMultipleArguments() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);

            // Act
            String message = MessageUtils.message("user.password.retry.limit.exceed", 10);

            // Assert
            assertThat(message).isEqualTo("密码输入错误次数过多，账户锁定10分钟");
        }

        @Test
        @DisplayName("消息不存在时应返回消息键")
        void shouldReturnCodeWhenMessageNotFound() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            String nonExistentCode = "non.existent.message.code";

            // Act
            String message = MessageUtils.message(nonExistentCode);

            // Assert
            assertThat(message).isEqualTo(nonExistentCode);
        }

        @Test
        @DisplayName("应该处理 null 参数")
        void shouldHandleNullArguments() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);

            // Act
            String message = MessageUtils.message("user.password.retry.limit.count", (Object) null);

            // Assert
            assertThat(message).isEqualTo("密码输入错误null次");
        }

        @Test
        @DisplayName("应该处理空参数数组")
        void shouldHandleEmptyArgumentsArray() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);

            // Act
            String message = MessageUtils.message("user.not.exists", new Object[]{});

            // Assert
            assertThat(message).isEqualTo("用户不存在");
        }

        @Test
        @DisplayName("应该处理 null 消息键")
        void shouldHandleNullCode() {
            // Act
            String message = MessageUtils.message(null);

            // Assert - 返回 null 因为找不到消息
            assertThat(message).isNull();
        }

        @Test
        @DisplayName("应该处理空消息键")
        void shouldHandleEmptyCode() {
            // Act
            String message = MessageUtils.message("");

            // Assert - 返回空字符串
            assertThat(message).isEmpty();
        }
    }

    @Nested
    @DisplayName("多语言支持测试")
    class MultiLanguageTest {

        @Test
        @DisplayName("应该支持中文语言环境")
        void shouldSupportChineseLocale() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);

            // Act
            String message = MessageUtils.message("common.success");

            // Assert
            assertThat(message).isEqualTo("操作成功");
        }

        @Test
        @DisplayName("应该支持英文语言环境（回退到默认）")
        void shouldFallbackToDefaultForEnglishLocale() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.ENGLISH);

            // Act
            String message = MessageUtils.message("common.success");

            // Assert - 由于没有英文消息文件，返回默认消息
            assertThat(message).isEqualTo("操作成功");
        }
    }

    @Nested
    @DisplayName("真实业务场景测试")
    class RealScenarioTest {

        @Test
        @DisplayName("用户登录错误消息场景")
        void shouldHandleUserLoginErrorScenario() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);

            // Act & Assert
            assertThat(MessageUtils.message("user.password.not.match"))
                .isEqualTo("用户名或密码错误");

            assertThat(MessageUtils.message("user.jcaptcha.error"))
                .isEqualTo("验证码错误");

            assertThat(MessageUtils.message("user.jcaptcha.expire"))
                .isEqualTo("验证码已失效");
        }

        @Test
        @DisplayName("文件上传错误消息场景")
        void shouldHandleFileUploadErrorScenario() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);

            // Act & Assert
            assertThat(MessageUtils.message("file.upload.msg.sizeLimitExceeded", 10))
                .isEqualTo("文件大小超出限制，最大允许10MB");

            assertThat(MessageUtils.message("file.upload.msg.fileNameLengthLimitExceeded", 255))
                .isEqualTo("文件名长度超出限制，最大长度255个字符");

            assertThat(MessageUtils.message("file.upload.msg.fileSizeLimitExceeded", 5))
                .isEqualTo("单个文件大小超出限制，最大允许5MB");
        }

        @Test
        @DisplayName("密码重试限制场景")
        void shouldHandlePasswordRetryLimitScenario() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);

            // Act & Assert - 第1次错误
            assertThat(MessageUtils.message("user.password.retry.limit.count", 1))
                .isEqualTo("密码输入错误1次");

            // 第3次错误
            assertThat(MessageUtils.message("user.password.retry.limit.count", 3))
                .isEqualTo("密码输入错误3次");

            // 超过限制
            assertThat(MessageUtils.message("user.password.retry.limit.exceed", 30))
                .isEqualTo("密码输入错误次数过多，账户锁定30分钟");
        }
    }

    @Nested
    @DisplayName("边界值测试")
    class BoundaryTest {

        @Test
        @DisplayName("应该处理超长参数")
        void shouldHandleLongArguments() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            String longString = "A".repeat(10000);

            // Act
            String message = MessageUtils.message("user.password.retry.limit.count", longString);

            // Assert
            assertThat(message).startsWith("密码输入错误");
            assertThat(message).contains(longString);
        }

        @Test
        @DisplayName("应该处理特殊字符参数")
        void shouldHandleSpecialCharacterArguments() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);

            // Act
            String message = MessageUtils.message("user.password.retry.limit.count", "<script>alert('xss')</script>");

            // Assert
            assertThat(message).isEqualTo("密码输入错误<script>alert('xss')</script>次");
        }

        @Test
        @DisplayName("应该处理数字参数")
        void shouldHandleNumericArguments() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);

            // Act & Assert
            assertThat(MessageUtils.message("file.upload.msg.sizeLimitExceeded", 0))
                .isEqualTo("文件大小超出限制，最大允许0MB");

            assertThat(MessageUtils.message("file.upload.msg.sizeLimitExceeded", -1))
                .isEqualTo("文件大小超出限制，最大允许-1MB");

            // MessageFormat 会自动对大数字添加千分位分隔符
            assertThat(MessageUtils.message("file.upload.msg.sizeLimitExceeded", 9999))
                .isEqualTo("文件大小超出限制，最大允许9,999MB");
        }
    }
}
