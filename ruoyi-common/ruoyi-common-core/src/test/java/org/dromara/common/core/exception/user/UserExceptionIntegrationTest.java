package org.dromara.common.core.exception.user;

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
 * UserException 集成测试.
 *
 * @author Test Team
 */
@DisplayName("UserException 集成测试")
class UserExceptionIntegrationTest extends BaseIntegrationTest {

  @Nested
  @DisplayName("构造函数测试")
  class ConstructorTest {

    @Test
    @DisplayName("应该通过错误码和参数创建异常")
    void shouldCreateWithCodeAndArgs() {
      // Act
      UserException exception = new UserException("user.not.exists", "admin");

      // Assert
      assertThat(exception.getModule()).isEqualTo("user");
      assertThat(exception.getCode()).isEqualTo("user.not.exists");
      assertThat(exception.getArgs()).containsExactly("admin");
      assertThat(exception.getDefaultMessage()).isNull();
    }

    @Test
    @DisplayName("应该支持无参数的错误码")
    void shouldCreateWithCodeOnly() {
      // Act
      UserException exception = new UserException("user.not.exists");

      // Assert
      assertThat(exception.getModule()).isEqualTo("user");
      assertThat(exception.getCode()).isEqualTo("user.not.exists");
      assertThat(exception.getArgs()).isEmpty();
    }

    @Test
    @DisplayName("应该支持多个参数")
    void shouldCreateWithMultipleArgs() {
      // Act
      UserException exception = new UserException("user.password.retry.limit.count", 3, "admin");

      // Assert
      assertThat(exception.getModule()).isEqualTo("user");
      assertThat(exception.getCode()).isEqualTo("user.password.retry.limit.count");
      assertThat(exception.getArgs()).containsExactly(3, "admin");
    }
  }

  @Nested
  @DisplayName("getMessage 方法测试 - MessageUtils 集成")
  class GetMessageIntegrationTest {

    @Test
    @DisplayName("应该通过 MessageUtils 获取用户相关国际化消息")
    void shouldGetUserMessageFromMessageUtils() {
      // Arrange
      LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
      UserException exception = new UserException("user.not.exists");

      // Act
      String message = exception.getMessage();

      // Assert
      assertThat(message).isEqualTo("用户不存在");
    }

    @Test
    @DisplayName("应该正确格式化带参数的消息")
    void shouldFormatMessageWithParameters() {
      // Arrange
      LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
      UserException exception = new UserException("user.password.retry.limit.count", 5);

      // Act
      String message = exception.getMessage();

      // Assert
      assertThat(message).isEqualTo("密码输入错误5次");
    }

    @Test
    @DisplayName("应该正确格式化多参数消息")
    void shouldFormatMessageWithMultipleParameters() {
      // Arrange
      LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
      UserException exception = new UserException("user.password.retry.limit.exceed", 30);

      // Act
      String message = exception.getMessage();

      // Assert
      assertThat(message).isEqualTo("密码输入错误次数过多，账户锁定30分钟");
    }
  }

  @Nested
  @DisplayName("真实业务场景测试")
  class RealScenarioTest {

    @Test
    @DisplayName("用户不存在场景")
    void shouldHandleUserNotExistsScenario() {
      // Arrange
      LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
      UserException exception = new UserException("user.not.exists");

      // Act
      String message = exception.getMessage();

      // Assert
      assertThat(exception.getModule()).isEqualTo("user");
      assertThat(message).isEqualTo("用户不存在");
    }

    @Test
    @DisplayName("用户密码错误场景")
    void shouldHandlePasswordNotMatchScenario() {
      // Arrange
      LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
      UserException exception = new UserException("user.password.not.match");

      // Act
      String message = exception.getMessage();

      // Assert
      assertThat(exception.getModule()).isEqualTo("user");
      assertThat(message).isEqualTo("用户名或密码错误");
    }

    @Test
    @DisplayName("密码重试次数场景")
    void shouldHandlePasswordRetryLimitScenario() {
      // Arrange
      LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
      UserException exception = new UserException("user.password.retry.limit.count", 3);

      // Act
      String message = exception.getMessage();

      // Assert
      assertThat(exception.getModule()).isEqualTo("user");
      assertThat(message).isEqualTo("密码输入错误3次");
    }

    @Test
    @DisplayName("账户锁定场景")
    void shouldHandleAccountLockedScenario() {
      // Arrange
      LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
      UserException exception = new UserException("user.password.retry.limit.exceed", 30);

      // Act
      String message = exception.getMessage();

      // Assert
      assertThat(exception.getModule()).isEqualTo("user");
      assertThat(message).isEqualTo("密码输入错误次数过多，账户锁定30分钟");
    }

    @Test
    @DisplayName("验证码错误场景")
    void shouldHandleCaptchaErrorScenario() {
      // Arrange
      LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
      UserException exception = new UserException("user.jcaptcha.error");

      // Act
      String message = exception.getMessage();

      // Assert
      assertThat(exception.getModule()).isEqualTo("user");
      assertThat(message).isEqualTo("验证码错误");
    }

    @Test
    @DisplayName("验证码过期场景")
    void shouldHandleCaptchaExpireScenario() {
      // Arrange
      LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
      UserException exception = new UserException("user.jcaptcha.expire");

      // Act
      String message = exception.getMessage();

      // Assert
      assertThat(exception.getModule()).isEqualTo("user");
      assertThat(message).isEqualTo("验证码已失效");
    }
  }

  @Nested
  @DisplayName("异常特性测试")
  class ExceptionFeaturesTest {

    @Test
    @DisplayName("应该是 BaseException 的子类")
    void shouldBeSubclassOfBaseException() {
      // Arrange
      UserException exception = new UserException("user.not.exists");

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
                    throw new UserException("user.not.exists");
                  }))
          .isInstanceOf(UserException.class)
          .isInstanceOf(BaseException.class)
          .hasMessage("用户不存在");
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
        throw new UserException("user.password.not.match");
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
        throw new UserException("user.jcaptcha.error");
      } catch (RuntimeException e) {
        caught = true;
        message = e.getMessage();
      }

      // Assert
      assertThat(caught).isTrue();
      assertThat(message).isEqualTo("验证码错误");
    }
  }

  @Nested
  @DisplayName("边界值测试")
  class BoundaryTest {

    @Test
    @DisplayName("应该处理空参数")
    void shouldHandleEmptyArgs() {
      // Arrange
      UserException exception = new UserException("user.not.exists", new Object[0]);

      // Act & Assert
      assertThat(exception.getArgs()).isEmpty();
      assertThat(exception.getMessage()).isEqualTo("用户不存在");
    }

    @Test
    @DisplayName("应该处理 null 参数")
    void shouldHandleNullArgs() {
      // Arrange
      LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
      UserException exception = new UserException("user.password.retry.limit.count", (Object) null);

      // Act
      String message = exception.getMessage();

      // Assert
      assertThat(message).isEqualTo("密码输入错误null次");
    }

    @Test
    @DisplayName("应该处理特殊字符参数")
    void shouldHandleSpecialCharactersInArgs() {
      // Arrange
      LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
      UserException exception =
          new UserException("user.password.retry.limit.count", "<script>alert('xss')</script>");

      // Act
      String message = exception.getMessage();

      // Assert
      assertThat(message).isEqualTo("密码输入错误<script>alert('xss')</script>次");
    }

    @Test
    @DisplayName("应该处理超大数字参数")
    void shouldHandleLargeNumberArgs() {
      // Arrange
      LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
      UserException exception = new UserException("user.password.retry.limit.exceed", 999999);

      // Act
      String message = exception.getMessage();

      // Assert
      assertThat(message).contains("999,999"); // MessageFormat adds thousand separators
    }
  }
}
