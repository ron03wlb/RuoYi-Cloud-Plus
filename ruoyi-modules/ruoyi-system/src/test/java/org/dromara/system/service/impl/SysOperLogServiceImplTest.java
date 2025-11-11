package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.system.domain.SysOperLog;
import org.dromara.system.domain.bo.SysOperLogBo;
import org.dromara.system.domain.vo.SysOperLogVo;
import org.dromara.system.mapper.SysOperLogMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysOperLogServiceImpl 单元测试
 *
 * @author Claude Code
 * @date 2025-11-07
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysOperLogServiceImpl 单元测试")
class SysOperLogServiceImplTest {

    @Mock
    private SysOperLogMapper baseMapper;

    @InjectMocks
    private SysOperLogServiceImpl operLogService;

    @Captor
    private ArgumentCaptor<LambdaQueryWrapper<SysOperLog>> wrapperCaptor;

    @Nested
    @DisplayName("1. 查询方法测试")
    class QueryMethodsTests {

        @Test
        @DisplayName("应该根据操作人员查询操作日志列表")
        void shouldReturnOperLogList_WhenQueryByOperName() {
            // Arrange
            SysOperLogBo queryBo = createOperLogBo(null, "系统管理员");
            queryBo.setOperName("系统管理员");

            List<SysOperLogVo> expectedList = Arrays.asList(
                createOperLogVo(1L, "新增用户", 0, "系统管理员"),
                createOperLogVo(2L, "修改用户", 0, "系统管理员")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysOperLogVo> result = operLogService.selectOperLogList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(SysOperLogVo::getOperName)
                .containsOnly("系统管理员");

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据操作模块标题查询操作日志列表")
        void shouldReturnOperLogList_WhenQueryByTitle() {
            // Arrange
            SysOperLogBo queryBo = createOperLogBo(null, "");
            queryBo.setTitle("用户管理");

            List<SysOperLogVo> expectedList = Collections.singletonList(
                createOperLogVo(1L, "用户管理", 0, "admin")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysOperLogVo> result = operLogService.selectOperLogList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(1);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据业务类型查询操作日志列表")
        void shouldReturnOperLogList_WhenQueryByBusinessType() {
            // Arrange
            SysOperLogBo queryBo = createOperLogBo(null, "");
            queryBo.setBusinessType(1); // 新增

            List<SysOperLogVo> expectedList = Arrays.asList(
                createOperLogVo(1L, "新增用户", 0, "admin"),
                createOperLogVo(2L, "新增角色", 0, "admin")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysOperLogVo> result = operLogService.selectOperLogList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(2);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据业务类型数组查询操作日志列表")
        void shouldReturnOperLogList_WhenQueryByBusinessTypes() {
            // Arrange
            SysOperLogBo queryBo = createOperLogBo(null, "");
            queryBo.setBusinessTypes(new Integer[]{1, 2}); // 新增和修改

            List<SysOperLogVo> expectedList = Arrays.asList(
                createOperLogVo(1L, "新增用户", 0, "admin"),
                createOperLogVo(2L, "修改用户", 0, "admin")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysOperLogVo> result = operLogService.selectOperLogList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(2);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据操作状态查询操作日志列表")
        void shouldReturnOperLogList_WhenQueryByStatus() {
            // Arrange
            SysOperLogBo queryBo = createOperLogBo(null, "");
            queryBo.setStatus(0); // 成功

            List<SysOperLogVo> expectedList = Arrays.asList(
                createOperLogVo(1L, "新增用户", 0, "admin"),
                createOperLogVo(2L, "修改用户", 0, "admin")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysOperLogVo> result = operLogService.selectOperLogList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(SysOperLogVo::getStatus)
                .containsOnly(0);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据时间范围查询操作日志列表")
        void shouldReturnOperLogList_WhenQueryByTimeRange() {
            // Arrange
            SysOperLogBo queryBo = createOperLogBo(null, "");
            Map<String, Object> params = new HashMap<>();
            params.put("beginTime", new Date());
            params.put("endTime", new Date());
            queryBo.setParams(params);

            List<SysOperLogVo> expectedList = Collections.singletonList(
                createOperLogVo(1L, "新增用户", 0, "admin")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysOperLogVo> result = operLogService.selectOperLogList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(1);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据多个条件查询操作日志列表")
        void shouldReturnOperLogList_WhenQueryByMultipleConditions() {
            // Arrange
            SysOperLogBo queryBo = createOperLogBo(null, "admin");
            queryBo.setOperName("admin");
            queryBo.setTitle("用户管理");
            queryBo.setBusinessType(1);
            queryBo.setStatus(0);

            List<SysOperLogVo> expectedList = Collections.singletonList(
                createOperLogVo(1L, "用户管理", 0, "admin")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysOperLogVo> result = operLogService.selectOperLogList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(1);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该返回空列表_当没有操作日志匹配")
        void shouldReturnEmptyList_WhenNoOperLogMatch() {
            // Arrange
            SysOperLogBo queryBo = createOperLogBo(null, "不存在的操作人");
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysOperLogVo> result = operLogService.selectOperLogList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该返回操作日志VO_当根据ID查询存在的操作日志")
        void shouldReturnOperLogVo_WhenQueryById() {
            // Arrange
            Long operId = 1L;
            SysOperLogVo expectedOperLog = createOperLogVo(operId, "新增用户", 0, "admin");

            when(baseMapper.selectVoById(operId)).thenReturn(expectedOperLog);

            // Act
            SysOperLogVo result = operLogService.selectOperLogById(operId);

            // Assert
            assertThat(result)
                .isNotNull()
                .extracting(
                    SysOperLogVo::getOperId,
                    SysOperLogVo::getTitle,
                    SysOperLogVo::getStatus,
                    SysOperLogVo::getOperName
                )
                .containsExactly(operId, "新增用户", 0, "admin");

            verify(baseMapper, times(1)).selectVoById(operId);
        }

        @Test
        @DisplayName("应该返回null_当根据ID查询不存在的操作日志")
        void shouldReturnNull_WhenOperLogIdDoesNotExist() {
            // Arrange
            Long nonExistentId = 999L;
            when(baseMapper.selectVoById(nonExistentId)).thenReturn(null);

            // Act
            SysOperLogVo result = operLogService.selectOperLogById(nonExistentId);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper, times(1)).selectVoById(nonExistentId);
        }
    }

    /**
     * 注意: 无法测试分页方法
     * <p>
     * <b>selectPageOperLogList</b> - 需要 MyBatis-Plus Page 对象和完整分页设置<br>
     * </p>
     */

    @Nested
    @DisplayName("2. 删除方法测试")
    class DeleteMethodsTests {

        @Test
        @DisplayName("应该成功批量删除操作日志_使用单个ID")
        void shouldDeleteOperLogs_WhenBatchDeleteWithSingleId() {
            // Arrange
            Long[] operIds = {1L};
            when(baseMapper.deleteByIds(Arrays.asList(operIds))).thenReturn(1);

            // Act
            int result = operLogService.deleteOperLogByIds(operIds);

            // Assert
            assertThat(result).isEqualTo(1);
            verify(baseMapper, times(1)).deleteByIds(Arrays.asList(operIds));
        }

        @Test
        @DisplayName("应该成功批量删除操作日志_使用多个ID")
        void shouldDeleteOperLogs_WhenBatchDeleteWithMultipleIds() {
            // Arrange
            Long[] operIds = {1L, 2L, 3L};
            when(baseMapper.deleteByIds(Arrays.asList(operIds))).thenReturn(3);

            // Act
            int result = operLogService.deleteOperLogByIds(operIds);

            // Assert
            assertThat(result).isEqualTo(3);
            verify(baseMapper, times(1)).deleteByIds(Arrays.asList(operIds));
        }

        @Test
        @DisplayName("应该返回0_当批量删除空ID数组")
        void shouldReturn0_WhenBatchDeleteWithEmptyArray() {
            // Arrange
            Long[] emptyIds = {};
            when(baseMapper.deleteByIds(Arrays.asList(emptyIds))).thenReturn(0);

            // Act
            int result = operLogService.deleteOperLogByIds(emptyIds);

            // Assert
            assertThat(result).isEqualTo(0);
            verify(baseMapper, times(1)).deleteByIds(Collections.emptyList());
        }

        @Test
        @DisplayName("应该成功清空所有操作日志")
        void shouldCleanAllOperLogs() {
            // Arrange
            when(baseMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(100);

            // Act
            operLogService.cleanOperLog();

            // Assert
            verify(baseMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该调用delete_当清空操作日志时没有数据")
        void shouldCallDelete_WhenCleanOperLogWithNoData() {
            // Arrange
            when(baseMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(0);

            // Act
            operLogService.cleanOperLog();

            // Assert
            verify(baseMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        }
    }

    /**
     * 注意: 无法测试 CRUD 相关方法
     * <p>
     * <b>insertOperlog()</b> - 使用 MapstructUtils.convert()，需要静态方法 mock<br>
     * 需要 mockito-inline 或集成测试环境才能测试这个方法
     * </p>
     */

    @Nested
    @DisplayName("3. 边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("应该处理null操作日志ID_selectOperLogById")
        void shouldHandleNullOperLogId_selectOperLogById() {
            // Arrange
            Long nullOperId = null;
            when(baseMapper.selectVoById(nullOperId)).thenReturn(null);

            // Act
            SysOperLogVo result = operLogService.selectOperLogById(nullOperId);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper, times(1)).selectVoById(nullOperId);
        }

        @Test
        @DisplayName("应该处理最大Long值_selectOperLogById")
        void shouldHandleMaxLongValue_selectOperLogById() {
            // Arrange
            Long maxOperId = Long.MAX_VALUE;
            when(baseMapper.selectVoById(maxOperId)).thenReturn(null);

            // Act
            SysOperLogVo result = operLogService.selectOperLogById(maxOperId);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper, times(1)).selectVoById(maxOperId);
        }

        @Test
        @DisplayName("应该处理空字符串操作人员名")
        void shouldHandleEmptyStringOperName() {
            // Arrange
            SysOperLogBo queryBo = createOperLogBo(null, "");
            queryBo.setOperName("");
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysOperLogVo> result = operLogService.selectOperLogList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该处理空字符串标题")
        void shouldHandleEmptyStringTitle() {
            // Arrange
            SysOperLogBo queryBo = createOperLogBo(null, "");
            queryBo.setTitle("");
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysOperLogVo> result = operLogService.selectOperLogList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该处理空业务类型数组")
        void shouldHandleEmptyBusinessTypesArray() {
            // Arrange
            SysOperLogBo queryBo = createOperLogBo(null, "");
            queryBo.setBusinessTypes(new Integer[]{});
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysOperLogVo> result = operLogService.selectOperLogList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该处理空params_当查询时间范围")
        void shouldHandleEmptyParams_WhenQueryTimeRange() {
            // Arrange
            SysOperLogBo queryBo = createOperLogBo(null, "");
            queryBo.setParams(new HashMap<>()); // 使用空HashMap而不是null
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysOperLogVo> result = operLogService.selectOperLogList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }
    }

    // ==================== Factory Methods ====================

    /**
     * 创建测试用 SysOperLogBo 业务对象
     */
    private static SysOperLogBo createOperLogBo(Long operId, String operName) {
        SysOperLogBo operLogBo = new SysOperLogBo();
        operLogBo.setOperId(operId);
        operLogBo.setOperName(operName);
        operLogBo.setTitle("测试操作");
        operLogBo.setBusinessType(1);
        operLogBo.setMethod("com.test.TestController.test()");
        operLogBo.setRequestMethod("POST");
        operLogBo.setOperatorType(1);
        operLogBo.setOperIp("127.0.0.1");
        operLogBo.setOperLocation("内网IP");
        operLogBo.setOperParam("{}");
        operLogBo.setJsonResult("{\"code\":200}");
        operLogBo.setStatus(0);
        operLogBo.setErrorMsg("");
        operLogBo.setParams(new HashMap<>());
        return operLogBo;
    }

    /**
     * 创建测试用 SysOperLogVo 视图对象
     */
    private static SysOperLogVo createOperLogVo(Long operId, String title, Integer status, String operName) {
        SysOperLogVo operLogVo = new SysOperLogVo();
        operLogVo.setOperId(operId);
        operLogVo.setTenantId("000000");
        operLogVo.setTitle(title);
        operLogVo.setBusinessType(1);
        operLogVo.setMethod("com.test.TestController.test()");
        operLogVo.setRequestMethod("POST");
        operLogVo.setOperatorType(1);
        operLogVo.setOperName(operName);
        operLogVo.setDeptName("测试部门");
        operLogVo.setOperUrl("/test/api");
        operLogVo.setOperIp("127.0.0.1");
        operLogVo.setOperLocation("内网IP");
        operLogVo.setOperParam("{}");
        operLogVo.setJsonResult("{\"code\":200}");
        operLogVo.setStatus(status);
        operLogVo.setErrorMsg("");
        operLogVo.setOperTime(new Date());
        operLogVo.setCostTime(100L);
        return operLogVo;
    }
}
