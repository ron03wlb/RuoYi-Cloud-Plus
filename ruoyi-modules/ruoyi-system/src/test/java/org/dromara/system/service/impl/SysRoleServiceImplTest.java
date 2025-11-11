package org.dromara.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.BaseUnitTest;
import org.dromara.system.TestDataFactory;
import org.dromara.system.domain.SysRole;
import org.dromara.system.domain.SysUserRole;
import org.dromara.system.domain.bo.SysRoleBo;
import org.dromara.system.domain.vo.SysRoleVo;
import org.dromara.system.mapper.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysRoleServiceImpl 单元测试
 * <p>
 * 测试角色服务的核心业务逻辑
 * </p>
 *
 * <p>测试范围:</p>
 * <ul>
 *   <li>查询类方法 (selectRoleById, selectRolesByUserId, etc.)</li>
 *   <li>验证类方法 (checkRoleNameUnique, checkRoleKeyUnique, etc.)</li>
 *   <li>业务逻辑方法 (selectRolePermissionByUserId, selectRolesAuthByUserId)</li>
 * </ul>
 *
 * <p>测试策略:</p>
 * <ul>
 *   <li>使用 Mockito Mock 所有 Mapper 依赖</li>
 *   <li>重点测试业务逻辑，不测试框架功能</li>
 *   <li>跳过依赖 LoginHelper 和 MapstructUtils 的方法</li>
 * </ul>
 *
 * @author Test Team
 */
@SuppressWarnings("unchecked")
@DisplayName("SysRoleServiceImpl 单元测试")
class SysRoleServiceImplTest extends BaseUnitTest {

    @Mock
    private SysRoleMapper baseMapper;

    @Mock
    private SysRoleMenuMapper roleMenuMapper;

    @Mock
    private SysUserRoleMapper userRoleMapper;

    @Mock
    private SysRoleDeptMapper roleDeptMapper;

    @InjectMocks
    private SysRoleServiceImpl roleService;

    /**
     * 初始化 MyBatis-Plus 表信息缓存
     * <p>
     * 在纯单元测试环境中，MyBatis-Plus 的 LambdaQueryWrapper 需要访问表信息缓存
     * 这个方法在所有测试执行前初始化 SysRole 实体的表信息
     * </p>
     */
    @BeforeAll
    static void initMybatisPlusTableInfo() {
        // 创建 MyBatis 配置
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);

        // 初始化 SysRole 表信息
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        TableInfoHelper.initTableInfo(assistant, SysRole.class);
    }

    // ====================
    // 1. 查询类方法测试
    // ====================

    @Nested
    @DisplayName("1. 查询类方法测试")
    class QueryTests {

        @Test
        @DisplayName("应该根据角色ID查询角色")
        void shouldSelectRoleById() {
            // Arrange
            Long roleId = 1L;
            SysRoleVo expectedRole = TestDataFactory.createRoleVo(roleId, "admin");
            when(baseMapper.selectRoleById(roleId)).thenReturn(expectedRole);

            // Act
            SysRoleVo result = roleService.selectRoleById(roleId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getRoleId()).isEqualTo(roleId);
            assertThat(result.getRoleKey()).isEqualTo("admin");

            // Verify
            verify(baseMapper, times(1)).selectRoleById(roleId);
        }

        @Test
        @DisplayName("应该在角色ID不存在时返回 null")
        void shouldReturnNullWhenRoleIdNotFound() {
            // Arrange
            Long roleId = 999L;
            when(baseMapper.selectRoleById(roleId)).thenReturn(null);

            // Act
            SysRoleVo result = roleService.selectRoleById(roleId);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该根据角色ID列表查询角色")
        void shouldSelectRoleByIds() {
            // Arrange
            List<Long> roleIds = Arrays.asList(1L, 2L, 3L);
            List<SysRoleVo> expectedRoles = Arrays.asList(
                TestDataFactory.createRoleVo(1L, "admin"),
                TestDataFactory.createRoleVo(2L, "user"),
                TestDataFactory.createRoleVo(3L, "guest")
            );

            when(baseMapper.selectRoleList(ArgumentMatchers.<Wrapper<SysRole>>any())).thenReturn(expectedRoles);

            // Act
            List<SysRoleVo> result = roleService.selectRoleByIds(roleIds);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result).extracting(SysRoleVo::getRoleKey)
                .containsExactly("admin", "user", "guest");
        }

        @Test
        @DisplayName("应该根据用户ID查询角色列表")
        void shouldSelectRolesByUserId() {
            // Arrange
            Long userId = 1L;
            List<SysRoleVo> expectedRoles = Arrays.asList(
                TestDataFactory.createRoleVo(1L, "admin"),
                TestDataFactory.createRoleVo(2L, "user")
            );

            when(baseMapper.selectRolesByUserId(userId)).thenReturn(expectedRoles);

            // Act
            List<SysRoleVo> result = roleService.selectRolesByUserId(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).extracting(SysRoleVo::getRoleKey)
                .containsExactly("admin", "user");

            // Verify
            verify(baseMapper, times(1)).selectRolesByUserId(userId);
        }

        @Test
        @DisplayName("应该在用户无角色时返回空列表")
        void shouldReturnEmptyListWhenUserHasNoRoles() {
            // Arrange
            Long userId = 999L;
            when(baseMapper.selectRolesByUserId(userId)).thenReturn(Collections.emptyList());

            // Act
            List<SysRoleVo> result = roleService.selectRolesByUserId(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该根据用户ID查询角色ID列表")
        void shouldSelectRoleListByUserId() {
            // Arrange
            Long userId = 1L;
            List<SysRoleVo> roles = Arrays.asList(
                TestDataFactory.createRoleVo(1L, "admin"),
                TestDataFactory.createRoleVo(2L, "user")
            );

            when(baseMapper.selectRolesByUserId(userId)).thenReturn(roles);

            // Act
            List<Long> result = roleService.selectRoleListByUserId(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("应该查询所有角色")
        void shouldSelectRoleAll() {
            // Arrange
            List<SysRoleVo> allRoles = TestDataFactory.createRoleList(3);
            when(baseMapper.selectRoleList(ArgumentMatchers.<Wrapper<SysRole>>any())).thenReturn(allRoles);

            // Act
            List<SysRoleVo> result = roleService.selectRoleAll();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("应该根据用户ID查询角色权限标识")
        void shouldSelectRolePermissionByUserId() {
            // Arrange
            Long userId = 1L;
            SysRoleVo role1 = TestDataFactory.createRoleVo(1L, "admin");
            SysRoleVo role2 = TestDataFactory.createRoleVo(2L, "user,guest"); // 多个权限用逗号分隔

            when(baseMapper.selectRolesByUserId(userId)).thenReturn(Arrays.asList(role1, role2));

            // Act
            Set<String> result = roleService.selectRolePermissionByUserId(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).contains("admin", "user", "guest");
        }

        @Test
        @DisplayName("应该在用户无角色时返回空权限集合")
        void shouldReturnEmptySetWhenUserHasNoRolePermissions() {
            // Arrange
            Long userId = 999L;
            when(baseMapper.selectRolesByUserId(userId)).thenReturn(Collections.emptyList());

            // Act
            Set<String> result = roleService.selectRolePermissionByUserId(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该根据条件查询角色列表")
        void shouldSelectRoleList() {
            // Arrange
            SysRoleBo query = new SysRoleBo();
            query.setRoleName("测试");
            query.setStatus("0");

            List<SysRoleVo> expectedRoles = Arrays.asList(
                TestDataFactory.createRoleVo(1L, "admin"),
                TestDataFactory.createRoleVo(2L, "user")
            );

            when(baseMapper.selectRoleList(ArgumentMatchers.<Wrapper<SysRole>>any())).thenReturn(expectedRoles);

            // Act
            List<SysRoleVo> result = roleService.selectRoleList(query);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("应该正确构建分页查询结果")
        void shouldSelectPageRoleList() {
            // Arrange
            SysRoleBo query = new SysRoleBo();
            query.setRoleName("测试");

            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNum(1);
            pageQuery.setPageSize(10);

            Page<SysRoleVo> mockPage = new Page<>();
            List<SysRoleVo> roles = Arrays.asList(
                TestDataFactory.createRoleVo(1L, "admin"),
                TestDataFactory.createRoleVo(2L, "user")
            );
            mockPage.setRecords(roles);
            mockPage.setTotal(2);

            when(baseMapper.selectPageRoleList(any(Page.class), ArgumentMatchers.<Wrapper<SysRole>>any()))
                .thenReturn(mockPage);

            // Act
            TableDataInfo<SysRoleVo> result = roleService.selectPageRoleList(query, pageQuery);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getRows()).hasSize(2);
            assertThat(result.getTotal()).isEqualTo(2);
            assertThat(result.getRows()).extracting(SysRoleVo::getRoleKey)
                .containsExactly("admin", "user");
        }
    }

    // ====================
    // 2. 验证类方法测试
    // ====================

    @Nested
    @DisplayName("2. 验证类方法测试")
    class ValidationTests {

        @Test
        @DisplayName("应该在角色名称唯一时返回 true")
        void shouldReturnTrueWhenRoleNameIsUnique() {
            // Arrange
            SysRoleBo role = TestDataFactory.createRoleBo(null, "new_role");
            role.setRoleName("新角色");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysRole>>any())).thenReturn(false);

            // Act
            boolean result = roleService.checkRoleNameUnique(role);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该在角色名称重复时返回 false")
        void shouldReturnFalseWhenRoleNameIsDuplicate() {
            // Arrange
            SysRoleBo role = TestDataFactory.createRoleBo(null, "existing_role");
            role.setRoleName("已存在角色");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysRole>>any())).thenReturn(true);

            // Act
            boolean result = roleService.checkRoleNameUnique(role);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("应该在更新角色时排除自身ID进行唯一性校验")
        void shouldExcludeSelfIdWhenCheckingRoleNameUniqueness() {
            // Arrange
            Long roleId = 1L;
            SysRoleBo role = TestDataFactory.createRoleBo(roleId, "test_role");
            role.setRoleName("测试角色");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysRole>>any())).thenReturn(false);

            // Act
            boolean result = roleService.checkRoleNameUnique(role);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该在角色标识唯一时返回 true")
        void shouldReturnTrueWhenRoleKeyIsUnique() {
            // Arrange
            SysRoleBo role = TestDataFactory.createRoleBo(null, "new_role");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysRole>>any())).thenReturn(false);

            // Act
            boolean result = roleService.checkRoleKeyUnique(role);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该在角色标识重复时返回 false")
        void shouldReturnFalseWhenRoleKeyIsDuplicate() {
            // Arrange
            SysRoleBo role = TestDataFactory.createRoleBo(null, "admin");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysRole>>any())).thenReturn(true);

            // Act
            boolean result = roleService.checkRoleKeyUnique(role);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("应该在更新角色时排除自身ID进行标识唯一性校验")
        void shouldExcludeSelfIdWhenCheckingRoleKeyUniqueness() {
            // Arrange
            Long roleId = 1L;
            SysRoleBo role = TestDataFactory.createRoleBo(roleId, "test_role");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysRole>>any())).thenReturn(false);

            // Act
            boolean result = roleService.checkRoleKeyUnique(role);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该正确统计角色关联的用户数量")
        void shouldCountUserRoleByRoleId() {
            // Arrange
            Long roleId = 1L;
            when(userRoleMapper.selectCount(ArgumentMatchers.<Wrapper<SysUserRole>>any())).thenReturn(5L);

            // Act
            long result = roleService.countUserRoleByRoleId(roleId);

            // Assert
            assertThat(result).isEqualTo(5L);
            verify(userRoleMapper, times(1)).selectCount(ArgumentMatchers.<Wrapper<SysUserRole>>any());
        }

        @Test
        @DisplayName("应该在角色未关联用户时返回0")
        void shouldReturnZeroWhenRoleHasNoUsers() {
            // Arrange
            Long roleId = 999L;
            when(userRoleMapper.selectCount(ArgumentMatchers.<Wrapper<SysUserRole>>any())).thenReturn(0L);

            // Act
            long result = roleService.countUserRoleByRoleId(roleId);

            // Assert
            assertThat(result).isEqualTo(0L);
        }
    }

    // ====================
    // 3. 业务逻辑方法测试
    // ====================

    @Nested
    @DisplayName("3. 业务逻辑方法测试")
    class BusinessLogicTests {

        @Test
        @DisplayName("应该正确查询用户角色授权状态")
        void shouldSelectRolesAuthByUserId() {
            // Arrange
            Long userId = 1L;

            // 用户已分配的角色
            List<SysRoleVo> userRoles = Arrays.asList(
                TestDataFactory.createRoleVo(1L, "admin"),
                TestDataFactory.createRoleVo(2L, "user")
            );

            // 系统所有角色
            List<SysRoleVo> allRoles = Arrays.asList(
                TestDataFactory.createRoleVo(1L, "admin"),
                TestDataFactory.createRoleVo(2L, "user"),
                TestDataFactory.createRoleVo(3L, "guest")
            );

            when(baseMapper.selectRolesByUserId(userId)).thenReturn(userRoles);
            when(baseMapper.selectRoleList(ArgumentMatchers.<Wrapper<SysRole>>any())).thenReturn(allRoles);

            // Act
            List<SysRoleVo> result = roleService.selectRolesAuthByUserId(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);

            // 验证前两个角色被标记为已授权
            assertThat(result.get(0).isFlag()).isTrue();
            assertThat(result.get(1).isFlag()).isTrue();
            assertThat(result.get(2).isFlag()).isFalse(); // 未授权的角色默认为 false
        }

        @Test
        @DisplayName("应该在用户无角色时正确返回所有角色（未标记授权）")
        void shouldSelectRolesAuthByUserIdWithNoUserRoles() {
            // Arrange
            Long userId = 999L;

            when(baseMapper.selectRolesByUserId(userId)).thenReturn(Collections.emptyList());
            when(baseMapper.selectRoleList(ArgumentMatchers.<Wrapper<SysRole>>any()))
                .thenReturn(TestDataFactory.createRoleList(3));

            // Act
            List<SysRoleVo> result = roleService.selectRolesAuthByUserId(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            // 所有角色都未被标记为已授权
            assertThat(result).allMatch(role -> !role.isFlag());
        }
    }

    // ====================
    // 4. 边界值和异常处理测试
    // ====================

    @Nested
    @DisplayName("4. 边界值和异常处理测试")
    class BoundaryTests {

        @Test
        @DisplayName("应该正确处理空角色ID列表")
        void shouldHandleEmptyRoleIdsList() {
            // Arrange
            List<Long> emptyRoleIds = Collections.emptyList();
            when(baseMapper.selectRoleList(ArgumentMatchers.<Wrapper<SysRole>>any()))
                .thenReturn(Collections.emptyList());

            // Act
            List<SysRoleVo> result = roleService.selectRoleByIds(emptyRoleIds);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该正确处理 null 用户ID")
        void shouldHandleNullUserId() {
            // Arrange
            Long nullUserId = null;
            when(baseMapper.selectRolesByUserId(nullUserId)).thenReturn(Collections.emptyList());

            // Act
            List<SysRoleVo> result = roleService.selectRolesByUserId(nullUserId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该正确处理角色权限标识中的空格")
        void shouldTrimRoleKeyWhenSelectingPermissions() {
            // Arrange
            Long userId = 1L;
            SysRoleVo role = TestDataFactory.createRoleVo(1L, " admin "); // 带空格

            when(baseMapper.selectRolesByUserId(userId)).thenReturn(Arrays.asList(role));

            // Act
            Set<String> result = roleService.selectRolePermissionByUserId(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).contains("admin");
        }

        @Test
        @DisplayName("应该正确处理多角色权限标识的分割")
        void shouldSplitMultipleRoleKeys() {
            // Arrange
            Long userId = 1L;
            SysRoleVo role = TestDataFactory.createRoleVo(1L, "admin,user,guest");

            when(baseMapper.selectRolesByUserId(userId)).thenReturn(Arrays.asList(role));

            // Act
            Set<String> result = roleService.selectRolePermissionByUserId(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result).contains("admin", "user", "guest");
        }
    }
}
