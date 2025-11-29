package org.dromara.common.web.config.properties;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dromara.common.web.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * XssProperties (XSS配置属性) 单元测试.
 *
 * <p>用途: XSS防护配置属性 测试范围: 属性设置、默认值、集合操作
 *
 * @author Test Team
 */
@DisplayName("XssProperties (XSS配置属性) 单元测试")
class XssPropertiesTest extends BaseUnitTest {

  @Nested
  @DisplayName("1. 属性设置测试")
  class PropertySetterTests {

    @Test
    @DisplayName("应该能设置和获取enabled属性")
    void shouldSetAndGetEnabled() {
      // Arrange
      XssProperties properties = new XssProperties();

      // Act
      properties.setEnabled(true);

      // Assert
      assertThat(properties.getEnabled()).isTrue();
    }

    @Test
    @DisplayName("应该能设置enabled为false")
    void shouldSetEnabledToFalse() {
      // Arrange
      XssProperties properties = new XssProperties();

      // Act
      properties.setEnabled(false);

      // Assert
      assertThat(properties.getEnabled()).isFalse();
    }

    @Test
    @DisplayName("应该能设置和获取excludeUrls")
    void shouldSetAndGetExcludeUrls() {
      // Arrange
      XssProperties properties = new XssProperties();
      List<String> urls = Arrays.asList("/api/public", "/api/upload");

      // Act
      properties.setExcludeUrls(urls);

      // Assert
      assertThat(properties.getExcludeUrls())
          .isNotNull()
          .hasSize(2)
          .containsExactly("/api/public", "/api/upload");
    }

    @Test
    @DisplayName("应该能设置空的excludeUrls列表")
    void shouldSetEmptyExcludeUrls() {
      // Arrange
      XssProperties properties = new XssProperties();
      List<String> urls = new ArrayList<>();

      // Act
      properties.setExcludeUrls(urls);

      // Assert
      assertThat(properties.getExcludeUrls()).isNotNull().isEmpty();
    }
  }

  @Nested
  @DisplayName("2. 默认值测试")
  class DefaultValueTests {

    @Test
    @DisplayName("enabled默认值应该为null")
    void enabledShouldBeNullByDefault() {
      // Arrange & Act
      XssProperties properties = new XssProperties();

      // Assert
      assertThat(properties.getEnabled()).isNull();
    }

    @Test
    @DisplayName("excludeUrls默认值应该为空列表")
    void excludeUrlsShouldBeEmptyListByDefault() {
      // Arrange & Act
      XssProperties properties = new XssProperties();

      // Assert
      assertThat(properties.getExcludeUrls()).isNotNull().isEmpty();
    }
  }

  @Nested
  @DisplayName("3. 集合操作测试")
  class CollectionOperationTests {

    @Test
    @DisplayName("应该能向excludeUrls添加元素")
    void shouldAddUrlsToExcludeUrls() {
      // Arrange
      XssProperties properties = new XssProperties();

      // Act
      properties.getExcludeUrls().add("/api/test1");
      properties.getExcludeUrls().add("/api/test2");

      // Assert
      assertThat(properties.getExcludeUrls()).hasSize(2).contains("/api/test1", "/api/test2");
    }

    @Test
    @DisplayName("应该能从excludeUrls移除元素")
    void shouldRemoveUrlsFromExcludeUrls() {
      // Arrange
      XssProperties properties = new XssProperties();
      properties.getExcludeUrls().add("/api/test1");
      properties.getExcludeUrls().add("/api/test2");

      // Act
      properties.getExcludeUrls().remove("/api/test1");

      // Assert
      assertThat(properties.getExcludeUrls()).hasSize(1).containsExactly("/api/test2");
    }

    @Test
    @DisplayName("应该能清空excludeUrls")
    void shouldClearExcludeUrls() {
      // Arrange
      XssProperties properties = new XssProperties();
      properties.getExcludeUrls().add("/api/test1");
      properties.getExcludeUrls().add("/api/test2");

      // Act
      properties.getExcludeUrls().clear();

      // Assert
      assertThat(properties.getExcludeUrls()).isEmpty();
    }
  }

  @Nested
  @DisplayName("4. 真实业务场景测试")
  class RealWorldScenarioTests {

    @Test
    @DisplayName("场景: 配置XSS过滤排除特定API路径")
    void shouldConfigureXssExclusionForSpecificApis() {
      // Arrange
      XssProperties properties = new XssProperties();

      // Act - 配置排除上传和公开API
      properties.setEnabled(true);
      properties.getExcludeUrls().add("/system/upload");
      properties.getExcludeUrls().add("/api/public/*");
      properties.getExcludeUrls().add("/webhook/*");

      // Assert
      assertThat(properties.getEnabled()).isTrue();
      assertThat(properties.getExcludeUrls())
          .hasSize(3)
          .contains("/system/upload", "/api/public/*", "/webhook/*");
    }

    @Test
    @DisplayName("场景: 禁用XSS过滤")
    void shouldDisableXssFiltering() {
      // Arrange
      XssProperties properties = new XssProperties();

      // Act
      properties.setEnabled(false);

      // Assert
      assertThat(properties.getEnabled()).isFalse();
    }

    @Test
    @DisplayName("场景: 启用XSS过滤但不排除任何URL")
    void shouldEnableXssWithoutExclusions() {
      // Arrange
      XssProperties properties = new XssProperties();

      // Act
      properties.setEnabled(true);

      // Assert
      assertThat(properties.getEnabled()).isTrue();
      assertThat(properties.getExcludeUrls()).isEmpty();
    }
  }

  @Nested
  @DisplayName("5. 边界值测试")
  class BoundaryTests {

    @Test
    @DisplayName("应该能处理大量的排除URL")
    void shouldHandleLargeNumberOfExcludeUrls() {
      // Arrange
      XssProperties properties = new XssProperties();
      List<String> urls = new ArrayList<>();
      for (int i = 0; i < 100; i++) {
        urls.add("/api/exclude" + i);
      }

      // Act
      properties.setExcludeUrls(urls);

      // Assert
      assertThat(properties.getExcludeUrls()).hasSize(100);
    }

    @Test
    @DisplayName("应该能处理包含特殊字符的URL")
    void shouldHandleUrlsWithSpecialCharacters() {
      // Arrange
      XssProperties properties = new XssProperties();

      // Act
      properties.getExcludeUrls().add("/api/test?param=value");
      properties.getExcludeUrls().add("/api/test#fragment");
      properties.getExcludeUrls().add("/api/test/**");

      // Assert
      assertThat(properties.getExcludeUrls())
          .hasSize(3)
          .contains("/api/test?param=value", "/api/test#fragment", "/api/test/**");
    }

    @Test
    @DisplayName("应该能处理重复的URL")
    void shouldHandleDuplicateUrls() {
      // Arrange
      XssProperties properties = new XssProperties();

      // Act
      properties.getExcludeUrls().add("/api/test");
      properties.getExcludeUrls().add("/api/test");

      // Assert - List允许重复
      assertThat(properties.getExcludeUrls()).hasSize(2).containsExactly("/api/test", "/api/test");
    }
  }
}
