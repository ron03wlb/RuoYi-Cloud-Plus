package org.dromara.common.test.utils;

import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.stp.StpUtil;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 认证测试工具类.
 *
 * <p>提供集成测试中的认证相关工具方法，包括：
 *
 * <ul>
 *   <li>模拟用户登录
 *   <li>生成测试 Token
 *   <li>设置用户权限
 *   <li>清除登录状态
 * </ul>
 *
 * <h3>使用示例：</h3>
 *
 * <pre>{@code
 * // 模拟用户登录
 * String token = AuthTestUtils.mockLogin(1L, "admin");
 *
 * // 设置用户权限
 * AuthTestUtils.setPermissions(1L, "system:user:list", "system:user:add");
 *
 * // 设置用户角色
 * AuthTestUtils.setRoles(1L, "admin", "user");
 *
 * // 清除登录状态
 * AuthTestUtils.logout(1L);
 * }</pre>
 *
 * <h3>重要说明：</h3>
 *
 * <p>本类使用 Sa-Token 的 Mock 上下文进行测试。所有需要使用 Sa-Token 功能的测试方法 会自动初始化 Mock 上下文，无需手动初始化.
 *
 * @author Lion Li
 * @since 2025-11-09
 */
public class AuthTestUtils {

  private static final Logger log = LoggerFactory.getLogger(AuthTestUtils.class);

  /** 线程本地变量，标记当前线程是否已初始化 Mock 上下文. */
  private static final ThreadLocal<Boolean> CONTEXT_INITIALIZED =
      ThreadLocal.withInitial(() -> false);

  /**
   * 确保 Sa-Token Mock 上下文已初始化.
   *
   * <p>使用 ThreadLocal 确保每个线程只初始化一次
   */
  private static void ensureMockContext() {
    if (!CONTEXT_INITIALIZED.get()) {
      try {
        SaTokenContextMockUtil.setMockContext();
        CONTEXT_INITIALIZED.set(true);
        log.debug("Sa-Token Mock 上下文已初始化");
      } catch (Exception e) {
        log.warn("初始化 Sa-Token Mock 上下文失败: {}", e.getMessage());
      }
    }
  }

  /**
   * 清除 Mock 上下文.
   *
   * <p>测试完成后应调用此方法清理上下文
   */
  public static void clearMockContext() {
    try {
      SaTokenContextMockUtil.clearContext();
      CONTEXT_INITIALIZED.remove();
      log.debug("Sa-Token Mock 上下文已清除");
    } catch (Exception e) {
      log.warn("清除 Sa-Token Mock 上下文失败: {}", e.getMessage());
    }
  }

  /**
   * 模拟用户登录.
   *
   * @param userId 用户ID
   * @param username 用户名
   * @return Token字符串
   */
  public static String mockLogin(Long userId, String username) {
    return mockLogin(userId, username, null);
  }

  /**
   * 模拟用户登录（带租户）.
   *
   * @param userId 用户ID
   * @param username 用户名
   * @param tenantId 租户ID
   * @return Token字符串
   */
  public static String mockLogin(Long userId, String username, String tenantId) {
    // 确保 Mock 上下文已初始化
    ensureMockContext();

    log.debug("模拟用户登录: userId={}, username={}, tenantId={}", userId, username, tenantId);

    // 执行登录
    StpUtil.login(userId);

    // 获取 Token
    String token = StpUtil.getTokenValue();

    // 设置用户Session信息
    Map<String, Object> sessionData = new HashMap<>();
    sessionData.put("userId", userId);
    sessionData.put("username", username);
    if (tenantId != null) {
      sessionData.put("tenantId", tenantId);
    }

    StpUtil.getSession().setDataMap(sessionData);

    log.debug("登录成功: token={}", token);
    return token;
  }

  /**
   * 模拟超级管理员登录.
   *
   * @return Token字符串
   */
  public static String mockAdminLogin() {
    return mockLogin(1L, "admin");
  }

  /**
   * 设置用户权限.
   *
   * @param userId 用户ID
   * @param permissions 权限列表
   */
  public static void setPermissions(Long userId, String... permissions) {
    ensureMockContext();
    log.debug("设置用户权限: userId={}, permissions={}", userId, permissions);

    for (String permission : permissions) {
      StpUtil.getPermissionList(userId).add(permission);
    }
  }

  /**
   * 设置用户角色.
   *
   * @param userId 用户ID
   * @param roles 角色列表
   */
  public static void setRoles(Long userId, String... roles) {
    ensureMockContext();
    log.debug("设置用户角色: userId={}, roles={}", userId, roles);

    for (String role : roles) {
      StpUtil.getRoleList(userId).add(role);
    }
  }

  /**
   * 检查用户是否已登录.
   *
   * @param userId 用户ID
   * @return true-已登录，false-未登录
   */
  public static boolean isLogin(Long userId) {
    ensureMockContext();
    return StpUtil.isLogin(userId);
  }

  /**
   * 获取当前登录用户ID.
   *
   * @return 用户ID
   */
  public static Long getLoginUserId() {
    ensureMockContext();
    return StpUtil.getLoginIdAsLong();
  }

  /**
   * 获取当前登录用户名.
   *
   * @return 用户名
   */
  public static String getLoginUsername() {
    ensureMockContext();
    return (String) StpUtil.getSession().get("username");
  }

  /**
   * 获取当前租户ID.
   *
   * @return 租户ID
   */
  public static String getTenantId() {
    ensureMockContext();
    return (String) StpUtil.getSession().get("tenantId");
  }

  /**
   * 登出指定用户.
   *
   * @param userId 用户ID
   */
  public static void logout(Long userId) {
    ensureMockContext();
    log.debug("登出用户: userId={}", userId);
    StpUtil.logout(userId);
  }

  /** 登出当前用户. */
  public static void logout() {
    ensureMockContext();
    log.debug("登出当前用户");
    StpUtil.logout();
  }

  /** 清除所有登录状态. */
  public static void clearAll() {
    log.debug("清除所有登录状态");
    // 注意：Sa-Token 没有直接的清除所有Session的方法
    // 需要手动清除或重启Redis
  }

  /**
   * 获取 Token.
   *
   * @param userId 用户ID
   * @return Token字符串
   */
  public static String getToken(Long userId) {
    ensureMockContext();
    return StpUtil.getTokenValueByLoginId(userId);
  }

  /**
   * 获取当前 Token.
   *
   * @return Token字符串
   */
  public static String getToken() {
    ensureMockContext();
    return StpUtil.getTokenValue();
  }

  /**
   * 模拟带权限的管理员登录.
   *
   * @param permissions 权限列表
   * @return Token字符串
   */
  public static String mockAdminLoginWithPermissions(String... permissions) {
    String token = mockAdminLogin();
    setPermissions(1L, permissions);
    return token;
  }

  /**
   * 模拟带角色的管理员登录.
   *
   * @param roles 角色列表
   * @return Token字符串
   */
  public static String mockAdminLoginWithRoles(String... roles) {
    String token = mockAdminLogin();
    setRoles(1L, roles);
    return token;
  }

  /**
   * 创建测试用的普通用户并登录.
   *
   * @param userId 用户ID
   * @param username 用户名
   * @return Token字符串
   */
  public static String mockNormalUserLogin(Long userId, String username) {
    String token = mockLogin(userId, username);
    // 设置基本权限
    setPermissions(userId, "common:read");
    setRoles(userId, "user");
    return token;
  }
}
