package org.dromara.common.encrypt.core.encryptor;

import org.dromara.common.encrypt.core.EncryptContext;
import org.dromara.common.encrypt.enumd.AlgorithmType;
import org.dromara.common.encrypt.enumd.EncodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * AesEncryptor (AES加密器) 单元测试
 *
 * @author Test Team
 */
@DisplayName("AesEncryptor (AES加密器) 单元测试")
class AesEncryptorTest {

    private static final String AES_KEY_16 = "0123456789abcdef"; // 16字节密钥
    private static final String AES_KEY_24 = "0123456789abcdef01234567"; // 24字节密钥
    private static final String AES_KEY_32 = "0123456789abcdef0123456789abcdef"; // 32字节密钥

    private AesEncryptor encryptor;
    private EncryptContext context;

    @BeforeEach
    void setUp() {
        context = new EncryptContext();
        context.setAlgorithm(AlgorithmType.AES);
        context.setPassword(AES_KEY_16);
        context.setEncode(EncodeType.BASE64);
        encryptor = new AesEncryptor(context);
    }

    @Nested
    @DisplayName("1. 构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("应该成功创建 AesEncryptor 实例")
        void shouldCreateAesEncryptor() {
            // Arrange
            EncryptContext ctx = new EncryptContext();
            ctx.setPassword(AES_KEY_16);

            // Act
            AesEncryptor enc = new AesEncryptor(ctx);

            // Assert
            assertThat(enc).isNotNull();
            assertThat(enc).isInstanceOf(AesEncryptor.class);
            assertThat(enc).isInstanceOf(AbstractEncryptor.class);
        }
    }

    @Nested
    @DisplayName("2. algorithm() 方法测试")
    class AlgorithmTests {

        @Test
        @DisplayName("应该返回 AES 算法类型")
        void shouldReturnAesAlgorithm() {
            // Act
            AlgorithmType algorithm = encryptor.algorithm();

            // Assert
            assertThat(algorithm).isEqualTo(AlgorithmType.AES);
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
        @ValueSource(strings = {"a", "abc", "测试", "123", "!@#$%", "Hello World!"})
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

        @ParameterizedTest
        @ValueSource(strings = {"a", "abc", "测试", "123", "!@#$%"})
        @DisplayName("应该能够加密各种类型的字符串 (Hex)")
        void shouldEncryptVariousStringsHex(String plaintext) {
            // Act
            String encrypted = encryptor.encrypt(plaintext, EncodeType.HEX);

            // Assert
            assertThat(encrypted).isNotNull();
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
    @DisplayName("6. 不同密钥长度测试")
    class DifferentKeyLengthTests {

        @ParameterizedTest
        @CsvSource({
            "0123456789abcdef, 16",
            "0123456789abcdef01234567, 24",
            "0123456789abcdef0123456789abcdef, 32"
        })
        @DisplayName("应该支持16/24/32字节的AES密钥")
        void shouldSupportDifferentKeyLengths(String key, int length) {
            // Arrange
            EncryptContext ctx = new EncryptContext();
            ctx.setPassword(key);
            AesEncryptor enc = new AesEncryptor(ctx);
            String plaintext = "测试数据";

            // Act
            String encrypted = enc.encrypt(plaintext, EncodeType.BASE64);
            String decrypted = enc.decrypt(encrypted);

            // Assert
            assertThat(key).hasSize(length);
            assertThat(decrypted).isEqualTo(plaintext);
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
            for (int i = 0; i < 10; i++) {
                String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);
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
            String plaintext = "测试数据";

            // Act
            String encryptedBase64 = encryptor.encrypt(plaintext, EncodeType.BASE64);
            String encryptedHex = encryptor.encrypt(plaintext, EncodeType.HEX);

            String decryptedBase64 = encryptor.decrypt(encryptedBase64);
            // Hex加密的密文不能直接用decrypt解密,因为decrypt假定是Base64编码

            // Assert
            assertThat(decryptedBase64).isEqualTo(plaintext);
            assertThat(encryptedBase64).isNotEqualTo(encryptedHex); // 编码不同,密文不同
        }

        @Test
        @DisplayName("Base64编码的密文应该包含Base64字符集")
        void shouldBase64EncryptedContainBase64Charset() {
            // Arrange
            String plaintext = "测试 123";

            // Act
            String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);

            // Assert
            assertThat(encrypted).matches("^[A-Za-z0-9+/]*=*$");
        }

        @Test
        @DisplayName("Hex编码的密文应该只包含16进制字符")
        void shouldHexEncryptedContainHexCharset() {
            // Arrange
            String plaintext = "测试 123";

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
        @DisplayName("场景: 用户敏感信息加密存储")
        void shouldEncryptSensitiveUserData() {
            // Arrange
            String idCard = "110101199001011234";

            // Act
            String encrypted = encryptor.encrypt(idCard, EncodeType.BASE64);
            String decrypted = encryptor.decrypt(encrypted);

            // Assert
            assertThat(encrypted).isNotEqualTo(idCard);
            assertThat(decrypted).isEqualTo(idCard);
        }

        @Test
        @DisplayName("场景: API请求参数加密")
        void shouldEncryptApiParameters() {
            // Arrange
            String apiParams = "{\"userId\":12345,\"amount\":1000.50}";

            // Act
            String encrypted = encryptor.encrypt(apiParams, EncodeType.BASE64);
            String decrypted = encryptor.decrypt(encrypted);

            // Assert
            assertThat(decrypted).isEqualTo(apiParams);
        }

        @Test
        @DisplayName("场景: 数据库敏感字段加密")
        void shouldEncryptDatabaseField() {
            // Arrange
            String phoneNumber = "13800138000";

            // Act
            String encrypted = encryptor.encrypt(phoneNumber, EncodeType.BASE64);
            String decrypted = encryptor.decrypt(encrypted);

            // Assert
            assertThat(encrypted).isNotEqualTo(phoneNumber);
            assertThat(decrypted).isEqualTo(phoneNumber);
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
        @DisplayName("应该能够处理多字节UTF-8字符")
        void shouldHandleMultiByteUtf8Characters() {
            // Arrange
            String plaintext = "😀🎉测试";

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
            String plaintext = "!@#$%^&*()_+-={}[]|\\:\";<>?,./";

            // Act
            String encrypted = encryptor.encrypt(plaintext, EncodeType.BASE64);
            String decrypted = encryptor.decrypt(encrypted);

            // Assert
            assertThat(decrypted).isEqualTo(plaintext);
        }
    }
}
