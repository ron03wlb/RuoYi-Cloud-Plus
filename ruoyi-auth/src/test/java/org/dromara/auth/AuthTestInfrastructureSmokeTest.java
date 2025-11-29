package org.dromara.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 测试基础设施验证测试
 *
 * <p>验证测试框架和基础设施是否正常工作
 *
 * @author Test Team
 */
@DisplayName("测试基础设施 Smoke Test")
class AuthTestInfrastructureSmokeTest extends BaseUnitTest {

  @Test
  @DisplayName("验证 JUnit 5 正常工作")
  void testJUnit5Works() {
    assertTrue(true);
    assertEquals(1, 1);
  }

  @Test
  @DisplayName("验证 AssertJ 正常工作")
  void testAssertJWorks() {
    assertThat("test").isNotNull().isNotEmpty().hasSize(4).startsWith("te").endsWith("st");
  }

  @Test
  @DisplayName("验证测试数据工厂正常工作")
  void testAuthTestDataFactoryWorks() {
    // 测试登录表单生成
    var passwordBody = AuthTestDataFactory.createPasswordLoginBody();
    assertThat(passwordBody).isNotNull();
    assertThat(passwordBody.getUsername()).isEqualTo("admin");
    assertThat(passwordBody.getPassword()).isEqualTo(AuthTestDataFactory.getDefaultPassword());

    // 测试客户端配置生成
    var client = AuthTestDataFactory.createClientVo();
    assertThat(client).isNotNull();
    assertThat(client.getClientKey()).isEqualTo("web");

    // 测试租户生成
    var tenant = AuthTestDataFactory.createTenantVo();
    assertThat(tenant).isNotNull();
    assertThat(tenant.getTenantId()).isEqualTo(AuthTestDataFactory.getDefaultTenantId());

    // 测试用户生成
    var user = AuthTestDataFactory.createLoginUser();
    assertThat(user).isNotNull();
    assertThat(user.getUsername()).isEqualTo("admin");
  }

  @Test
  @DisplayName("验证可以创建各种登录表单")
  void testCanCreateAllLoginForms() {
    assertThat(AuthTestDataFactory.createPasswordLoginBody()).isNotNull();
    assertThat(AuthTestDataFactory.createEmailLoginBody()).isNotNull();
    assertThat(AuthTestDataFactory.createSmsLoginBody()).isNotNull();
    assertThat(AuthTestDataFactory.createSocialLoginBody()).isNotNull();
    assertThat(AuthTestDataFactory.createXcxLoginBody()).isNotNull();
    assertThat(AuthTestDataFactory.createRegisterBody()).isNotNull();
  }

  @Test
  @DisplayName("验证可以创建各种状态的客户端")
  void testCanCreateVariousClientStates() {
    var normalClient = AuthTestDataFactory.createClientVo();
    assertThat(normalClient.getStatus()).isEqualTo("0");

    var disabledClient = AuthTestDataFactory.createDisabledClientVo();
    assertThat(disabledClient.getStatus()).isEqualTo("1");
  }

  @Test
  @DisplayName("验证可以创建各种状态的租户")
  void testCanCreateVariousTenantStates() {
    var normalTenant = AuthTestDataFactory.createTenantVo();
    assertThat(normalTenant.getStatus()).isEqualTo("0");
    assertThat(normalTenant.getExpireTime()).isInTheFuture();

    var disabledTenant = AuthTestDataFactory.createDisabledTenantVo();
    assertThat(disabledTenant.getStatus()).isEqualTo("1");

    var expiredTenant = AuthTestDataFactory.createExpiredTenantVo();
    assertThat(expiredTenant.getExpireTime()).isInThePast();
  }
}
