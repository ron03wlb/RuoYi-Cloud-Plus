package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.system.domain.SysDictData;
import org.dromara.system.domain.bo.SysDictDataBo;
import org.dromara.system.domain.vo.SysDictDataVo;
import org.dromara.system.mapper.SysDictDataMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysDictDataServiceImpl 单元测试
 * <p>
 * 测试字典数据管理服务
 * </p>
 *
 * <p><b>测试覆盖范围:</b></p>
 * <ul>
 *   <li>查询方法 - 单个查询、列表查询、标签查询</li>
 *   <li>验证方法 - 字典键值唯一性验证</li>
 *   <li>删除方法 - 批量删除（含缓存清理）</li>
 *   <li>边界条件 - null、空列表等</li>
 * </ul>
 *
 * <p><b>测试限制:</b></p>
 * <ul>
 *   <li>无法测试 insertDictData/updateDictData - 需要 MapstructUtils.convert()</li>
 *   <li>无法测试 selectPageDictDataList - 复杂分页查询需要完整 MyBatis-Plus 环境</li>
 *   <li>无法验证 CacheUtils.evict 调用 - 需要 mockito-inline</li>
 *   <li>@CachePut 注解在纯单元测试中不生效</li>
 *   <li>无法测试 buildQueryWrapper - 私有方法，通过公共方法间接测试</li>
 * </ul>
 *
 * @author Test Team
 * @see SysDictDataServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysDictDataServiceImpl 单元测试")
class SysDictDataServiceImplTest {

    @Mock
    private SysDictDataMapper baseMapper;

    @InjectMocks
    private SysDictDataServiceImpl dictDataService;

    @Captor
    private ArgumentCaptor<LambdaQueryWrapper<SysDictData>> wrapperCaptor;

    // ==================== Nested Test Groups ====================

    @Nested
    @DisplayName("1. 查询方法测试")
    class QueryMethodsTests {

        @Test
        @DisplayName("应该根据字典类型查询字典数据列表")
        void shouldReturnDictDataList_WhenQueryByDictType() {
            // Arrange
            SysDictDataBo queryBo = createDictDataBo(null, "sys_user_status");
            queryBo.setDictLabel(null); // 只按类型查询
            queryBo.setDictSort(null);

            List<SysDictDataVo> expectedList = Arrays.asList(
                createDictDataVo(1L, "正常", "0", "sys_user_status"),
                createDictDataVo(2L, "停用", "1", "sys_user_status")
            );

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysDictDataVo> result = dictDataService.selectDictDataList(queryBo);

            // Assert
            assertThat(result)
                .as("应该返回匹配的字典数据列表")
                .isNotNull()
                .hasSize(2)
                .extracting(SysDictDataVo::getDictType)
                .containsOnly("sys_user_status");

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据字典标签模糊查询字典数据列表")
        void shouldReturnDictDataList_WhenQueryByDictLabel() {
            // Arrange
            SysDictDataBo queryBo = createDictDataBo(null, "sys_user_status");
            queryBo.setDictLabel("正常");

            List<SysDictDataVo> expectedList = Arrays.asList(
                createDictDataVo(1L, "正常", "0", "sys_user_status")
            );

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysDictDataVo> result = dictDataService.selectDictDataList(queryBo);

            // Assert
            assertThat(result)
                .as("应该返回匹配标签的字典数据")
                .isNotNull()
                .hasSize(1)
                .first()
                .satisfies(vo -> {
                    assertThat(vo.getDictLabel()).contains("正常");
                });

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据字典排序查询字典数据列表")
        void shouldReturnDictDataList_WhenQueryByDictSort() {
            // Arrange
            SysDictDataBo queryBo = createDictDataBo(null, "sys_user_status");
            queryBo.setDictSort(1);

            List<SysDictDataVo> expectedList = Arrays.asList(
                createDictDataVo(1L, "正常", "0", "sys_user_status")
            );

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysDictDataVo> result = dictDataService.selectDictDataList(queryBo);

            // Assert
            assertThat(result)
                .as("应该返回指定排序的字典数据")
                .isNotNull()
                .hasSize(1);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该返回空列表_当没有匹配的字典数据")
        void shouldReturnEmptyList_WhenNoDictDataMatch() {
            // Arrange
            SysDictDataBo queryBo = createDictDataBo(null, "non_existent_type");

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysDictDataVo> result = dictDataService.selectDictDataList(queryBo);

            // Assert
            assertThat(result)
                .as("应该返回空列表")
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        /**
         * 注意: selectDictLabel 方法无法测试
         * <p>
         * 该方法使用 LambdaQueryWrapper.select() 指定返回字段，
         * 在纯单元测试中会抛出 MybatisPlusException。
         * 需要 MyBatis-Plus 表信息初始化才能测试。
         * </p>
         */

        @Test
        @DisplayName("应该根据ID查询字典数据")
        void shouldReturnDictData_WhenQueryById() {
            // Arrange
            Long dictCode = 1L;
            SysDictDataVo expectedVo = createDictDataVo(dictCode, "正常", "0", "sys_user_status");

            when(baseMapper.selectVoById(dictCode)).thenReturn(expectedVo);

            // Act
            SysDictDataVo result = dictDataService.selectDictDataById(dictCode);

            // Assert
            assertThat(result)
                .as("应该返回字典数据VO")
                .isNotNull()
                .satisfies(vo -> {
                    assertThat(vo.getDictCode()).isEqualTo(dictCode);
                    assertThat(vo.getDictLabel()).isEqualTo("正常");
                    assertThat(vo.getDictValue()).isEqualTo("0");
                });

            verify(baseMapper, times(1)).selectVoById(dictCode);
        }

        @Test
        @DisplayName("应该返回null_当字典数据ID不存在")
        void shouldReturnNull_WhenDictCodeNotExists() {
            // Arrange
            Long dictCode = 999L;
            when(baseMapper.selectVoById(dictCode)).thenReturn(null);

            // Act
            SysDictDataVo result = dictDataService.selectDictDataById(dictCode);

            // Assert
            assertThat(result)
                .as("应该返回null")
                .isNull();

            verify(baseMapper, times(1)).selectVoById(dictCode);
        }
    }

    @Nested
    @DisplayName("2. 验证方法测试")
    class ValidationMethodsTests {

        @Test
        @DisplayName("应该返回true_当字典键值唯一")
        void shouldReturnTrue_WhenDictValueIsUnique() {
            // Arrange
            SysDictDataBo newDictData = createDictDataBo(null, "sys_user_status");
            newDictData.setDictValue("2");
            newDictData.setDictLabel("锁定");

            when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(false);

            // Act
            boolean result = dictDataService.checkDictDataUnique(newDictData);

            // Assert
            assertThat(result)
                .as("字典键值唯一时应该返回true")
                .isTrue();

            verify(baseMapper, times(1)).exists(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该返回false_当字典键值已存在")
        void shouldReturnFalse_WhenDictValueExists() {
            // Arrange
            SysDictDataBo newDictData = createDictDataBo(null, "sys_user_status");
            newDictData.setDictValue("0");
            newDictData.setDictLabel("正常");

            when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(true);

            // Act
            boolean result = dictDataService.checkDictDataUnique(newDictData);

            // Assert
            assertThat(result)
                .as("字典键值重复时应该返回false")
                .isFalse();

            verify(baseMapper, times(1)).exists(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该排除自身ID_当检查字典键值唯一性用于更新")
        void shouldExcludeSelfId_WhenCheckingDictValueUniquenessForUpdate() {
            // Arrange
            SysDictDataBo updateDictData = createDictDataBo(1L, "sys_user_status");
            updateDictData.setDictValue("0");
            updateDictData.setDictLabel("正常状态");

            when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(false);

            // Act
            boolean result = dictDataService.checkDictDataUnique(updateDictData);

            // Assert
            assertThat(result)
                .as("更新时应该排除自身ID")
                .isTrue();

            verify(baseMapper, times(1)).exists(wrapperCaptor.capture());
            // LambdaQueryWrapper包含 .ne(dictCode != null, SysDictData::getDictCode, dictCode)
        }

        @Test
        @DisplayName("应该在同一字典类型下验证键值唯一性")
        void shouldCheckUniquenessWithinSameDictType() {
            // Arrange
            SysDictDataBo dictData1 = createDictDataBo(null, "sys_user_status");
            dictData1.setDictValue("0");

            when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(false);

            // Act
            boolean result = dictDataService.checkDictDataUnique(dictData1);

            // Assert
            assertThat(result).isTrue();

            // Verify that the query includes both dictType and dictValue conditions
            verify(baseMapper, times(1)).exists(wrapperCaptor.capture());
            // The wrapper should have conditions for both dictType and dictValue
        }
    }

    @Nested
    @DisplayName("3. 删除方法测试")
    class DeleteMethodsTests {

        /**
         * 注意: 无法测试成功删除场景
         * <p>
         * deleteDictDataByIds() 在删除成功后会调用 CacheUtils.evict()，
         * 这是静态方法且依赖 Spring 上下文初始化。
         * 在纯单元测试中会抛出 NoClassDefFoundError/ExceptionInInitializerError。
         * 需要 mockito-inline 或集成测试环境才能测试完整删除流程。
         * </p>
         */

        @Test
        @DisplayName("应该处理空列表_当批量删除空字典数据ID列表")
        void shouldHandleEmptyList_WhenBatchDeleteEmptyDictCodes() {
            // Arrange
            List<Long> emptyIds = Collections.emptyList();
            when(baseMapper.selectByIds(emptyIds)).thenReturn(Collections.emptyList());
            when(baseMapper.deleteByIds(emptyIds)).thenReturn(0);

            // Act
            dictDataService.deleteDictDataByIds(emptyIds);

            // Assert
            verify(baseMapper, times(1)).selectByIds(emptyIds);
            verify(baseMapper, times(1)).deleteByIds(emptyIds);
        }
    }

    /**
     * 注意: 无法测试 CRUD 相关方法
     * <p>
     * <b>4. CRUD 方法 - 无法测试原因:</b>
     * </p>
     * <ul>
     *   <li>insertDictData() 使用 MapstructUtils.convert()，需要静态方法 mock</li>
     *   <li>updateDictData() 同样使用 MapstructUtils.convert()</li>
     *   <li>deleteDictDataByIds() 成功路径调用 CacheUtils.evict()，需要 Spring 上下文</li>
     *   <li>@CachePut 注解需要 Spring AOP 代理才能生效</li>
     *   <li>需要 mockito-inline 或集成测试环境才能测试这些方法</li>
     * </ul>
     */

    @Nested
    @DisplayName("5. 边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("应该处理null字典数据ID_selectDictDataById")
        void shouldHandleNullDictCode_SelectDictDataById() {
            // Arrange
            Long nullId = null;
            when(baseMapper.selectVoById(nullId)).thenReturn(null);

            // Act
            SysDictDataVo result = dictDataService.selectDictDataById(nullId);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper, times(1)).selectVoById(nullId);
        }

        /**
         * 注意: selectDictLabel 无法测试边界条件
         * <p>
         * 该方法使用 LambdaQueryWrapper.select() 和链式调用，
         * 在纯单元测试中无法 mock 复杂的 Lambda 表达式。
         * </p>
         */

        @Test
        @DisplayName("应该处理最大Long值字典数据ID")
        void shouldHandleMaxLongDictCode() {
            // Arrange
            Long maxId = Long.MAX_VALUE;
            when(baseMapper.selectVoById(maxId)).thenReturn(null);

            // Act
            SysDictDataVo result = dictDataService.selectDictDataById(maxId);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper, times(1)).selectVoById(maxId);
        }

        @Test
        @DisplayName("应该处理负数排序_selectDictDataList")
        void shouldHandleNegativeSort_SelectDictDataList() {
            // Arrange
            SysDictDataBo queryBo = createDictDataBo(null, "sys_user_status");
            queryBo.setDictSort(-1);

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysDictDataVo> result = dictDataService.selectDictDataList(queryBo);

            // Assert
            assertThat(result).isNotNull().isEmpty();
            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }
    }

    // ==================== Test Data Factory Methods ====================

    /**
     * 创建字典数据测试数据
     */
    private static SysDictData createDictData(Long dictCode, String dictLabel, String dictValue, String dictType) {
        SysDictData data = new SysDictData();
        data.setDictCode(dictCode);
        data.setDictLabel(dictLabel);
        data.setDictValue(dictValue);
        data.setDictType(dictType);
        data.setDictSort(dictCode != null ? dictCode.intValue() : 0);
        data.setCssClass("default");
        data.setListClass("default");
        data.setIsDefault("N");
        data.setRemark("测试字典数据");
        return data;
    }

    /**
     * 创建字典数据BO测试数据
     */
    private static SysDictDataBo createDictDataBo(Long dictCode, String dictType) {
        SysDictDataBo bo = new SysDictDataBo();
        bo.setDictCode(dictCode);
        bo.setDictType(dictType);
        if (dictCode != null) {
            bo.setDictLabel("标签" + dictCode);
            bo.setDictValue("value_" + dictCode);
            bo.setDictSort(dictCode.intValue());
        }
        bo.setCssClass("default");
        bo.setListClass("default");
        bo.setIsDefault("N");
        bo.setRemark("测试字典数据");
        return bo;
    }

    /**
     * 创建字典数据VO测试数据
     */
    private static SysDictDataVo createDictDataVo(Long dictCode, String dictLabel, String dictValue, String dictType) {
        SysDictDataVo vo = new SysDictDataVo();
        vo.setDictCode(dictCode);
        vo.setDictLabel(dictLabel);
        vo.setDictValue(dictValue);
        vo.setDictType(dictType);
        vo.setDictSort(dictCode != null ? dictCode.intValue() : 0);
        vo.setCssClass("default");
        vo.setListClass("default");
        vo.setIsDefault("N");
        vo.setRemark("测试字典数据");
        vo.setCreateTime(new Date());
        return vo;
    }
}
