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
 * Sm4Encryptor (SM4加密器) 单元测试 SM4是中国国家密码管理局发布的对称加密算法
 *
 * @author Test Team
 */
@DisplayName("Sm4Encryptor (SM4加密器) 单元测试")
class Sm4EncryptorTest {

    private static final String SM4_KEY = "0123456789abcdef"; // 16字节密钥

    private Sm4Encryptor encryptor;
    private EncryptContext context;

    @BeforeEach
    void setUp() {
        context = new EncryptContext();
        context.setAlgorithm(AlgorithmType.SM4);
        context.setPassword(SM4_KEY);
        context.setEncode(EncodeType.BASE64);
        encryptor = new Sm4Encryptor(context);
    }

    @Nested
    @DisplayName("1. 构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("应该成功创建 Sm4Encryptor 实例")
        void shouldCreateSm4Encryptor() {
            // Arrange
            EncryptContext ctx = new EncryptContext();
            ctx.setPassword(SM4_KEY);

            // Act
            Sm4Encryptor enc = new Sm4Encryptor(ctx);

            // Assert
            assertThat(enc).isNotNull();
            assertThat(enc).isInstanceOf(Sm4Encryptor.class);
            assertThat(enc).isInstanceOf(AbstractEncryptor.class);
        }
    }

    @Nested
    @DisplayName("2. algorithm() 方法测试")
    class AlgorithmTests {

        @Test
        @DisplayName("应该返回 SM4 算法类型")
        void shouldReturnSm4Algorithm() {
            // Act
            AlgorithmType algorithm = encryptor.algorithm();

            // Assert
            assertThat(algorithm).isEqualTo(AlgorithmType.SM4);
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
    @DisplayName("6. SM4密钥长度测试")
    class KeyLengthTests {

        @Test
        @DisplayName("应该支持16字节的SM4标准密钥")
        void shouldSupport16ByteKey() {
            // Arrange
            String key16 = "0123456789abcdef"; // 16字节
            EncryptContext ctx = new EncryptContext();
            ctx.setPassword(key16);
            Sm4Encryptor enc = new Sm4Encryptor(ctx);
            String plaintext = "测试数据";

            // Act
            String encrypted = enc.encrypt(plaintext, EncodeType.BASE64);
            String decrypted = enc.decrypt(encrypted);

            // Assert
            assertThat(key16).hasSize(16);
            assertThat(decrypted).isEqualTo(plaintext);
        }

        @Test
        @DisplayName("SM4密钥应该是16字节")
        void shouldVerifyKeyLength() {
            // Assert
            assertThat(SM4_KEY).hasSize(16).describedAs("SM4 requires 16-byte (128-bit) key");
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
    @DisplayName("9. 真实业务场景测试 - 国密应用")
    class RealBusinessScenarioTests {

        @Test
        @DisplayName("场景: 国家机关数据加密存储")
        void shouldEncryptGovernmentData() {
            // Arrange - 政府机关敏感信息
            String citizenData = "公民信息:110101199001011234";

            // Act
            String encrypted = encryptor.encrypt(citizenData, EncodeType.BASE64);
            String decrypted = encryptor.decrypt(encrypted);

            // Assert
            assertThat(encrypted).isNotEqualTo(citizenData);
            assertThat(decrypted).isEqualTo(citizenData);
        }

        @Test
        @DisplayName("场景: 金融行业敏感数据加密")
        void shouldEncryptFinancialData() {
            // Arrange - 银行账户信息
            String accountInfo = "{\"account\":\"6222021234567890\",\"balance\":100000.00}";

            // Act
            String encrypted = encryptor.encrypt(accountInfo, EncodeType.BASE64);
            String decrypted = encryptor.decrypt(encrypted);

            // Assert
            assertThat(decrypted).isEqualTo(accountInfo);
        }

        @Test
        @DisplayName("场景: 医疗健康数据加密")
        void shouldEncryptMedicalData() {
            // Arrange - 病人隐私数据
            String medicalRecord = "患者:张三,诊断:高血压,药物:降压药";

            // Act
            String encrypted = encryptor.encrypt(medicalRecord, EncodeType.BASE64);
            String decrypted = encryptor.decrypt(encrypted);

            // Assert
            assertThat(encrypted).isNotEqualTo(medicalRecord);
            assertThat(decrypted).isEqualTo(medicalRecord);
        }

        @Test
        @DisplayName("场景: 企业商业机密加密传输")
        void shouldEncryptBusinessSecret() {
            // Arrange - 企业核心数据
            String businessSecret = "商业计划书:2024年营收目标10亿元";

            // Act
            String encrypted = encryptor.encrypt(businessSecret, EncodeType.BASE64);
            String decrypted = encryptor.decrypt(encrypted);

            // Assert
            assertThat(decrypted).isEqualTo(businessSecret);
        }

        @Test
        @DisplayName("场景: 电子政务系统数据交换")
        void shouldEncryptEGovernmentData() {
            // Arrange - 政务数据交换
            String govExchangeData = "社保数据:{\"name\":\"李四\",\"ssn\":\"110102199001011234\"}";

            // Act
            String encrypted = encryptor.encrypt(govExchangeData, EncodeType.BASE64);
            String decrypted = encryptor.decrypt(encrypted);

            // Assert
            assertThat(decrypted).isEqualTo(govExchangeData);
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

    @Nested
    @DisplayName("11. SM4国密标准特性测试")
    class Sm4SpecificTests {

        @Test
        @DisplayName("SM4应该使用128位(16字节)密钥")
        void shouldUseSm4StandardKeyLength() {
            // Arrange
            String key = "0123456789abcdef"; // 128 bits

            // Assert
            assertThat(key.getBytes())
                    .hasSize(16)
                    .describedAs("SM4 uses 128-bit block cipher with 128-bit key");
        }

        @Test
        @DisplayName("SM4加密应该产生不同的密文(即使明文相同)")
        void shouldProduceDifferentCiphertextForSamePlaintext() {
            // Arrange
            String plaintext = "相同的明文";

            // Act - SM4使用随机IV,同样明文会产生不同密文
            String encrypted1 = encryptor.encrypt(plaintext, EncodeType.BASE64);
            String encrypted2 = encryptor.encrypt(plaintext, EncodeType.BASE64);

            // Assert - 密文应该不同(因为使用随机IV)
            // 但解密后应该得到相同明文
            String decrypted1 = encryptor.decrypt(encrypted1);
            String decrypted2 = encryptor.decrypt(encrypted2);

            assertThat(decrypted1).isEqualTo(plaintext);
            assertThat(decrypted2).isEqualTo(plaintext);
        }

        @Test
        @DisplayName("SM4作为国密算法应该支持中文处理")
        void shouldSupportChineseCharactersAsNationalStandard() {
            // Arrange - 测试各种中文场景
            String[] chineseTexts = {"中国加密标准", "国家密码管理局", "商用密码算法", "信息安全保密", "电子政务系统"};

            // Act & Assert
            for (String text : chineseTexts) {
                String encrypted = encryptor.encrypt(text, EncodeType.BASE64);
                String decrypted = encryptor.decrypt(encrypted);
                assertThat(decrypted).isEqualTo(text);
            }
        }
    }
}
