package org.dromara.auth.properties;

import static org.assertj.core.api.Assertions.assertThat;

import org.dromara.auth.BaseUnitTest;
import org.dromara.auth.enums.CaptchaCategory;
import org.dromara.auth.enums.CaptchaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 认证配置类测试
 *
 * <p>测试 Properties 配置类的基本功能
 *
 * @author Test Team
 */
@DisplayName("认证配置类测试")
class AuthPropertiesTest extends BaseUnitTest {

  @Nested
  @DisplayName("1. CaptchaProperties 验证码配置测试")
  class CaptchaPropertiesTests {

    @Test
    @DisplayName("应该能够创建 CaptchaProperties 实例")
    void shouldCreateCaptchaPropertiesInstance() {
      // Act
      CaptchaProperties properties = new CaptchaProperties();

      // Assert
      assertThat(properties).isNotNull();
    }

    @Test
    @DisplayName("应该能够设置和获取 type")
    void shouldSetAndGetType() {
      // Arrange
      CaptchaProperties properties = new CaptchaProperties();

      // Act
      properties.setType(CaptchaType.MATH);

      // Assert
      assertThat(properties.getType()).isNotNull().isEqualTo(CaptchaType.MATH);
    }

    @Test
    @DisplayName("应该能够设置和获取 category")
    void shouldSetAndGetCategory() {
      // Arrange
      CaptchaProperties properties = new CaptchaProperties();

      // Act
      properties.setCategory(CaptchaCategory.LINE);

      // Assert
      assertThat(properties.getCategory()).isNotNull().isEqualTo(CaptchaCategory.LINE);
    }

    @Test
    @DisplayName("应该能够设置和获取 numberLength")
    void shouldSetAndGetNumberLength() {
      // Arrange
      CaptchaProperties properties = new CaptchaProperties();

      // Act
      properties.setNumberLength(4);

      // Assert
      assertThat(properties.getNumberLength()).isNotNull().isEqualTo(4);
    }

    @Test
    @DisplayName("应该能够设置和获取 charLength")
    void shouldSetAndGetCharLength() {
      // Arrange
      CaptchaProperties properties = new CaptchaProperties();

      // Act
      properties.setCharLength(5);

      // Assert
      assertThat(properties.getCharLength()).isNotNull().isEqualTo(5);
    }

    @Test
    @DisplayName("应该能够设置和获取 enabled")
    void shouldSetAndGetEnabled() {
      // Arrange
      CaptchaProperties properties = new CaptchaProperties();

      // Act
      properties.setEnabled(true);

      // Assert
      assertThat(properties.getEnabled()).isNotNull().isTrue();
    }

    @Test
    @DisplayName("应该支持禁用验证码")
    void shouldSupportDisablingCaptcha() {
      // Arrange
      CaptchaProperties properties = new CaptchaProperties();

      // Act
      properties.setEnabled(false);

      // Assert
      assertThat(properties.getEnabled()).isFalse();
    }

    @Test
    @DisplayName("应该能够配置完整的验证码属性")
    void shouldConfigureFullCaptchaProperties() {
      // Arrange
      CaptchaProperties properties = new CaptchaProperties();

      // Act
      properties.setType(CaptchaType.CHAR);
      properties.setCategory(CaptchaCategory.CIRCLE);
      properties.setNumberLength(6);
      properties.setCharLength(4);
      properties.setEnabled(true);

      // Assert
      assertThat(properties.getType()).isEqualTo(CaptchaType.CHAR);
      assertThat(properties.getCategory()).isEqualTo(CaptchaCategory.CIRCLE);
      assertThat(properties.getNumberLength()).isEqualTo(6);
      assertThat(properties.getCharLength()).isEqualTo(4);
      assertThat(properties.getEnabled()).isTrue();
    }

    @Test
    @DisplayName("numberLength 应该支持各种有效值")
    void numberLengthShouldSupportValidValues() {
      // Arrange
      CaptchaProperties properties = new CaptchaProperties();

      // Act & Assert
      properties.setNumberLength(2);
      assertThat(properties.getNumberLength()).isEqualTo(2);

      properties.setNumberLength(4);
      assertThat(properties.getNumberLength()).isEqualTo(4);

      properties.setNumberLength(6);
      assertThat(properties.getNumberLength()).isEqualTo(6);
    }

    @Test
    @DisplayName("charLength 应该支持各种有效值")
    void charLengthShouldSupportValidValues() {
      // Arrange
      CaptchaProperties properties = new CaptchaProperties();

      // Act & Assert
      properties.setCharLength(3);
      assertThat(properties.getCharLength()).isEqualTo(3);

      properties.setCharLength(5);
      assertThat(properties.getCharLength()).isEqualTo(5);

      properties.setCharLength(8);
      assertThat(properties.getCharLength()).isEqualTo(8);
    }

    @Test
    @DisplayName("应该支持所有 CaptchaType")
    void shouldSupportAllCaptchaTypes() {
      // Arrange
      CaptchaProperties properties = new CaptchaProperties();

      // Act & Assert - MATH
      properties.setType(CaptchaType.MATH);
      assertThat(properties.getType()).isEqualTo(CaptchaType.MATH);

      // Act & Assert - CHAR
      properties.setType(CaptchaType.CHAR);
      assertThat(properties.getType()).isEqualTo(CaptchaType.CHAR);
    }

    @Test
    @DisplayName("应该支持所有 CaptchaCategory")
    void shouldSupportAllCaptchaCategories() {
      // Arrange
      CaptchaProperties properties = new CaptchaProperties();

      // Act & Assert - LINE
      properties.setCategory(CaptchaCategory.LINE);
      assertThat(properties.getCategory()).isEqualTo(CaptchaCategory.LINE);

      // Act & Assert - CIRCLE
      properties.setCategory(CaptchaCategory.CIRCLE);
      assertThat(properties.getCategory()).isEqualTo(CaptchaCategory.CIRCLE);

      // Act & Assert - SHEAR
      properties.setCategory(CaptchaCategory.SHEAR);
      assertThat(properties.getCategory()).isEqualTo(CaptchaCategory.SHEAR);
    }
  }

  @Nested
  @DisplayName("2. UserPasswordProperties 密码配置测试")
  class UserPasswordPropertiesTests {

    @Test
    @DisplayName("应该能够创建 UserPasswordProperties 实例")
    void shouldCreateUserPasswordPropertiesInstance() {
      // Act
      UserPasswordProperties properties = new UserPasswordProperties();

      // Assert
      assertThat(properties).isNotNull();
    }

    @Test
    @DisplayName("应该能够设置和获取 maxRetryCount")
    void shouldSetAndGetMaxRetryCount() {
      // Arrange
      UserPasswordProperties properties = new UserPasswordProperties();

      // Act
      properties.setMaxRetryCount(5);

      // Assert
      assertThat(properties.getMaxRetryCount()).isNotNull().isEqualTo(5);
    }

    @Test
    @DisplayName("应该能够设置和获取 lockTime")
    void shouldSetAndGetLockTime() {
      // Arrange
      UserPasswordProperties properties = new UserPasswordProperties();

      // Act
      properties.setLockTime(10);

      // Assert
      assertThat(properties.getLockTime()).isNotNull().isEqualTo(10);
    }

    @Test
    @DisplayName("应该能够配置完整的密码属性")
    void shouldConfigureFullPasswordProperties() {
      // Arrange
      UserPasswordProperties properties = new UserPasswordProperties();

      // Act
      properties.setMaxRetryCount(5);
      properties.setLockTime(10);

      // Assert
      assertThat(properties.getMaxRetryCount()).isEqualTo(5);
      assertThat(properties.getLockTime()).isEqualTo(10);
    }

    @Test
    @DisplayName("maxRetryCount 应该支持各种有效值")
    void maxRetryCountShouldSupportValidValues() {
      // Arrange
      UserPasswordProperties properties = new UserPasswordProperties();

      // Act & Assert
      properties.setMaxRetryCount(3);
      assertThat(properties.getMaxRetryCount()).isEqualTo(3);

      properties.setMaxRetryCount(5);
      assertThat(properties.getMaxRetryCount()).isEqualTo(5);

      properties.setMaxRetryCount(10);
      assertThat(properties.getMaxRetryCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("lockTime 应该支持各种有效值（分钟）")
    void lockTimeShouldSupportValidValues() {
      // Arrange
      UserPasswordProperties properties = new UserPasswordProperties();

      // Act & Assert - 5分钟
      properties.setLockTime(5);
      assertThat(properties.getLockTime()).isEqualTo(5);

      // Act & Assert - 10分钟
      properties.setLockTime(10);
      assertThat(properties.getLockTime()).isEqualTo(10);

      // Act & Assert - 30分钟
      properties.setLockTime(30);
      assertThat(properties.getLockTime()).isEqualTo(30);
    }

    @Test
    @DisplayName("应该支持严格的安全策略")
    void shouldSupportStrictSecurityPolicy() {
      // Arrange
      UserPasswordProperties properties = new UserPasswordProperties();

      // Act - 严格策略：3次错误，锁定30分钟
      properties.setMaxRetryCount(3);
      properties.setLockTime(30);

      // Assert
      assertThat(properties.getMaxRetryCount()).isEqualTo(3);
      assertThat(properties.getLockTime()).isEqualTo(30);
    }

    @Test
    @DisplayName("应该支持宽松的安全策略")
    void shouldSupportRelaxedSecurityPolicy() {
      // Arrange
      UserPasswordProperties properties = new UserPasswordProperties();

      // Act - 宽松策略：10次错误，锁定5分钟
      properties.setMaxRetryCount(10);
      properties.setLockTime(5);

      // Assert
      assertThat(properties.getMaxRetryCount()).isEqualTo(10);
      assertThat(properties.getLockTime()).isEqualTo(5);
    }

    @Test
    @DisplayName("应该支持默认推荐策略")
    void shouldSupportDefaultRecommendedPolicy() {
      // Arrange
      UserPasswordProperties properties = new UserPasswordProperties();

      // Act - 默认推荐：5次错误，锁定10分钟
      properties.setMaxRetryCount(5);
      properties.setLockTime(10);

      // Assert
      assertThat(properties.getMaxRetryCount()).isEqualTo(5);
      assertThat(properties.getLockTime()).isEqualTo(10);
    }
  }

  @Nested
  @DisplayName("3. Properties 集成场景测试")
  class PropertiesIntegrationTests {

    @Test
    @DisplayName("验证码和密码配置应该能够协同工作")
    void captchaAndPasswordPropertiesShouldWorkTogether() {
      // Arrange
      CaptchaProperties captchaProps = new CaptchaProperties();
      UserPasswordProperties passwordProps = new UserPasswordProperties();

      // Act - 配置验证码
      captchaProps.setType(CaptchaType.MATH);
      captchaProps.setCategory(CaptchaCategory.LINE);
      captchaProps.setNumberLength(4);
      captchaProps.setEnabled(true);

      // Act - 配置密码策略
      passwordProps.setMaxRetryCount(5);
      passwordProps.setLockTime(10);

      // Assert - 两个配置都应该有效
      assertThat(captchaProps.getEnabled()).isTrue();
      assertThat(captchaProps.getType()).isEqualTo(CaptchaType.MATH);
      assertThat(passwordProps.getMaxRetryCount()).isEqualTo(5);
      assertThat(passwordProps.getLockTime()).isEqualTo(10);
    }

    @Test
    @DisplayName("禁用验证码时密码策略仍应生效")
    void passwordPolicyShouldWorkWhenCaptchaDisabled() {
      // Arrange
      CaptchaProperties captchaProps = new CaptchaProperties();
      UserPasswordProperties passwordProps = new UserPasswordProperties();

      // Act
      captchaProps.setEnabled(false);
      passwordProps.setMaxRetryCount(5);
      passwordProps.setLockTime(10);

      // Assert
      assertThat(captchaProps.getEnabled()).isFalse();
      assertThat(passwordProps.getMaxRetryCount()).isEqualTo(5);
    }
  }
}
