package org.dromara.gen.config;

import cn.dev33.satoken.dao.SaTokenDao;
import java.util.HashSet;
import java.util.Set;
import org.dromara.common.core.service.PermissionService;
import org.dromara.common.satoken.core.dao.PlusSaTokenDao;
import org.dromara.system.api.RemoteClientService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 测试配置类
 *
 * <p>提供所有测试需要的 Mock Bean
 *
 * @author Lion Li
 * @since 2025-11-09
 */
@TestConfiguration
public class TestSaTokenConfig {

    /** Sa-Token DAO (使用 @Primary 确保优先使用) */
    @Bean
    @Primary
    public SaTokenDao saTokenDao() {
        return new PlusSaTokenDao();
    }

    /**
     * Mock PermissionService
     *
     * <p>Sa-Token权限验证需要此服务
     */
    @Bean
    @Primary
    public PermissionService mockPermissionService() {
        PermissionService mock = Mockito.mock(PermissionService.class);

        // 默认返回测试权限
        Set<String> menuPermissions = new HashSet<>();
        menuPermissions.add("tool:gen:list");
        menuPermissions.add("tool:gen:query");
        menuPermissions.add("tool:gen:add");

        Set<String> rolePermissions = new HashSet<>();
        rolePermissions.add("admin");

        // 配置 mock 行为
        Mockito.when(mock.getMenuPermission(Mockito.anyLong())).thenReturn(menuPermissions);
        Mockito.when(mock.getRolePermission(Mockito.anyLong())).thenReturn(rolePermissions);

        return mock;
    }

    /**
     * Mock RemoteClientService
     *
     * <p>Dubbo 远程服务，避免 Dubbo 初始化错误
     */
    @Bean
    @Primary
    public RemoteClientService mockRemoteClientService() {
        return Mockito.mock(RemoteClientService.class);
    }
}
