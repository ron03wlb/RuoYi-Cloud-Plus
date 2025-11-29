package org.dromara.auth;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import org.dromara.auth.form.*;
import org.dromara.common.core.enums.UserType;
import org.dromara.system.api.domain.vo.RemoteClientVo;
import org.dromara.system.api.domain.vo.RemoteTenantVo;
import org.dromara.system.api.model.LoginUser;

/**
 * 认证模块测试数据工厂
 *
 * <p>提供认证相关的测试数据生成方法
 *
 * <p>主要功能:
 *
 * <ul>
 *   <li>生成各种登录表单数据
 *   <li>生成客户端配置数据
 *   <li>生成租户数据
 *   <li>生成用户数据
 * </ul>
 *
 * @author Test Team
 */
public class AuthTestDataFactory {

  private static final ThreadLocalRandom random = ThreadLocalRandom.current();
  private static final String DEFAULT_TENANT_ID = "000000";
  private static final String DEFAULT_PASSWORD = "admin123";
  private static final String DEFAULT_PASSWORD_HASH = BCrypt.hashpw(DEFAULT_PASSWORD);

  private AuthTestDataFactory() {
    throw new UnsupportedOperationException("Utility class");
  }

  // ==================== 登录表单生成 ====================

  /** 创建密码登录表单 */
  public static PasswordLoginBody createPasswordLoginBody() {
    PasswordLoginBody body = new PasswordLoginBody();
    body.setClientId("e5cd7e4891bf95d1d19206ce24a7b32e");
    body.setGrantType("password");
    body.setTenantId(DEFAULT_TENANT_ID);
    body.setUsername("admin");
    body.setPassword(DEFAULT_PASSWORD);
    body.setCode("1234");
    body.setUuid(IdUtil.fastSimpleUUID());
    return body;
  }

  /** 创建自定义密码登录表单 */
  public static PasswordLoginBody createPasswordLoginBody(String username, String password) {
    PasswordLoginBody body = new PasswordLoginBody();
    body.setClientId("e5cd7e4891bf95d1d19206ce24a7b32e");
    body.setGrantType("password");
    body.setTenantId(DEFAULT_TENANT_ID);
    body.setUsername(username);
    body.setPassword(password);
    body.setCode("1234");
    body.setUuid(IdUtil.fastSimpleUUID());
    return body;
  }

  /** 创建邮箱登录表单 */
  public static EmailLoginBody createEmailLoginBody() {
    EmailLoginBody body = new EmailLoginBody();
    body.setClientId("e5cd7e4891bf95d1d19206ce24a7b32e");
    body.setGrantType("email");
    body.setTenantId(DEFAULT_TENANT_ID);
    body.setEmail("admin@example.com");
    body.setEmailCode("123456");
    return body;
  }

  /** 创建短信登录表单 */
  public static SmsLoginBody createSmsLoginBody() {
    SmsLoginBody body = new SmsLoginBody();
    body.setClientId("e5cd7e4891bf95d1d19206ce24a7b32e");
    body.setGrantType("sms");
    body.setTenantId(DEFAULT_TENANT_ID);
    body.setPhonenumber("13800138000");
    body.setSmsCode("123456");
    return body;
  }

  /** 创建社交登录表单 */
  public static SocialLoginBody createSocialLoginBody() {
    SocialLoginBody body = new SocialLoginBody();
    body.setClientId("e5cd7e4891bf95d1d19206ce24a7b32e");
    body.setGrantType("social");
    body.setTenantId(DEFAULT_TENANT_ID);
    body.setSource("github");
    body.setSocialCode("mock_social_code");
    body.setSocialState("mock_state");
    return body;
  }

  /** 创建小程序登录表单 */
  public static XcxLoginBody createXcxLoginBody() {
    XcxLoginBody body = new XcxLoginBody();
    body.setClientId("e5cd7e4891bf95d1d19206ce24a7b32e");
    body.setGrantType("xcx");
    body.setTenantId(DEFAULT_TENANT_ID);
    body.setXcxCode("mock_xcx_code");
    return body;
  }

  /** 创建注册表单 */
  public static RegisterBody createRegisterBody() {
    RegisterBody body = new RegisterBody();
    body.setClientId("e5cd7e4891bf95d1d19206ce24a7b32e");
    body.setGrantType("password");
    body.setTenantId(DEFAULT_TENANT_ID);
    body.setUsername("testuser" + random.nextInt(10000));
    body.setPassword(DEFAULT_PASSWORD);
    body.setUserType(UserType.SYS_USER.getUserType());
    body.setCode("1234");
    body.setUuid(IdUtil.fastSimpleUUID());
    return body;
  }

  // ==================== 客户端配置生成 ====================

  /** 创建默认客户端配置 */
  public static RemoteClientVo createClientVo() {
    RemoteClientVo client = new RemoteClientVo();
    client.setId(1L);
    client.setClientId("e5cd7e4891bf95d1d19206ce24a7b32e");
    client.setClientKey("web");
    client.setClientSecret("test_secret");
    client.setGrantType("password,social,sms,email,xcx");
    client.setDeviceType("pc");
    client.setStatus("0"); // 正常
    client.setTimeout(7200L);
    client.setActiveTimeout(1800L);
    return client;
  }

  /** 创建自定义客户端配置 */
  public static RemoteClientVo createClientVo(String clientKey, String grantType) {
    RemoteClientVo client = createClientVo();
    client.setClientKey(clientKey);
    client.setGrantType(grantType);
    return client;
  }

  /** 创建停用的客户端配置 */
  public static RemoteClientVo createDisabledClientVo() {
    RemoteClientVo client = createClientVo();
    client.setStatus("1"); // 停用
    return client;
  }

  // ==================== 租户数据生成 ====================

  /** 创建默认租户 */
  public static RemoteTenantVo createTenantVo() {
    RemoteTenantVo tenant = new RemoteTenantVo();
    tenant.setTenantId(DEFAULT_TENANT_ID);
    tenant.setCompanyName("测试公司");
    tenant.setStatus("0"); // 正常
    tenant.setExpireTime(
        new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000)); // 一年后过期
    return tenant;
  }

  /** 创建停用的租户 */
  public static RemoteTenantVo createDisabledTenantVo() {
    RemoteTenantVo tenant = createTenantVo();
    tenant.setStatus("1"); // 停用
    return tenant;
  }

  /** 创建已过期的租户 */
  public static RemoteTenantVo createExpiredTenantVo() {
    RemoteTenantVo tenant = createTenantVo();
    tenant.setExpireTime(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)); // 昨天过期
    return tenant;
  }

  // ==================== 用户数据生成 ====================

  /** 创建登录用户 */
  public static LoginUser createLoginUser() {
    LoginUser user = new LoginUser();
    user.setUserId(1L);
    user.setUsername("admin");
    user.setPassword(DEFAULT_PASSWORD_HASH);
    user.setNickname("管理员");
    user.setUserType(UserType.SYS_USER.getUserType());
    user.setTenantId(DEFAULT_TENANT_ID);
    user.setDeptId(100L);
    user.setDeptName("测试部门");
    return user;
  }

  /** 创建自定义登录用户 */
  public static LoginUser createLoginUser(String username, String tenantId) {
    LoginUser user = createLoginUser();
    user.setUsername(username);
    user.setTenantId(tenantId);
    return user;
  }

  /** 创建超级管理员用户 */
  public static LoginUser createSuperAdminUser() {
    LoginUser user = createLoginUser();
    user.setUserId(1L);
    user.setUsername("admin");
    return user;
  }

  // ==================== 工具方法 ====================

  /** 获取默认租户ID */
  public static String getDefaultTenantId() {
    return DEFAULT_TENANT_ID;
  }

  /** 获取默认密码 */
  public static String getDefaultPassword() {
    return DEFAULT_PASSWORD;
  }

  /** 获取默认密码哈希 */
  public static String getDefaultPasswordHash() {
    return DEFAULT_PASSWORD_HASH;
  }

  /** 生成随机验证码 */
  public static String randomCaptchaCode() {
    return String.format("%04d", random.nextInt(10000));
  }

  /** 生成随机UUID */
  public static String randomUuid() {
    return IdUtil.fastSimpleUUID();
  }
}
