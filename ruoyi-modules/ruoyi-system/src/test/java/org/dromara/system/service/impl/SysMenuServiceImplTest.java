package org.dromara.system.service.impl;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.system.BaseUnitTest;
import org.dromara.system.TestDataFactory;
import org.dromara.system.domain.SysMenu;
import org.dromara.system.domain.SysRole;
import org.dromara.system.domain.SysRoleMenu;
import org.dromara.system.domain.SysTenantPackage;
import org.dromara.system.domain.bo.SysMenuBo;
import org.dromara.system.domain.vo.SysMenuVo;
import org.dromara.system.mapper.SysMenuMapper;
import org.dromara.system.mapper.SysRoleMapper;
import org.dromara.system.mapper.SysRoleMenuMapper;
import org.dromara.system.mapper.SysTenantPackageMapper;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * SysMenuServiceImpl 单元测试
 *
 * @author Test Team
 */
@SuppressWarnings("unchecked")
@DisplayName("SysMenuServiceImpl 单元测试")
class SysMenuServiceImplTest extends BaseUnitTest {

    @Mock
    private SysMenuMapper baseMapper;

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SysRoleMenuMapper roleMenuMapper;

    @Mock
    private SysTenantPackageMapper tenantPackageMapper;

    @InjectMocks
    private SysMenuServiceImpl menuService;

    @BeforeAll
    static void initMybatisPlusTableInfo() {
        // 初始化 MyBatis-Plus 表信息，以支持 LambdaQueryWrapper
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        TableInfoHelper.initTableInfo(assistant, SysMenu.class);
        TableInfoHelper.initTableInfo(assistant, SysRoleMenu.class);
    }

    @Nested
    @DisplayName("1. 查询方法测试")
    class QueryMethodsTests {

        @Test
        @DisplayName("应该根据菜单ID查询菜单")
        void shouldSelectMenuById() {
            // Arrange
            Long menuId = 1L;
            SysMenuVo expectedMenu = TestDataFactory.createMenuVo(menuId, "用户管理");
            when(baseMapper.selectVoById(menuId)).thenReturn(expectedMenu);

            // Act
            SysMenuVo result = menuService.selectMenuById(menuId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getMenuId()).isEqualTo(menuId);
            assertThat(result.getMenuName()).isEqualTo("用户管理");
            verify(baseMapper).selectVoById(menuId);
        }

        @Test
        @DisplayName("应该根据用户ID查询菜单权限")
        void shouldSelectMenuPermsByUserId() {
            // Arrange
            Long userId = 1L;
            Set<String> expectedPerms = Set.of("system:user:list", "system:role:query");
            when(baseMapper.selectMenuPermsByUserId(userId)).thenReturn(expectedPerms);

            // Act
            Set<String> result = menuService.selectMenuPermsByUserId(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).contains("system:user:list", "system:role:query");
            verify(baseMapper).selectMenuPermsByUserId(userId);
        }

        @Test
        @DisplayName("应该根据角色ID查询菜单权限")
        void shouldSelectMenuPermsByRoleId() {
            // Arrange
            Long roleId = 1L;
            Set<String> expectedPerms = Set.of("system:user:add", "system:user:edit");
            when(baseMapper.selectMenuPermsByRoleId(roleId)).thenReturn(expectedPerms);

            // Act
            Set<String> result = menuService.selectMenuPermsByRoleId(roleId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).contains("system:user:add", "system:user:edit");
            verify(baseMapper).selectMenuPermsByRoleId(roleId);
        }

        @Test
        @DisplayName("应该根据角色ID查询菜单ID列表")
        void shouldSelectMenuListByRoleId() {
            // Arrange
            Long roleId = 1L;
            SysRole role = new SysRole();
            role.setRoleId(roleId);
            role.setMenuCheckStrictly(true);

            List<Long> expectedMenuIds = Arrays.asList(1L, 2L, 3L);

            when(roleMapper.selectById(roleId)).thenReturn(role);
            when(baseMapper.selectMenuListByRoleId(roleId, true)).thenReturn(expectedMenuIds);

            // Act
            List<Long> result = menuService.selectMenuListByRoleId(roleId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result).containsExactly(1L, 2L, 3L);
            verify(roleMapper).selectById(roleId);
            verify(baseMapper).selectMenuListByRoleId(roleId, true);
        }

        @Test
        @DisplayName("应该返回空列表当租户套餐没有菜单时")
        void shouldReturnEmptyListWhenPackageHasNoMenus() {
            // Arrange
            Long packageId = 1L;
            SysTenantPackage tenantPackage = new SysTenantPackage();
            tenantPackage.setPackageId(packageId);
            tenantPackage.setMenuIds("");

            when(tenantPackageMapper.selectById(packageId)).thenReturn(tenantPackage);

            // Act
            List<Long> result = menuService.selectMenuListByPackageId(packageId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            verify(tenantPackageMapper).selectById(packageId);
        }
    }

    @Nested
    @DisplayName("2. 验证方法测试")
    class ValidationMethodsTests {

        @Test
        @DisplayName("应该检查菜单是否有子节点 - 单个菜单ID")
        void shouldCheckHasChildByMenuId() {
            // Arrange
            Long menuId = 1L;
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysMenu>>any())).thenReturn(true);

            // Act
            boolean result = menuService.hasChildByMenuId(menuId);

            // Assert
            assertThat(result).isTrue();
            verify(baseMapper).exists(ArgumentMatchers.<Wrapper<SysMenu>>any());
        }

        @Test
        @DisplayName("应该返回false当菜单没有子节点时")
        void shouldReturnFalseWhenMenuHasNoChild() {
            // Arrange
            Long menuId = 1L;
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysMenu>>any())).thenReturn(false);

            // Act
            boolean result = menuService.hasChildByMenuId(menuId);

            // Assert
            assertThat(result).isFalse();
            verify(baseMapper).exists(ArgumentMatchers.<Wrapper<SysMenu>>any());
        }

        @Test
        @DisplayName("应该检查菜单是否有子节点 - 菜单ID列表")
        void shouldCheckHasChildByMenuIdList() {
            // Arrange
            List<Long> menuIds = Arrays.asList(1L, 2L, 3L);
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysMenu>>any())).thenReturn(true);

            // Act
            boolean result = menuService.hasChildByMenuId(menuIds);

            // Assert
            assertThat(result).isTrue();
            verify(baseMapper).exists(ArgumentMatchers.<Wrapper<SysMenu>>any());
        }

        @Test
        @DisplayName("应该检查菜单是否被角色使用")
        void shouldCheckMenuExistRole() {
            // Arrange
            Long menuId = 1L;
            when(roleMenuMapper.exists(ArgumentMatchers.<Wrapper<SysRoleMenu>>any())).thenReturn(true);

            // Act
            boolean result = menuService.checkMenuExistRole(menuId);

            // Assert
            assertThat(result).isTrue();
            verify(roleMenuMapper).exists(ArgumentMatchers.<Wrapper<SysRoleMenu>>any());
        }

        @Test
        @DisplayName("应该检查菜单名称唯一性 - 唯一")
        void shouldReturnTrueWhenMenuNameIsUnique() {
            // Arrange
            SysMenuBo menu = TestDataFactory.createMenuBo(null, "新菜单");
            menu.setParentId(0L);
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysMenu>>any())).thenReturn(false);

            // Act
            boolean result = menuService.checkMenuNameUnique(menu);

            // Assert
            assertThat(result).isTrue();
            verify(baseMapper).exists(ArgumentMatchers.<Wrapper<SysMenu>>any());
        }

        @Test
        @DisplayName("应该检查菜单名称唯一性 - 重复")
        void shouldReturnFalseWhenMenuNameIsDuplicate() {
            // Arrange
            SysMenuBo menu = TestDataFactory.createMenuBo(null, "用户管理");
            menu.setParentId(0L);
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysMenu>>any())).thenReturn(true);

            // Act
            boolean result = menuService.checkMenuNameUnique(menu);

            // Assert
            assertThat(result).isFalse();
            verify(baseMapper).exists(ArgumentMatchers.<Wrapper<SysMenu>>any());
        }

        @Test
        @DisplayName("应该在更新时排除自身ID检查菜单名称唯一性")
        void shouldExcludeSelfIdWhenCheckingMenuNameUniquenessForUpdate() {
            // Arrange
            SysMenuBo menu = TestDataFactory.createMenuBo(1L, "用户管理");
            menu.setParentId(0L);
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysMenu>>any())).thenReturn(false);

            // Act
            boolean result = menuService.checkMenuNameUnique(menu);

            // Assert
            assertThat(result).isTrue();
            verify(baseMapper).exists(ArgumentMatchers.<Wrapper<SysMenu>>any());
        }
    }

    @Nested
    @DisplayName("3. 树形结构构建测试")
    class TreeBuildingTests {

        @Test
        @DisplayName("应该构建菜单树选择结构")
        void shouldBuildMenuTreeSelect() {
            // Arrange
            List<SysMenuVo> menus = Arrays.asList(
                createMenuVoWithParent(1L, "系统管理", 0L),
                createMenuVoWithParent(2L, "用户管理", 1L),
                createMenuVoWithParent(3L, "角色管理", 1L)
            );

            // Act
            List<Tree<Long>> result = menuService.buildMenuTreeSelect(menus);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // 验证根节点
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getName()).isEqualTo("系统管理");
            // 验证子节点
            assertThat(result.get(0).getChildren()).hasSize(2);
        }

        @Test
        @DisplayName("应该返回空列表当菜单列表为空时")
        void shouldReturnEmptyListWhenMenuListIsEmpty() {
            // Arrange
            List<SysMenuVo> menus = Collections.emptyList();

            // Act
            List<Tree<Long>> result = menuService.buildMenuTreeSelect(menus);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        private SysMenuVo createMenuVoWithParent(Long menuId, String menuName, Long parentId) {
            SysMenuVo vo = TestDataFactory.createMenuVo(menuId, menuName);
            vo.setParentId(parentId);
            return vo;
        }
    }

    @Nested
    @DisplayName("4. 删除方法测试")
    class DeleteMethodsTests {

        @Test
        @DisplayName("应该根据菜单ID删除菜单")
        void shouldDeleteMenuById() {
            // Arrange
            Long menuId = 1L;
            when(baseMapper.deleteById(menuId)).thenReturn(1);

            // Act
            int result = menuService.deleteMenuById(menuId);

            // Assert
            assertThat(result).isEqualTo(1);
            verify(baseMapper).deleteById(menuId);
        }

        @Test
        @DisplayName("应该批量删除菜单并删除角色菜单关联")
        void shouldBatchDeleteMenusAndRoleMenuRelations() {
            // Arrange
            List<Long> menuIds = Arrays.asList(1L, 2L, 3L);
            when(baseMapper.deleteByIds(menuIds)).thenReturn(3);
            when(roleMenuMapper.deleteByMenuIds(menuIds)).thenReturn(5);

            // Act
            menuService.deleteMenuById(menuIds);

            // Assert
            verify(baseMapper).deleteByIds(menuIds);
            verify(roleMenuMapper).deleteByMenuIds(menuIds);
        }
    }

    @Nested
    @DisplayName("5. 边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("应该处理null菜单ID")
        void shouldHandleNullMenuId() {
            // Arrange
            when(baseMapper.selectVoById(null)).thenReturn(null);

            // Act
            SysMenuVo result = menuService.selectMenuById(null);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper).selectVoById(null);
        }

        @Test
        @DisplayName("应该处理空权限集合")
        void shouldHandleEmptyPermissionSet() {
            // Arrange
            Long userId = 999L;
            when(baseMapper.selectMenuPermsByUserId(userId)).thenReturn(Collections.emptySet());

            // Act
            Set<String> result = menuService.selectMenuPermsByUserId(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            verify(baseMapper).selectMenuPermsByUserId(userId);
        }

        @Test
        @DisplayName("应该处理空菜单ID列表")
        void shouldHandleEmptyMenuIdList() {
            // Arrange
            List<Long> emptyList = Collections.emptyList();

            // Act
            menuService.deleteMenuById(emptyList);

            // Assert
            verify(baseMapper).deleteByIds(emptyList);
            verify(roleMenuMapper).deleteByMenuIds(emptyList);
        }
    }
}
