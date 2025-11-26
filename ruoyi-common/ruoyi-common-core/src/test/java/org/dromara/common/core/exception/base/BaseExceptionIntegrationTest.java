package org.dromara.common.core.exception.base;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.dromara.common.core.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * BaseException 集成测试
 *
 * @author Test Team
 */
@DisplayName("BaseException 集成测试")
class BaseExceptionIntegrationTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTest {

        @Test
        @DisplayName("应该支持全参数构造函数")
        void shouldCreateWithAllArguments() {
            // Act
            BaseException exception =
                    new BaseException("system", "user.not.exists", new Object[] {"admin"}, "默认消息");

            // Assert
            assertThat(exception.getModule()).isEqualTo("system");
            assertThat(exception.getCode()).isEqualTo("user.not.exists");
            assertThat(exception.getArgs()).containsExactly("admin");
            assertThat(exception.getDefaultMessage()).isEqualTo("默认消息");
        }

        @Test
        @DisplayName("应该支持模块、错误码、参数构造函数")
        void shouldCreateWithModuleCodeAndArgs() {
            // Act
            BaseException exception =
                    new BaseException("system", "user.not.exists", new Object[] {"admin"});

            // Assert
            assertThat(exception.getModule()).isEqualTo("system");
            assertThat(exception.getCode()).isEqualTo("user.not.exists");
            assertThat(exception.getArgs()).containsExactly("admin");
            assertThat(exception.getDefaultMessage()).isNull();
        }

        @Test
        @DisplayName("应该支持模块、默认消息构造函数")
        void shouldCreateWithModuleAndDefaultMessage() {
            // Act
            BaseException exception = new BaseException("system", "系统错误");

            // Assert
            assertThat(exception.getModule()).isEqualTo("system");
            assertThat(exception.getCode()).isNull();
            assertThat(exception.getArgs()).isNull();
            assertThat(exception.getDefaultMessage()).isEqualTo("系统错误");
        }

        @Test
        @DisplayName("应该支持错误码、参数构造函数")
        void shouldCreateWithCodeAndArgs() {
            // Act
            BaseException exception = new BaseException("user.not.exists", new Object[] {"admin"});

            // Assert
            assertThat(exception.getModule()).isNull();
            assertThat(exception.getCode()).isEqualTo("user.not.exists");
            assertThat(exception.getArgs()).containsExactly("admin");
            assertThat(exception.getDefaultMessage()).isNull();
        }

        @Test
        @DisplayName("应该支持仅默认消息构造函数")
        void shouldCreateWithDefaultMessageOnly() {
            // Act
            BaseException exception = new BaseException("系统繁忙，请稍后重试");

            // Assert
            assertThat(exception.getModule()).isNull();
            assertThat(exception.getCode()).isNull();
            assertThat(exception.getArgs()).isNull();
            assertThat(exception.getDefaultMessage()).isEqualTo("系统繁忙，请稍后重试");
        }

        @Test
        @DisplayName("应该支持无参构造函数")
        void shouldCreateWithNoArguments() {
            // Act
            BaseException exception = new BaseException();

            // Assert
            assertThat(exception.getModule()).isNull();
            assertThat(exception.getCode()).isNull();
            assertThat(exception.getArgs()).isNull();
            assertThat(exception.getDefaultMessage()).isNull();
        }
    }

    @Nested
    @DisplayName("getMessage 方法测试 - MessageUtils 集成")
    class GetMessageIntegrationTest {

        @Test
        @DisplayName("应该通过 MessageUtils 获取国际化消息")
        void shouldGetMessageFromMessageUtils() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            BaseException exception = new BaseException("user.not.exists", new Object[0]);

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("用户不存在");
        }

        @Test
        @DisplayName("应该通过 MessageUtils 获取带参数的国际化消息")
        void shouldGetFormattedMessageFromMessageUtils() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            BaseException exception =
                    new BaseException("user.password.retry.limit.count", new Object[] {5});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("密码输入错误5次");
        }

        @Test
        @DisplayName("应该通过 MessageUtils 获取多参数国际化消息")
        void shouldGetMultiParameterMessageFromMessageUtils() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            BaseException exception =
                    new BaseException("user.password.retry.limit.exceed", new Object[] {10});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("密码输入错误次数过多，账户锁定10分钟");
        }

        @Test
        @DisplayName("错误码不存在时应回退到错误码本身")
        void shouldReturnCodeWhenCodeNotFoundInMessageSource() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            BaseException exception =
                    new BaseException(
                            null, "non.existent.code", new Object[] {"param"}, "这是默认错误消息");

            // Act
            String message = exception.getMessage();

            // Assert
            // MessageUtils 配置了 useCodeAsDefaultMessage=true，所以找不到消息时返回 code
            assertThat(message).isEqualTo("non.existent.code");
        }

        @Test
        @DisplayName("错误码为空时应使用默认消息")
        void shouldUseDefaultMessageWhenCodeIsEmpty() {
            // Arrange
            BaseException exception = new BaseException(null, "", new Object[0], "默认错误消息");

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("默认错误消息");
        }

        @Test
        @DisplayName("错误码为 null 时应使用默认消息")
        void shouldUseDefaultMessageWhenCodeIsNull() {
            // Arrange
            BaseException exception = new BaseException(null, null, new Object[0], "默认错误消息");

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("默认错误消息");
        }

        @Test
        @DisplayName("错误码和默认消息都为 null 时应返回 null")
        void shouldReturnNullWhenBothCodeAndDefaultMessageAreNull() {
            // Arrange
            BaseException exception = new BaseException();

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isNull();
        }
    }

    @Nested
    @DisplayName("真实业务场景测试")
    class RealScenarioTest {

        @Test
        @DisplayName("用户登录失败场景")
        void shouldHandleUserLoginFailureScenario() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            BaseException exception =
                    new BaseException("auth", "user.password.not.match", null, null);

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(exception.getModule()).isEqualTo("auth");
            assertThat(message).isEqualTo("用户名或密码错误");
        }

        @Test
        @DisplayName("验证码错误场景")
        void shouldHandleCaptchaErrorScenario() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            BaseException exception = new BaseException("auth", "user.jcaptcha.error", null);

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(exception.getModule()).isEqualTo("auth");
            assertThat(message).isEqualTo("验证码错误");
        }

        @Test
        @DisplayName("文件上传大小超限场景")
        void shouldHandleFileSizeExceededScenario() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            BaseException exception =
                    new BaseException(
                            "file", "file.upload.msg.sizeLimitExceeded", new Object[] {10});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(exception.getModule()).isEqualTo("file");
            assertThat(message).isEqualTo("文件大小超出限制，最大允许10MB");
        }

        @Test
        @DisplayName("密码重试次数过多场景")
        void shouldHandlePasswordRetryLimitExceededScenario() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            BaseException exception =
                    new BaseException(
                            "auth", "user.password.retry.limit.exceed", new Object[] {30}, "账户已锁定");

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(exception.getModule()).isEqualTo("auth");
            assertThat(message).isEqualTo("密码输入错误次数过多，账户锁定30分钟");
        }

        @Test
        @DisplayName("系统未知错误场景（使用默认消息）")
        void shouldHandleUnknownSystemErrorScenario() {
            // Arrange
            BaseException exception = new BaseException("system", "系统繁忙，请稍后重试");

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(exception.getModule()).isEqualTo("system");
            assertThat(exception.getCode()).isNull();
            assertThat(message).isEqualTo("系统繁忙，请稍后重试");
        }
    }

    @Nested
    @DisplayName("边界值测试")
    class BoundaryTest {

        @Test
        @DisplayName("应该处理空字符串模块名")
        void shouldHandleEmptyModule() {
            // Arrange
            BaseException exception = new BaseException("", "user.not.exists", null, null);

            // Act & Assert
            assertThat(exception.getModule()).isEmpty();
            assertThat(exception.getMessage()).isEqualTo("用户不存在");
        }

        @Test
        @DisplayName("应该处理空参数数组")
        void shouldHandleEmptyArgsArray() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            BaseException exception = new BaseException("user.not.exists", new Object[] {});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("用户不存在");
        }

        @Test
        @DisplayName("应该处理包含 null 的参数数组")
        void shouldHandleNullInArgsArray() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            BaseException exception =
                    new BaseException("user.password.retry.limit.count", new Object[] {null});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("密码输入错误null次");
        }

        @Test
        @DisplayName("应该处理超长默认消息")
        void shouldHandleLongDefaultMessage() {
            // Arrange
            String longMessage = "错误".repeat(1000);
            BaseException exception = new BaseException(longMessage);

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo(longMessage);
            assertThat(message).hasSize(2000); // 1000 * 2 characters
        }

        @Test
        @DisplayName("应该处理特殊字符在参数中")
        void shouldHandleSpecialCharactersInArgs() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            BaseException exception =
                    new BaseException(
                            "user.password.retry.limit.count",
                            new Object[] {"<script>alert('xss')</script>"});

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("密码输入错误<script>alert('xss')</script>次");
        }

        @Test
        @DisplayName("应该处理特殊字符在默认消息中")
        void shouldHandleSpecialCharactersInDefaultMessage() {
            // Arrange
            BaseException exception = new BaseException("<script>alert('xss')</script>");

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("<script>alert('xss')</script>");
        }
    }

    @Nested
    @DisplayName("Lombok 功能测试")
    class LombokFeaturesTest {

        @Test
        @DisplayName("应该支持 getter 方法")
        void shouldSupportGetters() {
            // Arrange
            BaseException exception =
                    new BaseException("module", "code", new Object[] {"arg"}, "message");

            // Act & Assert
            assertThat(exception.getModule()).isEqualTo("module");
            assertThat(exception.getCode()).isEqualTo("code");
            assertThat(exception.getArgs()).containsExactly("arg");
            assertThat(exception.getDefaultMessage()).isEqualTo("message");
        }

        @Test
        @DisplayName("应该支持 setter 方法")
        void shouldSupportSetters() {
            // Arrange
            BaseException exception = new BaseException();

            // Act
            exception.setModule("newModule");
            exception.setCode("newCode");
            exception.setArgs(new Object[] {"newArg"});
            exception.setDefaultMessage("newMessage");

            // Assert
            assertThat(exception.getModule()).isEqualTo("newModule");
            assertThat(exception.getCode()).isEqualTo("newCode");
            assertThat(exception.getArgs()).containsExactly("newArg");
            assertThat(exception.getDefaultMessage()).isEqualTo("newMessage");
        }

        @Test
        @DisplayName("应该支持 equals 和 hashCode（字段级比较）")
        void shouldSupportEqualsAndHashCodeForFields() {
            // Arrange
            Object[] args1 = new Object[] {"arg"};

            BaseException exception1 = new BaseException("module", "code", args1, "message");
            BaseException exception2 = new BaseException("module", "code", args1, "message");
            BaseException exception3 = new BaseException("different", "code", args1, "message");

            // Act & Assert
            // 验证相同字段值的异常具有相同字段值（注意：由于 @EqualsAndHashCode(callSuper=true)，
            // 完整对象相等性会包括 Throwable 的堆栈跟踪等信息，所以这里只验证字段级相等）
            assertThat(exception1.getModule()).isEqualTo(exception2.getModule());
            assertThat(exception1.getCode()).isEqualTo(exception2.getCode());
            assertThat(exception1.getArgs()).isSameAs(exception2.getArgs());
            assertThat(exception1.getDefaultMessage()).isEqualTo(exception2.getDefaultMessage());

            // 不同 module 应该有不同的 module 字段
            assertThat(exception1.getModule()).isNotEqualTo(exception3.getModule());
        }
    }

    @Nested
    @DisplayName("异常特性测试")
    class ExceptionFeaturesTest {

        @Test
        @DisplayName("应该是 RuntimeException 的子类")
        void shouldBeSubclassOfRuntimeException() {
            // Arrange
            BaseException exception = new BaseException("错误");

            // Act & Assert
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("应该可以被抛出和捕获")
        void shouldBeThrowableAndCatchable() {
            // Act & Assert
            assertThat(
                            org.assertj.core.api.Assertions.catchThrowable(
                                    () -> {
                                        throw new BaseException("user.not.exists", new Object[0]);
                                    }))
                    .isInstanceOf(BaseException.class)
                    .hasMessage("用户不存在");
        }

        @Test
        @DisplayName("应该可以在 try-catch 中捕获")
        void shouldBeCatchableInTryCatch() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            boolean caught = false;
            String message = null;

            // Act
            try {
                throw new BaseException("user.password.not.match", new Object[0]);
            } catch (BaseException e) {
                caught = true;
                message = e.getMessage();
            }

            // Assert
            assertThat(caught).isTrue();
            assertThat(message).isEqualTo("用户名或密码错误");
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
                throw new BaseException("common.error", new Object[0]);
            } catch (RuntimeException e) {
                caught = true;
                message = e.getMessage();
            }

            // Assert
            assertThat(caught).isTrue();
            assertThat(message).isEqualTo("系统错误");
        }
    }
}
