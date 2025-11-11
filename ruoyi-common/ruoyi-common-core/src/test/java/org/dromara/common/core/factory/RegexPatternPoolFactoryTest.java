package org.dromara.common.core.factory;

import org.dromara.common.core.BaseUnitTest;
import org.dromara.common.core.constant.RegexConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RegexPatternPoolFactory (正则表达式模式池工厂) 单元测试
 *
 * @author Test Team
 */
@DisplayName("RegexPatternPoolFactory (正则表达式模式池工厂) 单元测试")
class RegexPatternPoolFactoryTest extends BaseUnitTest {

    @Nested
    @DisplayName("1. Pattern 常量定义测试")
    class PatternConstantsTests {

        @Test
        @DisplayName("DICTIONARY_TYPE - 应该正确初始化字典类型Pattern")
        void shouldInitializeDictionaryTypePattern() {
            // Assert
            assertThat(RegexPatternPoolFactory.DICTIONARY_TYPE).isNotNull();
            assertThat(RegexPatternPoolFactory.DICTIONARY_TYPE).isInstanceOf(Pattern.class);
            assertThat(RegexPatternPoolFactory.DICTIONARY_TYPE.pattern())
                .isEqualTo(RegexConstants.DICTIONARY_TYPE);
        }

        @Test
        @DisplayName("ID_CARD_LAST_6 - 应该正确初始化身份证后6位Pattern")
        void shouldInitializeIdCardLast6Pattern() {
            // Assert
            assertThat(RegexPatternPoolFactory.ID_CARD_LAST_6).isNotNull();
            assertThat(RegexPatternPoolFactory.ID_CARD_LAST_6).isInstanceOf(Pattern.class);
            assertThat(RegexPatternPoolFactory.ID_CARD_LAST_6.pattern())
                .isEqualTo(RegexConstants.ID_CARD_LAST_6);
        }

        @Test
        @DisplayName("QQ_NUMBER - 应该正确初始化QQ号码Pattern")
        void shouldInitializeQQNumberPattern() {
            // Assert
            assertThat(RegexPatternPoolFactory.QQ_NUMBER).isNotNull();
            assertThat(RegexPatternPoolFactory.QQ_NUMBER).isInstanceOf(Pattern.class);
            assertThat(RegexPatternPoolFactory.QQ_NUMBER.pattern())
                .isEqualTo(RegexConstants.QQ_NUMBER);
        }

        @Test
        @DisplayName("POSTAL_CODE - 应该正确初始化邮政编码Pattern")
        void shouldInitializePostalCodePattern() {
            // Assert
            assertThat(RegexPatternPoolFactory.POSTAL_CODE).isNotNull();
            assertThat(RegexPatternPoolFactory.POSTAL_CODE).isInstanceOf(Pattern.class);
            assertThat(RegexPatternPoolFactory.POSTAL_CODE.pattern())
                .isEqualTo(RegexConstants.POSTAL_CODE);
        }

        @Test
        @DisplayName("ACCOUNT - 应该正确初始化注册账号Pattern")
        void shouldInitializeAccountPattern() {
            // Assert
            assertThat(RegexPatternPoolFactory.ACCOUNT).isNotNull();
            assertThat(RegexPatternPoolFactory.ACCOUNT).isInstanceOf(Pattern.class);
            assertThat(RegexPatternPoolFactory.ACCOUNT.pattern())
                .isEqualTo(RegexConstants.ACCOUNT);
        }

        @Test
        @DisplayName("PASSWORD - 应该正确初始化密码Pattern")
        void shouldInitializePasswordPattern() {
            // Assert
            assertThat(RegexPatternPoolFactory.PASSWORD).isNotNull();
            assertThat(RegexPatternPoolFactory.PASSWORD).isInstanceOf(Pattern.class);
            assertThat(RegexPatternPoolFactory.PASSWORD.pattern())
                .isEqualTo(RegexConstants.PASSWORD);
        }

        @Test
        @DisplayName("STATUS - 应该正确初始化状态Pattern")
        void shouldInitializeStatusPattern() {
            // Assert
            assertThat(RegexPatternPoolFactory.STATUS).isNotNull();
            assertThat(RegexPatternPoolFactory.STATUS).isInstanceOf(Pattern.class);
            assertThat(RegexPatternPoolFactory.STATUS.pattern())
                .isEqualTo(RegexConstants.STATUS);
        }
    }

    @Nested
    @DisplayName("2. Pattern 匹配功能测试")
    class PatternMatchingTests {

        @Test
        @DisplayName("DICTIONARY_TYPE - 应该正确匹配有效的字典类型")
        void shouldMatchValidDictionaryType() {
            // Arrange
            String validDict1 = "sys_user_status";
            String validDict2 = "menu_type";
            String invalidDict1 = "123_invalid"; // 数字开头
            String invalidDict2 = "Invalid_Type"; // 大写字母

            // Assert
            assertThat(RegexPatternPoolFactory.DICTIONARY_TYPE.matcher(validDict1).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.DICTIONARY_TYPE.matcher(validDict2).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.DICTIONARY_TYPE.matcher(invalidDict1).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.DICTIONARY_TYPE.matcher(invalidDict2).matches()).isFalse();
        }

        @Test
        @DisplayName("ID_CARD_LAST_6 - 应该正确匹配身份证后6位")
        void shouldMatchValidIdCardLast6() {
            // Arrange
            // 格式：日期(01-31) + 顺序码(3位数字) + 校验码(0-9或X)
            String valid1 = "150123"; // 15日 + 012 + 3
            String valid2 = "31123X"; // 31日 + 123 + X
            String valid3 = "010000"; // 01日 + 000 + 0
            String invalid1 = "12345"; // 5位（太短）
            String invalid2 = "1234567"; // 7位（太长）
            String invalid3 = "400001"; // 40日（无效日期）
            String invalid4 = "1512AB"; // 包含非法字母

            // Assert
            assertThat(RegexPatternPoolFactory.ID_CARD_LAST_6.matcher(valid1).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.ID_CARD_LAST_6.matcher(valid2).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.ID_CARD_LAST_6.matcher(valid3).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.ID_CARD_LAST_6.matcher(invalid1).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.ID_CARD_LAST_6.matcher(invalid2).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.ID_CARD_LAST_6.matcher(invalid3).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.ID_CARD_LAST_6.matcher(invalid4).matches()).isFalse();
        }

        @Test
        @DisplayName("QQ_NUMBER - 应该正确匹配有效的QQ号码")
        void shouldMatchValidQQNumber() {
            // Arrange
            // 格式：[1-9][0-9]\d{4,9} => 总长度6-11位
            String valid1 = "123456"; // 6位（最短）
            String valid2 = "123456789"; // 9位
            String valid3 = "12345678901"; // 11位（最长）
            String invalid1 = "12345"; // 5位（太短）
            String invalid2 = "123456789012"; // 12位（太长）
            String invalid3 = "012345"; // 以0开头（无效）
            String invalid4 = "12345a"; // 包含字母

            // Assert
            assertThat(RegexPatternPoolFactory.QQ_NUMBER.matcher(valid1).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.QQ_NUMBER.matcher(valid2).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.QQ_NUMBER.matcher(valid3).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.QQ_NUMBER.matcher(invalid1).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.QQ_NUMBER.matcher(invalid2).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.QQ_NUMBER.matcher(invalid3).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.QQ_NUMBER.matcher(invalid4).matches()).isFalse();
        }

        @Test
        @DisplayName("POSTAL_CODE - 应该正确匹配有效的邮政编码")
        void shouldMatchValidPostalCode() {
            // Arrange
            String valid1 = "100000"; // 北京
            String valid2 = "200000"; // 上海
            String valid3 = "518000"; // 深圳
            String invalid1 = "12345"; // 5位
            String invalid2 = "1234567"; // 7位
            String invalid3 = "12345a"; // 包含字母

            // Assert
            assertThat(RegexPatternPoolFactory.POSTAL_CODE.matcher(valid1).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.POSTAL_CODE.matcher(valid2).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.POSTAL_CODE.matcher(valid3).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.POSTAL_CODE.matcher(invalid1).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.POSTAL_CODE.matcher(invalid2).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.POSTAL_CODE.matcher(invalid3).matches()).isFalse();
        }

        @Test
        @DisplayName("ACCOUNT - 应该正确匹配有效的注册账号")
        void shouldMatchValidAccount() {
            // Arrange
            String valid1 = "user123"; // 字母开头 + 数字
            String valid2 = "test_user"; // 字母 + 下划线
            String valid3 = "admin"; // 纯字母
            String invalid1 = "123user"; // 数字开头
            String invalid2 = "user@test"; // 包含特殊字符
            String invalid3 = "ab"; // 太短（假设最短5位）

            // Assert
            assertThat(RegexPatternPoolFactory.ACCOUNT.matcher(valid1).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.ACCOUNT.matcher(valid2).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.ACCOUNT.matcher(valid3).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.ACCOUNT.matcher(invalid1).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.ACCOUNT.matcher(invalid2).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.ACCOUNT.matcher(invalid3).matches()).isFalse();
        }

        @Test
        @DisplayName("PASSWORD - 应该正确匹配有效的密码")
        void shouldMatchValidPassword() {
            // Arrange
            String valid1 = "Admin123!"; // 包含大小写字母、数字、特殊字符
            String valid2 = "Password@2025"; // 包含大小写字母、数字、特殊字符
            String invalid1 = "admin123"; // 缺少大写字母和特殊字符
            String invalid2 = "ADMIN123"; // 缺少小写字母和特殊字符
            String invalid3 = "Admin!"; // 太短（少于8位）

            // Assert
            assertThat(RegexPatternPoolFactory.PASSWORD.matcher(valid1).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.PASSWORD.matcher(valid2).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.PASSWORD.matcher(invalid1).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.PASSWORD.matcher(invalid2).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.PASSWORD.matcher(invalid3).matches()).isFalse();
        }

        @Test
        @DisplayName("STATUS - 应该正确匹配有效的状态值")
        void shouldMatchValidStatus() {
            // Arrange
            String valid1 = "0"; // 正常
            String valid2 = "1"; // 停用
            String invalid1 = "2"; // 无效状态
            String invalid2 = "a"; // 非数字
            String invalid3 = ""; // 空字符串

            // Assert
            assertThat(RegexPatternPoolFactory.STATUS.matcher(valid1).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.STATUS.matcher(valid2).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.STATUS.matcher(invalid1).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.STATUS.matcher(invalid2).matches()).isFalse();
            assertThat(RegexPatternPoolFactory.STATUS.matcher(invalid3).matches()).isFalse();
        }
    }

    @Nested
    @DisplayName("3. 继承自 PatternPool 测试")
    class PatternPoolInheritanceTests {

        @Test
        @DisplayName("应该继承自 Hutool 的 PatternPool")
        void shouldExtendHutoolPatternPool() {
            // Assert
            assertThat(RegexPatternPoolFactory.class.getSuperclass().getSimpleName())
                .isEqualTo("PatternPool");
        }

        @Test
        @DisplayName("应该能够使用 PatternPool 的 get 方法")
        void shouldUsePatternPoolGetMethod() {
            // Act
            Pattern pattern = RegexPatternPoolFactory.get(RegexConstants.ACCOUNT);

            // Assert
            assertThat(pattern).isNotNull();
            assertThat(pattern.pattern()).isEqualTo(RegexConstants.ACCOUNT);
        }

        @Test
        @DisplayName("多次获取相同正则应该返回缓存的 Pattern（性能优化验证）")
        void shouldReturnCachedPatternForSameRegex() {
            // Act
            Pattern pattern1 = RegexPatternPoolFactory.get(RegexConstants.ACCOUNT);
            Pattern pattern2 = RegexPatternPoolFactory.get(RegexConstants.ACCOUNT);

            // Assert - 应该是同一个对象（缓存命中）
            assertThat(pattern1).isSameAs(pattern2);
        }
    }

    @Nested
    @DisplayName("4. 边界测试")
    class BoundaryTests {

        @Test
        @DisplayName("所有 Pattern 常量应该非 null")
        void shouldAllPatternsBeNonNull() {
            // Assert
            assertThat(RegexPatternPoolFactory.DICTIONARY_TYPE).isNotNull();
            assertThat(RegexPatternPoolFactory.ID_CARD_LAST_6).isNotNull();
            assertThat(RegexPatternPoolFactory.QQ_NUMBER).isNotNull();
            assertThat(RegexPatternPoolFactory.POSTAL_CODE).isNotNull();
            assertThat(RegexPatternPoolFactory.ACCOUNT).isNotNull();
            assertThat(RegexPatternPoolFactory.PASSWORD).isNotNull();
            assertThat(RegexPatternPoolFactory.STATUS).isNotNull();
        }

        @Test
        @DisplayName("所有 Pattern 常量应该是静态的")
        void shouldAllPatternsBeStatic() throws NoSuchFieldException {
            // Assert
            assertThat(java.lang.reflect.Modifier.isStatic(
                RegexPatternPoolFactory.class.getDeclaredField("DICTIONARY_TYPE").getModifiers()
            )).isTrue();

            assertThat(java.lang.reflect.Modifier.isStatic(
                RegexPatternPoolFactory.class.getDeclaredField("ID_CARD_LAST_6").getModifiers()
            )).isTrue();

            assertThat(java.lang.reflect.Modifier.isStatic(
                RegexPatternPoolFactory.class.getDeclaredField("QQ_NUMBER").getModifiers()
            )).isTrue();
        }

        @Test
        @DisplayName("所有 Pattern 常量应该是 final 的")
        void shouldAllPatternsBeFinal() throws NoSuchFieldException {
            // Assert
            assertThat(java.lang.reflect.Modifier.isFinal(
                RegexPatternPoolFactory.class.getDeclaredField("DICTIONARY_TYPE").getModifiers()
            )).isTrue();

            assertThat(java.lang.reflect.Modifier.isFinal(
                RegexPatternPoolFactory.class.getDeclaredField("ID_CARD_LAST_6").getModifiers()
            )).isTrue();

            assertThat(java.lang.reflect.Modifier.isFinal(
                RegexPatternPoolFactory.class.getDeclaredField("PASSWORD").getModifiers()
            )).isTrue();
        }
    }

    @Nested
    @DisplayName("5. 构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("应该能够实例化 RegexPatternPoolFactory (继承自 PatternPool)")
        void shouldInstantiateRegexPatternPoolFactory() {
            // Act - 通过反射实例化工厂类
            // 注意: 虽然这是一个工具类, 但它继承自 PatternPool, 可以被实例化
            RegexPatternPoolFactory factory = new RegexPatternPoolFactory();

            // Assert
            assertThat(factory).isNotNull();
            assertThat(factory).isInstanceOf(RegexPatternPoolFactory.class);
        }
    }

    @Nested
    @DisplayName("6. 真实业务场景测试")
    class RealBusinessScenarioTests {

        @Test
        @DisplayName("用户注册 - 验证账号格式")
        void shouldValidateUserRegistrationAccount() {
            // Arrange
            String validAccount = "admin123";
            String invalidAccount = "123admin";

            // Act & Assert
            assertThat(RegexPatternPoolFactory.ACCOUNT.matcher(validAccount).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.ACCOUNT.matcher(invalidAccount).matches()).isFalse();
        }

        @Test
        @DisplayName("用户注册 - 验证密码强度")
        void shouldValidatePasswordStrength() {
            // Arrange
            String strongPassword = "Admin@2025";
            String weakPassword = "123456";

            // Act & Assert
            assertThat(RegexPatternPoolFactory.PASSWORD.matcher(strongPassword).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.PASSWORD.matcher(weakPassword).matches()).isFalse();
        }

        @Test
        @DisplayName("用户管理 - 验证用户状态")
        void shouldValidateUserStatus() {
            // Arrange
            String enabledStatus = "0";
            String disabledStatus = "1";
            String invalidStatus = "999";

            // Act & Assert
            assertThat(RegexPatternPoolFactory.STATUS.matcher(enabledStatus).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.STATUS.matcher(disabledStatus).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.STATUS.matcher(invalidStatus).matches()).isFalse();
        }

        @Test
        @DisplayName("字典管理 - 验证字典类型命名")
        void shouldValidateDictTypeNaming() {
            // Arrange
            String validDictType = "sys_user_sex";
            String invalidDictType = "SysUserSex"; // 不符合命名规范

            // Act & Assert
            assertThat(RegexPatternPoolFactory.DICTIONARY_TYPE.matcher(validDictType).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.DICTIONARY_TYPE.matcher(invalidDictType).matches()).isFalse();
        }

        @Test
        @DisplayName("地址管理 - 验证邮政编码")
        void shouldValidatePostalCode() {
            // Arrange
            String validPostalCode = "100000"; // 北京
            String invalidPostalCode = "ABCDEF";

            // Act & Assert
            assertThat(RegexPatternPoolFactory.POSTAL_CODE.matcher(validPostalCode).matches()).isTrue();
            assertThat(RegexPatternPoolFactory.POSTAL_CODE.matcher(invalidPostalCode).matches()).isFalse();
        }
    }
}
