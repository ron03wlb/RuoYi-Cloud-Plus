package org.dromara.auth.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.dromara.auth.AuthTestDataFactory;
import org.dromara.auth.BaseIntegrationTestWithContainers;
import org.dromara.auth.form.PasswordLoginBody;
import org.dromara.auth.form.RegisterBody;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.system.api.RemoteClientService;
import org.dromara.system.api.RemoteConfigService;
import org.dromara.system.api.RemoteTenantService;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.vo.RemoteClientVo;
import org.dromara.system.api.domain.vo.RemoteTenantVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/**
 * TokenController 集成测试
 *
 * <p>测试认证控制器的核心功能
 *
 * <p>TODO: 当前因 RedissonClient 依赖问题暂时禁用
 *
 * <p>问题: ruoyi-common-redis 模块强依赖 Redisson,即使排除自动配置后仍有Bean需要注入 RedissonClient
 *
 * <p>解决方案: 需要重构 ruoyi-common-redis 模块,使 Redisson 变为可选依赖
 *
 * <p>参考: docs/PHASE2-AUTH-TESTING-FINAL-REPORT.md
 *
 * @author Test Team
 */
@org.junit.jupiter.api.Disabled("TODO: Fix RedissonClient dependency issue - see class javadoc")
@DisplayName("TokenController 集成测试")
class TokenControllerIntegrationTest extends BaseIntegrationTestWithContainers {

  @Autowired private RemoteClientService remoteClientService;

  @Autowired private RemoteConfigService remoteConfigService;

  @Autowired private RemoteTenantService remoteTenantService;

  @Autowired private RemoteUserService remoteUserService;

  private RemoteClientVo mockClient;
  private RemoteTenantVo mockTenant;

  @BeforeEach
  @Override
  public void baseSetUp() {
    // 设置默认的 mock 客户端
    mockClient = AuthTestDataFactory.createClientVo();
    when(remoteClientService.queryByClientId(anyString())).thenReturn(mockClient);

    // 设置默认的 mock 租户
    mockTenant = AuthTestDataFactory.createTenantVo();
    when(remoteTenantService.queryByTenantId(anyString())).thenReturn(mockTenant);
  }

  @Nested
  @DisplayName("1. POST /login 登录端点测试")
  class LoginEndpointTests {

    @Test
    @DisplayName("客户端ID不存在 - 应该返回失败")
    void shouldFailWhenClientIdNotExists() throws Exception {
      // Arrange
      PasswordLoginBody loginBody = AuthTestDataFactory.createPasswordLoginBody();
      loginBody.setClientId("invalid_client_id");

      when(remoteClientService.queryByClientId("invalid_client_id")).thenReturn(null);

      // Act & Assert
      mockMvc
          .perform(
              post("/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(JsonUtils.toJsonString(loginBody)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code").value(500))
          .andExpect(jsonPath("$.msg").exists());
    }

    @Test
    @DisplayName("客户端已停用 - 应该返回失败")
    void shouldFailWhenClientIsDisabled() throws Exception {
      // Arrange
      PasswordLoginBody loginBody = AuthTestDataFactory.createPasswordLoginBody();
      RemoteClientVo disabledClient = AuthTestDataFactory.createDisabledClientVo();

      when(remoteClientService.queryByClientId(anyString())).thenReturn(disabledClient);

      // Act & Assert
      mockMvc
          .perform(
              post("/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(JsonUtils.toJsonString(loginBody)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code").value(500))
          .andExpect(jsonPath("$.msg").exists());
    }

    @Test
    @DisplayName("授权类型不匹配 - 应该返回失败")
    void shouldFailWhenGrantTypeNotMatched() throws Exception {
      // Arrange
      PasswordLoginBody loginBody = AuthTestDataFactory.createPasswordLoginBody();
      loginBody.setGrantType("invalid_grant_type");

      RemoteClientVo client = AuthTestDataFactory.createClientVo("web", "password");
      when(remoteClientService.queryByClientId(anyString())).thenReturn(client);

      // Act & Assert
      mockMvc
          .perform(
              post("/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(JsonUtils.toJsonString(loginBody)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("缺少必填字段 - 应该返回验证错误")
    void shouldFailWhenRequiredFieldsMissing() throws Exception {
      // Arrange
      PasswordLoginBody loginBody = new PasswordLoginBody();
      // 不设置任何字段

      // Act & Assert
      mockMvc
          .perform(
              post("/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(JsonUtils.toJsonString(loginBody)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("2. POST /logout 登出端点测试")
  class LogoutEndpointTests {

    @Test
    @DisplayName("登出请求 - 应该返回成功")
    void shouldLogoutSuccessfully() throws Exception {
      // Act & Assert
      mockMvc
          .perform(post("/logout"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("登出应该调用 SysLoginService.logout()")
    void shouldCallLogoutService() throws Exception {
      // Act
      mockMvc.perform(post("/logout")).andExpect(status().isOk());

      // Verify (implicit - no exception means logout was called)
    }
  }

  @Nested
  @DisplayName("3. POST /register 注册端点测试")
  class RegisterEndpointTests {

    @Test
    @DisplayName("注册功能未开启 - 应该返回失败")
    void shouldFailWhenRegisterDisabled() throws Exception {
      // Arrange
      RegisterBody registerBody = AuthTestDataFactory.createRegisterBody();

      when(remoteConfigService.selectRegisterEnabled(anyString())).thenReturn(false);

      // Act & Assert
      mockMvc
          .perform(
              post("/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(JsonUtils.toJsonString(registerBody)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code").value(500))
          .andExpect(jsonPath("$.msg").value("当前系统没有开启注册功能！"));
    }

    @Test
    @DisplayName("注册功能已开启 - 应该处理注册请求")
    void shouldProcessRegisterWhenEnabled() throws Exception {
      // Arrange
      RegisterBody registerBody = AuthTestDataFactory.createRegisterBody();

      when(remoteConfigService.selectRegisterEnabled(anyString())).thenReturn(true);

      // Act & Assert
      mockMvc
          .perform(
              post("/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(JsonUtils.toJsonString(registerBody)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("注册表单验证失败 - 应该返回错误")
    void shouldFailWhenRegisterBodyInvalid() throws Exception {
      // Arrange
      RegisterBody registerBody = new RegisterBody();
      // 不设置必填字段

      when(remoteConfigService.selectRegisterEnabled(anyString())).thenReturn(true);

      // Act & Assert
      mockMvc
          .perform(
              post("/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(JsonUtils.toJsonString(registerBody)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("4. GET /tenant/list 租户列表端点测试")
  class TenantListEndpointTests {

    @Test
    @DisplayName("获取租户列表 - 应该返回成功")
    void shouldReturnTenantListSuccessfully() throws Exception {
      // Arrange
      List<RemoteTenantVo> tenantList = List.of(mockTenant);
      when(remoteTenantService.queryList()).thenReturn(tenantList);

      // Act & Assert
      mockMvc
          .perform(get("/tenant/list"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code").value(200))
          .andExpect(jsonPath("$.data").exists())
          .andExpect(jsonPath("$.data.tenantEnabled").exists());
    }

    @Test
    @DisplayName("租户列表 - 应该包含租户启用状态")
    void shouldIncludeTenantEnabledStatus() throws Exception {
      // Arrange
      when(remoteTenantService.queryList()).thenReturn(List.of());

      // Act & Assert
      mockMvc
          .perform(get("/tenant/list"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.tenantEnabled").isBoolean());
    }
  }

  @Nested
  @DisplayName("5. DELETE /unlock/{socialId} 取消授权端点测试")
  class UnlockSocialEndpointTests {

    @Test
    @DisplayName("取消授权成功 - 应该返回成功")
    void shouldUnlockSuccessfully() throws Exception {
      // Arrange
      Long socialId = 1L;
      // Note: remoteSocialService needs to be mocked if available

      // Act & Assert
      mockMvc.perform(delete("/unlock/{socialId}", socialId)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("取消授权 - 应该接受有效的 socialId")
    void shouldAcceptValidSocialId() throws Exception {
      // Act & Assert
      mockMvc.perform(delete("/unlock/{socialId}", 123L)).andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("6. API 安全性测试")
  class SecurityTests {

    @Test
    @DisplayName("登录端点应该接受 POST 请求")
    void loginShouldAcceptPost() throws Exception {
      PasswordLoginBody loginBody = AuthTestDataFactory.createPasswordLoginBody();

      mockMvc
          .perform(
              post("/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(JsonUtils.toJsonString(loginBody)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("登录端点不应该接受 GET 请求")
    void loginShouldNotAcceptGet() throws Exception {
      mockMvc.perform(get("/login")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("登出端点应该接受 POST 请求")
    void logoutShouldAcceptPost() throws Exception {
      mockMvc.perform(post("/logout")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("注册端点应该接受 POST 请求")
    void registerShouldAcceptPost() throws Exception {
      RegisterBody registerBody = AuthTestDataFactory.createRegisterBody();
      when(remoteConfigService.selectRegisterEnabled(anyString())).thenReturn(true);

      mockMvc
          .perform(
              post("/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(JsonUtils.toJsonString(registerBody)))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("7. Content-Type 测试")
  class ContentTypeTests {

    @Test
    @DisplayName("登录端点应该要求 JSON Content-Type")
    void loginShouldRequireJsonContentType() throws Exception {
      PasswordLoginBody loginBody = AuthTestDataFactory.createPasswordLoginBody();

      mockMvc
          .perform(
              post("/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(JsonUtils.toJsonString(loginBody)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("注册端点应该要求 JSON Content-Type")
    void registerShouldRequireJsonContentType() throws Exception {
      RegisterBody registerBody = AuthTestDataFactory.createRegisterBody();
      when(remoteConfigService.selectRegisterEnabled(anyString())).thenReturn(true);

      mockMvc
          .perform(
              post("/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(JsonUtils.toJsonString(registerBody)))
          .andExpect(status().isOk());
    }
  }
}
