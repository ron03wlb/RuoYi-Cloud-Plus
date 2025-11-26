package org.dromara.common.core.utils.ip;

import static org.assertj.core.api.Assertions.assertThat;

import org.dromara.common.core.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * RegionUtils 集成测试类
 *
 * <p>此测试需要 ip2region.xdb 资源文件支持。 测试 IP 地址解析功能,包括国内外公网IP、内网IP、特殊IP等场景。
 *
 * @author Test Team
 */
@DisplayName("RegionUtils 集成测试")
class RegionUtilsIntegrationTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("常量定义测试")
    class ConstantTest {

        @Test
        @DisplayName("应该正确定义IP地址库文件名称")
        void shouldHaveCorrectXdbFilename() {
            assertThat(RegionUtils.IP_XDB_FILENAME).isEqualTo("ip2region.xdb");
        }
    }

    @Nested
    @DisplayName("getCityInfo - 国内公网IP解析测试")
    class GetCityInfoDomesticIPTest {

        @ParameterizedTest
        @CsvSource({
            "114.114.114.114, 中国", // 114DNS (江苏南京)
            "119.75.217.109, 中国", // 百度服务器 (北京)
            "223.5.5.5, 中国", // 阿里DNS (浙江杭州)
            "180.101.50.242, 中国", // 百度搜索 (北京)
            "61.135.169.121, 中国" // 新浪 (北京)
        })
        @DisplayName("应该正确解析国内常见公网IP地址")
        void shouldCorrectlyResolveDomesticPublicIP(String ip, String expectedCountry) {
            String result = RegionUtils.getCityInfo(ip);

            assertThat(result).isNotNull().isNotEqualTo("未知").contains(expectedCountry);
        }

        @Test
        @DisplayName("应该返回详细的地理位置信息（包含省份/城市）")
        void shouldReturnDetailedLocationInfo() {
            // 114.114.114.114 是江苏南京的IP
            String result = RegionUtils.getCityInfo("114.114.114.114");

            assertThat(result)
                    .isNotNull()
                    .satisfiesAnyOf(
                            // 可能包含省份信息
                            r -> assertThat(r).containsAnyOf("江苏", "南京", "中国"),
                            // 或者至少包含国家信息
                            r -> assertThat(r).contains("中国"));
        }
    }

    @Nested
    @DisplayName("getCityInfo - 国外公网IP解析测试")
    class GetCityInfoInternationalIPTest {

        @ParameterizedTest
        @CsvSource({
            "8.8.8.8, 美国", // Google DNS
            "1.1.1.1, 澳大利亚", // Cloudflare DNS
            "208.67.222.222, 美国" // OpenDNS
        })
        @DisplayName("应该正确解析国外常见公网IP地址")
        void shouldCorrectlyResolveInternationalPublicIP(String ip, String expectedCountry) {
            String result = RegionUtils.getCityInfo(ip);

            assertThat(result).isNotNull().isNotEqualTo("未知").contains(expectedCountry);
        }
    }

    @Nested
    @DisplayName("getCityInfo - 内网IP地址测试")
    class GetCityInfoPrivateIPTest {

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "10.0.0.1", // 10.0.0.0/8
                    "172.16.0.1", // 172.16.0.0/12
                    "192.168.1.1", // 192.168.0.0/16
                    "127.0.0.1" // 本地回环
                })
        @DisplayName("应该处理内网IP地址（可能返回特殊标识）")
        void shouldHandlePrivateIPAddresses(String ip) {
            String result = RegionUtils.getCityInfo(ip);

            // 内网IP可能返回 "0" 或其他特殊标识
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("getCityInfo - 特殊IP地址测试")
    class GetCityInfoSpecialIPTest {

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "0.0.0.0", // 未指定地址
                    "255.255.255.255", // 广播地址
                    "224.0.0.1", // 组播地址
                    "169.254.1.1" // 链路本地地址
                })
        @DisplayName("应该处理特殊IP地址而不抛出异常")
        void shouldHandleSpecialIPAddressesWithoutException(String ip) {
            String result = RegionUtils.getCityInfo(ip);

            // 特殊IP可能无法解析,但不应该抛异常
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("getCityInfo - 无效IP地址测试")
    class GetCityInfoInvalidIPTest {

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "invalid-ip",
                    "256.256.256.256",
                    "192.168.1",
                    "192.168.1.1.1",
                    "www.example.com",
                    "localhost"
                })
        @DisplayName("应该处理无效IP格式,返回未知")
        void shouldHandleInvalidIPFormat(String ip) {
            String result = RegionUtils.getCityInfo(ip);

            assertThat(result).isNotNull().isEqualTo("未知");
        }
    }

    @Nested
    @DisplayName("getCityInfo - 边界测试")
    class GetCityInfoBoundaryTest {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("应该处理 null 和空字符串,返回未知")
        void shouldHandleNullAndEmptyString(String ip) {
            String result = RegionUtils.getCityInfo(ip);

            assertThat(result).isNotNull().isEqualTo("未知");
        }

        @ParameterizedTest
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("应该处理只包含空白字符的字符串")
        void shouldHandleWhitespaceOnlyString(String ip) {
            String result = RegionUtils.getCityInfo(ip);

            assertThat(result).isNotNull().isEqualTo("未知");
        }

        @ParameterizedTest
        @CsvSource({"  114.114.114.114  , 中国", "\t114.114.114.114\t, 中国", " 114.114.114.114, 中国"})
        @DisplayName("应该正确处理带前后空格的IP地址")
        void shouldTrimIPAddressWithWhitespace(String ip, String expectedCountry) {
            String result = RegionUtils.getCityInfo(ip);

            assertThat(result).isNotNull().contains(expectedCountry);
        }

        @Test
        @DisplayName("应该处理超长字符串输入")
        void shouldHandleVeryLongString() {
            String veryLongIp = "1".repeat(1000);

            String result = RegionUtils.getCityInfo(veryLongIp);

            assertThat(result).isNotNull().isEqualTo("未知");
        }
    }

    @Nested
    @DisplayName("getCityInfo - 数据格式化测试")
    class GetCityInfoFormattingTest {

        @Test
        @DisplayName("应该移除结果中的 '0|' 前缀")
        void shouldRemoveZeroPrefix() {
            // ip2region 返回格式: 国家|区域|省份|城市|ISP
            // 例如: "中国|0|江苏省|南京市|电信"
            // 方法应该移除 "0|" 部分
            String result = RegionUtils.getCityInfo("114.114.114.114");

            assertThat(result).isNotNull().doesNotContain("0|");
        }

        @Test
        @DisplayName("应该移除结果中的 '|0' 后缀")
        void shouldRemoveZeroSuffix() {
            String result = RegionUtils.getCityInfo("114.114.114.114");

            assertThat(result).isNotNull().doesNotContain("|0");
        }

        @Test
        @DisplayName("返回的地址信息应该不为空且有意义")
        void shouldReturnMeaningfulAddress() {
            String result = RegionUtils.getCityInfo("114.114.114.114");

            assertThat(result)
                    .isNotNull()
                    .isNotEmpty()
                    .isNotBlank()
                    .hasSizeGreaterThan(1); // 至少包含国家名称
        }
    }

    @Nested
    @DisplayName("getCityInfo - 真实业务场景测试")
    class GetCityInfoRealScenarioTest {

        @Test
        @DisplayName("应该正确解析用户登录IP（国内）")
        void shouldResolveUserLoginIPDomestic() {
            // 模拟用户从北京登录
            String loginIP = "180.101.50.242"; // 百度服务器IP

            String location = RegionUtils.getCityInfo(loginIP);

            assertThat(location).isNotNull().contains("中国");
        }

        @Test
        @DisplayName("应该正确解析API请求来源IP（国外）")
        void shouldResolveAPIRequestIPInternational() {
            // 模拟来自美国的API请求
            String apiIP = "8.8.8.8";

            String location = RegionUtils.getCityInfo(apiIP);

            assertThat(location).isNotNull().contains("美国");
        }

        @Test
        @DisplayName("应该处理内网环境下的IP地址")
        void shouldHandleIntranetEnvironmentIP() {
            // 模拟企业内网IP
            String intranetIP = "192.168.100.50";

            String location = RegionUtils.getCityInfo(intranetIP);

            // 内网IP不应该抛异常,应该返回特定结果
            assertThat(location).isNotNull();
        }
    }

    @Nested
    @DisplayName("静态初始化测试")
    class StaticInitializationTest {

        @Test
        @DisplayName("RegionUtils 应该成功完成静态初始化")
        void shouldSuccessfullyInitializeStaticBlock() {
            // 如果静态初始化失败,会抛出 ServiceException
            // 能执行到这里说明初始化成功

            // 验证能正常调用方法
            String result = RegionUtils.getCityInfo("114.114.114.114");

            assertThat(result).isNotNull().isNotEqualTo("未知");
        }

        @Test
        @DisplayName("应该能够重复调用 getCityInfo 而不出错")
        void shouldBeAbleToCallGetCityInfoMultipleTimes() {
            // 验证静态初始化的 Searcher 可以被重复使用
            for (int i = 0; i < 10; i++) {
                String result = RegionUtils.getCityInfo("114.114.114.114");
                assertThat(result).isNotNull().contains("中国");
            }
        }
    }
}
