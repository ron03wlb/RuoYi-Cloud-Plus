package org.dromara.common.test.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 租户测试工具类.
 *
 * <p>提供集成测试中的租户相关工具方法，包括：
 *
 * <ul>
 *   <li>设置当前租户
 *   <li>切换租户
 *   <li>清除租户上下文
 *   <li>验证租户隔离
 * </ul>
 *
 * <h3>使用示例：</h3>
 *
 * <pre>{@code
 * // 设置租户1
 * TenantTestUtils.setTenant("000000");
 *
 * // 执行租户1的操作
 * service.saveData(data);
 *
 * // 切换到租户2
 * TenantTestUtils.setTenant("000001");
 *
 * // 执行租户2的操作
 * service.saveData(data2);
 *
 * // 验证数据隔离
 * List<Data> tenant1Data = TenantTestUtils.executeInTenant("000000", () -> service.listAll());
 * List<Data> tenant2Data = TenantTestUtils.executeInTenant("000001", () -> service.listAll());
 *
 * // 清除租户上下文
 * TenantTestUtils.clear();
 * }</pre>
 *
 * @author Lion Li
 * @since 2025-11-09
 */
public class TenantTestUtils {

  private static final Logger log = LoggerFactory.getLogger(TenantTestUtils.class);

  /** 租户上下文线程变量. */
  private static final ThreadLocal<String> TENANT_CONTEXT = new ThreadLocal<>();

  /** 默认主租户ID. */
  public static final String DEFAULT_TENANT_ID = "000000";

  /** 测试租户1 ID. */
  public static final String TEST_TENANT_1_ID = "000001";

  /** 测试租户2 ID. */
  public static final String TEST_TENANT_2_ID = "000002";

  /**
   * 设置当前租户.
   *
   * @param tenantId 租户ID
   */
  public static void setTenant(String tenantId) {
    log.debug("设置当前租户: tenantId={}", tenantId);
    TENANT_CONTEXT.set(tenantId);

    // 如果用户已登录，同时更新Session中的租户信息
    try {
      if (AuthTestUtils.getLoginUserId() != null) {
        cn.dev33.satoken.stp.StpUtil.getSession().set("tenantId", tenantId);
      }
    } catch (Exception e) {
      log.debug("更新Session租户信息失败（可能用户未登录）: {}", e.getMessage());
    }
  }

  /** 设置为主租户. */
  public static void setDefaultTenant() {
    setTenant(DEFAULT_TENANT_ID);
  }

  /** 设置为测试租户1. */
  public static void setTestTenant1() {
    setTenant(TEST_TENANT_1_ID);
  }

  /** 设置为测试租户2. */
  public static void setTestTenant2() {
    setTenant(TEST_TENANT_2_ID);
  }

  /**
   * 获取当前租户ID.
   *
   * @return 租户ID
   */
  public static String getTenant() {
    return TENANT_CONTEXT.get();
  }

  /** 清除租户上下文. */
  public static void clear() {
    log.debug("清除租户上下文");
    TENANT_CONTEXT.remove();
  }

  /**
   * 在指定租户上下文中执行操作.
   *
   * @param tenantId 租户ID
   * @param operation 要执行的操作
   * @param <T> 返回值类型
   * @return 操作结果
   */
  public static <T> T executeInTenant(String tenantId, TenantOperation<T> operation) {
    // 保存当前租户
    String originalTenant = getTenant();

    try {
      // 切换到指定租户
      setTenant(tenantId);
      log.debug("在租户 {} 中执行操作", tenantId);

      // 执行操作
      return operation.execute();
    } finally {
      // 恢复原租户
      if (originalTenant != null) {
        setTenant(originalTenant);
      } else {
        clear();
      }
    }
  }

  /**
   * 在指定租户上下文中执行操作（无返回值）.
   *
   * @param tenantId 租户ID
   * @param operation 要执行的操作
   */
  public static void executeInTenant(String tenantId, TenantOperationVoid operation) {
    executeInTenant(
        tenantId,
        () -> {
          operation.execute();
          return null;
        });
  }

  /**
   * 验证租户隔离.
   *
   * <p>在两个不同租户下执行相同操作，验证结果是否隔离
   *
   * @param tenant1Id 租户1 ID
   * @param tenant2Id 租户2 ID
   * @param operation1 租户1的操作
   * @param operation2 租户2的操作（通常与operation1相同）
   * @param validator 验证器，用于验证两个租户的结果是否隔离
   * @param <T> 操作返回值类型
   */
  public static <T> void verifyTenantIsolation(
      String tenant1Id,
      String tenant2Id,
      TenantOperation<T> operation1,
      TenantOperation<T> operation2,
      TenantIsolationValidator<T> validator) {

    log.debug("验证租户隔离: tenant1={}, tenant2={}", tenant1Id, tenant2Id);

    // 在租户1中执行操作
    T result1 = executeInTenant(tenant1Id, operation1);

    // 在租户2中执行操作
    T result2 = executeInTenant(tenant2Id, operation2);

    // 验证隔离性
    validator.validate(result1, result2);

    log.debug("租户隔离验证通过");
  }

  /**
   * 模拟多租户用户登录.
   *
   * @param userId 用户ID
   * @param username 用户名
   * @param tenantId 租户ID
   * @return Token字符串
   */
  public static String mockTenantUserLogin(Long userId, String username, String tenantId) {
    setTenant(tenantId);
    return AuthTestUtils.mockLogin(userId, username, tenantId);
  }

  /**
   * 租户操作接口（有返回值）.
   *
   * @param <T> 返回值类型
   */
  @FunctionalInterface
  public interface TenantOperation<T> {
    T execute();
  }

  /** 租户操作接口（无返回值）. */
  @FunctionalInterface
  public interface TenantOperationVoid {
    void execute();
  }

  /**
   * 租户隔离验证器.
   *
   * @param <T> 结果类型
   */
  @FunctionalInterface
  public interface TenantIsolationValidator<T> {
    /**
     * 验证两个租户的结果是否隔离.
     *
     * @param result1 租户1的结果
     * @param result2 租户2的结果
     */
    void validate(T result1, T result2);
  }
}
