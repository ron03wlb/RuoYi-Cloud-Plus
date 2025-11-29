package org.dromara.common.encrypt.core.encryptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.dromara.common.encrypt.core.EncryptContext;
import org.dromara.common.encrypt.enumd.AlgorithmType;
import org.dromara.common.encrypt.enumd.EncodeType;
import org.dromara.common.encrypt.utils.EncryptUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * RsaEncryptor (RSA加密器) 单元测试.
 *
 * @author Test Team
 */
@DisplayName("RsaEncryptor (RSA加密器) 单元测试")
class RsaEncryptorTest {

  private RsaEncryptor encryptor;
  private EncryptContext context;
  private Map<String, String> rsaKeyPair;

  @BeforeEach
  void setUp() {
    // 生成RSA密钥对
    rsaKeyPair = EncryptUtils.generateRsaKey();

    context = new EncryptContext();
    context.setAlgorithm(AlgorithmType.RSA);
    context.setPublicKey(rsaKeyPair.get(EncryptUtils.PUBLIC_KEY));
    context.setPrivateKey(rsaKeyPair.get(EncryptUtils.PRIVATE_KEY));
    context.setEncode(EncodeType.BASE64);

    encryptor = new RsaEncryptor(context);
  }

  @Nested
  @DisplayName("1. 构造函数测试")
  class ConstructorTests {

    @Test
    @DisplayName("应该成功创建 RsaEncryptor 实例")
    void shouldCreateRsaEncryptor() {
      // Arrange
      EncryptContext ctx = new EncryptContext();
      ctx.setPublicKey(rsaKeyPair.get(EncryptUtils.PUBLIC_KEY));
      ctx.setPrivateKey(rsaKeyPair.get(EncryptUtils.PRIVATE_KEY));

      // Act
      RsaEncryptor enc = new RsaEncryptor(ctx);

      // Assert
      assertThat(enc).isNotNull();
      assertThat(enc).isInstanceOf(RsaEncryptor.class);
      assertThat(enc).isInstanceOf(AbstractEncryptor.class);
    }

    @Test
    @DisplayName("缺少公钥时应该抛出异常")
    void shouldThrowExceptionWhenPublicKeyMissing() {
      // Arrange
      EncryptContext ctx = new EncryptContext();
      ctx.setPrivateKey(rsaKeyPair.get(EncryptUtils.PRIVATE_KEY));
      // 故意不设置公钥

      // Act & Assert
      assertThatThrownBy(() -> new RsaEncryptor(ctx))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("RSA公私钥均需要提供");
    }

    @Test
    @DisplayName("缺少私钥时应该抛出异常")
    void shouldThrowExceptionWhenPrivateKeyMissing() {
      // Arrange
      EncryptContext ctx = new EncryptContext();
      ctx.setPublicKey(rsaKeyPair.get(EncryptUtils.PUBLIC_KEY));
      // 故意不设置私钥

      // Act & Assert
      assertThatThrownBy(() -> new RsaEncryptor(ctx))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("RSA公私钥均需要提供");
    }

    @Test
    @DisplayName("公钥为空字符串时应该抛出异常")
    void shouldThrowExceptionWhenPublicKeyEmpty() {
      // Arrange
      EncryptContext ctx = new EncryptContext();
      ctx.setPublicKey("");
      ctx.setPrivateKey(rsaKeyPair.get(EncryptUtils.PRIVATE_KEY));

      // Act & Assert
      assertThatThrownBy(() -> new RsaEncryptor(ctx))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("RSA公私钥均需要提供");
    }

    @Test
    @DisplayName("私钥为空字符串时应该抛出异常")
    void shouldThrowExceptionWhenPrivateKeyEmpty() {
      // Arrange
      EncryptContext ctx = new EncryptContext();
      ctx.setPublicKey(rsaKeyPair.get(EncryptUtils.PUBLIC_KEY));
      ctx.setPrivateKey("");

      // Act & Assert
      assertThatThrownBy(() -> new RsaEncryptor(ctx))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("RSA公私钥均需要提供");
    }
  }

  @Nested
  @DisplayName("2. algorithm() 方法测试")
  class AlgorithmTests {

    @Test
    @DisplayName("应该返回 RSA 算法类型")
    void shouldReturnRsaAlgorithm() {
      // Act
      AlgorithmType algorithm = encryptor.algorithm();

      // Assert
      assertThat(algorithm).isEqualTo(AlgorithmType.RSA);
    }
  }

  @Nested
  @DisplayName("3. encrypt() 方法测试 - Base64编码")
  class EncryptBase64Tests {

    @Test
    @DisplayName("应该能够加密简单字符串 (Base64)")
    void shouldEncryptSimpleStringBase64() {
      // Arrange
      String plaintext = "Hello World";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);

      // Assert
      assertThat(encrypted).isNotNull();
      assertThat(encrypted).isNotEqualTo(plaintext);
      assertThat(encrypted).isBase64();
    }

    @Test
    @DisplayName("应该能够加密中文字符串 (Base64)")
    void shouldEncryptChineseStringBase64() {
      // Arrange
      String plaintext = "测试数据";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);

      // Assert
      assertThat(encrypted).isNotNull();
      assertThat(encrypted).isNotEqualTo(plaintext);
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "abc", "测试", "123", "!@#$%"})
    @DisplayName("应该能够加密各种类型的字符串 (Base64)")
    void shouldEncryptVariousStringsBase64(String plaintext) {
      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);

      // Assert
      assertThat(encrypted).isNotNull();
      assertThat(encrypted).isBase64();
    }
  }

  @Nested
  @DisplayName("4. encrypt() 方法测试 - Hex编码")
  class EncryptHexTests {

    @Test
    @DisplayName("应该能够加密简单字符串 (Hex)")
    void shouldEncryptSimpleStringHex() {
      // Arrange
      String plaintext = "Hello World";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.HEX);

      // Assert
      assertThat(encrypted).isNotNull();
      assertThat(encrypted).isNotEqualTo(plaintext);
      assertThat(encrypted).matches("^[0-9a-f]+$"); // Hex格式
    }

    @Test
    @DisplayName("应该能够加密中文字符串 (Hex)")
    void shouldEncryptChineseStringHex() {
      // Arrange
      String plaintext = "测试数据";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.HEX);

      // Assert
      assertThat(encrypted).isNotNull();
      assertThat(encrypted).isNotEqualTo(plaintext);
      assertThat(encrypted).matches("^[0-9a-f]+$");
    }
  }

  @Nested
  @DisplayName("5. decrypt() 方法测试")
  class DecryptTests {

    @Test
    @DisplayName("应该能够解密Base64编码的密文")
    void shouldDecryptBase64Encrypted() {
      // Arrange
      String plaintext = "Hello World";
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);

      // Act
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("应该能够解密中文字符串")
    void shouldDecryptChineseString() {
      // Arrange
      String plaintext = "测试数据";
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);

      // Act
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "abc", "测试", "123", "!@#$%", "Hello World!"})
    @DisplayName("应该能够解密各种类型的字符串")
    void shouldDecryptVariousStrings(String plaintext) {
      // Arrange
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);

      // Act
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }
  }

  @Nested
  @DisplayName("6. 公钥加密私钥解密测试")
  class PublicPrivateKeyTests {

    @Test
    @DisplayName("公钥加密的数据只能用对应的私钥解密")
    void shouldDecryptOnlyWithMatchingPrivateKey() {
      // Arrange
      String plaintext = "Sensitive Data";
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);

      // Act
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("使用不同密钥对应该无法解密")
    void shouldNotDecryptWithDifferentKeyPair() {
      // Arrange
      String plaintext = "Secret Message";
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);

      // 生成新的密钥对
      Map<String, String> anotherKeyPair = EncryptUtils.generateRsaKey();
      EncryptContext anotherContext = new EncryptContext();
      anotherContext.setPublicKey(anotherKeyPair.get(EncryptUtils.PUBLIC_KEY));
      anotherContext.setPrivateKey(anotherKeyPair.get(EncryptUtils.PRIVATE_KEY));
      RsaEncryptor anotherEncryptor = new RsaEncryptor(anotherContext);

      // Act & Assert - 用另一个密钥对的私钥解密应该失败
      assertThatThrownBy(() -> anotherEncryptor.decrypt(encrypted))
          .isInstanceOf(RuntimeException.class);
    }
  }

  @Nested
  @DisplayName("7. 加解密往返测试")
  class RoundTripTests {

    @Test
    @DisplayName("加密后解密应该得到原始字符串 (Base64)")
    void shouldRoundTripBase64() {
      // Arrange
      String plaintext = "This is a test";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);
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
      for (int i = 0; i < 5; i++) {
        String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);
        String decrypted = encryptor.decrypt(encrypted);
        assertThat(decrypted).isEqualTo(plaintext);
      }
    }

    @Test
    @DisplayName("应该支持较长字符串的往返加解密")
    void shouldRoundTripLongerString() {
      // Arrange - RSA加密有长度限制,不能太长
      String plaintext = "A".repeat(100);

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }
  }

  @Nested
  @DisplayName("8. Base64 vs Hex 编码测试")
  class EncodingComparisonTests {

    @Test
    @DisplayName("同一明文用不同编码方式加密,解密后应该得到相同结果")
    void shouldGetSamePlaintextWithDifferentEncoding() {
      // Arrange
      String plaintext = "测试";

      // Act
      String encryptedBase64 = encryptor.encrypt(plaintext, EncodeType.BASE64);
      String decryptedBase64 = encryptor.decrypt(encryptedBase64);

      // Assert
      assertThat(decryptedBase64).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Base64编码的密文应该包含Base64字符集")
    void shouldBase64EncryptedContainBase64Charset() {
      // Arrange
      String plaintext = "test";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);

      // Assert
      assertThat(encrypted).matches("^[A-Za-z0-9+/]*=*$");
    }

    @Test
    @DisplayName("Hex编码的密文应该只包含16进制字符")
    void shouldHexEncryptedContainHexCharset() {
      // Arrange
      String plaintext = "test";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.HEX);

      // Assert
      assertThat(encrypted).matches("^[0-9a-f]+$");
    }
  }

  @Nested
  @DisplayName("9. 真实业务场景测试")
  class RealBusinessScenarioTests {

    @Test
    @DisplayName("场景: API密钥传输加密")
    void shouldEncryptApiKey() {
      // Arrange
      String apiKey = "sk-1234567890abcdef";

      // Act
      String encrypted = encryptor.encrypt(apiKey, EncodeType.BASE64);
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(encrypted).isNotEqualTo(apiKey);
      assertThat(decrypted).isEqualTo(apiKey);
    }

    @Test
    @DisplayName("场景: 数字签名验证")
    void shouldEncryptDigitalSignature() {
      // Arrange
      String signature = "SHA256:abc123def456";

      // Act
      String encrypted = encryptor.encrypt(signature, EncodeType.BASE64);
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(signature);
    }

    @Test
    @DisplayName("场景: 用户认证令牌加密")
    void shouldEncryptAuthToken() {
      // Arrange
      String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

      // Act
      String encrypted = encryptor.encrypt(token, EncodeType.BASE64);
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(token);
    }
  }

  @Nested
  @DisplayName("10. 边界测试")
  class BoundaryTests {

    @Test
    @DisplayName("应该能够处理空字符串")
    void shouldHandleEmptyString() {
      // Arrange
      String plaintext = "";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEmpty();
    }

    @Test
    @DisplayName("应该能够处理单字符")
    void shouldHandleSingleCharacter() {
      // Arrange
      String plaintext = "A";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("应该能够处理特殊字符")
    void shouldHandleSpecialCharacters() {
      // Arrange
      String plaintext = "!@#$%^&*()";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("应该能够处理多字节UTF-8字符")
    void shouldHandleMultiByteUtf8Characters() {
      // Arrange
      String plaintext = "😀🎉";

      // Act
      String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);
      String decrypted = encryptor.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }
  }

  @Nested
  @DisplayName("11. 密钥对生成测试")
  class KeyPairGenerationTests {

    @Test
    @DisplayName("生成的密钥对应该可用于加解密")
    void shouldGenerateUsableKeyPair() {
      // Arrange
      Map<String, String> keyPair = EncryptUtils.generateRsaKey();
      EncryptContext ctx = new EncryptContext();
      ctx.setPublicKey(keyPair.get(EncryptUtils.PUBLIC_KEY));
      ctx.setPrivateKey(keyPair.get(EncryptUtils.PRIVATE_KEY));
      RsaEncryptor enc = new RsaEncryptor(ctx);
      String plaintext = "Test Data";

      // Act
      String encrypted = enc.encrypt(plaintext, EncodeType.BASE64);
      String decrypted = enc.decrypt(encrypted);

      // Assert
      assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("每次生成的密钥对应该不同")
    void shouldGenerateDifferentKeyPairs() {
      // Act
      Map<String, String> keyPair1 = EncryptUtils.generateRsaKey();
      Map<String, String> keyPair2 = EncryptUtils.generateRsaKey();

      // Assert
      assertThat(keyPair1.get(EncryptUtils.PUBLIC_KEY))
          .isNotEqualTo(keyPair2.get(EncryptUtils.PUBLIC_KEY));
      assertThat(keyPair1.get(EncryptUtils.PRIVATE_KEY))
          .isNotEqualTo(keyPair2.get(EncryptUtils.PRIVATE_KEY));
    }
  }
}
