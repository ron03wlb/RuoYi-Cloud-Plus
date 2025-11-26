package org.dromara.common.satoken.config;

import static org.assertj.core.api.Assertions.assertThat;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import org.dromara.common.satoken.BaseUnitTest;
import org.dromara.common.satoken.core.dao.PlusSaTokenDao;
import org.dromara.common.satoken.core.service.SaPermissionImpl;
import org.dromara.common.satoken.handler.SaTokenExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * SaTokenConfiguration 测试
 *
 * <p>测试 Sa-Token 配置类的 Bean 创建
 *
 * @author Test Team
 */
@DisplayName("SaTokenConfiguration 测试")
class SaTokenConfigurationTest extends BaseUnitTest {

    private SaTokenConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration = new SaTokenConfiguration();
    }

    @Nested
    @DisplayName("1. Bean 创建测试")
    class BeanCreationTests {

        @Test
        @DisplayName("应该能够创建 StpLogic bean")
        void shouldCreateStpLogicBean() {
            // Act
            StpLogic stpLogic = configuration.getStpLogicJwt();

            // Assert
            assertThat(stpLogic).isNotNull();
            assertThat(stpLogic.getClass().getSimpleName()).contains("StpLogicJwt");
        }

        @Test
        @DisplayName("应该能够创建 StpInterface bean")
        void shouldCreateStpInterfaceBean() {
            // Act
            StpInterface stpInterface = configuration.stpInterface();

            // Assert
            assertThat(stpInterface).isNotNull();
            assertThat(stpInterface).isInstanceOf(SaPermissionImpl.class);
        }

        @Test
        @DisplayName("应该能够创建 SaTokenDao bean")
        void shouldCreateSaTokenDaoBean() {
            // Act
            SaTokenDao saTokenDao = configuration.saTokenDao();

            // Assert
            assertThat(saTokenDao).isNotNull();
            assertThat(saTokenDao).isInstanceOf(PlusSaTokenDao.class);
        }

        @Test
        @DisplayName("应该能够创建 SaTokenExceptionHandler bean")
        void shouldCreateExceptionHandlerBean() {
            // Act
            SaTokenExceptionHandler handler = configuration.saTokenExceptionHandler();

            // Assert
            assertThat(handler).isNotNull();
            assertThat(handler).isInstanceOf(SaTokenExceptionHandler.class);
        }
    }

    @Nested
    @DisplayName("2. 配置类注解验证测试")
    class AnnotationValidationTests {

        @Test
        @DisplayName("配置类应该有 @AutoConfiguration 注解")
        void configurationShouldHaveAutoConfigurationAnnotation() {
            // Arrange & Act
            boolean hasAnnotation =
                    SaTokenConfiguration.class.isAnnotationPresent(
                            org.springframework.boot.autoconfigure.AutoConfiguration.class);

            // Assert
            assertThat(hasAnnotation).isTrue();
        }

        @Test
        @DisplayName("配置类应该有 @PropertySource 注解")
        void configurationShouldHavePropertySourceAnnotation() {
            // Arrange & Act
            boolean hasAnnotation =
                    SaTokenConfiguration.class.isAnnotationPresent(
                            org.springframework.context.annotation.PropertySource.class);

            // Assert
            assertThat(hasAnnotation).isTrue();
        }
    }
}
