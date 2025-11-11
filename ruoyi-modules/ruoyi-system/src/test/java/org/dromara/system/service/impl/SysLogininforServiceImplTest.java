package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.system.domain.SysLogininfor;
import org.dromara.system.domain.bo.SysLogininforBo;
import org.dromara.system.domain.vo.SysLogininforVo;
import org.dromara.system.mapper.SysLogininforMapper;
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
 * SysLogininforServiceImpl 单元测试
 *
 * @author Claude Code
 * @date 2025-11-07
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysLogininforServiceImpl 单元测试")
class SysLogininforServiceImplTest {

    @Mock
    private SysLogininforMapper baseMapper;

    @InjectMocks
    private SysLogininforServiceImpl logininforService;

    @Captor
    private ArgumentCaptor<LambdaQueryWrapper<SysLogininfor>> wrapperCaptor;

    @Nested
    @DisplayName("1. 查询方法测试")
    class QueryMethodsTests {

        @Test
        @DisplayName("应该根据IP地址查询登录日志列表")
        void shouldReturnLogininforList_WhenQueryByIpaddr() {
            // Arrange
            SysLogininforBo queryBo = createLogininforBo(null, "admin");
            queryBo.setIpaddr("127.0.0.1");

            List<SysLogininforVo> expectedList = Arrays.asList(
                createLogininforVo(1L, "admin", "127.0.0.1", "0"),
                createLogininforVo(2L, "admin", "127.0.0.1", "0")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysLogininforVo> result = logininforService.selectLogininforList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(SysLogininforVo::getIpaddr)
                .containsOnly("127.0.0.1");

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据登录状态查询登录日志列表")
        void shouldReturnLogininforList_WhenQueryByStatus() {
            // Arrange
            SysLogininforBo queryBo = createLogininforBo(null, "admin");
            queryBo.setStatus("0"); // 成功

            List<SysLogininforVo> expectedList = Arrays.asList(
                createLogininforVo(1L, "admin", "127.0.0.1", "0"),
                createLogininforVo(2L, "test", "192.168.1.1", "0")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysLogininforVo> result = logininforService.selectLogininforList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(SysLogininforVo::getStatus)
                .containsOnly("0");

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据用户名查询登录日志列表")
        void shouldReturnLogininforList_WhenQueryByUserName() {
            // Arrange
            SysLogininforBo queryBo = createLogininforBo(null, "admin");
            queryBo.setUserName("admin");

            List<SysLogininforVo> expectedList = Collections.singletonList(
                createLogininforVo(1L, "admin", "127.0.0.1", "0")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysLogininforVo> result = logininforService.selectLogininforList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(1);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据时间范围查询登录日志列表")
        void shouldReturnLogininforList_WhenQueryByTimeRange() {
            // Arrange
            SysLogininforBo queryBo = createLogininforBo(null, "admin");
            Map<String, Object> params = new HashMap<>();
            params.put("beginTime", new Date());
            params.put("endTime", new Date());
            queryBo.setParams(params);

            List<SysLogininforVo> expectedList = Collections.singletonList(
                createLogininforVo(1L, "admin", "127.0.0.1", "0")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysLogininforVo> result = logininforService.selectLogininforList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(1);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据多个条件查询登录日志列表")
        void shouldReturnLogininforList_WhenQueryByMultipleConditions() {
            // Arrange
            SysLogininforBo queryBo = createLogininforBo(null, "admin");
            queryBo.setUserName("admin");
            queryBo.setIpaddr("127.0.0.1");
            queryBo.setStatus("0");

            List<SysLogininforVo> expectedList = Collections.singletonList(
                createLogininforVo(1L, "admin", "127.0.0.1", "0")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysLogininforVo> result = logininforService.selectLogininforList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(1);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该返回空列表_当没有登录日志匹配")
        void shouldReturnEmptyList_WhenNoLogininforMatch() {
            // Arrange
            SysLogininforBo queryBo = createLogininforBo(null, "不存在的用户");
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysLogininforVo> result = logininforService.selectLogininforList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该返回失败的登录日志列表")
        void shouldReturnLogininforList_WhenQueryByFailedStatus() {
            // Arrange
            SysLogininforBo queryBo = createLogininforBo(null, "admin");
            queryBo.setStatus("1"); // 失败

            List<SysLogininforVo> expectedList = Arrays.asList(
                createLogininforVo(1L, "admin", "127.0.0.1", "1"),
                createLogininforVo(2L, "admin", "127.0.0.2", "1")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysLogininforVo> result = logininforService.selectLogininforList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(SysLogininforVo::getStatus)
                .containsOnly("1");

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据部分IP地址查询登录日志列表")
        void shouldReturnLogininforList_WhenQueryByPartialIpaddr() {
            // Arrange
            SysLogininforBo queryBo = createLogininforBo(null, "admin");
            queryBo.setIpaddr("192.168");

            List<SysLogininforVo> expectedList = Arrays.asList(
                createLogininforVo(1L, "admin", "192.168.1.1", "0"),
                createLogininforVo(2L, "test", "192.168.1.2", "0")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysLogininforVo> result = logininforService.selectLogininforList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(2);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }
    }

    /**
     * 注意: 无法测试分页方法
     * <p>
     * <b>selectPageLogininforList</b> - 需要 MyBatis-Plus Page 对象和完整分页设置<br>
     * </p>
     */

    @Nested
    @DisplayName("2. 删除方法测试")
    class DeleteMethodsTests {

        @Test
        @DisplayName("应该成功批量删除登录日志_使用单个ID")
        void shouldDeleteLogininfor_WhenBatchDeleteWithSingleId() {
            // Arrange
            Long[] infoIds = {1L};
            when(baseMapper.deleteByIds(Arrays.asList(infoIds))).thenReturn(1);

            // Act
            int result = logininforService.deleteLogininforByIds(infoIds);

            // Assert
            assertThat(result).isEqualTo(1);
            verify(baseMapper, times(1)).deleteByIds(Arrays.asList(infoIds));
        }

        @Test
        @DisplayName("应该成功批量删除登录日志_使用多个ID")
        void shouldDeleteLogininfor_WhenBatchDeleteWithMultipleIds() {
            // Arrange
            Long[] infoIds = {1L, 2L, 3L};
            when(baseMapper.deleteByIds(Arrays.asList(infoIds))).thenReturn(3);

            // Act
            int result = logininforService.deleteLogininforByIds(infoIds);

            // Assert
            assertThat(result).isEqualTo(3);
            verify(baseMapper, times(1)).deleteByIds(Arrays.asList(infoIds));
        }

        @Test
        @DisplayName("应该返回0_当批量删除空ID数组")
        void shouldReturn0_WhenBatchDeleteWithEmptyArray() {
            // Arrange
            Long[] emptyIds = {};
            when(baseMapper.deleteByIds(Arrays.asList(emptyIds))).thenReturn(0);

            // Act
            int result = logininforService.deleteLogininforByIds(emptyIds);

            // Assert
            assertThat(result).isEqualTo(0);
            verify(baseMapper, times(1)).deleteByIds(Collections.emptyList());
        }

        @Test
        @DisplayName("应该成功清空所有登录日志")
        void shouldCleanAllLogininfor() {
            // Arrange
            when(baseMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(100);

            // Act
            logininforService.cleanLogininfor();

            // Assert
            verify(baseMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该调用delete_当清空登录日志时没有数据")
        void shouldCallDelete_WhenCleanLogininforWithNoData() {
            // Arrange
            when(baseMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(0);

            // Act
            logininforService.cleanLogininfor();

            // Assert
            verify(baseMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        }
    }

    /**
     * 注意: 无法测试 CRUD 相关方法
     * <p>
     * <b>insertLogininfor()</b> - 使用 MapstructUtils.convert()，需要静态方法 mock<br>
     * 需要 mockito-inline 或集成测试环境才能测试这个方法
     * </p>
     */

    @Nested
    @DisplayName("3. 边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("应该处理空字符串IP地址")
        void shouldHandleEmptyStringIpaddr() {
            // Arrange
            SysLogininforBo queryBo = createLogininforBo(null, "admin");
            queryBo.setIpaddr("");
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysLogininforVo> result = logininforService.selectLogininforList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该处理空字符串用户名")
        void shouldHandleEmptyStringUserName() {
            // Arrange
            SysLogininforBo queryBo = createLogininforBo(null, "");
            queryBo.setUserName("");
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysLogininforVo> result = logininforService.selectLogininforList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该处理空字符串状态")
        void shouldHandleEmptyStringStatus() {
            // Arrange
            SysLogininforBo queryBo = createLogininforBo(null, "admin");
            queryBo.setStatus("");
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysLogininforVo> result = logininforService.selectLogininforList(queryBo);

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
            SysLogininforBo queryBo = createLogininforBo(null, "admin");
            queryBo.setParams(new HashMap<>()); // 使用空HashMap而不是null
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysLogininforVo> result = logininforService.selectLogininforList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该处理所有条件为空的查询")
        void shouldHandleQueryWithAllEmptyConditions() {
            // Arrange
            SysLogininforBo queryBo = createLogininforBo(null, "");
            queryBo.setUserName("");
            queryBo.setIpaddr("");
            queryBo.setStatus("");
            queryBo.setParams(new HashMap<>());

            List<SysLogininforVo> expectedList = Arrays.asList(
                createLogininforVo(1L, "admin", "127.0.0.1", "0"),
                createLogininforVo(2L, "test", "192.168.1.1", "0")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysLogininforVo> result = logininforService.selectLogininforList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(2);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }
    }

    // ==================== Factory Methods ====================

    /**
     * 创建测试用 SysLogininforBo 业务对象
     */
    private static SysLogininforBo createLogininforBo(Long infoId, String userName) {
        SysLogininforBo logininforBo = new SysLogininforBo();
        logininforBo.setInfoId(infoId);
        logininforBo.setUserName(userName);
        logininforBo.setIpaddr("127.0.0.1");
        logininforBo.setStatus("0");
        logininforBo.setMsg("登录成功");
        logininforBo.setParams(new HashMap<>());
        return logininforBo;
    }

    /**
     * 创建测试用 SysLogininforVo 视图对象
     */
    private static SysLogininforVo createLogininforVo(Long infoId, String userName, String ipaddr, String status) {
        SysLogininforVo logininforVo = new SysLogininforVo();
        logininforVo.setInfoId(infoId);
        logininforVo.setTenantId("000000");
        logininforVo.setUserName(userName);
        logininforVo.setIpaddr(ipaddr);
        logininforVo.setLoginLocation("内网IP");
        logininforVo.setBrowser("Chrome");
        logininforVo.setOs("Windows 10");
        logininforVo.setStatus(status);
        logininforVo.setMsg(status.equals("0") ? "登录成功" : "登录失败");
        logininforVo.setLoginTime(new Date());
        return logininforVo;
    }
}
