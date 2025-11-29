package org.dromara.common.encrypt.core.encryptor;

import static org.assertj.core.api.Assertions.assertThat;

import org.dromara.common.encrypt.core.EncryptContext;
import org.dromara.common.encrypt.enumd.AlgorithmType;
import org.dromara.common.encrypt.enumd.EncodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Base64Encryptor (Base64加密器) 单元测试.
 *
 * @author Test Team
 */
@DisplayName("Base64Encryptor (Base64加密器) 单元测试")
class Base64EncryptorTest {

  private Base64Encryptor encryptor;
  private EncryptContext context;

  @BeforeEach
  void setUp() {
    context = new EncryptContext();
    context.setAlgorithm(AlgorithmType.BASE64);
    context.setEncode(EncodeType.DEFAULT);
    encryptor = new Base64Encryptor(context);
  }

  @Nested
  @DisplayName("1. 构造函数测试")
  class ConstructorTests {

    @Test
    @DisplayName("应该成功创建 Base64Encryptor 实例")
    void shouldCreateBase64Encryptor() {
      // Arrange
      EncryptContext ctx = new EncryptContext();

      // Act
      Base64Encryptor enc = new Base64Encryptor(ctx);

      // Assert
      assertThat(enc).isNotNull();
      assertThat(enc).isInstanceOf(Base64Encryptor.class);
      assertThat(enc).isInstanceOf(AbstractEncryptor.class);
    }
  }

  @Nested
  @DisplayName("2. algorithm() 方法测试")
  class AlgorithmTests {

    @Test
    @DisplayName("应该返回 BASE64 算法类型")
    void shouldReturnBase64Algorithm() {
      // Act
      AlgorithmType algorithm = encryptor.algorithm();

      // Assert
      assertThat(algorithm).isEqualTo(AlgorithmType.BASE64);
    }
  }

  @Nested
  @DisplayName("3. encrypt() 方法测试")
  class EncryptTests {

    @Test
    @DisplayName("应该能够加密简单字符串")
    void shouldEncryptSimpleString() {
      // Arrange
      String plaintext = "Hello World";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.DEFAULT);

      // Assert
      assertThat(encrypted).isNotNull();
      assertThat(encrypted).isNotEqualTo(plaintext);
      assertThat(encrypted).isEqualTo("SGVsbG8gV29ybGQ=");
    }

    @Test
    @DisplayName("应该能够加密中文字符串")
    void shouldEncryptChineseString() {
      // Arrange
      String plaintext = "测试数据";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.DEFAULT);

      // Assert
      assertThat(encrypted).isNotNull();
      assertThat(encrypted).isNotEqualTo(plaintext);
    }

    @Test
    @DisplayName("应该能够加密空字符串")
    void shouldEncryptEmptyString() {
      // Arrange
      String plaintext = "";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.DEFAULT);

      // Assert
      assertThat(encrypted).isNotNull();
      assertThat(encrypted).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "abc", "测试", "123", "!@#$%", "Hello World!"})
    @DisplayName("应该能够加密各种类型的字符串")
    void shouldEncryptVariousStrings(String plaintext) {
      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.DEFAULT);

      // Assert
      assertThat(encrypted).isNotNull();
    }

    @Test
    @DisplayName("应该能够加密特殊字符")
    void shouldEncryptSpecialCharacters() {
      // Arrange
      String plaintext = "!@#$%^&*()_+-={}[]|\\:\";<>?,./";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.DEFAULT);

      // Assert
      assertThat(encrypted).isNotNull();
      assertThat(encrypted).isNotEqualTo(plaintext);
    }

    @Test
    @DisplayName("加密不依赖 EncodeType 参数")
    void shouldIgnoreEncodeTypeParameter() {
      // Arrange
      String plaintext = "test";

      // Act
      String encrypted1 = encryptor.encrypt(plaintext, EncodeType.BASE64);
      String encrypted2 = encryptor.encrypt(plaintext, EncodeType.HEX);
      String encrypted3 = encryptor.encrypt(plaintext, EncodeType.DEFAULT);

      // Assert - Base64加密不使用EncodeType参数,结果应该相同
      assertThat(encrypted1).isEqualTo(encrypted2);
      assertThat(encrypted2).isEqualTo(encrypted3);
    }
  }

  @Nested
  @DisplayName("4. decrypt() 方法测试")
  class DecryptTests {

    @Test
    @DisplayName("应该能够解密简单字符串")
    void shouldDecryptSimpleString() {
      // Arrange
      String encrypted = "SGVsbG8gV29ybGQ=";

      // Act
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isNotNull();
      assertThat(decrypted).isEqualTo("Hello World");
    }

    @Test
    @DisplayName("应该能够解密中文字符串")
    void shouldDecryptChineseString() {
      // Arrange
      String plaintext = "测试数据";
      String encrypted = encryptor.encrypt(plaintext, EncodeType.DEFAULT);

      // Act
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("应该能够解密空字符串")
    void shouldDecryptEmptyString() {
      // Arrange
      String encrypted = "";

      // Act
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "abc", "测试", "123", "!@#$%", "Hello World!"})
    @DisplayName("应该能够解密各种类型的字符串")
    void shouldDecryptVariousStrings(String plaintext) {
      // Arrange
      String encrypted = encryptor.encrypt(plaintext, EncodeType.DEFAULT);

      // Act
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }
  }

  @Nested
  @DisplayName("5. 加解密往返测试")
  class RoundTripTests {

    @Test
    @DisplayName("加密后解密应该得到原始字符串")
    void shouldRoundTrip() {
      // Arrange
      String plaintext = "This is a test";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.DEFAULT);
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("应该支持多次往返加解密")
    void shouldSupportMultipleRoundTrips() {
      // Arrange
      String plaintext = "测试数据";

      // Act & Assert
      for (int i = 0; i < 10; i++) {
        String encrypted = encryptor.encrypt(plaintext, EncodeType.DEFAULT);
        String decrypted = encryptor.decrypt(encrypted);
        assertThat(decrypted).isEqualTo(plaintext);
      }
    }

    @Test
    @DisplayName("应该支持超长字符串的往返加解密")
    void shouldRoundTripLongString() {
      // Arrange
      String plaintext = "A".repeat(10000);

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.DEFAULT);
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }
  }

  @Nested
  @DisplayName("6. 真实业务场景测试")
  class RealBusinessScenarioTests {

    @Test
    @DisplayName("场景: 用户密码Base64编码传输")
    void shouldEncodePasswordForTransmission() {
      // Arrange
      String password = "MySecretPassword123!";

      // Act
      String encoded = encryptor.encrypt(password, EncodeType.DEFAULT);
      String decoded = encryptor.decrypt(encoded);

      // Assert
      assertThat(decoded).isEqualTo(password);
      assertThat(encoded).isNotEqualTo(password);
    }

    @Test
    @DisplayName("场景: JSON数据Base64编码")
    void shouldEncodeJsonData() {
      // Arrange
      String jsonData = "{\"name\":\"张三\",\"age\":30}";

      // Act
      String encoded = encryptor.encrypt(jsonData, EncodeType.DEFAULT);
      String decoded = encryptor.decrypt(encoded);

      // Assert
      assertThat(decoded).isEqualTo(jsonData);
    }

    @Test
    @DisplayName("场景: URL参数Base64编码")
    void shouldEncodeUrlParameter() {
      // Arrange
      String urlParam = "redirect=https://example.com/path?query=value";

      // Act
      String encoded = encryptor.encrypt(urlParam, EncodeType.DEFAULT);
      String decoded = encryptor.decrypt(encoded);

      // Assert
      assertThat(decoded).isEqualTo(urlParam);
      assertThat(encoded).doesNotContain("=", "?", "/"); // Base64结果不应包含URL特殊字符(除了'=')
    }
  }

  @Nested
  @DisplayName("7. 边界测试")
  class BoundaryTests {

    @Test
    @DisplayName("应该能够处理单字节字符串")
    void shouldHandleSingleByteString() {
      // Arrange
      String plaintext = "A";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.DEFAULT);
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("应该能够处理多字节UTF-8字符")
    void shouldHandleMultiByteUtf8Characters() {
      // Arrange
      String plaintext = "😀🎉测试";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.DEFAULT);
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("加密结果应该只包含Base64字符集")
    void shouldOnlyContainBase64Characters() {
      // Arrange
      String plaintext = "test 测试 123 !@#";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.DEFAULT);

      // Assert - Base64字符集: A-Z, a-z, 0-9, +, /, =
      assertThat(encrypted).matches("^[A-Za-z0-9+/]*=*$");
    }
  }
}
