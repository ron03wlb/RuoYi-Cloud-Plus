package org.dromara.common.core.utils.sql;

import static org.assertj.core.api.Assertions.*;

import org.dromara.common.core.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * SqlUtil 单元测试
 *
 * @author Lion Li
 */
@DisplayName("SqlUtil 工具类测试")
class SqlUtilTest extends BaseUnitTest {

    @Nested
    @DisplayName("isValidOrderBySql 方法测试")
    class IsValidOrderBySqlTest {

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "id",
                    "user_id",
                    "userName",
                    "create_time",
                    "id DESC",
                    "id ASC",
                    "id desc",
                    "id asc",
                    "user_id, create_time",
                    "user_id DESC, create_time ASC",
                    "id,name",
                    "id ,name",
                    "id, name",
                    "table.column",
                    "t.id",
                    "user.id DESC",
                    "field1, field2, field3"
                })
        @DisplayName("有效的 order by 语句")
        void shouldReturnTrueForValidOrderBySql(String value) {
            assertThat(SqlUtil.isValidOrderBySql(value)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "id;",
                    "id--",
                    "id/*",
                    "id*/",
                    "id'",
                    "id\"",
                    "id=1",
                    "id>1",
                    "id<1",
                    "id+1",
                    "id-1",
                    "id*1",
                    "id/1",
                    "id%1",
                    "id&1",
                    "id|1",
                    "id^1",
                    "id~1",
                    "id!1",
                    "id?1",
                    "id:1",
                    "id@1",
                    "id#1",
                    "id$1",
                    "id()",
                    "id[]",
                    "id{}",
                    "id<>",
                    "id!=",
                    "id;DROP TABLE users",
                    "id' OR '1'='1",
                    "id; DELETE FROM"
                })
        @DisplayName("无效的 order by 语句 - 包含非法字符")
        void shouldReturnFalseForInvalidOrderBySql(String value) {
            assertThat(SqlUtil.isValidOrderBySql(value)).isFalse();
        }

        @Test
        @DisplayName("包含中文字符应返回false")
        void shouldReturnFalseForChineseCharacters() {
            assertThat(SqlUtil.isValidOrderBySql("用户id")).isFalse();
            assertThat(SqlUtil.isValidOrderBySql("id 排序")).isFalse();
        }

        @Test
        @DisplayName("空字符串应匹配")
        void shouldMatchEmptyString() {
            // 空字符串符合正则模式 [a-zA-Z0-9_\ \,\.]+
            // 但实际上空字符串不匹配这个模式（需要至少1个字符）
            assertThat(SqlUtil.isValidOrderBySql("")).isFalse();
        }

        @Test
        @DisplayName("只包含空格应匹配")
        void shouldMatchWhitespace() {
            assertThat(SqlUtil.isValidOrderBySql("   ")).isTrue();
        }

        @Test
        @DisplayName("多个空格分隔")
        void shouldMatchMultipleSpaces() {
            assertThat(SqlUtil.isValidOrderBySql("id    DESC")).isTrue();
        }

        @Test
        @DisplayName("包含小数点的字段名")
        void shouldMatchDecimalPoint() {
            assertThat(SqlUtil.isValidOrderBySql("table.column")).isTrue();
            assertThat(SqlUtil.isValidOrderBySql("schema.table.column")).isTrue();
        }
    }

    @Nested
    @DisplayName("escapeOrderBySql 方法测试")
    class EscapeOrderBySqlTest {

        @ParameterizedTest
        @ValueSource(
                strings = {"id", "user_id", "id DESC", "id, name", "user_id DESC, create_time ASC"})
        @DisplayName("有效的语句应正常返回")
        void shouldReturnValueForValidSql(String value) {
            String result = SqlUtil.escapeOrderBySql(value);
            assertThat(result).isEqualTo(value);
        }

        @ParameterizedTest
        @ValueSource(strings = {"id;", "id--", "id' OR '1'='1", "id; DROP TABLE users"})
        @DisplayName("无效的语句应抛出异常")
        void shouldThrowExceptionForInvalidSql(String value) {
            assertThatThrownBy(() -> SqlUtil.escapeOrderBySql(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数不符合规范，不能进行查询");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null或空字符串应正常返回")
        void shouldReturnNullOrEmptyForNullOrEmpty(String value) {
            String result = SqlUtil.escapeOrderBySql(value);
            if (value == null) {
                assertThat(result).isNull();
            } else {
                assertThat(result).isEmpty();
            }
        }

        @Test
        @DisplayName("空白字符串应正常返回")
        void shouldReturnWhitespaceForWhitespace() {
            String value = "   ";
            String result = SqlUtil.escapeOrderBySql(value);
            assertThat(result).isEqualTo(value);
        }

        @Test
        @DisplayName("大小写混合的有效语句")
        void shouldHandleMixedCase() {
            assertThat(SqlUtil.escapeOrderBySql("UserId DESC")).isEqualTo("UserId DESC");
            assertThat(SqlUtil.escapeOrderBySql("user_ID ASC")).isEqualTo("user_ID ASC");
        }

        @Test
        @DisplayName("复杂的多字段排序")
        void shouldHandleComplexMultiFieldSort() {
            String value = "field1 DESC, field2 ASC, field3, field4 DESC";
            String result = SqlUtil.escapeOrderBySql(value);
            assertThat(result).isEqualTo(value);
        }
    }

    @Nested
    @DisplayName("filterKeyword 方法测试")
    class FilterKeywordTest {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null或空字符串应正常返回")
        void shouldNotThrowForNullOrEmpty(String value) {
            assertThatCode(() -> SqlUtil.filterKeyword(value)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("正常的业务数据应通过检查")
        void shouldPassForNormalData() {
            assertThatCode(() -> SqlUtil.filterKeyword("张三")).doesNotThrowAnyException();
            assertThatCode(() -> SqlUtil.filterKeyword("测试数据123")).doesNotThrowAnyException();
            assertThatCode(() -> SqlUtil.filterKeyword("normal-data_123"))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(
                strings = {"select * from users", "SELECT * FROM users", "SeLeCt * FrOm users"})
        @DisplayName("包含 SELECT 关键字应抛出异常")
        void shouldThrowForSelectKeyword(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"insert into users", "INSERT INTO users", "InSeRt InTo users"})
        @DisplayName("包含 INSERT 关键字应抛出异常")
        void shouldThrowForInsertKeyword(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"delete from users", "DELETE FROM users", "DeLeTe FrOm users"})
        @DisplayName("包含 DELETE 关键字应抛出异常")
        void shouldThrowForDeleteKeyword(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"update users set", "UPDATE users SET", "UpDaTe users SeT"})
        @DisplayName("包含 UPDATE 关键字应抛出异常")
        void shouldThrowForUpdateKeyword(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"drop table users", "DROP TABLE users", "DrOp TaBlE users"})
        @DisplayName("包含 DROP 关键字应抛出异常")
        void shouldThrowForDropKeyword(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"1' or '1'='1", "1 OR 1=1", "admin' OR '1'='1"})
        @DisplayName("包含 OR 注入应抛出异常")
        void shouldThrowForOrInjection(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"1' and '1'='1", "1 AND 1=1", "admin' AND '1'='1"})
        @DisplayName("包含 AND 注入应抛出异常")
        void shouldThrowForAndInjection(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"1 union select", "1 UNION SELECT", "1 UnIoN SeLeCt"})
        @DisplayName("包含 UNION SELECT 注入应抛出异常")
        void shouldThrowForUnionSelectInjection(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "extractvalue(1, '//a')",
                    "EXTRACTVALUE(1, '//a')",
                    "updatexml(1, '//a', 1)",
                    "UPDATEXML(1, '//a', 1)"
                })
        @DisplayName("包含 XML 函数注入应抛出异常")
        void shouldThrowForXmlFunctionInjection(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"sleep(5)", "SLEEP(5)", "select sleep(10)"})
        @DisplayName("包含延时函数注入应抛出异常")
        void shouldThrowForTimingAttack(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"exec sp_executesql", "EXEC sp_executesql", "test exec test"})
        @DisplayName("包含 EXEC 注入应抛出异常")
        void shouldThrowForExecInjection(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"select count from users", "COUNT from users", "test count test"})
        @DisplayName("包含 COUNT 关键字应抛出异常")
        void shouldThrowForCountKeyword(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"chr (65)", "CHR (65)", "char (65)", "CHAR (65)"})
        @DisplayName("包含 CHR/CHAR 函数应抛出异常")
        void shouldThrowForChrCharFunction(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "mid (database(), 1, 1)",
                    "MID (database(), 1, 1)",
                    "select mid from table"
                })
        @DisplayName("包含 MID 函数应抛出异常")
        void shouldThrowForMidFunction(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"master table users", "MASTER TABLE users", "select from master"})
        @DisplayName("包含 MASTER 关键字应抛出异常")
        void shouldThrowForMasterKeyword(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(
                strings = {"truncate table users", "TRUNCATE TABLE users", "TrUnCaTe TaBlE users"})
        @DisplayName("包含 TRUNCATE 关键字应抛出异常")
        void shouldThrowForTruncateKeyword(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"declare @var", "DECLARE @var", "DeClaRe @var"})
        @DisplayName("包含 DECLARE 关键字应抛出异常")
        void shouldThrowForDeclareKeyword(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"' like '%admin%", "LIKE '%pattern%'", "name LiKe '%test%'"})
        @DisplayName("包含 LIKE 关键字应抛出异常")
        void shouldThrowForLikeKeyword(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @ParameterizedTest
        @ValueSource(strings = {"user()", "USER()", "current_user()"})
        @DisplayName("包含 user() 函数应抛出异常")
        void shouldThrowForUserFunction(String value) {
            assertThatThrownBy(() -> SqlUtil.filterKeyword(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @Test
        @DisplayName("包含注释符号应抛出异常")
        void shouldThrowForCommentSymbols() {
            assertThatThrownBy(() -> SqlUtil.filterKeyword("test /* comment */"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @Test
        @DisplayName("包含加号应抛出异常")
        void shouldThrowForPlusSign() {
            assertThatThrownBy(() -> SqlUtil.filterKeyword("1+1"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @Test
        @DisplayName("大小写混合的关键字应被检测")
        void shouldDetectMixedCaseKeywords() {
            assertThatThrownBy(() -> SqlUtil.filterKeyword("SeLeCt * FrOm users"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> SqlUtil.filterKeyword("UnIoN AlL SeLeCt"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("关键字前后有空格应被检测")
        void shouldDetectKeywordsWithSpaces() {
            assertThatThrownBy(() -> SqlUtil.filterKeyword("test select test"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> SqlUtil.filterKeyword("test and test"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("综合场景测试")
    class IntegrationTest {

        @Test
        @DisplayName("完整的order by防护流程")
        void shouldProtectOrderByCompletely() {
            // 正常情况
            String validSql = "user_id DESC, create_time ASC";
            assertThatCode(
                            () -> {
                                String escaped = SqlUtil.escapeOrderBySql(validSql);
                                assertThat(escaped).isEqualTo(validSql);
                            })
                    .doesNotThrowAnyException();

            // SQL注入尝试
            assertThatThrownBy(() -> SqlUtil.escapeOrderBySql("id; DROP TABLE users"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数不符合规范，不能进行查询");
        }

        @Test
        @DisplayName("完整的关键字过滤流程")
        void shouldFilterKeywordsCompletely() {
            // 正常数据
            assertThatCode(() -> SqlUtil.filterKeyword("张三")).doesNotThrowAnyException();

            // 注入尝试
            assertThatThrownBy(() -> SqlUtil.filterKeyword("admin' OR '1'='1"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("参数存在SQL注入风险");
        }

        @Test
        @DisplayName("同时使用两种防护机制")
        void shouldUseBothProtectionMechanisms() {
            String userInput = "user_id DESC";

            // 先验证order by格式
            String escaped = SqlUtil.escapeOrderBySql(userInput);

            // 再检查关键字
            assertThatCode(() -> SqlUtil.filterKeyword(escaped)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("多种注入手法组合测试")
        void shouldDefendAgainstCombinedAttacks() {
            // 多种注入手法
            String[] attacks = {
                "id' OR '1'='1' --", "id; DROP TABLE users; --", "id' AND test", // 测试包含 "and " 的注入
            };

            for (String attack : attacks) {
                // escapeOrderBySql应该拦截（包含非法字符）
                assertThatThrownBy(() -> SqlUtil.escapeOrderBySql(attack))
                        .as("escapeOrderBySql 应该拦截: %s", attack)
                        .isInstanceOf(IllegalArgumentException.class);

                // filterKeyword也应该拦截（包含SQL关键字）
                assertThatThrownBy(() -> SqlUtil.filterKeyword(attack))
                        .as("filterKeyword 应该拦截: %s", attack)
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Nested
    @DisplayName("边界测试")
    class EdgeCasesTest {

        @Test
        @DisplayName("超长字符串")
        void shouldHandleVeryLongString() {
            String longString = "a".repeat(10000);
            // 超长但合法的字符串应该通过
            assertThat(SqlUtil.isValidOrderBySql(longString)).isTrue();
            assertThatCode(() -> SqlUtil.filterKeyword(longString)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("只包含空格的字符串")
        void shouldHandleWhitespaceOnly() {
            String whitespace = "   ";
            assertThat(SqlUtil.isValidOrderBySql(whitespace)).isTrue();
            assertThat(SqlUtil.escapeOrderBySql(whitespace)).isEqualTo(whitespace);
            assertThatCode(() -> SqlUtil.filterKeyword(whitespace)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("单个字符")
        void shouldHandleSingleCharacter() {
            assertThat(SqlUtil.isValidOrderBySql("a")).isTrue();
            assertThat(SqlUtil.escapeOrderBySql("a")).isEqualTo("a");
        }

        @Test
        @DisplayName("数字字段名")
        void shouldHandleNumericFieldName() {
            assertThat(SqlUtil.isValidOrderBySql("123")).isTrue();
            assertThat(SqlUtil.escapeOrderBySql("123")).isEqualTo("123");
        }

        @Test
        @DisplayName("下划线开头的字段名")
        void shouldHandleUnderscorePrefix() {
            assertThat(SqlUtil.isValidOrderBySql("_id")).isTrue();
            assertThat(SqlUtil.escapeOrderBySql("_id")).isEqualTo("_id");
        }

        @Test
        @DisplayName("关键字作为正常文本的一部分")
        void shouldHandleKeywordAsPartOfText() {
            // "android" 包含 "and " (注意有空格) 所以应该通过
            assertThatCode(() -> SqlUtil.filterKeyword("android")).doesNotThrowAnyException();

            // "select " 有空格后缀，会被检测
            assertThatThrownBy(() -> SqlUtil.filterKeyword("select from users"))
                    .isInstanceOf(IllegalArgumentException.class);

            // "and " 有空格后缀，会被检测
            assertThatThrownBy(() -> SqlUtil.filterKeyword("test and test"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Unicode字符")
        void shouldHandleUnicodeCharacters() {
            // 包含emoji
            assertThatCode(() -> SqlUtil.filterKeyword("测试😀数据")).doesNotThrowAnyException();

            // order by不接受Unicode
            assertThat(SqlUtil.isValidOrderBySql("字段名")).isFalse();
        }
    }
}
