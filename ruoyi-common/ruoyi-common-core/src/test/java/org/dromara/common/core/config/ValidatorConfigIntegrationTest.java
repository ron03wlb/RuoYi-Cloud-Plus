package org.dromara.common.core.config;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.*;
import java.util.Set;
import org.dromara.common.core.BaseIntegrationTest;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * ValidatorConfig 集成测试类
 *
 * <p>测试验证器配置的 Bean 创建、快速失败模式、国际化消息、分组验证等功能。
 *
 * @author Test Team
 */
@DisplayName("ValidatorConfig 集成测试")
class ValidatorConfigIntegrationTest extends BaseIntegrationTest {

    @Autowired private ApplicationContext applicationContext;

    @Autowired private Validator validator;

    /** 测试用户类 - 用于验证功能测试 */
    static class TestUser {
        @NotBlank(
                message = "用户名不能为空",
                groups = {AddGroup.class, EditGroup.class})
        @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间", groups = AddGroup.class)
        private String username;

        @NotBlank(message = "密码不能为空", groups = AddGroup.class)
        @Size(min = 6, message = "密码长度不能少于6位", groups = AddGroup.class)
        private String password;

        @Email(message = "邮箱格式不正确")
        private String email;

        @Min(value = 18, message = "年龄不能小于18岁")
        @Max(value = 120, message = "年龄不能大于120岁")
        private Integer age;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    @Nested
    @DisplayName("Bean 创建测试")
    class BeanCreationTest {

        @Test
        @DisplayName("应该成功创建 Validator Bean")
        void shouldCreateValidatorBean() {
            assertThat(validator).isNotNull();
        }

        @Test
        @DisplayName("应该能够从 ApplicationContext 获取 Validator")
        void shouldGetValidatorFromApplicationContext() {
            Validator bean = applicationContext.getBean(Validator.class);

            assertThat(bean).isNotNull();
            assertThat(bean).isSameAs(validator);
        }

        @Test
        @DisplayName("Validator 应该是单例")
        void shouldBeSingleton() {
            Validator bean1 = applicationContext.getBean(Validator.class);
            Validator bean2 = applicationContext.getBean(Validator.class);

            assertThat(bean1).isSameAs(bean2);
        }

        @Test
        @DisplayName("应该使用 HibernateValidator 实现")
        void shouldUseHibernateValidatorImplementation() {
            // 验证器类名应该包含 hibernate
            String validatorClassName = validator.getClass().getName();
            assertThat(validatorClassName).containsIgnoringCase("hibernate");
        }
    }

    @Nested
    @DisplayName("快速失败模式测试")
    class FailFastModeTest {

        @Test
        @DisplayName("快速失败模式：应该只返回第一个验证错误")
        void shouldReturnOnlyFirstValidationError() {
            TestUser user = new TestUser();
            user.setUsername(""); // 违反 @NotBlank
            user.setPassword(""); // 违反 @NotBlank
            user.setEmail("invalid"); // 违反 @Email
            user.setAge(10); // 违反 @Min(18)

            Set<ConstraintViolation<TestUser>> violations =
                    validator.validate(user, AddGroup.class);

            // 快速失败模式下,应该只返回1个错误（第一个遇到的错误）
            assertThat(violations).hasSize(1);
        }

        @Test
        @DisplayName("快速失败模式：应该在遇到第一个错误时停止验证")
        void shouldStopValidationOnFirstError() {
            TestUser user = new TestUser();
            // 设置多个违反约束的字段
            user.setUsername("a"); // 长度不足
            user.setPassword("123"); // 长度不足
            user.setAge(200); // 超过最大值

            Set<ConstraintViolation<TestUser>> violations =
                    validator.validate(user, AddGroup.class);

            // 快速失败：只返回第一个错误
            assertThat(violations).hasSize(1);
        }
    }

    @Nested
    @DisplayName("基本验证功能测试")
    class BasicValidationTest {

        @Test
        @DisplayName("应该验证通过当所有字段都有效")
        void shouldPassValidationWhenAllFieldsValid() {
            TestUser user = new TestUser();
            user.setUsername("testuser");
            user.setPassword("password123");
            user.setEmail("test@example.com");
            user.setAge(25);

            Set<ConstraintViolation<TestUser>> violations = validator.validate(user);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("应该验证失败当字段为空")
        void shouldFailValidationWhenFieldIsBlank() {
            TestUser user = new TestUser();
            user.setUsername(null); // 使用 null 来触发 @NotBlank,避免同时触发 @Size
            user.setPassword("validPassword123"); // 提供有效密码,确保只有用户名验证失败

            Set<ConstraintViolation<TestUser>> violations =
                    validator.validate(user, AddGroup.class);

            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getMessage()).isEqualTo("用户名不能为空");
        }

        @Test
        @DisplayName("应该验证失败当字段长度不符合要求")
        void shouldFailValidationWhenFieldLengthInvalid() {
            TestUser user = new TestUser();
            user.setUsername("ab"); // 长度小于3

            Set<ConstraintViolation<TestUser>> violations =
                    validator.validate(user, AddGroup.class);

            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("应该验证失败当邮箱格式不正确")
        void shouldFailValidationWhenEmailFormatInvalid() {
            TestUser user = new TestUser();
            user.setUsername("testuser");
            user.setPassword("password123");
            user.setEmail("invalid-email");

            Set<ConstraintViolation<TestUser>> violations = validator.validate(user);

            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getMessage()).isEqualTo("邮箱格式不正确");
        }

        @Test
        @DisplayName("应该验证失败当数值超出范围")
        void shouldFailValidationWhenNumberOutOfRange() {
            TestUser user = new TestUser();
            user.setUsername("testuser");
            user.setPassword("password123");
            user.setAge(10); // 小于最小值18

            Set<ConstraintViolation<TestUser>> violations = validator.validate(user);

            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getMessage()).isEqualTo("年龄不能小于18岁");
        }
    }

    @Nested
    @DisplayName("分组验证测试")
    class GroupValidationTest {

        @Test
        @DisplayName("应该只验证 AddGroup 组的约束")
        void shouldValidateOnlyAddGroupConstraints() {
            TestUser user = new TestUser();
            user.setUsername("ab"); // 违反 AddGroup 的 @Size 约束

            Set<ConstraintViolation<TestUser>> violations =
                    validator.validate(user, AddGroup.class);

            assertThat(violations).isNotEmpty();
            // 应该检测到用户名长度错误
        }

        @Test
        @DisplayName("应该只验证 EditGroup 组的约束")
        void shouldValidateOnlyEditGroupConstraints() {
            TestUser user = new TestUser();
            user.setUsername(""); // 违反 EditGroup 的 @NotBlank 约束
            // EditGroup 不包含 @Size 约束,所以不会验证长度

            Set<ConstraintViolation<TestUser>> violations =
                    validator.validate(user, EditGroup.class);

            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getMessage()).isEqualTo("用户名不能为空");
        }

        @Test
        @DisplayName("不指定分组时应该验证默认分组")
        void shouldValidateDefaultGroupWhenNoGroupSpecified() {
            TestUser user = new TestUser();
            user.setEmail("invalid-email");

            Set<ConstraintViolation<TestUser>> violations = validator.validate(user);

            assertThat(violations).isNotEmpty();
            // email 约束没有指定分组,属于默认分组
        }

        @Test
        @DisplayName("应该能够验证多个分组")
        void shouldValidateMultipleGroups() {
            TestUser user = new TestUser();
            user.setUsername(""); // 违反两个分组的约束

            Set<ConstraintViolation<TestUser>> violations =
                    validator.validate(user, AddGroup.class, EditGroup.class);

            assertThat(violations).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("属性验证测试")
    class PropertyValidationTest {

        @Test
        @DisplayName("应该能够验证单个属性")
        void shouldValidateSingleProperty() {
            TestUser user = new TestUser();
            user.setEmail("invalid-email");

            Set<ConstraintViolation<TestUser>> violations =
                    validator.validateProperty(user, "email");

            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getPropertyPath().toString())
                    .isEqualTo("email");
        }

        @Test
        @DisplayName("应该能够验证属性值")
        void shouldValidatePropertyValue() {
            Set<ConstraintViolation<TestUser>> violations =
                    validator.validateValue(TestUser.class, "email", "invalid-email");

            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("单个属性验证通过时不应该有错误")
        void shouldHaveNoErrorsWhenPropertyValid() {
            TestUser user = new TestUser();
            user.setEmail("valid@example.com");

            Set<ConstraintViolation<TestUser>> violations =
                    validator.validateProperty(user, "email");

            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("国际化消息测试")
    class InternationalizationTest {

        @Test
        @DisplayName("验证消息应该使用配置的 MessageSource")
        void shouldUseConfiguredMessageSource() {
            TestUser user = new TestUser();
            user.setUsername(null); // 使用 null 来触发 @NotBlank,避免同时触发 @Size
            user.setPassword("validPassword123"); // 提供有效密码

            Set<ConstraintViolation<TestUser>> violations =
                    validator.validate(user, AddGroup.class);

            assertThat(violations).isNotEmpty();
            // 验证消息应该是中文（来自我们的 messages.properties）
            String message = violations.iterator().next().getMessage();
            assertThat(message).isEqualTo("用户名不能为空");
        }

        @Test
        @DisplayName("应该支持自定义验证消息")
        void shouldSupportCustomValidationMessages() {
            TestUser user = new TestUser();
            user.setAge(10);

            Set<ConstraintViolation<TestUser>> violations = validator.validate(user);

            assertThat(violations).isNotEmpty();
            String message = violations.iterator().next().getMessage();
            assertThat(message).isEqualTo("年龄不能小于18岁");
        }
    }

    @Nested
    @DisplayName("真实业务场景测试")
    class RealWorldScenarioTest {

        @Test
        @DisplayName("用户注册场景：应该验证必填字段")
        void shouldValidateRequiredFieldsInUserRegistration() {
            // 模拟用户注册,缺少必填字段
            TestUser newUser = new TestUser();
            newUser.setEmail("test@example.com");
            newUser.setAge(25);

            Set<ConstraintViolation<TestUser>> violations =
                    validator.validate(newUser, AddGroup.class);

            assertThat(violations).isNotEmpty();
            // 应该检测到用户名和密码缺失（快速失败,只返回第一个）
        }

        @Test
        @DisplayName("用户编辑场景：应该验证部分字段")
        void shouldValidatePartialFieldsInUserEdit() {
            // 模拟用户编辑,只验证EditGroup字段
            TestUser existingUser = new TestUser();
            existingUser.setUsername(""); // EditGroup 要求不能为空

            Set<ConstraintViolation<TestUser>> violations =
                    validator.validate(existingUser, EditGroup.class);

            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("表单提交场景：应该在第一个错误处停止验证")
        void shouldStopAtFirstErrorInFormSubmission() {
            // 模拟表单提交,多个字段都有错误
            TestUser formData = new TestUser();
            formData.setUsername("a");
            formData.setPassword("123");
            formData.setEmail("invalid");
            formData.setAge(150);

            Set<ConstraintViolation<TestUser>> violations =
                    validator.validate(formData, AddGroup.class);

            // 快速失败模式：只返回第一个错误
            assertThat(violations).hasSize(1);
            // 用户体验更好：立即看到第一个错误,修复后再显示下一个
        }

        @Test
        @DisplayName("API参数验证场景：应该验证邮箱格式")
        void shouldValidateEmailFormatInApiParameter() {
            TestUser apiRequest = new TestUser();
            apiRequest.setUsername("testuser");
            apiRequest.setPassword("password123");
            apiRequest.setEmail("not-an-email");

            Set<ConstraintViolation<TestUser>> violations = validator.validate(apiRequest);

            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getMessage()).contains("邮箱");
        }
    }
}
