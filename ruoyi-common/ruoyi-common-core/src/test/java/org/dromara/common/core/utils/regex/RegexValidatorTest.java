package org.dromara.common.core.utils.regex;

import static org.assertj.core.api.Assertions.*;

import cn.hutool.core.exceptions.ValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * RegexValidator 测试类.
 *
 * <p>正则规则说明： - ACCOUNT: ^[a-zA-Z][a-zA-Z0-9_]{4,15}$ (账号必须以字母开头，长度5-16位，只能包含字母、数字、下划线) - STATUS:
 * ^[01]$ (状态只能是0或1)
 *
 * @author Test Team
 */
@DisplayName("RegexValidator 工具类测试")
class RegexValidatorTest {

  @Nested
  @DisplayName("isAccount 方法测试")
  class IsAccountTests {

    @ParameterizedTest
    @ValueSource(
        strings = {
          "admin", // 5位，全小写
          "Admin", // 5位，首字母大写
          "ADMIN", // 5位，全大写
          "user123", // 7位，字母+数字
          "test_user", // 9位，包含下划线
          "A1234", // 5位，最短有效长度
          "a12345678901234", // 16位，最长有效长度
          "UserName_123", // 12位，混合大小写、数字、下划线
          "aB_123", // 6位，混合格式
          "TestUser123" // 11位，驼峰命名
        })
    @DisplayName("应该接受有效的账号格式")
    void shouldAcceptValidAccounts(String account) {
      // Act & Assert
      assertThat(RegexValidator.isAccount(account)).as("账号 '%s' 应该是有效的", account).isTrue();
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
          "abc", // 3位，太短（最少5位）
          "abcd", // 4位，太短
          "a1234567890123456", // 17位，太长（最多16位）
          "123abc", // 数字开头
          "_admin", // 下划线开头
          "admin-user", // 包含连字符
          "admin user", // 包含空格
          "admin@user", // 包含@符号
          "用户名", // 中文字符
          "admin.user", // 包含点号
          "admin#123", // 包含#号
          "admin$123" // 包含$符号
        })
    @DisplayName("应该拒绝无效的账号格式")
    void shouldRejectInvalidAccounts(String account) {
      // Act & Assert
      assertThat(RegexValidator.isAccount(account)).as("账号 '%s' 应该是无效的", account).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("应该拒绝null和空字符串")
    void shouldRejectNullAndEmpty(String account) {
      // Act & Assert
      assertThat(RegexValidator.isAccount(account)).isFalse();
    }

    @Test
    @DisplayName("应该拒绝只包含空格的字符串")
    void shouldRejectWhitespaceOnly() {
      // Act & Assert
      assertThat(RegexValidator.isAccount("     ")).isFalse();
    }

    @Test
    @DisplayName("应该正确处理边界长度 - 5位（最短）")
    void shouldHandleMinimumLength() {
      // Act & Assert
      assertThat(RegexValidator.isAccount("a1234")).isTrue();
      assertThat(RegexValidator.isAccount("a123")).isFalse();
    }

    @Test
    @DisplayName("应该正确处理边界长度 - 16位（最长）")
    void shouldHandleMaximumLength() {
      // Act & Assert
      assertThat(RegexValidator.isAccount("a123456789012345")).isTrue(); // 16位
      assertThat(RegexValidator.isAccount("a1234567890123456")).isFalse(); // 17位
    }

    @Test
    @DisplayName("应该区分大小写")
    void shouldBeCaseSensitive() {
      // Act & Assert
      assertThat(RegexValidator.isAccount("Admin")).isTrue();
      assertThat(RegexValidator.isAccount("ADMIN")).isTrue();
      assertThat(RegexValidator.isAccount("admin")).isTrue();
    }
  }

  @Nested
  @DisplayName("validateAccount 方法测试")
  class ValidateAccountTests {

    @Test
    @DisplayName("验证有效账号应该返回原值")
    void shouldReturnOriginalValueForValidAccount() {
      // Arrange
      String validAccount = "admin123";

      // Act
      String result = RegexValidator.validateAccount(validAccount, "账号格式错误");

      // Assert
      assertThat(result).isEqualTo(validAccount);
    }

    @Test
    @DisplayName("验证无效账号应该抛出ValidateException")
    void shouldThrowValidateExceptionForInvalidAccount() {
      // Arrange
      String invalidAccount = "123admin"; // 数字开头
      String errorMsg = "账号格式不正确";

      // Act & Assert
      assertThatThrownBy(() -> RegexValidator.validateAccount(invalidAccount, errorMsg))
          .isInstanceOf(ValidateException.class)
          .hasMessage(errorMsg);
    }

    @Test
    @DisplayName("验证null账号应该抛出ValidateException")
    void shouldThrowValidateExceptionForNullAccount() {
      // Arrange
      String errorMsg = "账号不能为空";

      // Act & Assert
      assertThatThrownBy(() -> RegexValidator.validateAccount(null, errorMsg))
          .isInstanceOf(ValidateException.class)
          .hasMessage(errorMsg);
    }

    @Test
    @DisplayName("验证空账号应该抛出ValidateException")
    void shouldThrowValidateExceptionForEmptyAccount() {
      // Arrange
      String errorMsg = "账号不能为空";

      // Act & Assert
      assertThatThrownBy(() -> RegexValidator.validateAccount("", errorMsg))
          .isInstanceOf(ValidateException.class)
          .hasMessage(errorMsg);
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "User123", "test_user_01", "A1234"})
    @DisplayName("多个有效账号验证应该都返回原值")
    void shouldReturnOriginalValueForMultipleValidAccounts(String account) {
      // Act
      String result = RegexValidator.validateAccount(account, "账号格式错误");

      // Assert
      assertThat(result).isEqualTo(account);
    }

    @Test
    @DisplayName("应该能够自定义错误消息")
    void shouldSupportCustomErrorMessage() {
      // Arrange
      String customMsg = "自定义错误信息：账号格式必须以字母开头，长度5-16位";

      // Act & Assert
      assertThatThrownBy(() -> RegexValidator.validateAccount("123", customMsg))
          .isInstanceOf(ValidateException.class)
          .hasMessage(customMsg);
    }
  }

  @Nested
  @DisplayName("isStatus 方法测试")
  class IsStatusTests {

    @ParameterizedTest
    @ValueSource(strings = {"0", "1"})
    @DisplayName("应该接受有效的状态值")
    void shouldAcceptValidStatus(String status) {
      // Act & Assert
      assertThat(RegexValidator.isStatus(status)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
          "2", // 超出范围
          "01", // 两位数字
          "00", // 两个0
          "10", // 1和0
          "true", // 布尔字符串
          "false", // 布尔字符串
          "yes", // yes/no
          "no",
          " 0", // 前导空格
          "0 ", // 后置空格
          " 1 ", // 两边空格
          "O", // 字母O
          "l", // 字母l
          "一", // 中文
          "" // 空字符串
        })
    @DisplayName("应该拒绝无效的状态值")
    void shouldRejectInvalidStatus(String status) {
      // Act & Assert
      assertThat(RegexValidator.isStatus(status)).as("状态 '%s' 应该是无效的", status).isFalse();
    }

    @Test
    @DisplayName("应该拒绝null")
    void shouldRejectNull() {
      // Act & Assert
      assertThat(RegexValidator.isStatus(null)).isFalse();
    }

    @Test
    @DisplayName("应该拒绝空字符串")
    void shouldRejectEmptyString() {
      // Act & Assert
      assertThat(RegexValidator.isStatus("")).isFalse();
    }

    @Test
    @DisplayName("应该拒绝只包含空格")
    void shouldRejectWhitespaceOnly() {
      // Act & Assert
      assertThat(RegexValidator.isStatus("   ")).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "3", "9", "100", "-0"})
    @DisplayName("应该拒绝其他数字")
    void shouldRejectOtherNumbers(String status) {
      // Act & Assert
      assertThat(RegexValidator.isStatus(status)).isFalse();
    }
  }

  @Nested
  @DisplayName("validateStatus 方法测试")
  class ValidateStatusTests {

    @ParameterizedTest
    @ValueSource(strings = {"0", "1"})
    @DisplayName("验证有效状态应该返回原值")
    void shouldReturnOriginalValueForValidStatus(String status) {
      // Act
      String result = RegexValidator.validateStatus(status, "状态值错误");

      // Assert
      assertThat(result).isEqualTo(status);
    }

    @Test
    @DisplayName("验证无效状态应该抛出ValidateException")
    void shouldThrowValidateExceptionForInvalidStatus() {
      // Arrange
      String invalidStatus = "2";
      String errorMsg = "状态值必须是0或1";

      // Act & Assert
      assertThatThrownBy(() -> RegexValidator.validateStatus(invalidStatus, errorMsg))
          .isInstanceOf(ValidateException.class)
          .hasMessage(errorMsg);
    }

    @Test
    @DisplayName("验证null状态应该抛出ValidateException")
    void shouldThrowValidateExceptionForNullStatus() {
      // Arrange
      String errorMsg = "状态不能为空";

      // Act & Assert
      assertThatThrownBy(() -> RegexValidator.validateStatus(null, errorMsg))
          .isInstanceOf(ValidateException.class)
          .hasMessage(errorMsg);
    }

    @Test
    @DisplayName("验证空状态应该抛出ValidateException")
    void shouldThrowValidateExceptionForEmptyStatus() {
      // Arrange
      String errorMsg = "状态不能为空";

      // Act & Assert
      assertThatThrownBy(() -> RegexValidator.validateStatus("", errorMsg))
          .isInstanceOf(ValidateException.class)
          .hasMessage(errorMsg);
    }

    @Test
    @DisplayName("应该能够自定义错误消息")
    void shouldSupportCustomErrorMessage() {
      // Arrange
      String customMsg = "自定义错误：状态值只能是0（正常）或1（停用）";

      // Act & Assert
      assertThatThrownBy(() -> RegexValidator.validateStatus("2", customMsg))
          .isInstanceOf(ValidateException.class)
          .hasMessage(customMsg);
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false", "yes", "no", "active", "inactive"})
    @DisplayName("应该拒绝常见的非0/1状态值")
    void shouldRejectCommonNonBinaryStatusValues(String status) {
      // Act & Assert
      assertThatThrownBy(() -> RegexValidator.validateStatus(status, "状态值错误"))
          .isInstanceOf(ValidateException.class);
    }
  }

  @Nested
  @DisplayName("综合场景测试")
  class IntegrationTests {

    @Test
    @DisplayName("应该支持链式验证")
    void shouldSupportChainedValidation() {
      // Arrange
      String account = "admin";
      String status = "0";

      // Act
      String validatedAccount = RegexValidator.validateAccount(account, "账号错误");
      String validatedStatus = RegexValidator.validateStatus(status, "状态错误");

      // Assert
      assertThat(validatedAccount).isEqualTo(account);
      assertThat(validatedStatus).isEqualTo(status);
    }

    @Test
    @DisplayName("应该在第一个验证失败时停止")
    void shouldStopAtFirstValidationFailure() {
      // Arrange
      String invalidAccount = "123admin";
      String validStatus = "0";

      // Act & Assert
      assertThatThrownBy(
              () -> {
                RegexValidator.validateAccount(invalidAccount, "账号错误");
                RegexValidator.validateStatus(validStatus, "状态错误");
              })
          .isInstanceOf(ValidateException.class)
          .hasMessage("账号错误");
    }

    @Test
    @DisplayName("验证方法与检查方法应该一致")
    void shouldBeConsistentBetweenCheckAndValidateMethods() {
      // Arrange
      String[] testAccounts = {"admin", "123admin", "test_user", "ab"};

      // Act & Assert
      for (String account : testAccounts) {
        boolean isValid = RegexValidator.isAccount(account);

        if (isValid) {
          assertThatCode(() -> RegexValidator.validateAccount(account, "错误"))
              .doesNotThrowAnyException();
        } else {
          assertThatThrownBy(() -> RegexValidator.validateAccount(account, "错误"))
              .isInstanceOf(ValidateException.class);
        }
      }
    }
  }

  @Nested
  @DisplayName("Pattern常量测试")
  class PatternConstantsTests {

    @Test
    @DisplayName("ACCOUNT Pattern 应该不为null")
    void accountPatternShouldNotBeNull() {
      assertThat(RegexValidator.ACCOUNT).isNotNull();
    }

    @Test
    @DisplayName("STATUS Pattern 应该不为null")
    void statusPatternShouldNotBeNull() {
      assertThat(RegexValidator.STATUS).isNotNull();
    }

    @Test
    @DisplayName("DICTIONARY_TYPE Pattern 应该不为null")
    void dictionaryTypePatternShouldNotBeNull() {
      assertThat(RegexValidator.DICTIONARY_TYPE).isNotNull();
    }

    @Test
    @DisplayName("ID_CARD_LAST_6 Pattern 应该不为null")
    void idCardLast6PatternShouldNotBeNull() {
      assertThat(RegexValidator.ID_CARD_LAST_6).isNotNull();
    }

    @Test
    @DisplayName("QQ_NUMBER Pattern 应该不为null")
    void qqNumberPatternShouldNotBeNull() {
      assertThat(RegexValidator.QQ_NUMBER).isNotNull();
    }

    @Test
    @DisplayName("POSTAL_CODE Pattern 应该不为null")
    void postalCodePatternShouldNotBeNull() {
      assertThat(RegexValidator.POSTAL_CODE).isNotNull();
    }

    @Test
    @DisplayName("PASSWORD Pattern 应该不为null")
    void passwordPatternShouldNotBeNull() {
      assertThat(RegexValidator.PASSWORD).isNotNull();
    }
  }
}
