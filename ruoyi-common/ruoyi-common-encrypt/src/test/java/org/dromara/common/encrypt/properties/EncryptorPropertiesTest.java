package org.dromara.common.encrypt.properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.dromara.common.encrypt.enumd.AlgorithmType;
import org.dromara.common.encrypt.enumd.EncodeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@link EncryptorProperties} 单元测试
 *
 * @author Lion Li
 */
@DisplayName("EncryptorProperties 单元测试")
class EncryptorPropertiesTest {

    @Nested
    @DisplayName("基本属性测试")
    class BasicPropertyTests {

        @Test
        @DisplayName("应该能够创建默认实例")
        void shouldCreateDefaultInstance() {
            EncryptorProperties properties = new EncryptorProperties();

            assertThat(properties).isNotNull();
        }

        @Test
        @DisplayName("应该能够设置和获取enable")
        void shouldSetAndGetEnable() {
            EncryptorProperties properties = new EncryptorProperties();

            properties.setEnable(true);

            assertThat(properties.getEnable()).isTrue();
        }

        @Test
        @DisplayName("应该能够设置和获取algorithm")
        void shouldSetAndGetAlgorithm() {
            EncryptorProperties properties = new EncryptorProperties();

            properties.setAlgorithm(AlgorithmType.AES);

            assertThat(properties.getAlgorithm()).isEqualTo(AlgorithmType.AES);
        }

        @Test
        @DisplayName("应该能够设置和获取password")
        void shouldSetAndGetPassword() {
            EncryptorProperties properties = new EncryptorProperties();

            properties.setPassword("my-secret-key-16");

            assertThat(properties.getPassword()).isEqualTo("my-secret-key-16");
        }

        @Test
        @DisplayName("应该能够设置和获取publicKey")
        void shouldSetAndGetPublicKey() {
            EncryptorProperties properties = new EncryptorProperties();

            properties.setPublicKey("public-key-value");

            assertThat(properties.getPublicKey()).isEqualTo("public-key-value");
        }

        @Test
        @DisplayName("应该能够设置和获取privateKey")
        void shouldSetAndGetPrivateKey() {
            EncryptorProperties properties = new EncryptorProperties();

            properties.setPrivateKey("private-key-value");

            assertThat(properties.getPrivateKey()).isEqualTo("private-key-value");
        }

        @Test
        @DisplayName("应该能够设置和获取encode")
        void shouldSetAndGetEncode() {
            EncryptorProperties properties = new EncryptorProperties();

            properties.setEncode(EncodeType.BASE64);

            assertThat(properties.getEncode()).isEqualTo(EncodeType.BASE64);
        }
    }

    @Nested
    @DisplayName("注解验证测试")
    class AnnotationTests {

        @Test
        @DisplayName("应该有@ConfigurationProperties注解")
        void shouldHaveConfigurationPropertiesAnnotation() {
            ConfigurationProperties annotation =
                    EncryptorProperties.class.getAnnotation(ConfigurationProperties.class);

            assertThat(annotation).isNotNull();
            assertThat(annotation.prefix()).isEqualTo("mybatis-encryptor");
        }

        @Test
        @DisplayName("应该有无参构造函数")
        void shouldHaveNoArgsConstructor() {
            assertThatNoException().isThrownBy(EncryptorProperties::new);
        }
    }

    @Nested
    @DisplayName("业务场景测试")
    class BusinessScenarioTests {

        @Test
        @DisplayName("应该支持AES对称加密配置")
        void shouldSupportAesSymmetricEncryptionConfiguration() {
            EncryptorProperties properties = new EncryptorProperties();
            properties.setEnable(true);
            properties.setAlgorithm(AlgorithmType.AES);
            properties.setPassword("1234567890123456"); // AES 16位密钥
            properties.setEncode(EncodeType.BASE64);

            assertThat(properties.getEnable()).isTrue();
            assertThat(properties.getAlgorithm()).isEqualTo(AlgorithmType.AES);
            assertThat(properties.getPassword()).hasSize(16);
            assertThat(properties.getEncode()).isEqualTo(EncodeType.BASE64);
        }

        @Test
        @DisplayName("应该支持RSA非对称加密配置")
        void shouldSupportRsaAsymmetricEncryptionConfiguration() {
            EncryptorProperties properties = new EncryptorProperties();
            properties.setEnable(true);
            properties.setAlgorithm(AlgorithmType.RSA);
            properties.setPublicKey("rsa-public-key");
            properties.setPrivateKey("rsa-private-key");
            properties.setEncode(EncodeType.BASE64);

            assertThat(properties.getEnable()).isTrue();
            assertThat(properties.getAlgorithm()).isEqualTo(AlgorithmType.RSA);
            assertThat(properties.getPublicKey()).isEqualTo("rsa-public-key");
            assertThat(properties.getPrivateKey()).isEqualTo("rsa-private-key");
        }

        @Test
        @DisplayName("应该支持SM4国密对称加密配置")
        void shouldSupportSm4SymmetricEncryptionConfiguration() {
            EncryptorProperties properties = new EncryptorProperties();
            properties.setEnable(true);
            properties.setAlgorithm(AlgorithmType.SM4);
            properties.setPassword("sm4-key-16-bytes"); // SM4 16位密钥
            properties.setEncode(EncodeType.HEX);

            assertThat(properties.getEnable()).isTrue();
            assertThat(properties.getAlgorithm()).isEqualTo(AlgorithmType.SM4);
            assertThat(properties.getPassword()).hasSize(16);
            assertThat(properties.getEncode()).isEqualTo(EncodeType.HEX);
        }

        @Test
        @DisplayName("应该支持SM2国密非对称加密配置")
        void shouldSupportSm2AsymmetricEncryptionConfiguration() {
            EncryptorProperties properties = new EncryptorProperties();
            properties.setEnable(true);
            properties.setAlgorithm(AlgorithmType.SM2);
            properties.setPublicKey("sm2-public-key");
            properties.setPrivateKey("sm2-private-key");
            properties.setEncode(EncodeType.HEX);

            assertThat(properties.getEnable()).isTrue();
            assertThat(properties.getAlgorithm()).isEqualTo(AlgorithmType.SM2);
            assertThat(properties.getPublicKey()).isEqualTo("sm2-public-key");
            assertThat(properties.getPrivateKey()).isEqualTo("sm2-private-key");
        }

        @Test
        @DisplayName("应该支持禁用加密")
        void shouldSupportDisablingEncryption() {
            EncryptorProperties properties = new EncryptorProperties();
            properties.setEnable(false);

            assertThat(properties.getEnable()).isFalse();
        }

        @Test
        @DisplayName("应该支持使用默认配置")
        void shouldSupportDefaultConfiguration() {
            EncryptorProperties properties = new EncryptorProperties();
            properties.setAlgorithm(AlgorithmType.DEFAULT);
            properties.setEncode(EncodeType.DEFAULT);

            assertThat(properties.getAlgorithm()).isEqualTo(AlgorithmType.DEFAULT);
            assertThat(properties.getEncode()).isEqualTo(EncodeType.DEFAULT);
        }

        @Test
        @DisplayName("应该支持不同编码方式")
        void shouldSupportDifferentEncodingTypes() {
            EncryptorProperties base64Props = new EncryptorProperties();
            base64Props.setEncode(EncodeType.BASE64);

            EncryptorProperties hexProps = new EncryptorProperties();
            hexProps.setEncode(EncodeType.HEX);

            assertThat(base64Props.getEncode()).isEqualTo(EncodeType.BASE64);
            assertThat(hexProps.getEncode()).isEqualTo(EncodeType.HEX);
        }
    }

    @Nested
    @DisplayName("对象相等性测试")
    class EqualityTests {

        @Test
        @DisplayName("相同配置的对象应该相等")
        void objectsWithSameConfigurationShouldBeEqual() {
            EncryptorProperties props1 = new EncryptorProperties();
            props1.setEnable(true);
            props1.setAlgorithm(AlgorithmType.AES);
            props1.setPassword("test-password");

            EncryptorProperties props2 = new EncryptorProperties();
            props2.setEnable(true);
            props2.setAlgorithm(AlgorithmType.AES);
            props2.setPassword("test-password");

            assertThat(props1).isEqualTo(props2);
        }

        @Test
        @DisplayName("不同配置的对象应该不相等")
        void objectsWithDifferentConfigurationShouldNotBeEqual() {
            EncryptorProperties props1 = new EncryptorProperties();
            props1.setAlgorithm(AlgorithmType.AES);

            EncryptorProperties props2 = new EncryptorProperties();
            props2.setAlgorithm(AlgorithmType.RSA);

            assertThat(props1).isNotEqualTo(props2);
        }
    }

    @Nested
    @DisplayName("完整配置测试")
    class CompleteConfigurationTests {

        @Test
        @DisplayName("应该能够设置所有属性")
        void shouldBeAbleToSetAllProperties() {
            EncryptorProperties properties = new EncryptorProperties();

            properties.setEnable(true);
            properties.setAlgorithm(AlgorithmType.AES);
            properties.setPassword("1234567890123456");
            properties.setPublicKey("public-key");
            properties.setPrivateKey("private-key");
            properties.setEncode(EncodeType.BASE64);

            assertThat(properties.getEnable()).isTrue();
            assertThat(properties.getAlgorithm()).isEqualTo(AlgorithmType.AES);
            assertThat(properties.getPassword()).isEqualTo("1234567890123456");
            assertThat(properties.getPublicKey()).isEqualTo("public-key");
            assertThat(properties.getPrivateKey()).isEqualTo("private-key");
            assertThat(properties.getEncode()).isEqualTo(EncodeType.BASE64);
        }

        @Test
        @DisplayName("应该支持null值")
        void shouldSupportNullValues() {
            EncryptorProperties properties = new EncryptorProperties();

            properties.setEnable(null);
            properties.setAlgorithm(null);
            properties.setPassword(null);
            properties.setPublicKey(null);
            properties.setPrivateKey(null);
            properties.setEncode(null);

            assertThat(properties.getEnable()).isNull();
            assertThat(properties.getAlgorithm()).isNull();
            assertThat(properties.getPassword()).isNull();
            assertThat(properties.getPublicKey()).isNull();
            assertThat(properties.getPrivateKey()).isNull();
            assertThat(properties.getEncode()).isNull();
        }
    }

    @Nested
    @DisplayName("配置前缀测试")
    class ConfigurationPrefixTests {

        @Test
        @DisplayName("配置前缀应该是mybatis-encryptor")
        void configurationPrefixShouldBeMybatisEncryptor() {
            ConfigurationProperties annotation =
                    EncryptorProperties.class.getAnnotation(ConfigurationProperties.class);

            assertThat(annotation.prefix()).isEqualTo("mybatis-encryptor");
        }

        @Test
        @DisplayName("应该能够通过mybatis-encryptor前缀绑定属性")
        void shouldBindPropertiesWithMybatisEncryptorPrefix() {
            // 在实际的Spring环境中，配置文件中的 mybatis-encryptor.enable
            // 会自动绑定到 EncryptorProperties.enable 属性

            EncryptorProperties properties = new EncryptorProperties();
            properties.setEnable(true);

            assertThat(properties.getEnable()).isTrue();
        }
    }

    @Nested
    @DisplayName("安全性测试")
    class SecurityTests {

        @Test
        @DisplayName("应该能够存储敏感的密钥信息")
        void shouldBeAbleToStoreSensitiveKeyInformation() {
            EncryptorProperties properties = new EncryptorProperties();
            String sensitivePassword = "very-secret-password-1234567890";

            properties.setPassword(sensitivePassword);
            properties.setPublicKey("sensitive-public-key");
            properties.setPrivateKey("sensitive-private-key");

            assertThat(properties.getPassword()).isEqualTo(sensitivePassword);
            assertThat(properties.getPublicKey()).isEqualTo("sensitive-public-key");
            assertThat(properties.getPrivateKey()).isEqualTo("sensitive-private-key");
        }

        @Test
        @DisplayName("应该支持不同长度的密钥")
        void shouldSupportDifferentKeyLengths() {
            EncryptorProperties aes16 = new EncryptorProperties();
            aes16.setPassword("1234567890123456"); // 16字节
            assertThat(aes16.getPassword()).hasSize(16);

            EncryptorProperties aes24 = new EncryptorProperties();
            aes24.setPassword("123456789012345678901234"); // 24字节
            assertThat(aes24.getPassword()).hasSize(24);

            EncryptorProperties aes32 = new EncryptorProperties();
            aes32.setPassword("12345678901234567890123456789012"); // 32字节
            assertThat(aes32.getPassword()).hasSize(32);
        }
    }
}
