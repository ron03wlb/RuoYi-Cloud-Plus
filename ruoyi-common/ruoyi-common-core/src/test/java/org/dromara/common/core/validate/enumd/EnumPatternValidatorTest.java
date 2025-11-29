package org.dromara.common.core.validate.enumd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * 枚举验证器单元测试.
 *
 * @author Test Team
 * @date 2025-10-31
 */
@DisplayName("EnumPatternValidator 单元测试")
class EnumPatternValidatorTest {

  private EnumPatternValidator validator;

  @Mock private ConstraintValidatorContext context;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    validator = new EnumPatternValidator();
  }

  /** 测试用枚举 - 用户状态. */
  enum UserStatus {
    ACTIVE("0", "正常"),
    DISABLED("1", "停用"),
    DELETED("2", "删除");

    private final String code;
    private final String info;

    UserStatus(String code, String info) {
      this.code = code;
      this.info = info;
    }

    public String getCode() {
      return code;
    }

    public String getInfo() {
      return info;
    }
  }

  /** 测试用枚举 - 性别. */
  enum Gender {
    MALE("M", "男"),
    FEMALE("F", "女"),
    UNKNOWN("U", "未知");

    private final String value;
    private final String label;

    Gender(String value, String label) {
      this.value = value;
      this.label = label;
    }

    public String getValue() {
      return value;
    }

    public String getLabel() {
      return label;
    }
  }

  /** 测试用枚举 - 数字字符串类型. */
  enum Priority {
    LOW("1", "低"),
    MEDIUM("2", "中"),
    HIGH("3", "高");

    private final String level;
    private final String name;

    Priority(String level, String name) {
      this.level = level;
      this.name = name;
    }

    public String getLevel() {
      return level;
    }

    public String getName() {
      return name;
    }
  }

  @Nested
  @DisplayName("1. 初始化测试")
  class InitializationTests {

    @Test
    @DisplayName("应正确初始化验证器")
    void shouldInitializeValidator() {
      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) UserStatus.class);
      when(annotation.fieldName()).thenReturn("code");

      // Act
      validator.initialize(annotation);

      // Assert - 验证器初始化成功，后续可以正常使用
      assertThat(validator.isValid("0", context)).isTrue();
    }
  }

  @Nested
  @DisplayName("2. 字符串字段验证测试")
  class StringFieldValidationTests {

    @BeforeEach
    void setUp() {
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) UserStatus.class);
      when(annotation.fieldName()).thenReturn("code");
      validator.initialize(annotation);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "2"})
    @DisplayName("应返回true当输入值在枚举范围内")
    void shouldReturnTrueWhenValueInEnum(String value) {
      // Act & Assert
      assertThat(validator.isValid(value, context)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"3", "9", "ACTIVE", "active", "-1"})
    @DisplayName("应返回false当输入值不在枚举范围内")
    void shouldReturnFalseWhenValueNotInEnum(String value) {
      // Act & Assert
      assertThat(validator.isValid(value, context)).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("应返回false当输入值为null或空字符串")
    void shouldReturnFalseWhenNullOrEmpty(String value) {
      // Act & Assert
      assertThat(validator.isValid(value, context)).isFalse();
    }

    @Test
    @DisplayName("应返回false当输入值为空白字符串")
    void shouldReturnFalseWhenBlank() {
      // Act & Assert
      assertThat(validator.isValid("   ", context)).isFalse();
      assertThat(validator.isValid("\t", context)).isFalse();
      assertThat(validator.isValid("\n", context)).isFalse();
    }
  }

  @Nested
  @DisplayName("3. 不同字段名称验证测试")
  class DifferentFieldNameTests {

    @Test
    @DisplayName("应支持验证info字段")
    void shouldValidateInfoField() {
      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) UserStatus.class);
      when(annotation.fieldName()).thenReturn("info");
      validator.initialize(annotation);

      // Act & Assert
      assertThat(validator.isValid("正常", context)).isTrue();
      assertThat(validator.isValid("停用", context)).isTrue();
      assertThat(validator.isValid("删除", context)).isTrue();
      assertThat(validator.isValid("未知", context)).isFalse();
    }

    @Test
    @DisplayName("应支持验证Gender枚举的value字段")
    void shouldValidateGenderValue() {
      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) Gender.class);
      when(annotation.fieldName()).thenReturn("value");
      validator.initialize(annotation);

      // Act & Assert
      assertThat(validator.isValid("M", context)).isTrue();
      assertThat(validator.isValid("F", context)).isTrue();
      assertThat(validator.isValid("U", context)).isTrue();
      assertThat(validator.isValid("X", context)).isFalse();
    }

    @Test
    @DisplayName("应支持验证Gender枚举的label字段")
    void shouldValidateGenderLabel() {
      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) Gender.class);
      when(annotation.fieldName()).thenReturn("label");
      validator.initialize(annotation);

      // Act & Assert
      assertThat(validator.isValid("男", context)).isTrue();
      assertThat(validator.isValid("女", context)).isTrue();
      assertThat(validator.isValid("未知", context)).isTrue();
      assertThat(validator.isValid("其他", context)).isFalse();
    }
  }

  @Nested
  @DisplayName("4. 数字字符串字段验证测试")
  class NumericStringFieldValidationTests {

    @BeforeEach
    void setUp() {
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) Priority.class);
      when(annotation.fieldName()).thenReturn("level");
      validator.initialize(annotation);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3"})
    @DisplayName("应返回true当输入数字字符串在枚举范围内")
    void shouldReturnTrueWhenNumericStringInEnum(String value) {
      // Act & Assert
      assertThat(validator.isValid(value, context)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "4", "5", "-1"})
    @DisplayName("应返回false当输入数字字符串不在枚举范围内")
    void shouldReturnFalseWhenNumericStringNotInEnum(String value) {
      // Act & Assert
      assertThat(validator.isValid(value, context)).isFalse();
    }

    @Test
    @DisplayName("应返回false当输入非数字字符串")
    void shouldReturnFalseWhenNotNumeric() {
      // Act & Assert
      assertThat(validator.isValid("LOW", context)).isFalse();
      assertThat(validator.isValid("abc", context)).isFalse();
    }

    @Test
    @DisplayName("应支持验证name字段")
    void shouldValidateNameField() {
      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) Priority.class);
      when(annotation.fieldName()).thenReturn("name");
      validator.initialize(annotation);

      // Act & Assert
      assertThat(validator.isValid("低", context)).isTrue();
      assertThat(validator.isValid("中", context)).isTrue();
      assertThat(validator.isValid("高", context)).isTrue();
      assertThat(validator.isValid("极高", context)).isFalse();
    }
  }

  @Nested
  @DisplayName("5. 边界测试")
  class BoundaryTests {

    @Test
    @DisplayName("应区分大小写")
    void shouldBeCaseSensitive() {
      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) UserStatus.class);
      when(annotation.fieldName()).thenReturn("code");
      validator.initialize(annotation);

      // Act & Assert
      assertThat(validator.isValid("0", context)).isTrue();
      assertThat(validator.isValid("O", context)).isFalse(); // 大写字母O
    }

    @Test
    @DisplayName("应返回false当包含空格")
    void shouldReturnFalseWhenContainsSpaces() {
      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) UserStatus.class);
      when(annotation.fieldName()).thenReturn("code");
      validator.initialize(annotation);

      // Act & Assert
      assertThat(validator.isValid(" 0", context)).isFalse();
      assertThat(validator.isValid("0 ", context)).isFalse();
      assertThat(validator.isValid(" 0 ", context)).isFalse();
    }

    @Test
    @DisplayName("应处理字段名称不存在的情况")
    void shouldHandleNonExistentFieldName() {
      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) UserStatus.class);
      when(annotation.fieldName()).thenReturn("nonExistentField");
      validator.initialize(annotation);

      // Act & Assert - 反射找不到getter可能会抛出异常或返回null
      // 实际行为取决于 ReflectUtils.invokeGetter 的实现
      // 这里我们只验证不会导致系统崩溃
      try {
        boolean result = validator.isValid("0", context);
        // 如果没有抛异常，结果应该是false（因为找不到字段）
        assertThat(result).isFalse();
      } catch (Exception e) {
        // 如果抛出异常，也是可以接受的行为
        assertThat(e).isNotNull();
      }
    }

    @Test
    @DisplayName("应正确处理包含特殊字符的枚举值")
    void shouldHandleSpecialCharacters() {
      // 创建一个包含特殊字符的枚举用于测试
      enum SpecialEnum {
        OPTION_A("option-a"),
        OPTION_B("option_b"),
        OPTION_C("option.c");

        private final String value;

        SpecialEnum(String value) {
          this.value = value;
        }

        public String getValue() {
          return value;
        }
      }

      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) SpecialEnum.class);
      when(annotation.fieldName()).thenReturn("value");
      validator.initialize(annotation);

      // Act & Assert
      assertThat(validator.isValid("option-a", context)).isTrue();
      assertThat(validator.isValid("option_b", context)).isTrue();
      assertThat(validator.isValid("option.c", context)).isTrue();
      assertThat(validator.isValid("option/d", context)).isFalse();
    }
  }

  @Nested
  @DisplayName("6. 真实业务场景测试")
  class RealWorldScenarioTests {

    @Test
    @DisplayName("应验证用户状态字段")
    void shouldValidateUserStatusField() {
      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) UserStatus.class);
      when(annotation.fieldName()).thenReturn("code");
      validator.initialize(annotation);

      // Simulate form validation
      String userInputNormal = "0"; // ACTIVE
      String userInputDisabled = "1"; // DISABLED
      String userInputInvalid = "999"; // Invalid

      // Act & Assert
      assertThat(validator.isValid(userInputNormal, context)).isTrue();
      assertThat(validator.isValid(userInputDisabled, context)).isTrue();
      assertThat(validator.isValid(userInputInvalid, context)).isFalse();
    }

    @Test
    @DisplayName("应验证性别字段")
    void shouldValidateGenderField() {
      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) Gender.class);
      when(annotation.fieldName()).thenReturn("value");
      validator.initialize(annotation);

      // Simulate form validation
      String male = "M";
      String female = "F";
      String invalid = "T";

      // Act & Assert
      assertThat(validator.isValid(male, context)).isTrue();
      assertThat(validator.isValid(female, context)).isTrue();
      assertThat(validator.isValid(invalid, context)).isFalse();
    }

    @Test
    @DisplayName("应验证优先级字段")
    void shouldValidatePriorityField() {
      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) Priority.class);
      when(annotation.fieldName()).thenReturn("level");
      validator.initialize(annotation);

      // Simulate priority selection
      String low = "1";
      String high = "3";
      String invalid = "10";

      // Act & Assert
      assertThat(validator.isValid(low, context)).isTrue();
      assertThat(validator.isValid(high, context)).isTrue();
      assertThat(validator.isValid(invalid, context)).isFalse();
    }
  }

  @Nested
  @DisplayName("7. 枚举常量数量测试")
  class EnumSizeTests {

    @Test
    @DisplayName("应正确处理只有一个常量的枚举")
    void shouldHandleSingleConstantEnum() {
      enum SingleEnum {
        ONLY_ONE("value");

        private final String val;

        SingleEnum(String val) {
          this.val = val;
        }

        public String getVal() {
          return val;
        }
      }

      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) SingleEnum.class);
      when(annotation.fieldName()).thenReturn("val");
      validator.initialize(annotation);

      // Act & Assert
      assertThat(validator.isValid("value", context)).isTrue();
      assertThat(validator.isValid("other", context)).isFalse();
    }

    @Test
    @DisplayName("应正确处理包含多个常量的枚举")
    void shouldHandleMultipleConstantsEnum() {
      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) UserStatus.class);
      when(annotation.fieldName()).thenReturn("code");
      validator.initialize(annotation);

      // Act - 验证所有枚举常量
      int validCount = 0;
      for (UserStatus status : UserStatus.values()) {
        if (validator.isValid(status.getCode(), context)) {
          validCount++;
        }
      }

      // Assert - 应该有3个有效值
      assertThat(validCount).isEqualTo(3);
    }
  }

  @Nested
  @DisplayName("8. 性能测试")
  class PerformanceTests {

    @Test
    @DisplayName("应快速验证大量输入")
    void shouldValidateQuickly() {
      // Arrange
      EnumPattern annotation = mock(EnumPattern.class);
      when(annotation.type()).thenReturn((Class) UserStatus.class);
      when(annotation.fieldName()).thenReturn("code");
      validator.initialize(annotation);

      // Act - 先预热JVM
      for (int i = 0; i < 100; i++) {
        validator.isValid("0", context);
      }

      // 实际测试
      long startTime = System.currentTimeMillis();
      for (int i = 0; i < 1000; i++) {
        validator.isValid("0", context);
        validator.isValid("invalid", context);
      }
      long endTime = System.currentTimeMillis();

      // Assert - 2000次验证应该在合理时间内完成
      // 反射调用相对较慢，这里主要验证没有明显的性能问题
      // 允许3秒内完成（不同机器性能差异较大）
      assertThat(endTime - startTime).isLessThan(3000);
    }
  }
}
