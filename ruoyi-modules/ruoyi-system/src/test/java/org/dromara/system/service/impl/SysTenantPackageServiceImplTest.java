package org.dromara.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.BaseUnitTest;
import org.dromara.system.domain.SysTenantPackage;
import org.dromara.system.domain.bo.SysTenantPackageBo;
import org.dromara.system.domain.vo.SysTenantPackageVo;
import org.dromara.system.mapper.SysTenantMapper;
import org.dromara.system.mapper.SysTenantPackageMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * SysTenantPackageServiceImpl 单元测试
 *
 * @author Test Team
 */
@SuppressWarnings("unchecked")
@DisplayName("SysTenantPackageServiceImpl 单元测试")
class SysTenantPackageServiceImplTest extends BaseUnitTest {

  @Mock private SysTenantPackageMapper baseMapper;

  @Mock private SysTenantMapper tenantMapper;

  @InjectMocks private SysTenantPackageServiceImpl packageService;

  @BeforeAll
  static void initMybatisPlusTableInfo() {
    MybatisConfiguration configuration = new MybatisConfiguration();
    configuration.setMapUnderscoreToCamelCase(true);
    MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
    TableInfoHelper.initTableInfo(assistant, SysTenantPackage.class);
  }

  @Nested
  @DisplayName("1. 查询方法测试")
  class QueryTests {

    @Test
    @DisplayName("应该根据ID查询租户套餐")
    void shouldQueryById() {
      SysTenantPackageVo vo = new SysTenantPackageVo();
      vo.setPackageId(1L);
      vo.setPackageName("基础套餐");
      when(baseMapper.selectVoById(1L)).thenReturn(vo);

      SysTenantPackageVo result = packageService.queryById(1L);

      assertThat(result).isNotNull();
      assertThat(result.getPackageId()).isEqualTo(1L);
      verify(baseMapper).selectVoById(1L);
    }

    @Test
    @DisplayName("应该分页查询租户套餐列表")
    void shouldQueryPageList() {
      SysTenantPackageBo bo = new SysTenantPackageBo();
      PageQuery pageQuery = new PageQuery();
      pageQuery.setPageNum(1);
      pageQuery.setPageSize(10);

      Page<SysTenantPackageVo> page = new Page<>(1, 10);
      SysTenantPackageVo vo = new SysTenantPackageVo();
      vo.setPackageId(1L);
      page.setRecords(Collections.singletonList(vo));

      when(baseMapper.selectVoPage(any(Page.class), any(LambdaQueryWrapper.class)))
          .thenReturn(page);

      TableDataInfo<SysTenantPackageVo> result = packageService.queryPageList(bo, pageQuery);

      assertThat(result).isNotNull();
      assertThat(result.getRows()).hasSize(1);
    }

    @Test
    @DisplayName("应该查询所有正常状态的租户套餐")
    void shouldSelectList() {
      SysTenantPackageVo vo1 = new SysTenantPackageVo();
      vo1.setPackageId(1L);
      SysTenantPackageVo vo2 = new SysTenantPackageVo();
      vo2.setPackageId(2L);

      when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
          .thenReturn(Arrays.asList(vo1, vo2));

      List<SysTenantPackageVo> result = packageService.selectList();

      assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("应该根据条件查询租户套餐列表")
    void shouldQueryList() {
      SysTenantPackageBo bo = new SysTenantPackageBo();
      bo.setPackageName("基础");

      SysTenantPackageVo vo = new SysTenantPackageVo();
      vo.setPackageName("基础套餐");

      when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
          .thenReturn(Collections.singletonList(vo));

      List<SysTenantPackageVo> result = packageService.queryList(bo);

      assertThat(result).hasSize(1);
    }
  }
}
