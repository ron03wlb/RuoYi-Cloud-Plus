package org.dromara.common.core.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.dromara.common.core.BaseUnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * StringUtils 工具类测试
 *
 * <p>测试覆盖： - 空值判断 - 字符串操作 - 格式转换 - 集合转换 - 路径匹配 - 字符串拼接分割
 *
 * @author Test Team
 */
class StringUtilsTest extends BaseUnitTest {

    // ========================================
    // 空值判断测试
    // ========================================

    @ParameterizedTest
    @NullAndEmptySource
    void shouldReturnTrueWhenStringIsEmpty(String input) {
        assertThat(StringUtils.isEmpty(input)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenStringIsNotEmpty() {
        assertThat(StringUtils.isEmpty("hello")).isFalse();
        assertThat(StringUtils.isEmpty(" ")).isFalse();
        assertThat(StringUtils.isEmpty("   ")).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", " hello ", "  test  "})
    void shouldReturnTrueWhenStringIsNotEmpty(String input) {
        assertThat(StringUtils.isNotEmpty(input)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldReturnFalseWhenStringIsEmpty(String input) {
        assertThat(StringUtils.isNotEmpty(input)).isFalse();
    }

    // ========================================
    // 默认值测试
    // ========================================

    @Test
    void shouldReturnDefaultValueWhenStringIsBlank() {
        assertThat(StringUtils.blankToDefault("", "default")).isEqualTo("default");
        assertThat(StringUtils.blankToDefault("   ", "default")).isEqualTo("default");
        assertThat(StringUtils.blankToDefault(null, "default")).isEqualTo("default");
    }

    @Test
    void shouldReturnOriginalValueWhenStringIsNotBlank() {
        assertThat(StringUtils.blankToDefault("hello", "default")).isEqualTo("hello");
        assertThat(StringUtils.blankToDefault(" hello ", "default")).isEqualTo(" hello ");
    }

    // ========================================
    // Trim 测试
    // ========================================

    @Test
    void shouldTrimStringCorrectly() {
        assertThat(StringUtils.trim("  hello  ")).isEqualTo("hello");
        assertThat(StringUtils.trim("hello")).isEqualTo("hello");
        assertThat(StringUtils.trim("   ")).isEmpty();
    }

    @Test
    void shouldHandleNullInTrim() {
        assertThat(StringUtils.trim(null)).isNull();
    }

    // ========================================
    // Substring 测试
    // ========================================

    @Test
    void shouldSubstringFromStartCorrectly() {
        String str = "Hello World";
        assertThat(StringUtils.substring(str, 0)).isEqualTo("Hello World");
        assertThat(StringUtils.substring(str, 6)).isEqualTo("World");
        assertThat(StringUtils.substring(str, 11)).isEmpty();
    }

    @Test
    void shouldSubstringWithStartAndEndCorrectly() {
        String str = "Hello World";
        assertThat(StringUtils.substring(str, 0, 5)).isEqualTo("Hello");
        assertThat(StringUtils.substring(str, 6, 11)).isEqualTo("World");
        assertThat(StringUtils.substring(str, 0, 11)).isEqualTo("Hello World");
    }

    // ========================================
    // Format 测试
    // ========================================

    @Test
    void shouldFormatStringWithPlaceholders() {
        String result = StringUtils.format("Hello {}, your age is {}", "Alice", 25);
        assertThat(result).isEqualTo("Hello Alice, your age is 25");
    }

    @Test
    void shouldFormatStringWithNoParams() {
        String result = StringUtils.format("Hello World");
        assertThat(result).isEqualTo("Hello World");
    }

    @Test
    void shouldFormatStringWithEscapedBraces() {
        String result = StringUtils.format("This is \\{} for {}", "test");
        assertThat(result).isEqualTo("This is {} for test");
    }

    // ========================================
    // HTTP URL 检测
    // ========================================

    @ParameterizedTest
    @ValueSource(
            strings = {
                "http://example.com",
                "https://example.com",
                "http://www.example.com/path",
                "https://example.com:8080/path?query=1"
            })
    void shouldReturnTrueForValidHttpUrls(String url) {
        assertThat(StringUtils.ishttp(url)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/path/to/resource", "example.com", "not-a-url"})
    void shouldReturnFalseForInvalidHttpUrls(String url) {
        assertThat(StringUtils.ishttp(url)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"ftp://example.com", "file:///path/to/file"})
    void shouldReturnTrueForValidUrls(String url) {
        // Validator.isUrl() 也接受 ftp:// 和 file:// 协议
        assertThat(StringUtils.ishttp(url)).isTrue();
    }

    // ========================================
    // 字符串转集合测试
    // ========================================

    @Test
    void shouldConvertStringToSet() {
        Set<String> result = StringUtils.str2Set("a,b,c", ",");

        assertThat(result).hasSize(3).containsExactlyInAnyOrder("a", "b", "c");
    }

    @Test
    void shouldConvertStringToSetWithDuplicates() {
        Set<String> result = StringUtils.str2Set("a,b,a,c,b", ",");

        assertThat(result).hasSize(3).containsExactlyInAnyOrder("a", "b", "c");
    }

    @Test
    void shouldConvertStringToListWithFilter() {
        List<String> result = StringUtils.str2List("a, b, c", ",", true, true);

        assertThat(result).hasSize(3).containsExactly("a", "b", "c");
    }

    @Test
    void shouldConvertStringToListFilteringBlanks() {
        List<String> result = StringUtils.str2List("a, , b,  , c", ",", true, true);

        assertThat(result).hasSize(3).containsExactly("a", "b", "c");
    }

    @Test
    void shouldConvertStringToListWithoutTrim() {
        List<String> result = StringUtils.str2List("a, b, c", ",", false, false);

        assertThat(result).hasSize(3).containsExactly("a", " b", " c");
    }

    @Test
    void shouldReturnEmptyListWhenStringIsEmpty() {
        List<String> result = StringUtils.str2List("", ",", true, true);
        assertThat(result).isEmpty();
    }

    // ========================================
    // 包含检测测试
    // ========================================

    @Test
    void shouldReturnTrueWhenContainsAnyIgnoreCase() {
        assertThat(StringUtils.containsAnyIgnoreCase("Hello World", "HELLO")).isTrue();
        assertThat(StringUtils.containsAnyIgnoreCase("Hello World", "world")).isTrue();
        assertThat(StringUtils.containsAnyIgnoreCase("Hello World", "hello", "universe")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNotContainsAnyIgnoreCase() {
        assertThat(StringUtils.containsAnyIgnoreCase("Hello World", "foo")).isFalse();
        assertThat(StringUtils.containsAnyIgnoreCase("Hello World", "foo", "bar")).isFalse();
    }

    // ========================================
    // 驼峰和下划线转换测试
    // ========================================

    @ParameterizedTest
    @CsvSource({
        "helloWorld, hello_world",
        "HelloWorld, hello_world",
        "hello, hello",
        "HELLO, HELLO", // 全大写不转换
        "userNameList, user_name_list"
    })
    void shouldConvertToUnderScoreCase(String input, String expected) {
        assertThat(StringUtils.toUnderScoreCase(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "hello_world, helloWorld",
        "user_name, userName",
        "hello, hello",
        "user_name_list, userNameList"
    })
    void shouldConvertToCamelCase(String input, String expected) {
        assertThat(StringUtils.toCamelCase(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"HELLO_WORLD, HelloWorld", "USER_NAME, UserName", "hello, Hello"})
    void shouldConvertToCamelCaseWithUpperFirst(String input, String expected) {
        assertThat(StringUtils.convertToCamelCase(input)).isEqualTo(expected);
    }

    // ========================================
    // 字符串匹配检测
    // ========================================

    @Test
    void shouldReturnTrueWhenStringInStringIgnoreCase() {
        assertThat(StringUtils.inStringIgnoreCase("hello", "HELLO", "world")).isTrue();
        assertThat(StringUtils.inStringIgnoreCase("WORLD", "hello", "world")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenStringNotInStringIgnoreCase() {
        assertThat(StringUtils.inStringIgnoreCase("foo", "hello", "world")).isFalse();
    }

    // ========================================
    // Ant Path 匹配测试
    // ========================================

    @Test
    void shouldMatchAntPattern() {
        // 单字符通配符 ?
        assertThat(StringUtils.isMatch("/user/?", "/user/1")).isTrue();
        assertThat(StringUtils.isMatch("/user/?", "/user/12")).isFalse();

        // 单层通配符 *
        assertThat(StringUtils.isMatch("/user/*", "/user/list")).isTrue();
        assertThat(StringUtils.isMatch("/user/*", "/user/1/detail")).isFalse();

        // 多层通配符 **
        assertThat(StringUtils.isMatch("/user/**", "/user/1/detail")).isTrue();
        assertThat(StringUtils.isMatch("/api/**/*.json", "/api/v1/data/users.json")).isTrue();
    }

    @Test
    void shouldMatchesWithPatternList() {
        List<String> patterns = Arrays.asList("/api/**", "/admin/**");

        assertThat(StringUtils.matches("/api/users", patterns)).isTrue();
        assertThat(StringUtils.matches("/admin/config", patterns)).isTrue();
        assertThat(StringUtils.matches("/public/index", patterns)).isFalse();
    }

    @Test
    void shouldReturnFalseWhenMatchesWithEmptyString() {
        List<String> patterns = Arrays.asList("/api/**");
        assertThat(StringUtils.matches("", patterns)).isFalse();
    }

    @Test
    void shouldReturnFalseWhenMatchesWithEmptyPatterns() {
        assertThat(StringUtils.matches("/api/users", List.of())).isFalse();
    }

    // ========================================
    // 左补齐测试
    // ========================================

    @Test
    void shouldPadLeftWithZeroForNumber() {
        assertThat(StringUtils.padl(123, 5)).isEqualTo("00123");
        assertThat(StringUtils.padl(1, 3)).isEqualTo("001");
        assertThat(StringUtils.padl(12345, 5)).isEqualTo("12345");
        assertThat(StringUtils.padl(123456, 5)).isEqualTo("23456"); // 超长截取
    }

    @Test
    void shouldPadLeftWithCustomChar() {
        assertThat(StringUtils.padl("abc", 5, 'x')).isEqualTo("xxabc");
        assertThat(StringUtils.padl("hello", 8, '-')).isEqualTo("---hello");
        assertThat(StringUtils.padl("test", 4, '*')).isEqualTo("test");
    }

    @Test
    void shouldHandleNullInPadLeft() {
        assertThat(StringUtils.padl(null, 5, '0')).isEqualTo("00000");
    }

    @Test
    void shouldTruncateWhenStringLongerThanSize() {
        assertThat(StringUtils.padl("abcdefgh", 5, '0')).isEqualTo("defgh");
    }

    // ========================================
    // 分割字符串测试
    // ========================================

    @Test
    void shouldSplitListWithDefaultSeparator() {
        List<String> result = StringUtils.splitList("a,b,c");

        assertThat(result).hasSize(3).containsExactly("a", "b", "c");
    }

    @Test
    void shouldSplitListWithCustomSeparator() {
        List<String> result = StringUtils.splitList("a;b;c", ";");

        assertThat(result).hasSize(3).containsExactly("a", "b", "c");
    }

    @Test
    void shouldReturnEmptyListWhenSplitBlankString() {
        List<String> result = StringUtils.splitList("");
        assertThat(result).isEmpty();
    }

    @Test
    void shouldSplitToWithCustomMapper() {
        List<Integer> result =
                StringUtils.splitTo("1,2,3", obj -> Integer.parseInt(obj.toString()));

        assertThat(result).hasSize(3).containsExactly(1, 2, 3);
    }

    @Test
    void shouldSplitToWithCustomSeparatorAndMapper() {
        List<Long> result =
                StringUtils.splitTo("100;200;300", ";", obj -> Long.parseLong(obj.toString()));

        assertThat(result).hasSize(3).containsExactly(100L, 200L, 300L);
    }

    @Test
    void shouldFilterNullValuesInSplitTo() {
        List<String> result =
                StringUtils.splitTo(
                        "1,2,,3,",
                        obj -> {
                            try {
                                return obj.toString();
                            } catch (Exception e) {
                                return null;
                            }
                        });

        // 过滤掉 null 值
        assertThat(result).hasSizeGreaterThan(0);
    }

    // ========================================
    // 前缀检测测试
    // ========================================

    @Test
    void shouldReturnTrueWhenStartsWithAnyIgnoreCase() {
        assertThat(StringUtils.startWithAnyIgnoreCase("Hello World", "HELLO")).isTrue();
        assertThat(StringUtils.startWithAnyIgnoreCase("Hello World", "hello", "world")).isTrue();
        assertThat(StringUtils.startWithAnyIgnoreCase("Test", "test", "demo")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNotStartsWithAnyIgnoreCase() {
        assertThat(StringUtils.startWithAnyIgnoreCase("Hello World", "world")).isFalse();
        assertThat(StringUtils.startWithAnyIgnoreCase("Test", "demo", "sample")).isFalse();
    }

    // ========================================
    // 字符集转换测试
    // ========================================

    @Test
    void shouldConvertCharset() {
        String original = "Hello";
        String result =
                StringUtils.convert(original, StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldReturnOriginalWhenConvertBlankString() {
        assertThat(StringUtils.convert("", StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1))
                .isEmpty();
        assertThat(StringUtils.convert("   ", StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1))
                .isEqualTo("   ");
    }

    @Test
    void shouldReturnOriginalWhenConvertNull() {
        assertThat(StringUtils.convert(null, StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1))
                .isNull();
    }

    // ========================================
    // 逗号拼接测试
    // ========================================

    @Test
    void shouldJoinIterableWithComma() {
        List<String> list = Arrays.asList("a", "b", "c");
        String result = StringUtils.joinComma(list);

        assertThat(result).isEqualTo("a,b,c");
    }

    @Test
    void shouldJoinArrayWithComma() {
        String[] array = {"a", "b", "c"};
        String result = StringUtils.joinComma(array);

        assertThat(result).isEqualTo("a,b,c");
    }

    @Test
    void shouldJoinIntegerArrayWithComma() {
        Integer[] array = {1, 2, 3};
        String result = StringUtils.joinComma(array);

        assertThat(result).isEqualTo("1,2,3");
    }

    @Test
    void shouldJoinEmptyIterableWithComma() {
        List<String> list = List.of();
        String result = StringUtils.joinComma(list);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldJoinSingleElementWithComma() {
        List<String> list = List.of("single");
        String result = StringUtils.joinComma(list);

        assertThat(result).isEqualTo("single");
    }
}
