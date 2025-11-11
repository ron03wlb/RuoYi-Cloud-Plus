package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.BaseUnitTest;
import org.dromara.system.TestDataFactory;
import org.dromara.system.domain.SysClient;
import org.dromara.system.domain.bo.SysClientBo;
import org.dromara.system.domain.vo.SysClientVo;
import org.dromara.system.mapper.SysClientMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysClientServiceImpl 单元测试
 * <p>
 * 测试客户端管理服务的核心业务逻辑
 * </p>
 *
 * <p>测试范围:</p>
 * <ul>
 *   <li>查询类方法 (queryById, queryByClientId, queryPageList, queryList)</li>
 *   <li>状态更新方法 (updateClientStatus)</li>
 *   <li>删除方法 (deleteWithValidByIds)</li>
 * </ul>
 *
 * <p>测试策略:</p>
 * <ul>
 *   <li>使用 Mockito Mock SysClientMapper 依赖</li>
 *   <li>重点测试业务逻辑：grantType分割、缓存注解触发</li>
 *   <li>验证 Mapper 方法调用和参数</li>
 *   <li>跳过insertByBo/updateByBo方法（需要Spring上下文支持MapstructUtils）</li>
 * </ul>
 *
 * @author Test Team
 */
@SuppressWarnings("unchecked")
@DisplayName("SysClientServiceImpl 单元测试")
class SysClientServiceImplTest extends BaseUnitTest {

    @Mock
    private SysClientMapper baseMapper;

    @InjectMocks
    private SysClientServiceImpl clientService;

    /**
     * 初始化 MyBatis-Plus 表信息缓存
     * <p>
     * 在纯单元测试环境中，MyBatis-Plus 的 LambdaQueryWrapper 需要访问表信息缓存
     * 这个方法在所有测试执行前初始化 SysClient 实体的表信息
     * </p>
     */
    @BeforeAll
    static void initMybatisPlusTableInfo() {
        // 创建 MyBatis 配置
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);

        // 初始化 SysClient 表信息
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        TableInfoHelper.initTableInfo(assistant, SysClient.class);
    }

    // ====================
    // 1. 查询类方法测试
    // ====================

    @Nested
    @DisplayName("1. 查询类方法测试")
    class QueryTests {

        @Test
        @DisplayName("应该根据ID查询客户端并分割grantType")
        void shouldQueryByIdAndSplitGrantType() {
            // Arrange
            Long id = 1L;
            SysClientVo clientVo = TestDataFactory.createClientVo(id, "test_key");
            when(baseMapper.selectVoById(id)).thenReturn(clientVo);

            // Act
            SysClientVo result = clientService.queryById(id);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(id);
            assertThat(result.getGrantTypeList()).isNotEmpty();
            assertThat(result.getGrantTypeList()).contains("password", "client_credentials");

            // Verify
            verify(baseMapper, times(1)).selectVoById(id);
        }

        @Test
        @DisplayName("应该根据clientId查询客户端（缓存注解场景）")
        void shouldQueryByClientId() {
            // Arrange
            String clientId = "test_client_id_1";
            SysClientVo clientVo = TestDataFactory.createClientVo(1L, "test_key");
            clientVo.setClientId(clientId);
            when(baseMapper.selectVoOne(any(LambdaQueryWrapper.class))).thenReturn(clientVo);

            // Act
            SysClientVo result = clientService.queryByClientId(clientId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getClientId()).isEqualTo(clientId);

            // Verify
            verify(baseMapper, times(1)).selectVoOne(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该在clientId不存在时返回null")
        void shouldReturnNullWhenClientIdNotFound() {
            // Arrange
            String clientId = "nonexistent_client";
            when(baseMapper.selectVoOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // Act
            SysClientVo result = clientService.queryByClientId(clientId);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该分页查询客户端列表并分割每个记录的grantType")
        void shouldQueryPageListWithGrantTypeSplit() {
            // Arrange
            SysClientBo bo = new SysClientBo();
            bo.setClientKey("test");
            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNum(1);
            pageQuery.setPageSize(10);

            SysClientVo vo1 = TestDataFactory.createClientVo(1L, "key1");
            SysClientVo vo2 = TestDataFactory.createClientVo(2L, "key2");

            Page<SysClientVo> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(vo1, vo2));
            page.setTotal(2);

            when(baseMapper.selectVoPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            // Act
            TableDataInfo<SysClientVo> result = clientService.queryPageList(bo, pageQuery);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getRows()).hasSize(2);
            assertThat(result.getTotal()).isEqualTo(2);
            // 验证每个记录都有grantTypeList
            result.getRows().forEach(client -> {
                assertThat(client.getGrantTypeList()).isNotNull();
                assertThat(client.getGrantTypeList()).isNotEmpty();
            });

            // Verify
            verify(baseMapper, times(1)).selectVoPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该查询客户端列表（不分页）")
        void shouldQueryList() {
            // Arrange
            SysClientBo bo = new SysClientBo();
            bo.setStatus("0");

            SysClientVo vo1 = TestDataFactory.createClientVo(1L, "key1");
            SysClientVo vo2 = TestDataFactory.createClientVo(2L, "key2");

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(vo1, vo2));

            // Act
            List<SysClientVo> result = clientService.queryList(bo);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);

            // Verify
            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该在列表为空时返回空列表")
        void shouldReturnEmptyListWhenNoResults() {
            // Arrange
            SysClientBo bo = new SysClientBo();
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysClientVo> result = clientService.queryList(bo);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }
    }

    // ====================
    // 2. 状态更新方法测试
    // ====================

    @Nested
    @DisplayName("2. 状态更新方法测试")
    class UpdateStatusTests {

        @Test
        @DisplayName("应该成功更新客户端状态")
        void shouldUpdateClientStatus() {
            // Arrange
            String clientId = "test_client_id";
            String status = "1"; // 停用

            when(baseMapper.update(isNull(), any(LambdaUpdateWrapper.class))).thenReturn(1);

            // Act
            int result = clientService.updateClientStatus(clientId, status);

            // Assert
            assertThat(result).isEqualTo(1);

            // Verify
            verify(baseMapper, times(1)).update(isNull(), any(LambdaUpdateWrapper.class));
        }

        @Test
        @DisplayName("应该在更新状态未找到记录时返回0")
        void shouldReturn0WhenStatusUpdateFindsNoRecords() {
            // Arrange
            String clientId = "nonexistent_client";
            String status = "1";

            when(baseMapper.update(isNull(), any(LambdaUpdateWrapper.class))).thenReturn(0);

            // Act
            int result = clientService.updateClientStatus(clientId, status);

            // Assert
            assertThat(result).isEqualTo(0);
        }
    }

    // ====================
    // 3. 删除方法测试
    // ====================

    @Nested
    @DisplayName("3. 删除方法测试")
    class DeleteTests {

        @Test
        @DisplayName("应该成功批量删除客户端（缓存清除注解场景）")
        void shouldDeleteClientsByIds() {
            // Arrange
            List<Long> ids = Arrays.asList(1L, 2L, 3L);

            when(baseMapper.deleteByIds(ids)).thenReturn(3);

            // Act
            Boolean result = clientService.deleteWithValidByIds(ids, true);

            // Assert
            assertThat(result).isTrue();

            // Verify
            verify(baseMapper, times(1)).deleteByIds(ids);
        }

        @Test
        @DisplayName("应该在删除失败时返回false")
        void shouldReturnFalseWhenDeleteFails() {
            // Arrange
            List<Long> ids = Arrays.asList(1L, 2L);
            when(baseMapper.deleteByIds(ids)).thenReturn(0);

            // Act
            Boolean result = clientService.deleteWithValidByIds(ids, true);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("应该处理空ID列表删除")
        void shouldHandleEmptyIdList() {
            // Arrange
            List<Long> emptyIds = Collections.emptyList();
            when(baseMapper.deleteByIds(emptyIds)).thenReturn(0);

            // Act
            Boolean result = clientService.deleteWithValidByIds(emptyIds, true);

            // Assert
            assertThat(result).isFalse();
        }
    }

    // ====================
    // 4. 业务场景测试
    // ====================

    @Nested
    @DisplayName("4. 业务场景测试")
    class BusinessScenarioTests {

        @Test
        @DisplayName("场景: 根据多个条件查询客户端")
        void shouldQueryWithMultipleConditions() {
            // Arrange
            SysClientBo bo = new SysClientBo();
            bo.setClientKey("mobile_app");
            bo.setStatus("0");

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(
                Arrays.asList(TestDataFactory.createClientVo(1L, "mobile_app"))
            );

            // Act
            List<SysClientVo> result = clientService.queryList(bo);

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result.get(0).getClientKey()).isEqualTo("mobile_app");
        }

        @Test
        @DisplayName("场景: 禁用客户端（状态更新）")
        void shouldDisableClient() {
            // Arrange
            String clientId = "test_client_id";
            String disableStatus = "1";

            when(baseMapper.update(isNull(), any(LambdaUpdateWrapper.class))).thenReturn(1);

            // Act
            int result = clientService.updateClientStatus(clientId, disableStatus);

            // Assert
            assertThat(result).isEqualTo(1);
        }

        @Test
        @DisplayName("场景: 查询并验证grantType列表转换")
        void shouldQueryAndVerifyGrantTypeListConversion() {
            // Arrange
            Long clientId = 1L;
            SysClientVo vo = TestDataFactory.createClientVo(clientId, "app_key");
            vo.setGrantType("password,refresh_token,client_credentials");

            when(baseMapper.selectVoById(clientId)).thenReturn(vo);

            // Act
            SysClientVo result = clientService.queryById(clientId);

            // Assert
            assertThat(result.getGrantTypeList()).hasSize(3);
            assertThat(result.getGrantTypeList()).containsExactly("password", "refresh_token", "client_credentials");
        }
    }

    // ====================
    // 5. 边界值测试
    // ====================

    @Nested
    @DisplayName("5. 边界值测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("应该处理空查询条件")
        void shouldHandleEmptyQueryConditions() {
            // Arrange
            SysClientBo bo = new SysClientBo(); // 所有字段为null

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(
                Arrays.asList(
                    TestDataFactory.createClientVo(1L, "key1"),
                    TestDataFactory.createClientVo(2L, "key2")
                )
            );

            // Act
            List<SysClientVo> result = clientService.queryList(bo);

            // Assert - 应该返回所有客户端
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("应该处理单个grantType分割")
        void shouldHandleSingleGrantTypeSplit() {
            // Arrange
            Long clientId = 1L;
            SysClientVo vo = TestDataFactory.createClientVo(clientId, "app_key");
            vo.setGrantType("password"); // 单个类型

            when(baseMapper.selectVoById(clientId)).thenReturn(vo);

            // Act
            SysClientVo result = clientService.queryById(clientId);

            // Assert
            assertThat(result.getGrantTypeList()).hasSize(1);
            assertThat(result.getGrantTypeList()).containsExactly("password");
        }

        @Test
        @DisplayName("应该处理空grantType")
        void shouldHandleEmptyGrantType() {
            // Arrange
            Long clientId = 1L;
            SysClientVo vo = TestDataFactory.createClientVo(clientId, "app_key");
            vo.setGrantType(""); // 空字符串

            when(baseMapper.selectVoById(clientId)).thenReturn(vo);

            // Act
            SysClientVo result = clientService.queryById(clientId);

            // Assert
            assertThat(result.getGrantTypeList()).isNotNull();
        }
    }
}
