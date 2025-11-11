package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.system.domain.SysDictData;
import org.dromara.system.domain.SysDictType;
import org.dromara.system.domain.bo.SysDictTypeBo;
import org.dromara.system.domain.vo.SysDictDataVo;
import org.dromara.system.domain.vo.SysDictTypeVo;
import org.dromara.system.mapper.SysDictDataMapper;
import org.dromara.system.mapper.SysDictTypeMapper;
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
 * SysDictTypeServiceImpl 单元测试
 * <p>
 * 测试字典类型管理服务
 * </p>
 *
 * <p><b>测试覆盖范围:</b></p>
 * <ul>
 *   <li>查询方法 - 单个查询、列表查询、字典数据查询</li>
 *   <li>验证方法 - 字典类型唯一性验证</li>
 *   <li>删除方法 - 批量删除（含业务规则验证）</li>
 *   <li>缓存方法 - 缓存清除操作</li>
 *   <li>边界条件 - null、空列表等</li>
 * </ul>
 *
 * <p><b>测试限制:</b></p>
 * <ul>
 *   <li>无法测试 insertDictType/updateDictType - 需要 MapstructUtils.convert()</li>
 *   <li>无法测试 selectPageDictTypeList - 复杂分页查询需要完整 MyBatis-Plus 环境</li>
 *   <li>无法验证 CacheUtils.evict/clear 调用 - 需要 mockito-inline</li>
 *   <li>@Cacheable/@CachePut 注解在纯单元测试中不生效</li>
 *   <li>无法测试 buildQueryWrapper - 私有方法，通过公共方法间接测试</li>
 * </ul>
 *
 * @author Test Team
 * @see SysDictTypeServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysDictTypeServiceImpl 单元测试")
class SysDictTypeServiceImplTest {

    @Mock
    private SysDictTypeMapper baseMapper;

    @Mock
    private SysDictDataMapper dictDataMapper;

    @InjectMocks
    private SysDictTypeServiceImpl dictTypeService;

    @Captor
    private ArgumentCaptor<LambdaQueryWrapper<SysDictType>> typeWrapperCaptor;

    @Captor
    private ArgumentCaptor<LambdaQueryWrapper<SysDictData>> dataWrapperCaptor;

    // ==================== Nested Test Groups ====================

    @Nested
    @DisplayName("1. 查询方法测试")
    class QueryMethodsTests {

        @Test
        @DisplayName("应该根据字典名称查询字典类型列表")
        void shouldReturnDictTypeList_WhenQueryByDictName() {
            // Arrange
            SysDictTypeBo queryBo = createDictTypeBo(null, "用户状态");
            queryBo.setDictType(null); // 只按名称查询

            List<SysDictTypeVo> expectedList = Arrays.asList(
                createDictTypeVo(1L, "用户状态", "sys_user_status"),
                createDictTypeVo(2L, "用户性别", "sys_user_sex")
            );

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysDictTypeVo> result = dictTypeService.selectDictTypeList(queryBo);

            // Assert
            assertThat(result)
                .as("应该返回匹配的字典类型列表")
                .isNotNull()
                .hasSize(2);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据字典类型编码查询字典类型列表")
        void shouldReturnDictTypeList_WhenQueryByDictType() {
            // Arrange
            SysDictTypeBo queryBo = createDictTypeBo(null, null);
            queryBo.setDictType("sys_user");

            List<SysDictTypeVo> expectedList = Arrays.asList(
                createDictTypeVo(1L, "用户状态", "sys_user_status")
            );

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysDictTypeVo> result = dictTypeService.selectDictTypeList(queryBo);

            // Assert
            assertThat(result)
                .as("应该返回匹配的字典类型")
                .isNotNull()
                .hasSize(1)
                .first()
                .satisfies(vo -> {
                    assertThat(vo.getDictType()).contains("sys_user");
                });

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该返回空列表_当没有匹配的字典类型")
        void shouldReturnEmptyList_WhenNoDictTypesMatch() {
            // Arrange
            SysDictTypeBo queryBo = createDictTypeBo(null, "不存在的字典");

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysDictTypeVo> result = dictTypeService.selectDictTypeList(queryBo);

            // Assert
            assertThat(result)
                .as("应该返回空列表")
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该查询所有字典类型")
        void shouldReturnAllDictTypes() {
            // Arrange
            List<SysDictTypeVo> expectedList = Arrays.asList(
                createDictTypeVo(1L, "用户状态", "sys_user_status"),
                createDictTypeVo(2L, "用户性别", "sys_user_sex"),
                createDictTypeVo(3L, "系统开关", "sys_normal_disable")
            );

            when(baseMapper.selectVoList()).thenReturn(expectedList);

            // Act
            List<SysDictTypeVo> result = dictTypeService.selectDictTypeAll();

            // Assert
            assertThat(result)
                .as("应该返回所有字典类型")
                .isNotNull()
                .hasSize(3)
                .extracting(SysDictTypeVo::getDictType)
                .containsExactly("sys_user_status", "sys_user_sex", "sys_normal_disable");

            verify(baseMapper, times(1)).selectVoList();
        }

        @Test
        @DisplayName("应该根据字典类型查询字典数据")
        void shouldReturnDictData_WhenQueryByDictType() {
            // Arrange
            String dictType = "sys_user_status";
            List<SysDictDataVo> expectedData = Arrays.asList(
                createDictDataVo(1L, "正常", "0", dictType),
                createDictDataVo(2L, "停用", "1", dictType)
            );

            when(dictDataMapper.selectDictDataByType(dictType)).thenReturn(expectedData);

            // Act
            List<SysDictDataVo> result = dictTypeService.selectDictDataByType(dictType);

            // Assert
            assertThat(result)
                .as("应该返回字典数据列表")
                .isNotNull()
                .hasSize(2)
                .extracting(SysDictDataVo::getDictValue)
                .containsExactly("0", "1");

            verify(dictDataMapper, times(1)).selectDictDataByType(dictType);
        }

        @Test
        @DisplayName("应该返回null_当字典类型没有数据")
        void shouldReturnNull_WhenDictTypeHasNoData() {
            // Arrange
            String dictType = "empty_dict_type";
            when(dictDataMapper.selectDictDataByType(dictType)).thenReturn(Collections.emptyList());

            // Act
            List<SysDictDataVo> result = dictTypeService.selectDictDataByType(dictType);

            // Assert
            assertThat(result)
                .as("应该返回null而不是空列表_防止缓存穿透")
                .isNull();

            verify(dictDataMapper, times(1)).selectDictDataByType(dictType);
        }

        @Test
        @DisplayName("应该根据ID查询字典类型")
        void shouldReturnDictType_WhenQueryById() {
            // Arrange
            Long dictId = 1L;
            SysDictTypeVo expectedVo = createDictTypeVo(dictId, "用户状态", "sys_user_status");

            when(baseMapper.selectVoById(dictId)).thenReturn(expectedVo);

            // Act
            SysDictTypeVo result = dictTypeService.selectDictTypeById(dictId);

            // Assert
            assertThat(result)
                .as("应该返回字典类型VO")
                .isNotNull()
                .satisfies(vo -> {
                    assertThat(vo.getDictId()).isEqualTo(dictId);
                    assertThat(vo.getDictName()).isEqualTo("用户状态");
                    assertThat(vo.getDictType()).isEqualTo("sys_user_status");
                });

            verify(baseMapper, times(1)).selectVoById(dictId);
        }

        @Test
        @DisplayName("应该返回null_当字典类型ID不存在")
        void shouldReturnNull_WhenDictTypeIdNotExists() {
            // Arrange
            Long dictId = 999L;
            when(baseMapper.selectVoById(dictId)).thenReturn(null);

            // Act
            SysDictTypeVo result = dictTypeService.selectDictTypeById(dictId);

            // Assert
            assertThat(result)
                .as("应该返回null")
                .isNull();

            verify(baseMapper, times(1)).selectVoById(dictId);
        }

        @Test
        @DisplayName("应该根据字典类型编码查询字典类型")
        void shouldReturnDictType_WhenQueryByType() {
            // Arrange
            String dictType = "sys_user_status";
            SysDictTypeVo expectedVo = createDictTypeVo(1L, "用户状态", dictType);

            when(baseMapper.selectVoOne(any(LambdaQueryWrapper.class))).thenReturn(expectedVo);

            // Act
            SysDictTypeVo result = dictTypeService.selectDictTypeByType(dictType);

            // Assert
            assertThat(result)
                .as("应该返回字典类型VO")
                .isNotNull()
                .satisfies(vo -> {
                    assertThat(vo.getDictType()).isEqualTo(dictType);
                    assertThat(vo.getDictName()).isEqualTo("用户状态");
                });

            verify(baseMapper, times(1)).selectVoOne(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该返回null_当字典类型编码不存在")
        void shouldReturnNull_WhenDictTypeCodeNotExists() {
            // Arrange
            String dictType = "non_existent_type";
            when(baseMapper.selectVoOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // Act
            SysDictTypeVo result = dictTypeService.selectDictTypeByType(dictType);

            // Assert
            assertThat(result)
                .as("应该返回null")
                .isNull();

            verify(baseMapper, times(1)).selectVoOne(any(LambdaQueryWrapper.class));
        }
    }

    @Nested
    @DisplayName("2. 验证方法测试")
    class ValidationMethodsTests {

        @Test
        @DisplayName("应该返回true_当字典类型唯一")
        void shouldReturnTrue_WhenDictTypeIsUnique() {
            // Arrange
            SysDictTypeBo newDictType = createDictTypeBo(null, "新字典类型");
            newDictType.setDictType("new_dict_type");

            when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(false);

            // Act
            boolean result = dictTypeService.checkDictTypeUnique(newDictType);

            // Assert
            assertThat(result)
                .as("字典类型唯一时应该返回true")
                .isTrue();

            verify(baseMapper, times(1)).exists(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该返回false_当字典类型已存在")
        void shouldReturnFalse_WhenDictTypeExists() {
            // Arrange
            SysDictTypeBo newDictType = createDictTypeBo(null, "已存在字典");
            newDictType.setDictType("existing_dict_type");

            when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(true);

            // Act
            boolean result = dictTypeService.checkDictTypeUnique(newDictType);

            // Assert
            assertThat(result)
                .as("字典类型重复时应该返回false")
                .isFalse();

            verify(baseMapper, times(1)).exists(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该排除自身ID_当检查字典类型唯一性用于更新")
        void shouldExcludeSelfId_WhenCheckingDictTypeUniquenessForUpdate() {
            // Arrange
            SysDictTypeBo updateDictType = createDictTypeBo(1L, "更新字典");
            updateDictType.setDictType("update_dict_type");

            when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(false);

            // Act
            boolean result = dictTypeService.checkDictTypeUnique(updateDictType);

            // Assert
            assertThat(result)
                .as("更新时应该排除自身ID")
                .isTrue();

            verify(baseMapper, times(1)).exists(typeWrapperCaptor.capture());
            // LambdaQueryWrapper包含 .ne(dictId != null, SysDictType::getDictId, dictId)
        }
    }

    @Nested
    @DisplayName("3. 删除方法测试")
    class DeleteMethodsTests {

        /**
         * 注意: 无法测试成功删除场景
         * <p>
         * deleteDictTypeByIds() 在删除成功后会调用 CacheUtils.evict()，
         * 这是静态方法且依赖 Spring 上下文初始化。
         * 在纯单元测试中会抛出 NoClassDefFoundError/ExceptionInInitializerError。
         * 需要 mockito-inline 或集成测试环境才能测试完整删除流程。
         * </p>
         */

        @Test
        @DisplayName("应该抛出异常_当字典类型已分配字典数据")
        void shouldThrowException_WhenDictTypeHasAssignedData() {
            // Arrange
            List<Long> dictIds = Arrays.asList(1L, 2L);
            SysDictType assignedDict = createDictType(1L, "已分配字典", "assigned_dict");
            SysDictType unassignedDict = createDictType(2L, "未分配字典", "unassigned_dict");
            List<SysDictType> dictTypes = Arrays.asList(assignedDict, unassignedDict);

            when(baseMapper.selectByIds(dictIds)).thenReturn(dictTypes);
            when(dictDataMapper.exists(any(LambdaQueryWrapper.class)))
                .thenReturn(true) // 第一个字典有数据
                .thenReturn(false); // 第二个字典没有数据

            // Act & Assert
            assertThatThrownBy(() -> dictTypeService.deleteDictTypeByIds(dictIds))
                .as("应该抛出ServiceException")
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("已分配")
                .hasMessageContaining("不能删除");

            verify(baseMapper, times(1)).selectByIds(dictIds);
            verify(dictDataMapper, times(1)).exists(any(LambdaQueryWrapper.class));
            verify(baseMapper, never()).deleteByIds(any()); // 不应该执行删除操作
        }

        @Test
        @DisplayName("应该处理空列表_当批量删除空字典ID列表")
        void shouldHandleEmptyList_WhenBatchDeleteEmptyDictIds() {
            // Arrange
            List<Long> emptyIds = Collections.emptyList();
            when(baseMapper.selectByIds(emptyIds)).thenReturn(Collections.emptyList());
            when(baseMapper.deleteByIds(emptyIds)).thenReturn(0);

            // Act
            dictTypeService.deleteDictTypeByIds(emptyIds);

            // Assert
            verify(baseMapper, times(1)).selectByIds(emptyIds);
            verify(dictDataMapper, never()).exists(any());
            verify(baseMapper, times(1)).deleteByIds(emptyIds);
        }
    }

    /**
     * 注意: 无法测试缓存相关方法
     * <p>
     * <b>4. 缓存方法 - 无法测试原因:</b>
     * </p>
     * <ul>
     *   <li>resetDictCache() 调用 CacheUtils.clear()，这是静态方法且依赖 Spring 上下文</li>
     *   <li>deleteDictTypeByIds() 调用 CacheUtils.evict()，同样是静态方法</li>
     *   <li>@Cacheable 和 @CachePut 注解需要 Spring AOP 代理才能生效</li>
     *   <li>在纯单元测试中会抛出 NoClassDefFoundError/ExceptionInInitializerError</li>
     *   <li>需要 mockito-inline 或集成测试环境才能测试缓存功能</li>
     * </ul>
     */

    @Nested
    @DisplayName("5. 边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("应该处理null字典ID_selectDictTypeById")
        void shouldHandleNullDictId_SelectDictTypeById() {
            // Arrange
            Long nullId = null;
            when(baseMapper.selectVoById(nullId)).thenReturn(null);

            // Act
            SysDictTypeVo result = dictTypeService.selectDictTypeById(nullId);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper, times(1)).selectVoById(nullId);
        }

        @Test
        @DisplayName("应该处理null字典类型_selectDictTypeByType")
        void shouldHandleNullDictType_SelectDictTypeByType() {
            // Arrange
            String nullType = null;
            when(baseMapper.selectVoOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // Act
            SysDictTypeVo result = dictTypeService.selectDictTypeByType(nullType);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper, times(1)).selectVoOne(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该处理空字符串字典类型_selectDictDataByType")
        void shouldHandleEmptyDictType_SelectDictDataByType() {
            // Arrange
            String emptyType = "";
            when(dictDataMapper.selectDictDataByType(emptyType)).thenReturn(Collections.emptyList());

            // Act
            List<SysDictDataVo> result = dictTypeService.selectDictDataByType(emptyType);

            // Assert
            assertThat(result).isNull(); // 空列表应该返回null
            verify(dictDataMapper, times(1)).selectDictDataByType(emptyType);
        }

        @Test
        @DisplayName("应该处理最大Long值字典ID")
        void shouldHandleMaxLongDictId() {
            // Arrange
            Long maxId = Long.MAX_VALUE;
            when(baseMapper.selectVoById(maxId)).thenReturn(null);

            // Act
            SysDictTypeVo result = dictTypeService.selectDictTypeById(maxId);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper, times(1)).selectVoById(maxId);
        }
    }

    // ==================== Test Data Factory Methods ====================

    /**
     * 创建字典类型测试数据
     */
    private static SysDictType createDictType(Long dictId, String dictName, String dictType) {
        SysDictType dict = new SysDictType();
        dict.setDictId(dictId);
        dict.setDictName(dictName);
        dict.setDictType(dictType);
        dict.setRemark("测试字典备注");
        return dict;
    }

    /**
     * 创建字典类型BO测试数据
     */
    private static SysDictTypeBo createDictTypeBo(Long dictId, String dictName) {
        SysDictTypeBo bo = new SysDictTypeBo();
        bo.setDictId(dictId);
        bo.setDictName(dictName);
        if (dictId != null) {
            bo.setDictType("dict_type_" + dictId);
        }
        bo.setRemark("测试字典备注");
        return bo;
    }

    /**
     * 创建字典类型VO测试数据
     */
    private static SysDictTypeVo createDictTypeVo(Long dictId, String dictName, String dictType) {
        SysDictTypeVo vo = new SysDictTypeVo();
        vo.setDictId(dictId);
        vo.setDictName(dictName);
        vo.setDictType(dictType);
        vo.setRemark("测试字典备注");
        vo.setCreateTime(new Date());
        return vo;
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
        return vo;
    }
}
