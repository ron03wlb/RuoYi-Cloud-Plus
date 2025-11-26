package org.dromara.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.system.BaseUnitTest;
import org.dromara.system.TestDataFactory;
import org.dromara.system.domain.SysDept;
import org.dromara.system.domain.SysRole;
import org.dromara.system.domain.SysUser;
import org.dromara.system.domain.bo.SysDeptBo;
import org.dromara.system.domain.vo.SysDeptVo;
import org.dromara.system.mapper.SysDeptMapper;
import org.dromara.system.mapper.SysRoleMapper;
import org.dromara.system.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * SysDeptServiceImpl 单元测试
 *
 * @author Test Team
 */
@SuppressWarnings("unchecked")
@DisplayName("SysDeptServiceImpl 单元测试")
class SysDeptServiceImplTest extends BaseUnitTest {

    @Mock private SysDeptMapper baseMapper;

    @Mock private SysRoleMapper roleMapper;

    @Mock private SysUserMapper userMapper;

    @InjectMocks private SysDeptServiceImpl deptService;

    @BeforeAll
    static void initMybatisPlusTableInfo() {
        // 初始化 MyBatis-Plus 表信息，以支持 LambdaQueryWrapper
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        TableInfoHelper.initTableInfo(assistant, SysDept.class);
        TableInfoHelper.initTableInfo(assistant, SysUser.class);
    }

    @Nested
    @DisplayName("1. 查询方法测试")
    class QueryMethodsTests {

        @Test
        @DisplayName("应该根据部门ID查询部门信息")
        void shouldSelectDeptById() {
            // Arrange
            Long deptId = 1L;
            SysDeptVo dept = TestDataFactory.createDeptVo(deptId, "研发部");
            SysDeptVo parentDept = TestDataFactory.createDeptVo(0L, "总公司");
            parentDept.setDeptName("总公司");

            when(baseMapper.selectVoById(deptId)).thenReturn(dept);
            when(baseMapper.selectVoOne(ArgumentMatchers.<Wrapper<SysDept>>any()))
                    .thenReturn(parentDept);

            // Act
            SysDeptVo result = deptService.selectDeptById(deptId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getDeptId()).isEqualTo(deptId);
            assertThat(result.getDeptName()).isEqualTo("研发部");
            assertThat(result.getParentName()).isEqualTo("总公司");
            verify(baseMapper).selectVoById(deptId);
            verify(baseMapper).selectVoOne(ArgumentMatchers.<Wrapper<SysDept>>any());
        }

        @Test
        @DisplayName("应该返回null当部门不存在时")
        void shouldReturnNullWhenDeptNotFound() {
            // Arrange
            Long deptId = 999L;
            when(baseMapper.selectVoById(deptId)).thenReturn(null);

            // Act
            SysDeptVo result = deptService.selectDeptById(deptId);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper).selectVoById(deptId);
            verify(baseMapper, never()).selectVoOne(any());
        }

        @Test
        @DisplayName("应该根据部门ID列表查询部门")
        void shouldSelectDeptByIds() {
            // Arrange
            List<Long> deptIds = Arrays.asList(1L, 2L, 3L);
            List<SysDeptVo> expectedDepts =
                    Arrays.asList(
                            TestDataFactory.createDeptVo(1L, "研发部"),
                            TestDataFactory.createDeptVo(2L, "市场部"),
                            TestDataFactory.createDeptVo(3L, "财务部"));

            when(baseMapper.selectDeptList(ArgumentMatchers.<Wrapper<SysDept>>any()))
                    .thenReturn(expectedDepts);

            // Act
            List<SysDeptVo> result = deptService.selectDeptByIds(deptIds);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getDeptName()).isEqualTo("研发部");
            verify(baseMapper).selectDeptList(ArgumentMatchers.<Wrapper<SysDept>>any());
        }

        @Test
        @DisplayName("应该根据角色ID查询部门ID列表")
        void shouldSelectDeptListByRoleId() {
            // Arrange
            Long roleId = 1L;
            SysRole role = new SysRole();
            role.setRoleId(roleId);
            role.setDeptCheckStrictly(true);

            List<Long> expectedDeptIds = Arrays.asList(1L, 2L, 3L);

            when(roleMapper.selectById(roleId)).thenReturn(role);
            when(baseMapper.selectDeptListByRoleId(roleId, true)).thenReturn(expectedDeptIds);

            // Act
            List<Long> result = deptService.selectDeptListByRoleId(roleId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result).containsExactly(1L, 2L, 3L);
            verify(roleMapper).selectById(roleId);
            verify(baseMapper).selectDeptListByRoleId(roleId, true);
        }

        @Test
        @DisplayName("应该根据条件查询部门列表")
        void shouldSelectDeptList() {
            // Arrange
            SysDeptBo bo = TestDataFactory.createDeptBo(null, "研发");
            List<SysDeptVo> expectedDepts =
                    Arrays.asList(
                            TestDataFactory.createDeptVo(1L, "研发部"),
                            TestDataFactory.createDeptVo(2L, "研发一组"));

            when(baseMapper.selectDeptList(ArgumentMatchers.<Wrapper<SysDept>>any()))
                    .thenReturn(expectedDepts);

            // Act
            List<SysDeptVo> result = deptService.selectDeptList(bo);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            verify(baseMapper).selectDeptList(ArgumentMatchers.<Wrapper<SysDept>>any());
        }

        @Test
        @DisplayName("应该查询部门树结构")
        void shouldSelectDeptTreeList() {
            // Arrange
            SysDeptBo bo = TestDataFactory.createDeptBo(null, null);
            List<SysDeptVo> depts =
                    Arrays.asList(
                            createDeptVoWithParent(1L, "总公司", 0L),
                            createDeptVoWithParent(2L, "研发部", 1L),
                            createDeptVoWithParent(3L, "市场部", 1L));

            when(baseMapper.selectDeptList(ArgumentMatchers.<Wrapper<SysDept>>any()))
                    .thenReturn(depts);

            // Act
            List<Tree<Long>> result = deptService.selectDeptTreeList(bo);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            verify(baseMapper).selectDeptList(ArgumentMatchers.<Wrapper<SysDept>>any());
        }

        private SysDeptVo createDeptVoWithParent(Long deptId, String deptName, Long parentId) {
            SysDeptVo vo = TestDataFactory.createDeptVo(deptId, deptName);
            vo.setParentId(parentId);
            return vo;
        }
    }

    @Nested
    @DisplayName("2. 验证方法测试")
    class ValidationMethodsTests {

        @Test
        @DisplayName("应该检查部门是否有子节点 - 有子节点")
        void shouldCheckHasChildByDeptId() {
            // Arrange
            Long deptId = 1L;
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysDept>>any())).thenReturn(true);

            // Act
            boolean result = deptService.hasChildByDeptId(deptId);

            // Assert
            assertThat(result).isTrue();
            verify(baseMapper).exists(ArgumentMatchers.<Wrapper<SysDept>>any());
        }

        @Test
        @DisplayName("应该返回false当部门没有子节点时")
        void shouldReturnFalseWhenDeptHasNoChild() {
            // Arrange
            Long deptId = 1L;
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysDept>>any())).thenReturn(false);

            // Act
            boolean result = deptService.hasChildByDeptId(deptId);

            // Assert
            assertThat(result).isFalse();
            verify(baseMapper).exists(ArgumentMatchers.<Wrapper<SysDept>>any());
        }

        @Test
        @DisplayName("应该检查部门是否存在用户 - 存在用户")
        void shouldCheckDeptExistUser() {
            // Arrange
            Long deptId = 1L;
            when(userMapper.exists(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(true);

            // Act
            boolean result = deptService.checkDeptExistUser(deptId);

            // Assert
            assertThat(result).isTrue();
            verify(userMapper).exists(ArgumentMatchers.<Wrapper<SysUser>>any());
        }

        @Test
        @DisplayName("应该返回false当部门不存在用户时")
        void shouldReturnFalseWhenDeptHasNoUser() {
            // Arrange
            Long deptId = 1L;
            when(userMapper.exists(ArgumentMatchers.<Wrapper<SysUser>>any())).thenReturn(false);

            // Act
            boolean result = deptService.checkDeptExistUser(deptId);

            // Assert
            assertThat(result).isFalse();
            verify(userMapper).exists(ArgumentMatchers.<Wrapper<SysUser>>any());
        }

        @Test
        @DisplayName("应该检查部门名称唯一性 - 唯一")
        void shouldReturnTrueWhenDeptNameIsUnique() {
            // Arrange
            SysDeptBo dept = TestDataFactory.createDeptBo(null, "新部门");
            dept.setParentId(1L);
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysDept>>any())).thenReturn(false);

            // Act
            boolean result = deptService.checkDeptNameUnique(dept);

            // Assert
            assertThat(result).isTrue();
            verify(baseMapper).exists(ArgumentMatchers.<Wrapper<SysDept>>any());
        }

        @Test
        @DisplayName("应该检查部门名称唯一性 - 重复")
        void shouldReturnFalseWhenDeptNameIsDuplicate() {
            // Arrange
            SysDeptBo dept = TestDataFactory.createDeptBo(null, "研发部");
            dept.setParentId(1L);
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysDept>>any())).thenReturn(true);

            // Act
            boolean result = deptService.checkDeptNameUnique(dept);

            // Assert
            assertThat(result).isFalse();
            verify(baseMapper).exists(ArgumentMatchers.<Wrapper<SysDept>>any());
        }

        @Test
        @DisplayName("应该在更新时排除自身ID检查部门名称唯一性")
        void shouldExcludeSelfIdWhenCheckingDeptNameUniquenessForUpdate() {
            // Arrange
            SysDeptBo dept = TestDataFactory.createDeptBo(1L, "研发部");
            dept.setParentId(0L);
            when(baseMapper.exists(ArgumentMatchers.<Wrapper<SysDept>>any())).thenReturn(false);

            // Act
            boolean result = deptService.checkDeptNameUnique(dept);

            // Assert
            assertThat(result).isTrue();
            verify(baseMapper).exists(ArgumentMatchers.<Wrapper<SysDept>>any());
        }
    }

    @Nested
    @DisplayName("3. 树形结构构建测试")
    class TreeBuildingTests {

        @Test
        @DisplayName("应该构建部门树选择结构")
        void shouldBuildDeptTreeSelect() {
            // Arrange
            List<SysDeptVo> depts =
                    Arrays.asList(
                            createDeptVoWithParent(1L, "总公司", 0L),
                            createDeptVoWithParent(2L, "研发部", 1L),
                            createDeptVoWithParent(3L, "市场部", 1L),
                            createDeptVoWithParent(4L, "研发一组", 2L));

            // Act
            List<Tree<Long>> result = deptService.buildDeptTreeSelect(depts);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // 验证根节点
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getName()).isEqualTo("总公司");
            // 验证有子节点
            assertThat(result.get(0).getChildren()).isNotEmpty();
        }

        @Test
        @DisplayName("应该返回空列表当部门列表为空时")
        void shouldReturnEmptyListWhenDeptListIsEmpty() {
            // Arrange
            List<SysDeptVo> depts = Collections.emptyList();

            // Act
            List<Tree<Long>> result = deptService.buildDeptTreeSelect(depts);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该在树节点中设置disabled属性")
        void shouldSetDisabledPropertyInTreeNode() {
            // Arrange
            SysDeptVo disabledDept = createDeptVoWithParent(1L, "停用部门", 0L);
            disabledDept.setStatus("1"); // 1=停用
            List<SysDeptVo> depts = Arrays.asList(disabledDept);

            // Act
            List<Tree<Long>> result = deptService.buildDeptTreeSelect(depts);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).get("disabled")).isEqualTo(true);
        }

        private SysDeptVo createDeptVoWithParent(Long deptId, String deptName, Long parentId) {
            SysDeptVo vo = TestDataFactory.createDeptVo(deptId, deptName);
            vo.setParentId(parentId);
            return vo;
        }
    }

    @Nested
    @DisplayName("4. 删除方法测试")
    class DeleteMethodsTests {

        @Test
        @DisplayName("应该根据部门ID删除部门")
        void shouldDeleteDeptById() {
            // Arrange
            Long deptId = 1L;
            when(baseMapper.deleteById(deptId)).thenReturn(1);

            // Act
            int result = deptService.deleteDeptById(deptId);

            // Assert
            assertThat(result).isEqualTo(1);
            verify(baseMapper).deleteById(deptId);
        }

        @Test
        @DisplayName("应该返回0当删除失败时")
        void shouldReturnZeroWhenDeleteFails() {
            // Arrange
            Long deptId = 999L;
            when(baseMapper.deleteById(deptId)).thenReturn(0);

            // Act
            int result = deptService.deleteDeptById(deptId);

            // Assert
            assertThat(result).isEqualTo(0);
            verify(baseMapper).deleteById(deptId);
        }
    }

    @Nested
    @DisplayName("5. 边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("应该处理null部门ID")
        void shouldHandleNullDeptId() {
            // Arrange
            when(baseMapper.selectVoById(null)).thenReturn(null);

            // Act
            SysDeptVo result = deptService.selectDeptById(null);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper).selectVoById(null);
        }

        @Test
        @DisplayName("应该处理空部门ID列表")
        void shouldHandleEmptyDeptIdList() {
            // Arrange
            List<Long> emptyList = Collections.emptyList();
            when(baseMapper.selectDeptList(ArgumentMatchers.<Wrapper<SysDept>>any()))
                    .thenReturn(Collections.emptyList());

            // Act
            List<SysDeptVo> result = deptService.selectDeptByIds(emptyList);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            verify(baseMapper).selectDeptList(ArgumentMatchers.<Wrapper<SysDept>>any());
        }

        @Test
        @DisplayName("应该处理null parent部门")
        void shouldHandleNullParentDept() {
            // Arrange
            Long deptId = 1L;
            SysDeptVo dept = TestDataFactory.createDeptVo(deptId, "研发部");

            when(baseMapper.selectVoById(deptId)).thenReturn(dept);
            when(baseMapper.selectVoOne(ArgumentMatchers.<Wrapper<SysDept>>any())).thenReturn(null);

            // Act
            SysDeptVo result = deptService.selectDeptById(deptId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getParentName()).isNull();
            verify(baseMapper).selectVoById(deptId);
            verify(baseMapper).selectVoOne(ArgumentMatchers.<Wrapper<SysDept>>any());
        }
    }
}
