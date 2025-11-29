package org.dromara.common.tenant.properties;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dromara.common.tenant.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * TenantProperties 测试.
 *
 * <p>测试租户配置属性类
 *
 * @author Test Team
 */
@DisplayName("TenantProperties 测试")
class TenantPropertiesTest extends BaseUnitTest {

  @Nested
  @DisplayName("1. enable属性测试")
  class EnablePropertyTests {

    @Test
    @DisplayName("应该正确设置和获取enable属性为true")
    void shouldSetAndGetEnableTrue() {
      // Arrange
      TenantProperties properties = new TenantProperties();

      // Act
      properties.setEnable(true);

      // Assert
      assertThat(properties.getEnable()).isTrue();
    }

    @Test
    @DisplayName("应该正确设置和获取enable属性为false")
    void shouldSetAndGetEnableFalse() {
      // Arrange
      TenantProperties properties = new TenantProperties();

      // Act
      properties.setEnable(false);

      // Assert
      assertThat(properties.getEnable()).isFalse();
    }

    @Test
    @DisplayName("应该支持null的enable属性")
    void shouldSupportNullEnable() {
      // Arrange
      TenantProperties properties = new TenantProperties();

      // Act
      properties.setEnable(null);

      // Assert
      assertThat(properties.getEnable()).isNull();
    }

    @Test
    @DisplayName("默认enable应该为null")
    void shouldHaveNullEnableByDefault() {
      // Act
      TenantProperties properties = new TenantProperties();

      // Assert
      assertThat(properties.getEnable()).isNull();
    }
  }

  @Nested
  @DisplayName("2. excludes属性测试")
  class ExcludesPropertyTests {

    @Test
    @DisplayName("应该正确设置和获取excludes列表")
    void shouldSetAndGetExcludes() {
      // Arrange
      TenantProperties properties = new TenantProperties();
      List<String> excludes = Arrays.asList("sys_config", "sys_dict_data", "sys_dict_type");

      // Act
      properties.setExcludes(excludes);

      // Assert
      assertThat(properties.getExcludes()).hasSize(3);
      assertThat(properties.getExcludes())
          .containsExactly("sys_config", "sys_dict_data", "sys_dict_type");
    }

    @Test
    @DisplayName("应该支持空列表")
    void shouldSupportEmptyList() {
      // Arrange
      TenantProperties properties = new TenantProperties();
      List<String> excludes = new ArrayList<>();

      // Act
      properties.setExcludes(excludes);

      // Assert
      assertThat(properties.getExcludes()).isEmpty();
    }

    @Test
    @DisplayName("应该支持null的excludes")
    void shouldSupportNullExcludes() {
      // Arrange
      TenantProperties properties = new TenantProperties();

      // Act
      properties.setExcludes(null);

      // Assert
      assertThat(properties.getExcludes()).isNull();
    }

    @Test
    @DisplayName("默认excludes应该为null")
    void shouldHaveNullExcludesByDefault() {
      // Act
      TenantProperties properties = new TenantProperties();

      // Assert
      assertThat(properties.getExcludes()).isNull();
    }

    @Test
    @DisplayName("应该支持单个排除表")
    void shouldSupportSingleExclude() {
      // Arrange
      TenantProperties properties = new TenantProperties();
      List<String> excludes = Arrays.asList("sys_user");

      // Act
      properties.setExcludes(excludes);

      // Assert
      assertThat(properties.getExcludes()).hasSize(1);
      assertThat(properties.getExcludes()).containsExactly("sys_user");
    }

    @Test
    @DisplayName("应该支持多个排除表")
    void shouldSupportMultipleExcludes() {
      // Arrange
      TenantProperties properties = new TenantProperties();
      List<String> excludes =
          Arrays.asList("sys_config", "sys_dict_data", "sys_dict_type", "sys_menu", "sys_role");

      // Act
      properties.setExcludes(excludes);

      // Assert
      assertThat(properties.getExcludes()).hasSize(5);
    }
  }

  @Nested
  @DisplayName("3. 业务场景测试")
  class BusinessScenarioTests {

    @Test
    @DisplayName("应该创建启用租户的配置")
    void shouldCreateEnabledTenantConfig() {
      // Arrange
      TenantProperties properties = new TenantProperties();

      // Act
      properties.setEnable(true);
      properties.setExcludes(Arrays.asList("sys_config", "sys_dict_data"));

      // Assert
      assertThat(properties.getEnable()).isTrue();
      assertThat(properties.getExcludes()).hasSize(2);
    }

    @Test
    @DisplayName("应该创建禁用租户的配置")
    void shouldCreateDisabledTenantConfig() {
      // Arrange
      TenantProperties properties = new TenantProperties();

      // Act
      properties.setEnable(false);

      // Assert
      assertThat(properties.getEnable()).isFalse();
    }

    @Test
    @DisplayName("应该配置系统表排除列表")
    void shouldConfigureSystemTableExcludes() {
      // Arrange
      TenantProperties properties = new TenantProperties();
      List<String> systemTables =
          Arrays.asList(
              "sys_config",
              "sys_dict_data",
              "sys_dict_type",
              "sys_menu",
              "sys_dept",
              "sys_role",
              "sys_user");

      // Act
      properties.setEnable(true);
      properties.setExcludes(systemTables);

      // Assert
      assertThat(properties.getExcludes()).containsAll(systemTables);
    }

    @Test
    @DisplayName("应该配置代码生成表排除列表")
    void shouldConfigureGenTableExcludes() {
      // Arrange
      TenantProperties properties = new TenantProperties();
      List<String> genTables = Arrays.asList("gen_table", "gen_table_column");

      // Act
      properties.setEnable(true);
      properties.setExcludes(genTables);

      // Assert
      assertThat(properties.getExcludes()).contains("gen_table", "gen_table_column");
    }
  }

  @Nested
  @DisplayName("4. Lombok功能测试")
  class LombokFunctionTests {

    @Test
    @DisplayName("应该正确实现toString方法")
    void shouldImplementToString() {
      // Arrange
      TenantProperties properties = new TenantProperties();
      properties.setEnable(true);
      properties.setExcludes(Arrays.asList("sys_config"));

      // Act
      String toString = properties.toString();

      // Assert
      assertThat(toString).contains("TenantProperties");
      assertThat(toString).contains("true");
      assertThat(toString).contains("sys_config");
    }

    @Test
    @DisplayName("应该正确实现equals方法")
    void shouldImplementEquals() {
      // Arrange
      TenantProperties properties1 = new TenantProperties();
      properties1.setEnable(true);
      properties1.setExcludes(Arrays.asList("sys_config"));

      TenantProperties properties2 = new TenantProperties();
      properties2.setEnable(true);
      properties2.setExcludes(Arrays.asList("sys_config"));

      TenantProperties properties3 = new TenantProperties();
      properties3.setEnable(false);

      // Assert
      assertThat(properties1).isEqualTo(properties2);
      assertThat(properties1).isNotEqualTo(properties3);
    }

    @Test
    @DisplayName("应该正确实现hashCode方法")
    void shouldImplementHashCode() {
      // Arrange
      TenantProperties properties1 = new TenantProperties();
      properties1.setEnable(true);
      properties1.setExcludes(Arrays.asList("sys_config"));

      TenantProperties properties2 = new TenantProperties();
      properties2.setEnable(true);
      properties2.setExcludes(Arrays.asList("sys_config"));

      // Assert
      assertThat(properties1.hashCode()).isEqualTo(properties2.hashCode());
    }
  }

  @Nested
  @DisplayName("5. 边界条件测试")
  class EdgeCaseTests {

    @Test
    @DisplayName("应该处理包含null值的excludes列表")
    void shouldHandleExcludesWithNullValues() {
      // Arrange
      TenantProperties properties = new TenantProperties();
      List<String> excludes = new ArrayList<>();
      excludes.add("sys_config");
      excludes.add(null);
      excludes.add("sys_dict_data");

      // Act
      properties.setExcludes(excludes);

      // Assert
      assertThat(properties.getExcludes()).hasSize(3);
      assertThat(properties.getExcludes()).contains("sys_config", null, "sys_dict_data");
    }

    @Test
    @DisplayName("应该处理包含空字符串的excludes列表")
    void shouldHandleExcludesWithEmptyStrings() {
      // Arrange
      TenantProperties properties = new TenantProperties();
      List<String> excludes = Arrays.asList("sys_config", "", "sys_dict_data");

      // Act
      properties.setExcludes(excludes);

      // Assert
      assertThat(properties.getExcludes()).hasSize(3);
      assertThat(properties.getExcludes()).contains("");
    }

    @Test
    @DisplayName("应该处理包含重复值的excludes列表")
    void shouldHandleExcludesWithDuplicates() {
      // Arrange
      TenantProperties properties = new TenantProperties();
      List<String> excludes =
          Arrays.asList("sys_config", "sys_dict_data", "sys_config", "sys_dict_data");

      // Act
      properties.setExcludes(excludes);

      // Assert
      assertThat(properties.getExcludes()).hasSize(4);
    }

    @Test
    @DisplayName("应该处理大量排除表")
    void shouldHandleLargeNumberOfExcludes() {
      // Arrange
      TenantProperties properties = new TenantProperties();
      List<String> excludes = new ArrayList<>();
      for (int i = 0; i < 100; i++) {
        excludes.add("table_" + i);
      }

      // Act
      properties.setExcludes(excludes);

      // Assert
      assertThat(properties.getExcludes()).hasSize(100);
    }

    @Test
    @DisplayName("应该处理包含特殊字符的表名")
    void shouldHandleTableNamesWithSpecialCharacters() {
      // Arrange
      TenantProperties properties = new TenantProperties();
      List<String> excludes =
          Arrays.asList("sys_config", "表名_中文", "table-with-dash", "table_with_underscore");

      // Act
      properties.setExcludes(excludes);

      // Assert
      assertThat(properties.getExcludes()).containsAll(excludes);
    }
  }

  @Nested
  @DisplayName("6. 配置属性注解测试")
  class ConfigurationPropertiesTests {

    @Test
    @DisplayName("应该有ConfigurationProperties注解")
    void shouldHaveConfigurationPropertiesAnnotation() {
      // Act
      boolean hasAnnotation =
          TenantProperties.class.isAnnotationPresent(
              org.springframework.boot.context.properties.ConfigurationProperties.class);

      // Assert
      assertThat(hasAnnotation).isTrue();
    }

    @Test
    @DisplayName("ConfigurationProperties注解应该指定prefix为tenant")
    void shouldHaveTenantPrefix() {
      // Act
      org.springframework.boot.context.properties.ConfigurationProperties annotation =
          TenantProperties.class.getAnnotation(
              org.springframework.boot.context.properties.ConfigurationProperties.class);

      // Assert
      assertThat(annotation).isNotNull();
      assertThat(annotation.prefix()).isEqualTo("tenant");
    }
  }
}
