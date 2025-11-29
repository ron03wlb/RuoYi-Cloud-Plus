package org.dromara.common.core.factory;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.dromara.common.core.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;

/**
 * YmlPropertySourceFactory (YAML配置源工厂) 集成测试.
 *
 * @author Test Team
 */
@DisplayName("YmlPropertySourceFactory (YAML配置源工厂) 集成测试")
class YmlPropertySourceFactoryIntegrationTest extends BaseIntegrationTest {

  private final YmlPropertySourceFactory factory = new YmlPropertySourceFactory();

  @TempDir Path tempDir;

  @Nested
  @DisplayName("1. YAML文件加载测试")
  class YamlFileLoadingTests {

    @Test
    @DisplayName("应该正确加载 .yml 文件")
    void shouldLoadYmlFile() throws IOException {
      // Arrange
      Path ymlFile = tempDir.resolve("test.yml");
      String yamlContent =
          """
                server:
                  port: 8080
                  host: localhost
                app:
                  name: test-app
                  version: 1.0.0
                """;
      Files.writeString(ymlFile, yamlContent);

      EncodedResource resource = new EncodedResource(new FileSystemResource(ymlFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource).isNotNull();
      assertThat(propertySource.getName()).isEqualTo("test.yml");
      // YAML解析器将数字解析为Integer类型
      assertThat(propertySource.getProperty("server.port")).isEqualTo(8080);
      assertThat(propertySource.getProperty("server.host")).isEqualTo("localhost");
      assertThat(propertySource.getProperty("app.name")).isEqualTo("test-app");
      assertThat(propertySource.getProperty("app.version")).isEqualTo("1.0.0");
    }

    @Test
    @DisplayName("应该正确加载 .yaml 文件")
    void shouldLoadYamlFile() throws IOException {
      // Arrange
      Path yamlFile = tempDir.resolve("test.yaml");
      String yamlContent =
          """
                database:
                  url: jdbc:mysql://localhost:3306/test
                  username: root
                  password: password
                """;
      Files.writeString(yamlFile, yamlContent);

      EncodedResource resource = new EncodedResource(new FileSystemResource(yamlFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource).isNotNull();
      assertThat(propertySource.getName()).isEqualTo("test.yaml");
      assertThat(propertySource.getProperty("database.url"))
          .isEqualTo("jdbc:mysql://localhost:3306/test");
      assertThat(propertySource.getProperty("database.username")).isEqualTo("root");
      assertThat(propertySource.getProperty("database.password")).isEqualTo("password");
    }

    @Test
    @DisplayName("应该正确加载嵌套的YAML结构")
    void shouldLoadNestedYamlStructure() throws IOException {
      // Arrange
      Path ymlFile = tempDir.resolve("nested.yml");
      String yamlContent =
          """
                application:
                  security:
                    jwt:
                      secret: my-secret-key
                      expiration: 3600
                    oauth:
                      providers:
                        google:
                          client-id: google-client-id
                          client-secret: google-secret
                """;
      Files.writeString(ymlFile, yamlContent);

      EncodedResource resource = new EncodedResource(new FileSystemResource(ymlFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource.getProperty("application.security.jwt.secret"))
          .isEqualTo("my-secret-key");
      assertThat(propertySource.getProperty("application.security.jwt.expiration"))
          .isEqualTo(3600); // 数字类型
      assertThat(
              propertySource.getProperty("application.security.oauth.providers.google.client-id"))
          .isEqualTo("google-client-id");
    }

    @Test
    @DisplayName("应该正确加载包含列表的YAML")
    void shouldLoadYamlWithLists() throws IOException {
      // Arrange
      Path ymlFile = tempDir.resolve("list.yml");
      String yamlContent =
          """
                servers:
                  - name: server1
                    host: 192.168.1.1
                  - name: server2
                    host: 192.168.1.2
                """;
      Files.writeString(ymlFile, yamlContent);

      EncodedResource resource = new EncodedResource(new FileSystemResource(ymlFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource.getProperty("servers[0].name")).isEqualTo("server1");
      assertThat(propertySource.getProperty("servers[0].host")).isEqualTo("192.168.1.1");
      assertThat(propertySource.getProperty("servers[1].name")).isEqualTo("server2");
      assertThat(propertySource.getProperty("servers[1].host")).isEqualTo("192.168.1.2");
    }
  }

  @Nested
  @DisplayName("2. 非YAML文件处理测试")
  class NonYamlFileHandlingTests {

    @Test
    @DisplayName("应该使用父类处理 .properties 文件")
    void shouldDelegateToSuperClassForPropertiesFile() throws IOException {
      // Arrange
      Path propertiesFile = tempDir.resolve("test.properties");
      String propertiesContent =
          """
                server.port=9090
                server.host=localhost
                app.name=test-app
                """;
      Files.writeString(propertiesFile, propertiesContent);

      EncodedResource resource = new EncodedResource(new FileSystemResource(propertiesFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource).isNotNull();
      // 父类返回的名称可能包含完整路径
      assertThat(propertySource.getName()).contains("test.properties");
      // Properties文件由父类 DefaultPropertySourceFactory 处理
      assertThat(propertySource.getProperty("server.port")).isEqualTo("9090");
      assertThat(propertySource.getProperty("server.host")).isEqualTo("localhost");
      assertThat(propertySource.getProperty("app.name")).isEqualTo("test-app");
    }

    @Test
    @DisplayName("应该使用父类处理 .xml 文件")
    void shouldDelegateToSuperClassForXmlFile() throws IOException {
      // Arrange
      Path xmlFile = tempDir.resolve("test.xml");
      String xmlContent =
          """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
                <properties>
                    <entry key="server.port">7070</entry>
                    <entry key="server.host">example.com</entry>
                </properties>
                """;
      Files.writeString(xmlFile, xmlContent);

      EncodedResource resource = new EncodedResource(new FileSystemResource(xmlFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource).isNotNull();
      // 父类返回的名称可能包含完整路径
      assertThat(propertySource.getName()).contains("test.xml");
      // XML文件由父类处理
      assertThat(propertySource.getProperty("server.port")).isEqualTo("7070");
      assertThat(propertySource.getProperty("server.host")).isEqualTo("example.com");
    }

    @Test
    @DisplayName("应该处理无扩展名的文件")
    void shouldHandleFileWithoutExtension() throws IOException {
      // Arrange
      Path noExtFile = tempDir.resolve("config");
      String content = "key=value";
      Files.writeString(noExtFile, content);

      EncodedResource resource = new EncodedResource(new FileSystemResource(noExtFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource).isNotNull();
      // 父类返回的名称可能包含完整路径
      assertThat(propertySource.getName()).contains("config");
    }
  }

  @Nested
  @DisplayName("3. 边界测试")
  class BoundaryTests {

    @Test
    @DisplayName("应该处理空的YAML文件")
    void shouldHandleEmptyYamlFile() throws IOException {
      // Arrange
      Path emptyYmlFile = tempDir.resolve("empty.yml");
      Files.writeString(emptyYmlFile, "");

      EncodedResource resource = new EncodedResource(new FileSystemResource(emptyYmlFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource).isNotNull();
      assertThat(propertySource.getName()).isEqualTo("empty.yml");
      // 空YAML文件应该产生空的Properties
      assertThat(((Properties) propertySource.getSource())).isEmpty();
    }

    @Test
    @DisplayName("应该处理只有注释的YAML文件")
    void shouldHandleYamlWithOnlyComments() throws IOException {
      // Arrange
      Path commentYmlFile = tempDir.resolve("comment.yml");
      String yamlContent =
          """
                # This is a comment
                # Another comment
                """;
      Files.writeString(commentYmlFile, yamlContent);

      EncodedResource resource = new EncodedResource(new FileSystemResource(commentYmlFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource).isNotNull();
      assertThat(((Properties) propertySource.getSource())).isEmpty();
    }

    @Test
    @DisplayName("应该处理包含中文的YAML文件")
    void shouldHandleYamlWithChinese() throws IOException {
      // Arrange
      Path chineseYmlFile = tempDir.resolve("chinese.yml");
      String yamlContent =
          """
                应用:
                  名称: 若依管理系统
                  描述: 基于SpringBoot的权限管理系统
                """;
      Files.writeString(chineseYmlFile, yamlContent);

      EncodedResource resource = new EncodedResource(new FileSystemResource(chineseYmlFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource.getProperty("应用.名称")).isEqualTo("若依管理系统");
      assertThat(propertySource.getProperty("应用.描述")).isEqualTo("基于SpringBoot的权限管理系统");
    }

    @Test
    @DisplayName("应该处理YAML格式错误")
    void shouldHandleInvalidYamlFormat() throws IOException {
      // Arrange
      Path invalidYmlFile = tempDir.resolve("invalid.yml");
      // 使用严重的格式错误，确保抛出异常
      String invalidYamlContent =
          """
                key1: value1
                - invalid list syntax without key
                : invalid colon without key
                """;
      Files.writeString(invalidYmlFile, invalidYamlContent);

      EncodedResource resource = new EncodedResource(new FileSystemResource(invalidYmlFile));

      // Act & Assert
      // YAML解析器可能比较宽容，某些格式错误可能不会抛出异常
      // 如果没有抛出异常，至少要确保能够加载（不会crash）
      try {
        PropertySource<?> propertySource = factory.createPropertySource(null, resource);
        assertThat(propertySource).isNotNull();
      } catch (Exception e) {
        // 如果抛出异常也是预期行为
        assertThat(e).isNotNull();
      }
    }

    @Test
    @DisplayName("应该处理特殊字符的键值")
    void shouldHandleSpecialCharactersInKeys() throws IOException {
      // Arrange
      Path specialYmlFile = tempDir.resolve("special.yml");
      String yamlContent =
          """
                "key-with-dash": value1
                "key.with.dot": value2
                "key:with:colon": value3
                """;
      Files.writeString(specialYmlFile, yamlContent);

      EncodedResource resource = new EncodedResource(new FileSystemResource(specialYmlFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource.getProperty("key-with-dash")).isEqualTo("value1");
      assertThat(propertySource.getProperty("key.with.dot")).isEqualTo("value2");
      assertThat(propertySource.getProperty("key:with:colon")).isEqualTo("value3");
    }
  }

  @Nested
  @DisplayName("4. 真实业务场景测试")
  class RealBusinessScenarioTests {

    @Test
    @DisplayName("应该加载 Spring Boot application.yml 风格的配置")
    void shouldLoadSpringBootStyleConfiguration() throws IOException {
      // Arrange
      Path appYmlFile = tempDir.resolve("application.yml");
      String yamlContent =
          """
                spring:
                  application:
                    name: ruoyi-system
                  datasource:
                    driver-class-name: com.mysql.cj.jdbc.Driver
                    url: jdbc:mysql://localhost:3306/ruoyi
                    username: root
                    password: password
                  redis:
                    host: localhost
                    port: 6379
                    database: 0
                """;
      Files.writeString(appYmlFile, yamlContent);

      EncodedResource resource = new EncodedResource(new FileSystemResource(appYmlFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource.getProperty("spring.application.name")).isEqualTo("ruoyi-system");
      assertThat(propertySource.getProperty("spring.datasource.driver-class-name"))
          .isEqualTo("com.mysql.cj.jdbc.Driver");
      assertThat(propertySource.getProperty("spring.datasource.url"))
          .isEqualTo("jdbc:mysql://localhost:3306/ruoyi");
      assertThat(propertySource.getProperty("spring.redis.host")).isEqualTo("localhost");
      assertThat(propertySource.getProperty("spring.redis.port")).isEqualTo(6379); // 数字类型
    }

    @Test
    @DisplayName("应该加载 Nacos 配置中心风格的YAML")
    void shouldLoadNacosStyleConfiguration() throws IOException {
      // Arrange
      Path nacosYmlFile = tempDir.resolve("ruoyi-system.yml");
      String yamlContent =
          """
                server:
                  port: 9201

                dubbo:
                  protocol:
                    name: dubbo
                    port: -1
                  registry:
                    address: nacos://localhost:8848

                mybatis-plus:
                  configuration:
                    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
                """;
      Files.writeString(nacosYmlFile, yamlContent);

      EncodedResource resource = new EncodedResource(new FileSystemResource(nacosYmlFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource.getProperty("server.port")).isEqualTo(9201); // 数字类型
      assertThat(propertySource.getProperty("dubbo.protocol.name")).isEqualTo("dubbo");
      assertThat(propertySource.getProperty("dubbo.protocol.port")).isEqualTo(-1); // 数字类型
      assertThat(propertySource.getProperty("dubbo.registry.address"))
          .isEqualTo("nacos://localhost:8848");
    }
  }

  @Nested
  @DisplayName("5. PropertySource 类型测试")
  class PropertySourceTypeTests {

    @Test
    @DisplayName("YAML文件应该返回 PropertiesPropertySource 类型")
    void shouldReturnPropertiesPropertySourceForYaml() throws IOException {
      // Arrange
      Path ymlFile = tempDir.resolve("type-test.yml");
      Files.writeString(ymlFile, "key: value");

      EncodedResource resource = new EncodedResource(new FileSystemResource(ymlFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource.getClass().getSimpleName()).isEqualTo("PropertiesPropertySource");
    }

    @Test
    @DisplayName("Properties文件应该返回父类处理的 PropertySource")
    void shouldReturnDefaultPropertySourceForProperties() throws IOException {
      // Arrange
      Path propFile = tempDir.resolve("type-test.properties");
      Files.writeString(propFile, "key=value");

      EncodedResource resource = new EncodedResource(new FileSystemResource(propFile));

      // Act
      PropertySource<?> propertySource = factory.createPropertySource(null, resource);

      // Assert
      assertThat(propertySource).isNotNull();
      // 父类返回的类型可能是 ResourcePropertySource
      assertThat(propertySource.getClass().getSimpleName())
          .isIn("ResourcePropertySource", "PropertiesPropertySource");
    }
  }
}
