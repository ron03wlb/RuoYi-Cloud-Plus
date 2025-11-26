package org.dromara.auth.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dromara.auth.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 认证 VO 类测试
 *
 * <p>测试所有认证相关的 VO 类
 *
 * @author Test Team
 */
@DisplayName("认证 VO 类测试")
class AuthVoTest extends BaseUnitTest {

    @Nested
    @DisplayName("1. CaptchaVo 验证码信息测试")
    class CaptchaVoTests {

        @Test
        @DisplayName("应该能够创建 CaptchaVo 实例")
        void shouldCreateCaptchaVoInstance() {
            // Act
            CaptchaVo vo = new CaptchaVo();

            // Assert
            assertThat(vo).isNotNull();
        }

        @Test
        @DisplayName("captchaEnabled 默认值应该为 true")
        void captchaEnabledDefaultShouldBeTrue() {
            // Act
            CaptchaVo vo = new CaptchaVo();

            // Assert
            assertThat(vo.getCaptchaEnabled()).isNotNull().isTrue();
        }

        @Test
        @DisplayName("应该能够设置和获取 uuid")
        void shouldSetAndGetUuid() {
            // Arrange
            CaptchaVo vo = new CaptchaVo();
            String uuid = "550e8400-e29b-41d4-a716-446655440000";

            // Act
            vo.setUuid(uuid);

            // Assert
            assertThat(vo.getUuid()).isNotNull().isEqualTo(uuid);
        }

        @Test
        @DisplayName("应该能够设置和获取 img")
        void shouldSetAndGetImg() {
            // Arrange
            CaptchaVo vo = new CaptchaVo();
            String img = "data:image/png;base64,iVBORw0KGgoAAAANS...";

            // Act
            vo.setImg(img);

            // Assert
            assertThat(vo.getImg()).isNotNull().isEqualTo(img);
        }

        @Test
        @DisplayName("应该能够设置和获取 captchaEnabled")
        void shouldSetAndGetCaptchaEnabled() {
            // Arrange
            CaptchaVo vo = new CaptchaVo();

            // Act - 禁用验证码
            vo.setCaptchaEnabled(false);

            // Assert
            assertThat(vo.getCaptchaEnabled()).isFalse();

            // Act - 启用验证码
            vo.setCaptchaEnabled(true);

            // Assert
            assertThat(vo.getCaptchaEnabled()).isTrue();
        }

        @Test
        @DisplayName("应该能够配置完整的 CaptchaVo")
        void shouldConfigureFullCaptchaVo() {
            // Arrange
            CaptchaVo vo = new CaptchaVo();
            String uuid = "550e8400-e29b-41d4-a716-446655440000";
            String img = "data:image/png;base64,iVBORw0KGgoAAAANS...";

            // Act
            vo.setCaptchaEnabled(true);
            vo.setUuid(uuid);
            vo.setImg(img);

            // Assert
            assertThat(vo.getCaptchaEnabled()).isTrue();
            assertThat(vo.getUuid()).isEqualTo(uuid);
            assertThat(vo.getImg()).isEqualTo(img);
        }

        @Test
        @DisplayName("应该允许 null 值")
        void shouldAllowNullValues() {
            // Arrange
            CaptchaVo vo = new CaptchaVo();

            // Act
            vo.setCaptchaEnabled(null);
            vo.setUuid(null);
            vo.setImg(null);

            // Assert
            assertThat(vo.getCaptchaEnabled()).isNull();
            assertThat(vo.getUuid()).isNull();
            assertThat(vo.getImg()).isNull();
        }
    }

    @Nested
    @DisplayName("2. TenantListVo 租户列表测试")
    class TenantListVoTests {

        @Test
        @DisplayName("应该能够创建 TenantListVo 实例")
        void shouldCreateTenantListVoInstance() {
            // Act
            TenantListVo vo = new TenantListVo();

            // Assert
            assertThat(vo).isNotNull();
        }

        @Test
        @DisplayName("应该能够设置和获取 tenantId")
        void shouldSetAndGetTenantId() {
            // Arrange
            TenantListVo vo = new TenantListVo();
            String tenantId = "000000";

            // Act
            vo.setTenantId(tenantId);

            // Assert
            assertThat(vo.getTenantId()).isNotNull().isEqualTo(tenantId);
        }

        @Test
        @DisplayName("应该能够设置和获取 companyName")
        void shouldSetAndGetCompanyName() {
            // Arrange
            TenantListVo vo = new TenantListVo();
            String companyName = "测试公司";

            // Act
            vo.setCompanyName(companyName);

            // Assert
            assertThat(vo.getCompanyName()).isNotNull().isEqualTo(companyName);
        }

        @Test
        @DisplayName("应该能够设置和获取 domain")
        void shouldSetAndGetDomain() {
            // Arrange
            TenantListVo vo = new TenantListVo();
            String domain = "test.example.com";

            // Act
            vo.setDomain(domain);

            // Assert
            assertThat(vo.getDomain()).isNotNull().isEqualTo(domain);
        }

        @Test
        @DisplayName("应该能够配置完整的 TenantListVo")
        void shouldConfigureFullTenantListVo() {
            // Arrange
            TenantListVo vo = new TenantListVo();

            // Act
            vo.setTenantId("000000");
            vo.setCompanyName("测试公司");
            vo.setDomain("test.example.com");

            // Assert
            assertThat(vo.getTenantId()).isEqualTo("000000");
            assertThat(vo.getCompanyName()).isEqualTo("测试公司");
            assertThat(vo.getDomain()).isEqualTo("test.example.com");
        }

        @Test
        @DisplayName("应该允许 null 值")
        void shouldAllowNullValues() {
            // Arrange
            TenantListVo vo = new TenantListVo();

            // Act
            vo.setTenantId(null);
            vo.setCompanyName(null);
            vo.setDomain(null);

            // Assert
            assertThat(vo.getTenantId()).isNull();
            assertThat(vo.getCompanyName()).isNull();
            assertThat(vo.getDomain()).isNull();
        }
    }

    @Nested
    @DisplayName("3. LoginTenantVo 登录租户对象测试")
    class LoginTenantVoTests {

        @Test
        @DisplayName("应该能够创建 LoginTenantVo 实例")
        void shouldCreateLoginTenantVoInstance() {
            // Act
            LoginTenantVo vo = new LoginTenantVo();

            // Assert
            assertThat(vo).isNotNull();
        }

        @Test
        @DisplayName("应该能够设置和获取 tenantEnabled")
        void shouldSetAndGetTenantEnabled() {
            // Arrange
            LoginTenantVo vo = new LoginTenantVo();

            // Act - 启用租户
            vo.setTenantEnabled(true);

            // Assert
            assertThat(vo.getTenantEnabled()).isTrue();

            // Act - 禁用租户
            vo.setTenantEnabled(false);

            // Assert
            assertThat(vo.getTenantEnabled()).isFalse();
        }

        @Test
        @DisplayName("应该能够设置和获取 voList")
        void shouldSetAndGetVoList() {
            // Arrange
            LoginTenantVo vo = new LoginTenantVo();
            TenantListVo tenant1 = new TenantListVo();
            tenant1.setTenantId("000000");
            tenant1.setCompanyName("公司A");

            TenantListVo tenant2 = new TenantListVo();
            tenant2.setTenantId("000001");
            tenant2.setCompanyName("公司B");

            List<TenantListVo> tenantList = Arrays.asList(tenant1, tenant2);

            // Act
            vo.setVoList(tenantList);

            // Assert
            assertThat(vo.getVoList()).isNotNull().hasSize(2).contains(tenant1, tenant2);
        }

        @Test
        @DisplayName("应该能够配置完整的 LoginTenantVo")
        void shouldConfigureFullLoginTenantVo() {
            // Arrange
            LoginTenantVo vo = new LoginTenantVo();
            TenantListVo tenant = new TenantListVo();
            tenant.setTenantId("000000");
            tenant.setCompanyName("测试公司");
            tenant.setDomain("test.example.com");

            List<TenantListVo> tenantList = new ArrayList<>();
            tenantList.add(tenant);

            // Act
            vo.setTenantEnabled(true);
            vo.setVoList(tenantList);

            // Assert
            assertThat(vo.getTenantEnabled()).isTrue();
            assertThat(vo.getVoList()).hasSize(1);
            assertThat(vo.getVoList().get(0).getTenantId()).isEqualTo("000000");
        }

        @Test
        @DisplayName("应该支持空租户列表")
        void shouldSupportEmptyTenantList() {
            // Arrange
            LoginTenantVo vo = new LoginTenantVo();
            List<TenantListVo> emptyList = new ArrayList<>();

            // Act
            vo.setTenantEnabled(true);
            vo.setVoList(emptyList);

            // Assert
            assertThat(vo.getTenantEnabled()).isTrue();
            assertThat(vo.getVoList()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("应该允许 null 值")
        void shouldAllowNullValues() {
            // Arrange
            LoginTenantVo vo = new LoginTenantVo();

            // Act
            vo.setTenantEnabled(null);
            vo.setVoList(null);

            // Assert
            assertThat(vo.getTenantEnabled()).isNull();
            assertThat(vo.getVoList()).isNull();
        }
    }

    @Nested
    @DisplayName("4. LoginVo 登录验证信息测试")
    class LoginVoTests {

        @Test
        @DisplayName("应该能够创建 LoginVo 实例")
        void shouldCreateLoginVoInstance() {
            // Act
            LoginVo vo = new LoginVo();

            // Assert
            assertThat(vo).isNotNull();
        }

        @Test
        @DisplayName("应该能够设置和获取 accessToken")
        void shouldSetAndGetAccessToken() {
            // Arrange
            LoginVo vo = new LoginVo();
            String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

            // Act
            vo.setAccessToken(accessToken);

            // Assert
            assertThat(vo.getAccessToken()).isNotNull().isEqualTo(accessToken);
        }

        @Test
        @DisplayName("应该能够设置和获取 refreshToken")
        void shouldSetAndGetRefreshToken() {
            // Arrange
            LoginVo vo = new LoginVo();
            String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...refresh";

            // Act
            vo.setRefreshToken(refreshToken);

            // Assert
            assertThat(vo.getRefreshToken()).isNotNull().isEqualTo(refreshToken);
        }

        @Test
        @DisplayName("应该能够设置和获取 expireIn")
        void shouldSetAndGetExpireIn() {
            // Arrange
            LoginVo vo = new LoginVo();
            Long expireIn = 7200L; // 2小时

            // Act
            vo.setExpireIn(expireIn);

            // Assert
            assertThat(vo.getExpireIn()).isNotNull().isEqualTo(expireIn);
        }

        @Test
        @DisplayName("应该能够设置和获取 refreshExpireIn")
        void shouldSetAndGetRefreshExpireIn() {
            // Arrange
            LoginVo vo = new LoginVo();
            Long refreshExpireIn = 2592000L; // 30天

            // Act
            vo.setRefreshExpireIn(refreshExpireIn);

            // Assert
            assertThat(vo.getRefreshExpireIn()).isNotNull().isEqualTo(refreshExpireIn);
        }

        @Test
        @DisplayName("应该能够设置和获取 clientId")
        void shouldSetAndGetClientId() {
            // Arrange
            LoginVo vo = new LoginVo();
            String clientId = "e5cd7e4891bf95d1d19206ce24a7b32e";

            // Act
            vo.setClientId(clientId);

            // Assert
            assertThat(vo.getClientId()).isNotNull().isEqualTo(clientId);
        }

        @Test
        @DisplayName("应该能够设置和获取 scope")
        void shouldSetAndGetScope() {
            // Arrange
            LoginVo vo = new LoginVo();
            String scope = "read write";

            // Act
            vo.setScope(scope);

            // Assert
            assertThat(vo.getScope()).isNotNull().isEqualTo(scope);
        }

        @Test
        @DisplayName("应该能够设置和获取 openid")
        void shouldSetAndGetOpenid() {
            // Arrange
            LoginVo vo = new LoginVo();
            String openid = "oX5g_0A1B2C3D4E5F6G7H8I9J0K1L2M3";

            // Act
            vo.setOpenid(openid);

            // Assert
            assertThat(vo.getOpenid()).isNotNull().isEqualTo(openid);
        }

        @Test
        @DisplayName("应该能够配置完整的 LoginVo")
        void shouldConfigureFullLoginVo() {
            // Arrange
            LoginVo vo = new LoginVo();

            // Act
            vo.setAccessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
            vo.setRefreshToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...refresh");
            vo.setExpireIn(7200L);
            vo.setRefreshExpireIn(2592000L);
            vo.setClientId("e5cd7e4891bf95d1d19206ce24a7b32e");
            vo.setScope("read write");
            vo.setOpenid("oX5g_0A1B2C3D4E5F6G7H8I9J0K1L2M3");

            // Assert
            assertThat(vo.getAccessToken()).isEqualTo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
            assertThat(vo.getRefreshToken())
                    .isEqualTo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...refresh");
            assertThat(vo.getExpireIn()).isEqualTo(7200L);
            assertThat(vo.getRefreshExpireIn()).isEqualTo(2592000L);
            assertThat(vo.getClientId()).isEqualTo("e5cd7e4891bf95d1d19206ce24a7b32e");
            assertThat(vo.getScope()).isEqualTo("read write");
            assertThat(vo.getOpenid()).isEqualTo("oX5g_0A1B2C3D4E5F6G7H8I9J0K1L2M3");
        }

        @Test
        @DisplayName("expireIn 应该支持各种有效期")
        void expireInShouldSupportVariousDurations() {
            // Arrange
            LoginVo vo = new LoginVo();

            // Act & Assert - 1小时
            vo.setExpireIn(3600L);
            assertThat(vo.getExpireIn()).isEqualTo(3600L);

            // Act & Assert - 2小时
            vo.setExpireIn(7200L);
            assertThat(vo.getExpireIn()).isEqualTo(7200L);

            // Act & Assert - 1天
            vo.setExpireIn(86400L);
            assertThat(vo.getExpireIn()).isEqualTo(86400L);
        }

        @Test
        @DisplayName("refreshExpireIn 应该支持各种刷新期限")
        void refreshExpireInShouldSupportVariousDurations() {
            // Arrange
            LoginVo vo = new LoginVo();

            // Act & Assert - 7天
            vo.setRefreshExpireIn(604800L);
            assertThat(vo.getRefreshExpireIn()).isEqualTo(604800L);

            // Act & Assert - 30天
            vo.setRefreshExpireIn(2592000L);
            assertThat(vo.getRefreshExpireIn()).isEqualTo(2592000L);

            // Act & Assert - 90天
            vo.setRefreshExpireIn(7776000L);
            assertThat(vo.getRefreshExpireIn()).isEqualTo(7776000L);
        }

        @Test
        @DisplayName("应该允许 null 值")
        void shouldAllowNullValues() {
            // Arrange
            LoginVo vo = new LoginVo();

            // Act
            vo.setAccessToken(null);
            vo.setRefreshToken(null);
            vo.setExpireIn(null);
            vo.setRefreshExpireIn(null);
            vo.setClientId(null);
            vo.setScope(null);
            vo.setOpenid(null);

            // Assert
            assertThat(vo.getAccessToken()).isNull();
            assertThat(vo.getRefreshToken()).isNull();
            assertThat(vo.getExpireIn()).isNull();
            assertThat(vo.getRefreshExpireIn()).isNull();
            assertThat(vo.getClientId()).isNull();
            assertThat(vo.getScope()).isNull();
            assertThat(vo.getOpenid()).isNull();
        }
    }

    @Nested
    @DisplayName("5. VO 集成场景测试")
    class VoIntegrationTests {

        @Test
        @DisplayName("LoginTenantVo 和 TenantListVo 应该能够协同工作")
        void loginTenantVoAndTenantListVoShouldWorkTogether() {
            // Arrange
            TenantListVo tenant1 = new TenantListVo();
            tenant1.setTenantId("000000");
            tenant1.setCompanyName("公司A");
            tenant1.setDomain("a.example.com");

            TenantListVo tenant2 = new TenantListVo();
            tenant2.setTenantId("000001");
            tenant2.setCompanyName("公司B");
            tenant2.setDomain("b.example.com");

            LoginTenantVo loginTenantVo = new LoginTenantVo();
            loginTenantVo.setTenantEnabled(true);
            loginTenantVo.setVoList(Arrays.asList(tenant1, tenant2));

            // Act & Assert
            assertThat(loginTenantVo.getTenantEnabled()).isTrue();
            assertThat(loginTenantVo.getVoList()).hasSize(2);
            assertThat(loginTenantVo.getVoList().get(0).getTenantId()).isEqualTo("000000");
            assertThat(loginTenantVo.getVoList().get(1).getTenantId()).isEqualTo("000001");
        }

        @Test
        @DisplayName("LoginVo 和 CaptchaVo 应该能够在登录场景中协同工作")
        void loginVoAndCaptchaVoShouldWorkTogetherInLoginScenario() {
            // Arrange - 验证码响应
            CaptchaVo captchaVo = new CaptchaVo();
            captchaVo.setCaptchaEnabled(true);
            captchaVo.setUuid("550e8400-e29b-41d4-a716-446655440000");
            captchaVo.setImg("data:image/png;base64,iVBORw0KGgoAAAANS...");

            // Arrange - 登录响应
            LoginVo loginVo = new LoginVo();
            loginVo.setAccessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
            loginVo.setRefreshToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...refresh");
            loginVo.setExpireIn(7200L);
            loginVo.setRefreshExpireIn(2592000L);
            loginVo.setClientId("e5cd7e4891bf95d1d19206ce24a7b32e");

            // Act & Assert - 验证两个 VO 都有效
            assertThat(captchaVo.getCaptchaEnabled()).isTrue();
            assertThat(captchaVo.getUuid()).isNotNull();
            assertThat(loginVo.getAccessToken()).isNotNull();
            assertThat(loginVo.getExpireIn()).isEqualTo(7200L);
        }

        @Test
        @DisplayName("完整登录流程中所有 VO 应该能够协同工作")
        void allVosShouldWorkTogetherInCompleteLoginFlow() {
            // Arrange - 1. 租户列表
            TenantListVo tenant = new TenantListVo();
            tenant.setTenantId("000000");
            tenant.setCompanyName("测试公司");
            tenant.setDomain("test.example.com");

            LoginTenantVo loginTenantVo = new LoginTenantVo();
            loginTenantVo.setTenantEnabled(true);
            loginTenantVo.setVoList(Arrays.asList(tenant));

            // Arrange - 2. 验证码
            CaptchaVo captchaVo = new CaptchaVo();
            captchaVo.setCaptchaEnabled(true);
            captchaVo.setUuid("550e8400-e29b-41d4-a716-446655440000");
            captchaVo.setImg("data:image/png;base64,iVBORw0KGgoAAAANS...");

            // Arrange - 3. 登录结果
            LoginVo loginVo = new LoginVo();
            loginVo.setAccessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
            loginVo.setRefreshToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...refresh");
            loginVo.setExpireIn(7200L);
            loginVo.setClientId("e5cd7e4891bf95d1d19206ce24a7b32e");

            // Act & Assert - 验证完整流程
            // 步骤1: 获取租户列表
            assertThat(loginTenantVo.getTenantEnabled()).isTrue();
            assertThat(loginTenantVo.getVoList()).hasSize(1);

            // 步骤2: 获取验证码
            assertThat(captchaVo.getCaptchaEnabled()).isTrue();
            assertThat(captchaVo.getUuid()).isNotNull();

            // 步骤3: 登录成功
            assertThat(loginVo.getAccessToken()).isNotNull();
            assertThat(loginVo.getExpireIn()).isPositive();
        }
    }
}
