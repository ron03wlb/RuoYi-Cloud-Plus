package org.dromara.common.mybatis.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * DataScopeType 枚举类测试
 *
 * @author Test Team
 */
@DisplayName("DataScopeType 枚举类测试")
class DataScopeTypeTest {

    @Nested
    @DisplayName("1. 枚举常量测试")
    class EnumConstantsTests {

        @Test
        @DisplayName("应该包含6个数据权限类型")
        void shouldHaveSixDataScopeTypes() {
            // Act
            DataScopeType[] types = DataScopeType.values();

            // Assert
            assertThat(types).hasSize(6);
        }

        @Test
        @DisplayName("ALL 枚举常量验证")
        void shouldHaveAllConstant() {
            // Assert
            assertThat(DataScopeType.ALL).isNotNull();
            assertThat(DataScopeType.ALL.getCode()).isEqualTo("1");
            assertThat(DataScopeType.ALL.getSqlTemplate()).isEmpty();
            assertThat(DataScopeType.ALL.getElseSql()).isEmpty();
        }

        @Test
        @DisplayName("CUSTOM 枚举常量验证")
        void shouldHaveCustomConstant() {
            // Assert
            assertThat(DataScopeType.CUSTOM).isNotNull();
            assertThat(DataScopeType.CUSTOM.getCode()).isEqualTo("2");
            assertThat(DataScopeType.CUSTOM.getSqlTemplate()).contains("#{@sdss.getRoleCustom");
            assertThat(DataScopeType.CUSTOM.getElseSql()).isEqualTo(" 1 = 0 ");
        }

        @Test
        @DisplayName("DEPT 枚举常量验证")
        void shouldHaveDeptConstant() {
            // Assert
            assertThat(DataScopeType.DEPT).isNotNull();
            assertThat(DataScopeType.DEPT.getCode()).isEqualTo("3");
            assertThat(DataScopeType.DEPT.getSqlTemplate()).contains("#{#user.deptId}");
            assertThat(DataScopeType.DEPT.getElseSql()).isEqualTo(" 1 = 0 ");
        }

        @Test
        @DisplayName("DEPT_AND_CHILD 枚举常量验证")
        void shouldHaveDeptAndChildConstant() {
            // Assert
            assertThat(DataScopeType.DEPT_AND_CHILD).isNotNull();
            assertThat(DataScopeType.DEPT_AND_CHILD.getCode()).isEqualTo("4");
            assertThat(DataScopeType.DEPT_AND_CHILD.getSqlTemplate())
                    .contains("#{@sdss.getDeptAndChild");
            assertThat(DataScopeType.DEPT_AND_CHILD.getElseSql()).isEqualTo(" 1 = 0 ");
        }

        @Test
        @DisplayName("SELF 枚举常量验证")
        void shouldHaveSelfConstant() {
            // Assert
            assertThat(DataScopeType.SELF).isNotNull();
            assertThat(DataScopeType.SELF.getCode()).isEqualTo("5");
            assertThat(DataScopeType.SELF.getSqlTemplate()).contains("#{#user.userId}");
            assertThat(DataScopeType.SELF.getElseSql()).isEqualTo(" 1 = 0 ");
        }

        @Test
        @DisplayName("DEPT_AND_CHILD_OR_SELF 枚举常量验证")
        void shouldHaveDeptAndChildOrSelfConstant() {
            // Assert
            assertThat(DataScopeType.DEPT_AND_CHILD_OR_SELF).isNotNull();
            assertThat(DataScopeType.DEPT_AND_CHILD_OR_SELF.getCode()).isEqualTo("6");
            assertThat(DataScopeType.DEPT_AND_CHILD_OR_SELF.getSqlTemplate()).contains("OR");
            assertThat(DataScopeType.DEPT_AND_CHILD_OR_SELF.getElseSql()).isEqualTo(" 1 = 0 ");
        }
    }

    @Nested
    @DisplayName("2. findCode 方法测试 - 有效代码")
    class FindCodeMethodValidCodesTests {

        @Test
        @DisplayName("应该找到 ALL (代码:1)")
        void shouldFindAll() {
            // Act
            DataScopeType result = DataScopeType.findCode("1");

            // Assert
            assertThat(result).isEqualTo(DataScopeType.ALL);
        }

        @Test
        @DisplayName("应该找到 CUSTOM (代码:2)")
        void shouldFindCustom() {
            // Act
            DataScopeType result = DataScopeType.findCode("2");

            // Assert
            assertThat(result).isEqualTo(DataScopeType.CUSTOM);
        }

        @Test
        @DisplayName("应该找到 DEPT (代码:3)")
        void shouldFindDept() {
            // Act
            DataScopeType result = DataScopeType.findCode("3");

            // Assert
            assertThat(result).isEqualTo(DataScopeType.DEPT);
        }

        @Test
        @DisplayName("应该找到 DEPT_AND_CHILD (代码:4)")
        void shouldFindDeptAndChild() {
            // Act
            DataScopeType result = DataScopeType.findCode("4");

            // Assert
            assertThat(result).isEqualTo(DataScopeType.DEPT_AND_CHILD);
        }

        @Test
        @DisplayName("应该找到 SELF (代码:5)")
        void shouldFindSelf() {
            // Act
            DataScopeType result = DataScopeType.findCode("5");

            // Assert
            assertThat(result).isEqualTo(DataScopeType.SELF);
        }

        @Test
        @DisplayName("应该找到 DEPT_AND_CHILD_OR_SELF (代码:6)")
        void shouldFindDeptAndChildOrSelf() {
            // Act
            DataScopeType result = DataScopeType.findCode("6");

            // Assert
            assertThat(result).isEqualTo(DataScopeType.DEPT_AND_CHILD_OR_SELF);
        }
    }

    @Nested
    @DisplayName("3. findCode 方法测试 - 无效或边界值")
    class FindCodeMethodInvalidCodesTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("应该返回 null - 当输入为 null 或空字符串")
        void shouldReturnNull_WhenNullOrEmpty(String input) {
            // Act
            DataScopeType result = DataScopeType.findCode(input);

            // Assert
            assertThat(result).isNull();
        }

        @ParameterizedTest
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("应该返回 null - 当输入为空白字符")
        void shouldReturnNull_WhenBlank(String input) {
            // Act
            DataScopeType result = DataScopeType.findCode(input);

            // Assert
            assertThat(result).isNull();
        }

        @ParameterizedTest
        @ValueSource(strings = {"0", "7", "10", "99", "abc", "ALL", "CUSTOM"})
        @DisplayName("应该返回 null - 当代码未知")
        void shouldReturnNull_WhenUnknownCode(String code) {
            // Act
            DataScopeType result = DataScopeType.findCode(code);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("4. SQL 模板验证测试")
    class SqlTemplateValidationTests {

        @Test
        @DisplayName("ALL 类型不应该有 SQL 模板")
        void allShouldHaveNoSqlTemplate() {
            assertThat(DataScopeType.ALL.getSqlTemplate()).isEmpty();
        }

        @Test
        @DisplayName("其他类型都应该有 SQL 模板")
        void otherTypesShouldHaveSqlTemplate() {
            assertThat(DataScopeType.CUSTOM.getSqlTemplate()).isNotEmpty();
            assertThat(DataScopeType.DEPT.getSqlTemplate()).isNotEmpty();
            assertThat(DataScopeType.DEPT_AND_CHILD.getSqlTemplate()).isNotEmpty();
            assertThat(DataScopeType.SELF.getSqlTemplate()).isNotEmpty();
            assertThat(DataScopeType.DEPT_AND_CHILD_OR_SELF.getSqlTemplate()).isNotEmpty();
        }

        @Test
        @DisplayName("SQL 模板应该包含 SpEL 表达式")
        void sqlTemplatesShouldContainSpelExpressions() {
            // 验证 SpEL 表达式标记
            assertThat(DataScopeType.CUSTOM.getSqlTemplate()).contains("#{");
            assertThat(DataScopeType.DEPT.getSqlTemplate()).contains("#{");
            assertThat(DataScopeType.DEPT_AND_CHILD.getSqlTemplate()).contains("#{");
            assertThat(DataScopeType.SELF.getSqlTemplate()).contains("#{");
            assertThat(DataScopeType.DEPT_AND_CHILD_OR_SELF.getSqlTemplate()).contains("#{");
        }

        @Test
        @DisplayName("CUSTOM 应该使用 @sdss 服务调用")
        void customShouldUseServiceCall() {
            assertThat(DataScopeType.CUSTOM.getSqlTemplate())
                    .contains("@sdss")
                    .contains("getRoleCustom");
        }

        @Test
        @DisplayName("DEPT_AND_CHILD 应该使用 @sdss 服务调用")
        void deptAndChildShouldUseServiceCall() {
            assertThat(DataScopeType.DEPT_AND_CHILD.getSqlTemplate())
                    .contains("@sdss")
                    .contains("getDeptAndChild");
        }

        @Test
        @DisplayName("DEPT_AND_CHILD_OR_SELF 应该包含 OR 逻辑运算符")
        void deptAndChildOrSelfShouldContainOrOperator() {
            assertThat(DataScopeType.DEPT_AND_CHILD_OR_SELF.getSqlTemplate())
                    .containsIgnoringCase("OR");
        }

        @Test
        @DisplayName("DEPT 和 SELF 应该使用简单的等值比较")
        void deptAndSelfShouldUseSimpleComparison() {
            assertThat(DataScopeType.DEPT.getSqlTemplate()).contains("=");
            assertThat(DataScopeType.SELF.getSqlTemplate()).contains("=");
        }
    }

    @Nested
    @DisplayName("5. ElseSql 验证测试")
    class ElseSqlValidationTests {

        @Test
        @DisplayName("ALL 类型不应该有 else SQL")
        void allShouldHaveNoElseSql() {
            assertThat(DataScopeType.ALL.getElseSql()).isEmpty();
        }

        @Test
        @DisplayName("其他类型都应该有 else SQL")
        void otherTypesShouldHaveElseSql() {
            assertThat(DataScopeType.CUSTOM.getElseSql()).isNotEmpty();
            assertThat(DataScopeType.DEPT.getElseSql()).isNotEmpty();
            assertThat(DataScopeType.DEPT_AND_CHILD.getElseSql()).isNotEmpty();
            assertThat(DataScopeType.SELF.getElseSql()).isNotEmpty();
            assertThat(DataScopeType.DEPT_AND_CHILD_OR_SELF.getElseSql()).isNotEmpty();
        }

        @Test
        @DisplayName("所有 else SQL 都应该是 '1 = 0'")
        void allElseSqlShouldBeOneEqualsZero() {
            assertThat(DataScopeType.CUSTOM.getElseSql()).isEqualTo(" 1 = 0 ");
            assertThat(DataScopeType.DEPT.getElseSql()).isEqualTo(" 1 = 0 ");
            assertThat(DataScopeType.DEPT_AND_CHILD.getElseSql()).isEqualTo(" 1 = 0 ");
            assertThat(DataScopeType.SELF.getElseSql()).isEqualTo(" 1 = 0 ");
            assertThat(DataScopeType.DEPT_AND_CHILD_OR_SELF.getElseSql()).isEqualTo(" 1 = 0 ");
        }
    }

    @Nested
    @DisplayName("6. Code 属性验证测试")
    class CodeValidationTests {

        @Test
        @DisplayName("所有枚举常量都应该有唯一的 code")
        void allConstantsShouldHaveUniqueCode() {
            assertThat(DataScopeType.values())
                    .extracting(DataScopeType::getCode)
                    .doesNotHaveDuplicates();
        }

        @Test
        @DisplayName("所有 code 都应该是数字字符串")
        void allCodesShouldBeNumericStrings() {
            for (DataScopeType type : DataScopeType.values()) {
                assertThat(type.getCode())
                        .as("DataScopeType %s 的 code 应该是数字", type)
                        .matches("\\d+");
            }
        }

        @Test
        @DisplayName("所有 code 都不应该为 null 或空")
        void allCodesShouldNotBeNullOrEmpty() {
            for (DataScopeType type : DataScopeType.values()) {
                assertThat(type.getCode())
                        .as("DataScopeType %s 的 code 不应该为 null 或空", type)
                        .isNotEmpty();
            }
        }
    }

    @Nested
    @DisplayName("7. 综合场景测试")
    class ComprehensiveScenarioTests {

        @ParameterizedTest
        @CsvSource({
            "1, ALL, '', ''",
            "2, CUSTOM, '@sdss', ' 1 = 0 '",
            "3, DEPT, 'deptId', ' 1 = 0 '",
            "4, DEPT_AND_CHILD, 'getDeptAndChild', ' 1 = 0 '",
            "5, SELF, 'userId', ' 1 = 0 '",
            "6, DEPT_AND_CHILD_OR_SELF, 'OR', ' 1 = 0 '"
        })
        @DisplayName("综合验证 - 代码查找和属性验证")
        void shouldCorrectlyIdentifyDataScopeType(
                String code, String expectedName, String sqlKeyword, String expectedElseSql) {
            // Act
            DataScopeType result = DataScopeType.findCode(code);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(expectedName);
            assertThat(result.getCode()).isEqualTo(code);
            assertThat(result.getElseSql()).isEqualTo(expectedElseSql);

            if (!sqlKeyword.isEmpty()) {
                assertThat(result.getSqlTemplate()).contains(sqlKeyword);
            }
        }

        @Test
        @DisplayName("验证所有权限类型代码连续性")
        void shouldHaveConsecutiveCodes() {
            // 权限代码应该从 1 到 6 连续
            for (int i = 1; i <= 6; i++) {
                DataScopeType type = DataScopeType.findCode(String.valueOf(i));
                assertThat(type).as("应该能找到代码为 %d 的数据权限类型", i).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("8. Lombok @Getter 测试")
    class GetterMethodTests {

        @Test
        @DisplayName("getCode 方法应该返回正确的代码")
        void getCodeShouldReturnCorrectCode() {
            assertThat(DataScopeType.ALL.getCode()).isEqualTo("1");
            assertThat(DataScopeType.CUSTOM.getCode()).isEqualTo("2");
            assertThat(DataScopeType.DEPT.getCode()).isEqualTo("3");
            assertThat(DataScopeType.DEPT_AND_CHILD.getCode()).isEqualTo("4");
            assertThat(DataScopeType.SELF.getCode()).isEqualTo("5");
            assertThat(DataScopeType.DEPT_AND_CHILD_OR_SELF.getCode()).isEqualTo("6");
        }

        @Test
        @DisplayName("getSqlTemplate 方法应该正常工作")
        void getSqlTemplateShouldWork() {
            assertThat(DataScopeType.ALL.getSqlTemplate()).isNotNull();
            assertThat(DataScopeType.CUSTOM.getSqlTemplate()).isNotNull();
        }

        @Test
        @DisplayName("getElseSql 方法应该正常工作")
        void getElseSqlShouldWork() {
            assertThat(DataScopeType.ALL.getElseSql()).isNotNull();
            assertThat(DataScopeType.CUSTOM.getElseSql()).isNotNull();
        }

        @Test
        @DisplayName("所有 getter 方法返回值不应该为 null")
        void allGettersShouldNeverReturnNull() {
            for (DataScopeType type : DataScopeType.values()) {
                assertThat(type.getCode()).isNotNull();
                assertThat(type.getSqlTemplate()).isNotNull();
                assertThat(type.getElseSql()).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("9. 枚举基本特性测试")
    class EnumBasicFeaturesTests {

        @Test
        @DisplayName("valueOf 方法应该正确工作")
        void valueOfShouldWork() {
            assertThat(DataScopeType.valueOf("ALL")).isEqualTo(DataScopeType.ALL);
            assertThat(DataScopeType.valueOf("CUSTOM")).isEqualTo(DataScopeType.CUSTOM);
            assertThat(DataScopeType.valueOf("DEPT")).isEqualTo(DataScopeType.DEPT);
            assertThat(DataScopeType.valueOf("DEPT_AND_CHILD"))
                    .isEqualTo(DataScopeType.DEPT_AND_CHILD);
            assertThat(DataScopeType.valueOf("SELF")).isEqualTo(DataScopeType.SELF);
            assertThat(DataScopeType.valueOf("DEPT_AND_CHILD_OR_SELF"))
                    .isEqualTo(DataScopeType.DEPT_AND_CHILD_OR_SELF);
        }

        @Test
        @DisplayName("枚举常量应该是单例")
        void enumConstantsShouldBeSingletons() {
            assertThat(DataScopeType.valueOf("ALL")).isSameAs(DataScopeType.ALL);
            assertThat(DataScopeType.valueOf("CUSTOM")).isSameAs(DataScopeType.CUSTOM);
        }

        @Test
        @DisplayName("name 方法应该返回枚举常量名称")
        void nameShouldReturnConstantName() {
            assertThat(DataScopeType.ALL.name()).isEqualTo("ALL");
            assertThat(DataScopeType.CUSTOM.name()).isEqualTo("CUSTOM");
            assertThat(DataScopeType.DEPT.name()).isEqualTo("DEPT");
            assertThat(DataScopeType.DEPT_AND_CHILD.name()).isEqualTo("DEPT_AND_CHILD");
            assertThat(DataScopeType.SELF.name()).isEqualTo("SELF");
            assertThat(DataScopeType.DEPT_AND_CHILD_OR_SELF.name())
                    .isEqualTo("DEPT_AND_CHILD_OR_SELF");
        }

        @Test
        @DisplayName("ordinal 方法应该返回枚举常量顺序")
        void ordinalShouldReturnCorrectOrder() {
            assertThat(DataScopeType.ALL.ordinal()).isEqualTo(0);
            assertThat(DataScopeType.CUSTOM.ordinal()).isEqualTo(1);
            assertThat(DataScopeType.DEPT.ordinal()).isEqualTo(2);
            assertThat(DataScopeType.DEPT_AND_CHILD.ordinal()).isEqualTo(3);
            assertThat(DataScopeType.SELF.ordinal()).isEqualTo(4);
            assertThat(DataScopeType.DEPT_AND_CHILD_OR_SELF.ordinal()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("10. 真实业务场景测试")
    class RealBusinessScenarioTests {

        @Test
        @DisplayName("模拟查询全部数据场景")
        void shouldAllowAllDataAccess() {
            // Act
            DataScopeType type = DataScopeType.findCode("1");

            // Assert
            assertThat(type).isEqualTo(DataScopeType.ALL);
            assertThat(type.getSqlTemplate()).isEmpty(); // 全部数据不需要 SQL 条件
        }

        @Test
        @DisplayName("模拟自定义权限场景")
        void shouldUseCustomPermission() {
            // Act
            DataScopeType type = DataScopeType.findCode("2");

            // Assert
            assertThat(type).isEqualTo(DataScopeType.CUSTOM);
            assertThat(type.getSqlTemplate()).contains("getRoleCustom"); // 调用自定义权限服务
        }

        @Test
        @DisplayName("模拟部门权限场景")
        void shouldUseDeptPermission() {
            // Act
            DataScopeType type = DataScopeType.findCode("3");

            // Assert
            assertThat(type).isEqualTo(DataScopeType.DEPT);
            assertThat(type.getSqlTemplate()).contains("#user.deptId"); // 使用用户部门ID
        }

        @Test
        @DisplayName("模拟仅本人数据场景")
        void shouldUseSelfPermission() {
            // Act
            DataScopeType type = DataScopeType.findCode("5");

            // Assert
            assertThat(type).isEqualTo(DataScopeType.SELF);
            assertThat(type.getSqlTemplate()).contains("#user.userId"); // 使用用户ID
        }

        @Test
        @DisplayName("模拟组合权限场景（部门及本人）")
        void shouldUseCombinedPermission() {
            // Act
            DataScopeType type = DataScopeType.findCode("6");

            // Assert
            assertThat(type).isEqualTo(DataScopeType.DEPT_AND_CHILD_OR_SELF);
            assertThat(type.getSqlTemplate())
                    .contains("getDeptAndChild")
                    .contains("OR")
                    .contains("#user.userId");
        }
    }
}
