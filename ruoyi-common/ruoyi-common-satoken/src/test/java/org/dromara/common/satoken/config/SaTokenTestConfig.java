package org.dromara.common.satoken.config;

import org.dromara.common.core.service.PermissionService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.HashSet;
import java.util.Set;

/**
 * Sa-Token 測試配置類
 * <p>
 * 提供所有測試需要的 Mock Bean
 * </p>
 *
 * <p>功能:</p>
 * <ul>
 *   <li>Mock PermissionService 接口</li>
 *   <li>提供測試用的權限數據</li>
 *   <li>使用 @Primary 覆蓋默認 Bean</li>
 * </ul>
 *
 * @author Test Team
 */
@TestConfiguration
public class SaTokenTestConfig {

    /**
     * Mock PermissionService
     * <p>
     * SaPermissionImpl 依賴此接口來獲取權限數據
     * </p>
     */
    @Bean
    @Primary
    public PermissionService mockPermissionService() {
        PermissionService mock = Mockito.mock(PermissionService.class);

        // 默認返回一些測試權限
        Set<String> menuPermissions = new HashSet<>();
        menuPermissions.add("system:user:list");
        menuPermissions.add("system:user:add");
        menuPermissions.add("system:role:list");

        Set<String> rolePermissions = new HashSet<>();
        rolePermissions.add("admin");
        rolePermissions.add("common");

        // 配置 mock 行為
        Mockito.when(mock.getMenuPermission(Mockito.anyLong())).thenReturn(menuPermissions);
        Mockito.when(mock.getRolePermission(Mockito.anyLong())).thenReturn(rolePermissions);

        return mock;
    }
}
