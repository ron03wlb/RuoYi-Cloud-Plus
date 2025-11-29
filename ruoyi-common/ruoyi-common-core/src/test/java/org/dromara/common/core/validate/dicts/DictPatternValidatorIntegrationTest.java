package org.dromara.common.core.validate.dicts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.dromara.common.core.BaseIntegrationTest;
import org.dromara.common.core.service.DictService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * DictPatternValidator 集成测试.
 *
 * @author Test Team
 */
@DisplayName("DictPatternValidator 集成测试")
class DictPatternValidatorIntegrationTest extends BaseIntegrationTest {

  @Autowired private Validator validator;

  @MockBean private DictService dictService;

  /** 测试用的DTO类. */
  static class UserDto {
    @DictPattern(dictType = "sys_user_sex", separator = ",", message = "性别字典值无效")
    private String sex;

    @DictPattern(dictType = "sys_user_status", separator = ",", message = "状态字典值无效")
    private String status;

    public String getSex() {
      return sex;
    }

    public void setSex(String sex) {
      this.sex = sex;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }
  }

  @Nested
  @DisplayName("1. 有效字典值验证测试")
  class ValidDictValueTests {

    @Test
    @DisplayName("应该通过验证 - 当字典值存在于字典中")
    void shouldPassValidation_WhenDictValueExists() {
      // Arrange
      when(dictService.getDictLabel("sys_user_sex", "0", ",")).thenReturn("男");

      UserDto user = new UserDto();
      user.setSex("0");

      // Act
      Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

      // Assert
      assertThat(violations)
          .filteredOn(v -> v.getPropertyPath().toString().equals("sex"))
          .isEmpty();
      verify(dictService).getDictLabel("sys_user_sex", "0", ",");
    }

    @Test
    @DisplayName("应该通过验证 - 多个字典值都存在")
    void shouldPassValidation_WhenAllDictValuesExist() {
      // Arrange
      when(dictService.getDictLabel("sys_user_sex", "1", ",")).thenReturn("女");
      when(dictService.getDictLabel("sys_user_status", "0", ",")).thenReturn("正常");

      UserDto user = new UserDto();
      user.setSex("1");
      user.setStatus("0");

      // Act
      Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

      // Assert
      assertThat(violations).isEmpty();
      verify(dictService).getDictLabel("sys_user_sex", "1", ",");
      verify(dictService).getDictLabel("sys_user_status", "0", ",");
    }

    @Test
    @DisplayName("应该通过验证 - 字典值为数字字符串")
    void shouldPassValidation_WhenDictValueIsNumericString() {
      // Arrange
      when(dictService.getDictLabel("sys_user_sex", "0", ",")).thenReturn("男");
      when(dictService.getDictLabel("sys_user_status", "1", ",")).thenReturn("停用");

      UserDto user = new UserDto();
      user.setSex("0"); // 设置 sex 字段避免 null
      user.setStatus("1");

      // Act
      Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

      // Assert
      assertThat(violations)
          .filteredOn(v -> v.getPropertyPath().toString().equals("status"))
          .isEmpty();
      verify(dictService).getDictLabel("sys_user_status", "1", ",");
    }
  }

  @Nested
  @DisplayName("2. 无效字典值验证测试")
  class InvalidDictValueTests {

    @Test
    @DisplayName("应该验证失败 - 当字典值不存在于字典中")
    void shouldFailValidation_WhenDictValueNotExists() {
      // Arrange
      when(dictService.getDictLabel("sys_user_sex", "99", ",")).thenReturn(""); // 返回空字符串表示字典值不存在

      UserDto user = new UserDto();
      user.setSex("99");

      // Act
      Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

      // Assert
      assertThat(violations)
          .filteredOn(v -> v.getPropertyPath().toString().equals("sex"))
          .hasSize(1)
          .first()
          .satisfies(
              v -> {
                assertThat(v.getMessage()).isEqualTo("性别字典值无效");
                assertThat(v.getInvalidValue()).isEqualTo("99");
              });
      verify(dictService).getDictLabel("sys_user_sex", "99", ",");
    }

    @Test
    @DisplayName("应该验证失败 - 当字典服务返回null")
    void shouldFailValidation_WhenDictServiceReturnsNull() {
      // Arrange
      when(dictService.getDictLabel("sys_user_sex", "invalid", ",")).thenReturn(null);

      UserDto user = new UserDto();
      user.setSex("invalid");

      // Act
      Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

      // Assert
      assertThat(violations)
          .filteredOn(v -> v.getPropertyPath().toString().equals("sex"))
          .hasSize(1)
          .first()
          .satisfies(v -> assertThat(v.getMessage()).isEqualTo("性别字典值无效"));
      verify(dictService).getDictLabel("sys_user_sex", "invalid", ",");
    }

    @Test
    @DisplayName("应该验证失败 - 当字典值为空字符串")
    void shouldFailValidation_WhenDictValueIsEmpty() {
      // Arrange
      UserDto user = new UserDto();
      user.setSex("");

      // Act
      Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

      // Assert
      assertThat(violations)
          .filteredOn(v -> v.getPropertyPath().toString().equals("sex"))
          .hasSize(1)
          .first()
          .satisfies(v -> assertThat(v.getMessage()).isEqualTo("性别字典值无效"));
      verifyNoInteractions(dictService);
    }

    @Test
    @DisplayName("应该验证失败 - 当字典值为null")
    void shouldFailValidation_WhenDictValueIsNull() {
      // Arrange
      UserDto user = new UserDto();
      user.setSex(null);
      // 不设置 status，让它也为 null，这样两个字段都会验证失败

      // Act
      Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

      // Assert
      // null 值会触发验证失败（DictPatternValidator.isValid 返回 false）
      assertThat(violations)
          .filteredOn(v -> v.getPropertyPath().toString().equals("sex"))
          .hasSize(1)
          .first()
          .satisfies(v -> assertThat(v.getMessage()).isEqualTo("性别字典值无效"));
      // DictService 不会被调用，因为 isBlank 检查在 DictService 调用之前
      verifyNoInteractions(dictService);
    }

    @Test
    @DisplayName("应该验证失败 - 当字典值只包含空格")
    void shouldFailValidation_WhenDictValueContainsOnlySpaces() {
      // Arrange
      UserDto user = new UserDto();
      user.setSex("   ");

      // Act
      Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

      // Assert
      assertThat(violations)
          .filteredOn(v -> v.getPropertyPath().toString().equals("sex"))
          .hasSize(1);
      verifyNoInteractions(dictService);
    }
  }

  @Nested
  @DisplayName("3. 自定义分隔符测试")
  class CustomSeparatorTests {

    static class ProductDto {
      @DictPattern(dictType = "product_category", separator = ";", message = "分类字典值无效")
      private String category;

      public String getCategory() {
        return category;
      }

      public void setCategory(String category) {
        this.category = category;
      }
    }

    @Test
    @DisplayName("应该使用自定义分隔符进行验证")
    void shouldUseCustomSeparator() {
      // Arrange
      when(dictService.getDictLabel("product_category", "electronics", ";")).thenReturn("电子产品");

      ProductDto product = new ProductDto();
      product.setCategory("electronics");

      // Act
      Set<ConstraintViolation<ProductDto>> violations = validator.validate(product);

      // Assert
      assertThat(violations).isEmpty();
      verify(dictService).getDictLabel("product_category", "electronics", ";");
    }
  }

  @Nested
  @DisplayName("4. 真实业务场景测试")
  class RealBusinessScenarioTests {

    @Test
    @DisplayName("用户性别字典验证")
    void shouldValidateUserSex() {
      // Arrange
      when(dictService.getDictLabel("sys_user_sex", "0", ",")).thenReturn("男");
      when(dictService.getDictLabel("sys_user_sex", "1", ",")).thenReturn("女");
      when(dictService.getDictLabel("sys_user_sex", "2", ",")).thenReturn("未知");

      // Test valid values
      for (String sex : new String[] {"0", "1", "2"}) {
        UserDto user = new UserDto();
        user.setSex(sex);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

        assertThat(violations)
            .filteredOn(v -> v.getPropertyPath().toString().equals("sex"))
            .isEmpty();
      }

      verify(dictService, times(3)).getDictLabel(eq("sys_user_sex"), anyString(), eq(","));
    }

    @Test
    @DisplayName("用户状态字典验证")
    void shouldValidateUserStatus() {
      // Arrange
      when(dictService.getDictLabel("sys_user_sex", "0", ",")).thenReturn("男");
      when(dictService.getDictLabel("sys_user_status", "0", ",")).thenReturn("正常");
      when(dictService.getDictLabel("sys_user_status", "1", ",")).thenReturn("停用");

      // Test valid status
      UserDto user1 = new UserDto();
      user1.setSex("0"); // 设置 sex 避免 null 验证失败
      user1.setStatus("0");
      assertThat(validator.validate(user1))
          .filteredOn(v -> v.getPropertyPath().toString().equals("status"))
          .isEmpty();

      // Test invalid status
      when(dictService.getDictLabel("sys_user_status", "99", ",")).thenReturn("");
      UserDto user2 = new UserDto();
      user2.setSex("0"); // 设置 sex 避免 null 验证失败
      user2.setStatus("99");
      assertThat(validator.validate(user2))
          .filteredOn(v -> v.getPropertyPath().toString().equals("status"))
          .hasSize(1);
    }
  }

  @Nested
  @DisplayName("5. 边界测试")
  class BoundaryTests {

    @Test
    @DisplayName("应该正确处理特殊字符字典值")
    void shouldHandleSpecialCharacters() {
      // Arrange
      when(dictService.getDictLabel("sys_user_sex", "A-1", ",")).thenReturn("特殊-1");

      UserDto user = new UserDto();
      user.setSex("A-1");

      // Act
      Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

      // Assert
      assertThat(violations)
          .filteredOn(v -> v.getPropertyPath().toString().equals("sex"))
          .isEmpty();
      verify(dictService).getDictLabel("sys_user_sex", "A-1", ",");
    }

    @Test
    @DisplayName("应该正确处理中文字典值")
    void shouldHandleChineseCharacters() {
      // Arrange
      when(dictService.getDictLabel("sys_user_sex", "男", ",")).thenReturn("男性");

      UserDto user = new UserDto();
      user.setSex("男");

      // Act
      Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

      // Assert
      assertThat(violations)
          .filteredOn(v -> v.getPropertyPath().toString().equals("sex"))
          .isEmpty();
      verify(dictService).getDictLabel("sys_user_sex", "男", ",");
    }

    @Test
    @DisplayName("应该正确处理超长字典值")
    void shouldHandleLongDictValue() {
      // Arrange
      String longValue = "x".repeat(100);
      when(dictService.getDictLabel("sys_user_sex", longValue, ",")).thenReturn("");

      UserDto user = new UserDto();
      user.setSex(longValue);

      // Act
      Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

      // Assert
      assertThat(violations)
          .filteredOn(v -> v.getPropertyPath().toString().equals("sex"))
          .hasSize(1);
      verify(dictService).getDictLabel("sys_user_sex", longValue, ",");
    }
  }

  @Nested
  @DisplayName("6. 注解字段测试")
  class AnnotationFieldTests {

    @Test
    @DisplayName("应该正确读取dictType字段")
    void shouldReadDictTypeField() throws NoSuchFieldException {
      // Arrange
      DictPattern annotation =
          UserDto.class.getDeclaredField("sex").getAnnotation(DictPattern.class);

      // Assert
      assertThat(annotation).isNotNull();
      assertThat(annotation.dictType()).isEqualTo("sys_user_sex");
    }

    @Test
    @DisplayName("应该正确读取separator字段")
    void shouldReadSeparatorField() throws NoSuchFieldException {
      // Arrange
      DictPattern annotation =
          UserDto.class.getDeclaredField("sex").getAnnotation(DictPattern.class);

      // Assert
      assertThat(annotation).isNotNull();
      assertThat(annotation.separator()).isEqualTo(",");
    }

    @Test
    @DisplayName("应该正确读取message字段")
    void shouldReadMessageField() throws NoSuchFieldException {
      // Arrange
      DictPattern annotation =
          UserDto.class.getDeclaredField("sex").getAnnotation(DictPattern.class);

      // Assert
      assertThat(annotation).isNotNull();
      assertThat(annotation.message()).isEqualTo("性别字典值无效");
    }
  }

  @Nested
  @DisplayName("7. 边界分支覆盖测试")
  class BranchCoverageTests {

    /** 测试用DTO - separator为空字符串. */
    static class EmptySeparatorDto {
      @DictPattern(dictType = "sys_user_sex", separator = "", message = "字典值无效")
      private String field;

      public String getField() {
        return field;
      }

      public void setField(String field) {
        this.field = field;
      }
    }

    /** 测试用DTO - separator为空白字符串. */
    static class BlankSeparatorDto {
      @DictPattern(dictType = "sys_user_sex", separator = "   ", message = "字典值无效")
      private String field;

      public String getField() {
        return field;
      }

      public void setField(String field) {
        this.field = field;
      }
    }

    /** 测试用DTO - dictType为空字符串. */
    static class EmptyDictTypeDto {
      @DictPattern(dictType = "", separator = ",", message = "字典值无效")
      private String field;

      public String getField() {
        return field;
      }

      public void setField(String field) {
        this.field = field;
      }
    }

    /** 测试用DTO - dictType为空白字符串. */
    static class BlankDictTypeDto {
      @DictPattern(dictType = "   ", separator = ",", message = "字典值无效")
      private String field;

      public String getField() {
        return field;
      }

      public void setField(String field) {
        this.field = field;
      }
    }

    @Test
    @DisplayName("应该使用默认分隔符 - 当separator为空字符串")
    void shouldUseDefaultSeparator_WhenSeparatorIsEmpty() {
      // Arrange
      // 当 separator 为空时，应该使用默认的 "," 分隔符
      when(dictService.getDictLabel("sys_user_sex", "0", ",")).thenReturn("男");

      EmptySeparatorDto dto = new EmptySeparatorDto();
      dto.setField("0");

      // Act
      Set<ConstraintViolation<EmptySeparatorDto>> violations = validator.validate(dto);

      // Assert
      assertThat(violations).isEmpty();
      // 验证使用了默认分隔符 ","
      verify(dictService).getDictLabel("sys_user_sex", "0", ",");
    }

    @Test
    @DisplayName("应该使用默认分隔符 - 当separator为空白字符串")
    void shouldUseDefaultSeparator_WhenSeparatorIsBlank() {
      // Arrange
      when(dictService.getDictLabel("sys_user_sex", "1", ",")).thenReturn("女");

      BlankSeparatorDto dto = new BlankSeparatorDto();
      dto.setField("1");

      // Act
      Set<ConstraintViolation<BlankSeparatorDto>> violations = validator.validate(dto);

      // Assert
      assertThat(violations).isEmpty();
      // 验证使用了默认分隔符 ","
      verify(dictService).getDictLabel("sys_user_sex", "1", ",");
    }

    @Test
    @DisplayName("应该验证失败 - 当dictType为空字符串")
    void shouldFailValidation_WhenDictTypeIsEmpty() {
      // Arrange
      EmptyDictTypeDto dto = new EmptyDictTypeDto();
      dto.setField("0");

      // Act
      Set<ConstraintViolation<EmptyDictTypeDto>> violations = validator.validate(dto);

      // Assert
      assertThat(violations).hasSize(1);
      assertThat(violations).first().satisfies(v -> assertThat(v.getMessage()).isEqualTo("字典值无效"));
      // DictService 不应该被调用
      verifyNoInteractions(dictService);
    }

    @Test
    @DisplayName("应该验证失败 - 当dictType为空白字符串")
    void shouldFailValidation_WhenDictTypeIsBlank() {
      // Arrange
      BlankDictTypeDto dto = new BlankDictTypeDto();
      dto.setField("1");

      // Act
      Set<ConstraintViolation<BlankDictTypeDto>> violations = validator.validate(dto);

      // Assert
      assertThat(violations).hasSize(1);
      assertThat(violations).first().satisfies(v -> assertThat(v.getMessage()).isEqualTo("字典值无效"));
      // DictService 不应该被调用
      verifyNoInteractions(dictService);
    }
  }
}
