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
import org.dromara.system.domain.SysUser;
import org.dromara.system.domain.SysUserRole;
import org.dromara.system.domain.bo.SysUserBo;
import org.dromara.system.domain.vo.SysPostVo;
import org.dromara.system.domain.vo.SysRoleVo;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.mapper.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysUserServiceImpl 单元测试
 * <p>
 * 测试用户服务的核心业务逻辑
 * </p>
 *
 * <p>测试范围:</p>
 * <ul>
 *   <li>查询类方法 (selectUserByUserName, selectUserById, etc.)</li>
 *   <li>验证类方法 (checkUserNameUnique, checkPhoneUnique, etc.)</li>
 *   <li>业务逻辑方法 (selectUserRoleGroup, selectUserPostGroup)</li>
 * </ul>
 *
 * <p>测试策略:</p>
 * <ul>
 *   <li>使用 Mockito Mock 所有 Mapper 依赖</li>
 *   <li>重点测试业务逻辑，不测试框架功能</li>
 *   <li>跳过依赖 LoginHelper 的方法 (需要 Servlet 上下文)</li>
 * </ul>
 *
 * @author Test Team
 */
@SuppressWarnings("unchecked")
@DisplayName("SysUserServiceImpl 单元测试")
class SysUserServiceImplTest extends BaseUnitTest {

    @Mock
    private SysUserMapper baseMapper;

    @Mock
    private SysDeptMapper deptMapper;

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SysPostMapper postMapper;

    @Mock
    private SysUserRoleMapper userRoleMapper;

    @Mock
    private SysUserPostMapper userPostMapper;

    @InjectMocks
    private SysUserServiceImpl userService;

    /**
     * 初始化 MyBatis-Plus 表信息缓存
     * <p>
     * 在纯单元测试环境中，MyBatis-Plus 的 LambdaQueryWrapper 需要访问表信息缓存
     * 这个方法在所有测试执行前初始化 SysUser 实体的表信息
     * </p>
     */
    @BeforeAll
    static void initMybatisPlusTableInfo() {
        // 创建 MyBatis 配置
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);

        // 初始化 SysUser 表信息
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        TableInfoHelper.initTableInfo(assistant, SysUser.class);
    }

    // ====================
    // 1. 查询类方法测试
    // ====================

    @Nested
    @DisplayName("1. 查询类方法测试")
    class QueryTests {

        @Test
        @DisplayName("应该根据用户名查询用户")
        void shouldSelectUserByUserName() {
            // Arrange
            String userName = "testuser";
            SysUserVo expectedUser = TestDataFactory.createUserVo(1L, userName);
            when(baseMapper.selectVoOne(any(LambdaQueryWrapper.class))).thenReturn(expectedUser);

            // Act
            SysUserVo result = userService.selectUserByUserName(userName);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUserName()).isEqualTo(userName);
            assertThat(result.getUserId()).isEqualTo(1L);

            // Verify
            verify(baseMapper, times(1)).selectVoOne(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该在用户名不存在时返回 null")
        void shouldReturnNullWhenUserNameNotFound() {
            // Arrange
            String userName = "nonexistent";
            when(baseMapper.selectVoOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // Act
            SysUserVo result = userService.selectUserByUserName(userName);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该根据手机号查询用户")
        void shouldSelectUserByPhonenumber() {
            // Arrange
            String phonenumber = "13800138000";
            SysUserVo expectedUser = TestDataFactory.createUserVo(1L, "testuser");
            expectedUser.setPhonenumber(phonenumber);
            when(baseMapper.selectVoOne(any(LambdaQueryWrapper.class))).thenReturn(expectedUser);

            // Act
            SysUserVo result = userService.selectUserByPhonenumber(phonenumber);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getPhonenumber()).isEqualTo(phonenumber);

            // Verify
            verify(baseMapper, times(1)).selectVoOne(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据用户ID查询用户及其角色")
        void shouldSelectUserByIdWithRoles() {
            // Arrange
            Long userId = 1L;
            SysUserVo user = TestDataFactory.createUserVo(userId, "testuser");
            List<SysRoleVo> roles = Arrays.asList(
                TestDataFactory.createRoleVo(1L, "admin"),
                TestDataFactory.createRoleVo(2L, "user")
            );

            when(baseMapper.selectVoById(userId)).thenReturn(user);
            when(roleMapper.selectRolesByUserId(userId)).thenReturn(roles);

            // Act
            SysUserVo result = userService.selectUserById(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getRoles()).isNotNull();
            assertThat(result.getRoles()).hasSize(2);
            assertThat(result.getRoles()).extracting(SysRoleVo::getRoleKey)
                .containsExactly("admin", "user");

            // Verify
            verify(baseMapper, times(1)).selectVoById(userId);
            verify(roleMapper, times(1)).selectRolesByUserId(userId);
        }

        @Test
        @DisplayName("应该在用户ID不存在时返回 null")
        void shouldReturnNullWhenUserIdNotFound() {
            // Arrange
            Long userId = 999L;
            when(baseMapper.selectVoById(userId)).thenReturn(null);

            // Act
            SysUserVo result = userService.selectUserById(userId);

            // Assert
            assertThat(result).isNull();

            // Verify - 不应该查询角色
            verify(baseMapper, times(1)).selectVoById(userId);
            verify(roleMapper, never()).selectRolesByUserId(any());
        }

        @Test
        @DisplayName("应该根据用户ID列表查询用户")
        void shouldSelectUserByIds() {
            // Arrange
            List<Long> userIds = Arrays.asList(1L, 2L, 3L);
            Long deptId = 100L;
            List<SysUserVo> expectedUsers = Arrays.asList(
                TestDataFactory.createUserVo(1L, "user1"),
                TestDataFactory.createUserVo(2L, "user2"),
                TestDataFactory.createUserVo(3L, "user3")
            );

            // Directly mock selectUserList method
            when(baseMapper.selectUserList(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(expectedUsers);

            // Act
            List<SysUserVo> result = userService.selectUserByIds(userIds, deptId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result).extracting(SysUserVo::getUserId)
                .containsExactly(1L, 2L, 3L);
        }

        @Test
        @DisplayName("应该正确查询用户所属角色组")
        void shouldSelectUserRoleGroup() {
            // Arrange
            Long userId = 1L;
            List<SysRoleVo> roles = Arrays.asList(
                TestDataFactory.createRoleVo(1L, "admin"),
                TestDataFactory.createRoleVo(2L, "user"),
                TestDataFactory.createRoleVo(3L, "guest")
            );
            roles.get(0).setRoleName("管理员");
            roles.get(1).setRoleName("普通用户");
            roles.get(2).setRoleName("访客");

            when(roleMapper.selectRolesByUserId(userId)).thenReturn(roles);

            // Act
            String result = userService.selectUserRoleGroup(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).contains("管理员", "普通用户", "访客");

            // Verify
            verify(roleMapper, times(1)).selectRolesByUserId(userId);
        }

        @Test
        @DisplayName("应该在用户无角色时返回空字符串")
        void shouldReturnEmptyStringWhenUserHasNoRoles() {
            // Arrange
            Long userId = 1L;
            when(roleMapper.selectRolesByUserId(userId)).thenReturn(Collections.emptyList());

            // Act
            String result = userService.selectUserRoleGroup(userId);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该正确查询用户所属岗位组")
        void shouldSelectUserPostGroup() {
            // Arrange
            Long userId = 1L;
            List<SysPostVo> posts = Arrays.asList(
                TestDataFactory.createPostVo(1L, "CEO"),
                TestDataFactory.createPostVo(2L, "CTO")
            );
            posts.get(0).setPostName("董事长");
            posts.get(1).setPostName("技术总监");

            when(postMapper.selectPostsByUserId(userId)).thenReturn(posts);

            // Act
            String result = userService.selectUserPostGroup(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).contains("董事长", "技术总监");

            // Verify
            verify(postMapper, times(1)).selectPostsByUserId(userId);
        }

        @Test
        @DisplayName("应该在用户无岗位时返回空字符串")
        void shouldReturnEmptyStringWhenUserHasNoPosts() {
            // Arrange
            Long userId = 1L;
            when(postMapper.selectPostsByUserId(userId)).thenReturn(Collections.emptyList());

            // Act
            String result = userService.selectUserPostGroup(userId);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    // ====================
    // 2. 验证类方法测试
    // ====================

    @Nested
    @DisplayName("2. 验证类方法测试")
    class ValidationTests {

        @Test
        @DisplayName("应该在用户名唯一时返回 true")
        void shouldReturnTrueWhenUserNameIsUnique() {
            // Arrange
            SysUserBo user = TestDataFactory.createUserBo(null, "newuser");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(false);

            // Act
            boolean result = userService.checkUserNameUnique(user);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该在用户名重复时返回 false")
        void shouldReturnFalseWhenUserNameIsDuplicate() {
            // Arrange
            SysUserBo user = TestDataFactory.createUserBo(null, "existinguser");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(true);

            // Act
            boolean result = userService.checkUserNameUnique(user);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("应该在更新用户时排除自身ID进行唯一性校验")
        void shouldExcludeSelfIdWhenCheckingUserNameUniqueness() {
            // Arrange
            Long userId = 1L;
            SysUserBo user = TestDataFactory.createUserBo(userId, "testuser");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(false);

            // Act
            boolean result = userService.checkUserNameUnique(user);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该在手机号唯一时返回 true")
        void shouldReturnTrueWhenPhoneIsUnique() {
            // Arrange
            SysUserBo user = TestDataFactory.createUserBo(null, "testuser");
            user.setPhonenumber("13800138000");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(false);

            // Act
            boolean result = userService.checkPhoneUnique(user);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该在手机号重复时返回 false")
        void shouldReturnFalseWhenPhoneIsDuplicate() {
            // Arrange
            SysUserBo user = TestDataFactory.createUserBo(null, "testuser");
            user.setPhonenumber("13800138000");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(true);

            // Act
            boolean result = userService.checkPhoneUnique(user);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("应该在邮箱唯一时返回 true")
        void shouldReturnTrueWhenEmailIsUnique() {
            // Arrange
            SysUserBo user = TestDataFactory.createUserBo(null, "testuser");
            user.setEmail("test@example.com");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(false);

            // Act
            boolean result = userService.checkEmailUnique(user);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该在邮箱重复时返回 false")
        void shouldReturnFalseWhenEmailIsDuplicate() {
            // Arrange
            SysUserBo user = TestDataFactory.createUserBo(null, "testuser");
            user.setEmail("test@example.com");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(true);

            // Act
            boolean result = userService.checkEmailUnique(user);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("应该在更新时排除自身ID进行手机号唯一性校验")
        void shouldExcludeSelfIdWhenCheckingPhoneUniqueness() {
            // Arrange
            Long userId = 1L;
            SysUserBo user = TestDataFactory.createUserBo(userId, "testuser");
            user.setPhonenumber("13800138000");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(false);

            // Act
            boolean result = userService.checkPhoneUnique(user);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该在更新时排除自身ID进行邮箱唯一性校验")
        void shouldExcludeSelfIdWhenCheckingEmailUniqueness() {
            // Arrange
            Long userId = 1L;
            SysUserBo user = TestDataFactory.createUserBo(userId, "testuser");
            user.setEmail("test@example.com");
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(false);

            // Act
            boolean result = userService.checkEmailUnique(user);

            // Assert
            assertThat(result).isTrue();
        }
    }

    // ====================
    // 3. 分页查询测试
    // ====================

    @Nested
    @DisplayName("3. 分页查询测试")
    class PaginationTests {

        @Test
        @DisplayName("应该正确构建分页查询结果")
        void shouldSelectPageUserList() {
            // Arrange
            SysUserBo queryUser = new SysUserBo();
            queryUser.setUserName("test");
            queryUser.setStatus("0");

            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNum(1);
            pageQuery.setPageSize(10);

            Page<SysUserVo> mockPage = new Page<>();
            List<SysUserVo> users = Arrays.asList(
                TestDataFactory.createUserVo(1L, "testuser1"),
                TestDataFactory.createUserVo(2L, "testuser2")
            );
            mockPage.setRecords(users);
            mockPage.setTotal(2);

            when(baseMapper.selectPageUserList(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

            // Act
            TableDataInfo<SysUserVo> result = userService.selectPageUserList(queryUser, pageQuery);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getRows()).hasSize(2);
            assertThat(result.getTotal()).isEqualTo(2);
            assertThat(result.getRows()).extracting(SysUserVo::getUserName)
                .containsExactly("testuser1", "testuser2");

            // Verify
            verify(baseMapper, times(1))
                .selectPageUserList(any(Page.class), any(LambdaQueryWrapper.class));
        }
    }

    // ====================
    // 4. 边界值和异常处理测试
    // ====================

    @Nested
    @DisplayName("4. 边界值和异常处理测试")
    class BoundaryTests {

        @Test
        @DisplayName("应该正确处理空用户ID列表")
        void shouldHandleEmptyUserIdsList() {
            // Arrange
            List<Long> emptyUserIds = Collections.emptyList();
            Long deptId = 100L;
            // Directly mock selectUserList method
            when(baseMapper.selectUserList(ArgumentMatchers.<Wrapper<SysUser>>any()))
                .thenReturn(Collections.emptyList());

            // Act
            List<SysUserVo> result = userService.selectUserByIds(emptyUserIds, deptId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该正确处理 null 部门ID")
        void shouldHandleNullDeptId() {
            // Arrange
            List<Long> userIds = Arrays.asList(1L, 2L);
            Long deptId = null;
            List<SysUserVo> expectedUsers = Arrays.asList(
                TestDataFactory.createUserVo(1L, "user1"),
                TestDataFactory.createUserVo(2L, "user2")
            );
            // Directly mock selectUserList method
            when(baseMapper.selectUserList(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(expectedUsers);

            // Act
            List<SysUserVo> result = userService.selectUserByIds(userIds, deptId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
        }
    }

    // ====================
    // 5. 简单查询方法测试
    // ====================

    @Nested
    @DisplayName("5. 简单查询方法测试")
    class SimpleQueryTests {

        @Test
        @DisplayName("应该根据用户ID查询用户名")
        void shouldSelectUserNameById() {
            // Arrange
            Long userId = 1L;
            SysUser user = TestDataFactory.createUser(userId, "testuser");
            when(baseMapper.selectOne(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(user);

            // Act
            String result = userService.selectUserNameById(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("testuser");
        }

        @Test
        @DisplayName("应该在用户不存在时返回 null")
        void shouldReturnNullWhenUserNotFoundForUserName() {
            // Arrange
            Long userId = 999L;
            when(baseMapper.selectOne(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(null);

            // Act
            String result = userService.selectUserNameById(userId);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该根据用户ID查询昵称")
        void shouldSelectNicknameById() {
            // Arrange
            Long userId = 1L;
            SysUser user = TestDataFactory.createUser(userId, "testuser");
            user.setNickName("测试用户1");
            when(baseMapper.selectOne(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(user);

            // Act
            String result = userService.selectNicknameById(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("测试用户1");
        }

        @Test
        @DisplayName("应该根据用户ID查询手机号")
        void shouldSelectPhonenumberById() {
            // Arrange
            Long userId = 1L;
            SysUser user = TestDataFactory.createUser(userId, "testuser");
            user.setPhonenumber("13800138000");
            when(baseMapper.selectOne(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(user);

            // Act
            String result = userService.selectPhonenumberById(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("13800138000");
        }

        @Test
        @DisplayName("应该根据用户ID查询邮箱")
        void shouldSelectEmailById() {
            // Arrange
            Long userId = 1L;
            SysUser user = TestDataFactory.createUser(userId, "testuser");
            user.setEmail("test@example.com");
            when(baseMapper.selectOne(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(user);

            // Act
            String result = userService.selectEmailById(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("应该根据部门ID查询用户列表")
        void shouldSelectUserListByDept() {
            // Arrange
            Long deptId = 100L;
            List<SysUserVo> expectedUsers = Arrays.asList(
                TestDataFactory.createUserVo(1L, "user1"),
                TestDataFactory.createUserVo(2L, "user2")
            );
            when(baseMapper.selectVoList(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(expectedUsers);

            // Act
            List<SysUserVo> result = userService.selectUserListByDept(deptId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).extracting(SysUserVo::getUserName)
                .containsExactly("user1", "user2");
        }

        @Test
        @DisplayName("应该根据角色ID列表查询用户ID列表")
        void shouldSelectUserIdsByRoleIds() {
            // Arrange
            List<Long> roleIds = Arrays.asList(1L, 2L);
            List<SysUserRole> userRoles = new ArrayList<>();
            SysUserRole ur1 = new SysUserRole();
            ur1.setUserId(10L);
            ur1.setRoleId(1L);
            SysUserRole ur2 = new SysUserRole();
            ur2.setUserId(20L);
            ur2.setRoleId(2L);
            userRoles.add(ur1);
            userRoles.add(ur2);

            when(userRoleMapper.selectList(ArgumentMatchers.<Wrapper<SysUserRole>>any())).thenReturn(userRoles);

            // Act
            List<Long> result = userService.selectUserIdsByRoleIds(roleIds);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(10L, 20L);
        }

        @Test
        @DisplayName("应该在部门无用户时返回空列表")
        void shouldReturnEmptyListWhenDeptHasNoUsers() {
            // Arrange
            Long deptId = 999L;
            when(baseMapper.selectVoList(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(Collections.emptyList());

            // Act
            List<SysUserVo> result = userService.selectUserListByDept(deptId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该在角色无用户时返回空列表")
        void shouldReturnEmptyListWhenRoleHasNoUsers() {
            // Arrange
            List<Long> roleIds = Arrays.asList(999L);
            when(userRoleMapper.selectList(ArgumentMatchers.<Wrapper<SysUserRole>>any())).thenReturn(Collections.emptyList());

            // Act
            List<Long> result = userService.selectUserIdsByRoleIds(roleIds);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }
    }

    // ====================
    // 6. 更新方法测试
    // ====================

    @Nested
    @DisplayName("6. 更新方法测试")
    class UpdateTests {

        @Test
        @DisplayName("应该成功更新用户状态")
        void shouldUpdateUserStatus() {
            // Arrange
            Long userId = 1L;
            String status = "1"; // 停用
            when(baseMapper.update(isNull(), ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(1);

            // Act
            int result = userService.updateUserStatus(userId, status);

            // Assert
            assertThat(result).isEqualTo(1);
            verify(baseMapper, times(1)).update(isNull(), ArgumentMatchers.<Wrapper<SysUser>>any());
        }

        @Test
        @DisplayName("应该成功更新用户基本信息")
        void shouldUpdateUserProfile() {
            // Arrange
            SysUserBo user = TestDataFactory.createUserBo(1L, "testuser");
            user.setNickName("新昵称");
            user.setPhonenumber("13900139000");
            user.setEmail("new@example.com");
            user.setSex("0");

            when(baseMapper.update(isNull(), ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(1);

            // Act
            int result = userService.updateUserProfile(user);

            // Assert
            assertThat(result).isEqualTo(1);
            verify(baseMapper, times(1)).update(isNull(), ArgumentMatchers.<Wrapper<SysUser>>any());
        }

        @Test
        @DisplayName("应该成功更新用户头像")
        void shouldUpdateUserAvatar() {
            // Arrange
            Long userId = 1L;
            Long avatarId = 100L;
            when(baseMapper.update(isNull(), ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(1);

            // Act
            boolean result = userService.updateUserAvatar(userId, avatarId);

            // Assert
            assertThat(result).isTrue();
            verify(baseMapper, times(1)).update(isNull(), ArgumentMatchers.<Wrapper<SysUser>>any());
        }

        @Test
        @DisplayName("应该成功重置用户密码")
        void shouldResetUserPassword() {
            // Arrange
            Long userId = 1L;
            String newPassword = "$2a$10$encrypted_password";
            when(baseMapper.update(isNull(), ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(1);

            // Act
            int result = userService.resetUserPwd(userId, newPassword);

            // Assert
            assertThat(result).isEqualTo(1);
            verify(baseMapper, times(1)).update(isNull(), ArgumentMatchers.<Wrapper<SysUser>>any());
        }

        @Test
        @DisplayName("应该在更新失败时返回0")
        void shouldReturnZeroWhenUpdateFails() {
            // Arrange
            Long userId = 999L;
            String status = "1";
            when(baseMapper.update(isNull(), ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(0);

            // Act
            int result = userService.updateUserStatus(userId, status);

            // Assert
            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("应该在头像更新失败时返回false")
        void shouldReturnFalseWhenAvatarUpdateFails() {
            // Arrange
            Long userId = 999L;
            Long avatarId = 100L;
            when(baseMapper.update(isNull(), ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(0);

            // Act
            boolean result = userService.updateUserAvatar(userId, avatarId);

            // Assert
            assertThat(result).isFalse();
        }
    }
}
