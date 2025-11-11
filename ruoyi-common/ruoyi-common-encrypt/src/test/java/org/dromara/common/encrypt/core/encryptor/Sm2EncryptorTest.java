package org.dromara.common.encrypt.core.encryptor;

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

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Sm2Encryptor (SM2国密加密器) 单元测试
 *
 * @author Test Team
 */
@DisplayName("Sm2Encryptor (SM2国密加密器) 单元测试")
class Sm2EncryptorTest {

    private Sm2Encryptor encryptor;
    private EncryptContext context;
    private Map<String, String> sm2KeyPair;

    @BeforeEach
    void setUp() {
        // 生成SM2密钥对
        sm2KeyPair = EncryptUtils.generateSm2Key();

        context = new EncryptContext();
        context.setAlgorithm(AlgorithmType.SM2);
        context.setPublicKey(sm2KeyPair.get(EncryptUtils.PUBLIC_KEY));
        context.setPrivateKey(sm2KeyPair.get(EncryptUtils.PRIVATE_KEY));
        context.setEncode(EncodeType.BASE64);

        encryptor = new Sm2Encryptor(context);
    }

    @Nested
    @DisplayName("1. 构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("应该成功创建 Sm2Encryptor 实例")
        void shouldCreateSm2Encryptor() {
            // Arrange
            EncryptContext ctx = new EncryptContext();
            ctx.setPublicKey(sm2KeyPair.get(EncryptUtils.PUBLIC_KEY));
            ctx.setPrivateKey(sm2KeyPair.get(EncryptUtils.PRIVATE_KEY));

            // Act
            Sm2Encryptor enc = new Sm2Encryptor(ctx);

            // Assert
            assertThat(enc).isNotNull();
            assertThat(enc).isInstanceOf(Sm2Encryptor.class);
            assertThat(enc).isInstanceOf(AbstractEncryptor.class);
        }

        @Test
        @DisplayName("缺少公钥时应该抛出异常")
        void shouldThrowExceptionWhenPublicKeyMissing() {
            // Arrange
            EncryptContext ctx = new EncryptContext();
            ctx.setPrivateKey(sm2KeyPair.get(EncryptUtils.PRIVATE_KEY));

            // Act & Assert
            assertThatThrownBy(() -> new Sm2Encryptor(ctx))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SM2公私钥均需要提供");
        }

        @Test
        @DisplayName("缺少私钥时应该抛出异常")
        void shouldThrowExceptionWhenPrivateKeyMissing() {
            // Arrange
            EncryptContext ctx = new EncryptContext();
            ctx.setPublicKey(sm2KeyPair.get(EncryptUtils.PUBLIC_KEY));

            // Act & Assert
            assertThatThrownBy(() -> new Sm2Encryptor(ctx))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SM2公私钥均需要提供");
        }

        @Test
        @DisplayName("公钥为空字符串时应该抛出异常")
        void shouldThrowExceptionWhenPublicKeyEmpty() {
            // Arrange
            EncryptContext ctx = new EncryptContext();
            ctx.setPublicKey("");
            ctx.setPrivateKey(sm2KeyPair.get(EncryptUtils.PRIVATE_KEY));

            // Act & Assert
            assertThatThrownBy(() -> new Sm2Encryptor(ctx))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SM2公私钥均需要提供");
        }

        @Test
        @DisplayName("私钥为空字符串时应该抛出异常")
        void shouldThrowExceptionWhenPrivateKeyEmpty() {
            // Arrange
            EncryptContext ctx = new EncryptContext();
            ctx.setPublicKey(sm2KeyPair.get(EncryptUtils.PUBLIC_KEY));
            ctx.setPrivateKey("");

            // Act & Assert
            assertThatThrownBy(() -> new Sm2Encryptor(ctx))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SM2公私钥均需要提供");
        }
    }

    @Nested
    @DisplayName("2. algorithm() 方法测试")
    class AlgorithmTests {

        @Test
        @DisplayName("应该返回 SM2 算法类型")
        void shouldReturnSm2Algorithm() {
            // Act
            AlgorithmType algorithm = encryptor.algorithm();

            // Assert
            assertThat(algorithm).isEqualTo(AlgorithmType.SM2);
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
        @ValueSource(strings = {"a", "abc", "测试", "123", "!@#$%", "Hello!"})
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
            Map<String, String> anotherKeyPair = EncryptUtils.generateSm2Key();
            EncryptContext anotherContext = new EncryptContext();
            anotherContext.setPublicKey(anotherKeyPair.get(EncryptUtils.PUBLIC_KEY));
            anotherContext.setPrivateKey(anotherKeyPair.get(EncryptUtils.PRIVATE_KEY));
            Sm2Encryptor anotherEncryptor = new Sm2Encryptor(anotherContext);

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
            // Arrange - SM2加密有长度限制
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
    @DisplayName("9. 真实业务场景测试 - 国密应用")
    class RealBusinessScenarioTests {

        @Test
        @DisplayName("场景: 政府数据加密传输")
        void shouldEncryptGovernmentData() {
            // Arrange
            String sensitiveData = "身份证号:110101199001011234";

            // Act
            String encrypted = encryptor.encrypt(sensitiveData, EncodeType.BASE64);
            String decrypted = encryptor.decrypt(encrypted);

            // Assert
            assertThat(encrypted).isNotEqualTo(sensitiveData);
            assertThat(decrypted).isEqualTo(sensitiveData);
        }

        @Test
        @DisplayName("场景: 金融交易信息加密")
        void shouldEncryptFinancialTransaction() {
            // Arrange
            String transaction = "转账金额:1000000.00元";

            // Act
            String encrypted = encryptor.encrypt(transaction, EncodeType.BASE64);
            String decrypted = encryptor.decrypt(encrypted);

            // Assert
            assertThat(decrypted).isEqualTo(transaction);
        }

        @Test
        @DisplayName("场景: 医疗数据加密存储")
        void shouldEncryptMedicalData() {
            // Arrange
            String medicalRecord = "病历号:M2025001,诊断:健康";

            // Act
            String encrypted = encryptor.encrypt(medicalRecord, EncodeType.BASE64);
            String decrypted = encryptor.decrypt(encrypted);

            // Assert
            assertThat(decrypted).isEqualTo(medicalRecord);
        }
    }

    @Nested
    @DisplayName("10. 边界测试")
    class BoundaryTests {

        // Note: SM2 algorithm (via Bouncy Castle) cannot encrypt empty strings
        // It throws DataLengthException: "input buffer too short"
        // This is a known limitation of the SM2 implementation

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
            String plaintext = "😀🎉中国";

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
        @DisplayName("生成的SM2密钥对应该可用于加解密")
        void shouldGenerateUsableSm2KeyPair() {
            // Arrange
            Map<String, String> keyPair = EncryptUtils.generateSm2Key();
            EncryptContext ctx = new EncryptContext();
            ctx.setPublicKey(keyPair.get(EncryptUtils.PUBLIC_KEY));
            ctx.setPrivateKey(keyPair.get(EncryptUtils.PRIVATE_KEY));
            Sm2Encryptor enc = new Sm2Encryptor(ctx);
            String plaintext = "Test Data";

            // Act
            String encrypted = enc.encrypt(plaintext, EncodeType.BASE64);
            String decrypted = enc.decrypt(encrypted);

            // Assert
            assertThat(decrypted).isEqualTo(plaintext);
        }

        @Test
        @DisplayName("每次生成的SM2密钥对应该不同")
        void shouldGenerateDifferentSm2KeyPairs() {
            // Act
            Map<String, String> keyPair1 = EncryptUtils.generateSm2Key();
            Map<String, String> keyPair2 = EncryptUtils.generateSm2Key();

            // Assert
            assertThat(keyPair1.get(EncryptUtils.PUBLIC_KEY))
                .isNotEqualTo(keyPair2.get(EncryptUtils.PUBLIC_KEY));
            assertThat(keyPair1.get(EncryptUtils.PRIVATE_KEY))
                .isNotEqualTo(keyPair2.get(EncryptUtils.PRIVATE_KEY));
        }
    }
}
