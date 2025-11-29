package org.dromara.common.core.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.dromara.common.core.BaseIntegrationTest;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ValidatorUtils 集成测试.
 *
 * @author Test Team
 */
@DisplayName("ValidatorUtils 集成测试")
class ValidatorUtilsIntegrationTest extends BaseIntegrationTest {

  /** 简单用户测试对象. */
  static class SimpleUser {
    @NotNull(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
    private String username;

    @NotNull(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50之间")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Min(value = 0, message = "年龄不能小于0")
    @Max(value = 150, message = "年龄不能大于150")
    private Integer age;

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

  /** 带分组校验的用户对象. */
  static class GroupUser {
    @NotNull(
        groups = {AddGroup.class, EditGroup.class},
        message = "用户名不能为空")
    private String username;

    @NotNull(groups = AddGroup.class, message = "密码不能为空")
    private String password;

    @NotNull(groups = EditGroup.class, message = "ID不能为空")
    private Long id;

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

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }
  }

  /** 嵌套校验对象. */
  static class NestedUser {
    @NotNull(message = "用户名不能为空")
    private String username;

    @Valid
    @NotNull(message = "地址不能为空")
    private Address address;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public Address getAddress() {
      return address;
    }

    public void setAddress(Address address) {
      this.address = address;
    }
  }

  static class Address {
    @NotNull(message = "城市不能为空")
    private String city;

    @NotNull(message = "街道不能为空")
    private String street;

    public String getCity() {
      return city;
    }

    public void setCity(String city) {
      this.city = city;
    }

    public String getStreet() {
      return street;
    }

    public void setStreet(String street) {
      this.street = street;
    }
  }

  /** 集合校验对象. */
  static class UserWithRoles {
    @NotNull(message = "用户名不能为空")
    private String username;

    @NotEmpty(message = "角色列表不能为空")
    @Size(min = 1, max = 10, message = "角色数量必须在1-10之间")
    private List<String> roles;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public List<String> getRoles() {
      return roles;
    }

    public void setRoles(List<String> roles) {
      this.roles = roles;
    }
  }

  /** 辅助方法：从异常中提取约束违规消息. */
  private Set<String> extractViolationMessages(ConstraintViolationException exception) {
    return exception.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toSet());
  }

  @Nested
  @DisplayName("基本校验测试")
  class BasicValidationTest {

    @Test
    @DisplayName("校验通过 - 所有字段有效")
    void shouldPassValidationWhenAllFieldsValid() {
      // Arrange
      SimpleUser user = new SimpleUser();
      user.setUsername("testuser");
      user.setPassword("password123");
      user.setEmail("test@example.com");
      user.setAge(25);

      // Act & Assert
      assertThat(catchThrowable(() -> ValidatorUtils.validate(user))).isNull(); // 校验通过，无异常
    }

    @Test
    @DisplayName("校验失败 - 用户名为 null")
    void shouldFailValidationWhenUsernameIsNull() {
      // Arrange
      SimpleUser user = new SimpleUser();
      user.setPassword("password123");

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(extractViolationMessages(exception)).contains("用户名不能为空");
    }

    @Test
    @DisplayName("校验失败 - 用户名长度不足")
    void shouldFailValidationWhenUsernameTooShort() {
      // Arrange
      SimpleUser user = new SimpleUser();
      user.setUsername("ab"); // 长度为2，小于最小值3
      user.setPassword("password123");

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(extractViolationMessages(exception)).contains("用户名长度必须在3-20之间");
    }

    @Test
    @DisplayName("校验失败 - 用户名长度过长")
    void shouldFailValidationWhenUsernameTooLong() {
      // Arrange
      SimpleUser user = new SimpleUser();
      user.setUsername("a".repeat(21)); // 长度为21，超过最大值20
      user.setPassword("password123");

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(extractViolationMessages(exception)).contains("用户名长度必须在3-20之间");
    }

    @Test
    @DisplayName("校验失败 - 密码长度不足")
    void shouldFailValidationWhenPasswordTooShort() {
      // Arrange
      SimpleUser user = new SimpleUser();
      user.setUsername("testuser");
      user.setPassword("12345"); // 长度为5，小于最小值6

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(extractViolationMessages(exception)).contains("密码长度必须在6-50之间");
    }

    @Test
    @DisplayName("校验失败 - 邮箱格式错误")
    void shouldFailValidationWhenEmailFormatInvalid() {
      // Arrange
      SimpleUser user = new SimpleUser();
      user.setUsername("testuser");
      user.setPassword("password123");
      user.setEmail("invalid-email"); // 无效邮箱格式

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(extractViolationMessages(exception)).contains("邮箱格式不正确");
    }

    @Test
    @DisplayName("校验失败 - 年龄为负数")
    void shouldFailValidationWhenAgeIsNegative() {
      // Arrange
      SimpleUser user = new SimpleUser();
      user.setUsername("testuser");
      user.setPassword("password123");
      user.setAge(-1); // 负数年龄

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(extractViolationMessages(exception)).contains("年龄不能小于0");
    }

    @Test
    @DisplayName("校验失败 - 年龄超过最大值")
    void shouldFailValidationWhenAgeExceedsMax() {
      // Arrange
      SimpleUser user = new SimpleUser();
      user.setUsername("testuser");
      user.setPassword("password123");
      user.setAge(151); // 超过最大值150

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(extractViolationMessages(exception)).contains("年龄不能大于150");
    }

    @Test
    @DisplayName("校验失败 - 多个字段同时违规")
    void shouldFailValidationWithMultipleViolations() {
      // Arrange
      SimpleUser user = new SimpleUser();
      // username 为 null, password 为 null, email 格式错误, age 为负数
      user.setEmail("invalid");
      user.setAge(-1);

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(exception.getConstraintViolations()).isNotEmpty(); // 至少有违规
      // 注意：不同的验证器实现可能返回不同数量的违规，这里只验证有违规即可
    }
  }

  @Nested
  @DisplayName("分组校验测试")
  class GroupValidationTest {

    @Test
    @DisplayName("添加分组校验 - 校验通过")
    void shouldPassAddGroupValidation() {
      // Arrange
      GroupUser user = new GroupUser();
      user.setUsername("testuser");
      user.setPassword("password123");
      // id 可以为 null，因为 AddGroup 不校验 id

      // Act & Assert
      assertThat(catchThrowable(() -> ValidatorUtils.validate(user, AddGroup.class))).isNull();
    }

    @Test
    @DisplayName("添加分组校验 - 缺少密码失败")
    void shouldFailAddGroupValidationWhenPasswordMissing() {
      // Arrange
      GroupUser user = new GroupUser();
      user.setUsername("testuser");
      // 缺少密码

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user, AddGroup.class));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(extractViolationMessages(exception)).contains("密码不能为空");
    }

    @Test
    @DisplayName("编辑分组校验 - 校验通过")
    void shouldPassEditGroupValidation() {
      // Arrange
      GroupUser user = new GroupUser();
      user.setUsername("testuser");
      user.setId(1L);
      // password 可以为 null，因为 EditGroup 不校验 password

      // Act & Assert
      assertThat(catchThrowable(() -> ValidatorUtils.validate(user, EditGroup.class))).isNull();
    }

    @Test
    @DisplayName("编辑分组校验 - 缺少ID失败")
    void shouldFailEditGroupValidationWhenIdMissing() {
      // Arrange
      GroupUser user = new GroupUser();
      user.setUsername("testuser");
      // 缺少 ID

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user, EditGroup.class));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(extractViolationMessages(exception)).contains("ID不能为空");
    }

    @Test
    @DisplayName("多分组校验 - 同时校验添加和编辑分组")
    void shouldValidateMultipleGroups() {
      // Arrange
      GroupUser user = new GroupUser();
      user.setUsername("testuser");
      // 缺少 password 和 id

      // Act
      Throwable thrown =
          catchThrowable(() -> ValidatorUtils.validate(user, AddGroup.class, EditGroup.class));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(exception.getConstraintViolations()).isNotEmpty(); // 至少有违规
    }

    @Test
    @DisplayName("无分组校验 - 不校验分组约束")
    void shouldNotValidateGroupConstraintsWhenNoGroupSpecified() {
      // Arrange
      GroupUser user = new GroupUser();
      user.setUsername("testuser");
      // 缺少 password 和 id，但无分组校验时这些字段不会被校验

      // Act & Assert
      assertThat(catchThrowable(() -> ValidatorUtils.validate(user))).isNull(); // 无分组时，分组约束不生效
    }
  }

  @Nested
  @DisplayName("嵌套对象校验测试")
  class NestedValidationTest {

    @Test
    @DisplayName("嵌套对象校验通过")
    void shouldPassNestedValidation() {
      // Arrange
      Address address = new Address();
      address.setCity("Beijing");
      address.setStreet("Changan Street");

      NestedUser user = new NestedUser();
      user.setUsername("testuser");
      user.setAddress(address);

      // Act & Assert
      assertThat(catchThrowable(() -> ValidatorUtils.validate(user))).isNull();
    }

    @Test
    @DisplayName("嵌套对象校验失败 - 地址为 null")
    void shouldFailNestedValidationWhenAddressIsNull() {
      // Arrange
      NestedUser user = new NestedUser();
      user.setUsername("testuser");
      user.setAddress(null);

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(extractViolationMessages(exception)).contains("地址不能为空");
    }

    @Test
    @DisplayName("嵌套对象校验失败 - 地址字段缺失")
    void shouldFailNestedValidationWhenAddressFieldsMissing() {
      // Arrange
      Address address = new Address();
      // city 和 street 都为 null

      NestedUser user = new NestedUser();
      user.setUsername("testuser");
      user.setAddress(address);

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(exception.getConstraintViolations()).isNotEmpty(); // 至少有违规
    }
  }

  @Nested
  @DisplayName("集合校验测试")
  class CollectionValidationTest {

    @Test
    @DisplayName("集合校验通过")
    void shouldPassCollectionValidation() {
      // Arrange
      UserWithRoles user = new UserWithRoles();
      user.setUsername("testuser");
      user.setRoles(Arrays.asList("admin", "user"));

      // Act & Assert
      assertThat(catchThrowable(() -> ValidatorUtils.validate(user))).isNull();
    }

    @Test
    @DisplayName("集合校验失败 - 角色列表为 null")
    void shouldFailCollectionValidationWhenRolesIsNull() {
      // Arrange
      UserWithRoles user = new UserWithRoles();
      user.setUsername("testuser");
      user.setRoles(null);

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(extractViolationMessages(exception)).contains("角色列表不能为空");
    }

    @Test
    @DisplayName("集合校验失败 - 角色列表为空")
    void shouldFailCollectionValidationWhenRolesIsEmpty() {
      // Arrange
      UserWithRoles user = new UserWithRoles();
      user.setUsername("testuser");
      user.setRoles(Collections.emptyList());

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(extractViolationMessages(exception)).contains("角色列表不能为空");
    }

    @Test
    @DisplayName("集合校验失败 - 角色数量超过最大值")
    void shouldFailCollectionValidationWhenRolesSizeExceedsMax() {
      // Arrange
      UserWithRoles user = new UserWithRoles();
      user.setUsername("testuser");
      user.setRoles(
          Arrays.asList(
              "role1", "role2", "role3", "role4", "role5", "role6", "role7", "role8", "role9",
              "role10", "role11")); // 11个角色

      // Act
      Throwable thrown = catchThrowable(() -> ValidatorUtils.validate(user));

      // Assert
      assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
      ConstraintViolationException exception = (ConstraintViolationException) thrown;
      assertThat(extractViolationMessages(exception)).contains("角色数量必须在1-10之间");
    }
  }
}
