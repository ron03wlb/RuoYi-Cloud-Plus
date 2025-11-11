package org.dromara.auth.config;

import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.resource.api.RemoteMessageService;
import org.dromara.system.api.*;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 測試配置類
 * <p>
 * 提供所有測試需要的 Mock Bean
 * </p>
 *
 * <p>功能:</p>
 * <ul>
 *   <li>Mock 所有 Dubbo RPC 服務</li>
 *   <li>使用 @Primary 覆蓋默認 Bean</li>
 *   <li>避免真實 Dubbo 連接</li>
 * </ul>
 *
 * @author Test Team
 */
@TestConfiguration
public class AuthTestConfig {

    /**
     * Mock RemoteClientService
     */
    @Bean
    @Primary
    public RemoteClientService mockRemoteClientService() {
        return Mockito.mock(RemoteClientService.class);
    }

    /**
     * Mock RemoteUserService
     */
    @Bean
    @Primary
    public RemoteUserService mockRemoteUserService() {
        return Mockito.mock(RemoteUserService.class);
    }

    /**
     * Mock RemoteTenantService
     */
    @Bean
    @Primary
    public RemoteTenantService mockRemoteTenantService() {
        return Mockito.mock(RemoteTenantService.class);
    }

    /**
     * Mock RemoteConfigService
     */
    @Bean
    @Primary
    public RemoteConfigService mockRemoteConfigService() {
        return Mockito.mock(RemoteConfigService.class);
    }

    /**
     * Mock RemoteSocialService
     */
    @Bean
    @Primary
    public RemoteSocialService mockRemoteSocialService() {
        return Mockito.mock(RemoteSocialService.class);
    }

    /**
     * Mock RemoteLogService
     */
    @Bean
    @Primary
    public RemoteLogService mockRemoteLogService() {
        return Mockito.mock(RemoteLogService.class);
    }

    /**
     * Mock RemoteMessageService
     */
    @Bean
    @Primary
    public RemoteMessageService mockRemoteMessageService() {
        return Mockito.mock(RemoteMessageService.class);
    }
}
