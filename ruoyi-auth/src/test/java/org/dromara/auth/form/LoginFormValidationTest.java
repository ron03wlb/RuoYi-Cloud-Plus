package org.dromara.auth.form;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.dromara.auth.AuthTestDataFactory;
import org.dromara.auth.BaseUnitTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * 登录表单验证测试
 *
 * <p>测试所有登录表单的 Bean Validation 注解
 *
 * @author Test Team
 */
@DisplayName("登录表单验证测试")
class LoginFormValidationTest extends BaseUnitTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("1. PasswordLoginBody 密码登录表单验证")
    class PasswordLoginBodyValidationTests {

        @Test
        @DisplayName("有效的密码登录表单 - 应该通过验证")
        void shouldPassValidationWithValidData() {
            // Arrange
            PasswordLoginBody body = AuthTestDataFactory.createPasswordLoginBody();

            // Act
            Set<ConstraintViolation<PasswordLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("用户名为空或空白 - 应该验证失败")
        void shouldFailValidationWhenUsernameIsBlank(String username) {
            // Arrange
            PasswordLoginBody body = AuthTestDataFactory.createPasswordLoginBody();
            body.setUsername(username);

            // Act
            Set<ConstraintViolation<PasswordLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations)
                    .isNotEmpty()
                    .extracting(ConstraintViolation::getMessage)
                    .anyMatch(msg -> msg.contains("username") || msg.contains("blank"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "ab3456789012345678901234567890123"}) // 1个字符和33个字符
        @DisplayName("用户名长度不符合要求 - 应该验证失败")
        void shouldFailValidationWhenUsernameLengthInvalid(String username) {
            // Arrange
            PasswordLoginBody body = AuthTestDataFactory.createPasswordLoginBody();
            body.setUsername(username);

            // Act
            Set<ConstraintViolation<PasswordLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations)
                    .isNotEmpty()
                    .extracting(ConstraintViolation::getMessage)
                    .anyMatch(msg -> msg.contains("length") || msg.contains("长度"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("密码为空或空白 - 应该验证失败")
        void shouldFailValidationWhenPasswordIsBlank(String password) {
            // Arrange
            PasswordLoginBody body = AuthTestDataFactory.createPasswordLoginBody();
            body.setPassword(password);

            // Act
            Set<ConstraintViolation<PasswordLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations)
                    .isNotEmpty()
                    .extracting(ConstraintViolation::getMessage)
                    .anyMatch(msg -> msg.contains("password") || msg.contains("blank"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"1234", "1234567890123456789012345678901"}) // 4个字符和31个字符
        @DisplayName("密码长度不符合要求 - 应该验证失败")
        void shouldFailValidationWhenPasswordLengthInvalid(String password) {
            // Arrange
            PasswordLoginBody body = AuthTestDataFactory.createPasswordLoginBody();
            body.setPassword(password);

            // Act
            Set<ConstraintViolation<PasswordLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations)
                    .isNotEmpty()
                    .extracting(ConstraintViolation::getMessage)
                    .anyMatch(msg -> msg.contains("length") || msg.contains("长度"));
        }

        @Test
        @DisplayName("最小长度边界测试 - 用户名2位，密码5位")
        void shouldPassValidationWithMinimumLengths() {
            // Arrange
            PasswordLoginBody body = AuthTestDataFactory.createPasswordLoginBody();
            body.setUsername("ab"); // 最小2位
            body.setPassword("12345"); // 最小5位

            // Act
            Set<ConstraintViolation<PasswordLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("最大长度边界测试 - 用户名30位，密码30位")
        void shouldPassValidationWithMaximumLengths() {
            // Arrange
            PasswordLoginBody body = AuthTestDataFactory.createPasswordLoginBody();
            body.setUsername("a".repeat(30)); // 最大30位
            body.setPassword("1".repeat(30)); // 最大30位

            // Act
            Set<ConstraintViolation<PasswordLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("2. EmailLoginBody 邮箱登录表单验证")
    class EmailLoginBodyValidationTests {

        @Test
        @DisplayName("有效的邮箱登录表单 - 应该通过验证")
        void shouldPassValidationWithValidData() {
            // Arrange
            EmailLoginBody body = AuthTestDataFactory.createEmailLoginBody();

            // Act
            Set<ConstraintViolation<EmailLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("邮箱为空或空白 - 应该验证失败")
        void shouldFailValidationWhenEmailIsBlank(String email) {
            // Arrange
            EmailLoginBody body = AuthTestDataFactory.createEmailLoginBody();
            body.setEmail(email);

            // Act
            Set<ConstraintViolation<EmailLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "invalid",
                    "invalid@",
                    "@example.com",
                    "invalid@.com",
                    "invalid..email@example.com"
                })
        @DisplayName("邮箱格式不正确 - 应该验证失败")
        void shouldFailValidationWhenEmailFormatInvalid(String email) {
            // Arrange
            EmailLoginBody body = AuthTestDataFactory.createEmailLoginBody();
            body.setEmail(email);

            // Act
            Set<ConstraintViolation<EmailLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations)
                    .isNotEmpty()
                    .extracting(ConstraintViolation::getMessage)
                    .anyMatch(
                            msg ->
                                    msg.contains("email")
                                            || msg.contains("valid")
                                            || msg.contains("邮箱"));
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "user@example.com",
                    "user.name@example.com",
                    "user+tag@example.co.uk",
                    "user_123@sub.example.com"
                })
        @DisplayName("各种有效的邮箱格式 - 应该通过验证")
        void shouldPassValidationWithVariousValidEmails(String email) {
            // Arrange
            EmailLoginBody body = AuthTestDataFactory.createEmailLoginBody();
            body.setEmail(email);

            // Act
            Set<ConstraintViolation<EmailLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("邮箱验证码为空 - 应该验证失败")
        void shouldFailValidationWhenEmailCodeIsBlank(String emailCode) {
            // Arrange
            EmailLoginBody body = AuthTestDataFactory.createEmailLoginBody();
            body.setEmailCode(emailCode);

            // Act
            Set<ConstraintViolation<EmailLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("3. SmsLoginBody 短信登录表单验证")
    class SmsLoginBodyValidationTests {

        @Test
        @DisplayName("有效的短信登录表单 - 应该通过验证")
        void shouldPassValidationWithValidData() {
            // Arrange
            SmsLoginBody body = AuthTestDataFactory.createSmsLoginBody();

            // Act
            Set<ConstraintViolation<SmsLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("手机号为空或空白 - 应该验证失败")
        void shouldFailValidationWhenPhonenumberIsBlank(String phonenumber) {
            // Arrange
            SmsLoginBody body = AuthTestDataFactory.createSmsLoginBody();
            body.setPhonenumber(phonenumber);

            // Act
            Set<ConstraintViolation<SmsLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("短信验证码为空 - 应该验证失败")
        void shouldFailValidationWhenSmsCodeIsBlank(String smsCode) {
            // Arrange
            SmsLoginBody body = AuthTestDataFactory.createSmsLoginBody();
            body.setSmsCode(smsCode);

            // Act
            Set<ConstraintViolation<SmsLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("4. SocialLoginBody 社交登录表单验证")
    class SocialLoginBodyValidationTests {

        @Test
        @DisplayName("有效的社交登录表单 - 应该通过验证")
        void shouldPassValidationWithValidData() {
            // Arrange
            SocialLoginBody body = AuthTestDataFactory.createSocialLoginBody();

            // Act
            Set<ConstraintViolation<SocialLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("社交平台来源为空 - 应该验证失败")
        void shouldFailValidationWhenSourceIsBlank(String source) {
            // Arrange
            SocialLoginBody body = AuthTestDataFactory.createSocialLoginBody();
            body.setSource(source);

            // Act
            Set<ConstraintViolation<SocialLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("社交登录代码为空 - 应该验证失败")
        void shouldFailValidationWhenSocialCodeIsBlank(String socialCode) {
            // Arrange
            SocialLoginBody body = AuthTestDataFactory.createSocialLoginBody();
            body.setSocialCode(socialCode);

            // Act
            Set<ConstraintViolation<SocialLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("社交登录状态为空 - 应该验证失败")
        void shouldFailValidationWhenSocialStateIsBlank(String socialState) {
            // Arrange
            SocialLoginBody body = AuthTestDataFactory.createSocialLoginBody();
            body.setSocialState(socialState);

            // Act
            Set<ConstraintViolation<SocialLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"github", "wechat", "qq", "alipay", "dingtalk"})
        @DisplayName("各种社交平台来源 - 应该通过验证")
        void shouldPassValidationWithVariousSources(String source) {
            // Arrange
            SocialLoginBody body = AuthTestDataFactory.createSocialLoginBody();
            body.setSource(source);

            // Act
            Set<ConstraintViolation<SocialLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("5. XcxLoginBody 小程序登录表单验证")
    class XcxLoginBodyValidationTests {

        @Test
        @DisplayName("有效的小程序登录表单 - 应该通过验证")
        void shouldPassValidationWithValidData() {
            // Arrange
            XcxLoginBody body = AuthTestDataFactory.createXcxLoginBody();

            // Act
            Set<ConstraintViolation<XcxLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("小程序代码为空 - 应该验证失败")
        void shouldFailValidationWhenXcxCodeIsBlank(String xcxCode) {
            // Arrange
            XcxLoginBody body = AuthTestDataFactory.createXcxLoginBody();
            body.setXcxCode(xcxCode);

            // Act
            Set<ConstraintViolation<XcxLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("appid 为空 - 应该通过验证（appid是可选的）")
        void shouldPassValidationWhenAppidIsNull() {
            // Arrange
            XcxLoginBody body = AuthTestDataFactory.createXcxLoginBody();
            body.setAppid(null);

            // Act
            Set<ConstraintViolation<XcxLoginBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("6. RegisterBody 注册表单验证")
    class RegisterBodyValidationTests {

        @Test
        @DisplayName("有效的注册表单 - 应该通过验证")
        void shouldPassValidationWithValidData() {
            // Arrange
            RegisterBody body = AuthTestDataFactory.createRegisterBody();

            // Act
            Set<ConstraintViolation<RegisterBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("用户名为空或空白 - 应该验证失败")
        void shouldFailValidationWhenUsernameIsBlank(String username) {
            // Arrange
            RegisterBody body = AuthTestDataFactory.createRegisterBody();
            body.setUsername(username);

            // Act
            Set<ConstraintViolation<RegisterBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "ab3456789012345678901234567890123"})
        @DisplayName("用户名长度不符合要求 - 应该验证失败")
        void shouldFailValidationWhenUsernameLengthInvalid(String username) {
            // Arrange
            RegisterBody body = AuthTestDataFactory.createRegisterBody();
            body.setUsername(username);

            // Act
            Set<ConstraintViolation<RegisterBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("密码为空 - 应该验证失败")
        void shouldFailValidationWhenPasswordIsBlank(String password) {
            // Arrange
            RegisterBody body = AuthTestDataFactory.createRegisterBody();
            body.setPassword(password);

            // Act
            Set<ConstraintViolation<RegisterBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("userType 为空 - 应该通过验证（userType是可选的）")
        void shouldPassValidationWhenUserTypeIsNull() {
            // Arrange
            RegisterBody body = AuthTestDataFactory.createRegisterBody();
            body.setUserType(null);

            // Act
            Set<ConstraintViolation<RegisterBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("包含中文用户名 - 应该通过验证")
        void shouldPassValidationWithChineseUsername() {
            // Arrange
            RegisterBody body = AuthTestDataFactory.createRegisterBody();
            body.setUsername("测试用户");

            // Act
            Set<ConstraintViolation<RegisterBody>> violations = validator.validate(body);

            // Assert
            assertThat(violations).isEmpty();
        }
    }
}
