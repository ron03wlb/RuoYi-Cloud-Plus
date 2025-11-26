package org.dromara.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.Collections;
import java.util.List;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.BaseUnitTest;
import org.dromara.system.domain.SysTenant;
import org.dromara.system.domain.bo.SysTenantBo;
import org.dromara.system.domain.vo.SysTenantVo;
import org.dromara.system.mapper.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * SysTenantServiceImpl 单元测试
 *
 * @author Test Team
 */
@SuppressWarnings("unchecked")
@DisplayName("SysTenantServiceImpl 单元测试")
class SysTenantServiceImplTest extends BaseUnitTest {

    @Mock private SysTenantMapper baseMapper;

    @Mock private SysTenantPackageMapper tenantPackageMapper;

    @Mock private SysUserMapper userMapper;

    @Mock private SysDeptMapper deptMapper;

    @Mock private SysRoleMapper roleMapper;

    @Mock private SysRoleMenuMapper roleMenuMapper;

    @Mock private SysRoleDeptMapper roleDeptMapper;

    @Mock private SysUserRoleMapper userRoleMapper;

    @Mock private SysDictTypeMapper dictTypeMapper;

    @Mock private SysDictDataMapper dictDataMapper;

    @Mock private SysConfigMapper configMapper;

    @InjectMocks private SysTenantServiceImpl tenantService;

    @BeforeAll
    static void initMybatisPlusTableInfo() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        TableInfoHelper.initTableInfo(assistant, SysTenant.class);
    }

    @Nested
    @DisplayName("1. 查询方法测试")
    class QueryTests {

        @Test
        @DisplayName("应该根据ID查询租户")
        void shouldQueryById() {
            SysTenantVo vo = new SysTenantVo();
            vo.setId(1L);
            vo.setTenantId("000000");
            vo.setCompanyName("测试公司");

            when(baseMapper.selectVoById(1L)).thenReturn(vo);

            SysTenantVo result = tenantService.queryById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTenantId()).isEqualTo("000000");
            verify(baseMapper).selectVoById(1L);
        }

        @Test
        @DisplayName("应该根据租户ID查询租户")
        void shouldQueryByTenantId() {
            SysTenantVo vo = new SysTenantVo();
            vo.setId(1L);
            vo.setTenantId("000000");

            when(baseMapper.selectVoOne(any(LambdaQueryWrapper.class))).thenReturn(vo);

            SysTenantVo result = tenantService.queryByTenantId("000000");

            assertThat(result).isNotNull();
            assertThat(result.getTenantId()).isEqualTo("000000");
        }

        @Test
        @DisplayName("应该分页查询租户列表")
        void shouldQueryPageList() {
            SysTenantBo bo = new SysTenantBo();
            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNum(1);
            pageQuery.setPageSize(10);

            Page<SysTenantVo> page = new Page<>(1, 10);
            SysTenantVo vo = new SysTenantVo();
            vo.setId(1L);
            page.setRecords(Collections.singletonList(vo));

            when(baseMapper.selectVoPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            TableDataInfo<SysTenantVo> result = tenantService.queryPageList(bo, pageQuery);

            assertThat(result).isNotNull();
            assertThat(result.getRows()).hasSize(1);
        }

        @Test
        @DisplayName("应该根据条件查询租户列表")
        void shouldQueryList() {
            SysTenantBo bo = new SysTenantBo();
            bo.setCompanyName("测试");

            SysTenantVo vo = new SysTenantVo();
            vo.setCompanyName("测试公司");

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.singletonList(vo));

            List<SysTenantVo> result = tenantService.queryList(bo);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCompanyName()).contains("测试");
        }

        @Test
        @DisplayName("应该在租户不存在时返回null")
        void shouldReturnNullWhenTenantNotFound() {
            when(baseMapper.selectVoOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            SysTenantVo result = tenantService.queryByTenantId("nonexistent");

            assertThat(result).isNull();
        }
    }
}
