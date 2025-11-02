package org.dromara.common.core.utils.regex;

import org.dromara.common.core.BaseUnitTest;
import org.dromara.common.core.constant.RegexConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RegexUtils 单元测试
 *
 * @author Lion Li
 */
@DisplayName("RegexUtils 工具类测试")
class RegexUtilsTest extends BaseUnitTest {

    @Nested
    @DisplayName("extractFromString 方法测试")
    class ExtractFromStringTest {

        @Test
        @DisplayName("从字符串中提取匹配的邮箱")
        void shouldExtractEmailFromString() {
            String input = "联系邮箱: test@example.com，请及时查收";
            // 需要添加捕获组
            String regex = "(" + RegexConstants.EMAIL + ")";
            String result = RegexUtils.extractFromString(input, regex, "无邮箱");

            assertThat(result).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("从字符串中提取匹配的手机号")
        void shouldExtractMobileFromString() {
            String input = "我的手机号是13812345678，欢迎联系";
            // 需要添加捕获组
            String regex = "(" + RegexConstants.MOBILE + ")";
            String result = RegexUtils.extractFromString(input, regex, "无手机号");

            assertThat(result).isEqualTo("13812345678");
        }

        @Test
        @DisplayName("从字符串中提取匹配的身份证号")
        void shouldExtractIdCardFromString() {
            String input = "身份证号: 110101199001011234";
            // 需要添加捕获组
            String regex = "(" + RegexConstants.CITIZEN_ID + ")";
            String result = RegexUtils.extractFromString(input, regex, "无身份证");

            assertThat(result).isEqualTo("110101199001011234");
        }

        @Test
        @DisplayName("从字符串中提取匹配的URL")
        void shouldExtractUrlFromString() {
            String input = "访问网站 https://www.example.com 了解更多";
            // 需要添加捕获组
            String regex = "(" + RegexConstants.URL_HTTP + ")";
            String result = RegexUtils.extractFromString(input, regex, "无URL");

            assertThat(result).isEqualTo("https://www.example.com");
        }

        @Test
        @DisplayName("从字符串中提取匹配的IPv4地址")
        void shouldExtractIpv4FromString() {
            String input = "服务器IP: 192.168.1.1，请连接";
            // IPv4正则需要捕获组，不需要锚点
            String regex = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
            String result = RegexUtils.extractFromString(input, regex, "无IP");

            assertThat(result).isEqualTo("192.168.1.1");
        }

        @Test
        @DisplayName("从完整字符串中提取邮政编码")
        void shouldExtractPostalCodeFromFullString() {
            // POSTAL_CODE 有锚点，需要完整匹配
            String input = "100000";  // 只包含邮政编码本身
            String regex = RegexConstants.POSTAL_CODE;
            String result = RegexUtils.extractFromString(input, "(" + regex + ")", "无邮编");

            assertThat(result).isEqualTo("100000");
        }

        @Test
        @DisplayName("从完整字符串中提取账号")
        void shouldExtractAccountFromFullString() {
            // ACCOUNT 有锚点，需要完整匹配
            String input = "admin123";  // 只包含账号本身
            String regex = RegexConstants.ACCOUNT;
            String result = RegexUtils.extractFromString(input, "(" + regex + ")", "未匹配");

            assertThat(result).isEqualTo("admin123");
        }

        @Test
        @DisplayName("从完整字符串中提取QQ号")
        void shouldExtractQQNumberFromFullString() {
            // QQ_NUMBER 有锚点，需要完整匹配
            String input = "123456789";  // 只包含QQ号本身
            String regex = RegexConstants.QQ_NUMBER;
            String result = RegexUtils.extractFromString(input, "(" + regex + ")", "未匹配");

            assertThat(result).isEqualTo("123456789");
        }

        @Test
        @DisplayName("从完整字符串中提取字典类型")
        void shouldExtractDictionaryTypeFromFullString() {
            // DICTIONARY_TYPE 有锚点，需要完整匹配
            String input = "user_type";  // 只包含字典类型本身
            String regex = RegexConstants.DICTIONARY_TYPE;
            String result = RegexUtils.extractFromString(input, "(" + regex + ")", "未匹配");

            assertThat(result).isEqualTo("user_type");
        }

        @Test
        @DisplayName("没有匹配时返回默认值")
        void shouldReturnDefaultWhenNoMatch() {
            String input = "这是一段没有邮箱的文本";
            String result = RegexUtils.extractFromString(input, RegexConstants.EMAIL, "无邮箱");

            assertThat(result).isEqualTo("无邮箱");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("输入为null或空字符串时返回默认值")
        void shouldReturnDefaultWhenInputIsNullOrEmpty(String input) {
            String result = RegexUtils.extractFromString(input, RegexConstants.EMAIL, "默认值");

            assertThat(result).isEqualTo("默认值");
        }

        @Test
        @DisplayName("正则表达式为null时返回默认值")
        void shouldReturnDefaultWhenRegexIsNull() {
            String input = "test@example.com";
            String result = RegexUtils.extractFromString(input, null, "默认值");

            assertThat(result).isEqualTo("默认值");
        }

        @Test
        @DisplayName("正则表达式无效时返回默认值")
        void shouldReturnDefaultWhenRegexIsInvalid() {
            String input = "test content";
            String invalidRegex = "[invalid(regex";
            String result = RegexUtils.extractFromString(input, invalidRegex, "默认值");

            assertThat(result).isEqualTo("默认值");
        }

        @Test
        @DisplayName("默认值为null")
        void shouldSupportNullDefaultValue() {
            String input = "没有匹配内容";
            String result = RegexUtils.extractFromString(input, RegexConstants.EMAIL, null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("默认值为空字符串")
        void shouldSupportEmptyDefaultValue() {
            String input = "没有匹配内容";
            String result = RegexUtils.extractFromString(input, RegexConstants.EMAIL, "");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("提取中文字符")
        void shouldExtractChineseCharacters() {
            String input = "姓名: 张三，年龄: 25";
            String chineseRegex = "[\u4e00-\u9fa5]+";
            String result = RegexUtils.extractFromString(input, "姓名:\\s*([\\u4e00-\\u9fa5]+)", "无姓名");

            assertThat(result).isEqualTo("张三");
        }

        @Test
        @DisplayName("提取数字")
        void shouldExtractNumbers() {
            String input = "价格: 12345 元";
            String result = RegexUtils.extractFromString(input, "价格:\\s*(\\d+)", "无价格");

            assertThat(result).isEqualTo("12345");
        }

        @Test
        @DisplayName("提取带小数的数字")
        void shouldExtractDecimalNumbers() {
            String input = "金额: 123.45 元";
            String result = RegexUtils.extractFromString(input, "金额:\\s*(\\d+\\.\\d+)", "无金额");

            assertThat(result).isEqualTo("123.45");
        }

        @Test
        @DisplayName("从完整字符串中提取权限字符串")
        void shouldExtractPermissionStringFromFullString() {
            // PERMISSION_STRING 有锚点，需要完整匹配
            String input = "system:user:add";  // 只包含权限字符串本身
            String regex = RegexConstants.PERMISSION_STRING;
            String result = RegexUtils.extractFromString(input, "(" + regex + ")", "无权限");

            // 验证提取的结果符合权限字符串格式
            assertThat(result).isEqualTo("system:user:add");
        }

        @Test
        @DisplayName("多个匹配时提取第一个")
        void shouldExtractFirstMatchWhenMultipleMatches() {
            String input = "邮箱1: test1@example.com, 邮箱2: test2@example.com";
            // 需要添加捕获组
            String regex = "(" + RegexConstants.EMAIL + ")";
            String result = RegexUtils.extractFromString(input, regex, "无邮箱");

            // extractFromString 使用 ReUtil.get(regex, input, 1)，提取第一个匹配的分组
            assertThat(result).isEqualTo("test1@example.com");
        }

        @Test
        @DisplayName("包含特殊字符的输入")
        void shouldHandleInputWithSpecialCharacters() {
            String input = "特殊字符: $@#%^&*()";
            String result = RegexUtils.extractFromString(input, "特殊字符:\\s*([\\$@#%\\^&*()]+)", "无特殊字符");

            assertThat(result).isEqualTo("$@#%^&*()");
        }

        @Test
        @DisplayName("提取HTML标签内容")
        void shouldExtractHtmlTagContent() {
            String input = "<div>Hello World</div>";
            String result = RegexUtils.extractFromString(input, "<div>(.*?)</div>", "无内容");

            assertThat(result).isEqualTo("Hello World");
        }

        @Test
        @DisplayName("提取日期格式")
        void shouldExtractDateFormat() {
            String input = "日期: 2025-10-29";
            String result = RegexUtils.extractFromString(input, "日期:\\s*(\\d{4}-\\d{2}-\\d{2})", "无日期");

            assertThat(result).isEqualTo("2025-10-29");
        }

        @Test
        @DisplayName("提取时间格式")
        void shouldExtractTimeFormat() {
            String input = "时间: 14:30:45";
            String result = RegexUtils.extractFromString(input, "时间:\\s*(\\d{2}:\\d{2}:\\d{2})", "无时间");

            assertThat(result).isEqualTo("14:30:45");
        }
    }

    @Nested
    @DisplayName("继承自Hutool ReUtil的方法测试")
    class HutoolMethodsTest {

        @Test
        @DisplayName("isMatch方法 - 验证邮箱格式")
        void shouldValidateEmailFormat() {
            assertThat(RegexUtils.isMatch(RegexConstants.EMAIL, "test@example.com")).isTrue();
            assertThat(RegexUtils.isMatch(RegexConstants.EMAIL, "invalid-email")).isFalse();
        }

        @Test
        @DisplayName("isMatch方法 - 验证手机号格式")
        void shouldValidateMobileFormat() {
            assertThat(RegexUtils.isMatch(RegexConstants.MOBILE, "13812345678")).isTrue();
            assertThat(RegexUtils.isMatch(RegexConstants.MOBILE, "12345")).isFalse();
        }

        @Test
        @DisplayName("isMatch方法 - 验证身份证格式")
        void shouldValidateIdCardFormat() {
            assertThat(RegexUtils.isMatch(RegexConstants.CITIZEN_ID, "110101199001011234")).isTrue();
            assertThat(RegexUtils.isMatch(RegexConstants.CITIZEN_ID, "12345")).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "user_type",
            "system_config",
            "a123_test"
        })
        @DisplayName("验证字典类型格式 - 有效格式")
        void shouldValidateDictionaryTypeFormat(String input) {
            assertThat(RegexUtils.isMatch(RegexConstants.DICTIONARY_TYPE, input)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "User_Type",      // 大写字母开头
            "1_type",         // 数字开头
            "_type",          // 下划线开头
            "user-type",      // 包含连字符
            "user type"       // 包含空格
        })
        @DisplayName("验证字典类型格式 - 无效格式")
        void shouldRejectInvalidDictionaryTypeFormat(String input) {
            assertThat(RegexUtils.isMatch(RegexConstants.DICTIONARY_TYPE, input)).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "system:user:add",
            "system:user:*",
            "system:*:*",
            "admin:role:delete",
            ""  // 允许空字符串
        })
        @DisplayName("验证权限字符串格式 - 有效格式")
        void shouldValidatePermissionStringFormat(String input) {
            assertThat(RegexUtils.isMatch(RegexConstants.PERMISSION_STRING, input)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "*:user:add",     // 第一部分不能有 *
            "system",         // 缺少部分
            "system:user",    // 只有两部分
            "system::add",    // 中间为空
            "system:user:add:extra"  // 多余部分
        })
        @DisplayName("验证权限字符串格式 - 无效格式")
        void shouldRejectInvalidPermissionStringFormat(String input) {
            assertThat(RegexUtils.isMatch(RegexConstants.PERMISSION_STRING, input)).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "100000",
            "200000",
            "518000"
        })
        @DisplayName("验证邮政编码格式 - 有效格式")
        void shouldValidatePostalCodeFormat(String input) {
            assertThat(RegexUtils.isMatch(RegexConstants.POSTAL_CODE, input)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "000000",   // 不能以0开头
            "12345",    // 不足6位
            "1234567",  // 超过6位
            "10000a"    // 包含字母
        })
        @DisplayName("验证邮政编码格式 - 无效格式")
        void shouldRejectInvalidPostalCodeFormat(String input) {
            assertThat(RegexUtils.isMatch(RegexConstants.POSTAL_CODE, input)).isFalse();
        }

        @Test
        @DisplayName("get方法 - 提取匹配的分组")
        void shouldExtractMatchedGroup() {
            String input = "邮箱: test@example.com";
            String result = RegexUtils.get(RegexConstants.EMAIL, input, 0);

            assertThat(result).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("replaceAll方法 - 替换所有匹配项")
        void shouldReplaceAllMatches() {
            String input = "手机号: 13812345678, 备用: 13987654321";
            String result = RegexUtils.replaceAll(input, RegexConstants.MOBILE, "***");

            assertThat(result).isEqualTo("手机号: ***, 备用: ***");
        }
    }

    @Nested
    @DisplayName("边界情况测试")
    class EdgeCasesTest {

        @Test
        @DisplayName("超长字符串处理")
        void shouldHandleVeryLongString() {
            String longString = "A".repeat(10000) + " test@example.com " + "B".repeat(10000);
            // 需要添加捕获组
            String regex = "(" + RegexConstants.EMAIL + ")";
            String result = RegexUtils.extractFromString(longString, regex, "无邮箱");

            assertThat(result).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("包含换行符的字符串")
        void shouldHandleStringWithNewlines() {
            String input = "第一行\n邮箱: test@example.com\n第三行";
            // 需要添加捕获组
            String regex = "(" + RegexConstants.EMAIL + ")";
            String result = RegexUtils.extractFromString(input, regex, "无邮箱");

            assertThat(result).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("包含制表符的字符串")
        void shouldHandleStringWithTabs() {
            String input = "信息:\ttest@example.com\t更多";
            // 需要添加捕获组
            String regex = "(" + RegexConstants.EMAIL + ")";
            String result = RegexUtils.extractFromString(input, regex, "无邮箱");

            assertThat(result).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("Unicode字符处理")
        void shouldHandleUnicodeCharacters() {
            String input = "邮箱📧: test@example.com";
            // 需要添加捕获组
            String regex = "(" + RegexConstants.EMAIL + ")";
            String result = RegexUtils.extractFromString(input, regex, "无邮箱");

            assertThat(result).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("正则表达式没有捕获组时")
        void shouldHandleRegexWithoutCaptureGroup() {
            String input = "test@example.com";
            // 使用没有捕获组的正则
            String result = RegexUtils.extractFromString(input, RegexConstants.EMAIL, "默认值");

            // 因为extractFromString使用get(regex, input, 1)获取第一个分组
            // 如果没有分组会返回null，进而返回defaultInput
            assertThat(result).isEqualTo("默认值");
        }
    }
}
