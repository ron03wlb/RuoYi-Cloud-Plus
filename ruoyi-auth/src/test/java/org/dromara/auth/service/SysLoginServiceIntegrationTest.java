package org.dromara.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cn.hutool.crypto.digest.BCrypt;
import java.time.Duration;
import org.dromara.auth.AuthTestDataFactory;
import org.dromara.auth.BaseIntegrationTestWithContainers;
import org.dromara.auth.form.RegisterBody;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.exception.user.CaptchaException;
import org.dromara.common.core.exception.user.CaptchaExpireException;
import org.dromara.common.core.exception.user.UserException;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.tenant.exception.TenantException;
import org.dromara.system.api.RemoteSocialService;
import org.dromara.system.api.RemoteTenantService;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.bo.RemoteUserBo;
import org.dromara.system.api.domain.vo.RemoteTenantVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SysLoginService 集成测试
 *
 * <p>使用真实 Spring 容器和 Redis 环境测试登录服务
 *
 * <p>TODO: 当前因 RedissonClient 依赖问题暂时禁用
 *
 * <p>问题: ruoyi-common-redis 模块强依赖 Redisson,即使排除自动配置后仍有Bean需要注入 RedissonClient
 *
 * <p>解决方案: 需要重构 ruoyi-common-redis 模块,使 Redisson 变为可选依赖
 *
 * <p>参考: docs/PHASE2-AUTH-TESTING-FINAL-REPORT.md
 *
 * @author Test Team
 */
@org.junit.jupiter.api.Disabled("TODO: Fix RedissonClient dependency issue - see class javadoc")
@DisplayName("SysLoginService 集成测试")
class SysLoginServiceIntegrationTest extends BaseIntegrationTestWithContainers {

    @Autowired private SysLoginService sysLoginService;

    @Autowired private RemoteUserService remoteUserService;

    @Autowired private RemoteTenantService remoteTenantService;

    @Autowired private RemoteSocialService remoteSocialService;

    @BeforeEach
    @Override
    public void baseSetUp() {
        // 清理 Redis 测试数据
        // RedisUtils 可以直接使用，因为 Spring 容器已启动
    }

    @Nested
    @DisplayName("1. validateCaptcha() 验证码校验测试")
    class ValidateCaptchaTests {

        @Test
        @DisplayName("验证码正确 - 应该校验成功")
        void shouldPassWhenCaptchaIsCorrect() {
            // Arrange
            String tenantId = AuthTestDataFactory.getDefaultTenantId();
            String username = "admin";
            String code = "1234";
            String uuid = "test-uuid-" + System.currentTimeMillis();
            String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + uuid;

            // 设置 Redis 中的验证码
            RedisUtils.setCacheObject(verifyKey, "1234", Duration.ofMinutes(5));

            // Act & Assert
            assertThatNoException()
                    .isThrownBy(
                            () -> sysLoginService.validateCaptcha(tenantId, username, code, uuid));

            // Verify Redis 已删除验证码
            assertThat((Object) RedisUtils.getCacheObject(verifyKey)).isNull();
        }

        @Test
        @DisplayName("验证码已过期 - 应该抛出 CaptchaExpireException")
        void shouldThrowCaptchaExpireExceptionWhenCaptchaExpired() {
            // Arrange
            String tenantId = AuthTestDataFactory.getDefaultTenantId();
            String username = "admin";
            String code = "1234";
            String uuid = "test-uuid-expired-" + System.currentTimeMillis();
            String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + uuid;

            // 不设置 Redis 值，模拟过期

            // Act & Assert
            assertThatThrownBy(
                            () -> sysLoginService.validateCaptcha(tenantId, username, code, uuid))
                    .isInstanceOf(CaptchaExpireException.class);
        }

        @Test
        @DisplayName("验证码错误 - 应该抛出 CaptchaException")
        void shouldThrowCaptchaExceptionWhenCaptchaIsWrong() {
            // Arrange
            String tenantId = AuthTestDataFactory.getDefaultTenantId();
            String username = "admin";
            String code = "1234";
            String uuid = "test-uuid-wrong-" + System.currentTimeMillis();
            String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + uuid;

            // 设置不匹配的验证码
            RedisUtils.setCacheObject(verifyKey, "5678", Duration.ofMinutes(5));

            // Act & Assert
            assertThatThrownBy(
                            () -> sysLoginService.validateCaptcha(tenantId, username, code, uuid))
                    .isInstanceOf(CaptchaException.class);

            // Verify Redis 已删除验证码
            assertThat((Object) RedisUtils.getCacheObject(verifyKey)).isNull();
        }

        @Test
        @DisplayName("验证码大小写不敏感 - 应该通过")
        void shouldBeCaseInsensitive() {
            // Arrange
            String tenantId = AuthTestDataFactory.getDefaultTenantId();
            String username = "admin";
            String uuid = "test-uuid-case-" + System.currentTimeMillis();
            String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + uuid;

            // 设置小写验证码
            RedisUtils.setCacheObject(verifyKey, "abcd", Duration.ofMinutes(5));

            // Act & Assert - 大写输入应该通过
            assertThatNoException()
                    .isThrownBy(
                            () ->
                                    sysLoginService.validateCaptcha(
                                            tenantId, username, "ABCD", uuid));
        }
    }

    @Nested
    @DisplayName("2. register() 用户注册测试")
    class RegisterTests {

        @Test
        @DisplayName("正常注册 - 应该成功创建用户")
        void shouldRegisterSuccessfully() {
            // Arrange
            RegisterBody registerBody = AuthTestDataFactory.createRegisterBody();
            when(remoteUserService.registerUserInfo(any(RemoteUserBo.class))).thenReturn(true);

            // Act & Assert
            assertThatNoException().isThrownBy(() -> sysLoginService.register(registerBody));

            // Verify
            verify(remoteUserService)
                    .registerUserInfo(
                            argThat(
                                    bo ->
                                            bo.getUserName().equals(registerBody.getUsername())
                                                    && bo.getTenantId()
                                                            .equals(registerBody.getTenantId())
                                                    && BCrypt.checkpw(
                                                            registerBody.getPassword(),
                                                            bo.getPassword())));
        }

        @Test
        @DisplayName("注册失败 - 应该抛出 UserException")
        void shouldThrowUserExceptionWhenRegisterFails() {
            // Arrange
            RegisterBody registerBody = AuthTestDataFactory.createRegisterBody();
            when(remoteUserService.registerUserInfo(any(RemoteUserBo.class))).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> sysLoginService.register(registerBody))
                    .isInstanceOf(UserException.class);
        }

        @Test
        @DisplayName("密码应该被哈希存储")
        void shouldHashPasswordBeforeStore() {
            // Arrange
            RegisterBody registerBody = AuthTestDataFactory.createRegisterBody();
            String originalPassword = registerBody.getPassword();
            when(remoteUserService.registerUserInfo(any(RemoteUserBo.class))).thenReturn(true);

            // Act
            sysLoginService.register(registerBody);

            // Verify - 密码不应该以明文存储
            verify(remoteUserService)
                    .registerUserInfo(
                            argThat(
                                    bo -> {
                                        String hashedPassword = bo.getPassword();
                                        return !hashedPassword.equals(originalPassword)
                                                && BCrypt.checkpw(originalPassword, hashedPassword);
                                    }));
        }
    }

    @Nested
    @DisplayName("3. checkTenant() 租户校验测试")
    class CheckTenantTests {

        @Test
        @DisplayName("租户ID为空 - 应该抛出 TenantException")
        void shouldThrowExceptionWhenTenantIdIsBlank() {
            // Act & Assert
            assertThatThrownBy(() -> sysLoginService.checkTenant(""))
                    .isInstanceOf(TenantException.class);

            assertThatThrownBy(() -> sysLoginService.checkTenant(null))
                    .isInstanceOf(TenantException.class);
        }

        @Test
        @DisplayName("租户不存在 - 应该抛出 TenantException")
        void shouldThrowExceptionWhenTenantNotExists() {
            // Arrange
            String tenantId = "999999";
            when(remoteTenantService.queryByTenantId(tenantId)).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> sysLoginService.checkTenant(tenantId))
                    .isInstanceOf(TenantException.class);
        }

        @Test
        @DisplayName("租户已停用 - 应该抛出 TenantException")
        void shouldThrowExceptionWhenTenantIsDisabled() {
            // Arrange
            RemoteTenantVo tenant = AuthTestDataFactory.createDisabledTenantVo();
            when(remoteTenantService.queryByTenantId(tenant.getTenantId())).thenReturn(tenant);

            // Act & Assert
            assertThatThrownBy(() -> sysLoginService.checkTenant(tenant.getTenantId()))
                    .isInstanceOf(TenantException.class);
        }

        @Test
        @DisplayName("租户已过期 - 应该抛出 TenantException")
        void shouldThrowExceptionWhenTenantExpired() {
            // Arrange
            RemoteTenantVo tenant = AuthTestDataFactory.createExpiredTenantVo();
            when(remoteTenantService.queryByTenantId(tenant.getTenantId())).thenReturn(tenant);

            // Act & Assert
            assertThatThrownBy(() -> sysLoginService.checkTenant(tenant.getTenantId()))
                    .isInstanceOf(TenantException.class);
        }

        @Test
        @DisplayName("租户正常 - 应该校验通过")
        void shouldPassWhenTenantIsValid() {
            // Arrange
            RemoteTenantVo tenant = AuthTestDataFactory.createTenantVo();
            when(remoteTenantService.queryByTenantId(tenant.getTenantId())).thenReturn(tenant);

            // Act & Assert
            assertThatNoException()
                    .isThrownBy(() -> sysLoginService.checkTenant(tenant.getTenantId()));
        }
    }
}
