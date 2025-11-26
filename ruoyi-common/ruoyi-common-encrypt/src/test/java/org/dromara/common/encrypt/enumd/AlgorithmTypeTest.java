package org.dromara.common.encrypt.enumd;

import static org.assertj.core.api.Assertions.assertThat;

import org.dromara.common.encrypt.core.encryptor.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link AlgorithmType} 单元测试
 *
 * @author Lion Li
 */
@DisplayName("AlgorithmType 单元测试")
class AlgorithmTypeTest {

    @Nested
    @DisplayName("枚举常量测试")
    class EnumConstantsTests {

        @Test
        @DisplayName("应该有6个枚举常量")
        void shouldHaveSixConstants() {
            AlgorithmType[] values = AlgorithmType.values();

            assertThat(values).hasSize(6);
        }

        @Test
        @DisplayName("应该包含DEFAULT常量")
        void shouldContainDefault() {
            AlgorithmType type = AlgorithmType.DEFAULT;

            assertThat(type).isNotNull();
            assertThat(type.getClazz()).isNull();
        }

        @Test
        @DisplayName("应该包含BASE64常量")
        void shouldContainBase64() {
            AlgorithmType type = AlgorithmType.BASE64;

            assertThat(type).isNotNull();
            assertThat(type.getClazz()).isEqualTo(Base64Encryptor.class);
        }

        @Test
        @DisplayName("应该包含AES常量")
        void shouldContainAes() {
            AlgorithmType type = AlgorithmType.AES;

            assertThat(type).isNotNull();
            assertThat(type.getClazz()).isEqualTo(AesEncryptor.class);
        }

        @Test
        @DisplayName("应该包含RSA常量")
        void shouldContainRsa() {
            AlgorithmType type = AlgorithmType.RSA;

            assertThat(type).isNotNull();
            assertThat(type.getClazz()).isEqualTo(RsaEncryptor.class);
        }

        @Test
        @DisplayName("应该包含SM2常量")
        void shouldContainSm2() {
            AlgorithmType type = AlgorithmType.SM2;

            assertThat(type).isNotNull();
            assertThat(type.getClazz()).isEqualTo(Sm2Encryptor.class);
        }

        @Test
        @DisplayName("应该包含SM4常量")
        void shouldContainSm4() {
            AlgorithmType type = AlgorithmType.SM4;

            assertThat(type).isNotNull();
            assertThat(type.getClazz()).isEqualTo(Sm4Encryptor.class);
        }
    }

    @Nested
    @DisplayName("getClazz() 方法测试")
    class GetClazzTests {

        @Test
        @DisplayName("DEFAULT应该返回null")
        void defaultShouldReturnNull() {
            assertThat(AlgorithmType.DEFAULT.getClazz()).isNull();
        }

        @Test
        @DisplayName("BASE64应该返回Base64Encryptor类")
        void base64ShouldReturnBase64EncryptorClass() {
            Class<? extends AbstractEncryptor> clazz = AlgorithmType.BASE64.getClazz();

            assertThat(clazz).isEqualTo(Base64Encryptor.class);
            assertThat(AbstractEncryptor.class).isAssignableFrom(clazz);
        }

        @Test
        @DisplayName("AES应该返回AesEncryptor类")
        void aesShouldReturnAesEncryptorClass() {
            Class<? extends AbstractEncryptor> clazz = AlgorithmType.AES.getClazz();

            assertThat(clazz).isEqualTo(AesEncryptor.class);
            assertThat(AbstractEncryptor.class).isAssignableFrom(clazz);
        }

        @Test
        @DisplayName("RSA应该返回RsaEncryptor类")
        void rsaShouldReturnRsaEncryptorClass() {
            Class<? extends AbstractEncryptor> clazz = AlgorithmType.RSA.getClazz();

            assertThat(clazz).isEqualTo(RsaEncryptor.class);
            assertThat(AbstractEncryptor.class).isAssignableFrom(clazz);
        }

        @Test
        @DisplayName("SM2应该返回Sm2Encryptor类")
        void sm2ShouldReturnSm2EncryptorClass() {
            Class<? extends AbstractEncryptor> clazz = AlgorithmType.SM2.getClazz();

            assertThat(clazz).isEqualTo(Sm2Encryptor.class);
            assertThat(AbstractEncryptor.class).isAssignableFrom(clazz);
        }

        @Test
        @DisplayName("SM4应该返回Sm4Encryptor类")
        void sm4ShouldReturnSm4EncryptorClass() {
            Class<? extends AbstractEncryptor> clazz = AlgorithmType.SM4.getClazz();

            assertThat(clazz).isEqualTo(Sm4Encryptor.class);
            assertThat(AbstractEncryptor.class).isAssignableFrom(clazz);
        }

        @Test
        @DisplayName("所有非DEFAULT的枚举应该返回非null的类")
        void allNonDefaultShouldReturnNonNullClass() {
            for (AlgorithmType type : AlgorithmType.values()) {
                if (type != AlgorithmType.DEFAULT) {
                    assertThat(type.getClazz()).isNotNull();
                    assertThat(AbstractEncryptor.class).isAssignableFrom(type.getClazz());
                }
            }
        }
    }

    @Nested
    @DisplayName("valueOf() 方法测试")
    class ValueOfTests {

        @Test
        @DisplayName("应该能够通过名称获取DEFAULT")
        void shouldGetDefaultByName() {
            AlgorithmType type = AlgorithmType.valueOf("DEFAULT");

            assertThat(type).isEqualTo(AlgorithmType.DEFAULT);
        }

        @Test
        @DisplayName("应该能够通过名称获取BASE64")
        void shouldGetBase64ByName() {
            AlgorithmType type = AlgorithmType.valueOf("BASE64");

            assertThat(type).isEqualTo(AlgorithmType.BASE64);
        }

        @Test
        @DisplayName("应该能够通过名称获取AES")
        void shouldGetAesByName() {
            AlgorithmType type = AlgorithmType.valueOf("AES");

            assertThat(type).isEqualTo(AlgorithmType.AES);
        }

        @Test
        @DisplayName("应该能够通过名称获取RSA")
        void shouldGetRsaByName() {
            AlgorithmType type = AlgorithmType.valueOf("RSA");

            assertThat(type).isEqualTo(AlgorithmType.RSA);
        }

        @Test
        @DisplayName("应该能够通过名称获取SM2")
        void shouldGetSm2ByName() {
            AlgorithmType type = AlgorithmType.valueOf("SM2");

            assertThat(type).isEqualTo(AlgorithmType.SM2);
        }

        @Test
        @DisplayName("应该能够通过名称获取SM4")
        void shouldGetSm4ByName() {
            AlgorithmType type = AlgorithmType.valueOf("SM4");

            assertThat(type).isEqualTo(AlgorithmType.SM4);
        }
    }

    @Nested
    @DisplayName("枚举比较测试")
    class EnumComparisonTests {

        @Test
        @DisplayName("相同枚举常量应该相等")
        void sameConstantsShouldBeEqual() {
            assertThat(AlgorithmType.BASE64).isEqualTo(AlgorithmType.BASE64);
            assertThat(AlgorithmType.AES).isEqualTo(AlgorithmType.AES);
        }

        @Test
        @DisplayName("不同枚举常量应该不相等")
        void differentConstantsShouldNotBeEqual() {
            assertThat(AlgorithmType.BASE64).isNotEqualTo(AlgorithmType.AES);
            assertThat(AlgorithmType.RSA).isNotEqualTo(AlgorithmType.SM2);
        }

        @Test
        @DisplayName("枚举常量应该支持switch语句")
        void shouldSupportSwitchStatement() {
            AlgorithmType type = AlgorithmType.AES;

            String result =
                    switch (type) {
                        case DEFAULT -> "default";
                        case BASE64 -> "base64";
                        case AES -> "aes";
                        case RSA -> "rsa";
                        case SM2 -> "sm2";
                        case SM4 -> "sm4";
                    };

            assertThat(result).isEqualTo("aes");
        }
    }

    @Nested
    @DisplayName("业务场景测试")
    class BusinessScenarioTests {

        @Test
        @DisplayName("应该支持对称加密算法")
        void shouldSupportSymmetricAlgorithms() {
            // AES 和 SM4 是对称加密算法
            assertThat(AlgorithmType.AES.getClazz()).isEqualTo(AesEncryptor.class);
            assertThat(AlgorithmType.SM4.getClazz()).isEqualTo(Sm4Encryptor.class);
        }

        @Test
        @DisplayName("应该支持非对称加密算法")
        void shouldSupportAsymmetricAlgorithms() {
            // RSA 和 SM2 是非对称加密算法
            assertThat(AlgorithmType.RSA.getClazz()).isEqualTo(RsaEncryptor.class);
            assertThat(AlgorithmType.SM2.getClazz()).isEqualTo(Sm2Encryptor.class);
        }

        @Test
        @DisplayName("应该支持编码算法")
        void shouldSupportEncodingAlgorithm() {
            // BASE64 是编码算法
            assertThat(AlgorithmType.BASE64.getClazz()).isEqualTo(Base64Encryptor.class);
        }

        @Test
        @DisplayName("应该支持国密算法")
        void shouldSupportChineseNationalCryptoAlgorithms() {
            // SM2 和 SM4 是国密算法
            assertThat(AlgorithmType.SM2.getClazz()).isEqualTo(Sm2Encryptor.class);
            assertThat(AlgorithmType.SM4.getClazz()).isEqualTo(Sm4Encryptor.class);
        }

        @Test
        @DisplayName("应该支持默认配置")
        void shouldSupportDefaultConfiguration() {
            // DEFAULT 表示使用配置文件中的默认算法
            assertThat(AlgorithmType.DEFAULT.getClazz()).isNull();
        }
    }

    @Nested
    @DisplayName("枚举序列化测试")
    class EnumSerializationTests {

        @Test
        @DisplayName("枚举name()应该返回常量名称")
        void nameShouldReturnConstantName() {
            assertThat(AlgorithmType.BASE64.name()).isEqualTo("BASE64");
            assertThat(AlgorithmType.AES.name()).isEqualTo("AES");
            assertThat(AlgorithmType.RSA.name()).isEqualTo("RSA");
        }

        @Test
        @DisplayName("枚举ordinal()应该返回正确的序号")
        void ordinalShouldReturnCorrectIndex() {
            assertThat(AlgorithmType.DEFAULT.ordinal()).isZero();
            assertThat(AlgorithmType.BASE64.ordinal()).isEqualTo(1);
            assertThat(AlgorithmType.AES.ordinal()).isEqualTo(2);
            assertThat(AlgorithmType.RSA.ordinal()).isEqualTo(3);
            assertThat(AlgorithmType.SM2.ordinal()).isEqualTo(4);
            assertThat(AlgorithmType.SM4.ordinal()).isEqualTo(5);
        }

        @Test
        @DisplayName("枚举toString()应该返回常量名称")
        void toStringShouldReturnConstantName() {
            assertThat(AlgorithmType.BASE64.toString()).isEqualTo("BASE64");
            assertThat(AlgorithmType.AES.toString()).isEqualTo("AES");
        }
    }
}
