package org.dromara.system.service.impl;

import org.dromara.system.service.ISysMenuService;
import org.dromara.system.service.ISysRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SysPermissionServiceImpl 单元测试
 * <p>
 * 测试用户权限处理服务
 * </p>
 *
 * <p><b>测试覆盖范围:</b></p>
 * <ul>
 *   <li>角色权限获取</li>
 *   <li>菜单权限获取</li>
 * </ul>
 *
 * <p><b>测试限制:</b></p>
 * <ul>
 *   <li>无法测试超级管理员路径 - LoginHelper.isSuperAdmin() 是静态方法,需要 mockito-inline 支持</li>
 *   <li>当前测试仅覆盖普通用户权限获取逻辑</li>
 * </ul>
 *
 * @author Test Team
 * @see SysPermissionServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysPermissionServiceImpl 单元测试")
class SysPermissionServiceImplTest {

    @Mock
    private ISysRoleService roleService;

    @Mock
    private ISysMenuService menuService;

    @InjectMocks
    private SysPermissionServiceImpl permissionService;

    // ==================== Nested Test Groups ====================

    @Nested
    @DisplayName("1. 角色权限获取测试")
    class GetRolePermissionTests {

        @Test
        @DisplayName("应该返回普通用户的角色权限列表")
        void shouldReturnRolePermissions_WhenNormalUser() {
            // Arrange - 准备测试数据
            Long userId = 2L; // 普通用户ID
            Set<String> expectedRoles = new HashSet<>(Arrays.asList("common", "operator", "viewer"));

            // Mock roleService 返回角色权限
            when(roleService.selectRolePermissionByUserId(userId)).thenReturn(expectedRoles);

            // Act - 执行测试
            Set<String> result = permissionService.getRolePermission(userId);

            // Assert - 验证结果
            assertThat(result)
                .as("应该返回普通用户的角色权限")
                .isNotNull()
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(expectedRoles);

            // Verify - 验证交互
            verify(roleService, times(1)).selectRolePermissionByUserId(userId);
            verifyNoMoreInteractions(roleService);
            verifyNoInteractions(menuService); // menuService 不应该被调用
        }

        @Test
        @DisplayName("应该返回空集合_当用户没有任何角色权限")
        void shouldReturnEmptySet_WhenUserHasNoRoles() {
            // Arrange
            Long userId = 3L;
            Set<String> emptyRoles = new HashSet<>();

            when(roleService.selectRolePermissionByUserId(userId)).thenReturn(emptyRoles);

            // Act
            Set<String> result = permissionService.getRolePermission(userId);

            // Assert
            assertThat(result)
                .as("应该返回空集合")
                .isNotNull()
                .isEmpty();

            verify(roleService, times(1)).selectRolePermissionByUserId(userId);
        }

        @Test
        @DisplayName("应该返回单个角色权限_当用户只有一个角色")
        void shouldReturnSingleRole_WhenUserHasOnlyOneRole() {
            // Arrange
            Long userId = 4L;
            Set<String> singleRole = new HashSet<>(Arrays.asList("viewer"));

            when(roleService.selectRolePermissionByUserId(userId)).thenReturn(singleRole);

            // Act
            Set<String> result = permissionService.getRolePermission(userId);

            // Assert
            assertThat(result)
                .as("应该返回单个角色权限")
                .isNotNull()
                .hasSize(1)
                .containsExactly("viewer");

            verify(roleService, times(1)).selectRolePermissionByUserId(userId);
        }

        @Test
        @DisplayName("应该返回多个角色权限_当用户有多个角色")
        void shouldReturnMultipleRoles_WhenUserHasMultipleRoles() {
            // Arrange
            Long userId = 5L;
            Set<String> multipleRoles = new HashSet<>(Arrays.asList(
                "admin", "manager", "operator", "viewer", "auditor"
            ));

            when(roleService.selectRolePermissionByUserId(userId)).thenReturn(multipleRoles);

            // Act
            Set<String> result = permissionService.getRolePermission(userId);

            // Assert
            assertThat(result)
                .as("应该返回多个角色权限")
                .isNotNull()
                .hasSize(5)
                .containsExactlyInAnyOrderElementsOf(multipleRoles);

            verify(roleService, times(1)).selectRolePermissionByUserId(userId);
        }

        @Test
        @DisplayName("应该处理不同用户ID调用_每次调用都应该查询数据库")
        void shouldHandleDifferentUserIds_EachCallShouldQueryDatabase() {
            // Arrange
            Long userId1 = 10L;
            Long userId2 = 20L;
            Set<String> roles1 = new HashSet<>(Arrays.asList("role1"));
            Set<String> roles2 = new HashSet<>(Arrays.asList("role2"));

            when(roleService.selectRolePermissionByUserId(userId1)).thenReturn(roles1);
            when(roleService.selectRolePermissionByUserId(userId2)).thenReturn(roles2);

            // Act
            Set<String> result1 = permissionService.getRolePermission(userId1);
            Set<String> result2 = permissionService.getRolePermission(userId2);

            // Assert
            assertThat(result1).containsExactly("role1");
            assertThat(result2).containsExactly("role2");

            verify(roleService, times(1)).selectRolePermissionByUserId(userId1);
            verify(roleService, times(1)).selectRolePermissionByUserId(userId2);
        }

        /**
         * 注意: 无法测试超级管理员路径
         * <p>
         * 原因: LoginHelper.isSuperAdmin(userId) 是静态方法,需要 mockito-inline 才能 mock
         * </p>
         * <p>
         * 期望行为: 当 userId=1L (超级管理员) 时,应该返回包含 "superadmin" 的 Set,
         * 而不调用 roleService.selectRolePermissionByUserId()
         * </p>
         */
    }

    @Nested
    @DisplayName("2. 菜单权限获取测试")
    class GetMenuPermissionTests {

        @Test
        @DisplayName("应该返回普通用户的菜单权限列表")
        void shouldReturnMenuPermissions_WhenNormalUser() {
            // Arrange - 准备测试数据
            Long userId = 2L; // 普通用户ID
            Set<String> expectedPerms = new HashSet<>(Arrays.asList(
                "system:user:list",
                "system:user:query",
                "system:role:list",
                "system:menu:list"
            ));

            // Mock menuService 返回菜单权限
            when(menuService.selectMenuPermsByUserId(userId)).thenReturn(expectedPerms);

            // Act - 执行测试
            Set<String> result = permissionService.getMenuPermission(userId);

            // Assert - 验证结果
            assertThat(result)
                .as("应该返回普通用户的菜单权限")
                .isNotNull()
                .isNotEmpty()
                .hasSize(4)
                .containsExactlyInAnyOrderElementsOf(expectedPerms);

            // Verify - 验证交互
            verify(menuService, times(1)).selectMenuPermsByUserId(userId);
            verifyNoMoreInteractions(menuService);
            verifyNoInteractions(roleService); // roleService 不应该被调用
        }

        @Test
        @DisplayName("应该返回空集合_当用户没有任何菜单权限")
        void shouldReturnEmptySet_WhenUserHasNoMenuPermissions() {
            // Arrange
            Long userId = 3L;
            Set<String> emptyPerms = new HashSet<>();

            when(menuService.selectMenuPermsByUserId(userId)).thenReturn(emptyPerms);

            // Act
            Set<String> result = permissionService.getMenuPermission(userId);

            // Assert
            assertThat(result)
                .as("应该返回空集合")
                .isNotNull()
                .isEmpty();

            verify(menuService, times(1)).selectMenuPermsByUserId(userId);
        }

        @Test
        @DisplayName("应该返回单个菜单权限_当用户只有一个权限")
        void shouldReturnSinglePermission_WhenUserHasOnlyOnePermission() {
            // Arrange
            Long userId = 4L;
            Set<String> singlePerm = new HashSet<>(Arrays.asList("system:user:list"));

            when(menuService.selectMenuPermsByUserId(userId)).thenReturn(singlePerm);

            // Act
            Set<String> result = permissionService.getMenuPermission(userId);

            // Assert
            assertThat(result)
                .as("应该返回单个菜单权限")
                .isNotNull()
                .hasSize(1)
                .containsExactly("system:user:list");

            verify(menuService, times(1)).selectMenuPermsByUserId(userId);
        }

        @Test
        @DisplayName("应该返回多个菜单权限_当用户有多个权限")
        void shouldReturnMultiplePermissions_WhenUserHasMultiplePermissions() {
            // Arrange
            Long userId = 5L;
            Set<String> multiplePerms = new HashSet<>(Arrays.asList(
                "system:user:list", "system:user:add", "system:user:edit",
                "system:user:remove", "system:user:export", "system:user:import",
                "system:role:list", "system:role:add", "system:role:edit",
                "system:menu:list", "system:dept:list"
            ));

            when(menuService.selectMenuPermsByUserId(userId)).thenReturn(multiplePerms);

            // Act
            Set<String> result = permissionService.getMenuPermission(userId);

            // Assert
            assertThat(result)
                .as("应该返回多个菜单权限")
                .isNotNull()
                .hasSize(11)
                .containsExactlyInAnyOrderElementsOf(multiplePerms);

            verify(menuService, times(1)).selectMenuPermsByUserId(userId);
        }

        @Test
        @DisplayName("应该返回完整的CRUD权限集合_当用户有完整操作权限")
        void shouldReturnFullCrudPermissions_WhenUserHasFullAccess() {
            // Arrange
            Long userId = 6L;
            Set<String> crudPerms = new HashSet<>(Arrays.asList(
                "system:user:list",   // 查询
                "system:user:query",  // 详情
                "system:user:add",    // 新增
                "system:user:edit",   // 编辑
                "system:user:remove", // 删除
                "system:user:export", // 导出
                "system:user:import"  // 导入
            ));

            when(menuService.selectMenuPermsByUserId(userId)).thenReturn(crudPerms);

            // Act
            Set<String> result = permissionService.getMenuPermission(userId);

            // Assert
            assertThat(result)
                .as("应该返回完整的CRUD权限")
                .isNotNull()
                .hasSize(7)
                .containsAll(Arrays.asList(
                    "system:user:list", "system:user:query",
                    "system:user:add", "system:user:edit",
                    "system:user:remove", "system:user:export",
                    "system:user:import"
                ));

            verify(menuService, times(1)).selectMenuPermsByUserId(userId);
        }

        @Test
        @DisplayName("应该处理不同模块的权限_包含system、monitor、workflow等")
        void shouldHandleDifferentModulePermissions_IncludingSystemMonitorWorkflow() {
            // Arrange
            Long userId = 7L;
            Set<String> multiModulePerms = new HashSet<>(Arrays.asList(
                "system:user:list",
                "monitor:online:list",
                "workflow:process:list",
                "gen:code:preview",
                "tool:build:list"
            ));

            when(menuService.selectMenuPermsByUserId(userId)).thenReturn(multiModulePerms);

            // Act
            Set<String> result = permissionService.getMenuPermission(userId);

            // Assert
            assertThat(result)
                .as("应该返回不同模块的权限")
                .isNotNull()
                .hasSize(5)
                .contains(
                    "system:user:list",
                    "monitor:online:list",
                    "workflow:process:list",
                    "gen:code:preview",
                    "tool:build:list"
                );

            verify(menuService, times(1)).selectMenuPermsByUserId(userId);
        }

        @Test
        @DisplayName("应该处理不同用户ID调用_每次调用都应该查询数据库")
        void shouldHandleDifferentUserIds_EachCallShouldQueryDatabase() {
            // Arrange
            Long userId1 = 10L;
            Long userId2 = 20L;
            Set<String> perms1 = new HashSet<>(Arrays.asList("system:user:list"));
            Set<String> perms2 = new HashSet<>(Arrays.asList("system:role:list"));

            when(menuService.selectMenuPermsByUserId(userId1)).thenReturn(perms1);
            when(menuService.selectMenuPermsByUserId(userId2)).thenReturn(perms2);

            // Act
            Set<String> result1 = permissionService.getMenuPermission(userId1);
            Set<String> result2 = permissionService.getMenuPermission(userId2);

            // Assert
            assertThat(result1).containsExactly("system:user:list");
            assertThat(result2).containsExactly("system:role:list");

            verify(menuService, times(1)).selectMenuPermsByUserId(userId1);
            verify(menuService, times(1)).selectMenuPermsByUserId(userId2);
        }

        /**
         * 注意: 无法测试超级管理员路径
         * <p>
         * 原因: LoginHelper.isSuperAdmin(userId) 是静态方法,需要 mockito-inline 才能 mock
         * </p>
         * <p>
         * 期望行为: 当 userId=1L (超级管理员) 时,应该返回包含 "*:*:*" 的 Set,
         * 而不调用 menuService.selectMenuPermsByUserId()
         * </p>
         */
    }

    @Nested
    @DisplayName("3. 边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("应该处理userId为null的情况_getRolePermission")
        void shouldHandleNullUserId_GetRolePermission() {
            // Arrange
            Long userId = null;
            Set<String> emptySet = new HashSet<>();

            when(roleService.selectRolePermissionByUserId(userId)).thenReturn(emptySet);

            // Act
            Set<String> result = permissionService.getRolePermission(userId);

            // Assert
            assertThat(result)
                .as("应该返回空集合")
                .isNotNull()
                .isEmpty();

            verify(roleService, times(1)).selectRolePermissionByUserId(userId);
        }

        @Test
        @DisplayName("应该处理userId为null的情况_getMenuPermission")
        void shouldHandleNullUserId_GetMenuPermission() {
            // Arrange
            Long userId = null;
            Set<String> emptySet = new HashSet<>();

            when(menuService.selectMenuPermsByUserId(userId)).thenReturn(emptySet);

            // Act
            Set<String> result = permissionService.getMenuPermission(userId);

            // Assert
            assertThat(result)
                .as("应该返回空集合")
                .isNotNull()
                .isEmpty();

            verify(menuService, times(1)).selectMenuPermsByUserId(userId);
        }

        @Test
        @DisplayName("应该处理userId为0的情况_getRolePermission")
        void shouldHandleZeroUserId_GetRolePermission() {
            // Arrange
            Long userId = 0L;
            Set<String> emptySet = new HashSet<>();

            when(roleService.selectRolePermissionByUserId(userId)).thenReturn(emptySet);

            // Act
            Set<String> result = permissionService.getRolePermission(userId);

            // Assert
            assertThat(result)
                .as("应该返回空集合")
                .isNotNull()
                .isEmpty();

            verify(roleService, times(1)).selectRolePermissionByUserId(userId);
        }

        @Test
        @DisplayName("应该处理userId为负数的情况_getMenuPermission")
        void shouldHandleNegativeUserId_GetMenuPermission() {
            // Arrange
            Long userId = -1L;
            Set<String> emptySet = new HashSet<>();

            when(menuService.selectMenuPermsByUserId(userId)).thenReturn(emptySet);

            // Act
            Set<String> result = permissionService.getMenuPermission(userId);

            // Assert
            assertThat(result)
                .as("应该返回空集合")
                .isNotNull()
                .isEmpty();

            verify(menuService, times(1)).selectMenuPermsByUserId(userId);
        }

        @Test
        @DisplayName("应该处理userId为最大值的情况")
        void shouldHandleMaxLongUserId() {
            // Arrange
            Long userId = Long.MAX_VALUE;
            Set<String> emptySet = new HashSet<>();

            when(roleService.selectRolePermissionByUserId(userId)).thenReturn(emptySet);
            when(menuService.selectMenuPermsByUserId(userId)).thenReturn(emptySet);

            // Act
            Set<String> roleResult = permissionService.getRolePermission(userId);
            Set<String> menuResult = permissionService.getMenuPermission(userId);

            // Assert
            assertThat(roleResult).isNotNull().isEmpty();
            assertThat(menuResult).isNotNull().isEmpty();

            verify(roleService, times(1)).selectRolePermissionByUserId(userId);
            verify(menuService, times(1)).selectMenuPermsByUserId(userId);
        }
    }

    @Nested
    @DisplayName("4. 服务交互验证测试")
    class ServiceInteractionTests {

        @Test
        @DisplayName("getRolePermission应该只调用roleService_不调用menuService")
        void getRolePermissionShouldOnlyCallRoleService() {
            // Arrange
            Long userId = 100L;
            Set<String> roles = new HashSet<>(Arrays.asList("role1"));
            when(roleService.selectRolePermissionByUserId(userId)).thenReturn(roles);

            // Act
            permissionService.getRolePermission(userId);

            // Assert - 验证只调用了 roleService,没有调用 menuService
            verify(roleService, times(1)).selectRolePermissionByUserId(userId);
            verifyNoMoreInteractions(roleService);
            verifyNoInteractions(menuService);
        }

        @Test
        @DisplayName("getMenuPermission应该只调用menuService_不调用roleService")
        void getMenuPermissionShouldOnlyCallMenuService() {
            // Arrange
            Long userId = 100L;
            Set<String> perms = new HashSet<>(Arrays.asList("system:user:list"));
            when(menuService.selectMenuPermsByUserId(userId)).thenReturn(perms);

            // Act
            permissionService.getMenuPermission(userId);

            // Assert - 验证只调用了 menuService,没有调用 roleService
            verify(menuService, times(1)).selectMenuPermsByUserId(userId);
            verifyNoMoreInteractions(menuService);
            verifyNoInteractions(roleService);
        }

        @Test
        @DisplayName("多次调用getRolePermission应该每次都查询roleService")
        void multipleCallsToGetRolePermissionShouldQueryEachTime() {
            // Arrange
            Long userId = 100L;
            Set<String> roles = new HashSet<>(Arrays.asList("role1"));
            when(roleService.selectRolePermissionByUserId(userId)).thenReturn(roles);

            // Act - 调用3次
            permissionService.getRolePermission(userId);
            permissionService.getRolePermission(userId);
            permissionService.getRolePermission(userId);

            // Assert - 应该调用了3次
            verify(roleService, times(3)).selectRolePermissionByUserId(userId);
        }

        @Test
        @DisplayName("多次调用getMenuPermission应该每次都查询menuService")
        void multipleCallsToGetMenuPermissionShouldQueryEachTime() {
            // Arrange
            Long userId = 100L;
            Set<String> perms = new HashSet<>(Arrays.asList("system:user:list"));
            when(menuService.selectMenuPermsByUserId(userId)).thenReturn(perms);

            // Act - 调用3次
            permissionService.getMenuPermission(userId);
            permissionService.getMenuPermission(userId);
            permissionService.getMenuPermission(userId);

            // Assert - 应该调用了3次
            verify(menuService, times(3)).selectMenuPermsByUserId(userId);
        }
    }
}
