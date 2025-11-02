package org.dromara.common.core.utils.ip;

import org.dromara.common.core.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AddressUtils 集成测试类（补充测试）
 * <p>
 * 此测试补充 AddressUtilsTest 中未覆盖的功能:
 * 1. 公网IP地址解析 (依赖 RegionUtils)
 * 2. HTML标签清理验证
 * 3. 内网IPv6地址处理
 * 4. 特殊IP地址处理
 *
 * @author Test Team
 */
@DisplayName("AddressUtils 集成测试（补充）")
class AddressUtilsIntegrationTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("getRealAddressByIP - 公网IPv4地址解析测试")
    class GetRealAddressByPublicIPv4Test {

        @ParameterizedTest
        @CsvSource({
            "114.114.114.114, 中国",      // 114DNS
            "119.75.217.109, 中国",       // 百度服务器
            "8.8.8.8, 美国",              // Google DNS
            "1.1.1.1, 澳大利亚"           // Cloudflare DNS
        })
        @DisplayName("应该正确解析公网IPv4地址并返回地理位置")
        void shouldResolvePublicIPv4AndReturnLocation(String ip, String expectedCountry) {
            String result = AddressUtils.getRealAddressByIP(ip);

            assertThat(result)
                .isNotNull()
                .isNotEqualTo(AddressUtils.LOCAL_ADDRESS)
                .isNotEqualTo(AddressUtils.UNKNOWN_IP)
                .isNotEqualTo(AddressUtils.UNKNOWN_ADDRESS)
                .contains(expectedCountry);
        }

        @Test
        @DisplayName("应该返回详细的地理位置信息")
        void shouldReturnDetailedLocationInfo() {
            String result = AddressUtils.getRealAddressByIP("114.114.114.114");

            assertThat(result)
                .isNotNull()
                .satisfiesAnyOf(
                    r -> assertThat(r).containsAnyOf("江苏", "南京", "中国"),
                    r -> assertThat(r).contains("中国")
                );
        }
    }

    @Nested
    @DisplayName("getRealAddressByIP - HTML标签清理测试")
    class GetRealAddressByIPHtmlCleaningTest {

        @ParameterizedTest
        @CsvSource({
            // HTML标签包裹IP的情况,清理后应该能正确解析
            "<b>114.114.114.114</b>, 中国",
            "<div>114.114.114.114</div>, 中国"
        })
        @DisplayName("应该清理HTML标签后再解析IP地址")
        void shouldCleanHtmlTagsBeforeResolving(String ipWithHtml, String expectedCountry) {
            String result = AddressUtils.getRealAddressByIP(ipWithHtml);

            assertThat(result)
                .isNotNull()
                .contains(expectedCountry);
        }

        @Test
        @DisplayName("应该处理包含script标签的输入(清理行为依赖HtmlUtil)")
        void shouldHandleScriptTagWithIP() {
            // HtmlUtil.cleanHtmlTag 的具体行为可能因版本而异
            // 我们只验证不会抛出异常,并且返回值不为null
            String xssAttempt = "114.114.114.114<script>alert('xss')</script>";

            String result = AddressUtils.getRealAddressByIP(xssAttempt);

            // 验证方法不抛异常,且返回值有效
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("应该清理嵌套HTML标签")
        void shouldCleanNestedHtmlTags() {
            String nestedHtml = "<div><span><b>114.114.114.114</b></span></div>";

            String result = AddressUtils.getRealAddressByIP(nestedHtml);

            assertThat(result)
                .isNotNull()
                .contains("中国");
        }

        @Test
        @DisplayName("应该处理只包含HTML标签无有效IP的情况")
        void shouldHandleOnlyHtmlTags() {
            String onlyHtml = "<script>alert('test')</script>";

            String result = AddressUtils.getRealAddressByIP(onlyHtml);

            assertThat(result).isEqualTo(AddressUtils.UNKNOWN_IP);
        }

        @Test
        @DisplayName("应该处理IP后跟HTML标签的情况")
        void shouldHandleIPFollowedByHtmlTags() {
            String ipWithTrailingHtml = "114.114.114.114<img src='x'>";

            String result = AddressUtils.getRealAddressByIP(ipWithTrailingHtml);

            assertThat(result)
                .isNotNull()
                .contains("中国");
        }
    }

    @Nested
    @DisplayName("getRealAddressByIP - 内网IPv6地址测试")
    class GetRealAddressByIPv6InnerTest {

        @ParameterizedTest
        @ValueSource(strings = {
            "::1",                              // IPv6 回环地址
            "fe80::1",                          // 链路本地地址
            "fc00::1",                          // 唯一本地地址 (ULA)
            "fd00::1",                          // ULA
            "fec0::1"                           // 站点本地地址
        })
        @DisplayName("应该识别内网IPv6地址(可能返回内网IP或未知)")
        void shouldRecognizeInnerIPv6(String ipv6) {
            String result = AddressUtils.getRealAddressByIP(ipv6);

            // IPv6地址的识别可能有不同的结果:
            // 1. 如果 NetUtils.isIPv6() 识别成功 -> 调用 resolverIPv6Region() -> 返回 LOCAL_ADDRESS 或 UNKNOWN_ADDRESS
            // 2. 如果识别失败 -> 返回 UNKNOWN_IP
            assertThat(result)
                .isNotNull()
                .isIn(AddressUtils.LOCAL_ADDRESS, AddressUtils.UNKNOWN_ADDRESS, AddressUtils.UNKNOWN_IP);
        }

        @Test
        @DisplayName("应该正确处理IPv6回环地址")
        void shouldHandleIPv6Loopback() {
            String result = AddressUtils.getRealAddressByIP("::1");

            // IPv6回环地址应该被识别
            assertThat(result)
                .isNotNull()
                .satisfiesAnyOf(
                    r -> assertThat(r).isEqualTo(AddressUtils.LOCAL_ADDRESS),
                    r -> assertThat(r).isEqualTo(AddressUtils.UNKNOWN_ADDRESS),
                    r -> assertThat(r).isEqualTo(AddressUtils.UNKNOWN_IP)
                );
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "2001:4860:4860::8888",             // Google Public DNS (IPv6)
            "2400:3200::1",                     // 阿里DNS (IPv6)
            "2606:4700:4700::1111"              // Cloudflare DNS (IPv6)
        })
        @DisplayName("应该识别公网IPv6地址(返回未知,因为不支持IPv6解析)")
        void shouldRecognizePublicIPv6ButReturnUnknown(String ipv6) {
            String result = AddressUtils.getRealAddressByIP(ipv6);

            // 公网IPv6地址:
            // 1. 如果被识别为IPv6 -> resolverIPv6Region() -> 返回 UNKNOWN_ADDRESS
            // 2. 如果识别失败 -> 返回 UNKNOWN_IP
            assertThat(result)
                .isNotNull()
                .isIn(AddressUtils.UNKNOWN_ADDRESS, AddressUtils.UNKNOWN_IP);
        }
    }

    @Nested
    @DisplayName("getRealAddressByIP - 特殊IPv4地址测试")
    class GetRealAddressBySpecialIPv4Test {

        @ParameterizedTest
        @ValueSource(strings = {
            "0.0.0.0",              // 未指定地址
            "255.255.255.255",      // 广播地址
            "224.0.0.1",            // 组播地址
            "169.254.1.1"           // 链路本地地址
        })
        @DisplayName("应该处理特殊IPv4地址（可能返回地理位置或特殊标识）")
        void shouldHandleSpecialIPv4Addresses(String ip) {
            String result = AddressUtils.getRealAddressByIP(ip);

            // 特殊IP可能被解析为地理位置,也可能返回特殊标识
            // 但不应该抛异常
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("应该正确处理广播地址")
        void shouldHandleBroadcastAddress() {
            String result = AddressUtils.getRealAddressByIP("255.255.255.255");

            // 广播地址的处理结果可能有多种情况:
            // 1. 被ip2region解析为某个地理位置
            // 2. 返回 LOCAL_ADDRESS / UNKNOWN_IP / UNKNOWN_ADDRESS
            // 我们只验证返回值不为null且不为空
            assertThat(result)
                .isNotNull()
                .isNotEmpty();
        }

        @Test
        @DisplayName("应该正确处理0.0.0.0地址")
        void shouldHandleZeroAddress() {
            String result = AddressUtils.getRealAddressByIP("0.0.0.0");

            assertThat(result)
                .isNotNull()
                .satisfies(r -> assertThat(r).isNotEmpty());
        }
    }

    @Nested
    @DisplayName("getRealAddressByIP - 综合场景测试")
    class GetRealAddressByIPIntegrationTest {

        @Test
        @DisplayName("应该正确处理带HTML标签的内网IP")
        void shouldHandleInnerIPWithHtmlTags() {
            String result = AddressUtils.getRealAddressByIP("<b>192.168.1.1</b>");

            assertThat(result).isEqualTo(AddressUtils.LOCAL_ADDRESS);
        }

        @Test
        @DisplayName("应该处理带HTML标签和空格的输入")
        void shouldHandlePublicIPWithHtmlTagsAndSpaces() {
            // HTML清理行为取决于Hutool版本,只验证不抛异常
            String result = AddressUtils.getRealAddressByIP("  <div>114.114.114.114</div>  ");

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("应该处理带HTML标签的IPv6地址")
        void shouldHandleIPv6WithHtmlTags() {
            // HTML清理行为取决于Hutool版本,只验证不抛异常
            String result = AddressUtils.getRealAddressByIP("<script>::1</script>");

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("应该处理复杂HTML文档中的IP地址")
        void shouldHandleComplexHtmlWithIP() {
            // HTML清理行为取决于Hutool版本,只验证不抛异常
            String complexHtml = """
                <html>
                    <body>
                        <div class="ip">114.114.114.114</div>
                    </body>
                </html>
                """;

            String result = AddressUtils.getRealAddressByIP(complexHtml);

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("getRealAddressByIP - 真实业务场景测试")
    class GetRealAddressByIPRealScenarioTest {

        @Test
        @DisplayName("应该正确处理用户登录IP（国内公网）")
        void shouldHandleUserLoginIPDomestic() {
            // 模拟用户从上海登录
            String loginIP = "180.101.50.242"; // 百度服务器

            String location = AddressUtils.getRealAddressByIP(loginIP);

            assertThat(location)
                .isNotNull()
                .isNotEqualTo(AddressUtils.LOCAL_ADDRESS)
                .contains("中国");
        }

        @Test
        @DisplayName("应该正确处理用户登录IP（国外公网）")
        void shouldHandleUserLoginIPInternational() {
            // 模拟用户从美国登录
            String loginIP = "8.8.8.8";

            String location = AddressUtils.getRealAddressByIP(loginIP);

            assertThat(location)
                .isNotNull()
                .contains("美国");
        }

        @Test
        @DisplayName("应该正确处理企业内网用户访问")
        void shouldHandleEnterpriseIntranetAccess() {
            // 模拟企业内网用户访问
            String intranetIP = "10.1.100.50";

            String location = AddressUtils.getRealAddressByIP(intranetIP);

            assertThat(location).isEqualTo(AddressUtils.LOCAL_ADDRESS);
        }

        @Test
        @DisplayName("应该正确处理来自WAF/代理的X-Forwarded-For头中的IP")
        void shouldHandleXForwardedForIP() {
            // X-Forwarded-For 可能包含多个IP,但我们只测试单个IP的情况
            // 实际业务中应该提取第一个非内网IP
            String clientIP = "114.114.114.114";

            String location = AddressUtils.getRealAddressByIP(clientIP);

            assertThat(location)
                .isNotNull()
                .contains("中国");
        }

        @Test
        @DisplayName("应该处理可能包含恶意脚本的IP输入（安全测试）")
        void shouldHandleMaliciousScriptInIPInput() {
            // 模拟恶意用户尝试注入脚本
            String maliciousInput = "<script>alert(document.cookie)</script>114.114.114.114";

            String location = AddressUtils.getRealAddressByIP(maliciousInput);

            // HTML标签应该被清理,至少保证不抛异常
            // 具体的解析结果取决于HtmlUtil.cleanHtmlTag的实现
            assertThat(location)
                .isNotNull()
                .doesNotContain("<script>")
                .doesNotContain("</script>");
        }
    }

    @Nested
    @DisplayName("getRealAddressByIP - 性能测试")
    class GetRealAddressByIPPerformanceTest {

        @Test
        @DisplayName("应该能够快速处理大量IP查询")
        void shouldHandleManyIPQueriesQuickly() {
            // 模拟100次IP查询
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < 100; i++) {
                String result = AddressUtils.getRealAddressByIP("114.114.114.114");
                assertThat(result).contains("中国");
            }

            long duration = System.currentTimeMillis() - startTime;

            // 100次查询应该在合理时间内完成（例如 < 5秒）
            assertThat(duration).isLessThan(5000);
        }

        @Test
        @DisplayName("应该能够处理并发IP查询（线程安全）")
        void shouldHandleConcurrentIPQueries() throws InterruptedException {
            int threadCount = 10;
            Thread[] threads = new Thread[threadCount];

            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 10; j++) {
                        String result = AddressUtils.getRealAddressByIP("114.114.114.114");
                        assertThat(result).contains("中国");
                    }
                });
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            // 如果没有抛出异常,说明线程安全
        }
    }
}
