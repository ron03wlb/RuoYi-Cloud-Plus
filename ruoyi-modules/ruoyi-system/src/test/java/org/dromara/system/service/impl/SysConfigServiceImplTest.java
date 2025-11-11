package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.system.domain.SysConfig;
import org.dromara.system.domain.bo.SysConfigBo;
import org.dromara.system.domain.vo.SysConfigVo;
import org.dromara.system.mapper.SysConfigMapper;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysConfigServiceImpl 单元测试
 *
 * @author Claude Code
 * @date 2025-11-07
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysConfigServiceImpl 单元测试")
class SysConfigServiceImplTest {

    @Mock
    private SysConfigMapper baseMapper;

    @InjectMocks
    private SysConfigServiceImpl configService;

    @Captor
    private ArgumentCaptor<LambdaQueryWrapper<SysConfig>> wrapperCaptor;

    @Nested
    @DisplayName("1. 查询方法测试")
    class QueryMethodsTests {

        @Test
        @DisplayName("应该根据参数名称查询配置列表")
        void shouldReturnConfigList_WhenQueryByConfigName() {
            // Arrange
            SysConfigBo queryBo = createConfigBo(null, "用户管理");
            queryBo.setConfigName("用户管理");

            List<SysConfigVo> expectedList = Arrays.asList(
                createConfigVo(1L, "用户管理-注册开关", "sys.account.registerUser", "true", "N"),
                createConfigVo(2L, "用户管理-验证码开关", "sys.account.captchaEnabled", "true", "N")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysConfigVo> result = configService.selectConfigList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(SysConfigVo::getConfigName)
                .allMatch(name -> name.contains("用户管理"));

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据参数类型查询配置列表")
        void shouldReturnConfigList_WhenQueryByConfigType() {
            // Arrange
            SysConfigBo queryBo = createConfigBo(null, "");
            queryBo.setConfigType(SystemConstants.YES);

            List<SysConfigVo> expectedList = Arrays.asList(
                createConfigVo(1L, "主框架页-默认皮肤", "sys.index.skinName", "skin-blue", SystemConstants.YES),
                createConfigVo(2L, "用户管理-账号初始密码", "sys.user.initPassword", "123456", SystemConstants.YES)
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysConfigVo> result = configService.selectConfigList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(SysConfigVo::getConfigType)
                .containsOnly(SystemConstants.YES);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据参数键名查询配置列表")
        void shouldReturnConfigList_WhenQueryByConfigKey() {
            // Arrange
            SysConfigBo queryBo = createConfigBo(null, "");
            queryBo.setConfigKey("sys.account");

            List<SysConfigVo> expectedList = Collections.singletonList(
                createConfigVo(1L, "用户管理-注册开关", "sys.account.registerUser", "false", "N")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysConfigVo> result = configService.selectConfigList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(1)
                .extracting(SysConfigVo::getConfigKey)
                .allMatch(key -> key.contains("sys.account"));

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该返回空列表_当没有配置匹配")
        void shouldReturnEmptyList_WhenNoConfigsMatch() {
            // Arrange
            SysConfigBo queryBo = createConfigBo(null, "不存在的配置");
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            // Act
            List<SysConfigVo> result = configService.selectConfigList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该根据多个条件查询配置列表")
        void shouldReturnConfigList_WhenQueryByMultipleConditions() {
            // Arrange
            SysConfigBo queryBo = createConfigBo(null, "用户管理");
            queryBo.setConfigName("用户管理");
            queryBo.setConfigType("N");
            queryBo.setConfigKey("sys.user");

            List<SysConfigVo> expectedList = Collections.singletonList(
                createConfigVo(1L, "用户管理-账号初始密码", "sys.user.initPassword", "123456", "N")
            );
            when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

            // Act
            List<SysConfigVo> result = configService.selectConfigList(queryBo);

            // Assert
            assertThat(result)
                .isNotNull()
                .hasSize(1);

            verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该返回配置VO_当根据ID查询存在的配置")
        void shouldReturnConfigVo_WhenQueryById() {
            // Arrange
            Long configId = 1L;
            SysConfigVo expectedConfig = createConfigVo(configId, "用户管理-注册开关",
                "sys.account.registerUser", "true", "N");

            when(baseMapper.selectVoById(configId)).thenReturn(expectedConfig);

            // Act
            SysConfigVo result = configService.selectConfigById(configId);

            // Assert
            assertThat(result)
                .isNotNull()
                .extracting(
                    SysConfigVo::getConfigId,
                    SysConfigVo::getConfigName,
                    SysConfigVo::getConfigKey,
                    SysConfigVo::getConfigValue
                )
                .containsExactly(configId, "用户管理-注册开关", "sys.account.registerUser", "true");

            verify(baseMapper, times(1)).selectVoById(configId);
        }

        @Test
        @DisplayName("应该返回null_当根据ID查询不存在的配置")
        void shouldReturnNull_WhenConfigIdDoesNotExist() {
            // Arrange
            Long nonExistentId = 999L;
            when(baseMapper.selectVoById(nonExistentId)).thenReturn(null);

            // Act
            SysConfigVo result = configService.selectConfigById(nonExistentId);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper, times(1)).selectVoById(nonExistentId);
        }

        @Test
        @DisplayName("应该返回配置值_当根据键名查询存在的配置")
        void shouldReturnConfigValue_WhenQueryByExistingKey() {
            // Arrange
            String configKey = "sys.account.registerUser";
            SysConfig config = createConfig(1L, "用户管理-注册开关", configKey, "true", "N");

            when(baseMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(config);

            // Act
            String result = configService.selectConfigByKey(configKey);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEqualTo("true");

            verify(baseMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该返回空字符串_当根据键名查询不存在的配置")
        void shouldReturnEmptyString_WhenConfigKeyDoesNotExist() {
            // Arrange
            String nonExistentKey = "non.existent.key";
            when(baseMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // Act
            String result = configService.selectConfigByKey(nonExistentKey);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        }
    }

    /**
     * 注意: 无法测试分页和特殊方法
     * <p>
     * <b>selectPageConfigList</b> - 需要 MyBatis-Plus Page 对象和完整分页设置<br>
     * <b>selectRegisterEnabled</b> - 使用 TenantHelper.dynamic()，需要 Spring 上下文和租户配置<br>
     * </p>
     */

    @Nested
    @DisplayName("2. 验证方法测试")
    class ValidationMethodsTests {

        @Test
        @DisplayName("应该返回true_当参数键名唯一")
        void shouldReturnTrue_WhenConfigKeyIsUnique() {
            // Arrange
            SysConfigBo newConfig = createConfigBo(null, "新参数配置");
            newConfig.setConfigKey("new.config.key");

            when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(false);

            // Act
            boolean result = configService.checkConfigKeyUnique(newConfig);

            // Assert
            assertThat(result).isTrue();
            verify(baseMapper, times(1)).exists(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该返回false_当参数键名已存在")
        void shouldReturnFalse_WhenConfigKeyAlreadyExists() {
            // Arrange
            SysConfigBo duplicateConfig = createConfigBo(null, "重复参数配置");
            duplicateConfig.setConfigKey("sys.account.registerUser");

            when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(true);

            // Act
            boolean result = configService.checkConfigKeyUnique(duplicateConfig);

            // Assert
            assertThat(result).isFalse();
            verify(baseMapper, times(1)).exists(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该排除自身ID_当检查参数键名唯一性更新时")
        void shouldExcludeSelfId_WhenCheckingUniquenessDuringUpdate() {
            // Arrange
            SysConfigBo existingConfig = createConfigBo(1L, "现有参数配置");
            existingConfig.setConfigKey("sys.account.registerUser");

            when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(false);

            // Act
            boolean result = configService.checkConfigKeyUnique(existingConfig);

            // Assert
            assertThat(result).isTrue();

            verify(baseMapper, times(1)).exists(wrapperCaptor.capture());

            // 验证查询条件应该排除自身ID
            LambdaQueryWrapper<SysConfig> capturedWrapper = wrapperCaptor.getValue();
            assertThat(capturedWrapper).isNotNull();
        }
    }

    @Nested
    @DisplayName("3. 删除方法测试")
    class DeleteMethodsTests {

        @Test
        @DisplayName("应该正常处理_当删除空配置ID列表")
        void shouldHandleEmptyList_WhenDeletingEmptyConfigIds() {
            // Arrange
            List<Long> emptyConfigIds = Collections.emptyList();
            when(baseMapper.selectByIds(emptyConfigIds)).thenReturn(Collections.emptyList());

            // Act
            configService.deleteConfigByIds(emptyConfigIds);

            // Assert
            verify(baseMapper, times(1)).selectByIds(emptyConfigIds);
            verify(baseMapper, times(1)).deleteByIds(emptyConfigIds);
        }

        @Test
        @DisplayName("应该抛出异常_当删除内置参数配置")
        void shouldThrowException_WhenDeletingBuiltInConfig() {
            // Arrange
            List<Long> configIds = Arrays.asList(1L, 2L);
            SysConfig builtInConfig = createConfig(1L, "主框架页-默认皮肤",
                "sys.index.skinName", "skin-blue", SystemConstants.YES);
            SysConfig normalConfig = createConfig(2L, "用户自定义参数",
                "custom.config.key", "custom-value", "N");

            List<SysConfig> configs = Arrays.asList(builtInConfig, normalConfig);

            when(baseMapper.selectByIds(configIds)).thenReturn(configs);

            // Act & Assert
            assertThatThrownBy(() -> configService.deleteConfigByIds(configIds))
                .as("应该抛出ServiceException")
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("内置参数")
                .hasMessageContaining("不能删除");

            verify(baseMapper, times(1)).selectByIds(configIds);
            verify(baseMapper, never()).deleteByIds(any()); // 不应该执行删除操作
        }

        @Test
        @DisplayName("应该在第一个内置参数时抛出异常")
        void shouldThrowOnFirstBuiltInConfig_WhenMultipleBuiltInConfigs() {
            // Arrange
            List<Long> configIds = Arrays.asList(1L, 2L, 3L);
            SysConfig builtIn1 = createConfig(1L, "内置参数1",
                "sys.config.key1", "value1", SystemConstants.YES);
            SysConfig normalConfig = createConfig(2L, "普通参数",
                "custom.key", "value", "N");
            SysConfig builtIn2 = createConfig(3L, "内置参数2",
                "sys.config.key2", "value2", SystemConstants.YES);

            List<SysConfig> configs = Arrays.asList(builtIn1, normalConfig, builtIn2);

            when(baseMapper.selectByIds(configIds)).thenReturn(configs);

            // Act & Assert
            assertThatThrownBy(() -> configService.deleteConfigByIds(configIds))
                .as("应该在第一个内置参数时抛出异常")
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("sys.config.key1"); // 应该是第一个内置参数的key

            verify(baseMapper, times(1)).selectByIds(configIds);
            verify(baseMapper, never()).deleteByIds(any());
        }
    }

    /**
     * 注意: 无法测试 CRUD 相关方法和缓存方法
     * <p>
     * <b>insertConfig()</b> - 使用 MapstructUtils.convert()，需要静态方法 mock<br>
     * <b>updateConfig()</b> - 使用 MapstructUtils.convert() + CacheUtils.evict()，需要静态方法 mock<br>
     * <b>deleteConfigByIds() 成功路径</b> - 调用 CacheUtils.evict()，需要 Spring 上下文<br>
     * <b>resetConfigCache()</b> - 调用 CacheUtils.clear()，需要 Spring 上下文<br>
     *
     * @CachePut 和 @Cacheable 注解需要 Spring AOP 代理才能生效<br>
     * 需要 mockito-inline 或集成测试环境才能测试这些方法
     * </p>
     */

    @Nested
    @DisplayName("4. 边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("应该处理null配置ID_selectConfigById")
        void shouldHandleNullConfigId_selectConfigById() {
            // Arrange
            Long nullConfigId = null;
            when(baseMapper.selectVoById(nullConfigId)).thenReturn(null);

            // Act
            SysConfigVo result = configService.selectConfigById(nullConfigId);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper, times(1)).selectVoById(nullConfigId);
        }

        @Test
        @DisplayName("应该处理null参数键名_selectConfigByKey")
        void shouldHandleNullConfigKey_selectConfigByKey() {
            // Arrange
            String nullConfigKey = null;
            when(baseMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // Act
            String result = configService.selectConfigByKey(nullConfigKey);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEmpty(); // 返回空字符串而不是 null

            verify(baseMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该处理空字符串参数键名_selectConfigByKey")
        void shouldHandleEmptyStringConfigKey_selectConfigByKey() {
            // Arrange
            String emptyConfigKey = "";
            when(baseMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // Act
            String result = configService.selectConfigByKey(emptyConfigKey);

            // Assert
            assertThat(result)
                .isNotNull()
                .isEmpty();

            verify(baseMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("应该处理最大Long值_selectConfigById")
        void shouldHandleMaxLongValue_selectConfigById() {
            // Arrange
            Long maxConfigId = Long.MAX_VALUE;
            when(baseMapper.selectVoById(maxConfigId)).thenReturn(null);

            // Act
            SysConfigVo result = configService.selectConfigById(maxConfigId);

            // Assert
            assertThat(result).isNull();
            verify(baseMapper, times(1)).selectVoById(maxConfigId);
        }
    }

    // ==================== Factory Methods ====================

    /**
     * 创建测试用 SysConfig 实体
     */
    private static SysConfig createConfig(Long configId, String configName, String configKey,
                                          String configValue, String configType) {
        SysConfig config = new SysConfig();
        config.setConfigId(configId);
        config.setConfigName(configName);
        config.setConfigKey(configKey);
        config.setConfigValue(configValue);
        config.setConfigType(configType);
        config.setRemark("测试配置备注");
        return config;
    }

    /**
     * 创建测试用 SysConfigBo 业务对象
     */
    private static SysConfigBo createConfigBo(Long configId, String configName) {
        SysConfigBo configBo = new SysConfigBo();
        configBo.setConfigId(configId);
        configBo.setConfigName(configName);
        configBo.setConfigKey("test.config.key");
        configBo.setConfigValue("test-value");
        configBo.setConfigType("N");
        configBo.setRemark("测试配置业务对象");
        return configBo;
    }

    /**
     * 创建测试用 SysConfigVo 视图对象
     */
    private static SysConfigVo createConfigVo(Long configId, String configName, String configKey,
                                              String configValue, String configType) {
        SysConfigVo configVo = new SysConfigVo();
        configVo.setConfigId(configId);
        configVo.setConfigName(configName);
        configVo.setConfigKey(configKey);
        configVo.setConfigValue(configValue);
        configVo.setConfigType(configType);
        configVo.setRemark("测试配置视图对象");
        return configVo;
    }
}
