package org.dromara.common.encrypt.utils;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * {@link EncryptUtils} 单元测试
 *
 * @author Lion Li
 */
@DisplayName("EncryptUtils 单元测试")
class EncryptUtilsTest {

  @Nested
  @DisplayName("Base64加解密测试")
  class Base64EncryptionTests {

    @Test
    @DisplayName("应该能够Base64加密简单字符串")
    void shouldEncryptSimpleStringWithBase64() {
      String plaintext = "Hello, World!";

      String encrypted = EncryptUtils.encryptByBase64(plaintext);

      assertThat(encrypted).isNotNull();
      assertThat(encrypted).isNotEqualTo(plaintext);
      assertThat(encrypted).isBase64();
    }

    @Test
    @DisplayName("应该能够Base64解密字符串")
    void shouldDecryptBase64String() {
      String plaintext = "Hello, World!";
      String encrypted = EncryptUtils.encryptByBase64(plaintext);

      String decrypted = EncryptUtils.decryptByBase64(encrypted);

      assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Base64加解密应该是可逆的")
    void base64EncryptionShouldBeReversible() {
      String original = "测试中文加密123ABC!@#";

      String encrypted = EncryptUtils.encryptByBase64(original);
      String decrypted = EncryptUtils.decryptByBase64(encrypted);

      assertThat(decrypted).isEqualTo(original);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a", "abc", "测试", "123", "!@#$%"})
    @DisplayName("应该能够处理各种长度的字符串")
    void shouldHandleVariousStringLengths(String plaintext) {
      String encrypted = EncryptUtils.encryptByBase64(plaintext);
      String decrypted = EncryptUtils.decryptByBase64(encrypted);

      assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("应该能够处理超长字符串")
    void shouldHandleVeryLongString() {
      String longString = "a".repeat(1000);

      String encrypted = EncryptUtils.encryptByBase64(longString);
      String decrypted = EncryptUtils.decryptByBase64(encrypted);

      assertThat(decrypted).isEqualTo(longString);
    }
  }

  @Nested
  @DisplayName("AES加解密测试")
  class AesEncryptionTests {

    @Test
    @DisplayName("应该能够使用16位密钥进行AES加密")
    void shouldEncryptWithAes16ByteKey() {
      String plaintext = "敏感数据";
      String password = "1234567890123456"; // 16 bytes

      String encrypted = EncryptUtils.encryptByAes(plaintext, password);

      assertThat(encrypted).isNotNull();
      assertThat(encrypted).isNotEqualTo(plaintext);
    }

    @Test
    @DisplayName("应该能够使用24位密钥进行AES加密")
    void shouldEncryptWithAes24ByteKey() {
      String plaintext = "敏感数据";
      String password = "123456789012345678901234"; // 24 bytes

      String encrypted = EncryptUtils.encryptByAes(plaintext, password);

      assertThat(encrypted).isNotNull();
    }

    @Test
    @DisplayName("应该能够使用32位密钥进行AES加密")
    void shouldEncryptWithAes32ByteKey() {
      String plaintext = "敏感数据";
      String password = "12345678901234567890123456789012"; // 32 bytes

      String encrypted = EncryptUtils.encryptByAes(plaintext, password);

      assertThat(encrypted).isNotNull();
    }

    @Test
    @DisplayName("AES加解密应该是可逆的 - Base64编码")
    void aesEncryptionShouldBeReversibleWithBase64() {
      String original = "重要的机密数据123";
      String password = "1234567890123456";

      String encrypted = EncryptUtils.encryptByAes(original, password);
      String decrypted = EncryptUtils.decryptByAes(encrypted, password);

      assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("AES加解密应该是可逆的 - Hex编码")
    void aesEncryptionShouldBeReversibleWithHex() {
      String original = "重要的机密数据456";
      String password = "1234567890123456";

      String encrypted = EncryptUtils.encryptByAesHex(original, password);
      String decrypted = EncryptUtils.decryptByAes(encrypted, password);

      assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("使用空密钥应该抛出异常")
    void shouldThrowExceptionWithEmptyPassword() {
      assertThatThrownBy(() -> EncryptUtils.encryptByAes("data", ""))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("AES需要传入秘钥信息");
    }

    @Test
    @DisplayName("使用null密钥应该抛出异常")
    void shouldThrowExceptionWithNullPassword() {
      assertThatThrownBy(() -> EncryptUtils.encryptByAes("data", null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("AES需要传入秘钥信息");
    }

    @Test
    @DisplayName("使用非法长度密钥应该抛出异常")
    void shouldThrowExceptionWithInvalidKeyLength() {
      assertThatThrownBy(() -> EncryptUtils.encryptByAes("data", "12345")) // 5 bytes, invalid
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("AES秘钥长度要求为16位、24位、32位");
    }

    @ParameterizedTest
    @ValueSource(ints = {16, 24, 32})
    @DisplayName("应该支持所有有效的AES密钥长度")
    void shouldSupportAllValidAesKeyLengths(int length) {
      String plaintext = "test data";
      String password = "a".repeat(length);

      assertThatCode(
              () -> {
                String encrypted = EncryptUtils.encryptByAes(plaintext, password);
                String decrypted = EncryptUtils.decryptByAes(encrypted, password);
                assertThat(decrypted).isEqualTo(plaintext);
              })
          .doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("SM4加解密测试")
  class Sm4EncryptionTests {

    @Test
    @DisplayName("应该能够使用16位密钥进行SM4加密 - Base64")
    void shouldEncryptWithSm4Base64() {
      String plaintext = "国密SM4测试数据";
      String password = "1234567890123456"; // SM4 requires 16 bytes

      String encrypted = EncryptUtils.encryptBySm4(plaintext, password);

      assertThat(encrypted).isNotNull();
      assertThat(encrypted).isNotEqualTo(plaintext);
    }

    @Test
    @DisplayName("应该能够使用16位密钥进行SM4加密 - Hex")
    void shouldEncryptWithSm4Hex() {
      String plaintext = "国密SM4测试数据";
      String password = "1234567890123456";

      String encrypted = EncryptUtils.encryptBySm4Hex(plaintext, password);

      assertThat(encrypted).isNotNull();
      assertThat(encrypted).matches("[0-9a-fA-F]+"); // Should be hex
    }

    @Test
    @DisplayName("SM4加解密应该是可逆的 - Base64")
    void sm4EncryptionShouldBeReversibleWithBase64() {
      String original = "SM4国密算法测试";
      String password = "1234567890123456";

      String encrypted = EncryptUtils.encryptBySm4(original, password);
      String decrypted = EncryptUtils.decryptBySm4(encrypted, password);

      assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("SM4加解密应该是可逆的 - Hex")
    void sm4EncryptionShouldBeReversibleWithHex() {
      String original = "SM4国密算法Hex测试";
      String password = "abcdef1234567890";

      String encrypted = EncryptUtils.encryptBySm4Hex(original, password);
      String decrypted = EncryptUtils.decryptBySm4(encrypted, password);

      assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("使用空密钥应该抛出异常")
    void shouldThrowExceptionWithEmptyPassword() {
      assertThatThrownBy(() -> EncryptUtils.encryptBySm4("data", ""))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("SM4需要传入秘钥信息");
    }

    @Test
    @DisplayName("使用非16位密钥应该抛出异常")
    void shouldThrowExceptionWithNon16ByteKey() {
      assertThatThrownBy(() -> EncryptUtils.encryptBySm4("data", "12345")) // Not 16 bytes
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("SM4秘钥长度要求为16位");
    }
  }

  @Nested
  @DisplayName("RSA加解密测试")
  class RsaEncryptionTests {

    @Test
    @DisplayName("应该能够生成RSA密钥对")
    void shouldGenerateRsaKeyPair() {
      Map<String, String> keyPair = EncryptUtils.generateRsaKey();

      assertThat(keyPair).isNotNull();
      assertThat(keyPair).containsKeys(EncryptUtils.PUBLIC_KEY, EncryptUtils.PRIVATE_KEY);
      assertThat(keyPair.get(EncryptUtils.PUBLIC_KEY)).isNotEmpty();
      assertThat(keyPair.get(EncryptUtils.PRIVATE_KEY)).isNotEmpty();
    }

    @Test
    @DisplayName("RSA公钥加密私钥解密应该是可逆的 - Base64")
    void rsaPublicEncryptPrivateDecryptShouldBeReversible() {
      Map<String, String> keyPair = EncryptUtils.generateRsaKey();
      String publicKey = keyPair.get(EncryptUtils.PUBLIC_KEY);
      String privateKey = keyPair.get(EncryptUtils.PRIVATE_KEY);
      String original = "RSA非对称加密测试";

      String encrypted = EncryptUtils.encryptByRsa(original, publicKey);
      String decrypted = EncryptUtils.decryptByRsa(encrypted, privateKey);

      assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("RSA公钥加密私钥解密应该是可逆的 - Hex")
    void rsaPublicEncryptPrivateDecryptShouldBeReversibleWithHex() {
      Map<String, String> keyPair = EncryptUtils.generateRsaKey();
      String publicKey = keyPair.get(EncryptUtils.PUBLIC_KEY);
      String privateKey = keyPair.get(EncryptUtils.PRIVATE_KEY);
      String original = "RSA Hex测试";

      String encrypted = EncryptUtils.encryptByRsaHex(original, publicKey);
      String decrypted = EncryptUtils.decryptByRsa(encrypted, privateKey);

      assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("使用空公钥加密应该抛出异常")
    void shouldThrowExceptionWithEmptyPublicKey() {
      assertThatThrownBy(() -> EncryptUtils.encryptByRsa("data", ""))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("RSA需要传入公钥进行加密");
    }

    @Test
    @DisplayName("使用空私钥解密应该抛出异常")
    void shouldThrowExceptionWithEmptyPrivateKey() {
      assertThatThrownBy(() -> EncryptUtils.decryptByRsa("data", ""))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("RSA需要传入私钥进行解密");
    }
  }

  @Nested
  @DisplayName("SM2加解密测试")
  class Sm2EncryptionTests {

    @Test
    @DisplayName("应该能够生成SM2密钥对")
    void shouldGenerateSm2KeyPair() {
      Map<String, String> keyPair = EncryptUtils.generateSm2Key();

      assertThat(keyPair).isNotNull();
      assertThat(keyPair).containsKeys(EncryptUtils.PUBLIC_KEY, EncryptUtils.PRIVATE_KEY);
      assertThat(keyPair.get(EncryptUtils.PUBLIC_KEY)).isNotEmpty();
      assertThat(keyPair.get(EncryptUtils.PRIVATE_KEY)).isNotEmpty();
    }

    @Test
    @DisplayName("SM2公钥加密私钥解密应该是可逆的 - Base64")
    void sm2PublicEncryptPrivateDecryptShouldBeReversible() {
      Map<String, String> keyPair = EncryptUtils.generateSm2Key();
      String publicKey = keyPair.get(EncryptUtils.PUBLIC_KEY);
      String privateKey = keyPair.get(EncryptUtils.PRIVATE_KEY);
      String original = "SM2国密非对称加密测试";

      String encrypted = EncryptUtils.encryptBySm2(original, publicKey);
      String decrypted = EncryptUtils.decryptBySm2(encrypted, privateKey);

      assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("SM2公钥加密私钥解密应该是可逆的 - Hex")
    void sm2PublicEncryptPrivateDecryptShouldBeReversibleWithHex() {
      Map<String, String> keyPair = EncryptUtils.generateSm2Key();
      String publicKey = keyPair.get(EncryptUtils.PUBLIC_KEY);
      String privateKey = keyPair.get(EncryptUtils.PRIVATE_KEY);
      String original = "SM2 Hex测试";

      String encrypted = EncryptUtils.encryptBySm2Hex(original, publicKey);
      String decrypted = EncryptUtils.decryptBySm2(encrypted, privateKey);

      assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("使用空公钥加密应该抛出异常")
    void shouldThrowExceptionWithEmptyPublicKey() {
      assertThatThrownBy(() -> EncryptUtils.encryptBySm2("data", ""))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("SM2需要传入公钥进行加密");
    }

    @Test
    @DisplayName("使用空私钥解密应该抛出异常")
    void shouldThrowExceptionWithEmptyPrivateKey() {
      assertThatThrownBy(() -> EncryptUtils.decryptBySm2("data", ""))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("SM2需要传入私钥进行解密");
    }
  }

  @Nested
  @DisplayName("哈希算法测试")
  class HashAlgorithmTests {

    @Test
    @DisplayName("应该能够计算MD5哈希")
    void shouldCalculateMd5Hash() {
      String data = "password123";

      String hash = EncryptUtils.encryptByMd5(data);

      assertThat(hash).isNotNull();
      assertThat(hash).hasSize(32); // MD5 produces 32 hex characters
      assertThat(hash).matches("[0-9a-f]+");
    }

    @Test
    @DisplayName("相同数据的MD5哈希应该相同")
    void sameMd5HashForSameData() {
      String data = "test-data";

      String hash1 = EncryptUtils.encryptByMd5(data);
      String hash2 = EncryptUtils.encryptByMd5(data);

      assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("不同数据的MD5哈希应该不同")
    void differentMd5HashForDifferentData() {
      String hash1 = EncryptUtils.encryptByMd5("data1");
      String hash2 = EncryptUtils.encryptByMd5("data2");

      assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("应该能够计算SHA256哈希")
    void shouldCalculateSha256Hash() {
      String data = "password123";

      String hash = EncryptUtils.encryptBySha256(data);

      assertThat(hash).isNotNull();
      assertThat(hash).hasSize(64); // SHA-256 produces 64 hex characters
      assertThat(hash).matches("[0-9a-f]+");
    }

    @Test
    @DisplayName("相同数据的SHA256哈希应该相同")
    void sameSha256HashForSameData() {
      String data = "test-data";

      String hash1 = EncryptUtils.encryptBySha256(data);
      String hash2 = EncryptUtils.encryptBySha256(data);

      assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("应该能够计算SM3哈希")
    void shouldCalculateSm3Hash() {
      String data = "国密SM3哈希测试";

      String hash = EncryptUtils.encryptBySm3(data);

      assertThat(hash).isNotNull();
      assertThat(hash).hasSize(64); // SM3 produces 64 hex characters
      assertThat(hash).matches("[0-9a-f]+");
    }

    @Test
    @DisplayName("相同数据的SM3哈希应该相同")
    void sameSm3HashForSameData() {
      String data = "sm3-test-data";

      String hash1 = EncryptUtils.encryptBySm3(data);
      String hash2 = EncryptUtils.encryptBySm3(data);

      assertThat(hash1).isEqualTo(hash2);
    }
  }

  @Nested
  @DisplayName("常量测试")
  class ConstantTests {

    @Test
    @DisplayName("PUBLIC_KEY常量应该是publicKey")
    void publicKeyConstantShouldBePublicKey() {
      assertThat(EncryptUtils.PUBLIC_KEY).isEqualTo("publicKey");
    }

    @Test
    @DisplayName("PRIVATE_KEY常量应该是privateKey")
    void privateKeyConstantShouldBePrivateKey() {
      assertThat(EncryptUtils.PRIVATE_KEY).isEqualTo("privateKey");
    }
  }

  @Nested
  @DisplayName("业务场景测试")
  class BusinessScenarioTests {

    @Test
    @DisplayName("应该支持用户密码加密存储")
    void shouldSupportPasswordEncryptionStorage() {
      String password = "user-password-123";

      // 使用单向哈希存储密码
      String md5Hash = EncryptUtils.encryptByMd5(password);
      String sha256Hash = EncryptUtils.encryptBySha256(password);

      assertThat(md5Hash).isNotEqualTo(password);
      assertThat(sha256Hash).isNotEqualTo(password);
      assertThat(md5Hash).isNotEqualTo(sha256Hash);
    }

    @Test
    @DisplayName("应该支持敏感数据的对称加密")
    void shouldSupportSensitiveDataSymmetricEncryption() {
      String sensitiveData = "18612345678"; // 手机号
      String password = "1234567890123456";

      // AES 对称加密
      String encrypted = EncryptUtils.encryptByAes(sensitiveData, password);
      String decrypted = EncryptUtils.decryptByAes(encrypted, password);

      assertThat(encrypted).isNotEqualTo(sensitiveData);
      assertThat(decrypted).isEqualTo(sensitiveData);
    }

    @Test
    @DisplayName("应该支持API通信的非对称加密")
    void shouldSupportApiCommunicationAsymmetricEncryption() {
      Map<String, String> keyPair = EncryptUtils.generateRsaKey();
      String apiData = "{\"userId\":123,\"action\":\"transfer\"}";

      // 客户端使用公钥加密
      String encrypted = EncryptUtils.encryptByRsa(apiData, keyPair.get(EncryptUtils.PUBLIC_KEY));
      // 服务端使用私钥解密
      String decrypted =
          EncryptUtils.decryptByRsa(encrypted, keyPair.get(EncryptUtils.PRIVATE_KEY));

      assertThat(decrypted).isEqualTo(apiData);
    }

    @Test
    @DisplayName("应该支持国密算法")
    void shouldSupportChineseNationalCryptoAlgorithms() {
      String data = "涉密信息";

      // SM3 哈希
      String sm3Hash = EncryptUtils.encryptBySm3(data);
      assertThat(sm3Hash).isNotEmpty();

      // SM4 对称加密
      String sm4Encrypted = EncryptUtils.encryptBySm4(data, "1234567890123456");
      String sm4Decrypted = EncryptUtils.decryptBySm4(sm4Encrypted, "1234567890123456");
      assertThat(sm4Decrypted).isEqualTo(data);

      // SM2 非对称加密
      Map<String, String> sm2Keys = EncryptUtils.generateSm2Key();
      String sm2Encrypted = EncryptUtils.encryptBySm2(data, sm2Keys.get(EncryptUtils.PUBLIC_KEY));
      String sm2Decrypted =
          EncryptUtils.decryptBySm2(sm2Encrypted, sm2Keys.get(EncryptUtils.PRIVATE_KEY));
      assertThat(sm2Decrypted).isEqualTo(data);
    }
  }

  @Nested
  @DisplayName("性能和边界测试")
  class PerformanceAndEdgeCaseTests {

    @Test
    @DisplayName("应该能够处理空字符串")
    void shouldHandleEmptyString() {
      assertThatCode(
              () -> {
                String encrypted = EncryptUtils.encryptByBase64("");
                String decrypted = EncryptUtils.decryptByBase64(encrypted);
                assertThat(decrypted).isEmpty();
              })
          .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("应该能够处理长字符串")
    void shouldHandleLongString() {
      String longString = "a".repeat(10000);
      String password = "1234567890123456";

      String encrypted = EncryptUtils.encryptByAes(longString, password);
      String decrypted = EncryptUtils.decryptByAes(encrypted, password);

      assertThat(decrypted).isEqualTo(longString);
    }

    @Test
    @DisplayName("应该能够处理特殊字符")
    void shouldHandleSpecialCharacters() {
      String specialChars = "!@#$%^&*()_+-=[]{}|;:',.<>?/~`测试中文";
      String password = "1234567890123456";

      String encrypted = EncryptUtils.encryptByAes(specialChars, password);
      String decrypted = EncryptUtils.decryptByAes(encrypted, password);

      assertThat(decrypted).isEqualTo(specialChars);
    }

    @Test
    @DisplayName("生成的密钥对应该每次都不同")
    void generatedKeyPairsShouldBeDifferent() {
      Map<String, String> keyPair1 = EncryptUtils.generateRsaKey();
      Map<String, String> keyPair2 = EncryptUtils.generateRsaKey();

      assertThat(keyPair1.get(EncryptUtils.PUBLIC_KEY))
          .isNotEqualTo(keyPair2.get(EncryptUtils.PUBLIC_KEY));
      assertThat(keyPair1.get(EncryptUtils.PRIVATE_KEY))
          .isNotEqualTo(keyPair2.get(EncryptUtils.PRIVATE_KEY));
    }
  }
}
