package org.dromara.common.core.exception.user;

import static org.assertj.core.api.Assertions.*;

import java.util.Locale;
import org.dromara.common.core.BaseIntegrationTest;
import org.dromara.common.core.exception.base.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * CaptchaException 和 CaptchaExpireException 集成测试
 *
 * <p>测试验证码相关异常类的功能，包括： - CaptchaException（验证码错误异常） - CaptchaExpireException（验证码过期异常）
 *
 * <p>这些异常类继承自 UserException，依赖 MessageUtils 进行国际化消息处理， 因此需要 Spring 容器环境进行集成测试。
 *
 * @author Test Team
 * @date 2025-10-31
 */
@DisplayName("验证码异常类集成测试")
class CaptchaExceptionsIntegrationTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("CaptchaException（验证码错误异常）测试")
    class CaptchaExceptionTests {

        @Nested
        @DisplayName("构造函数测试")
        class ConstructorTests {

            @Test
            @DisplayName("应该通过无参构造函数创建默认验证码错误异常")
            void shouldCreateWithDefaultMessage() {
                // Act
                CaptchaException exception = new CaptchaException();

                // Assert
                assertThat(exception).isNotNull();
                assertThat(exception.getModule()).isEqualTo("user");
                assertThat(exception.getCode()).isEqualTo("user.jcaptcha.error");
                assertThat(exception.getArgs()).isEmpty();
            }

            @Test
            @DisplayName("应该通过自定义消息创建验证码错误异常")
            void shouldCreateWithCustomMessage() {
                // Arrange
                String customMessage = "user.captcha.invalid";

                // Act
                CaptchaException exception = new CaptchaException(customMessage);

                // Assert
                assertThat(exception).isNotNull();
                assertThat(exception.getCode()).isEqualTo(customMessage);
                assertThat(exception.getModule()).isEqualTo("user");
            }
        }

        @Nested
        @DisplayName("getMessage 方法测试 - MessageUtils 集成")
        class GetMessageTests {

            @Test
            @DisplayName("应该获取默认验证码错误消息（中文）")
            void shouldGetDefaultCaptchaErrorMessageInChinese() {
                // Arrange
                LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
                CaptchaException exception = new CaptchaException();

                // Act
                String message = exception.getMessage();

                // Assert
                assertThat(message).isEqualTo("验证码错误");
            }

            @Test
            @DisplayName("应该获取自定义消息对应的国际化文本")
            void shouldGetCustomMessageFromMessageUtils() {
                // Arrange
                LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
                CaptchaException exception = new CaptchaException("user.jcaptcha.expire");

                // Act
                String message = exception.getMessage();

                // Assert
                // 自定义消息码应该能被正确解析
                assertThat(message).isNotBlank();
            }
        }

        @Nested
        @DisplayName("继承特性测试")
        class InheritanceTests {

            @Test
            @DisplayName("应该继承自 UserException")
            void shouldInheritFromUserException() {
                // Arrange
                CaptchaException exception = new CaptchaException();

                // Assert
                assertThat(exception).isInstanceOf(UserException.class);
            }

            @Test
            @DisplayName("应该继承自 BaseException")
            void shouldInheritFromBaseException() {
                // Arrange
                CaptchaException exception = new CaptchaException();

                // Assert
                assertThat(exception).isInstanceOf(BaseException.class);
            }

            @Test
            @DisplayName("应该继承自 RuntimeException")
            void shouldInheritFromRuntimeException() {
                // Arrange
                CaptchaException exception = new CaptchaException();

                // Assert
                assertThat(exception).isInstanceOf(RuntimeException.class);
            }
        }

        @Nested
        @DisplayName("序列化测试")
        class SerializationTests {

            @Test
            @DisplayName("应该定义 serialVersionUID")
            void shouldDefineSerialVersionUID() {
                // Assert
                assertThatCode(
                                () -> {
                                    java.lang.reflect.Field field =
                                            CaptchaException.class.getDeclaredField(
                                                    "serialVersionUID");
                                    field.setAccessible(true);
                                    long serialVersionUID = field.getLong(null);
                                    assertThat(serialVersionUID).isEqualTo(1L);
                                })
                        .doesNotThrowAnyException();
            }
        }

        @Nested
        @DisplayName("真实业务场景测试")
        class RealWorldScenarioTests {

            @Test
            @DisplayName("场景: 用户输入错误的验证码")
            void shouldHandleIncorrectCaptchaInput() {
                // Arrange & Act
                Throwable thrown =
                        catchThrowable(
                                () -> {
                                    // 模拟验证码验证失败
                                    throw new CaptchaException();
                                });

                // Assert
                assertThat(thrown)
                        .isInstanceOf(CaptchaException.class)
                        .hasFieldOrPropertyWithValue("code", "user.jcaptcha.error");
            }

            @Test
            @DisplayName("场景: 验证码格式不合法")
            void shouldHandleInvalidCaptchaFormat() {
                // Arrange & Act
                Throwable thrown =
                        catchThrowable(
                                () -> {
                                    // 模拟验证码格式不合法
                                    throw new CaptchaException("user.captcha.format.invalid");
                                });

                // Assert
                assertThat(thrown)
                        .isInstanceOf(CaptchaException.class)
                        .isInstanceOf(UserException.class);
            }
        }
    }

    @Nested
    @DisplayName("CaptchaExpireException（验证码过期异常）测试")
    class CaptchaExpireExceptionTests {

        @Nested
        @DisplayName("构造函数测试")
        class ConstructorTests {

            @Test
            @DisplayName("应该通过无参构造函数创建验证码过期异常")
            void shouldCreateWithDefaultMessage() {
                // Act
                CaptchaExpireException exception = new CaptchaExpireException();

                // Assert
                assertThat(exception).isNotNull();
                assertThat(exception.getModule()).isEqualTo("user");
                assertThat(exception.getCode()).isEqualTo("user.jcaptcha.expire");
                assertThat(exception.getArgs()).isEmpty();
            }
        }

        @Nested
        @DisplayName("getMessage 方法测试 - MessageUtils 集成")
        class GetMessageTests {

            @Test
            @DisplayName("应该获取验证码过期消息（中文）")
            void shouldGetCaptchaExpireMessageInChinese() {
                // Arrange
                LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
                CaptchaExpireException exception = new CaptchaExpireException();

                // Act
                String message = exception.getMessage();

                // Assert
                assertThat(message).isEqualTo("验证码已失效");
            }
        }

        @Nested
        @DisplayName("继承特性测试")
        class InheritanceTests {

            @Test
            @DisplayName("应该继承自 UserException")
            void shouldInheritFromUserException() {
                // Arrange
                CaptchaExpireException exception = new CaptchaExpireException();

                // Assert
                assertThat(exception).isInstanceOf(UserException.class);
            }

            @Test
            @DisplayName("应该继承自 BaseException")
            void shouldInheritFromBaseException() {
                // Arrange
                CaptchaExpireException exception = new CaptchaExpireException();

                // Assert
                assertThat(exception).isInstanceOf(BaseException.class);
            }

            @Test
            @DisplayName("应该继承自 RuntimeException")
            void shouldInheritFromRuntimeException() {
                // Arrange
                CaptchaExpireException exception = new CaptchaExpireException();

                // Assert
                assertThat(exception).isInstanceOf(RuntimeException.class);
            }
        }

        @Nested
        @DisplayName("序列化测试")
        class SerializationTests {

            @Test
            @DisplayName("应该定义 serialVersionUID")
            void shouldDefineSerialVersionUID() {
                // Assert
                assertThatCode(
                                () -> {
                                    java.lang.reflect.Field field =
                                            CaptchaExpireException.class.getDeclaredField(
                                                    "serialVersionUID");
                                    field.setAccessible(true);
                                    long serialVersionUID = field.getLong(null);
                                    assertThat(serialVersionUID).isEqualTo(1L);
                                })
                        .doesNotThrowAnyException();
            }
        }

        @Nested
        @DisplayName("真实业务场景测试")
        class RealWorldScenarioTests {

            @Test
            @DisplayName("场景: 用户超时输入验证码")
            void shouldHandleExpiredCaptcha() {
                // Arrange & Act
                Throwable thrown =
                        catchThrowable(
                                () -> {
                                    // 模拟验证码已过期（超过5分钟）
                                    throw new CaptchaExpireException();
                                });

                // Assert
                assertThat(thrown)
                        .isInstanceOf(CaptchaExpireException.class)
                        .hasFieldOrPropertyWithValue("code", "user.jcaptcha.expire");
            }

            @Test
            @DisplayName("场景: 验证码已被使用过")
            void shouldHandleCaptchaAlreadyUsed() {
                // Arrange & Act
                Throwable thrown =
                        catchThrowable(
                                () -> {
                                    // 验证码已过期（被使用后失效）
                                    throw new CaptchaExpireException();
                                });

                // Assert
                assertThat(thrown).isInstanceOf(CaptchaExpireException.class);
                LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
                assertThat(((CaptchaExpireException) thrown).getMessage()).contains("失效");
            }
        }
    }

    @Nested
    @DisplayName("两种异常的区别测试")
    class DifferenceBetweenExceptionsTests {

        @Test
        @DisplayName("CaptchaException 和 CaptchaExpireException 应该有不同的错误码")
        void shouldHaveDifferentErrorCodes() {
            // Arrange
            CaptchaException captchaException = new CaptchaException();
            CaptchaExpireException expireException = new CaptchaExpireException();

            // Assert
            assertThat(captchaException.getCode()).isEqualTo("user.jcaptcha.error");
            assertThat(expireException.getCode()).isEqualTo("user.jcaptcha.expire");
            assertThat(captchaException.getCode()).isNotEqualTo(expireException.getCode());
        }

        @Test
        @DisplayName("应该有不同的国际化消息")
        void shouldHaveDifferentMessages() {
            // Arrange
            LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
            CaptchaException captchaException = new CaptchaException();
            CaptchaExpireException expireException = new CaptchaExpireException();

            // Act
            String errorMessage = captchaException.getMessage();
            String expireMessage = expireException.getMessage();

            // Assert
            assertThat(errorMessage).isEqualTo("验证码错误");
            assertThat(expireMessage).isEqualTo("验证码已失效");
            assertThat(errorMessage).isNotEqualTo(expireMessage);
        }

        @Test
        @DisplayName("两者都应该继承自 UserException")
        void shouldBothInheritFromUserException() {
            // Arrange
            CaptchaException captchaException = new CaptchaException();
            CaptchaExpireException expireException = new CaptchaExpireException();

            // Assert
            assertThat(captchaException).isInstanceOf(UserException.class);
            assertThat(expireException).isInstanceOf(UserException.class);
        }
    }

    @Nested
    @DisplayName("异常抛出和捕获测试")
    class ThrowAndCatchTests {

        @Test
        @DisplayName("应该能正确抛出和捕获 CaptchaException")
        void shouldThrowAndCatchCaptchaException() {
            // Assert
            assertThatThrownBy(
                            () -> {
                                throw new CaptchaException();
                            })
                    .isInstanceOf(CaptchaException.class)
                    .isInstanceOf(UserException.class)
                    .hasFieldOrPropertyWithValue("code", "user.jcaptcha.error");
        }

        @Test
        @DisplayName("应该能正确抛出和捕获 CaptchaExpireException")
        void shouldThrowAndCatchCaptchaExpireException() {
            // Assert
            assertThatThrownBy(
                            () -> {
                                throw new CaptchaExpireException();
                            })
                    .isInstanceOf(CaptchaExpireException.class)
                    .isInstanceOf(UserException.class)
                    .hasFieldOrPropertyWithValue("code", "user.jcaptcha.expire");
        }

        @Test
        @DisplayName("应该能通过 UserException 类型捕获验证码异常")
        void shouldCatchAsUserException() {
            // Arrange & Act
            Throwable captchaError =
                    catchThrowable(
                            () -> {
                                throw new CaptchaException();
                            });

            Throwable captchaExpire =
                    catchThrowable(
                            () -> {
                                throw new CaptchaExpireException();
                            });

            // Assert
            assertThat(captchaError).isInstanceOf(UserException.class);
            assertThat(captchaExpire).isInstanceOf(UserException.class);
        }

        @Test
        @DisplayName("应该能通过 RuntimeException 类型捕获验证码异常")
        void shouldCatchAsRuntimeException() {
            // Arrange & Act
            Throwable captchaError =
                    catchThrowable(
                            () -> {
                                throw new CaptchaException();
                            });

            Throwable captchaExpire =
                    catchThrowable(
                            () -> {
                                throw new CaptchaExpireException();
                            });

            // Assert
            assertThat(captchaError).isInstanceOf(RuntimeException.class);
            assertThat(captchaExpire).isInstanceOf(RuntimeException.class);
        }
    }
}
