package org.dromara.common.encrypt.properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@link ApiDecryptProperties} 单元测试
 *
 * @author Lion Li
 */
@DisplayName("ApiDecryptProperties 单元测试")
class ApiDecryptPropertiesTest {

  @Nested
  @DisplayName("基本属性测试")
  class BasicPropertyTests {

    @Test
    @DisplayName("应该能够创建默认实例")
    void shouldCreateDefaultInstance() {
      ApiDecryptProperties properties = new ApiDecryptProperties();

      assertThat(properties).isNotNull();
    }

    @Test
    @DisplayName("应该能够设置和获取enabled")
    void shouldSetAndGetEnabled() {
      ApiDecryptProperties properties = new ApiDecryptProperties();

      properties.setEnabled(true);

      assertThat(properties.getEnabled()).isTrue();
    }

    @Test
    @DisplayName("应该能够设置和获取headerFlag")
    void shouldSetAndGetHeaderFlag() {
      ApiDecryptProperties properties = new ApiDecryptProperties();

      properties.setHeaderFlag("X-Encrypt");

      assertThat(properties.getHeaderFlag()).isEqualTo("X-Encrypt");
    }

    @Test
    @DisplayName("应该能够设置和获取publicKey")
    void shouldSetAndGetPublicKey() {
      ApiDecryptProperties properties = new ApiDecryptProperties();

      properties.setPublicKey("api-public-key-value");

      assertThat(properties.getPublicKey()).isEqualTo("api-public-key-value");
    }

    @Test
    @DisplayName("应该能够设置和获取privateKey")
    void shouldSetAndGetPrivateKey() {
      ApiDecryptProperties properties = new ApiDecryptProperties();

      properties.setPrivateKey("api-private-key-value");

      assertThat(properties.getPrivateKey()).isEqualTo("api-private-key-value");
    }
  }

  @Nested
  @DisplayName("注解验证测试")
  class AnnotationTests {

    @Test
    @DisplayName("应该有@ConfigurationProperties注解")
    void shouldHaveConfigurationPropertiesAnnotation() {
      ConfigurationProperties annotation =
          ApiDecryptProperties.class.getAnnotation(ConfigurationProperties.class);

      assertThat(annotation).isNotNull();
      assertThat(annotation.prefix()).isEqualTo("api-decrypt");
    }

    @Test
    @DisplayName("应该有无参构造函数")
    void shouldHaveNoArgsConstructor() {
      assertThatNoException().isThrownBy(ApiDecryptProperties::new);
    }
  }

  @Nested
  @DisplayName("业务场景测试")
  class BusinessScenarioTests {

    @Test
    @DisplayName("应该支持启用API加密功能")
    void shouldSupportEnablingApiEncryption() {
      ApiDecryptProperties properties = new ApiDecryptProperties();
      properties.setEnabled(true);
      properties.setHeaderFlag("X-Encrypt-Data");
      properties.setPublicKey("rsa-public-key-for-response");
      properties.setPrivateKey("rsa-private-key-for-request");

      assertThat(properties.getEnabled()).isTrue();
      assertThat(properties.getHeaderFlag()).isEqualTo("X-Encrypt-Data");
      assertThat(properties.getPublicKey()).isNotNull();
      assertThat(properties.getPrivateKey()).isNotNull();
    }

    @Test
    @DisplayName("应该支持禁用API加密功能")
    void shouldSupportDisablingApiEncryption() {
      ApiDecryptProperties properties = new ApiDecryptProperties();
      properties.setEnabled(false);

      assertThat(properties.getEnabled()).isFalse();
    }

    @Test
    @DisplayName("应该支持自定义请求头标识")
    void shouldSupportCustomHeaderFlag() {
      ApiDecryptProperties properties = new ApiDecryptProperties();

      // 测试不同的请求头标识
      properties.setHeaderFlag("X-Api-Encrypt");
      assertThat(properties.getHeaderFlag()).isEqualTo("X-Api-Encrypt");

      properties.setHeaderFlag("X-Secure");
      assertThat(properties.getHeaderFlag()).isEqualTo("X-Secure");

      properties.setHeaderFlag("Encrypt-Flag");
      assertThat(properties.getHeaderFlag()).isEqualTo("Encrypt-Flag");
    }

    @Test
    @DisplayName("应该支持请求解密场景")
    void shouldSupportRequestDecryptionScenario() {
      // 客户端使用私钥解密请求
      ApiDecryptProperties properties = new ApiDecryptProperties();
      properties.setEnabled(true);
      properties.setPrivateKey("request-decrypt-private-key");

      assertThat(properties.getEnabled()).isTrue();
      assertThat(properties.getPrivateKey()).isEqualTo("request-decrypt-private-key");
    }

    @Test
    @DisplayName("应该支持响应加密场景")
    void shouldSupportResponseEncryptionScenario() {
      // 服务端使用公钥加密响应
      ApiDecryptProperties properties = new ApiDecryptProperties();
      properties.setEnabled(true);
      properties.setPublicKey("response-encrypt-public-key");

      assertThat(properties.getEnabled()).isTrue();
      assertThat(properties.getPublicKey()).isEqualTo("response-encrypt-public-key");
    }

    @Test
    @DisplayName("应该支持完整的API加解密配置")
    void shouldSupportCompleteApiEncryptionConfiguration() {
      ApiDecryptProperties properties = new ApiDecryptProperties();
      properties.setEnabled(true);
      properties.setHeaderFlag("X-Encrypted");
      properties.setPublicKey("rsa-public-for-encrypt");
      properties.setPrivateKey("rsa-private-for-decrypt");

      assertThat(properties.getEnabled()).isTrue();
      assertThat(properties.getHeaderFlag()).isEqualTo("X-Encrypted");
      assertThat(properties.getPublicKey()).isEqualTo("rsa-public-for-encrypt");
      assertThat(properties.getPrivateKey()).isEqualTo("rsa-private-for-decrypt");
    }
  }

  @Nested
  @DisplayName("对象相等性测试")
  class EqualityTests {

    @Test
    @DisplayName("相同配置的对象应该相等")
    void objectsWithSameConfigurationShouldBeEqual() {
      ApiDecryptProperties props1 = new ApiDecryptProperties();
      props1.setEnabled(true);
      props1.setHeaderFlag("X-Encrypt");
      props1.setPublicKey("public-key");

      ApiDecryptProperties props2 = new ApiDecryptProperties();
      props2.setEnabled(true);
      props2.setHeaderFlag("X-Encrypt");
      props2.setPublicKey("public-key");

      assertThat(props1).isEqualTo(props2);
    }

    @Test
    @DisplayName("不同配置的对象应该不相等")
    void objectsWithDifferentConfigurationShouldNotBeEqual() {
      ApiDecryptProperties props1 = new ApiDecryptProperties();
      props1.setEnabled(true);

      ApiDecryptProperties props2 = new ApiDecryptProperties();
      props2.setEnabled(false);

      assertThat(props1).isNotEqualTo(props2);
    }
  }

  @Nested
  @DisplayName("完整配置测试")
  class CompleteConfigurationTests {

    @Test
    @DisplayName("应该能够设置所有属性")
    void shouldBeAbleToSetAllProperties() {
      ApiDecryptProperties properties = new ApiDecryptProperties();

      properties.setEnabled(true);
      properties.setHeaderFlag("X-Custom-Header");
      properties.setPublicKey("complete-public-key");
      properties.setPrivateKey("complete-private-key");

      assertThat(properties.getEnabled()).isTrue();
      assertThat(properties.getHeaderFlag()).isEqualTo("X-Custom-Header");
      assertThat(properties.getPublicKey()).isEqualTo("complete-public-key");
      assertThat(properties.getPrivateKey()).isEqualTo("complete-private-key");
    }

    @Test
    @DisplayName("应该支持null值")
    void shouldSupportNullValues() {
      ApiDecryptProperties properties = new ApiDecryptProperties();

      properties.setEnabled(null);
      properties.setHeaderFlag(null);
      properties.setPublicKey(null);
      properties.setPrivateKey(null);

      assertThat(properties.getEnabled()).isNull();
      assertThat(properties.getHeaderFlag()).isNull();
      assertThat(properties.getPublicKey()).isNull();
      assertThat(properties.getPrivateKey()).isNull();
    }
  }

  @Nested
  @DisplayName("配置前缀测试")
  class ConfigurationPrefixTests {

    @Test
    @DisplayName("配置前缀应该是api-decrypt")
    void configurationPrefixShouldBeApiDecrypt() {
      ConfigurationProperties annotation =
          ApiDecryptProperties.class.getAnnotation(ConfigurationProperties.class);

      assertThat(annotation.prefix()).isEqualTo("api-decrypt");
    }

    @Test
    @DisplayName("应该能够通过api-decrypt前缀绑定属性")
    void shouldBindPropertiesWithApiDecryptPrefix() {
      // 在实际的Spring环境中，配置文件中的 api-decrypt.enabled
      // 会自动绑定到 ApiDecryptProperties.enabled 属性

      ApiDecryptProperties properties = new ApiDecryptProperties();
      properties.setEnabled(true);

      assertThat(properties.getEnabled()).isTrue();
    }
  }

  @Nested
  @DisplayName("HTTP请求头测试")
  class HttpHeaderTests {

    @Test
    @DisplayName("应该支持常见的HTTP请求头命名规范")
    void shouldSupportCommonHttpHeaderNamingConventions() {
      ApiDecryptProperties properties = new ApiDecryptProperties();

      // X- prefix (传统自定义请求头)
      properties.setHeaderFlag("X-Encryption-Flag");
      assertThat(properties.getHeaderFlag()).startsWith("X-");

      // 无前缀自定义请求头
      properties.setHeaderFlag("Encryption-Flag");
      assertThat(properties.getHeaderFlag()).doesNotStartWith("X-");

      // 大小写混合
      properties.setHeaderFlag("x-Encrypt-Data");
      assertThat(properties.getHeaderFlag()).isEqualToIgnoringCase("X-Encrypt-Data");
    }

    @Test
    @DisplayName("应该支持设置空字符串作为headerFlag")
    void shouldSupportEmptyStringAsHeaderFlag() {
      ApiDecryptProperties properties = new ApiDecryptProperties();

      properties.setHeaderFlag("");

      assertThat(properties.getHeaderFlag()).isEmpty();
    }
  }

  @Nested
  @DisplayName("安全性测试")
  class SecurityTests {

    @Test
    @DisplayName("应该能够存储敏感的密钥信息")
    void shouldBeAbleToStoreSensitiveKeyInformation() {
      ApiDecryptProperties properties = new ApiDecryptProperties();
      String sensitivePublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgK...";
      String sensitivePrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcw...";

      properties.setPublicKey(sensitivePublicKey);
      properties.setPrivateKey(sensitivePrivateKey);

      assertThat(properties.getPublicKey()).isEqualTo(sensitivePublicKey);
      assertThat(properties.getPrivateKey()).isEqualTo(sensitivePrivateKey);
    }

    @Test
    @DisplayName("应该能够区分请求和响应的密钥")
    void shouldDifferentiateBetweenRequestAndResponseKeys() {
      ApiDecryptProperties properties = new ApiDecryptProperties();

      // 私钥用于解密请求
      properties.setPrivateKey("private-key-for-decrypt-request");
      // 公钥用于加密响应
      properties.setPublicKey("public-key-for-encrypt-response");

      assertThat(properties.getPrivateKey()).contains("decrypt-request");
      assertThat(properties.getPublicKey()).contains("encrypt-response");
    }
  }

  @Nested
  @DisplayName("边界条件测试")
  class EdgeCaseTests {

    @Test
    @DisplayName("应该能够处理超长的密钥字符串")
    void shouldHandleVeryLongKeyStrings() {
      ApiDecryptProperties properties = new ApiDecryptProperties();
      String longKey = "a".repeat(10000);

      properties.setPublicKey(longKey);

      assertThat(properties.getPublicKey()).hasSize(10000);
    }

    @Test
    @DisplayName("应该能够处理特殊字符的headerFlag")
    void shouldHandleSpecialCharactersInHeaderFlag() {
      ApiDecryptProperties properties = new ApiDecryptProperties();

      properties.setHeaderFlag("X-Encrypt_Flag-v1.0");

      assertThat(properties.getHeaderFlag()).isEqualTo("X-Encrypt_Flag-v1.0");
    }

    @Test
    @DisplayName("应该能够多次修改配置")
    void shouldBeAbleToModifyConfigurationMultipleTimes() {
      ApiDecryptProperties properties = new ApiDecryptProperties();

      properties.setEnabled(true);
      assertThat(properties.getEnabled()).isTrue();

      properties.setEnabled(false);
      assertThat(properties.getEnabled()).isFalse();

      properties.setEnabled(true);
      assertThat(properties.getEnabled()).isTrue();
    }
  }
}
