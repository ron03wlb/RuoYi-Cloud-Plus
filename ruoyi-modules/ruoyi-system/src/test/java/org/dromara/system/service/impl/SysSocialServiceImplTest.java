package org.dromara.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.system.BaseUnitTest;
import org.dromara.system.TestDataFactory;
import org.dromara.system.domain.SysSocial;
import org.dromara.system.domain.bo.SysSocialBo;
import org.dromara.system.domain.vo.SysSocialVo;
import org.dromara.system.mapper.SysSocialMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * SysSocialServiceImpl 单元测试
 *
 * <p>测试社会化关系服务的核心业务逻辑
 *
 * <p>测试范围:
 *
 * <ul>
 *   <li>查询类方法 (queryById, queryList, queryListByUserId, selectByAuthId)
 *   <li>删除方法 (deleteWithValidById)
 * </ul>
 *
 * <p>测试策略:
 *
 * <ul>
 *   <li>使用 Mockito Mock SysSocialMapper 依赖
 *   <li>重点测试查询条件构建和列表过滤逻辑
 *   <li>验证 Mapper 方法调用和参数
 *   <li>跳过insertByBo/updateByBo方法（需要Spring上下文支持MapstructUtils）
 * </ul>
 *
 * @author Test Team
 */
@SuppressWarnings("unchecked")
@DisplayName("SysSocialServiceImpl 单元测试")
class SysSocialServiceImplTest extends BaseUnitTest {

    @Mock private SysSocialMapper baseMapper;

    @InjectMocks private SysSocialServiceImpl socialService;

    /**
     * 初始化 MyBatis-Plus 表信息缓存
     *
     * <p>在纯单元测试环境中，MyBatis-Plus 的 LambdaQueryWrapper 需要访问表信息缓存 这个方法在所有测试执行前初始化 SysSocial 实体的表信息
     */
    @BeforeAll
    static void initMybatisPlusTableInfo() {
        // 创建 MyBatis 配置
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);

        // 初始化 SysSocial 表信息
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        TableInfoHelper.initTableInfo(assistant, SysSocial.class);
    }

    // ====================
    // 1. 查询类方法测试
    // ====================

    @Nested
    @DisplayName("1. 查询类方法测试")
    class QueryTests {

        @Test
        @DisplayName("应该根据ID查询社会化关系")
        void shouldQueryById() {
            // Arrange
            String id = "1";
            SysSocialVo expectedVo = TestDataFactory.createSocialVo(1L, 100L, "wechat");
            when(baseMapper.selectVoById(id)).thenReturn(expectedVo);

            // Act
            SysSocialVo result = socialService.queryById(id);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUserId()).isEqualTo(100L);
            assertThat(result.getSource()).isEqualTo("wechat");

            // Verify
            verify(baseMapper, times(1)).selectVoById(id);
        }

        @Test
        @DisplayName("应该在ID不存在时返回null")
        void shouldReturnNullWhenIdNotFound() {
            // Arrange
            String id = "999";
            when(baseMapper.selectVoById(id)).thenReturn(null);

            // Act
            SysSocialVo result = socialService.queryById(id);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该根据条件查询社会化关系列表")
        void shouldQueryListWithConditions() {
            // Arrange
            SysSocialBo bo = new SysSocialBo();
            bo.setUserId(100L);
            bo.setSource("github");

            SysSocialVo vo1 = TestDataFactory.createSocialVo(1L, 100L, "github");
            SysSocialVo vo2 = TestDataFactory.createSocialVo(2L, 100L, "github");

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(vo1, vo2));

            // Act
            List<SysSocialVo> result = socialService.queryList(bo);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(vo -> vo.getUserId().equals(100L));
            assertThat(result).allMatch(vo -> vo.getSource().equals("github"));

            // Verify
            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据用户ID查询社会化关系列表")
        void shouldQueryListByUserId() {
            // Arrange
            Long userId = 100L;

            SysSocialVo vo1 = TestDataFactory.createSocialVo(1L, userId, "wechat");
            SysSocialVo vo2 = TestDataFactory.createSocialVo(2L, userId, "github");
            SysSocialVo vo3 = TestDataFactory.createSocialVo(3L, userId, "qq");

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(vo1, vo2, vo3));

            // Act
            List<SysSocialVo> result = socialService.queryListByUserId(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result).allMatch(vo -> vo.getUserId().equals(userId));

            // 验证包含不同的社交平台
            assertThat(result)
                    .extracting(SysSocialVo::getSource)
                    .containsExactlyInAnyOrder("wechat", "github", "qq");

            // Verify
            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据authId查询社会化关系")
        void shouldSelectByAuthId() {
            // Arrange
            String authId = "auth_wechat_100";

            SysSocialVo vo = TestDataFactory.createSocialVo(1L, 100L, "wechat");
            vo.setAuthId(authId);

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.singletonList(vo));

            // Act
            List<SysSocialVo> result = socialService.selectByAuthId(authId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAuthId()).isEqualTo(authId);

            // Verify
            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该在列表查询无结果时返回空列表")
        void shouldReturnEmptyListWhenNoResults() {
            // Arrange
            SysSocialBo bo = new SysSocialBo();
            bo.setUserId(999L);

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());

            // Act
            List<SysSocialVo> result = socialService.queryList(bo);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该支持多条件组合查询")
        void shouldSupportMultipleConditions() {
            // Arrange
            SysSocialBo bo = new SysSocialBo();
            bo.setUserId(100L);
            bo.setAuthId("auth_wechat_100");
            bo.setSource("wechat");

            SysSocialVo vo = TestDataFactory.createSocialVo(1L, 100L, "wechat");
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.singletonList(vo));

            // Act
            List<SysSocialVo> result = socialService.queryList(bo);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUserId()).isEqualTo(100L);
            assertThat(result.get(0).getSource()).isEqualTo("wechat");
        }
    }

    // ====================
    // 2. 删除方法测试
    // ====================

    @Nested
    @DisplayName("2. 删除方法测试")
    class DeleteTests {

        @Test
        @DisplayName("应该成功删除社会化关系")
        void shouldDeleteSuccessfully() {
            // Arrange
            Long id = 1L;
            when(baseMapper.deleteById(id)).thenReturn(1);

            // Act
            Boolean result = socialService.deleteWithValidById(id);

            // Assert
            assertThat(result).isTrue();

            // Verify
            verify(baseMapper, times(1)).deleteById(id);
        }

        @Test
        @DisplayName("应该在删除失败时返回false")
        void shouldReturnFalseWhenDeleteFails() {
            // Arrange
            Long id = 999L;
            when(baseMapper.deleteById(id)).thenReturn(0);

            // Act
            Boolean result = socialService.deleteWithValidById(id);

            // Assert
            assertThat(result).isFalse();
        }
    }

    // ====================
    // 3. 业务场景测试
    // ====================

    @Nested
    @DisplayName("3. 业务场景测试")
    class BusinessScenarioTests {

        @Test
        @DisplayName("场景: 查询用户的所有社交平台绑定")
        void shouldFindAllUserSocialBindings() {
            // Arrange - 用户绑定了多个社交平台
            Long userId = 100L;

            SysSocialVo wechatBinding = TestDataFactory.createSocialVo(1L, userId, "wechat");
            SysSocialVo githubBinding = TestDataFactory.createSocialVo(2L, userId, "github");
            SysSocialVo qqBinding = TestDataFactory.createSocialVo(3L, userId, "qq");

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(wechatBinding, githubBinding, qqBinding));

            // Act
            List<SysSocialVo> bindings = socialService.queryListByUserId(userId);

            // Assert
            assertThat(bindings).hasSize(3);
            assertThat(bindings)
                    .extracting(SysSocialVo::getSource)
                    .containsExactlyInAnyOrder("wechat", "github", "qq");
        }

        @Test
        @DisplayName("场景: 检查特定平台是否已绑定")
        void shouldCheckIfPlatformAlreadyBound() {
            // Arrange - 查询用户是否已绑定微信
            SysSocialBo bo = new SysSocialBo();
            bo.setUserId(100L);
            bo.setSource("wechat");

            SysSocialVo existingBinding = TestDataFactory.createSocialVo(1L, 100L, "wechat");
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.singletonList(existingBinding));

            // Act
            List<SysSocialVo> result = socialService.queryList(bo);

            // Assert
            assertThat(result).isNotEmpty(); // 已绑定
            assertThat(result.get(0).getSource()).isEqualTo("wechat");
        }

        @Test
        @DisplayName("场景: 第三方登录时查找已绑定的用户")
        void shouldFindBoundUserByAuthId() {
            // Arrange - 第三方平台返回authId，查找系统中绑定的用户
            String authId = "auth_wechat_external_123456";

            SysSocialVo boundAccount = TestDataFactory.createSocialVo(1L, 100L, "wechat");
            boundAccount.setAuthId(authId);

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.singletonList(boundAccount));

            // Act
            List<SysSocialVo> result = socialService.selectByAuthId(authId);

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result.get(0).getUserId()).isEqualTo(100L);
            assertThat(result.get(0).getAuthId()).isEqualTo(authId);
        }

        @Test
        @DisplayName("场景: 解绑社交账号")
        void shouldUnbindSocialAccount() {
            // Arrange - 用户想解绑某个社交账号
            Long bindingId = 1L;
            when(baseMapper.deleteById(bindingId)).thenReturn(1);

            // Act
            Boolean unbound = socialService.deleteWithValidById(bindingId);

            // Assert
            assertThat(unbound).isTrue();
        }
    }

    // ====================
    // 4. 边界值测试
    // ====================

    @Nested
    @DisplayName("4. 边界值测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("应该处理空查询条件")
        void shouldHandleEmptyQueryConditions() {
            // Arrange - 所有字段为null
            SysSocialBo bo = new SysSocialBo();

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
                    .thenReturn(
                            Arrays.asList(
                                    TestDataFactory.createSocialVo(1L, 100L, "wechat"),
                                    TestDataFactory.createSocialVo(2L, 200L, "github")));

            // Act
            List<SysSocialVo> result = socialService.queryList(bo);

            // Assert - 应该返回所有记录
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("应该处理用户无社交绑定的情况")
        void shouldHandleUserWithNoBindings() {
            // Arrange
            Long userId = 999L;
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());

            // Act
            List<SysSocialVo> result = socialService.queryListByUserId(userId);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该处理不存在的authId")
        void shouldHandleNonExistentAuthId() {
            // Arrange
            String authId = "nonexistent_auth_id";
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());

            // Act
            List<SysSocialVo> result = socialService.selectByAuthId(authId);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该处理单个条件查询")
        void shouldHandleSingleConditionQuery() {
            // Arrange - 只设置source条件
            SysSocialBo bo = new SysSocialBo();
            bo.setSource("github");

            SysSocialVo vo1 = TestDataFactory.createSocialVo(1L, 100L, "github");
            SysSocialVo vo2 = TestDataFactory.createSocialVo(2L, 200L, "github");

            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(vo1, vo2));

            // Act
            List<SysSocialVo> result = socialService.queryList(bo);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(vo -> vo.getSource().equals("github"));
        }
    }
}
