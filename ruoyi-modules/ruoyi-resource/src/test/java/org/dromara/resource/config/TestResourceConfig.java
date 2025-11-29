package org.dromara.resource.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.dromara.common.core.service.PermissionService;
import org.dromara.common.tenant.properties.TenantProperties;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 集成测试专用配置类
 *
 * <p>提供测试所需的 Mock Bean，避免 Bean 定义冲突
 *
 * @author Lion Li
 * @since 2025-11-10
 */
@TestConfiguration
public class TestResourceConfig {

  /**
   * Mock PermissionService
   *
   * <p>为 Sa-Token 权限验证提供 Mock 实现
   */
  @Bean
  @Primary
  public PermissionService mockPermissionService() {
    PermissionService mock = Mockito.mock(PermissionService.class);

    // 默认返回测试权限
    Set<String> menuPermissions = new HashSet<>();
    menuPermissions.add("system:oss:upload");
    menuPermissions.add("system:oss:download");
    menuPermissions.add("system:oss:list");
    menuPermissions.add("system:oss:remove");

    Set<String> rolePermissions = new HashSet<>();
    rolePermissions.add("admin");

    // 配置 mock 行为
    Mockito.when(mock.getMenuPermission(Mockito.anyLong())).thenReturn(menuPermissions);
    Mockito.when(mock.getRolePermission(Mockito.anyLong())).thenReturn(rolePermissions);

    return mock;
  }

  /**
   * 提供租户配置属性
   *
   * <p>为多租户插件提供必要的配置信息 注意：在测试环境中禁用租户功能，避免加载 TenantConfiguration
   */
  @Bean
  @Primary
  public TenantProperties tenantProperties() {
    TenantProperties properties = new TenantProperties();
    properties.setEnable(false); // 测试环境禁用租户功能
    properties.setExcludes(new ArrayList<>());
    return properties;
  }

  // 注意：SaTokenDao 已移至测试类使用 @MockBean 替代，避免 Bean 冲突
}
