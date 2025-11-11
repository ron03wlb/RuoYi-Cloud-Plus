package org.dromara.auth.enums;

import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import org.dromara.auth.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 验证码枚举测试
 * <p>
 * 测试 CaptchaType 和 CaptchaCategory 枚举类
 * </p>
 *
 * @author Test Team
 */
@DisplayName("验证码枚举测试")
class CaptchaEnumsTest extends BaseUnitTest {

    @Nested
    @DisplayName("1. CaptchaType 验证码类型测试")
    class CaptchaTypeTests {

        @Test
        @DisplayName("应该有两种验证码类型 - MATH 和 CHAR")
        void shouldHaveTwoTypes() {
            // Arrange & Act
            CaptchaType[] types = CaptchaType.values();

            // Assert
            assertThat(types)
                .hasSize(2)
                .contains(CaptchaType.MATH, CaptchaType.CHAR);
        }

        @Test
        @DisplayName("MATH 类型应该使用 MathGenerator")
        void mathTypeShouldUseMathGenerator() {
            // Arrange & Act
            Class<?> generatorClass = CaptchaType.MATH.getClazz();

            // Assert
            assertThat(generatorClass).isEqualTo(MathGenerator.class);
        }

        @Test
        @DisplayName("CHAR 类型应该使用 RandomGenerator")
        void charTypeShouldUseRandomGenerator() {
            // Arrange & Act
            Class<?> generatorClass = CaptchaType.CHAR.getClazz();

            // Assert
            assertThat(generatorClass).isEqualTo(RandomGenerator.class);
        }

        @Test
        @DisplayName("valueOf() 应该能够正确转换字符串")
        void valueOfShouldConvertStringCorrectly() {
            // Arrange & Act
            CaptchaType mathType = CaptchaType.valueOf("MATH");
            CaptchaType charType = CaptchaType.valueOf("CHAR");

            // Assert
            assertThat(mathType).isEqualTo(CaptchaType.MATH);
            assertThat(charType).isEqualTo(CaptchaType.CHAR);
        }

        @ParameterizedTest
        @EnumSource(CaptchaType.class)
        @DisplayName("每个类型都应该有有效的 CodeGenerator 类")
        void eachTypeShouldHaveValidCodeGeneratorClass(CaptchaType type) {
            // Act & Assert
            assertThat(type.getClazz())
                .isNotNull()
                .isNotInterface();
        }

        @Test
        @DisplayName("枚举名称应该符合命名规范")
        void enumNamesShouldFollowConvention() {
            // Arrange & Act & Assert
            assertThat(CaptchaType.MATH.name()).isEqualTo("MATH");
            assertThat(CaptchaType.CHAR.name()).isEqualTo("CHAR");
        }
    }

    @Nested
    @DisplayName("2. CaptchaCategory 验证码类别测试")
    class CaptchaCategoryTests {

        @Test
        @DisplayName("应该有三种验证码类别 - LINE, CIRCLE, SHEAR")
        void shouldHaveThreeCategories() {
            // Arrange & Act
            CaptchaCategory[] categories = CaptchaCategory.values();

            // Assert
            assertThat(categories)
                .hasSize(3)
                .contains(CaptchaCategory.LINE, CaptchaCategory.CIRCLE, CaptchaCategory.SHEAR);
        }

        @Test
        @DisplayName("LINE 类别应该使用 LineCaptcha")
        void lineCategoryShouldUseLineCaptcha() {
            // Arrange & Act
            Class<?> captchaClass = CaptchaCategory.LINE.getClazz();

            // Assert
            assertThat(captchaClass).isEqualTo(LineCaptcha.class);
        }

        @Test
        @DisplayName("CIRCLE 类别应该使用 CircleCaptcha")
        void circleCategoryShouldUseCircleCaptcha() {
            // Arrange & Act
            Class<?> captchaClass = CaptchaCategory.CIRCLE.getClazz();

            // Assert
            assertThat(captchaClass).isEqualTo(CircleCaptcha.class);
        }

        @Test
        @DisplayName("SHEAR 类别应该使用 ShearCaptcha")
        void shearCategoryShouldUseShearCaptcha() {
            // Arrange & Act
            Class<?> captchaClass = CaptchaCategory.SHEAR.getClazz();

            // Assert
            assertThat(captchaClass).isEqualTo(ShearCaptcha.class);
        }

        @Test
        @DisplayName("valueOf() 应该能够正确转换字符串")
        void valueOfShouldConvertStringCorrectly() {
            // Arrange & Act
            CaptchaCategory lineCategory = CaptchaCategory.valueOf("LINE");
            CaptchaCategory circleCategory = CaptchaCategory.valueOf("CIRCLE");
            CaptchaCategory shearCategory = CaptchaCategory.valueOf("SHEAR");

            // Assert
            assertThat(lineCategory).isEqualTo(CaptchaCategory.LINE);
            assertThat(circleCategory).isEqualTo(CaptchaCategory.CIRCLE);
            assertThat(shearCategory).isEqualTo(CaptchaCategory.SHEAR);
        }

        @ParameterizedTest
        @EnumSource(CaptchaCategory.class)
        @DisplayName("每个类别都应该有有效的 Captcha 类")
        void eachCategoryShouldHaveValidCaptchaClass(CaptchaCategory category) {
            // Act & Assert
            assertThat(category.getClazz())
                .isNotNull()
                .isNotInterface();
        }

        @Test
        @DisplayName("枚举名称应该符合命名规范")
        void enumNamesShouldFollowConvention() {
            // Arrange & Act & Assert
            assertThat(CaptchaCategory.LINE.name()).isEqualTo("LINE");
            assertThat(CaptchaCategory.CIRCLE.name()).isEqualTo("CIRCLE");
            assertThat(CaptchaCategory.SHEAR.name()).isEqualTo("SHEAR");
        }

        @Test
        @DisplayName("枚举值顺序应该保持一致")
        void enumOrderShouldBeConsistent() {
            // Arrange & Act
            CaptchaCategory[] categories = CaptchaCategory.values();

            // Assert
            assertThat(categories[0]).isEqualTo(CaptchaCategory.LINE);
            assertThat(categories[1]).isEqualTo(CaptchaCategory.CIRCLE);
            assertThat(categories[2]).isEqualTo(CaptchaCategory.SHEAR);
        }
    }

    @Nested
    @DisplayName("3. 枚举集成测试")
    class EnumIntegrationTests {

        @Test
        @DisplayName("CaptchaType 和 CaptchaCategory 应该可以协同工作")
        void captchaTypeAndCategoryShouldWorkTogether() {
            // Arrange
            CaptchaType type = CaptchaType.MATH;
            CaptchaCategory category = CaptchaCategory.LINE;

            // Act & Assert - 验证两个枚举都有有效的类
            assertThat(type.getClazz()).isNotNull();
            assertThat(category.getClazz()).isNotNull();
        }

        @Test
        @DisplayName("所有枚举组合都应该有效")
        void allEnumCombinationsShouldBeValid() {
            // Arrange & Act
            for (CaptchaType type : CaptchaType.values()) {
                for (CaptchaCategory category : CaptchaCategory.values()) {
                    // Assert - 验证所有组合都有效
                    assertThat(type.getClazz()).isNotNull();
                    assertThat(category.getClazz()).isNotNull();
                }
            }
        }
    }
}
