package org.dromara.common.core.utils.ip;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * AddressUtils 测试类
 *
 * <p>注意：由于 RegionUtils 在静态初始化时会加载 ip2region.xdb 文件， 且该文件在测试环境中不存在，因此本测试只覆盖不依赖 RegionUtils 的逻辑。 涉及
 * RegionUtils 的测试需要在集成测试中完成。
 *
 * @author Test Team
 */
@DisplayName("AddressUtils 测试")
class AddressUtilsTest {

    @Nested
    @DisplayName("常量定义测试")
    class ConstantTest {

        @Test
        @DisplayName("应该正确定义未知IP常量")
        void shouldHaveCorrectUnknownIpConstant() {
            assertThat(AddressUtils.UNKNOWN_IP).isEqualTo("XX XX");
        }

        @Test
        @DisplayName("应该正确定义内网地址常量")
        void shouldHaveCorrectLocalAddressConstant() {
            assertThat(AddressUtils.LOCAL_ADDRESS).isEqualTo("内网IP");
        }

        @Test
        @DisplayName("应该正确定义未知地址常量")
        void shouldHaveCorrectUnknownAddressConstant() {
            assertThat(AddressUtils.UNKNOWN_ADDRESS).isEqualTo("未知");
        }
    }

    @Nested
    @DisplayName("getRealAddressByIP - IPv4地址解析测试")
    class GetRealAddressByIPv4Test {

        @Test
        @DisplayName("应该返回内网IP标识（IPv4私有地址）")
        void shouldReturnLocalAddressForPrivateIPv4() {
            // 10.0.0.0/8
            assertThat(AddressUtils.getRealAddressByIP("10.0.0.1"))
                    .isEqualTo(AddressUtils.LOCAL_ADDRESS);

            // 172.16.0.0/12
            assertThat(AddressUtils.getRealAddressByIP("172.16.0.1"))
                    .isEqualTo(AddressUtils.LOCAL_ADDRESS);

            // 192.168.0.0/16
            assertThat(AddressUtils.getRealAddressByIP("192.168.1.1"))
                    .isEqualTo(AddressUtils.LOCAL_ADDRESS);
        }

        @Test
        @DisplayName("应该返回本地回环地址标识")
        void shouldReturnLocalAddressForLoopback() {
            assertThat(AddressUtils.getRealAddressByIP("127.0.0.1"))
                    .isEqualTo(AddressUtils.LOCAL_ADDRESS);
        }
    }

    @Nested
    @DisplayName("getRealAddressByIP - IPv6地址解析测试")
    class GetRealAddressByIPv6Test {

        @Test
        @DisplayName("应该返回未知地址（IPv6公网地址不支持）")
        void shouldReturnUnknownAddressForPublicIPv6() {
            // 公网IPv6地址（ip2region不支持IPv6）
            assertThat(AddressUtils.getRealAddressByIP("2001:4860:4860::8888"))
                    .isEqualTo(AddressUtils.UNKNOWN_ADDRESS);

            assertThat(AddressUtils.getRealAddressByIP("2400:3200::1"))
                    .isEqualTo(AddressUtils.UNKNOWN_ADDRESS);
        }
    }

    @Nested
    @DisplayName("getRealAddressByIP - 边界和异常测试")
    class GetRealAddressByIPBoundaryTest {

        @Test
        @DisplayName("应该处理 null 输入")
        void shouldHandleNullInput() {
            String result = AddressUtils.getRealAddressByIP(null);
            assertThat(result).isEqualTo(AddressUtils.UNKNOWN_IP);
        }

        @Test
        @DisplayName("应该处理空字符串输入")
        void shouldHandleEmptyStringInput() {
            assertThat(AddressUtils.getRealAddressByIP("")).isEqualTo(AddressUtils.UNKNOWN_IP);

            assertThat(AddressUtils.getRealAddressByIP("   ")).isEqualTo(AddressUtils.UNKNOWN_IP);
        }

        @Test
        @DisplayName("应该处理无效的IP地址格式")
        void shouldHandleInvalidIpFormat() {
            assertThat(AddressUtils.getRealAddressByIP("invalid-ip"))
                    .isEqualTo(AddressUtils.UNKNOWN_IP);

            assertThat(AddressUtils.getRealAddressByIP("256.256.256.256"))
                    .isEqualTo(AddressUtils.UNKNOWN_IP);

            assertThat(AddressUtils.getRealAddressByIP("192.168.1"))
                    .isEqualTo(AddressUtils.UNKNOWN_IP);

            assertThat(AddressUtils.getRealAddressByIP("192.168.1.1.1"))
                    .isEqualTo(AddressUtils.UNKNOWN_IP);
        }

        @Test
        @DisplayName("应该处理主机名而非IP地址")
        void shouldHandleHostname() {
            assertThat(AddressUtils.getRealAddressByIP("www.example.com"))
                    .isEqualTo(AddressUtils.UNKNOWN_IP);

            assertThat(AddressUtils.getRealAddressByIP("localhost"))
                    .isEqualTo(AddressUtils.UNKNOWN_IP);
        }
    }

    // 注意：特殊IP地址（0.0.0.0, 255.255.255.255, 组播地址等）
    // 会被识别为有效IPv4，从而尝试调用 RegionUtils。
    // 这些测试需要在集成测试中完成。

    @Nested
    @DisplayName("getRealAddressByIP - 真实场景测试")
    class GetRealAddressByIPRealScenarioTest {

        @Test
        @DisplayName("应该正确处理企业内网网段")
        void shouldHandleEnterprisePrivateNetworks() {
            // 企业常用网段
            assertThat(AddressUtils.getRealAddressByIP("10.1.1.1"))
                    .isEqualTo(AddressUtils.LOCAL_ADDRESS);

            assertThat(AddressUtils.getRealAddressByIP("172.16.100.1"))
                    .isEqualTo(AddressUtils.LOCAL_ADDRESS);

            assertThat(AddressUtils.getRealAddressByIP("192.168.0.100"))
                    .isEqualTo(AddressUtils.LOCAL_ADDRESS);
        }
    }
}
