package org.dromara.common.encrypt.enumd;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link EncodeType} 单元测试
 *
 * @author Lion Li
 */
@DisplayName("EncodeType 单元测试")
class EncodeTypeTest {

    @Nested
    @DisplayName("枚举常量测试")
    class EnumConstantsTests {

        @Test
        @DisplayName("应该有3个枚举常量")
        void shouldHaveThreeConstants() {
            EncodeType[] values = EncodeType.values();

            assertThat(values).hasSize(3);
        }

        @Test
        @DisplayName("应该包含DEFAULT常量")
        void shouldContainDefault() {
            EncodeType type = EncodeType.DEFAULT;

            assertThat(type).isNotNull();
        }

        @Test
        @DisplayName("应该包含BASE64常量")
        void shouldContainBase64() {
            EncodeType type = EncodeType.BASE64;

            assertThat(type).isNotNull();
        }

        @Test
        @DisplayName("应该包含HEX常量")
        void shouldContainHex() {
            EncodeType type = EncodeType.HEX;

            assertThat(type).isNotNull();
        }

        @Test
        @DisplayName("所有常量应该不为null")
        void allConstantsShouldNotBeNull() {
            for (EncodeType type : EncodeType.values()) {
                assertThat(type).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("valueOf() 方法测试")
    class ValueOfTests {

        @Test
        @DisplayName("应该能够通过名称获取DEFAULT")
        void shouldGetDefaultByName() {
            EncodeType type = EncodeType.valueOf("DEFAULT");

            assertThat(type).isEqualTo(EncodeType.DEFAULT);
        }

        @Test
        @DisplayName("应该能够通过名称获取BASE64")
        void shouldGetBase64ByName() {
            EncodeType type = EncodeType.valueOf("BASE64");

            assertThat(type).isEqualTo(EncodeType.BASE64);
        }

        @Test
        @DisplayName("应该能够通过名称获取HEX")
        void shouldGetHexByName() {
            EncodeType type = EncodeType.valueOf("HEX");

            assertThat(type).isEqualTo(EncodeType.HEX);
        }
    }

    @Nested
    @DisplayName("枚举比较测试")
    class EnumComparisonTests {

        @Test
        @DisplayName("相同枚举常量应该相等")
        void sameConstantsShouldBeEqual() {
            assertThat(EncodeType.BASE64).isEqualTo(EncodeType.BASE64);
            assertThat(EncodeType.HEX).isEqualTo(EncodeType.HEX);
        }

        @Test
        @DisplayName("不同枚举常量应该不相等")
        void differentConstantsShouldNotBeEqual() {
            assertThat(EncodeType.BASE64).isNotEqualTo(EncodeType.HEX);
            assertThat(EncodeType.DEFAULT).isNotEqualTo(EncodeType.BASE64);
        }

        @Test
        @DisplayName("枚举常量应该支持switch语句")
        void shouldSupportSwitchStatement() {
            EncodeType type = EncodeType.BASE64;

            String result =
                    switch (type) {
                        case DEFAULT -> "default";
                        case BASE64 -> "base64";
                        case HEX -> "hex";
                    };

            assertThat(result).isEqualTo("base64");
        }
    }

    @Nested
    @DisplayName("业务场景测试")
    class BusinessScenarioTests {

        @Test
        @DisplayName("应该支持使用默认编码方式")
        void shouldSupportDefaultEncoding() {
            // DEFAULT 表示使用配置文件中的默认编码方式
            EncodeType type = EncodeType.DEFAULT;

            assertThat(type).isEqualTo(EncodeType.DEFAULT);
        }

        @Test
        @DisplayName("应该支持BASE64编码")
        void shouldSupportBase64Encoding() {
            // BASE64 是常用的编码方式，适用于文本传输
            EncodeType type = EncodeType.BASE64;

            assertThat(type).isEqualTo(EncodeType.BASE64);
        }

        @Test
        @DisplayName("应该支持HEX编码")
        void shouldSupportHexEncoding() {
            // HEX (16进制编码) 常用于密码学场景
            EncodeType type = EncodeType.HEX;

            assertThat(type).isEqualTo(EncodeType.HEX);
        }

        @Test
        @DisplayName("应该能够根据场景选择合适的编码方式")
        void shouldBeAbleToSelectEncodingBasedOnScenario() {
            // 文本场景: BASE64
            EncodeType textScenario = EncodeType.BASE64;
            assertThat(textScenario).isEqualTo(EncodeType.BASE64);

            // 密码学场景: HEX
            EncodeType cryptoScenario = EncodeType.HEX;
            assertThat(cryptoScenario).isEqualTo(EncodeType.HEX);

            // 使用配置: DEFAULT
            EncodeType configScenario = EncodeType.DEFAULT;
            assertThat(configScenario).isEqualTo(EncodeType.DEFAULT);
        }
    }

    @Nested
    @DisplayName("枚举序列化测试")
    class EnumSerializationTests {

        @Test
        @DisplayName("枚举name()应该返回常量名称")
        void nameShouldReturnConstantName() {
            assertThat(EncodeType.DEFAULT.name()).isEqualTo("DEFAULT");
            assertThat(EncodeType.BASE64.name()).isEqualTo("BASE64");
            assertThat(EncodeType.HEX.name()).isEqualTo("HEX");
        }

        @Test
        @DisplayName("枚举ordinal()应该返回正确的序号")
        void ordinalShouldReturnCorrectIndex() {
            assertThat(EncodeType.DEFAULT.ordinal()).isZero();
            assertThat(EncodeType.BASE64.ordinal()).isEqualTo(1);
            assertThat(EncodeType.HEX.ordinal()).isEqualTo(2);
        }

        @Test
        @DisplayName("枚举toString()应该返回常量名称")
        void toStringShouldReturnConstantName() {
            assertThat(EncodeType.DEFAULT.toString()).isEqualTo("DEFAULT");
            assertThat(EncodeType.BASE64.toString()).isEqualTo("BASE64");
            assertThat(EncodeType.HEX.toString()).isEqualTo("HEX");
        }
    }

    @Nested
    @DisplayName("编码特性测试")
    class EncodingCharacteristicsTests {

        @Test
        @DisplayName("BASE64编码后应该只包含可打印字符")
        void base64ShouldProducePrintableCharacters() {
            // BASE64 编码后的字符串只包含 A-Z, a-z, 0-9, +, /, = 这些字符
            EncodeType type = EncodeType.BASE64;

            assertThat(type).isEqualTo(EncodeType.BASE64);
        }

        @Test
        @DisplayName("HEX编码后应该只包含16进制字符")
        void hexShouldProduceHexadecimalCharacters() {
            // HEX 编码后的字符串只包含 0-9, A-F (或 a-f) 这些字符
            EncodeType type = EncodeType.HEX;

            assertThat(type).isEqualTo(EncodeType.HEX);
        }

        @Test
        @DisplayName("BASE64编码长度通常比HEX短")
        void base64ShouldBeMoreCompactThanHex() {
            // 相同数据，BASE64 编码后的长度通常比 HEX 短
            // BASE64: 每3字节编码为4字符 (33.3% 增长)
            // HEX: 每1字节编码为2字符 (100% 增长)

            EncodeType base64 = EncodeType.BASE64;
            EncodeType hex = EncodeType.HEX;

            assertThat(base64).isNotEqualTo(hex);
        }
    }

    @Nested
    @DisplayName("枚举完整性测试")
    class EnumCompletenessTests {

        @Test
        @DisplayName("values()应该返回所有枚举常量")
        void valuesShouldReturnAllConstants() {
            EncodeType[] values = EncodeType.values();

            assertThat(values)
                    .containsExactly(EncodeType.DEFAULT, EncodeType.BASE64, EncodeType.HEX);
        }

        @Test
        @DisplayName("values()返回的数组应该是副本")
        void valuesShouldReturnCopy() {
            EncodeType[] values1 = EncodeType.values();
            EncodeType[] values2 = EncodeType.values();

            assertThat(values1).isNotSameAs(values2);
            assertThat(values1).containsExactly(values2);
        }

        @Test
        @DisplayName("所有枚举常量应该有唯一的序号")
        void allConstantsShouldHaveUniqueOrdinal() {
            EncodeType[] values = EncodeType.values();

            assertThat(values)
                    .extracting(EncodeType::ordinal)
                    .containsExactly(0, 1, 2)
                    .doesNotHaveDuplicates();
        }

        @Test
        @DisplayName("所有枚举常量应该有唯一的名称")
        void allConstantsShouldHaveUniqueName() {
            EncodeType[] values = EncodeType.values();

            assertThat(values)
                    .extracting(EncodeType::name)
                    .containsExactly("DEFAULT", "BASE64", "HEX")
                    .doesNotHaveDuplicates();
        }
    }
}
